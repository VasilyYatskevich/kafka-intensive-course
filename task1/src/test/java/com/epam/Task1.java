package com.epam;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static com.epam.KafkaConsumer.receivedMessages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
class Task1 {
    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private KafkaProducer kafkaProducer;

    @SpyBean
    private KafkaConsumer kafkaConsumer;

    @Test
    void testProduceAndConsumeKafkaMessage() {
        List<String> producedMessages = List.of("test", "Belarus", "hi", "my name is", "dog");

        producedMessages.forEach(it -> kafkaProducer.writeToKafka(it));

        verify(kafkaConsumer, timeout(10000).times(producedMessages.size())).receive(any());

        assertEquals(producedMessages, receivedMessages);
    }
}
