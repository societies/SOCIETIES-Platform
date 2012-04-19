package org.societies.platform.sns.socialconnector.fs;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FoursquareConnector.impl.FoursquareConnectorImpl;

public class TestFSConnector {

	private static ISocialConnector connector = null;
	private static final Logger logger = Logger.getLogger(TestFSConnector.class
			.getSimpleName());
	static String defaultAccessTokenString = "5ZAFZUGOUSFQAEDSWPCXQLJVMBFY1GDI41T5SNMUJP5B2QNA";

	@BeforeClass
	public static void initConnector() {
		connector = new FoursquareConnectorImpl(defaultAccessTokenString,
				"yangdingqi");
		logger.info("Connector name: " + connector.getConnectorName());
		logger.info("Connector id: " + connector.getID());
		assertNotNull(connector);

	}

	// @Test
	// public void getSocialCheckinTest(){
	// String checkins = connector.getRecentCheckins();
	// logger.info("Social Friends (JSON):\n" + checkins);
	// assertNotNull("Social Friends (JSON STRING)", checkins);
	// }

	// @Test
	// public void getSocialFollowerTest(){
	// String followers = connector.getUserFollowers();
	// logger.info("Social Followers (JSON):\n" + followers);
	// assertNotNull("Social Followers (JSON STRING)", followers);
	// }

	@Test
	public void getSocialProfileTest() {
		String profile = connector.getUserProfile();
		logger.info("Social Profile (JSON STRING):\n" + profile);
		assertNotNull("Social Profile (JSON STRING)", profile);
	}

}
