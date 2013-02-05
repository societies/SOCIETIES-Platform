package org.societies.pubsub.interfaces;

import java.util.List;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;

public interface Pubsub {
	
	public List<String> discoItems(String pubsubService, String node) throws XMPPError, CommunicationException;
	
	public void ownerCreate(String pubsubService, String node) throws XMPPError, CommunicationException;
	
	public void ownerDelete(String pubsubService, String node) throws XMPPError, CommunicationException;
	
	public void ownerPurgeItems(String pubsubServiceJid, String node) throws XMPPError, CommunicationException;
	
	public String publisherPublish(String pubsubService, String node, String itemId, String item) throws XMPPError, CommunicationException;
	
	public void publisherDelete(String pubsubServiceJid, String node, String itemId) throws XMPPError, CommunicationException;
	
	public SubscriptionParcelable subscriberSubscribe(String pubsubService,	String node, ISubscriber subscriber) throws XMPPError, CommunicationException;
	
	public void subscriberUnsubscribe(String pubsubService,	String node, ISubscriber subscriber) throws XMPPError, CommunicationException;
}
