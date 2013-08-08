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
package org.societies.privacytrust.trust.api.broker.remote;

import java.util.Set;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.TrustRelationship;

/**
 * This interface provides access to the trust values associated with individuals,
 * communities and services.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public interface ITrustBrokerRemoteClient {
	
	/**
	 * Retrieves the trust relationships matching the supplied trust query. 
	 * The method returns an <i>empty</i> set if no matching trust relationship
	 * is found. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationships(Set)}
	 * method.
	 *
	 * @param requestor 
	 *            (required) the identifier of the entity on whose behalf to
	 *            request the trust relationships specified in the query.
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationships.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.2
	 * @see TrustRelationship
	 */
	public void retrieveTrustRelationships(final Requestor requestor,
			final TrustQuery query, final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException;
	
	/**
	 * Retrieves the extended trust relationships matching the supplied trust 
	 * query. Compared to {@link #retrieveTrustRelationships}, the 
	 * relationships returned by this method also include the related trust 
	 * evidence. However, the trustor specified in the trust query <i>must</i>
	 * identity the local CSS, otherwise an exception will be thrown. The
	 * method returns an <i>empty</i> set if no matching trust relationship is
	 * found. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedExtTrustRelationships}
	 * method.
	 *
	 * @param query
	 *            (required) the query encapsulating the request for the 
	 *            extended trust relationships.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.2
	 * @see ExtTrustRelationship
	 */
	public void retrieveExtTrustRelationships(final TrustQuery query, 
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException;
	
	/**
	 * Retrieves the trust relationship matching the supplied trust query. The
	 * method returns <code>null</code> if no matching trust relationship is 
	 * found. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationship(TrustRelationship)}
	 * method.
	 * 
	 * @param requestor
	 *            (required) the identifier of the entity on whose behalf to
	 *            request the trust relationship specified in the query.
	 * @param query
	 *            (required) the query encapsulating the request for the 
	 *            extended trust relationship.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationship cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.2
	 * @see TrustRelationship
	 */
	public void retrieveTrustRelationship(final Requestor requestor,
			final TrustQuery query, final ITrustBrokerRemoteClientCallback callback)
					throws TrustException;
	
	/**
	 * Retrieves the extended trust relationship matching the supplied trust
	 * query. Compared to {@link #retrieveTrustRelationship}, the relationship
	 * returned by this method also includes the related trust evidence. 
	 * However, the trustor specified in the trust query <i>must</i>
	 * identity the local CSS, otherwise an exception will be thrown.The
	 * method returns <code>null</code> if no matching trust relationship is 
	 * found. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedExtTrustRelationship}
	 * method.
	 * 
	 * @param query
	 *            (required) the query encapsulating the request for the 
	 *            extended trust relationship.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationship cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.2
	 * @see ExtTrustRelationship
	 */
	public void retrieveExtTrustRelationship(final TrustQuery query,
			final ITrustBrokerRemoteClientCallback callback)
					throws TrustException;
	
	/**
	 * Retrieves the trust value matching the supplied trust query. The method
	 * returns <code>null</code> if no matching trust value is found. The
	 * result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustValue(Double)}
	 * method.
	 * 
	 * @param requestor
	 *            (required) the identifier of the entity on whose behalf to
	 *            request the trust value specified in the query.
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            value.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust value cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.2
	 */
	public void retrieveTrustValue(final Requestor requestor, 
			final TrustQuery query,	final ITrustBrokerRemoteClientCallback callback)
					throws TrustException;
	
	/**
	 * Removes the trust relationships matching the supplied trust query. The
	 * trustor specified in the trust query <i>must</i> identity the local CSS,
	 * otherwise an exception will be thrown. The method returns 
	 * <code>true</code> if any trust relationships matched the specified 
	 * query. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRemovedTrustRelationships}
	 * method.
	 *
	 * @param query
	 *            (required) the query identifying the trust relationships to
	 *            be removed.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be removed.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.2
	 * @see TrustRelationship
	 */
	public void removeTrustRelationships(final TrustQuery query, 
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException;
}