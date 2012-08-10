/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package org.societies.context.event.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.event.CtxEvent;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.event.api.CtxEventMgrException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Lazy;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ICtxEventMgr} interface.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.4
 */
@Service("ctxEventMgr")
@Lazy(false)
public class CtxEventMgr implements ICtxEventMgr, BundleContextAware {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxEventMgr.class);
	
	private static final String EVENT_ID_PROPERTY_KEY = "id";
	
	private static final List<String> EVENT_SCHEMA_PACKAGES = 
			Collections.unmodifiableList(Arrays.asList("org.societies.api.schema.context.model"));
			
	/** The OSGi EventAdmin service. */
	@Autowired(required=true)
	private EventAdmin eventAdmin;
	
	/** The PubsubClient service reference. */
	private PubsubClient pubsubClient;
	
	/** The owner IIdentity of the pubsub service */
	private IIdentity pubsubId;
	
	private IIdentityManager idMgr;
	
	/** The OSGi bundle context. */
	private BundleContext bundleContext;
	
	@Autowired(required=true)
	CtxEventMgr(PubsubClient pubsubClient, ICommManager commMgr) 
			throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.pubsubClient = pubsubClient;
		this.idMgr = commMgr.getIdManager();
		try {
			this.pubsubId = this.idMgr.getThisNetworkNode();
			this.pubsubClient.addJaxbPackages(EVENT_SCHEMA_PACKAGES);
			this.pubsubClient.ownerCreate(this.pubsubId, CtxChangeEventTopic.CREATED);
			this.pubsubClient.ownerCreate(this.pubsubId, CtxChangeEventTopic.UPDATED);
			this.pubsubClient.ownerCreate(this.pubsubId, CtxChangeEventTopic.MODIFIED);
			this.pubsubClient.ownerCreate(this.pubsubId, CtxChangeEventTopic.REMOVED);
		} catch (Exception e) {
			
			LOG.error(this.getClass() + " could not be instantiated: "
					+ e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#post(org.societies.api.context.event.CtxEvent, java.lang.String[], org.societies.context.api.event.CtxEventScope)
	 */
	@Override
	public void post(final CtxEvent event, final String[] topics,
			final CtxEventScope scope) throws CtxException {
		
		if (event == null)
			throw new NullPointerException("event can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		
		if (event instanceof CtxChangeEvent) {
			
			switch (scope) {

			case LOCAL:
				this.postLocalChangeEvent((CtxChangeEvent) event, topics);
				break;
			case INTRA_CSS:
				// TODO Handle intra-CSS event publishing
				break;
			case INTER_CSS:
				this.postRemoteChangeEvent((CtxChangeEvent) event, topics);
				break;
			case BROADCAST:
				this.postLocalChangeEvent((CtxChangeEvent) event, topics);
				this.postRemoteChangeEvent((CtxChangeEvent) event, topics);
				break;	
			default:
				throw new CtxEventMgrException("Cannot send event to topics "
						+ Arrays.toString(topics) 
						+ ": Unsupported CtxEventScope: " + scope);
			}
		} else {
			throw new CtxEventMgrException("Cannot send event to topics "
					+ Arrays.toString(topics) 
					+ ": Unsupported CtxEvent implementation");
		}
	}
	
	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#registerChangeListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final CtxIdentifier ctxId) throws CtxException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");
		
		try {
			final IIdentity pubsubId = this.idMgr.fromJid(ctxId.getOwnerId());
			if (this.idMgr.isMine(pubsubId))
				this.registerLocalChangeListener(listener, topics, ctxId);
			else
				this.registerRemoteChangeListener(pubsubId, listener, topics, ctxId);
		} catch (InvalidFormatException ife) {
			
			throw new CtxEventMgrException("Could not register context change event listener: "
					+ "ctxId is not a valid IIdentity: " + ife.getLocalizedMessage(), ife);
		}
	}

	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#unregisterChangeListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final CtxIdentifier ctxId) throws CtxException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");
		
		// TODO Auto-generated method stub
	}

	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#registerChangeListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final CtxEntityIdentifier scope, 
			final String attrType) throws CtxException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		
		try {
			final IIdentity pubsubId = this.idMgr.fromJid(scope.getOwnerId());
			if (this.idMgr.isMine(pubsubId))
				this.registerLocalChangeListener(listener, topics, scope, attrType);
			else
				this.registerRemoteChangeListener(pubsubId, listener, topics, scope, attrType);
		} catch (InvalidFormatException ife) {
			
			throw new CtxEventMgrException("Could not register context change event listener: "
					+ "ctxId is not a valid IIdentity: " + ife.getLocalizedMessage(), ife);
		}
	}

	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#unregisterChangeListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final CtxEntityIdentifier scope, 
			final String attrType) throws CtxException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		
		// TODO Auto-generated method stub
	}
	
	/*
	 * @see org.springframework.osgi.context.BundleContextAware#setBundleContext(org.osgi.framework.BundleContext)
	 */
	@Override
	public void setBundleContext(BundleContext bundleContext) {

		this.bundleContext = bundleContext;
	}
	
	private void postLocalChangeEvent(CtxChangeEvent event, String[] topics) 
			throws CtxEventMgrException {
		
		for (int i = 0; i < topics.length; ++i) {
			
			if (this.eventAdmin == null)
				throw new CtxEventMgrException("Could not send local context change event to topic '"
						+ topics[i] + "': OSGi EventAdmin service is not available");
		
			final Map<String, Object> props = new HashMap<String, Object>();
			props.put(EVENT_ID_PROPERTY_KEY, event.getId().toString());
			if (LOG.isDebugEnabled()) 
				LOG.debug("Sending local context change event to topic '" + topics[i] + "'"
						+ " with properties '" + props + "'");
			this.eventAdmin.postEvent(new Event(topics[i], props));
		}
	}
	
	private void postRemoteChangeEvent(CtxChangeEvent event, String[] topics) 
			throws CtxEventMgrException {
		
		final String itemId = event.getId().toString();
		final CtxIdentifierBean eventBean = 
				CtxModelBeanTranslator.getInstance().fromCtxIdentifier(event.getId());
		for (int i = 0; i < topics.length; ++i) {
			
			if (this.pubsubClient == null)
				throw new CtxEventMgrException("Could not send remote context change event to topic '"
						+ topics[i] + "': PubsubClient service is not available");
		
			if (LOG.isDebugEnabled()) 
				LOG.debug("Sending remote context change event to topic '" + topics[i] + "'"
						+ " with itemId '" + itemId + "'");
			try {
				this.pubsubClient.publisherPublish(this.pubsubId, topics[i], 
						itemId, eventBean);
			} catch (Exception e) {
				
				throw new CtxEventMgrException("Could not send remote context change event to topic '" 
						+ topics[i] + "'" + " with itemId '" + itemId + "': "
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private void registerLocalChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final CtxIdentifier ctxId) throws CtxException {
		
		final Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, topics);
		props.put(EventConstants.EVENT_FILTER, 
				"(" + EVENT_ID_PROPERTY_KEY + "=" + ctxId + ")");
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering local context change event listener to topics "
					+ Arrays.toString(topics)
					+ " with properties '" + props + "'");
		this.bundleContext.registerService(EventHandler.class.getName(),
				new LocalChangeEventHandler(listener), props);
	}
	
	private void registerRemoteChangeListener(final IIdentity pubsubId,
			final CtxChangeEventListener listener, final String[] topics,
			final CtxIdentifier ctxId) throws CtxException {
		
		final String filter = ctxId.toString();
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering remote context change event listener to pubsubId "
					+ pubsubId + " for topics "	+ Arrays.toString(topics)
					+ " with filter '" + filter + "'");
		try {
			for (int i = 0; i < topics.length; ++i)
				this.pubsubClient.subscriberSubscribe(pubsubId, topics[i], 
						new RemoteChangeEventHandler(listener, filter));
		} catch (Exception e) {
			
			throw new CtxEventMgrException(
					"Could not register remote context change event listener to pubsubId "
					+ pubsubId + " for topics "	+ Arrays.toString(topics)
					+ " with filter '" + filter + "': " + e.getLocalizedMessage(), e);
		}
	}
	
	private void registerLocalChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {
		
		final Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, topics);
		final StringBuilder eventFilterSB = new StringBuilder();
		eventFilterSB.append("(");
		eventFilterSB.append(EVENT_ID_PROPERTY_KEY + "=" + scope + "/ATTRIBUTE/");
		if (attrType != null)
			eventFilterSB.append(attrType + "/*");
		else
			eventFilterSB.append("*");
		eventFilterSB.append(")");
		props.put(EventConstants.EVENT_FILTER, eventFilterSB.toString());
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering local context change event listener to topics "
					+ Arrays.toString(topics)
					+ " with properties '" + props + "'");
		this.bundleContext.registerService(EventHandler.class.getName(),
				new LocalChangeEventHandler(listener), props);
	}
	
	private void registerRemoteChangeListener(final IIdentity pubsubId,
			final CtxChangeEventListener listener, final String[] topics,
			final CtxEntityIdentifier scope, final String attrType)
					throws CtxException {
		
		final StringBuilder eventFilterSB = new StringBuilder();
		eventFilterSB.append(scope.toString() + "/ATTRIBUTE");
		if (attrType != null)
			eventFilterSB.append("/" + attrType);
		eventFilterSB.append("/\\S+");
		final String filter = eventFilterSB.toString();
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering remote context change event listener to pubsubId "
					+ pubsubId + " for topics "	+ Arrays.toString(topics)
					+ " with filter '" + filter + "'");
		try {
			for (int i = 0; i < topics.length; ++i) {
				
				final Subscription subscription = this.pubsubClient.subscriberSubscribe(pubsubId, topics[i], 
						new RemoteChangeEventHandler(listener, filter));
				if (LOG.isDebugEnabled())
					LOG.debug("subscription=" + subscription);
			}
		} catch (Exception e) {
			
			throw new CtxEventMgrException(
					"Could not register remote context change event listener to pubsubId "
					+ pubsubId + " for topics "	+ Arrays.toString(topics)
					+ " with filter '" + filter + "': " + e.getLocalizedMessage(), e);
		}
	}
	
	private class LocalChangeEventHandler implements EventHandler {

		/** The listener to forward CtxChangeEvents. */
		private final CtxChangeEventListener listener;
		
		private LocalChangeEventHandler(CtxChangeEventListener listener) {
			
			this.listener = listener;
		}
		
		/*
		 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
		 */
		@Override
		public void handleEvent(Event osgiEvent) {
		
			try {
				this.checkEventProps(osgiEvent);

				// Extract the String form of the CtxIdentifier
				final String ctxIdStr = (String) osgiEvent.getProperty(EVENT_ID_PROPERTY_KEY);
				final CtxIdentifier ctxId = CtxIdentifierFactory.getInstance().fromString(ctxIdStr);
				
				final CtxChangeEvent ctxChangeEvent = new CtxChangeEvent(ctxId);
				final String topic = osgiEvent.getTopic();
				if (CtxChangeEventTopic.CREATED.equals(topic))
					this.listener.onCreation(ctxChangeEvent);
				else if (CtxChangeEventTopic.UPDATED.equals(topic))
					this.listener.onUpdate(ctxChangeEvent);
				else if (CtxChangeEventTopic.MODIFIED.equals(topic))
					this.listener.onModification(ctxChangeEvent);
				else if (CtxChangeEventTopic.REMOVED.equals(topic))
					this.listener.onRemoval(ctxChangeEvent);
				else
					LOG.error("Unexpected local context change event topic: '" + topic + "'");

			} catch (CtxEventMgrException ceme) {
				
				LOG.error("Malformed local context change event: " 
						+ ceme.getLocalizedMessage(), ceme);
			} catch (MalformedCtxIdentifierException mcie) {
				
				LOG.error("Malformed context identifier in local context change event: "
						+ mcie.getLocalizedMessage(), mcie);
			}
		}
		
		private void checkEventProps(final Event osgiEvent) throws CtxEventMgrException {
			
			if (!(osgiEvent.getProperty("id") instanceof String))
				throw new CtxEventMgrException("'id' property is missing or incorrect");
		}
	}
	
	private class RemoteChangeEventHandler implements Subscriber {

		/** The listener to forward CtxChangeEvents. */
		private final CtxChangeEventListener listener;
		
		/** The regular expression to match context identifiers. */
		private final Pattern patternFilter;
		
		private RemoteChangeEventHandler(final CtxChangeEventListener listener,
				final String filter) {
			
			this.listener = listener;
			this.patternFilter = Pattern.compile(filter);
		}
		
		/*
		 * @see org.societies.api.comm.xmpp.pubsub.Subscriber#pubsubEvent(org.societies.api.identity.IIdentity, java.lang.String, java.lang.String, java.lang.Object)
		 */
		@Override
		public void pubsubEvent(IIdentity pubsubService, String node, 
				String itemId, Object payload) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("pubsubEvent:pubsubService=" + pubsubService
						+ ",node=" + node + ",itemId=" + itemId 
						+ ",payload=" + payload);
			
			try {
				if (itemId == null) {
					LOG.error("Remote context change event itemId can't be null");
					return;
				}
				
				// Check if itemId, i.e. ctxId, matches against filter
				final Matcher filterMatcher = this.patternFilter.matcher(itemId);
				if (!filterMatcher.matches()) {
					if (LOG.isDebugEnabled())
						LOG.debug("Ignoring remote context change event for ctxId " + itemId);
					return;
				}
				
				final CtxIdentifier ctxId = CtxIdentifierFactory.getInstance().fromString(itemId);
				final CtxChangeEvent ctxChangeEvent = new CtxChangeEvent(ctxId);
				final String topic = node;
				if (CtxChangeEventTopic.CREATED.equals(topic))
					this.listener.onCreation(ctxChangeEvent);
				else if (CtxChangeEventTopic.UPDATED.equals(topic))
					this.listener.onUpdate(ctxChangeEvent);
				else if (CtxChangeEventTopic.MODIFIED.equals(topic))
					this.listener.onModification(ctxChangeEvent);
				else if (CtxChangeEventTopic.REMOVED.equals(topic))
					this.listener.onRemoval(ctxChangeEvent);
				else
					LOG.error("Unexpected remote context change event topic: '" + topic + "'");
			} catch (MalformedCtxIdentifierException mcie) {
				
				LOG.error("Malformed context identifier in remote context change event: "
						+ mcie.getLocalizedMessage(), mcie);
			}
		}
	}
}