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
package org.societies.android.platform;

import java.util.List;
 
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;

import android.content.Context;
import android.util.Log;

/**
 * This is the class that implements callbacks from the 
 * communication layer. It currently accepts XMPP messages.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
class CommunicationCallback implements ICommCallback{

    private List<String> nameSpaces;
    private List<String> javaPackages;
    private static final String LOG_TAG = CommunicationCallback.class.getName();
    private Context context;
    
    public CommunicationCallback(Context _context, List<String> _nameSpaces, List<String> _javaPackages){
	context = _context;
	nameSpaces = _nameSpaces;
	javaPackages = _javaPackages;
    }
    /* (non-Javadoc)
     * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
     */
    public List<String> getXMLNamespaces() {
	// TODO Auto-generated method stub
	return nameSpaces;
    }

    /* (non-Javadoc)
     * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
     */
    public List<String> getJavaPackages() {
	// TODO Auto-generated method stub
	return javaPackages;
    }

    /* (non-Javadoc)
     * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
     */
    public void receiveResult(Stanza stanza, Object payload) {
	// TODO Auto-generated method stub
	Log.d(LOG_TAG, "receiveResult");
	Log.d(LOG_TAG, "Payload class of type: " + payload.getClass().getName());
	debugStanza(stanza);
    }

    /* (non-Javadoc)
     * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
     */
    public void receiveError(Stanza stanza, XMPPError error) {
	// TODO Auto-generated method stub
	Log.d(LOG_TAG, "receiveError");
    }

    /* (non-Javadoc)
     * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
     */
    public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
	// TODO Auto-generated method stub
	Log.d(LOG_TAG, "receiveInfo");
    }

    /* (non-Javadoc)
     * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
     */
    public void receiveItems(Stanza stanza, String node, List<String> items) {
	// TODO Auto-generated method stub
	Log.d(LOG_TAG, "receiveItems");
	debugStanza(stanza);
	Log.d(LOG_TAG, "node: "+node);
	Log.d(LOG_TAG, "items:");
	for(String  item:items)
		Log.d(LOG_TAG, item);
    }

    /* (non-Javadoc)
     * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
     */
    public void receiveMessage(Stanza stanza, Object payload) {
	// TODO Auto-generated method stub
	Log.d(LOG_TAG, "receiveMessage");
	debugStanza(stanza);
    }
    
    private void debugStanza(Stanza stanza) {
	Log.d(LOG_TAG, "id="+stanza.getId());
	Log.d(LOG_TAG, "from="+stanza.getFrom());
	Log.d(LOG_TAG, "to="+stanza.getTo());
    }

}
