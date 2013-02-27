/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.android.platform.personalisation.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.internal.personalisation.IPersonalisationManagerInternalAndroid;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.personalisation.mgmt.PersonalisationManagerBean;
import org.societies.api.schema.personalisation.mgmt.PersonalisationMethodType;
import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author Eliza
 *
 */
public class PersonalisationManagerAndroid implements IPersonalisationManagerInternalAndroid{

	protected static final List<String> ELEMENT_NAMES = Collections.unmodifiableList(Arrays.asList("PersonalisationManagerBean","ActionBean"));
	protected static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/personalisation/model",
					"http://societies.org/api/schema/personalisation/mgmt",
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/schema/servicelifecycle/model"));
	protected static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.personalisation.model",
					"org.societies.api.schema.personalisation.mgmt",
					"org.societies.api.schema.identity",
					"org.societies.api.schema.servicelifecycle.model"));



	//Logging tag
	private static final String LOG_TAG = PersonalisationManagerAndroid.class.getName();
	private final Context applicationContext;
	private final ClientCommunicationMgr commMgr;
	private final boolean restrictBroadcast;

	public PersonalisationManagerAndroid(Context applicationContext,
			boolean restrictBroadcast, ClientCommunicationMgr ccm) {
		this.applicationContext = applicationContext;
		this.commMgr = ccm;
		this.restrictBroadcast = restrictBroadcast;
	}

	@Override
	public ActionBean getIntentAction(String clientID, RequestorBean requestor,
			String ownerID, ServiceResourceIdentifier serviceID,
			String preferenceName) {
		// TODO Auto-generated method stub
		PersonalisationCommCallback callback = new PersonalisationCommCallback(GET_INTENT_ACTION, clientID);
		
		//only service my identities
		try{
			IIdentityManager idm = this.commMgr.getIdManager();
			if (idm.isMine(idm.fromJid(ownerID))){
				IIdentity cloudNode = idm.getCloudNode();
				PersonalisationManagerBean bean = new PersonalisationManagerBean();
				bean.setMethod(PersonalisationMethodType.GET_INTENT_ACTION);
				bean.setRequestor(requestor);
				bean.setServiceId(serviceID);
				bean.setUserIdentity(ownerID);
				bean.setParameterName(preferenceName);

				Stanza stanza = new Stanza(cloudNode);

				
			
				commMgr.sendIQ(stanza, IQ.Type.GET, bean, callback);
			}
		
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public ActionBean getPreference(String clientID, RequestorBean requestor,
			String ownerID, String serviceType,
			ServiceResourceIdentifier serviceID, String preferenceName) {
		PersonalisationCommCallback callback = new PersonalisationCommCallback(GET_PREFERENCE, clientID);
		
		//only service my identities
		try{
			IIdentityManager idm = this.commMgr.getIdManager();
			if (idm.isMine(idm.fromJid(ownerID))){
				IIdentity cloudNode = idm.getCloudNode();
				PersonalisationManagerBean bean = new PersonalisationManagerBean();
				bean.setMethod(PersonalisationMethodType.GET_PREFERENCE);
				bean.setRequestor(requestor);
				bean.setServiceId(serviceID);
				bean.setUserIdentity(ownerID);
				bean.setServiceType(serviceType);
				bean.setParameterName(preferenceName);

				Stanza stanza = new Stanza(cloudNode);

				
				commMgr.sendIQ(stanza, IQ.Type.GET, bean, callback);
				
				
			}
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public ActionBean getIntentAction(String clientID, String ownerID,
			ServiceResourceIdentifier serviceID, String preferenceName) {
		PersonalisationCommCallback callback = new PersonalisationCommCallback(GET_INTENT_ACTION, clientID);
		
		//only service my identities
		try {
			IIdentityManager idm = this.commMgr.getIdManager();
			if (idm.isMine(idm.fromJid(ownerID))){
				IIdentity cloudNode = idm.getCloudNode();
				PersonalisationManagerBean bean = new PersonalisationManagerBean();
				bean.setMethod(PersonalisationMethodType.GET_INTENT_ACTION);
				bean.setServiceId(serviceID);
				bean.setUserIdentity(ownerID);
				bean.setParameterName(preferenceName);

				Stanza stanza = new Stanza(cloudNode);

				
				
					commMgr.sendIQ(stanza, IQ.Type.GET, bean, callback);
				
			}
		
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ActionBean getPreference(String clientID, String ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
		PersonalisationCommCallback callback = new PersonalisationCommCallback(GET_PREFERENCE, clientID);
		
		//only service my identities
		try {
			IIdentityManager idm = this.commMgr.getIdManager();
			if (idm.isMine(idm.fromJid(ownerID))){
				IIdentity cloudNode = idm.getCloudNode();
				PersonalisationManagerBean bean = new PersonalisationManagerBean();
				bean.setMethod(PersonalisationMethodType.GET_PREFERENCE);
				bean.setServiceId(serviceID);
				bean.setUserIdentity(ownerID);
				bean.setServiceType(serviceType);
				bean.setParameterName(preferenceName);

				Stanza stanza = new Stanza(cloudNode);

				
				commMgr.sendIQ(stanza, IQ.Type.GET, bean, callback);
				
			}
			
		
		} catch (org.societies.api.identity.InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		return null;
	}

	private class PersonalisationCommCallback implements ICommCallback{

		private String returnIntent;
		private String client;

		public PersonalisationCommCallback(String returnIntent, String client) {
			this.returnIntent = returnIntent;
			this.client = client;
		}
		public List<String> getJavaPackages() {
			// TODO Auto-generated method stub
			return PACKAGES;
		}

		public List<String> getXMLNamespaces() {
			// TODO Auto-generated method stub
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
			Log.d(LOG_TAG, "Received bean");
			if (client!=null){
				if (bean==null) 
				{
					Log.d(LOG_TAG, ">>>> msgBean is null");
					return;
				}
				if (bean instanceof ActionBean){
					//AAction toReturn = toAAction((ActionBean) bean);
					Intent intent = new Intent(returnIntent);
					intent.putExtra(INTENT_RETURN_VALUE, (Parcelable) bean);
					if (restrictBroadcast){
						intent.setPackage(client);
					}
					PersonalisationManagerAndroid.this.applicationContext.sendBroadcast(intent);
					Log.d(LOG_TAG, "SendBroadcast intent with action object");
					

				}else{
					Log.d(LOG_TAG, "Received bean not instance of ActionBean");
				}
			}else{
				Log.d(LOG_TAG, "No client specified to send the intent to");
			}
		}


	}


}
