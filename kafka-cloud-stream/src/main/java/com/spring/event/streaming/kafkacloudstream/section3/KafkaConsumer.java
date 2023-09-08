package com.spring.event.streaming.kafkacloudstream.section3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class KafkaConsumer {

    @Bean
    public Consumer<Flux<String>> stringConsumer() {
        return stringFlux -> stringFlux
                .doOnNext(message -> log.info("message received by consumer: {}", message))
                .subscribe();
    }
}