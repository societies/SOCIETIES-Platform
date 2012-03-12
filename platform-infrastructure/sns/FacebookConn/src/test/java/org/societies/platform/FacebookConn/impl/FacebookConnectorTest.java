package org.societies.platform.FacebookConn.impl;

import junit.framework.TestCase;

import org.junit.Test;
import org.societies.platform.FacebookConn.Connector;

public class FacebookConnectorTest extends TestCase{

	@Test
	public void test() {
		Connector facebook = new FacebookConnectorImpl("", "");
		assertTrue(facebook.getConnectorName(), facebook.getConnectorName()==Connector.FACEBOOK_CONN);
		
    
	}

}
