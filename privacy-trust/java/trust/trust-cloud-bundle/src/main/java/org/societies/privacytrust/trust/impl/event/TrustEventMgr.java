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
package org.societies.privacytrust.trust.impl.event;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.trust.event.ITrustEventListener;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustEvent;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener;
import org.societies.privacytrust.trust.api.event.TrustEventMgrException;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ITrustEventMgr} service.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.5
 */
@Service
@Lazy(value = false)
public class TrustEventMgr implements ITrustEventMgr {

	private static final Logger LOG = LoggerFactory.getLogger(TrustEventMgr.class);
	
	private static final List<String> EVENT_SCHEMA_CLASSES = 
			Collections.unmodifiableList(Arrays.asList(
					"org.societies.api.schema.privacytrust.trust.model.TrustUpdateEventBean"));
	
	/** The platform Event Mgr service reference. */
	@Autowired(required=true)
	private IEventMgr eventMgr;
	
	/** The PubsubClient service reference. */
	private PubsubClient pubsubClient;
	
	private final Set<LocalTrustEventHandler> localHandlers =
			new CopyOnWriteArraySet<LocalTrustEventHandler>();
	
	private final ExecutorService localDispatchingService =
			Executors.newSingleThreadExecutor();
	
	@SuppressWarnings("unused")
	private final ExecutorService remoteDispatchingService =
			Executors.newSingleThreadExecutor();
	
