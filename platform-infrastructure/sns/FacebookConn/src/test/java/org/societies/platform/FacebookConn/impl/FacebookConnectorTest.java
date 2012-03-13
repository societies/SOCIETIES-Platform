package org.societies.platform.FacebookConn.impl;

import junit.framework.TestCase;

import org.junit.Test;
import org.societies.platform.FacebookConn.SocialConnector;

public class FacebookConnectorTest extends TestCase{

	@Test
	public void test() {
		SocialConnector facebook = new FacebookConnectorImpl("", "");
		assertTrue(facebook.getConnectorName(), facebook.getConnectorName()==SocialConnector.FACEBOOK_CONN);
		
    
	}

}
