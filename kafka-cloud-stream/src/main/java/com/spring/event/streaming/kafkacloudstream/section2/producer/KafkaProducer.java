package com.spring.event.streaming.kafkacloudstream.section2.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.cloud.stream.binder.reactorkafka.SenderOptionsCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderOptions;

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

    @Bean
    public SenderOptionsCustomizer<String, SenderOptions> senderOptionsCustomizer() {
        return (string, senderOptions) -> senderOptions
                .producerProperty(ProducerConfig.ACKS_CONFIG, "all")
                .producerProperty(ProducerConfig.BATCH_SIZE_CONFIG, "20001");
    }
}
