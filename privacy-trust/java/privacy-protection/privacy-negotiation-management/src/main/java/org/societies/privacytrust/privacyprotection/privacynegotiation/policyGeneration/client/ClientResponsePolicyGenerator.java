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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;

/**
 * @author Eliza
 *
 */
public class ClientResponsePolicyGenerator {

	private IUserFeedback userFeedback;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	public ClientResponsePolicyGenerator(PrivacyPolicyNegotiationManager policyMgr){
		userFeedback = policyMgr.getUserFeedback();
		privacyPreferenceManager = policyMgr.getPrivacyPreferenceManager();
	}
	public ResponsePolicy generatePolicy(NegotiationDetails details, RequestPolicy providerPolicy){
		List<RequestItem> requestItems = providerPolicy.getRequestItems();
		HashMap<RequestItem, ResponseItem> evaluatePPNPreferences = this.privacyPreferenceManager.evaluatePPNPreferences(providerPolicy);
		
		ResponsePolicy responsePolicy = new ResponsePolicy();
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		responseItems.addAll(evaluatePPNPreferences.values());
		
		responsePolicy.setResponseItems(responseItems);
		responsePolicy.setRequestor(providerPolicy.getRequestor());
		responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
		
		NegotiationDetailsBean negDetailsBean = new NegotiationDetailsBean();
		negDetailsBean.setNegotiationID(details.getNegotiationID());
		negDetailsBean.setRequestor(providerPolicy.getRequestor());
		try {
			ResponsePolicy privacyNegotiationFB = userFeedback.getPrivacyNegotiationFB(responsePolicy, negDetailsBean).get();
			privacyNegotiationFB.setRequestor(providerPolicy.getRequestor());
			privacyNegotiationFB.setNegotiationStatus(NegotiationStatus.ONGOING);
			
			return privacyNegotiationFB;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResponsePolicy emptyPolicy = new ResponsePolicy();
		emptyPolicy.setRequestor(providerPolicy.getRequestor());
		emptyPolicy.setNegotiationStatus(NegotiationStatus.FAILED);
		return emptyPolicy;
		
	}
	
	
}
