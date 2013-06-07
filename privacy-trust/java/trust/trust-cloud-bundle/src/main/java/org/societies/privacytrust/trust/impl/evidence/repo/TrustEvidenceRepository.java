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

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.evidence.repo.TrustEvidenceRepositoryException;
import org.societies.privacytrust.trust.impl.common.hibernate.DateTimeUserType;
import org.societies.privacytrust.trust.impl.common.hibernate.TrustedEntityIdUserType;
import org.societies.privacytrust.trust.impl.evidence.repo.model.TableName;
import org.societies.privacytrust.trust.impl.evidence.repo.model.TrustEvidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the {@link ITrustEvidenceRepository} interface.
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
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#addEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public ITrustEvidence addEvidence(final TrustedEntityId subjectId, 
			final TrustedEntityId objectId, 
			final TrustEvidenceType type, final Date timestamp,
			final Serializable info, final TrustedEntityId sourceId) 
			throws TrustEvidenceRepositoryException {
		
		ITrustEvidence evidence = new TrustEvidence(
				subjectId, objectId, type, timestamp, info, sourceId);
		
		if (LOG.isDebugEnabled())
			LOG.debug("Adding trust evidence " + evidence + " to the Trust Evidence Repository...");
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			final Serializable evidenceId = session.save(evidence);
			session.flush();
			tx.commit();
			return (ITrustEvidence) session.get(TrustEvidence.class, evidenceId);
	
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for trust evidence " + evidence);
			if (tx != null)
				tx.rollback();
			throw new TrustEvidenceRepositoryException("Could not add evidence " 
					+ evidence + ": " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#retrieveEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.util.Date, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public SortedSet<ITrustEvidence> retrieveEvidence(
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final Date startDate, 
			final Date endDate, final TrustedEntityId sourceId)
					throws TrustEvidenceRepositoryException {
		
		final SortedSet<ITrustEvidence> result = new TreeSet<ITrustEvidence>();
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving trust evidence between dates '"
					+ startDate + "' and '" + endDate + "' of type " + type
					+ " with subjectId '" + subjectId + "', objectId '" 
					+ objectId + "' and sourceId '" + sourceId 
					+ "' from the Trust Evidence Repository...");
		result.addAll(this.retrieve(subjectId, objectId, type, startDate,
				endDate, sourceId));
		
		return result;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#retrieveLatestEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public SortedSet<ITrustEvidence> retrieveLatestEvidence(
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final TrustedEntityId sourceId)
					throws TrustEvidenceRepositoryException {
		
		final SortedSet<ITrustEvidence> result = new TreeSet<ITrustEvidence>();
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving latest trust evidence of type " + type 
					+ " with subjectId '" + subjectId + "', objectId '" 
					+ objectId + "' and sourceId '" + sourceId 
					+ "' from the Trust Evidence Repository...");
		result.addAll(this.retrieveLatest(subjectId, objectId, type, sourceId));
		
		return result;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#removeEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.util.Date, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void removeEvidence(final TrustedEntityId subjectId,
			final TrustedEntityId objectId,	final TrustEvidenceType type,
			final Date startDate, final Date endDate,
			final TrustedEntityId sourceId)
					throws TrustEvidenceRepositoryException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Removing trust evidence between dates '"
					+ startDate + "' and '" + endDate + "' of type " + type
					+ " with subjectId '" + subjectId + "' and objectId '" 
					+ objectId + "' from the Trust Evidence Repository...");
		this.remove(subjectId, objectId, type, startDate, endDate, sourceId);
	}
	
	@SuppressWarnings("unchecked")
	private Set<ITrustEvidence> retrieve(final TrustedEntityId subjectId,
			final TrustedEntityId objectId,	final TrustEvidenceType type,
			final Date startDate, final Date endDate, 
			final TrustedEntityId sourceId)
					throws TrustEvidenceRepositoryException {
		
		final Set<ITrustEvidence> result = new LinkedHashSet<ITrustEvidence>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			final Criteria criteria = session.createCriteria(TrustEvidence.class);
		
			if (subjectId != null)
				criteria.add(Restrictions.eq("subjectId", subjectId));
			
			if (objectId != null)
				criteria.add(Restrictions.eq("objectId", objectId));
			
			if (type != null)
				criteria.add(Restrictions.eq("type", type));
		
			if (startDate != null) 
				criteria.add(Restrictions.ge("timestamp", startDate));
		
			if (endDate != null)
				criteria.add(Restrictions.le("timestamp", endDate));
			
			if (sourceId != null)
				criteria.add(Restrictions.eq("sourceId", sourceId));
			
			criteria.addOrder(Order.asc("timestamp"));
	
			result.addAll(criteria.list());
		} catch (Exception e) {
			throw new TrustEvidenceRepositoryException(
					"Could not retrieve evidence with subjectId '" + subjectId
					+ "', objectId '" + objectId + "' and sourceId '"
					+ sourceId + "' of type '" + type + "' between dates '"
					+ startDate + "' and '" + endDate + "': " 
					+ e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private Set<ITrustEvidence> retrieveLatest(
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final TrustedEntityId sourceId)	
					throws TrustEvidenceRepositoryException {
		
		final Set<ITrustEvidence> result = new LinkedHashSet<ITrustEvidence>();
		String sqlRetrieve = "select * from " + TableName.TRUST_EVIDENCE + " ec" 
				+ " left outer join " + TableName.TRUST_EVIDENCE + " ec2"
				+ " on (ec.subject_id = ec2.subject_id"
				+ " and ec.object_id = ec2.object_id"
				+ " and ec.type = ec2.type"
				+ " and ec.source_id = ec2.source_id"
				+ " and ec.timestamp < ec2.timestamp)"
				+ " where ec2.id is null";
		
		if (subjectId != null)
			sqlRetrieve += " and ec.subject_id = :subjectId";
		
		if (objectId != null)
			sqlRetrieve += " and ec.object_id = :objectId";
		
		if (type != null)
			sqlRetrieve += " and ec.type = :type";
		
		if (sourceId != null)
			sqlRetrieve += " and ec.source_id = :sourceId";
		
		sqlRetrieve += " order by ec.timestamp asc";
		
		final Session session = sessionFactory.openSession();
		try {
			final Query latestQuery = session.createSQLQuery(sqlRetrieve).addEntity(TrustEvidence.class);
			
			if (subjectId != null)
				latestQuery.setParameter("subjectId", subjectId, 
						Hibernate.custom(TrustedEntityIdUserType.class));

			if (objectId != null)
				latestQuery.setParameter("objectId", objectId, 
						Hibernate.custom(TrustedEntityIdUserType.class));

			if (type != null)
				latestQuery.setParameter("type", type.ordinal());

			if (sourceId != null)
				latestQuery.setParameter("sourceId", sourceId, 
						Hibernate.custom(TrustedEntityIdUserType.class));
			
			result.addAll(latestQuery.list());
		} catch (Exception e) {
			throw new TrustEvidenceRepositoryException(
					"Could not retrieve latest evidence with subjectId '"
					+ subjectId	+ "', objectId '" + objectId 
					+ "' and sourceId '" + sourceId + "' of type '" + type 
					+ "': "	+ e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}

		return result;
	}
	
	private void remove(final TrustedEntityId subjectId, 
			final TrustedEntityId objectId,	final TrustEvidenceType type,
			final Date startDate, final Date endDate, 
			final TrustedEntityId sourceId)
					throws TrustEvidenceRepositoryException {
		
		boolean hasWhere = false;
		boolean isFirstParam = true;
		String hqlDelete = "delete " + TrustEvidence.class.getName() + " ec";
		
		if (subjectId != null) {
			if (!hasWhere) {
				hqlDelete += " where";
				hasWhere = true;
			}
			if (!isFirstParam)
				hqlDelete += " and";
			else
				isFirstParam = false;
			hqlDelete += " ec.subjectId = :subjectId";
		}
		
		if (objectId != null) {
			if (!hasWhere) {
				hqlDelete += " where";
				hasWhere = true;
			}
			if (!isFirstParam)
				hqlDelete += " and";
			else
				isFirstParam = false;
			hqlDelete += " ec.objectId = :objectId";
		}
		
		if (type != null) {
			if (!hasWhere) {
				hqlDelete += " where";
				hasWhere = true;
			}
			if (!isFirstParam)
				hqlDelete += " and";
			else
				isFirstParam = false;
			hqlDelete += " ec.type = :type";
		}
		
		if (startDate != null) {
			if (!hasWhere) {
				hqlDelete += " where";
				hasWhere = true;
			}
			if (!isFirstParam)
				hqlDelete += " and";
			else
				isFirstParam = false;
			hqlDelete += " ec.timestamp >= :startDate";
		}
		
		if (endDate != null) {
			if (!hasWhere) {
				hqlDelete += " where";
				hasWhere = true;
			}
			if (!isFirstParam)
				hqlDelete += " and";
			else
				isFirstParam = false;
			hqlDelete += " ec.timestamp <= :endDate";
		}
		
		if (sourceId != null) {
			if (!hasWhere) {
				hqlDelete += " where";
				hasWhere = true;
			}
			if (!isFirstParam)
				hqlDelete += " and";
			else
				isFirstParam = false;
			hqlDelete += " ec.sourceId = :sourceId";
		}
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null; 
		try {
			tx = session.beginTransaction();
			final Query deleteQuery = session.createQuery(hqlDelete);
			
			if (subjectId != null)
				deleteQuery.setParameter("subjectId", subjectId, Hibernate.custom(TrustedEntityIdUserType.class));
			
			if (objectId != null)
				deleteQuery.setParameter("objectId", objectId, Hibernate.custom(TrustedEntityIdUserType.class));
		
			if (type != null)
				deleteQuery.setParameter("type", type);
		
			if (startDate != null)
				deleteQuery.setParameter("startDate", startDate, Hibernate.custom(DateTimeUserType.class));
		
			if (endDate != null)
				deleteQuery.setParameter("endDate", endDate, Hibernate.custom(DateTimeUserType.class));
			
			if (sourceId != null)
				deleteQuery.setParameter("sourceId", sourceId, Hibernate.custom(TrustedEntityIdUserType.class));
		        
			int deletedEntities = deleteQuery.executeUpdate();
			if (LOG.isDebugEnabled())
				LOG.debug("Removed " + deletedEntities 
						+ " pieces of trust evidence from the Trust Evidence Repository");
			tx.commit();
			
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for query " + hqlDelete);
			if (tx != null)
				tx.rollback();
			throw new TrustEvidenceRepositoryException(
					"Could not remove evidence with subjectId '"
					+ subjectId + "' and objectId '" + objectId + "': " 
					+ e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
	}
}