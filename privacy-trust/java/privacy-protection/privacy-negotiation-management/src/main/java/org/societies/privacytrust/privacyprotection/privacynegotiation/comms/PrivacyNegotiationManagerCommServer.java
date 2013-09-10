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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.privacytrust.privacyprotection.privacynegotiation.comms;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegAgentMethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationACKBeanResult;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationAgentBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationGetPolicyBeanResult;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationMainBeanResult;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;


public class PrivacyNegotiationManagerCommServer implements IFeatureServer{
	private static Logger LOG = LoggerFactory.getLogger(PrivacyNegotiationManagerCommServer.class);
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/negotiation",
					"http://societies.org/api/schema/privacytrust/privacy/model/privacypolicy",
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation",
					"org.societies.api.schema.privacytrust.privacy.model.privacypolicy",
					"org.societies.api.schema.identity",
					"org.societies.api.schema.servicelifecycle.model"));

	//PRIVATE VARIABLES
	private ICommManager commManager;
	private IIdentityManager idMgr;
	private INegotiationAgent negAgent;



	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public INegotiationAgent getNegAgent() {
		return negAgent;
	}

	public void setNegAgent(INegotiationAgent negAgent) {
		this.negAgent = negAgent;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		this.idMgr = this.commManager.getIdManager();
	}

	public void initBean(){
		try {
			this.commManager.register(this);
			this.LOG.debug("Registered "+this.getClass().toString()+" as FeatureServer");
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//METHODS
	public PrivacyNegotiationManagerCommServer() {
	}


	
	public Object getQuery(Stanza stanza, Object ibean){
		if (ibean instanceof NegotiationAgentBean){
			NegotiationAgentBean bean = (NegotiationAgentBean) ibean;
			this.LOG.debug("Received Query");
			if (bean.getMethod().equals(NegAgentMethodType.ACKNOWLEDGE_AGREEMENT)){

				AgreementEnvelope agreementEnvelope = bean.getAgreementEnvelope();



				//Object obj = Util.convertToObject(agreementEnvelopeArray, this.getClass());
				if (agreementEnvelope!=null){
					
						Boolean b;
						try {
							b = this.negAgent.acknowledgeAgreement(agreementEnvelope).get();
							NegotiationACKBeanResult resultBean = new NegotiationACKBeanResult();
							resultBean.setAcknowledgement(b);
							resultBean.setRequestor(bean.getRequestor());
							return resultBean;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						NegotiationACKBeanResult resultBean = new NegotiationACKBeanResult();
						resultBean.setAcknowledgement(false);
						resultBean.setRequestor(bean.getRequestor());
						return resultBean;
				}
				
			}else if (bean.getMethod().equals(NegAgentMethodType.GET_POLICY)){
				try{

					RequestPolicy policy =  this.negAgent.getPolicy(bean.getRequestor()).get();
					if (policy!=null){
						NegotiationGetPolicyBeanResult resultBean = new NegotiationGetPolicyBeanResult();
						resultBean.setRequestor(bean.getRequestor());
						resultBean.setRequestPolicy(policy);
						return resultBean;
					}
				} catch (InterruptedException e){

				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new NegotiationGetPolicyBeanResult();

			}else if (bean.getMethod().equals(NegAgentMethodType.NEGOTIATE)){
				try{
					
					ResponsePolicy responsePolicy = bean.getResponsePolicy();
					
					//Object obj = Util.convertToObject(responseArray,this.getClass());
					if (responsePolicy!=null){
						
							ResponsePolicy providerPolicy = (this.negAgent.negotiate(bean.getRequestor(), responsePolicy)).get();
							if (providerPolicy!=null){
								NegotiationMainBeanResult resultBean = new NegotiationMainBeanResult();
								resultBean.setRequestor(bean.getRequestor());
								resultBean.setResponsePolicy(providerPolicy);
								return resultBean;
							}
						
					}
				} catch (InterruptedException e){

				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			 ResponsePolicy emptyResponsePolicy = new ResponsePolicy();
			 emptyResponsePolicy.setRequestor(bean.getRequestor());
			 emptyResponsePolicy.setNegotiationStatus(NegotiationStatus.FAILED);
			 NegotiationMainBeanResult resultBean = new NegotiationMainBeanResult();
			 resultBean.setRequestor(bean.getRequestor());
			 resultBean.setResponsePolicy(emptyResponsePolicy);
			return resultBean;
		}else{
			this.LOG.error("Received unknown object: "+ibean.getClass()+". Expected :"+NegotiationAgentBean.class.toString());
			return "";
		}
		
	}



	private Requestor getRequestorFromBean(RequestorBean bean){
		IIdentityManager idm = this.commManager.getIdManager();
		try {
			if (bean instanceof RequestorCisBean){
				RequestorCis requestor = new RequestorCis(idm.fromJid(bean.getRequestorId()), idm.fromJid(((RequestorCisBean) bean).getCisRequestorId()));
				return requestor;

			}else if (bean instanceof RequestorServiceBean){
				RequestorService requestor = new RequestorService(idm.fromJid(bean.getRequestorId()), ((RequestorServiceBean) bean).getRequestorServiceId());
				return requestor;
			}else{
				return new Requestor(idm.fromJid(bean.getRequestorId()));
			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}


	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}


}
