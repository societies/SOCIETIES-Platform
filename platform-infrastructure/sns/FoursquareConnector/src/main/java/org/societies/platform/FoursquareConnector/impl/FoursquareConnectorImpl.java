package org.societies.platform.FoursquareConnector.impl;

import java.util.Map;
import java.util.UUID;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FoursquareConnector.FoursquareConnector;

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
	private String lastUpdate   = "yesterday";
	private long tokenExpiration=0;
	
	private String apiKey = "LTNRV3JPEKSFUCMOF4HY05GZHW4BWIZ1Y2YGBJCLMGEXZFG4";
	private String apiSecret = "2Y0YDIH5XQV13P2ZE3EWZDGEAIHXXQNMOUAEVU4XIWRYRBBS";

	private OAuthService service;
	
	public FoursquareConnectorImpl(){};

	public FoursquareConnectorImpl(String access_token, String identity) {
		this.accessTokenString = access_token;
		this.identity = identity;
		this.name 	= ISocialConnector.TWITTER_CONN;
		this.id		= this.name + "_" + UUID.randomUUID();
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
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res = (JSONObject) obj;
		if (res != null)
			return res.toJSONString();
		else
			return null;
	}

	public String getUserFriends() {
		OAuthRequest request = new OAuthRequest(Verb.GET, USER_PROFILE
				+ accessToken.getToken());
		this.service.signRequest(accessToken, request);
		Response response = request.send();
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res = (JSONObject) obj;
		if (res != null)
			return res.toJSONString();
		else
			return null;
	}

	/*
	 * Activity in foursquare is defined as check-in
	 * @see org.societies.api.internal.sns.ISocialConnector#getUserActivities()
	 */
	public String getUserActivities() {
		OAuthRequest request = new OAuthRequest(Verb.GET, RECENT_CHECKINS
				+ accessToken.getToken());
		this.service.signRequest(accessToken, request);
		Response response = request.send();
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(response.getBody());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res = (JSONObject) obj;
		if (res != null)
			return res.toJSONString();
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
		this.name= name;
		
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

}