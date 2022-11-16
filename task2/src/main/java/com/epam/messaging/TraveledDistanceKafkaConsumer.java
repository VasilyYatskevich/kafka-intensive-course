package com.epam.messaging;

import com.epam.service.VehicleService;
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
public class TraveledDistanceKafkaConsumer {
    private static Logger logger = LoggerFactory.getLogger(VehicleSignalKafkaConsumer.class);

    @Autowired
    private VehicleService vehicleService;

    @KafkaListener(
            topics = "#{'${topics.vehicle-output-topic-name}'.split(',')}",
            groupId = "vehicle-output-consumer-group"
    )
    public void listen(
            @Payload String data,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.CONSUMER) Consumer<?, ? > consumer,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partition
    ) {
        double distance = Double.parseDouble(data);

        logger.debug("Consumed distance event. \nConsumer: " + consumer + "\nPartition: " + partition);

        vehicleService.logDistance(key, distance);
    }
}
