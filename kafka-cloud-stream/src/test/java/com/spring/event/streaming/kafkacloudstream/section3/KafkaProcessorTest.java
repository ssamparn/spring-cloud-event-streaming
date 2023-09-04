package com.spring.event.streaming.kafkacloudstream.section3;

import com.spring.event.streaming.kafkacloudstream.AbstractIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@TestPropertySource(properties = {
        "section=section3",
        "spring.cloud.function.definition=testProducer;stringProcessor;testConsumer",
        "spring.cloud.stream.bindings.testProducer-out-0.destination=input-topic",
        "spring.cloud.stream.bindings.testConsumer-in-0.destination=output-topic"
})
public class KafkaProcessorTest extends AbstractIntegrationTest {

    private static final Sinks.Many<String> requestSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<String> responseSink = Sinks.many().unicast().onBackpressureBuffer();

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<String>> testProducer() {
            return requestSink::asFlux;
        }

        @Bean
        public Consumer<Flux<String>> testConsumer() {
            return message -> message
                    .doOnNext(responseSink::tryEmitNext)
                    .subscribe();
        }
    }

    @Test
    public void processorTest() {

        // produce
        requestSink.tryEmitNext("sam");
        requestSink.tryEmitNext("mike");
        requestSink.tryEmitNext("eliot");

        // consume
        responseSink.asFlux()
                .take(3)
                .timeout(Duration.ofSeconds(5))
                .doOnNext(message -> log.info("message received: {}", message))
                .as(StepVerifier::create)
                .consumeNextWith(message -> Assertions.assertEquals("SAM", message))
                .consumeNextWith(message -> Assertions.assertEquals("MIKE", message))
                .consumeNextWith(message -> Assertions.assertEquals("ELIOT", message))
                .verifyComplete();
    }
}