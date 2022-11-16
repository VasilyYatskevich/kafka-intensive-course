package com.epam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
public class KafkaConfig {
    @Value("${topics.vehicle-input-topic-name}")
    private String vehicleInputTopic;

    @Value("${topics.vehicle-output-topic-name}")
    private String vehicleOutputTopic;

    @Value("${topics.partitions}")
    private int partitions;

    @Value("${topics.replicationFactor}")
    private int replicationFactor;

    @Value("${topics.consumersNumber}")
    private int consumersNumbers;

    @Bean
    public NewTopic inputTopic() {
        return TopicBuilder.name(vehicleInputTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    public NewTopic outputTopic() {
        return TopicBuilder.name(vehicleOutputTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> concurrentListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(consumersNumbers);

        return factory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
