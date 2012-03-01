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
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IIdentityManager;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.utilities.DBC.Dbc;


public class CommsClient implements ICommCallback, ICSSRemoteManager {
	private final static String EXTERNAL_COMMUNICATION_MANAGER = "XCManager.societies.local";
	private final static String XMPP_SERVER = "societies.local";
	
	private static Logger LOG = LoggerFactory.getLogger(CommsClient.class);

	private ICommManager commManager;

	/**
	 * Default Constructor
	 */
	public CommsClient() {
	}
	/**
	 * Used by Spring to initialise bean
	 */
	public void initService() {
		try {
			LOG.debug("Initialise with Communication Manager");
			this.commManager.register(this);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void receiveError(Stanza stanza, XMPPError exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInfo(Stanza stanza, String arg1, XMPPInfo exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza stanza, String arg1, List<String> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Stanza stanza, Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveResult(Stanza stanza, Object object) {
		// TODO Auto-generated method stub
		
	}
	
	//Spring injection points
	public ICommManager getCommManager() {
		return commManager;
	}
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	//Remote interface methods
	@Override
	public void changeCSSNodeStatus(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getCssRecord(ICSSManagerCallback profile) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void loginCSS(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void loginXMPPServer(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void logoutCSS(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void logoutXMPPServer(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void modifyCssRecord(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void registerCSS(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void registerCSSNode(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void registerXMPPServer(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on registerXMPPServer");

		 IIdentityManager idManager = new IdentityManager();
		 Identity pubsubID = idManager.fromJid(EXTERNAL_COMMUNICATION_MANAGER);
		
//		Identity toIdentity = new Identity(IdentityType.CSS, EXTERNAL_COMMUNICATION_MANAGER, XMPP_SERVER) {
//			@Override
//			public String getJid() {
//				return getIdentifier() + "." + getDomainIdentifier();
//			}
//		};
		Stanza stanza = new Stanza(pubsubID);
		CommsClientCallback commsCallback = new CommsClientCallback(stanza.getId(), callback);
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setProfile(profile);
		messageBean.setMethod(MethodType.REGISTER_XMPP_SERVER);
		
		try {
			this.commManager.sendIQGet(stanza, messageBean, commsCallback);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void setPresenceStatus(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void synchProfile(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void unregisterCSS(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void unregisterCSSNode(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void unregisterXMPPServer(CssRecord profile, ICSSManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}

}
