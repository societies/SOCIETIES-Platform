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
package org.societies.api.privacytrust.trust;

import java.util.concurrent.Future;

import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This interface provides access to the trust values associated with individuals,
 * communities and services.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public interface ITrustBroker {

	/**
	 * Retrieves the trust value of the specified entity.
	 * 
	 * @param teid
	 *            the identifier of the entity whose trust value to retrieve.
	 * @return the trust value of the specified entity.
	 * @throws TrustException if the trust value of the specified entity
	 *         cannot be retrieved
	 * @throws NullPointerException if the specified entity identifier is
	 *         <code>null</code>
	 */
	public Future<Double> retrieveTrust(final TrustedEntityId teid) throws TrustException;
	
	/**
	 * Registers the specified listener for trust value update events associated
	 * with the identified entity.
	 * 
	 * @param listener
	 *            the listener to register for trust update events
	 * @param teid
	 *            the identifier of the entity whose trust value update events
	 *            to register for
	 * @throws TrustException if the specified listener cannot be registered
	 * @throws NullPointerException if any of the specified listener or entity
	 *         identifier is <code>null</code>
	 */
	public void registerTrustUpdateEventListener(final ITrustUpdateEventListener listener,
			final TrustedEntityId teid) throws TrustException;
	
	/**
	 * Unregisters the specified listener from trust value update events associated
	 * with the identified entity.
	 * 
	 * @param listener
	 *            the listener to unregister from trust update events
	 * @param teid
	 *            the identifier of the entity whose trust value update events
	 *            to unregister from
	 * @throws TrustException if the specified listener cannot be unregistered
	 * @throws NullPointerException if any of the specified listener or entity
	 *         identifier is <code>null</code>
	 */
	public void unregisterTrustUpdateEventListener(final ITrustUpdateEventListener listener,
			final TrustedEntityId teid) throws TrustException;
}