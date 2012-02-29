package org.societies.pubsub.interfaces;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;

public interface Pubsub {
	
	public void ownerCreate(String pubsubService, String node) throws XMPPError, CommunicationException;
	
	public void ownerDelete(String pubsubService, String node) throws XMPPError, CommunicationException;
	
	public String publisherPublish(String pubsubService, String node, String itemId, String item) throws XMPPError, CommunicationException;
	
	public SubscriptionParcelable subscriberSubscribe(String pubsubService,	String node, ISubscriber subscriber) throws XMPPError, CommunicationException;
}
