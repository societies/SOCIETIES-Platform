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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.util.privacypolicy.NegotiationStatusUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponsePolicyUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;

/**
 * @author Eliza
 * @author Olivier Maridat (Trialog)
 *
 */
public class ClientResponsePolicyGenerator {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	private IUserFeedback userFeedback;
	private IPrivacyPreferenceManager privacyPreferenceManager;

	public ClientResponsePolicyGenerator(PrivacyPolicyNegotiationManager policyMgr){
		userFeedback = policyMgr.getUserFeedback();
		privacyPreferenceManager = policyMgr.getPrivacyPreferenceManager();
	}

	public ResponsePolicy generatePolicy(NegotiationDetails details, RequestPolicy providerPolicy){
		// Generate an empty failed response policy (may be useful)
		ResponsePolicy responsePolicyEmpty = new ResponsePolicy();
		responsePolicyEmpty.setRequestor(providerPolicy.getRequestor());
		responsePolicyEmpty.setNegotiationStatus(NegotiationStatus.FAILED);
		// Retrieve requested items
		List<RequestItem> requestItems = providerPolicy.getRequestItems();

		// -- Retrieve PPN preferences
		HashMap<RequestItem, ResponseItem> evaluatePPNPreferences = this.privacyPreferenceManager.evaluatePPNPreferences(providerPolicy);

		// -- ResponsePolicy corresponding to the requested privacy policy
		ResponsePolicy responsePolicyGenerated = new ResponsePolicy();
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		// - Loop over requested items
		for (RequestItem requestItem : requestItems){
			boolean found = false;
			for (RequestItem ppnPreferenceKey : evaluatePPNPreferences.keySet()){
				// This item is already in the privacy preferences: use the existing response item
				if (RequestItemUtils.equal(requestItem, ppnPreferenceKey)){
					responseItems.add(evaluatePPNPreferences.get(ppnPreferenceKey));
					found = true;
				}
			}
			// This item is not in the privacy preferences: create an empty response item for it, PERMIT by default to simplify the user's life
			if (!found){
				ResponseItem item = new ResponseItem();
				item.setRequestItem(requestItem);
				item.setDecision(Decision.PERMIT);
				responseItems.add(item);
			}
		}
		responsePolicyGenerated.setResponseItems(responseItems);
		responsePolicyGenerated.setRequestor(providerPolicy.getRequestor());
		responsePolicyGenerated.setNegotiationStatus(NegotiationStatus.ONGOING);
		LOG.debug("Generated response policy to be displayed to the user");
		LOG.debug(ResponsePolicyUtils.toString(responsePolicyGenerated));
		return responsePolicyGenerated;
	}

	public ResponsePolicy generatePolicyUserApproved(NegotiationDetails details, ResponsePolicy requestedResponsePolicy){
		// Generate an empty failed response policy ( may be needed)
		ResponsePolicy responsePolicyEmpty = new ResponsePolicy();
		responsePolicyEmpty.setRequestor(requestedResponsePolicy.getRequestor());
		responsePolicyEmpty.setNegotiationStatus(NegotiationStatus.FAILED);

		// -- Let the user change the selected response policy
		ResponsePolicy responsePolicyUserApproved = new ResponsePolicy();
		NegotiationDetailsBean negDetailsBean = new NegotiationDetailsBean();
		negDetailsBean.setNegotiationID(details.getNegotiationID());
		negDetailsBean.setRequestor(requestedResponsePolicy.getRequestor());
		try {
			responsePolicyUserApproved = userFeedback.getPrivacyNegotiationFB(requestedResponsePolicy, negDetailsBean).get();
			if (null == responsePolicyUserApproved){
				LOG.error("Result of userfeedback negotiation request is null");
				responsePolicyUserApproved = responsePolicyEmpty;
			}
			else if (NegotiationStatusUtils.equal(NegotiationStatus.FAILED, responsePolicyUserApproved.getNegotiationStatus())){
				LOG.error("Result of userfeedback negotiation request has a 'failed' status");
				responsePolicyUserApproved = responsePolicyEmpty;
				responsePolicyUserApproved.setResponseItems(new ArrayList<ResponseItem>());
			}
			else {
				LOG.debug("Result of userfeedback negotiation request is valid, let's check it with the requested policy");
				responsePolicyUserApproved.setNegotiationStatus(NegotiationStatus.ONGOING);
			}
			LOG.debug("Generated user response policy. ResponsePolicy contains: "+responsePolicyUserApproved.getResponseItems().size()+" responseItems");
			LOG.debug(ResponsePolicyUtils.toString(responsePolicyUserApproved));
			return responsePolicyUserApproved;
		} catch (InterruptedException e) {
			LOG.error("Negotiation failed due to interrupted exception", e);
			responsePolicyUserApproved = responsePolicyEmpty;
		} catch (ExecutionException e) {
			LOG.error("Negotiation failed due to execution exception", e);
			responsePolicyUserApproved = responsePolicyEmpty;
		}
		return responsePolicyUserApproved;
	}
}
