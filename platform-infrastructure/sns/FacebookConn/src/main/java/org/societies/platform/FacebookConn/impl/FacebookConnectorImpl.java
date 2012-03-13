package org.societies.platform.FacebookConn.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.societies.platform.FacebookConn.FacebookConnector;
import org.societies.platform.FacebookConn.SocialConnector;
import org.societies.platform.FacebookConn.exeptions.MissingTokenExeptions;

import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;



public class FacebookConnectorImpl implements FacebookConnector {

	
	private String 				access_token = null;
	private String 				identity	 = null;
	private String 				name;
	private String 				id;
	
	private List<Object>		parameters;
	private FacebookClient 		facebookClient;
	private int					maxPostLimit = 50;
	private long				tokenExpiration=0;
	
	public FacebookConnectorImpl (String access_token, String identity){
		
		this.identity		= identity;
		this.access_token	= access_token;
		this.name 			= SocialConnector.FACEBOOK_CONN;
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
	
	
	public String getSocialData(String path) throws MissingTokenExeptions {
		
		if (access_token==null) throw new MissingTokenExeptions();
		
		
		WebRequestor requestor = new DefaultWebRequestor();
		try {
		
			Response response = requestor.executeGet(genURL(path));
			return response.getBody();
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
			Response response = doGet.executeGet("http://wd.teamlife.it/test.php");
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
	
	public void setParameter(Object p){
		if (parameters == null){
			parameters= new ArrayList<Object>();
		}
		
		parameters.add(p);
	}
	
	
	
	public String genURL(String path){
		String opt= "";
		if (parameters!=null){
			Iterator it = parameters.iterator();
			while (it.hasNext()){
				Parameter p = (Parameter) it.next();
				opt += "&" +p.name +"=" + p.value;
			}
		}
		 if (path.equals(FEED))
			 		return "https://graph.facebook.com/me/"+path+"?access_token="+access_token+opt;
			 	else
			 		return "https://graph.facebook.com/"+path+"?access_token="+access_token+opt ;
	}


	public void resetParameters() {
		parameters = null;
	}

	

	public JSONObject getUserProfile() {
		JSONObject profile = null;
		try {
			profile = new JSONObject(getSocialData(ME));
		} 
		catch (MissingTokenExeptions e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		} 
		return profile;
	}

	
	public JSONObject getUserFriends() {
		JSONObject users= null;
		try {
			users = new JSONObject(getSocialData(FRIENDS));
		} 
		catch (MissingTokenExeptions e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		} 
		
		return users;
	}

	public JSONObject getUserActivities() {
		JSONObject users= null;
		try {
			users = new JSONObject(getSocialData(FEED));
		} 
		catch (MissingTokenExeptions e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		} 
		
		return users;
	}
	

	public JSONObject getUserGroups() {
		JSONObject groups= null;
		try {
			groups = new JSONObject(getSocialData(GROUPS));
		} 
		catch (MissingTokenExeptions e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		} 
		
		return groups;
	}

	public long getTokenExpiration() {
		return tokenExpiration;
	}
	
	public void setTokenExpiration(long expiration){
		this.tokenExpiration = expiration;
	}

	
	
}
