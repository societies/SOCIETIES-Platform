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
package org.societies.api.internal.privacytrust.trust;

import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.NonUniqueTrustQueryResultException;
import org.societies.api.privacytrust.trust.TrustAccessControlException;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This interface extends the external {@link org.societies.api.privacytrust.
 * trust.ITrustBroker ITrustBroker} interface and provides platform services with access to the
 * trust values associated with individuals, communities and services.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.2
 */
public interface ITrustBroker extends org.societies.api.privacytrust.trust.ITrustBroker {
	
	/**
	 * Retrieves the trust relationships matching the supplied trust query. The
	 * method returns an <i>empty</i> list if no matching trust relationship is
	 * found. 
	 * 
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationships.
	 * @return the trust relationships matching the specified query.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the requested trust relationships cannot be 
	 *         retrieved.
	 * @throws NullPointerException if any of the required parameters is 
	 *         <code>null</code>.
	 * @since 1.1
	 */
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final TrustQuery query)	throws TrustException;
	
	/**
	 * Retrieves the trust relationships matching the supplied trust query. 
	 * Compared to {@link #retrieveTrustRelationships(TrustQuery)}, the
	 * relationships returned by this method also include the related trust 
	 * evidence. However, the trustor specified in the trust query <i>must</i>
	 * identity the local CSS, otherwise an exception will be thrown. The
	 * method returns an <i>empty</i> list if no matching trust relationship is
	 * found.
	 *
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationships.
	 * @return the trust relationships matching the supplied trust query.
	 * @throws TrustAccessControlException if the specified trustor identifies 
	 *         a <i>non-local</i> CSS.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the required parameters is 
	 *         <code>null</code>.
	 * @since 1.1
	 */
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			final TrustQuery query) throws TrustException;
	
	/**
	 * Retrieves the trust relationship matching the supplied trust query. The
	 * method returns <code>null</code> if no matching trust relationship is
	 * found. 
	 * 
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationship.
	 * @return the trust relationship matching the specified query.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationship.
	 * @throws TrustException if the requested trust relationship cannot be 
	 *         retrieved.
	 * @throws NonUniqueTrustQueryResultException if the query returns multiple
	 *         results.
	 * @throws NullPointerException if any of the required parameters is 
	 *         <code>null</code>.
	 * @since 1.1
	 */
	public Future<TrustRelationship> retrieveTrustRelationship(
			final TrustQuery query)	throws TrustException;
	
	/**
	 * Retrieves the trust relationship matching the supplied trust query. 
	 * Compared to {@link #retrieveTrustRelationship(TrustQuery)}, the
	 * relationship returned by this method also includes the related trust 
	 * evidence. However, the trustor specified in the trust query <i>must</i>
	 * identity the local CSS, otherwise an exception will be thrown. The
	 * method returns <code>null</null> if no matching trust relationship is
	 * found.
	 *
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationship.
	 * @return the trust relationship matching the supplied trust query.
	 * @throws TrustAccessControlException if the specified trustor identifies 
	 *         a <i>non-local</i> CSS.
	 * @throws TrustException if the trust relationship cannot be retrieved.
	 * @throws NullPointerException if any of the required parameters is 
	 *         <code>null</code>.
	 * @since 1.1
	 */
	public Future<ExtTrustRelationship> retrieveExtTrustRelationship(
			final TrustQuery query) throws TrustException;
	
	/**
	 * Retrieves the trust value matching the supplied trust query. The method
	 * returns <code>null</code> if no matching trust value is found. 
	 * 
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            value.
	 * @return the trust value matching the specified query.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust value.
	 * @throws TrustException if the requested trust value cannot be retrieved.
	 * @throws NonUniqueTrustQueryResultException if the query returns multiple
	 *         results.
	 * @throws NullPointerException if any of the required parameters is 
	 *         <code>null</code>.
	 * @since 1.1
	 */
	public Future<Double> retrieveTrustValue(final TrustQuery query)
			throws TrustException;
	
	/**
	 * Removes the trust relationships matching the supplied trust query. The
	 * method returns <code>true</code> if any trust relationships matched the
	 * specified query. If the specified requestor is denied access to the 
	 * requested trust relationships, a TrustAccessControlException is thrown.
	 * 
	 * @param query
	 *            (required) the query identifying the trust relationships to
	 *            be removed.
	 * @return <code>true</code> if any trust relationships matched the
	 *         specified query; <code>false</code> otherwise. 
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the requested trust relationships cannot be 
	 *         removed.
	 * @throws NullPointerException if any of the required parameters is 
	 *         <code>null</code>.
	 * @since 1.2
	 */
	public Future<Boolean> removeTrustRelationships(
			final TrustQuery query)	throws TrustException;
	
	/**  
	 * Registers the specified listener for trust update events matching the 
	 * supplied trust query.
	 * <p>
	 * To unregister the specified listener, use the
	 * {@link #unregisterTrustUpdateListener(Requestor, ITrustUpdateEventListener, TrustQuery)}
	 * method.
	 * 
	 * @param listener
	 *            (required) the listener to register for trust update events.
	 * @param query
	 *            (required) the query specifying the trust update events to 
	 *            match.
	 * @throws TrustAccessControlException if the specified requestor is not
	 *         allowed to register for updates of the specified trust 
	 *         relationships.
	 * @throws TrustException if the specified listener cannot be registered
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @see #unregisterTrustUpdateListener(ITrustUpdateEventListener, TrustQuery) 
	 * @since 1.1
	 */
	public void registerTrustUpdateListener(
			final ITrustUpdateEventListener listener, final TrustQuery query)
					throws TrustException;
	
	/**
	 * Unregisters the specified listener from trust update events matching the 
	 * supplied trust query.
	 * <p>
	 * The method has no effect if the specified listener has not been 
	 * previously registered using the 
	 * {@link #registerTrustUpdateListener(ITrustUpdateEventListener, TrustQuery)}
	 * method.
	 * 
	 * @param listener
	 *            (required) the listener to unregister from trust update 
	 *            events.
	 * @param query
	 *            (required) the query specifying the trust update events to 
	 *            match.
	 * @throws TrustAccessControlException if the specified requestor is not
	 *         allowed to unregister from updates of the specified trust 
	 *         relationships.
	 * @throws TrustException if the specified listener cannot be unregistered
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @see #registerTrustUpdateListener(Requestor, ITrustUpdateEventListener, TrustQuery)
	 * @since 1.1
	 */
	public void unregisterTrustUpdateListener(
			final ITrustUpdateEventListener listener,
			final TrustQuery query)	throws TrustException;

	/**
	 * 
	 * @param trusteeId
	 * @return
	 * @throws TrustException
	 * @deprecated As of 0.5, use {@link #retrieveTrust(TrustedEntityId, TrustedEntityId)}.
	 */
	@Deprecated
	public Future<Double> retrieveTrust(
			final TrustedEntityId trusteeId) throws TrustException;
}
