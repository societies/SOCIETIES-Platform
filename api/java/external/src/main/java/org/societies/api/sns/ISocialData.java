package org.societies.api.sns;

import java.util.Map;

public interface ISocialData{

	
	
	public static final String POST_NAME = "name";
	public static final String POST_TYPE = "type";	
	public static final String POST_LAT = "lat";
	public static final String POST_LON = "lon";
	public static final String POST_FROM = "from";
	public static final String POST_TO 	 = "to";
	public static final String POST_MESSAGE = "message";
	public static final String POST_PLACE = "place";
	public static final String POST_DESCR = "description";
	public static final String POST_LOCATION = "location";
	
	
	public static enum STATE {DATA_AVAILABLE, DOWNLOADING, ERROR, NEED_UPDATE}
	
	/**
	 * get the Current state of the bundle
	 * @return STATE
	 */
	STATE getState();
    
    /**
     * Check if the specific connector is available
     * @param connector Connetor instance
     * @return boolean
     */
    boolean isAvailable(ISocialConnector connector);
    
    
    /**
     * The Method post a message (String) to a specifc SocialNetwork
     * @param socialNetworkName  the name of the Social Network [Facebook, Twitter, Foursquare]
     * @param message the String to be posted
     */
    void postMessage(ISocialConnector.SocialNetwork socialNetworkName, String message);
    
   /**
    * This Method allow to post on a specific Social network more than a simple string, like events or checkin. 
    * the data required are put in the MAP <key, value> in order to be correctly processed 
    * 
    * @param socialNetwork to send the data	
    * @param data a MAP<String, Data> to instruct the connector to post the data on the SN
    */
    void postData(ISocialConnector.SocialNetwork socialNetwork, Map<String,?> data);
    
}