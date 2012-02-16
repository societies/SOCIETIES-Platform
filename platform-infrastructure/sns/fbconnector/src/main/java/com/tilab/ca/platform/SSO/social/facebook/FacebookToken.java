package com.tilab.ca.platform.SSO.social.facebook;


public class FacebookToken {
    
    String accessToken = null;
    int expires = -1;

	public int getExpires() {
		return expires;
	}
	public void setExpires(int expires) {
		this.expires = expires;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
  
    
}
