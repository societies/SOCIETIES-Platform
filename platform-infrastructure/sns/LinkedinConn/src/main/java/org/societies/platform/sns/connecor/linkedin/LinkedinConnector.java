package org.societies.platform.sns.connecor.linkedin;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.societies.api.internal.sns.ISocialConnector;



public class LinkedinConnector implements ISocialConnector {
	
	private String 				access_token = null;
	private String 				identity	 = null;
	private String 				name;
	private String 				id;
	private String				lastUpdate   = "yesterday";
	private LinkedinToken 		token=null;
	private Properties			parameters;

	public static final String PROFILE_URL 			= "http://api.linkedin.com/v1/people/~:(id,first-name,last-name,languages,skills,educations,date-of-birth,honors,associations,email-address,summary,public-profile-url,picture-url,specialties,industry,headline,formatted-name,maiden-name,patents,interests)";
	public static final String FRIENDS_URL 			= "http://api.linkedin.com/v1/people/~/connections";
	public static final String GROUPS_URL 			= "http://api.linkedin.com/v1/people/~/group-memberships?membership-state=member";
	public static final String ACTIVITIES_URL 		= "http://api.linkedin.com/v1/people/~/network/updates?scope=self";
	public static final String POST_URL		 		= "http://api.linkedin.com/v1/people/~/shares";
	
	
	
	public static final String ME 		= "profile";
	public static final String FEEDS 	= "activities";
	public static final String GROUPS   = "groups";
	public static final String FRIENDS 	= "friends";
	
	
	
	private OAuthService service;
	
	/**
	 * Empty conctructor
	 */
	public LinkedinConnector(){}
	
	public LinkedinConnector (String access_token, String identity){
		
		this.identity		= identity;
		this.token			= new LinkedinToken(access_token);
		this.name 			= ISocialConnector.LINKEDIN_CONN;
		this.id				= this.name + "_" + UUID.randomUUID();
		this.service 		= token.getAuthService();
		 
		 Scanner in = new Scanner(System.in);
		 
		 System.out.println("=== LinkedIn's OAuth Workflow ===");
		 System.out.println();
		 
		 System.out.println("=== LinkedIn's OAuth Workflow ===");
		 System.out.println();

		
		
	}
	
	public String getID(){
		return this.id;
	}
	
	public void setToken(String access_token) {
		this.access_token = access_token;
		this.id			  = this.name + "_" + UUID.randomUUID();
		
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
		
		return "";

	}
	
	
	
	
	public Map<String, String> requireAccessToken() {

		HashMap<String, String > credential = new HashMap<String, String>();

//		credential.put(AUTH_TOKEN, "");
//		
		return credential;
	}
	public void disconnect() {
		access_token="";
	}

	public void setMaxPostLimit(int postLimit) {
		
	}
	
	public void setParameter(String key, String value){
		if (parameters == null){
			parameters= new Properties();
		}
		parameters.put(key, value);
	}
	
	
	
	


	public void resetParameters() {
		parameters = new Properties();
	}


	
    private String get(String URL){
    	OAuthRequest request = new OAuthRequest(Verb.GET, URL);
		request.addHeader("x-li-format", "json");
		this.service.signRequest(token.getAccessToken(), request);
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
	

	public String getUserProfile() {
		return get(PROFILE_URL);
	}

	
	
	
	public String getUserFriends() {
		return get(FRIENDS_URL);
	
	}

	public String getUserActivities() {
		return get(ACTIVITIES_URL);
		
	}
	

	public String getUserGroups() {
		return get(GROUPS_URL);
	}

	public long getTokenExpiration() {
		return -1;
	}
	
	public void setTokenExpiration(long expiration){
		
	}

	
	public Map<String, String> getAllSocialData(){
		
		Map<String, String> results = new HashMap<String, String>();
		results.put(ME, get(PROFILE_URL));
		results.put(FEEDS, get(ACTIVITIES_URL));
		results.put(GROUPS, get(GROUPS_URL));
		results.put(FRIENDS, get(FRIENDS_URL));
		return results;
		
	}

	
	
	@Override
	public void post(String value) {
		
		
		JSONObject tweet = null;
		String res = null;
			
			OAuthRequest request = new OAuthRequest(Verb.POST, POST_URL);
			// set the headers to the server knows what we are sending
			request.addHeader("Content-Type", "application/json");
			request.addHeader("x-li-format", "json");
			
			
			request.addPayload(value);
			this.service.signRequest(token.getAccessToken(), request);
			Response response = request.send();

			res = response.getBody();
			System.out.println(res.toString());
	
			
//		if(res == null)
//			System.out.println("failure");
//		JSONObject resjson=null;
//		try {
//			resjson = new JSONObject(res);
//			if(resjson.has("error")){
//				System.out.println(resjson.get("error"));
//			}
//			if(resjson.has("text")){
//				String resStatus = resjson.getString("text");
//				if(resStatus.equalsIgnoreCase(tweet.getString("status")))
//					System.out.println("success");
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
}
