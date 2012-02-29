package org.societies.pubsub;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.comm.xmpp.pubsub.PubsubClient;
import org.societies.comm.xmpp.pubsub.Subscriber;
import org.societies.comm.xmpp.pubsub.Subscription;
import org.societies.pubsub.interfaces.ISubscriber;
import org.societies.pubsub.interfaces.Pubsub;
import org.societies.pubsub.interfaces.SubscriptionParcelable;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PubsubSkeleton implements Pubsub {

	private PubsubClient pubsubClientImpl;
	
	public PubsubSkeleton(PubsubClient pubsubClient) {
		pubsubClientImpl = pubsubClient; 
	}
	
	public void ownerCreate(final String pubsubService, final String node) {
		try {
			pubsubClientImpl.ownerCreate((new IdentityManager()).fromJid(pubsubService), node);
		} catch(Exception e) { // TODO
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public void ownerDelete(final String pubsubService, final String node) {
		try {
			pubsubClientImpl.ownerDelete((new IdentityManager()).fromJid(pubsubService), node);
		} catch(Exception e) { // TODO
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String publisherPublish(final String pubsubService,
			final String node, final String itemId, final String item)
			throws XMPPError, CommunicationException {
		try { // TODO optimize: change pubsub to receive marshalled string directly
			Element itemPojo = MarshallUtils.stringToElement(item);
			
			String id = pubsubClientImpl.publisherPublish(
					(new IdentityManager()).fromJid(pubsubService), node,
					itemId, itemPojo);
			return id;
		} catch (IOException e) {
			throw new CommunicationException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new CommunicationException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new CommunicationException(e.getMessage(), e);
		}
	}

	public SubscriptionParcelable subscriberSubscribe(String pubsubService,	String node, final ISubscriber subscriber) throws XMPPError, CommunicationException {
		Subscription subscription = pubsubClientImpl.subscriberSubscribe((new IdentityManager()).fromJid(pubsubService), node, new Subscriber() {
			public void pubsubEvent(Identity pubsubService, String node,
					String itemId, Element item) {
				try {
					subscriber.pubsubEvent(pubsubService.getJid(), node, itemId, MarshallUtils.nodeToString(item));
				} catch (TransformerException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}			
		});
		return new SubscriptionParcelable(subscription);
	}
}
