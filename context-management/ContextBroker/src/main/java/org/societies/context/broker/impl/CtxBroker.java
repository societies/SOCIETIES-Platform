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
import java.util.Dictionary;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
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
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.context.broker.api.CtxBrokerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 3p Context Broker Implementation
 */
@Service
public class CtxBroker implements org.societies.api.context.broker.ICtxBroker {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(InternalCtxBroker.class);
	
	/** The privacy logging facility. */
	@Autowired(required=false)
	private IPrivacyLogAppender privacyLogAppender;
	
	private boolean hasPrivacyLogAppender = false;

	/**
	 * The IIdentity Mgmt service reference.
	 *
	 * @see {@link #setIdentityMgr(IIdentityManager)}
	 */
	private IIdentityManager idMgr;
	
	/**
	 * The Internal Ctx Broker service reference.
	 *
	 * @see {@link #setCtxBroker(ICtxBroker)}
	 */
	@Autowired(required=true)
	private ICtxBroker internalCtxBroker;
	
	/**
	 * Instantiates the external Context Broker in Spring.
	 * 
	 * @param commMgr
	 *            the Comm Manager
	 */
	@Autowired(required=true)
	CtxBroker(ICommManager commMgr) {
		
		LOG.info(this.getClass() + " instantiated");
		this.idMgr = commMgr.getIdManager();
	}
	
	/*
	 * Used for JUnit testing only.
	 */
	public CtxBroker() {
		
		LOG.info(this.getClass() + " instantiated");
	}

	@Override
	@Async
	public Future<CtxEntity> createEntity(final Requestor requestor, 
			final IIdentity targetCss, final String type) throws CtxException {

		Future<CtxEntity> entity = null;
		if (idMgr.isMine(targetCss)) {
			entity = internalCtxBroker.createEntity(type);
		} else {
			LOG.info("remote call");
		}

		return entity;
	}

	@Override
	@Async
	public Future<CtxAttribute> createAttribute(final Requestor requestor,
			final CtxEntityIdentifier scope, final String type) throws CtxException {

		Future<CtxAttribute> ctxAttribute = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(scope.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			ctxAttribute = internalCtxBroker.createAttribute(scope, type);
		} else{
			LOG.info("remote call");
		}
		return ctxAttribute;
	}
	
	@Override
	@Async
	public Future<CtxAssociation> createAssociation(final Requestor requestor,
			final IIdentity targetCss, final String type) throws CtxException {

		Future<CtxAssociation> ctxAssoc = null;

		if (idMgr.isMine(targetCss)) {
			ctxAssoc = internalCtxBroker.createAssociation(type);
		} else {
			LOG.info("remote call");
		}

		return ctxAssoc;
	}

	@Override
	@Async
	public Future<List<Object>> evaluateSimilarity(
			Serializable objectUnderComparison,
			List<Serializable> referenceObjects) throws CtxException {

		Future<List<Object>> obj = internalCtxBroker.evaluateSimilarity(objectUnderComparison, referenceObjects);
		return obj;
	}

	@Override
	@Async
	public Future<CtxModelObject> remove(final Requestor requestor,
			final CtxIdentifier identifier) throws CtxException {

		Future<CtxModelObject> obj = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(identifier.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			obj = internalCtxBroker.remove(identifier);
		} else {
			LOG.info("remote call");
		}
		return obj;
	}

