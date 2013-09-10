package org.societies.comm.xmpp.event.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.identity.IIdentity;
import org.societies.comm.xmpp.pubsub.impl.PubsubClientImpl;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

public class PubsubEventFactoryImpl extends PubsubEventFactory implements Subscriber {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubEventFactoryImpl.class);
	
	private PubsubClient ps;
	private IIdentity localIdentity;
	private Map<Subscription,PubsubEventStreamImpl> psStreamMap;
	
	public PubsubEventFactoryImpl(PubsubClientImpl psi) {
		ps = psi;
		localIdentity = psi.getICommManager().getIdManager().getThisNetworkNode();
		psStreamMap = new HashMap<Subscription, PubsubEventStreamImpl>();
		newFactory(localIdentity, this);
	}

	@Override
	public PubsubEventStream getStream(IIdentity pubsubService, String node) {
		Subscription s = new Subscription(pubsubService, localIdentity, node, null);
		PubsubEventStreamImpl pesi = psStreamMap.get(s);
		if (pesi==null) {
			try {
				List<String> nodeList = ps.discoItems(pubsubService, null); // TODO only root nodes
				if (!nodeList.contains(node))
					ps.ownerCreate(pubsubService, node);
				ps.subscriberSubscribe(pubsubService, node, this);
				pesi = new PubsubEventStreamImpl(pubsubService, node, new SimpleApplicationEventMulticaster(), ps);
				psStreamMap.put(s, pesi);
			} catch (XMPPError e) {
				LOG.error("XMPPError while creating event stream", e);
			} catch (CommunicationException e) {
				LOG.error("CommunicationException while creating event stream", e);
			}
		}
		return pesi;
	}

	@Override
	public void pubsubEvent(IIdentity pubsubService, String node, String itemId,
			Object item) {
		Subscription s = new Subscription(pubsubService, localIdentity, node, null);
		PubsubEventStreamImpl pesi = psStreamMap.get(s);
		if (pesi==null) {
			PubsubEvent newEvent = new PubsubEvent(this, item);
			pesi.newRemoteEvent(newEvent, itemId);
		}
	}

}
