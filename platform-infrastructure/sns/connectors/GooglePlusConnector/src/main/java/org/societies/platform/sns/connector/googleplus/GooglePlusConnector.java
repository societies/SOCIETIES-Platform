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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.platform.sns.connector.googleplus.exceptions.HttpException;



public class GooglePlusConnector implements ISocialConnector {
			
	public static final String GP_CLIENT_ID		= "";
	public static final String GP_ClIENT_SECRET	= "";
	public static final String GP_CALLBACK_URL	= "http://127.0.0.1:8080/doConnect.html?type=gp";
	
	
	public static final String BASE_URL = "https://www.googleapis.com/plus/v1/";
	public static final String PROFILE_PATH = "people/me";
	public static final String ACTIVITIES_PATH = "people/me/activities/public";
	public static final String PROFILE_URL = BASE_URL + PROFILE_PATH;	
	public static final String ACTIVITIES_URL = BASE_URL + ACTIVITIES_PATH;	
		
	public static final String ME 		= "profile";
	public static final String FEEDS 	= "activities";
	public static final String GROUPS   = "groups";
	public static final String FRIENDS 	= "friends";
	
	public static final String MAX_POST_LIMIT_PARAMETER = "maxResults";
	
	private String access_token = null;
	private String name = SocialNetwork.GOOGLEPLUS.value();
	private String id;
	private String lastUpdate = "yesterday";
	private GooglePlusTokenManager token=null;
	private Map<String, String> parameters = new HashMap<String, String>();
	private OAuthService service;
	
	public GooglePlusConnector(){}
	
	public GooglePlusConnector(String access_token, String identity){
		this.access_token = access_token;
		this.token = new GooglePlusTokenManager(access_token);
		this.id	= this.name + "_" + UUID.randomUUID();
		this.service = token.getAuthService();
	}
	
	public String getID(){
		return this.id;
	}
	
	public void setToken(String access_token) {
		this.access_token = access_token;
		this.token = new GooglePlusTokenManager(access_token);
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getToken() {		
		return this.access_token;
	}

	public void setConnectorName(String name) {
		this.name= name;		
	}

	public String getConnectorName() {
		return name;
	}
	
	public String getSocialData(String path)  {
		return get(BASE_URL + path);
	}
	
	public Map<String, String> requireAccessToken() { // TODO
		HashMap<String, String > credential = new HashMap<String, String>();
		return credential;
	}

	public void disconnect() {
	}

	public void setMaxPostLimit(int postLimit) { // TODO
		parameters.put(MAX_POST_LIMIT_PARAMETER, Integer.toString(postLimit));
	}
	
	public void setParameter(String key, String value){ // TODO
		parameters.put(key, value);
	}

	public void resetParameters() {
		parameters.clear();
	}
	
	private String get(String URL){
		OAuthRequest request = new OAuthRequest(Verb.GET, URL);
		request.addHeader("x-li-format", "json");
		addParameters(request);
		this.service.signRequest(token.getAccessToken(), request);
		Response response = request.send();
		
		if(!response.isSuccessful())
			throw new HttpException(response.getCode(), response.getBody());
			
		JSONObject res = null;
		try {
			res = new JSONObject(response.getBody());
		} catch (JSONException e) {
			return response.getBody();
		}

		return res.toString();
	} 
	
	private void addParameters(OAuthRequest request) {
		for(Map.Entry<String, String> parameter:parameters.entrySet())
			request.addQuerystringParameter(parameter.getKey(), parameter.getValue());
	}
	
	public String getUserProfile() {
		return get(PROFILE_URL);
	}
	
	public String getUserFriends() {
		return "";
	
	}

	public String getUserActivities() {
		return get(ACTIVITIES_URL);		
	}

	public String getUserGroups() {
		return "";
	}

	public long getTokenExpiration() { // TODO
		return -1;
	}
	
	public void setTokenExpiration(long expiration){ // TODO
		
	}
	
	public Map<String, String> getAllSocialData(){
		Map<String, String> results = new HashMap<String, String>();
		results.put(ME, getUserProfile());
		results.put(FEEDS, getUserActivities());
		results.put(GROUPS, getUserGroups());
		results.put(FRIENDS, getUserFriends());
		return results;
	}
	
	@Override
	public void post(String value) {
	}

	@Override
	public SocialNetwork getSocialNetwork() {
	    // TODO Auto-generated method stub
	    return SocialNetwork.GOOGLEPLUS;
	}

	
}
