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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.datatype.DatatypeConfigurationException;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.context.contextmanagement.RetrieveIndividualEntityIdBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.context.broker.api.CtxBrokerException;
import org.societies.context.broker.api.security.CtxPermission;
import org.societies.context.broker.api.security.ICtxAccessController;
import org.societies.context.broker.impl.comm.CtxBrokerClient;
import org.societies.context.broker.impl.comm.ICtxCallback;
import org.societies.context.broker.impl.comm.callbacks.CreateAttributeCallback;
import org.societies.context.broker.impl.comm.callbacks.CreateEntityCallback;
import org.societies.context.broker.impl.comm.callbacks.LookupCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveCtxCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveIndividualEntCallback;
import org.societies.context.broker.impl.comm.callbacks.UpdateCtxCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * 3p Context Broker Implementation
 */
@Service
public class CtxBroker implements org.societies.api.context.broker.ICtxBroker {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBroker.class);

	/** ICtxAccessController service reference. */
	@Autowired(required=true)
	private ICtxAccessController ctxAccessController;

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
	 * The Ctx Broker client Service reference.
	 *
	 * @see {@link #setCtxBrokerClient(CtxBrokerClient)}
	 */
	@Autowired(required=true)
	private CtxBrokerClient ctxBrokerClient;

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

	@Override
	@Async
	public Future<CtxEntity> createEntity(final Requestor requestor, 
			final IIdentity targetCss, final String type) throws CtxException {

		CtxEntity entity = null;

		if (idMgr.isMine(targetCss)) {

			Future<CtxEntity> localEntity = internalCtxBroker.createEntity(type);

			return localEntity;

		} else {

			final CreateEntityCallback callback = new CreateEntityCallback();

			// change target to local identity for testing
			//LOG.info("createEntity remote call: target id changed to local" + this.getLocalID());
			//ctxBrokerClient.createRemoteEntity(requestor, this.getLocalID(), type, callback);

			ctxBrokerClient.createRemoteEntity(requestor, targetCss, type, callback);

			synchronized (callback) {
				try {
					callback.wait();
					entity = callback.getResult();
				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote createEntity");
				}
			}
			//end of remote comm code
		}
		return new AsyncResult<CtxEntity>(entity);
	}


	@Override
	@Async
	public Future<CtxAttribute> createAttribute(final Requestor requestor,
			final CtxEntityIdentifier scope, final String type) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		CtxAttribute ctxAttribute = null;

		IIdentity targetCss = null; 

		//LOG.info("createAttribute requestor" + requestor);
		//LOG.info("createAttribute scope" + scope.getOwnerId().toString());
		try {
			// change target to local identity for testing
			if(scope.getOwnerId().equals("local")) {
				targetCss = this.getLocalID();
			} else	targetCss = this.idMgr.fromJid(scope.getOwnerId());
			//LOG.info("createAttribute targetCss" + targetCss);

		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}

		if (this.idMgr.isMine(targetCss)) {
			//LOG.info("createAttribute local: ");
			try {
				ctxAttribute = internalCtxBroker.createAttribute(scope, type).get();

			} catch (Exception e) {

				throw new CtxBrokerException("Could not create context attribute with scope"
						+ scope + " and type " + type + ": " + e.getLocalizedMessage(), e);
			}

		} else {
			// remote call
			final CreateAttributeCallback callback = new CreateAttributeCallback();

			LOG.info("createAttribute perform remote call targetCSS:"+targetCss +" type:"+type);
			ctxBrokerClient.createRemoteAttribute(requestor, targetCss, scope, type, callback);
			//LOG.info("createAttribute remote call performed ");

			synchronized (callback) {
				try {
					//LOG.info("Attribute creation Callback wait");
					callback.wait();
					ctxAttribute = callback.getResult();
					//LOG.info("ctxAttribute retrieved from callback : "+ctxAttribute.getId());
				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote ctxAttribute");
				}
			}
			//end of remote code
		}
		return new AsyncResult<CtxAttribute>(ctxAttribute);
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
			targetCss = this.idMgr.fromJid(identifier.getOwnerId());
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

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#retrieve(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	@Async
	public Future<CtxModelObject> retrieve(final Requestor requestor,
			final CtxIdentifier identifier) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (identifier == null)
			throw new NullPointerException("identifier can't be null");

		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving context model object with id " +  identifier);

		CtxModelObject obj = null;

		IIdentity target;
		try {
			target = this.idMgr.fromJid(identifier.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ identifier.getOwnerId() + "':" + ife.getLocalizedMessage(), ife);
		}
		this.logRequest(requestor, target);

		// target is a CIS code starts 
		if (IdentityType.CIS.equals(target.getType())) {
			LOG.info("target is a CIS " +target.getJid());
			try {
				// TODO check if CIS is locally maintained or a remote call is necessary
				// TODO add access control (?)
				obj = internalCtxBroker.retrieve(identifier).get();
			} catch (Exception e) {				
				throw new CtxBrokerException(
						"Platform context broker failed to retrieve context model object with id " 
								+ identifier + ": " +  e.getLocalizedMessage(), e);
			}
			return new AsyncResult<CtxModelObject>(obj);

			//target is a CSS code end 
		} else if (IdentityType.CSS.equals(target.getType()) 
				|| IdentityType.CSS_RICH.equals(target.getType())
				|| IdentityType.CSS_LIGHT.equals(target.getType())){


			if (this.idMgr.isMine(target)) {

				this.ctxAccessController.checkPermission(requestor, target,
						new CtxPermission(identifier, CtxPermission.READ));
				try {
					obj = internalCtxBroker.retrieve(identifier).get();
				} catch (Exception e) {
					throw new CtxBrokerException(
							"Platform context broker failed to retrieve context model object with id " 
									+ identifier + ": " +  e.getLocalizedMessage(), e);
				}
				return new AsyncResult<CtxModelObject>(obj);

			} else {
				final RetrieveCtxCallback callback = new RetrieveCtxCallback();
				LOG.info("retrieve CSS context object remote call identifier " +identifier.toString());
				ctxBrokerClient.retrieveRemote(requestor, identifier, callback); 
				///LOG.info("RetrieveCtx remote call performed ");

				synchronized (callback) {
					try {
						LOG.info("RetrieveCtx remote call result received 1 ");
						callback.wait();
						obj = callback.getResult();
						//LOG.info("RetrieveCtx remote call result received 2 " +obj.getId().toString());
					} catch (InterruptedException e) {

						throw new CtxBrokerException("Interrupted while waiting for remote ctxAttribute");
					}
				}											
			}//end of remote code
		}
		return new AsyncResult<CtxModelObject>(obj);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#retrieveIndividualEntityId(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity)
	 */
	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveIndividualEntityId(
			final Requestor requestor, final IIdentity cssId) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (cssId == null)
			throw new NullPointerException("cssId can't be null");
		if (!IdentityType.CSS.equals(cssId.getType()) && 
				!IdentityType.CSS_RICH.equals(cssId.getType()) &&
				!IdentityType.CSS_LIGHT.equals(cssId.getType()))
			throw new IllegalArgumentException("cssId IdentityType is not CSS");

		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving the CtxEntityIdentifier for CSS " + cssId);

		CtxEntityIdentifier individualEntityId = null;

		if (this.idMgr.isMine(cssId)) {

			IndividualCtxEntity individualEntity;
			try {
				individualEntity = this.internalCtxBroker.retrieveIndividualEntity(cssId).get();
				if (individualEntity != null)
					individualEntityId = individualEntity.getId();
			} catch (Exception e) {

				throw new CtxBrokerException(
						"Could not retrieve IndividualCtxEntity from platform Context Broker: "
								+ e.getLocalizedMessage(), e);
			}
		} else {
			LOG.info("RetrieveIndividualEntCallback remote call");
			RetrieveIndividualEntCallback callback = new RetrieveIndividualEntCallback();
			
			ctxBrokerClient.retrieveRemoteIndividualEntId(requestor, cssId, callback);
			synchronized (callback) {
				try {
					LOG.info("RetrieveCtx remote call result received 1 ");
					callback.wait();
					individualEntityId = callback.getResult();
					LOG.info("RetrieveIndividualEntCallback remote call result received 2 " +individualEntityId.toString());
				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote ctxAttribute");
				}
			}
			
		}

		return new AsyncResult<CtxEntityIdentifier>(individualEntityId);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#retrieveCommunityEntityId(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity)
	 */
	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			final Requestor requestor, final IIdentity cisId) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (cisId == null)
			throw new NullPointerException("cisId can't be null");
		if (!IdentityType.CIS.equals(cisId.getType()))
			throw new IllegalArgumentException("cisId IdentityType is not CIS");

		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving the CtxEntityIdentifier for CIS " + cisId);

		// TODO if (this.idMgr.isMine(cssId)) {

		return this.internalCtxBroker.retrieveCommunityEntityId(cisId);
		/* TODO	
		} else {

			LOG.warn("remote call");
		} */
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
			targetCss = this.idMgr.fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(requestor, targetCss);

		if (idMgr.isMine(targetCss)) {

			hocObj = internalCtxBroker.retrieveHistory(attrId, startDate, endDate);
		} else {

			LOG.info("remote call");
		}

		return hocObj;
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#update(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	@Async
	public Future<CtxModelObject> update(final Requestor requestor,
			final CtxModelObject object) throws CtxException {

		if (object == null)
			throw new NullPointerException("object can't be null");

		CtxModelObject updatedObject = null;
		IIdentity target;
		try {
			target = this.idMgr.fromJid(object.getId().getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}


		if (IdentityType.CSS.equals(target.getType()) 
				|| IdentityType.CSS_RICH.equals(target.getType())
				|| IdentityType.CSS_LIGHT.equals(target.getType())) {

			if (idMgr.isMine(target)) {

				this.ctxAccessController.checkPermission(requestor, target,	
						new CtxPermission(object.getId(), CtxPermission.WRITE));
				try {
					updatedObject = internalCtxBroker.update(object).get();
				} catch (Exception e) {
					throw new CtxBrokerException(
							"Platform context broker failed to update context model object with id "
									+ object.getId() + ": " +  e.getLocalizedMessage(), e);
				}
				return new AsyncResult<CtxModelObject>(updatedObject);

			} else {
				final UpdateCtxCallback callback = new UpdateCtxCallback();
				LOG.info("update method remote call ctx object id:"+object.getId());
				ctxBrokerClient.updateRemote(requestor, object, callback);
				//LOG.info("UpdateCtx remote call performed ");

				synchronized (callback) {
					try {
						//LOG.info("UpdateCtx remote call result received 1 ");
						callback.wait();
						updatedObject = callback.getResult();
						//LOG.info("UpdateCtx remote call result received 2 " +updatedObject.getId().toString());
					} catch (InterruptedException e) {

						throw new CtxBrokerException("Interrupted while waiting for remote ctxAttribute");
					}
				}
			}
		}else if (IdentityType.CIS.equals(target.getType())){
			try {
				updatedObject = internalCtxBroker.update(object).get();
			} catch (Exception e) {
				throw new CtxBrokerException(
						"Platform context broker failed to update context model object with id "
								+ object.getId() + ": " +  e.getLocalizedMessage(), e);
			}
		}

		return new AsyncResult<CtxModelObject>(updatedObject);
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

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#lookup(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor, 
			final CtxEntityIdentifier entityId, 
			final CtxModelType modelType, String type) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (entityId == null)
			throw new NullPointerException("entityId can't be null");
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		// TODO access control
		// final List<CtxIdentifier> ctxIdList = new ArrayList<CtxIdentifier>();

		return this.internalCtxBroker.lookup(entityId, modelType, type);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#lookup(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor,
			final IIdentity target, final CtxModelType modelType,
			final String type) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (target == null)
			throw new NullPointerException("target can't be null");
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		final List<CtxIdentifier> localCtxIdListResult = new ArrayList<CtxIdentifier>();
		List<CtxIdentifier> remoteCtxIdListResult = new ArrayList<CtxIdentifier>();

		//LOG.info("lookup method called requestor:"+ requestor.toString());
		//LOG.info("lookup method called target:"+ target.getJid());
		//LOG.info("lookup method called modelType:"+ modelType.toString());
		//LOG.info("lookup method called type:"+ type);


		if (this.idMgr.isMine(target)) {
			//LOG.info("lookup local call 1");
			List<CtxIdentifier> ctxIdListFromDb;
			try {
				ctxIdListFromDb = internalCtxBroker.lookup(modelType, type).get();

				//if (ctxIdListFromDb != null) LOG.info("lookup local call 2 data before access control : " + ctxIdListFromDb);


			} catch (Exception e) {
				throw new CtxBrokerException("Platform context broker failed to lookup " 
						+ modelType	+ " objects of type " + type + ": " 
						+  e.getLocalizedMessage(), e);
			} 
			if (!ctxIdListFromDb.isEmpty()) {

				for (final CtxIdentifier ctxId : ctxIdListFromDb) {		
					try {
						this.ctxAccessController.checkPermission(requestor, target,
								new CtxPermission(ctxId, CtxPermission.READ));
						localCtxIdListResult.add(ctxId);


					} catch (CtxAccessControlException cace) {
						// do nothing
					}
				}
				if (localCtxIdListResult.isEmpty())
					throw new CtxAccessControlException("Could not lookup " 
							+ modelType	+ " objects of type " + type 
							+ ": Access denied");

				if (localCtxIdListResult!=null)	LOG.info("lookup local call 3: " + localCtxIdListResult);


				return new AsyncResult<List<CtxIdentifier>>(localCtxIdListResult);
			}
		} else {
			//LOG.info("lookup remote call 1 requestor" + requestor);
			//LOG.info("lookup remote call 1 target" + target);

			final LookupCallback callback = new LookupCallback();

			// Testing code : change target to local identity 
			//LOG.info("lookup remote call: target id changed to local " + this.getLocalID());
			//ctxBrokerClient.lookupRemote(requestor,  this.getLocalID() , modelType, type, callback);
			LOG.info("lookup remote call target id:" + target.toString());
			//real code
			ctxBrokerClient.lookupRemote(requestor, target, modelType, type, callback);
			//LOG.info("lookup remote call 2");
			synchronized (callback) {

				try {
					callback.wait();
					remoteCtxIdListResult = callback.getResult();
					//LOG.info("lookup remote call 3" + callback.getResult());
				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote createEntity");
				}
			}
		}

		return new AsyncResult<List<CtxIdentifier>>(remoteCtxIdListResult);
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

	/*
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

		internalCtxBroker.registerForChanges(listener, ctxId);
	}

	/*
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

	/*
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

		internalCtxBroker.registerForChanges(listener, scope,attrType);
	}

	/*
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
	 * Sets the {@link ICtxAccessController} service reference.
	 * 
	 * @param ctxAccessController
	 *            the {@link ICtxAccessController} service reference to set
	 */
	public void setCtxAccessController(ICtxAccessController ctxAccessController) {

		this.ctxAccessController = ctxAccessController;
	}

	/**
	 * Sets the {@link IPrivacyLogAppender} service reference.
	 * 
	 * @param privacyLogAppender
	 *            the {@link IPrivacyLogAppender} service reference to set
	 */
	public void setPrivacyLogAppender(IPrivacyLogAppender privacyLogAppender) {

		this.privacyLogAppender = privacyLogAppender;
	}

	private void logRequest(final Requestor requestor, final IIdentity target) {

		try {
			if (this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, target);
		} catch (ServiceUnavailableException sue) {
			// do nothing
		}
	}

	/*
	 * 
	 */
	private IIdentity getLocalID(){
		INetworkNode cssNodeId = this.idMgr.getThisNetworkNode();
		IIdentity localID = null;
		try {
			localID = this.idMgr.fromJid(cssNodeId.getBareJid());
			//LOG.info("changed to local id " + localID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return localID;
	}

}