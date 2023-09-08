package com.spring.event.streaming.kafkacloudstream.section5.processor;

import com.spring.event.streaming.kafkacloudstream.common.MessageConverter;
import com.spring.event.streaming.kafkacloudstream.section5.dto.DigitalDelivery;
import com.spring.event.streaming.kafkacloudstream.section5.dto.OrderEvent;
import com.spring.event.streaming.kafkacloudstream.section5.dto.PhysicalDelivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;


/**
 * Order Router class will consume the events from kafka producer and provide it to kafka consumer
 */
@Slf4j
@Configuration
public class OrderRouter {

    private static final String DIGITAL_DELIVERY_CHANNEL = "digital-delivery-out";
    private static final String PHYSICAL_DELIVERY_CHANNEL = "physical-delivery-out";

    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Function<Flux<Message<OrderEvent>>, Mono<Void>> orderProcessor() {
        return orderEventMessageFlux -> orderEventMessageFlux
                .map(MessageConverter::toMessageRecord)
                .doOnNext(messageRecord -> this.route(messageRecord.message()))
                .doOnNext(messageRecord -> messageRecord.acknowledgment().acknowledge())
                .then();
    }

    private void route(OrderEvent orderEvent) {
        switch (orderEvent.orderType()) {
            case DIGITAL -> this.toDigitalDelivery(orderEvent);
            case PHYSICAL -> this.toPhysicalDelivery(orderEvent);
        }
    }

    private void toDigitalDelivery(OrderEvent orderEvent) {
        DigitalDelivery digitalDelivery = new DigitalDelivery(orderEvent.productId(), "%s@gmail.com".formatted(orderEvent.customerId()));
        // Stream bridge will create below binding at runtime. Config is provided in application.yml file
        this.streamBridge.send(DIGITAL_DELIVERY_CHANNEL, digitalDelivery);
    }

    private void toPhysicalDelivery(OrderEvent orderEvent) {
        PhysicalDelivery physicalDelivery = new PhysicalDelivery(orderEvent.productId(), "Neuweg", "Hilversum", "%s@gmail.com".formatted(orderEvent.customerId()));
        // Stream bridge will create below binding at runtime. Config is provided in application.yml file
        this.streamBridge.send(PHYSICAL_DELIVERY_CHANNEL, physicalDelivery);
    }

}