	@Override
	@Async
	public Future<CtxModelObject> retrieve(final Requestor requestor,
			CtxIdentifier identifier) throws CtxException {

		Future<CtxModelObject> obj = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(identifier.getOperatorId());
			if (this.hasPrivacyLogAppender && this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, targetCss);
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ identifier.getOperatorId() + "':" + ife.getLocalizedMessage(), ife);
		}
		if (idMgr.isMine(targetCss)) {
			obj = internalCtxBroker.retrieve(identifier);
		} else {
			LOG.info("remote call");
		}
		return obj;
	}

	@Override
	@Async
	public Future<List<CtxAttribute>> retrieveFuture(
			final Requestor requestor, CtxAttributeIdentifier attrId, Date date) throws CtxException {

		Future<List<CtxAttribute>> futureObj = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(attrId.getOperatorId());
			if (this.hasPrivacyLogAppender && this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, targetCss);
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOperatorId() + "': " + ife.getLocalizedMessage(), ife);
		}
		if (idMgr.isMine(targetCss)) {
			futureObj = internalCtxBroker.retrieveFuture(attrId, date);
		} else {
			LOG.info("remote call");
		}
		return futureObj;
	}

	@Override
	@Async
	public Future<List<CtxAttribute>> retrieveFuture(
			final Requestor requestor, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {

		Future<List<CtxAttribute>> futureObj = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(attrId.getOperatorId());
			if (this.hasPrivacyLogAppender && this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, targetCss);
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOperatorId() + "': " + ife.getLocalizedMessage(), ife);
		}
		if (idMgr.isMine(targetCss)) {
			futureObj = internalCtxBroker.retrieveFuture(attrId, modificationIndex);
		} else {
			LOG.info("remote call");
		}
		return futureObj;
	}

	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			final Requestor requestor, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {

		Future<List<CtxHistoryAttribute>> hocObj = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(attrId.getOperatorId());
			if (this.hasPrivacyLogAppender && this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, targetCss);
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOperatorId() + "': " + ife.getLocalizedMessage(), ife);
		}
		if (idMgr.isMine(targetCss)) {
			hocObj = internalCtxBroker.retrieveHistory(attrId, modificationIndex);
		} else {
			LOG.info("remote call");
		}

		return hocObj;
	}

	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			final Requestor requestor, CtxAttributeIdentifier attrId,
			Date startDate, Date endDate) throws CtxException {

		Future<List<CtxHistoryAttribute>> hocObj = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(attrId.getOperatorId());
			if (this.hasPrivacyLogAppender && this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, targetCss);
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOperatorId() + "': " + ife.getLocalizedMessage(), ife);
		}
		if (idMgr.isMine(targetCss)) {
			hocObj = internalCtxBroker.retrieveHistory(attrId, startDate, endDate);
		} else {
			LOG.info("remote call");
		}

		return hocObj;
	}

	@Override
	@Async
	public Future<CtxModelObject> update(final Requestor requestor,
			CtxModelObject object) throws CtxException {

		Future<CtxModelObject> obj = null;
		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(object.getId().getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			obj = internalCtxBroker.update(object);
		} else {
			LOG.info("remote call");
		}

		return obj;
	}

	@Override
	@Async
	public Future<CtxEntity> retrieveAdministratingCSS(
			final Requestor requestor, CtxEntityIdentifier communityEntId) throws CtxException {

		// change return type
		//Future<CtxEntity> admEntity = internalCtxBroker.retrieveAdministratingCSS(communityEntId);
		return null;
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			final Requestor requestor, CtxEntityIdentifier community) throws CtxException {

		Future<List<CtxEntityIdentifier>> entID = null;
		
		IIdentity targetCis;
		try {
			targetCis = this.idMgr.fromJid(community.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCis)) {
			entID = internalCtxBroker.retrieveCommunityMembers(community);
		}else{
			LOG.info("remote call");
		}

		return entID;
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			final Requestor requestor, CtxEntityIdentifier community) throws CtxException {

		Future<List<CtxEntityIdentifier>> entityList = null;
		
		IIdentity targetCis;
		try {
			targetCis = this.idMgr.fromJid(community.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCis)) {
			entityList = internalCtxBroker.retrieveParentCommunities(community);
		} else {
			LOG.info("remote call");
		}
		
		return entityList;
	}

	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor,
			final IIdentity targetCss, final CtxModelType modelType,
			final String type) throws CtxException {

		Future<List<CtxIdentifier>> ctxIdList = null;
		if (idMgr.isMine(targetCss)) {
			ctxIdList = internalCtxBroker.lookup(modelType, type);
		} else {
			LOG.info("remote call");
		}

		return ctxIdList;
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> lookupEntities(
			final Requestor requestor, final IIdentity targetCss, 
			final String entityType, final String attribType,
			final Serializable minAttribValue, final Serializable maxAttribValue)
					throws CtxException {

		Future<List<CtxEntityIdentifier>> entIdList = null;

		if (idMgr.isMine(targetCss)){
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
	@Async
	public void registerForChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(ctxId.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			internalCtxBroker.registerForChanges(listener, ctxId);
		} else {
			LOG.info("remote call");
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	@Async
	public void unregisterFromChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(ctxId.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			internalCtxBroker.unregisterFromChanges(listener, ctxId);
		} else {
			LOG.info("remote call");
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	@Async
	public void registerForChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");

		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(scope.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			internalCtxBroker.registerForChanges(listener, scope,attrType);
		} else {
			LOG.info("remote call");
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.datatypes.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	@Async
	public void unregisterFromChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");

		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(scope.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			internalCtxBroker.unregisterFromChanges(listener, scope, attrType);
		} else {
			LOG.info("remote call");
		}
	}

	@Override
	@Async
	public Future<Set<CtxBond>> retrieveBonds(Requestor requestor,
			CtxEntityIdentifier community) throws CtxException {

		Future<Set<CtxBond>> bonds = null;
		
		IIdentity targetCis;
		try {
			targetCis = this.idMgr.fromJid(community.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCis)) {
			bonds = internalCtxBroker.retrieveBonds(community);
		} else {
			LOG.info("remote call");
		}

		return bonds;
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			final Requestor requestor, CtxEntityIdentifier community)
					throws CtxException {

		Future<List<CtxEntityIdentifier>> ctxEntIdList = null;
		
		IIdentity targetCis;
		try {
			targetCis = this.idMgr.fromJid(community.getOperatorId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCis)) {
			ctxEntIdList = internalCtxBroker.retrieveSubCommunities(community);
		} else {
			LOG.info("remote call");
		}

		return ctxEntIdList;
	}
	
	/**
	 * Sets the IIdentity Mgmt service reference.
	 * 
	 * @param idMgr
	 *            the IIdentity Mgmt service reference to set.
	 */
	public void setIdentityMgr(IIdentityManager idMgr) {
		
		this.idMgr = idMgr;
	}
	
	/**
	 * This method is called when the {@link IPrivacyLogAppender} service is
	 * bound.
	 * 
	 * @param privacyLogAppender
	 *            the service that was bound
	 * @param props
	 *            the set of properties that the service was registered with
	 */
	public void bindPrivacyLogAppender(IPrivacyLogAppender privacyLogAppender, Dictionary<Object,Object> props) {
		
		LOG.info("Binding service reference " + privacyLogAppender);
		this.hasPrivacyLogAppender = true;
	}
	
	/**
	 * This method is called when the {@link IPrivacyLogAppender} service is
	 * unbound.
	 * 
	 * @param privacyLogAppender
	 *            the service that was unbound
	 * @param props
	 *            the set of properties that the service was registered with
	 */
	public void unbindPrivacyLogAppender(IPrivacyLogAppender privacyLogAppender, Dictionary<Object,Object> props) {
		
		LOG.info("Unbinding service reference " + privacyLogAppender);
		this.hasPrivacyLogAppender = false;
	}
}