package org.societies.api.sns;

import org.societies.api.schema.sns.socialdata.model.SocialNetwork;


public interface ISocialDataExternal {
		
		public static final String POST_NAME = "name";
		public static final String POST_TYPE = "type";	
		public static final String POST_LAT = "lat";
		public static final String POST_LON = "lon";
		public static final String POST_FROM = "from";
		public static final String POST_TO   = "to";
		public static final String POST_MESSAGE = "message";
		public static final String POST_PLACE = "place";
		public static final String POST_DESCR = "description";
		public static final String POST_LOCATION = "location";
		
		
		
		
		/**
		
	     * Check if the specific connector is available
	     * @param connector Connetor instance
	     * @return boolean
	     */
	    boolean isConnected(SocialNetwork socialNetworkName);
	    
	    
	    /**
	     * Provides the status of the bundle. 
	     * @return {@link SocialDataState}
	     */
	    SocialDataState getStatus();
	    
	    
	    /**
	     * Post an Update Message to a social network. This can be a Twitt
	     * @param toSocialNetwotkName
	     * @param message
	     */
	    void postMessage(SocialNetwork toSocialNetwork, Message message);
	    
	   
	    void postCheckin(SocialNetwork SocialNetwork, Checkin checkin);
	    
	   
	    void postEvent(SocialNetwork socialNetwork, Event event);
	   
	    



}