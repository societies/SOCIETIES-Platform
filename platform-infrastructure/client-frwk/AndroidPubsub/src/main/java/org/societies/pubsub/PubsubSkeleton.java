package org.societies.pubsub;

import java.util.List;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.pubsub.interfaces.ISubscriber;
import org.societies.pubsub.interfaces.Pubsub;
import org.societies.pubsub.interfaces.SubscriptionParcelable;

public class PubsubSkeleton implements Pubsub {

	private PubsubClientImpl pubsubClientImpl;
	
	public PubsubSkeleton(PubsubClientImpl pubsubClient) {
		pubsubClientImpl = pubsubClient; 
	}
	
	public List<String> discoItems(String pubsubService, String node) throws XMPPError, CommunicationException  {
		return pubsubClientImpl.discoItems((new IdentityManager()).fromJid(pubsubService), node);
	}
	
	public void ownerCreate(final String pubsubService, final String node) throws XMPPError, CommunicationException {
		pubsubClientImpl.ownerCreate((new IdentityManager()).fromJid(pubsubService), node);
	}
	
	public void ownerDelete(final String pubsubService, final String node) throws XMPPError, CommunicationException {
		pubsubClientImpl.ownerDelete((new IdentityManager()).fromJid(pubsubService), node);
	}

	public String publisherPublish(final String pubsubService,
			final String node, final String itemId, final String item)
			throws XMPPError, CommunicationException {
		
			String id = pubsubClientImpl.publisherPublish(
					(new IdentityManager()).fromJid(pubsubService), node,
					itemId, item);
			return id;
	}

	public SubscriptionParcelable subscriberSubscribe(String pubsubService,	String node, final ISubscriber subscriber) throws XMPPError, CommunicationException {		
		Subscription subscription = pubsubClientImpl.subscriberSubscribe((new IdentityManager()).fromJid(pubsubService), node, subscriber);
		return new SubscriptionParcelable(subscription);
	}
	
	public void subscriberUnsubscribe(String pubsubService,	String node, final ISubscriber subscriber) throws XMPPError, CommunicationException {		
		pubsubClientImpl.subscriberUnsubscribe((new IdentityManager()).fromJid(pubsubService), node, subscriber);
	}
}
