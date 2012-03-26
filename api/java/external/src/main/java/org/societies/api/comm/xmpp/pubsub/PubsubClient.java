/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM ISRAEL
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
 * This is the interface of the service exposed by this OSGi bundle. It is meant to be used by other bundles to perform some
 * get information on the state of this bundle.
 * This bundle handles the logic described in XEP-SOC1 related to basic community membership management, and a service to
 * get the current community state can be requested.
 * 
 */

package org.societies.api.comm.xmpp.pubsub;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.pubsub.Affiliation;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;
import org.societies.api.identity.IIdentity;
import org.w3c.dom.Element;

public interface PubsubClient {
	
	/**
	 * Add the list of package names that will be used as payloads for pubsub events
	 * @param packageList List of package names
	 * @throws JAXBException
	 */
	public void addJaxbPackages(List<String> packageList) throws JAXBException;
	public List<String> discoItems(IIdentity pubsubService, String node) throws XMPPError, CommunicationException;
	// TODO remove subId from interface - it's not interesting for the API users, only for the client implementation
	// TODO disco info nodes
	// TODO reception of notifications from a 3rd party subscription

	/**
	 * Subscribe to a pubsub node and provide the subscriber callback object
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @param subscriber
	 * @return
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public Subscription subscriberSubscribe(IIdentity pubsubService, String node, Subscriber subscriber) throws XMPPError, CommunicationException;

	/**
	 * Unsubscribe from an pubsub node of this identity
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @param subscriber
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public void subscriberUnsubscribe(IIdentity pubsubService, String node, Subscriber subscriber) throws XMPPError, CommunicationException;

	/**
	 * Retrieve the last event that was published to this node
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @param subId 
	 * @return
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public List<Element> subscriberRetrieveLast(IIdentity pubsubService, String node, String subId) throws XMPPError, CommunicationException;
	public List<Element> subscriberRetrieveSpecific(IIdentity pubsubService, String node, String subId, List<String> itemIdList) throws XMPPError, CommunicationException;
	
	/**
	 * Publishes an event for this identity, on this node of unique itemID and content of item
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @param itemId unique id for this publish event
	 * @param item Payload object for this event. Must map to the JaxbMapping provided earlier
	 * @return
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public String publisherPublish(IIdentity pubsubService, String node, String itemId, Object item) throws XMPPError, CommunicationException;

	/**
	 * Remove an event for this node of provided itemID
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @param itemId unique id of the published event
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public void publisherDelete(IIdentity pubsubService, String node, String itemId) throws XMPPError, CommunicationException;
	
	/**
	 * Creates a pubsub node for this identity using the provided node name ("topic")
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public void ownerCreate(IIdentity pubsubService, String node) throws XMPPError, CommunicationException;
	
	/**
	 * Deletes the pubsub node for this identity and node name
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public void ownerDelete(IIdentity pubsubService, String node) throws XMPPError, CommunicationException;
	
	/**
	 * Deletes all the published events for this identity on this node
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public void ownerPurgeItems(IIdentity pubsubService, String node) throws XMPPError, CommunicationException;
	
	/**
	 * Returns the list of identities of users who are subscribed to this pubsub node
	 * @param pubsubService Identity of the pubsub service
	 * @param node Pubsub event node
	 * @return
	 * @throws XMPPError
	 * @throws CommunicationException
	 */
	public Map<IIdentity, SubscriptionState> ownerGetSubscriptions(IIdentity pubsubService, String node) throws XMPPError, CommunicationException;
	
	public Map<IIdentity, Affiliation> ownerGetAffiliations(IIdentity pubsubService, String node) throws XMPPError, CommunicationException;
	public void ownerSetSubscriptions(IIdentity pubsubService, String node, Map<IIdentity, SubscriptionState> subscriptions) throws XMPPError, CommunicationException;
	public void ownerSetAffiliations(IIdentity pubsubService, String node, Map<IIdentity, Affiliation> affiliations) throws XMPPError, CommunicationException;
}
