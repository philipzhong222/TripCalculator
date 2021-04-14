package com.etr.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.etr.model.CostResponse;
import com.etr.model.CostWithVehicleTypeResponse;
import com.etr.model.Location;
import com.etr.model.Route;
import com.etr.model.VehicleRate;
  
/**
 * 1. Keep the location configuration map 
 * 2. Provide function to retrieve distance between locations.
 * 3. Provide function to retrieve KmRate based on vehicle type
 * 
 * @author philip.zhong
 *
 */

@Service
public class CostConfigurator 
{
	
	private static Logger logger = LoggerFactory.getLogger(CostConfigurator.class);
	
	//Keep the location map
	//Key: location name;
	//Value: location which has absolute distance
	private Map<String, Location> locationNameMap = new HashMap<String, Location>();
	
	//Keep vehicle rate map
	//Key: vehicle type
	//Value: VehicleRate instance
	private Map<String, VehicleRate> vehicleRateMap = new HashMap<String, VehicleRate>();
	
	/**
	 * Return the cost between two locations.
	 * 
	 * @param fromName: the first location name
	 * @param toName: the 2nd location name.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CostResponse getCostBetweenLocations(String fromName, String toName) 
			throws IllegalArgumentException {
		
		BigDecimal distance = getDistanceBetweenLocations(fromName.trim(), toName.trim());  	
    	BigDecimal perKM = new BigDecimal(0.25);
    	BigDecimal cost = distance.multiply(perKM);
    	
    	distance = distance.setScale(3, RoundingMode.HALF_EVEN);
    	cost = cost.setScale(2, RoundingMode.HALF_EVEN);
    	
    	CostResponse resp = new CostResponse();
    	resp.setCost(cost);
    	resp.setDistance(distance);
    	
    	return resp;
	}
	
	/**
	 * Get cost based on distance and vehicle type.
	 * 
	 * The return includes total cost of distance and vehicle type.
	 * 
	 * @param fromName
	 * @param toName
	 * @param vehicleType
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CostResponse getCostWithVehicleType(String fromName, String toName, String vehicleType) 
			throws IllegalArgumentException {
		
		//Distance cost
		BigDecimal distance = getDistanceBetweenLocations(fromName.trim(), toName.trim());  	
    	BigDecimal perKM = new BigDecimal(0.25);
    	BigDecimal cost = distance.multiply(perKM);
    	
    	distance = distance.setScale(3, RoundingMode.HALF_EVEN);
    	cost = cost.setScale(2, RoundingMode.HALF_EVEN);
    	
    	//VehicleRate
    	VehicleRate vehicleRate = vehicleRateMap.get(vehicleType);
    	
    	//For direction
    	Location fromLocation = locationNameMap.get(fromName);
		Location toLocation = locationNameMap.get(toName);
    	
    	CostWithVehicleTypeResponse resp = new CostWithVehicleTypeResponse();
    	resp.setDistance(distance);
    	
    	if(Integer.parseInt(fromLocation.getId()) < Integer.parseInt(toLocation.getId()))
    		resp.setDirection("westbound");
    	else 
    		resp.setDirection("eastbound");
    	
    	//Calculate kmRate
    	if(vehicleRate != null) {
    		BigDecimal kmRate = vehicleRate.getTripTollCharge().add(vehicleRate.getCameraCharge());
    		
    		if(Integer.parseInt(fromLocation.getId()) < Integer.parseInt(toLocation.getId()))
    			kmRate = kmRate.add(vehicleRate.getWestbound());
        	else 
        		kmRate = kmRate.add(vehicleRate.getEastbound());
    		
    		resp.setKmRate(kmRate.setScale(2, RoundingMode.HALF_EVEN));
    	}
    	
    	//need to confirm the requirement
    	//tripCharge = cost + kmRate??
    	resp.setTripCharge(cost.add(resp.getKmRate()));
    	
    	return resp;
	}
	
	/**
	 * Return the distance between two locations.
	 * 
	 * @param fromName: the first location name
	 * @param toName: the 2nd location name.
	 * @return
	 * @throws IllegalArgumentException -- if the input location name doesn't exists.
	 */
	public BigDecimal getDistanceBetweenLocations(String fromName, String toName) 
			throws IllegalArgumentException {
		
		Location fromLocation = locationNameMap.get(fromName);
		Location toLocation = locationNameMap.get(toName);
		
		if(fromLocation == null || toLocation == null) {
			System.out.println("The input location name doesn't exists! ");
			listLocationNames();
			throw new IllegalArgumentException("The input location name doesn't exists.");
		}
		
		return fromLocation.getDistance().subtract(toLocation.getDistance()).abs();
	}

	
	/**
	 * Generate location map. 
	 * 
	 * Each location has the absolute distance from the first location to the current location.
	 * 
	 * @return
	 * 		false -- something wrong when generating the config map.
	 */
	
