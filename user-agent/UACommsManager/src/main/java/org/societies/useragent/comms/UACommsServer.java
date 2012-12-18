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

package org.societies.useragent.comms;

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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.societies.useragent.api.monitoring.IInternalUserActionMonitor;

public class UACommsServer implements IFeatureServer{

	public static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/useragent/monitoring",
					"http://societies.org/api/schema/useragent/feedback"));
	public static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.useragent.monitoring",
					"org.societies.api.schema.useragent.feedback"));

	//PRIVATE VARIABLES
	private ICommManager commsMgr;
	private IInternalUserActionMonitor uam;
	private IInternalUserFeedback feedback;
	private IIdentityManager idManager;
	private Logger LOG = LoggerFactory.getLogger(UACommsServer.class);

	//PROPERTIES
	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
	}

	public void setUam(IInternalUserActionMonitor uam) {
		this.uam = uam;
	}
	
	public void setFeedback(IInternalUserFeedback feedback){
		this.feedback = feedback;
	}

	//METHODS
	public UACommsServer(){
	}

	public void initService() {
		//REGISTER OUR CommsManager WITH THE XMPP Communication Manager
		try {
			getCommsMgr().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idManager = commsMgr.getIdManager();
	}

	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	
	/*
	 * USER ACTION MONITOR METHODS
	 */
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("UACommsServer received message with no return type!!!");
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		if (payload instanceof UserActionMonitorBean){ 
			this.receiveMessage(stanza, (UserActionMonitorBean)payload);
		}
	}

	private void receiveMessage(Stanza stanza, UserActionMonitorBean monitorBean){
		//---- UAM Bundle ---
		LOG.info("Message received for UAM - processing");

		switch(monitorBean.getMethod()){
		case MONITOR:
			try {
				String senderDeviceId = monitorBean.getSenderDeviceId();
				IIdentity owner = idManager.fromJid(monitorBean.getIdentity());
				ServiceResourceIdentifier serviceId = monitorBean.getServiceResourceIdentifier();
				String serviceType = monitorBean.getServiceType();
				String parameterName = monitorBean.getParameterName();
				String value = monitorBean.getValue();
				IAction action = new Action(serviceId, serviceType, parameterName, value);
				LOG.info("Sending remote message to local UAM");
				uam.monitorFromRemoteNode(senderDeviceId, owner, action);
				break;
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
	}

	
	/*
	 * USER FEEDBACK METHODS
	 */
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		LOG.info("UACommsServer received message with a return type!!!");
		Object result = null;
		if (payload instanceof UserActionMonitorBean){ 
			this.receiveMessage(stanza, (UserActionMonitorBean)payload);
			result = true;
		}
		if (payload instanceof UserFeedbackBean){
			result = this.getQuery(stanza, (UserFeedbackBean)payload);
		}
		return result;
	}

	private Object getQuery(Stanza stanza, UserFeedbackBean feedbackBean){
		//---- User Feedback Bundle ----
		LOG.info("Message received for User Feedback - processing");

		Object resultBean = null;

		switch(feedbackBean.getMethod()){
		case GET_EXPLICIT_FB:
			try {
				String requestId = feedbackBean.getRequestId();
				int expType = feedbackBean.getType();
				String expProposalText = feedbackBean.getProposalText();
				List<String> tmp = feedbackBean.getOptions();
				String[] options = new String[tmp.size()];
				//create array of options
				int i = 0;
				for(String nextOption: tmp){
					options[i] = nextOption;
					i++;
				}
				LOG.debug("Sending remote message to local User Feedback - explicit");
				ExpProposalContent expContent = new ExpProposalContent(expProposalText, options);
				List<String> result = feedback.getExplicitFBforRemote(expType, expContent).get();
				
				//create response bean
				ExpFeedbackResultBean expResultBean = new ExpFeedbackResultBean();
				expResultBean.setRequestId(requestId);
				expResultBean.setFeedback(result);
				resultBean = expResultBean;

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			break;
			
		case GET_IMPLICIT_FB:
			try {
				String requestId = feedbackBean.getRequestId();
				int impType = feedbackBean.getType();
				String impProposalText = feedbackBean.getProposalText();
				int timeout = feedbackBean.getTimeout();
				LOG.debug("Sending remote message to local User Feedback - implicit");
				ImpProposalContent impContent = new ImpProposalContent(impProposalText, timeout);
				Boolean result = feedback.getImplicitFBforRemote(impType, impContent).get();
				
				//create response bean
				ImpFeedbackResultBean impResultBean = new ImpFeedbackResultBean();
				impResultBean.setRequestId(requestId);
				impResultBean.setAccepted(result.booleanValue());
				resultBean = impResultBean;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			break;
		}

		return resultBean;
	}


	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		return null;
	}





}
