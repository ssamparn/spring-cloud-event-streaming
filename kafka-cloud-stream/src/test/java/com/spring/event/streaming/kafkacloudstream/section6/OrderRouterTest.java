package com.spring.event.streaming.kafkacloudstream.section6;

import com.spring.event.streaming.kafkacloudstream.AbstractIntegrationTest;
import com.spring.event.streaming.kafkacloudstream.section6.dto.DigitalDelivery;
import com.spring.event.streaming.kafkacloudstream.section6.dto.OrderEvent;
import com.spring.event.streaming.kafkacloudstream.section6.dto.OrderType;
import com.spring.event.streaming.kafkacloudstream.section6.dto.PhysicalDelivery;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestPropertySource(properties = {
        "section=section6",
        "spring.cloud.function.definition=testOrderEventProducer;orderProcessor;testDigitalDeliveryConsumer;testPhysicalDeliveryConsumer",
        "spring.cloud.stream.bindings.testOrderEventProducer-out-0.destination=order-events-topic",
        "spring.cloud.stream.bindings.testDigitalDeliveryConsumer-in-0.destination=digital-delivery-topic",
        "spring.cloud.stream.bindings.testPhysicalDeliveryConsumer-in-0.destination=physical-delivery-topic"
})
public class OrderRouterTest extends AbstractIntegrationTest {

    private static final Sinks.Many<OrderEvent> orderEventSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<DigitalDelivery> digitalDeliverySink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<PhysicalDelivery> physicalDeliverySink = Sinks.many().unicast().onBackpressureBuffer();

    @Test
    public void orderRouterTest() {

        // produce messages
        orderEventSink.tryEmitNext(new OrderEvent(1, 123, OrderType.DIGITAL));
        orderEventSink.tryEmitNext(new OrderEvent(2, 564, OrderType.PHYSICAL));
        orderEventSink.tryEmitNext(new OrderEvent(3, 732, OrderType.DIGITAL));
        orderEventSink.tryEmitNext(new OrderEvent(4, 688, OrderType.PHYSICAL));

        // consumer messages

        digitalDeliverySink.asFlux()
                .take(2)
                .as(StepVerifier::create)
                .consumeNextWith(digitalDeliveryProduct -> assertEquals(123,  digitalDeliveryProduct.productId()))
                .consumeNextWith(digitalDeliveryProduct -> assertEquals(732,  digitalDeliveryProduct.productId()))
                .verifyComplete();

        physicalDeliverySink.asFlux()
                .take(2)
                .as(StepVerifier::create)
                .consumeNextWith(physicalDeliveryProduct -> assertEquals(564,  physicalDeliveryProduct.productId()))
                .consumeNextWith(physicalDeliveryProduct -> assertEquals(688,  physicalDeliveryProduct.productId()))
                .verifyComplete();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Supplier<Flux<OrderEvent>> testOrderEventProducer() {
            return orderEventSink::asFlux;
        }

        @Bean
        public Consumer<Flux<DigitalDelivery>> testDigitalDeliveryConsumer() {
            return digitalDeliveryFlux -> digitalDeliveryFlux
                    .doOnNext(digitalDeliverySink::tryEmitNext)
                    .subscribe();
        }

        @Bean
        public Consumer<Flux<PhysicalDelivery>> testPhysicalDeliveryConsumer() {
            return physicalDeliveryFlux -> physicalDeliveryFlux
                    .doOnNext(physicalDeliverySink::tryEmitNext)
                    .subscribe();
        }
    }
}