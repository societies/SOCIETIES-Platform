package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnectorInternal;


public class FriendsConveterFactory{
	
	public static FriendsConverter getPersonConverter(ISocialConnectorInternal connector){
		if (connector.getConnectorName().equals(ISocialConnectorInternal.FACEBOOK_CONN)) 
			return new FriendsConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.TWITTER_CONN))
			return new FriendsConverterFromTwitter();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.FOURSQUARE_CONN))
			return new FriendsConverterFromFoursquare();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.LINKEDIN_CONN))
			return new FriendsConverterFromLinkedin();
		else 
			return new FriendsConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
