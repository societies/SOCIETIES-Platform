package org.societies.api.internal.sns;

import java.util.Map;

public interface ISocialConnector {
	
	public final static String FACEBOOK_CONN 		= "facebook";
	public final static String TWITTER_CONN  		= "twitter";
	public final static String FOURSQUARE_CONN 		= "foursquare";
	public final static String LINKEDIN_CONN 		= "linkedin";
	public final static String GOOGLEPLUS_CONN 		= "googleplus";
	
	public enum SocialNetwork{
		Facebook,
		Foursquare,
		twitter,
		linkedin,
		googleplus;
		
		private final String value = "";
		
		public static SocialNetwork fromValue(String v) {
	        for (SocialNetwork c: SocialNetwork.values()) {
	            if (c.value.equals(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
	
	
	/**
	 * Constants
	 */
	public final static String AUTH_TOKEN 			= "auth_token";
	public final static String IDENTITY 			= "identity";

	
	public enum  SocialDataType{
		PROFILE,
		PERSON,
		GROUP,
		ACTIVITY
	}
	
	/**
	 * Get Social Connector id, because for a single social network there can be more than one instance for the same user.
	 * @return unique string, which the first part should contains the name of the SN
	 */
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
     * Return the JSON object that contains user profile data
     * @return JSONObject
     */
    String getUserProfile();
   
    /**
     * Provides the JSON Object with the list of friends 
     * @return JSON Object
     */
    String getUserFriends();
    
    /**
     * Provides the JSON Object with the user Activity feed
     * @return JSON Object
     */
    String getUserActivities();
    
    /**
     * Provides the JSON Object with the Groups subscribed by the user
     * @return JSON Object
     */
    String getUserGroups();
    
    /**
     * This method write a message in the social network by using the connector.
     * 
     * @param activity MUST be a JSON document to allow the connector to post in the right way the post
     */
    void post(String activityEntry);
    
    
}
