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

import org.societies.android.api.personalisation.IPersonalisationManagerAndroid;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.personalisation.model.IAction;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;

import android.content.Context;

/**
 * @author Eliza
 *
 */
public class PersonalisationManagerAndroid implements IPersonalisationManagerAndroid{

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
	
	
	
	//Logging tag
    private static final String LOG_TAG = PersonalisationManagerAndroid.class.getName();
    
	public PersonalisationManagerAndroid(Context applicationContext,
			PubsubClientAndroid pubsubClient, ClientCommunicationMgr ccm,
			boolean restrictBroadcast) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * to be removed if the String based methods are to be used
	 */
	@Override
	public IAction getIntentAction(String clientID, Requestor requestor,
			IIdentity ownerID, AServiceResourceIdentifier serviceID,
			String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IAction getIntentAction(String clientID, String requestor,
			String ownerID, String serviceID, String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * to be removed if the String based methods are to be used
	 */
	@Override
	public IAction getPreference(String clientID, Requestor requestor,
			IIdentity ownerID, String serviceType,
			AServiceResourceIdentifier serviceID, String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IAction getPreference(String clientID, String requestor,
			String ownerID, String serviceType, String serviceID,
			String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}

	
	private class PersonalisationCallback implements ICommCallback{

		private String returnIntent;
		private String client;
		
		public PersonalisationCallback(String returnIntent, String client) {
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

		public void receiveResult(Stanza arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
