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
	public void sendIQ(Stanza stanza, IQ.Type type, Object payload,
			CommCallback callback) throws CommunicationException {
		helper.sendIQ(stanza, type, payload, callback);
	}

	@Override
	public void sendMessage(Stanza stanza, Message.Type type, Object payload)
			throws CommunicationException {
		helper.sendMessage(stanza, type, payload);
	}

	@Override
	public void sendMessage(Stanza stanza, Object payload)
			throws CommunicationException {
		helper.sendMessage(stanza, null, payload);
	}
}