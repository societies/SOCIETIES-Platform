package org.societies.platform.FacebookConn.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.FacebookConnector;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;
import com.restfb.json.JsonObject;



public class FacebookConnectorImpl implements FacebookConnector {
	
	private String 				access_token = null;
	private String 				identity	 = null;
	private String 				name;
	private String 				id;
	
	private Properties			parameters;
	private FacebookClient 		facebookClient;
	private int					maxPostLimit = 50;
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
		
		System.out.println("Execute query:"+path);
		return facebookClient.fetchObject(path, JsonObject.class).toString();
		

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
		
			
			if (access_token==null) return null;
			facebookClient = new DefaultFacebookClient(access_token);
			
			JsonObject profile 		= facebookClient.fetchObject(ME, JsonObject.class); 
			//JsonObject photos		= facebookClient.fetchObject(THUMB,JsonObject.class );
			//JsonObject activities   = facebookClient.fetchObject(ACTIVITIES, JsonObject.class);
			//JsonObject books		= facebookClient.fetchObject(BOOKS, JsonObject.class);
			//JsonObject movies		= facebookClient.fetchObject(MOVIES, JsonObject.class);
			
			// We can add whatever is missing
			//JsonObject photo = new JsonObject();
			//photo.put("type", "thumb");
			//photo.put("primary", true);
			//photo.put("value", photos);
			
//			profile.accumulate("photos",	 new JSONArray().put(photo));
//			profile.accumulate("books", 	 books.getString("data"));
//			profile.accumulate("activities", activities.getString("data"));
//			profile.accumulate("movies", 	 movies.getString("data"));
			
		
			return profile.toString(1);
		
	}

	
	public String getUserFriends() {
	
		return getSocialData(FRIENDS);
	}

	public String getUserActivities() {
		
		return getSocialData(FEED);
		
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

	
	
	
}
