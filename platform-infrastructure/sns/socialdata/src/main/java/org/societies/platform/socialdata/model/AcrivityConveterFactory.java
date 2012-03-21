package org.societies.platform.socialdata.model;

import org.societies.api.internal.sns.ISocialConnector;


public class AcrivityConveterFactory{
	
	public static ActivityConverter getPersonConverter(ISocialConnector connector){
		if (connector.getConnectorName().equals(ISocialConnector.FACEBOOK_CONN)) 
			return new ActivityConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnector.TWITTER_CONN))
			return new ActivityConverterFromTwitter();
		else 
			return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
