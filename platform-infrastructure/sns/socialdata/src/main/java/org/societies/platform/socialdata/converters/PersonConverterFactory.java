package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;


public class PersonConverterFactory{
	
	public static PersonConverter getPersonConverter(ISocialConnector connector){
		if (ISocialConnector.FACEBOOK_CONN.equals(connector.getConnectorName())) 
			return new PersonConverterFromFacebook();
		else if (ISocialConnector.TWITTER_CONN.equals(connector.getConnectorName()))
			return new PersonConverterFromTwitter();
		else 
			return new PersonConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
