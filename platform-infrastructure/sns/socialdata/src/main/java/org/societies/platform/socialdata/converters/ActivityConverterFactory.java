package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.sns.SocialNetwork;
import org.societies.api.sns.SocialNetworkName;


public class ActivityConverterFactory{
	
	public static ActivityConverter getActivityConverter(ISocialConnector connector){
		if (connector.getSocialNetworkName().equals(SocialNetworkName.FACEBOOK)) 
			return new ActivityConverterFromFacebook();
		
		if (connector.getSocialNetworkName().equals(SocialNetworkName.TWITTER))
			
		    	return new ActivityConverterFromTwitter();
		
		if (connector.getSocialNetworkName().equals(SocialNetworkName.FOURSQUARE)) 
			return new ActivityConverterFromGooglePlus();
		
		if (connector.getSocialNetworkName().equals(SocialNetworkName.LINKEDIN)) 
			return new ActivityConverterFromLinkedin();

		
		
		return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
