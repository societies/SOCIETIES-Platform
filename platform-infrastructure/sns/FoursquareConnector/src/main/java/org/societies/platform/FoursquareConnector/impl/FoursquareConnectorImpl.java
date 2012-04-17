package org.societies.platform.FoursquareConnector.impl;

import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.societies.platform.FoursquareConnector.FoursquareConnector;

/*
 * Foursquare connector implementation
 * 
 * dingqi yang
 */
public class FoursquareConnectorImpl implements FoursquareConnector{

	private String 	accessTokenString = null;
	private Token 	accessToken = null;
	private String 	identity = null;


	private String apiKey = "LTNRV3JPEKSFUCMOF4HY05GZHW4BWIZ1Y2YGBJCLMGEXZFG4";
	private String apiSecret = "2Y0YDIH5XQV13P2ZE3EWZDGEAIHXXQNMOUAEVU4XIWRYRBBS";

	private OAuthService service;

	public FoursquareConnectorImpl(String access_token, String identity){
		this.accessTokenString = access_token;
		this.identity = identity;
		this.service =  new ServiceBuilder()
							.provider(Foursquare2Api.class)
							.apiKey(apiKey)
							.apiSecret(apiSecret)
							.callback("http://localhost:8080/examples/servlets/auth")
							.build();
		this.accessToken = new Token(this.accessTokenString,"");
	}

	
	public String getUserProfile(){
		OAuthRequest request = new OAuthRequest(Verb.GET, RECENT_CHECKINS + accessToken.getToken());
		this.service.signRequest(accessToken, request);
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
	
	
	public String getRecentCheckins(){
		OAuthRequest request = new OAuthRequest(Verb.GET, USER_PROFILE + accessToken.getToken());
		this.service.signRequest(accessToken, request);
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
	
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setToken(java.lang.String)
	 */
	
	public void setToken(String access_token) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setTokenExpiration(long)
	 */
	
	public void setTokenExpiration(long expires) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getTokenExpiration()
	 */
	
	public long getTokenExpiration() {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getToken()
	 */
	
	public String getToken() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setConnectorName(java.lang.String)
	 */
	
	public void setConnectorName(String name) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getConnectorName()
	 */
	
	public String getConnectorName() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getSocialData(java.lang.String)
	 */
	
	public String getSocialData(String path) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#requireAccessToken()
	 */
	
	public Map<String, String> requireAccessToken() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#disconnect()
	 */
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setMaxPostLimit(int)
	 */
	
	public void setMaxPostLimit(int postLimit) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#setParameter(java.lang.String, java.lang.String)
	 */
	
	public void setParameter(String key, String value) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#resetParameters()
	 */
	
	public void resetParameters() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserFriends()
	 */
	
	public String getUserFriends() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserActivities()
	 */
	
	public String getUserActivities() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserGroups()
	 */
	
	public String getUserGroups() {
		// TODO Auto-generated method stub
		return null;
	}


}