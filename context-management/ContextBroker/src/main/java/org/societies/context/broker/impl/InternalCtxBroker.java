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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEvaluationResults;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.DataTypeUtils;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.context.api.community.db.ICommunityCtxDBMgr;
import org.societies.context.api.community.inference.ICommunityCtxInferenceMgr;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.api.similarity.ICtxSimilarityEvaluator;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;
import org.societies.context.api.user.inference.IUserCtxInferenceMgr;
import org.societies.context.broker.api.CtxBrokerException;
import org.societies.context.broker.api.security.CtxPermission;
import org.societies.context.broker.api.security.ICtxAccessController;
import org.societies.context.broker.impl.comm.CtxBrokerClient;
import org.societies.context.broker.impl.comm.callbacks.CreateAssociationCallback;
import org.societies.context.broker.impl.comm.callbacks.CreateAttributeCallback;
import org.societies.context.broker.impl.comm.callbacks.CreateEntityCallback;
import org.societies.context.broker.impl.comm.callbacks.LookupCallback;
import org.societies.context.broker.impl.comm.callbacks.RemoveCtxCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveCommunityEntityIdCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveCtxCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveIndividualEntCallback;
import org.societies.context.broker.impl.comm.callbacks.UpdateCtxCallback;
import org.societies.context.broker.impl.util.CtxBrokerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * Internal Context Broker Implementation
 * This class implements the internal context broker interfaces and orchestrates the db 
 * management 
 */
@Service
public class InternalCtxBroker implements ICtxBroker {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(InternalCtxBroker.class);

	/** to define a dedicated Logger for Performance Testing */
	private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage");

	/** The event topics to create for local CSSs and CISs */
	public static final String[] EVENT_TOPICS = new String[] {
		CtxChangeEventTopic.CREATED,
		CtxChangeEventTopic.UPDATED,
		CtxChangeEventTopic.MODIFIED,
		CtxChangeEventTopic.REMOVED };

	/** The Comms Mgr service reference. */
	@Autowired(required=true)
	private ICommManager commMgr;

	/** The Comms Mgr Factory service reference. */
	@Autowired(required=true)
	private ICISCommunicationMgrFactory commMgrFactory;

	/** The privacy logging facility. */
	@Autowired(required=false)
	private IPrivacyLogAppender privacyLogAppender;

	/** The Context Event Mgmt service reference. */
	@Autowired(required=true)
	private ICtxEventMgr ctxEventMgr;

	/**
	 * The User Context History Mgmt service reference. 
	 * 
	 * @see {@link #setUserCtxHistoryMgr(IUserCtxHistoryMgr)}
	 */
	@Autowired(required=true)
	private IUserCtxHistoryMgr userCtxHistoryMgr;

	/**
	 * The User Context DB Mgmt service reference.
	 * 
	 * @see {@link #setUserCtxDBMgr(IUserCtxDBMgr)}
	 */
	@Autowired(required=true)
	private IUserCtxDBMgr userCtxDBMgr;

	/**
	 * The Community Context DB Mgmt service reference.
	 * 
	 * @see {@link #setCommunityCtxDBMgr(ICommunityCtxDBMgr)}
	 */
	@Autowired(required=true)
	private ICommunityCtxDBMgr communityCtxDBMgr;

	/**
	 * The User Inference Mgmt service reference.
	 * 
	 * @see {@link #setUserCtxInferenceMgr(IUserCtxInferenceMgr)}
	 */
	@Autowired(required=false)
	private IUserCtxInferenceMgr userCtxInferenceMgr;

	/**
	 * The Community Inference Mgmt service reference.
	 * 
	 * @see {@link #setCommunityCtxInferenceMgr(ICommunityCtxInferenceMgr)}
	 */
	@Autowired(required=false)
	private ICommunityCtxInferenceMgr communityCtxInferenceMgr;

	@Autowired(required=true)
	private CtxBrokerClient ctxBrokerClient;

	/** The ICtxAccessController service reference. */
	@Autowired(required=true)
	private ICtxAccessController ctxAccessController;

	/**
	 * Instantiates the platform Context Broker in Spring.
	 * 
	 * @param ctxBootLoader
	 */
	@Autowired(required=true)
	InternalCtxBroker(CtxBootLoader ctxBootLoader) {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}

	/*
	 * Used for JUnit testing only.
	 */
	public InternalCtxBroker() {

		LOG.info(this.getClass() + " instantiated");
	}


	@Override
	@Async
	public Future<CtxAssociation> createAssociation(String type) throws CtxException {

		return this.createAssociation(null, null, type);
	}


