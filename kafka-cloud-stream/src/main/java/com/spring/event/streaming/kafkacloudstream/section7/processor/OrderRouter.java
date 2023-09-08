package com.spring.event.streaming.kafkacloudstream.section7.processor;

import com.spring.event.streaming.kafkacloudstream.common.MessageConverter;
import com.spring.event.streaming.kafkacloudstream.common.MessageRecord;
import com.spring.event.streaming.kafkacloudstream.section7.dto.DigitalDelivery;
import com.spring.event.streaming.kafkacloudstream.section7.dto.OrderEvent;
import com.spring.event.streaming.kafkacloudstream.section7.dto.PhysicalDelivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Function;


/**
 * Order Router class will consume the events from kafka producer and provide it to kafka consumer
 */
@Slf4j
@Configuration
public class OrderRouter {

    private static final String DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
    private static final String DIGITAL_DELIVERY_TOPIC = "digital-delivery-topic";
    private static final String PHYSICAL_DELIVERY_TOPIC = "physical-delivery-topic";

    @Bean
    public Function<Flux<Message<OrderEvent>>, Flux<Message<?>>> orderProcessor() {
        return orderEventMessageFlux -> orderEventMessageFlux
                .map(MessageConverter::toMessageRecord)
                .map(this::route);
    }

    private Message<?> route(MessageRecord<OrderEvent> orderEventMessageRecord) {
        Message message = switch (orderEventMessageRecord.message().orderType()) {
            case DIGITAL -> this.toDigitalDelivery(orderEventMessageRecord.message());
            case PHYSICAL -> this.toPhysicalDelivery(orderEventMessageRecord.message());
        };
        orderEventMessageRecord.acknowledgment().acknowledge();

        return message;
    }

    private Message<DigitalDelivery> toDigitalDelivery(OrderEvent orderEvent) {
        DigitalDelivery digitalDelivery = new DigitalDelivery(orderEvent.productId(), "%s@gmail.com".formatted(orderEvent.customerId()));
        return MessageBuilder
                .withPayload(digitalDelivery)
                .setHeader(DESTINATION_HEADER, DIGITAL_DELIVERY_TOPIC)
                .build();
    }

    private Message<PhysicalDelivery> toPhysicalDelivery(OrderEvent orderEvent) {
        PhysicalDelivery physicalDelivery = new PhysicalDelivery(orderEvent.productId(), "Neuweg", "Hilversum", "%s@gmail.com".formatted(orderEvent.customerId()));
        return MessageBuilder
                .withPayload(physicalDelivery)
                .setHeader(DESTINATION_HEADER, PHYSICAL_DELIVERY_TOPIC)
                .build();
    }
}