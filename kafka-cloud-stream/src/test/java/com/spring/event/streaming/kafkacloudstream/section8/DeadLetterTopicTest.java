package com.spring.event.streaming.kafkacloudstream.section8;

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

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@TestPropertySource(properties = {
        "section=section8",
        "spring.cloud.function.definition=testStringProducer;characterProcessor;testCharacterConsumer;testDeadLetterTopicConsumer",
        "spring.cloud.stream.bindings.testStringProducer-out-0.destination=input-events-topic",
        "spring.cloud.stream.bindings.characterProcessor-in-0.destination=input-events-topic",
        "spring.cloud.stream.bindings.characterProcessor-out-0.destination=output-events-topic",
        "spring.cloud.stream.bindings.testCharacterConsumer-in-0.destination=output-events-topic",
        "spring.cloud.stream.bindings.testDeadLetterTopicConsumer-in-0.destination=dlq-topic"
})
public class DeadLetterTopicTest extends AbstractIntegrationTest {

    private static final Sinks.Many<String> requestSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<Character> responseSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<String> deadLetterTopicSink = Sinks.many().unicast().onBackpressureBuffer();

    @Test
    public void characterProcessingTest() {

        // produce
        requestSink.tryEmitNext("egetq duis");
        requestSink.tryEmitNext("fermentum et");
        requestSink.tryEmitNext("quisz lectus");
        requestSink.tryEmitNext("qu"); // exception will be thrown for this record
        requestSink.tryEmitNext("sita amet");

        // consume character
        responseSink.asFlux()
                .take(4)
                .doOnNext(character -> log.info("character consumer received: {}", character))
                .as(StepVerifier::create)
                .consumeNextWith(character -> Assertions.assertEquals('t', character))
                .consumeNextWith(character -> Assertions.assertEquals('m', character))
                .consumeNextWith(character -> Assertions.assertEquals('s', character))
                .consumeNextWith(character -> Assertions.assertEquals('a', character))
                .verifyComplete();

        // consume error
        deadLetterTopicSink.asFlux()
                .take(1)
                .doOnNext(errorRecord -> log.info("dead letter topic consumer received: {}", errorRecord))
                .as(StepVerifier::create)
                .consumeNextWith(errorMessage -> Assertions.assertEquals("qu", errorMessage))
                .verifyComplete();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<String>> testStringProducer() {
            return requestSink::asFlux;
        }

        @Bean
        public Consumer<Flux<Character>> testCharacterConsumer() {
            return characterFlux -> characterFlux
                    .doOnNext(responseSink::tryEmitNext)
                    .subscribe();
        }

        @Bean
        public Consumer<Flux<String>> testDeadLetterTopicConsumer() {
            return deadLetterStringFlux -> deadLetterStringFlux
                    .doOnNext(deadLetterTopicSink::tryEmitNext)
                    .subscribe();
        }
    }
}
