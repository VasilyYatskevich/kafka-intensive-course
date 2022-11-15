package com.epam.service;

import com.epam.messaging.TraveledDistanceKafkaProducer;
import com.epam.messaging.VehicleSignalKafkaProducer;
import com.epam.model.VehicleSignal;
import com.epam.model.VehicleTraveledDistance;
import org.apache.lucene.util.SloppyMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.epam.service.VehicleTraveledDistanceStore.vehicleTraveledDistancesStore;

@Service
public class VehicleService {
    private static Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Autowired
    private VehicleSignalKafkaProducer vehicleSignalKafkaProducer;

    @Autowired
    private TraveledDistanceKafkaProducer traveledDistanceKafkaProducer;

    public void acceptVehicleSignal(VehicleSignal signal) {
        vehicleSignalKafkaProducer.send(signal);
    }

    public void calculateAndEmitDistance(VehicleSignal signal) {
        double distance = calculateVehicleTraveledDistance(signal);

        traveledDistanceKafkaProducer.send(signal.getId(), distance);
    }

    public double calculateVehicleTraveledDistance(VehicleSignal signal) {
        VehicleTraveledDistance vehicleTraveledDistance;
        if (vehicleTraveledDistancesStore.containsKey(signal.getId())) {
            VehicleTraveledDistance existing = vehicleTraveledDistancesStore.get(signal.getId());
            double distance = calculateDistance(
                    existing.getLatitude(),
                    existing.getLongitude(),
                    signal.getLatitude(),
                    signal.getLongitude()
            );
            vehicleTraveledDistance = signalToMapEntry(signal, distance);
        } else {
            vehicleTraveledDistance = signalToMapEntry(signal, 0);
        }

        vehicleTraveledDistancesStore.put(signal.getId(), vehicleTraveledDistance);

        return vehicleTraveledDistance.getTraveledDistance();
    }

    public void logDistance(String id, double distance) {
        logger.info(String.format("Vehicle %s already traveled %.2f meters", id, distance));
    }

    /**
     * Calculates distance in meters
     */
    private double calculateDistance(double lat1, double long1, double lat2, double long2) {
        return SloppyMath.haversinMeters(lat1, long1, lat2, long2);
    }

    private VehicleTraveledDistance signalToMapEntry(VehicleSignal signal, double distance) {
        return new VehicleTraveledDistance(signal.getId(), signal.getLatitude(), signal.getLongitude(), distance);
    }
}
