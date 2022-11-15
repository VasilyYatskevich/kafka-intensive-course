package com.epam.controller;

import com.epam.model.VehicleSignal;
import com.epam.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @PostMapping(value = "/vehicle", consumes = APPLICATION_JSON_VALUE)
    public void acceptVehicleSignal(@RequestBody VehicleSignal signal) {
        vehicleService.acceptVehicleSignal(signal);
    }
}
