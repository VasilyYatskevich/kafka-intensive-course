package com.epam.config;

import com.epam.model.VehicleSignal;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.*;

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
    public ProducerFactory<String, Double> traveledDistanceProducerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DoubleSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ProducerFactory<String, VehicleSignal> vehicleSignalProducerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Double> traveledDistanceKafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(traveledDistanceProducerFactory(properties));
    }

    @Bean
    public KafkaTemplate<String, VehicleSignal> vehicleSignalKafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(vehicleSignalProducerFactory(properties));
    }

    @Bean
    public ConsumerFactory<String, VehicleSignal> vehicleSignalConsumerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "vehicle-input-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<VehicleSignal>().trustedPackages("*")
        );
    }

    @Bean
    public ConsumerFactory<String, Double> traveledDistanceConsumerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "vehicle-output-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DoubleDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Double> traveledDistanceListenerContainerFactory(
            KafkaProperties properties
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Double> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(traveledDistanceConsumerFactory(properties));

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VehicleSignal> vehicleSignalConcurrentListenerContainerFactory(
            KafkaProperties properties
    ) {
        ConcurrentKafkaListenerContainerFactory<String, VehicleSignal> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(vehicleSignalConsumerFactory(properties));
        factory.setConcurrency(consumersNumbers);

        return factory;
    }
}
