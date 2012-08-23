package org.societies.platform.FacebookConn.impl;

import java.util.Date;
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
import com.restfb.DefaultJsonMapper;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.WebRequestor.Response;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchRequest.BatchRequestBuilder;
import com.restfb.batch.BatchResponse;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.FacebookType;



public class FacebookConnectorImpl implements FacebookConnector {
	
	private String 				access_token = null;
	private String 				identity	 = null;
	private String 				name;
	private String 				id;
	private String				lastUpdate   = "yesterday";
	
	private Properties			parameters;
	private FacebookClient 		facebookClient;
	private int					maxPostLimit    = 700;
	private long				tokenExpiration = 0;
	private boolean				firstTime = true;
	
	
	/**
	 * Empty conctructor
	 */
	public FacebookConnectorImpl(){}
	
	/**
	 * Facebook connector construtor. Initialize the minimum set of parameter to 
	 * generate an instance of Facebook connector.
	 * @param access_token - the use token to grant read/write access to his FB account
	 * @param identity     - societies Identity
	 * 
	 */
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
		facebookClient		= new DefaultFacebookClient(access_token);
		this.id				= this.name + "_" + UUID.randomUUID();
		
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
		
		try{
		if (access_token==null) genError("Access token is null", "Connector with no token", 500);
		
		BatchRequest request = new BatchRequestBuilder(path).parameters(Parameter.with("until", lastUpdate), Parameter.with("limit", maxPostLimit)).build();
		List<BatchResponse> batchResponses = facebookClient.executeBatch(request);
		BatchResponse response = batchResponses.get(0);
		
		
		
		lastUpdate= new Date().toString();
		
		if (response==null) 
			return genError("No response from Facebook (Empty)", "Empty response", 200);
		else if (response.getBody().length()==0)
			return response.toString();
		else{
		    JsonObject result = new JsonObject(response.getBody());
		   return result.toString(1);
		}
		}
		catch(Exception ex){
		  return genError(ex.getMessage(), "Request failure", 400);
		}
	

	}
	
	private String genError(String message, String type,  int code){
		return "{  \"error\" : {\"message\":  \""+ message + " \", \"type\": \"" + type + "\", \"code\" :\"" +code +"\"} }";
	}
	
	
	public Map<String, String> requireAccessToken() {

		HashMap<String, String > credential = new HashMap<String, String>();
//		try {
//		
//			WebRequestor doGet = new DefaultWebRequestor();
//			Response response = doGet.executeGet("http://wd.teamlife.it/fbconnector.php");
//			System.out.println("Auth: " +response.getBody()+ " code:" + response.getStatusCode());
//			
//		} 
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//		// this should be provided by an external component
//		credential.put(AUTH_TOKEN, "");
		
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
		try{
		BatchRequest reqME = new BatchRequestBuilder(ME).build();
		BatchRequest reqBOOK = new BatchRequestBuilder(BOOKS).build();
		BatchRequest reqINTEREST = new BatchRequestBuilder(INTERESTS).build();
		BatchRequest reqMUSIC = new BatchRequestBuilder(MUSIC).build();
		BatchRequest reqLIKES = new BatchRequestBuilder(LIKES).build();
		//BatchRequest reqThumb = new BatchRequestBuilder(THUMB).build();
		
		
		List<BatchResponse> batchResponses = facebookClient.executeBatch(reqME,reqBOOK,reqINTEREST,reqMUSIC, reqLIKES);
		
		BatchResponse responseMe   		= batchResponses.get(0);
		BatchResponse responseBook	 	= batchResponses.get(1);
		BatchResponse responseInterest	= batchResponses.get(2);
		BatchResponse responseMusic	    = batchResponses.get(3);
		BatchResponse responseLikes	    = batchResponses.get(4);
		//BatchResponse responseThumb	    = batchResponses.get(5);
		
		JsonObject me = new JsonObject(responseMe.getBody());
//		System.out.println("ME:"+me.toString(1));		
//		System.out.println("Book:"+responseBook.getBody());	
//		System.out.println("Interest:"+responseInterest.getBody());	
//		System.out.println("Music:"+responseMusic.getBody());	
//		System.out.println("Likes:"+responseLikes.getBody());	
		
		
		
		me.put("turnOns", convertToPluralField(responseLikes.getBody()));
		me.put("books", convertToPluralField(responseBook.getBody()));
		me.put("music", convertToPluralField(responseMusic.getBody()));
		me.put("interest", convertToPluralField(responseInterest.getBody()));
		//me.put("thumb", convertToPluralField(responseThumb.getBody()));
		return me.toString(1);   
		}
		  catch(Exception ex){
			  return genError(ex.getMessage(), "Request failure", 400);
		  }
		
		
		
	}

	
	private JsonArray convertToPluralField(String data){
		
		JsonObject jDataObj = new JsonObject(data);
		JsonArray  jData = jDataObj.getJsonArray("data");
		
		JsonArray  pluralFields = new JsonArray();
		
		for (int i=0; i<jData.length(); i++){
			JsonObject like = jData.getJsonObject(i);
			JsonObject field = new JsonObject();
			
			field.put("value", like.get("name"));
			field.put("type",  like.get("category"));
			field.put("id",  like.get("id"));
			
			pluralFields.put(field);
			
		}
		
		return pluralFields;
	}
	
	
	public String getUserFriends() {

		try{
			JsonArray fullFriends = new JsonArray();
			
	 		BatchRequest request = new BatchRequestBuilder(FRIENDS).build();
			List<BatchResponse> batchResponses = facebookClient.executeBatch(request);
			BatchResponse response = batchResponses.get(0);
			JsonObject friends = new JsonObject(response.getBody());
			fullFriends = friends.getJsonArray("data");
			boolean goOn = friends.has("paging");
			
			
			while (goOn){
				
				if (friends.getJsonObject("paging").has("next")){
					
				    String url = friends.getJsonObject("paging").getString("next");
					Response resp = facebookClient.getWebRequestor().executeGet(url);
					if (resp!=null){
						friends = new JsonObject(resp.getBody());
						if (friends.has("data")){
						    JsonArray moreFriends = new JsonArray(friends.getString("data"));
						    for(int i=0; i<moreFriends.length();i++)
						    	fullFriends.put(moreFriends.getJsonObject(i));
						}
						else goOn=false;
					}
					goOn = friends.has("paging");
				}
				else goOn=false;
			}
			
			JsonObject jresp = new JsonObject();
			jresp.put("data", fullFriends);
			return jresp.toString(1);
		}catch(Exception ex){
			return genError(ex.getMessage(), "Unable to get Friends", 400);
		}
		//return getSocialData(FRIENDS);
	}

	public String getUserActivities() {
		
		BatchRequest request   = null;
		BatchResponse response = null;
//		try{
//			
			
			if (firstTime){
				request   = new BatchRequestBuilder(FEED).parameters(Parameter.with("limit", maxPostLimit)).build();
				firstTime = false;
			}
			else
				request= new BatchRequestBuilder(FEED).parameters(Parameter.with("since", lastUpdate), Parameter.with("limit", maxPostLimit)).build();
			

			try{
				JsonArray fullActivities = new JsonArray();
				
		 		
				List<BatchResponse> batchResponses = facebookClient.executeBatch(request);
				response = batchResponses.get(0);
				JsonObject activities = new JsonObject(response.getBody());
				fullActivities = activities.getJsonArray("data");
				boolean goOn = activities.has("paging");
				
				
				while (goOn){
					
					if (activities.getJsonObject("paging").has("next")){
						
					    String url = activities.getJsonObject("paging").getString("next");
						Response resp = facebookClient.getWebRequestor().executeGet(url);
						if (resp!=null){
							activities = new JsonObject(resp.getBody());
							if (activities.has("data")){
							    JsonArray moreActivities = new JsonArray(activities.getString("data"));
							    for(int i=0; i<moreActivities.length();i++)
							    	fullActivities.put(moreActivities.getJsonObject(i));
							}
							else goOn=false;
						}
						goOn = activities.has("paging");
					}
					else goOn=false;
				}
				
				JsonObject jresp = new JsonObject();
				jresp.put("data", fullActivities);
				return jresp.toString(1);
			}catch(Exception ex){
				return genError(ex.getMessage(), "Unable to get Activities", 400);
			}
			
			
//			List<BatchResponse> batchResponses = facebookClient.executeBatch(request);
//			response = batchResponses.get(0);
//			System.out.println("response:::"+response.toString());
		
//		return response.getBody();
//		}
//		  catch(Exception ex){
//			  return genError(ex.getMessage(), "Request failure", 400);
//		  }
		
		
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

	
	
	@Override
	public void post(String value) {
		JsonObject complexValue = null;
		try{
			complexValue = new JsonObject(value);
		}catch (Exception e) {
			
		}
		if (complexValue != null){
			if (complexValue.has("event")){
//				Date tomorrow 		= DateFormat.parse(complexValue.getInt("from") + 1000L * 60L * 60L * 24L);
//				Date twoDaysFromNow = DateFormat.parse(complexValue.getInt("to") + 1000L * 60L * 60L * 48L);
//				
				
				JsonObject jEvt = complexValue.getJsonObject("event");
				FacebookType publishEventResponse = facebookClient.publish( EVENTS, FacebookType.class,
						Parameter.with("name",jEvt.getString("name")), 
						Parameter.with("start_time", jEvt.getString("from")),
						Parameter.with("end_time",   jEvt.getString("to")),
						Parameter.with("location",   jEvt.getString("location")),
						Parameter.with("description",   jEvt.getString("description"))
				
				);
				
			}
			else if (complexValue.has("checkin")){
				Map<String, String> coordinates = new HashMap<String, String>();
				JsonObject jCK = complexValue.getJsonObject("checkin");
				coordinates.put("latitude", jCK.getString("lat"));
				coordinates.put("longitude", jCK.getString("lon"));
				                        
				FacebookType publishCheckinResponse = facebookClient.publish(CHECKINS,
				FacebookType.class, Parameter.with("message", jCK.getString("message")),
			    Parameter.with("coordinates", coordinates), 
			    Parameter.with("place", jCK.getString("place")));

				System.out.println("Published checkin ID: " + publishCheckinResponse.getId());
				
			}
		}
		else{
			System.out.println(" Just a post! ==> "+value );
			FacebookType publishMessageResponse = facebookClient.publish(FEED, FacebookType.class, Parameter.with("message", value));
		}
		
		
		
	}
	
	
}
