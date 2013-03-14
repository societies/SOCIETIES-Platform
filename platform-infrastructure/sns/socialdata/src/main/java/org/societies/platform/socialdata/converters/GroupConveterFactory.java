package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.sns.SocialNetworkName;



public class GroupConveterFactory{
	
	public static GroupConverter getPersonConverter(ISocialConnector connector){
		if (connector.getConnectorName().equals(SocialNetworkName.FACEBOOK)) 
			return new GroupConverterFromFacebook();
		else if (connector.getConnectorName().equals(SocialNetworkName.TWITTER))
			return new GroupConverterFromTwitter();
		else if (connector.getConnectorName().equals(SocialNetworkName.LINKEDIN))
			return new GroupConverterFromLinkedin();
		else if (connector.getConnectorName().equals(SocialNetworkName.FOURSQUARE))
			return new GroupConverterFromFoursquare();
		else 
			return new GroupConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
