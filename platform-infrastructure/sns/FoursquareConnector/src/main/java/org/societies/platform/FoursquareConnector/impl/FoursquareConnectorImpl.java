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

import java.util.Map;
import java.util.UUID;

//import org.json.simple.*;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FoursquareConnector.FoursquareConnector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Foursquare connector implementation
 * 
 * dingqi yang
 */
public class FoursquareConnectorImpl implements FoursquareConnector {

	private String accessTokenString = null;
	private Token accessToken = null;
	private String identity = null;
	private String name;
	private String id;
	private String lastUpdate = "yesterday";
	private long tokenExpiration = 0;
	

	private String apiKey = "LTNRV3JPEKSFUCMOF4HY05GZHW4BWIZ1Y2YGBJCLMGEXZFG4";
	private String apiSecret = "2Y0YDIH5XQV13P2ZE3EWZDGEAIHXXQNMOUAEVU4XIWRYRBBS";

	private OAuthService service;

	public FoursquareConnectorImpl() {
	};

	public FoursquareConnectorImpl(String access_token, String identity) {
		this.accessTokenString = access_token;
		this.identity = identity;
		
		this.name = ISocialConnector.FOURSQUARE_CONN;
		this.id = this.name + "_" + UUID.randomUUID();
		this.service = new ServiceBuilder()
				.provider(Foursquare2Api.class)
				.apiKey(apiKey)
				.apiSecret(apiSecret)
				.callback(
						"http://157.159.160.188:8080/examples/servlets/servlet/FoursquareOauth")
				.build();
		this.accessToken = new Token(this.accessTokenString, "");
	}

	public String getUserProfile() {
		OAuthRequest request = new OAuthRequest(Verb.GET, USER_PROFILE
				+ accessToken.getToken());
		this.service.signRequest(accessToken, request);
		Response response = request.send();
		JSONObject res = null;
		try {
			res = new JSONObject(response.getBody());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return response.getBody();
		}
		if (res != null)
			return res.toString();
		else
			return null;
	}

	public String getUserFriends() {
		OAuthRequest request = new OAuthRequest(Verb.GET, USER_PROFILE
				+ accessToken.getToken());
		this.service.signRequest(accessToken, request);
		Response response = request.send();
		JSONObject res = null;
		try {
			res = new JSONObject(response.getBody());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return response.getBody();
		}
		if (res != null)
			return res.toString();
		else
			return null;
	}

	/*
	 * Activity in foursquare is defined as check-in
	 * 
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserActivities()
	 */
	public String getUserActivities() {
		OAuthRequest request = new OAuthRequest(Verb.GET, RECENT_CHECKINS
				+ accessToken.getToken());
		this.service.signRequest(accessToken, request);
		Response response = request.send();
		JSONObject res = null;
		try {
			res = new JSONObject(response.getBody());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return response.getBody();
		}
		if (res != null)
			return res.toString();
		else
			return null;
	}

	public String getID() {
		return this.id;
	}

	public void setToken(String access_token) {
		this.accessTokenString = access_token;
		this.accessToken = new Token(this.accessTokenString, "");

	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setTokenExpiration(long expires) {
		this.tokenExpiration = expires;
	}

	public void setConnectorName(String name) {
		this.name = name;

	}

	public String getConnectorName() {
		return name;
	}

	public long getTokenExpiration() {
		return this.tokenExpiration;
	}

	public String getToken() {
		return this.accessTokenString;
	}

	public String getSocialData(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> requireAccessToken() {
		// TODO Auto-generated method stub
		return null;
	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

	public void setMaxPostLimit(int postLimit) {
		// TODO Auto-generated method stub

	}

	public void setParameter(String key, String value) {
		// TODO Auto-generated method stub

	}

	public void resetParameters() {
		// TODO Auto-generated method stub

	}

	public String getUserGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#post(java.lang.String)
	 */
	@Override
	public void post(String activity) {
		JSONObject checkin = null;
		String res = null;
		try {
			checkin = new JSONObject(activity);
			OAuthRequest request = new OAuthRequest(Verb.POST, POST_CHECKINS+ accessToken.getToken());
			if (checkin.has("venueId"))
				request.addBodyParameter("venueId", checkin.getString("venueId"));
			if (checkin.has("eventId"))
				request.addBodyParameter("eventId", checkin.getString("eventId"));
			if (checkin.has("shout"))
				request.addBodyParameter("shout", checkin.getString("shout"));
			if (checkin.has("broadcast"))
				request.addBodyParameter("broadcast", checkin.getString("broadcast"));
			if (checkin.has("ll"))
				request.addBodyParameter("ll", checkin.getString("ll"));
			if (checkin.has("llAcc"))
				request.addBodyParameter("llAcc", checkin.getString("llAcc"));
			if (checkin.has("alt"))
				request.addBodyParameter("alt", checkin.getString("alt"));
			if (checkin.has("altAcc"))
				request.addBodyParameter("altAcc", checkin.getString("altAcc"));
			
			this.service.signRequest(accessToken, request);
			Response response = request.send();

			res = response.getBody();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(res);
		if(res == null)
			System.out.println("failure");
		JSONObject resjson=null;
		try {
			resjson = new JSONObject(res);
			if(resjson.has("meta")){
				JSONObject m = resjson.getJSONObject(("meta"));
				String code = m.getString("code");
				if(code.equalsIgnoreCase("200"))
					System.out.println("success");
				else if(code.startsWith("4")) 
					System.out.println(m.toString());
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}