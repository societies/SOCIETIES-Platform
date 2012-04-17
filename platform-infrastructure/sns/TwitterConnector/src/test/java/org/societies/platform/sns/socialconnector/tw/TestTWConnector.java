package org.societies.platform.sns.socialconnector.tw;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.TwitterConnector.impl.TwitterConnectorImpl;


public class TestTWConnector {

	
    private static ISocialConnector connector  = null;
	private static final Logger logger   = Logger.getLogger(TestTWConnector.class.getSimpleName());
	
	
	@BeforeClass
	public static void initConnector() {
		connector = new TwitterConnectorImpl();
		
		logger.info("Connector name: "+ connector.getConnectorName());
		logger.info("Connector id: "+ connector.getID());
		assertNotNull(connector);
	
	}
	
	@Test
	public void getSocialFriendTest(){
		String friends = connector.getUserFriends();
		logger.info("Social Friends (JSON):\n" + friends);
		assertNotNull("Social Friends (JSON STRING)", friends);
	}
	
//	@Test
//	public void getSocialFollowerTest(){
//		String followers = connector.getUserFollowers();
//		logger.info("Social Followers (JSON):\n" + followers);
//		assertNotNull("Social Followers (JSON STRING)", followers);
//	}
	
	@Test
	public void getSocialProfileTest(){
		String profile = connector.getUserProfile();
		logger.info("Facebook Profile (JSON STRING):\n" + profile);
		assertNotNull("Social Profile (JSON STRING)", profile);
	}
	
		
	
}
