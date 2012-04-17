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

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.TrustUpdateListener;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.model.MalformedTrustedEntityIdException;
import org.societies.privacytrust.trust.api.model.TrustedEntity;
import org.societies.privacytrust.trust.api.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TrustBroker implements ITrustBroker {
	
	private static final Logger LOG = LoggerFactory.getLogger(TrustBroker.class);
	
	@Autowired(required=true)
	private ITrustEventMgr trustEventMgr;
	
	@Autowired(required=true)
	private ITrustRepository trustRepo;
			
	TrustBroker() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrust(java.lang.Object)
	 */
	@Async
	@Override
	public Future<Double> retrieveTrust(Object entityId) throws TrustException {
		
		if (entityId == null)
			throw new NullPointerException("entityId can't be null");
		
		final Double trustValue;
		
		final String entityIdStr = entityId.toString();
		final TrustedEntityType entityType;
		if (entityId instanceof IIdentity) {
			IIdentity id = (IIdentity) entityId;
			if (IdentityType.CSS.equals(id.getType()))
				entityType = TrustedEntityType.CSS;
			else if (IdentityType.CIS.equals(id.getType()))	
				entityType = TrustedEntityType.CIS;
			else
				entityType = TrustedEntityType.LGC;
		} else if (entityId instanceof ServiceResourceIdentifier) {
			entityType = TrustedEntityType.SVC;
		} else {
			entityType = TrustedEntityType.LGC;
		}
		
		final TrustedEntityId teid;
		try {
			teid = new TrustedEntityId(entityType, entityIdStr);
		} catch (MalformedTrustedEntityIdException mteide) {	
			throw new TrustBrokerException("Could not create TrustedEntityId for entity '"
					+ entityId + "'", mteide);
		}
		
		if (this.trustRepo == null)
			throw new TrustBrokerException("Could not retrieve trust value for entity '"
					+ teid + "': ITrustRepositoryService is not available");
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving trust value for entity '"
					+ teid + "' from Trust Repository");
		final TrustedEntity entity = this.trustRepo.retrieveEntity(teid);
		if (entity != null)
			trustValue = entity.getUserPerceivedTrust().getValue();
		else
			trustValue = null;
			
		return new AsyncResult<Double>(trustValue);
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateEventListener(org.societies.api.internal.privacytrust.trust.TrustUpdateListener, java.lang.Object)
	 */
	@Override
	public void registerTrustUpdateEventListener(TrustUpdateListener listener,
			Object entityId) throws TrustException {
		// TODO Auto-generated method stub
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (entityId == null)
			throw new NullPointerException("entityId can't be null");
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateEventListener(org.societies.api.internal.privacytrust.trust.TrustUpdateListener, java.lang.Object)
	 */
	@Override
	public void unregisterTrustUpdateEventListener(TrustUpdateListener listener,
			Object entityId) throws TrustException {
		// TODO Auto-generated method stub
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (entityId == null)
			throw new NullPointerException("entityId can't be null");
	}
}