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
package org.societies.api.privacytrust.privacy.model.privacypolicy;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;

/**
 * The RequestItem class is used to represent a request to access a specific piece of personal data. 
 * It is embedded inside a RequestPolicy which is the privacy policy of a service provider. 
 * The RequestItem contains a Resource object, a list of Action objects, a list of Conditions (obligations) 
 * and a flag that declares the request as optional. 
 * @author Elizabeth
 *
 */
@Deprecated
public class RequestItem implements Serializable{

	private Resource resource;
	private List<Action> actions;
	private List<Condition> conditions;
	private boolean optional;

	private RequestItem(){
		this.actions = new ArrayList<Action>();
		this.conditions = new ArrayList<Condition>();
		this.optional = false;
	}
	public RequestItem(Resource r, List<Action> actions, List<Condition> conditions){
		this.resource = r;
		this.actions = actions;
		this.conditions = conditions;
		this.optional = false;
		if (null==conditions){
			throw new NullPointerException("List<Condition> condition parameter cannot be null. Use empty list instead");
		}
		
		if (null==actions){
			throw new NullPointerException("List<Action> action parameter cannot be null. Use empty list instead");
		}
	}

	public RequestItem(Resource r, List<Action> actions, List<Condition> conditions, boolean isOptional){
		this.resource = r;
		this.actions = actions;
		this.conditions = conditions;
		this.optional = isOptional;
	}

	public Resource getResource(){
		return this.resource;
	}

	public List<Action> getActions(){
		return this.actions;
	}

	public List<Condition> getConditions(){
		return this.conditions;
	}

	public void setConditions(List<Condition> conditions){
		this.conditions = conditions;
	}

	public void setActions(List<Action> actions){
		this.actions = actions;
	}
	
	public String getStatus() {
		for(Condition condition : conditions) {
			if (condition.getConditionName().equals(ConditionConstants.SHARE_WITH_3RD_PARTIES)) {
				return "Public";
			}
			if (condition.getConditionName().equals(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY)) {
				return "Members only";
			}
		}
		return "Private";
	}
	
	public String getInferenceStatus() {
		for(Condition condition : conditions) {
			if (condition.getConditionName().equals(ConditionConstants.MAY_BE_INFERRED)) {
				return "May be inferred";
			}
		}
		return null;
	}
	
	public String toXMLString(){
		StringBuilder str = new StringBuilder("\n<Target>");
		str.append(this.resource.toXMLString());
		for (Action action : actions){
			str.append(action.toXMLString());
		}
		for (Condition con : conditions){
			str.append(con.toXMLString());
		}
		str.append(this.printOptional());
		str.append("\n</Target>");
		return str.toString();
	}

	public boolean isOptional(){
		return this.optional;
	}

	public void setOptional(boolean isOptional){
		this.optional = isOptional;
	}
	private String printOptional(){
		return "\n<optional>"+this.optional+"</optional>";
	}
	public String toString(){
		return this.toXMLString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
		result = prime * result
				+ ((conditions == null) ? 0 : conditions.hashCode());
		result = prime * result + (optional ? 1231 : 1237);
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// -- Verify reference equality
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		// -- Verify obj type
		RequestItem rhs = (RequestItem) obj;
		return new EqualsBuilder()
			.append(this.getActions(), rhs.getActions())
			.append(this.getConditions(), rhs.getConditions())
			.append(this.getResource(), rhs.getResource())
			.append(this.isOptional(), rhs.isOptional())
			.isEquals();
	}

}
