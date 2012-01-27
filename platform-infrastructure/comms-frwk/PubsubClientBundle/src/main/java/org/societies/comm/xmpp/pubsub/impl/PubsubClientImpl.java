package org.societies.comm.xmpp.pubsub.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jabber.protocol.pubsub.Create;
import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Subscribe;
import org.jabber.protocol.pubsub.Unsubscribe;
import org.jabber.protocol.pubsub.event.Event;
import org.jabber.protocol.pubsub.event.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.comm.xmpp.pubsub.Affiliation;
import org.societies.comm.xmpp.pubsub.PubsubClient;
import org.societies.comm.xmpp.pubsub.Subscriber;
import org.societies.comm.xmpp.pubsub.Subscription;
import org.societies.comm.xmpp.pubsub.SubscriptionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class PubsubClientImpl implements PubsubClient, ICommCallback {

	public static final int TIMEOUT = 10000;
	
	private final static List<String> NAMESPACES = Collections
			.singletonList("http://jabber.org/protocol/pubsub");
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList("jabber.x.data",
					"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.owner",
					"org.jabber.protocol.pubsub.event"));
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubClientImpl.class);
	
	private ICommManager endpoint;
	private Map<String,Object> responses;
	private Map<Subscription,Subscriber> subscribers;
	
	@Autowired
	public PubsubClientImpl(ICommManager endpoint) {
		responses = new HashMap<String, Object>();
		subscribers = new HashMap<Subscription, Subscriber>();
		this.endpoint = endpoint;
		try {
			endpoint.register(this);
		} catch (CommunicationException e) {
			LOG.error(e.getMessage());
		}
	}
	
	/*
	 * CommCallback Impl
	 */
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		if (payload instanceof Event) {
			Items items = ((Event)payload).getItems();
			String node = items.getNode();
			Subscription sub = new Subscription(stanza.getFrom(), stanza.getTo(), node, null); // TODO may break due to mismatch between "to" and local identity
			Subscriber subscriber = subscribers.get(sub);
			for (org.jabber.protocol.pubsub.event.Item i : items.getItem())
				subscriber.pubsubEvent(stanza.getFrom(), node, i.getId(), (Element) i.getAny());
		}
	}
	// TODO subId
//	<message from='pubsub.shakespeare.lit' to='francisco@denmark.lit' id='foo'>
//	  <event xmlns='http://jabber.org/protocol/pubsub#event'>
//	    <items node='princely_musings'>
//	      <item id='ae890ac52d0df67ed7cfdf51b644e901'/>
//	    </items>
//	  </event>
//	  <headers xmlns='http://jabber.org/protocol/shim'>
//	    <header name='SubID'>123-abc</header>
//	    <header name='SubID'>004-yyy</header>
//	  </headers>
//	</message>

	
	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		synchronized (responses) {
			LOG.info("receiveResult 4 id "+stanza.getId());
			responses.put(stanza.getId(), payload);
			responses.notifyAll();
		}
	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		synchronized (responses) {
			LOG.info("receiveError 4 id "+stanza.getId());
			responses.put(stanza.getId(), error);
			responses.notifyAll();
		}
	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<XMPPNode> items) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * PubsubClient Impl - emulates synchronous
	 */
	
	private Object blockingIQ(Stanza stanza, Object payload) throws CommunicationException, XMPPError  {
		Object response = null;
		endpoint.sendIQSet(stanza, payload, this);
		synchronized (responses) {
			response = responses.remove(stanza.getId());
			while (response==null) {
				try {
					LOG.info("waiting response 4 id "+stanza.getId());
					responses.wait(TIMEOUT);
				} catch (InterruptedException e) {
					LOG.info(e.getMessage());
				}
				LOG.info("checking response 4 id "+stanza.getId());
				response = responses.remove(stanza.getId());
			}
			LOG.info("got response 4 id "+stanza.getId());
		}
		if (response instanceof XMPPError)
			throw (XMPPError)response;
		return response;
	}
	
	@Override
	public List<String> discoItems(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		// TODO
		return null;
	}

	@Override
	public String subscriberSubscribe(Identity pubsubService, String node,
			Subscriber subscriber) throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Subscribe sub = new Subscribe();
		sub.setJid(endpoint.getIdentity().getJid());
		sub.setNode(node);
		payload.setSubscribe(sub);

		Object response = blockingIQ(stanza, payload);
		
		String subId = ((Pubsub)response).getSubscription().getSubid();
		subscribers.put(new Subscription(pubsubService, endpoint.getIdentity(), node, subId), subscriber);
		
		return subId;
	}

	@Override
	public void subscriberUnsubscribe(Identity pubsubService, String node,
			Identity subscriber, String subId) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Unsubscribe unsub = new Unsubscribe();
		unsub.setJid(endpoint.getIdentity().getJid());
		unsub.setNode(node);
		payload.setUnsubscribe(unsub);

		Object response = blockingIQ(stanza, payload);		
		
		subscribers.remove(new Subscription(pubsubService, endpoint.getIdentity(), node, subId));
	}

	

	@Override
	public List<Element> subscriberRetrieveLast(Identity pubsubService,
			String node, String subId) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Element> subscriberRetrieveSpecific(Identity pubsubService,
			String node, String subId, List<String> itemIdList)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String publisherPublish(Identity pubsubService, String node,
			String itemId, Element item) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Publish p = new Publish();
		p.setNode(node);
		Item i = new Item();
		if (itemId!=null)
			i.setId(itemId);
		i.setAny(item);
		p.setItem(i);
		payload.setPublish(p);
		
		Object response = blockingIQ(stanza, payload);
		
		return ((Pubsub)response).getPublish().getItem().getId();
	}

	@Override
	public void publisherDelete(Identity pubsubService, String node,
			String itemId) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ownerCreate(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Create c = new Create();
		c.setNode(node);
		payload.setCreate(c);
		
		blockingIQ(stanza, payload);
	}

	@Override
	public void ownerDelete(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ownerPurgeItems(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Identity, SubscriptionState> ownerGetSubscriptions(
			Identity pubsubService, String node) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Identity, Affiliation> ownerGetAffiliations(
			Identity pubsubService, String node) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ownerSetSubscriptions(Identity pubsubService, String node,
			Map<Identity, SubscriptionState> subscriptions) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ownerSetAffiliations(Identity pubsubService, String node,
			Map<Identity, Affiliation> affiliations) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}
	
}
