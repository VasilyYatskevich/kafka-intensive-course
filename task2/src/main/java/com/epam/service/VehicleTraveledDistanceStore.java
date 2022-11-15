package com.epam.service;

import com.epam.model.VehicleTraveledDistance;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VehicleTraveledDistanceStore {
    public static Map<Long, VehicleTraveledDistance> vehicleTraveledDistancesStore = new HashMap<>();
}
