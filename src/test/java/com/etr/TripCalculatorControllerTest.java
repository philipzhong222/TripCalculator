package com.etr;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
public class TripCalculatorControllerTest {

    //private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void init() {}
    
    //Green case -- cost with distance only
    @Test
    public void costWithDistanceOnlyOK() throws JSONException {

        String expected = "{distance:115.277,cost:28.82}";

        ResponseEntity<String> response = restTemplate.getForEntity("/costoftrip?fromLocation=QEW&toLocation=Westney Road", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    //Failed case -- input location doesn't exist.
    @Test
    public void costFail() throws JSONException {

    	String expected = "{status:400,error:\"Bad Request\",\"message\":\"The input location name doesn't exists.\",\"path\":\"/costoftrip\"}";

        ResponseEntity<String> response = restTemplate.getForEntity("/costoftrip?fromLocation=QEW&toLocation=NONExist Road", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);

    }
    
    //Cost with Vehicle type -- multi
    @Test
    public void costWithViehicleTypeMulti() throws JSONException {

        String expected = "{distance:14.062,"
        		+ "direction:eastbound,"
        		+ "kmRate:54.30,"
        		+ "tripCharge:57.82}";

        ResponseEntity<String> response = restTemplate.getForEntity("/costoftrip?fromLocation=Bronte Road&toLocation=QEW&vehicleType=multi", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
  //Cost with Vehicle type -- light
    @Test
    public void costWithViehicleTypeLigth() throws JSONException {

        String expected = "{distance:14.062,"
        		+ "direction:eastbound,"
        		+ "kmRate:5.67,"
        		+ "tripCharge:9.19}";

        ResponseEntity<String> response = restTemplate.getForEntity("/costoftrip?fromLocation=Bronte Road&toLocation=QEW&vehicleType=light", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
  //Cost with Vehicle type -- heavy
    @Test
    public void costWithViehicleTypeHeavy() throws JSONException {

        String expected = "{distance:14.062,"
        		+ "direction:westbound,"
        		+ "kmRate:47.74,"
        		+ "tripCharge:51.26}";

        ResponseEntity<String> response = restTemplate.getForEntity("/costoftrip?fromLocation=QEW&toLocation=Bronte Road&vehicleType=heavy", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

}
