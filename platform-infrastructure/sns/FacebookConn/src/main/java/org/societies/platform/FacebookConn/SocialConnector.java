package org.societies.platform.FacebookConn;

import java.util.Map;

import org.json.JSONObject;
import org.societies.platform.FacebookConn.exeptions.MissingTokenExeptions;

public interface SocialConnector {
	
	public final static String FACEBOOK_CONN 		= "facebook";
	public final static String TWITTER_CONN  		= "twitter";
	public final static String FOURSQUARE_CONN 		= "foursquare";
	
	/**
	 * Constants
	 */
	public final static String AUTH_TOKEN 			= "auth_token";

	
	public enum  SocialDataType{
		PROFILE,
		PERSON,
		GROUP,
		ACTIVITY
	}
	
	String getID();
	
	/**
	 * Set token to access social network data
	 * @param access_token  
	 */
	void setToken(String access_token);
	
	
	/**
	 * set token expiration time
	 */
	void setTokenExpiration(long expires);
	
	/**
	 * Return the token expiration time
	 * @return expiration (long)
	 */
	long getTokenExpiration();
	
	/**
	 * Get stored access token
	 * @return
	 */
    String getToken();
   
    /**
     * Set connector name based on the social network related
     * @param name
     */
    void setConnectorName(String name);
    
    /**
     * return connectore name saved
     * @return
     */
    String getConnectorName();
    
    /**
     * Get data from social network
     * @param path used to access specific data into the social network
     * @return String/JSON to be parsed
     * @throws MissingTokenExeptions 
     */
    String getSocialData(String path) throws MissingTokenExeptions;
    
    
    
    /**
     * Trigger a process to fetch access token data
     * @return
     */
    Map<String, String> requireAccessToken();
    
    
    /**
     * Disconnect the connector to the social network removing all tokens
     */
    void disconnect();
    
    /**
     * Set max number of items to fetch
     * @param postLimit
     */
    void setMaxPostLimit(int postLimit);
    
    /**
     * Set specific parameter for the social network to make rich query
     * @param parmeters with the social network sintax
     */
    void setParameter(Object p);
    
    /**
     * Remove previus parameters configuration
     */
    void resetParameters();
   
    
    /**
     * Return the JSON object that contains user profile data
     * @return JSONObject
     */
    JSONObject getUserProfile();
   
    /**
     * Provides the JSON Object with the list of friends 
     * @return JSON Object
     */
    JSONObject getUserFriends();
    
    /**
     * Provides the JSON Object with the user Activity feed
     * @return JSON Object
     */
    JSONObject getUserActivities();
    
    /**
     * Provides the JSON Object with the Groups subscribed by the user
     * @return JSON Object
     */
    JSONObject getUserGroups();
    
    
    
    
}