	@Override
	@Async
	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope,
			String type) throws CtxException {

		return this.createAttribute(null, scope, type);
	}


	@Override
	@Async
	public Future<CtxEntity> createEntity(String type) throws CtxException {


		return this.createEntity(null, null, type);
	}

	/**
	 * The Context Similarity Evaluator service reference.
	 *
	 * @see {@link #setCtxSimilarityEvaluator(ICtxSimilarityEvaluator)}
	 */
	@Autowired(required=true)
	private ICtxSimilarityEvaluator ctxSimilarityEval;

	@Override
	@Async
	public Future<IndividualCtxEntity> createIndividualEntity(
			final IIdentity cssId, final String ownerType) throws CtxException {

		if (cssId == null)
			throw new NullPointerException("cssId can't be null");
		if (ownerType == null)
			throw new NullPointerException("ownerType can't be null");

		IndividualCtxEntity cssOwnerEnt = null;

		try {
			if (LOG.isInfoEnabled())
				LOG.info("Creating event topics '" + Arrays.toString(EVENT_TOPICS) 
						+ "' for CSS owner " + cssId);
			this.ctxEventMgr.createTopics(cssId, EVENT_TOPICS);

			LOG.info("Checking if CSS owner context entity " + cssId + " exists...");
			cssOwnerEnt = this.retrieveIndividualEntity(cssId).get();
			if (cssOwnerEnt != null) {

				LOG.info("Found CSS owner context entity " + cssOwnerEnt.getId());
			} else {

				cssOwnerEnt = this.userCtxDBMgr.createIndividualEntity(
						cssId.getBareJid(), ownerType); 

				// TODO remove after pilot!
				final CtxAttribute cssIdAttr = this.userCtxDBMgr.createAttribute(
						cssOwnerEnt.getId(), CtxAttributeTypes.ID); 

				this.updateAttribute(cssIdAttr.getId(), cssId.toString());
				LOG.info("Created CSS owner context entity " + cssOwnerEnt.getId());
			}

			return new AsyncResult<IndividualCtxEntity>(cssOwnerEnt);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not create CSS owner context entity " + cssId
					+ ": " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Async
	public Future<CommunityCtxEntity> createCommunityEntity(IIdentity cisId)
			throws CtxException {

		if (cisId == null)
			throw new NullPointerException("cisId can't be null");
		if (!IdentityType.CIS.equals(cisId.getType()))
			throw new IllegalArgumentException("cisId is not of type CIS");

		if (LOG.isInfoEnabled())
			LOG.info("Creating event topics '" + Arrays.toString(EVENT_TOPICS) 
					+ "' for CIS " + cisId);
		this.ctxEventMgr.createTopics(cisId, EVENT_TOPICS);

		CommunityCtxEntity communityCtxEnt = communityCtxDBMgr.createCommunityEntity(cisId.toString());

		LOG.info("Community Context CREATE ENTITY performed with context ID:"+communityCtxEnt.getId()+" of type:"+communityCtxEnt.getType());
		return new AsyncResult<CommunityCtxEntity>(communityCtxEnt);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createCssNode(org.societies.api.identity.INetworkNode)
	 */
	@Override
	@Async
	public Future<CtxEntity> createCssNode(final INetworkNode cssNodeId)
			throws CtxException {

		if (cssNodeId == null)
			throw new NullPointerException("cssNodeId can't be null");

		CtxEntity result = null;
		try {
			LOG.info("Checking if CSS node context entity " + cssNodeId + " exists...");
			result = this.retrieveCssNode(cssNodeId).get();
			if (result != null) {
				LOG.info("Found CSS node context entity " + result);
			} else {
				final IIdentity cssId = this.commMgr.getIdManager().fromJid(
						cssNodeId.getBareJid().replace('@', '.')); // Android JIDs contain '@' instead of '.'
				final IndividualCtxEntity cssEnt = this.retrieveIndividualEntity(cssId).get();
				if (cssEnt == null)
					throw new CtxBrokerException("The IndividualCtxEntity for CSS '" 
							+ cssId + "' could not be found. Does node " + cssNodeId
							+ " belong to a local CSS?");
				final CtxAssociation ownsCssNodesAssoc;
				if (cssEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).isEmpty())
					ownsCssNodesAssoc = this.userCtxDBMgr.createAssociation(
							CtxAssociationTypes.OWNS_CSS_NODES);
				else
					ownsCssNodesAssoc = (CtxAssociation) this.userCtxDBMgr.retrieve(
							cssEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).iterator().next());
				ownsCssNodesAssoc.setParentEntity(cssEnt.getId());
				result = this.userCtxDBMgr.createEntity(CtxEntityTypes.CSS_NODE);
				ownsCssNodesAssoc.addChildEntity(result.getId());
				this.userCtxDBMgr.update(ownsCssNodesAssoc);
				final CtxAttribute cssNodeIdAttr = this.userCtxDBMgr.createAttribute(
						result.getId(), CtxAttributeTypes.ID);
				cssNodeIdAttr.setStringValue(cssNodeId.toString());
				this.userCtxDBMgr.update(cssNodeIdAttr);
				LOG.info("Created CSS node context entity " + result.getId());
			}

		} catch (Exception e) {
			throw new CtxBrokerException("Could not create CSS node context entity " + cssNodeId
					+ ": " + e.getLocalizedMessage(), e);
		}

		return new AsyncResult<CtxEntity>(result);
	}
	
	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#lookup(org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final CtxModelType modelType,
			String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		final IIdentity target = this.getLocalIdentity();
		return this.lookup(requestor, target, modelType, type);
	}
	
	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#lookup(org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	public Future<List<CtxIdentifier>> lookup(final IIdentity target, 
			final String type) throws CtxException {
	
		final Requestor requestor = this.getLocalRequestor();
		return this.lookup(requestor, target, type);
	}
	
	/*
	 * @see org.societies.api.context.broker.ICtxBroker#lookup(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor,
			final IIdentity target, final String type) throws CtxException {

		if (requestor == null) 
			throw new NullPointerException("requestor can't be null");
		if (target == null)
			throw new NullPointerException("target can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		
		if (LOG.isDebugEnabled())
			LOG.debug("lookup: requestor=" + requestor + ", target="
					+ target + ", type=" + type);

		final List<CtxIdentifier> result = new ArrayList<CtxIdentifier>();

		if (this.isLocalId(target)) { // L O C A L
			
			// Retrieve sub-types
			final DataTypeUtils dataTypeUtil = new DataTypeUtils();
			final Set<String> subTypes = dataTypeUtil.getLookableDataTypes(type);
			if (LOG.isDebugEnabled())
				LOG.debug("'" + type + "' subTypes: " + subTypes);
			
			if (IdentityType.CIS != target.getType()) { // U S E R
				
				result.addAll(this.userCtxDBMgr.lookup(target.getJid(), subTypes));
				
			} else { // C O M M U N I T Y
			
				// TODO lookup in Community DB
				throw new CtxBrokerException("Generic lookup for CIS data is not supported yet");
			}
			
		} else { // R E M O T E
			
			final LookupCallback callback = new LookupCallback();
			this.ctxBrokerClient.lookup(requestor, target, null, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null)
						result.addAll(callback.getResult());
					else
						throw callback.getException();

				} catch (InterruptedException e) {
					throw new CtxBrokerException("Interrupted while waiting for remote lookup response");
				}
			}
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("lookup: result=" + result);
		return new AsyncResult<List<CtxIdentifier>>(result);
	}
	
	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#lookup(org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final IIdentity target,
			final CtxModelType modelType, final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.lookup(requestor, target, modelType, type);
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
		
		if (LOG.isDebugEnabled())
			LOG.debug("lookup: requestor=" + requestor + ", target=" + target
					+ ", modelType=" + modelType + ", type=" + type);
		
		final List<CtxIdentifier> result = new ArrayList<CtxIdentifier>();
		
		if (this.isLocalId(target)) { // L O C A L
			
			if (IdentityType.CIS != target.getType()) { // U S E R
				// TODO Add IIdentity JID param to UserDBMgr
				result.addAll(this.userCtxDBMgr.lookup(modelType, type));
				
			} else { // C O M M U N I T Y
				// TODO Add IIdentity JID param to CommunityDBMgr
				result.addAll(this.communityCtxDBMgr.lookup(modelType, type));
			}
			
		} else { // R E M O T E
			
			final LookupCallback callback = new LookupCallback();
			this.ctxBrokerClient.lookup(requestor, target, modelType, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null)
						result.addAll(callback.getResult());
					else
						throw callback.getException();

				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote lookup");
				}
			}
		}

		if (LOG.isDebugEnabled())
			LOG.debug("lookup: result=" + result);
		return new AsyncResult<List<CtxIdentifier>>(result);
	}
	
	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#lookup(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final CtxEntityIdentifier entityId, 
			final CtxModelType modelType, final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.lookup(requestor, entityId, modelType, type);
	}
	
	/*
	 * @see org.societies.api.context.broker.ICtxBroker#lookup(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor,
			final CtxEntityIdentifier entityId, final CtxModelType modelType, 
			final String type) throws CtxException {

		if(requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (entityId == null)
			throw new NullPointerException("entityId can't be null");
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		if (!CtxModelType.ATTRIBUTE.equals(modelType) && !CtxModelType.ASSOCIATION.equals(modelType))
			throw new IllegalArgumentException("modelType is not ATTRIBUTE or ASSOCIATION");

		if (LOG.isDebugEnabled())
			LOG.debug("lookup: requestor=" + requestor + ", entityId="
					+ entityId + ", modelType=" + modelType + ", type=" 
					+ type);

		final List<CtxIdentifier> result = new ArrayList<CtxIdentifier>();
		
		final IIdentity target = this.extractIIdentity(entityId);
		// TODO Test
		//final CtxEntity entity;
		try {
			List<CtxIdentifier> listResults = this.lookup(requestor, target, modelType, type).get();
			result.addAll(listResults);
			/*
			if (IdentityType.CIS.equals(targetId.getType()))
				entity = (CtxEntity) this.communityCtxDBMgr.retrieve(entityId);
			else
				entity = (CtxEntity) this.userCtxDBMgr.retrieve(entityId);

			if (CtxModelType.ATTRIBUTE.equals(modelType)) {
				final Set<CtxAttribute> attrs = entity.getAttributes(type);
				for (final CtxAttribute attr : attrs)
					result.add(attr.getId());

			} else if (CtxModelType.ASSOCIATION.equals(modelType))  {

				final Set<CtxAssociationIdentifier> assocIds = entity.getAssociations(type);
				for (final CtxAssociationIdentifier assocId : assocIds)
					result.add(assocId);
			}
			 */
		} catch (Exception e) {

			throw new CtxBrokerException("Could not look up context " + modelType
					+ "(s) of type '" + type + "' under entity " + entityId
					+ ": " + e.getLocalizedMessage(), e);
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("lookup: result=" + result);
		return new AsyncResult<List<CtxIdentifier>>(result);
	}
	
	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {

		return this.lookupEntities(null, null, entityType, attribType, minAttribValue, maxAttribValue);
	}
	
	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> lookupEntities(List<CtxEntityIdentifier> ctxEntityIDList, String ctxAttributeType, Serializable value){

		List<CtxEntityIdentifier> entityList = new ArrayList<CtxEntityIdentifier>(); 
		try {
			for(CtxEntityIdentifier entityId :ctxEntityIDList){
				CtxEntity entity = (CtxEntity) this.retrieve(entityId).get();

				Set<CtxAttribute> ctxAttrSet = entity.getAttributes(ctxAttributeType);
				for(CtxAttribute ctxAttr : ctxAttrSet){

					if(CtxBrokerUtils.compareAttributeValues(ctxAttr,value)) {
						entityList.add(entityId);
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<CtxEntityIdentifier>>(entityList);
	}

	@Override
	@Async
	public Future<CtxModelObject> remove(CtxIdentifier identifier) throws CtxException {

		return this.remove(null, identifier);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieveIndividualEntity(org.societies.api.identity.IIdentity)
	 */
	@Override
	@Async
	public Future<IndividualCtxEntity> retrieveIndividualEntity(
			final IIdentity cssId) throws CtxException {

		if (cssId == null)
			throw new NullPointerException("cssId can't be null");

		this.logRequest(null, cssId);

		final IndividualCtxEntity cssOwner = 
				this.userCtxDBMgr.retrieveIndividualEntity(cssId.getBareJid());

		return new AsyncResult<IndividualCtxEntity>(cssOwner);
	}

	@Override
	@Async
	@Deprecated
	public Future<IndividualCtxEntity> retrieveCssOperator()
			throws CtxException {

		IIdentity localCssId;
		try {
			localCssId = this.commMgr.getIdManager().fromJid(this.commMgr.getIdManager().getThisNetworkNode().getBareJid());
		} catch (InvalidFormatException ife) {

			throw new CtxBrokerException("Could not retrieve local CSS IIdentity: "
					+ ife.getLocalizedMessage(), ife);
		}

		return this.retrieveIndividualEntity(localCssId);
	}


	@Override
	@Async
	public Future<CtxEntity> retrieveCssNode(final INetworkNode cssNodeId) 
			throws CtxException {

		if (cssNodeId == null)
			throw new NullPointerException("cssNodeId can't be null");

		CtxEntity cssNode = null;
		final List<CtxEntityIdentifier> entIds = this.userCtxDBMgr.lookupEntities(
				CtxEntityTypes.CSS_NODE, CtxAttributeTypes.ID, cssNodeId.toString(), cssNodeId.toString());
		if (!entIds.isEmpty()) {

			try {
				cssNode = (CtxEntity) this.retrieve(entIds.get(0)).get();
			} catch (Exception e) {

				throw new CtxBrokerException("Failed to retrieve CSS node context entity " + cssNodeId
						+ ": " + e.getLocalizedMessage(), e);
			}
		}
		return new AsyncResult<CtxEntity>(cssNode);
	}

	private void storeHoc(CtxModelObject ctxModelObj) throws CtxException {

		// ********************** HISTORY CODE ******************************* 
		// TODO move to HoC
		// this part allows the storage of attribute updates to context history
		if (CtxModelType.ATTRIBUTE.equals(ctxModelObj.getModelType())) {
			final CtxAttribute ctxAttr = (CtxAttribute) ctxModelObj;
			try {
				if (ctxAttr.isHistoryRecorded() && this.userCtxHistoryMgr != null){
					this.userCtxHistoryMgr.storeHoCAttribute(ctxAttr);
				}
			} catch (ServiceUnavailableException sue) {

				throw new CtxBrokerException("Could not update Context History: "
						+ "Context History Mgr is not available");
			}			
		}
	}
	// ********************** HISTORY CODE *******************************end of hoc code	

	/*
	 * Inference of a ctxAttribute will be triggered in case:
	 * a) retrieved value is null
	 * b) qoc of the retrieved value is not acceptable
	 */	
	protected CtxAttribute initiateInference(CtxAttribute ctxAttr){

		CtxAttribute inferedCtxAttr  = null;

		Boolean inferValue = false;
		Boolean isInferable = false;

		//CtxAttribute ctxAttr = (CtxAttribute) modelObjReturn;

		if(this.userCtxInferenceMgr.getInferrableTypes().contains(ctxAttr.getType()))  isInferable = true;

		if( !CtxBrokerUtils.hasValue(ctxAttr) && isInferable) {
			//LOG.info("has value "+ CtxBrokerUtils.hasValue(ctxAttr));
			inferValue = true;
		}

		if (CtxBrokerUtils.hasValue(ctxAttr) && isInferable) {
			if (this.userCtxInferenceMgr.isPoorQuality(ctxAttr.getQuality())) 
				inferValue = true;
		}
		//	LOG.info("inferValue: "+ inferValue);

		//TO DO remove following line when integration is completed
		inferValue = false;
		if(inferValue){
			//		LOG.info("before inference infered CtxAttr: "+ ctxAttr.getStringValue());	
			// TO DO multiple inference methods will be added
			inferedCtxAttr = userCtxInferenceMgr.predictContext(ctxAttr.getId(), new Date());	
			//		LOG.info("after inference inferedCtxAttr: "+ inferedCtxAttr.getId());
			//		LOG.info("after inference inferedCtxAttr: "+ inferedCtxAttr.getStringValue());
			//modelObjReturn = (CtxModelObject) inferedCtxAttr;
		}

		return inferedCtxAttr;
	}


	@Override
	@Async
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value) throws CtxException {

		return this.updateAttribute(attributeId, value, null);
	}


	@Override
	@Async
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric) throws CtxException {

		if (attributeId == null)
			throw new NullPointerException("attributeId can't be null");

		// Will throw IllegalArgumentException if value type is not supported
		final CtxAttributeValueType valueType = CtxBrokerUtils.findAttributeValueType(value);

		final CtxAttribute attributeReturn ;
		final CtxAttribute currentAttribute ;

		try {
			currentAttribute = this.retrieveAttribute(attributeId, false).get();
			//CtxAttribute attribute = (CtxAttribute) this.userCtxDBMgr.retrieve(attributeId);

			if (currentAttribute == null) {
				// Requested attribute not found
				return new AsyncResult<CtxAttribute>(null);
			} else {
				if (CtxAttributeValueType.EMPTY.equals(valueType))
					currentAttribute.setStringValue(null);
				else if (CtxAttributeValueType.STRING.equals(valueType))
					currentAttribute.setStringValue((String) value);
				else if (CtxAttributeValueType.INTEGER.equals(valueType))
					currentAttribute.setIntegerValue((Integer) value);
				else if (CtxAttributeValueType.DOUBLE.equals(valueType))
					currentAttribute.setDoubleValue((Double) value);
				else if (CtxAttributeValueType.BINARY.equals(valueType)){
					currentAttribute.setBinaryValue((byte[]) value);
					currentAttribute.setValueType(valueType);
				} else throw new CtxBrokerException("unkown type of attribute value");
			}
			attributeReturn = (CtxAttribute) this.update(currentAttribute).get();
			String valueString = CtxBrokerUtils.attributeValueAsString(value);
			LOG.info("Context UPDATE performed for context ID:"+attributeReturn.getId()+" of type:"+attributeReturn.getType()+" with value:" + valueString);

		} catch (InterruptedException e) {
			throw new CtxBrokerException("updateAttribute including value failed " + e.getLocalizedMessage());
		} catch (ExecutionException e) {
			throw new CtxBrokerException("updateAttribute including value failed " + e.getLocalizedMessage());
		}

		return new AsyncResult<CtxAttribute>(attributeReturn);
	}



	//***********************************************
	//     Context Change Events Methods  
	//***********************************************

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#registerForChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.identity.IIdentity)
	 */
	@Override
	public void registerForChanges(final CtxChangeEventListener listener,
			final IIdentity ownerId) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ownerId == null)
			throw new NullPointerException("ownerId can't be null");

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Registering context change event listener for IIdentity '"
						+ ownerId + "' to topics '" 
						+ Arrays.toString(topics) + "'");
			this.ctxEventMgr.registerChangeListener(listener, topics, ownerId);
		} else {
			throw new CtxBrokerException("Could not register context change event listener for IIdentity '"
					+ ownerId + "' to topics '" + Arrays.toString(topics)
					+ "': ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.identity.IIdentity)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final IIdentity ownerId) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ownerId == null)
			throw new NullPointerException("ownerId can't be null");

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Unregistering context change event listener for IIdentity '"
						+ ownerId + "' to topics '" 
						+ Arrays.toString(topics) + "'");
			this.ctxEventMgr.unregisterChangeListener(listener, topics, ownerId);
		} else {
			throw new CtxBrokerException("Could not register context change event listener for IIdentity '"
					+ ownerId + "' to topics '" + Arrays.toString(topics)
					+ "': ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerForChanges(final CtxChangeEventListener listener,
			final CtxIdentifier ctxId) throws CtxException {

		this.registerForChanges(null, listener, ctxId);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final CtxIdentifier ctxId) throws CtxException {

		this.unregisterFromChanges(null, listener, ctxId);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#registerForChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerForChanges(final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, final String attrType)
					throws CtxException {

		this.registerForChanges(null, listener, scope, attrType);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, final String attrType) throws CtxException {

		this.unregisterFromChanges(null, listener, scope, attrType);
	}


	//***********************************************
	//     Context Inference Methods  
	//***********************************************

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, Date date) throws CtxException {

		IIdentity targetCss;
		try {
			targetCss = this.commMgr.getIdManager().fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(null, targetCss);

		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException {


		IIdentity targetCss;
		try {
			targetCss = this.commMgr.getIdManager().fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(null, targetCss);

		return null;
	}

	//***********************************************
	//     Community Context Management Methods  
	//***********************************************


	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			final IIdentity cisId) throws CtxException {

		return this.retrieveCommunityEntityId(null, cisId);
	}


	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			CtxEntityIdentifier communityId) throws CtxException {

		List<CtxEntityIdentifier> result = null; 

		if (communityId == null)
			throw new NullPointerException("communityId can't be null");

		try {
			final CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.retrieve(communityId).get();

			Set<CtxEntityIdentifier> commMembersSet = communityEntity.getMembers();
			result = new ArrayList<CtxEntityIdentifier>(commMembersSet);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<List<CtxEntityIdentifier>>(result);
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Async
	public Future<IndividualCtxEntity> retrieveAdministratingCSS(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Async
	public Future<Set<CtxBond>> retrieveBonds(CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	//***********************************************
	//     Context History Management Methods  
	//***********************************************


	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			CtxAttributeIdentifier attrId, Date startDate, Date endDate) throws CtxException {

		/*
		final List<CtxHistoryAttribute> result = new ArrayList<CtxHistoryAttribute>();
		IIdentity targetCss;
		try {
			targetCss = this.commMgr.getIdManager().fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(null, targetCss);

		result.addAll(this.userCtxHistoryMgr.retrieveHistory(attrId, startDate, endDate));
		 */
		return this.retrieveHistory(null, attrId, startDate, endDate);
	}

	@Override
	@Async
	public Future<Integer> removeHistory(String type, Date startDate, Date endDate) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			CtxAttributeIdentifier attrId, int arg1) throws CtxException {
		// TODO Auto-generated method stub
		IIdentity targetCss;
		try {
			targetCss = this.commMgr.getIdManager().fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(null, targetCss);

		return null;
	}


	@Override
	@Async
	public Future<List<CtxAttributeIdentifier>> getHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> arg1)
					throws CtxException {

		List<CtxAttributeIdentifier> tupleAttrIDs = new ArrayList<CtxAttributeIdentifier>(); 

		//	final String tupleAttrType = "tupleIds_" + primaryAttrIdentifier.getType();
		//final String tupleAttrType = "tupleIds_" + primaryAttrIdentifier.toString();

		final String tupleAttrType = "tuple_"+primaryAttrIdentifier.getType().toString()+"_"+primaryAttrIdentifier.getObjectNumber().toString();

		List<CtxIdentifier> ls;
		try {
			ls = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			if (ls.size() > 0) {
				CtxIdentifier id = ls.get(0);
				final CtxAttribute tupleIdsAttribute = (CtxAttribute) this.userCtxDBMgr.retrieve(id);

				//deserialise object
				tupleAttrIDs = (List<CtxAttributeIdentifier>) SerialisationHelper.deserialise(tupleIdsAttribute.getBinaryValue(), this.getClass().getClassLoader());
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<CtxAttributeIdentifier>>(tupleAttrIDs);
	}

	@Override
	@Async
	public Future<Boolean> removeHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Async
	public Future<Boolean> setHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException {

		boolean result = false;

		result = this.userCtxHistoryMgr.setCtxHistoryTuples(primaryAttrIdentifier, listOfEscortingAttributeIds);
		return new AsyncResult<Boolean>(result);
	}

	@Override
	@Async
	public Future<List<CtxAttributeIdentifier>> updateHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> newAttrList)
					throws CtxException {

		List<CtxAttributeIdentifier> results = new ArrayList<CtxAttributeIdentifier>();
		try {
			for (CtxAttributeIdentifier escortingAttrID : newAttrList) {
				// set history flag for all escorting attributes

				CtxAttribute attr = (CtxAttribute) this.userCtxDBMgr.retrieve(escortingAttrID);
				if(attr != null && attr.isHistoryRecorded() == false){
					attr.setHistoryRecorded(true);
					this.update(attr);	
				}

			}
			// set hoc recording flag for the attributes contained in tuple list --end

			final String tupleAttrType = "tuple_"+primaryAttrIdentifier.getType().toString()+"_"+primaryAttrIdentifier.getObjectNumber().toString();

			List<CtxAttributeIdentifier> newTupleAttrIDs = new ArrayList<CtxAttributeIdentifier>(); 
			newTupleAttrIDs.add(0,primaryAttrIdentifier);
			newTupleAttrIDs.addAll(newAttrList);

			List<CtxIdentifier> ls;			
			ls = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			if (ls.size() > 0) {
				CtxIdentifier id = ls.get(0);
				final CtxAttribute tupleIdsAttribute = (CtxAttribute) this.userCtxDBMgr.retrieve(id);

				//deserialise object
				byte[] attrIdsBlob = SerialisationHelper.serialise((Serializable) newTupleAttrIDs);
				tupleIdsAttribute.setBinaryValue(attrIdsBlob);
				CtxAttribute updatedAttr = (CtxAttribute) this.update(tupleIdsAttribute).get();

				if(updatedAttr != null && updatedAttr.getType().contains("tuple_")) {
					results = (List<CtxAttributeIdentifier>) SerialisationHelper.deserialise(updatedAttr.getBinaryValue(), this.getClass().getClassLoader());
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<CtxAttributeIdentifier>>(results);
	}

	@Override
	@Async
	public Future<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>> retrieveHistoryTuples(
			String attributeType, List<CtxAttributeIdentifier> escortingAttrIds,
			Date startDate, Date endDate) {

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		try {
			List<CtxIdentifier> ctxAttrListIds = this.lookup(CtxModelType.ATTRIBUTE, attributeType).get();
			//LOG.info("ctxAttribute list "+ctxAttrListIds);
			CtxAttributeIdentifier primaryAttrId = null;

			for(int i=0; i< ctxAttrListIds.size(); i++){
				primaryAttrId = (CtxAttributeIdentifier) ctxAttrListIds.get(i);

				IIdentity targetCss;
				try {
					targetCss = this.commMgr.getIdManager().fromJid(primaryAttrId.getOwnerId());
				} catch (InvalidFormatException ife) {
					throw new CtxBrokerException("Could not create IIdentity from JID", ife);
				}

				this.logRequest(null, targetCss);

				List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
				Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tempTupleResults = new HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>(); 

				tempTupleResults =	retrieveHistoryTuples(primaryAttrId, listOfEscortingAttributeIds, startDate, endDate).get();
				tupleResults.putAll(tempTupleResults);
			}			
			// short tupleResults data based on timestamps
			tupleResults = shortByTime((HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>) tupleResults);


		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>>(tupleResults);
	}




	@Override
	@Async
	public Future<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>> retrieveHistoryTuples(
			CtxAttributeIdentifier primaryAttrId, List<CtxAttributeIdentifier> escortingAttrIds,
			Date startDate, Date endDate) throws CtxException {

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		//LOG.info("retrieveHistoryTuples updating hocAttrs primaryAttr: "+primaryAttrId);

		if(primaryAttrId!= null){ // TODO throw NPE otherwise

			IIdentity targetCss;
			try {
				targetCss = this.commMgr.getIdManager().fromJid(primaryAttrId.getOwnerId());
			} catch (InvalidFormatException ife) {
				throw new CtxBrokerException("Could not create IIdentity from JID", ife);
			}

			this.logRequest(null, targetCss);
			String tupleAttrType = "tuple_"+primaryAttrId.getType().toString()+"_"+primaryAttrId.getObjectNumber().toString();
			//LOG.info("retrieve: tuple attr type "+ tupleAttrType);

			List<CtxIdentifier> listIds;

			try {
				listIds = this.lookup(CtxModelType.ATTRIBUTE,tupleAttrType).get();
				if( listIds.size()>0){

					CtxAttributeIdentifier tupleAttrTypeID = (CtxAttributeIdentifier) listIds.get(0);

					// retrieve historic attrs of type "tuple_action"
					// each hoc attr contains a value (blob) list of historic attrs store together
					List<CtxHistoryAttribute> hocResults = retrieveHistory(tupleAttrTypeID,startDate,endDate).get();            

					// for each "tuple_status" hoc attr 
					for (CtxHistoryAttribute hocAttr : hocResults) {

						// get the list of hoc attrs stored as BlobValue
						List<CtxHistoryAttribute> tupleValueList = (List<CtxHistoryAttribute>) SerialisationHelper.deserialise(hocAttr.getBinaryValue(), this.getClass().getClassLoader());

						// list of historic attributes contained in "tuple_status" retrieved
						//LOG.info("retrieveHistoryTuples tupleValueList: "+tupleValueList);
						//int ia = 0;
						//for each historic attr 
						for (CtxHistoryAttribute tupledHoCAttrTemp : tupleValueList){
							//the key , primary historic attribute
							CtxHistoryAttribute keyAttr = null;
							//the escorting historic attributes
							List<CtxHistoryAttribute> listEscHocAttrs = new ArrayList<CtxHistoryAttribute>();
							//for each historic attr in blob value check if the identifier equals the primary identifier
							if (tupledHoCAttrTemp.getId().toString().equals(primaryAttrId.toString())){
								//	ia++;
								keyAttr = tupledHoCAttrTemp;
								for (CtxHistoryAttribute tupledHoCAttrEscorting : tupleValueList){
									if (!(tupledHoCAttrEscorting.getId().toString().equals(primaryAttrId.toString()))){
										listEscHocAttrs.add(tupledHoCAttrEscorting);
									}  
								}
								results.put(keyAttr, listEscHocAttrs);    
							}
						}// end of for loop
					}	
				}//if size
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(results == null){
			results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		}
		//LOG.info("retrieveHistoryTuples results: "+results);

		return new AsyncResult<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>>(results);
	}




	private LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>> shortByTime(HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>> data){
		LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>> result = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		TreeMap<Date,CtxHistoryAttribute> tempHocDataTreeMap = new TreeMap<Date,CtxHistoryAttribute>();

		for(CtxHistoryAttribute hocAttr: data.keySet()){

			tempHocDataTreeMap.put(hocAttr.getLastUpdated(),hocAttr);
		}

		for(Date date :tempHocDataTreeMap.keySet()){

			CtxHistoryAttribute keyHocAttr = tempHocDataTreeMap.get(date);
			result.put(keyHocAttr, data.get(keyHocAttr));
		}

		return result;
	}


	/*
	public void storeHoCAttributeTuples(CtxAttribute primaryAttr){

		//String tupleAttrType = "tuple_"+primaryAttr.getType().toString();
		LOG.info("storing hoc tuples for " +primaryAttr.getId());

		String tupleAttrType = "tuple_"+primaryAttr.getId().getType().toString()+"_"+primaryAttr.getId().getObjectNumber().toString();
		//LOG.info("store: tuple attr type "+ tupleAttrType);
		// the attr that will maintain the tuples; 
		CtxAttribute tupleAttr = null;
		List<CtxHistoryAttribute> tupleValueList = new ArrayList<CtxHistoryAttribute>();
		try {
			List<CtxAttributeIdentifier> tempEscListIds = new ArrayList<CtxAttributeIdentifier>();
			List<CtxAttributeIdentifier> tupleListIds = this.getHistoryTuples(primaryAttr.getId(),tempEscListIds).get();

			List<CtxIdentifier> tupleAttrIDsList = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			if(tupleAttrIDsList.size() != 0){
				LOG.info("retrieved: "+ tupleAttrType);
				//tuple_status retrieved
				tupleAttr = (CtxAttribute) this.retrieveAttribute( (CtxAttributeIdentifier) tupleAttrIDsList.get(0), false).get();
			} else {
				LOG.info("created: "+ tupleAttrType);
				//tuple_status created
				tupleAttr = this.createAttribute(primaryAttr.getScope(), tupleAttrType).get();
			} 

			//prepare value of ctxAttribute
			for (CtxAttributeIdentifier tupleAttrID : tupleListIds) {
				//for one of the escorting attrIds retrieve all history and find the latest value

				List<CtxHistoryAttribute> allValues = this.retrieveHistory(tupleAttrID, null, null).get();
				if (allValues != null){
					//finding latest hoc value
					int size = allValues.size();
					int last = 0;
					if (size >= 1){
						last = size-1;    
						CtxHistoryAttribute latestHoCAttr2 = allValues.get(last);
						if (latestHoCAttr2 != null )tupleValueList.add(latestHoCAttr2);
						LOG.info(" latestHoCAttr2 attribute to be stored in tuple: "+ latestHoCAttr2);
					}
				}           

			}
			byte[] tupleValueListBlob = SerialisationHelper.serialise((Serializable) tupleValueList);
			if(tupleAttr != null) tupleAttr.setBinaryValue(tupleValueListBlob);

			//LOG.info("ready to store tupleAttr: "+tupleAttr);

			CtxHistoryAttribute hocAttr = this.userCtxHistoryMgr.createHistoryAttribute(tupleAttr);


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	 */

	@Override
	@Async
	public Future<CtxHistoryAttribute> createHistoryAttribute(CtxAttributeIdentifier attID, Date date, Serializable value, CtxAttributeValueType valueType){

		CtxHistoryAttribute hocAttr = null;
		try {
			hocAttr = this.userCtxHistoryMgr.createHistoryAttribute(attID,date,value,valueType);
		} catch (CtxException e) {

			LOG.error("context attribute not stored in context DB"
					+ attID + ": " + e.getLocalizedMessage(), e);

		}	

		return new AsyncResult<CtxHistoryAttribute>(hocAttr);
	}


	/*
	 * HoC tuples will be stored in an attribute of type "tuple_attibuteType" (tuple_status)
	 * the value will contain a list of ICtxHistoricAttribute 
	 * 
	 * tupleAttrIDs  the list of escorting attributes (also contains primary attribute id)
	 * ctxHocAttr  primary attribute to be stored 
	 */


	//********************************************************************
	//**************** end of hoc code  **********************************



	//******************************************
	//  service refs used by junit tests
	//******************************************

	/**
	 * Sets the User Context DB Mgmt service reference.
	 * 
	 * @param userDB
	 *            the User Context DB Mgmt service reference to set.
	 */
	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) {

		this.userCtxDBMgr = userDB;
	}

	/**
	 * Sets the Community Context DB Mgmt service reference.
	 * 
	 * @param userDB
	 *            the User Context DB Mgmt service reference to set.
	 */
	public void setCommunityCtxDBMgr(ICommunityCtxDBMgr communityCtxDBMgr) {

		this.communityCtxDBMgr = communityCtxDBMgr;
	}

	/**
	 * Sets the User Context History Mgmt service reference.
	 * 
	 * @param userCtxHistoryMgr
	 *            the User Context History Mgmt service reference to set
	 */
	public void setUserCtxHistoryMgr(IUserCtxHistoryMgr userCtxHistoryMgr) {

		this.userCtxHistoryMgr = userCtxHistoryMgr;
	}

	/**
	 * Sets the UserCtxInferenceMgr service reference.
	 * 
	 * @param idMgr
	 *            the UserCtxInferenceMgr service reference to set.
	 */
	public void setUserCtxInferenceMgr(IUserCtxInferenceMgr userCtxInferenceMgr) {

		this.userCtxInferenceMgr = userCtxInferenceMgr;
	}

	/**
	 * Sets the UserCtxInferenceMgr service reference.
	 * 
	 * @param ICtxSimilarityEvaluator
	 *            the ctxSimilarityEval service reference to set.
	 */
	public void setCtxSimilarityEvaluator(ICtxSimilarityEvaluator ICSE) {
		this.ctxSimilarityEval = ICSE;
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

	//******************************************
	//  service refs used by junit tests
	//******************************************

	@Override
	public CtxAttribute estimateCommunityContext(CtxEntityIdentifier communityCtxEntityID,	CtxAttributeIdentifier ctxAttrId) {

		CtxAttribute returnCtxAttr = this.communityCtxInferenceMgr.estimateCommunityContext(communityCtxEntityID, ctxAttrId);

		// TODO at this point check if inference (estimation) outcome is acceptable and if yes persist ctxAttribute

		if(returnCtxAttr != null)
			try {

				returnCtxAttr = (CtxAttribute) this.update(returnCtxAttr).get();

			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return returnCtxAttr;
	}

	private void logRequest(final Requestor requestor, final IIdentity target) {

		try {
			if (this.privacyLogAppender != null)
				this.privacyLogAppender.logContext(requestor, target);
		} catch (ServiceUnavailableException sue) {
			// do nothing
		}
	}

	@Override
	@Async
	public Future<CtxEntity> createEntity(Requestor requestor,
			IIdentity targetID, String type) throws CtxException {

		if (requestor == null) requestor = this.getLocalRequestor();
		if (targetID == null) targetID = this.getLocalIdentity();

		CtxEntity entityResult = null;

		// CSS case
		if (IdentityType.CSS.equals(targetID.getType()) 
				|| IdentityType.CSS_RICH.equals(targetID.getType())
				|| IdentityType.CSS_LIGHT.equals(targetID.getType())) {


			if (this.commMgr.getIdManager().isMine(targetID)) {

				entityResult = this.userCtxDBMgr.createEntity(type);

			}else {

				final CreateEntityCallback callback = new CreateEntityCallback();
				this.ctxBrokerClient.createEntity(requestor, targetID, type, callback);

				synchronized (callback) {
					try {
						callback.wait();
						entityResult = callback.getResult();
					} catch (InterruptedException e) {

						throw new CtxBrokerException("Interrupted while waiting for remote createEntity: "+e.getLocalizedMessage(),e);
					}
				}
			}
			// CIS case
		} else if (IdentityType.CIS.equals(targetID.getType())){

			entityResult = this.communityCtxDBMgr.createEntity(targetID.toString(), type);
			LOG.info("Community Context CREATE ENTITY performed with context ID:"+entityResult.getId()+" of type:"+entityResult.getType());

		} 

		return new AsyncResult<CtxEntity>(entityResult);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#createAttribute(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAttribute> createAttribute(Requestor requestor,
			CtxEntityIdentifier scope, String type) throws CtxException {

		if(requestor == null)requestor = getLocalRequestor();

		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		CtxAttribute ctxAttributeResult = null;
		IIdentity scopeID;

		try {
			scopeID = this.commMgr.getIdManager().fromJid(scope.getOwnerId());
		} catch (InvalidFormatException e1) {
			throw new CtxBrokerException(scope.getOwnerId()
					+ ": Invalid owner IIdentity String: " 
					+ e1.getLocalizedMessage(), e1);
		} 

		if (IdentityType.CSS.equals(scopeID.getType()) 
				|| IdentityType.CSS_RICH.equals(scopeID.getType())
				|| IdentityType.CSS_LIGHT.equals(scopeID.getType())) {

			//local call
			if (this.commMgr.getIdManager().isMine(scopeID)){

				ctxAttributeResult  =	this.userCtxDBMgr.createAttribute(scope, type);	

				LOG.info("Context CREATE ATTRIBUTE performed for context ID:"+ctxAttributeResult.getId()+" of type:"+ctxAttributeResult.getType());			

				//TODO origin type should be set in db manager
				if (ctxAttributeResult.getQuality().getOriginType() == null) {
					ctxAttributeResult.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
				}			

			} else {

				// remote call
				final CreateAttributeCallback callback = new CreateAttributeCallback();
				ctxBrokerClient.createAttribute(requestor, scopeID, scope, type, callback);
				synchronized (callback) {
					try {
						//LOG.info("Attribute creation Callback wait");
						callback.wait();
						ctxAttributeResult = callback.getResult();
						//LOG.info("ctxAttribute retrieved from callback : "+ctxAttribute.getId());
					} catch (InterruptedException e) {

						throw new CtxBrokerException("Interrupted while waiting for remote ctxAttribute");
					}
				}
				//end of remote code
			}

			//community context 
		} else if (IdentityType.CIS.equals(scopeID.getType())){

			ctxAttributeResult = this.communityCtxDBMgr.createAttribute(scope, type);
			LOG.info("Community Context CREATE ATTRIBUTE performed with context ID:"+ctxAttributeResult.getId()+" of type:"+ctxAttributeResult.getType());
		} 

		return new AsyncResult<CtxAttribute>(ctxAttributeResult);
	}

	@Override
	@Async
	public Future<CtxAssociation> createAssociation(Requestor requestor,
			IIdentity targetId, String type) throws CtxException {

		if (requestor == null) requestor = this.getLocalRequestor();
		if (targetId == null) targetId = this.getLocalIdentity();


		CtxAssociation associationResult = null;
		// CSS case
		if (IdentityType.CSS.equals(targetId.getType()) 
				|| IdentityType.CSS_RICH.equals(targetId.getType())
				|| IdentityType.CSS_LIGHT.equals(targetId.getType())) {



			if (this.commMgr.getIdManager().isMine(targetId)) {

				associationResult = this.userCtxDBMgr.createAssociation(type);

			}else {

				final CreateAssociationCallback callback = new CreateAssociationCallback();
				this.ctxBrokerClient.createAssociation(requestor, targetId, type, callback);

				synchronized (callback) {
					try {
						callback.wait();
						associationResult = callback.getResult();

					} catch (InterruptedException e) {
						throw new CtxBrokerException("Interrupted while waiting for remote createEntity: "+e.getLocalizedMessage(),e);
					}
				}			
			}

		} else if (IdentityType.CIS.equals(targetId.getType())){

			associationResult = this.communityCtxDBMgr.createAssociation(targetId.toString(), type);
			LOG.info("Community Context CREATE ASSOCIATION performed with context ID:"+associationResult.getId()+" of type:"+associationResult.getType());
		} 


		if (associationResult!=null)
			return new AsyncResult<CtxAssociation>(associationResult);
		else 
			return new AsyncResult<CtxAssociation>(null);	
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieve(org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	@Async
	public Future<CtxModelObject> retrieve(final CtxIdentifier ctxId) 
			throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.retrieve(requestor, ctxId);
	}
	
	/*
	 * @see org.societies.api.context.broker.ICtxBroker#retrieve(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	@Async
	public Future<CtxModelObject> retrieve(final Requestor requestor,
			final CtxIdentifier ctxId) throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		if (LOG.isDebugEnabled())
			LOG.debug("retrieve: requestor=" + requestor + ", ctxId=" + ctxId);
		
		CtxModelObject result = null;

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(ctxId);
				
		// Log with Privacy Log Appender
		this.logRequest(requestor, target);

		if (this.isLocalId(target)) { // L O C A L
			
			// Check if access control is required.
			if(!requestor.equals(this.getLocalRequestor())) {
				// Check READ permission
				this.ctxAccessController.checkPermission(requestor, target,
						new CtxPermission(ctxId, CtxPermission.READ));
			}
			// No CtxAccessControlException thrown implies READ access has been granted
			
			if (IdentityType.CIS != target.getType()) { // U S E R
				
				if (ctxId instanceof CtxAttributeIdentifier) { // I N F E R E N C E
					result = this.inferUserAttribute((CtxAttributeIdentifier) ctxId);
				} else {
					result = this.userCtxDBMgr.retrieve(ctxId);
				}	
				
			} else { // C O M M U N I T Y
				
				if (ctxId instanceof CtxAttributeIdentifier) { // I N F E R E N C E
					result = this.inferCommunityAttribute((CtxAttributeIdentifier) ctxId);
				} else {
					result = this.communityCtxDBMgr.retrieve(ctxId);
				}
			}
			
			// TODO Obfuscate result if 
			// 1. requestor is not local AND
			// 2. result is a context attribute
			if(!requestor.equals(this.getLocalRequestor()) && result instanceof CtxAttribute) {
			}
			
		} else { // R E M O T E
			
			// Needed for performance test
			long initTimestamp = System.nanoTime();
			
			final RetrieveCtxCallback callback = new RetrieveCtxCallback();
			this.ctxBrokerClient.retrieve(requestor, ctxId, callback); 
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null)
						result = callback.getResult();
					else 
						throw callback.getException();

					// Needed for performance test
					if (PERF_LOG.isTraceEnabled()) {
						final IPerformanceMessage m = new PerformanceMessage();
						m.setTestContext("ContextBroker_Delay_RemoteContextRetrieval");
						m.setSourceComponent(this.getClass().getName());
						m.setPerformanceType(IPerformanceMessage.Delay);
						m.setOperationType("RemoteCSS_ContextRetrieval");
						m.setD82TestTableName("S11");
						long delay = System.nanoTime() - initTimestamp;
						m.setPerformanceNameValue("Delay="+(delay));
						PERF_LOG.trace(m.toString());
					}

				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote retrieve");
				}
			}
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("retrieve: result=" + ((result != null) ? result.getId() : "NULL"));
		return new AsyncResult<CtxModelObject>(result);
	}
	
	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieveAttribute(org.societies.api.context.model.CtxAttributeIdentifier, boolean)
	 */
	@Override
	@Async
	public Future<CtxAttribute> retrieveAttribute(
			final CtxAttributeIdentifier ctxAttrId, 
			final boolean enableInference) throws CtxException {

		if (ctxAttrId == null)
			throw new NullPointerException("ctxAttrId can't be null");

		if (LOG.isDebugEnabled())
			LOG.debug("retrieveAttribute: ctxAttrId=" + ctxAttrId 
					+ ", enableInference=" + enableInference);

		CtxAttribute result = null;

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(ctxAttrId);

		// Log with Privacy Log Appender
		this.logRequest(null, target);
		
		if (this.isLocalId(target)) { // L O C A L
			
			if (IdentityType.CIS != target.getType()) { // U S E R

				if (enableInference) { // I N F E R E N C E
					result = this.inferUserAttribute(ctxAttrId);
				} else {
					result = (CtxAttribute) this.userCtxDBMgr.retrieve(ctxAttrId);
				}	

			} else { // C O M M U N I T Y

				if (enableInference) { // I N F E R E N C E
					result = this.inferCommunityAttribute(ctxAttrId);
				} else {
					result = (CtxAttribute) this.communityCtxDBMgr.retrieve(ctxAttrId);
				}
			}
			
		} else { // R E M O T E
			
			throw new IllegalArgumentException("'" + ctxAttrId + "' owned by '"
					+ target + "' is not managed locally");
		}

		if (LOG.isDebugEnabled())
			LOG.debug("retrieveAttribute: result=" + ((result != null) ? result.getId() : "NULL"));
		return new AsyncResult<CtxAttribute>(result);
	}
	
	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieve(java.util.List)
	 */
	@Override
	public Future<List<CtxModelObject>> retrieve(
			final List<CtxIdentifier> ctxIdList) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.retrieve(requestor, ctxIdList);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#retrieve(org.societies.api.identity.Requestor, java.util.List)
	 */
	@Override
	public Future<List<CtxModelObject>> retrieve(Requestor requestor,
			final List<CtxIdentifier> ctxIdList) throws CtxException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (ctxIdList == null)
			throw new NullPointerException("ctxIdList can't be null");
		
		if (LOG.isDebugEnabled())
			LOG.debug("retrieve: requestor=" + requestor + ", ctxIdList="
					+ ctxIdList);

		final List<CtxModelObject> result = new ArrayList<CtxModelObject>(
				ctxIdList.size());

		for (final CtxIdentifier ctxId : ctxIdList) {
			try {
				result.add(this.retrieve(requestor, ctxId).get());
			} catch (CtxException ce) {
				// This can also be a CtxAccessControlException
				// TODO Should we immediately fail in case of a CtxAccessControlException ? 
				throw ce;
			} catch (Exception e) {
				throw new CtxBrokerException(e.getLocalizedMessage(), e);
			}
		}
						
		if (LOG.isDebugEnabled())
			LOG.debug("retrieve: result=" + result);
		return new AsyncResult<List<CtxModelObject>>(result);
	}
	
	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#update(org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	@Async
	public Future<CtxModelObject> update(final CtxModelObject ctxModelObject) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.update(requestor, ctxModelObject);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#update(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	@Async
	public Future<CtxModelObject> update(final Requestor requestor,
			final CtxModelObject ctxModelObject) throws CtxException {

		if(requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (ctxModelObject == null) 
			throw new NullPointerException("ctxModelObject can't be null");

		if (LOG.isDebugEnabled())
			LOG.debug("update: requestor=" + requestor + ", ctxModelObject=" + ctxModelObject);
		
		CtxModelObject result = null; 

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(ctxModelObject);
		
		if (this.isLocalId(target)) { // L O C A L
			
			// Check if access control is required
			if (!requestor.equals(this.getLocalRequestor())) {
				// Check WRITE permission
				this.ctxAccessController.checkPermission(requestor, target,	
						new CtxPermission(ctxModelObject.getId(), CtxPermission.WRITE));
			}
			// No CtxAccessControlException thrown implies WRITE access has been granted
			
			if (IdentityType.CIS != target.getType()) { // U S E R
				
				result = this.userCtxDBMgr.update(ctxModelObject);
				
			} else { // C O M M U N I T Y
				
				result = this.communityCtxDBMgr.update(ctxModelObject);
			}
			
			// Update HoC
			this.storeHoc(ctxModelObject);
			
		} else { // R E M O T E
			
			final UpdateCtxCallback callback = new UpdateCtxCallback();
			this.ctxBrokerClient.update(requestor, ctxModelObject, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null)
						result = callback.getResult();
					else
						throw callback.getException();

				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote update");
				}
			}
		}

		if (LOG.isDebugEnabled())
			LOG.debug("update: result=" + ((result != null) ? result.getId() : "NULL"));
		return new AsyncResult<CtxModelObject>(result);
	}

	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> lookupEntities(
			Requestor requestor, IIdentity targetCss, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {

		if (requestor == null) requestor = this.getLocalRequestor();
		if (targetCss == null) targetCss = this.getLocalIdentity();

		final List<CtxEntityIdentifier> results = new ArrayList<CtxEntityIdentifier>(); 

		results.addAll(
				this.userCtxDBMgr.lookupEntities(entityType, attribType, minAttribValue, maxAttribValue));

		return new AsyncResult<List<CtxEntityIdentifier>>(results);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.Requestor, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerForChanges(Requestor requestor,
			CtxChangeEventListener listener, CtxIdentifier ctxId)
					throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		if (requestor == null)
			requestor = this.getLocalRequestor();

		final String[] topics = new String[] {
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Registering context change event listener for object '"
						+ ctxId + "' to topics '" + Arrays.toString(topics) + "'");
			this.ctxEventMgr.registerChangeListener(listener, topics, ctxId);

			try {
				if (ctxId instanceof CtxAttributeIdentifier 
						&& this.userCtxInferenceMgr.getInferrableTypes().contains(ctxId.getType())) {
					if (LOG.isInfoEnabled()) // TODO DEBUG
						LOG.info("Triggering continuous inference of attribute '" + ctxId + "'");
					this.userCtxInferenceMgr.refineContinuously(
							(CtxAttributeIdentifier) ctxId, new Double(0)); // TODO handle updateFreq
				}
			} catch (ServiceUnavailableException sue) {

				LOG.warn("Could not check if attribute requires inference: "
						+ "User Context Inference Mgr is not available");
			}
		} else {
			throw new CtxBrokerException("Could not register context change event listener for object '"
					+ ctxId + "' to topics '" + Arrays.toString(topics)
					+ "': ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.Requestor, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterFromChanges(Requestor requestor,
			CtxChangeEventListener listener, CtxIdentifier ctxId)
					throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		if (requestor == null)
			requestor = this.getLocalRequestor();

		final String[] topics = new String[] {
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Unregistering context change event listener for object '"
						+ ctxId + "' to topics '" + Arrays.toString(topics) + "'");
			this.ctxEventMgr.unregisterChangeListener(listener, topics, ctxId);

			/* TODO
			try {
				if (ctxId instanceof CtxAttributeIdentifier 
						&& this.userCtxInferenceMgr.getInferrableTypes().contains(ctxId.getType())) {
					if (LOG.isInfoEnabled()) // TODO DEBUG
						LOG.info("Triggering continuous inference of attribute '" + ctxId + "'");
					this.userCtxInferenceMgr.refineContinuously(
							(CtxAttributeIdentifier) ctxId, new Double(0)); // TODO handle updateFreq
				}
			} catch (ServiceUnavailableException sue) {

				LOG.warn("Could not check if attribute requires inference: "
						+ "User Context Inference Mgr is not available");
			}
			 */
		} else {
			throw new CtxBrokerException(
					"Could not unregister context change event listener for object '"
							+ ctxId + "' to topics '" + Arrays.toString(topics)
							+ "': ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.Requestor, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerForChanges(Requestor requestor,
			CtxChangeEventListener listener, CtxEntityIdentifier scope,
			String attrType) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");

		if (requestor == null)
			requestor = this.getLocalRequestor();

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Registering context change event listener for attributes with scope '"
						+ scope + "' and type '" + attrType + "' to topics '" 
						+ Arrays.toString(topics) + "'");
			this.ctxEventMgr.registerChangeListener(listener, topics, scope, attrType );
		} else {
			throw new CtxBrokerException("Could not register context change event listener for attributes with scope '"
					+ scope + "' and type '" + attrType + "' to topics '" + Arrays.toString(topics)
					+ "': ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.Requestor, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterFromChanges(Requestor requestor,
			CtxChangeEventListener listener, CtxEntityIdentifier scope,
			String attrType) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");

		if (requestor == null)
			requestor = this.getLocalRequestor();

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Unregistering context change event listener for attributes with scope '"
						+ scope + "' and type '" + attrType + "' to topics '" 
						+ Arrays.toString(topics) + "'");
			this.ctxEventMgr.unregisterChangeListener(listener, topics, scope, attrType );
		} else {
			throw new CtxBrokerException(
					"Could not unregister context change event listener for attributes with scope '"
							+ scope + "' and type '" + attrType + "' to topics '" + Arrays.toString(topics)
							+ "': ICtxEventMgr service is not available");
		}
	}

	@Override
	@Async
	public Future<CtxModelObject> remove(Requestor requestor,
			CtxIdentifier identifier) throws CtxException {

		CtxModelObject objectResult = null ;

		if (requestor == null) requestor = getLocalRequestor();

		if (identifier == null)
			throw new NullPointerException("identifier can't be null");

		if (LOG.isDebugEnabled())
			LOG.debug("Removing context model object with id " +  identifier);

		IIdentity target;

		try {
			target = this.commMgr.getIdManager().fromJid(identifier.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ identifier.getOwnerId() + "':" + ife.getLocalizedMessage(), ife);
		}
		this.logRequest(requestor, target);

		// target is a CIS 
		if (IdentityType.CIS.equals(target.getType())) {
			//LOG.info("target is a CIS " +target.getJid());
			try {
				// TODO check if CIS is locally maintained or a remote call is necessary
				// TODO add access control (?)
				objectResult = this.communityCtxDBMgr.retrieve(identifier);

			} catch (Exception e) {				
				throw new CtxBrokerException(
						"Platform context broker failed to retrieve context model object with id " 
								+ identifier + ": " +  e.getLocalizedMessage(), e);
			}
			return new AsyncResult<CtxModelObject>(objectResult);

			//target is a CSS 
		} else if (IdentityType.CSS.equals(target.getType()) 
				|| IdentityType.CSS_RICH.equals(target.getType())
				|| IdentityType.CSS_LIGHT.equals(target.getType())){

			if (this.commMgr.getIdManager().isMine(target)) {

				if(!requestor.equals(this.getLocalRequestor())){

					this.ctxAccessController.checkPermission(requestor, target,
							new CtxPermission(identifier, CtxPermission.DELETE));
				}
				try {
					objectResult = this.userCtxDBMgr.remove(identifier);	

				} catch (Exception e) {
					throw new CtxBrokerException(
							"Platform context broker failed to remove context model object with id " 
									+ identifier + ": " +  e.getLocalizedMessage(), e);
				}
				return new AsyncResult<CtxModelObject>(objectResult);

			} else {

				final RemoveCtxCallback callback = new RemoveCtxCallback();
				this.ctxBrokerClient.remove(requestor, identifier, callback); 
				synchronized (callback) {
					try {
						callback.wait();
						objectResult = callback.getResult();
					} catch (InterruptedException e) {
						throw new CtxBrokerException("Interrupted while waiting for response");
					}
				}											

			}//end of remote code
		}
		LOG.info("REMOVE context data identifier: " + objectResult.getId());

		return new AsyncResult<CtxModelObject>(objectResult);
	}


	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveIndividualEntityId(
			Requestor requestor, IIdentity cssId) throws CtxException {

		if (requestor == null) requestor = this.getLocalRequestor();

		if (cssId == null)
			throw new NullPointerException("cssId can't be null");

		if (!IdentityType.CSS.equals(cssId.getType()) && 
				!IdentityType.CSS_RICH.equals(cssId.getType()) &&
				!IdentityType.CSS_LIGHT.equals(cssId.getType()))
			throw new IllegalArgumentException("cssId IdentityType is not CSS");

		CtxEntityIdentifier individualEntityIdResult = null;

		// local call
		if (this.commMgr.getIdManager().isMine(cssId)) {

			try {
				//call local method
				IndividualCtxEntity indiEnt = this.retrieveIndividualEntity(cssId).get();

				if (indiEnt != null)
					individualEntityIdResult = indiEnt.getId();
			} catch (Exception e) {

				throw new CtxBrokerException(
						"Could not retrieve IndividualCtxEntity from platform Context Broker: "
								+ e.getLocalizedMessage(), e);
			}

			// remote call
		} else {

			RetrieveIndividualEntCallback callback = new RetrieveIndividualEntCallback();
			ctxBrokerClient.retrieveIndividualEntityId(requestor, cssId, callback);
			synchronized (callback) {
				try {

					callback.wait();
					individualEntityIdResult = callback.getResult();

				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote ctxAttribute");
				}
			}

		}
		return new AsyncResult<CtxEntityIdentifier>(individualEntityIdResult);

	}

	@Override
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			Requestor requestor, IIdentity cisId) throws CtxException {

		if (requestor == null) requestor = getLocalRequestor();

		if (cisId == null)
			throw new NullPointerException("cisId can't be null");

		if (!IdentityType.CIS.equals(cisId.getType()))
			throw new IllegalArgumentException("cisId IdentityType is not CIS");

		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving the CtxEntityIdentifier for CIS " + cisId);

		CtxEntityIdentifier communityEntityId = null;

		final CommunityCtxEntity communityEntity;
		try {
			if (this.isLocalCisId(cisId)) {
				// local call
				if (LOG.isDebugEnabled())
					LOG.debug("retrieveCommunityEntityId for CIS " + cisId + " local call");
				communityEntity = this.communityCtxDBMgr.retrieveCommunityEntity(cisId.toString());
				if (communityEntity != null)
					communityEntityId = communityEntity.getId();
			} else {
				// remote call
				if (LOG.isDebugEnabled())
					LOG.debug("retrieveCommunityEntityId for CIS " + cisId + " remote call");
				final RetrieveCommunityEntityIdCallback callback = 
						new RetrieveCommunityEntityIdCallback();

				this.ctxBrokerClient.retrieveCommunityEntityId(requestor, cisId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null)
							communityEntityId = callback.getResult();
						else
							throw callback.getException();
					} catch (InterruptedException e) {

						throw new CtxBrokerException("Interrupted while waiting for response");
					}
				}
			}
		} catch (CtxException ce) {

			throw new CtxBrokerException(
					"Could not retrieve community CtxEntityIdentifier: "
							+ ce.getLocalizedMessage(), ce);
		}

		return new AsyncResult<CtxEntityIdentifier>(communityEntityId);
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(Requestor requestor,
			CtxAttributeIdentifier attrId, Date date) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(Requestor requestor,
			CtxAttributeIdentifier attrId, int modificationIndex)
					throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			Requestor requestor, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			Requestor requestor, CtxAttributeIdentifier attrId, Date startDate,
			Date endDate) throws CtxException {

		if(requestor == null) requestor = getLocalRequestor();

		final List<CtxHistoryAttribute> result = new ArrayList<CtxHistoryAttribute>();
		IIdentity identity;
		try {
			identity = this.commMgr.getIdManager().fromJid(attrId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ attrId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}

		this.logRequest(requestor, identity);


		if (IdentityType.CSS.equals(identity.getType()) 
				|| IdentityType.CSS_RICH.equals(identity.getType())
				|| IdentityType.CSS_LIGHT.equals(identity.getType())) {

			if (this.commMgr.getIdManager().isMine(identity) ) {
				//hocObj = internalCtxBroker.retrieveHistory(attrId, startDate, endDate);
				result.addAll(this.userCtxHistoryMgr.retrieveHistory(attrId, startDate, endDate));
			} else {
				LOG.info("remote call is not supported for ctx history data");
			}

		} else if (IdentityType.CIS.equals(identity.getType())){
			if (isLocalCisId(identity) ) {
				result.addAll(this.userCtxHistoryMgr.retrieveHistory(attrId, startDate, endDate));
			} else {
				LOG.info("remote call is not supported for ctx history data");
			}
		}
		return new AsyncResult<List<CtxHistoryAttribute>>(result);
	}



	@Override
	public Future<CtxEntity> retrieveAdministratingCSS(Requestor requestor,
			CtxEntityIdentifier communityEntId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<CtxBond>> retrieveBonds(Requestor requestor,
			CtxEntityIdentifier communityID) throws CtxException {

		// TODO check if idMgr.isMine(targetCis) expression is valid

		Set<CtxBond> bondsSet = null;

		IIdentity targetCis;
		try {
			targetCis = this.commMgr.getIdManager().fromJid(communityID.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID", ife);
		}
		if (this.commMgr.getIdManager().isMine(targetCis)) {

			CommunityCtxEntity commEntity;
			try {
				commEntity = (CommunityCtxEntity) this.retrieve(communityID).get();
				bondsSet = commEntity.getBonds();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} 

		return new AsyncResult<Set<CtxBond>>(bondsSet);		
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			Requestor requestor, CtxEntityIdentifier community)
					throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			Requestor requestor, CtxEntityIdentifier community)
					throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			Requestor requestor, CtxEntityIdentifier community)
					throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	public Requestor getLocalRequestor() throws CtxBrokerException {

		final INetworkNode cssNodeId = this.commMgr.getIdManager().getThisNetworkNode();
		try {
			final IIdentity cssOwnerId = this.commMgr.getIdManager().fromJid(cssNodeId.getBareJid());
			return new Requestor(cssOwnerId);
		} catch (InvalidFormatException e) {

			throw new CtxBrokerException("requestor could not be set for local network node '" 
					+ cssNodeId + "': " + e.getLocalizedMessage(), e);
		}	
	}

	private IIdentity getLocalIdentity() throws CtxBrokerException {

		IIdentity cssOwnerId = null;
		INetworkNode cssNodeId = this.commMgr.getIdManager().getThisNetworkNode();

		try {
			cssOwnerId = this.commMgr.getIdManager().fromJid(cssNodeId.getBareJid());

		} catch (InvalidFormatException e) {
			throw new CtxBrokerException(" cssOwnerId could not be set: " + e.getLocalizedMessage(), e);
		}

		return cssOwnerId;	
	}



	//control monitoring classes

	@Override
	public void disableCtxMonitoring(CtxAttributeValueType type) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableCtxMonitoring(CtxAttributeValueType type) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub

	}


	/**
	 * Sets the {@link ICtxAccessController} service reference.
	 *
	 * @param ctxAccessController
	 * the {@link ICtxAccessController} service reference to set
	 */
	public void setCtxAccessController(ICtxAccessController ctxAccessController) {

		this.ctxAccessController = ctxAccessController;
	}


	@Override
	public Future<CtxAssociation> createAssociation(IIdentity identity, String type)
			throws CtxException {

		Requestor req = null;
		return this.createAssociation(req, identity, type);
	}

	@Override
	public Future<CtxEntity> createEntity(IIdentity identity, String type)
			throws CtxException {

		Requestor req = null;

		return this.createEntity(req, identity, type);
	}

	/**
	 * added by eboylan for CSE integration test
	 */
	@Override
	public CtxEvaluationResults evaluateSimilarity(String[] ids, ArrayList<String> attrib) throws CtxException {
		LOG.info("EBOYLANLOGFOOTPRINT: ctxSimilarity broker = " + ctxSimilarityEval);
		LOG.info("EBOYLANLOGFOOTPRINT internalCtxBroker.evaluateSimilarity called");
		CtxEvaluationResults evalResult = (CtxEvaluationResults) this.ctxSimilarityEval.evaluateSimilarity(ids, attrib); //this.
		return evalResult;
	}
	
	private CtxAttribute inferUserAttribute(
			final CtxAttributeIdentifier ctxAttrId) throws CtxException {
		
		if (ctxAttrId == null)
			throw new NullPointerException("ctxAttrId can't be null");

		CtxAttribute result = (CtxAttribute) this.userCtxDBMgr.retrieve(ctxAttrId);
		// Check if inference is applicable/required:
		// 1. inferrable attribute type AND
		// 2. existing attribute either has no value OR a value of poor quality
		try {
			if (result != null 
					&& this.userCtxInferenceMgr.getInferrableTypes().contains(result.getType())
					&& (!CtxBrokerUtils.hasValue(result) || 
							this.userCtxInferenceMgr.isPoorQuality(result.getQuality()))) {
				if (LOG.isDebugEnabled())
					LOG.debug("Inferring user context attribute '" + ctxAttrId + "'");
				final CtxAttribute inferredAttribute = 
						this.userCtxInferenceMgr.refineOnDemand(ctxAttrId);
				if (LOG.isDebugEnabled())
					LOG.debug("Inferred user context attribute '" + inferredAttribute + "'");
				if (inferredAttribute != null)
					result = inferredAttribute;
			}

		} catch (ServiceUnavailableException sue) {

			throw new CtxBrokerException("Could not infer user context attribute '" + ctxAttrId 
					+ "': User Context Inference Mgr is not available");
		}
		
		return result;
	}

	private CtxAttribute inferCommunityAttribute(
			final CtxAttributeIdentifier ctxAttrId) throws CtxException {
	
		if (ctxAttrId == null)
			throw new NullPointerException("ctxAttrId can't be null");

		CtxAttribute result = (CtxAttribute) this.communityCtxDBMgr.retrieve(ctxAttrId);
		// Check if inference is applicable/required:
		// 1. inferrable attribute type
		try {
			if (this.communityCtxInferenceMgr.getInferrableTypes().contains(ctxAttrId.getType())) {
				if (LOG.isDebugEnabled())
					LOG.debug("Inferring community context attribute '" + ctxAttrId + "'");
				final CtxAttribute inferredAttribute = 
						this.communityCtxInferenceMgr.estimateCommunityContext(ctxAttrId.getScope(), ctxAttrId);
				if (LOG.isDebugEnabled())
					LOG.debug("Inferred community context attribute '" + inferredAttribute + "'");
				if (inferredAttribute != null && CtxBrokerUtils.hasValue(inferredAttribute)) {
					// TODO The Community Inference Mgr should persist the estimated attribute in the Community DB - *not* the Context Broker
					try {
						result = (CtxAttribute) this.update(inferredAttribute).get();
					} catch (InterruptedException ie) {
						throw new CtxBrokerException("Interrupted while updating inferred community context attribute '"
								+ inferredAttribute.getId() + "': "
								+ ie.getLocalizedMessage(), ie);
					} catch (ExecutionException ee) {
						throw new CtxBrokerException("Could not update inferred community context attribute '"
								+ inferredAttribute.getId() + "': "
								+ ee.getLocalizedMessage(), ee);
					}
				}
			}
			
		} catch (ServiceUnavailableException sue) {

			throw new CtxBrokerException("Could not infer community context attribute '" + ctxAttrId 
					+ "': Community Context Inference Mgr is not available");
		}
		
		return result;
	}
	
	private boolean isLocalId(final IIdentity id) {

		if (id == null)
			throw new NullPointerException("id can't be null");
		if (!IdentityType.CIS.equals(id.getType())) // U S E R  I D
			return this.isLocalCssId(id);
		else                                        // C O M M U N I T Y  I D
			return this.isLocalCisId(id);
	}

	private boolean isLocalCssId(final IIdentity cssId) {

		if (cssId == null)
			throw new NullPointerException("cssId can't be null");
		if (IdentityType.CIS.equals(cssId.getType()))
			throw new IllegalArgumentException("cssId IdentityType is not CSS");

		return this.commMgr.getIdManager().isMine(cssId);
	}
	
	private boolean isLocalCisId(final IIdentity cisId) {

		if (cisId == null)
			throw new NullPointerException("cisId can't be null");
		if (!IdentityType.CIS.equals(cisId.getType()))
			throw new IllegalArgumentException("cisId IdentityType is not CIS");

		return (this.commMgrFactory.getAllCISCommMgrs().get(cisId) != null);
	}
	
	private IIdentity extractIIdentity(CtxIdentifier ctxId) throws CtxBrokerException {
		
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");
		
		final IIdentity target;
		try {
			target = this.commMgr.getIdManager().fromJid(ctxId.getOwnerId());
		} catch (InvalidFormatException ife) {
			throw new CtxBrokerException("Could not create IIdentity from JID '"
					+ ctxId.getOwnerId() + "': " + ife.getLocalizedMessage(), ife);
		}
		
		return target;
	}
	
	private IIdentity extractIIdentity(CtxModelObject ctxModelObject) throws CtxBrokerException {
		
		if (ctxModelObject == null)
			throw new NullPointerException("ctxModelObject can't be null");
		
		return this.extractIIdentity(ctxModelObject.getId());
	}
}