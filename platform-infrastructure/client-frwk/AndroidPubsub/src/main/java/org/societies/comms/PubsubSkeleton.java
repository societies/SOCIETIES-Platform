package org.societies.comms;

import java.util.List;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.pubsub.interfaces.ISubscriber;
import org.societies.pubsub.interfaces.Pubsub;
import org.societies.pubsub.interfaces.SubscriptionParcelable;

public class PubsubSkeleton implements Pubsub {

	private PubsubClientImpl pubsubClientImpl;
	
	public PubsubSkeleton(PubsubClientImpl pubsubClient) {
		pubsubClientImpl = pubsubClient; 
	}
	
	public List<String> discoItems(String pubsubService, String node) throws XMPPError, CommunicationException  {
		return pubsubClientImpl.discoItems(pubsubService, node);
	}
	
	public void ownerCreate(final String pubsubService, final String node) throws XMPPError, CommunicationException {		
		pubsubClientImpl.ownerCreate(pubsubService, node);		
	}
	
	public void ownerDelete(final String pubsubService, final String node) throws XMPPError, CommunicationException {
		pubsubClientImpl.ownerDelete(pubsubService, node);
	}
	
	public void ownerPurgeItems(String pubsubServiceJid, String node)
			throws XMPPError, CommunicationException {
		pubsubClientImpl.ownerPurgeItems(pubsubServiceJid, node);
	}

	public String publisherPublish(final String pubsubService,	final String node, final String itemId, final String item) throws XMPPError, CommunicationException {
		return pubsubClientImpl.publisherPublish(pubsubService, node, itemId, item);
	}
	
	public void publisherDelete(String pubsubServiceJid, String node,
			String itemId) throws XMPPError, CommunicationException {
		pubsubClientImpl.publisherDelete(pubsubServiceJid, node, itemId);
	}

	public SubscriptionParcelable subscriberSubscribe(String pubsubService,	String node, final ISubscriber subscriber) throws XMPPError, CommunicationException {		
		return pubsubClientImpl.subscriberSubscribe(pubsubService, node, subscriber);		
	}
	
	public void subscriberUnsubscribe(String pubsubService,	String node, final ISubscriber subscriber) throws XMPPError, CommunicationException {		
		pubsubClientImpl.subscriberUnsubscribe(pubsubService, node, subscriber);
	}
}
