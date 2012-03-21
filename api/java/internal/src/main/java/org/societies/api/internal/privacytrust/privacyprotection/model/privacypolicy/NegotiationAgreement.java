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

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

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
public class NegotiationAgreement implements IAgreement, Serializable {

	private List<RequestItem> items;
	private ServiceResourceIdentifier serviceID;
	private IIdentity serviceDPI;
	private IIdentity userDPI;
	private IIdentity userPublicDPI;

	private NegotiationAgreement(){
		this.items = new ArrayList<RequestItem>();
	}

	public NegotiationAgreement(ResponsePolicy policy){
		this.serviceID = policy.getSubject().getServiceID();
		this.serviceDPI = policy.getSubject().getDPI();
		List<RequestItem> l = new ArrayList<RequestItem>();
		for (ResponseItem r : policy.getResponseItems()){
			l.add(r.getRequestItem());
		}
		this.items = java.util.Collections.unmodifiableList(l);
	}
	public NegotiationAgreement(List<RequestItem> items){
		this.items = java.util.Collections.unmodifiableList(items);
	}
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getServiceIdentifier()
	 */
	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {
		
		return this.serviceID;
	}
	
	public void setRequestItems(List<RequestItem> items){
		this.items = java.util.Collections.unmodifiableList(items);
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getServiceDPI()
	 */
	@Override
	public IIdentity getServiceDPI() {
		return this.serviceDPI;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getUserDPI()
	 */
	@Override
	public IIdentity getUserDPI() {
		return this.userDPI;
	}

	public IIdentity getUserPublicDPI(){
		return this.userPublicDPI;
	}

	public void setUserPublicDPI(IIdentity userPublicDPI){
		this.userPublicDPI = userPublicDPI;
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#setServiceDPI(org.personalsmartspace.sre.api.pss3p.Identity)
	 */
	@Override
	public void setServiceDPI(IIdentity serviceDPI) {
		this.serviceDPI = serviceDPI;
		
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#setServiceIdentifier(org.personalsmartspace.sre.api.pss3p.ServiceResourceIdentifier)
	 */
	@Override
	public void setServiceIdentifier(ServiceResourceIdentifier serviceId) {
		this.serviceID = serviceId;
		
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#setUserDPI(org.personalsmartspace.sre.api.pss3p.Identity)
	 */
	@Override
	public void setUserDPI(IIdentity userDPI) {
		this.userDPI = userDPI;
		
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getRequestedItems()
	 */
	@Override
	public List<RequestItem> getRequestedItems() {
		return this.items;
	}

}

