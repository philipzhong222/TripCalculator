package com.etr.model;

public enum VehicleTypeEnum {
	
	light("light"), heavy("heavy"), multi("multi");
	
	private final String type;
	
	private VehicleTypeEnum(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
}