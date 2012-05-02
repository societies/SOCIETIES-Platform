package org.societies.platform.TwitterConnector.impl;

import java.util.Map;
import java.util.UUID;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.TwitterConnector.*;
import org.societies.platform.TwitterConnector.model.TwitterToken;

/*
 * twitter connector implementation
 */
public class TwitterConnectorImpl implements TwitterConnector{

	private TwitterToken 	twToken = null;
	private OAuthService 	service;
	private String 			name;
	private String 			id;
	private String			lastUpdate   = "yesterday";

	public TwitterConnectorImpl(){
		this.twToken 		= new TwitterToken();
		this.service 		= twToken.getAuthService();
		this.name 			= ISocialConnector.TWITTER_CONN;
		this.id				= this.name + "_" + UUID.randomUUID();

	}


	public String getUserProfile(){
		OAuthRequest request = new OAuthRequest(Verb.GET, ACCOUNT_VERIFICATION);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res=(JSONObject)obj;
		if(res!=null)
			return res.toJSONString();
		else
			return null;
	}


	public String getUserFriends(){
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_FRIENDS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res=(JSONObject)obj;
		JSONArray friendsIDList = (JSONArray) res.get("ids");
		JSONArray friendsList = new JSONArray();
		JSONObject other = null;
		JSONObject friends = new JSONObject();
		
		for(int i=0;i<friendsIDList.size();i++){
			//			System.out.println(friendsIDList.get(i).toString());
			other = getOtherProfileJson(friendsIDList.get(i).toString());
			//			System.out.println(other);
			if (!other.toJSONString().contains("No user matches for specified terms"))
				friendsList.add(other);
		}

		friends.put("friends", friendsList);
		if(res!=null)
			//			return res.toJSONString();
			return friends.toString();
		else
			return null;
	}


	public String getUserFollowers(){
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_FOLLOWERS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res=(JSONObject)obj;
		JSONArray followersIDList = (JSONArray) res.get("ids");
		JSONArray followersList = new JSONArray();
		JSONObject other = null;
		JSONObject followers = new JSONObject();
		
		for(int i=0;i<followersIDList.size();i++){
			//			System.out.println(friendsIDList.get(i).toString());
			other = getOtherProfileJson(followersIDList.get(i).toString());
			//			System.out.println(other);
			if (!other.toJSONString().contains("No user matches for specified terms"))
				followersList.add(other);
		}

		followers.put("friends", followersList);
		if(res!=null)
			//			return res.toJSONString();
			return followers.toString();
		else
			return null;

	}


	public String getOtherProfileString(String id){
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_OTHER_PROFILE_URL+id);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray res = (JSONArray) obj;
		if(res!=null)
			return res.get(0).toString();
		else 
			return null;
	}

	public JSONObject getOtherProfileJson(String id){
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_OTHER_PROFILE_URL+id);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray res = (JSONArray) obj;
		if(res!=null)
			return (JSONObject) res.get(0);
		else 
			return null;
	}

/*
 * Activities in Twitter is defined as tweets
 * @see org.societies.api.internal.sns.ISocialConnector#getUserActivities()
 */
	public String getUserActivities() {
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_TWEETS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray res = (JSONArray) obj;
//		System.out.println(res.toString());
		if(res!=null)
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
		// TODO Auto-generated method stub

	}

	public String getConnectorName() {
		// TODO Auto-generated method stub
		return null;
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

}