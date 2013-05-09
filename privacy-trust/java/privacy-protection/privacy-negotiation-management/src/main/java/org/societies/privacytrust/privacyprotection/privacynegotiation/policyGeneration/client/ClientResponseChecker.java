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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.DecisionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponsePolicyUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;



/**
 * @author Elizabeth
 * @author Olivier Maridat (Trialog)
 *
 */
public class ClientResponseChecker {
	private static Logger LOG = LoggerFactory.getLogger(ClientResponseChecker.class);

	public ClientResponseChecker(){

	}

	/**
	 * Check that the provided ResponsePolicy (inferred by privacy preferences, and approved by the user)
	 * match the existing ResponsePolicy based on the original RequestPolicy
	 * All mandatory fields should at least be in the provider ResponsePolicy
	 * And all mandatory actions and conditions should be in provided RequestItem
	 * @param myPolicy
	 * @param providerPolicy
	 * @return True if provided and requested ResponsePolicy match
	 */
	public boolean checkResponse(ResponsePolicy myPolicy, ResponsePolicy providerPolicy){
		/* 2013-04-18 - fixed by Olivier:
		 * Fix: when providerPolicy was empty or not complete, the reply was true.
		 * Added: We need to check that every mandatory fields at least are in the providerPolicy.
		 */

		// -- Empty requested ResponsePolicy
		if (null == myPolicy || null == myPolicy.getResponseItems() || myPolicy.getResponseItems().size() <= 0) {
			LOG.info("Empty requested policy");
			return true;
		}
		// -- Empty provider ResponsePolicy
		if ((null == providerPolicy || null == providerPolicy.getResponseItems() || providerPolicy.getResponseItems().size() <= 0)
				&& !ResponsePolicyUtils.hasOptionalResponseItemsOnly(myPolicy)) {
			LOG.info("Empty provided policy and requested policy not completely optional");
			return false;
		}

		// -- Check every ResponseItems		
		for (ResponseItem myItem : myPolicy.getResponseItems()){
			LOG.info("Requested item \""+myItem.getRequestItem().getResource().getDataType()+"\"...");
			ResponseItem providerItem = ResponseItemUtils.containSameResource(myItem, providerPolicy.getResponseItems());
			// - Resource not accepted (because not even their)
			if (null == providerItem) {
				// But it was optional
				if (myItem.getRequestItem().isOptional()) {
					continue;
				}
				LOG.info("Mandatory requested item \""+myItem.getRequestItem().getResource().getDataType()+"\" not provided");
				return false;
			}
			// - Resource requested
			else {
				// Requested item is not matching
				if (!checkResponseItem(myItem, providerItem)) {
					LOG.info("Provided item \""+providerItem.getRequestItem().getResource().getDataType()+"\" doesn't match the request item");
					return false;
				}
				// Requested item is matching
				else {
					LOG.info("Provided item \""+providerItem.getRequestItem().getResource().getDataType()+"\" matches the request item");
					// TODO Question for Eliza: the code below is not used. Is it normal? If you need to change the provided ResponseItemList, you should return it, it will be easier in my (humble) opinion.
//					providerItem = new ResponseItem();
//					providerItem.setDecision(Decision.PERMIT);
//					RequestItem requestItem = providerItem.getRequestItem();
//					providerItem.setRequestItem(requestItem);
				}
			}
		}
		return true;
	}

	/**
	 * Decision is equal, or requested item is optional
	 * All mandatory actions are set
	 * All mandatory conditions are set
	 * @param myItem
	 * @param providerItem
	 * @return
	 */
	private boolean checkResponseItem(ResponseItem myItem, ResponseItem providerItem){
		RequestItem myRequestItem = myItem.getRequestItem();
		RequestItem providerRequestItem = providerItem.getRequestItem();

		// -- Check Decision
		// PERMIT decision OR (not PERMIT decision but optional field or underterminate requested decision
		if (!DecisionUtils.equal(Decision.PERMIT, providerItem.getDecision())
				&& !myItem.getRequestItem().isOptional()
				&& !DecisionUtils.equal(Decision.INDETERMINATE, myItem.getDecision())){
			LOG.info("Mandatory requested item is provided as DENY");
			return false;
		}

		// -- Check Actions
		// All mandatory requested actions are available in the provided action list
		if (!ActionUtils.containAllMandotory(providerRequestItem.getActions(), myRequestItem.getActions())) {
			LOG.info("At least one mandatory requested actions is not available in the provided action list");
			return false;
		}

		// -- Check Conditions
		// All mandatory requested conditions are available in the provided condition list
		if (!ConditionUtils.containAllMandotory(providerRequestItem.getConditions(), myRequestItem.getConditions())) {
			LOG.info("At least one mandatory requested conditions is not available in the provided condition list");
			return false;
		}
		return true;
	}

	@Deprecated
	private ResponseItem containsItem(ResponseItem item, List<ResponseItem> list){
		for (ResponseItem r : list){
			if (item.getRequestItem().getResource().getDataType().equals(r.getRequestItem().getResource().getDataType())){
				return r;
			}
		}

		return null;
	}

	@Deprecated
	private boolean containsAction(Action action, List<Action> actions){
		for (Action a : actions){
			if (a.getActionConstant().equals(a.getActionConstant())){
				return true;

			}
		}

		return false;
	}

	@Deprecated
	private boolean containsCondition(Condition condition, List<Condition> conditions){
		for (Condition con : conditions){
			if (con.getConditionConstant().equals(condition.getConditionConstant())){
				if (con.getValue().equalsIgnoreCase(condition.getValue())){
					return true;
				}
			}
		}

		return false;
	}
}
