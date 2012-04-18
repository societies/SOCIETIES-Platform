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
package org.societies.context.broker.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 3p Context Broker Implementation
 */
@Service
public class CtxBroker implements org.societies.api.context.broker.ICtxBroker {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(InternalCtxBroker.class);

	/**
	 * The Internal Ctx Broker service reference.
	 *
	 * @see {@link #setCtxBroker(ICtxBroker)}
	 */
	@Autowired(required=true)
	private ICtxBroker internalCtxBroker = null;

	/**
	 * The Internal Ctx Broker service reference.
	 *
	 * @see {@link #setidentManager(IIdentityManager)}
	 */
	@Autowired(required=false)
	private IIdentityManager identManager= null;


	public CtxBroker() throws CtxException {
		LOG.info(this.getClass().getName()+": External Broker ");
		LOG.info(this.getClass().getName()+": Return internalCtxBroker "+ this.internalCtxBroker);
		LOG.info(this.getClass().getName()+": Return identManager "+ this.identManager);
	}

	@Override
	public Future<CtxAssociation> createAssociation(IIdentity requester,
			String type) throws CtxException {

		Future<CtxAssociation> ctxAssoc = null;

		if (identManager.isMine(requester)){
			ctxAssoc = internalCtxBroker.createAssociation(type);
		} else{
			LOG.info("remote call");
		}

		return ctxAssoc;
	}

	@Override
	public Future<CtxAttribute> createAttribute(IIdentity requester,
			CtxEntityIdentifier scope, String type) throws CtxException {

		Future<CtxAttribute> ctxAttribute = null;
		if (identManager.isMine(requester)){
			ctxAttribute = internalCtxBroker.createAttribute(scope, type);
		}else{
			LOG.info("remote call");
		}
		return ctxAttribute;
	}

	@Override
	public Future<CtxEntity> createEntity(IIdentity requester,
			String type) throws CtxException {

		Future<CtxEntity> entity = null;
		if (identManager.isMine(requester)){
			entity = internalCtxBroker.createEntity(type);
		}else{
			LOG.info("remote call");
		}

		return entity;
	}

	@Override
	public Future<List<Object>> evaluateSimilarity(
			Serializable objectUnderComparison,
			List<Serializable> referenceObjects) throws CtxException {

		Future<List<Object>> obj = internalCtxBroker.evaluateSimilarity(objectUnderComparison, referenceObjects);
		return obj;
	}


	@Override
	public Future<CtxModelObject> remove(IIdentity requester,
			CtxIdentifier identifier) throws CtxException {

		Future<CtxModelObject> obj = null;
		if (identManager.isMine(requester)){
			obj = internalCtxBroker.remove(identifier);
		}else{
			LOG.info("remote call");
		}
		return obj;
	}

