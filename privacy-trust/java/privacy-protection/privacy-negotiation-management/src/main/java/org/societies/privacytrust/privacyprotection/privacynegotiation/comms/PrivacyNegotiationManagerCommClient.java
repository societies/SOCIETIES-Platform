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
package org.societies.privacytrust.privacyprotection.privacynegotiation.comms;

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
import org.societies.api.internal.privacytrust.privacyprotection.remote.INegotiationAgentRemote;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.*;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Comms Client that initiates the remote communication
 *
 * @author aleckey
 */
public class PrivacyNegotiationManagerCommClient implements INegotiationAgentRemote, ICommCallback {
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
    private Logger logging = LoggerFactory.getLogger(PrivacyNegotiationManagerCommClient.class);
    private IIdentityManager idMgr;


    private final Hashtable<String, NegotiationACKBeanResult> ackResults = new Hashtable<String, NegotiationACKBeanResult>();
    private final Hashtable<String, NegotiationGetPolicyBeanResult> policyResults = new Hashtable<String, NegotiationGetPolicyBeanResult>();
    private final Hashtable<String, NegotiationMainBeanResult> mainResults = new Hashtable<String, NegotiationMainBeanResult>();


    //PROPERTIES
    public ICommManager getCommManager() {
        return commManager;
    }

    public void setCommManager(ICommManager commManager) {
        this.commManager = commManager;
    }

    public PrivacyNegotiationManagerCommClient() {
//		ackResults = new Hashtable<String, NegotiationACKBeanResult>();
//		policyResults = new Hashtable<String, NegotiationGetPolicyBeanResult>();
//		mainResults = new Hashtable<String, NegotiationMainBeanResult>();
    }

