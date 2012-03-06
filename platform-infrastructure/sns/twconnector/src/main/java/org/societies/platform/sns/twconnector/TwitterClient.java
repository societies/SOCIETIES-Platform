package org.societies.platform.sns.twconnector;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.model.*;
import org.scribe.oauth.*;

/*
 * twitter sn client 
 */
class TwitterClient{
	
	private TwitterToken twToken = null;
	private OAuthService service;
	
	public TwitterClient(){
		this.twToken = new TwitterToken();
		this.service = twToken.getAuthService();
	}
	

	public JSONObject getUserProfile(String userID) throws ParseException{
		OAuthRequest request = new OAuthRequest(Verb.GET, ActionURL.ACCOUNT_VERIFICATION);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(response.getBody());
		JSONObject res=(JSONObject)obj;
		if(res!=null)
			return res;
		else
			return null;
	}
	
	public JSONObject getUserFriends(String userID) throws ParseException{
		OAuthRequest request = new OAuthRequest(Verb.GET, ActionURL.GET_FRIENDS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(response.getBody());
		JSONObject res=(JSONObject)obj;
		if(res!=null)
			return res;
		else
			return null;
	}
	
	public JSONObject getUserFollowers(String userID) throws ParseException{
		OAuthRequest request = new OAuthRequest(Verb.GET, ActionURL.GET_FOLLOWERS_URL);
		this.service.signRequest(twToken.getAccessToken(), request);
		Response response = request.send();
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(response.getBody());
		JSONObject res=(JSONObject)obj;
		if(res!=null)
			return res;
		else 
			return null;
	}
	
	
}