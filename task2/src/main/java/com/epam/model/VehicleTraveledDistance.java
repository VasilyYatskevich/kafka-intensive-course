package com.epam.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VehicleTraveledDistance {
    private long id;
    private double latitude;
    private double longitude;
    private double traveledDistance;
}
