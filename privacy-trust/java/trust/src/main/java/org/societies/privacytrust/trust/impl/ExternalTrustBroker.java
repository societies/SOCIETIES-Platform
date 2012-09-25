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
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
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
public class ExternalTrustBroker implements org.societies.api.privacytrust.trust.ITrustBroker {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExternalTrustBroker.class);
	
	/** The internal Trust Broker service reference. */
	@Autowired(required=true)
	private ITrustBroker internalTrustBroker;
			
	ExternalTrustBroker() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrust(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Async
	@Override
	public Future<Double> retrieveTrust(final TrustedEntityId teid) throws TrustException {
					
		return this.internalTrustBroker.retrieveTrust(teid);
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#registerTrustUpdateEventListener(org.societies.api.internal.privacytrust.trust.TrustUpdateListener, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void registerTrustUpdateEventListener(final ITrustUpdateEventListener listener,
			final TrustedEntityId teid) throws TrustException {
		
		this.internalTrustBroker.registerTrustUpdateEventListener(listener, teid);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.ITrustBroker#unregisterTrustUpdateEventListener(org.societies.api.internal.privacytrust.trust.TrustUpdateListener, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void unregisterTrustUpdateEventListener(final ITrustUpdateEventListener listener,
			final TrustedEntityId teid) throws TrustException {
		
		this.internalTrustBroker.unregisterTrustUpdateEventListener(listener, teid);
	}
}