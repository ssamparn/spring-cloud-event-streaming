package com.spring.event.streaming.kafkacloudstream.section2;

import com.spring.event.streaming.kafkacloudstream.AbstractIntegrationTest;
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

@TestPropertySource(properties = {
        "section=section2",
        "spring.cloud.function.definition=stringProducer;testConsumer",
        "spring.cloud.stream.bindings.testConsumer-in-0.destination=input-topic"
})
public class KafkaProducerTest extends AbstractIntegrationTest {

    private static final Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Consumer<Flux<String>> testConsumer() {
            return message -> message
                .doOnNext(sink::tryEmitNext)
                .subscribe();
        }
    }

    @Test
    public void producerTest() {
        sink
            .asFlux()
            .take(5)
            .timeout(Duration.ofSeconds(5))
            .as(StepVerifier::create)
            .consumeNextWith(s -> Assertions.assertEquals("message: 0", s))
            .consumeNextWith(s -> Assertions.assertEquals("message: 1", s))
            .consumeNextWith(s -> Assertions.assertEquals("message: 2", s))
            .consumeNextWith(s -> Assertions.assertEquals("message: 3", s))
            .consumeNextWith(s -> Assertions.assertEquals("message: 4", s))
            .verifyComplete();
    }
}
