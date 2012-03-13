package org.societies.platform.socialdata.model;

import org.societies.platform.FacebookConn.SocialConnector;

public class GroupConveterFactory{
	
	public static GroupConverter getPersonConverter(SocialConnector connector){
		if (connector.getConnectorName().equals(SocialConnector.FACEBOOK_CONN)) 
			return new GroupConverterFromFacebook();
		else if (connector.getConnectorName().equals(SocialConnector.TWITTER_CONN))
			return new GroupConverterFromTwitter();
		else 
			return new GroupConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
