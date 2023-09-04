package com.spring.event.streaming.kafkacloudstream.common;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import reactor.kafka.receiver.ReceiverOffset;

public class MessageConverter {

    public static <T> MessageRecord<T> toMessageRecord(Message<T> message) {
        T payload = message.getPayload();
        String key = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
        ReceiverOffset acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class);
        return new MessageRecord<>(key, payload, acknowledgment);
    }
}
