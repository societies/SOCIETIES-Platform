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
package org.societies.privacytrust.remote.privacynegotiationmanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationStatus;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.internal.privacytrust.privacyprotection.remote.INegotiationAgentRemote;
import org.societies.api.internal.privacytrust.privacyprotection.util.remote.Util;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegAgentMethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationACKBeanResult;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationAgentBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationGetPolicyBeanResult;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationMainBeanResult;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.springframework.scheduling.annotation.AsyncResult;
/**
 * Comms Client that initiates the remote communication
 *
 * @author aleckey
 *
 */
public class PrivacyNegotiationManagerCommClient implements INegotiationAgentRemote, ICommCallback{
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/negotiation", 
					"http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation",
					"org.societies.api.schema.servicelifecycle.model"));

	//PRIVATE VARIABLES
	private ICommManager commManager;
	private Logger logging = LoggerFactory.getLogger(PrivacyNegotiationManagerCommClient.class);
	private IIdentityManager idMgr;
	

	private Hashtable<String, NegotiationACKBeanResult> ackResults = new Hashtable<String, NegotiationACKBeanResult>();
	private Hashtable<String, NegotiationGetPolicyBeanResult> policyResults = new Hashtable<String, NegotiationGetPolicyBeanResult>();
	private Hashtable<String, NegotiationMainBeanResult> mainResults = new Hashtable<String, NegotiationMainBeanResult>();
	

	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public PrivacyNegotiationManagerCommClient() {	}

	public void initBean() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idMgr = commManager.getIdManager();
	}



	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages() */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces() */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) { }

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) { }

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) { 

	}

	@Override
	public void receiveResult(Stanza stanza, Object bean) {
		this.logging.debug("Received resultBean");
		if (bean instanceof NegotiationACKBeanResult){
			this.logging.debug("Received : NegotiationACKBeanResult");
			String id = getId(NegAgentMethodType.ACKNOWLEDGE_AGREEMENT, ((NegotiationACKBeanResult) bean).getRequestor().getRequestorId());
			this.ackResults.put(id, (NegotiationACKBeanResult) bean);
			this.ackResults.notifyAll();
		}else if (bean instanceof NegotiationGetPolicyBeanResult){
			this.logging.debug("Received : NegotiationGetPolicyBeanResult");
			String id = getId(NegAgentMethodType.GET_POLICY, ((NegotiationACKBeanResult) bean).getRequestor().getRequestorId());
			this.policyResults.put(id, (NegotiationGetPolicyBeanResult) bean);
			this.policyResults.notifyAll();
		}else if (bean instanceof NegotiationMainBeanResult){
			this.logging.debug("Received : NegotiationMainBeanResult");
			String id = getId(NegAgentMethodType.NEGOTIATE, ((NegotiationACKBeanResult) bean).getRequestor().getRequestorId());
			this.mainResults.put(id, (NegotiationMainBeanResult) bean);
			this.mainResults.notifyAll();
		}else{
			this.logging.debug("Received unknown bean");
			this.logging.debug("Bean is of type: "+bean.getClass().getName());
			
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
	}


	/**
	 * INegotiationAgent remote interface
	 */

	@Override
	public Future<Boolean> acknowledgeAgreement(IAgreementEnvelope envelope) {
		IIdentity toIdentity = envelope.getAgreement().getRequestor().getRequestorId();
		/*		try {
			toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}*/
		Stanza stanza = new Stanza(toIdentity);
		NegotiationAgentBean bean = new NegotiationAgentBean();
		bean.setAgreementEnvelope(Util.toByteArray(envelope));
		bean.setMethod(NegAgentMethodType.ACKNOWLEDGE_AGREEMENT);
		try {
			this.commManager.sendIQGet(stanza, bean, this);
			this.logging.debug("Sending "+NegAgentMethodType.ACKNOWLEDGE_AGREEMENT+" IQGET");
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String id = getId(NegAgentMethodType.ACKNOWLEDGE_AGREEMENT, envelope.getAgreement().getRequestor().getRequestorId().getJid());

		while (!this.ackResults.containsKey(id)){
			try {
				this.logging.debug("waiting for results");
				synchronized(this.ackResults){

					this.ackResults.wait();
				} 
				this.logging.debug("Returning acknowledgement result ");
				NegotiationACKBeanResult resultBean = this.ackResults.get(id);
				this.ackResults.remove(id);
				return new AsyncResult<Boolean>(resultBean.isAcknowledgement());
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new AsyncResult<Boolean>(false);

	}

	@Override
	public Future<RequestPolicy> getPolicy(Requestor requestor) {
		/*		IIdentity toIdentity = null;
		try {
			toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);*/
		Stanza stanza = new Stanza(requestor.getRequestorId());
		NegotiationAgentBean bean = new NegotiationAgentBean();
		bean.setRequestor(createRequestorBean(requestor));
		bean.setMethod(NegAgentMethodType.GET_POLICY);

		try{
			this.commManager.sendIQGet(stanza, bean, this);
			this.logging.debug("Sending "+NegAgentMethodType.GET_POLICY+" IQGET");
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String id = getId(NegAgentMethodType.GET_POLICY, requestor.getRequestorId().getJid());
		
		while(!this.policyResults.containsKey(id)){
			try {
				synchronized (this.policyResults) {
					this.policyResults.wait();
					
				}
				this.logging.debug("Returning getPolicy result");
				NegotiationGetPolicyBeanResult result = policyResults.get(id);
				RequestPolicy policy = (RequestPolicy) Util.convertToObject(result.getRequestPolicy(), this.getClass());
				this.policyResults.remove(id);
				return new AsyncResult<RequestPolicy>(policy);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		RequestPolicy policy = new RequestPolicy(new ArrayList<RequestItem>());
		policy.setRequestor(requestor);
		return new AsyncResult<RequestPolicy> (policy);
	}

	/**
	 * this method is not used 
	 */
	@Override
	public Future<IIdentity> getProviderIdentity() {
/*		IIdentity toIdentity = null;
		try {
			toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		NegotiationAgentBean bean = new NegotiationAgentBean();
		bean.setMethod(NegAgentMethodType.GET_PROVIDER_IDENTITY);
		try{
			this.commManager.sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (negBeanResult == null){
			try{
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String idstr = this.negBeanResult.getIdentity();
		IIdentity id;
		try {
			id = idMgr.fromJid(idstr);
			this.negBeanResult = null;
			return new AsyncResult<IIdentity> (id);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.negBeanResult = null;*/
		return null;

	}

	@Override
	public Future<ResponsePolicy> negotiate(Requestor requestor, ResponsePolicy policy) {
		/*		IIdentity toIdentity = null;
		try {

			toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);*/
		Stanza stanza = new Stanza(requestor.getRequestorId());
		NegotiationAgentBean bean = new NegotiationAgentBean();
		bean.setMethod(NegAgentMethodType.NEGOTIATE);
		bean.setRequestor(createRequestorBean(requestor));
		bean.setResponsePolicy(Util.toByteArray(policy));
		try{
			this.commManager.sendIQGet(stanza, bean, this);
			this.logging.debug("Sending "+NegAgentMethodType.NEGOTIATE+" IQGET");
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String id = getId(NegAgentMethodType.NEGOTIATE, requestor.getRequestorId().getJid());
		
		while(!this.mainResults.containsKey(id)){
			try {
				synchronized(this.mainResults){
					this.mainResults.wait();
				}
				this.logging.debug("Returning negotiate result");
				NegotiationMainBeanResult result = this.mainResults.get(id);
				
				ResponsePolicy resp = (ResponsePolicy) Util.convertToObject(result.getResponsePolicy(), this.getClass());
				this.mainResults.remove(id);
				return new AsyncResult<ResponsePolicy>(resp);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		ResponsePolicy emptyPolicy = new ResponsePolicy(requestor, new ArrayList<ResponseItem>(), NegotiationStatus.FAILED);
		return new AsyncResult<ResponsePolicy> (emptyPolicy);		
	}

	private RequestorBean createRequestorBean(Requestor requestor){
		if (requestor instanceof RequestorCis){
			RequestorCisBean cisRequestorBean = new RequestorCisBean();
			cisRequestorBean.setRequestorId(requestor.getRequestorId().getBareJid());
			cisRequestorBean.setCisRequestorId(((RequestorCis) requestor).getCisRequestorId().getBareJid());
			return cisRequestorBean;
		}else if (requestor instanceof RequestorService){
			RequestorServiceBean serviceRequestorBean = new RequestorServiceBean();
			serviceRequestorBean.setRequestorId(requestor.getRequestorId().getBareJid());
			serviceRequestorBean.setRequestorServiceId(((RequestorService) requestor).getRequestorServiceId());
			return serviceRequestorBean;
		}else{
			RequestorBean requestorBean = new RequestorBean();
			requestorBean.setRequestorId(requestor.getRequestorId().getBareJid());
			return requestorBean;
		}
	}


	private static String getId(NegAgentMethodType methodType, String requestorJID){
		return methodType+":"+requestorJID;
	}








}
