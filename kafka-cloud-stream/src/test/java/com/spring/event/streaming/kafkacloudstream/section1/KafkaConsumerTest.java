package com.spring.event.streaming.kafkacloudstream.section1;

import com.spring.event.streaming.kafkacloudstream.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

@DirtiesContext
@TestPropertySource(properties = {
        "section=section1",
        "spring.cloud.function.definition=testProducer;stringConsumer",
        "spring.cloud.stream.bindings.testProducer-out-0.destination=input-topic"
})
@ExtendWith(OutputCaptureExtension.class)
public class KafkaConsumerTest extends AbstractIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Supplier<Flux<String>> testProducer() {
            return () -> Flux.just("Hello World!");
        }
    }

    @Test
    public void consumerTest(CapturedOutput capturedOutput) {
        Mono<String> stringMono = Mono.delay(Duration.ofMillis(500))
                .then(Mono.fromSupplier(capturedOutput::getOut));

        StepVerifier.create(stringMono)
                .consumeNextWith(s -> Assertions.assertTrue(s.contains("Hello World!")))
                .verifyComplete();
    }
}
