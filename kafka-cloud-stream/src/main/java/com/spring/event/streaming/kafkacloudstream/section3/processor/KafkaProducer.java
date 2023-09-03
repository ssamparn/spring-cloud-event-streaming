package com.spring.event.streaming.kafkacloudstream.section3.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class KafkaProducer {

    @Bean
    public Supplier<Flux<String>> stringProducer() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(i -> "message: " + i)
                .doOnNext(message -> log.info("message produced by producer: {}", message));
    }
}