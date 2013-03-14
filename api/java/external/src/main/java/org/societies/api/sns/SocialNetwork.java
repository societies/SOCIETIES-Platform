package org.societies.api.sns;

public class SocialNetwork {
	
	/**
	 * Social Netowrk Class that list  availables connectors
	 * 
	 */
	
	public final static String SN_FACEBOOK				= "facebook";
	public final static String SN_TWITTER		  		= "twitter";
	public final static String SN_FOURSQUARE	 		= "foursquare";
	public final static String SN_LINKEDIN		 		= "linkedin";
	public final static String SN_GOOGLEPLUS	 		= "googleplus";
	
	public final static String SN_UNKNOWN	 			= "unknown";
	

	
	/**
	 * Provide the String value of the social network name defined as enumeration
	 * @param name of the social network
	 * @return String of the associate name.
	 */
	public static String getSocialNetworkName(SocialNetworkName name) {
		switch (name) {
		case FACEBOOK:
			return SN_FACEBOOK;
		case TWITTER:
			return SN_TWITTER;
		case LINKEDIN:
			return SN_LINKEDIN;
		case GOOGLEPLUS:
			return SN_GOOGLEPLUS;
		default:
			return SN_UNKNOWN;
		}

	}
	
	
	public static SocialNetworkName getSocialNetwork(String value){
	    if (value.equals(SN_FACEBOOK)) return SocialNetworkName.FACEBOOK;
	    if (value.equals(SN_FOURSQUARE))  return SocialNetworkName.FOURSQUARE;
	    if (value.equals(SN_TWITTER))  return SocialNetworkName.TWITTER;
	    if (value.equals(SN_LINKEDIN))  return SocialNetworkName.LINKEDIN;
	    
	  
	    return null;
	    
	}

	
	

	
}
