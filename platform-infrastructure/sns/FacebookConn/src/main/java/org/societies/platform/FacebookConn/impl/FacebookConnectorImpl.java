package org.societies.platform.FacebookConn.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.media.jai.UntiledOpImage;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.FacebookConnector;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchRequest.BatchRequestBuilder;
import com.restfb.batch.BatchResponse;
import com.restfb.json.JsonObject;



public class FacebookConnectorImpl implements FacebookConnector {
	
	private String 				access_token = null;
	private String 				identity	 = null;
	private String 				name;
	private String 				id;
	private String				lastUpdate   = "last week";
	
	private Properties			parameters;
	private FacebookClient 		facebookClient;
	private int					maxPostLimit = 200;
	private long				tokenExpiration=0;
	
	public FacebookConnectorImpl(){}
	
	public FacebookConnectorImpl (String access_token, String identity){
		
		this.identity		= identity;
		this.access_token	= access_token;
		this.name 			= ISocialConnector.FACEBOOK_CONN;
		this.id				= this.name + "_" + UUID.randomUUID();
		facebookClient		= new DefaultFacebookClient(access_token);
		
	}
	
	public String getID(){
		return this.id;
	}
	
	public void setToken(String access_token) {
		this.access_token = access_token;
		
		
		
	}
	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getToken() {
		
		facebookClient = new DefaultFacebookClient();
		String appId		= "368482799848413";
		String secretKey	= "c1788688a3091638768ed803d6ebdbd0";
		String sessionKeys  = null;
		List<AccessToken> tokens = facebookClient.convertSessionKeysToAccessTokens(appId, secretKey, sessionKeys);
		Iterator<AccessToken>it = tokens.iterator();
		while(it.hasNext()){
			AccessToken at= it.next();
			System.out.println("token:"+at.getAccessToken() + "expires:"+at.getExpires());
		}
		return access_token;
	}
	public void setConnectorName(String name) {
		this.name= name;
		
	}
	public String getConnectorName() {
		return name;
	}
	

	
	
	public String getSocialData(String path)  {
		
		if (access_token==null) return null;
		
		BatchRequest request = new BatchRequestBuilder(path).parameters(Parameter.with("until", lastUpdate), Parameter.with("limit", maxPostLimit)).build();
		List<BatchResponse> batchResponses = facebookClient.executeBatch(request);
		BatchResponse response = batchResponses.get(0);
		lastUpdate= new Date().toString();
		return response.getBody();

	}
	
	
	
	
	public Map<String, String> requireAccessToken() {

		HashMap<String, String > credential = new HashMap<String, String>();
		try {
		
			WebRequestor doGet = new DefaultWebRequestor();
			Response response = doGet.executeGet("http://wd.teamlife.it/fbconnector.php");
			System.out.println("Auth: " +response.getBody()+ " code:" + response.getStatusCode());
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		// this should be provided by an external component
		credential.put(AUTH_TOKEN, "");
		
		return credential;
	}
	public void disconnect() {
		access_token="";
	}

	public void setMaxPostLimit(int postLimit) {
		 this.maxPostLimit = postLimit;
	}
	
	public void setParameter(String key, String value){
		if (parameters == null){
			parameters= new Properties();
		}
		parameters.put(key, value);
	}
	
	
	
	public String genURL(String path){
		String opt= "";
		if (parameters ==null) parameters = new Properties();
		if (!parameters.contains("limit")) parameters.put("limit", ""+this.maxPostLimit);
		//if (!parameters.contains("until")) parameters.put("until", "yesterday");
	    Enumeration it = parameters.keys();
	    while (it.hasMoreElements()){
			String k = it.nextElement().toString();
			opt += "&" +k +"=" +parameters.getProperty(k);
		}
		
		return "https://graph.facebook.com/"+path+"?access_token="+access_token+opt ;
	}


	public void resetParameters() {
		parameters = new Properties();
	}


	

	public String getUserProfile() {
		return getSocialData(ME);
	}

	
	public String getUserFriends() {
	
		return getSocialData(FRIENDS);
	}

	public String getUserActivities() {
		
		
		
 		BatchRequest request = new BatchRequestBuilder(FEED).parameters(Parameter.with("since", lastUpdate), Parameter.with("limit", maxPostLimit)).build();
		List<BatchResponse> batchResponses = facebookClient.executeBatch(request);
		BatchResponse response = batchResponses.get(0);
		
		
		return response.getBody();
	}
	

	public String getUserGroups() {
		return getSocialData(GROUPS);
	}

	public long getTokenExpiration() {
		return tokenExpiration;
	}
	
	public void setTokenExpiration(long expiration){
		this.tokenExpiration = expiration;
	}

	public Map<String, String> getAllSocialData(){
		
	
		
		BatchRequest meRequest = new BatchRequestBuilder(ME).build();
		BatchRequest feedRequest = new BatchRequestBuilder(FEED).parameters(Parameter.with("from", "yesterday")).build();
		BatchRequest groupRequest = new BatchRequestBuilder(GROUPS).build();
		BatchRequest friendsRequest = new BatchRequestBuilder(FRIENDS).build();
		
		List<BatchResponse> batchResponses = facebookClient.executeBatch(meRequest, feedRequest, groupRequest, friendsRequest);
		
		
		// Responses are ordered to match up with their corresponding requests.

		BatchResponse meResponse = batchResponses.get(0);
		BatchResponse feedResponse = batchResponses.get(1);
		BatchResponse groupResponse = batchResponses.get(2);
		BatchResponse friendsResponse = batchResponses.get(3);
		
		// Since batches can have heterogenous response types, it's up to you
		// to parse the JSON into Java objects yourself. Luckily RestFB has some built-in
		// support to help you with this.

		JsonMapper jsonMapper = new DefaultJsonMapper();
		Map<String, String> results = new HashMap<String, String>();
		
		results.put(ME, jsonMapper.toJavaObject(meResponse.getBody(), JsonObject.class).toString());
		results.put(FEED, jsonMapper.toJavaObject(feedResponse.getBody(), JsonObject.class).toString());
		results.put(GROUPS, jsonMapper.toJavaObject(groupResponse.getBody(), JsonObject.class).toString());
		results.put(FRIENDS, jsonMapper.toJavaObject(friendsResponse.getBody(), JsonObject.class).toString());
		
		
		return results;
		
		
	}
	
	
}
