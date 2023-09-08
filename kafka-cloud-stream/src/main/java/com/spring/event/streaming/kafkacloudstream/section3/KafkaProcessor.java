package com.spring.event.streaming.kafkacloudstream.section3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Configuration
public class KafkaProcessor {

    @Bean
    public Function<Flux<String>, Flux<String>> stringProcessor() {
        return string -> string
                .doOnNext(inputMessage -> log.info("processor received: {}", inputMessage))
                .concatMap(this::process)
                .doOnNext(outputMessage -> log.info("processor emitted: {}", outputMessage));
    }

    private Mono<String> process(String input) {
        return Mono.just(input) // could be a database call or third party service call etc.
                .map(String::toUpperCase);
    }
}