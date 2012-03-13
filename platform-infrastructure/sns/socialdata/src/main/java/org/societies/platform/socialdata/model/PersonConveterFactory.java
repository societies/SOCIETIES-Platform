package org.societies.platform.socialdata.model;

import org.societies.platform.FacebookConn.SocialConnector;

public class PersonConveterFactory{
	
	public static PersonConverter getPersonConverter(SocialConnector connector){
		if (connector.getConnectorName().equals(SocialConnector.FACEBOOK_CONN)) 
			return new PersonConverterFromFacebook();
		else if (connector.getConnectorName().equals(SocialConnector.TWITTER_CONN))
			return new PersonConverterFromTwitter();
		else 
			return new PersonConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
