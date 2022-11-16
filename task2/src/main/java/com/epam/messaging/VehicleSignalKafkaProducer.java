package com.epam.messaging;

import com.epam.model.VehicleSignal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class VehicleSignalKafkaProducer {
    private static Logger logger = LoggerFactory.getLogger(VehicleSignalKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private ObjectMapper mapper;

    @Value("${topics.vehicle-input-topic-name}")
    private String vehicleInputTopic;

    public void send(VehicleSignal data) throws JsonProcessingException {
        String key = "id_" + data.getId();
        String value = mapper.writeValueAsString(data);
        ListenableFuture<SendResult<String, String>> send = template.send(vehicleInputTopic, key, value);

        send.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("Failed to send signal to Kafka: " + ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.debug("Successfully sent signal to Kafka: " + getFormattedMetadata(result));
            }
        });
    }

    private String getFormattedMetadata(SendResult<String, String> result) {
        RecordMetadata metadata = result.getRecordMetadata();

        return "\nKey: " + result.getProducerRecord().key() +
                "\nTopic: " + metadata.topic() +
                "\nPartition: " + metadata.partition() +
                "\nOffset: " + metadata.offset();
    }
}
