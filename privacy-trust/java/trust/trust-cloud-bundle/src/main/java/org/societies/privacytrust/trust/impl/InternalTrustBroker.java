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
import org.societies.api.privacytrust.trust.TrustAccessControlException;
import org.societies.api.privacytrust.trust.TrustException;
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
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
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
		
		return this.doRetrieveTrustRelationshipsByType(requestor, trustorId, null, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final TrustedEntityId trustorId) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		return this.doRetrieveTrustRelationshipsByType(null, trustorId, null, null);
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	@Async
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			final TrustedEntityId trustorId) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		return this.doRetrieveExtTrustRelationshipsByType(trustorId, null, null);
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
		
		return this.doRetrieveTrustRelationships(requestor, trustorId, trusteeId);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId)
					throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		return this.doRetrieveTrustRelationships(null, trustorId, trusteeId);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	@Async
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId)
					throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		return this.doRetrieveExtTrustRelationships(trustorId, trusteeId);
	}
	
	@Async
	private Future<Set<TrustRelationship>> doRetrieveTrustRelationships(
			Requestor requestor, final TrustedEntityId trustorId, 
			final TrustedEntityId trusteeId) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving all trust relationships between trustor '" 
					+ trustorId	+ "' and trustee '" + trusteeId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		final Set<TrustRelationship> trustRelationships =
				new HashSet<TrustRelationship>();
		try {
			final boolean doLocal = (this.trustNodeMgr.getMyIds().contains(trustorId) 
					&& this.trustNodeMgr.isMaster());
			if (LOG.isDebugEnabled())
				LOG.debug("doLocal for trustor '" + trustorId + "' is " + doLocal);
			if (doLocal) { // L O C A L

				if (this.trustRepo == null)
					throw new TrustBrokerException(
							"Could not retrieve all trust relationships between trustor '" 
							+ trustorId	+ "' and trustee '" + trusteeId 
							+ "' on behalf of requestor '" + requestor + "'" 
							+ "': ITrustRepository service is not available");
				final ITrustedEntity entity = this.trustRepo.retrieveEntity(trustorId, trusteeId);
				if (entity != null) {
					
					if (entity.getDirectTrust().getValue() != null)
						trustRelationships.add(new TrustRelationship(trustorId, trusteeId, 
								TrustValueType.DIRECT, entity.getDirectTrust().getValue(), 
								entity.getDirectTrust().getLastUpdated()));
					if (entity.getIndirectTrust().getValue() != null)
						trustRelationships.add(new TrustRelationship(trustorId, trusteeId, 
								TrustValueType.INDIRECT, entity.getIndirectTrust().getValue(), 
								entity.getIndirectTrust().getLastUpdated()));
					if (entity.getUserPerceivedTrust().getValue() != null)
						trustRelationships.add(new TrustRelationship(trustorId, trusteeId, 
								TrustValueType.USER_PERCEIVED, entity.getUserPerceivedTrust().getValue(), 
								entity.getUserPerceivedTrust().getLastUpdated()));
				}

			} else { // R E M O T E

				if (this.trustBrokerRemoteClient == null)
					throw new TrustBrokerException(
							"Could not retrieve all trust relationships between trustor '" 
							+ trustorId	+ "' and trustee '" + trusteeId 
							+ "' on behalf of requestor '" + requestor + "'"
							+ "': ITrustBrokerRemoteClient service is not available");

				final RemoteClientCallback callback = new RemoteClientCallback();
				this.trustBrokerRemoteClient.retrieveTrustRelationships(
						requestor, trustorId, trusteeId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							trustRelationships.addAll(callback.getTrustRelationships());
						else
							throw callback.getException();
						
					} catch (InterruptedException ie) {

						throw new TrustBrokerException(
								"Interrupted while receiving all trust relationships between trustor '" 
								+ trustorId	+ "' and trustee '" + trusteeId 
								+ "' on behalf of requestor '" + requestor + "'");
					}
				}
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(
					"Could not retrieve all trust relationships between trustor '" 
					+ trustorId	+ "' and trustee '" + trusteeId 
					+ "' on behalf of requestor '" + requestor + "'" 
					+ "': " + sue.getLocalizedMessage(), sue);
		}
			
		return new AsyncResult<Set<TrustRelationship>>(trustRelationships);
	}
	
	@Async
	private Future<Set<ExtTrustRelationship>> doRetrieveExtTrustRelationships(
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId) 
					throws TrustException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving all (extended) trust relationships between trustor '" 
					+ trustorId	+ "' and trustee '" + trusteeId + "'");
		
		if (!this.trustNodeMgr.getMyIds().contains(trustorId))
			throw new TrustAccessControlException("Trustor '" + trustorId 
					+ "' is not recognised as a local CSS");
		
		final Set<ExtTrustRelationship> trustRelationships =
				new LinkedHashSet<ExtTrustRelationship>();
		try {
			final boolean doLocal = (this.trustNodeMgr.isMaster());
			if (LOG.isDebugEnabled())
				LOG.debug("doLocal for trustor '" + trustorId + "' is " + doLocal);
			if (doLocal) { // L O C A L

				if (this.trustRepo == null)
					throw new TrustBrokerException(
							"Could not retrieve all (extended) trust relationships between trustor '" 
							+ trustorId	+ "' and trustee '" + trusteeId
							+ "': ITrustRepository service is not available");
				final ITrustedEntity entity = this.trustRepo.retrieveEntity(trustorId, trusteeId);
				if (entity != null) {
					// TODO Assign trust evidence per trust value type
					final Set<TrustEvidence> trustEvidence = fromITrustedEvidence(entity.getEvidence());
					if (entity.getDirectTrust().getValue() != null)
						trustRelationships.add(new ExtTrustRelationship(trustorId, trusteeId, 
								TrustValueType.DIRECT, entity.getDirectTrust().getValue(), 
								entity.getDirectTrust().getLastUpdated(), trustEvidence));
					if (entity.getIndirectTrust().getValue() != null)
						trustRelationships.add(new ExtTrustRelationship(trustorId, trusteeId, 
								TrustValueType.INDIRECT, entity.getIndirectTrust().getValue(), 
								entity.getIndirectTrust().getLastUpdated(), trustEvidence));
					if (entity.getUserPerceivedTrust().getValue() != null)
						trustRelationships.add(new ExtTrustRelationship(trustorId, trusteeId, 
								TrustValueType.USER_PERCEIVED, entity.getUserPerceivedTrust().getValue(), 
								entity.getUserPerceivedTrust().getLastUpdated(), trustEvidence));
				}

			} else { // R E M O T E  ( I N T R A - C S S )

				if (this.trustBrokerRemoteClient == null)
					throw new TrustBrokerException(
							"Could not retrieve all (extended) trust relationships between trustor '" 
							+ trustorId	+ "' and trustee '" + trusteeId 
							+ "': ITrustBrokerRemoteClient service is not available");
				
				throw new IllegalStateException("Unimplemented remote method call: "
						+ "Retrieve all (extended) trust relationships between trustor '" 
						+ trustorId	+ "' and trustee '" + trusteeId + "'");

				/*final RemoteClientCallback callback = new RemoteClientCallback();
				this.trustBrokerRemoteClient.retrieveTrustRelationships(
						requestor, trustorId, trusteeId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							trustRelationships.addAll(callback.getTrustRelationships());
						else
							throw callback.getException();
						
					} catch (InterruptedException ie) {

						throw new TrustBrokerException(
								"Interrupted while receiving all trust relationships between trustor '" 
								+ trustorId	+ "' and trustee '" + trusteeId + "'");
					}
				}*/
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(
					"Could not retrieve all (extended) trust relationships between trustor '" 
					+ trustorId	+ "' and trustee '" + trusteeId 
					+ "': " + sue.getLocalizedMessage(), sue);
		}
			
		return new AsyncResult<Set<ExtTrustRelationship>>(trustRelationships);
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
		
		return this.doRetrieveTrustRelationship(requestor, trustorId, trusteeId, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<TrustRelationship> retrieveTrustRelationship(
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId, 
			final TrustValueType trustValueType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");	
		
		return this.doRetrieveTrustRelationship(null, trustorId, trusteeId, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationship(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<ExtTrustRelationship> retrieveExtTrustRelationship(
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId, 
			final TrustValueType trustValueType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");	
		
		return this.doRetrieveExtTrustRelationship(trustorId, trusteeId, trustValueType);
	}
	
	@Async
	private Future<TrustRelationship> doRetrieveTrustRelationship(
			Requestor requestor,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving trust relationship of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		TrustRelationship trustRelationship = null;
		try {
			final boolean doLocal = (this.trustNodeMgr.getMyIds().contains(trustorId) 
					&& this.trustNodeMgr.isMaster());
			if (LOG.isDebugEnabled())
				LOG.debug("doLocal for trustor '" + trustorId + "' is " + doLocal);
			if (doLocal) { // L O C A L

				if (this.trustRepo == null)
					throw new TrustBrokerException(
							"Could not retrieve trust relationship of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "' on behalf of requestor '" + requestor + "'" 
							+ "': ITrustRepository service is not available");
				final ITrustedEntity entity = this.trustRepo.retrieveEntity(trustorId, trusteeId);
				if (entity != null) {
					if (TrustValueType.DIRECT == trustValueType) {
						if (entity.getDirectTrust().getValue() != null)
							trustRelationship = new TrustRelationship(trustorId,
									trusteeId, trustValueType, 
									entity.getDirectTrust().getValue(),
									entity.getDirectTrust().getLastUpdated());
					} else if (TrustValueType.INDIRECT == trustValueType) {
						if (entity.getIndirectTrust().getValue() != null)
							trustRelationship = new TrustRelationship(trustorId,
									trusteeId, trustValueType,
									entity.getIndirectTrust().getValue(),
									entity.getIndirectTrust().getLastUpdated());
					} else if (TrustValueType.USER_PERCEIVED == trustValueType) {
						if (entity.getUserPerceivedTrust().getValue() != null)
							trustRelationship = new TrustRelationship(trustorId,
									trusteeId, trustValueType,
									entity.getUserPerceivedTrust().getValue(),
									entity.getUserPerceivedTrust().getLastUpdated());
					} else {
						throw new TrustBrokerException(
								"Could not retrieve trust relationship of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "' on behalf of requestor '" + requestor + "'" 
								+ "': Unsupported trust value type '" + trustValueType + "'");
					}
				}

			} else { // R E M O T E

				if (this.trustBrokerRemoteClient == null)
					throw new TrustBrokerException(
							"Could not retrieve trust relationship of type '" + trustValueType
							+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
							+ "' on behalf of requestor '" + requestor + "'"
							+ "': ITrustBrokerRemoteClient service is not available");

				final RemoteClientCallback callback = new RemoteClientCallback();
				this.trustBrokerRemoteClient.retrieveTrustRelationship(
						requestor, trustorId, trusteeId, trustValueType, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							trustRelationship = callback.getTrustRelationship();
						else
							throw callback.getException();
						
					} catch (InterruptedException ie) {

						throw new TrustBrokerException(
								"Interrupted while receiving trust relationship of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "' on behalf of requestor '" + requestor + "'");
					}
				}
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(
					"Could not retrieve trust relationship of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'" 
					+ "': " + sue.getLocalizedMessage(), sue);
		}
			
		return new AsyncResult<TrustRelationship>(trustRelationship);
	}
	
	@Async
	private Future<ExtTrustRelationship> doRetrieveExtTrustRelationship(
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving (extended) trust relationship of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId + "'");
		
		if (!this.trustNodeMgr.getMyIds().contains(trustorId))
			throw new TrustAccessControlException("Trustor '" + trustorId 
					+ "' is not recognised as a local CSS");
		
		ExtTrustRelationship trustRelationship = null;
		try {
			final boolean doLocal = (this.trustNodeMgr.isMaster());
			if (LOG.isDebugEnabled())
				LOG.debug("doLocal for trustor '" + trustorId + "' is " + doLocal);
			if (doLocal) { // L O C A L

				if (this.trustRepo == null)
					throw new TrustBrokerException(
							"Could not retrieve (extended) trust relationship of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "': ITrustRepository service is not available");
				final ITrustedEntity entity = this.trustRepo.retrieveEntity(trustorId, trusteeId);
				// TODO Assign trust evidence per trust value type
				final Set<TrustEvidence> trustEvidence = fromITrustedEvidence(entity.getEvidence());
				if (entity != null) {
					if (TrustValueType.DIRECT == trustValueType) {
						if (entity.getDirectTrust().getValue() != null)
							trustRelationship = new ExtTrustRelationship(trustorId,
									trusteeId, trustValueType, 
									entity.getDirectTrust().getValue(),
									entity.getDirectTrust().getLastUpdated(), trustEvidence);
					} else if (TrustValueType.INDIRECT == trustValueType) {
						if (entity.getIndirectTrust().getValue() != null)
							trustRelationship = new ExtTrustRelationship(trustorId,
									trusteeId, trustValueType,
									entity.getIndirectTrust().getValue(),
									entity.getIndirectTrust().getLastUpdated(), trustEvidence);
					} else if (TrustValueType.USER_PERCEIVED == trustValueType) {
						if (entity.getUserPerceivedTrust().getValue() != null)
							trustRelationship = new ExtTrustRelationship(trustorId,
									trusteeId, trustValueType,
									entity.getUserPerceivedTrust().getValue(),
									entity.getUserPerceivedTrust().getLastUpdated(), trustEvidence);
					} else {
						throw new TrustBrokerException(
								"Could not retrieve (extended) trust relationship of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "': Unsupported trust value type '" + trustValueType + "'");
					}
				}

			} else { // R E M O T E ( I N T R A - C S S )

				if (this.trustBrokerRemoteClient == null)
					throw new TrustBrokerException(
							"Could not retrieve (extended) trust relationship of type '" + trustValueType
							+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
							+ "': ITrustBrokerRemoteClient service is not available");
				
				throw new IllegalStateException("Unimplemented remote method call: "
						+ "Retrieve all (extended) trust relationships between trustor '" 
						+ trustorId	+ "' and trustee '" + trusteeId + "'");

				/*final RemoteClientCallback callback = new RemoteClientCallback();
				this.trustBrokerRemoteClient.retrieveTrustRelationship(
						requestor, trustorId, trusteeId, trustValueType, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							trustRelationship = callback.getTrustRelationship();
						else
							throw callback.getException();
						
					} catch (InterruptedException ie) {

						throw new TrustBrokerException(
								"Interrupted while receiving trust relationship of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "' on behalf of requestor '" + requestor + "'");
					}
				}*/
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(
					"Could not retrieve (extended) trust relationship of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
					+ "': " + sue.getLocalizedMessage(), sue);
		}
			
		return new AsyncResult<ExtTrustRelationship>(trustRelationship);
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
		
		return this.doRetrieveTrustValue(requestor, trustorId, trusteeId, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustValue(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Double> retrieveTrustValue(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId, final TrustValueType trustValueType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");	
		
		return this.doRetrieveTrustValue(null, trustorId, trusteeId, trustValueType);
	}
	
	@Async
	private Future<Double> doRetrieveTrustValue(Requestor requestor,
			final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId, final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving trust value of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		Double trustValue = null;
		try {
			final boolean doLocal = (this.trustNodeMgr.getMyIds().contains(trustorId) 
					&& this.trustNodeMgr.isMaster());
			if (LOG.isDebugEnabled())
				LOG.debug("doLocal for trustor '" + trustorId + "' is " + doLocal);
			if (doLocal) { // L O C A L

				if (this.trustRepo == null)
					throw new TrustBrokerException(
							"Could not retrieve trust value of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "' on behalf of requestor '" + requestor + "'" 
							+ "': ITrustRepository service is not available");
				final ITrustedEntity entity = this.trustRepo.retrieveEntity(trustorId, trusteeId);
				if (entity != null) {
					if (TrustValueType.DIRECT == trustValueType)
						trustValue = entity.getDirectTrust().getValue();
					else if (TrustValueType.INDIRECT == trustValueType)
						trustValue = entity.getIndirectTrust().getValue();
					else if (TrustValueType.USER_PERCEIVED == trustValueType)
						trustValue = entity.getUserPerceivedTrust().getValue();
					else
						throw new TrustBrokerException(
								"Could not retrieve trust value of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "' on behalf of requestor '" + requestor + "'" 
								+ "': Unsupported trust value type '" + trustValueType + "'");
				}

			} else { // R E M O T E

				if (this.trustBrokerRemoteClient == null)
					throw new TrustBrokerException(
							"Could not retrieve trust value of type '" + trustValueType
							+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
							+ "' on behalf of requestor '" + requestor + "'"
							+ "': ITrustBrokerRemoteClient service is not available");

				final RemoteClientCallback callback = new RemoteClientCallback();
				this.trustBrokerRemoteClient.retrieveTrustValue(
						requestor, trustorId, trusteeId, trustValueType, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							trustValue = callback.getTrustValue();
						else 
							throw callback.getException();
						
					} catch (InterruptedException ie) {

						throw new TrustBrokerException(
								"Interrupted while receiving trust value of type '" + trustValueType 
								+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
								+ "' on behalf of requestor '" + requestor + "'");
					}
				}
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(
					"Could not retrieve trust value of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'" 
					+ "': " + sue.getLocalizedMessage(), sue);
		}
			
		return new AsyncResult<Double>(trustValue);
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
		
		return this.doRetrieveTrustRelationshipsByType(requestor, trustorId, 
				trusteeType, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		return this.doRetrieveTrustRelationshipsByType(null, trustorId, 
				trusteeType, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	@Async
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		return this.doRetrieveExtTrustRelationshipsByType(trustorId, 
				trusteeType, null);
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
		
		return this.doRetrieveTrustRelationshipsByType(requestor, trustorId, 
				null, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		return this.doRetrieveTrustRelationshipsByType(null, trustorId, 
				null, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		return this.doRetrieveExtTrustRelationshipsByType(trustorId, 
				null, trustValueType);
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
		
		return this.doRetrieveTrustRelationshipsByType(requestor, trustorId, 
				trusteeType, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		return this.doRetrieveTrustRelationshipsByType(null, trustorId, 
				trusteeType, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveExtTrustRelationships(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	@Async
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		return this.doRetrieveExtTrustRelationshipsByType(trustorId, 
				trusteeType, trustValueType);
	}
	
	@Async
	private Future<Set<TrustRelationship>> doRetrieveTrustRelationshipsByType(
			Requestor requestor, final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving trust relationships of trustor '" 
					+ trustorId	+ "' with entities of type '" + trusteeType 
					+ "' and trust values of type '" + trustValueType
					+ "' on behalf of requestor '" + requestor + "'");
		
		final Set<TrustRelationship> trustRelationships =
				new HashSet<TrustRelationship>();
		try {
			final boolean doLocal = (this.trustNodeMgr.getMyIds().contains(trustorId) 
					&& this.trustNodeMgr.isMaster());
			if (LOG.isDebugEnabled())
				LOG.debug("doLocal for trustor '" + trustorId + "' is " + doLocal);
			if (doLocal) { // L O C A L

				if (this.trustRepo == null)
					throw new TrustBrokerException(
							"Could not retrieve trust relationships of trustor '" 
							+ trustorId	+ "' with entities of type '" + trusteeType 
							+ "' and trust values of type '" + trustValueType
							+ "' on behalf of requestor '" + requestor + "'" 
							+ "': ITrustRepository service is not available");
				
				final Set<ITrustedEntity> entities = 
						this.trustRepo.retrieveEntities(trustorId, trusteeType, trustValueType);		
				for (final ITrustedEntity entity : entities) {
					
					if (entity.getDirectTrust().getValue() != null
							&& (null == trustValueType || TrustValueType.DIRECT == trustValueType))
						trustRelationships.add(new TrustRelationship(trustorId, 
								entity.getTrusteeId(), TrustValueType.DIRECT, 
								entity.getDirectTrust().getValue(), 
								entity.getDirectTrust().getLastUpdated()));
					if (entity.getIndirectTrust().getValue() != null
							&& (null == trustValueType || TrustValueType.INDIRECT == trustValueType))
						trustRelationships.add(new TrustRelationship(trustorId,
								entity.getTrusteeId(), 
								TrustValueType.INDIRECT, 
								entity.getIndirectTrust().getValue(), 
								entity.getIndirectTrust().getLastUpdated()));
					if (entity.getUserPerceivedTrust().getValue() != null
							&& (null == trustValueType || TrustValueType.USER_PERCEIVED == trustValueType))
						trustRelationships.add(new TrustRelationship(trustorId, 
								entity.getTrusteeId(), 
								TrustValueType.USER_PERCEIVED, 
								entity.getUserPerceivedTrust().getValue(), 
								entity.getUserPerceivedTrust().getLastUpdated()));
				}

			} else { // R E M O T E

				if (this.trustBrokerRemoteClient == null)
					throw new TrustBrokerException(
							"Could not retrieve trust relationships of trustor '" 
							+ trustorId	+ "' with entities of type '" + trusteeType 
							+ "' and trust values of type '" + trustValueType
							+ "' on behalf of requestor '" + requestor + "'"
							+ "': ITrustBrokerRemoteClient service is not available");

				final RemoteClientCallback callback = new RemoteClientCallback();
				if (trusteeType != null && trustValueType == null)
					this.trustBrokerRemoteClient.retrieveTrustRelationships(
							requestor, trustorId, trusteeType, callback);
				else if (trusteeType == null && trustValueType != null)
					this.trustBrokerRemoteClient.retrieveTrustRelationships(
							requestor, trustorId, trustValueType, callback);
				else
					this.trustBrokerRemoteClient.retrieveTrustRelationships(
							requestor, trustorId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							trustRelationships.addAll(callback.getTrustRelationships());
						else
							throw callback.getException();
						
					} catch (InterruptedException ie) {

						throw new TrustBrokerException(
								"Interrupted while receiving trust relationships of trustor '" 
								+ trustorId	+ "' with entities of type '" + trusteeType 
								+ "' and trust values of type '" + trustValueType
								+ "' on behalf of requestor '" + requestor + "'");
					}
				}
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(
					"Could not retrieve trust relationships of trustor '" 
					+ trustorId	+ "' with entities of type '" + trusteeType 
					+ "' and trust values of type '" + trustValueType
					+ "' on behalf of requestor '" + requestor + "'" 
					+ "': " + sue.getLocalizedMessage(), sue);
		}
			
		return new AsyncResult<Set<TrustRelationship>>(trustRelationships);
	}
	
	@Async
	private Future<Set<ExtTrustRelationship>> doRetrieveExtTrustRelationshipsByType(
			final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving (extended) trust relationships of trustor '" 
					+ trustorId	+ "' with entities of type '" + trusteeType 
					+ "' and trust values of type '" + trustValueType + "'");
		
		if (!this.trustNodeMgr.getMyIds().contains(trustorId))
			throw new TrustAccessControlException("Trustor '" + trustorId 
					+ "' is not recognised as a local CSS");
		
		final Set<ExtTrustRelationship> trustRelationships =
				new LinkedHashSet<ExtTrustRelationship>();
		try {
			final boolean doLocal = (this.trustNodeMgr.isMaster());
			if (LOG.isDebugEnabled())
				LOG.debug("doLocal for trustor '" + trustorId + "' is " + doLocal);
			if (doLocal) { // L O C A L

				if (this.trustRepo == null)
					throw new TrustBrokerException(
							"Could not retrieve (extended) trust relationships of trustor '" 
							+ trustorId	+ "' with entities of type '" + trusteeType 
							+ "' and trust values of type '" + trustValueType
							+ "': ITrustRepository service is not available");
				
				final Set<ITrustedEntity> entities = 
						this.trustRepo.retrieveEntities(trustorId, trusteeType, trustValueType);		
				for (final ITrustedEntity entity : entities) {
					
					// TODO Assign trust evidence per trust value type
					final Set<TrustEvidence> trustEvidence = fromITrustedEvidence(entity.getEvidence());
					if (entity.getDirectTrust().getValue() != null
							&& (null == trustValueType || TrustValueType.DIRECT == trustValueType))
						trustRelationships.add(new ExtTrustRelationship(trustorId, 
								entity.getTrusteeId(), TrustValueType.DIRECT, 
								entity.getDirectTrust().getValue(), 
								entity.getDirectTrust().getLastUpdated(), trustEvidence));
					if (entity.getIndirectTrust().getValue() != null
							&& (null == trustValueType || TrustValueType.INDIRECT == trustValueType))
						trustRelationships.add(new ExtTrustRelationship(trustorId,
								entity.getTrusteeId(), 
								TrustValueType.INDIRECT, 
								entity.getIndirectTrust().getValue(), 
								entity.getIndirectTrust().getLastUpdated(), trustEvidence));
					if (entity.getUserPerceivedTrust().getValue() != null
							&& (null == trustValueType || TrustValueType.USER_PERCEIVED == trustValueType))
						trustRelationships.add(new ExtTrustRelationship(trustorId, 
								entity.getTrusteeId(), 
								TrustValueType.USER_PERCEIVED, 
								entity.getUserPerceivedTrust().getValue(), 
								entity.getUserPerceivedTrust().getLastUpdated(), trustEvidence));
				}

			} else { // R E M O T E ( I N T R A - C S S )

				if (this.trustBrokerRemoteClient == null)
					throw new TrustBrokerException(
							"Could not retrieve (extended) trust relationships of trustor '" 
							+ trustorId	+ "' with entities of type '" + trusteeType 
							+ "' and trust values of type '" + trustValueType
							+ "': ITrustBrokerRemoteClient service is not available");

				throw new IllegalStateException("Unimplemented remote method call: "
						+ "Retrieve (extended) trust relationships of trustor '" 
						+ trustorId	+ "' with entities of type '" + trusteeType 
						+ "' and trust values of type '" + trustValueType + "'");
				
				/*final RemoteClientCallback callback = new RemoteClientCallback();
				if (trusteeType != null && trustValueType == null)
					this.trustBrokerRemoteClient.retrieveTrustRelationships(
							requestor, trustorId, trusteeType, callback);
				else if (trusteeType == null && trustValueType != null)
					this.trustBrokerRemoteClient.retrieveTrustRelationships(
							requestor, trustorId, trustValueType, callback);
				else
					this.trustBrokerRemoteClient.retrieveTrustRelationships(
							requestor, trustorId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							trustRelationships.addAll(callback.getTrustRelationships());
						else
							throw callback.getException();
						
					} catch (InterruptedException ie) {

						throw new TrustBrokerException(
								"Interrupted while receiving trust relationships of trustor '" 
								+ trustorId	+ "' with entities of type '" + trusteeType 
								+ "' and trust values of type '" + trustValueType
								+ "' on behalf of requestor '" + requestor + "'");
					}
				}*/
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustBrokerException(
					"Could not retrieve (extended) trust relationships of trustor '" 
					+ trustorId	+ "' with entities of type '" + trusteeType 
					+ "' and trust values of type '" + trustValueType
					+ "': " + sue.getLocalizedMessage(), sue);
		}
			
		return new AsyncResult<Set<ExtTrustRelationship>>(trustRelationships);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		this.doRegisterTrustUpdateListener(
				requestor, listener, trustorId, null, null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		this.doUnregisterTrustUpdateListener(
				requestor, listener, trustorId, null, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId) throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		this.doRegisterTrustUpdateListener(
				null, listener, trustorId, null, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId) throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		this.doUnregisterTrustUpdateListener(
				null, listener, trustorId, null, null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		this.doRegisterTrustUpdateListener(
				requestor, listener, trustorId, trusteeId, null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		this.doUnregisterTrustUpdateListener(
				requestor, listener, trustorId, trusteeId, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId)
					throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		this.doRegisterTrustUpdateListener(
				null, listener, trustorId, trusteeId, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId)
					throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		this.doUnregisterTrustUpdateListener(
				null, listener, trustorId, trusteeId, null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doRegisterTrustUpdateListener(
				requestor, listener, trustorId, trusteeId, trustValueType);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		this.doUnregisterTrustUpdateListener(
				requestor, listener, trustorId, trusteeId, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doRegisterTrustUpdateListener(
				null, listener, trustorId, trusteeId, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) 
					throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		this.doUnregisterTrustUpdateListener(
				null, listener, trustorId, trusteeId, trustValueType);
	}
	
	private void doRegisterTrustUpdateListener(Requestor requestor,
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		if (LOG.isDebugEnabled())
			LOG.debug("Registering for trust value updates of type '" + trustValueType 
					+ "' assigned to entity '"	+ trusteeId	+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		if (this.trustEventMgr == null)
			throw new TrustBrokerException(
					"Could not register for trust value updates of type '" 
					+ trustValueType + "' assigned to entity '"	+ trusteeId	
					+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'" 
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
					"Could not register for trust value updates of type '" 
					+ trustValueType + "' assigned to entity '"	+ trusteeId	
					+ "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'"
					+ ": Unsupported trust value type '" + trustValueType + "'");
		
		if (LOG.isDebugEnabled())
			LOG.debug("Registering event listener for trustor '" + trustorId 
					+ "' and trustee '" + trusteeId + "' to topics '" + Arrays.toString(topics) + "'");
		if (trusteeId == null)
			this.trustEventMgr.registerUpdateListener(listener, topics, trustorId);
		else
			this.trustEventMgr.registerUpdateListener(listener, topics, trustorId, trusteeId);
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
		if (trusteeId == null)
			this.trustEventMgr.unregisterUpdateListener(listener, topics, trustorId);
		else
			this.trustEventMgr.unregisterUpdateListener(listener, topics, trustorId, trusteeId);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		this.doRegisterTrustUpdateListenerByType(
				requestor, listener, trustorId, trusteeType, null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		this.doUnregisterTrustUpdateListenerByType(
				requestor, listener, trustorId, trusteeType, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType)
					throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		this.doRegisterTrustUpdateListenerByType(
				null, listener, trustorId, trusteeType, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType)
					throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		this.doUnregisterTrustUpdateListenerByType(
				null, listener, trustorId, trusteeType, null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doRegisterTrustUpdateListener(
				requestor, listener, trustorId, null, trustValueType);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doUnregisterTrustUpdateListener(
				requestor, listener, trustorId, null, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doRegisterTrustUpdateListener(
				null, listener, trustorId, null, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doUnregisterTrustUpdateListener(
				null, listener, trustorId, null, trustValueType);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doRegisterTrustUpdateListenerByType(
				requestor, listener, trustorId, trusteeType, trustValueType);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doUnregisterTrustUpdateListenerByType(
				requestor, listener, trustorId, trusteeType, trustValueType);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		this.doRegisterTrustUpdateListenerByType(
				null, listener, trustorId, trusteeType, trustValueType);
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		this.doUnregisterTrustUpdateListenerByType(
				null, listener, trustorId, trusteeType, trustValueType);
	}
	
	private void doRegisterTrustUpdateListenerByType(Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();

		if (LOG.isDebugEnabled())
			LOG.debug("Registering for trust value updates of type '" + trustValueType 
					+ "' assigned to entities of type '" + trusteeType + "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'");

		if (this.trustEventMgr == null)
			throw new TrustBrokerException(
					"Could not register for trust value updates of type '" 
					+ trustValueType + "' assigned to entities of type '" 
					+ trusteeType + "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'" 
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
					"Could not register for trust value updates of type '" 
					+ trustValueType + "' assigned to entities of type '" 
					+ trusteeType + "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'"
					+ ": Unsupported trust value type '" + trustValueType + "'");

		if (LOG.isDebugEnabled())
			LOG.debug("Registering event listener for trustor '" + trustorId 
					+ "' and trustee type '" + trusteeType + "' to topics '" + Arrays.toString(topics) + "'");
		this.trustEventMgr.registerUpdateListener(listener, topics, trustorId, trusteeType);
	}
	
	private void doUnregisterTrustUpdateListenerByType(Requestor requestor,
			final ITrustUpdateEventListener listener, 
			final TrustedEntityId trustorId, final TrustedEntityType trusteeType,
			final TrustValueType trustValueType) throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();

		if (LOG.isDebugEnabled())
			LOG.debug("Unregistering from trust value updates of type '" + trustValueType 
					+ "' assigned to entities of type '" + trusteeType + "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'");

		if (this.trustEventMgr == null)
			throw new TrustBrokerException(
					"Could not unregister from trust value updates of type '" 
					+ trustValueType + "' assigned to entities of type '" 
					+ trusteeType + "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'" 
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
					+ trustValueType + "' assigned to entities of type '" 
					+ trusteeType + "' by '" + trustorId 
					+ "' on behalf of requestor '" + requestor + "'"
					+ ": Unsupported trust value type '" + trustValueType + "'");

		if (LOG.isDebugEnabled())
			LOG.debug("Unregistering event listener for trustor '" + trustorId 
					+ "' and trustee type '" + trusteeType + "' from topics '" + Arrays.toString(topics) + "'");
		this.trustEventMgr.unregisterUpdateListener(listener, topics, trustorId, trusteeType);
	}
	
	private class RemoteClientCallback implements ITrustBrokerRemoteClientCallback {

		private Set<TrustRelationship> trustRelationships;
		
		private TrustRelationship trustRelationship;
		
		private Double trustValue;
		
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
	
	private static Set<TrustEvidence> fromITrustedEvidence(Set<ITrustEvidence> evidenceSet) {
		
		final Set<TrustEvidence> result = new LinkedHashSet<TrustEvidence>(evidenceSet.size());
		for (final ITrustEvidence evidence : evidenceSet)
			result.add(new TrustEvidence(evidence.getSubjectId(),
					evidence.getObjectId(), evidence.getType(), 
					evidence.getTimestamp(), evidence.getInfo(), 
					evidence.getSourceId()));
		
		return result;
	}
}