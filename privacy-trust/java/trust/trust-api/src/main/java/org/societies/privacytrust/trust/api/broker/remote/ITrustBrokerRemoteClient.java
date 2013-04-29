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
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

/**
 * This interface provides access to the trust values associated with individuals,
 * communities and services.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public interface ITrustBrokerRemoteClient {
	
	/**
	 * Retrieves all trust relationships of the specified trustor. The method
	 * returns an <i>empty</i> set if the identified trustor has not 
	 * established any trust relationships. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationships(Set)}
	 * method.
	 *
	 * @param requestor 
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust
	 *            relationships to retrieve.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(final Requestor requestor,
			final TrustedEntityId trustorId,
			final ITrustBrokerRemoteClientCallback callback)
					throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor with the
	 * supplied trustee. The method returns an <i>empty</i> set if no trust
	 * relationships exist between the identified trustor and trustee. The
	 * result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationships(Set)}
	 * method.
	 *
	 * @param requestor 
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust 
	 *            relationships to retrieve.
	 * @param trusteeId
	 *            (required) the identifier of the entity trusted by the 
	 *            specified trustor.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(final Requestor requestor,
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId, 
			final ITrustBrokerRemoteClientCallback callback) throws TrustException;
	
	/**
	 * Retrieves the trust relationship of the specified type which the given
	 * trustor has established with the supplied trustee. The method returns 
	 * <code>null</code> if no trust relationship of the specified type has
	 * been established with the supplied trustee by the given trustor. The
	 * result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationship(TrustRelationship)}
	 * method.
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
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationship cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationship(final Requestor requestor, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) throws TrustException;
	
	/**
	 * Retrieves the trust value of the specified type which the given trustor
	 * has assigned to the supplied trustee. The method returns 
	 * <code>null</code> if no trust value of the specified type has been
	 * assigned to the supplied trustee by the given trustor. The
	 * result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustValue(Double)}
	 * method.
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
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust value cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustValue(final Requestor requestor, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the {@link TrustedEntityType type}
	 * of the entities trusted by the trustor is also specified. The method
	 * returns an <i>empty</i> set if no trust relationships match
	 * the supplied criteria. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationships(Set)}
	 * method.
	 *
	 * @param requestor 
	 *            (required)
	 * @param trustorId
	 *            (required) the identifier of the entity which has established
	 *            the trust relationships to retrieve.
	 * @param trusteeType
	 *            (required) the {@link TrustedEntityType type} of the trusted
	 *            entities to match, e.g. {@link TrustedEntityType#CSS CSS}.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(final Requestor requestor, 
			final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType,
			final ITrustBrokerRemoteClientCallback callback) throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the trust value type, i.e. one of
	 * {@link TrustValueType#DIRECT DIRECT}, 
	 * {@link TrustValueType#INDIRECT INDIRECT}, or
	 * {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}, is also specified.
	 * The method returns an <i>empty</i> set if no trust relationships match
	 * the supplied criteria. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationships(Set)}
	 * method.
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
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(final Requestor requestor,
			final TrustedEntityId trustorId,
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) throws TrustException;
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the {@link TrustedEntityType type}
	 * of the entities trusted by the trustor and/or the trust value type, i.e. one of 
	 * {@link TrustValueType#DIRECT DIRECT}, 
	 * {@link TrustValueType#INDIRECT INDIRECT}, or
	 * {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}, are also specified.
	 * The method returns an <i>empty</i> set if no trust relationships match
	 * the supplied criteria. The result is returned through the 
	 * {@link ITrustBrokerRemoteClientCallback#onRetrievedTrustRelationships(Set)}
	 * method.
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
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws TrustException if the trust relationships cannot be retrieved.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType, 
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) throws TrustException;
}