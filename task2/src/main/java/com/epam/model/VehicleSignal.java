package com.epam.model;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Getter
public class VehicleSignal {
    @Positive
    private long id;
    @Min(-180)
    @Max(180)
    private double longitude;
    @Min(-90)
    @Max(90)
    private double latitude;
}
