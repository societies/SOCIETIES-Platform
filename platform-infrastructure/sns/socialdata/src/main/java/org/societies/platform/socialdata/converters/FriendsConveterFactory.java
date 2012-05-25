package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;


public class FriendsConveterFactory{
	
	public static FriendsConverter getPersonConverter(ISocialConnector connector){
		if (connector.getConnectorName().equals(ISocialConnector.FACEBOOK_CONN)) 
			return new FriendsConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnector.TWITTER_CONN))
			return new FriendsConverterFromTwitter();
		else if (connector.getConnectorName().equals(ISocialConnector.FOURSQUARE_CONN))
			return new FriendsConverterFromFoursquare();
		else 
			return new FriendsConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
