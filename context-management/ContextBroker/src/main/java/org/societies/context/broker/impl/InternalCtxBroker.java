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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.CtxAccessControlException;
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
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.context.api.community.db.ICommunityCtxDBMgr;
import org.societies.context.api.community.inference.ICommunityCtxInferenceMgr;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.api.similarity.ICtxSimilarityEvaluator;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;
import org.societies.context.api.user.inference.IUserCtxInferenceMgr;
import org.societies.context.broker.api.CtxBrokerException;
import org.societies.context.broker.api.security.ICtxAccessController;
import org.societies.context.broker.impl.comm.CtxBrokerClient;
import org.societies.context.broker.impl.comm.callbacks.CreateAssociationCallback;
import org.societies.context.broker.impl.comm.callbacks.CreateAttributeCallback;
import org.societies.context.broker.impl.comm.callbacks.CreateEntityCallback;
import org.societies.context.broker.impl.comm.callbacks.LookupCallback;
import org.societies.context.broker.impl.comm.callbacks.RemoveCtxCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveAllCtxCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveCommunityEntityIdCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveCtxCallback;
import org.societies.context.broker.impl.comm.callbacks.RetrieveFutureCtxCallback;
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

	/**
	 * The Context Similarity Evaluator service reference.
	 *
	 * @see {@link #setCtxSimilarityEvaluator(ICtxSimilarityEvaluator)}
	 */
	@Autowired(required=true)
	private ICtxSimilarityEvaluator ctxSimilarityEval;

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

		LOG.info("{} instantiated", this.getClass());
	}

	/*
	 * Used for JUnit testing only.
	 */
	public InternalCtxBroker() {

		LOG.info("{} instantiated", this.getClass());
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createEntity(java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxEntity> createEntity(final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		final IIdentity target = this.getLocalIdentity();
		return this.createEntity(requestor, target, type);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createEntity(org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxEntity> createEntity(final IIdentity target, 
			final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.createEntity(requestor, target, type);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#createEntity(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxEntity> createEntity(final Requestor requestor,
			final IIdentity target, final String type) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (target == null) {
			throw new NullPointerException("target can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		LOG.debug("createEntity: requestor={}, target={}, type={}",
				new Object[] { requestor, target, type });

		CtxEntity result = null;

		if (this.isLocalId(target)) { // L O C A L

			if (IdentityType.CIS != target.getType()) { // U S E R
				// TODO Add target parameter to UserCtxDBMgr interface
				result = this.userCtxDBMgr.createEntity(type);

			} else { // C O M M U N I T Y

				result = this.communityCtxDBMgr.createEntity(target.getBareJid(), type);
			}

		} else { // R E M O T E

			final CreateEntityCallback callback = new CreateEntityCallback();
			this.ctxBrokerClient.createEntity(requestor, target, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result = callback.getResult();
					} else {
						throw callback.getException();
					}

				} catch (InterruptedException ie) {

					throw new CtxBrokerException("Interrupted while waiting for remote createEntity: "
							+ ie.getLocalizedMessage(), ie);
				}
			}
		}

		LOG.debug("createEntity: result={}", result);
		return new AsyncResult<CtxEntity>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createIndividualEntity(org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	@Async
	public Future<IndividualCtxEntity> createIndividualEntity(
			final IIdentity cssId, final String ownerType) throws CtxException {

		if (cssId == null) {
			throw new NullPointerException("cssId can't be null");
		}
		if (ownerType == null) {
			throw new NullPointerException("ownerType can't be null");
		}

		LOG.debug("createIndividualEntity: cssId={}, ownerType={}", cssId, ownerType);

		IndividualCtxEntity result = null;

		try {
			LOG.info("Creating event topics '{}' for CSS owner {}", 
					Arrays.toString(EVENT_TOPICS), cssId);
			this.ctxEventMgr.createTopics(cssId, EVENT_TOPICS);

			LOG.info("Checking if CSS owner context entity for {} exists...", cssId);
			result = this.retrieveIndividualEntity(cssId).get();
			if (result != null) {
				LOG.info("Found CSS owner context entity {}", result);
			} else {
				result = this.userCtxDBMgr.createIndividualEntity(
						cssId.getBareJid(), ownerType); 

				final CtxAttribute cssIdAttr = this.userCtxDBMgr.createAttribute(
						result.getId(), CtxAttributeTypes.ID); 
				this.updateAttribute(cssIdAttr.getId(), cssId.toString());
				LOG.info("Created CSS owner context entity {}" + result);
			}
		} catch (Exception e) {
			throw new CtxBrokerException("Could not create CSS owner context entity " + cssId
					+ ": " + e.getLocalizedMessage(), e);
		}

		LOG.debug("createIndividualEntity: result={}", result);
		return new AsyncResult<IndividualCtxEntity>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createCommunityEntity(org.societies.api.identity.IIdentity)
	 */
	@Override
	@Async
	public Future<CommunityCtxEntity> createCommunityEntity(IIdentity cisId)
			throws CtxException {

		if (cisId == null) {
			throw new NullPointerException("cisId can't be null");
		}
		if (!IdentityType.CIS.equals(cisId.getType())) {
			throw new IllegalArgumentException("cisId is not of type CIS");
		}

		LOG.debug("createCommunityEntity: cisId={}", cisId);

		LOG.info("Creating event topics '{}' for CIS {}", Arrays.toString(EVENT_TOPICS), cisId);
		this.ctxEventMgr.createTopics(cisId, EVENT_TOPICS);

		final CommunityCtxEntity result = 
				this.communityCtxDBMgr.createCommunityEntity(cisId.toString());

		LOG.debug("createCommunityEntity: result={}", result);
		return new AsyncResult<CommunityCtxEntity>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createCssNode(org.societies.api.identity.INetworkNode)
	 */
	@Override
	@Async
	public Future<CtxEntity> createCssNode(final INetworkNode cssNodeId)
			throws CtxException {

		if (cssNodeId == null) {
			throw new NullPointerException("cssNodeId can't be null");
		}

		CtxEntity result = null;
		try {
			LOG.info("Checking if CSS node context entity {} exists...", cssNodeId);
			result = this.retrieveCssNode(cssNodeId).get();
			if (result != null) {
				LOG.info("Found CSS node context entity {}", result);
			} else {
				final IIdentity cssId = this.commMgr.getIdManager().fromJid(
						cssNodeId.getBareJid().replace('@', '.')); // Android JIDs contain '@' instead of '.'
				final IndividualCtxEntity cssEnt = this.retrieveIndividualEntity(cssId).get();
				if (cssEnt == null) {
					throw new CtxBrokerException("The IndividualCtxEntity for CSS '" 
							+ cssId + "' could not be found. Does node " + cssNodeId
							+ " belong to a local CSS?");
				}
				final CtxAssociation ownsCssNodesAssoc;
				if (cssEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).isEmpty()) {
					ownsCssNodesAssoc = this.userCtxDBMgr.createAssociation(
							CtxAssociationTypes.OWNS_CSS_NODES);
				} else {
					ownsCssNodesAssoc = (CtxAssociation) this.userCtxDBMgr.retrieve(
							cssEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).iterator().next());
				}
				ownsCssNodesAssoc.setParentEntity(cssEnt.getId());
				result = this.userCtxDBMgr.createEntity(CtxEntityTypes.CSS_NODE);
				ownsCssNodesAssoc.addChildEntity(result.getId());
				this.userCtxDBMgr.update(ownsCssNodesAssoc);
				final CtxAttribute cssNodeIdAttr = this.userCtxDBMgr.createAttribute(
						result.getId(), CtxAttributeTypes.ID);
				cssNodeIdAttr.setStringValue(cssNodeId.toString());
				this.userCtxDBMgr.update(cssNodeIdAttr);
				LOG.info("Created CSS node context entity {}", result);
			}

		} catch (Exception e) {
			throw new CtxBrokerException("Could not create CSS node context entity " + cssNodeId
					+ ": " + e.getLocalizedMessage(), e);
		}

		return new AsyncResult<CtxEntity>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAttribute> createAttribute(final CtxEntityIdentifier scope,
			final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.createAttribute(requestor, scope, type);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#createAttribute(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAttribute> createAttribute(final Requestor requestor,
			final CtxEntityIdentifier scope, final String type) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		LOG.debug("createAttribute: requestor={}, scope={}, type={}",
				new Object[] { requestor, scope, type });

		CtxAttribute result = null;

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(scope);

		if (this.isLocalId(target)) { // L O C A L

			if (IdentityType.CIS != target.getType()) { // U S E R

				result = this.userCtxDBMgr.createAttribute(scope, type);

			} else { // C O M M U N I T Y

				result = this.communityCtxDBMgr.createAttribute(scope, type);
			}

		} else { // R E M O T E

			final CreateAttributeCallback callback = new CreateAttributeCallback();
			this.ctxBrokerClient.createAttribute(requestor, target, scope, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result = callback.getResult();
					} else {
						throw callback.getException();
					}
				} catch (InterruptedException ie) {
					throw new CtxBrokerException("Interrupted while waiting for remote createAttribute: "
							+ ie.getLocalizedMessage(), ie);
				}
			}
		}

		LOG.debug("createAttribute: result={}", result);
		return new AsyncResult<CtxAttribute>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createAssociation(java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAssociation> createAssociation(final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		final IIdentity target = this.getLocalIdentity();
		return this.createAssociation(requestor, target, type);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createAssociation(org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAssociation> createAssociation(final IIdentity target,
			final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.createAssociation(requestor, target, type);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#createAssociation(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAssociation> createAssociation(final Requestor requestor,
			final IIdentity target, final String type) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (target == null) {
			throw new NullPointerException("target can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		LOG.debug("createAssociation: requestor={}, target={}, type={}",
				new Object[] { requestor, target, type });

		CtxAssociation result = null;

		if (this.isLocalId(target)) { // L O C A L

			if (IdentityType.CIS != target.getType()) { // U S E R
				// TODO Add target parameter to UserCtxDBMgr interface
				result = this.userCtxDBMgr.createAssociation(type);

			} else { // C O M M U N I T Y

				result = this.communityCtxDBMgr.createAssociation(target.getBareJid(), type);
			}

		} else { // R E M O T E

			final CreateAssociationCallback callback = new CreateAssociationCallback();
			this.ctxBrokerClient.createAssociation(requestor, target, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result = callback.getResult();
					} else {
						throw callback.getException();
					}
				} catch (InterruptedException ie) {
					throw new CtxBrokerException("Interrupted while waiting for remote createAssociation: "
							+ ie.getLocalizedMessage(), ie);
				}
			}
		}

		LOG.debug("createAssociation: result={}", result);
		return new AsyncResult<CtxAssociation>(result);	
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

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (target == null) {
			throw new NullPointerException("target can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		LOG.debug("lookup: requestor={}, target={}, type={}",
				new Object[] { requestor, target, type });

		final List<CtxIdentifier> result = new ArrayList<CtxIdentifier>();

		if (this.isLocalId(target)) { 
			// L O C A L
			// Retrieve sub-types
			final Set<String> subTypes = new DataTypeUtils().getLookableDataTypes(type);
			LOG.debug("lookup: type={}, subTypes={}", type, subTypes);
			if (IdentityType.CIS != target.getType()) { 
				// U S E R
				result.addAll(this.userCtxDBMgr.lookup(target.getBareJid(), subTypes));
			} else { 
				// C O M M U N I T Y
				result.addAll(this.communityCtxDBMgr.lookup(target.getBareJid(), subTypes));
			}
		} else { 
			// R E M O T E
			final LookupCallback callback = new LookupCallback();
			this.ctxBrokerClient.lookup(requestor, target, null, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result.addAll(callback.getResult());
					} else {
						throw callback.getException();
					}
				} catch (InterruptedException e) {
					throw new CtxBrokerException("Interrupted while waiting for remote lookup response");
				}
			}
		}

		LOG.debug("lookup: result={}", result);
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

		if (requestor == null) { 
			throw new NullPointerException("requestor can't be null");
		}
		if (target == null) {
			throw new NullPointerException("target can't be null");
		}
		if (modelType == null) {
			throw new NullPointerException("modelType can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		LOG.debug("lookup: requestor={}, target={}, modelType={}, type={}", 
				new Object[] { requestor, target, modelType, type });

		final List<CtxIdentifier> result = new ArrayList<CtxIdentifier>();

		if (this.isLocalId(target)) {
			final Set<String> types = new HashSet<String>();
			types.add(type);
			// L O C A L
			if (IdentityType.CIS != target.getType()) { 
				// U S E R
				result.addAll(this.userCtxDBMgr.lookup(target.getBareJid(), modelType, types));
			} else { 
				// C O M M U N I T Y
				result.addAll(this.communityCtxDBMgr.lookup(target.getBareJid(), modelType, types));
			}
		} else { 
			// R E M O T E
			final LookupCallback callback = new LookupCallback();
			this.ctxBrokerClient.lookup(requestor, target, modelType, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result.addAll(callback.getResult());
					} else {
						throw callback.getException();
					}
				} catch (InterruptedException e) {
					throw new CtxBrokerException("Interrupted while waiting for remote lookup");
				}
			}
		}

		LOG.debug("lookup: result={}", result);
		return new AsyncResult<List<CtxIdentifier>>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#lookup(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final CtxEntityIdentifier scope, 
			final CtxModelType modelType, final String type) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.lookup(requestor, scope, modelType, type);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#lookup(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxModelType, java.lang.String)
	 */
	@Override
	@Async
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor,
			final CtxEntityIdentifier scope, final CtxModelType modelType, 
			final String type) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}
		if (modelType == null) {
			throw new NullPointerException("modelType can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		if (!CtxModelType.ATTRIBUTE.equals(modelType) && !CtxModelType.ASSOCIATION.equals(modelType)) {
			throw new IllegalArgumentException("modelType is not ATTRIBUTE or ASSOCIATION");
		}

		LOG.debug("lookup: requestor={}, scope={}, modelType={}, type={}",
				new Object[] { requestor, scope, modelType, type });

		final List<CtxIdentifier> result = new ArrayList<CtxIdentifier>();

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(scope);

		if (this.isLocalId(target)) {
			final Set<String> types = new HashSet<String>();
			types.add(type);
			// L O C A L
			if (IdentityType.CIS != target.getType()) { 
				// U S E R
				result.addAll(this.userCtxDBMgr.lookup(scope, modelType, types));
			} else { 
				// C O M M U N I T Y
				result.addAll(this.communityCtxDBMgr.lookup(scope, modelType, types));
			}
		} else { 
			// R E M O T E
			final LookupCallback callback = new LookupCallback();
			this.ctxBrokerClient.lookup(requestor, scope, modelType, type, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result.addAll(callback.getResult());
					} else {
						throw callback.getException();
					}
				} catch (InterruptedException e) {
					throw new CtxBrokerException("Interrupted while waiting for remote lookup");
				}
			}
		}

		LOG.debug("lookup: result={}", result);
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

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieveIndividualEntity(org.societies.api.identity.IIdentity)
	 */
	@Override
	@Async
	public Future<IndividualCtxEntity> retrieveIndividualEntity(
			final IIdentity cssId) throws CtxException {

		if (cssId == null) {
			throw new NullPointerException("cssId can't be null");
		}

		LOG.debug("retrieveIndividualEntity: cssId={}", cssId);

		this.logRequest(null, cssId);

		final IndividualCtxEntity result = 
				this.userCtxDBMgr.retrieveIndividualEntity(cssId.getBareJid());

		LOG.debug("retrieveIndividualEntity: result={}", result);
		return new AsyncResult<IndividualCtxEntity>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieveCssOperator()
	 */
	@Override
	@Async
	@Deprecated
	public Future<IndividualCtxEntity> retrieveCssOperator()
			throws CtxException {

		LOG.debug("retrieveCssOperator");

		final IIdentity localCssId = this.getLocalIdentity();
		return this.retrieveIndividualEntity(localCssId);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieveCssNode(org.societies.api.identity.INetworkNode)
	 */
	@Override
	@Async
	public Future<CtxEntity> retrieveCssNode(final INetworkNode cssNodeId) 
			throws CtxException {

		if (cssNodeId == null) {
			throw new NullPointerException("cssNodeId can't be null");
		}

		LOG.debug("retrieveCssNode: cssNodeId={}", cssNodeId);

		CtxEntity result = null;
		final List<CtxEntityIdentifier> entIds = this.userCtxDBMgr.lookupEntities(
				CtxEntityTypes.CSS_NODE, CtxAttributeTypes.ID, cssNodeId.toString(), cssNodeId.toString());
		if (!entIds.isEmpty()) {
			try {
				result = (CtxEntity) this.retrieve(entIds.get(0)).get();
			} catch (Exception e) {
				throw new CtxBrokerException("Failed to retrieve CSS node context entity " + cssNodeId
						+ ": " + e.getLocalizedMessage(), e);
			}
		}

		LOG.debug("retrieveCssNode: result={}", result);
		return new AsyncResult<CtxEntity>(result);
	}

	private void storeHoc(CtxModelObject ctxModelObj) throws CtxException {

		// ********************** HISTORY CODE ******************************* 

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

		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (ownerId == null) {
			throw new NullPointerException("ownerId can't be null");
		}

		LOG.debug("registerForChanges: listener={}, ownerId={}", listener, ownerId);

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			LOG.info("Registering context change event listener '{}' for IIdentity '{}'"
					+ " to topics '{}'", new Object[] { listener, ownerId,
							Arrays.toString(topics) });
			this.ctxEventMgr.registerChangeListener(listener, topics, ownerId);
		} else {
			throw new CtxBrokerException("ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.identity.IIdentity)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final IIdentity ownerId) throws CtxException {

		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (ownerId == null) {
			throw new NullPointerException("ownerId can't be null");
		}

		LOG.debug("unregisterFromChanges: listener={}, ownerId={}", listener, ownerId);

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			LOG.info("Unregistering context change event listener '{}' for IIdentity '{}'"
					+ " from topics '{}'", new Object[] { listener, ownerId,
							Arrays.toString(topics) });
			this.ctxEventMgr.unregisterChangeListener(listener, topics, ownerId);
		} else {
			throw new CtxBrokerException("ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerForChanges(final CtxChangeEventListener listener,
			final CtxIdentifier ctxId) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		this.registerForChanges(requestor, listener, ctxId);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final CtxIdentifier ctxId) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		this.unregisterFromChanges(requestor, listener, ctxId);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#registerForChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerForChanges(final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, final String attrType)
					throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		this.registerForChanges(requestor, listener, scope, attrType);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, final String attrType) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		this.unregisterFromChanges(requestor, listener, scope, attrType);
	}


	//***********************************************
	//     Context Inference Methods  
	//***********************************************

	@Override
	@Async
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, Date date) throws CtxException {

	LOG.debug("retrieveFuture internal method ");
		final Requestor requestor = this.getLocalRequestor();
		return this.retrieveFuture(requestor, attrId,date );
	}


	@Override
	@Async
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.retrieveFuture(requestor, attrId,modificationIndex );
	}

	//***********************************************
	//     Community Context Management Methods  
	//***********************************************

	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			final IIdentity cisId) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.retrieveCommunityEntityId(requestor, cisId);
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

		final Requestor requestor = this.getLocalRequestor();
		return this.retrieveHistory(requestor, attrId, startDate, endDate);
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
			//ls = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			ls = this.lookup(primaryAttrIdentifier.getScope(), CtxModelType.ATTRIBUTE, tupleAttrType).get();

			if (ls.size() > 0) {
				CtxIdentifier id = ls.get(0);
				final CtxAttribute tupleIdsAttribute = (CtxAttribute) this.userCtxDBMgr.retrieve(id);

				//deserialise object
				tupleAttrIDs = (List<CtxAttributeIdentifier>) SerialisationHelper.deserialise(tupleIdsAttribute.getBinaryValue(), this.getClass().getClassLoader());
			}

		} catch (Exception e) {
			LOG.error("Exception when trying to get the history tuples identifiers for attrID: "+primaryAttrIdentifier+". "+e.getLocalizedMessage());
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
			//ls = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			ls = this.lookup(primaryAttrIdentifier.getScope(), CtxModelType.ATTRIBUTE, tupleAttrType).get();
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

		} catch (Exception e) {
			LOG.error("Exception when trying to updateHistoryTuples for attrID: "+primaryAttrIdentifier+". "+e.getLocalizedMessage());
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
			IIdentity localID = this.getLocalIdentity();

			//List<CtxIdentifier> ctxAttrListIds = this.lookup(CtxModelType.ATTRIBUTE, attributeType).get();
			List<CtxIdentifier> ctxAttrListIds = this.lookup(localID, CtxModelType.ATTRIBUTE, attributeType).get();
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


		} catch (Exception e) {
			LOG.error("Exception when trying to retrieve history tuples for attribute Type: "+attributeType+". "+e.getLocalizedMessage());
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

		if (primaryAttrId == null) {
			throw new NullPointerException("primary CtxAttribute Id can't be null");
		}

		if(primaryAttrId!= null){ 

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
				listIds = this.lookup(primaryAttrId.getScope(), CtxModelType.ATTRIBUTE,tupleAttrType).get();
				//listIds = this.lookup(CtxModelType.ATTRIBUTE,tupleAttrType).get();
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
			} catch (Exception e) {
				LOG.error("Exception when trying to retrieve history tuples for attribute id: "+primaryAttrId+". "+e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		//if(results == null){
		//	results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		//}
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

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (ctxId == null) {
			throw new NullPointerException("ctxId can't be null");
		}

		LOG.debug("retrieve: requestor={}, ctxId={}", requestor, ctxId);

		CtxModelObject result = null;

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(ctxId);

		// Log with Privacy Log Appender
		this.logRequest(requestor, target);

		if (this.isLocalId(target)) { // L O C A L

			// Check if access control is required.
			if(!requestor.equals(this.getLocalRequestor())) {
				// Check READ permission
				this.ctxAccessController.checkPermission(requestor, ctxId, 
						ActionConstants.READ);
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

			// Obfuscate non-null result if requestor is not local
			if (result != null && !requestor.equals(this.getLocalRequestor())) {
				result = this.ctxAccessController.obfuscate(requestor, result);
			}

		} else { // R E M O T E

			// Needed for performance test
			long initTimestamp = System.nanoTime();

			final RetrieveCtxCallback callback = new RetrieveCtxCallback();
			this.ctxBrokerClient.retrieve(requestor, ctxId, callback); 
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result = callback.getResult();
					} else { 
						throw callback.getException();
					}

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

		LOG.debug("retrieve: result={}", result);
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

		if (ctxAttrId == null) {
			throw new NullPointerException("ctxAttrId can't be null");
		}

		LOG.debug("retrieveAttribute: ctxAttrId={}, enableInference={}",
				ctxAttrId, enableInference);

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

		LOG.debug("retrieveAttribute: result={}", result);
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
	public Future<List<CtxModelObject>> retrieve(final Requestor requestor,
			final List<CtxIdentifier> ctxIdList) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (ctxIdList == null) {
			throw new NullPointerException("ctxIdList can't be null");
		}

		LOG.debug("retrieve: requestor={}, ctxIdList={}", requestor, ctxIdList);

		final List<CtxModelObject> result = new ArrayList<CtxModelObject>(
				ctxIdList.size());

		// Indicates whether a CtxAccessControlException has been thrown
		boolean isAccessControlExceptionThrown = false;
		// The local user context IDs specified in the request
		final List<CtxIdentifier> localUserCtxIdList = new ArrayList<CtxIdentifier>(ctxIdList.size());
		// The local community context IDs specified in the request
		final List<CtxIdentifier> localCommunityCtxIdList = new ArrayList<CtxIdentifier>(ctxIdList.size());
		// The remote context IDs specified in the request
		final List<CtxIdentifier> remoteCtxIdList = new ArrayList<CtxIdentifier>(ctxIdList.size());

		for (final CtxIdentifier ctxId : ctxIdList) {
			final IIdentity target = this.extractIIdentity(ctxId);
			if (this.isLocalId(target)) {
				// L O C A L
				if (IdentityType.CIS != target.getType()) { 
					// U S E R
					// Collect all local user context IDs to be retrieved
					localUserCtxIdList.add(ctxId);
				} else { 
					// C O M M U N I T Y
					// Collect all local community context IDs to be retrieved
					localCommunityCtxIdList.add(ctxId);
				}
			} else { 
				// R E M O T E
				// Collect all remote context IDs to be retrieved
				remoteCtxIdList.add(ctxId);
			}
		}

		if (!localUserCtxIdList.isEmpty()) { 
			// L O C A L  U S E R
			// Check if access control is required.
			if(!requestor.equals(this.getLocalRequestor())) {
				// Check READ permission
				List<CtxIdentifier> allowedUserCtxIdList = new ArrayList<CtxIdentifier>();
				try {
					allowedUserCtxIdList = this.ctxAccessController.checkPermission(
							requestor, localUserCtxIdList, ActionConstants.READ);
				} catch (CtxAccessControlException cace) {
					// Flag that a CtxAccessControlException has been thrown
					isAccessControlExceptionThrown = true;
				}
				// Retain only allowed user context IDs 
				localUserCtxIdList.retainAll(allowedUserCtxIdList);
			}

			for (final CtxIdentifier localUserCtxId : localUserCtxIdList) {
				final CtxModelObject ctxModelObject = this.userCtxDBMgr.retrieve(localUserCtxId); 
				if (ctxModelObject != null) {
					result.add(ctxModelObject);
				}
			}
		}

		if (!localCommunityCtxIdList.isEmpty()) { 
			// L O C A L  C O M M U N I T Y
			// Check if access control is required.
			if(!requestor.equals(this.getLocalRequestor())) {
				// Check READ permission
				List<CtxIdentifier> allowedCommunityCtxIdList = new ArrayList<CtxIdentifier>();
				try {
					allowedCommunityCtxIdList = this.ctxAccessController.checkPermission(
							requestor, localCommunityCtxIdList, ActionConstants.READ);
				} catch (CtxAccessControlException cace) {
					// Flag that a CtxAccessControlException has been thrown
					isAccessControlExceptionThrown = true;
				}
				// Retain only allowed community context IDs 
				localCommunityCtxIdList.retainAll(allowedCommunityCtxIdList);
			}

			for (final CtxIdentifier localCommunityCtxId : localCommunityCtxIdList) {
				final CtxModelObject ctxModelObject = this.communityCtxDBMgr.retrieve(localCommunityCtxId); 
				if (ctxModelObject != null) {
					result.add(ctxModelObject);
				}
			}
		}

		// Obfuscate non-empty result if requestor is not local
		if(!result.isEmpty() && !requestor.equals(this.getLocalRequestor())) {
			result.retainAll(this.ctxAccessController.obfuscate(requestor, result));
		}

		if (!remoteCtxIdList.isEmpty()) { // R E M O T E

			// Create map to group context identifiers per remote target CSS/CIS
			final Map<IIdentity, List<CtxIdentifier>> targetMap = 
					new HashMap<IIdentity, List<CtxIdentifier>>();
			for (final CtxIdentifier remoteCtxId : remoteCtxIdList) {
				final IIdentity target = this.extractIIdentity(remoteCtxId);
				if (targetMap.get(target) == null) {
					targetMap.put(target, new ArrayList<CtxIdentifier>());
				}
				targetMap.get(target).add(remoteCtxId);
			}
			LOG.debug("retrieve: targetMap={}", targetMap);
			
			// Perform remote retrieve operation(s) asynchronously
			final List<RetrieveAllCtxCallback> callbacks = 
					new ArrayList<RetrieveAllCtxCallback>(targetMap.size());
			// Init countdown to the number of remote CSSs/CISs 
			final CountDownLatch doneSignal = new CountDownLatch(targetMap.size());
			for (final Map.Entry<IIdentity, List<CtxIdentifier>> targetEntry : targetMap.entrySet()) {
				final RetrieveAllCtxCallback callback = new RetrieveAllCtxCallback(doneSignal);
				this.ctxBrokerClient.retrieve(requestor, targetEntry.getKey(),
						targetEntry.getValue(), callback);
				callbacks.add(callback);
			}
			try {
				doneSignal.await();
			} catch (InterruptedException ie) {
				throw new CtxBrokerException("Interrupted while waiting for remote context model objects '"
						+ remoteCtxIdList + "'");
			}
			for (final RetrieveAllCtxCallback callback : callbacks) {
				if (callback.getException() == null) {
					result.addAll(callback.getResult());
				} else {
					if (callback.getException() instanceof CtxAccessControlException) {
						isAccessControlExceptionThrown = true;
					} else {
						throw callback.getException();
					}
				}
			}
		}

		if (isAccessControlExceptionThrown && result.isEmpty()) {
			throw new CtxAccessControlException("'" + ActionConstants.READ.name()
					+ "' access to '" + ctxIdList + "' denied for requestor '"
					+ requestor + "'");
		}

		LOG.debug("retrieve: result={}", result);
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

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (ctxModelObject == null) { 
			throw new NullPointerException("ctxModelObject can't be null");
		}

		LOG.debug("update: requestor={}, ctxModelObject={}", requestor, ctxModelObject);

		CtxModelObject result = null; 

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(ctxModelObject);

		if (this.isLocalId(target)) { // L O C A L

			// Check if access control is required
			if (!requestor.equals(this.getLocalRequestor())) {
				// Check WRITE permission
				this.ctxAccessController.checkPermission(requestor, 
						ctxModelObject.getId(), ActionConstants.WRITE);
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
					if (callback.getException() == null) {
						result = callback.getResult();
					} else {
						throw callback.getException();
					}
				} catch (InterruptedException e) {
					throw new CtxBrokerException("Interrupted while waiting for remote update");
				}
			}
		}

		LOG.debug("update: result={}", result);
		return new AsyncResult<CtxModelObject>(result);
	}

	/*
	 * @see org.societies.api.internal.context.broker.ICtxBroker#remove(org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	@Async
	public Future<CtxModelObject> remove(final CtxIdentifier ctxId) throws CtxException {

		final Requestor requestor = this.getLocalRequestor();
		return this.remove(requestor, ctxId);
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#remove(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	@Async
	public Future<CtxModelObject> remove(final Requestor requestor,
			final CtxIdentifier ctxId) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (ctxId == null) {
			throw new NullPointerException("ctxId can't be null");
		}

		LOG.debug("remove: reqeustor={}, ctxId={}", requestor, ctxId);

		CtxModelObject result = null;

		// Extract target IIdentity
		final IIdentity target = this.extractIIdentity(ctxId);

		// Log with Privacy Log Appender
		this.logRequest(requestor, target);

		if (this.isLocalId(target)) { // L O C A L

			// Check if access control is required
			if(!requestor.equals(this.getLocalRequestor())) {
				// Check DELETE permission
				this.ctxAccessController.checkPermission(requestor, ctxId, 
						ActionConstants.DELETE);
			}
			// No CtxAccessControlException thrown implies DELETE access has been granted

			if (IdentityType.CIS != target.getType()) { // U S E R
				result = this.userCtxDBMgr.remove(ctxId);
			} else { // C O M M U N I T Y
				result = this.communityCtxDBMgr.remove(ctxId);
			}

		} else { // R E M O T E

			final RemoveCtxCallback callback = new RemoveCtxCallback();
			this.ctxBrokerClient.remove(requestor, ctxId, callback); 
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result = callback.getResult();
					} else { 
						throw callback.getException();
					}
				} catch (InterruptedException e) {
					throw new CtxBrokerException("Interrupted while waiting for remote remove");
				}
			}	
		}

		LOG.debug("remove: result={}", result);
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
	public void registerForChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (ctxId == null) {
			throw new NullPointerException("ctxId can't be null");
		}

		LOG.debug("registerForChanges: requestor={}, listener={}, ctxId={}",
				new Object[] { requestor, listener, ctxId });

		final String[] topics = new String[] {
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			LOG.info("Registering context change event listener '{}'"
					+ " for object '{}' to topics '{}'", new Object[] { 
							listener, ctxId, Arrays.toString(topics) });
			this.ctxEventMgr.registerChangeListener(listener, topics, ctxId);

			try {
				if (ctxId instanceof CtxAttributeIdentifier 
						&& this.userCtxInferenceMgr.getInferrableTypes().contains(ctxId.getType())) {
					LOG.info("Triggering continuous inference of attribute '{}'", ctxId);
					this.userCtxInferenceMgr.refineContinuously(
							(CtxAttributeIdentifier) ctxId, new Double(0)); // TODO handle updateFreq
				}
			} catch (ServiceUnavailableException sue) {

				LOG.warn("Could not check if attribute requires inference: "
						+ "User Context Inference Mgr is not available");
			}
		} else {
			throw new CtxBrokerException("ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.Requestor, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterFromChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (ctxId == null) {
			throw new NullPointerException("ctxId can't be null");
		}

		LOG.debug("unregisterFromChanges: requestor={}, listener={}, ctxId={}",
				new Object[] { requestor, listener, ctxId });

		final String[] topics = new String[] {
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			LOG.info("Unregistering context change event listener '{}'"
					+ " for object '{}' from topics '{}'", new Object[] { 
							listener, ctxId, Arrays.toString(topics) });
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
			throw new CtxBrokerException("ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.Requestor, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerForChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}

		LOG.debug("registerForChanges: requestor={}, listener={}, scope={}, attrType={}",
				new Object[] { requestor, listener, scope, attrType });

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			LOG.info("Registering context change event listener '{}' for attributes with scope '{}'"
					+ " and type '{}' to topics '{}'", new Object[] { listener,
							scope, attrType, Arrays.toString(topics) });
			this.ctxEventMgr.registerChangeListener(listener, topics, scope, attrType );
		} else {
			throw new CtxBrokerException("ICtxEventMgr service is not available");
		}
	}

	/*
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.Requestor, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterFromChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (listener == null) {
			throw new NullPointerException("listener can't be null");
		}
		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}

		LOG.debug("unregisterFromChanges: requestor={}, listener={}, scope={}, attrType={}",
				new Object[] { requestor, listener, scope, attrType });

		final String[] topics = new String[] {
				CtxChangeEventTopic.CREATED,
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			LOG.info("Unregistering context change event listener '{}' for attributes with scope '{}'"
					+ " and type '{}' from topics '{}'", new Object[] { listener,
							scope, attrType, Arrays.toString(topics) });
			this.ctxEventMgr.unregisterChangeListener(listener, topics, scope, attrType );
		} else {
			throw new CtxBrokerException("ICtxEventMgr service is not available");
		}
	}

	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveIndividualEntityId(
			Requestor requestor, final IIdentity cssId) throws CtxException {

		if (requestor == null) {
			// TODO throw new NullPointerException("requestor can't be null");
			requestor = this.getLocalRequestor();
		}
		if (cssId == null) {
			throw new NullPointerException("cssId can't be null");
		}

		LOG.debug("retrieveIndividualEntityId: requestor={}. cssId={}", requestor, cssId);

		CtxEntityIdentifier result = null;

		if (this.isLocalCssId(cssId)) {
			// L O C A L
			try {
				final IndividualCtxEntity indiEnt = this.retrieveIndividualEntity(cssId).get();
				if (indiEnt != null) {
					result = indiEnt.getId();
				}
			} catch (Exception e) {
				throw new CtxBrokerException(
						"Could not retrieve IndividualCtxEntity from platform Context Broker: "
								+ e.getLocalizedMessage(), e);
			}
		} else {
			// R E M O T E
			final RetrieveIndividualEntCallback callback = new RetrieveIndividualEntCallback();
			this.ctxBrokerClient.retrieveIndividualEntityId(requestor, cssId, callback);
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result = callback.getResult();
					} else {
						throw callback.getException();
					}
				} catch (InterruptedException e) {
					throw new CtxBrokerException("Interrupted while waiting for remote IndvidualCtxEntity id");
				}
			}

		}

		LOG.debug("retrieveIndividualEntityId: result={}", result);
		return new AsyncResult<CtxEntityIdentifier>(result);
	}

	@Override
	@Async
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			final Requestor requestor, final IIdentity cisId) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (cisId == null) {
			throw new NullPointerException("cisId can't be null");
		}

		LOG.debug("retrieveCommunityEntityId: requestor={}, cisId={}", requestor, cisId);

		CtxEntityIdentifier result = null;

		try {
			if (this.isLocalCisId(cisId)) {
				// L O C A L
				final CommunityCtxEntity communityEntity = 
						this.communityCtxDBMgr.retrieveCommunityEntity(cisId.getBareJid());
				if (communityEntity != null) {
					result = communityEntity.getId();
				}
			} else {
				// R E M O T E
				final RetrieveCommunityEntityIdCallback callback = 
						new RetrieveCommunityEntityIdCallback();
				this.ctxBrokerClient.retrieveCommunityEntityId(requestor, cisId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() == null) {
							result = callback.getResult();
						} else { 
							throw callback.getException();
						}
					} catch (InterruptedException e) {
						throw new CtxBrokerException("Interrupted while waiting for remote CommunityCtxEntity id");
					}
				}
			}
		} catch (Exception e) {
			throw new CtxBrokerException(e.getLocalizedMessage(), e);
		}

		LOG.debug("retrieveCommunityEntityId: result={}", result);
		return new AsyncResult<CtxEntityIdentifier>(result);
	}

	
	
	@Override
	@Async
	public Future<List<CtxAttribute>> retrieveFuture(Requestor requestor,
			CtxAttributeIdentifier attrId, Date date) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (attrId == null) {
			throw new NullPointerException("ctxId can't be null");
		}
		
		LOG.debug("retrieveFuture: attrId={}, date={}", attrId, date);
		
		final IIdentity target = this.extractIIdentity(attrId);

		this.logRequest(null, target);

		List<CtxAttribute> result = new ArrayList<CtxAttribute>();

		if (this.isLocalId(target)) {

			// L O C A L
			if (IdentityType.CIS != target.getType()) { 
				LOG.debug(" L O C A L ");
				// U S E R
				result.add(this.userCtxInferenceMgr.predictContext(attrId, date));
			} else { 
				// C O M M U N I T Y
				result.add(this.communityCtxInferenceMgr.predictContext(attrId, date));
			}
		}else { // R E M O T E
		
			final RetrieveFutureCtxCallback callback = new RetrieveFutureCtxCallback();
			this.ctxBrokerClient.retrieveFuture(requestor, attrId, date, callback); 
			synchronized (callback) {
				try {
					callback.wait();
					if (callback.getException() == null) {
						result = callback.getResult();
						
					} else { 
						throw callback.getException();
					}					

				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote retrieve");
				}
			}
		}
		LOG.debug("retrieveFuture: result={}", result);
		return new AsyncResult<List<CtxAttribute>>(result);
	}


	
	@Override
	@Async
	public Future<List<CtxAttribute>> retrieveFuture(Requestor requestor,
			CtxAttributeIdentifier attrId, int modificationIndex)
					throws CtxException {
		LOG.debug("retrieveFuture: attrId={}, modificationIndex={}", attrId, modificationIndex);

		final IIdentity target = this.extractIIdentity(attrId);

		this.logRequest(null, target);

		List<CtxAttribute> result = new ArrayList<CtxAttribute>();

		try {

			result = this.retrieveFuture(attrId, null).get();

		} catch (Exception e) {
			LOG.error("Exception on predicting context attribute :"+attrId+" for modification index : "+modificationIndex+". "+ e.getLocalizedMessage());
			e.printStackTrace();
		}

		LOG.debug("retrieveFuture: result={}", result);
		return new AsyncResult<List<CtxAttribute>>(result);
	}
		
	
	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			Requestor requestor, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {

		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			final Requestor requestor, CtxAttributeIdentifier attrId, Date startDate,
			Date endDate) throws CtxException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}

		LOG.debug("retrieveHistory: requestor={}, attrId={}, startDate={}, endDate={}",
				new Object[] { requestor, attrId, startDate, endDate });

		final List<CtxHistoryAttribute> result = new ArrayList<CtxHistoryAttribute>();

		final IIdentity target = this.extractIIdentity(attrId);

		this.logRequest(requestor, target);

		if (IdentityType.CSS.equals(target.getType()) 
				|| IdentityType.CSS_RICH.equals(target.getType())
				|| IdentityType.CSS_LIGHT.equals(target.getType())) {

			if (this.commMgr.getIdManager().isMine(target) ) {
				result.addAll(this.userCtxHistoryMgr.retrieveHistory(attrId, startDate, endDate));
			} else {
				LOG.error("remote call is not supported for ctx history data");
			}

		} else if (IdentityType.CIS.equals(target.getType())){
			if (isLocalCisId(target) ) {
				result.addAll(this.userCtxHistoryMgr.retrieveHistory(attrId, startDate, endDate));
			} else {
				LOG.error("remote call is not supported for ctx history data");
			}
		}

		LOG.debug("retrieveHistory: requestor={}", result);
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

		final INetworkNode cssNodeId = this.commMgr.getIdManager().getThisNetworkNode();
		try {
			return this.commMgr.getIdManager().fromJid(cssNodeId.getBareJid());
		} catch (InvalidFormatException e) {
			throw new CtxBrokerException(" cssOwnerId could not be set: " + e.getLocalizedMessage(), e);
		}	
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

	/**
	 * added by eboylan for CSE integration test
	 */
	@Override
	public CtxEvaluationResults evaluateSimilarity(String[] ids, ArrayList<String> attrib) throws CtxException {
		//LOG.info("EBOYLANLOGFOOTPRINT: ctxSimilarity broker = " + ctxSimilarityEval);
		//LOG.info("EBOYLANLOGFOOTPRINT internalCtxBroker.evaluateSimilarity called");
		CtxEvaluationResults evalResult = (CtxEvaluationResults) this.ctxSimilarityEval.evaluateSimilarity(ids, attrib); //this.
		return evalResult;
	}

	private CtxAttribute inferUserAttribute(
			final CtxAttributeIdentifier ctxAttrId) throws CtxException {

		if (ctxAttrId == null) {
			throw new NullPointerException("ctxAttrId can't be null");
		}

		CtxAttribute result = (CtxAttribute) this.userCtxDBMgr.retrieve(ctxAttrId);
		// Check if inference is applicable/required:
		// 1. inferrable attribute type AND
		// 2. existing attribute either has no value OR a value of poor quality
		try {
			if (result != null 
					&& this.userCtxInferenceMgr.getInferrableTypes().contains(result.getType())
					&& (!CtxBrokerUtils.hasValue(result) || 
							this.userCtxInferenceMgr.isPoorQuality(result.getQuality()))) {
				LOG.debug("Inferring user context attribute with id '{}'", ctxAttrId);
				final CtxAttribute inferredAttribute = 
						this.userCtxInferenceMgr.refineOnDemand(ctxAttrId);
				LOG.debug("Inferred user context attribute '{}'", inferredAttribute);
				if (inferredAttribute != null) {
					result = inferredAttribute;
				}
			}
		} catch (ServiceUnavailableException sue) {
			throw new CtxBrokerException("User Context Inference Mgr is not available");
		}

		return result;
	}

	private CtxAttribute inferCommunityAttribute(
			final CtxAttributeIdentifier ctxAttrId) throws CtxException {

		if (ctxAttrId == null) {
			throw new NullPointerException("ctxAttrId can't be null");
		}

		CtxAttribute result = (CtxAttribute) this.communityCtxDBMgr.retrieve(ctxAttrId);
		// Check if inference is applicable/required:
		// 1. inferrable attribute type
		try {
			if (this.communityCtxInferenceMgr.getInferrableTypes().contains(ctxAttrId.getType())) {
				LOG.debug("Inferring community context attribute with id '{}'", ctxAttrId);
				final CtxAttribute inferredAttribute = 
						this.communityCtxInferenceMgr.estimateCommunityContext(ctxAttrId.getScope(), ctxAttrId);
				LOG.debug("Inferred community context attribute '{}'", inferredAttribute);
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
			throw new CtxBrokerException("Community Context Inference Mgr is not available");
		}

		return result;
	}

	private boolean isLocalId(final IIdentity id) {

		if (id == null) {
			throw new NullPointerException("id can't be null");
		}

		if (IdentityType.CIS != id.getType()) {
			// U S E R  I D
			return this.isLocalCssId(id);
		} else {
			// C O M M U N I T Y  I D
			return this.isLocalCisId(id);
		}
	}

	private boolean isLocalCssId(final IIdentity cssId) {

		if (cssId == null) {
			throw new NullPointerException("cssId can't be null");
		}
		if (IdentityType.CIS == cssId.getType()) {
			throw new IllegalArgumentException("cssId IdentityType is not CSS");
		}

		return this.commMgr.getIdManager().isMine(cssId);
	}

	private boolean isLocalCisId(final IIdentity cisId) {

		if (cisId == null) {
			throw new NullPointerException("cisId can't be null");
		}
		if (IdentityType.CIS != cisId.getType()) {
			throw new IllegalArgumentException("cisId IdentityType is not CIS");
		}

		return (this.commMgrFactory.getAllCISCommMgrs().get(cisId) != null);
	}

	private IIdentity extractIIdentity(CtxIdentifier ctxId) throws CtxBrokerException {

		if (ctxId == null) {
			throw new NullPointerException("ctxId can't be null");
		}

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

		if (ctxModelObject == null) {
			throw new NullPointerException("ctxModelObject can't be null");
		}

		return this.extractIIdentity(ctxModelObject.getId());
	}

	@Override
	public CtxAttribute communityInheritance(CtxAttributeIdentifier ctxAttrID) {

		if (ctxAttrID == null)
			throw new NullPointerException("Exception while initiating context inheritance, ctxAttrID can't be null");

		//TODO add more controls
		CtxAttribute ctxAttribute = this.userCtxInferenceMgr.inheritContext(ctxAttrID);

		return ctxAttribute;
	}
}