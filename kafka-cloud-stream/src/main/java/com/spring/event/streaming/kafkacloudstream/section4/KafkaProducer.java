package com.spring.event.streaming.kafkacloudstream.section4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class KafkaProducer {

    @Bean
    public Supplier<Flux<Message<String>>> stringProducer() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(this::generateMessage)
                .doOnNext(message -> log.info("message produced by producer: {}", message));
    }

    private Message<String> generateMessage(long i) {
        return MessageBuilder.withPayload("message: " + i)
                .setHeader(KafkaHeaders.KEY, ("key-" + i))
                .build();
    }
}