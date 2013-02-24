package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnectorInternal;



public class ActivityConveterFactory{
	
	public static ActivityConverter getActivityConverter(ISocialConnectorInternal connector){
		if (connector.getConnectorName().equals(ISocialConnectorInternal.FACEBOOK_CONN)) 
			return new ActivityConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.TWITTER_CONN))
			return new ActivityConverterFromTwitter();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.FOURSQUARE_CONN))
			return new ActivityConverterFromFoursquare();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.LINKEDIN_CONN))
			return new ActivityConverterFromLinkedin();
		else 
			return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
