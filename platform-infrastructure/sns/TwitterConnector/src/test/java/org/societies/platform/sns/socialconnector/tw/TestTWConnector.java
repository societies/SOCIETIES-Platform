/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.platform.sns.socialconnector.tw;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.TwitterConnector.impl.TwitterConnectorImpl;

public class TestTWConnector {

	private static ISocialConnector connector = null;
	private static final Logger logger = Logger.getLogger(TestTWConnector.class
			.getSimpleName());
	// final static String Token =
	// "468234144-7jBtrulMAriO1yjg2J9POY6aeW2TwnwrEXWeDWYn,1lY5pClLbeJ2MGC8A9995Dlx7gxNqdnLPQarsplwLpU";
	final static String Token = "";

	@Before
	public void setUp() {
		connector = new TwitterConnectorImpl(Token, "dingqi");

		logger.info("Connector name: " + connector.getConnectorName());
		logger.info("Connector id: " + connector.getID());
		assertNotNull(connector);

	}

	@Test
	public void getSocialFriendTest() {
		String friends = connector.getUserFriends();
		logger.info("Social Friends (JSON):\n" + friends);
		assertNotNull("Social Friends (JSON):\n", friends);
	}

	// @Test
	// public void getSocialFollowerTest(){
	// String followers = connector.getUserFollowers();
	// logger.info("Social Followers (JSON):\n" + followers);
	// assertNotNull("Social Followers (JSON STRING)", followers);
	// }

	@Test
	public void getSocialProfileTest() {
		String profile = connector.getUserProfile();
		logger.info("Social Profile (JSON):\n" + profile);
		assertNotNull("Social Profile (JSON):\n", profile);
	}

	
	
	@Test
	public void postTest(){
		
		try{
			JSONObject post = new JSONObject("activity");
			post.put("status", "I'm feel good!");
			
			connector.post(post.toString());
		}
		catch(Exception ex){}
		
		
	}
	
	
	@After
	public void tearDown() throws Exception {
		connector = null;
	}

}
