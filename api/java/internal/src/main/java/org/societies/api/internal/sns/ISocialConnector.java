package org.societies.api.internal.sns;

import java.io.Serializable;
import java.util.Map;

import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

public interface ISocialConnector extends Serializable {

    /**
     * Constants
     */
    public final static String AUTH_TOKEN 	= "auth_token";
    public final static String IDENTITY 	= "identity";

    /**
     * Get Social Connector id, because for a single social network there can be
     * more than one instance for the same user.
     * 
     * @return unique string, which the first part should contains the name of
     *         the SN
     */
    String getID();

    /**
     * Set token to access social network data
     * 
     * @param access_token
     */
    void setToken(String access_token);

   /**
    * 
    * Set token expiration values
    * @deprecated
    * @param expires long value
    */
    void setTokenExpiration(long expires);

    /**
     * Return token expiration value
     * @deprecated
     * @return expiration (long)
     */
    long getTokenExpiration();

    /**
     * Get the access_token to grant access to SocialNetork data
     * @return String value
     */
    String getToken();

    /**
     * Social Network String name
     * @param name
     */
    void setConnectorName(String name);

    /**
     * Get Social network simbolic string name
     * @return String name
     */
    String getConnectorName();

    /**
     * Extension for use specific social network PATH query to fetch data
     * that is not defined in the list of SocialDataType
     * 
     * @param path used to access specific data into the social network
     * @return String/JSON to be parsed
     * @throws MissingTokenExeptions
     * @deprecated
     */
    String getSocialData(String path);

    /**
     * NOT IMPLEMENTES
     * @deprecated
     * @return
     */
    Map<String, String> requireAccessToken();

   
    /**
     * Remove in a shot all connectors
     */
    void disconnect();

    /**
     * Set number of items to fetch from the net
     * @deprecated
     * @param postLimit
     */
    void setMaxPostLimit(int postLimit);

    /**
     * Set SN Specific param to improve the query
     * [access_token is the only parameter MANDATORY]
     * 
     * @param key Param  
     * @param value Param
    
     */
    void setParameter(String key, String value);

    
    /**
     * Remove previus parameters configuration
     */
    void resetParameters();

    /**
     * Return the JSON object that contains user profile data
     * 
     * @return JSONObject
     */
    String getUserProfile();

    /**
     * Provides the JSON String value that contains the frinds list
     * 
     * @return JSON String
     */
    String getUserFriends();

    /**
     * Provides the JSON String with the Activity feeds
     * 
     * @return JSON String
     */
    String getUserActivities();

    /**
     * Provides the String Object with the  subscribed Groups
     * 
     * @return String Object
     */
    String getUserGroups();

    /**
     * This method write a message in the social network by using the connector.
     * 
     * @param activity
     *            MUST be a JSON document to allow the connector to post in the
     *            right way the post
     */
    void post(String activityEntry);
    
    
    /**
     * Provide the name of the SocialNetwork 
     * @return {@link Socialnetwork}
     */
    SocialNetwork getSocialNetwork();

}
