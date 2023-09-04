package com.spring.event.streaming.kafkacloudstream.section4;

import com.spring.event.streaming.kafkacloudstream.AbstractIntegrationTest;
import com.spring.event.streaming.kafkacloudstream.common.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;

@Slf4j
@TestPropertySource(properties = {
        "section=section4",
        "spring.cloud.function.definition=stringProducer;testConsumer",
        "spring.cloud.stream.bindings.testConsumer-in-0.destination=input-topic"
})
public class KafkaProducerWithKeyTest extends AbstractIntegrationTest {

    private static final Sinks.Many<Message<String>> sink = Sinks.many().unicast().onBackpressureBuffer();

    @Test
    public void producerTestWithKeys() {
        sink.asFlux()
                .map(MessageConverter::toMessageRecord)
                .take(1)
                .timeout(Duration.ofSeconds(5))
                .as(StepVerifier::create)
                .consumeNextWith(r -> Assertions.assertEquals("message: 0", r.message()))
                .verifyComplete();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Consumer<Flux<Message<String>>> testConsumer() {
            return f -> f
                    .doOnNext(sink::tryEmitNext)
                    .subscribe();
        }
    }
}
