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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.internal.context.broker.*;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
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

	private static Logger LOG = LoggerFactory
			.getLogger(ContextSourceManagement.class);
	private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage"); // to define a dedicated Logger for Performance Testing


	/**
	 * The Communication Manager service reference.
	 * 
	 * @see {@link #setCommMgr(ICommManager)}
	 */
	private ICommManager commMgr;

	public ICommManager getCommManager() {
		return commMgr;
	}

	public void setCommManager(ICommManager commManager) {
		this.commMgr = commManager;
	}

	/**
	 * The Context Broker service reference.
	 * 
	 * @see {@link #setCtxBroker(ICtxBroker)}
	 */
	@Autowired(required = true)
	private ICtxBroker ctxBroker;

	/**
	 * The Device Manager service reference
	 * 
	 * @see {@link #setDeviceManager(IDeviceManager)}
	 */
	private IDeviceManager deviceManager;

	/**
	 * Sets the Device Manager service reference.
	 * 
	 * @param deviceManager
	 *            the Device Manager service reference to set.
	 */
	public void setDeviceManager(IDeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	private IEventMgr eventManager;

	public IEventMgr getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventMgr eventManager) {
		if (null == eventManager) {
			LOG.error("[COMM02] EventManager not available");
		}
		this.eventManager = eventManager;
	}

	private IIdentityManager idManager;

	private NewDeviceListener newDeviceListener;
	private final String sensor = "CONTEXT_SOURCE";
	private int counter;

	/**
	 * Sets the Context Broker service reference
	 * 
	 * @param ctxBroker
	 *            the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	@Autowired(required=true)
	public ContextSourceManagement(ICommManager commManager, 
			IDeviceManager deviceManager, IEventMgr eventManager) {
		
		this.idManager = commManager.getIdManager();
	}
	
	/**
	 * Empty constructor used for testing
	 */
	public ContextSourceManagement() {
//		activate();
	}

	//@PostConstruct
	public void activate() {
		this.newDeviceListener = new NewDeviceListener(deviceManager,
				eventManager, this);
		idManager = commMgr.getIdManager();
		// newDeviceListener.run();
		
		try {
			List<CtxEntityIdentifier> shadowEntitiesFuture = ctxBroker
					.lookupEntities(sensor, "CtxSourceId", null, null).get();
			counter = shadowEntitiesFuture.size();
		} catch (CtxException e) {
			LOG.error(e.getMessage());
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		} catch (ExecutionException e) {
			LOG.error(e.getMessage());
		}
		
		LOG.info("{}", "CSM started");
	}

	//@PreDestroy
	public void deactivate() {
		this.newDeviceListener.stop();
		LOG.info("CSM + DeviceListener stopped");
	}

	@Override
	@Async
	public Future<String> register(String name, String contextType) {
		return registerFull(null, name, contextType, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.context.source.ICtxSourceMgr#register(org.societies
	 * .api.context.model.INetworkNode, java.lang.String, java.lang.String)
	 */
	@Async
	public Future<String> register(INetworkNode contextOwner, String name,
			String contextType) {
		return registerFull(contextOwner, name, contextType, null);
	}

	/**
	 * full internal register, used by external register methods and by DeviceManager connector
	 */
	@Async
	Future<String> registerFull(INetworkNode contextOwner, String name,
			String contextType, String id) {
		long timestamp = System.nanoTime();
		
		if (ctxBroker == null) {
			LOG.error("Could not register " + contextType
					+ ": Context Broker cannot be found");
			return null;
		}

		if (id==null)
			id = name + counter++; 

		try {
			Future<List<CtxEntityIdentifier>> shadowEntitiesFuture = ctxBroker
					.lookupEntities(sensor, "CtxSourceId", null, null);// TODO
																		// Check
																		// if
																		// min
																		// or
																		// max
																		// values
																		// are
																		// necessary
			List<CtxEntityIdentifier> shadowEntities = shadowEntitiesFuture
					.get();

			// Check if ID composed before does already exist... Sense?
			if (shadowEntities.size() > 0) {
				for (CtxEntityIdentifier cei : shadowEntities) {
					Set<CtxAttribute> sourceIDs = ((CtxEntity) ctxBroker
							.retrieve(cei).get()).getAttributes("CtxSourceId");
					if (sourceIDs.size() == 0)
						continue;
					if (sourceIDs.size() > 1) {
						LOG.error("wrong formatting of CtxEntity "
								+ cei
								+ ". More than 1 attribute \"CtxSourceId\". Disregarded.");
					} else {
						String shadowEntID = sourceIDs.iterator().next()
								.getStringValue();
						if (shadowEntID.equals(id)) {
							LOG.error("Sensor-ID "
									+ id
									+ " is not unique. Sensor could not be registered");
							return null;
						}
					}
				}
			}

			Future<CtxEntity> fooEntFuture;
			fooEntFuture = ctxBroker.createEntity(sensor);
			CtxEntity fooEnt = fooEntFuture.get();

			Future<CtxAttribute> nameAttrFuture = ctxBroker.createAttribute(
					fooEnt.getId(), "CtxSourceId");
			CtxAttribute nameAttr = nameAttrFuture.get();
			// nameAttr.setStringValue(id);
			ctxBroker.updateAttribute(nameAttr.getId(), id);

			Future<CtxAttribute> ctxTypeAttrFuture = ctxBroker.createAttribute(
					fooEnt.getId(), "CtxType");
			CtxAttribute ctxTypeAttr = ctxTypeAttrFuture.get();
			ctxBroker.updateAttribute(ctxTypeAttr.getId(), contextType);

			CtxEntity ownerCtxEntity = null;
			if (contextOwner != null) {

				String cssOwnerStr = contextOwner.getBareJid();
				IIdentity ownerCtxEntityID = idManager.fromJid(cssOwnerStr);
				ownerCtxEntity = ctxBroker.retrieveIndividualEntity(
						ownerCtxEntityID).get();

				Future<CtxAssociation> futAssociationToContextOwnerEntity = ctxBroker
						.createAssociation("providesUpdatesFor");
				CtxAssociation associationToContextOwnerEntity = futAssociationToContextOwnerEntity
						.get();
				associationToContextOwnerEntity.setParentEntity(fooEnt.getId());
				associationToContextOwnerEntity.addChildEntity(ownerCtxEntity
						.getId());
			}

			LOG.debug("Created entity: " + fooEnt);
		} catch (CtxException e) {
			LOG.error(e.getMessage());
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		} catch (ExecutionException e) {
			LOG.error(e.getMessage());
		} catch (InvalidFormatException e) {
			LOG.error(e.getMessage());
		}
		
		IPerformanceMessage m = new PerformanceMessage();
		m.setTestContext("CSM_Delay_ComponentInternal");
		m.setSourceComponent(this.getClass()+"");
		m.setPerformanceType(IPerformanceMessage.Delay);
		m.setOperationType("Register");
		m.setD82TestTableName("S11");
		m.setPerformanceNameValue("Delay="+(System.nanoTime()-timestamp ));

		PERF_LOG.trace(m.toString());

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

		if (LOG.isTraceEnabled())
			LOG.debug("Sending update: id=" + identifier + ", data=" + data
					+ ", ownerEntity=" + owner + ", inferred=" + inferred
					+ ", precision=" + precision + ", frequency=" + frequency);

		Future<List<CtxEntityIdentifier>> shadowEntitiesFuture;
		List<CtxEntityIdentifier> shadowEntities;
		CtxEntityIdentifier shadowEntityID = null;
		Set<CtxAttribute> attrs = null;
		CtxEntity shadowEntity = null;

		try {
			String type = "";
			Future<CtxAttribute> dataAttrFuture;
			CtxAttribute dataAttr;
			CtxQuality quality;

			shadowEntitiesFuture = ctxBroker.lookupEntities(sensor,
					"CtxSourceId", identifier, identifier);
			shadowEntities = shadowEntitiesFuture.get();
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
				shadowEntity = (CtxEntity) ctxBroker.retrieve(shadowEntityID)
						.get();
			}

			attrs = shadowEntity.getAttributes("CtxType");
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
				dataAttrFuture = ctxBroker.createAttribute(shadowEntityID,
						"data");
				dataAttr = dataAttrFuture.get();
			}

			updateData(data, dataAttr);

			dataAttr.setSourceId(identifier);
			dataAttr.setHistoryRecorded(true);

			quality = dataAttr.getQuality();
			quality.setOriginType(CtxOriginType.SENSED);

			if (USE_QOC) {
				dataAttr.setSourceId(identifier);
				dataAttr.setHistoryRecorded(true);

				if (inferred)
					quality.setOriginType(CtxOriginType.INFERRED);
				quality.setPrecision(precision);
				quality.setUpdateFrequency(frequency);
			}

			ctxBroker.update(dataAttr);

			/* update Context Information with Information Owner Entity */
			if (owner == null) {
				try {
					// Check if the shadow entity has an association to an
					// ctxEntity
					List<CtxIdentifier> assocIdentifierList = ctxBroker.lookup(
							CtxModelType.ASSOCIATION, "providesUpdatesFor")
							.get();
					CtxAssociation temp;
					CtxEntity parent;
					CtxEntity child;

					if (assocIdentifierList.size() != 0)
						for (CtxIdentifier ctxId : assocIdentifierList) {
							temp = (CtxAssociation) ctxBroker.retrieve(ctxId)
									.get();
							if (temp.parentEntity == null)
								continue;
							parent = (CtxEntity) ctxBroker.retrieve(
									temp.parentEntity).get();
							if (parent != shadowEntity)
								continue;
							if (temp.childEntities == null
									|| temp.childEntities.size() == 0)
								continue;
							child = (CtxEntity) ctxBroker.retrieve(
									temp.childEntities.iterator().next()).get();
							if (child != null) {
								owner = child;
								break;
							}
						}

					// owner = ctxBroker.retrieveCssOperator().get();
					owner = ctxBroker.retrieveCssNode(
							commMgr.getIdManager().getThisNetworkNode()).get();

				} catch (CtxException e) {
					LOG.error(
							"Could not handle update from " + identifier
									+ ": Could not retrieve device entity: "
									+ e.getLocalizedMessage(), e);
					return new AsyncResult<Boolean>(false);
				}
			}

			attrs = owner.getAttributes(type);
			if (attrs.size() > 0)
				dataAttr = attrs.iterator().next();
			else {
				dataAttrFuture = ctxBroker.createAttribute(owner.getId(), type);
				dataAttr = dataAttrFuture.get();
				dataAttr.setSourceId(identifier);
			}
			if (LOG.isDebugEnabled())
				LOG.debug("dataAttr=" + dataAttr);

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
			dataAttr.setHistoryRecorded(true);
			// Update attribute.
			updateData(data, dataAttr);

		} catch (CtxException e) {
			LOG.error(
					"Could not handle update from " + identifier + ": "
							+ e.getLocalizedMessage(), e);
			return new AsyncResult<Boolean>(false);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
			return new AsyncResult<Boolean>(false);
		} catch (ExecutionException e) {
			LOG.error(e.getMessage());
			return new AsyncResult<Boolean>(false);
		}
		
		IPerformanceMessage m = new PerformanceMessage();
		m.setTestContext("CSM_Delay_ComponentInternal");
		m.setSourceComponent(this.getClass()+"");
		m.setPerformanceType(IPerformanceMessage.Delay);
		m.setOperationType("SendUpdate");
		m.setD82TestTableName("S11");
		m.setPerformanceNameValue("Delay="+(System.nanoTime()-timestamp ));

		PERF_LOG.trace(m.toString());

		return new AsyncResult<Boolean>(true);

	}

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
			throws CtxException {
		if (value instanceof String) {
			attr.setStringValue((String) value);
			attr.setValueType(CtxAttributeValueType.STRING);
		} else if (value instanceof Integer) {
			attr.setIntegerValue((Integer) value);
			attr.setValueType(CtxAttributeValueType.INTEGER);
		} else if (value instanceof Double) {
			attr.setDoubleValue((Double) value);
			attr.setValueType(CtxAttributeValueType.DOUBLE);
		} else {
			byte[] blobBytes = null;
			try {
				blobBytes = SerialisationHelper.serialise(value);
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
			attr.setBinaryValue(blobBytes);
			attr.setValueType(CtxAttributeValueType.BINARY);
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
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		} catch (ExecutionException e) {
			LOG.error(e.getMessage());
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
			shadowEntitiesFuture = ctxBroker.lookupEntities(sensor,
					"CtxSourceId", identifier, identifier);
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

}
