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
package org.societies.platform.FoursquareConnector.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.societies.platform.FoursquareConnector.impl.FoursquareConnectorImpl;

class Test {

	// static String defaultAccessTokenString =
	// "5ZAFZUGOUSFQAEDSWPCXQLJVMBFY1GDI41T5SNMUJP5B2QNA";
	static String defaultAccessTokenString = "5ZAFZUGOUSFQAEDSWPCXQLJVMBFY1GDI41T5SNMUJP5B2QNA";

	public static void main(String[] args) {
		FoursquareConnectorImpl f = new FoursquareConnectorImpl(
				defaultAccessTokenString, "yangdingqi");

//		testProfileExtraction(f);
//		testFriendsExtraction(f);
//		testCheckinsExtraction(f);
		testPostCheckins(f);
	}

	public static void testProfileExtraction(FoursquareConnectorImpl f) {
		String r = null;
		r = f.getUserProfile();
		if (r == null)
			System.out.println("user profile = null");
		else
			System.out.println(r);
	}

	public static void testFriendsExtraction(FoursquareConnectorImpl f) {
		String r = null;
		r = f.getUserFriends();
		if (r == null)
			System.out.println("user profile = null");
		else
			System.out.println(r);
	}

	public static void testCheckinsExtraction(FoursquareConnectorImpl f) {
		String r = null;
		r = f.getUserActivities();
		if (r == null)
			System.out.println("user recent checkins = null");
		else
			System.out.println(r);
	}
	
	public static void testPostCheckins(FoursquareConnectorImpl f) {
		JSONObject checkin = new JSONObject();
		try {
			checkin.put("venueId", "4d6c77ae6fb7a35df4690c0d");
			checkin.put("broadcast", "twitter");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(tweet.toString());
		f.post(checkin.toString());
	}

}

// package org.societies.platform.FoursquareConnector.impl;
//
// import java.util.*;
//
// import org.scribe.builder.*;
// import org.scribe.builder.api.*;
// import org.scribe.model.*;
// import org.scribe.oauth.*;
//
// public class Test
// {
// private static final String PROTECTED_RESOURCE_URL =
// "https://api.foursquare.com/v2/users/self?oauth_token=";
// private static final Token EMPTY_TOKEN = null;
//
// public static void main(String[] args)
// {
// // Replace these with your own api key and secret
// // String apiKey = "LTNRV3JPEKSFUCMOF4HY05GZHW4BWIZ1Y2YGBJCLMGEXZFG4";
// // String apiSecret = "2Y0YDIH5XQV13P2ZE3EWZDGEAIHXXQNMOUAEVU4XIWRYRBBS";
// String apiKey = "LTNRV3JPEKSFUCMOF4HY05GZHW4BWIZ1Y2YGBJCLMGEXZFG4";
// String apiSecret = "2Y0YDIH5XQV13P2ZE3EWZDGEAIHXXQNMOUAEVU4XIWRYRBBS";
// OAuthService service = new ServiceBuilder()
// .provider(Foursquare2Api.class)
// .apiKey(apiKey)
// .apiSecret(apiSecret)
// .callback("http://localhost:8080/examples/servlets/auth")
// .build();
// // Scanner in = new Scanner(System.in);
// //
// // System.out.println("=== Foursquare2's OAuth Workflow dingqi ===");
// // System.out.println();
// //
// // // Obtain the Authorization URL
// // System.out.println("Fetching the Authorization URL...");
// // String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
// // System.out.println("Got the Authorization URL!");
// // System.out.println("Now go and authorize Scribe here:");
// // System.out.println(authorizationUrl);
// // System.out.println("And paste the authorization code here");
// // System.out.print(">>");
// // Verifier verifier = new Verifier(in.nextLine());
// // System.out.println();
//
// String defaultAccessToken =
// "5ZAFZUGOUSFQAEDSWPCXQLJVMBFY1GDI41T5SNMUJP5B2QNA";
// // String defaultAccessToken =
// "B5CK43Y3BZZT5KQEDPFPTVY4KFX22Y3T4E4ALEK1HLILGEDY";
// Token accessToken = new Token(defaultAccessToken,"");
//
// // Trade the Request Token and Verfier for the Access Token
// // System.out.println("Trading the Request Token for an Access Token...");
// // Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
// // System.out.println("Got the Access Token!");
// // System.out.println("(if your curious it looks like this: " + accessToken +
// " )");
// // System.out.println();
//
// // Now let's go and ask for a protected resource!
// System.out.println("Now we're going to access a protected resource...");
// OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL +
// accessToken.getToken());
// service.signRequest(accessToken, request);
// Response response = request.send();
// System.out.println("Got it! Lets see what we found...");
// System.out.println();
// System.out.println(response.getCode());
// System.out.println(response.getBody());
//
// System.out.println();
// System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
//
// }
// }
