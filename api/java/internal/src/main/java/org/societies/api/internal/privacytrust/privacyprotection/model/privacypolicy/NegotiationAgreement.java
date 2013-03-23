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
package org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;


/**
 * The NegotiationAgreement class represents the agreement reached between the user and the service provider. 
 * It lists all the personal data that hte user has agreed to give the provider access to and what operations 
 * the provider is allowed to perform on these personal data. It also contains all the obligations the provider 
 * and the user have agreed for each item in the personal data such as the data retention period, sharing with 
 * third parties, the right to opt-out etc. The NegotiationAgreement document is drafted by the user after a 
 * successful negotiation and sent to the provider embedded in an AgreementEnvelope object.
 * @author Elizabeth
 *
 */
@Deprecated
public class NegotiationAgreement implements IAgreement, Serializable {

	private List<ResponseItem> items;
	private RequestorBean requestor;
	private IIdentity userId;
	private IIdentity userPublicId;

	private NegotiationAgreement(){
		this.items = new ArrayList<ResponseItem>();
	}

	public NegotiationAgreement(ResponsePolicy policy){
		this.requestor = policy.getRequestor();
		this.items = java.util.Collections.unmodifiableList(policy.getResponseItems());
	}
	public NegotiationAgreement(List<ResponseItem> items){
		this.items = java.util.Collections.unmodifiableList(items);
	}
	
	
	public void setResponseItems(List<ResponseItem> items){
		this.items = java.util.Collections.unmodifiableList(items);
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement#getUserIdentity()
	 */
	@Override
	public IIdentity getUserIdentity() {
		return this.userId;
	}

	@Override
	public IIdentity getUserPublicIdentity(){
		return this.userPublicId;
	}

	@Override
	public void setUserPublicIdentity(IIdentity userPublicId){
		this.userPublicId = userPublicId;
	}


	public void setRequestor(RequestorBean requestor){
		this.requestor = requestor;
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement#setUserDPI(org.societies.api.identity.IIdentity)
	 */
	@Override
	public void setUserIdentity(IIdentity userDPI) {
		this.userId = userDPI;
		
	}

	/* 
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement#getRequestedItems()
	 */
	@Override
	public List<ResponseItem> getRequestedItems() {
		return this.items;
	}

	@Override
	public RequestorBean getRequestor() {
		// TODO Auto-generated method stub
		return this.requestor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result
				+ ((requestor == null) ? 0 : requestor.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result
				+ ((userPublicId == null) ? 0 : userPublicId.hashCode());
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
		NegotiationAgreement rhs = (NegotiationAgreement) obj;
		return new EqualsBuilder()
			.append(this.getUserIdentity(), rhs.getUserIdentity())
			.append(this.getUserPublicIdentity(), rhs.getUserPublicIdentity())
			.isEquals()
			&& RequestorUtils.equal(this.getRequestor(), rhs.getRequestor())
			&& ResponseItemUtils.equal(this.getRequestedItems(), rhs.getRequestedItems());
	}


	public String toString() {
		StringBuilder sb = new StringBuilder("Agreement: [");
		sb.append("requestor: "+RequestorUtils.toString(getRequestor())+", ");
		sb.append("user id: "+getUserIdentity()+", ");
		sb.append("user public id: "+getUserPublicIdentity()+", ");
		sb.append("requested items: "+ResponseItemUtils.toString(getRequestedItems())+", ");
		return sb.toString();
	}

}

