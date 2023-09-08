package com.spring.event.streaming.kafkacloudstream.section6.consumer;

import com.spring.event.streaming.kafkacloudstream.common.MessageConverter;
import com.spring.event.streaming.kafkacloudstream.common.MessageRecord;
import com.spring.event.streaming.kafkacloudstream.section6.dto.PhysicalDelivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Configuration
public class PhysicalDeliveryConsumer {

    @Bean
    public Function<Flux<Message<PhysicalDelivery>>, Mono<Void>> physicalDeliveryReceiver() {
        return physicalDeliveryMessageFlux -> physicalDeliveryMessageFlux
                .map(MessageConverter::toMessageRecord)
                .doOnNext(this::printDetails)
                .then();
    }

    private void printDetails(MessageRecord<PhysicalDelivery> physicalDeliveryRecord) {
        log.info("physical consumer {}", physicalDeliveryRecord.message());
        physicalDeliveryRecord.acknowledgment().acknowledge();
    }

}
