package org.societies.platform.FacebookConn.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.FacebookConnector;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;
import com.restfb.json.JsonObject;
import com.restfb.types.User;



public class FacebookConnectorImpl implements FacebookConnector {

	
	private static final String DATA 		 = "data";
	private String 				access_token = null;
	private String 				identity	 = null;
	private String 				name;
	private String 				id;
	
	private Properties			parameters;
	private FacebookClient 		facebookClient;
	private int					maxPostLimit = 50;
	private long				tokenExpiration=0;
	
	public FacebookConnectorImpl (String access_token, String identity){
		
		this.identity		= identity;
		this.access_token	= access_token;
		this.name 			= ISocialConnector.FACEBOOK_CONN;
		this.id				= this.name + "_" + UUID.randomUUID();
		
	}
	
	public String getID(){
		return this.id;
	}
	
	public void setToken(String access_token) {
		this.access_token = access_token;
		
	}
	public String getToken() {
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
		
	
		
		WebRequestor requestor = new DefaultWebRequestor();
		try {
		
			Response response = requestor.executeGet(genURL(path));
			JSONObject json_response = null;
			boolean next = true;
			
			System.out.println("Response code:"+response.getStatusCode());
			while (next && response.getStatusCode()==200){
				
				JSONObject json = null;
				try {
					System.out.println("=== RESPONSE BODY:");
					System.out.println(response.getBody());
					json = new JSONObject(response.getBody());
					if (json_response == null) {
						json_response = json;  // FIRST TIME;
						System.out.println("=== MAKE JSON");
					}
					else
						json_response.append("data", json.getString("data"));
					
					
					if (json.has("paging")) {
						JSONObject paging = new JSONObject(json.getString("paging"));
						if (paging.has("next"))
							response = requestor.executeGet(paging.getString("next"));
						else
							return json_response.toString(1);
					}	
					else
						return json_response.toString(1);
					
				} catch (JSONException e) {
					next= false;
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {}
		return null;
		
		
		// TODO Auto-generated method stub
		//return myFeedConnectionPage.toString();
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
		parameters = null;
	}

	

	public String getUserProfile() {
		JSONObject profile = null;
		try {
			
			profile 			= new JSONObject(getSocialData(ME));
			
			// Get Activities
			String  activities	= getSocialData(ACTIVITIES);
			if (activities!=null){
				JSONObject json_activities = new JSONObject(activities);
				if (json_activities.has(DATA)) profile.put("activities", json_activities.getJSONArray(DATA));
			}
			
			
			return profile.toString(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
			
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
