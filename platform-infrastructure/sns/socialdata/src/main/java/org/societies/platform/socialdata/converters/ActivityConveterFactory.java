package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

public class ActivityConveterFactory{
	
	public static ActivityConverter getConverter(ISocialConnector connector){
		
	    	if (connector.getSocialNetwork().equals(SocialNetwork.FACEBOOK)) 
			return new ActivityConverterFromFacebook();
		else if (connector.getSocialNetwork().equals(SocialNetwork.TWITTER))
			return new ActivityConverterFromTwitter();
		else if (connector.getSocialNetwork().equals(SocialNetwork.FOURSQUARE))
			return new ActivityConverterFromFoursquare();
		else if (connector.getSocialNetwork().equals(SocialNetwork.LINKEDIN))
			return new ActivityConverterFromLinkedin();
		else 
			return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
