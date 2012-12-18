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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.context.broker.api.CtxBrokerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 3p Context Broker Implementation
 */
@Service
public class CtxBroker implements org.societies.api.context.broker.ICtxBroker {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBroker.class);

	/** The privacy logging facility. */
	@Autowired(required=false)
	private IPrivacyLogAppender privacyLogAppender;

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


	/*
	 * Used for JUnit testing only.
	 */
	public CtxBroker(InternalCtxBroker internalCtxBroker) {
		this.internalCtxBroker  = internalCtxBroker;
		LOG.info(this.getClass() + " instantiated " +internalCtxBroker);
	}


	//******************************************
	// Basic CRUD methods
	//******************************************

	@Override
	@Async
	public Future<CtxEntity> createEntity(Requestor requestor, 
			final IIdentity targetCss, final String type) throws CtxException {

		return internalCtxBroker.createEntity(requestor, targetCss, type);
	}


	@Override
	@Async
	public Future<CtxAttribute> createAttribute( Requestor requestor,
			final CtxEntityIdentifier scope, final String type) throws CtxException {

		return this.internalCtxBroker.createAttribute(requestor, scope,  type);
	}

	@Override
	@Async
	public Future<CtxAssociation> createAssociation(final Requestor requestor,
			final IIdentity targetCss, final String type) throws CtxException {

		return this.internalCtxBroker.createAssociation(requestor, targetCss, type);
	}


	@Override
	@Async
	public Future<CtxModelObject> remove(final Requestor requestor,
			final CtxIdentifier identifier) throws CtxException {

		return this.internalCtxBroker.remove(requestor, identifier);
	}


	@Override
	@Async
	public Future<CtxModelObject> retrieve( Requestor requestor,
			final CtxIdentifier identifier) throws CtxException {

		return this.internalCtxBroker.retrieve(requestor, identifier);
	}


	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveIndividualEntityId(
			Requestor requestor, final IIdentity cssId) throws CtxException {


		return this.internalCtxBroker.retrieveIndividualEntityId(requestor, cssId);
	}


	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			final Requestor requestor, final IIdentity cisId) throws CtxException {

		return this.internalCtxBroker.retrieveCommunityEntityId(requestor, cisId);
	}

	@Override
	@Async
	public Future<CtxModelObject> update( Requestor requestor,
			final CtxModelObject object) throws CtxException {

		return this.internalCtxBroker.update(requestor, object);
	}


	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor, 
			final CtxEntityIdentifier entityId, 
			final CtxModelType modelType, String type) throws CtxException {

		return this.internalCtxBroker.lookup(requestor, entityId, modelType, type);
	}


	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup( Requestor requestor,
			final IIdentity target, final CtxModelType modelType,
			final String type) throws CtxException {


		return this.internalCtxBroker.lookup(requestor, target, modelType, type);
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> lookupEntities(
			final Requestor requestor, final IIdentity targetCss, 
			final String entityType, final String attribType,
			final Serializable minAttribValue, final Serializable maxAttribValue)
					throws CtxException {

		return this.internalCtxBroker.lookupEntities(requestor, targetCss, entityType, attribType, minAttribValue, maxAttribValue);
	}


	//*************** end of basic CRUD ************************


	@Override
	@Async
	public Future<CtxEntity> retrieveAdministratingCSS(
			final Requestor requestor, CtxEntityIdentifier communityEntId) throws CtxException {

		return this.internalCtxBroker.retrieveAdministratingCSS(requestor, communityEntId);
	}


	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			final Requestor requestor, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {

		Future<List<CtxHistoryAttribute>> hocObj = null;

		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(requestor, targetCss);

		if (idMgr.isMine(targetCss)) {

			hocObj = internalCtxBroker.retrieveHistory(attrId, modificationIndex);
		} else {

			LOG.info("remote call is not supported for ctx history data");
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
			targetCss = this.idMgr.fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(requestor, targetCss);

		if (idMgr.isMine(targetCss)) {

			hocObj = internalCtxBroker.retrieveHistory(attrId, startDate, endDate);
		} else {

			LOG.info("remote call is not supported for ctx history data");
		}

		return hocObj;
	}


	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			final Requestor requestor, CtxEntityIdentifier community) throws CtxException {

		Future<List<CtxEntityIdentifier>> entID = null;

		IIdentity targetCis;
		try {
			targetCis = this.idMgr.fromJid(community.getOwnerId());
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
			targetCis = this.idMgr.fromJid(community.getOwnerId());
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
	public void registerForChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		internalCtxBroker.registerForChanges(listener, ctxId);
	}


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
			targetCss = this.idMgr.fromJid(ctxId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCss)) {
			internalCtxBroker.unregisterFromChanges(listener, ctxId);
		} else {
			LOG.info("remote call");
		}

	}

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

		internalCtxBroker.registerForChanges(listener, scope,attrType);
	}


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

		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(scope.getOwnerId());
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
		/*
		Future<Set<CtxBond>> bonds = null;

		IIdentity targetCis;
		try {
			targetCis = this.idMgr.fromJid(community.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (idMgr.isMine(targetCis)) {
			bonds = internalCtxBroker.retrieveBonds(community);
		} else {
			LOG.info("remote call");
		}

		return bonds;
		 */
		return this.internalCtxBroker.retrieveBonds(requestor, community);
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			final Requestor requestor, CtxEntityIdentifier community)
					throws CtxException {

		Future<List<CtxEntityIdentifier>> ctxEntIdList = null;

		IIdentity targetCis;
		try {
			targetCis = this.idMgr.fromJid(community.getOwnerId());
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
	public Future<List<CtxAttribute>> retrieveFuture(
			final Requestor requestor, CtxAttributeIdentifier attrId, Date date) throws CtxException {

		Future<List<CtxAttribute>> futureObj = null;

		IIdentity targetCss;
		try {
			targetCss = this.idMgr.fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(requestor, targetCss);

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
			targetCss = this.idMgr.fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(requestor, targetCss);

		if (idMgr.isMine(targetCss)) {

			futureObj = internalCtxBroker.retrieveFuture(attrId, modificationIndex);
		} else {

			LOG.info("remote call");
		}
		return futureObj;
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


	private void logRequest(final Requestor requestor, final IIdentity target) {

		try {
			if (this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, target);
		} catch (ServiceUnavailableException sue) {
			// do nothing
		}
	}
}