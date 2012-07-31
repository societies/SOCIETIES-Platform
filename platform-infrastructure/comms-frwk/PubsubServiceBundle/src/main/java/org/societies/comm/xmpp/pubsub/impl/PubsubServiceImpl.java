/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author Joao M. Goncalves (PTIN)
 * 
 * TODO
 */

package org.societies.comm.xmpp.pubsub.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jabber.protocol.pubsub.Create;
import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Items;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Subscription;
import org.jabber.protocol.pubsub.errors.Unsupported;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.pubsub.PubsubService;

// TODO
public class PubsubServiceImpl implements PubsubService {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubServiceImpl.class);
	
	// PubSub Errors
	private static final Object ERROR_SUBID_REQUIRED = null;
	private static final Object ERROR_NOT_SUBSCRIBED = null;
	private static final Object ERROR_INVALID_SUBID = null;
	private static final Object ERROR_NODEID_REQUIRED = null;
	private static final Object ERROR_ITEM_REQUIRED = null;
	private static final Object ERROR_INVALID_JID = null;
	/*
	static {
		org.jabber.protocol.pubsub.errors.ObjectFactory errorFactory = new org.jabber.protocol.pubsub.errors.ObjectFactory();
		ERROR_SUBID_REQUIRED = errorFactory.createSubidRequired("");
		ERROR_NOT_SUBSCRIBED = errorFactory.createNotSubscribed("");
		ERROR_INVALID_SUBID = errorFactory.createInvalidSubid("");
		ERROR_NODEID_REQUIRED = errorFactory.createNodeidRequired("");
		ERROR_ITEM_REQUIRED = errorFactory.createItemRequired("");
		ERROR_INVALID_JID = errorFactory.createInvalidJid("");
	}
	*/
	// PubSub Constants
	private static final String SUBSCRIPTION_SUBSCRIBED = "subscribed";
	private static final String SUBSCRIPTION_UNCONFIGURED = "unconfigured"; // TODO
	private static final String SUBSCRIPTION_NONE = "none";
	private static final String AFFILIATION_OWNER = "owner";
	private static final String AFFILIATION_MEMBER = "member";
	private static final String AFFILIATION_PUBLISHER = "publisher";
	private static final String AFFILIATION_PUBLISH_ONLY = "publish-only";
	private static final String AFFILIATION_OUTCAST = "outcast";
	
	// Fields
	private Map<String, PubsubNode> nodes;
	private Map<String, String> redirectedNodes;
	private PubsubEventSender pes;
	private List<IIdentity> admins;
	private ICommManager endpoint;
	private IIdentityManager idm;
	
	public PubsubServiceImpl(ICommManager endpoint) {
		nodes = new HashMap<String, PubsubNode>();
		redirectedNodes = new HashMap<String, String>();
		pes = new PubsubEventSender(endpoint);
		this.endpoint = endpoint;
		admins = new ArrayList<IIdentity>();
		idm = endpoint.getIdManager();
	}

	@Override
	public Pubsub subscriberSubscribe(Stanza stanza, Pubsub payload) throws XMPPError {
		IIdentity sender = stanza.getFrom();
		IIdentity subscriber;
		try {
			subscriber = idm.fromJid(payload.getSubscribe().getJid());
		} catch (InvalidFormatException e) {
			throw new XMPPError(StanzaError.bad_request, null, ERROR_INVALID_JID);
		}
		String nodeId = payload.getSubscribe().getNode();
		// TODO "The <subscribe/> element SHOULD possess a 'node' attribute"... what happens when it doesn't?
		
		// 6.1.3.1 JIDs Do Not Match (match sender and subscriber)
		if (!sender.equals(subscriber) && !admins.contains(sender))
			throw new XMPPError(StanzaError.bad_request, null, ERROR_INVALID_JID);
		
		// TODO Access Control
		
		PubsubNode node = nodes.get(nodeId);
		
		if (node==null) {
			String redirectUri = redirectedNodes.get(nodeId);
			// 6.1.3.12 Node Does Not Exist
			if (redirectUri==null)
				throw new XMPPError(StanzaError.item_not_found);
			// 6.1.3.11 Node Has Moved
			else
				throw new XMPPError(StanzaError.gone,redirectUri,null);
		}
		
		// New Subscription
		String subId = node.newSubscription(subscriber);
		
		// Build success response
		Pubsub response = new Pubsub();
		Subscription subs = new Subscription();
		subs.setJid(subscriber.getJid());
		subs.setNode(nodeId);
		subs.setSubid(subId);
		subs.setSubscription(SUBSCRIPTION_SUBSCRIBED);
		response.setSubscription(subs);
		return response;
	}

	@Override
	public void subscriberUnsubscribe(Stanza stanza, Pubsub payload) throws XMPPError {
		IIdentity sender = stanza.getFrom();
		IIdentity subscriber;
		try {
			subscriber = idm.fromJid(payload.getUnsubscribe().getJid());
		} catch (InvalidFormatException e) {
			throw new XMPPError(StanzaError.bad_request, null, ERROR_INVALID_JID);
		}
		String nodeId = payload.getUnsubscribe().getNode();
		String subId = payload.getUnsubscribe().getSubid();
		
		// 6.2.3.3 Insufficient Privileges (match sender and subscriber)
		if (!sender.equals(subscriber) && !admins.contains(sender))
			throw new XMPPError(StanzaError.forbidden);
		
		PubsubNode node = nodes.get(nodeId);
		
		// 6.2.3.4 Node Does Not Exist
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found);
		
		List<String> subIdList = node.getSubscriptions(subscriber);
		
		// 6.2.3.2 No Such Subscriber
		if (subIdList==null)
			throw new XMPPError(StanzaError.unexpected_request, null, ERROR_NOT_SUBSCRIBED);
		
		// 6.2.3.1 No Subscription ID
		if (subIdList.size()>1 && subId==null)
			throw new XMPPError(StanzaError.bad_request, null, ERROR_SUBID_REQUIRED); 
		
		if (subId!=null) {
			// 6.2.3.5 Bad Subscription ID
			if (!subIdList.contains(subId))
				throw new XMPPError(StanzaError.unexpected_request, null, ERROR_INVALID_SUBID); 
			
			// Unsubscribe
			node.unsubscribe(subId);
		}
		else
			node.unsubscribe(subIdList.get(0));
	}

	@Override
	public Pubsub subscriberOptionsRequest(Stanza stanza, Pubsub payload) throws XMPPError {
		// TODO 6.3
		Unsupported u = new Unsupported();
		u.setFeature("subscription-options");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public Pubsub subscriberOptionsSubmission(Stanza stanza, Pubsub payload) throws XMPPError {
		// TODO 6.3
		Unsupported u = new Unsupported();
		u.setFeature("subscription-options");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public Pubsub subscriberSubscribeConfigure(Stanza stanza, Pubsub payload) throws XMPPError {
		// TODO 6.3
		Unsupported u = new Unsupported();
		u.setFeature("subscription-options");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public Pubsub subscriberDefaultOptions(Stanza stanza, Pubsub payload) throws XMPPError {
		// TODO 6.4
		Unsupported u = new Unsupported();
		u.setFeature("subscription-options");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public Pubsub subscriberRetrieve(Stanza stanza, Pubsub payload) throws XMPPError {
		IIdentity sender = stanza.getFrom();
		String nodeId = payload.getItems().getNode();
		String subId = payload.getItems().getSubid();
		List<Item> itemList = payload.getItems().getItem();
		BigInteger maxItems = payload.getItems().getMaxItems();
		
		PubsubNode node = nodes.get(nodeId);
		
		// 6.5.9.11 Node Does Not Exist
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found);
		
		List<String> subIdList = node.getSubscriptions(sender);
		
		// 6.5.9.3 Entity Not Subscribed
		if (subIdList==null)
			throw new XMPPError(StanzaError.unexpected_request, null, ERROR_NOT_SUBSCRIBED);
		
		// 6.5.9.1 Subscription ID Required
		if (subIdList.size()>1 && subId==null)
			throw new XMPPError(StanzaError.bad_request, null, ERROR_SUBID_REQUIRED);
		
		if (subId!=null) {
			// 6.5.9.2 Invalid Subscription ID
			if (!subIdList.contains(subId))
				throw new XMPPError(StanzaError.unexpected_request, null, ERROR_INVALID_SUBID);
		}
		else
			subId = subIdList.get(0);
		
		// TODO Access Control
		
		// Retrieve
		Items responseItems = new Items();
		List<Item> responseItemList = responseItems.getItem();
		if (itemList!=null && itemList.size()>0) {
			// Get specific items
			for (Item i : itemList) {
				i.setAny(node.getItemPayload(i.getId()));
				responseItemList.add(i);
			}
		}
		else {
			// Get newest items
			// TODO 6.5.4 Returning Some Items
			for (String itemId : node.getItemIds()) {
				Item i = new Item();
				i.setId(itemId);
				Object itemPayload = node.getItemPayload(itemId);
				i.setAny(itemPayload);
				responseItemList.add(i);
				if (maxItems!=null && responseItemList.size()==maxItems.intValue())
					break;
			}
		}
		
		// Build response
		Pubsub response = new Pubsub();
		responseItems.setNode(nodeId);
		response.setItems(responseItems);
		return response;
	}

	@Override
	public Pubsub publisherPublish(Stanza stanza, Pubsub payload) throws XMPPError {
		String nodeId = payload.getPublish().getNode();
		Item item = payload.getPublish().getItem();
		String sender = stanza.getFrom().getJid();
		
		PubsubNode node = nodes.get(nodeId);
		
		// 7.1.3.3 Node Does Not Exist or http://jabber.org/protocol/pubsub#auto-create
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found); // TODO http://jabber.org/protocol/pubsub#auto-create
		
		if (item==null) {
			throw new XMPPError(StanzaError.feature_not_implemented); // TODO support for transient nodes (and itemless notifications)
		}
		else {
			// 7.1.3.5 Bad Payload
			//TODO If the <item/> element contains more than one payload element or the namespace of the root payload element does not match the configured namespace for the node
			
			// Publish and Update Item ID for Response			
			String itemId = node.publishItem(item.getId(),item.getAny(),sender);
			item.setId(itemId);
			
			// Build Notifications
			org.jabber.protocol.pubsub.event.Event event = new org.jabber.protocol.pubsub.event.Event();
			org.jabber.protocol.pubsub.event.Items eventItems = new org.jabber.protocol.pubsub.event.Items();
			org.jabber.protocol.pubsub.event.Item eventItem = new org.jabber.protocol.pubsub.event.Item();
			eventItem.setId(itemId);
			eventItem.setAny(item.getAny());
			eventItems.setNode(nodeId);
			eventItems.getItem().add(eventItem);
			event.setItems(eventItems);
			pes.sendEvent(node.getSubscribers(), event); // TODO 7.1.2.2 Notification Without Payload
		}

		// Build Response
		item.setAny(null);
		return payload;
	}

	@Override
	public Pubsub publisherPublishOptions(Stanza stanza, Pubsub payload) throws XMPPError {
		// TODO 7.1.5
		Unsupported u = new Unsupported();
		u.setFeature("publish-options");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public void publisherDelete(Stanza stanza, Pubsub payload) throws XMPPError {
		String nodeId = payload.getRetract().getNode();
		List<Item> item = payload.getRetract().getItem();
		Boolean notify = payload.getRetract().isNotify();
		
		// 7.2.3.3 NodeID Required
		if (nodeId==null)
			throw new XMPPError(StanzaError.bad_request, null, ERROR_NODEID_REQUIRED);
		
		PubsubNode node = nodes.get(nodeId);
		
		// 7.2.3.2 Node Does Not Exist
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found);
		
		// 7.2.3.4 Item or ItemID Required
		if (item==null || item.size()!=1 || item.get(0).getId()==null)
			throw new XMPPError(StanzaError.bad_request, null, ERROR_ITEM_REQUIRED);
		
		// TODO Access model
		
		// Remove Item
		String itemId = item.get(0).getId();
		node.removeItem(itemId);
		
		// 7.2.2.1 Delete And Notify
		if (notify!=null && notify) {
			org.jabber.protocol.pubsub.event.Items eventItems = new org.jabber.protocol.pubsub.event.Items();
			org.jabber.protocol.pubsub.event.Retract retractEvent = new org.jabber.protocol.pubsub.event.Retract();
			retractEvent.setId(itemId);
			eventItems.setNode(nodeId);
			eventItems.getRetract().add(retractEvent);
			pes.sendEvent(node.getSubscribers(), eventItems);
			// TODO 7.2.2.2 Inclusion of Subscription ID
		}
	}

	@Override
	public Pubsub ownerCreate(Stanza stanza, Pubsub payload) throws XMPPError {
		// Support for Support for http://jabber.org/protocol/pubsub#create-nodes
		IIdentity owner = stanza.getFrom();
		String nodeId = payload.getCreate().getNode();
		
		// TODO access model
		
		if (nodeId==null) {
			// Support for http://jabber.org/protocol/pubsub#instant-nodes
			nodeId = UUID.randomUUID().toString();
			while (nodes.containsKey(nodeId)) {
				nodeId = UUID.randomUUID().toString();
			}
		}
		else {
			// Example 128. NodeID already exists
			if (nodes.keySet().contains(nodeId))
				throw new XMPPError(StanzaError.conflict, "Node: '" + nodeId + "' already exists");
		}
		
		// Create Node
		PubsubNode newNode = new PubsubNode(owner, nodeId);
		nodes.put(nodeId, newNode);
		endpoint.addRootNode(newNode);
		
		// Build success response
		Pubsub response = new Pubsub();
		Create value = new Create();
		value.setNode(nodeId);
		response.setCreate(value);
		return response;
	}

	@Override
	public Pubsub ownerCreateConfigure(Stanza stanza, Pubsub payload) throws XMPPError {
		// TODO 8.2
		Unsupported u = new Unsupported();
		u.setFeature("config-node");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public org.jabber.protocol.pubsub.owner.Pubsub ownerConfigureRequest(Stanza stanza,
			org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError {
		// TODO 8.2
		Unsupported u = new Unsupported();
		u.setFeature("config-node");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public org.jabber.protocol.pubsub.owner.Pubsub ownerConfigureSubmission(Stanza stanza,
			org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError {
		// TODO 8.2
		Unsupported u = new Unsupported();
		u.setFeature("config-node");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	@Override
	public org.jabber.protocol.pubsub.owner.Pubsub ownerDefaultConfiguration(Stanza stanza,
			org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError {
		// TODO 8.2
		Unsupported u = new Unsupported();
		u.setFeature("config-node");
		throw new XMPPError(StanzaError.feature_not_implemented, null, u);
	}

	// 8.4 Delete a Node
	@Override
	public void ownerDelete(Stanza stanza,
			org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError {
		String nodeId = payload.getDelete().getNode();
		PubsubNode node = nodes.get(nodeId);
		
		// 8.4.3.2 Node Does Not Exist
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found);
		
		// 8.4.3.1 Insufficient Privileges
		IIdentity sender = stanza.getFrom();
		if (!node.getOwner().equals(sender))
			throw new XMPPError(StanzaError.forbidden);
		
		// Remove Node
		nodes.remove(nodeId);
		endpoint.removeRootNode(node);
		
		// Example 156. Owner deletes a node with redirection
		String redirectUri = null;
		if (payload.getDelete().getRedirect()!=null) {
			redirectUri = payload.getDelete().getRedirect().getUri();
			redirectedNodes.put(nodeId, redirectUri);
		}
		
		// Example 158. Subscribers are notified of node deletion
		org.jabber.protocol.pubsub.event.Delete deleteEvent = new org.jabber.protocol.pubsub.event.Delete();
		deleteEvent.setNode(nodeId);
		if (redirectUri!=null) {
			org.jabber.protocol.pubsub.event.Redirect redirectEvent = new org.jabber.protocol.pubsub.event.Redirect();
			redirectEvent.setUri(redirectUri);
			deleteEvent.setRedirect(redirectEvent);
		}
		pes.sendEvent(node.getSubscribers(), deleteEvent);
	}

	// 8.5 Purge All Node Items
	@Override
	public void ownerPurgeItems(Stanza stanza,
			org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError {
		String nodeId = payload.getPurge().getNode();
		PubsubNode node = nodes.get(nodeId);
		
		// 8.5.3.4 Node Does Not Exist
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found);
		
		// 8.5.3.2 Insufficient Privileges
		IIdentity sender = stanza.getFrom();
		if (!node.getOwner().equals(sender))
			throw new XMPPError(StanzaError.forbidden);
		
		// Purge Items
		node.purge();
		
		// Example 163. Subscribers are notified of node purge
		org.jabber.protocol.pubsub.event.Purge purgeEvent = new org.jabber.protocol.pubsub.event.Purge();
		purgeEvent.setNode(nodeId);
		pes.sendEvent(node.getSubscribers(), purgeEvent);
	}

	// 8.8 Manage Subscriptions
	@Override
	public org.jabber.protocol.pubsub.owner.Pubsub ownerSubscriptions(Stanza stanza,
			org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError {
		String nodeId = payload.getSubscriptions().getNode();
		PubsubNode node = nodes.get(nodeId);
		
		// Example 186. Node does not exist
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found);
		
		// Example 185. Entity is not an owner
		IIdentity sender = stanza.getFrom();
		if (!node.getOwner().equals(sender))
			throw new XMPPError(StanzaError.forbidden);
		
		if (payload.getSubscriptions().getSubscription().size()>0) {
			// 8.8.2 Modify Subscriptions
			List<org.jabber.protocol.pubsub.owner.Subscription> subscriptions = new ArrayList<org.jabber.protocol.pubsub.owner.Subscription>(payload.getSubscriptions().getSubscription());
			for (org.jabber.protocol.pubsub.owner.Subscription s : payload.getSubscriptions().getSubscription()) {
				List<String> subs;
				try {
					subs = node.getSubscriptions(idm.fromJid(s.getJid()));
				} catch (InvalidFormatException e) {
					throw new XMPPError(StanzaError.bad_request, null, ERROR_INVALID_JID);
				}
				if (s.getSubscription().equals(SUBSCRIPTION_SUBSCRIBED)) {
					if (subs==null) {
						node.newSubscription(sender);
						subscriptions.remove(s);
					}
				}
				if (s.getSubscription().equals(SUBSCRIPTION_NONE)) {
					if (subs!=null) {
						if (subs.size()==1) {
							node.unsubscribe(subs.get(0));
							subscriptions.remove(s);
						}
						else {
							if (s.getSubid()!=null && subs.contains(s.getSubid())) {
								node.unsubscribe(s.getSubid());
								subscriptions.remove(s);
							}
						}
					}
				}
			}
			
			// Empty success response
			if (subscriptions.size()==0)
				return null;
			// Error
			else
				return null; // TODO
		}
		else {
			// 8.8.1 Retrieve Subscriptions List
			for (IIdentity subscriber : node.getSubscribers()) {
				List<String> subscriberSubscriptions = node.getSubscriptions(subscriber);
				if (subscriberSubscriptions.size()==1) {
					org.jabber.protocol.pubsub.owner.Subscription sub = new org.jabber.protocol.pubsub.owner.Subscription();
					sub.setJid(subscriber.getJid());
					sub.setSubscription(SUBSCRIPTION_SUBSCRIBED);
					payload.getSubscriptions().getSubscription().add(sub);
				}
				else {
					for (String subId : subscriberSubscriptions) {
						org.jabber.protocol.pubsub.owner.Subscription sub = new org.jabber.protocol.pubsub.owner.Subscription();
						sub.setJid(subscriber.getJid());
						sub.setSubscription(SUBSCRIPTION_SUBSCRIBED);
						sub.setSubid(subId);
						payload.getSubscriptions().getSubscription().add(sub);
					}
				}
			}
			return payload;
		}
	}

	// 8.9 Manage Affiliations
	@Override
	public org.jabber.protocol.pubsub.owner.Pubsub ownerAffiliations(Stanza stanza,
			org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError {
		String nodeId = payload.getAffiliations().getNode();
		PubsubNode node = nodes.get(nodeId);
		
		// Example 205. Node does not exist
		if (node==null)
			throw new XMPPError(StanzaError.item_not_found);
		
		// Example 204. Entity is not an owner
		IIdentity sender = stanza.getFrom();
		if (!node.getOwner().equals(sender))
			throw new XMPPError(StanzaError.forbidden);
		
		
		if (payload.getAffiliations().getAffiliation().size()>0) {
			// 8.9.2 Modify Affiliation
			// TODO
		}
		else {
			// 8.9.1 Retrieve Affiliations List
			// TODO
		}
		return null;
	}
	
	
}
