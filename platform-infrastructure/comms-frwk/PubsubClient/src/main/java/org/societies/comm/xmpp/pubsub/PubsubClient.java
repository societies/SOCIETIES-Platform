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

package org.societies.comm.xmpp.pubsub;

import java.util.List;
import java.util.Map;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.w3c.dom.Element;

public interface PubsubClient {
	
	public List<String> discoItems(Identity pubsubService, String node) throws XMPPError, CommunicationException;
	// TODO remove subId from interface - it's not interesting for the API users, only for the client implementation
	// TODO disco info nodes
	// TODO reception of notifications from a 3rd party subscription
	public Subscription subscriberSubscribe(Identity pubsubService, String node, Subscriber subscriber) throws XMPPError, CommunicationException;
//	public String subscriberSubscribe(Identity pubsubService, String node, Identity subscriber) throws XMPPError, CommunicationException;
	public void subscriberUnsubscribe(Identity pubsubService, String node, Subscriber subscriber) throws XMPPError, CommunicationException;
//	public Pubsub subscriberOptionsRequest(Identity pubsubService) throws XMPPError, CommunicationException;
//	public Pubsub subscriberOptionsSubmission(Identity pubsubService) throws XMPPError, CommunicationException;
//	public Pubsub subscriberSubscribeConfigure(Identity pubsubService) throws XMPPError, CommunicationException;
//	public Pubsub subscriberDefaultOptions(Identity pubsubService) throws XMPPError, CommunicationException;
	public List<Element> subscriberRetrieveLast(Identity pubsubService, String node, String subId) throws XMPPError, CommunicationException;
	public List<Element> subscriberRetrieveSpecific(Identity pubsubService, String node, String subId, List<String> itemIdList) throws XMPPError, CommunicationException;
	public String publisherPublish(Identity pubsubService, String node, String itemId, Element item) throws XMPPError, CommunicationException;
//	public Pubsub publisherPublishOptions(Stanza stanza, Pubsub payload) throws XMPPError, CommunicationException;
	public void publisherDelete(Identity pubsubService, String node, String itemId) throws XMPPError, CommunicationException;
	public void ownerCreate(Identity pubsubService, String node) throws XMPPError, CommunicationException;
//	public Pubsub ownerCreateConfigure(Stanza stanza, Pubsub payload) throws XMPPError, CommunicationException;
//	public org.jabber.protocol.pubsub.owner.Pubsub ownerConfigureRequest(Stanza stanza, org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError, CommunicationException;
//	public org.jabber.protocol.pubsub.owner.Pubsub ownerConfigureSubmission(Stanza stanza, org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError, CommunicationException;
//	public org.jabber.protocol.pubsub.owner.Pubsub ownerDefaultConfiguration(Stanza stanza, org.jabber.protocol.pubsub.owner.Pubsub payload) throws XMPPError, CommunicationException;
	public void ownerDelete(Identity pubsubService, String node) throws XMPPError, CommunicationException;
	public void ownerPurgeItems(Identity pubsubService, String node) throws XMPPError, CommunicationException;
	public Map<Identity, SubscriptionState> ownerGetSubscriptions(Identity pubsubService, String node) throws XMPPError, CommunicationException;
	public Map<Identity, Affiliation> ownerGetAffiliations(Identity pubsubService, String node) throws XMPPError, CommunicationException;
	public void ownerSetSubscriptions(Identity pubsubService, String node, Map<Identity, SubscriptionState> subscriptions) throws XMPPError, CommunicationException;
	public void ownerSetAffiliations(Identity pubsubService, String node, Map<Identity, Affiliation> affiliations) throws XMPPError, CommunicationException;
}
