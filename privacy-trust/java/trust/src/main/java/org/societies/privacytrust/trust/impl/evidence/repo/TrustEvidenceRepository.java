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
package org.societies.privacytrust.trust.impl.evidence.repo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.evidence.repo.TrustEvidenceRepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Repository
public class TrustEvidenceRepository implements ITrustEvidenceRepository {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustEvidenceRepository.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	TrustEvidenceRepository() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#addEvidence(org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence)
	 */
	@Override
	public boolean addEvidence(ITrustEvidence evidence)
			throws TrustEvidenceRepositoryException {
		
		if (evidence == null)
			throw new NullPointerException("evidence can't be null");
		
		boolean result = false;

		// TODO check if the entity is already present
		// if (this.retrieveEntity(entity.getTeid()) != null)
		//	return false;
		
		final Session session = sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		try {
			if (LOG.isDebugEnabled())
				LOG.debug("Adding trust evidence " + evidence + " to the Trust Evidence Repository...");
	
			session.save(evidence);
			session.flush();
			transaction.commit();
			result = true;
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for trust evidence " + evidence);
			transaction.rollback();
			throw new TrustEvidenceRepositoryException("Could not add evidence " + evidence, e);
		} finally {
			if (session != null)
				session.close();
		}
		
		return result;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#retrieveEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public ITrustEvidence retrieveEvidence(TrustedEntityId teid)
			throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#removeEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void removeEvidence(TrustedEntityId teid)
			throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub

	}
}