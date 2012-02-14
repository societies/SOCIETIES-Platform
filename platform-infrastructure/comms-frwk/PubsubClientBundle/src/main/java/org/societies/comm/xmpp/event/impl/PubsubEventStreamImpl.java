package org.societies.comm.xmpp.event.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.w3c.dom.Element;

public class PubsubEventStreamImpl extends PubsubEventStream {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubEventStreamImpl.class);
	
	private PubsubClient psc;

	public PubsubEventStreamImpl(Identity pubsubService, String node,
			ApplicationEventMulticaster multicaster, PubsubClient psc) {
		super(pubsubService, node, multicaster);
		this.psc = psc;
	}

	@Override
	public String publishLocalEvent(Object payload) {
		String itemId = null;
		try {
			itemId = psc.publisherPublish(pubsubService, node, null, payload);
		} catch (XMPPError e) {
			LOG.error("XMPPError while publishing event", e);
		} catch (CommunicationException e) {
			LOG.error("CommunicationException while publishing event", e);
		}
		return itemId;
	}
	
	public void newRemoteEvent(PubsubEvent pe, String itemId) {
		multicastRemoteEvent(pe,itemId);
	}

}
