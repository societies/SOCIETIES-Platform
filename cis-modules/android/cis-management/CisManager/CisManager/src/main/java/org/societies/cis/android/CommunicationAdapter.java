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
package org.societies.cis.android;

import java.util.Arrays;
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import android.util.Log;

/**
 * In charge of setting up communication with the CisManager cloud.
 * 
 * TODO: This should be generalized later for different types of conenctions.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
class CommunicationAdapter {

    private boolean online = false;
	
    // Get an identifier for log messages:
    private static final String LOG_TAG = CommunicationAdapter.class.getName();
    //TODO: change these to the CIS manager relevant beans!
    private static final List<String> ELEMENT_NAMES = Arrays.asList("communities", "subscribedTo");
    //Specify the XCCom name spaces you understand:
    private static final List<String> NAME_SPACES = Arrays.asList(
	    		"http://societies.org/api/schema/cis/manager");
    // TODO: What does this mean?
    private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.cis.manager");
    //TODO: Address of the cloud node? I thought this was set in the comms manager?
    private static final String DESTINATION = "xcmanager.jabber.sintef9013.com";

    private final IIdentity toXCManager;
    private final ICommCallback callback = createCallback();
    private ClientCommunicationMgr ccm;

    public CommunicationAdapter(){
    	try {
    	    toXCManager = IdentityManagerImpl.staticfromJid(DESTINATION);
    	    } catch (InvalidFormatException e) {
    		Log.e(LOG_TAG, e.getMessage(), e);
		throw new RuntimeException(e);
		}     
    }
	    
    boolean isOnline(){
	return online;
    }
    /**
     * When CommunicationAdapter is created it does not go online automatically
     * You have to call this method explicitly
     * 
     * @return:
     * 0 means not even tried
     * 1 means I am online
     * 2 means no network available on device
     * 3 means service not responding
     * 4 means authentication error
     */
    int goOnline(){
	//TODO: log in to network.
	
	return 0;
    }
    
    /**
     * @return
     */
    int goOffline(){
	online = false;
	// TODO: clean up network
	return 0;
    }
    private ICommCallback createCallback() {
    	return new ICommCallback() {

			public List<String> getXMLNamespaces() {
				return NAME_SPACES;
			}

			public List<String> getJavaPackages() {
				return PACKAGES;
			}

			public void receiveResult(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveResult");
				Log.d(LOG_TAG, "Payload class of type: " + payload.getClass().getName());
				debugStanza(stanza);				
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				Log.d(LOG_TAG, "receiveError");
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				Log.d(LOG_TAG, "receiveInfo");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveMessage");
				debugStanza(stanza);
				
			}
			
			private void debugStanza(Stanza stanza) {
				Log.d(LOG_TAG, "id="+stanza.getId());
				Log.d(LOG_TAG, "from="+stanza.getFrom());
				Log.d(LOG_TAG, "to="+stanza.getTo());
			}

			public void receiveItems(Stanza stanza, String node, List<String> items) {
				Log.d(LOG_TAG, "receiveItems");
				debugStanza(stanza);
				Log.d(LOG_TAG, "node: "+node);
				Log.d(LOG_TAG, "items:");
				for(String  item:items)
					Log.d(LOG_TAG, item);
			}
		};
    }

}
