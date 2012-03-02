package org.societies.comm.xmpp.event.impl;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.springframework.context.event.ApplicationEventMulticaster;

public class PubsubEventStreamImpl extends PubsubEventStream {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubEventStreamImpl.class);
	
	private PubsubClient psc;

	public PubsubEventStreamImpl(IIdentity pubsubService, String node,
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
	
	@Override
	public void addJaxbPackages(List<String> packageList) throws JAXBException {
		psc.addJaxbPackages(packageList);
	}
	
	public void newRemoteEvent(PubsubEvent pe, String itemId) {
		multicastRemoteEvent(pe,itemId);
	}
}