    public void initBean() {
        //REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
        try {
            getCommManager().register(this);
        } catch (CommunicationException e) {
            logging.error("Error registering with comm manager", e);
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
    public void receiveError(Stanza arg0, XMPPError arg1) {
    }

    @Override
    public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
    }

    @Override
    public void receiveMessage(Stanza arg0, Object arg1) {

    }

    @Override
    public void receiveResult(Stanza stanza, Object bean) {
        this.logging.debug("Received resultBean");

        if (bean == null) {
            throw new IllegalArgumentException("'bean' was null, cannot process result");
        }

        if (logging.isDebugEnabled())
            logging.debug("Received: " + bean.getClass().getSimpleName());

        if (bean instanceof NegotiationACKBeanResult) {
            NegotiationACKBeanResult negotiationACKBeanResult = (NegotiationACKBeanResult) bean;

            if (negotiationACKBeanResult.getRequestor() == null) {
                throw new IllegalArgumentException("bean.requestor is null, cannot process result");
            }

            String id = getId(NegAgentMethodType.ACKNOWLEDGE_AGREEMENT, negotiationACKBeanResult.getRequestor().getRequestorId());
            this.ackResults.put(id, (NegotiationACKBeanResult) bean);
            synchronized (this.ackResults) {
                this.ackResults.notifyAll();
            }
        } else if (bean instanceof NegotiationGetPolicyBeanResult) {
            NegotiationGetPolicyBeanResult negotiationGetPolicyBeanResult = (NegotiationGetPolicyBeanResult) bean;

            if (negotiationGetPolicyBeanResult.getRequestor() == null) {
                throw new IllegalArgumentException("bean.requestor is null, cannot process result");
            }

            String id = getId(NegAgentMethodType.GET_POLICY, negotiationGetPolicyBeanResult.getRequestor().getRequestorId());
            this.policyResults.put(id, (NegotiationGetPolicyBeanResult) bean);
            synchronized (this.policyResults) {
                this.policyResults.notifyAll();
            }

        } else if (bean instanceof NegotiationMainBeanResult) {
            NegotiationMainBeanResult negotiationMainBeanResult = (NegotiationMainBeanResult) bean;

            if (negotiationMainBeanResult.getRequestor() == null) {
                throw new IllegalArgumentException("bean.requestor is null, cannot process result");
            }

            String id = getId(NegAgentMethodType.NEGOTIATE, negotiationMainBeanResult.getRequestor().getRequestorId());
            this.mainResults.put(id, (NegotiationMainBeanResult) bean);
            synchronized (this.mainResults) {
                this.mainResults.notifyAll();
            }

        } else {
            this.logging.debug("Received unknown bean");
//			if (bean!=null)
            this.logging.debug("Bean is of type: " + bean.getClass().getName());
//			else
//				this.logging.debug("Bean is null! Silent confirmation?");

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
    public Future<Boolean> acknowledgeAgreement(AgreementEnvelope envelope) {
        try {
            IIdentity toIdentity = idMgr.fromJid(envelope.getAgreement().getRequestor().getRequestorId());
        /*		try {
            toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}*/
            Stanza stanza = new Stanza(toIdentity);
            NegotiationAgentBean bean = new NegotiationAgentBean();
            bean.setAgreementEnvelope(envelope);
            bean.setRequestor(envelope.getAgreement().getRequestor());
            bean.setMethod(NegAgentMethodType.ACKNOWLEDGE_AGREEMENT);

            this.commManager.sendIQGet(stanza, bean, this);
            this.logging.debug("Sending " + NegAgentMethodType.ACKNOWLEDGE_AGREEMENT + " IQGET");


            String id = getId(NegAgentMethodType.ACKNOWLEDGE_AGREEMENT, envelope.getAgreement().getRequestor().getRequestorId());

            while (!this.ackResults.containsKey(id)) {
                try {
                    this.logging.debug("waiting for results");
                    synchronized (this.ackResults) {

                        this.ackResults.wait();
                    }

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return new AsyncResult<Boolean>(false);
                }
            }

            this.logging.debug("Returning acknowledgement result ");
            NegotiationACKBeanResult resultBean = this.ackResults.get(id);
            this.ackResults.remove(id);
            return new AsyncResult<Boolean>(resultBean.isAcknowledgement());


        } catch (CommunicationException e) {
            logging.error("", e);
        } catch (InvalidFormatException e) {
            logging.error("", e);
        }

        return new AsyncResult<Boolean>(false);

    }

    @Override
    public Future<RequestPolicy> getPolicy(RequestorBean requestor) {
        try {

            Stanza stanza = new Stanza(this.idMgr.fromJid(requestor.getRequestorId()));
            NegotiationAgentBean bean = new NegotiationAgentBean();
            bean.setRequestor(requestor);
            bean.setMethod(NegAgentMethodType.GET_POLICY);
            this.commManager.sendIQGet(stanza, bean, this);
            this.logging.debug("Sending " + NegAgentMethodType.GET_POLICY + " IQGET");
        } catch (CommunicationException e) {
            logging.error("", e);
       } catch (InvalidFormatException e) {
            logging.error("", e);
        }

        String id = getId(NegAgentMethodType.GET_POLICY, requestor.getRequestorId());

        while (!this.policyResults.containsKey(id)) {
            try {
                synchronized (this.policyResults) {
                    this.policyResults.wait();

                }

            } catch (InterruptedException e) {
                logging.error("", e);

                RequestPolicy policy = new RequestPolicy();
                policy.setRequestItems(new ArrayList<RequestItem>());
                policy.setRequestor(requestor);
                return new AsyncResult<RequestPolicy>(policy);
            }
        }

        this.logging.debug("Returning getPolicy result");
        NegotiationGetPolicyBeanResult result = policyResults.get(id);

        RequestPolicy policy = (RequestPolicy) result.getRequestPolicy();//SerialisationHelper.deserialise(result.getRequestPolicy(), this.getClass().getClassLoader());
        //RequestPolicy policy = (RequestPolicy) Util.convertToObject(result.getRequestPolicy(), this.getClass());
        this.policyResults.remove(id);
        return new AsyncResult<RequestPolicy>(policy);

    }

    /**
     * this method is not used
     */
    @Override
    public Future<IIdentity> getProviderIdentity() {

        return null;

    }

    @Override
    public Future<ResponsePolicy> negotiate(RequestorBean requestor, ResponsePolicy policy) {
        try {
            Stanza stanza = new Stanza(this.idMgr.fromJid(requestor.getRequestorId()));
            NegotiationAgentBean bean = new NegotiationAgentBean();
            bean.setMethod(NegAgentMethodType.NEGOTIATE);
            bean.setRequestor(requestor);
            bean.setResponsePolicy(policy);
            this.commManager.sendIQGet(stanza, bean, this);
            this.logging.debug("Sending " + NegAgentMethodType.NEGOTIATE + " IQGET");
        } catch (CommunicationException e) {
            logging.error("", e);
        } catch (InvalidFormatException e) {
            logging.error("", e);
        }

        String id = getId(NegAgentMethodType.NEGOTIATE, requestor.getRequestorId());

        while (!this.mainResults.containsKey(id)) {
            try {
                synchronized (this.mainResults) {
                    this.mainResults.wait();
                }
            } catch (InterruptedException e) {
                logging.error("", e);

                ResponsePolicy emptyPolicy = new ResponsePolicy();
                emptyPolicy.setRequestor(requestor);
                emptyPolicy.setResponseItems(new ArrayList<ResponseItem>());
                emptyPolicy.setNegotiationStatus(NegotiationStatus.FAILED);
                return new AsyncResult<ResponsePolicy>(emptyPolicy);
            }
        }

        this.logging.debug("Returning negotiate result");
        NegotiationMainBeanResult result = this.mainResults.get(id);

        ResponsePolicy resp = (ResponsePolicy) result.getResponsePolicy();//SerialisationHelper.deserialise(result.getResponsePolicy(), this.getClass().getClassLoader());

        //ResponsePolicy resp = (ResponsePolicy) Util.convertToObject(result.getResponsePolicy(), this.getClass());
        this.mainResults.remove(id);
        return new AsyncResult<ResponsePolicy>(resp);


    }

/*	private RequestorBean createRequestorBean(Requestor requestor){
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
	}*/


    private static String getId(NegAgentMethodType methodType, String requestorJID) {
        return methodType + ":" + requestorJID;
    }


}
