package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

public class ActivityConverterFactory{
	
	public static ActivityConverter getConverter(ISocialConnector connector){
		
	    	if (connector.getSocialNetwork().equals(SocialNetwork.FACEBOOK.value())) 
			return new ActivityConverterFromFacebook();
		
		if (connector.getSocialNetwork().equals(SocialNetwork.TWITTER.value()))
			
		    	return new ActivityConverterFromTwitter();
		
		if (connector.getSocialNetwork().equals(SocialNetwork.FOURSQUARE.value())) 
			return new ActivityConverterFromGooglePlus();
		
		if (connector.getSocialNetwork().equals(SocialNetwork.LINKEDIN.value())) 
			return new ActivityConverterFromLinkedin();

		
		
		return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
