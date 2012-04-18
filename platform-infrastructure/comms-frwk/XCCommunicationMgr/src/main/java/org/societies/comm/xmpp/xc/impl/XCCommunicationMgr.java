package org.societies.comm.xmpp.xc.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.util.log.Log;
import org.jivesoftware.whack.ExternalComponentManager;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.identity.IdentityManagerImpl;
import org.xmpp.component.AbstractComponent;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;
import org.xmpp.packet.Presence;

public class XCCommunicationMgr extends AbstractComponent implements ICommManagerController, ICommManager {

	private final CommManagerHelper helper;

	private String host;
	private String subDomain;
	private String secretKey;
	private ExternalComponentManager manager;
	private IIdentity thisIdentity;
	private IIdentityManager idm;
	private Set<INetworkNode> otherNodes;

	public XCCommunicationMgr(String host, String subDomain,
			String secretKey) {
		this.helper = new CommManagerHelper();
		this.host = host;
		this.subDomain = subDomain;
		this.secretKey = secretKey;
		otherNodes = new HashSet<INetworkNode>();
	}
	
	@Override
	public INetworkNode login(String identifier, String domain, String password) {
		this.host = domain;
		this.subDomain = identifier+"."+domain;
		this.secretKey = password;
		initWhackCommManager();
		return idm.getThisNetworkNode();
	}

	@Override
	public INetworkNode loginFromConfig() {
		initWhackCommManager();
		return idm.getThisNetworkNode();
	}

	@Override
	public boolean logout() {
		return UnRegisterCommManager();
	}

