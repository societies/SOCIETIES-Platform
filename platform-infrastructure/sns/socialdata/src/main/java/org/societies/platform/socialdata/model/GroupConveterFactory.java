package org.societies.platform.socialdata.model;

import org.societies.api.internal.sns.ISocialConnector;



public class GroupConveterFactory{
	
	public static GroupConverter getPersonConverter(ISocialConnector connector){
		if (connector.getConnectorName().equals(ISocialConnector.FACEBOOK_CONN)) 
			return new GroupConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnector.TWITTER_CONN))
			return new GroupConverterFromTwitter();
		else 
			return new GroupConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
