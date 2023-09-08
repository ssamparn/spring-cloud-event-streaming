package com.spring.event.streaming.kafkacloudstream.section7.dto;

public record OrderEvent(int customerId,
                         int productId,
                         OrderType orderType) {
}