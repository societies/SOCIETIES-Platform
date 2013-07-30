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

import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of the external {@link org.societies.api.privacytrust.trust.
 * ITrustBroker ITrustBroker} interface.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
@Service
@Lazy(value = false)
public class TrustBroker implements org.societies.api.privacytrust.trust.ITrustBroker {
	
	private static final Logger LOG = LoggerFactory.getLogger(TrustBroker.class);
	
	/** The Trust Node Mgr service reference. */
	@Autowired(required=true)
	private ITrustNodeMgr trustNodeMgr;
	
	/** The internal Trust Broker service reference. */
	@Autowired(required=true)
	private ITrustBroker internalTrustBroker;
			
	TrustBroker() {
		
		LOG.info("{} instantiated", this.getClass());
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Async
	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustQuery query)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		LOG.debug("Retrieving trust relationships matching query '{}'"	
				+ " on behalf of requestor '{}'", query, requestor);
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationships(
				requestor, query);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Async
	@Override
	public Future<TrustRelationship> retrieveTrustRelationship(
			final Requestor requestor, final TrustQuery query) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		LOG.debug("Retrieving trust relationship matching query '{}'"	
				+ " on behalf of requestor '{}'", query, requestor);
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationship(
				requestor, query);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustValue(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Async
	@Override
	public Future<Double> retrieveTrustValue(final Requestor requestor,
			final TrustQuery query) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		LOG.debug("Retrieving trust value matching query '{}'"
				+ " on behalf of requestor '{}'", query, requestor);
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustValue(requestor, query);
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
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		LOG.debug("Registering for trust update events matching query '{}'"
				+ " on behalf of requestor '{}'", query, requestor);
		
		// TODO access control
		
		this.internalTrustBroker.registerTrustUpdateListener(
				requestor, listener, query);
	}

	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.TrustQuery)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, final TrustQuery query)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		LOG.debug("Unregistering from trust update events matching query '{}'"
				+ " on behalf of requestor '{}'", query, requestor);
		
		// TODO access control
		
		this.internalTrustBroker.unregisterTrustUpdateListener(
				requestor, listener, query);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Async
	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		LOG.debug("Retrieving all trust relationships of trustor '{}'"
				+ " on behalf of requestor '{}'", trustorId, requestor);
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationships(
				requestor, new TrustQuery(trustorId));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Async
	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId, 
			final TrustedEntityId trusteeId) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		LOG.debug("Retrieving all trust relationships between trustor '{}'" 
				+ " and trustee '{}' on behalf of requestor '{}'",
				new Object[] { trustorId, trusteeId, requestor});
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationships(
				requestor, new TrustQuery(trustorId).setTrusteeId(trusteeId));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Async
	@Override
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
		
		LOG.debug("Retrieving trust relationship of type '{}'" 
					+ " between trustor '{}' and trustee '{}'" 
					+ " on behalf of requestor '{}'",
					new Object[] { trustValueType, trustorId, trusteeId, requestor });
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationship(
				requestor, new TrustQuery(trustorId).setTrusteeId(trusteeId)
				.setTrustValueType(trustValueType));
	}

	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustValue(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Async
	@Override
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
		
		LOG.debug("Retrieving trust value of type '{}'" 
				+ " between trustor '{}' and trustee '{}'" 
				+ " on behalf of requestor '{}'",
				new Object[] { trustValueType, trustorId, trusteeId, requestor });
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustValue(
				requestor, new TrustQuery(trustorId).setTrusteeId(trusteeId)
				.setTrustValueType(trustValueType));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Async
	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		LOG.debug("Retrieving trust relationships of trustor '{}'" 
				+ " with entities of type '{}'"
				+ " on behalf of requestor '{}'",
				new Object[] { trustorId, trusteeType, requestor });
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationships(
				requestor, new TrustQuery(trustorId).setTrusteeType(trusteeType));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Async
	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		LOG.debug("Retrieving trust relationships of trustor '{}'" 
				+ " with trust values of type '{}'"
				+ " on behalf of requestor '{}'",
				new Object[] {trustorId, trustValueType, requestor});
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationships(
				requestor, new TrustQuery(trustorId).setTrustValueType(trustValueType));
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Async
	@Override
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
		
		LOG.debug("Retrieving trust relationships of trustor '{}'" 
					+ " with entities of type '{}'" 
					+ " and trust values of type '{}'"
					+ " on behalf of requestor '{}'",
					new Object[] { trustorId, trusteeType, trustValueType, requestor });
		
		// TODO access control
		
		return this.internalTrustBroker.retrieveTrustRelationships(
				requestor, new TrustQuery(trustorId).setTrusteeType(trusteeType)
				.setTrustValueType(trustValueType));
	}
}