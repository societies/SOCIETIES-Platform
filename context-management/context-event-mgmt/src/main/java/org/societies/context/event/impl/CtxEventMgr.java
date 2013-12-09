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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.event.CtxEvent;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.context.contextmanagement.CtxChangeEventBean;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.event.api.CtxEventMgrException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ICtxEventMgr} interface.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.4
 */
@Service("ctxEventMgr")
@Lazy(false)
public class CtxEventMgr implements ICtxEventMgr {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxEventMgr.class);
	
	private static final List<String> EVENT_SCHEMA_CLASSES = 
			Collections.unmodifiableList(Arrays.asList(
					"org.societies.api.schema.context.contextmanagement.CtxChangeEventBean"));
	
	private static final String MODIFIED_LOCATION_SYMBOLIC =
			CtxChangeEventTopic.MODIFIED + "/locationSymbolic";
			
	/** The Event Mgr service. */
	@Autowired(required=true)
	private IEventMgr eventMgr;
	
	/** The PubsubClient service reference. */
	private PubsubClient pubsubClient;
	
	@Autowired(required=true)
	private ICommManager commMgr;
	
	private final Set<LocalChangeEventHandler> localHandlers =
			new CopyOnWriteArraySet<LocalChangeEventHandler>();
	
	private final ExecutorService localDispatchingService =
			Executors.newSingleThreadExecutor();
	
	private final ExecutorService remoteDispatchingService =
			Executors.newSingleThreadExecutor();
	
	@Autowired(required=true)
	CtxEventMgr(PubsubClient pubsubClient) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.pubsubClient = pubsubClient;
		try {
			if (LOG.isDebugEnabled())
				LOG.debug("Adding remote context event payload classes '" + EVENT_SCHEMA_CLASSES + "'");
			this.pubsubClient.addSimpleClasses(EVENT_SCHEMA_CLASSES);
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
			final CtxEventScope scope) {
		
		if (event == null)
			throw new NullPointerException("event can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		
		if (LOG.isDebugEnabled())
			LOG.debug("Posting context event '" + event + "' to topics '"
					+ Arrays.toString(topics) + "' with scope " + scope);
		if (event instanceof CtxChangeEvent) {
			
			switch (scope) {

			case LOCAL:
				this.localDispatchingService.execute(new LocalChangeEventDispatcher(
						(CtxChangeEvent) event, topics));
				break;
			case INTRA_CSS:
				// TODO Handle intra-CSS event publishing
				break;
			case INTER_CSS:
				this.remoteDispatchingService.execute(new RemoteChangeEventDispatcher(
						(CtxChangeEvent) event, topics));
				break;
			case BROADCAST:
				this.localDispatchingService.execute(new LocalChangeEventDispatcher(
						(CtxChangeEvent) event, topics));
				this.remoteDispatchingService.execute(new RemoteChangeEventDispatcher(
						(CtxChangeEvent) event, topics));
				break;	
			default:
				LOG.error("Cannot post event to topics "
						+ Arrays.toString(topics) 
						+ ": Unsupported CtxEventScope: " + scope);
			}
		} else {
			LOG.error("Cannot post event to topics "
					+ Arrays.toString(topics) 
					+ ": Unsupported CtxEvent implementation");
		}
	}
	
	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#registerChangeListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.identity.IIdentity)
	 */
	@Override
	public void registerChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final IIdentity ownerId) throws CtxException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (ownerId == null)
			throw new NullPointerException("ownerId can't be null");
		
		if (this.commMgr.getIdManager().isMine(ownerId)) {
			// local
			final String filter = "(" + CSSEventConstants.EVENT_NAME + "=*" 
					+ ownerId.toString() + "*)";
			this.registerLocalChangeListener(listener, topics, filter);
		} else {
			// remote
			// TODO ? this.registerRemoteChangeListener(ownerId, listener, topics);
		}
	}

	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#unregisterChangeListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.identity.IIdentity)
	 */
	@Override
	public void unregisterChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final IIdentity ownerId) throws CtxException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		if (ownerId == null)
			throw new NullPointerException("ownerId can't be null");
		
		if (this.commMgr.getIdManager().isMine(ownerId)) {
			// local
			final String filter = "(" + CSSEventConstants.EVENT_NAME + "=*" 
					+ ownerId.toString() + "*)";
			this.unregisterLocalChangeListener(listener, topics, filter);
		} else {
			// remote
			// TODO ?
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
			final IIdentity pubsubId = this.commMgr.getIdManager().fromJid(ctxId.getOwnerId());
			if (this.commMgr.getIdManager().isMine(pubsubId)) {
				// local
				final String filter =  "(" + CSSEventConstants.EVENT_NAME + "=" 
						+ ctxId + ")";
				this.registerLocalChangeListener(listener, topics, filter);
			} else {
				// remote
				final String filter = ctxId.toString();
				this.registerRemoteChangeListener(pubsubId, listener, topics, filter);
			}
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
	
		try {
			final IIdentity pubsubId = this.commMgr.getIdManager().fromJid(ctxId.getOwnerId());
			if (this.commMgr.getIdManager().isMine(pubsubId)) {
				// local
				final String filter =  "(" + CSSEventConstants.EVENT_NAME + "=" 
						+ ctxId + ")";
				this.unregisterLocalChangeListener(listener, topics, filter);
			} else {
				// remote
				// TODO
				//this.unregisterRemoteChangeListener(pubsubId, listener, topics, ctxId);
			}
		} catch (InvalidFormatException ife) {
			
			throw new CtxEventMgrException("Could not unregister context change event listener: "
					+ "ctxId is not a valid IIdentity: " + ife.getLocalizedMessage(), ife);
		}
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
			final IIdentity pubsubId = this.commMgr.getIdManager().fromJid(scope.getOwnerId());
			if (this.commMgr.getIdManager().isMine(pubsubId)) {
				// local
				final StringBuilder eventFilterSB = new StringBuilder();
				eventFilterSB.append("(");
				eventFilterSB.append(CSSEventConstants.EVENT_NAME + "=" + scope + "/ATTRIBUTE/");
				if (attrType != null)
					eventFilterSB.append(attrType + "/*");
				else
					eventFilterSB.append("*");
				eventFilterSB.append(")");
				this.registerLocalChangeListener(listener, topics, eventFilterSB.toString());
			} else {
				// remote
				final StringBuilder eventFilterSB = new StringBuilder();
				eventFilterSB.append(scope.toString() + "/ATTRIBUTE");
				if (attrType != null)
					eventFilterSB.append("/" + attrType);
				eventFilterSB.append("/\\S+");
				this.registerRemoteChangeListener(pubsubId, listener, topics, eventFilterSB.toString());
			}
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
		
		try {
			final IIdentity pubsubId = this.commMgr.getIdManager().fromJid(scope.getOwnerId());
			if (this.commMgr.getIdManager().isMine(pubsubId)) {
				// local
				final StringBuilder eventFilterSB = new StringBuilder();
				eventFilterSB.append("(");
				eventFilterSB.append(CSSEventConstants.EVENT_NAME + "=" + scope + "/ATTRIBUTE/");
				if (attrType != null)
					eventFilterSB.append(attrType + "/*");
				else
					eventFilterSB.append("*");
				eventFilterSB.append(")");
				this.unregisterLocalChangeListener(listener, topics, eventFilterSB.toString());
			} else {
				// remote
				// TODO
			}
		} catch (InvalidFormatException ife) {
			
			throw new CtxEventMgrException("Could not register context change event listener: "
					+ "ctxId is not a valid IIdentity: " + ife.getLocalizedMessage(), ife);
		}
	}
	
	/*
	 * @see org.societies.context.api.event.ICtxEventMgr#createTopics(org.societies.api.identity.IIdentity, java.lang.String[])
	 */
	@Override
	public void createTopics(final IIdentity ownerId, final String[] topics)
			throws CtxException {

		final List<String> newTopics = new ArrayList<String>(Arrays.asList(topics));
		final List<String> existingTopics;
		try {
			existingTopics = this.pubsubClient.discoItems(ownerId, null);
		} catch (Exception e) {
			throw new CtxEventMgrException("Failed to discover topics for IIdentity "
					+ ownerId + ": " + e.getLocalizedMessage(), e);
		}
		// Special treatment for user LOCATION_SYMBOLIC modification events
		if (IdentityType.CIS != ownerId.getType()) {
			newTopics.add(MODIFIED_LOCATION_SYMBOLIC);
		}
		for (final String topic : newTopics) {
			if (existingTopics == null || !existingTopics.contains(topic)) {
				if (LOG.isInfoEnabled())
					LOG.info("Creating pubsub node '" + topic + "' for IIdentity " + ownerId);
				try {
					this.pubsubClient.ownerCreate(ownerId, topic);
				} catch (Exception e) {
					throw new CtxEventMgrException("Failed to create topic '"
							+ topic + "' for IIdentity " + ownerId + ": " 
							+ e.getLocalizedMessage(), e);
				}
			} else {
				if (LOG.isInfoEnabled())
					LOG.info("Found pubsub node '" + topic + "' for IIdentity " + ownerId);
			}
		}
	}
	
	private class LocalChangeEventDispatcher implements Runnable {
		
		private final CtxChangeEvent event;
		
		private final String[] topics;
		
		private LocalChangeEventDispatcher(CtxChangeEvent event, String[] topics) {
			
			this.event = event;
			this.topics = topics;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			if (LOG.isDebugEnabled()) 
				LOG.debug("Posting local context change event '" + this.event 
						+ "' to topics '" + Arrays.toString(this.topics) + "'");
			for (int i = 0; i < this.topics.length; ++i) {

				final InternalEvent internalEvent = new InternalEvent(
						this.topics[i], this.event.getId().toString(), 
						this.event.getId().toString(), this.event.getId());
				if (LOG.isDebugEnabled())
					LOG.debug("Posting local context change event to topic '" 
							+ this.topics[i] + "'" + " with internal event name '" 
							+ internalEvent.geteventName() + "'");
				try {
					if (eventMgr == null) {
						LOG.error("Could not post local context change event to topic '"
								+ this.topics[i] + "' with internal event name '" 
								+ internalEvent.geteventName() 
								+ "'': IEventMgr service is not available");
						return;
					}
					eventMgr.publishInternalEvent(internalEvent);
				} catch (EMSException emse) {

					LOG.error("Could not post local context change event to topic '"
							+ topics[i] + "' with internal event name '" 
							+ internalEvent.geteventName() 
							+ "': " + emse.getLocalizedMessage(), emse);
				}
			}
		}
	}
	
	private class RemoteChangeEventDispatcher implements Runnable {
		
		private final CtxChangeEvent event;
		
		private final List<String> topics;
		
		private RemoteChangeEventDispatcher(CtxChangeEvent event, String[] topics) {
			
			this.event = event;
			this.topics = new ArrayList<String>(Arrays.asList(topics));
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isDebugEnabled()) 
				LOG.debug("Posting remote context change event '" + this.event 
						+ "' to topics '" + this.topics + "'");
			final IIdentity pubsubId;
			final String itemId;
			final CtxChangeEventBean eventBean;
			try {
				pubsubId = commMgr.getIdManager().fromJid(
						this.event.getId().getOwnerId());
				itemId = this.event.getId().toString();
				eventBean = new CtxChangeEventBean();
				eventBean.setId(itemId);
			} catch (Exception e) {
				LOG.error("Could not post remote context change event '" 
						+ this.event + "' to topics '" + this.topics + "': "
						+ e.getLocalizedMessage(), e);
				return;
			}
			
			// Special treatment for user LOCATION_SYMBOLIC modification events
			if (IdentityType.CIS != pubsubId.getType()
					&& CtxAttributeTypes.LOCATION_SYMBOLIC.equals(this.event.getId().getType())
					&& this.topics.contains(CtxChangeEventTopic.MODIFIED)) {
				this.topics.add(MODIFIED_LOCATION_SYMBOLIC);
			}

			for (final String topic : this.topics) {

				if (LOG.isDebugEnabled()) 
					LOG.debug("Posting remote context change event to topic '" 
							+ topic + "'" + " with itemId '" + itemId + "'");
				try {
					if (pubsubClient == null) {
						LOG.error("Could not post remote context change event to topic '"
								+ topic + "': PubsubClient service is not available");
						return;
					}
					pubsubClient.publisherPublish(pubsubId, topic,
							itemId, eventBean);
				} catch (Exception e) { 
					LOG.error("Could not post remote context change event to topic '" 
							+ topic + "'" + " with itemId '" + itemId + "': "
							+ e.getLocalizedMessage(), e);
				}
			}
		}
	}
	
	private void registerLocalChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final String filter) throws CtxException {
		
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering local context change event listener to topics "
					+ Arrays.toString(topics)
					+ " with filter '" + filter + "'");
		final LocalChangeEventHandler localHandler =
				new LocalChangeEventHandler(listener, filter);
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size before register: " + this.localHandlers.size());
		if (!this.localHandlers.add(localHandler))
			throw new CtxEventMgrException(
					"Could not register local context change event listener to topics "
					+ Arrays.toString(topics)
					+ " with filter '" + filter + "': Listener already registered");
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size after register: " + this.localHandlers.size());
		this.eventMgr.subscribeInternalEvent(localHandler, topics, filter);
	}
	
	private void unregisterLocalChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final String filter) throws CtxException {
		
		if (LOG.isInfoEnabled()) 
			LOG.info("Unregistering local context change event listener from topics "
					+ Arrays.toString(topics)
					+ " with filter '" + filter + "'");
		final LocalChangeEventHandler localHandler =
				new LocalChangeEventHandler(listener, filter);
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size before unregister: " + this.localHandlers.size());
		if (!this.localHandlers.remove(localHandler))
			throw new CtxEventMgrException(
					"Could not unregister local context change event listener from topics "
					+ Arrays.toString(topics)
					+ " with filter '" + filter + "': Listener was not registered");
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size after unregister: " + this.localHandlers.size());
		this.eventMgr.unSubscribeInternalEvent(localHandler, topics, filter);
	}
	
	private void registerRemoteChangeListener(final IIdentity pubsubId,
			final CtxChangeEventListener listener, final String[] topics,
			final String filter) throws CtxException {
		
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering remote context change event listener to pubsubId "
					+ pubsubId + " and topics " + Arrays.toString(topics)
					+ " with filter '" + filter + "'");
		try {
			for (int i = 0; i < topics.length; ++i)
				this.pubsubClient.subscriberSubscribe(pubsubId, topics[i], 
						new RemoteChangeEventHandler(listener, filter));
		} catch (Exception e) {
			
			throw new CtxEventMgrException(
					"Could not register remote context change event listener to pubsubId "
					+ pubsubId + " and topics "	+ Arrays.toString(topics)
					+ " with filter '" + filter + "': " + e.getLocalizedMessage(), e);
		}
	}
	
	private class LocalChangeEventHandler extends EventListener {

		/** The listener to forward CtxChangeEvents. */
		private final CtxChangeEventListener listener;
		
		/** 
		 * The filter used for the EventListener registration.
		 *  
		 * @see #hashCode()
		 * @see #equals(Object) 
		 */
		private final String filter;
		
		private LocalChangeEventHandler(final CtxChangeEventListener listener,
				final String filter) {
			
			this.listener = listener;
			this.filter = filter;
		}
		
		/*
		 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
		 */
		@Override
		public void handleExternalEvent(CSSEvent cssEvent) {
			
			LOG.warn("Received unexpected external CSS event"
					+ ": type=" + cssEvent.geteventType()
					+ ", name=" + cssEvent.geteventName()
					+ ", source=" + cssEvent.geteventSource());
		}

		/*
		 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
		 */
		@Override
		public void handleInternalEvent(InternalEvent internalEvent) {
		
			try {
				this.checkEventProps(internalEvent);
				final CtxChangeEvent ctxChangeEvent = new CtxChangeEvent(
						(CtxIdentifier) internalEvent.geteventInfo());
				final String topic = internalEvent.geteventType();
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
			}
		}
		
		private void checkEventProps(final InternalEvent internalEvent)
				throws CtxEventMgrException {
			
			if (!(internalEvent.geteventInfo() instanceof CtxIdentifier))
				throw new CtxEventMgrException("internal event info is missing or incorrect");
		}
		
		/*
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.listener == null) ? 0 : this.listener.hashCode());
			result = prime * result
					+ ((this.filter == null) ? 0 : this.filter.hashCode());
			
			return result;
		}

		/*
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object that) {
			
			if (this == that)
				return true;
			if (that == null)
				return false;
			if (this.getClass() != that.getClass())
				return false;
			
			LocalChangeEventHandler other = (LocalChangeEventHandler) that;
			if (this.listener == null) {
				if (other.listener != null)
					return false;
			} else if (!this.listener.equals(other.listener))
				return false;
			if (this.filter == null) {
				if (other.filter != null)
					return false;
			} else if (!this.filter.equals(other.filter))
				return false;
			
			return true;
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