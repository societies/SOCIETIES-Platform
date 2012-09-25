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

package org.societies.android.platform.personalisation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.personalisation.IPersonalisationManagerAndroid;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.personalisation.mgmt.PersonalisationManagerBean;
import org.societies.api.schema.personalisation.mgmt.PersonalisationMethodType;
import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PersonalisationManagerAndroid extends Service implements IPersonalisationManagerAndroid{

	private static final String LOG_TAG = PersonalisationManagerAndroid.class.getName();
	private static final List<String> ELEMENT_NAMES = Arrays.asList(
			"PersonalisationManagerBean", 
			"ActionBean", "RequestorBean", 
			"RequestorCisBean", 
			"RequestorServiceBean", 
			"ServiceResourceIdentifier");
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

	//currently hard coded but should be injected
	private static final String DESTINATION = "university.societies.local.macs.hw.ac.uk";

	private IIdentity toXCManager = null;
	private ClientCommunicationMgr ccm;

	private IBinder binder = null;

	/**
	 * Create Binder object for local service invocation
	 */
	public class LocalBinder extends Binder {
		public IPersonalisationManagerAndroid getService() {
			return PersonalisationManagerAndroid.this;
		}
	}
	
	
	Hashtable<String,IAction> results = new Hashtable<String, IAction>();
	@Override
	public void onCreate () {

		this.binder = new LocalBinder();

		this.ccm = new ClientCommunicationMgr(this);

		try {
			toXCManager = IdentityManagerImpl.staticfromJid(DESTINATION);
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException(e);
		}     
		Log.d(LOG_TAG, "Personalisation Manager service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Personalisation service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	public class LocalBinder extends Binder {
		public PersonalisationManagerAndroid getService() {
			return PersonalisationManagerAndroid.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}


	
	
	
	/*
	 * Callback - 
	 */
	private class PersonalisationCallback implements ICommCallback{

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public List<String> getXMLNamespaces() {
			return NAMESPACES;
		}

		public void receiveError(Stanza arg0, XMPPError arg1) {
			// TODO Auto-generated method stub
			
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			// TODO Auto-generated method stub
			
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			// TODO Auto-generated method stub
			
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		public void receiveResult(Stanza stanza, Object bean) {
			if (bean instanceof ActionBean){
				Action action = new Action();
				action.setServiceID(((ActionBean) bean).getServiceID());
				action.setparameterName(((ActionBean) bean).getParameterName());	
				action.setvalue(((ActionBean) bean).getValue());
				if (null!=((ActionBean) bean).getServiceType()){
					action.setServiceType(((ActionBean) bean).getServiceType());
				}
				
				results.put(getId(action.getServiceID(), action.getparameterName()), action);
				results.notifyAll();
			}
			
		}
		
	}

	public IAction getIntentAction(Requestor requestor, IIdentity userIdentity,
			ServiceResourceIdentifier serviceID, String parameterName) {
		// TODO Auto-generated method stub
		return null;
	}

	public IAction getPreference(Requestor requestor, IIdentity userIdentity,
			String serviceType, ServiceResourceIdentifier serviceID, String parameterName) {
		Log.d(LOG_TAG, "Personalisation Mgr request for preference for " +userIdentity.getJid()+ 
				": "+serviceID.getServiceInstanceIdentifier()+" : "+parameterName);
		
		String id = getId(serviceID,parameterName);
		PersonalisationManagerBean bean = new PersonalisationManagerBean();
		bean.setMethod(PersonalisationMethodType.GET_PREFERENCE);
		bean.setRequestor(getRequestor(requestor));
		bean.setServiceType(serviceType);
		bean.setServiceId(serviceID);
		bean.setUserIdentity(userIdentity.getJid());
		bean.setParameterName(parameterName);
		
		Stanza stanza = new Stanza(userIdentity);
		
		ICommCallback callback = new PersonalisationCallback();
		ccm.register(ELEMENT_NAMES, callback);
		try {
			ccm.sendIQ(stanza, IQ.Type.GET, bean, callback);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (!this.results.containsKey(id)){
			try {
				this.results.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		return results.get(id);
	}
	/*@Override
	public void monitor(IIdentity identity, IAction action){
		Log.d(LOG_TAG, "User Agent received monitored user action from identity " +identity.getJid()+ 
				": "+action.getparameterName()+" = "+action.getvalue());
		
		//CREATE MESSAGE BEAN
		UserActionMonitorBean uamBean = new UserActionMonitorBean();
		Log.d(LOG_TAG, "Creating message to send to virgo user agent");
		uamBean.setIdentity(identity.getJid());
		uamBean.setServiceResourceIdentifier(action.getServiceID());
		uamBean.setServiceType(action.getServiceType());
		uamBean.setParameterName(action.getparameterName());
		uamBean.setValue(action.getvalue());
		uamBean.setMethod(MonitoringMethodType.MONITOR);
		
		Stanza stanza = new Stanza(toXCManager);
		
		ICommCallback callback = new UserAgentCallback();
				
		try {
			Log.d(LOG_TAG, "registering info with comms FW:");
			List<String> nameSpaces = callback.getXMLNamespaces();
			List<String> jPackages = callback.getJavaPackages();
			for(String nextNameSpace: nameSpaces){
				Log.d(LOG_TAG, nextNameSpace);
			}
			for(String nextPackage: jPackages){
				Log.d(LOG_TAG, nextPackage);
			}
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.SET, uamBean, callback);
			Log.d(LOG_TAG, "Stanza sent!");
		} catch (Exception e) {
			Log.e(LOG_TAG, Log.getStackTraceString(e));
        } 
	}*/
	
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
}
