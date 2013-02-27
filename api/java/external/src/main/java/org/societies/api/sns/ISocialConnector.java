package org.societies.api.sns;

import java.io.Serializable;



public interface ISocialConnector extends Serializable {


	
	/**
	 * 
	 * Social network constants Strings	
	 * 
	 **/
	
	public final static String FACEBOOK_CONN 		= "facebook";
	public final static String TWITTER_CONN  		= "twitter";
	public final static String FOURSQUARE_CONN 		= "foursquare";
	public final static String LINKEDIN_CONN 		= "linkedin";
	public final static String GOOGLEPLUS_CONN 		= "googleplus";
	
	
	/**
	 * Return the enumeration name of the social network
	 */
	
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
	 * Get Social Connector id, because for a single social network there can be more than one instance for the same user.
	 * @return unique string, which the first part should contains the name of the SN
	 */
	String getID();
	
    /**
     * Get Connector Name 
     * return connectore name saved
     * @return
     */
    String getConnectorName();
  
    
    /**
     * Give the socila
     */
    
    /**
     * This method write a message in the social network by using the connector.
     * 
     * @param activity MUST be a JSON document to allow the connector to post in the right way the post
     */
    void post(String activityEntry);
    
    
}
