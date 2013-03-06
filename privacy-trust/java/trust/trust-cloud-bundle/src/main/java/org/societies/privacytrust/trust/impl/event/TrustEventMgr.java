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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.event.ITrustEventListener;
import org.societies.api.privacytrust.trust.event.TrustEvent;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
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
	
	/** The platform Event Mgr service reference. */
	@Autowired(required=true)
	private IEventMgr eventMgr;
	
	TrustEventMgr() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#postEvent(org.societies.api.privacytrust.trust.event.TrustEvent, java.lang.String[])
	 */
	@Override
	public void postEvent(final TrustEvent event, final String[] topics)
			throws TrustEventMgrException {
		
		if (event == null)
			throw new NullPointerException("event can't be null");
		if (topics == null)
			throw new NullPointerException("topics can't be null");
		
		if (event instanceof TrustUpdateEvent)
			this.postUpdateEvent((TrustUpdateEvent) event, topics);
		else if (event instanceof TrustEvidenceUpdateEvent)
			this.postEvidenceUpdateEvent((TrustEvidenceUpdateEvent) event, topics);
		else
			throw new TrustEventMgrException("Could not post event "
					+ event + ": Unsupported TrustEvent implementation");
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
		this.eventMgr.subscribeInternalEvent(new TrustUpdateEventHandler(listener),
				topics,	filter);
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
		this.eventMgr.subscribeInternalEvent(new TrustEvidenceUpdateEventHandler(listener),
				topics,	filter);
	}
	
	private void postUpdateEvent(final TrustUpdateEvent event, final String[] topics)
			throws TrustEventMgrException {
		
		final TrustUpdateEventInfo eventInfo = new TrustUpdateEventInfo(
				event.getOldValue(), event.getNewValue()); 
		
		for (int i = 0; i < topics.length; ++i) {
			
			final InternalEvent internalEvent = new InternalEvent(
					topics[i], event.getTrusteeId().toString(), 
					event.getTrustorId().toString(), eventInfo);

			if (this.eventMgr == null)
				throw new TrustEventMgrException("Could not send TrustUpdateEvent '"
						+ event + "' to topic " + topics[i]
						+ ": IEventMgr service is not available");
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

				throw new TrustEventMgrException("Could not post internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ", info=" + internalEvent.geteventInfo()
						+ " to topic " + topics[i]
						+ ": " + emse.getLocalizedMessage(), emse);
			}
		}
	}
	
	private void postEvidenceUpdateEvent(final TrustEvidenceUpdateEvent event,
			final String[] topics) throws TrustEventMgrException {
		
		for (int i = 0; i < topics.length; ++i) {
			
			final InternalEvent internalEvent = new InternalEvent(
					topics[i], event.getSource().getObjectId().toString(), 
					event.getSource().getSubjectId().toString(), event.getSource());

			if (this.eventMgr == null)
				throw new TrustEventMgrException("Could not send TrustEvidenceUpdateEvent '"
						+ event + "' to topic " + topics[i]
						+ ": IEventMgr service is not available");
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

				throw new TrustEventMgrException("Could not post internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ", info=" + internalEvent.geteventInfo()
						+ " to topic " + topics[i]
						+ ": " + emse.getLocalizedMessage(), emse);
			}
		}
	}
	
	private class TrustUpdateEventHandler extends EventListener {

		/** The listener to forward TrustUpdateEvents. */
		private final ITrustUpdateEventListener listener;
		
		private TrustUpdateEventHandler(final ITrustUpdateEventListener listener) {
			
			this.listener = listener;
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
						trusteeId, eventInfo.getOldValue(), eventInfo.getNewValue());
				if (LOG.isDebugEnabled())
					LOG.debug("Forwarding TrustUpdateEvent " + event + " to listener");
				this.listener.onUpdate(event);
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

		private final Double oldValue;
		
		private final Double newValue;
		
		private TrustUpdateEventInfo(final Double oldValue, final Double newValue) {
			
			this.oldValue = oldValue;
			this.newValue = newValue;
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
			sb.append("oldValue=" + this.getOldValue());
			sb.append(",");
			sb.append("newValue=" + this.getNewValue());
			sb.append("}");
			
			return sb.toString();
		}
	}
	
	private class TrustEvidenceUpdateEventHandler extends EventListener {

		/** The listener to forward TrustUpdateEvents. */
		private final ITrustEvidenceUpdateEventListener listener;
		
		private TrustEvidenceUpdateEventHandler(
				final ITrustEvidenceUpdateEventListener listener) {
			
			this.listener = listener;
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
			this.listener.onNew(event);
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