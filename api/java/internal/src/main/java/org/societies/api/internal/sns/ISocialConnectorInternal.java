package org.societies.api.internal.sns;

import java.util.Map;


public interface ISocialConnectorInternal extends org.societies.api.sns.ISocialConnector {

	
	/**
	 * Constants
	 */
	public final static String AUTH_TOKEN 			= "auth_token";
	public final static String IDENTITY 			= "identity";

	
	/**
	 * Enumeration Social Data type 
	 */
	
	public enum  SocialDataType{
		PROFILE,
		PERSON,
		GROUP,
		ACTIVITY
	}
	
	/**
	 * Set token to access social network data
	 * @param access_token  
	 */
	void setToken(String access_token);
	
	
	/**
	 * Set token expiration time
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
    String getSocialData(String path);
    
    
    
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
    * @param key of the param
    * @param value in a string form
    */
    void setParameter(String key, String value);
    
    /**
     * Remove previus parameters configuration
     */
    void resetParameters();
   
    
    /**
     * Return the JSON String that contains user profile data
     * @return String to be parse as JSONObject
     */
    String getUserProfile();
   
    /**
     * Provides the JSON String   with the list of friends 
     * @return JSON Object
     */
    String getUserFriends();
    
    /**
     * Provides the String JSON  with the user Activity feed
     * @return JSON String
     */
    String getUserActivities();
    
    /**
     * Provides the JSON String with the Groups subscribed by the user
     * @return JSON String
     */
    String getUserGroups();
    
    
    
    
    String getApiKey();
    
    
    String getApiSecret();
    
    
    String getCallabackURL();
    
    
}
