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
 * This is the implementation of both the {@link PubsubService} service and of the {@link NamespaceExtension} interface.
 * It handles XEP-SOC1 related logic. Registers on XCCommunicationFrameworkBundle to receive staza elements of namespace
 * http://societies.org/community, and handles those requests. 
 * 
 */

package org.societies.comm.xmpp.pubsub.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;
import org.jabber.protocol.pubsub.Options;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.owner.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.comm.xmpp.pubsub.PubsubService;

// TODO
// no distinction between get and set... join and leave should be set and who should be get
// 

public class PubsubServiceRouter implements IFeatureServer {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubServiceRouter.class);

	private final static List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays.asList("http://jabber.org/protocol/pubsub",
   					"http://jabber.org/protocol/pubsub#errors",
   					"http://jabber.org/protocol/pubsub#owner",
   					"http://jabber.org/protocol/pubsub#event",
   					"jabber:x:data"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList(
					"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.owner",
					"org.jabber.protocol.pubsub.event",
					"jabber.x.data"));

	private ICommManager endpoint;
	private PubsubService impl;

	public PubsubServiceRouter(ICommManager endpoint) {
		init(endpoint, null);
	}
	
	private void init(ICommManager endpoint, SessionFactory sf) {
		this.endpoint = endpoint;
		if (sf==null)
			impl = new PubsubServiceImpl(endpoint);
		else
			impl = new PubsubServiceImpl(endpoint,sf);
		try {
			endpoint.register(this); // TODO unregister??
		} catch (CommunicationException e) {
			LOG.error(e.getMessage());
		}
	}

	public PubsubServiceRouter(ICommManager endpoint, SessionFactory sf) {
		init(endpoint, sf);
	}

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
		// do nothing
		// no use-case so far for community-received messages
	}
	
	// TODO sort out between get and set stanzas
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		return receiveQuery(stanza, payload);
	}

	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		return receiveQuery(stanza, payload);
	}

	public Object receiveQuery(Stanza stanza, Object payload) throws XMPPError {
		// all received IQs contain a either a normal or owner pubsub element
		if (payload.getClass().equals(Pubsub.class)) {
			// Subscriber and Publisher use cases; Owner Create use case
			Pubsub ps = (Pubsub) payload;

			if (ps.getSubscribe() != null) {
				if (ps.getOptions() != null) {
					// Subscribe and Configure Options
					return impl.subscriberSubscribeConfigure(stanza, ps);
				} else {
					// Subscribe
					return impl.subscriberSubscribe(stanza, ps);
				}
			}
			if (ps.getUnsubscribe() != null) {
				// Unsubscribe
				impl.subscriberUnsubscribe(stanza, ps);
				return null;
			}
			Options options = ps.getOptions();
			if (options != null) {
				if (options.getAny() != null) {
					// Options Form Submission
					return impl.subscriberOptionsSubmission(stanza, ps);
				} else {
					// Options Form Request
					return impl.subscriberOptionsRequest(stanza, ps);
				}
			}
			if (ps.getDefault() != null) {
				// Default Subscription Options Request
				return impl.subscriberDefaultOptions(stanza, ps);
			}
			if (ps.getItems() != null) {
				// Retrieve Items
				return impl.subscriberRetrieve(stanza, ps);
			}
			if (ps.getPublish() != null) {
				if (ps.getPublishOptions() != null) {
					// Publish with Options
					return impl.publisherPublishOptions(stanza, ps);
				} else {
					// Publish
					return impl.publisherPublish(stanza, ps);
				}
			}
			if (ps.getRetract() != null) {
				// Delete Published Item
				impl.publisherDelete(stanza, ps);
				return null;
			}
			if (ps.getCreate() != null) {
				if (ps.getConfigure() != null) {
					// Create and Configure Node
					return impl.ownerCreateConfigure(stanza, ps);
				} else {
					// Create Node
					return impl.ownerCreate(stanza, ps);
				}
			}
		}
		if (payload.getClass().equals(
				org.jabber.protocol.pubsub.owner.Pubsub.class)) {
			// Owner use cases
			org.jabber.protocol.pubsub.owner.Pubsub ops = (org.jabber.protocol.pubsub.owner.Pubsub) payload;

			Configure configure = ops.getConfigure();
			if (configure != null) {
				if (configure.getX() != null) {
					// Configure Form Submission
					return impl.ownerConfigureSubmission(stanza, ops);
				} else {
					// Configure Form Request
					return impl.ownerConfigureRequest(stanza, ops);
				}
			}
			if (ops.getDefault() != null) {
				// Default Configuration Request
				return impl.ownerDefaultConfiguration(stanza, ops);
			}
			if (ops.getDelete() != null) {
				// Delete Node or Delete and Redirect
				impl.ownerDelete(stanza, ops);
				return null;
			}
			if (ops.getPurge() != null) {
				// Purge Node Items
				impl.ownerPurgeItems(stanza, ops);
				return null;
			}
			if (ops.getSubscriptions() != null) {
				// List or Manage Subscriptions
				return impl.ownerSubscriptions(stanza, ops);
			}
			if (ops.getAffiliations() != null) {
				// List or Manage Affiliations
				return impl.ownerAffiliations(stanza, ops);
			}
		}
		throw new XMPPError(StanzaError.service_unavailable);
	}
}
