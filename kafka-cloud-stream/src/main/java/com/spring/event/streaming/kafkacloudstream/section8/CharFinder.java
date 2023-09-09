package com.spring.event.streaming.kafkacloudstream.section8;

import com.spring.event.streaming.kafkacloudstream.common.MessageConverter;
import com.spring.event.streaming.kafkacloudstream.common.MessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

@Slf4j
@Configuration
public class CharFinder {

    private static final String DLQ_TOPIC = "dlq-topic";

    // Here we are going to use stream bridge as a dead letter topic producer
    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Function<Flux<Message<String>>, Flux<Character>> characterProcessor() {
        return stringFlux -> stringFlux
                .map(MessageConverter::toMessageRecord)
                .delayElements(Duration.ofSeconds(1))
                .concatMap(messageRecord -> this.find(messageRecord.message())
                        .onErrorResume(ex -> Mono.fromRunnable(() -> this.handleError(ex, messageRecord)))
                        .doAfterTerminate(() -> messageRecord.acknowledgment().acknowledge())
                );
    }

    private Mono<Character> find(String message) {
        return Mono.just(message)
                .map(m -> m.charAt(3));
    }

    private void handleError(Throwable ex, MessageRecord<String> messageRecord) {
        log.error(ex.getMessage());
        this.streamBridge.send(
                DLQ_TOPIC,
                MessageBuilder.withPayload(messageRecord.message())
                        .setHeader(KafkaHeaders.KEY, messageRecord.key())
                        .build()
        );
    }
}
