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
package org.societies.platform.socialdata.utils;

import java.util.HashMap;
import java.util.Map;

import org.societies.api.internal.schema.sns.socialdata.SocialDataMethod;
import org.societies.api.internal.schema.sns.socialdata.SocialdataMessageBean;
import org.societies.api.internal.schema.sns.socialdata.Socialnetwork;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;

/**
 * Utility class with common code for sending/receiving SocialData Comms beans.
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class SocialDataCommsUtils {
	
	public static final String PARAMS_SEPARATOR = ";";
	public static final String PARAM_NAME_VALUE_SEPARATOR = "=";
	
	public static final String PARAM_NAME_VALIDITY = "validity";

	public static Socialnetwork socialNetwork(ISocialConnector.SocialNetwork socialNetwork) {
		return Socialnetwork.fromValue(socialNetwork.name().toLowerCase());
	}
	
	public static ISocialConnector.SocialNetwork socialNetwork(Socialnetwork socialNetwork) {
		switch(socialNetwork) {
		case FACEBOOK:
			return SocialNetwork.Facebook;
		case FOURSQUARE:
			return SocialNetwork.Foursquare;
		case TWITTER:
			return SocialNetwork.twitter;
		}
		throw new IllegalArgumentException("Social Network '"+socialNetwork+"' not defined in the internal API.");
	}
	
	public static Map<String, String> parseParams(String params) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		String[] paramsSplit = params.split(PARAMS_SEPARATOR);
		for(int i=0; i<paramsSplit.length; i++) 
			if(paramsSplit[i].length() > 0) {
				String[] nameValue = paramsSplit[i].split(PARAM_NAME_VALUE_SEPARATOR, 2);
				paramsMap.put(nameValue[0], nameValue[1]);
			}
		return paramsMap;
	}
	
	public static SocialdataMessageBean createAddConnectorMessageBean(ISocialConnector.SocialNetwork socialNetwork, String token, long validity) {
		SocialdataMessageBean messageBean = new SocialdataMessageBean();
		messageBean.setMethod(SocialDataMethod.ADD_CONNECTOR);
		messageBean.setSnName(SocialDataCommsUtils.socialNetwork(socialNetwork));
		messageBean.setToken(token);
		messageBean.setValidity(validity);
		return messageBean;
	}
}
