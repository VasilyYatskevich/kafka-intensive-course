package com.epam.messaging;

import com.epam.model.VehicleSignal;
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
public class VehicleSignalKafkaConsumer {
    private static Logger logger = LoggerFactory.getLogger(VehicleSignalKafkaConsumer.class);

    @Autowired
    private VehicleService vehicleService;

    @KafkaListener(topics = "#{'${topics.vehicle-input-topic-name}'.split(',')}", containerFactory = "vehicleSignalConcurrentListenerContainerFactory")
    public void listen(
            @Payload VehicleSignal signal,
            @Header(KafkaHeaders.CONSUMER) Consumer<?, ? > consumer,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partition
    ) {
        logger.debug("Consumed vehicle event. \nConsumer name: " + consumer + "\nPartition: " + partition);

        vehicleService.calculateAndEmitDistance(signal);
    }
}
