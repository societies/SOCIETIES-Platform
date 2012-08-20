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
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class ServiceDiscovery extends Service implements IServiceDiscoveryRemote, ICommCallback {

	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("ServiceDiscoveryMsgBean", "ServiceDiscoveryResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/servicelifecycle/servicediscovery");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.servicelifecycle.servicediscovery");
    
    private static final String LOG_TAG = ServiceDiscovery.class.getName();
    private ClientCommunicationMgr commMgr;
	
	public ServiceDiscovery() {
		try {
        	commMgr.register(ELEMENT_NAMES, this);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }    
	}
	
	public void getService(ServiceResourceIdentifier arg0, IIdentity arg1, IServiceDiscoveryCallback discoCallback) {
		
	}

	public void getServices(IIdentity arg0, IServiceDiscoveryCallback discoCallback) {
	
	}

	public void searchService(
			org.societies.api.schema.servicelifecycle.model.Service service,
			IIdentity arg1, IServiceDiscoveryCallback discoCallback) {
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//@see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	public List<String> getXMLNamespaces() {
		return NAME_SPACES;
	}

	//@see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	//NOT CALLED BY commMgr.register() METHOD
	public void receiveResult(Stanza stanza, Object payload) { }

	////NOT CALLED BY commMgr.register() METHOD
	public void receiveError(Stanza stanza, XMPPError error) { }

	//NOT CALLED BY commMgr.register() METHOD
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) { }

	//NOT CALLED BY commMgr.register() METHOD
	public void receiveItems(Stanza stanza, String node, List<String> items) { }

	//NOT CALLED BY commMgr.register() METHOD
	public void receiveMessage(Stanza stanza, Object payload) { }
}
