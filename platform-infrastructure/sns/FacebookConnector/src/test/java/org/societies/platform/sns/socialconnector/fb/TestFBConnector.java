package org.societies.platform.sns.socialconnector.fb;

import static org.junit.Assert.assertNotNull;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;

import com.restfb.json.JsonObject;

public class TestFBConnector {

	private static ISocialConnector connector = null;
	private static final Logger logger = LoggerFactory.getLogger(TestFBConnector.class);
    
	private String TEST_TOKEN = "AAAFs43XOj3IBANAuFLLTycEWZCiHLvqN1BH9f4OGyhQbWJ2GZC7D57XbITHafLpisDjx0B9OtZCx3hhoxZANUNqOl8FK6tzchZAthmjTQVwZDZD";
	
	@Before
	public void setUp() {
		//connector = new FacebookConnectorImpl("PUT HERE YOUR TOKEN", "Societies Username ");
		
		connector = new FacebookConnectorImpl(TEST_TOKEN, "societies.project@gmail.com");
		logger.info("Connector name: " + connector.getConnectorName());
		logger.info("Connector id: " + connector.getID());
		assertNotNull(connector);

	}

	@After
	public void tearDown() throws Exception {
		logger.info("Facebook test copleted");
		connector = null;
	}

	@Test
	public void getSocialFriendTest() {
		String friends = connector.getUserFriends();
		logger.info("Social Friends (JSON STRING):\n" + friends);
		assertNotNull("Social Friends (JSON STRING)", friends);
	}

	@Test
	public void getSocialProfileTest() {
		String profile = connector.getUserProfile();
		logger.info("Facebook Profile (JSON STRING):\n" + profile);
		assertNotNull("Social Profile (JSON STRING)", profile);
	}

	@Test
	public void getSocialGroupTest() {
		String groups = connector.getUserGroups();
		logger.info("Facebook Groups (JSON STRING):\n" + groups);
		assertNotNull("Social Groups (JSON STRING)", groups);
	}

	@Test
	public void getSocialActivitiesTest() {
		String activities = connector.getUserActivities();
		logger.info("Facebook activities (JSON STRING):\n" + activities);
		assertNotNull("Social activities (JSON STRING)", activities);

		logger.info(activities);
		JsonObject jactivities = new JsonObject(activities);
		assertNotNull(jactivities);
		if (jactivities.has("error"))
			logger.info("Connector return the following error:\n"
					+ jactivities.getJsonObject("error").toString(1));
		else
			logger.info("Connector return the following activities:\n"
					+ jactivities.toString(1));

	}
	
	
//	@Test
//	public void postATestMessage(){
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Date date = new Date();
//		String value="[TEST] Hello World! It's "+dateFormat.format(date);
//		connector.post(value);
//		logger.info("Facebook POST test:"+  value);
//		
//	}
//	
//	@Test
//	public void postATestEvent(){
//		
//		String value="{ \"event\": {"+
//        "\"name\": \"JUNIT Test Event\","+
//        "\"from\": \"2013-08-11\","+
//        "\"to\": \"2013-08-12\","+
//        "\"location\": \"NoWhere\","+
//        "\"description\": \"Social Network connector post test (FB)\"}"+
//        "}";
//		
//		connector.post(value);
//		logger.info("Facebook POST test: " + value);
//		
//	}
	
//	@Test
//	public void postATestCheckin(){
//		
//		String value="{ \"checkin\": {"+
//        "\"lat\": \"53.345149444145\","+
//        "\"lon\": \"-6.2539714878708\","+
//        "\"message\": \"Trinity Capital Hotel Dublin\","+
//        "\"place\": 171512719546772}"+
//        "}";
//		
//		connector.post(value);
//		logger.info("Facebook POST test:"+ value);
//		
//	}

}
