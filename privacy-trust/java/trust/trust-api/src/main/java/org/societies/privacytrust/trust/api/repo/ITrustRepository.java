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
package org.societies.privacytrust.trust.api.repo;

import java.util.Set;
import java.util.SortedSet;

import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;

public interface ITrustRepository {

	/**
	 * Creates an {@link ITrustedEntity} object with the specified trustor and
	 * trustee {@link TrustedEntityId TrustedEntityIds} which is persisted in
	 * the Trust Repository. If the repository already contains the entity, the
	 * call leaves the repository unchanged and returns the existing entity
	 * associated with the supplied TrustedEntityIds. This prevents duplicate 
	 * entities in the repository.
	 * 
	 * @param trustorId
	 *            the identifier of the trustor of the entity to be added to
	 *            the trust repository
	 * @param trusteeId
	 *            the identifier of the trustee referenced in the entity to be
	 *            added to the trust repository
	 * @return the newly created {@link TrustedEntity} or the existing one if
	 *         the repository already contains the specified entity
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 * @throws TrustRepositoryException
	 *             if there is a problem accessing the Trust Repository
	 * @since 0.5
	 */
	public ITrustedEntity createEntity(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustRepositoryException;

	/**
	 * 
	 * @param trustorId
	 *            the identifier of the trustor of the entity to be added to
	 *            the trust repository
	 * @param trusteeId
	 *            the identifier of the trustee referenced in the entity to be
	 *            added to the trust repository
	 * @return
	 * @throws TrustRepositoryException
	 * @since 0.5
	 */
	public ITrustedEntity retrieveEntity(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustRepositoryException;
	
	/**
	 * 
	 * @param entity
	 * @return
	 * @throws TrustRepositoryException
	 */
	public ITrustedEntity updateEntity(ITrustedEntity entity) throws TrustRepositoryException;
	
	/**
	 * Removes the entity from the Trust Repository matching the specified
	 * trustor and trustee.
	 * 
	 * @param trustorId
	 *            (required) the identifier of the trustor of the entity to be
	 *            removed from the trust repository
	 * @param trusteeId
	 *            (required) the identifier of the trustee referenced in the
	 *            entity to be removed from the trust repository
	 * @return <code>true</code> if the Trust Repository contained the 
	 *         identified entity; <code>false</code> otherwise.
	 * @throws TrustRepositoryException if the requested trusted entity cannot
	 *         be removed.
	 * @since 0.5
	 */
	public boolean removeEntity(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustRepositoryException;
	
	/**
	 * Retrieves all entities from the Trust Repository matching the specified 
	 * criteria.
	 * 
	 * @param trustorId
	 *            (required) the identifier of the trustor.
	 * @param entityType
	 *            (optional) the trusted entity type to match; otherwise
	 *            <code>null</code> to match all entity types.
	 * @param valueType
	 *            (optional) the trust value type to match, i.e. filter out
	 *            entities having a <code>null</code> value of the specified 
	 *            type.
	 * @return all entities matching the specified criteria.
	 * @throws TrustRepositoryException
	 *             if there is a problem accessing the Trust Repository.
	 * @throws NullPointerException
	 *             if any of the required parameters is <code>null</null>.
	 * @since 1.1
	 */
	public Set<ITrustedEntity> retrieveEntities(final TrustedEntityId trustorId,
			final TrustedEntityType entityType,	final TrustValueType valueType)
					throws TrustRepositoryException;
	
	/**
	 * Returns the mean trust value of the specified type assigned by the
	 * supplied trustor. The type of the entities whose trust values to 
	 * estimate the mean of may optionally be provided.
	 * 
	 * @param trustorId
	 *            (required) the identifier of the trustor.
	 * @param valueType
	 *            (required) the type of trust values whose mean is to be
	 *            calculated.
	 * @param entityType
	 *            (optional) the trusted entity type to match; otherwise
	 *            <code>null</code> to match all entity types.
	 * @return all entities matching the specified criteria.
	 * @throws TrustRepositoryException
	 *             if there is a problem accessing the Trust Repository.
	 * @throws NullPointerException
	 *             if any of the required parameters is <code>null</null>.
	 * @since 1.1
	 */
	public double retrieveMeanTrustValue(final TrustedEntityId trustorId,
			final TrustValueType valueType, final TrustedEntityType entityType)
					throws TrustRepositoryException;
	
	/**
	 * Returns a set of CSSs from the Trust Repository whose elements are
	 * ordered based on their similarity to the specified trustor. More 
	 * specifically, the first element in the returned set is the CSS with the
	 * lowest similarity value (or <code>null</code>), while the last element
	 * has the greatest similarity value. The size of the returned set may
	 * optionally be limited, i.e. only the <code>maxResults</code> with the
	 * greater similarity will be returned. A similarity value threshold may
	 * also be specified.
	 * 
	 * @param trustorId
	 *            (required) the identifier of the trustor.
	 * @param similarityThreshold
	 *            (optional) the lowest similarity value to match (inclusive);
	 *            <code>null</code> to match any similarity value.
	 * @param maxResults
	 *            (optional) the maximum number of CSSs to retrieve;
	 *            <code>null</code> to retrieve all CSSs.
	 * @return a set of CSSs from the Trust Repository whose elements are
	 *             ordered based on their similarity to the specified trustor.
	 * @throws TrustRepositoryException
	 *             if there is a problem accessing the Trust Repository.
	 * @throws NullPointerException
	 *             if any of the required parameters is <code>null</null>.
	 * @throws IllegalArgumentException
	 *             if the specified maxResults is less than 1.
	 * @since 1.1
	 */
	public SortedSet<ITrustedCss> retrieveCssBySimilarity(
			final TrustedEntityId trustorId, final Double similarityThreshold,
			final Integer maxResults) throws TrustRepositoryException;
	
	/**
	 * Removes all entities from the Trust Repository matching the specified 
	 * criteria.
	 * 
	 * @param trustorId
	 *            (optional) the identifier of the trustor. otherwise
	 *            <code>null</code> to match all entities.
	 * @param entityType
	 *            (optional) the trusted entity type to match; otherwise
	 *            <code>null</code> to match all entity types.
	 * @param valueType
	 *            (optional) the trust value type to match, i.e. filter out
	 *            entities having a <code>null</code> value of the specified 
	 *            type.
	 * @return <code>true</code> if the Trust Repository contained any entities 
	 *         matching the specified criteria; <code>false</code> otherwise.
	 * @throws TrustRepositoryException if the requested trusted entities
	 *         cannot be removed.
	 * @since 1.2
	 */
	public boolean removeEntities(final TrustedEntityId trustorId,
			final TrustedEntityType entityType,	final TrustValueType valueType) 
			throws TrustRepositoryException;
}