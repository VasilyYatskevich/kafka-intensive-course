package com.epam;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaConsumer {
    public static List<String> receivedMessages = new ArrayList<>();

    @KafkaListener(topics = "${spring.kafka.topic.name}")
    public void receive(@Payload String value) {
        receivedMessages.add(value);
    }
}
