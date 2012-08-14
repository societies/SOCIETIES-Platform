package org.societies.pubsub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jabber.protocol.pubsub.Create;
import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Items;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Retract;
import org.jabber.protocol.pubsub.Subscribe;
import org.jabber.protocol.pubsub.Unsubscribe;
import org.jabber.protocol.pubsub.owner.Affiliations;
import org.jabber.protocol.pubsub.owner.Delete;
import org.jabber.protocol.pubsub.owner.Purge;
import org.jabber.protocol.pubsub.owner.Subscriptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.Affiliation;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.pubsub.interfaces.ISubscriber;
import org.societies.pubsub.interfaces.SubscriptionParcelable;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PubsubClientImpl implements org.societies.pubsub.interfaces.Pubsub, ICommCallback {

	public static final int TIMEOUT = 10000;
	
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays.asList("http://jabber.org/protocol/pubsub",
						   					"http://jabber.org/protocol/pubsub#errors",
						   					"http://jabber.org/protocol/pubsub#event",
						   					"http://jabber.org/protocol/pubsub#owner",
						   					"http://jabber.org/protocol/disco#items"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList("org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.event",
					"org.jabber.protocol.pubsub.owner"));
	
	private static final List<String> ELEMENTS = Collections.unmodifiableList(
			Arrays.asList("pubsub", 
					      "event", 
					      "query"));
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubClientImpl.class);
	
	private ICommManager endpoint;
	private Map<String,Object> responses;
	private Map<Subscription,List<ISubscriber>> subscribers;	
	
	public PubsubClientImpl(ICommManager endpoint) {
		responses = new HashMap<String, Object>();
		subscribers = new HashMap<Subscription, List<ISubscriber>>();
		this.endpoint = endpoint;
		try {
			endpoint.register(this);
		} catch (CommunicationException e) {
			LOG.error(e.getMessage());
		} 
	}
	
	public ICommManager getICommManager() {
		return endpoint;
	}
	
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	public static List<String> getXMLElements() {
		return ELEMENTS;
	}

	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	
	public void receiveMessage(Stanza stanza, Object payload) {
		if (payload instanceof org.jabber.protocol.pubsub.event.Event) {
			org.jabber.protocol.pubsub.event.Items items = ((org.jabber.protocol.pubsub.event.Event)payload).getItems();
			String node = items.getNode();
			Subscription sub = new Subscription(stanza.getFrom(), stanza.getTo(), node, null); // TODO may break due to mismatch between "to" and local IIdentity
			org.jabber.protocol.pubsub.event.Item i = items.getItem().get(0); // TODO assume only one item per notification
			try {
				List<ISubscriber> subscriberList = subscribers.get(sub);
				for (ISubscriber subscriber : subscriberList)
					subscriber.pubsubEvent(stanza.getFrom().getJid(), node, i.getId(), MarshallUtils.nodeToString((Element)i.getAny()));

			} catch (TransformerException e) {
				LOG.error("Error while unmarshalling pubsub event payload", e);
			}
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

	
	public void receiveResult(Stanza stanza, Object payload) {
		synchronized (responses) {
			LOG.info("receiveResult 4 id "+stanza.getId());
			responses.put(stanza.getId(), payload);
			responses.notifyAll();
		}
	}

	public void receiveError(Stanza stanza, XMPPError error) {
		synchronized (responses) {
			LOG.info("receiveError 4 id "+stanza.getId());
			responses.put(stanza.getId(), error);
			responses.notifyAll();
		}
	}

	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		
	}

	public void receiveItems(Stanza stanza, final String node, final List<String> items) {
		Entry<String, List<String>> mapSimpleEntry = new Entry<String, List<String>>() {
			public String getKey() {
				return node;
			}
			public List<String> getValue() {
				return items;
			}
			public List<String> setValue(List<String> value) {
				throw new UnsupportedOperationException("Immutable object.");
			}			
		};
		synchronized (responses) {
			LOG.info("receiveItems 4 id "+stanza.getId());
			responses.put(stanza.getId(), mapSimpleEntry);
			responses.notifyAll();
		}
	}
	
	private Object blockingIQ(Stanza stanza, Object payload) throws CommunicationException, XMPPError  {
		endpoint.sendIQSet(stanza, payload, this);
		return waitForResponse(stanza.getId());
	}
	
	private Object waitForResponse(String id) throws XMPPError {
		Object response = null;
		synchronized (responses) {				
			while (!responses.containsKey(id)) {
				try {
					LOG.info("waiting response 4 id "+id);
					responses.wait(TIMEOUT);
				} catch (InterruptedException e) {
					LOG.info(e.getMessage());
				}
				LOG.info("checking response 4 id "+id+" in "+Arrays.toString(responses.keySet().toArray()));
			}
			response = responses.remove(id);
			LOG.info("got response 4 id "+id);
		}
		if (response instanceof XMPPError)
			throw (XMPPError)response;
		return response;
	}

	public List<String> discoItems(String pubsubService, String node)
			throws XMPPError, CommunicationException {
			String id = endpoint.getItems(convertStringToIdentity(pubsubService), node, this);
			Object response = waitForResponse(id);
			
			// TODO node check
//		String returnedNode = ((SimpleEntry<String, List<XMPPNode>>)response).getKey();
//		if (returnedNode != node)
//			throw new CommunicationException("");
			if (response!=null)
				return ((Entry<String, List<String>>)response).getValue();
			else
				return null;
	}

	public SubscriptionParcelable subscriberSubscribe(String pubsubService, String node,
			ISubscriber subscriber) throws XMPPError, CommunicationException {
		IIdentity pubsubServiceIdentity = convertStringToIdentity(pubsubService);
		Subscription subscription = new Subscription(pubsubServiceIdentity, localIdentity(), node, null);
		List<ISubscriber> subscriberList = subscribers.get(subscription);
		
		if (subscriberList==null) {
			subscriberList = new ArrayList<ISubscriber>();
			
			Stanza stanza = new Stanza(pubsubServiceIdentity);
			Pubsub payload = new Pubsub();
			Subscribe sub = new Subscribe();
			sub.setJid(localIdentity().getBareJid());
			sub.setNode(node);
			payload.setSubscribe(sub);
	
			Object response = blockingIQ(stanza, payload);
			
			String subId = ((Pubsub)response).getSubscription().getSubid();
			subscription = new Subscription(pubsubServiceIdentity, localIdentity(), node, subId);
			subscribers.put(subscription, subscriberList);
		}
		
		subscriberList.add(subscriber);
		
		return new SubscriptionParcelable(subscription, endpoint.getIdManager());
	}

	public void subscriberUnsubscribe(String pubsubService, String node,
			ISubscriber subscriber) throws XMPPError,
			CommunicationException {
		IIdentity pubsubServiceIdentity = convertStringToIdentity(pubsubService);
		Subscription subscription = new Subscription(pubsubServiceIdentity, localIdentity(), node, null);
		List<ISubscriber> subscriberList = subscribers.get(subscription);
//		subscriberList.remove(subscriber);  // TODO Unsubscribes all subcribers. Change 
		subscriberList.clear();             //  to allow single unsubscription.
		
		if (subscriberList.size()==0) {
			Stanza stanza = new Stanza(pubsubServiceIdentity);
			Pubsub payload = new Pubsub();
			Unsubscribe unsub = new Unsubscribe();
			unsub.setJid(localIdentity().getJid());
			unsub.setNode(node);
			payload.setUnsubscribe(unsub);
	
			Object response = blockingIQ(stanza, payload);
		}
	}

	public List<Element> subscriberRetrieveLast(IIdentity pubsubService,
			String node, String subId) throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Items items = new Items();
		items.setNode(node);
		if (subId!=null)
			items.setSubid(subId);
		// TODO max items... in the server also!
		payload.setItems(items);
		
		Object response = blockingIQ(stanza, payload);
		
		List<Item> itemList = ((Pubsub)response).getItems().getItem();
		List<Element> returnList = new ArrayList<Element>();
		for (Item i : itemList)
			returnList.add((Element) i.getAny());
		
		return returnList;
	}

	public List<Element> subscriberRetrieveSpecific(IIdentity pubsubService,
			String node, String subId, List<String> itemIdList)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Items items = new Items();
		items.setNode(node);
		if (subId!=null)
			items.setSubid(subId);
		
		for(String itemId : itemIdList) {
			Item item = new Item();
			item.setId(itemId);
			items.getItem().add(item);
		}
		
		payload.setItems(items);
		
		Object response = blockingIQ(stanza, payload);
		
		List<Item> itemList = ((Pubsub)response).getItems().getItem();
		List<Element> returnList = new ArrayList<Element>();
		for (Item i : itemList)
			returnList.add((Element) i.getAny());
		
		return returnList;
	}

	public String publisherPublish(String pubsubServiceJid, String node,
			String itemId, String item) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(convertStringToIdentity(pubsubServiceJid));
		Pubsub payload = new Pubsub();
		Publish p = new Publish();
		p.setNode(node);
		Item i = new Item();
		if (itemId!=null)
			i.setId(itemId);

		try {
			i.setAny(MarshallUtils.stringToElement(item));
		} catch (SAXException e) {
			LOG.error("SAXException when parsing string to XML Element", e);
		} catch (IOException e) {
			LOG.error("IOException when parsing string to XML Element", e);
		} catch (ParserConfigurationException e) {
			LOG.error("ParserConfigurationException when parsing string to XML Element", e);
		}
		//i.setAny(item); // TODO 
		
		p.setItem(i);
		payload.setPublish(p);
		
		Object response = blockingIQ(stanza, payload);
		
		try {
			return ((Pubsub)response).getPublish().getItem().getId();
		} catch(NullPointerException e) { // 7.1.2 the IQ-result SHOULD include an empty <item/> element that specifies the ItemID of the published item.
			return null; 
		}
	}

	public void publisherDelete(String pubsubServiceJid, String node,
			String itemId) throws XMPPError, CommunicationException {
		IIdentity pubsubService = convertStringToIdentity(pubsubServiceJid);
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		
		Retract retract = new Retract();
		retract.setNode(node);
		Item i = new Item();
		i.setId(itemId);
		retract.getItem().add(i);
		payload.setRetract(retract);
		
		Object response = blockingIQ(stanza, payload);
	}

	public void ownerCreate(String pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(convertStringToIdentity(pubsubService));
		Pubsub payload = new Pubsub();
		Create c = new Create();
		c.setNode(node);
		payload.setCreate(c);
				
		blockingIQ(stanza, payload);
	}

	public void ownerDelete(String pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(convertStringToIdentity(pubsubService));
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Delete delete = new Delete();
		delete.setNode(node);
		payload.setDelete(delete);
		
		blockingIQ(stanza, payload);
	}

	public void ownerPurgeItems(String pubsubServiceJid, String node)
			throws XMPPError, CommunicationException {
		IIdentity pubsubService = convertStringToIdentity(pubsubServiceJid);
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Purge purge = new Purge();
		purge.setNode(node);
		payload.setPurge(purge);
		
		blockingIQ(stanza, payload);
	}

	public Map<IIdentity, SubscriptionState> ownerGetSubscriptions(
			IIdentity pubsubService, String node) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Subscriptions subs = new Subscriptions();
		subs.setNode(node);
		payload.setSubscriptions(subs);
		
		blockingIQ(stanza, payload);
		
		List<org.jabber.protocol.pubsub.owner.Subscription> subList = ((org.jabber.protocol.pubsub.owner.Pubsub)payload).getSubscriptions().getSubscription();
		Map<IIdentity, SubscriptionState> returnMap = new HashMap<IIdentity, SubscriptionState>();
		for (org.jabber.protocol.pubsub.owner.Subscription s : subList)
			try {
				returnMap.put(endpoint.getIdManager().fromJid(s.getJid()), SubscriptionState.valueOf(s.getSubscription()));
			} catch (InvalidFormatException e) {
				LOG.warn("Unable to parse subscription JIDs",e);
			}
		return returnMap;
	}

	public Map<IIdentity, Affiliation> ownerGetAffiliations(
			IIdentity pubsubService, String node) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Affiliations affs = new Affiliations();
		affs.setNode(node);
		payload.setAffiliations(affs);
		
		blockingIQ(stanza, payload);
		
		List<org.jabber.protocol.pubsub.owner.Affiliation> affList = ((org.jabber.protocol.pubsub.owner.Pubsub)payload).getAffiliations().getAffiliation();
		Map<IIdentity, Affiliation> returnMap = new HashMap<IIdentity, Affiliation>();
		for (org.jabber.protocol.pubsub.owner.Affiliation a : affList)
			try {
				returnMap.put(endpoint.getIdManager().fromJid(a.getJid()), Affiliation.valueOf(a.getAffiliation()));
			} catch (InvalidFormatException e) {
				LOG.warn("Unable to parse affiliation JIDs",e);
			}
		
		return returnMap;
	}

	public void ownerSetSubscriptions(IIdentity pubsubService, String node,
			Map<IIdentity, SubscriptionState> subscriptions) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Subscriptions subs = new Subscriptions();
		subs.setNode(node);
		payload.setSubscriptions(subs);
		
		for (IIdentity subscriber : subscriptions.keySet()) {
			org.jabber.protocol.pubsub.owner.Subscription s = new org.jabber.protocol.pubsub.owner.Subscription();
			s.setJid(subscriber.getJid());
			s.setSubscription(subscriptions.get(subscriber).toString());
			subs.getSubscription().add(s);
		}
		
		blockingIQ(stanza, payload);
		
		// TODO error handling on multiple subscription changes
	}

	public void ownerSetAffiliations(IIdentity pubsubService, String node,
			Map<IIdentity, Affiliation> affiliations) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Affiliations affs = new Affiliations();
		affs.setNode(node);
		payload.setAffiliations(affs);
		
		for (IIdentity subscriber : affiliations.keySet()) {
			org.jabber.protocol.pubsub.owner.Affiliation a = new org.jabber.protocol.pubsub.owner.Affiliation();
			a.setJid(subscriber.getJid());
			a.setAffiliation(affiliations.get(subscriber).toString());
			affs.getAffiliation().add(a);
		}
		
		blockingIQ(stanza, payload);
		
		// TODO error handling on multiple affiliation changes
		
	}
	
	private IIdentity convertStringToIdentity(String jid) throws XMPPError {
		try {
			return endpoint.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			throw new XMPPError(StanzaError.jid_malformed, "Invalid JID: "+jid);
		}
	}
	
	private IIdentity localIdentity() {
		return endpoint.getIdManager().getThisNetworkNode();
	}
	
}
