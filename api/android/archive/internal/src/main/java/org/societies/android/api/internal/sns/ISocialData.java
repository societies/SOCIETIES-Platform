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

import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;

/**
 * Android interface for access SocialData bundle.
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public interface ISocialData {
	String methodsArray [] = {"addSocialConnector(SocialNetwork socialNetwork, String token, long validity)",
							  "removeSocialConnector(String client, String connectorId)",
							  "getSocialConnectors(String client)"};
	
	public static final String ADD_SOCIAL_CONNECTOR = "org.societies.android.platform.sns.ADD_SOCIAL_CONNECTOR";
	public static final String REMOVE_SOCIAL_CONNECTOR = "org.societies.android.platform.sns.REMOVE_SOCIAL_CONNECTOR";
	public static final String GET_SOCIAL_CONNECTORS = "org.societies.android.platform.sns.GET_SOCIAL_CONNECTORS";
	public static final String INTENT_RETURN_KEY = "org.societies.android.platform.sns.ReturnValue";
	public static final String ACTION_XMPP_ERROR = "org.societies.android.platform.sns.action.XMPP_ERROR";
	public static final String EXTRA_STANZA_ERROR = "org.societies.android.platform.sns.extra.STANZA_ERROR";
		
	/**
	 * Create and add a new social connector.
	 * A broadcast intent is sent with the Action ADD_SOCIAL_CONNECTOR 
	 * and the Extra INTENT_RETURN_KEY with the ID of the added connector.
	 * @param client Package name of the application that will receive the intent with the asynchronous return value.
	 * @param socialNetwork Social network to add.
	 * @param token Token for the connector.
	 * @param validity Validity of the connector.
	 */
	void addSocialConnector(String client, SocialNetwork socialNetwork, String token, long validity);
	
	/**
	 * Remove the social connector. 
	 * @param client Package name of the application that will receive the intent with the asynchronous return value.
	 * @param connectorId ID of the connector to remove.
	 */
	void removeSocialConnector(String client, String connectorId);
	
	/**
	 * Get a list of existing social connectors.
	 * A broadcast intent is sent with the Action GET_SOCIAL_CONNECTORS 
	 * and the Extra INTENT_RETURN_KEY with an Array of AConnectorBean.
	 * @param client Package name of the application that will receive the intent with the asynchronous return value.
	 */
	void getSocialConnectors(String client);
}
