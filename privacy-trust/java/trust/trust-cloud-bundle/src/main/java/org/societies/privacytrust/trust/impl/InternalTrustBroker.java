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
package org.societies.privacytrust.trust.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.NonUniqueTrustQueryResultException;
import org.societies.api.privacytrust.trust.TrustAccessControlException;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ITrustBroker} interface.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.3
 */
@Service
@Lazy(value = false)
public class InternalTrustBroker implements ITrustBroker {
	
	private static final Logger LOG = LoggerFactory.getLogger(InternalTrustBroker.class);
	
	/** The Trust Node Mgr service reference. */
	@Autowired(required=true)
	private ITrustNodeMgr trustNodeMgr;
	
	/** The Trust Event Mgr service reference. */
	@Autowired(required=true)
	private ITrustEventMgr trustEventMgr;
	
	/** The Trust Repository service reference. */
	@Autowired(required=false)
	private ITrustRepository trustRepo;
	
	/** The Trust Broker Remote Client service reference. */
	@Autowired(required=false)
	private ITrustBrokerRemoteClient trustBrokerRemoteClient;
			
	InternalTrustBroker() {
		
		LOG.info("{} instantiated", this.getClass());
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustQuery query) 
					throws TrustException {
		
		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		
		LOG.debug("Retrieving trust relationships matching query '{}'"
				+ " on behalf of requestor '{}'", query, requestor);
		
		if (this.isLocalQuery(query)) {
			// L O C A L
			LOG.debug("query '{}' is LOCAL", query);
			return new AsyncResult<Set<TrustRelationship>>(
					this.retrieveLocalTrustRelationships(requestor, query));
		} else {
			// R E M O T E
			LOG.debug("query '{}' is REMOTE", query);
			return new AsyncResult<Set<TrustRelationship>>(
					this.retrieveRemoteTrustRelationships(requestor, query));
		}
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final TrustQuery query) throws TrustException {
		
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		
		final Requestor requestor = this.trustNodeMgr.getLocalRequestor();
		return this.retrieveTrustRelationships(requestor, query);
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationships(org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			final TrustQuery query) throws TrustException {
		
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		
		LOG.debug("Retrieving extended trust relationships matching query '{}'",
				query);
		
		if (!this.trustNodeMgr.getMyIds().contains(query.getTrustorId())) {
			throw new TrustAccessControlException("Trustor '"
					+ query.getTrustorId() + "' is not recognised as a local CSS");
		}
		
		if (this.isLocalQuery(query)) {
			// L O C A L
			LOG.debug("query '{}' is LOCAL", query);
			return new AsyncResult<Set<ExtTrustRelationship>>(
					this.retrieveLocalExtTrustRelationships(query));
		} else {
			// R E M O T E  ( I N T R A - C S S ) 
			LOG.debug("query '{}' is REMOTE", query);
			return new AsyncResult<Set<ExtTrustRelationship>>(
					this.retrieveRemoteExtTrustRelationships(query));
		}
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<TrustRelationship> retrieveTrustRelationship(
			final Requestor requestor, final TrustQuery query) throws TrustException {
		
		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		
		LOG.debug("Retrieving trust relationship matching query '{}'"
				+ " on behalf of requestor '{}'", query, requestor);
		
		if (this.isLocalQuery(query)) {
			// L O C A L
			LOG.debug("query '{}' is LOCAL", query);
			return new AsyncResult<TrustRelationship>(
					this.retrieveLocalTrustRelationship(requestor, query));
		} else {
			// R E M O T E
			LOG.debug("query '{}' is REMOTE", query);
			return new AsyncResult<TrustRelationship>(
					this.retrieveRemoteTrustRelationship(requestor, query));
		}
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<TrustRelationship> retrieveTrustRelationship(
			final TrustQuery query) throws TrustException {
		
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		
		final Requestor requestor = this.trustNodeMgr.getLocalRequestor();
		return this.retrieveTrustRelationship(requestor, query);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationship(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<ExtTrustRelationship> retrieveExtTrustRelationship(
			final TrustQuery query) throws TrustException {
		
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		
		LOG.debug("Retrieving extended trust relationship matching query '{}'",
				query);
		
		if (!this.trustNodeMgr.getMyIds().contains(query.getTrustorId())) {
			throw new TrustAccessControlException("Trustor '"
					+ query.getTrustorId() + "' is not recognised as a local CSS");
		}
		
		if (this.isLocalQuery(query)) {
			// L O C A L
			LOG.debug("query '{}' is LOCAL", query);
			return new AsyncResult<ExtTrustRelationship>(
					this.retrieveLocalExtTrustRelationship(query));
		} else {
			// R E M O T E  ( I N T R A - C S S ) 
			LOG.debug("query '{}' is REMOTE", query);
			return new AsyncResult<ExtTrustRelationship>(
					this.retrieveRemoteExtTrustRelationship(query));
		}
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustValue(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<Double> retrieveTrustValue(final Requestor requestor, 
			final TrustQuery query)	throws TrustException {
		
		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		if (query.getTrusteeId() == null) {
			throw new IllegalArgumentException("trusteeId in query can't be null");
		}
		if (query.getTrustValueType() == null) {
			throw new IllegalArgumentException("trustValueType in query can't be null");
		}
		
		LOG.debug("Retrieving trust value matching query '{}'"
				+ " on behalf of requestor '{}'", query, requestor);
		
		if (this.isLocalQuery(query)) {
			// L O C A L
			LOG.debug("query '{}' is LOCAL", query);
			return new AsyncResult<Double>(
					this.retrieveLocalTrustValue(requestor, query));
		} else {
			// R E M O T E
			LOG.debug("query '{}' is REMOTE", query);
			return new AsyncResult<Double>(
					this.retrieveRemoteTrustValue(requestor, query));
		}
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustValue(org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<Double> retrieveTrustValue(final TrustQuery query)
			throws TrustException {
		
		if (query == null)
			throw new NullPointerException("query can't be null");	
		
		final Requestor requestor = this.trustNodeMgr.getLocalRequestor();
		return this.retrieveTrustValue(requestor, query);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#removeTrustRelationships(org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	@Async
	public Future<Boolean> removeTrustRelationships(
			final TrustQuery query)	throws TrustException {
		
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		
		LOG.debug("Removing trust relationship matching query '{}'", query);
		
		if (!this.trustNodeMgr.getMyIds().contains(query.getTrustorId())) {
			throw new TrustAccessControlException("Trustor '"
					+ query.getTrustorId() + "' is not recognised as a local CSS");
		}
		
		if (this.isLocalQuery(query)) {
			// L O C A L
			LOG.debug("query '{}' is LOCAL", query);
			return new AsyncResult<Boolean>(
					this.removeLocalTrustRelationships(query));
		} else {
			// R E M O T E  ( I N T R A - C S S ) 
			LOG.debug("query '{}' is REMOTE", query);
			return new AsyncResult<Boolean>(
					this.removeRemoteTrustRelationships(query));
		}
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		return this.retrieveTrustRelationships(requestor, new TrustQuery(trustorId) 
				.setTrusteeType(trusteeType));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		return this.retrieveTrustRelationships(requestor, new TrustQuery(trustorId) 
				.setTrustValueType(trustValueType));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		return this.retrieveTrustRelationships(requestor, new TrustQuery(trustorId) 
				.setTrusteeType(trusteeType).setTrustValueType(trustValueType));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener,
			final TrustQuery query) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		if (query.getTrusteeType() != null) {
			this.doRegisterTrustUpdateListenerByType(requestor, listener,
					query.getTrustorId(), query.getTrusteeType(),
					query.getTrustValueType());
		} else {
			this.doRegisterTrustUpdateListener(requestor, listener, 
					query.getTrustorId(), query.getTrusteeId(), 
					query.getTrustValueType());
		}
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener,
			final TrustQuery query) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		if (query.getTrusteeType() != null) {
			this.doUnregisterTrustUpdateListenerByType(requestor, listener,
					query.getTrustorId(), query.getTrusteeType(), 
					query.getTrustValueType());
		} else {
			this.doUnregisterTrustUpdateListener(requestor, listener, 
					query.getTrustorId(), query.getTrusteeId(), 
					query.getTrustValueType());
		}
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustQuery query) throws TrustException {
		
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		if (query.getTrusteeType() != null) {
			this.doRegisterTrustUpdateListenerByType(null, listener,
					query.getTrustorId(), query.getTrusteeType(),
					query.getTrustValueType());
		} else {
			this.doRegisterTrustUpdateListener(null, listener, 
					query.getTrustorId(), query.getTrusteeId(), 
					query.getTrustValueType());
		}
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustQuery query) throws TrustException {
		
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		if (query.getTrusteeType() != null) {
			this.doUnregisterTrustUpdateListenerByType(null, listener,
					query.getTrustorId(), query.getTrusteeType(),
					query.getTrustValueType());
		} else {
			this.doUnregisterTrustUpdateListener(null, listener, 
					query.getTrustorId(), query.getTrusteeId(), 
					query.getTrustValueType());
		}
	}
	
	private void doRegisterTrustUpdateListener(Requestor requestor,
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		LOG.debug("Registering for trust value updates of type '{}'" 
				+ " assigned to entity '{}' by '{}'" 
				+ " on behalf of requestor '{}'",
				new Object[] {trustValueType, trusteeId, trustorId, requestor });
		
		if (this.trustEventMgr == null) {
			throw new TrustBrokerException(
					"ITrustEventMgr service is not available");
		}
		
		final String[] topics;
		if (null == trustValueType) {
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED,
				TrustEventTopic.INDIRECT_TRUST_UPDATED,
				TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		} else if (TrustValueType.DIRECT == trustValueType) { 
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED };
		} else if (TrustValueType.INDIRECT == trustValueType) {
			topics = new String[] { TrustEventTopic.INDIRECT_TRUST_UPDATED };
		} else if (TrustValueType.USER_PERCEIVED == trustValueType) {
			topics = new String[] { TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		} else {
			throw new TrustBrokerException(
					"Unsupported trust value type '" + trustValueType + "'");
		}
		
		LOG.debug("Registering event listener for trustor '{}'" 
				+ " and trustee '{}' to topics '{}'",
				new Object[] {trustorId, trusteeId, Arrays.toString(topics)});
		if (trusteeId == null) {
			this.trustEventMgr.registerUpdateListener(listener, topics, trustorId);
		} else {
			this.trustEventMgr.registerUpdateListener(listener, topics, trustorId, trusteeId);
		}
	}
	
	private void doUnregisterTrustUpdateListener(Requestor requestor,
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		if (LOG.isDebugEnabled())
			LOG.debug("Unregistering from trust value updates of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		if (this.trustEventMgr == null)
			throw new TrustBrokerException(
					"Could not unregister from trust value updates of type '" 
					+ trustValueType + "' assigned to entity '"	+ trusteeId	
					+ "' by '" + trustorId + "' on behalf of requestor '" + requestor + "'" 
					+ ": ITrustEventMgr service is not available");
		
		final String[] topics;
		if (null == trustValueType)
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED,
				TrustEventTopic.INDIRECT_TRUST_UPDATED,
				TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		else if (TrustValueType.DIRECT == trustValueType)
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED };
		else if (TrustValueType.INDIRECT == trustValueType)
			topics = new String[] { TrustEventTopic.INDIRECT_TRUST_UPDATED };
		else if (TrustValueType.USER_PERCEIVED == trustValueType)
			topics = new String[] { TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		else
			throw new TrustBrokerException(
					"Could not unregister from trust value updates of type '" 
					+ trustValueType + "' assigned to entity '"	+ trusteeId	
					+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'"
					+ ": Unsupported trust value type '" + trustValueType + "'");
		
		if (LOG.isDebugEnabled())
			LOG.debug("Unregistering event listener for trustor '" + trustorId 
					+ "' and trustee '" + trusteeId + "' from topics '" + Arrays.toString(topics) + "'");
		if (trusteeId == null) {
			this.trustEventMgr.unregisterUpdateListener(listener, topics, trustorId);
		} else {
			this.trustEventMgr.unregisterUpdateListener(listener, topics, trustorId, trusteeId);
		}
	}
	
	private void doRegisterTrustUpdateListenerByType(Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();

		LOG.debug("Registering for trust value updates of type '{}'" 
				+ " assigned to entities of type '{}' by '{}'" 
				+ " on behalf of requestor '{}'",
				new Object[] { trustValueType, trusteeType, trustorId, requestor});

		if (this.trustEventMgr == null) {
			throw new TrustBrokerException(
					"ITrustEventMgr service is not available");
		}

		final String[] topics;
		if (null == trustValueType) {
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED,
				TrustEventTopic.INDIRECT_TRUST_UPDATED,
				TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		} else if (TrustValueType.DIRECT == trustValueType) {
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED };
		} else if (TrustValueType.INDIRECT == trustValueType) {
			topics = new String[] { TrustEventTopic.INDIRECT_TRUST_UPDATED };
		} else if (TrustValueType.USER_PERCEIVED == trustValueType) {
			topics = new String[] { TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		} else {
			throw new TrustBrokerException(
					"Unsupported trust value type '" + trustValueType + "'");
		}

		LOG.debug("Registering event listener for trustor '{}'" 
				+ " and trustee type '{}' to topics '{}'",
				new Object[] { trustorId, trusteeType, Arrays.toString(topics)});
		this.trustEventMgr.registerUpdateListener(listener, topics, trustorId, trusteeType);
	}
	
	private void doUnregisterTrustUpdateListenerByType(Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();

		LOG.debug("Unregistering from trust value updates of type '{}'" 
				+ " assigned to entities of type '{}' by '{}'" 
				+ " on behalf of requestor '{}'",
				new Object[] { trustValueType, trusteeType, trustorId, requestor});

		if (this.trustEventMgr == null) {
			throw new TrustBrokerException(
					"ITrustEventMgr service is not available");
		}

		final String[] topics;
		if (null == trustValueType) {
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED,
				TrustEventTopic.INDIRECT_TRUST_UPDATED,
				TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		} else if (TrustValueType.DIRECT == trustValueType) {
			topics = new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED };
		} else if (TrustValueType.INDIRECT == trustValueType) {
			topics = new String[] { TrustEventTopic.INDIRECT_TRUST_UPDATED };
		} else if (TrustValueType.USER_PERCEIVED == trustValueType) {
			topics = new String[] { TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		} else {
			throw new TrustBrokerException("Unsupported trust value type '"
					+ trustValueType + "'");
		}

		LOG.debug("Unregistering event listener for trustor '{}'" 
					+ "' and trustee type '{}' from topics '{}'",
					new Object[] { trustorId, trusteeType, Arrays.toString(topics)});
		this.trustEventMgr.unregisterUpdateListener(listener, topics, trustorId, trusteeType);
	}
	
	/*
	 * TODO Remove once no longer referenced.
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrust(org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Deprecated
	public Future<Double> retrieveTrust(
			final TrustedEntityId trusteeId) throws TrustException {
		
		return new AsyncResult<Double>(null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		return this.retrieveTrustRelationships(requestor, new TrustQuery(trustorId));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		return this.retrieveTrustRelationships(requestor, 
				new TrustQuery(trustorId).setTrusteeId(trusteeId));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<TrustRelationship> retrieveTrustRelationship(
			final Requestor requestor,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");	
		
		return this.retrieveTrustRelationship(requestor, new TrustQuery(trustorId)
				.setTrusteeId(trusteeId).setTrustValueType(trustValueType));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustValue(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Double> retrieveTrustValue(final Requestor requestor,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");	
		
		return this.retrieveTrustValue(requestor, new TrustQuery(trustorId)
				.setTrusteeId(trusteeId).setTrustValueType(trustValueType));
	}
	
	private class RemoteClientCallback implements ITrustBrokerRemoteClientCallback {

		private Set<TrustRelationship> trustRelationships;
		
		private Set<ExtTrustRelationship> extTrustRelationships;
		
		private TrustRelationship trustRelationship;
		
		private ExtTrustRelationship extTrustRelationship;
		
		private Double trustValue;
		
		private boolean removeQueryMatched;
		
		private TrustException trustException;
		
		/*
		 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationships(java.util.Set)
		 */
		@Override
		public void onRetrievedTrustRelationships(Set<TrustRelationship> trustRelationships) {
			
			this.trustRelationships = trustRelationships;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private Set<TrustRelationship> getTrustRelationships() {
			
			return this.trustRelationships;
		}
		
		/*
		 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback#onRetrievedExtTrustRelationships(java.util.Set)
		 */
		@Override
		public void onRetrievedExtTrustRelationships(Set<ExtTrustRelationship> extTrustRelationships) {
			
			this.extTrustRelationships = extTrustRelationships;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private Set<ExtTrustRelationship> getExtTrustRelationships() {
			
			return this.extTrustRelationships;
		}
		
		/*
		 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationship(org.societies.api.privacytrust.trust.model.TrustRelationship)
		 */
		@Override
		public void onRetrievedTrustRelationship(TrustRelationship trustRelationship) {
			
			this.trustRelationship = trustRelationship;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private TrustRelationship getTrustRelationship() {
			
			return this.trustRelationship;
		}
		
		/*
		 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback#onRetrievedExtTrustRelationship(org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship)
		 */
		@Override
		public void onRetrievedExtTrustRelationship(ExtTrustRelationship extTrustRelationship) {
			
			this.extTrustRelationship = extTrustRelationship;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private ExtTrustRelationship getExtTrustRelationship() {
			
			return this.extTrustRelationship;
		}
		
		/*
		 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback#onRetrievedTrustValue(java.lang.Double)
		 */
		@Override
		public void onRetrievedTrustValue(Double trustValue) {
			
			this.trustValue = trustValue;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private Double getTrustValue() {
			
			return this.trustValue;
		}
		
		/*
		 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback#onRemovedTrustRelationships(boolean)
		 */
		@Override
		public void onRemovedTrustRelationships(boolean result) {
			
			this.removeQueryMatched = result;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private boolean isRemoveQueryMatched() {
			
			return this.removeQueryMatched;
		}
		
		/*
		 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback#onException(org.societies.api.privacytrust.trust.TrustException)
		 */
		@Override
		public void onException(TrustException trustException) {
			
			this.trustException = trustException;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private TrustException getException() {
			
			return this.trustException;
		}
	}
	
	private Set<TrustRelationship> retrieveLocalTrustRelationships(
			final Requestor requestor, final TrustQuery query) 
			throws TrustException {
		
		final Set<ITrustedEntity> entities =
				this.retrieveTrustedEntities(query);
		
		return entitiesToRelationships(entities, query);
	}
	
	private Set<ExtTrustRelationship> retrieveLocalExtTrustRelationships(
			final TrustQuery query)	throws TrustException {
		
		final Set<ITrustedEntity> entities =
				this.retrieveTrustedEntities(query);
		
		return entitiesToExtRelationships(entities, query);
	}
	
	private TrustRelationship retrieveLocalTrustRelationship(
			final Requestor requestor, final TrustQuery query) 
			throws TrustException {
		
		final Set<ITrustedEntity> entities =
				this.retrieveTrustedEntities(query);
		
		final Set<TrustRelationship> result = entitiesToRelationships(entities, query); 
		if (result.isEmpty()) {
			return null;
		} else if (result.size() == 1) {
			return result.iterator().next();
		} else {
			throw new NonUniqueTrustQueryResultException("Query returned "
					+ result.size() + " results");
		}
	}
	
	private ExtTrustRelationship retrieveLocalExtTrustRelationship(
			final TrustQuery query) throws TrustException {
		
		final Set<ITrustedEntity> entities =
				this.retrieveTrustedEntities(query);
		
		final Set<ExtTrustRelationship> result = entitiesToExtRelationships(entities, query); 
		if (result.isEmpty()) {
			return null;
		} else if (result.size() == 1) {
			return result.iterator().next();
		} else {
			throw new NonUniqueTrustQueryResultException("Query returned "
					+ result.size() + " results");
		}
	}
	
	private Double retrieveLocalTrustValue(
			final Requestor requestor, final TrustQuery query) 
			throws TrustException {
		
		final Set<ITrustedEntity> entities =
				this.retrieveTrustedEntities(query);
		
		if (entities.isEmpty()) {
			return null;
		} else if (entities.size() == 1) {
			final ITrustedEntity entity = entities.iterator().next();
			if (TrustValueType.DIRECT == query.getTrustValueType()) {
				return entity.getDirectTrust().getValue();
			} else if (TrustValueType.INDIRECT == query.getTrustValueType()) {
				return entity.getIndirectTrust().getValue();
			} else { // if (TrustValueType.USER_PERCEIVED == query.getTrustValueType())
				return entity.getUserPerceivedTrust().getValue();
			}
		} else {
			throw new NonUniqueTrustQueryResultException("Query returned "
					+ entities.size() + " results");
		}
	}
	
	private boolean removeLocalTrustRelationships(final TrustQuery query) 
			throws TrustException {
		
		try {
			if (this.trustRepo == null) {
				throw new TrustBrokerException(
						"ITrustRepository service is not available");
			}
			if (query.getTrusteeId() != null) {
				return this.trustRepo.removeEntity(query.getTrustorId(), 
						query.getTrusteeId());
			} else {
				return this.trustRepo.removeEntities(query.getTrustorId(), 
						query.getTrusteeType(), query.getTrustValueType());
			}
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}
	}
	
	private Set<TrustRelationship> retrieveRemoteTrustRelationships(
			final Requestor requestor, final TrustQuery query) 
			throws TrustException {

		final RemoteClientCallback callback = new RemoteClientCallback();
		try {
			if (this.trustBrokerRemoteClient == null) {
				throw new TrustBrokerException(
						"ITrustBrokerRemoteClient service is not available");
			}
			this.trustBrokerRemoteClient.retrieveTrustRelationships(
					requestor, query, callback);
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}

		synchronized (callback) {
			try {
				callback.wait();
				if (callback.getException() == null) {
					return callback.getTrustRelationships();
				} else {
					throw callback.getException();
				}
			} catch (InterruptedException ie) {
				throw new TrustBrokerException(
						"Interrupted while waiting for remote response: "
								+ ie.getLocalizedMessage(), ie);
			}
		}
	}
	
	private Set<ExtTrustRelationship> retrieveRemoteExtTrustRelationships(
			final TrustQuery query)	throws TrustException {

		final RemoteClientCallback callback = new RemoteClientCallback();
		try {
			if (this.trustBrokerRemoteClient == null) {
				throw new TrustBrokerException(
						"ITrustBrokerRemoteClient service is not available");
			}
			this.trustBrokerRemoteClient.retrieveExtTrustRelationships(
					query, callback);
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}

		synchronized (callback) {
			try {
				callback.wait();
				if (callback.getException() == null) {
					return callback.getExtTrustRelationships();
				} else {
					throw callback.getException();
				}
			} catch (InterruptedException ie) {
				throw new TrustBrokerException(
						"Interrupted while waiting for remote response: "
								+ ie.getLocalizedMessage(), ie);
			}
		}
	}
	
	private TrustRelationship retrieveRemoteTrustRelationship(
			final Requestor requestor, final TrustQuery query) 
			throws TrustException {

		final RemoteClientCallback callback = new RemoteClientCallback();
		try {
			if (this.trustBrokerRemoteClient == null) {
				throw new TrustBrokerException(
						"ITrustBrokerRemoteClient service is not available");
			}
			this.trustBrokerRemoteClient.retrieveTrustRelationship(
					requestor, query, callback);
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}

		synchronized (callback) {
			try {
				callback.wait();
				if (callback.getException() == null) {
					return callback.getTrustRelationship();
				} else {
					throw callback.getException();
				}
			} catch (InterruptedException ie) {
				throw new TrustBrokerException(
						"Interrupted while waiting for remote response: "
								+ ie.getLocalizedMessage(), ie);
			}
		}
	}
	
	private ExtTrustRelationship retrieveRemoteExtTrustRelationship(
			final TrustQuery query)	throws TrustException {

		final RemoteClientCallback callback = new RemoteClientCallback();
		try {
			if (this.trustBrokerRemoteClient == null) {
				throw new TrustBrokerException(
						"ITrustBrokerRemoteClient service is not available");
			}
			this.trustBrokerRemoteClient.retrieveExtTrustRelationship(
					query, callback);
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}

		synchronized (callback) {
			try {
				callback.wait();
				if (callback.getException() == null) {
					return callback.getExtTrustRelationship();
				} else {
					throw callback.getException();
				}
			} catch (InterruptedException ie) {
				throw new TrustBrokerException(
						"Interrupted while waiting for remote response: "
								+ ie.getLocalizedMessage(), ie);
			}
		}
	}
	
	private Double retrieveRemoteTrustValue(
			final Requestor requestor, final TrustQuery query) 
			throws TrustException {

		final RemoteClientCallback callback = new RemoteClientCallback();
		try {
			if (this.trustBrokerRemoteClient == null) {
				throw new TrustBrokerException(
						"ITrustBrokerRemoteClient service is not available");
			}
			this.trustBrokerRemoteClient.retrieveTrustValue(
					requestor, query, callback);
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}

		synchronized (callback) {
			try {
				callback.wait();
				if (callback.getException() == null) {
					return callback.getTrustValue();
				} else {
					throw callback.getException();
				}
			} catch (InterruptedException ie) {
				throw new TrustBrokerException(
						"Interrupted while waiting for remote response: "
								+ ie.getLocalizedMessage(), ie);
			}
		}
	}
	
	private boolean removeRemoteTrustRelationships(final TrustQuery query) 
			throws TrustException {

		final RemoteClientCallback callback = new RemoteClientCallback();
		try {
			if (this.trustBrokerRemoteClient == null) {
				throw new TrustBrokerException(
						"ITrustBrokerRemoteClient service is not available");
			}
			this.trustBrokerRemoteClient.removeTrustRelationships(query, callback);
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}
		
		synchronized (callback) {
			try {
				callback.wait();
				if (callback.getException() == null) {
					return callback.isRemoveQueryMatched();
				} else {
					throw callback.getException();
				}
			} catch (InterruptedException ie) {
				throw new TrustBrokerException(
						"Interrupted while waiting for remote response: "
								+ ie.getLocalizedMessage(), ie);
			}
		}
	}
	
	private Set<ITrustedEntity> retrieveTrustedEntities(final TrustQuery query) 
					throws TrustException {

		final Set<ITrustedEntity> result =
				new LinkedHashSet<ITrustedEntity>();

		try {
			if (this.trustRepo == null) {
				throw new TrustBrokerException(
						"ITrustRepository service is not available");
			}
			if (query.getTrusteeId() != null) {
				final ITrustedEntity trustedEntity =
						this.trustRepo.retrieveEntity(query.getTrustorId(),
								query.getTrusteeId());
				if (trustedEntity != null)
					result.add(trustedEntity);
			} else {
				result.addAll(this.trustRepo.retrieveEntities(query.getTrustorId(),
						query.getTrusteeType(), query.getTrustValueType()));
			} 

		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(sue.getLocalizedMessage(), sue);
		}
		
		return result;
	}
	
	private boolean isLocalQuery(final TrustQuery query) {
		
		return this.trustNodeMgr.getMyIds().contains(query.getTrustorId()) 
				&& this.trustNodeMgr.isMaster();
	}
	
	private static Set<TrustRelationship> entitiesToRelationships(
			final Set<ITrustedEntity> entities, final TrustQuery query) {

		final Set<TrustRelationship> result =
				new LinkedHashSet<TrustRelationship>();

		final TrustValueType trustValueType = query.getTrustValueType();
		for (final ITrustedEntity entity : entities) {
			
			if (entity.getDirectTrust().getValue() != null
					&& (null == trustValueType || TrustValueType.DIRECT == trustValueType)) {
				result.add(new TrustRelationship(entity.getTrustorId(), 
						entity.getTrusteeId(), TrustValueType.DIRECT, 
						entity.getDirectTrust().getValue(), 
						entity.getDirectTrust().getLastUpdated()));
			}
			if (entity.getIndirectTrust().getValue() != null
					&& (null == trustValueType || TrustValueType.INDIRECT == trustValueType)) {
				result.add(new TrustRelationship(entity.getTrustorId(),
						entity.getTrusteeId(), 
						TrustValueType.INDIRECT, 
						entity.getIndirectTrust().getValue(), 
						entity.getIndirectTrust().getLastUpdated()));
			}
			if (entity.getUserPerceivedTrust().getValue() != null
					&& (null == trustValueType || TrustValueType.USER_PERCEIVED == trustValueType)) {
				result.add(new TrustRelationship(entity.getTrustorId(), 
						entity.getTrusteeId(), 
						TrustValueType.USER_PERCEIVED, 
						entity.getUserPerceivedTrust().getValue(), 
						entity.getUserPerceivedTrust().getLastUpdated()));
			}
		}

		return result;
	}
	
	private static Set<ExtTrustRelationship> entitiesToExtRelationships(
			final Set<ITrustedEntity> entities, final TrustQuery query) {

		final Set<ExtTrustRelationship> result =
				new LinkedHashSet<ExtTrustRelationship>(entities.size());

		final TrustValueType trustValueType = query.getTrustValueType();
		for (final ITrustedEntity entity : entities) {

			final Set<TrustEvidence> evidenceSet = iEvidenceToEvidence(entity.getEvidence());
			// TODO Needs optimisation
			final Set<TrustEvidence> directEvidenceSet = new LinkedHashSet<TrustEvidence>();
			final Set<TrustEvidence> indirectEvidenceSet = new LinkedHashSet<TrustEvidence>();
			for (final TrustEvidence evidence : evidenceSet) {
				if (evidence.getSourceId() == null) {
					directEvidenceSet.add(evidence);
				} else {
					indirectEvidenceSet.add(evidence);
				}
			}
			
			if (entity.getDirectTrust().getValue() != null
					&& (null == trustValueType || TrustValueType.DIRECT == trustValueType)) {
				result.add(new ExtTrustRelationship(entity.getTrustorId(), 
						entity.getTrusteeId(), TrustValueType.DIRECT, 
						entity.getDirectTrust().getValue(), 
						entity.getDirectTrust().getLastUpdated(),
						directEvidenceSet));
			}
			if (entity.getIndirectTrust().getValue() != null
					&& (null == trustValueType || TrustValueType.INDIRECT == trustValueType)) {
				result.add(new ExtTrustRelationship(entity.getTrustorId(),
						entity.getTrusteeId(), 
						TrustValueType.INDIRECT, 
						entity.getIndirectTrust().getValue(), 
						entity.getIndirectTrust().getLastUpdated(),
						indirectEvidenceSet));
			}
			if (entity.getUserPerceivedTrust().getValue() != null
					&& (null == trustValueType || TrustValueType.USER_PERCEIVED == trustValueType)) {
				result.add(new ExtTrustRelationship(entity.getTrustorId(), 
						entity.getTrusteeId(), 
						TrustValueType.USER_PERCEIVED, 
						entity.getUserPerceivedTrust().getValue(), 
						entity.getUserPerceivedTrust().getLastUpdated(),
						new HashSet<TrustEvidence>()));
			}
		}

		return result;
	}
	
	private static Set<TrustEvidence> iEvidenceToEvidence(Set<ITrustEvidence> evidenceSet) {
		
		final Set<TrustEvidence> result = new LinkedHashSet<TrustEvidence>(evidenceSet.size());
		for (final ITrustEvidence evidence : evidenceSet) {
			result.add(new TrustEvidence(evidence.getSubjectId(),
					evidence.getObjectId(), evidence.getType(), 
					evidence.getTimestamp(), evidence.getInfo(), 
					evidence.getSourceId()));
		}
		
		return result;
	}
}