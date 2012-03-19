package org.societies.platform.socialdata.model;

import org.societies.api.internal.sns.ISocialConnector;


public class PersonConveterFactory{
	
	public static PersonConverter getPersonConverter(ISocialConnector connector){
		if (connector.getConnectorName().equals(ISocialConnector.FACEBOOK_CONN)) 
			return new PersonConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnector.TWITTER_CONN))
			return new PersonConverterFromTwitter();
		else 
			return new PersonConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
