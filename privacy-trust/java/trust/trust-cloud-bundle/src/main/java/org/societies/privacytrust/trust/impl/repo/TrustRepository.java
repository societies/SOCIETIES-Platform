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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.api.repo.TrustRepositoryException;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCis;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCss;
import org.societies.privacytrust.trust.impl.repo.model.TrustedEntity;
import org.societies.privacytrust.trust.impl.repo.model.TrustedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the {@link ITrustRepository} interface.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.5
 */
@Repository
@Lazy(value = false)
public class TrustRepository implements ITrustRepository {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustRepository.class);
	
	private static Comparator<ITrustedCss> CssSimilarityComparator = 
			new Comparator<ITrustedCss>() {

		/*
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ITrustedCss css1, ITrustedCss css2) {
			
			// null_css == null_css
			if (css1 == null && css2 == null) 
				return 0; 
		    // css1 > null_css 
		    if (css1 != null && css2 == null) 
		    	return +1;
		    // null_css < css2
		    if (css1 == null && css2 != null) 
		    	return -1;
		    
		    // null_simil == null_simil
		    if (css1.getSimilarity() == null && css2.getSimilarity() == null)
		    	return 0;
		    // simil1 > null_simil
		    if (css1.getSimilarity() != null && css2.getSimilarity() == null)
		    	return +1;
		    // null_simil < simil2
		    if (css1.getSimilarity() == null && css2.getSimilarity() != null)
		    	return -1;
		    
		    return css1.getSimilarity().compareTo(css2.getSimilarity());
		}
	};
	
	/** The Trust Event Mgr service reference. */
	@Autowired
	private ITrustEventMgr trustEventMgr;
	
	/** The Hibernate session factory. */
	@Autowired
	private SessionFactory sessionFactory;
	
	TrustRepository() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#createEntity(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public ITrustedEntity createEntity(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustRepositoryException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");

		final ITrustedEntity entity;
		switch (trusteeId.getEntityType()) {
		
		case CSS:
			entity = new TrustedCss(trustorId, trusteeId);
			break;
		case CIS:
			entity = new TrustedCis(trustorId, trusteeId);
			break;
		case SVC:
			entity = new TrustedService(trustorId, trusteeId);
			break;
		default:
			throw new TrustRepositoryException("Unsupported trustee entity type: "
					+ trusteeId.getEntityType());
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("Adding trusted entity " + entity + " to the Trust Repository...");
		
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.save(entity);
			session.flush();
			tx.commit();
			
		} catch (ConstraintViolationException cve) {
			LOG.warn("Rolling back transaction for existing entity " + entity);
			if (tx != null)
				tx.rollback();
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for entity " + entity);
			if (tx != null)
				tx.rollback();
			throw new TrustRepositoryException("Could not add entity " + entity, e);
		} finally {
			if (session != null)
				session.close();
		}
		
		return this.retrieveEntity(trustorId, trusteeId);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#retrieveEntity(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public ITrustedEntity retrieveEntity(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId)
			throws TrustRepositoryException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		Class<? extends TrustedEntity> entityClass = TrustedEntity.class;
		if (TrustedEntityType.CSS.equals(trusteeId.getEntityType()))
			entityClass = TrustedCss.class;
		else if (TrustedEntityType.CIS.equals(trusteeId.getEntityType()))
			entityClass = TrustedCis.class;
		else if (TrustedEntityType.SVC.equals(trusteeId.getEntityType()))
			entityClass = TrustedService.class;
		// TODO TrustedEntityType.LGC
		
		final ITrustedEntity result;
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Criteria criteria = session.createCriteria(entityClass)
					.add(Restrictions.and(
							Restrictions.eq("trustorId", trustorId),
							Restrictions.eq("trusteeId", trusteeId)));
			
			//if (TrustedCss.class.equals(entityClass))
			//	criteria.setFetchMode("communities", FetchMode.SELECT);
			
			//if (TrustedCis.class.equals(entityClass))
			//	criteria.setFetchMode("members", FetchMode.SELECT);
			
			result = (ITrustedEntity) criteria.uniqueResult();
		} catch (Exception e) {
			throw new TrustRepositoryException("Could not retrieve entity ("
					+ trustorId + "," + trusteeId + "): " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)
	 */
	@Override
	public ITrustedEntity updateEntity(ITrustedEntity entity)
			throws TrustRepositoryException {
		
		if (entity == null)
			throw new NullPointerException("entity can't be null");
		
		TrustedEntity result = null;
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			result = (TrustedEntity) session.merge(entity);
			tx.commit();
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for entity " + entity);
			if (tx != null)
				tx.rollback();
			throw new TrustRepositoryException("Could not add entity " + entity
					+ ": " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
		
		while (!result.getUpdateEventQueue().isEmpty()) {
			final TrustUpdateEvent event = result.getUpdateEventQueue().poll();
			final String eventTopic;
			if (TrustValueType.DIRECT == event.getTrustRelationship().getTrustValueType())
				eventTopic = TrustEventTopic.DIRECT_TRUST_UPDATED;
			else if (TrustValueType.INDIRECT == event.getTrustRelationship().getTrustValueType())
				eventTopic = TrustEventTopic.INDIRECT_TRUST_UPDATED;
			else //if (TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType())
				eventTopic = TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED;
			if (this.trustEventMgr == null) {
				LOG.error("Could not post TrustUpdateEvent " + event
						+ " to topic '" + eventTopic + "': " 
						+ "Trust Event Mgr is not available");
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("Posting TrustUpdateEvent " + event
							+ " to topic '" + eventTopic + "'");
				this.trustEventMgr.postEvent(event, 
						new String[] { eventTopic });
			}
		}
		
		return result;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#removeEntity(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void removeEntity(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustRepositoryException {

		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		final ITrustedEntity entity = this.retrieveEntity(trustorId, trusteeId);
		if (entity == null)
			return;
		
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.delete(entity);
			tx.commit();
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for entity " + entity);
			if (tx != null)
				tx.rollback();
			throw new TrustRepositoryException("Could not remove entity " + entity
					+ ": " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#retrieveEntities(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)
	 */
	@Override
	public Set<ITrustedEntity> retrieveEntities(final TrustedEntityId trustorId,
			final TrustedEntityType entityType, final TrustValueType valueType)
					throws TrustRepositoryException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		final Class<? extends TrustedEntity> daClass;
		if (null == entityType)
			daClass = null;
		else if (TrustedEntityType.CSS == entityType)
			daClass = TrustedCss.class;
		else if (TrustedEntityType.CIS == entityType)
			daClass = TrustedCis.class;
		else if (TrustedEntityType.SVC == entityType)
			daClass = TrustedService.class;
		else
			throw new TrustRepositoryException("Unsupported entityType: "
					+ entityType);
			
		final Set<ITrustedEntity> result = new HashSet<ITrustedEntity>();
		if (null != daClass) {
			result.addAll(this.doRetrieveEntities(trustorId, daClass, valueType));
		} else {
			result.addAll(this.doRetrieveEntities(trustorId, TrustedCss.class, valueType));
			result.addAll(this.doRetrieveEntities(trustorId, TrustedCis.class, valueType));
			result.addAll(this.doRetrieveEntities(trustorId, TrustedService.class, valueType));
		}
		
		return result;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#retrieveMeanTrustValue(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType, org.societies.api.privacytrust.trust.model.TrustedEntityType)
	 */
	@Override
	public double retrieveMeanTrustValue(final TrustedEntityId trustorId,
			final TrustValueType valueType, final TrustedEntityType entityType)
					throws TrustRepositoryException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (valueType == null)
			throw new NullPointerException("valueType can't be null");
		
		final String valueProperty;
		if (TrustValueType.DIRECT == valueType)
			valueProperty = "directTrust.value";
		else if (TrustValueType.INDIRECT == valueType)
			valueProperty = "indirectTrust.value";
		else // if (TrustValueType.USER_PERCEIVED == valueType)
			valueProperty = "userPerceivedTrust.value";
		
		final Class<? extends TrustedEntity> entityClass;
		if (null == entityType)
			entityClass = TrustedEntity.class;
		else if (TrustedEntityType.CSS == entityType)
			entityClass = TrustedCss.class;
		else if (TrustedEntityType.CIS == entityType)
			entityClass = TrustedCis.class;
		else if (TrustedEntityType.SVC == entityType)
			entityClass = TrustedService.class;
		else
			throw new TrustRepositoryException("Unsupported entityType: "
					+ entityType);
		
		return this.doRetrieveMeanTrustValue(trustorId, valueProperty, entityClass);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.repo.ITrustRepository#retrieveCssBySimilarity(org.societies.api.privacytrust.trust.model.TrustedEntityId, java.lang.Double, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SortedSet<ITrustedCss> retrieveCssBySimilarity(
			final TrustedEntityId trustorId, final Double similarityThreshold, 
			final Integer maxResults) throws TrustRepositoryException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (maxResults != null && maxResults < 1)
			throw new IllegalArgumentException("maxResults can't be less than 1");
		
		final SortedSet<ITrustedCss> result =
				new TreeSet<ITrustedCss>(CssSimilarityComparator);
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Criteria criteria = session.createCriteria(TrustedCss.class)
					.add(Restrictions.eq("trustorId", trustorId))
					.addOrder(Order.desc("similarity"));
			if (similarityThreshold != null)
				criteria.add(Restrictions.ge("similarity", similarityThreshold));
			if (maxResults != null)
				criteria.setMaxResults(maxResults);
			result.addAll(criteria.list());
		} catch (Exception e) {
			throw new TrustRepositoryException("Could not retrieve entities trusted by '"
					+ trustorId + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends TrustedEntity> Set<T> doRetrieveEntities(
			final TrustedEntityId trustorId, final Class<T> entityClass,
			final TrustValueType valueType)	throws TrustRepositoryException {
		
		final Set<T> result = new HashSet<T>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Criteria criteria = session.createCriteria(entityClass)
					.add(Restrictions.eq("trustorId", trustorId));
			if (TrustValueType.DIRECT == valueType)
				criteria.add(Restrictions.isNotNull("directTrust.value"));
			else if (TrustValueType.INDIRECT == valueType)
				criteria.add(Restrictions.isNotNull("indirectTrust.value"));
			else if (TrustValueType.USER_PERCEIVED == valueType)
				criteria.add(Restrictions.isNotNull("userPerceivedTrust.value"));
			result.addAll(criteria.list());
		} catch (Exception e) {
			throw new TrustRepositoryException("Could not retrieve entities trusted by '"
					+ trustorId + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private double doRetrieveMeanTrustValue(
			final TrustedEntityId trustorId, final String valueProperty,	
			final Class<? extends TrustedEntity> entityClass) 
					throws TrustRepositoryException {
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Criteria criteria = session.createCriteria(entityClass)
					.add(Restrictions.eq("trustorId", trustorId));
			final ProjectionList projList = Projections.projectionList();
			projList.add(Projections.sum(valueProperty));
			projList.add(Projections.count(valueProperty));
			criteria.setProjection(projList);
			final List<Object[]> results = criteria.list();
			double totalSum = 0.0d;
			int totalCount = 0;
			for (Object[] resultsEntry : results) {
				if (resultsEntry[0] != null) {
					totalSum += (Double) resultsEntry[0];
					totalCount += (Integer) resultsEntry[1];
				}
			}
			return (totalCount > 0) ? (totalSum / totalCount) : 0.0d;
		
		} catch (Exception e) {
			throw new TrustRepositoryException("Could not estimate mean trust value assigned by '"
					+ trustorId + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
	}
}