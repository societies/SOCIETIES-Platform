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
package org.societies.platform.sns.connector.googleplus;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.internal.sns.ISocialConnector;

@Ignore
public class GooglePlusConnectorTest {
		
	// get your token at http://dev.lucasimone.eu
	private final String TEST_TOKEN = "";

	private ISocialConnector connector = null;
	
	@Before
	public void setUp() {
		connector = new GooglePlusConnector(TEST_TOKEN, "societies.project@gmail.com");
	}

	@After
	public void tearDown() throws Exception {
		connector = null;
	}

	@Test
	public void testCreateConnector() {
		assertThat(connector, is(notNullValue()));
		assertThat(connector.getID(), is(not(equalTo(""))));
	}
	
	@Test
	public void testGetUserProfile() throws Exception {
		String profileJson = connector.getUserProfile();
		
		System.out.println("getUserProfile="+profileJson);
		
		assertTrue(new JSONObject(profileJson).has("name"));
	}

	@Test
	public void testGetActivities() throws Exception {
		String json = connector.getUserActivities();
		
		System.out.println("getUserActivities="+json);
		
		assertTrue(new JSONObject(json).has("title"));
	}
	
	@Test
	public void testGetSocialPath() throws Exception {
		String path = connector.getSocialData(GooglePlusConnector.PROFILE_PATH);
		String profile = connector.getUserProfile();
		
		assertThat(new JSONObject(path).getString("id"), is(equalTo(new JSONObject(profile).getString("id"))));
	}
	
	@Test
	public void testParameters() throws Exception {
		final int POST_LIMIT = 2;
		
		connector.setMaxPostLimit(POST_LIMIT);
		connector.setParameter("query", "edgar");
		
		String json = connector.getSocialData("people");
		
		JSONObject jsonObject = new JSONObject(json);
		
		assertThat(jsonObject.getJSONArray("items").length(), is(equalTo(POST_LIMIT)));
	}
	
}
