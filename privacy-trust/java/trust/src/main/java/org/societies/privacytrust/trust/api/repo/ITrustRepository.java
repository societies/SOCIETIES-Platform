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

import java.util.List;

import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;

public interface ITrustRepository {

	/**
	 * Creates an {@link ITrustedEntity} object with the specified
	 * {@link TrustedEntityId} which is persisted in the Trust Repository. If
	 * the repository already contains the entity, the call leaves the
	 * repository unchanged and returns the existing entity associated with the
	 * supplied TrustedEntityId. This prevents duplicate entities in the
	 * repository.
	 * 
	 * @param teid
	 *            the identifier of the entity to be added to the repository
	 * @return the newly created {@link TrustedEntity} or the existing one if
	 *         the repository already contains the identified entity
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 * @throws TrustRepositoryException
	 *             if there is a problem accessing the Trust Repository
	 * @since 0.3
	 */
	public ITrustedEntity createEntity(final TrustedEntityId teid) throws TrustRepositoryException;

	/**
	 * 
	 * @param teid
	 * @return
	 * @throws TrustRepositoryException
	 */
	public ITrustedEntity retrieveEntity(final TrustedEntityId teid) throws TrustRepositoryException;
	
	/**
	 * 
	 * @param entity
	 * @return
	 * @throws TrustRepositoryException
	 */
	public ITrustedEntity updateEntity(ITrustedEntity entity) throws TrustRepositoryException;
	
	/**
	 * 
	 * @param teid
	 * @throws TrustRepositoryException
	 */
	public void removeEntity(TrustedEntityId teid) throws TrustRepositoryException;
	
	/**
	 * 
	 * @param trustorId
	 * @param type
	 * @return
	 * @throws TrustRepositoryException
	 * @since 0.3
	 */
	public <T extends ITrustedEntity> List<T> retrieveEntities(final String trustorId,
			final Class<T> entityClass) throws TrustRepositoryException;
}