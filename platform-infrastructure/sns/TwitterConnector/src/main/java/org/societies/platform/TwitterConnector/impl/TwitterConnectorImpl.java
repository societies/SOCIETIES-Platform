package org.societies.platform.TwitterConnector.impl;

import java.util.Map;
import java.util.UUID;

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

	public TwitterConnectorImpl() {
		this.twToken = new TwitterToken();
		this.service = twToken.getAuthService();
		this.name = ISocialConnector.TWITTER_CONN;
		this.id = this.name + "_" + UUID.randomUUID();

	}

	public TwitterConnectorImpl(String access_token, String identity) {
		this.twToken = new TwitterToken(access_token);
		this.service = twToken.getAuthService();
		this.name = ISocialConnector.TWITTER_CONN;
		this.id = this.name + "_" + UUID.randomUUID();
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
		JSONObject res =null;
		try {
			res = new JSONObject(response.getBody());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject friends = new JSONObject();
		JSONArray friendsList = new JSONArray();
//		System.out.println(res.toString());
		try {
			JSONArray friendsIDList = res.getJSONArray("ids");
			
			JSONObject other = null;
			
			for (int i = 0; i < friendsIDList.length(); i++) {
				// System.out.println(friendsIDList.get(i).toString());

				other = getOtherProfileJson(friendsIDList.get(i).toString());

				// System.out.println(other);
				if (!other.toString().contains(
						"No user matches for specified terms"))
					friendsList.put(other);
				friends.put("friends", friendsList);
			}
		} catch (JSONException e) {
			return response.getBody();
		}
		
		if (res != null)
			// return res.toJSONString();
			return friends.toString();
		else
			return null;
	}

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
		JSONObject user =null;
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
		JSONObject user =null;
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
		// TODO Auto-generated method stub

	}

	public void setTokenExpiration(long expires) {
		// TODO Auto-generated method stub

	}

	public long getTokenExpiration() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getToken() {
		// TODO Auto-generated method stub
		return null;
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
		
		return  "{\"data\" : []}";
	}

}