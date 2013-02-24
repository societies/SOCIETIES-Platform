package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnectorInternal;


public class ActivityConverterFactory{
	
	public static ActivityConverter getActivityConverter(ISocialConnectorInternal connector){
		if (connector.getConnectorName().equals(ISocialConnectorInternal.FACEBOOK_CONN)) 
			return new ActivityConverterFromFacebook();
		else if (connector.getConnectorName().equals(ISocialConnectorInternal.TWITTER_CONN))
			return new ActivityConverterFromTwitter();
		else if (ISocialConnectorInternal.GOOGLEPLUS_CONN.equals(connector.getConnectorName())) 
			return new ActivityConverterFromGooglePlus();
		else 
			return new ActivityConverterFromSN();
		
	}
	
	

	
	
	
	
	
}
