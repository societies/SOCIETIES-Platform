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
package org.societies.context.community.db.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.context.api.community.db.ICommunityCtxDBMgr;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.community.db.impl.model.CommunityCtxEntityBaseDAO;
import org.societies.context.community.db.impl.model.CommunityCtxModelDAOTranslator;
import org.societies.context.community.db.impl.model.CommunityCtxModelObjectNumberDAO;
import org.societies.context.community.db.impl.model.CommunityCtxAssociationDAO;
import org.societies.context.community.db.impl.model.CommunityCtxAttributeDAO;
import org.societies.context.community.db.impl.model.CommunityCtxEntityDAO;
import org.societies.context.community.db.impl.model.CtxModelObjectDAO;
import org.societies.context.community.db.impl.model.CommunityCtxQualityDAO;
import org.societies.context.community.db.impl.model.hibernate.CtxEntityIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This component is responsible for establishing the functionality of the 
 * Context DB Management at CIS level, i.e. for the community context data.
 * 
 * @author
 * 
 */
@Service("communityCtxDBMgr")
public class CommunityCtxDBMgr implements ICommunityCtxDBMgr {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CommunityCtxDBMgr.class);
	
	/** The Hibernate session factory. */
	@Autowired
	private SessionFactory sessionFactory;
	
	/** The Context Event Mgmt service reference. */
	@Autowired(required=true)
	private ICtxEventMgr ctxEventMgr;
	
	public CommunityCtxDBMgr () {

		LOG.info("{} instantiated", this.getClass());		
	}

	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public CtxAttribute createAttribute(final CtxEntityIdentifier scope,
			final String type) throws CtxException {
	
		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		final CommunityCtxEntityBaseDAO entityDAO;
		try {
			entityDAO = this.retrieve(CommunityCtxEntityBaseDAO.class, scope);
		} catch (Exception e) {
			throw new CommunityCtxDBMgrException("Could not create attribute of type '"
					+ type + "': " + e.getLocalizedMessage(), e);
		}
		if (entityDAO == null) {
			throw new CommunityCtxDBMgrException("Could not create attribute of type '"
					+ type + "': Scope not found: " + scope);
		}

		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxAttributeIdentifier id =	
				new CtxAttributeIdentifier(scope, type, modelObjectNumber);
		final CommunityCtxAttributeDAO attributeDAO = new CommunityCtxAttributeDAO(id);
		final CommunityCtxQualityDAO qualityDAO = new CommunityCtxQualityDAO(id);
		attributeDAO.setQuality(qualityDAO);
		entityDAO.addAttribute(attributeDAO);
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(attributeDAO);
			tx.commit();				
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new CommunityCtxDBMgrException("Could not create attribute of type '"
					+ type + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(id), 
					new String[] { CtxChangeEventTopic.CREATED }, CtxEventScope.BROADCAST);
		} else {
			LOG.warn("Could not send context change event to topics '" 
					+ CtxChangeEventTopic.CREATED 
					+ "' with scope '" + CtxEventScope.BROADCAST + "': "
					+ "ICtxEventMgr service is not available");
		}

		return (CtxAttribute) this.retrieve(id);
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#createAssociation(java.lang.String, java.lang.String)
	 */
	public CtxAssociation createAssociation(final String cisId,
			final String type) throws CtxException {
		
		if (cisId == null) {
			throw new NullPointerException("ownerId can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxAssociationIdentifier id = new CtxAssociationIdentifier(
				cisId, type, modelObjectNumber);
		final CommunityCtxAssociationDAO associationDAO = new CommunityCtxAssociationDAO(id);

		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(associationDAO);
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new CommunityCtxDBMgrException("Could not create association of type '"
					+ type + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(id), 
					new String[] { CtxChangeEventTopic.CREATED }, CtxEventScope.BROADCAST);
		} else {
			LOG.warn("Could not send context change event to topics '" 
					+ CtxChangeEventTopic.CREATED 
					+ "' with scope '" + CtxEventScope.BROADCAST + "': "
					+ "ICtxEventMgr service is not available");
		}
		
		return (CtxAssociation) this.retrieve(id);
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#createEntity(java.lang.String, java.lang.String)
	 */
	public CtxEntity createEntity(final String cisId,
			final String type) throws CtxException {
		
		if (cisId == null) {
			throw new NullPointerException("cisId can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxEntityIdentifier id = 
				new CtxEntityIdentifier(cisId, type, modelObjectNumber);
		final CommunityCtxEntityBaseDAO entityDAO = new CommunityCtxEntityBaseDAO(id);

		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(entityDAO);
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new CommunityCtxDBMgrException("Could not create entity of type '" 
					+ type + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(id), 
					new String[] {CtxChangeEventTopic.CREATED}, CtxEventScope.BROADCAST);
		} else {
			LOG.warn("Could not send context change event to topics '"
					+ CtxChangeEventTopic.CREATED
					+ "' with scope '" + CtxEventScope.BROADCAST + "': "
					+ "ICtxEventMgr service is not available");
		}
		
		return (CtxEntity) this.retrieve(id);
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#createCommunityEntity(java.lang.String)
	 */
	@Override
	public CommunityCtxEntity createCommunityEntity(final String cisId)
			throws CtxException {
		
		if (cisId == null) {
			throw new NullPointerException("cisId can't be null");
		}

		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxEntityIdentifier id = new CtxEntityIdentifier(
				cisId.toString(), CtxEntityTypes.COMMUNITY, modelObjectNumber);		
		final CommunityCtxEntityDAO entityDAO = new CommunityCtxEntityDAO(id);
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(entityDAO);
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new CommunityCtxDBMgrException("Could not create community context entity for CIS '" 
					+ cisId + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		// create IS_MEMBER_OF association for new CommunityCtxEntity
		final CtxAssociation isMemberOfAssoc = this.createAssociation(
				cisId.toString(), CtxAssociationTypes.IS_MEMBER_OF);
		isMemberOfAssoc.setParentEntity(id);
		this.update(isMemberOfAssoc);
		
		// create HAS_MEMBERS association for new CommunityCtxEntity
		final CtxAssociation hasMembersAssoc = this.createAssociation(
				cisId.toString(), CtxAssociationTypes.HAS_MEMBERS);
		hasMembersAssoc.setParentEntity(id);
		this.update(hasMembersAssoc);
		
		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(id), 
					new String[] { CtxChangeEventTopic.CREATED }, CtxEventScope.BROADCAST);
		} else {
			LOG.warn("Could not send context change event to topics '" 
					+ CtxChangeEventTopic.CREATED 
					+ "' with scope '" + CtxEventScope.BROADCAST + "': "
					+ "ICtxEventMgr service is not available");
		}
		
		return (CommunityCtxEntity) this.retrieve(id);
	}

	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#remove(org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public CtxModelObject remove(final CtxIdentifier id)
			throws CtxException {
		
		if (id == null) {
			throw new NullPointerException("id can't be null");
		}
		
		final CtxModelObject result = this.retrieve(id);
		if (result == null) {
			return null;
		}
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			final CtxModelObjectDAO dao = CommunityCtxModelDAOTranslator
					.getInstance().fromCtxModelObject(result);
			tx = session.beginTransaction();
			session.delete(dao);
			session.flush();
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new CommunityCtxDBMgrException("Could not remove model object '" + id 
					+ "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(id), 
					new String[] { CtxChangeEventTopic.REMOVED }, CtxEventScope.BROADCAST);
		} else {
			LOG.warn("Could not send context change event to topics '" 
					+ CtxChangeEventTopic.REMOVED 
					+ "' with scope '" + CtxEventScope.BROADCAST + "': "
					+ "ICtxEventMgr service is not available");
		}
		
		return result;
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#update(org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	public CtxModelObject update(CtxModelObject modelObject) throws CtxException {

		if (modelObject == null) { 
			throw new NullPointerException("modelObject can't be null");
		}
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {	
			final CtxModelObjectDAO modelObjectDAO = 
					CommunityCtxModelDAOTranslator.getInstance().fromCtxModelObject(modelObject);
			tx = session.beginTransaction();
			session.merge(modelObjectDAO);
			session.flush();
			tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
				throw new CommunityCtxDBMgrException("Could not update '" 
						+ modelObject + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		} 
		
		// TODO CtxChangeEventTopic.MODIFIED should only be used if the model object is actually modified
		final String[] topics = new String[] { CtxChangeEventTopic.UPDATED, CtxChangeEventTopic.MODIFIED };
		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(modelObject.getId()), 
					topics, CtxEventScope.BROADCAST);
		} else {
			LOG.warn("Could not send context change event to topics '" 
					+ Arrays.toString(topics) 
					+ "' with scope '" + CtxEventScope.BROADCAST 
					+ "': ICtxEventMgr service is not available");
		}
		      
		return this.retrieve(modelObject.getId());
	}

	@Override
	public IndividualCtxEntity retrieveAdministratingCss(
			CtxEntityIdentifier id) throws CtxException {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public CtxBond retrieveBonds(CtxEntityIdentifier ctxId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#retrieveCommunityEntity(java.lang.String)
	 */
	@Override
	public CommunityCtxEntity retrieveCommunityEntity(final String cisId)
			throws CtxException {

		if (cisId == null) {
			throw new NullPointerException("cisId can't be null");
		}
		
		final CommunityCtxEntity entity;
		
		final Session session = sessionFactory.openSession();
		try {
			final Query query = session.getNamedQuery("getCommunityCtxEntityIdByOwnerId");
			query.setParameter("ownerId", cisId, Hibernate.STRING);
			final CtxEntityIdentifier entityId = (CtxEntityIdentifier) query.uniqueResult();
			if (entityId != null) {
				entity = (CommunityCtxEntity) this.retrieve(entityId);
			} else {
				entity = null;
			}
        } catch (Exception e) {
        	throw new CommunityCtxDBMgrException("Could not retrieve community entity for CIS '"
        			+ cisId + "': "	+ e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
			
		return entity;
	}

	@Override
	public List<CtxEntityIdentifier> retrieveCommunityMembers(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxEntityIdentifier> retrieveParentCommunities(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#retrieve(org.societies.api.context.model.CtxIdentifier)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CtxModelObject retrieve(final CtxIdentifier id)
			throws CtxException {
		
		if (id == null) {
			throw new NullPointerException("id can't be null");
		}

		final CtxModelObject result;
		final CtxModelObjectDAO dao;

        try {
        	switch (id.getModelType()) {
        	
        	case ENTITY:            	
            	dao = this.retrieve(CommunityCtxEntityBaseDAO.class, id);
            	if (dao == null)
            		break;
            	final Session session = this.sessionFactory.openSession();
            	Query query;
            	//CtxAssociations where this entity is member of
            	final Set<CtxAssociationIdentifier> associationIds = new HashSet<CtxAssociationIdentifier>();

            	try { 
            		if (dao instanceof CommunityCtxEntityDAO) {
                		// Retrieve all associations whose parent entity is this entity
                		query = session.getNamedQuery("getCommunityCtxAssociationsByParentEntityId");
                    	query.setParameter("parentEntId", ((CommunityCtxEntityBaseDAO) dao).getId(),
                    			Hibernate.custom(CtxEntityIdentifierType.class));
                    	final List<CommunityCtxAssociationDAO> associations = query.list();

                    	// IS_MEMBER_OF association --> communities
                    	final Set<CtxEntityIdentifier> communityIds = new HashSet<CtxEntityIdentifier>();
                    	// HAS_MEMBERS association --> members
                    	final Set<CtxEntityIdentifier> memberIds = new HashSet<CtxEntityIdentifier>();

                    	
                		for (final CommunityCtxAssociationDAO association : associations) {
                			associationIds.add(association.getId());
                			if (CtxAssociationTypes.IS_MEMBER_OF.equals(association.getId().getType()))
                				communityIds.addAll(association.getChildEntities());
                			else if (CtxAssociationTypes.HAS_MEMBERS.equals(association.getId().getType()))
                				memberIds.addAll(association.getChildEntities());
                		}	            			
                		((CommunityCtxEntityDAO) dao).setCommunities(communityIds);
                		((CommunityCtxEntityDAO) dao).setMembers(memberIds);
            		} else if (dao instanceof CommunityCtxEntityBaseDAO) {
            			// Retrieve CtxAssociationIds where this entity is parent
            			query = session.getNamedQuery("getCommunityCtxAssociationIdsByParentEntityId");
            			query.setParameter("parentEntId", ((CommunityCtxEntityBaseDAO) dao).getId(), 
            					Hibernate.custom(CtxEntityIdentifierType.class));
            			associationIds.addAll(query.list());
            			// Retrieve CtxAssociationIds where this entity is child
            			query = session.getNamedQuery("getCommunityCtxAssociationIdsByChildEntityId");
            			query.setParameter("childEntId", ((CommunityCtxEntityBaseDAO) dao).getId(), 
            					Hibernate.custom(CtxEntityIdentifierType.class));
            			associationIds.addAll(query.list());

            		} 
            		((CommunityCtxEntityBaseDAO) dao).setAssociations(associationIds);
            					
            	} finally {
            		if (session != null)
            			session.close();
            	}
            	break;
            	
        	case ATTRIBUTE:	
        		dao = this.retrieve(CommunityCtxAttributeDAO.class, id);
        		break;
        		
        	case ASSOCIATION:
        		dao = this.retrieve(CommunityCtxAssociationDAO.class, id);
        		break;
        	
        	default:
        		throw new CommunityCtxDBMgrException("Could not retrieve '"
    					+ id + "': Unsupported CtxModelType: " + id.getModelType());
            }
        	
        	if (dao == null) {
        		return null;
        	}
        	
        	result = CommunityCtxModelDAOTranslator.getInstance()
        			.fromCtxModelObjectDAO(dao);
         
        } catch (Exception e) {
			throw new CommunityCtxDBMgrException("Could not retrieve '"
					+ id + "': " + e.getLocalizedMessage(), e);
		}

		return result;
	}

	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#lookup(java.lang.String, java.util.Set)
	 */
	@Override
	public Set<CtxIdentifier> lookup(final String ownerId, 
			final Set<String> types) throws CtxException {

		if (ownerId == null) { 
			throw new NullPointerException("ownerId can't be null");
		}
		if (types == null) { 
			throw new NullPointerException("types can't be null");
		}
		if (types.isEmpty()) { 
			throw new IllegalArgumentException("types can't be empty");
		}
		
		LOG.debug("lookup: ownerId={}, types={}", ownerId, types);
		
		final Set<CtxIdentifier> result = new LinkedHashSet<CtxIdentifier>();
		
		result.addAll(this.lookup(ownerId, CtxModelType.ENTITY, types));
		result.addAll(this.lookup(ownerId, CtxModelType.ATTRIBUTE, types));
		result.addAll(this.lookup(ownerId, CtxModelType.ASSOCIATION, types));

		LOG.debug("lookup: result={}", result);
		return result;
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#lookup(java.lang.String, org.societies.api.context.model.CtxModelType, java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<CtxIdentifier> lookup(final String ownerId, 
			final CtxModelType modelType, final Set<String> types)
					throws CtxException {

		if (ownerId == null) {
			throw new NullPointerException("ownerId can't be null");
		}
		if (modelType == null) {
			throw new NullPointerException("modelType can't be null");
		}
		if (types == null) {
			throw new NullPointerException("types can't be null");
		}
		if (types.isEmpty()) { 
			throw new IllegalArgumentException("types can't be empty");
		}

		LOG.debug("lookup: ownerId={}, modelType={}, types={}", 
				new Object[] { ownerId, modelType, types });

		final Set<CtxIdentifier> result = new LinkedHashSet<CtxIdentifier>();

		final Session session = sessionFactory.openSession();
		try {
			final Query query;
			switch (modelType) {

			case ENTITY:
				// lookup for entities
				query = session.getNamedQuery("getCommunityCtxEntityBaseIdByOwnerIdAndType");
				break;

			case ATTRIBUTE:
				// lookup for attributes
				query = session.getNamedQuery("getCommunityCtxAttributeIdsByOwnerIdAndType");
				break;

			case ASSOCIATION:
				// lookup for associations
				query = session.getNamedQuery("getCommunityCtxAssociationIdsByOwnerIdAndType");
				break;

			default:
				throw new IllegalArgumentException("Unsupported context model type: " + modelType);
			}

			for (final String type : types) {
				query.setParameter("ownerId", ownerId, Hibernate.STRING);
				query.setParameter("type", type, Hibernate.STRING);
				result.addAll(query.list());
			}
		} catch (Exception e) {
			throw new CommunityCtxDBMgrException(e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		LOG.debug("lookup: result={}", result);
		return result;
	}

	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#lookup(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxModelType, java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<CtxIdentifier> lookup(final CtxEntityIdentifier scope,
			final CtxModelType modelType, final Set<String> types) 
					throws CtxException {

		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}
		if (modelType == null) {
			throw new NullPointerException("modelType can't be null");
		}
		if (types == null) { 
			throw new NullPointerException("types can't be null");
		}
		if (types.isEmpty()) { 
			throw new IllegalArgumentException("types can't be empty");
		}

		LOG.debug("lookup: scope={}, modelType={}, types={}", 
				new Object[] { scope, modelType, types });

		final Set<CtxIdentifier> result = new LinkedHashSet<CtxIdentifier>();

		final Session session = sessionFactory.openSession();
		try {
			for (final String type : types) {
				switch (modelType) {

				case ATTRIBUTE:
					// lookup for attributes
					final Query query = session.getNamedQuery("getCommunityCtxAttributeIdsByScopeAndType");
					query.setParameter("scope", scope.toString(), Hibernate.STRING);
					query.setParameter("type", type, Hibernate.STRING);
					result.addAll(query.list());
					break;

				case ASSOCIATION:
					// lookup for associations
					final Query childQuery = session.getNamedQuery("getCommunityCtxAssociationIdsByChildEntityIdAndType");
					childQuery.setParameter("childEntId", scope, Hibernate.custom(CtxEntityIdentifierType.class));
					childQuery.setParameter("type", type, Hibernate.STRING);
					result.addAll(childQuery.list());

					final Query parentQuery = session.getNamedQuery("getCommunityCtxAssociationsByParentEntityIdAndType");
					parentQuery.setParameter("parentEntId", scope, Hibernate.custom(CtxEntityIdentifierType.class));
					parentQuery.setParameter("type", type, Hibernate.STRING);
					result.addAll(parentQuery.list());
					break;

				default:
					throw new IllegalArgumentException("Unsupported context model type: " + modelType);
				}
			}
		} catch (Exception e) {
			throw new CommunityCtxDBMgrException(e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		LOG.debug("lookup: result={}", result);
		return result;
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#lookupCommunityCtxEntity(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CtxIdentifier> lookupCommunityCtxEntity(String attrType)
			throws CtxException {
		
		if (attrType == null)
			throw new NullPointerException("attribute type can't be null");
		
		final List<CtxIdentifier> foundList = new ArrayList<CtxIdentifier>();
		
		final Session session = sessionFactory.openSession();
        try {
        	final Query query;
        	
           	query = session.getNamedQuery("getCommunityCtxEntityIdsByAttrType");
           	query.setParameter("attrType", attrType, Hibernate.STRING);
            foundList.addAll(query.list());
            
        } catch (Exception e) {
        	throw new CommunityCtxDBMgrException("Could not lookup CommunityCtxEntity objects of type '" + attrType + "': "
        			+ e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
        
		return foundList;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends CtxModelObjectDAO> T retrieve(
			final Class<T> modelObjectClass,
			final CtxIdentifier ctxId) throws Exception {
		
		T result = null;
		
		final Session session = sessionFactory.openSession();
		try {
			final Criteria criteria = session.createCriteria(modelObjectClass)
					.add(Restrictions.eq("ctxId", ctxId)); 
			result = (T) criteria.uniqueResult();
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}
	
	private Long generateNextObjectNumber() throws CommunityCtxDBMgrException {
		
		final CommunityCtxModelObjectNumberDAO objectNumberDAO =
				new CommunityCtxModelObjectNumberDAO();
		
		final Session session = this.sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(objectNumberDAO);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw new CommunityCtxDBMgrException(
					"Could not generate next context model object number");
		} finally {
			if (session != null)
				session.close();
		}
		
		return objectNumberDAO.getNextValue();
	}

}