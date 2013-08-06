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

import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * This interface provides access to the {@link TrustRelationship trust relationships} 
 * established with other individuals (CSSs), communities (CISs) or services. 
 * The trust value associated with the relationship between a trustor and a trustee 
 * can be {@link TrustValueType#DIRECT direct}, {@link TrustValueType#INDIRECT indirect}
 * or {@link TrustValueType#USER_PERCEIVED user-perceived}.
 * <p>
 * For example, to retrieve all trust relationships established by a particular
 * trustor, the following {@link TrustQuery trust query} can be used:
 * <pre>
 * final TrustQuery query = new TrustQuery(trustorId);
 * Set&lt;TrustRelationship&gt; trs = trustBroker.retrieveTrustRelationships(query); 
 * </pre>
 * To retrieve the trust relationships established by a particular
 * trustor with any CIS, the following trust query can be used:
 * <pre>
 * final TrustQuery query = new TrustQuery(trustorId).setTrusteeType(TrustedEntityType.CIS);
 * Set&lt;TrustRelationship&gt; trs = trustBroker.retrieveTrustRelationships(query); 
 * </pre>
 * If the trust consumer is only interested in the trust relationship of a 
 * certain type between a trustor and a trustee, the following trust query can
 * be used:
 * <pre>
 * final TrustQuery query = new TrustQuery(trustorId).setTrusteeId(trusteeId)
 *                            .setTrustValueType(TrustValueType.USER_PERCEIVED);
 * TrustRelationship tr = trustBroker.retrieveTrustRelationship(query); 
 * </pre>
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @see TrustRelationship
 * @since 0.4
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface ITrustBroker {
	
	/**
	 * Retrieves the trust relationships matching the supplied trust query. The
	 * method returns an <i>empty</i> list if no matching trust relationship is
	 * found. 
	 * 
	 * @param requestor
	 *            (required) the identifier of the entity on whose behalf to
	 *            request the trust relationships specified in the query.
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
			final Requestor requestor, final TrustQuery query) 
					throws TrustException;
	
	/**
	 * Retrieves the trust relationship matching the supplied trust query. The
	 * method returns <code>null</code> if no matching trust relationship is
	 * found. 
	 * 
	 * @param requestor
	 *            (required) the identifier of the entity on whose behalf to
	 *            request the trust relationship specified in the query.
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
			final Requestor requestor, final TrustQuery query) 
					throws TrustException;
	
	/**
	 * Retrieves the trust value matching the supplied trust query. The method
	 * returns <code>null</code> if no matching trust value is found. 
	 * 
	 * @param requestor
	 *            (required) the identifier of the entity on whose behalf to
	 *            request the trust value specified in the query.
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
	public Future<Double> retrieveTrustValue(final Requestor requestor, 
			final TrustQuery query)	throws TrustException;
	
	/**  
	 * Registers the specified listener for trust update events matching the 
	 * supplied trust query.
	 * <p>
	 * To unregister the specified listener, use the
	 * {@link #unregisterTrustUpdateListener(Requestor, ITrustUpdateEventListener, TrustQuery)}
	 * method.
	 * 
	 * @param requestor
	 *            (required) the identifier of the entity on whose behalf to
	 *            register for trust update events.
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
	 * @see #unregisterTrustUpdateListener(Requestor, ITrustUpdateEventListener, 
	 * @since 1.1
	 */
	public void registerTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener, final TrustQuery query)
					throws TrustException;
	
	/**
	 * Unregisters the specified listener from trust update events matching the 
	 * supplied trust query.
	 * <p>
	 * The method has no effect if the specified listener has not been 
	 * previously registered using the 
	 * {@link #registerTrustUpdateListener(Requestor, ITrustUpdateEventListener, TrustQuery)}
	 * method.
	 * 
	 * @param requestor
	 *            (required) the identifier of the entity on whose behalf to
	 *            unregister from trust update events.
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
	public void unregisterTrustUpdateListener(final Requestor requestor,
			final ITrustUpdateEventListener listener,
			final TrustQuery query)	throws TrustException;
	
	/**
	 * Retrieves all trust relationships of the specified trustor. More 
	 * specifically, the method returns all {@link TrustValueType#DIRECT
	 * direct}, {@link TrustValueType#INDIRECT indirect}, as well as,
	 * {@link TrustValueType#USER_PERCEIVED user-perceived} trust relationships
	 * established by the identified <i>local</i> or <i>remote</i> CSS 
	 * (trustor). The method returns an <i>empty</i> set if the identified 
	 * trustor has not established any trust relationships. 
	 *
	 * @param requestor 
	 *            (required) the identifier of the entity on whose behalf to
	 *            request the specified trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust
	 *            relationships to retrieve.
	 * @return all trust relationships of the specified trustor.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the required parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 * @deprecated As of 1.1, use {@link #retrieveTrustRelationships(Requestor, TrustQuery)}.
	 */
	@Deprecated
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId)
					throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor with the
	 * supplied trustee. The method returns an <i>empty</i> set if no trust
	 * relationships exist between the identified trustor and trustee.
	 *
	 * @param requestor 
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust 
	 *            relationships to retrieve.
	 * @param trusteeId
	 *            (required) the identifier of the entity trusted by the 
	 *            specified trustor.
	 * @return the trust relationships of the specified trustor with the 
	 *         supplied trustee.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 * @deprecated As of 1.1, use {@link #retrieveTrustRelationships(Requestor, TrustQuery)}.
	 */
	@Deprecated
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustException;
	
	/**
	 * Retrieves the trust relationship of the specified type which the given
	 * trustor has established with the supplied trustee. The method returns 
	 * <code>null</code> if no trust relationship of the specified type has
	 * been established with the supplied trustee by the given trustor.
	 * 
	 * @param requestor
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity which has assigned
	 *            the trust value to retrieve.
	 * @param trusteeId
	 *            (required) the identifier of the entity whose trust value to
	 *            retrieve.
	 * @param trustValueType
	 *            (required) the type of the trust value, i.e. one of 
	 *            {@link TrustValueType#DIRECT DIRECT},
	 *            {@link TrustValueType#INDIRECT INDIRECT}, or
	 *            {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}.
	 * @return the trust relationship of the specified type which the given
	 *         trustor has established with the supplied trustee.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the trust relationship cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.0
	 * @deprecated As of 1.1, use {@link #retrieveTrustRelationship(Requestor, TrustQuery)}.
	 */
	@Deprecated
	public Future<TrustRelationship> retrieveTrustRelationship(final Requestor requestor, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException;
	
	/**
	 * Retrieves the trust value of the specified type which the given trustor
	 * has assigned to the supplied trustee. The method returns 
	 * <code>null</code> if no trust value of the specified type has been
	 * assigned to the supplied trustee by the given trustor.
	 * 
	 * @param requestor
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity which has assigned
	 *            the trust value to retrieve.
	 * @param trusteeId
	 *            (required) the identifier of the entity whose trust value to
	 *            retrieve.
	 * @param trustValueType
	 *            (required) the type of the trust value, i.e. one of 
	 *            {@link TrustValueType#DIRECT DIRECT},
	 *            {@link TrustValueType#INDIRECT INDIRECT}, or
	 *            {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}.
	 * @return the trust value of the specified type which the given trustor
	 *         has assigned to the supplied trustee.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the trust value cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.0
	 * @deprecated As of 1.1, use {@link #retrieveTrustValue(Requestor, TrustQuery)}.
	 */
	@Deprecated
	public Future<Double> retrieveTrustValue(final Requestor requestor, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType) throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the {@link TrustedEntityType type}
	 * of the entities trusted by the trustor is also specified. The method
	 * returns an <i>empty</i> set if no trust relationships match
	 * the supplied criteria.
	 *
	 * @param requestor 
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity which has established
	 *            the trust relationships to retrieve.
	 * @param trusteeType
	 *            (required) the {@link TrustedEntityType type} of the trusted
	 *            entities to match, e.g. {@link TrustedEntityType#CSS CSS}.
	 * @return the trust relationships of the specified trustor that match the
	 *         specified criteria.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 * @deprecated As of 1.1, use {@link #retrieveTrustRelationships(Requestor, TrustQuery)}.
	 */
	@Deprecated
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType) throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the trust value type, i.e. one of
	 * {@link TrustValueType#DIRECT DIRECT}, 
	 * {@link TrustValueType#INDIRECT INDIRECT}, or
	 * {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}, is also specified.
	 * The method returns an <i>empty</i> set if no trust relationships match
	 * the supplied criteria.
	 *
	 * @param requestor 
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity which has established
	 *            the trust relationships to retrieve.
	 * @param trustValueType
	 *            (required) the type of the trust value, i.e. one of 
	 *            {@link TrustValueType#DIRECT DIRECT},
	 *            {@link TrustValueType#INDIRECT INDIRECT}, or
	 *            {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}.
	 * @return the trust relationships of the specified trustor that match the
	 *         specified criteria.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 * @deprecated As of 1.1, use {@link #retrieveTrustRelationships(Requestor, TrustQuery)}.
	 */
	@Deprecated
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustValueType trustValueType) throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the {@link TrustedEntityType type}
	 * of the entities trusted by the trustor and the trust value type, i.e. one of 
	 * {@link TrustValueType#DIRECT DIRECT}, 
	 * {@link TrustValueType#INDIRECT INDIRECT}, or
	 * {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}, are also specified.
	 * The method returns an <i>empty</i> set if no trust relationships match
	 * the supplied criteria.
	 *
	 * @param requestor 
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity which has established
	 *            the trust relationships to retrieve.
	 * @param trusteeType
	 *            (required) the {@link TrustedEntityType type} of the trusted
	 *            entities to match, e.g. {@link TrustedEntityType#CSS CSS}.
	 * @param trustValueType
	 *            (required) the type of the trust value, i.e. one of 
	 *            {@link TrustValueType#DIRECT DIRECT},
	 *            {@link TrustValueType#INDIRECT INDIRECT}, or
	 *            {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}.
	 * @return the trust relationships of the specified trustor that match the
	 *         specified criteria.
	 * @throws TrustAccessControlException if the specified requestor is denied
	 *         access to the requested trust relationships.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 * @deprecated As of 1.1, use {@link #retrieveTrustRelationships(Requestor, TrustQuery)}.
	 */
	@Deprecated
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType, 
			final TrustValueType trustValueType) throws TrustException;
}