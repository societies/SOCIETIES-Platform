package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.sns.SocialNetworkName;



public class ActivityConveterFactory{
	
	public static ActivityConverter getActivityConverter(ISocialConnector connector){
		
	    	if (connector.getSocialNetworkName().equals(SocialNetworkName.FACEBOOK)) 
			return new ActivityConverterFromFacebook();
		else if (connector.getSocialNetworkName().equals(SocialNetworkName.TWITTER))
			return new ActivityConverterFromTwitter();
		else if (connector.getSocialNetworkName().equals(SocialNetworkName.FOURSQUARE))
			return new ActivityConverterFromFoursquare();
		else if (connector.getSocialNetworkName().equals(SocialNetworkName.LINKEDIN))
			return new ActivityConverterFromLinkedin();
		else 
			return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
