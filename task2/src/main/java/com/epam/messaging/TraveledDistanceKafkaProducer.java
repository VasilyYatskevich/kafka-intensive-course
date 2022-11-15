package com.epam.messaging;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class TraveledDistanceKafkaProducer {
    private static Logger logger = LoggerFactory.getLogger(VehicleSignalKafkaProducer.class);

    @Qualifier("traveledDistanceKafkaTemplate")
    @Autowired
    private KafkaTemplate<String, Double> template;

    @Value("${topics.vehicle-output-topic-name}")
    private String vehicleInputTopic;

    public void send(Long id, Double distance) {
        String key = "id_" + id;
        ListenableFuture<SendResult<String, Double>> send = template.send(vehicleInputTopic, key, distance);

        send.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("Failed to send distance event to Kafka: " + ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Double> result) {
                logger.debug("Successfully sent signal to Kafka: " + getFormattedMetadata(result));
            }
        });
    }

    private String getFormattedMetadata(SendResult<String, Double> result) {
        RecordMetadata metadata = result.getRecordMetadata();

        return "\nSignal id: " + result.getProducerRecord().key() +
                "\nTopic: " + metadata.topic() +
                "\nPartition: " + metadata.partition() +
                "\nOffset: " + metadata.offset();
    }
}
