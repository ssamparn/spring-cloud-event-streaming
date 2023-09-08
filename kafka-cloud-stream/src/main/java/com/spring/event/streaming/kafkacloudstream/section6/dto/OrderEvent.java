package com.spring.event.streaming.kafkacloudstream.section6.dto;

public record OrderEvent(int customerId,
                         int productId,
                         OrderType orderType) {
}
