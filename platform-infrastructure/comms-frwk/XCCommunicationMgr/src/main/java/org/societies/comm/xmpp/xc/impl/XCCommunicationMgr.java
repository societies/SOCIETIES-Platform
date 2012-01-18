package org.societies.comm.xmpp.xc.impl;


import org.jivesoftware.whack.ExternalComponentManager;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.exceptions.CommunicationException;
import org.societies.comm.xmpp.interfaces.CommCallback;
import org.societies.comm.xmpp.interfaces.CommManager;
import org.societies.comm.xmpp.interfaces.FeatureServer;
import org.xmpp.component.AbstractComponent;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;

public class XCCommunicationMgr extends AbstractComponent implements
		CommManager {

	private final CommManagerHelper helper;

	private String host;
	private String subDomain;
	private String secretKey;
	private ExternalComponentManager manager;

	public XCCommunicationMgr(String host, String subDomain,
			String secretKey) {
		this.helper = new CommManagerHelper();
		this.host = host;
		this.subDomain = subDomain;
		this.secretKey = secretKey;

		initWhackCommManager();
	}

	private void initWhackCommManager() {
		manager = new ExternalComponentManager(host);
		manager.setSecretKey(subDomain, secretKey);

		log.info("Connected!");
		try {
			manager.addComponent(subDomain, this);
		} catch (ComponentException e) {
			e.printStackTrace();
		}
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
	protected IQ handleIQGet(IQ iq) {
		log.debug("IQ Received");
		IQ response = helper.dispatchIQ(iq);
		return response;
	}

	@Override
	protected IQ handleIQSet(IQ iq) throws Exception {
		return handleIQGet(iq);
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

	/*
	 * Implementation of CommunicationManager methods
	 */

	// TODO test thread.getclassloader and Async
	@Override
	public void register(FeatureServer fs) throws CommunicationException,
			ClassNotFoundException {
		helper.register(fs);
	}

	@Override
	public void sendMessage(Stanza stanza, String type, Object payload)
			throws CommunicationException {
		Type mType = Message.Type.valueOf(type);
		Message m = helper.sendMessage(stanza, mType, payload);
		this.send(m);
	}

	@Override
	public void sendMessage(Stanza stanza, Object payload)
			throws CommunicationException {
		Message m = helper.sendMessage(stanza, null, payload);
		this.send(m);
	}

	@Override
	public void sendIQGet(Stanza stanza, Object payload, CommCallback callback)
			throws CommunicationException {
		IQ iq = helper.sendIQ(stanza, IQ.Type.get, payload, callback);
		this.send(iq);
	}

	@Override
	public void sendIQSet(Stanza stanza, Object payload, CommCallback callback)
			throws CommunicationException {
		IQ iq = helper.sendIQ(stanza, IQ.Type.set, payload, callback);
		this.send(iq);
	}
}