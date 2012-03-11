package org.societies.platform.sns.twconnector;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


/*
 * twitter access token manager
 */
class TwitterToken{


	private String defaultApiKey = "cLD3W6l4bfXs8cwlzXGmRQ";
	private String defaultApiSecret = "IN6Oo79VnduEt5HRI9IQY07SpW86xkcN4UICuFg1zA0";
	private String defaultAccessToken = "468234144-1rapLRk4vm1u8H0zCQkV19pps7mVbuST8ANVMhDp";
	private String defaultAccessTokenSecret = "LKDpdsiNb0k98X49u9FI4ouyXblYk4E3mdDXLC0WCk";
	private Token accessToken = null;
	private OAuthService service = null;

	public TwitterToken(){
		this.service = new ServiceBuilder()
		.provider(TwitterApi.class)
		.apiKey(defaultApiKey)
		.apiSecret(defaultApiSecret)
		.build();

		this.accessToken = new Token(defaultAccessToken, defaultAccessTokenSecret);
	}
	
	public Token getAccessToken(){
		return this.accessToken;	
	}
	
	public OAuthService getAuthService(){
		return this.service;	
	}
	
//  get access token from user
	public void getAccessTokenFromUser(){
		Token requestToken = service.getRequestToken();
		String authUrl = service.getAuthorizationUrl(requestToken);
		Verifier verifier = new Verifier("verifier you got from the user");
		this.accessToken = service.getAccessToken(requestToken, verifier);
		
	}
	
//	get access token from context broker
	public Token getAccessTokenFromContextBroker(){
		return null;
	}
	

//	set access token from context broker
	public boolean setAccessTokenToContextBrocker(){
		return false;
	}

}