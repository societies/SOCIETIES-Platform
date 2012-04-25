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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.utilities.DBC.Dbc;
import org.societies.api.identity.INetworkNode;

public class CommsClient implements ICommCallback, ICSSRemoteManager {
	private final static String EXTERNAL_COMMUNICATION_MANAGER = "XCManager.societies.local";
	private final static String XMPP_SERVER = "societies.local";

	private static Logger LOG = LoggerFactory.getLogger(CommsClient.class);

	private ICommManager commManager;
	private IIdentityManager idMgr;

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
			idMgr = commManager.getIdManager();

		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Obtain an identity to communicate with
	 * 
	 * @return IIdentity identity
	 * @throws InvalidFormatException
	 */
	private IIdentity getIdentity() throws InvalidFormatException {
		IIdentity toIdentity = idMgr.fromJid(EXTERNAL_COMMUNICATION_MANAGER);
		return toIdentity;

	}

	// ICommCallback implementation
	@Override
	public List<String> getJavaPackages() {
		Dbc.ensure(
				"Message bean Java packages list must have at least one member ",
				CommsServer.MESSAGE_BEAN_PACKAGES != null
						&& CommsServer.MESSAGE_BEAN_PACKAGES.size() > 0);
		return CommsServer.MESSAGE_BEAN_PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		Dbc.ensure(
				"Message bean namespaces list must have at least one member ",
				CommsServer.MESSAGE_BEAN_NAMESPACES != null
						&& CommsServer.MESSAGE_BEAN_NAMESPACES.size() > 0);
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

	// Spring injection points
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	// Remote interface methods
	@Override
	public void changeCSSNodeStatus(CssRecord profile,
			ICSSManagerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getCssRecord(ICSSManagerCallback profile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loginCSS(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on loginCSS");

		LOG.debug("Record profile identity: " + profile.getCssIdentity());
		LOG.debug("Record profile password: " + profile.getPassword());
		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.LOGIN_CSS);
			LOG.debug("Message Bean profile identity: "
					+ messageBean.getProfile().getCssIdentity());
			LOG.debug("Message Bean profile password: "
					+ messageBean.getProfile().getPassword());

			try {
				LOG.debug("Sending stanza with CSS identity: "
						+ profile.getCssIdentity() + " and password: "
						+ profile.getPassword());
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void loginXMPPServer(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on loginXMPPServer");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.LOGIN_XMPP_SERVER);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void logoutCSS(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on logoutCSS");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.LOGOUT_CSS);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void logoutXMPPServer(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on logoutXMPPServer");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.LOGOUT_XMPP_SERVER);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void modifyCssRecord(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on modifyCssRecord");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.MODIFY_CSS_RECORD);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void registerCSS(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on registerCSS");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.REGISTER_CSS);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void registerCSSNode(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on registerCSSNode");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.REGISTER_CSS_NODE);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void registerXMPPServer(CssRecord profile,
			ICSSManagerCallback callback) {
		LOG.debug("Remote call on registerXMPPServer");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.REGISTER_XMPP_SERVER);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void setPresenceStatus(CssRecord profile,
			ICSSManagerCallback callback) {
		LOG.debug("Remote call on setPresenceStatus");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.SET_PRESENCE_STATUS);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void synchProfile(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on synchProfile");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.SYNCH_PROFILE);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void unregisterCSS(CssRecord profile, ICSSManagerCallback callback) {
		LOG.debug("Remote call on unregisterCSS");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.UNREGISTER_CSS);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void unregisterCSSNode(CssRecord profile,
			ICSSManagerCallback callback) {
		LOG.debug("Remote call on unregisterCSSNode");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.UNREGISTER_CSS_NODE);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void unregisterXMPPServer(CssRecord profile,
			ICSSManagerCallback callback) {
		LOG.debug("Remote call on unregisterXMPPServer");

		IIdentity toIdentity;
		try {
			toIdentity = getIdentity();
			Stanza stanza = new Stanza(toIdentity);
			CommsClientCallback commsCallback = new CommsClientCallback(
					stanza.getId(), callback);

			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setProfile(profile);
			messageBean.setMethod(MethodType.UNREGISTER_XMPP_SERVER);

			try {
				this.commManager.sendIQGet(stanza, messageBean, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// @Override
	public void sendCssFriendRequest(String cssFriendId) {

		LOG.debug("Remote call on sendCssFriendRequest");

		try {

			Stanza stanza = new Stanza(commManager.getIdManager().fromJid(
					cssFriendId));
			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			

			messageBean.setMethod(MethodType.SEND_CSS_FRIEND_REQUEST);

			try {
				this.commManager.sendMessage(stanza, messageBean);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.css.management.ICSSRemoteManager#updateCssFriendRequest(org.societies.api.schema.cssmanagement.CssRequest)
	 */
	@Override
	public void updateCssFriendRequest(CssRequest request) {
		// TODO Auto-generated method stub
		LOG.debug("Remote call on sendCssFriendRequest");

		try {

			Stanza stanza = new Stanza(commManager.getIdManager().fromJid(
					request.getCssIdentity()));
			CssManagerMessageBean messageBean = new CssManagerMessageBean();

			request.setOrigin(CssRequestOrigin.REMOTE);
			
			messageBean.setMethod(MethodType.UPDATE_CSS_FRIEND_REQUEST);
			messageBean.setRequestStatus(request.getRequestStatus());

			try {
				this.commManager.sendMessage(stanza, messageBean);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.css.management.ICSSRemoteManager#updateCssFriendRequest(org.societies.api.schema.cssmanagement.CssRequest)
	 */
	@Override
	public void updateCssRequest(CssRequest request) {
		// TODO Auto-generated method stub
		LOG.debug("Remote call on updateCssRequest");

		try {

			Stanza stanza = new Stanza(commManager.getIdManager().fromJid(
					request.getCssIdentity()));
			CssManagerMessageBean messageBean = new CssManagerMessageBean();

			messageBean.setMethod(MethodType.UPDATE_CSS_REQUEST);
			request.setOrigin(CssRequestOrigin.REMOTE);
			messageBean.setRequestStatus(request.getRequestStatus());

			try {
				this.commManager.sendMessage(stanza, messageBean);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
		
}
