package org.societies.android.platform.pubsub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import main.java.org.societies.comms.Create;
import main.java.org.societies.comms.Delete;
import main.java.org.societies.comms.IIdentity;
import main.java.org.societies.comms.Item;
import main.java.org.societies.comms.Publish;
import main.java.org.societies.comms.Purge;
import main.java.org.societies.comms.Retract;
import main.java.org.societies.comms.Stanza;
import main.java.org.societies.comms.Subscribe;
import main.java.org.societies.comms.Subscription;
import main.java.org.societies.comms.Unsubscribe;

import org.societies.android.api.pubsub.Pubsub;
import org.societies.android.api.pubsub.SubscriptionParcelable;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.pubsub.interfaces.ISubscriber;
import org.xml.sax.SAXException;

import android.content.Context;

public class PubsubServiceBase implements Pubsub {
	private static final String LOG_TAG = PubsubServiceBase.class.getName();
	
	public static final int TIMEOUT = 10000;
	
	private final static String [] NAMESPACES = {"http://jabber.org/protocol/pubsub",
						   					"http://jabber.org/protocol/pubsub#errors",
						   					"http://jabber.org/protocol/pubsub#event",
						   					"http://jabber.org/protocol/pubsub#owner",
						   					"http://jabber.org/protocol/disco#items"};
	private static final String [] PACKAGES = {"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.event",
					"org.jabber.protocol.pubsub.owner"};
	
	private static final String [] ELEMENTS = {"pubsub", "event","query"};
	
	private ClientCommunicationMgr ccm;
	private Context androidContext;
	private boolean restrictBroadcast;
	
	public PubsubServiceBase (Context androidContext, ClientCommunicationMgr ccm, boolean restrictBroadcast) {
		this.ccm = ccm;
		this.androidContext = androidContext;
		this.restrictBroadcast = restrictBroadcast;
	}
	
	public String[] discoItems(String pubsubService, String node) {
		this.ccm.getItems(entity, node, callback)
		String id = this.ccm.getItems(convertStringToIdentity(pubsubService), node, this);
		Object response = waitForResponse(id);
		
		if (response!=null)
			return ((Entry<String, List<String>>)response).getValue();
		else
			return null;
	}

	public boolean ownerCreate(String pubsubService, String node) {
		Stanza stanza = new Stanza(convertStringToIdentity(pubsubService));
		Pubsub payload = new Pubsub();
		Create c = new Create();
		c.setNode(node);
		payload.setCreate(c);
				
		blockingIQ(stanza, payload);
	}

	public boolean ownerDelete(String pubsubService, String node) {
		Stanza stanza = new Stanza(convertStringToIdentity(pubsubService));
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Delete delete = new Delete();
		delete.setNode(node);
		payload.setDelete(delete);
		
		blockingIQ(stanza, payload);
	}

	public boolean ownerPurgeItems(String pubsubServiceJid, String node) {
		IIdentity pubsubService = convertStringToIdentity(pubsubServiceJid);
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Purge purge = new Purge();
		purge.setNode(node);
		payload.setPurge(purge);
		
		blockingIQ(stanza, payload);
	}

	public String publisherPublish(String pubsubService, String node, String itemId, String item) {
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

	public boolean publisherDelete(String pubsubServiceJid, String node, String itemId) {
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

	public SubscriptionParcelable subscriberSubscribe(String pubsubService, String node, long remoteCallID) {
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
		
		return new SubscriptionParcelable(subscription, this.ccm.getIdManager());
	}

	public boolean subscriberUnsubscribe(String pubsubService, String node, long remoteCallID) {
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
}
