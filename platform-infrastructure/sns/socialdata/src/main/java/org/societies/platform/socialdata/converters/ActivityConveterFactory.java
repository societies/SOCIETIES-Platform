package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;



public class ActivityConveterFactory{
	
	public static ActivityConverter getActivityConverter(ISocialConnector connector){
		if (connector.getConnectorName().equals(ISocialConnector.FACEBOOK_CONN)) 
			return new ActivityConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnector.TWITTER_CONN))
			return new ActivityConverterFromTwitter();
		else if (connector.getConnectorName().equals(ISocialConnector.FOURSQUARE_CONN))
			return new ActivityConverterFromFoursquare();
		else 
			return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
