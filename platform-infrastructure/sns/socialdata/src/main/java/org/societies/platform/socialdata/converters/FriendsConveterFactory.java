package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

public class FriendsConveterFactory{
	

	public static FriendsConverter getConverter(ISocialConnector connector){
		if (connector.getSocialNetwork().equals(SocialNetwork.FACEBOOK)) 
			return new FriendsConverterFromFacebook();
		else if (connector.getSocialNetwork().equals(SocialNetwork.TWITTER))
			return new FriendsConverterFromTwitter();
		else if (connector.getSocialNetwork().equals(SocialNetwork.FOURSQUARE))
			return new FriendsConverterFromFoursquare();
		else if (connector.getSocialNetwork().equals(SocialNetwork.LINKEDIN))
			return new FriendsConverterFromLinkedin();
		else 
			return new FriendsConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
