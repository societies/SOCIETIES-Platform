package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.sns.SocialNetworkName;


public class PersonConverterFactory{
	
	public static PersonConverter getPersonConverter(ISocialConnector connector){
		if (SocialNetworkName.FACEBOOK.equals(connector.getSocialNetworkName())) 
			return new PersonConverterFromFacebook();
		else if (SocialNetworkName.TWITTER.equals(connector.getSocialNetworkName()))
			return new PersonConverterFromTwitter();
		else if (SocialNetworkName.FOURSQUARE.equals(connector.getSocialNetworkName()))
			return new PersonConverterFromFoursquare();
		else if (SocialNetworkName.LINKEDIN.equals(connector.getSocialNetworkName()))
			return new PersonConverterFromLinkedin();
		else if (SocialNetworkName.GOOGLEPLUS.equals(connector.getSocialNetworkName())) 
			return new PersonConverterFromGooglePlus();
		else 
			return new PersonConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
