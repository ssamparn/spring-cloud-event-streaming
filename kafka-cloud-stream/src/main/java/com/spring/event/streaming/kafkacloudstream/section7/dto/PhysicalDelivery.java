package com.spring.event.streaming.kafkacloudstream.section7.dto;

public record PhysicalDelivery(int productId,
                               String street,
                               String city,
                               String country) {
}
