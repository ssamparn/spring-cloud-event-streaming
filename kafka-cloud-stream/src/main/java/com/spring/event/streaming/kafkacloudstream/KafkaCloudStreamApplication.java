package com.spring.event.streaming.kafkacloudstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.spring.event.streaming.kafkacloudstream.${section}")
public class KafkaCloudStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaCloudStreamApplication.class, args);
	}

}
