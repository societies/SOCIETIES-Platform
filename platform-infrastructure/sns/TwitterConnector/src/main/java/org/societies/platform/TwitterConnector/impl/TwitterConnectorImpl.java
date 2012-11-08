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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.TwitterConnector.TwitterConnector;
import org.societies.platform.TwitterConnector.model.TwitterToken;

/*
 * twitter connector implementation
 */
public class TwitterConnectorImpl implements TwitterConnector {

	private TwitterToken twToken = null;
	private OAuthService service;
	private String name;
	private String id;
	private String lastUpdate = "yesterday";
	private String access_token;
	private String identity;

	// public TwitterConnectorImpl() {
	// this.twToken = new TwitterToken();
	// this.service = twToken.getAuthService();
	// this.name = ISocialConnector.TWITTER_CONN;
	// this.id = this.name + "_" + UUID.randomUUID();
	//
	// }

	public TwitterConnectorImpl() {
	}

	public TwitterConnectorImpl(String access_token, String identity) {
		this.twToken = new TwitterToken(access_token);
		this.service = twToken.getAuthService();
		this.name = ISocialConnector.TWITTER_CONN;
		this.id = this.name + "_" + UUID.randomUUID();
		this.access_token= access_token;
		this.identity = identity;
	}

	public String getUserProfile() {
		OAuthRequest request = new OAuthRequest(Verb.GET, ACCOUNT_VERIFICATION);
		this.service.signRequest(twToken.getAccessToken(), request);
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
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_FRIENDS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONObject res = null;
		try {
			res = new JSONObject(response.getBody());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (res != null)
			return res.toString();
		else
			return null;
	}

//		public String getUserFriends() {
//			OAuthRequest request = new OAuthRequest(Verb.GET, GET_FRIENDS_URL);
//			this.service.signRequest(twToken.getAccessToken(), request);
//			Response response = request.send();
//			JSONObject res = null;
//			try {
//				res = new JSONObject(response.getBody());
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			JSONObject friends = new JSONObject();
//			JSONArray friendsList = new JSONArray();
//			// System.out.println(res.toString());
//			try {
//				JSONArray friendsIDList = res.getJSONArray("ids");
//	
//				JSONObject other = null;
//	
//				for (int i = 0; i < friendsIDList.length(); i++) {
//					 System.out.println(friendsIDList.get(i).toString());
//	
//					other = getOtherProfileJson(friendsIDList.get(i).toString());
//	
//					 System.out.println(other);
//					if (!other.toString().contains(
//							"No user matches for specified terms"))
//						friendsList.put(other);
//					friends.put("friends", friendsList);
//				}
//			} catch (JSONException e) {
//				return response.getBody();
//			}
//	
//			if (res != null)
//				// return res.toJSONString();
//				return friends.toString();
//			else
//				return null;
//		}

	public String getUserFollowers() {
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_FOLLOWERS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONObject res = null;
		JSONObject followers = new JSONObject();
		JSONArray followersList = new JSONArray();
		try {
			res = new JSONObject(response.getBody());
			JSONArray followersIDList = res.getJSONArray("ids");

			JSONObject other = null;

			for (int i = 0; i < followersIDList.length(); i++) {
				// System.out.println(friendsIDList.get(i).toString());

				other = getOtherProfileJson(followersIDList.get(i).toString());

				// System.out.println(other);
				if (!other.toString().contains(
						"No user matches for specified terms"))
					followersList.put(other);
				followers.put("friends", followersList);
			}
		} catch (JSONException e) {
			response.getBody();
		}

		if (res != null)
			// return res.toJSONString();
			return followers.toString();
		else
			return null;

	}

	public String getOtherProfileString(String id) {
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_OTHER_PROFILE_URL
				+ id);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONArray res = null;
		JSONObject user = null;
		try {
			res = new JSONArray(response.getBody());
			user = res.getJSONObject(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (res != null)
			return user.toString();
		else
			return null;
	}

	public JSONObject getOtherProfileJson(String id) {
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_OTHER_PROFILE_URL
				+ id);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONArray res = null;
		JSONObject user = null;
		try {
			res = new JSONArray(response.getBody());
			user = res.getJSONObject(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (res != null)
			return user;
		else
			return null;
	}

	/*
	 * Activities in Twitter is defined as tweets
	 * 
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserActivities()
	 */
	public String getUserActivities() {
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_TWEETS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONArray res = null;
		try {
			res = new JSONArray(response.getBody());
		} catch (JSONException e) {
			return response.getBody();
		}
		// System.out.println(res.toString());
		if (res != null)
			return res.toString();
		else
			return null;
	}

	public String getID() {
		return this.id;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setToken(String access_token) {
		this.access_token = access_token;

	}

	public void setTokenExpiration(long expires) {
		

	}

	public long getTokenExpiration() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getToken() {
		// TODO Auto-generated method stub
		return this.access_token;
	}

	public void setConnectorName(String name) {
		this.name = name;

	}

	public String getConnectorName() {

		return name;
	}

	public String getSocialData(String path) {

		return "{}";
	}

	public Map<String, String> requireAccessToken() {
		// TODO Auto-generated method stub
		return null;
	}

	public void disconnect() {
		this.service = null;
		this.twToken = null;

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

		return "{\"data\" : []}";
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#post(java.lang.String)
	 */
	@Override
	public void post(String activity) {

		JSONObject tweet = null;
		String res = null;
		try {
			tweet = new JSONObject(activity);
			OAuthRequest request = new OAuthRequest(Verb.POST, POST_TWEET_URL);
			if (tweet.has("status"))
				request.addBodyParameter("status", tweet.getString("status"));
			if (tweet.has("in_reply_to_status_id"))
				request.addBodyParameter("in_reply_to_status_id", tweet.getString("in_reply_to_status_id"));
			if (tweet.has("lat"))
				request.addBodyParameter("lat", tweet.getString("lat"));
			if (tweet.has("long"))
				request.addBodyParameter("long", tweet.getString("long"));
			if (tweet.has("place_id"))
				request.addBodyParameter("place_id", tweet.getString("place_id"));
			if (tweet.has("display_coordinates"))
				request.addBodyParameter("display_coordinates", tweet.getString("display_coordinates"));
			if (tweet.has("trim_user"))
				request.addBodyParameter("trim_user", tweet.getString("trim_user"));
			if (tweet.has("include_entities"))
				request.addBodyParameter("include_entities", tweet.getString("include_entities"));
			
			this.service.signRequest(twToken.getAccessToken(), request);
			Response response = request.send();

			res = response.getBody();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(res == null)
			System.out.println("failure");
		JSONObject resjson=null;
		try {
			resjson = new JSONObject(res);
			if(resjson.has("error")){
				System.out.println(resjson.get("error"));
			}
			if(resjson.has("text")){
				String resStatus = resjson.getString("text");
				if(resStatus.equalsIgnoreCase(tweet.getString("status")))
					System.out.println("success");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}