package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

public class PersonConverterFactory{
	
	public static PersonConverter getConverter(ISocialConnector connector){

	    
	    if (SocialNetwork.FACEBOOK.equals(connector.getSocialNetwork())) 
			return new PersonConverterFromFacebook();
		else if (SocialNetwork.TWITTER.equals(connector.getSocialNetwork()))
			return new PersonConverterFromTwitter();
		else if (SocialNetwork.FOURSQUARE.equals(connector.getSocialNetwork()))
			return new PersonConverterFromFoursquare();
		else if (SocialNetwork.LINKEDIN.equals(connector.getSocialNetwork()))
			return new PersonConverterFromLinkedin();
		else if (SocialNetwork.GOOGLEPLUS.equals(connector.getSocialNetwork())) 
			return new PersonConverterFromGooglePlus();
		else 
			return new PersonConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
