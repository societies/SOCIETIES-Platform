package org.societies.platform.FacebookConn.impl;

import junit.framework.TestCase;

import org.junit.Test;
import org.societies.api.internal.sns.ISocialConnector;

public class FacebookConnectorTest extends TestCase{

	@Test
	public void test() {
		ISocialConnector facebook = new FacebookConnectorImpl("", "");
		assertTrue(facebook.getConnectorName(), facebook.getConnectorName()==ISocialConnector.FACEBOOK_CONN);
	}

}
