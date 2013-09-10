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
package org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.provider;

import java.security.Key;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementEnvelopeUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.provider.ProviderResponsePolicyGenerator;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Elizabeth
 *
 */
public class NegotiationAgent implements INegotiationAgent{

	private IIdentity myIdentity;
	private IPrivacyPolicyManager policyMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICommManager commsMgr;
	
	public NegotiationAgent(){
	}
	
	public void initialiseNegotiationAgent(){
		this.myIdentity = this.commsMgr.getIdManager().getThisNetworkNode();
	}
	
	public void setPublicIdentity(IIdentity identity){
		this.myIdentity = identity;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent#getPolicy(org.societies.api.identity.Requestor)
	 */
	@Override
	public Future<RequestPolicy> getPolicy(RequestorBean requestor) {
		this.log("Returning requested policy for : "+requestor.toString());
	
		RequestPolicy requestedPolicy;
		try {
			requestedPolicy = RequestPolicyUtils.toRequestPolicyBean(this.getPolicyMgr().getPrivacyPolicy(RequestorUtils.toRequestor(requestor, this.commsMgr.getIdManager())));
			if (requestedPolicy==null){
				log("RequestPolicy is NULL");
			}else{
				log("FOUND non-null request policy and returning to requestor");
			}
			return new AsyncResult<RequestPolicy>(requestedPolicy);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<RequestPolicy>(null);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent#getProviderIdentity()
	 */
	@Override
	public Future<IIdentity> getProviderIdentity() {
		return new AsyncResult<IIdentity>(this.myIdentity);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent#negotiate(org.societies.api.identity.Requestor, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy)
	 */
	@Override
	public Future<ResponsePolicy> negotiate(RequestorBean requestor, ResponsePolicy policy) {
		log("Received responsePolicy from client");
		log(policy.toString());
		try {
			RequestPolicy myPolicy = RequestPolicyUtils.toRequestPolicyBean(this.getPolicyMgr().getPrivacyPolicy(RequestorUtils.toRequestor(requestor, this.commsMgr.getIdManager())));
			if (myPolicy==null){
				log("Could not retrieve MY POLICY!");
			}
			ProviderResponsePolicyGenerator respPolGen = new ProviderResponsePolicyGenerator();
			ResponsePolicy myResponse = respPolGen.generateResponse(policy, myPolicy);
			return new AsyncResult<ResponsePolicy>(myResponse);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<ResponsePolicy>(null);
	}
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.negotiation.api.platform.INegotiationAgent#acknowledgeAgreement(org.personalsmartspace.spm.negotiation.api.platform.IAgreementEnvelope)
	 */
	@Override
	public Future<Boolean> acknowledgeAgreement(AgreementEnvelope envelope) {
		log("Client requests to acknowledge agreement");
		try{
			
			Key key = AgreementEnvelopeUtils.getPublicKey(envelope);
			
			log("Got Public Key from Agreement Envelope "+key.toString());
		}catch(Exception e){
			log("Could not retrieve Public Key from Agreement Envelope");
			e.printStackTrace();
		}

		
		return new AsyncResult<Boolean>(true);
		
	}
	
	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}

	/**
	 * @return the policyMgr
	 */
	public IPrivacyPolicyManager getPolicyMgr() {
		return policyMgr;
	}

	/**
	 * @param policyMgr the policyMgr to set
	 */
	public void setPolicyMgr(IPrivacyPolicyManager policyMgr) {
		this.policyMgr = policyMgr;
	}

	/**
	 * @return the commsMgr
	 */
	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	/**
	 * @param commsMgr the commsMgr to set
	 */
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
	}

}
