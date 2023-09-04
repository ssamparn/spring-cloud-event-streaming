package com.spring.event.streaming.kafkacloudstream;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest(
        properties = {
                "spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest"
        }
)
@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
public abstract class AbstractIntegrationTest {

}
