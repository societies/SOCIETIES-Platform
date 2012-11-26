package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;



public class GroupConveterFactory{
	
	public static GroupConverter getPersonConverter(ISocialConnector connector){
		if (connector.getConnectorName().equals(ISocialConnector.FACEBOOK_CONN)) 
			return new GroupConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnector.TWITTER_CONN))
			return new GroupConverterFromTwitter();
		else if (connector.getConnectorName().equals(ISocialConnector.LINKEDIN_CONN))
			return new GroupConverterFromLinkedin();
		else if (connector.getConnectorName().equals(ISocialConnector.FOURSQUARE_CONN))
			return new GroupConverterFromFoursquare();
		else 
			return new GroupConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
