package com.etr.model;

import java.math.BigDecimal;

public class CostWithVehicleTypeResponse extends CostResponse{

	private String direction;
	private BigDecimal kmRate;
	private BigDecimal tripCharge;
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public BigDecimal getKmRate() {
		return kmRate;
	}
	public void setKmRate(BigDecimal kmRate) {
		this.kmRate = kmRate;
	}
	public BigDecimal getTripCharge() {
		return tripCharge;
	}
	public void setTripCharge(BigDecimal tripCharge) {
		this.tripCharge = tripCharge;
	}
	

}
