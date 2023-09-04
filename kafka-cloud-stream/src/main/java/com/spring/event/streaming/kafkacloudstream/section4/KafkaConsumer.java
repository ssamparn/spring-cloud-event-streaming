package com.spring.event.streaming.kafkacloudstream.section4;

import com.spring.event.streaming.kafkacloudstream.common.MessageConverter;
import com.spring.event.streaming.kafkacloudstream.common.MessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class KafkaConsumer {

    @Bean
    public Consumer<Flux<Message<String>>> stringConsumer() {
        return messageFlux -> messageFlux
                .map(MessageConverter::toMessageRecord)
                .doOnNext(this::printAndAcknowledgMessageDetails)
                .subscribe();
    }

    private void printAndAcknowledgMessageDetails(MessageRecord<String> record) {
        log.info("payload received by consumer: {}", record.message());
        log.info("header received by consumer: {}", record.key());
        record.acknowledgment().acknowledge();
    }
}