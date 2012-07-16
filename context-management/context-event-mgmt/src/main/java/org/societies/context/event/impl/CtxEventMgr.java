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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.event.CtxEvent;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
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
			
	/** The OSGi EventAdmin service. */
	@Autowired(required=true)
	private EventAdmin eventAdmin;
	
	/** The OSGi bundle context. */
	private BundleContext bundleContext;
	
	CtxEventMgr() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}
	
	/* (non-Javadoc)
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
		
		// TODO Take event scope into account
		
		if (event instanceof CtxChangeEvent)
			this.postChangeEvent((CtxChangeEvent) event, topics);
		else
			throw new CtxEventMgrException("Cannot send event to topics "
					+ Arrays.toString(topics) 
					+ ": Unsupported CtxEvent implementation");
	}
	
	/* (non-Javadoc)
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
		
		final Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, topics);
		// TODO Add ctx event constants
		props.put(EventConstants.EVENT_FILTER, "(id=" + ctxId + ")");
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering context event listener to topics "
					+ Arrays.toString(topics)
					+ " with properties '" + props + "'");
		this.bundleContext.registerService(EventHandler.class.getName(),
				new CtxChangeEventHandler(listener), props);
	}

	/* (non-Javadoc)
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
		
		final Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, topics);
		// TODO Add ctx event constants
		final StringBuilder eventFilterSB = new StringBuilder();
		eventFilterSB.append("(");
		eventFilterSB.append("id=" + scope + "/ATTRIBUTE/");
		if (attrType != null)
			eventFilterSB.append(attrType + "/*");
		else
			eventFilterSB.append("*");
		eventFilterSB.append(")");
		props.put(EventConstants.EVENT_FILTER, eventFilterSB.toString());
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering context event listener to topics "
					+ Arrays.toString(topics)
					+ " with properties '" + props + "'");
		this.bundleContext.registerService(EventHandler.class.getName(),
				new CtxChangeEventHandler(listener), props);
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
	
	/* (non-Javadoc)
	 * @see org.springframework.osgi.context.BundleContextAware#setBundleContext(org.osgi.framework.BundleContext)
	 */
	@Override
	public void setBundleContext(BundleContext bundleContext) {

		this.bundleContext = bundleContext;
	}
	
	private void postChangeEvent(CtxChangeEvent event, String[] topics) throws CtxEventMgrException {
		
		for (int i = 0; i < topics.length; ++i) {
			
			if (this.eventAdmin == null)
				throw new CtxEventMgrException("Could not send context event to topic '"
						+ topics[i] + "': OSGi EventAdmin service is not available");
		
			final Map<String, Object> props = new HashMap<String, Object>();
			// TODO Add ctx event constants
			props.put("id", event.getId().toString());
			if (LOG.isDebugEnabled()) 
				LOG.debug("Sending context event to topic '" + topics[i] + "'"
						+ " with properties '" + props + "'");
			this.eventAdmin.postEvent(new Event(topics[i], props));
		}
	}
	
	private class CtxChangeEventHandler implements EventHandler {

		/** The listener to forward CtxChangeEvents. */
		private final CtxChangeEventListener listener;
		
		private CtxChangeEventHandler(CtxChangeEventListener listener) {
			this.listener = listener;
		}
		
		/* (non-Javadoc)
		 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
		 */
		@Override
		public void handleEvent(Event osgiEvent) {
		
			// TODO Add ctx event constants
			try {
				this.checkEventProps(osgiEvent);

				// Extract the String form of the CtxIdentifier
				final String ctxIdStr = (String) osgiEvent.getProperty("id");
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
					LOG.error("Unexpected context change event topic: '" + topic + "'");

			} catch (CtxEventMgrException ceme) {
				
				LOG.error("Malformed context change event: " 
						+ ceme.getLocalizedMessage(), ceme);
			} catch (MalformedCtxIdentifierException mcie) {
				
				LOG.error("Malformed context identifier in context change event: "
						+ mcie.getLocalizedMessage(), mcie);
			}
		}
		
		private void checkEventProps(final Event osgiEvent) throws CtxEventMgrException {
			
			if (!(osgiEvent.getProperty("id") instanceof String))
				throw new CtxEventMgrException("'id' property is missing or incorrect");
		}
	}
}