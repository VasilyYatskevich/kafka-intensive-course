package com.epam.messaging;

import com.epam.model.VehicleSignal;
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
public class VehicleSignalKafkaProducer {
    private static Logger logger = LoggerFactory.getLogger(VehicleSignalKafkaProducer.class);

    @Qualifier("vehicleSignalKafkaTemplate")
    @Autowired
    private KafkaTemplate<String, VehicleSignal> template;

    @Value("${topics.vehicle-input-topic-name}")
    private String vehicleInputTopic;

    public void send(VehicleSignal data) {
        String key = "id_" + data.getId();
        ListenableFuture<SendResult<String, VehicleSignal>> send = template.send(vehicleInputTopic, key, data);

        send.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("Failed to send signal to Kafka: " + ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, VehicleSignal> result) {
                logger.debug("Successfully sent signal to Kafka: " + getFormattedMetadata(result));
            }
        });
    }

    private String getFormattedMetadata(SendResult<String, VehicleSignal> result) {
        RecordMetadata metadata = result.getRecordMetadata();

        return "\nKey: " + result.getProducerRecord().key() +
                "\nTopic: " + metadata.topic() +
                "\nPartition: " + metadata.partition() +
                "\nOffset: " + metadata.offset();
    }
}
