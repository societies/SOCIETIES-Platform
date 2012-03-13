package org.societies.platform.socialdata.model;

import org.societies.platform.FacebookConn.SocialConnector;

public class AcrivityConveterFactory{
	
	public static ActivityConverter getPersonConverter(SocialConnector connector){
		if (connector.getConnectorName().equals(SocialConnector.FACEBOOK_CONN)) 
			return new ActivityConverterFromFacebook();
		else if (connector.getConnectorName().equals(SocialConnector.TWITTER_CONN))
			return new ActivityConverterFromTwitter();
		else 
			return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
