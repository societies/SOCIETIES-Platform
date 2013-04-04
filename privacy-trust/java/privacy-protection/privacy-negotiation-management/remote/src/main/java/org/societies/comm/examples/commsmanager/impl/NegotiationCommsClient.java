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
package org.societies.comm.examples.commsmanager.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreementEnvelope;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegAgentMethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationAgentBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationBeanResult;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
/**
 * Comms Client that initiates the remote communication
 *
 * @author aleckey
 *
 */
public class NegotiationCommsClient implements INegotiationAgent, ICommCallback{
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/negotiation", 
					  		"http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation",
					  		"org.societies.api.schema.servicelifecycle.model"));

	//PRIVATE VARIABLES
	private ICommManager commManager;
	private static Logger LOG = LoggerFactory.getLogger(NegotiationCommsClient.class);
	private IIdentityManager idMgr;
	private NegotiationBeanResult negBeanResult = null;
	
	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public NegotiationCommsClient() {	}

	public void InitService() {
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
	public void receiveResult(Stanza arg0, Object obj) {
		if (obj instanceof NegotiationBeanResult){
			negBeanResult = (NegotiationBeanResult) obj;	
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
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (negBeanResult == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.negBeanResult = null;
		return new AsyncResult<Boolean> (this.negBeanResult.isAcknowledgement());
		
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
		
		RequestPolicy policy = (RequestPolicy) Util.convertToObject(this.negBeanResult.getRequestPolicy(), this.getClass());
		this.negBeanResult = null;
		return new AsyncResult<RequestPolicy> (policy);
	}

	@Override
	public Future<IIdentity> getProviderIdentity() {
		IIdentity toIdentity = null;
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
		
		this.negBeanResult = null;
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
		ResponsePolicy resp = (ResponsePolicy) Util.convertToObject(this.negBeanResult.getRequestPolicy(), this.getClass());
		this.negBeanResult = null;
		return new AsyncResult<ResponsePolicy> (resp);		
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
	



	
	
	

	
}
