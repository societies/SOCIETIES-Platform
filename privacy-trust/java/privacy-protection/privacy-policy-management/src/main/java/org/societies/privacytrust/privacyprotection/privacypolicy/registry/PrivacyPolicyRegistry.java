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
package org.societies.privacytrust.privacyprotection.privacypolicy.registry;

import java.io.Serializable;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.RequestorBean;

/**
 * Class that keeps a registry of all the privacy policies of the services this PSS provides to other PSSs
 * 
 * @author Elizabeth
 *
 */
public class PrivacyPolicyRegistry implements Serializable{
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPolicyRegistry.class.getName());

	private static final long serialVersionUID = -7184601590881429985L;
	private Hashtable<String, CtxIdentifier> policies;


	public PrivacyPolicyRegistry(){
		this.policies = new Hashtable<String, CtxIdentifier>();		
	}


	/**
	 * method to retrieve a policy document from the registry
	 * @param requestor		the serviceID of the service or Identity of CIS for which the policy is for
	 * @return				the policy document
	 */
	public CtxIdentifier getPolicyStorageID(RequestorBean requestor){
		if (requestor==null){
			return null;
		}
		if (this.policies.containsKey(RequestorUtils.toUriString(requestor))) {
			return this.policies.get(RequestorUtils.toUriString(requestor));
		}
		return null;
	}

	/**
	 * method to add a service policy document to the registry object
	 * @param requestor	the serviceID of the service or the IIdentity of the CIS for which this policy is for
	 * @param policy	the policy document 
	 */
	public void addPolicy (RequestorBean requestor, CtxIdentifier ctxID){
		if (this.policies == null){
			this.policies = new Hashtable<String, CtxIdentifier>();	
		}
		this.policies.put(RequestorUtils.toUriString(requestor), ctxID);
	}

	/**
	 * method to check if any policies exist in the registry
	 * @return
	 */
	public boolean isEmpty(){
		return this.policies.isEmpty();
	}

	public void replaceServiceIdentifier(RequestorBean oldRequestor, RequestorBean newRequestor){
		CtxIdentifier ctxID = this.getPolicyStorageID(oldRequestor);
		if (ctxID==null){
			return;
		}
		this.policies.remove(RequestorUtils.toUriString(oldRequestor));
		this.policies.put(RequestorUtils.toUriString(newRequestor), ctxID);
	}

	public void removePolicy(RequestorBean requestor){
		if (this.policies.containsKey(RequestorUtils.toUriString(requestor))){
			this.policies.remove(RequestorUtils.toUriString(requestor));
		}
	}

	/**
	 * @return the policies
	 */
	public Hashtable<String, CtxIdentifier> getPolicies() {
		return policies;
	}

	/**
	 * @param policies the policies to set
	 */
	public void setPolicies(Hashtable<String, CtxIdentifier> policies) {
		this.policies = policies;
	}
}
