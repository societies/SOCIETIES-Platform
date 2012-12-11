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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.useragent.monitoring.MonitoringMethodType;
import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.remote.IUserAgentRemoteMgr;
import org.springframework.scheduling.annotation.AsyncResult;

public class UACommsClient implements IUserAgentRemoteMgr, ICommCallback{	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/useragent/monitoring",
					"http://societies.org/api/schema/useragent/feedback"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.useragent.monitoring",
					"org.societies.api.schema.useragent.feedback"));

	//PRIVATE VARIABLES
	private ICommManager commsMgr;
	private Logger LOG = LoggerFactory.getLogger(UACommsClient.class);
	private RequestIdGenerator idGen = new RequestIdGenerator();
	private HashMap<String, Object> results = new HashMap<String, Object>();
	IIdentity receiverId = null;  //Identity to send the message to

	//PROPERTIES
	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
	}

	public UACommsClient() {	
	}

	public void initService() {	
		
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		LOG.info("Registering UACommsClient with XMPP Communication Manager");
		try {
			getCommsMgr().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
		/*
		 * HARD CODED - TO BE CHANGED!!!
		 */
		try {
			receiverId = commsMgr.getIdManager().fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This will never be called as there will be no monitored action sent Virgo -> Virgo
	 */
	@Override
	public void monitor(String senderDeviceId, IIdentity owner, IAction action) {
		LOG.info("monitor method called in UACommsClient");

		Stanza stanza = new Stanza(receiverId);

		//CREATE MESSAGE BEAN
		LOG.info("Creating message to send to UACommsServer");
		UserActionMonitorBean uamBean = new UserActionMonitorBean();
		uamBean.setSenderDeviceId(senderDeviceId);
		uamBean.setIdentity(owner.getJid());
		uamBean.setServiceResourceIdentifier(action.getServiceID());
		uamBean.setServiceType(action.getServiceType());
		uamBean.setParameterName(action.getparameterName());
		uamBean.setValue(action.getvalue());
		uamBean.setMethod(MonitoringMethodType.MONITOR);
		try {
			LOG.info("Sending UAM message to UACommsServer");
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback.RecieveMessage()"
			commsMgr.sendMessage(stanza, uamBean);
		} catch (CommunicationException e) {
			e.printStackTrace();
		};
	}


	@Override
	public Future<List<String>> getExplicitFB(int type, ExpProposalContent content){
		LOG.debug("getExplicitFB method called in UACommsClient");

		String requestId = idGen.getId();
		results.put(requestId, null);
		try {
			
			Stanza stanza = new Stanza(receiverId);

			//CREATE MESSAGE BEAN
			LOG.debug("Creating message to send to UACommsServer");
			UserFeedbackBean ufBean = new UserFeedbackBean();
			ufBean.setRequestId(requestId);
			ufBean.setType(type);
			ufBean.setProposalText(content.getProposalText());
			List<String> optionsList = new ArrayList<String>();
			for(String nextOption: content.getOptions()){
				optionsList.add(nextOption);
			}
			ufBean.setOptions(optionsList);
			ufBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
			LOG.debug("Sending explicit feedback message to remote UACommsServer");
			//SEND REQUEST - RESPONSE WILL BE IN "callback.receiveResult()"
			commsMgr.sendIQGet(stanza, ufBean, this);

		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
		while (((ArrayList<String>)this.results.get(requestId)) == null){
			try {
				synchronized(results){
					this.results.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<String> requestResult = (List<String>)this.results.get(requestId);
		this.results.remove(requestId); //remove from results table to minimize size

		return new AsyncResult<List<String>>(requestResult);
	}


	public Future<Boolean> getImplicitFB(int type, ImpProposalContent content){
		LOG.debug("getImplicitFB method called in UACommsClient");

		String requestId = idGen.getId();
		results.put(requestId, null);
		try {
			
			Stanza stanza = new Stanza(receiverId);

			//CREATE MESSAGE BEAN
			LOG.debug("Creating message to send to UACommsServer");
			UserFeedbackBean ufBean = new UserFeedbackBean();
			ufBean.setRequestId(requestId);
			ufBean.setType(type);
			ufBean.setProposalText(content.getProposalText());
			ufBean.setTimeout(content.getTimeout());
			ufBean.setMethod(FeedbackMethodType.GET_IMPLICIT_FB);
			LOG.debug("Sending implicit feedback message to remote UACommsServer");
			//SEND REQUEST - RESPONSE WILL BE IN "callback.receiveResult()"
			commsMgr.sendIQGet(stanza, ufBean, this);

		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
		while (((Boolean)this.results.get(requestId)) == null){
			try {
				synchronized(results){
					this.results.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Boolean requestResult = (Boolean)this.results.get(requestId);
		this.results.remove(requestId); //remove from results table to minimize size

		return new AsyncResult<Boolean>(requestResult);
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
	public void receiveError(Stanza stanza, XMPPError error) {
		LOG.error("Could not send message with stanza: "+stanza.toString());
		LOG.error("Error message is: "+error.getMessage());
	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveResult(Stanza stanza, Object bean) {
		//Received explicit feedback from remote UA
		if (bean instanceof ExpFeedbackResultBean){
			String requestId = ((ExpFeedbackResultBean)bean).getRequestId();
			List<String> result = ((ExpFeedbackResultBean)bean).getFeedback();
			synchronized(results){
				this.results.put(requestId, result);
				this.results.notifyAll();
			}
			
		//Received implicit feedback from remote UA
		}else if (bean instanceof ImpFeedbackResultBean){
			String requestId = ((ImpFeedbackResultBean)bean).getRequestId();
			boolean result = ((ImpFeedbackResultBean)bean).isAccepted();
			synchronized(results){
				this.results.put(requestId, new Boolean(result));
				this.results.notifyAll();
			}
		}
	}
}
