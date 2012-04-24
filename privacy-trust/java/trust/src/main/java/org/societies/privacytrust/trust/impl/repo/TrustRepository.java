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
package org.societies.privacytrust.trust.impl.repo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.api.repo.TrustRepositoryException;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCis;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCss;
import org.societies.privacytrust.trust.impl.repo.model.TrustedEntity;
import org.societies.privacytrust.trust.impl.repo.model.TrustedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the {@link ITrustRepository} interface.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.5
 */
@Repository
public class TrustRepository implements ITrustRepository {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustRepository.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	TrustRepository() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#addEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)
	 */
	@Override
	public boolean addEntity(ITrustedEntity entity)
			throws TrustRepositoryException {
		
		boolean result = false;

		final Session session = sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		try {
			session.save(entity);
			transaction.commit();
			result = true;
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for entity " + entity);
			transaction.rollback();
			throw new TrustRepositoryException("Could not add entity " + entity, e);
		} finally {
			if (session != null)
				session.close();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#retrieveEntity(java.lang.String, org.societies.privacytrust.trust.api.model.TrustedEntityId)
	 */
	@Override
	public ITrustedEntity retrieveEntity(TrustedEntityId teid)
			throws TrustRepositoryException {
		
		ITrustedEntity result = null;
		
		final Session session = sessionFactory.openSession();
		if (TrustedEntityType.CSS.equals(teid.getEntityType()))
			result = (ITrustedEntity) session.get(TrustedCss.class, null);
		else if (TrustedEntityType.CIS.equals(teid.getEntityType()))
			result = (ITrustedEntity) session.get(TrustedCis.class, null);
		else if (TrustedEntityType.SVC.equals(teid.getEntityType()))
			result = (ITrustedEntity) session.get(TrustedService.class, null);
			
		return result;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)
	 */
	@Override
	public ITrustedEntity updateEntity(ITrustedEntity entity)
			throws TrustRepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#removeEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)
	 */
	@Override
	public boolean removeEntity(ITrustedEntity entity)
			throws TrustRepositoryException {
		// TODO Auto-generated method stub
		return false;
	}
}