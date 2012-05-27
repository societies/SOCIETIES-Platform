package org.societies.platform.sns.socialconnector.fb;

import static org.junit.Assert.assertNotNull;

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

	@Before
	public void setUp() {
		connector = new FacebookConnectorImpl("PUT HERE YOUR TOKEN", "Luca");

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
		String profile = connector.getUserFriends();
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
	
	
	@Test
	public void postTest(){
		
		logger.info("Facebook POST test: this method is not yet implemented... please wait!");
		
	}

}
