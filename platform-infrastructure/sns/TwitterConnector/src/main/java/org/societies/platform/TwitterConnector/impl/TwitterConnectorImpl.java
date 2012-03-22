package org.societies.platform.TwitterConnector.impl;

import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.societies.platform.TwitterConnector.*;
import org.societies.platform.TwitterConnector.model.TwitterToken;

/*
 * twitter sn client 
 */
public class TwitterConnectorImpl implements TwitterConnector{

	private TwitterToken twToken = null;
	private OAuthService service;

	public TwitterConnectorImpl(){
		this.twToken = new TwitterToken();
		this.service = twToken.getAuthService();
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
		if(res!=null)
			return res.toJSONString();
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
		if(res!=null)
			return res.toJSONString();
		else 
			return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getID()
	 */
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setToken(java.lang.String)
	 */
	@Override
	public void setToken(String access_token) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setTokenExpiration(long)
	 */
	@Override
	public void setTokenExpiration(long expires) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getTokenExpiration()
	 */
	@Override
	public long getTokenExpiration() {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getToken()
	 */
	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setConnectorName(java.lang.String)
	 */
	@Override
	public void setConnectorName(String name) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getConnectorName()
	 */
	@Override
	public String getConnectorName() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getSocialData(java.lang.String)
	 */
	@Override
	public String getSocialData(String path) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#requireAccessToken()
	 */
	@Override
	public Map<String, String> requireAccessToken() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#disconnect()
	 */
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setMaxPostLimit(int)
	 */
	@Override
	public void setMaxPostLimit(int postLimit) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public void setParameter(String key, String value) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#resetParameters()
	 */
	@Override
	public void resetParameters() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserActivities()
	 */
	@Override
	public String getUserActivities() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserGroups()
	 */
	@Override
	public String getUserGroups() {
		// TODO Auto-generated method stub
		return null;
	}

}