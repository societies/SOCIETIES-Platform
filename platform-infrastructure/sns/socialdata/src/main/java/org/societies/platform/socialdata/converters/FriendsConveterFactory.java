package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.sns.SocialNetworkName;


public class FriendsConveterFactory{
	
	public static FriendsConverter getFriendsConverter(ISocialConnector connector){
		if (connector.getSocialNetworkName().equals(SocialNetworkName.FACEBOOK)) 
			return new FriendsConverterFromFacebook();
		else if (connector.getSocialNetworkName().equals(SocialNetworkName.TWITTER))
			return new FriendsConverterFromTwitter();
		else if (connector.getSocialNetworkName().equals(SocialNetworkName.FOURSQUARE))
			return new FriendsConverterFromFoursquare();
		else if (connector.getSocialNetworkName().equals(SocialNetworkName.LINKEDIN))
			return new FriendsConverterFromLinkedin();
		else 
			return new FriendsConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
