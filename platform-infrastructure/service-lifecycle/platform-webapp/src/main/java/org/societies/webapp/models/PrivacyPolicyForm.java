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
package org.societies.webapp.models;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.SimpleDataIdentifier;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.DataTypeFactory;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.springframework.util.AutoPopulatingList;

/**
 * Describe your class here...
 *
 * @author olivierm
 *
 */
public class PrivacyPolicyForm {
	private String cssOwnerId;
	private String cisId;
	private AutoPopulatingList<PrivacyPolicyResourceForm> resources;
	
	public PrivacyPolicyForm() {
		resources = new AutoPopulatingList<PrivacyPolicyResourceForm>(PrivacyPolicyResourceForm.class);
	}
	
	public void createEmptyPrivacyPolicyFrom() {
		resources = new AutoPopulatingList<PrivacyPolicyResourceForm>(PrivacyPolicyResourceForm.class);
		
		PrivacyPolicyResourceForm resource = new PrivacyPolicyResourceForm();
		
		AutoPopulatingList<PrivacyActionForm> actions = new AutoPopulatingList<PrivacyActionForm>(PrivacyActionForm.class);
		actions.add(new PrivacyActionForm());
		resource.setActions(actions);
		
		resources.add(resource);
	}
	/**
	 * @return the cssOwnerId
	 */
	public String getCssOwnerId() {
		return cssOwnerId;
	}
	/**
	 * @param cssOwnerId the cssOwnerId to set
	 */
	public void setCssOwnerId(String cssOwnerId) {
		this.cssOwnerId = cssOwnerId;
	}
	/**
	 * @return the cisId
	 */
	public String getCisId() {
		return cisId;
	}
	/**
	 * @param cisId the cisId to set
	 */
	public void setCisId(String cisId) {
		this.cisId = cisId;
	}
	/**
	 * @return the resource
	 */
	public AutoPopulatingList<PrivacyPolicyResourceForm> getResources() {
		return resources;
	}
	/**
	 * @param resource the resource to set
	 */
	public void setResources(AutoPopulatingList<PrivacyPolicyResourceForm> resources) {
		this.resources = resources;
	}
	public void addResource(PrivacyPolicyResourceForm resource) {
		if (null == resources) {
			resources = new AutoPopulatingList<PrivacyPolicyResourceForm>(PrivacyPolicyResourceForm.class);
		}
		this.resources.add(resource);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("PrivacyPolicyForm ["
				+ (cssOwnerId != null ? "cssOwnerId=" + cssOwnerId + ", " : "")
				+ (cisId != null ? "cisId=" + cisId + ", " : ""));
		int i = 0;
		if (null != resources && resources.size() > 0) {
			str.append(", resources=\n");
			for(PrivacyPolicyResourceForm resource : resources) {
				str.append("* Resource "+(i++)+": "+resource.toString()+"\n");
			}
		}
		str.append("]");
		return str.toString();
	}

	/**
	 * @return
	 * @throws InvalidFormatException 
	 * @throws MalformedCtxIdentifierException 
	 */
	public RequestPolicy toRequestPolicy(IIdentityManager idManager) throws InvalidFormatException, MalformedCtxIdentifierException {
		clean();
		
		// -- Requestor
		RequestorCis requestor = null;
		if (null != cssOwnerId && !"".equals(cssOwnerId) && null != cisId && !"".equals(cisId)) {
			requestor = new RequestorCis(idManager.fromJid(cssOwnerId), idManager.fromJid(cisId));
		}
		// -- Resources
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		for(PrivacyPolicyResourceForm resourceForm : resources) {
			// Data Id
			String type = resourceForm.getResourceType();
			DataIdentifierScheme scheme;
			if (null == resourceForm.getResourceType() || "".equals(resourceForm.getResourceType()) || "NONE".equals(resourceForm.getResourceType())) {
				try {
					scheme = DataIdentifierScheme.fromValue(resourceForm.getResourceSchemeCustom());
				}
				catch (IllegalArgumentException e) {
					scheme = DataIdentifierScheme.valueOf(resourceForm.getResourceSchemeCustom());
				}
				type = resourceForm.getResourceTypeCustom();
			}
			else {
				DataIdentifier dataType = DataTypeFactory.fromUri(type);
				type = dataType.getType();
				scheme = dataType.getScheme();
			}
			Resource resource = new Resource(scheme, type);
			// Actions
			List<Action> actions = new ArrayList<Action>();
			for(PrivacyActionForm actionForm : resourceForm.getActions()) {
				if (null != actionForm && null != actionForm.getAction()) {
					actions.add(new Action(actionForm.getAction(), actionForm.isOptional()));
				}
			}
			// Conditions
			List<Condition> conditions = new ArrayList<Condition>();
			for(PrivacyConditionForm conditionForm : resourceForm.getConditions()) {
				if (null != conditionForm && null != conditionForm.getConditionName()) {
					conditions.add(new Condition(conditionForm.getConditionName(), conditionForm.getValue(), conditionForm.isOptional()));
				}
			}
			requestItems.add(new RequestItem(resource, actions, conditions));
		}
		RequestPolicy privacyPolicy = new RequestPolicy(requestItems);
		privacyPolicy.setRequestor(requestor);
		return privacyPolicy;
	}
	
	public void clean() {
		AutoPopulatingList<PrivacyPolicyResourceForm> resourcesTmp = new AutoPopulatingList<PrivacyPolicyResourceForm>(PrivacyPolicyResourceForm.class);
		for(PrivacyPolicyResourceForm resourceForm : resources) {
			if (!resourceForm.isEmpty()) {
				resourcesTmp.add(resourceForm);
			}
		}
		resources = resourcesTmp;
	}


	
	
	
}
