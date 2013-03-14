package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

public class ActivityConverterFactory{
	
	public static ActivityConverter getActivityConverter(ISocialConnector connector){
		if (connector.getSocialNetwork().equals(SocialNetwork.FACEBOOK)) 
			return new ActivityConverterFromFacebook();
		
		if (connector.getSocialNetwork().equals(SocialNetwork.TWITTER))
			
		    	return new ActivityConverterFromTwitter();
		
		if (connector.getSocialNetwork().equals(SocialNetwork.FOURSQUARE)) 
			return new ActivityConverterFromGooglePlus();
		
		if (connector.getSocialNetwork().equals(SocialNetwork.LINKEDIN)) 
			return new ActivityConverterFromLinkedin();

		
		
		return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
