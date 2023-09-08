package com.spring.event.streaming.kafkacloudstream.section5.producer;

import com.spring.event.streaming.kafkacloudstream.section5.dto.OrderEvent;
import com.spring.event.streaming.kafkacloudstream.section5.dto.OrderType;
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
public class OrderEventProducerConfig {

    @Bean
    public Supplier<Flux<Message<OrderEvent>>> orderEventProducer() {
        return () -> Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .map(this::createMessageOrderEvent)
                .doOnNext(orderEventMessage -> log.info("order event message produced: {}", orderEventMessage));
    }

    private Message<OrderEvent> createMessageOrderEvent(Integer integer) {
        OrderType orderType = integer % 2 == 0 ? OrderType.DIGITAL : OrderType.PHYSICAL;
        OrderEvent orderEvent = new OrderEvent(integer, integer, orderType);

        return MessageBuilder
                .withPayload(orderEvent)
                .setHeader(KafkaHeaders.KEY, "order-id-" + integer)
                .build();
    }
}
