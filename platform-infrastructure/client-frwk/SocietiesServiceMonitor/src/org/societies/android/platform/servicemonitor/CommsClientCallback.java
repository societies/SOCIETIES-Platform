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
package org.societies.android.platform.servicemonitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResultBean;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryResultBean;
import org.societies.api.internal.servicelifecycle.IServiceControlCallback;

import android.util.Log;


/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class CommsClientCallback implements ICommCallback {

	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("ServiceDiscoveryMsgBean", "ServiceDiscoveryResultBean");
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/servicelifecycle/model",
				  		"http://societies.org/api/schema/servicelifecycle/servicediscovery",
				  		"http://societies.org/api/schema/servicelifecycle/servicecontrol"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.servicelifecycle.model",
						"org.societies.api.schema.servicelifecycle.servicediscovery",
						"org.societies.api.schema.servicelifecycle.servicecontrol"));

    private static final String LOG_TAG = CommsClientCallback.class.getName();
	
	//MAP TO STORE THE ALL THE CLIENT CONNECTIONS
	private static final Map<String, IServiceDiscoveryCallback> serviceDiscoveryClients = new HashMap<String, IServiceDiscoveryCallback>();
	private static final Map<String, IServiceControlCallback> serviceControlClients = new HashMap<String,IServiceControlCallback>();
	
	/** Constructor for callback
	 * @param clientID unique ID of send request to comms framework
	 * @param serviceDiscoveryClient callback from originating client
	 */
	public CommsClientCallback(String clientID, IServiceDiscoveryCallback serviceDiscoveryClient) {
		//STORE THIS CALLBACK WITH THIS REQUEST ID
		serviceDiscoveryClients.put(clientID, serviceDiscoveryClient);
	}
	
	/** Constructor for callback
	 * @param clientID unique ID of send request to comms framework
	 * @param serviceControlClient callback from originating client
	 */
	public CommsClientCallback(String clientID, IServiceControlCallback serviceControlClient) {
		//STORE THIS CALLBACK WITH THIS REQUEST ID
		serviceControlClients.put(clientID, serviceControlClient);
	}

	/**Returns the correct service discovery client callback for this request 
	 * @param requestID the id of the initiating request
	 * @return
	 * @throws UnavailableException
	 */
	private IServiceDiscoveryCallback getRequestingClient(String requestID) {
		IServiceDiscoveryCallback requestingClient = (IServiceDiscoveryCallback) serviceDiscoveryClients.get(requestID);
		serviceDiscoveryClients.remove(requestID);
		return requestingClient;
	}

	/**Returns the correct service control client callback for this request 
	 * @param requestID the id of the initiating request
	 * @return
	 * @throws UnavailableException
	 */
	private IServiceControlCallback getRequestingControlClient(String requestID) {
		IServiceControlCallback requestingClient = (IServiceControlCallback) serviceControlClients.get(requestID);
		serviceControlClients.remove(requestID);
		return requestingClient;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveResult(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object) */
	public void receiveResult(Stanza returnStanza, Object msgBean) {
		Log.d(LOG_TAG, "SLM receive Result called!");
		
		// --------- Service Discovery Bean ---------
		if (msgBean.getClass().equals(ServiceDiscoveryResultBean.class)) {
			Log.d(LOG_TAG, "ServiceDiscoveryBeanResult!");
			ServiceDiscoveryResultBean serviceDiscoveryResult = (ServiceDiscoveryResultBean) msgBean;
			IServiceDiscoveryCallback serviceDiscoveryClient = getRequestingClient(returnStanza.getId());
			serviceDiscoveryClient.getResult(serviceDiscoveryResult.getServices());	
		} 
		// --------- Service Control Bean ---------
		if(msgBean.getClass().equals(ServiceControlResultBean.class)){
			Log.d(LOG_TAG, "ServiceControlBeanResult!");
			ServiceControlResultBean serviceControlResult = (ServiceControlResultBean) msgBean;
			IServiceControlCallback serviceControlClient = getRequestingControlClient(returnStanza.getId());
			Log.d(LOG_TAG, "ServiceControlBeanResult: " + serviceControlResult.getControlResult().getMessage());
			serviceControlClient.setResult(serviceControlResult.getControlResult());
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#getJavaPackages() */
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#getXMLNamespaces() */
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveError(org.societies.comm.xmpp.datatypes.Stanza, org.societies.comm.xmpp.datatypes.XMPPError)
	 */
	public void receiveError(Stanza returnStanza, XMPPError info) {
		Log.d(LOG_TAG, "received an Error!");
		Log.d(LOG_TAG, info.getMessage());
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveInfo(org.societies.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.comm.xmpp.datatypes.XMPPInfo)*/
	public void receiveInfo(Stanza returnStanza, String node, XMPPInfo info) {
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)*/
	public void receiveMessage(Stanza returnStanza, Object messageBean) {
		//System.out.println(messageBean.getClass().toString());		
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)*/
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
	}
}
