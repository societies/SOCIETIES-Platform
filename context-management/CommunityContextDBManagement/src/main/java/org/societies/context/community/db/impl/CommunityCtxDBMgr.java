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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.context.api.community.db.ICommunityCtxDBMgr;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.community.db.impl.CtxModelObjectNumberGenerator;
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
	
	/** The Context Event Mgmt service reference. */
	@Autowired(required=true)
	private ICtxEventMgr ctxEventMgr;

	private final ConcurrentMap<CtxIdentifier, CtxModelObject> modelObjects;
	
	
	public CommunityCtxDBMgr () {

		LOG.info(this.getClass() + " instantiated");
		this.modelObjects =  new ConcurrentHashMap<CtxIdentifier, CtxModelObject>();		
	}

	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#createCommunityAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public CtxAttribute createCommunityAttribute(CtxEntityIdentifier scope, String type)
			throws CtxException {
	
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		final CommunityCtxEntity entity = (CommunityCtxEntity) modelObjects.get(scope);
		
		if (entity == null)
			throw new CommunityCtxDBMgrException("Scope not found: " + scope);

		CtxAttributeIdentifier attrIdentifier = new CtxAttributeIdentifier(scope, type, CtxModelObjectNumberGenerator.getNextValue());
		final CtxAttribute attribute = new CtxAttribute(attrIdentifier);

		this.modelObjects.put(attribute.getId(), attribute);
		entity.addAttribute(attribute);
		
		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(attribute.getId()), 
					new String[] { CtxChangeEventTopic.CREATED }, CtxEventScope.LOCAL);
		} else {
			LOG.warn("Could not send context change event to topics '" 
					+ CtxChangeEventTopic.CREATED 
					+ "' with scope '" + CtxEventScope.LOCAL + "': "
					+ "ICtxEventMgr service is not available");
		}

		return attribute;
	}
	
	@Override
	public CommunityCtxEntity createCommunityEntity(IIdentity cisId)
			throws CtxException {

		final CtxEntityIdentifier identifier;
		
		identifier = new CtxEntityIdentifier(cisId.toString(), 
					CtxEntityTypes.COMMUNITY, CtxModelObjectNumberGenerator.getNextValue());

		final CommunityCtxEntity entity = new CommunityCtxEntity(identifier);
		
		this.modelObjects.put(entity.getId(), entity);		
		
		if (this.ctxEventMgr != null) {
			this.ctxEventMgr.post(new CtxChangeEvent(entity.getId()), 
					new String[] { CtxChangeEventTopic.CREATED }, CtxEventScope.LOCAL);
		} else {
			LOG.warn("Could not send context change event to topics '" 
					+ CtxChangeEventTopic.CREATED 
					+ "' with scope '" + CtxEventScope.LOCAL + "': "
					+ "ICtxEventMgr service is not available");
		}
		return entity;
	}

	@Override
	public CommunityCtxEntity removeCommunityEntity(CtxEntityIdentifier ctxId)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public List<CtxEntityIdentifier> retrieveChildCommunities(
			CtxEntityIdentifier ctxId) throws CtxException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#retrieveCommunityEntity(org.societies.api.identity.IIdentity)
	 */
	@Override
	public CommunityCtxEntity retrieveCommunityEntity(final IIdentity cisId)
			throws CtxException {

		if (cisId == null)
			throw new NullPointerException("cisId can't be null");
		
		CommunityCtxEntity entity = null;
		
		for (final CtxModelObject foundEntity : this.modelObjects.values())
			if (cisId.toString().equals(foundEntity.getOwnerId())
					&& CtxEntityTypes.COMMUNITY.equals(foundEntity.getType()))
				entity = (CommunityCtxEntity) foundEntity;
			
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

	@Override
	public List<CtxEntityIdentifier> retrieveSubCommunities(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommunityCtxEntity updateCommunityEntity(CommunityCtxEntity entity)
			throws CtxException {

		if (this.modelObjects.keySet().contains(entity.getId())) {
			this.modelObjects.put(entity.getId(), entity);
		
			final String[] topics = new String[] { CtxChangeEventTopic.UPDATED, CtxChangeEventTopic.MODIFIED };
			if (this.ctxEventMgr != null) {
				this.ctxEventMgr.post(new CtxChangeEvent(entity.getId()), 
						topics, CtxEventScope.LOCAL);
			} else {
				LOG.warn("Could not send context change event to topics '" 
						+ Arrays.toString(topics) 
						+ "' with scope '" + CtxEventScope.LOCAL 
						+ "': ICtxEventMgr service is not available");
			}
		}
					      
		return entity;
	}

	/*
	 * @see org.societies.context.api.community.db.ICommunityCtxDBMgr#retrieve(org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public CtxModelObject retrieve(final CtxIdentifier ctxId)
			throws CtxException {

		return this.modelObjects.get(ctxId);
	}
}