package com.spring.event.streaming.kafkacloudstream.common;

import reactor.kafka.receiver.ReceiverOffset;

public record MessageRecord<T>(String key,
                               T message,
                               ReceiverOffset acknowledgment) {
}
