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
package org.societies.context.source.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.CtxQuality;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.context.broker.*;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class ContextSourceManagement implements ICtxSourceMgr {

	private static Logger LOG = LoggerFactory.getLogger(ContextSourceManagement.class);
	private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage"); // to define a dedicated Logger for Performance Testing

	/**
	 * The Context Broker service reference.
	 * 
	 * @see {@link #setCtxBroker(ICtxBroker)}
	 */
	private ICtxBroker ctxBroker;
	
	/**
	 * The Communication Manager service reference.
	 * 
	 * @see {@link #getCommManager()}
	 * @see {@link #setCommMgr(ICommManager)}
	 */
	@Autowired(required = true)
	private ICommManager commMgr;
	
	private volatile int counter = 0;

	@Autowired(required=true)
	public ContextSourceManagement(ICtxBroker ctxBroker) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.ctxBroker = ctxBroker;
		try {
			final List<CtxIdentifier> sourceEntIds = this.ctxBroker.lookup(
					CtxModelType.ENTITY, CtxEntityTypes.CONTEXT_SOURCE).get();
			for (final CtxIdentifier sourceEntId : sourceEntIds) {
				if (LOG.isDebugEnabled())
					LOG.debug("Removing " + CtxEntityTypes.CONTEXT_SOURCE + " entity " + sourceEntId);
				this.ctxBroker.remove(sourceEntId);
			}
			if (LOG.isInfoEnabled())
				LOG.info("Removed " + sourceEntIds.size() + " obsolete context source registration(s)");
			
		} catch (Exception e) {
			
			LOG.error("Could not initialise CSM: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/**
	 * Empty constructor used for junit testing
	 */
	public ContextSourceManagement() {}

	/*
	 * @see org.societies.api.context.source.ICtxSourceMgr#register(java.lang.String, java.lang.String)
	 */
	@Override
	@Async
	public Future<String> register(String name, String contextType) {

		return registerFull(null, name, contextType, null);
	}

	/*
	 * @see org.societies.api.context.source.ICtxSourceMgr#register(org.societies.api.identity.INetworkNode, java.lang.String, java.lang.String)
	 */
	@Override
	@Async
	public Future<String> register(INetworkNode contextOwner, String name,
			String contextType) {

		return registerFull(contextOwner, name, contextType, null);
	}

	/**
	 * full internal register, used by external register methods and by DeviceManager connector
	 */
	@Async
	Future<String> registerFull(INetworkNode cssNodeId, String name,
			String contextType, String id) {
		long timestamp = System.nanoTime();
		
		if (this.ctxBroker == null) {
			LOG.error("Could not register " + contextType
					+ ": Context Broker is not available");
			return new AsyncResult<String>(null);
		}

		if (id == null)
			id = name + this.counter++; 

		try {
			// TODO use existing registration if available
			final List<CtxEntityIdentifier> shadowEntities = this.ctxBroker.lookupEntities(
					CtxEntityTypes.CONTEXT_SOURCE, CtxAttributeTypes.ID, id, id).get();
			if (shadowEntities.size() > 0) {
				LOG.error("Aborting registration of '" + name + "' context source"
						+ ": Generated source id '" + id + "' already in use");
				return new AsyncResult<String>(null);
			}

			final CtxEntity shadowEnt = this.ctxBroker.createEntity(CtxEntityTypes.CONTEXT_SOURCE).get();
			// 1. Source ID
			final CtxAttribute sourceIdAttr = this.ctxBroker.createAttribute(
					shadowEnt.getId(), CtxAttributeTypes.ID).get();
			this.ctxBroker.updateAttribute(sourceIdAttr.getId(), id);
			// 2. context type
			final CtxAttribute ctxTypeAttr = this.ctxBroker.createAttribute(
					shadowEnt.getId(), CtxAttributeTypes.TYPE).get();
			this.ctxBroker.updateAttribute(ctxTypeAttr.getId(), contextType);

			final CtxEntity ctxOwnerEntity;
			if (cssNodeId != null) {
				ctxOwnerEntity = this.ctxBroker.retrieveCssNode(cssNodeId).get();
			} else { // (cssNodeId == null)
				ctxOwnerEntity = this.ctxBroker.retrieveCssNode(
						this.commMgr.getIdManager().getThisNetworkNode()).get();
			}
			if (ctxOwnerEntity == null) {
				LOG.error("Could not complete registration of '"
						+ name + "' context source with id '" 
						+ id + "': CSS Node entity does not exist"); // TODO
				return new AsyncResult<String>(null);
			}
				
			final CtxAssociation assocToCtxOwnerEntity = this.ctxBroker
					.createAssociation(CtxAssociationTypes.PROVIDES_UPDATES_FOR).get();
			assocToCtxOwnerEntity.setParentEntity(shadowEnt.getId());
			assocToCtxOwnerEntity.addChildEntity(ctxOwnerEntity.getId());
			this.ctxBroker.update(assocToCtxOwnerEntity);

			// performance logging
			IPerformanceMessage m = new PerformanceMessage();
			m.setTestContext("CSM_Delay_ComponentInternal");
			m.setSourceComponent(this.getClass()+"");
			m.setPerformanceType(IPerformanceMessage.Delay);
			m.setOperationType("Register");
			m.setD82TestTableName("S11");
			m.setPerformanceNameValue("Delay="+(System.nanoTime()-timestamp ));

			PERF_LOG.trace(m.toString());
			
		} catch (Exception e) {
			LOG.error("Could not complete registration of '"
					+ name + "' context source with id '" 
					+ id + "': " + e.getMessage(), e);
			return new AsyncResult<String>(null);
		}

		return new AsyncResult<String>(id);
	}

	private Future<Boolean> completeSendUpdate(String identifier,
			Serializable data, CtxEntity owner, boolean inferred,
			double precision, double frequency, boolean USE_QOC) {
		long timestamp = System.nanoTime();

		if (this.ctxBroker == null) {
			LOG.error("Could not handle update from " + identifier
					+ ": Context Broker is not available");
			return new AsyncResult<Boolean>(false);
		}

		if (LOG.isDebugEnabled())
			LOG.debug("Sending update: id=" + identifier + ", data=" + data
					+ ", ownerEntity=" + owner + ", inferred=" + inferred
					+ ", precision=" + precision + ", frequency=" + frequency);

		List<CtxEntityIdentifier> shadowEntities;
		CtxEntityIdentifier shadowEntityID = null;
		Set<CtxAttribute> attrs = null;
		CtxEntity shadowEntity = null;

		try {
			String type = "";
			CtxAttribute dataAttr;
			CtxQuality quality;

			shadowEntities = ctxBroker.lookupEntities(CtxEntityTypes.CONTEXT_SOURCE,
					CtxAttributeTypes.ID, identifier, identifier).get();
			if (shadowEntities.size() > 1) {
				if (LOG.isErrorEnabled())
					LOG.error("Sensor-ID " + identifier
							+ " is not unique. No information stored.");
				return new AsyncResult<Boolean>(false);
				// throw new
				// Exception("Ambiguity: more than one context source with this identifier exists.");
			} else if (shadowEntities.isEmpty()) {
				if (LOG.isErrorEnabled())
					LOG.error("Sensor-ID " + identifier
							+ " is not available. No information stored.");
				return new AsyncResult<Boolean>(false);
				// throw new
				// Exception("Sending failure due to missing Registration.");
			} else {
				shadowEntityID = shadowEntities.get(0);
				shadowEntity = (CtxEntity) this.ctxBroker.retrieve(shadowEntityID)
						.get();
			}

			attrs = shadowEntity.getAttributes(CtxAttributeTypes.TYPE);
			if (attrs != null && attrs.size() > 0)
				type = attrs.iterator().next().getStringValue();
			else
				type = "data";
			if (LOG.isDebugEnabled())
				LOG.debug("type is " + type);

			/* update Context Information at Context Source Shadow Entity */
			attrs = shadowEntity.getAttributes("data");
			if (attrs != null && attrs.size() > 0)
				dataAttr = attrs.iterator().next();
			else {
				dataAttr = this.ctxBroker.createAttribute(shadowEntityID,
						"data").get();
			}

			dataAttr.setSourceId(identifier);
			// TODO why? dataAttr.setHistoryRecorded(true);

			quality = dataAttr.getQuality();
			quality.setOriginType(CtxOriginType.SENSED);

			if (USE_QOC) {
				if (inferred)
					quality.setOriginType(CtxOriginType.INFERRED);
				quality.setPrecision(precision);
				quality.setUpdateFrequency(frequency);
			}

			this.updateData(data, dataAttr);

			/* update Context Information with Information Owner Entity */
			if (LOG.isDebugEnabled())
				LOG.debug("Acquiring context owner entity for shadow entity " + shadowEntity.getId());
			if (owner == null) {
				try {
					// Check if the shadow entity has an association to an
					// ctxEntity
					final Set<CtxAssociationIdentifier> assocIdentifiers = 
							shadowEntity.getAssociations(CtxAssociationTypes.PROVIDES_UPDATES_FOR);
					CtxAssociation providesUpdatesForAssoc;
					CtxEntity childEnt;

					if (assocIdentifiers.size() != 0) {
						for (final CtxAssociationIdentifier ctxId : assocIdentifiers) {
							providesUpdatesForAssoc = (CtxAssociation) this.ctxBroker.retrieve(ctxId)
									.get();
							if (providesUpdatesForAssoc.parentEntity == null 
									|| !shadowEntity.getId().equals(providesUpdatesForAssoc.getParentEntity())
									|| providesUpdatesForAssoc.getChildEntities().isEmpty()) {
								if (LOG.isDebugEnabled())
									LOG.debug("Ignoring association " + providesUpdatesForAssoc.getId());
								continue;
							}
							childEnt = (CtxEntity) this.ctxBroker.retrieve(
									providesUpdatesForAssoc.childEntities.iterator().next()).get();
							if (childEnt != null) {
								owner = childEnt;
								break;
							} else {
								LOG.error("child is null!!!");
							}
						}
					}
					
					if (owner == null)
						owner = this.ctxBroker.retrieveCssNode(
							this.commMgr.getIdManager().getThisNetworkNode()).get();
					// TODO null check again!!

				} catch (Exception e) {
					LOG.error("Could not handle update from " + identifier
									+ ": Could not retrieve context owner entity: "
									+ e.getLocalizedMessage(), e);
					return new AsyncResult<Boolean>(false);
				}
			}

			if (LOG.isDebugEnabled())
				LOG.debug("Context owner entity for shadow entity " + shadowEntity.getId() + " is " + owner.getId());
			attrs = owner.getAttributes(type);
			if (attrs.size() > 0) {
				for (final CtxAttribute foundAttr : attrs) {
					if (foundAttr.getSourceId() != null && identifier.equals(foundAttr.getSourceId())) {
						dataAttr = foundAttr;
						break;
					}
				}
			} else {
				dataAttr = this.ctxBroker.createAttribute(owner.getId(), type).get();
				dataAttr.setSourceId(identifier);
			}
			if (LOG.isDebugEnabled())
				LOG.debug("dataAttr.getId()=" + dataAttr.getId());

			// Update QoC information.
			quality = dataAttr.getQuality();
			quality.setOriginType(CtxOriginType.SENSED);

			if (USE_QOC) {
				if (inferred)
					quality.setOriginType(CtxOriginType.INFERRED);
				quality.setPrecision(precision);
				quality.setUpdateFrequency(frequency);
			}

			// Set history recorded flag.
			// TODO why? dataAttr.setHistoryRecorded(true);
			// Update attribute.
			this.updateData(data, dataAttr);
			
			// performance logging
			if (PERF_LOG.isTraceEnabled()) {
				IPerformanceMessage m = new PerformanceMessage();
				m.setTestContext("CSM_Delay_ComponentInternal");
				m.setSourceComponent(this.getClass()+"");
				m.setPerformanceType(IPerformanceMessage.Delay);
				m.setOperationType("SendUpdate");
				m.setD82TestTableName("S11");
				m.setPerformanceNameValue("Delay="+(System.nanoTime()-timestamp ));
				PERF_LOG.trace(m.toString());
			}

		} catch (Exception e) {
			LOG.error("Could not handle update from " + identifier + ": "
					+ e.getLocalizedMessage(), e);
			return new AsyncResult<Boolean>(false);
		}

		return new AsyncResult<Boolean>(true);
	}

	/*
	 * @see org.societies.api.context.source.ICtxSourceMgr#sendUpdate(java.lang.String, java.io.Serializable, org.societies.api.context.model.CtxEntity)
	 */
	@Override
	@Async
	public Future<Boolean> sendUpdate(String identifier, Serializable data,
			CtxEntity owner) {
		
		return completeSendUpdate(identifier, data, owner, false, 0, 0, false);
	}

	@Override
	@Async
	public Future<Boolean> sendUpdate(String identifier, Serializable data,
			CtxEntity owner, boolean inferred, double precision,
			double frequency) {

		return completeSendUpdate(identifier, data, owner, inferred, precision,
				frequency, true);
	}

	@Override
	@Async
	public Future<Boolean> sendUpdate(String identifier, Serializable data) {
		return completeSendUpdate(identifier, data, null, false, 0, 0, false);
	}

	private void updateData(Serializable value, CtxAttribute attr)
			throws Exception {
		
		if (value instanceof String) {
			attr.setStringValue((String) value);
			attr.setValueType(CtxAttributeValueType.STRING);
		} else if (value instanceof Integer) {
			attr.setIntegerValue((Integer) value);
			attr.setValueType(CtxAttributeValueType.INTEGER);
		} else if (value instanceof Double) {
			attr.setDoubleValue((Double) value);
			attr.setValueType(CtxAttributeValueType.DOUBLE);
		} else if (value instanceof Serializable) {
			final byte[] blobBytes = SerialisationHelper.serialise(value);
			attr.setBinaryValue(blobBytes);
			attr.setValueType(CtxAttributeValueType.BINARY);
		} else { // if value == null
			attr.setStringValue(null);
			attr.setValueType(CtxAttributeValueType.EMPTY);
		}

		try {
			attr = (CtxAttribute) ctxBroker.update(attr).get();
		} catch (CtxException cde) {
			// If the value is a String attempt to store it as a blob. As the
			// String might just be too long.
			if (value instanceof String) {
				if (LOG.isDebugEnabled())
					LOG.debug("Attempting to store String value as a blob");
				byte[] blobBytes = null;
				try {
					blobBytes = SerialisationHelper.serialise(value);
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
				attr.setBinaryValue(blobBytes);
				attr.setValueType(CtxAttributeValueType.BINARY);
				ctxBroker.update(attr);
			} else {
				throw cde;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Async
	public Future<Boolean> unregister(String identifier) {
		if (ctxBroker == null) {
			LOG.error("Could not unregister " + identifier
					+ ": Context Broker cannot be found");
			return new AsyncResult<Boolean>(false);
		}

		Future<List<CtxEntityIdentifier>> shadowEntitiesFuture;
		List<CtxEntityIdentifier> shadowEntities;
		CtxIdentifier shadowEntity = null;
		try {
			shadowEntitiesFuture = ctxBroker.lookupEntities(CtxEntityTypes.CONTEXT_SOURCE,
					CtxAttributeTypes.ID, identifier, identifier);
			shadowEntities = shadowEntitiesFuture.get();
			if (shadowEntities.size() > 1) {
				LOG.debug("Sensor-ID " + identifier
						+ " is not unique. Sensor could not be unregistered");
				return new AsyncResult<Boolean>(false);
				// throw new
				// Exception("Unregistering failure due to ambiguity.");
			} else if (shadowEntities.isEmpty()) {
				LOG.debug("Sensor-ID " + identifier
						+ " is not available. Sensor could not be unregistered");
				return new AsyncResult<Boolean>(false);
				// throw new
				// Exception("Unregistering failure due to missing Registration.");
			} else
				shadowEntity = shadowEntities.get(0);

			ctxBroker.remove(shadowEntity);
		} catch (CtxException e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
			return new AsyncResult<Boolean>(false);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
			return new AsyncResult<Boolean>(false);
		} catch (ExecutionException e) {
			LOG.error(e.getMessage());
			return new AsyncResult<Boolean>(false);
		}
		return new AsyncResult<Boolean>(true);
	}
	
	public ICommManager getCommManager() {
		
		return commMgr;
	}

	public void setCommManager(ICommManager commManager) {
		
		this.commMgr = commManager;
	}
	
	/**
	 * Sets the Context Broker service reference
	 * 
	 * @param ctxBroker
	 *            the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
}