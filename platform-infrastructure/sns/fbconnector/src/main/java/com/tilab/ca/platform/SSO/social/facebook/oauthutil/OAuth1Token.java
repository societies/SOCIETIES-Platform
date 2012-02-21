/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
 ******************************************** 
  $Id: PortalEvent.java 7284 2010-04-14 17:38:23Z papurello $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/aup/branches/rel-1_3-ev/src/main/java/com/tilab/ca/platform/aup/presentation/portal/PortalEvent.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook.oauthutil;

/**
 * @author X0157246
 */
public class OAuth1Token {
    
    String token = null;
    String tokenSecret = null;
    String socialUsername = null;
    
	int expires = -1;

	public int getExpires() {
		return expires;
	}
	public void setExpires(int expires) {
		this.expires = expires;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getTokenSecret() {
		return tokenSecret;
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	
	public String getSocialUsername() {
		return socialUsername;
	}
	public void setSocialUsername(String socialUsername) {
		this.socialUsername = socialUsername;
	}
    
}
