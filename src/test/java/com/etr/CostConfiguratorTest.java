package com.etr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.etr.model.CostResponse;
import com.etr.model.CostWithVehicleTypeResponse;
import com.etr.model.Location;
import com.etr.model.Route;
import com.etr.service.CostConfigurator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
@ActiveProfiles("test")
public class CostConfiguratorTest {
    
    @Autowired
    private CostConfigurator costConfigurator;

    @Before
    public void init() {
    }
    
    //getCostBetweenLocations()
    @Test
    public void getCostBetweenLocationsOK() throws JSONException {
    	
    	CostResponse resp = costConfigurator.getCostBetweenLocations("Dundas Street", "Westney Road");
    	BigDecimal distance = resp.getDistance();
    	BigDecimal cost = resp.getCost();
    	
    	BigDecimal expDis = new BigDecimal(109.2149000).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal expCost = new BigDecimal(27.300).setScale(2, RoundingMode.HALF_EVEN);
    	
    	assertEquals(expDis, distance);
    	assertEquals(expCost, cost);

    }
    
    //Failure case -- input location name doesn't exist.
    @Test(expected = IllegalArgumentException.class)
    public void getCostBetweenLocationsFail() throws JSONException {
    	
    	//Non-existing location, expected to get exception
    	costConfigurator.getCostBetweenLocations("Dundas Street", "ABC Road");
    }
    
    //getLocationConfig()
    @Test
    public void getLocationConfigTest() throws JSONException, IOException, ParseException {
    	
    	Map<String, Location> map = costConfigurator.getLocationConfig();
    	assertEquals(map.size(), 44);
    	
    	Location dundasStr = map.get("2");
    	assertEquals(dundasStr.getName(), "Dundas Street");
    	assertEquals(dundasStr.getLat(), "43.383554");
    	assertEquals(dundasStr.getLng(), "-79.833478");
    	
    	List<Route> routes = dundasStr.getRoutes();
    	assertTrue(routes.size() == 2);
    	for(Route route: routes) {
    		if("1".equalsIgnoreCase(route.getToId()))
    		{
    			BigDecimal expDis = new BigDecimal(6.062).setScale(3, RoundingMode.HALF_EVEN);
    			BigDecimal distance = route.getDistance().setScale(3, RoundingMode.HALF_EVEN);
    			assertEquals(expDis, distance);
    		}
    	}

    }
    
    //getDistanceBetweenLocations()
    @Test
    public void getDistanceBetweenLocationsTest() throws JSONException {
    	
    	BigDecimal distance = costConfigurator.getDistanceBetweenLocations("Dundas Street", "Westney Road");
    	BigDecimal expDis = new BigDecimal(109.2149000).setScale(3, RoundingMode.HALF_EVEN);
    	
    	assertEquals(expDis, distance.setScale(3, RoundingMode.HALF_EVEN));
    }
    
    //Failure case -- getDistanceBetweenLocations()
    @Test(expected = IllegalArgumentException.class)
    public void getDistanceBetweenLocationsFail() throws JSONException {
    	
    	BigDecimal distance = costConfigurator.getDistanceBetweenLocations("QQQ Street", "QEW");
    	BigDecimal expDis = new BigDecimal(109.2149000).setScale(3, RoundingMode.HALF_EVEN);
    	
    	assertEquals(expDis, distance.setScale(3, RoundingMode.HALF_EVEN));
    }
    
    //getCostWithVehicleType -- heavy
    @Test
    public void getCostWithVehicleTypeHeavy() throws JSONException {
    	
    	CostWithVehicleTypeResponse resp = (CostWithVehicleTypeResponse) costConfigurator.getCostWithVehicleType("QEW", "Bronte Road", "heavy");
    	BigDecimal expDis = new BigDecimal(14.062).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal expCos = new BigDecimal(51.26).setScale(2, RoundingMode.HALF_EVEN);
    	BigDecimal expKmRate = new BigDecimal(47.74).setScale(2, RoundingMode.HALF_EVEN);
    	
    	assertEquals(expDis, resp.getDistance().setScale(3, RoundingMode.HALF_EVEN));
    	assertEquals(expCos, resp.getTripCharge().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals(expKmRate, resp.getKmRate().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals("westbound", resp.getDirection());
    }
    
  //getCostWithVehicleType -- light
    @Test
    public void getCostWithVehicleTypeLight() throws JSONException {
    	
    	CostWithVehicleTypeResponse resp = (CostWithVehicleTypeResponse) costConfigurator.getCostWithVehicleType("QEW", "Bronte Road", "light");
    	BigDecimal expDis = new BigDecimal(14.062).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal expCos = new BigDecimal(9.14).setScale(2, RoundingMode.HALF_EVEN);
    	BigDecimal expKmRate = new BigDecimal(5.62).setScale(2, RoundingMode.HALF_EVEN);
    	
    	assertEquals(expDis, resp.getDistance().setScale(3, RoundingMode.HALF_EVEN));
    	assertEquals(expCos, resp.getTripCharge().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals(expKmRate, resp.getKmRate().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals("westbound", resp.getDirection());
    }
    
  //getCostWithVehicleType -- multi
    @Test
    public void getCostWithVehicleTypeMulti() throws JSONException {
    	
    	CostWithVehicleTypeResponse resp = (CostWithVehicleTypeResponse) costConfigurator.getCostWithVehicleType("QEW", "Bronte Road", "multi");
    	BigDecimal expDis = new BigDecimal(14.062).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal expCos = new BigDecimal(57.63).setScale(2, RoundingMode.HALF_EVEN);
    	BigDecimal expKmRate = new BigDecimal(54.11).setScale(2, RoundingMode.HALF_EVEN);
    	
    	assertEquals(expDis, resp.getDistance().setScale(3, RoundingMode.HALF_EVEN));
    	assertEquals(expCos, resp.getTripCharge().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals(expKmRate, resp.getKmRate().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals("westbound", resp.getDirection());
    }
    
  //getCostWithVehicleType -- direction
    @Test
    public void getCostWithVehicleTypeDirction() throws JSONException {
    	
    	CostWithVehicleTypeResponse resp = (CostWithVehicleTypeResponse) costConfigurator.getCostWithVehicleType("Bronte Road", "QEW", "multi");
    	BigDecimal expDis = new BigDecimal(14.062).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal expCos = new BigDecimal(57.82).setScale(2, RoundingMode.HALF_EVEN);
    	BigDecimal expKmRate = new BigDecimal(54.30).setScale(2, RoundingMode.HALF_EVEN);
    	
    	assertEquals(expDis, resp.getDistance().setScale(3, RoundingMode.HALF_EVEN));
    	assertEquals(expCos, resp.getTripCharge().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals(expKmRate, resp.getKmRate().setScale(2, RoundingMode.HALF_EVEN));
    	assertEquals("eastbound", resp.getDirection());
    }

}
