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
package org.societies.security.policynegotiator.requester;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.security.policynegotiator.MethodType;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;

/**
 * Describe your class here...
 *
 * @author mitjav
 *
 */
public class UserFeedbackSelectSopCallback implements
		IUserFeedbackResponseEventListener<List<String>> {

	private static Logger log = LoggerFactory.getLogger(UserFeedbackSelectSopCallback.class);

	NegotiationRequester requester;
	Requestor provider;
	boolean includePrivacyPolicyNegotiation;
	String sop;
	INegotiationCallback finalCallback;
	int sessionId;
	
	public UserFeedbackSelectSopCallback(
			NegotiationRequester requester,
			Requestor provider,
			boolean includePrivacyPolicyNegotiation,
			String sop,
			INegotiationCallback finalCallback,
			int sessionId
			) {
		
		this.requester = requester;
		this.provider = provider;
		this.includePrivacyPolicyNegotiation = includePrivacyPolicyNegotiation;
		this.sop = sop;
		this.finalCallback = finalCallback;
		this.sessionId = sessionId;

	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener#responseReceived(java.lang.Object)
	 */
	@Override
	public void responseReceived(List<String> result) {
		
		log.debug("responseReceived({})", result);
		log.info("selectSopOption: user selected \"{}\"", result.get(0));
		try {
			String selectedSop = result.get(0);
			IIdentity identity = requester.getGroupMgr().getIdMgr().getThisNetworkNode();
			sop = requester.getSignatureMgr().signXml(sop, selectedSop, identity);
			ProviderCallback callback = new ProviderCallback(requester, provider,
					MethodType.ACCEPT_POLICY_AND_GET_SLA, includePrivacyPolicyNegotiation,
					finalCallback); 
			requester.getGroupMgr().acceptPolicyAndGetSla(
					sessionId,
					sop,
					false,
					provider.getRequestorId(),
					callback);
		} catch (Exception e) {
			log.warn("receiveResult(): session {}: ", sessionId, e);
		}
	}

}
