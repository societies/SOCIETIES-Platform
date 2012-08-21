/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.platform.sns.connecor.linkedin;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/*
 * twitter access token manager
 */
public class LinkedinToken {

	
	
	private String defaultApiKey = "u2rlilsezfjx";
	private String defaultApiSecret = "DgvJS0g5Obokq0aa";
	
	
	private Token accessToken = null;
	private OAuthService service = null;

	public LinkedinToken(String accessToken) {
		this.service = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(defaultApiKey).apiSecret(defaultApiSecret).build();

		String[] tokenList = null;
		if (accessToken.contains(",")) {
			tokenList = accessToken.split(",");
			this.accessToken = new Token(tokenList[0], tokenList[1]);
		} else
			this.accessToken = new Token("", "");
		
	}

	
	public Token getAccessToken() {
		return this.accessToken;
	}

	public OAuthService getAuthService() {
		return this.service;
	}

	// get access token from user
	public void getAccessTokenFromUser() {
		Token requestToken = service.getRequestToken();
		String authUrl = service.getAuthorizationUrl(requestToken);
		Verifier verifier = new Verifier("verifier you got from the user");
		this.accessToken = service.getAccessToken(requestToken, verifier);

	}

	// get access token from context broker
	public Token getAccessTokenFromContextBroker() {
		return null;
	}

	// set access token from context broker
	public boolean setAccessTokenToContextBrocker() {
		return false;
	}

}