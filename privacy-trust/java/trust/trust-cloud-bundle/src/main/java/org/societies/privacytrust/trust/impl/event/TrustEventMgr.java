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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
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
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean2;
import org.societies.api.schema.privacytrust.trust.model.TrustUpdateEventBean;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener;
import org.societies.privacytrust.trust.api.event.TrustEventMgrException;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
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
	
	private static final List<String> EVENT_REMOTE_TOPICS = 
			Collections.unmodifiableList(Arrays.asList(
					TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED,
					TrustEventTopic.INDIRECT_TRUST_UPDATED,
					TrustEventTopic.DIRECT_TRUST_UPDATED
					));
	
	private static final List<String> EVENT_SCHEMA_CLASSES = 
			Collections.unmodifiableList(Arrays.asList(
					"org.societies.api.schema.privacytrust.trust.model.TrustUpdateEventBean"));
	
	/** The Trust Node Mgr service reference. */
	private ITrustNodeMgr trustNodeMgr;
	
	/** The platform Event Mgr service reference. */
	@Autowired(required=true)
	private IEventMgr eventMgr;
	
	/** The PubsubClient service reference. */
	private PubsubClient pubsubClient;
	
	private final Set<LocalTrustEventHandler> localHandlers =
			new CopyOnWriteArraySet<LocalTrustEventHandler>();
	
	private final Set<RemoteTrustUpdateEventHandler> remoteHandlers =
			new CopyOnWriteArraySet<RemoteTrustUpdateEventHandler>();
	
	private final ExecutorService localDispatchingService =
			Executors.newSingleThreadExecutor();
	
	private final ExecutorService remoteDispatchingService =
			Executors.newSingleThreadExecutor();
	
	@Autowired(required=true)
	TrustEventMgr(ITrustNodeMgr trustNodeMgr, PubsubClient pubsubClient)
			throws Exception {
		
		LOG.info("{} instantiated", this.getClass());
		this.trustNodeMgr = trustNodeMgr;
		this.pubsubClient = pubsubClient;
		try {
			this.createRemoteTopics();
		} catch (Exception e) {
			LOG.error(this.getClass() + " could not be initialised: "
					+ e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#postEvent(org.societies.api.privacytrust.trust.event.TrustEvent, java.lang.String[])
	 */
	@Override
	public void postEvent(final TrustEvent event, final String[] topics) {
		
		if (event == null) {
			throw new NullPointerException("event can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		
		this.localDispatchingService.execute(new LocalTrustEventDispatcher(
				event, topics));
		this.remoteDispatchingService.execute(new RemoteTrustEventDispatcher(
				event, topics));
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[])
	 */
	@Override
	public void registerUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		} 
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		
		this.doRegisterUpdateListener(listener, topics, null, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[])
	 */
	@Override
	public void unregisterUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		
		this.doUnregisterUpdateListener(listener, topics, null, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerUpdateListener(final ITrustUpdateEventListener listener, 
			final String[] topics, final TrustedEntityId trustorId) 
					throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		
		this.doRegisterUpdateListener(listener, topics, trustorId, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterUpdateListener(final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId)
					throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		
		this.doUnregisterUpdateListener(listener, topics, trustorId, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerUpdateListener(final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		if (trusteeId == null) {
			throw new NullPointerException("trusteeId can't be null");
		}
		
		this.doRegisterUpdateListener(listener, topics, trustorId, trusteeId);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterUpdateListener(final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		if (trusteeId == null) {
			throw new NullPointerException("trusteeId can't be null");
		}
		
		this.doUnregisterUpdateListener(listener, topics, trustorId, trusteeId);
	}
	
	private void doRegisterUpdateListener(
			final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException {
		
		if (this.isLocalId(trustorId)) {
			// L O C A L registration
			final String filter = this.createLocalFilter(trustorId, trusteeId);
			this.doRegisterLocalUpdateListener(listener, topics, filter);
		} else {
			// R E M O T E registration
			final String trustorIdFilter = this.createRemoteFilter(trustorId);
			final String trusteeIdFilter = this.createRemoteFilter(trusteeId);
			this.doRegisterRemoteUpdateListener(listener, topics, trustorId, 
					trustorIdFilter, trusteeIdFilter);
		}
	}
	
	private void doUnregisterUpdateListener(
			final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustEventMgrException {
		
		if (this.isLocalId(trustorId)) {
			// L O C A L deregistration
			final String filter = this.createLocalFilter(trustorId, trusteeId);
			this.doUnregisterLocalUpdateListener(listener, topics, filter);
		} else {
			// R E M O T E deregistration
			final String trustorIdFilter = this.createRemoteFilter(trustorId);
			final String trusteeIdFilter = this.createRemoteFilter(trusteeId);
			this.doUnregisterRemoteUpdateListener(listener, topics, trustorId,
					trustorIdFilter, trusteeIdFilter);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	public void registerUpdateListener(final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		if (trusteeType == null) {
			throw new NullPointerException("trusteeType can't be null");
		}
		
		this.doRegisterUpdateListenerByType(listener, topics, trustorId, trusteeType);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	public void unregisterUpdateListener(final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		if (trusteeType == null) {
			throw new NullPointerException("trusteeType can't be null");
		}
		
		this.doUnregisterUpdateListenerByType(listener, topics, trustorId, trusteeType);
	}
	
	private void doRegisterUpdateListenerByType(
			final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustEventMgrException {
		
		if (this.isLocalId(trustorId)) {
			// L O C A L registration
			final String filter = this.createLocalFilter(trustorId, trusteeType);
			this.doRegisterLocalUpdateListener(listener, topics, filter);
		} else {
			// R E M O T E registration
			final String trustorIdFilter = this.createRemoteFilter(trustorId);
			final String trusteeIdFilter = this.createRemoteFilter(trusteeType);
			this.doRegisterRemoteUpdateListener(listener, topics, trustorId,
					trustorIdFilter, trusteeIdFilter);
		}
	}
	
	private void doUnregisterUpdateListenerByType(
			final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustEventMgrException {
		
		if (this.isLocalId(trustorId)) {
			// L O C A L registration
			final String filter = this.createLocalFilter(trustorId, trusteeType);
			this.doUnregisterLocalUpdateListener(listener, topics, filter);
		} else {
			// R E M O T E registration
			final String trustorIdFilter = this.createRemoteFilter(trustorId);
			final String trusteeIdFilter = this.createRemoteFilter(trusteeType);
			this.doUnregisterRemoteUpdateListener(listener, topics, trustorId,
					trustorIdFilter, trusteeIdFilter);
		}
	}
	
	private void doRegisterLocalUpdateListener(
			final ITrustUpdateEventListener listener,
			final String[] topics, final String filter) 
					throws TrustEventMgrException {
		
		LOG.info("Registering local TrustUpdateEvent listener {}" 
				+ " to topics {} using filter '{}'", new Object[] {
						listener, Arrays.toString(topics), filter });
		final LocalTrustEventHandler localHandler = 
				new LocalTrustUpdateEventHandler(listener, filter);
		LOG.debug("localHandlers size before register: {}", this.localHandlers.size());
		if (this.localHandlers.add(localHandler)) {
			LOG.debug("Registering local TrustUpdateEvent handler {}" 
					+ " to topics {} using filter '{}'", new Object[] {
							localHandler, Arrays.toString(topics), filter });
			this.eventMgr.subscribeInternalEvent(localHandler,
				topics,	filter);
		} else {
			LOG.warn("TrustUpdateEvent listener " + listener + " already registered to topics "
					+ Arrays.toString(topics));
		}
		LOG.debug("localHandlers size after register: {}", this.localHandlers.size());
	}
	
	private void doUnregisterLocalUpdateListener(
			final ITrustUpdateEventListener listener,
			final String[] topics, final String filter) 
					throws TrustEventMgrException {
		
		LOG.info("Unregistering local TrustUpdateEvent listener {} from topics {}"
				+ " using filter '{}'", new Object[] { listener, 
						Arrays.toString(topics), filter });
		final LocalTrustEventHandler localHandler = 
				new LocalTrustUpdateEventHandler(listener, filter);
		LOG.debug("localHandlers size before unregister: {}", this.localHandlers.size());
		if (this.localHandlers.remove(localHandler)) {
			LOG.debug("Unregistering local TrustUpdateEvent handler {}"  
					+ " from topics {} using filter '{}'", new Object[] {
							localHandler, Arrays.toString(topics), filter });
			this.eventMgr.unSubscribeInternalEvent(localHandler, topics, filter);
		} else {
			LOG.warn("Nothing to do - TrustUpdateEvent listener " + listener 
					+ " was not registered to topics " 
					+ Arrays.toString(topics));
		}
		LOG.debug("localHandlers size after unregister: {}", this.localHandlers.size());
	}
	
	private void doRegisterRemoteUpdateListener(
			final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final String trustorIdFilter, final String trusteeIdFilter)
					throws TrustEventMgrException {
		
		LOG.info("Registering remote TrustUpdateEvent listener {} to topics {}" 
				+ " using filters: trustorIdFilter={}, trusteeIdFilter={}",
				new Object[] { listener, Arrays.toString(topics), 
						trustorIdFilter, trusteeIdFilter });
		try {
			final IIdentity pubsubId;
			if (trustorId != null)
				pubsubId = this.trustNodeMgr.fromId(trustorId);
			else 
				pubsubId = this.trustNodeMgr.getLocalIdentity();
			for (int i = 0; i < topics.length; ++i) {
				final TrustValueType trustValueType = 
						this.createTrustValueType(topics[i]);
				final RemoteTrustUpdateEventHandler remoteHandler =
						new RemoteTrustUpdateEventHandler(listener, trustorIdFilter,
								trusteeIdFilter, trustValueType);
				LOG.debug("remoteHandlers size before register: {}", this.remoteHandlers.size());
				if (this.remoteHandlers.add(remoteHandler)) {
					LOG.debug("Registering remote TrustUpdateEvent handler {}"
							+ " to topic {} of pubsubId {} using filters"
							+ ": trustorIdFilter={}, trusteeIdFilter={}" 
							+ ", trustValueType={}", new Object[] { 
									remoteHandler, topics[i], pubsubId,
									trustorIdFilter, trusteeIdFilter, 
									trustValueType });
					
					try {
						this.pubsubClient.subscriberSubscribe(pubsubId, 
								topics[i], remoteHandler);
					} catch (Exception e) {
						this.remoteHandlers.remove(remoteHandler);
						throw e;
					}
				} else {
					LOG.warn("TrustUpdateEvent listener " + listener 
							+ " already registered to topic " + topics[i]);
				}
				LOG.debug("remoteHandlers size after register: {}", this.remoteHandlers.size());
			}
		} catch (Exception e) {
			throw new TrustEventMgrException("Could not register remote TrustUpdateEvent listener "
					+ listener + " to topics " + Arrays.toString(topics)
					+ ": " + e.getLocalizedMessage(), e);
		}
	}
	
	private void doUnregisterRemoteUpdateListener(
			final ITrustUpdateEventListener listener,
			final String[] topics, final TrustedEntityId trustorId,
			final String trustorIdFilter, final String trusteeIdFilter)
					throws TrustEventMgrException {
		
		LOG.info("Unregistering remote TrustUpdateEvent listener {}"
				+ " from topics {} using filters: trustorIdFilter={}"
				+ ", trusteeIdFilter=", new Object[] { 
						listener, Arrays.toString(topics),
						trustorIdFilter, trusteeIdFilter });
		try {
			final IIdentity pubsubId;
			if (trustorId != null)
				pubsubId = this.trustNodeMgr.fromId(trustorId);
			else 
				pubsubId = this.trustNodeMgr.getLocalIdentity();
			for (int i = 0; i < topics.length; ++i) {
				final TrustValueType trustValueType =
						this.createTrustValueType(topics[i]);
				final RemoteTrustUpdateEventHandler remoteHandler =
						new RemoteTrustUpdateEventHandler(listener, trustorIdFilter,
								trusteeIdFilter, trustValueType);
				LOG.debug("remoteHandlers size before unregister: {}", this.remoteHandlers.size());
				if (this.remoteHandlers.remove(remoteHandler)) {
					LOG.debug("Unregistering remote TrustUpdateEvent handler {}"
							+ " from topic {} of pubsubId {} using filters"
							+ ": trustorIdFilter={}, trusteeIdFilter={}" 
							+ ", trustValueType={}", new Object[] { 
									remoteHandler, topics[i], pubsubId,
									trustorIdFilter, trusteeIdFilter, 
									trustValueType });
					try {
						this.pubsubClient.subscriberUnsubscribe(pubsubId, 
								topics[i], remoteHandler);
					} catch (Exception e) {
						this.remoteHandlers.add(remoteHandler);
						throw e;
					}
				} else {
					LOG.warn("Nothing to do - TrustUpdateEvent listener " + listener 
							+ " was not registered to topic " + topics[i]);
				}
				LOG.debug("remoteHandlers size after unregister: {}", this.remoteHandlers.size());
			}
		} catch (Exception e) {
			throw new TrustEventMgrException("Could not unregister remote TrustUpdateEvent listener "
					+ listener + " from topics " + Arrays.toString(topics)
					+ ": " + e.getLocalizedMessage(), e);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerEvidenceUpdateListener(org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener, java.lang.String[])
	 */
	@Override
	public void registerEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		
		this.doRegisterEvidenceUpdateListener(listener, topics, null, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterEvidenceUpdateListener(org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener, java.lang.String[])
	 */
	@Override
	public void unregisterEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener, 
			final String[] topics) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		
		this.doUnregisterEvidenceUpdateListener(listener, topics, null, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#registerEvidenceUpdateListener(org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener,
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (subjectId == null) {
			throw new NullPointerException("subjectId can't be null");
		}
		if (objectId == null) {
			throw new NullPointerException("objectId can't be null");
		}
		
		this.doRegisterEvidenceUpdateListener(listener, topics, subjectId, objectId);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.event.ITrustEventMgr#unregisterEvidenceUpdateListener(org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener, java.lang.String[], org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener,
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException {
		
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (topics == null) {
			throw new NullPointerException("topics can't be null");
		}
		if (topics.length == 0) {
			throw new IllegalArgumentException("topics can't be empty");
		}
		if (subjectId == null) {
			throw new NullPointerException("subjectId can't be null");
		}
		if (objectId == null) {
			throw new NullPointerException("objectId can't be null");
		}
		
		this.doUnregisterEvidenceUpdateListener(listener, topics, subjectId, objectId);
	}
	
	private void doRegisterEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener,
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException {
		
		final String filter = this.createLocalFilter(subjectId, objectId);
		LOG.info("Registering TrustEvidenceUpdateEvent listener {}" 
					+ " to topics {}", listener, Arrays.toString(topics));
		final LocalTrustEventHandler localHandler = 
				new LocalTrustEvidenceUpdateEventHandler(listener, filter);
		LOG.debug("localHandlers size before register: {}", this.localHandlers.size());
		if (this.localHandlers.add(localHandler)) {
			this.eventMgr.subscribeInternalEvent(localHandler, topics, filter);
		} else {
			LOG.warn("Nothing to do - TrustEvidenceUpdateEvent listener " 
					+ listener + " already registered to topics " 
					+ Arrays.toString(topics));
		}
		LOG.debug("localHandlers size after register: {}", this.localHandlers.size());
	}
	
	private void doUnregisterEvidenceUpdateListener(
			final ITrustEvidenceUpdateEventListener listener,
			final String[] topics, final TrustedEntityId subjectId,
			final TrustedEntityId objectId) throws TrustEventMgrException {
		
		final String filter = this.createLocalFilter(subjectId, objectId);
		LOG.info("Unregistering TrustEvidenceUpdateEvent listener {}" 
				+ " from topics {}", listener, Arrays.toString(topics));
		final LocalTrustEventHandler localHandler = 
				new LocalTrustEvidenceUpdateEventHandler(listener, filter);
		LOG.debug("localHandlers size before unregister: {}", this.localHandlers.size());
		if (this.localHandlers.remove(localHandler)) {
			this.eventMgr.unSubscribeInternalEvent(localHandler, topics, filter);
		} else {
			LOG.warn("Nothing to do - TrustEvidenceUpdateEvent listener " 
					+ listener + " was never registered to topics "
					+ Arrays.toString(topics));
		}
		LOG.debug("localHandlers size after unregister: {}", this.localHandlers.size());
	}
	
	private void postLocalUpdateEvent(final TrustUpdateEvent event, 
			final String[] topics) { 
		
		final String internalEventName = 
				event.getTrustRelationship().getTrusteeId().toString();
		final String internalEventSource = 
				event.getTrustRelationship().getTrustorId().toString();
		for (int i = 0; i < topics.length; ++i) {
			
			final InternalEvent internalEvent = new InternalEvent(
					topics[i], internalEventName, internalEventSource,
					event.getTrustRelationship());

			if (this.eventMgr == null) {
				LOG.error("Could not post local TrustUpdateEvent '"
						+ event + "' to topic " + topics[i]
						+ ": IEventMgr service is not available");
				return;
			}
			try {
				LOG.debug("Posting internal event: type={}, name={}, source={}" 
						+ ", info={} to topic '{}'", new Object[] { 
								internalEvent.geteventType(),
								internalEvent.geteventName(), 
								internalEvent.geteventSource(),
								internalEvent.geteventInfo(), topics[i] });
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
			
			try {
				LOG.debug("Posting internal event: type={}, name={}, source={}" 
						+ ", info={} to topic '{}'", new Object[] { 
								internalEvent.geteventType(),
								internalEvent.geteventName(), 
								internalEvent.geteventSource(),
								internalEvent.geteventInfo(), topics[i] });
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
	
	private void postRemoteUpdateEvent(final TrustUpdateEvent event, 
			final String[] topics) { 
		
		final IIdentity pubsubId;
		final String itemId;
		final TrustUpdateEventBean eventBean;
		
		try {
			pubsubId = this.trustNodeMgr.fromId(event.getTrustRelationship().getTrustorId());
			itemId = event.getTrustRelationship().getTrusteeId().toString();
			eventBean = new TrustUpdateEventBean();
			//TODO eventBean.setTrustRelationship(TrustModelBeanTranslator.getInstance()
			//		.fromTrustRelationship(event.getTrustRelationship()));
			eventBean.setTrustRelationship(new TrustRelationshipBean2());
			eventBean.getTrustRelationship().setTrustorId(TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityId(event.getTrustRelationship().getTrustorId()));
			eventBean.getTrustRelationship().setTrusteeId(TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityId(event.getTrustRelationship().getTrusteeId()));
			eventBean.getTrustRelationship().setTrustValueType(TrustModelBeanTranslator.getInstance()
					.fromTrustValueType(event.getTrustRelationship().getTrustValueType()));
			eventBean.getTrustRelationship().setTrustValue(event.getTrustRelationship().getTrustValue());
		} catch (Exception e) {
			LOG.error("Could not post remote TrustUpdateEvent '" 
					+ event + "' to topics '" + Arrays.toString(topics) + "': "
					+ e.getLocalizedMessage(), e);
			return;
		}
		
		for (int i = 0; i < topics.length; ++i) {

			try {
				LOG.debug("Posting pubsub event: pubsubId={}, itemId={}"
						+ " to topic '{}'", new Object[] { pubsubId, itemId,
								topics[i] });
				this.pubsubClient.publisherPublish(pubsubId, topics[i], 
						itemId, eventBean);
			} catch (Exception e) {

				LOG.error("Could not post pubsub event"
						+ ": pubsubId=" + pubsubId
						+ ", itemId=" + itemId
						+ " to topic " + topics[i]
						+ ": " + e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class LocalTrustEventDispatcher implements Runnable {
		
		private final TrustEvent event;
		
		private final String[] topics;
		
		private LocalTrustEventDispatcher(TrustEvent event, String[] topics) {
			
			this.event = event;
			this.topics = Arrays.copyOf(topics, topics.length);
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (this.event instanceof TrustUpdateEvent) {
				LOG.debug("Posting local TrustUpdateEvent '{}' to topics '{}'",
						this.event, Arrays.toString(this.topics));
				postLocalUpdateEvent((TrustUpdateEvent) event, topics);
			} else if (this.event instanceof TrustEvidenceUpdateEvent) {
				LOG.debug("Posting local TrustEvidenceUpdateEvent '{}' to topics '{}'",
						this.event, Arrays.toString(this.topics));
				postLocalEvidenceUpdateEvent((TrustEvidenceUpdateEvent) event, topics);
			} else {
				LOG.error("Could not post local trust event "
						+ this.event + ": Unsupported TrustEvent implementation '"
						+ this.event.getClass() + "'");
			}
		}
	}
	
	private class RemoteTrustEventDispatcher implements Runnable {
		
		private final TrustEvent event;
		
		private final String[] topics;
		
		private RemoteTrustEventDispatcher(TrustEvent event, String[] topics) {
			
			this.event = event;
			this.topics = Arrays.copyOf(topics, topics.length);
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (this.event instanceof TrustUpdateEvent) {
				LOG.debug("Posting remote TrustUpdateEvent '{}' to topics '{}'",
						this.event, Arrays.toString(this.topics));
				postRemoteUpdateEvent((TrustUpdateEvent) this.event, this.topics);
			} else if (this.event instanceof TrustEvidenceUpdateEvent) {
				LOG.debug("Ignoring TrustEvidenceUpdateEvent '{}'" 
							+ ": Remote publishing not supported", this.event);
				//postLocalEvidenceUpdateEvent((TrustEvidenceUpdateEvent) event, topics);// TODO for testing ppubsub eventing locally
			} else {
				LOG.error("Could not post remote trust event "
						+ this.event + ": Unsupported TrustEvent implementation '"
						+ this.event.getClass() + "'");
			}
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
			
			LOG.debug("Received internal event: type={}, name={}, source={}" 
					+ ", info={}", new Object[] { internalEvent.geteventType(),
							internalEvent.geteventName(), 
							internalEvent.geteventSource(),
							internalEvent.geteventInfo() });
			
			if (!(internalEvent.geteventInfo() instanceof TrustRelationship)) {
				LOG.error("Cannot handle internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ": Unexpected eventInfo: " + internalEvent.geteventInfo());
				return;
			}
			
			final TrustRelationship trustRelationship = 
					(TrustRelationship) internalEvent.geteventInfo();
			final TrustUpdateEvent event = new TrustUpdateEvent(trustRelationship);
			LOG.debug("Forwarding local TrustUpdateEvent {} to listener {}", 
					event, super.listener);
			((ITrustUpdateEventListener) super.listener).onUpdate(event);
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
			
			LOG.debug("Received internal event: type={}, name={}, source={}" 
					+ ", info={}", new Object[] { internalEvent.geteventType(),
							internalEvent.geteventName(), 
							internalEvent.geteventSource(),
							internalEvent.geteventInfo() });
			
			if (!(internalEvent.geteventInfo() instanceof ITrustEvidence)) {
				LOG.error("Cannot handle internal event"
						+ ": type=" + internalEvent.geteventType()
						+ ", name=" + internalEvent.geteventName()
						+ ", source=" + internalEvent.geteventSource()
						+ ": Unexpected eventInfo: " + internalEvent.geteventInfo());
				return;
			}
			final TrustEvidenceUpdateEvent event = 
					new TrustEvidenceUpdateEvent((ITrustEvidence) 
							internalEvent.geteventInfo());
			LOG.debug("Forwarding local TrustEvidenceUpdateEvent {} to listener {}", 
					event, super.listener);
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
	
	private class RemoteTrustUpdateEventHandler implements Subscriber {
		
		/** The listener to forward TrustUpdateEvents. */
		private final ITrustUpdateEventListener listener;
		
		/** The regular expression to match trustor identifiers. */
		private final Pattern trustorIdPattern;
		
		/** The regular expression to match trustee identifiers. */
		private final Pattern trusteeIdPattern;
		
		/** The trust value type to match. */
		final TrustValueType trustValueType;
		
		private RemoteTrustUpdateEventHandler(final ITrustUpdateEventListener listener,
				final String trustorIdFilter, final String trusteeIdFilter,
				final TrustValueType trustValueType) {
			
			this.listener = listener;
			this.trustorIdPattern = Pattern.compile(trustorIdFilter);
			this.trusteeIdPattern = Pattern.compile(trusteeIdFilter);
			this.trustValueType = trustValueType;
		}
		
		/*
		 * @see org.societies.api.comm.xmpp.pubsub.Subscriber#pubsubEvent(org.societies.api.identity.IIdentity, java.lang.String, java.lang.String, java.lang.Object)
		 */
		@Override
		public void pubsubEvent(IIdentity pubsubService, String node, 
				String itemId, Object payload) {
			
			LOG.debug("pubsubEvent: pubsubService={}, node={}, itemId={}"  
					+ ", payload={}", new Object[] { pubsubService, node,
							itemId, payload });
			
			if (itemId == null) {
				LOG.error("Cannot handle remote TrustUpdateEvent: "
						+ "itemId can't be null");
				return;
			}
			if (!(payload instanceof TrustUpdateEventBean)) {
				LOG.error("Cannot handle remote TrustUpdateEvent: "
						+ "Unexpected payload: " + payload);
				return;
			}
				
			try {
				//TODO final TrustRelationship trustRelationship = TrustModelBeanTranslator
				//		.getInstance().fromTrustRelationshipBean(
				//				((TrustUpdateEventBean) payload).getTrustRelationship());
				final TrustRelationship trustRelationship = new TrustRelationship(
						TrustModelBeanTranslator.getInstance().fromTrustedEntityIdBean(((TrustUpdateEventBean) payload).getTrustRelationship().getTrustorId()),
						TrustModelBeanTranslator.getInstance().fromTrustedEntityIdBean(((TrustUpdateEventBean) payload).getTrustRelationship().getTrusteeId()), 
						TrustModelBeanTranslator.getInstance().fromTrustValueTypeBean(((TrustUpdateEventBean) payload).getTrustRelationship().getTrustValueType()), 
						((TrustUpdateEventBean) payload).getTrustRelationship().getTrustValue(), 
						new Date());
				
				// Check if trust relationship matches the filters of this handler.
				final Matcher trustorIdMatcher = this.trustorIdPattern.matcher(
						trustRelationship.getTrustorId().toString());
				final Matcher trusteeIdMatcher = this.trusteeIdPattern.matcher(
						trustRelationship.getTrusteeId().toString());
				LOG.debug("Checking remote TrustUpdateEvent for trust relationship {}"
						+ " using filters: trustorIdPattern={}"
						+ ", trusteeIdPattern={}, trustValueType={}",
						new Object[] { trustRelationship, this.trustorIdPattern.pattern(),
								this.trusteeIdPattern.pattern(), this.trustValueType });
				if (!trustorIdMatcher.matches()
						|| !trusteeIdMatcher.matches() 
						|| this.trustValueType != trustRelationship.getTrustValueType()) {
					LOG.debug("Ignoring remote TrustUpdateEvent for trust relationship {}"
							+ " using filters: trustorIdPattern={}"
							+ ", trusteeIdPattern={}, trustValueType={}",
							new Object[] { trustRelationship, this.trustorIdPattern.pattern(),
									this.trusteeIdPattern.pattern(), this.trustValueType });
					return;
				}
				
				final TrustUpdateEvent event = new TrustUpdateEvent(trustRelationship); 
				LOG.debug("Forwarding remote TrustUpdateEvent {} to listener {}", 
						event, this.listener);
				this.listener.onUpdate(event);
			} catch (Exception e) {
				LOG.error("Cannot handle remote TrustUpdateEvent: "
						+ e.getLocalizedMessage(), e);
			}
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
					+ ((this.trustorIdPattern == null) ? 0 : this.trustorIdPattern.pattern().hashCode());
			result = prime * result
					+ ((this.trusteeIdPattern == null) ? 0 : this.trusteeIdPattern.pattern().hashCode());
			result = prime * result
					+ ((this.trustValueType == null) ? 0 : this.trustValueType.hashCode());
			
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
			
			RemoteTrustUpdateEventHandler other = (RemoteTrustUpdateEventHandler) that;
			if (this.listener == null) {
				if (other.listener != null)
					return false;
			} else if (!this.listener.equals(other.listener))
				return false;
			if (this.trustorIdPattern == null && other.trustorIdPattern != null) {
				return false;
			} else if (this.trustorIdPattern != null && other.trustorIdPattern == null) {
				return false;
			} else if (this.trustorIdPattern != null && other.trustorIdPattern != null) { 
				if (!this.trustorIdPattern.pattern().equals(other.trustorIdPattern.pattern()))
					return false;
			}
			if (this.trusteeIdPattern == null && other.trusteeIdPattern != null) {
				return false;
			} else if (this.trusteeIdPattern != null && other.trusteeIdPattern == null) {
				return false;
			} else if (this.trusteeIdPattern != null && other.trusteeIdPattern != null) { 
				if (!this.trusteeIdPattern.pattern().equals(other.trusteeIdPattern.pattern()))
					return false;
			}
			if (this.trustValueType != other.trustValueType)
				return false;
			
			return true;
		}
	}
	
	private boolean isLocalId(final TrustedEntityId id) {
		
		return (id == null || this.trustNodeMgr.getMyIds().contains(id));
		//return false; // TODO for testing pubsub eventing locally
	}
	
	private String createLocalFilter(final TrustedEntityId trustorId, 
			final TrustedEntityId trusteeId) {
		
		String filter = null;
		if (trustorId != null && trusteeId != null)
			filter = "(&" 
					+ "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId + ")"
					+ "(" + CSSEventConstants.EVENT_NAME + "=" + trusteeId + ")"
					+ ")";
		else if (trustorId != null)
			filter = "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId + ")";
		else if (trusteeId != null)
			filter = "(" + CSSEventConstants.EVENT_NAME + "=" + trusteeId + ")";
		
		return filter;
	}
	
	private String createLocalFilter(final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) {
		
		String filter = null;
		if (trustorId != null && trusteeType != null)
			filter = "(&" 
					+ "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId + ")"
					+ "(" + CSSEventConstants.EVENT_NAME + "=*:" + trusteeType + ":*)"
					+ ")";
		else if (trustorId != null)
			filter = "(" + CSSEventConstants.EVENT_SOURCE + "=" + trustorId + ")";
		else if (trusteeType != null)
			filter = "(" + CSSEventConstants.EVENT_NAME + "=*:" + trusteeType + ":*)";
		
		return filter;
	}
	
	private String createRemoteFilter(final TrustedEntityId id) {
		
		return (id != null) ? id.toString() : "\\S+";
	}
	
	private String createRemoteFilter(final TrustedEntityType type) {
		
		return (type != null) ? "\\S+:" + type + ":\\S+" : "\\S+"; 
	}
	
	private TrustValueType createTrustValueType(final String eventTopic) {
		
		if (TrustEventTopic.DIRECT_TRUST_UPDATED.equals(eventTopic))
			return TrustValueType.DIRECT;
		else if (TrustEventTopic.INDIRECT_TRUST_UPDATED.equals(eventTopic))
			return TrustValueType.INDIRECT;
		else //if (TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED.equals(eventTopic))
			return TrustValueType.USER_PERCEIVED;
	}
	
	private void createRemoteTopics() throws TrustEventMgrException {
		
		LOG.debug("Adding remote trust event payload classes '{}'",
				EVENT_SCHEMA_CLASSES);
		try {
			this.pubsubClient.addSimpleClasses(EVENT_SCHEMA_CLASSES);
		} catch (Exception e) {
			throw new TrustEventMgrException(
					"Failed to add remote remote trust event payload classes '" 
					+ EVENT_SCHEMA_CLASSES + "': " + e.getLocalizedMessage(), e);
		}
		
		for (final TrustedEntityId myTeid : this.trustNodeMgr.getMyIds()) {
			try {
				final IIdentity ownerId = this.trustNodeMgr.fromId(myTeid);
				this.doCreateRemoteTopics(ownerId);
			} catch (Exception e) {
				throw new TrustEventMgrException("Failed to convert TrustedEntityId '" + myTeid
						+ "' to IIdentity: " + e.getLocalizedMessage(), e);
			}
		}
	}
		
	private void doCreateRemoteTopics(final IIdentity ownerId) 
			throws TrustEventMgrException {

		final List<String> existingTopics;
		try {
			existingTopics = this.pubsubClient.discoItems(ownerId, null);
		} catch (Exception e) {
			throw new TrustEventMgrException("Failed to discover topics for IIdentity "
					+ ownerId + ": " + e.getLocalizedMessage(), e);
		}
		
		for (final String topic : EVENT_REMOTE_TOPICS) {	
			if (existingTopics == null || !existingTopics.contains(topic)) {
				LOG.info("Creating pubsub node '{}' for IIdentity '{}'", topic, ownerId);
				try {
					this.pubsubClient.ownerCreate(ownerId, topic);
				} catch (Exception e) {
					throw new TrustEventMgrException("Failed to create topic '"
							+ topic + "' for IIdentity " + ownerId + ": " 
							+ e.getLocalizedMessage(), e);
				}
			} else {
				LOG.info("Found pubsub node '{}' for IIdentity '{}'", topic, ownerId);
			}
		}
	}
}