	private void initWhackCommManager() {
		manager = new ExternalComponentManager(host);
		manager.setSecretKey(subDomain, secretKey);

		log.info("Connected!");
		try {
			manager.addComponent(subDomain, this);
			idm = new IdentityManagerImpl(subDomain);
			thisIdentity = idm.getThisNetworkNode();
			
			probePresence();
		} catch (ComponentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		log.info("Added the component!");
		
	}

	private void probePresence() {
		Presence subscribe = new Presence();
		String bareJid = thisIdentity.getIdentifier()+"@"+thisIdentity.getDomain();
		subscribe.setFrom(subDomain);
		subscribe.setTo(bareJid);
		subscribe.setType(org.xmpp.packet.Presence.Type.subscribe);
		log.info("SENDING: "+subscribe.toXML());
		this.send(subscribe);
		
		Presence probe = new Presence();
		probe.setFrom(subDomain);
		probe.setTo(bareJid);
		probe.setType(org.xmpp.packet.Presence.Type.probe);
		log.info("SENDING: "+probe.toXML());
		this.send(probe);
	}

	/**
	 * Unregisters the XC Manager as an external component from the XMPP Server
	 */
	public boolean UnRegisterCommManager() {
		try {
			manager.removeComponent(subDomain);
		} catch (ComponentException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * Implementation of AbstractComponent methods
	 */

	@Override
	public String getDescription() {
		return "this external component acts as a dispatcher to "
				+ "facilitate the communication among Societies components";
	}

	@Override
	public String getName() {
		return "Societies Communication Manager";
	}

	@Override
	protected String[] discoInfoFeatureNamespaces() {
		return helper.getSupportedNamespaces();
	}
	
	@Override
	protected IQ handleDiscoItems(IQ iq) {
		IQ response = helper.handleDiscoItems(iq);
		response.setFrom(thisIdentity.getJid());
		log.info("disco#items response: "+response.toXML());
		return response;
	}

	@Override
	protected IQ handleIQGet(IQ iq) {
		log.info("IQ Received: "+iq.toXML());
		IQ response = helper.dispatchIQ(iq);
		response.setFrom(thisIdentity.getJid());
		log.info("sending iq response: "+response.toXML());
		return response;
	}

	@Override
	protected IQ handleIQSet(IQ iq) throws Exception {
		return handleIQGet(iq); // TODO
	}

	@Override
	protected void handleIQResult(IQ iq) {
		log.debug("IQ Result received");
		helper.dispatchIQResult(iq);
	}

	@Override
	protected void handleIQError(IQ iq) {
		helper.dispatchIQError(iq);
	}

	@Override
	protected void handleMessage(Message message) {
		helper.dispatchMessage(message);
	}
	
	@Override
	protected void handlePresence(Presence presence) {
		log.info("RECEIVED: "+presence.toXML());
		try {
			IIdentity nodeIdentity = idm.fromJid(presence.getFrom().toString());
			if (nodeIdentity instanceof INetworkNode) {
				INetworkNode node = (INetworkNode)nodeIdentity;
				if (presence.getType()==null) {
					otherNodes.add(node);
				}
				else if (presence.getType().equals(Presence.Type.unavailable)) {
					otherNodes.remove(node);
				}
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		
		log.info("otherNodes: "+Arrays.toString(otherNodes.toArray()));
	}

	/*
	 * Implementation of CommunicationManager methods
	 */

	@Override
	public void register(IFeatureServer fs) throws CommunicationException {
		helper.register(fs);
	}
	
	@Override
	public void register(ICommCallback messageCallback) throws CommunicationException {
		helper.register(messageCallback);
	}

	@Override
	public void sendMessage(Stanza stanza, String type, Object payload)
			throws CommunicationException {
		stanza.setFrom(thisIdentity);
		Type mType = Message.Type.valueOf(type);
		Message m = helper.sendMessage(stanza, mType, payload);
		System.out.println("sending message: "+m.toXML());
		this.send(m);
	}

	@Override
	public void sendMessage(Stanza stanza, Object payload)
			throws CommunicationException {
		stanza.setFrom(thisIdentity);
		Message m = helper.sendMessage(stanza, null, payload);
		System.out.println("sending message: "+m.toXML());
		this.send(m);
	}

	@Override
	public void sendIQGet(Stanza stanza, Object payload, ICommCallback callback)
			throws CommunicationException {
		stanza.setFrom(thisIdentity);
		IQ iq = helper.sendIQ(stanza, IQ.Type.get, payload, callback);
		System.out.println("sending iq get: "+iq.toXML());
		this.send(iq);
	}

	@Override
	public void sendIQSet(Stanza stanza, Object payload, ICommCallback callback)
			throws CommunicationException {
		stanza.setFrom(thisIdentity);
		IQ iq = helper.sendIQ(stanza, IQ.Type.set, payload, callback);
		System.out.println("sending iq set: "+iq.toXML());
		this.send(iq);
	}

	@Override
	public void addRootNode(XMPPNode newNode) {
		helper.addRootNode(newNode);
	}

	@Override
	public void removeRootNode(XMPPNode node) {
		helper.removeRootNode(node);
	}

	@Override
	public String getInfo(IIdentity entity, String node, ICommCallback callback)  throws CommunicationException {
		IQ iq = helper.buildInfoIq(entity, node, callback);
		iq.setFrom(thisIdentity.getJid());
		this.send(iq);
		return iq.getID();
	}

	@Override
	public String getItems(IIdentity entity, String node, ICommCallback callback)  throws CommunicationException {
		IQ iq = helper.buildItemsIq(entity, node, callback);
		iq.setFrom(thisIdentity.getJid());
		this.send(iq);
		return iq.getID();
	}

	@Override
	public IIdentityManager getIdManager() {
		return idm;
	}

	// Controll methods
	@Override
	public INetworkNode newMainIdentity(String identifier, String domain,
			String password) throws XMPPError {
		// TODO Auto-generated method stub
		// TODO when identity is created it needs to add the component to the roster for presence
		return null;
	}

	@Override
	public boolean destroyMainIdentity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<INetworkNode> getOtherNodes() {
		return otherNodes;
	}

	@Override
	public boolean isConnected() {
		if (idm!=null && idm.getThisNetworkNode()!=null)
			return true;
		else
			return false;
	}
}
