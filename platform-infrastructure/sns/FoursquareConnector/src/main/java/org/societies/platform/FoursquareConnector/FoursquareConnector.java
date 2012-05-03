package org.societies.platform.FoursquareConnector;

import org.societies.api.internal.sns.ISocialConnector;


public interface FoursquareConnector extends ISocialConnector {
	
	public static final String USER_PROFILE = "https://api.foursquare.com/v2/users/self?oauth_token=";
	public static final String RECENT_CHECKINS = "https://api.foursquare.com/v2/users/self/checkins?oauth_token=";
	

}