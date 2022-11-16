package com.epam.messaging;

import com.epam.model.VehicleSignal;
import com.epam.service.VehicleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class VehicleSignalKafkaConsumer {
    private static Logger logger = LoggerFactory.getLogger(VehicleSignalKafkaConsumer.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper mapper;

    @KafkaListener(
            topics = "#{'${topics.vehicle-input-topic-name}'.split(',')}",
            containerFactory = "concurrentListenerContainerFactory"
    )
    public void listen(
            @Payload String data,
            @Header(KafkaHeaders.CONSUMER) Consumer<?, ? > consumer,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partition
    ) throws JsonProcessingException {
        VehicleSignal signal = mapper.readValue(data, VehicleSignal.class);

        logger.debug("Consumed vehicle event. \nConsumer: " + consumer + "\nPartition: " + partition);

        vehicleService.calculateAndEmitDistance(signal);
    }
}
