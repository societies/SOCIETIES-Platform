package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnectorInternal;


public class PersonConverterFactory{
	
	public static IPersonConverter getPersonConverter(ISocialConnectorInternal connector){
		if (ISocialConnectorInternal.FACEBOOK_CONN.equals(connector.getConnectorName())) 
			return new PersonConverterFromFacebook();
		else if (ISocialConnectorInternal.TWITTER_CONN.equals(connector.getConnectorName()))
			return new PersonConverterFromTwitter();
		else if (ISocialConnectorInternal.FOURSQUARE_CONN.equals(connector.getConnectorName()))
			return new PersonConverterFromFoursquare();
		else if (ISocialConnectorInternal.LINKEDIN_CONN.equals(connector.getConnectorName()))
			return new PersonConverterFromLinkedin();
		else if (ISocialConnectorInternal.GOOGLEPLUS_CONN.equals(connector.getConnectorName())) 
			return new PersonConverterFromGooglePlus();
		else 
			return new PersonConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
