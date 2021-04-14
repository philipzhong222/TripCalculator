package com.etr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etr.model.CostResponse;
import com.etr.model.VehicleTypeEnum;
import com.etr.service.CostConfigurator;

@RestController
public class TripCalculatorController {

    @Autowired
    private CostConfigurator costConfigurator;

    @GetMapping("/costoftrip")
    CostResponse getCost(
    		@RequestParam(value = "fromLocation", required = true) String fromLocation,
    		@RequestParam(value = "toLocation", required = true) String toLocation,
			@RequestParam(value = "vehicleType", required = false) VehicleTypeEnum vehicleType
			) throws IllegalArgumentException {
        
    	CostResponse resp = null;
    	
    	if(vehicleType == null)
    		resp = costConfigurator.getCostBetweenLocations(fromLocation, toLocation);
    	else
    		resp = costConfigurator.getCostWithVehicleType(fromLocation, toLocation, vehicleType.name());
    	
    	return resp;
    }

}
