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
import java.util.Arrays;
import java.util.List;

import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.springframework.util.AutoPopulatingList;

/**
 * Describe your class here...
 *
 * @author olivierm
 *
 */
public class PrivacyPolicyResourceForm {
	private String resourceType;
	private String resourceSchemeCustom;
	private String resourceTypeCustom;
	private List<PrivacyActionForm> actions;
	private List<PrivacyConditionForm> conditions;
	private boolean optional;

	public PrivacyPolicyResourceForm() {
		actions = new ArrayList<PrivacyActionForm>();
		conditions = new ArrayList<PrivacyConditionForm>();
		optional = false;
	}
	public boolean isOptional() {
		return optional;
	}
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	/**
	 * @return the resourceId
	 */
	public String getResourceType() {
		return resourceType;
	}
	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	/**
	 * @return the resourceTypeCustom
	 */
	public String getResourceTypeCustom() {
		return resourceTypeCustom;
	}
	/**
	 * @param resourceTypeCustom the resourceTypeCustom to set
	 */
	public void setResourceTypeCustom(String resourceTypeCustom) {
		this.resourceTypeCustom = resourceTypeCustom;
	}
	/**
	 * @return the resourceSchemeCustom
	 */
	public String getResourceSchemeCustom() {
		return resourceSchemeCustom;
	}
	/**
	 * @param resourceSchemeCustom the resourceSchemeCustom to set
	 */
	public void setResourceSchemeCustom(String resourceSchemeCustom) {
		this.resourceSchemeCustom = resourceSchemeCustom;
	}
	/**
	 * @return the conditions
	 */
	public List<PrivacyConditionForm> getConditions() {
		return conditions;
	}
	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(List<PrivacyConditionForm> conditions) {
		this.conditions = conditions;
	}
	/**
	 * @param conditions the conditions to set
	 */
	public void addCondition(PrivacyConditionForm condition) {
		if (null == conditions) {
			conditions = new ArrayList<PrivacyConditionForm>();
		}
		this.conditions.add(condition);
	}
	
	/**
	 * @return the actions
	 */
	public List<PrivacyActionForm> getActions() {
		return actions;
	}
	/**
	 * @param actions the actions to set
	 */
	public void setActions(List<PrivacyActionForm> actions) {
		this.actions = actions;
	}
	public void addAction(PrivacyActionForm action) {
		if (null == actions) {
			actions = new ArrayList<PrivacyActionForm>();
		}
		this.actions.add(action);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("PrivacyPolicyResourceForm ["
				+ (resourceType != null ? "resourceType=" + resourceType + ", " : "")
				+ "optional :"+(optional ? "yes" : "no")+", ");
		int j = 0;
		if (null != actions && actions.size() > 0) {
			str.append(", actions=\n");
			for(Action action : actions) {
				str.append("** Action "+(j++)+": "+action.getActionType()+(action.isOptional() ? " (optional)" : "")+"\n");
			}
		}
		int i = 0;
		if (null != conditions && conditions.size() > 0) {
			str.append(", conditions=\n");
			for(Condition condition : conditions) {
				str.append("** Condition "+(i++)+": "+condition.getTheCondition()+"="+condition.getValue()+(condition.isOptional() ? " (optional)" : "")+"\n");
			}
		}
		str.append("]");
		return str.toString();
	}
	
	public boolean isEmpty() {
		return (null == resourceType
				&& (null == actions || actions.size() <= 0)
				&& (null == conditions || conditions.size() <= 0));
	}
	
	
}