	@Autowired(required=true)
	TrustEventMgr(PubsubClient pubsubClient) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		this.pubsubClient = pubsubClient;
		try {
			if (LOG.isDebugEnabled())
				LOG.debug("Adding remote remote trust event payload classes '" 
						+ EVENT_SCHEMA_CLASSES + "'");
			this.pubsubClient.addSimpleClasses(EVENT_SCHEMA_CLASSES);
			// TODO create nodes
		} catch (Exception e) {
			
			LOG.error(this.getClass() + " could not be instantiated: "
					+ e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#postEvent(org.societies.api.privacytrust.trust.event.TrustEvent, java.lang.String[])
	 */
	@Override
	public void postEvent(final TrustEvent event, final String[] topics) {
		
		if (event == null)
			throw new NullPointerException("event can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		
		this.localDispatchingService.execute(new LocalTrustEventDispatcher(
				event, topics));
		/* TODO this.remoteDispatchingService.execute(new RemoteTrustEventDispatcher(
				event, topics)); */
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerListener(org.societies.api.privacytrust.trust.event.ITrustEventListener, java.lang.String[])
	 */
	@Override
	public void registerListener(final ITrustEventListener listener, 
			final String[] topics) throws TrustEventMgrException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		
		if (listener instanceof ITrustUpdateEventListener)
			this.registerUpdateListener((ITrustUpdateEventListener) listener,
					topics, null, null);
		else if (listener instanceof ITrustEvidenceUpdateEventListener)
			this.registerEvidenceUpdateListener(
					(ITrustEvidenceUpdateEventListener) listener, topics, null,
					null);
		else
			throw new TrustEventMgrException("Could not register trust event listener "
					+ listener + ": Unsupported ITrustEventListener extension");
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterListener(org.societies.api.privacytrust.trust.event.ITrustEventListener, java.lang.String[])
	 */
	@Override
	public void unregisterListener(final ITrustEventListener listener, 
			final String[] topics) throws TrustEventMgrException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		
		if (listener instanceof ITrustUpdateEventListener)
			this.unregisterUpdateListener((ITrustUpdateEventListener) listener,
					topics, null, null);
		else if (listener instanceof ITrustEvidenceUpdateEventListener)
			this.unregisterEvidenceUpdateListener(
					(ITrustEvidenceUpdateEventListener) listener, topics, null,
					null);
		else
			throw new TrustEventMgrException("Could not unregister trust event listener "
					+ listener + ": Unsupported ITrustEventListener extension");
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerUpdateListener(final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException {
		
		String filter = null;
		if (trustorId != null && trusteeId != null)
			filter = "(&" 
					+ "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId.toString() + ")"
					+ "(" + CSSEventConstants.EVENT_NAME + "=" + trusteeId.toString() + ")"
					+ ")";
		else if (trustorId != null)
			filter = "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId.toString() + ")";
		else if (trusteeId != null)
			filter = "(" + CSSEventConstants.EVENT_NAME + "=" + trusteeId.toString() + ")";
			
		if (this.eventMgr == null)
			throw new TrustEventMgrException("Could not register TrustUpdateEvent listener '"
					+ listener + "' to topics " + Arrays.toString(topics)
					+ ": IEventMgr service is not available");
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering TrustUpdateEvent listener to topics "
					+ Arrays.toString(topics));
		final LocalTrustEventHandler localHandler = 
				new LocalTrustUpdateEventHandler(listener, filter);
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size before register: " + this.localHandlers.size());
		if (this.localHandlers.add(localHandler)) {
			this.eventMgr.subscribeInternalEvent(localHandler,
				topics,	filter);
		} else {
			LOG.warn("TrustUpdateEvent listener already registered to topics "
					+ Arrays.toString(topics));
		}
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size after register: " + this.localHandlers.size());
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterUpdateListener(final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException {
		
		String filter = null;
		if (trustorId != null && trusteeId != null)
			filter = "(&" 
					+ "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId.toString() + ")"
					+ "(" + CSSEventConstants.EVENT_NAME + "=" + trusteeId.toString() + ")"
					+ ")";
		else if (trustorId != null)
			filter = "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId.toString() + ")";
		else if (trusteeId != null)
			filter = "(" + CSSEventConstants.EVENT_NAME + "=" + trusteeId.toString() + ")";
			
		if (this.eventMgr == null)
			throw new TrustEventMgrException("Could not unregister TrustUpdateEvent listener '"
					+ listener + "' from topics " + Arrays.toString(topics)
					+ ": IEventMgr service is not available");
		if (LOG.isInfoEnabled()) 
			LOG.info("Unregistering TrustUpdateEvent listener from topics "
					+ Arrays.toString(topics));
		final LocalTrustEventHandler localHandler = 
				new LocalTrustUpdateEventHandler(listener, filter);
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size before unregister: " + this.localHandlers.size());
		if (this.localHandlers.remove(localHandler)) {
			this.eventMgr.unSubscribeInternalEvent(localHandler, topics, filter);
		} else {
			LOG.warn("Nothing to do - TrustUpdateEvent listener was not registered to topics "
					+ Arrays.toString(topics));
		}
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size after unregister: " + this.localHandlers.size());
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerEvidenceUpdateListener(org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener,
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException {
		
		String filter = null;
		if (subjectId != null && objectId != null)
			filter = "(&" 
					+ "(" + CSSEventConstants.EVENT_SOURCE + "=" + subjectId.toString() + ")"
					+ "(" + CSSEventConstants.EVENT_NAME + "=" + objectId.toString() + ")"
					+ ")";
		else if (subjectId != null)
			filter = "(" + CSSEventConstants.EVENT_SOURCE + "=" + subjectId.toString() + ")";
		else if (objectId != null)
			filter = "(" + CSSEventConstants.EVENT_NAME + "=" + objectId.toString() + ")";
			
		if (this.eventMgr == null)
			throw new TrustEventMgrException("Could not register TrustEvidenceUpdateEvent listener '"
					+ listener + "' to topics " + Arrays.toString(topics)
					+ ": IEventMgr service is not available");
		if (LOG.isInfoEnabled()) 
			LOG.info("Registering TrustEvidenceUpdateEvent listener to topics "
					+ Arrays.toString(topics));
		final LocalTrustEventHandler localHandler = 
				new LocalTrustEvidenceUpdateEventHandler(listener, filter);
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size before register: " + this.localHandlers.size());
		if (this.localHandlers.add(localHandler)) {
			this.eventMgr.subscribeInternalEvent(localHandler, topics, filter);
		} else {
			LOG.warn("Nothing to do - TrustEvidenceUpdateEvent listener already registered to topics "
					+ Arrays.toString(topics));
		}
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size after register: " + this.localHandlers.size());
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterEvidenceUpdateListener(org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener,
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException {
		
		String filter = null;
		if (subjectId != null && objectId != null)
			filter = "(&" 
					+ "(" + CSSEventConstants.EVENT_SOURCE + "=" + subjectId.toString() + ")"
					+ "(" + CSSEventConstants.EVENT_NAME + "=" + objectId.toString() + ")"
					+ ")";
		else if (subjectId != null)
			filter = "(" + CSSEventConstants.EVENT_SOURCE + "=" + subjectId.toString() + ")";
		else if (objectId != null)
			filter = "(" + CSSEventConstants.EVENT_NAME + "=" + objectId.toString() + ")";
			
		if (this.eventMgr == null)
			throw new TrustEventMgrException("Could not register TrustEvidenceUpdateEvent listener '"
					+ listener + "' to topics " + Arrays.toString(topics)
					+ ": IEventMgr service is not available");
		if (LOG.isInfoEnabled()) 
			LOG.info("Unregistering TrustEvidenceUpdateEvent listener from topics "
					+ Arrays.toString(topics));
		final LocalTrustEventHandler localHandler = 
				new LocalTrustEvidenceUpdateEventHandler(listener, filter);
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size before unregister: " + this.localHandlers.size());
		if (this.localHandlers.remove(localHandler)) {
			this.eventMgr.unSubscribeInternalEvent(localHandler, topics, filter);
		} else {
			LOG.warn("Nothing to do - TrustEvidenceUpdateEvent listener was never registered to topics "
					+ Arrays.toString(topics));
		}
		if (LOG.isDebugEnabled())
			LOG.debug("localHandlers size after unregister: " + this.localHandlers.size());
	}
	
	private void postLocalUpdateEvent(final TrustUpdateEvent event, 
			final String[] topics) {
		
		final TrustUpdateEventInfo eventInfo = new TrustUpdateEventInfo(
				event.getValueType(), event.getOldValue(), event.getNewValue()); 
		
		for (int i = 0; i < topics.length; ++i) {
			
			final InternalEvent internalEvent = new InternalEvent(
					topics[i], event.getTrusteeId().toString(), 
					event.getTrustorId().toString(), eventInfo);

			if (this.eventMgr == null) {
				LOG.error("Could not send TrustUpdateEvent '"
						+ event + "' to topic " + topics[i]
						+ ": IEventMgr service is not available");
				return;
			}
			try {
				if (LOG.isDebugEnabled())
					LOG.debug("Posting internal event"
							+ ": type=" + internalEvent.geteventType()
							+ ", name=" + internalEvent.geteventName()
							+ ", source=" + internalEvent.geteventSource()
							+ ", info=" + internalEvent.geteventInfo()
							+ " to topic " + topics[i]);
				this.eventMgr.publishInternalEvent(internalEvent);
			} catch (EMSException emse) {

				LOG.error("Could not post internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ", info=" + internalEvent.geteventInfo()
						+ " to topic " + topics[i]
						+ ": " + emse.getLocalizedMessage(), emse);
			}
		}
	}
	
	private void postLocalEvidenceUpdateEvent(final TrustEvidenceUpdateEvent event,
			final String[] topics) {
		
		for (int i = 0; i < topics.length; ++i) {
			
			final InternalEvent internalEvent = new InternalEvent(
					topics[i], event.getSource().getObjectId().toString(), 
					event.getSource().getSubjectId().toString(), event.getSource());

			if (this.eventMgr == null) {
				LOG.error("Could not send TrustEvidenceUpdateEvent '"
						+ event + "' to topic " + topics[i]
						+ ": IEventMgr service is not available");
				return;
			}
			try {
				if (LOG.isDebugEnabled())
					LOG.debug("Posting internal event"
							+ ": type=" + internalEvent.geteventType()
							+ ", name=" + internalEvent.geteventName()
							+ ", source=" + internalEvent.geteventSource()
							+ ", info=" + internalEvent.geteventInfo()
							+ " to topic " + topics[i]);
				this.eventMgr.publishInternalEvent(internalEvent);
			} catch (EMSException emse) {

				LOG.error("Could not post internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ", info=" + internalEvent.geteventInfo()
						+ " to topic " + topics[i]
						+ ": " + emse.getLocalizedMessage(), emse);
			}
		}
	}
	
	private class LocalTrustEventDispatcher implements Runnable {
		
		private final TrustEvent event;
		
		private final String[] topics;
		
		private LocalTrustEventDispatcher(TrustEvent event, String[] topics) {
			
			this.event = event;
			this.topics = topics;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (LOG.isDebugEnabled()) 
				LOG.debug("Posting local trust event '" + this.event 
						+ "' to topics '" + Arrays.toString(this.topics) + "'");
			
			if (this.event instanceof TrustUpdateEvent)
				postLocalUpdateEvent((TrustUpdateEvent) event, topics);
			else if (this.event instanceof TrustEvidenceUpdateEvent)
				postLocalEvidenceUpdateEvent((TrustEvidenceUpdateEvent) event, topics);
			else
				LOG.error("Could not post local trust event "
						+ this.event + ": Unsupported TrustEvent implementation '"
						+ this.event.getClass() + "'");
		}
	}
	
	private abstract class LocalTrustEventHandler extends EventListener {
		
		/** The listener to forward TrustEvents. */
		protected final ITrustEventListener listener;
		
		/** The event filter.*/
		protected final String filter;
		
		private LocalTrustEventHandler(final ITrustEventListener listener, final String filter) {
			
			this.listener = listener;
			this.filter = filter;
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
			
			LocalTrustEventHandler other = (LocalTrustEventHandler) that;
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
	
	private class LocalTrustUpdateEventHandler extends LocalTrustEventHandler {
		
		private LocalTrustUpdateEventHandler(
				final ITrustUpdateEventListener listener, 
				final String filter) {
			
			super(listener, filter);
		}

		/*
		 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
		 */
		@Override
		public void handleInternalEvent(InternalEvent internalEvent) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received internal event"
					+ ": type=" + internalEvent.geteventType()
					+ ", name=" + internalEvent.geteventName()
					+ ", source=" + internalEvent.geteventSource()
					+ ", info=" + internalEvent.geteventInfo());
			
			if (!(internalEvent.geteventInfo() instanceof TrustUpdateEventInfo)) {
				LOG.error("Cannot handle internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ": Unexpected eventInfo: " + internalEvent.geteventInfo());
				return;
			}
			
			final TrustUpdateEventInfo eventInfo = (TrustUpdateEventInfo) internalEvent.geteventInfo();
			try {
				final TrustedEntityId trustorId = new TrustedEntityId(internalEvent.geteventSource());
				final TrustedEntityId trusteeId = new TrustedEntityId(internalEvent.geteventName());
				final TrustUpdateEvent event = new TrustUpdateEvent(trustorId,
						trusteeId, eventInfo.getValueType(),
						eventInfo.getOldValue(), eventInfo.getNewValue());
				if (LOG.isDebugEnabled())
					LOG.debug("Forwarding TrustUpdateEvent " + event + " to listener");
				((ITrustUpdateEventListener) super.listener).onUpdate(event);
			} catch (MalformedTrustedEntityIdException mteide) {
				
				LOG.error("Cannot forward TrustUpdateEvent to listener:"
						+ " Failed to create TrustedEntityId from internal event property: "
						+ mteide.getLocalizedMessage(), mteide);
			}	
			
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
	}
	
	private class TrustUpdateEventInfo implements Serializable {
		
		private static final long serialVersionUID = -8227968800295980580L;
		
		private final TrustValueType valueType;

		private final Double oldValue;
		
		private final Double newValue;
		
		private TrustUpdateEventInfo(final TrustValueType valueType,
				final Double oldValue, final Double newValue) {
			
			if (valueType == null)
				throw new NullPointerException("valueType can't be null");
			
			this.valueType = valueType;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
		private TrustValueType getValueType() {
			
			return this.valueType;
		}
		
		private Double getOldValue() {
			
			return this.oldValue;
		}
		
		private Double getNewValue() {
			
			return this.newValue;
		}
		
		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			
			final StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("valueType=" + this.getValueType());
			sb.append(",");
			sb.append("oldValue=" + this.getOldValue());
			sb.append(",");
			sb.append("newValue=" + this.getNewValue());
			sb.append("}");
			
			return sb.toString();
		}
	}
	
	private class LocalTrustEvidenceUpdateEventHandler extends LocalTrustEventHandler {
		
		private LocalTrustEvidenceUpdateEventHandler(
				final ITrustEvidenceUpdateEventListener listener, 
				final String filter) {
			
			super(listener, filter);
		}

		/*
		 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
		 */
		@Override
		public void handleInternalEvent(final InternalEvent internalEvent) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received internal event"
					+ ": type=" + internalEvent.geteventType()
					+ ", name=" + internalEvent.geteventName()
					+ ", source=" + internalEvent.geteventSource()
					+ ", info=" + internalEvent.geteventInfo());
			
			if (!(internalEvent.geteventInfo() instanceof ITrustEvidence)) {
				LOG.error("Cannot handle internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ": Unexpected eventInfo: " + internalEvent.geteventInfo());
				return;
			}
			final ITrustEvidence evidence = 
					(ITrustEvidence) internalEvent.geteventInfo(); 
			final TrustEvidenceUpdateEvent event = 
					new TrustEvidenceUpdateEvent(evidence);
			if (LOG.isDebugEnabled())
				LOG.debug("Forwarding TrustEvidenceUpdateEvent " + event + " to listener");
			((ITrustEvidenceUpdateEventListener) super.listener).onNew(event);
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
	}
}