	@Override
	public Future<CtxModelObject> retrieve(IIdentity requester,
			CtxIdentifier identifier) throws CtxException {

		Future<CtxModelObject> obj = null;
		if (identManager.isMine(requester)){
			obj = internalCtxBroker.retrieve(identifier);
		}else{
			LOG.info("remote call");
		}
		return obj;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			IIdentity requester, CtxAttributeIdentifier attrId, Date date) throws CtxException {

		Future<List<CtxAttribute>> futureObj = null;

		if (identManager.isMine(requester)){
			futureObj = internalCtxBroker.retrieveFuture(attrId, date);
		}else{
			LOG.info("remote call");
		}
		return futureObj;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			IIdentity requester, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {

		Future<List<CtxAttribute>> futureObj = null;
		if (identManager.isMine(requester)){
			futureObj = internalCtxBroker.retrieveFuture(attrId, modificationIndex);
		}else{
			LOG.info("remote call");
		}
		return futureObj;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			IIdentity requester, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {

		Future<List<CtxHistoryAttribute>> hocObj = null;
		if (identManager.isMine(requester)){
			hocObj = internalCtxBroker.retrieveHistory(attrId, modificationIndex);
		}else{
			LOG.info("remote call");
		}

		return hocObj;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			IIdentity requester, CtxAttributeIdentifier attrId,
			Date startDate, Date endDate) throws CtxException {

		Future<List<CtxHistoryAttribute>> hocObj = null;
		if (identManager.isMine(requester)){
			hocObj = internalCtxBroker.retrieveHistory(attrId, startDate, endDate);
		}else{
			LOG.info("remote call");
		}

		return hocObj;
	}


	@Override
	public Future<CtxModelObject> update(IIdentity requester,
			CtxModelObject object) throws CtxException {

		Future<CtxModelObject> obj = null;

		if (identManager.isMine(requester)){
			obj = internalCtxBroker.update(object);
		}else{
			LOG.info("remote call");
		}

		return obj;
	}

	@Override
	public Future<CtxEntity> retrieveAdministratingCSS(
			IIdentity requester, CtxEntityIdentifier communityEntId) throws CtxException {

		// change return type
		//Future<CtxEntity> admEntity = internalCtxBroker.retrieveAdministratingCSS(communityEntId);
		return null;
	}



	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			IIdentity requester, CtxEntityIdentifier community) throws CtxException {

		Future<List<CtxEntityIdentifier>> entID = null;

		if (identManager.isMine(requester)){
			entID = internalCtxBroker.retrieveCommunityMembers(community);
		}else{
			LOG.info("remote call");
		}

		return entID;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			IIdentity requester, CtxEntityIdentifier community) throws CtxException {

		Future<List<CtxEntityIdentifier>> entityList = null;
		if (identManager.isMine(requester)){
			entityList = internalCtxBroker.retrieveParentCommunities(community);
		}else{
			LOG.info("remote call");
		}
		return entityList;
	}

	@Override
	public Future<List<CtxIdentifier>> lookup(IIdentity requester,
			CtxModelType modelType, String type) throws CtxException {

		Future<List<CtxIdentifier>> ctxIdList = null;
		if (identManager.isMine(requester)){
			ctxIdList = internalCtxBroker.lookup(modelType, type);
		}else{
			LOG.info("remote call");
		}

		return ctxIdList;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> lookupEntities(
			IIdentity requester, String entityType, String attribType,
			Serializable minAttribValue, Serializable maxAttribValue)
					throws CtxException {

		Future<List<CtxEntityIdentifier>> entIdList = null;

		if (identManager.isMine(requester)){
			entIdList = internalCtxBroker.lookupEntities(entityType, attribType, minAttribValue, maxAttribValue);
		}else{
			LOG.info("remote call");
		}
		return entIdList;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerForChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException {

		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		if (identManager.isMine(requester)){
			internalCtxBroker.registerForChanges(listener, ctxId);
		}else{
			LOG.info("remote call");
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterFromChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException {

		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		if (identManager.isMine(requester)){
			internalCtxBroker.unregisterFromChanges(listener, ctxId);
		}else{
			LOG.info("remote call");
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerForChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {

		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");

		if (identManager.isMine(requester)){
			internalCtxBroker.registerForChanges(listener, scope,attrType);
		}else{
			LOG.info("remote call");
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.datatypes.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterFromChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {

		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");

		if (identManager.isMine(requester)){
			internalCtxBroker.unregisterFromChanges(listener, scope, attrType);
		}else{
			LOG.info("remote call");
		}
	}

	@Override
	public Future<Set<CtxBond>> retrieveBonds(IIdentity requester,
			CtxEntityIdentifier community) throws CtxException {

		Future<Set<CtxBond>> bonds = null;
		if (identManager.isMine(requester)){
			bonds = internalCtxBroker.retrieveBonds(community);
		}else{
			LOG.info("remote call");
		}

		return bonds;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			IIdentity requester, CtxEntityIdentifier community)
					throws CtxException {

		Future<List<CtxEntityIdentifier>> ctxEntIdList = null;
		if (identManager.isMine(requester)){
			ctxEntIdList = internalCtxBroker.retrieveSubCommunities(community);
		}else{
			LOG.info("remote call");
		}

		return ctxEntIdList;
	}
}