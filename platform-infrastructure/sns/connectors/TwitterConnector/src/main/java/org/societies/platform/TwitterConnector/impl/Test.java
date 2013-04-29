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
package org.societies.platform.TwitterConnector.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.societies.platform.TwitterConnector.TwitterConnector;

class Test {

	public static void main(String[] args) {
		String Token = "787965889-emwjMz8srYln1zlsi8ncfUF5iAigKDCHLNZPP7WS,3Lb5hvuxKABvHkvYVwbCaTJz5TT6RknRh2Ady8UTKw";
		TwitterConnectorImpl t = new TwitterConnectorImpl(Token, "dingqi");

		testProfileExtraction(t);
		testFriendsExtraction(t);
		
		
		
//		testTweetPost(t);
//		testFollowersExtraction(t);
		testTweetsExtraction(t);
	}

	public static void testProfileExtraction(TwitterConnector t) {
		String r = null;
		r = t.getUserProfile();
		if (r == null)
			System.out.println("user profile = null");
		else
			System.out.println(r);
	}

	public static void testFriendsExtraction(TwitterConnector t) {
		String r = null;
		r = t.getUserFriends();
		if (r == null)
			System.out.println("connection error");
		else
			System.out.println(r);
	}

//	public static void testFollowersExtraction(TwitterConnector t) {
//		String r = null;
//		r = t.getUserFollowers();
//		if (r == null)
//			System.out.println("connection error");
//		else
//			System.out.println(r);
//	}

	public static void testTweetsExtraction(TwitterConnector t) {
		String r = null;
		r = t.getUserActivities();
		if (r == null)
			System.out.println("connection error");
		else
			System.out.println(r);
	}
	
	public static void testTweetPost(TwitterConnector t){
//		ActivityEntry entry = new ActivityEntryImpl();
//		entry.setId("dingqi");
//		entry.setContent("Hi, I am a tester");
//		System.out.println(entry.toString());
		
		JSONObject tweet = new JSONObject();
		try {
			tweet.put("status", "Hi, I am a tester!");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(tweet.toString());
		t.post(tweet.toString());
		
		
	}

}
