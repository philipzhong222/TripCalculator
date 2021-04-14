package com.etr.model;

import java.math.BigDecimal;

public class Route {
	private String toId;
	private BigDecimal distance;
	
	public String getToId() {
		return toId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	public BigDecimal getDistance() {
		return distance;
	}
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}
	
	
}
