package com.tilab.ca.platform.SSO.social.facebook.social;

import java.util.List;

/**
 * OpenSocialConnector interface
*/

/**
 * @author AAntonazzo
 *
 */
public interface OpenSocialConnector {
    
	
	/**
	 * @param token (OAuth2 access_token -> FB, OAuth1 token -> TW)
	 *        tokenSecret (OAuth1 tokenSecret -> TW)
	 * @return List of Activity entries (Open Social)
	 */
	public List<TimSocialActivity> getActivityStreams(String token, String tokenSecret);
    
    
}
