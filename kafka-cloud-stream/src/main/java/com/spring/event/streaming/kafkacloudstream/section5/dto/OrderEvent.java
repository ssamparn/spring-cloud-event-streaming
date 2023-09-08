package com.spring.event.streaming.kafkacloudstream.section5.dto;

public record OrderEvent(int customerId,
                         int productId,
                         OrderType orderType) {
}
