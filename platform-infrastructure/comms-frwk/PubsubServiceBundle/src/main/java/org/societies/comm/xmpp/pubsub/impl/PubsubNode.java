package org.societies.comm.xmpp.pubsub.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.HostedNode;
import org.societies.api.identity.IIdentity;

// TODO collection node support
public class PubsubNode extends HostedNode {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubNode.class);
	
	private IIdentity owner;
	// configurations
	private Map<String, IIdentity> subscriptionsById;
	private Map<IIdentity, List<String>> subscriptionsByUser;
	// subscriberoptions
	private Stack<String> itemIdByOrder;
	private Map<String, Object> itemsById;
	private Map<String, String> publisherByItemId;
	// itempublishoptions??? these suck!
	
	public PubsubNode(IIdentity owner, String nodeId) {
		super(nodeId, null); // TODO collection nodes
		this.owner = owner;
		subscriptionsById = new HashMap<String, IIdentity>();
		subscriptionsByUser = new HashMap<IIdentity, List<String>>();
		itemIdByOrder = new Stack<String>();
		itemsById = new HashMap<String, Object>();
		publisherByItemId = new HashMap<String, String>();
	}

	public String newSubscription(IIdentity subscriber) {
		// Generate subId
		String subId = UUID.randomUUID().toString();
		while (subscriptionsById.containsKey(subId))
			subId = UUID.randomUUID().toString();
		
		// Subscribe
		subscriptionsById.put(subId, subscriber);
		List<String> subIdList = subscriptionsByUser.get(subscriber);
		if (subIdList==null) {
			subIdList = new ArrayList<String>();
			subscriptionsByUser.put(subscriber, subIdList);
		}
		subIdList.add(subId);
		return subId;
	}
	
	public List<String> getSubscriptions(IIdentity subscriber) {
		return subscriptionsByUser.get(subscriber);
	}
	
	public Collection<IIdentity> getSubscribers() {
		return subscriptionsByUser.keySet();
	}

	public void unsubscribe(String string) {
		IIdentity subscriber = subscriptionsById.get(string);
		subscriptionsById.remove(string);
		subscriptionsByUser.remove(subscriber);
	}

	public String publishItem(String itemId, Object itemObject, String publisher) {
		if (itemId!=null) {
			// Check for existing itemId and delete old item
			Object oldItem = itemsById.get(itemId);
			if (oldItem!=null) {
				itemsById.remove(itemId);
				itemIdByOrder.remove(itemId); // TODO pop the changed item to the top of the stack?
			}
		}
		else {
			// Generate itemId
			itemId = UUID.randomUUID().toString();
			while (itemsById.containsKey(itemId))
				itemId = UUID.randomUUID().toString();
		}
		
		// Publish
		itemsById.put(itemId, itemObject);
		itemIdByOrder.push(itemId);
		publisherByItemId.put(itemId, publisher);
		return itemId;
	}

	public Object getItemPayload(String itemId) {
		return itemsById.get(itemId);
	}

	public List<String> getItemIds() {
		return Collections.unmodifiableList(itemIdByOrder);
	}

	public void removeItem(String itemId) {
		itemsById.remove(itemId);
		itemIdByOrder.remove(itemId);
	}

	public void purge() {
		itemIdByOrder = new Stack<String>();
		itemsById = new HashMap<String, Object>();
		publisherByItemId = new HashMap<String, String>();
	}

	public IIdentity getOwner() {
		return owner;
	}
}
