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
package org.societies.personalisation.groupmanager.impl;

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
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.personalisation.mgmt.PersonalisationManagerBean;
import org.societies.api.schema.personalisation.mgmt.PersonalisationMethodType;
import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.springframework.scheduling.annotation.AsyncResult;


public class PersonalisationCommsClient implements IPersonalisationManager, ICommCallback {
	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/personalisation/model",
				  		"http://societies.org/api/schema/personalisation/mgmt",
				  		"http://societies.org/api/schema/identity",
				  		"http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
		  Arrays.asList("org.societies.api.schema.personalisation.model",
						"org.societies.api.schema.personalisation.mgmt",
						"org.societies.api.schema.identity",
						"org.societies.api.schema.servicelifecycle.model"));
	
	
	
	private ICommManager commsMgr;
	private static Logger LOG = LoggerFactory.getLogger(PersonalisationCommsClient.class);
	private IIdentityManager idMgr;
	Hashtable<String,IAction> results = new Hashtable<String, IAction>();
	public PersonalisationCommsClient(){
		
	}
	public void InitService(){
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommsMgr().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
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
	public void receiveResult(Stanza stanza, Object bean) {
		if (bean instanceof ActionBean){
			Action action = new Action();
			action.setServiceID(((ActionBean) bean).getServiceID());
			action.setparameterName(((ActionBean) bean).getParameterName());	
			action.setvalue(((ActionBean) bean).getValue());
			if (null!=((ActionBean) bean).getServiceType()){
				action.setServiceType(((ActionBean) bean).getServiceType());
			}
			
			this.results.put(getId(action.getServiceID(), action.getparameterName()), action);
			this.results.notifyAll();
		}
		
	}

	@Override
	public Future<IAction> getIntentAction(Requestor requestor, IIdentity userIdentity,
			ServiceResourceIdentifier serviceID, String parameterName) {
		String id = getId(serviceID,parameterName);
		this.results.put(id, new Action());
		IIdentity toIdentity = idMgr.getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);
		
		PersonalisationManagerBean bean = new PersonalisationManagerBean();
		bean.setMethod(PersonalisationMethodType.GET_INTENT_ACTION);
		bean.setRequestor(getRequestor(requestor));
		bean.setServiceId(serviceID);
		bean.setUserIdentity(userIdentity.getJid());
		bean.setParameterName(parameterName);
		
		try {
			getCommsMgr().sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
		
		//waiting for the result to be returned
/*		while (null==this.results.get(id).getparameterName()){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		while (!this.results.containsKey(id)){
			try {
				this.results.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new AsyncResult<IAction>(this.results.get(id));
	}

	
	@Override
	public Future<IAction> getPreference(Requestor requestor, IIdentity userIdentity,
			String serviceType, ServiceResourceIdentifier serviceID, String parameterName) {
		String id = getId(serviceID,parameterName);
		this.results.put(id, new Action());

		Stanza stanza = new Stanza(userIdentity);
		
		PersonalisationManagerBean bean = new PersonalisationManagerBean();
		bean.setMethod(PersonalisationMethodType.GET_PREFERENCE);
		bean.setRequestor(getRequestor(requestor));
		bean.setServiceType(serviceType);
		bean.setServiceId(serviceID);
		bean.setUserIdentity(userIdentity.getJid());
		bean.setParameterName(parameterName);
		
		try {
			getCommsMgr().sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
		//waiting for the result to be returned
		while (null==this.results.get(id).getparameterName()){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new AsyncResult<IAction>(this.results.get(id));
	}
	
	
	
	private String getId(ServiceResourceIdentifier serviceID, String parameterName) {
		return serviceID.getServiceInstanceIdentifier()+":"+parameterName;
	}
	
	
	private RequestorBean getRequestor(Requestor requestor) {
		if (requestor instanceof RequestorCis){
			RequestorCisBean bean =  new RequestorCisBean();
			bean.setRequestorId(requestor.getRequestorId().getJid());
			bean.setCisRequestorId(((RequestorCis) requestor).getCisRequestorId().getJid());
			return bean;
		}else if (requestor instanceof RequestorService){
			RequestorServiceBean bean = new RequestorServiceBean();
			bean.setRequestorId(requestor.getRequestorId().getJid());
			bean.setRequestorServiceId(((RequestorService) requestor).getRequestorServiceId());
			return bean;
		}
		
		RequestorBean bean = new RequestorBean();
		bean.setRequestorId(requestor.getRequestorId().getJid());
		return bean;
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
		this.idMgr = commsMgr.getIdManager();
	}

	@Override
	public void registerPersonalisableService(IActionConsumer arg0) {
		// this cannot be done remotely. 
		
	}
}