	@PostConstruct
	public boolean generateConfigMap() {
		
		Map<String, Location> localIdMap = null;
		
		try {
			localIdMap = getLocationConfig();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		} catch (ParseException e) {
			logger.error(e.getMessage());
			return false;
		}
		
		if(localIdMap == null)
			return false;
		
		//Absolute distance -- keep the distance from the first location to the current location
		BigDecimal absoluDistance = new BigDecimal(0);
		
		//To keep the location that has been gone through
		Set<String> passedLocSet = new HashSet<String>();
		
		String curId = "1";
		Location curLocation = localIdMap.get(curId);
		passedLocSet.add(curId);
		
		while(curLocation != null) {
			curLocation.setDistance(absoluDistance);
			locationNameMap.put(curLocation.getName(), curLocation);
			
			List<Route> routes = curLocation.getRoutes();
			//curLocation should be assigned to the next location
			curLocation = null;
			for(Route route: routes) {
				String toId = route.getToId();
				if(!passedLocSet.contains(toId)) {
					BigDecimal routeDistance = route.getDistance();
					absoluDistance = absoluDistance.add(routeDistance);
					passedLocSet.add(toId);
					
					curId = toId;
					curLocation = localIdMap.get(curId);
				}
			};
		}
		
		//load vehicle rate map 
		try {
			getVehicleRateConfig();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		} catch (ParseException e) {
			logger.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Read config file interchanges.json, get the location configuration information.
	 * 
	 * @return HashMap: key -- location Id; value -- location
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Location> getLocationConfig() throws IOException, ParseException
    {
    	
    	InputStream inputStream = CostConfigurator.class.getResourceAsStream("/interchanges.json");
    	Reader reader = new InputStreamReader(inputStream);

        Object obj = new JSONParser().parse(reader);
        JSONObject jo = (JSONObject) obj;
          
        // locations
        JSONObject locations = (JSONObject) jo.get("locations");
        
        Map<String, Location> locationMap = new HashMap<String, Location>();
        
        Set<JSONObject> keys = locations.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
        	//create a Location
        	Location location = new Location();
        	
        	String locationId = (String) it.next();
        	location.setId(locationId);
        	
        	JSONObject loc = (JSONObject) locations.get(locationId);
        	Iterator loIt = loc.entrySet().iterator();
        	while (loIt.hasNext()) {
                Map.Entry pair = (Entry) loIt.next();
                String loKey = (String) pair.getKey();
                if("routes".equalsIgnoreCase(loKey)) {
                	
                	//routes
                	List<Route> routes = new ArrayList<Route>();
                	
                	JSONArray routesJSON = (JSONArray) pair.getValue();
                	Iterator rouIt = routesJSON.iterator();
                	while(rouIt.hasNext()) {
                		
                		//route
                		Route route = new Route();
                		JSONObject routeJSON = (JSONObject) rouIt.next();
                		Iterator oneRouIt = routeJSON.entrySet().iterator();
                    	while (oneRouIt.hasNext()) {
                    		Map.Entry oneRoutePair = (Entry) oneRouIt.next();
                    		String routeKey = (String) oneRoutePair.getKey();
                    		Object routeVal =  oneRoutePair.getValue();
                    		
                    		//toId
                    		if("toId".equalsIgnoreCase(routeKey))
                    			route.setToId(Long.toString((Long)routeVal));
                    		if("distance".equalsIgnoreCase(routeKey)) {
                    			//distance
                    			if(routeVal instanceof Double) {
                    				double distance = (Double)routeVal;
                    				BigDecimal bd = new BigDecimal(distance);
                    				route.setDistance(bd);
                    			}
                    			else if(routeVal instanceof Long){
                    				Long distance = (Long)routeVal;
                    				BigDecimal bd = new BigDecimal(distance);
                    				route.setDistance(bd);
                    			}
                    				
                    		}
                    	}
                    	
                    	routes.add(route);
                    }
                	location.setRoutes(routes);
                }
                //other attributes -- name, lat, lng
                else if("name".equalsIgnoreCase(loKey))
                	location.setName((String)pair.getValue());
                else if("lat".equalsIgnoreCase(loKey)) {
                	Double lat = (Double)pair.getValue();
                	location.setLat(Double.toString(lat));
                }
                else if("lng".equalsIgnoreCase(loKey)) {
                	Double lng = (Double)pair.getValue();
                	location.setLng(Double.toString(lng));
                }
            }
        	
        	locationMap.put(locationId, location);
        }
        
        //logger.debug("Location size -- " + locationMap.size());
        
        return locationMap;
    }
    
    /**
     * Read rates.json file to get vehicle rate information and save in local map.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void getVehicleRateConfig() throws IOException, ParseException
    {
    	
    	InputStream inputStream = CostConfigurator.class.getResourceAsStream("/rates.json");
    	Reader reader = new InputStreamReader(inputStream);

        Object obj = new JSONParser().parse(reader);
        JSONObject jo = (JSONObject) obj;
          
        // locations
        JSONObject rates = (JSONObject) jo.get("rates");
        
        //Map<String, VehicleRate> vehicleRateMap = new HashMap<String, VehicleRate>();
        
        Set<JSONObject> keys = rates.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
        	//create a VehicleRate
        	VehicleRate vehicleRate = new VehicleRate();
        	
        	String vehicleType = (String) it.next();
        	//location.setId(locationId);
        	
        	JSONObject rate = (JSONObject) rates.get(vehicleType);
        	Iterator loIt = rate.entrySet().iterator();
        	while (loIt.hasNext()) {
                Map.Entry pair = (Entry) loIt.next();
                String loKey = (String) pair.getKey();
                Object chgVal =  pair.getValue();
                
                BigDecimal bd = null;
                if(chgVal instanceof Double) {
    				double val = (Double)chgVal;
    				bd = new BigDecimal(val);
    			}
    			else if(chgVal instanceof Long){
    				Long val = (Long)chgVal;
    				bd = new BigDecimal(val);
    			}
                
                if("trip_toll_charge".equalsIgnoreCase(loKey)) {
                	vehicleRate.setTripTollCharge(bd);
                }
                else if("camera_charge".equalsIgnoreCase(loKey)) {
                	vehicleRate.setCameraCharge(bd);
                }
                else if("eastbound".equalsIgnoreCase(loKey)) {
                	vehicleRate.setEastbound(bd.setScale(4, RoundingMode.HALF_EVEN));
                }
                else if("westbound".equalsIgnoreCase(loKey)) {
                	vehicleRate.setWestbound(bd.setScale(4, RoundingMode.HALF_EVEN));
                }
            }
        	
        	vehicleRateMap.put(vehicleType, vehicleRate);
        }
        
        logger.debug("Vehicle map size -- " + vehicleRateMap.size());
    }
    
    /**
     * Print out all exists locations
     */
    public void listLocationNames() {
    	
    	logger.debug("Current locations -- ");
    	for(String name: locationNameMap.keySet()){
    		System.out.println(name);
    	}
    }
}