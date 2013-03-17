package org.societies.platform.socialdata.converters;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;


public class GroupConveterFactory{
	

	public static GroupConverter getConverter(ISocialConnector connector){
		if (connector.getSocialNetwork().equals(SocialNetwork.FACEBOOK)) 
			return new GroupConverterFromFacebook();
		else if (connector.getSocialNetwork().equals(SocialNetwork.TWITTER))
			return new GroupConverterFromTwitter();
		else if (connector.getSocialNetwork().equals(SocialNetwork.LINKEDIN))
			return new GroupConverterFromLinkedin();
		else if (connector.getSocialNetwork().equals(SocialNetwork.FOURSQUARE))
			return new GroupConverterFromFoursquare();
		else 
			return new GroupConverterFromSN();	
	}
}
