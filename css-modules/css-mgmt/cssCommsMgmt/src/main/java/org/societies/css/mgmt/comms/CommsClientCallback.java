/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.css.mgmt.comms;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.schema.cssmanagement.CssManagerResultBean;
import org.societies.utilities.DBC.Dbc;

public class CommsClientCallback implements ICommCallback {

	
	private ICSSManagerCallback sourceCallback = null;
	private static Logger LOG = LoggerFactory.getLogger(CommsClientCallback.class);
	
	public CommsClientCallback (String clientId, ICSSManagerCallback sourceCallback) {
		this.sourceCallback = sourceCallback;
	}
	
	
	@Override
	public List<String> getJavaPackages() {
		Dbc.ensure("Message bean Java packages list must have at least one member ", CommsServer.MESSAGE_BEAN_PACKAGES != null && CommsServer.MESSAGE_BEAN_PACKAGES.size() > 0);
		return CommsServer.MESSAGE_BEAN_PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		Dbc.ensure("Message bean namespaces list must have at least one member ", CommsServer.MESSAGE_BEAN_NAMESPACES != null && CommsServer.MESSAGE_BEAN_NAMESPACES.size() > 0);
		return CommsServer.MESSAGE_BEAN_NAMESPACES;
	}

	@Override
	public void receiveError(Stanza returnStanza, XMPPError info) {
		System.out.println(info.getMessage());
	}

	@Override
	public void receiveInfo(Stanza returnStanza, String node, XMPPInfo info) {
		System.out.println(info.getIdentityName());
	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Stanza returnStanza, Object messageBean) {
		System.out.println(messageBean.getClass().toString());		
	}

	@Override
	public void receiveResult(Stanza stanza, Object result) {
		if (result instanceof CssManagerResultBean) {
			LOG.debug("Callback with result");
			CssManagerResultBean resultBean = (CssManagerResultBean) result;
			this.sourceCallback.receiveResult(resultBean);
		}
	}

}
