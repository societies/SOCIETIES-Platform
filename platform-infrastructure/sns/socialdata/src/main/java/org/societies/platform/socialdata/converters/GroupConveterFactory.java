package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnectorInternal;
import org.societies.api.sns.ISocialConnector;



public class GroupConveterFactory{
	
	public static GroupConverter getPersonConverter(ISocialConnectorInternal connector){
		if (connector.getConnectorName().equals(ISocialConnector.FACEBOOK_CONN)) 
			return new GroupConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.TWITTER_CONN))
			return new GroupConverterFromTwitter();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.LINKEDIN_CONN))
			return new GroupConverterFromLinkedin();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.FOURSQUARE_CONN))
			return new GroupConverterFromFoursquare();
		else 
			return new GroupConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
