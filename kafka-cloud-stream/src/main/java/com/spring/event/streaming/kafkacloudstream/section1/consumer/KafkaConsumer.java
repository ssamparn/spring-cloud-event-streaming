package com.spring.event.streaming.kafkacloudstream.section1.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.cloud.stream.binder.reactorkafka.ReceiverOptionsCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Configuration
public class KafkaConsumer {

    // an example of a simple spring cloud stream kafka consumer
    @Bean
    public Consumer<Flux<String>> stringConsumer() {
        return stringFlux -> stringFlux
                .doOnNext(message -> log.info("message received by consumer: {}", message))
                .subscribe();
    }

    // spring cloud kafka consumer as a function
    @Bean
    public Function<Flux<String>, Mono<Void>> stringFunction() {
        return stringFlux -> stringFlux
                .doOnNext(message -> log.info("message received by function: {}", message))
                .then();
    }

    @Bean
    public ReceiverOptionsCustomizer<String, ReceiverOptions> receiverOptionsCustomizer() {
        return (bindingName, receiverOptions) -> {
            log.info("Kafka binding name: {}", bindingName);
            return receiverOptions.consumerProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "f8c3de3d");
        };
    }

}
