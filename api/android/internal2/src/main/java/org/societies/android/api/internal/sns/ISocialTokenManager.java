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
package org.societies.android.api.internal.sns;

import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

/**
 * Interface to manage access tokens of social networks.
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public interface ISocialTokenManager {	
	String methodsArray [] = {"getToken(String client, SocialNetwork socialNetwork)"};
	

	public static final String GET_TOKEN = "org.societies.android.platform.socialdata.GET_TOKEN";
	public static final String INTENT_RETURN_KEY = "org.societies.android.platform.socialdata.ReturnValue";
	public static final String SOCIAL_NETWORK_KEY = "org.societies.android.platform.socialdata.SocialNetwork";
	public static final String EXTRA_EXPIRES = "org.societies.android.platform.socialdata.SocialTokenManager.extra.EXPIRES";
	
	/**
	 * Get an access token for the desired social network.
	 * A broadcast intent is sent with the Action GET_TOKEN, 
	 * and the Extra SOCIAL_NETWORK_KEY with the value of the socialNetwork parameter, 
	 * the Extra INTENT_RETURN_KEY with the token 
	 * and the Extra EXTRA_EXPIRES with the expire time of the token. 
	 * If, for some reason, cannot get the token the intent will return the last two extras as null.
	 * @param client Package name of the application that will receive the intent with the asynchronous return value.
	 * @param socialNetwork Social network to get the token.
	 */
	public void getToken(String client, SocialNetwork socialNetwork);

}
