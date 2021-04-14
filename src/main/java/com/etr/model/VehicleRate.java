package com.etr.model;

import java.math.BigDecimal;

public class VehicleRate {
	private BigDecimal tripTollCharge;
	private BigDecimal cameraCharge;
	private BigDecimal eastbound;
	private BigDecimal westbound;
	
	public BigDecimal getTripTollCharge() {
		return tripTollCharge;
	}
	public void setTripTollCharge(BigDecimal tripTollCharge) {
		this.tripTollCharge = tripTollCharge;
	}
	public BigDecimal getCameraCharge() {
		return cameraCharge;
	}
	public void setCameraCharge(BigDecimal cameraCharge) {
		this.cameraCharge = cameraCharge;
	}
	public BigDecimal getEastbound() {
		return eastbound;
	}
	public void setEastbound(BigDecimal eastbound) {
		this.eastbound = eastbound;
	}
	public BigDecimal getWestbound() {
		return westbound;
	}
	public void setWestbound(BigDecimal westbound) {
		this.westbound = westbound;
	}
	
}
