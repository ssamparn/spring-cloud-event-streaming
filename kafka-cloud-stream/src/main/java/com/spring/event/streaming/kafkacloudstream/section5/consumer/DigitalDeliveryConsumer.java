package com.spring.event.streaming.kafkacloudstream.section5.consumer;

import com.spring.event.streaming.kafkacloudstream.common.MessageConverter;
import com.spring.event.streaming.kafkacloudstream.common.MessageRecord;
import com.spring.event.streaming.kafkacloudstream.section5.dto.DigitalDelivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Configuration
public class DigitalDeliveryConsumer {

    @Bean
    public Function<Flux<Message<DigitalDelivery>>, Mono<Void>> digitalDeliveryReceiver() {
        return digitalDeliveryMessageFlux -> digitalDeliveryMessageFlux
                .map(MessageConverter::toMessageRecord)
                .doOnNext(this::printDetails)
                .then();
    }

    private void printDetails(MessageRecord<DigitalDelivery> digitalDeliveryRecord) {
        log.info("digital consumer {}", digitalDeliveryRecord.message());
        digitalDeliveryRecord.acknowledgment().acknowledge();
    }

}
