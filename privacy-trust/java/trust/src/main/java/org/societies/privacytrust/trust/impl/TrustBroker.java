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
import java.util.Dictionary;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemote;
import org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemoteCallback;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.ITrustedEntityIdMgr;
import org.societies.privacytrust.trust.api.TrustedEntityIdMgrException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
public class TrustBroker implements ITrustBroker {
	
	private static final Logger LOG = LoggerFactory.getLogger(TrustBroker.class);
	
	/** The Trusted Entity Id Mgr service reference. */
	@Autowired(required=true)
	private ITrustedEntityIdMgr trustedEntityIdMgr;
	
	/** The Trust Event Mgr service reference. */
	@Autowired(required=true)
	private ITrustEventMgr trustEventMgr;
	
	/** The Trust Repository service reference. */
	@Autowired(required=false)
	private ITrustRepository trustRepo;
	
	/** The Remote Trust Broker service reference. */
	@Autowired(required=false)
	private ITrustBrokerRemote trustBrokerRemote;
	private boolean hasTrustBrokerRemote = false;
			
	TrustBroker() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrust(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Async
	@Override
	public Future<Double> retrieveTrust(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		Double trustValue = null;
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving trust value for entity (" + trustorId 
					+ ", " + trusteeId + ")");
		
		boolean doLocal;
		try {
			doLocal = this.trustedEntityIdMgr.isLocalId(trustorId);
		} catch (TrustedEntityIdMgrException teidme) {
			throw new TrustBrokerException("Could not determine if the retrieve request needs remote handling: "
					+ teidme.getLocalizedMessage());
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("doLocal for trustor " + trustorId + " is '" + doLocal + "'");
		if (doLocal) {
		
			if (this.trustRepo == null)
				throw new TrustBrokerException("Could not retrieve trust value for entity (" 
						+ trustorId	+ ", " + trusteeId 
						+ "): ITrustRepository service is not available");
			final ITrustedEntity entity = this.trustRepo.retrieveEntity(trustorId, trusteeId);
			if (entity != null)
				trustValue = entity.getUserPerceivedTrust().getValue();

		} else {
			
			if (this.hasTrustBrokerRemote && this.trustBrokerRemote != null) {
				
				final RemoteRetrieveCallback callback = new RemoteRetrieveCallback();
				this.trustBrokerRemote.retrieveTrust(trustorId, trusteeId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						trustValue = callback.getResult();
					} catch (InterruptedException ie) {
						
						throw new TrustBrokerException("Interrupted while receiveing trust for entity (" 
						+ trustorId	+ ", " + trusteeId + ")");
					}
				}
				
			} else {
				
				throw new TrustBrokerException("Cannot retrieve trust for entity (" 
						+ trustorId	+ ", " + trusteeId 
						+ "): ITrustBrokerRemote service is not available");
			}
		}
			
		return new AsyncResult<Double>(trustValue);
	}

	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#registerTrustUpdateEventListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerTrustUpdateEventListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId)
					throws TrustException {
		
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		if (this.trustEventMgr == null)
			throw new TrustBrokerException("Could not register trust update listener for entity (" 
					+ trustorId	+ ", " + trusteeId + "): ITrustEventMgr service is not available");
		
		final String[] topics = new String[] { TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED };
		if (LOG.isDebugEnabled())
			LOG.debug("Registering event listener for entity (" + trustorId 
					+ ", " + trusteeId + ") to topics '" + Arrays.toString(topics) + "'");
		this.trustEventMgr.registerUpdateListener(listener, topics, trustorId, trusteeId);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.ITrustBroker#unregisterTrustUpdateEventListener(org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterTrustUpdateEventListener(
			final ITrustUpdateEventListener listener,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId) 
					throws TrustException {
		// TODO Auto-generated method stub
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		if (this.trustEventMgr == null)
			throw new TrustBrokerException("Could not unregister trust update listener for entity (" 
					+ trustorId	+ ", " + trusteeId + "): ITrustEventMgr service is not available");
	}
	
	/**
	 * This method is called when the {@link ITrustBrokerRemote} service is bound.
	 * 
	 * @param trustBrokerRemote
	 *            the {@link ITrustBrokerRemote} service that was bound
	 * @param props
	 *            the set of properties that the {@link ITrustBrokerRemote} service
	 *            was registered with
	 */
	public void bindTrustBrokerRemote(ITrustBrokerRemote trustBrokerRemote, Dictionary<Object,Object> props) {
		
		LOG.info("Binding service reference " + trustBrokerRemote);
		this.hasTrustBrokerRemote = true;
	}
	
	/**
	 * This method is called when the {@link ITrustBrokerRemote} service is unbound.
	 * 
	 * @param trustBrokerRemote
	 *            the {@link ITrustBrokerRemote} service that was unbound
	 * @param props
	 *            the set of properties that the {@link ITrustBrokerRemote} service
	 *            was registered with
	 */
	public void unbindTrustBrokerRemote(ITrustBrokerRemote trustBrokerRemote, Dictionary<Object,Object> props) {
		
		LOG.info("Unbinding service reference " + trustBrokerRemote);
		this.hasTrustBrokerRemote = false;
	}
	
	private class RemoteRetrieveCallback implements ITrustBrokerRemoteCallback {

		private Double trustValue;
		
		/*
		 * @see org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemoteCallback#onRetrievedTrust(java.lang.Double)
		 */
		@Override
		public void onRetrievedTrust(Double value) {
			
			this.trustValue = value;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private Double getResult() {
			
			return this.trustValue;
		}
	}
}