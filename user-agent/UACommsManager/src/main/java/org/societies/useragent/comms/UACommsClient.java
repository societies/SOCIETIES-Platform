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
import java.util.List;

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
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.useragent.monitoring.MethodType;
import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.remote.IUserAgentRemoteMgr;
import org.societies.useragent.api.remote.feedback.IUserFeedbackCallback;

public class UACommsClient implements IUserAgentRemoteMgr, ICommCallback{	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/useragent/monitoring"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.useragent.monitoring"));

	//PRIVATE VARIABLES
	private ICommManager commsMgr;
	private IIdentityManager idManager;
	private Logger LOG = LoggerFactory.getLogger(UACommsClient.class);
	IIdentity toIdentity = null;

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
		idManager = commsMgr.getIdManager();
		
		//Hard coded destination - temporary
		try {
			toIdentity = idManager.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void monitor(IIdentity owner, IAction action) {
		LOG.info("monitor method called in UACommsClient");
		
		Stanza stanza = new Stanza(toIdentity);

		//CREATE MESSAGE BEAN
		LOG.info("Creating message to send to UACommsServer");
		UserActionMonitorBean uamBean = new UserActionMonitorBean();
		uamBean.setIdentity(owner.getJid());
		uamBean.setServiceResourceIdentifier(action.getServiceID());
		uamBean.setServiceType(action.getServiceType());
		uamBean.setParameterName(action.getparameterName());
		uamBean.setValue(action.getvalue());
		uamBean.setMethod(MethodType.MONITOR);
		try {
			LOG.info("Sending UAM message to UACommsServer");
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback.RecieveMessage()"
			commsMgr.sendMessage(stanza, uamBean);
		} catch (CommunicationException e) {
			e.printStackTrace();
		};
	}
	
	@Override
	public void getExplicitFB(int type, ExpProposalContent content, IUserFeedbackCallback callback){
		LOG.info("getExplicitFB method called in UACommsClient");
		
		Stanza stanza = new Stanza(toIdentity);
		
		//CREATE MESSAGE BEAN
		LOG.info("Creating message to send to UACommsServer");
		UserFeedbackBean ufBean = new UserFeedbackBean();
		ufBean.setType(type);
		ufBean.setProposalText(content.getProposalText());
		List<String> optionsList = new ArrayList<String>();
		for(String nextOption: content.getOptions()){
			optionsList.add(nextOption);
		}
		ufBean.setOptions(optionsList);
		try {
			LOG.info("Sending Feedback message to UACommsServer");
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback.RecieveMessage()"
			commsMgr.sendMessage(stanza, ufBean);
		} catch (CommunicationException e) {
			e.printStackTrace();
		};
	}
	
	public void getImplicitFB(int type, ImpProposalContent content, IUserFeedbackCallback callback){
		
	}

	/*@Override
	public void registerForActionUpdates(IUserActionListener listener) {
		IIdentity toIdentity = null;
		try {
			toIdentity = idManager.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);

		//CREATE MESSAGE BEAN
		UserActionMonitorBean uaBean = new UserActionMonitorBean();
		//uaBean.setListener(listener);
		//uaBean.setMethod(MethodType.REGISTER);
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback.RecieveMessage()"
			commManager.sendMessage(stanza, uaBean);
		} catch (CommunicationException e) {
			e.printStackTrace();
		};
	}*/

	
	
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

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
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
