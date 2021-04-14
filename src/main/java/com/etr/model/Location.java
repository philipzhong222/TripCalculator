package com.etr.model;

import java.math.BigDecimal;
import java.util.List;

public class Location {
	private String id;
	private String name;
	private String lat;
	private String lng;
	private List<Route> routes;
	
	//Keep the distance from the first location to this location.
	//It is not gotten from config file. It is calculated based on config file.
	private BigDecimal distance;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public List<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	public BigDecimal getDistance() {
		return distance;
	}
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}
	
}
