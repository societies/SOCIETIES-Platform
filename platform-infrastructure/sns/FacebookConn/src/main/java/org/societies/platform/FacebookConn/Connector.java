package org.societies.platform.FacebookConn;

import java.util.Map;

import org.societies.platform.FacebookConn.exeptions.MissingTokenExeptions;

import com.restfb.Parameter;

public interface Connector {
	
	public final static String FACEBOOK_CONN 		= "facebook";
	public final static String TWITTER_CONN  		= "twitter";
	public final static String FOURSQUARE_CONN 		= "foursquare";
	
	public final static String AUTH_TOKEN 			= "auth_token";
	
	/**
	 * Set token to access social network data
	 * @param access_token  
	 */
	void setToken(String access_token);
	    
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
    
    
    void setMaxPostLimit(int postLimit);
    
    
    void setParameter(Object p);
    
    
    void resetParameters();

	
    
    
    
}
