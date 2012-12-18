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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.context.model.CtxIdentifierBean;
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
					"org.societies.api.schema.context.model.CtxIdentifierBean"));
			
	/** The Event Mgr service. */
	@Autowired(required=true)
	private IEventMgr eventMgr;
	
	/** The PubsubClient service reference. */
	private PubsubClient pubsubClient;
	
	/** The owner IIdentity of the pubsub service */
	private IIdentity pubsubId;
	
	private IIdentityManager idMgr;
	
	@Autowired(required=true)
	CtxEventMgr(PubsubClient pubsubClient, ICommManager commMgr) 
			throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.pubsubClient = pubsubClient;
		this.idMgr = commMgr.getIdManager();
		try {
			this.pubsubId = this.idMgr.getThisNetworkNode();
			this.pubsubClient.addSimpleClasses(EVENT_SCHEMA_CLASSES);
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
		
		if (this.idMgr.isMine(ownerId))
			this.registerLocalChangeListener(listener, topics, ownerId);
		else
			; // TODO ? this.registerRemoteChangeListener(ownerId, listener, topics);
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
		
		// TODO Auto-generated method stub
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
	
	private void postLocalChangeEvent(CtxChangeEvent event, String[] topics) 
			throws CtxEventMgrException {
		
		for (int i = 0; i < topics.length; ++i) {
			
			final InternalEvent internalEvent = new InternalEvent(
					topics[i], event.getId().toString(), event.getId().toString(), event.getId());
			if (LOG.isDebugEnabled())
				LOG.debug("Sending local context change event to topic '" 
						+ topics[i] + "'" + " with internal event name '" 
						+ internalEvent.geteventName() + "'");
			try {
				if (this.eventMgr == null)
					throw new CtxEventMgrException(
							"Could not send local context change event to topic '"
							+ topics[i] + "' with internal event name '" 
							+ internalEvent.geteventName() 
							+ "'': IEventMgr service is not available");
				this.eventMgr.publishInternalEvent(internalEvent);
			} catch (EMSException emse) {

				throw new CtxEventMgrException(
						"Could not send local context change event to topic '"
						+ topics[i] + "' with internal event name '" 
						+ internalEvent.geteventName() 
						+ "': " + emse.getLocalizedMessage(), emse);
			}
		}
	}
	
	private void postRemoteChangeEvent(CtxChangeEvent event, String[] topics) 
			throws CtxEventMgrException {
		
		final String itemId = event.getId().toString();
		final CtxIdentifierBean eventBean = 
				CtxModelBeanTranslator.getInstance().fromCtxIdentifier(event.getId());
		for (int i = 0; i < topics.length; ++i) {
		
			if (LOG.isDebugEnabled()) 
				LOG.debug("Sending remote context change event to topic '" + topics[i] + "'"
						+ " with itemId '" + itemId + "'");
			try {
				if (this.pubsubClient == null)
					throw new CtxEventMgrException("Could not send remote context change event to topic '"
							+ topics[i] + "': PubsubClient service is not available");
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
			final String[] topics, final IIdentity ownerId) throws CtxException {
		
		final String filter = "(" + CSSEventConstants.EVENT_NAME + "=*" 
				+ ownerId.toString() + "*)";
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering local context change event listener to topics "
					+ Arrays.toString(topics)
					+ " with filter '" + filter + "'");
		this.eventMgr.subscribeInternalEvent(
				new LocalChangeEventHandler(listener), topics, filter);
	}
	
	private void registerLocalChangeListener(final CtxChangeEventListener listener,
			final String[] topics, final CtxIdentifier ctxId) throws CtxException {
		
		final String filter =  "(" + CSSEventConstants.EVENT_NAME + "=" 
				+ ctxId + ")";
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering local context change event listener to topics "
					+ Arrays.toString(topics)
					+ " with filter '" + filter + "'");
		this.eventMgr.subscribeInternalEvent(
				new LocalChangeEventHandler(listener), topics, filter);
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
		
		final StringBuilder eventFilterSB = new StringBuilder();
		eventFilterSB.append("(");
		eventFilterSB.append(CSSEventConstants.EVENT_NAME + "=" + scope + "/ATTRIBUTE/");
		if (attrType != null)
			eventFilterSB.append(attrType + "/*");
		else
			eventFilterSB.append("*");
		eventFilterSB.append(")");
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering local context change event listener to topics "
					+ Arrays.toString(topics)
					+ " with filter '" + eventFilterSB.toString() + "'");
		this.eventMgr.subscribeInternalEvent(
				new LocalChangeEventHandler(listener), topics, eventFilterSB.toString());
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
	
	private class LocalChangeEventHandler extends EventListener {

		/** The listener to forward CtxChangeEvents. */
		private final CtxChangeEventListener listener;
		
		private LocalChangeEventHandler(CtxChangeEventListener listener) {
			
			this.listener = listener;
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