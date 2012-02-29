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


import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.context.source.ICtxSourceMgrCallback;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.css.devicemgmt.devicemanager.IDeviceManager;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class ContextSourceManagement implements ICtxSourceMgr {

	private static Logger LOG = LoggerFactory
			.getLogger(ContextSourceManagement.class);
	/**
	 * The User Context DB Mgmt service reference.
	 * 
	 * @see {@link #setUserCtxDBMgr(IUserCtxDBMgr)}
	 */
	@Autowired(required = true)
	private IUserCtxDBMgr userCtxDBMgr = null;

	/**
	 * The Context Broker service reference.
	 * 
	 * @see {@link #setCtxBroker(ICtxBroker)}
	 */
	@Autowired(required = true)
	private ICtxBroker ctxBroker = null;

	/**
	 * The Device Manager service reference
	 * 
	 * @see {@link #setDeviceManager(IDeviceManager)}
	 */
	@Autowired(required = true)
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

	private NewDeviceListener newDeviceListener;
	private final String sensor = "CONTEXT_SOURCE";
	private int counter;

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
	 * Sets the Context Broker service reference
	 * 
	 * @param ctxBroker
	 *            the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ContextSourceManagement() {
		this.newDeviceListener = new NewDeviceListener(deviceManager);
		newDeviceListener.run();
		LOG.info("{}", "CSM started");

	}

	// TODO replace with new interface
	@Override
	public void register(String name, String contextType,
			ICtxSourceMgrCallback source) {
		register(name, contextType);

	}

	@Async
	public Future<String> register(String name, String contextType) {
		if (ctxBroker == null) {
			LOG.error("Could not register " + contextType
					+ ": Context Broker cannot be found");
			return null;
		}

		String id = name + counter++; // TODO interface with IDs provided by
										// Device Manager

		try {
			Future<List<CtxEntityIdentifier>> shadowEntitiesFuture = ctxBroker
					.lookupEntities(sensor, "CtxSourceId", null, null);
			List<CtxEntityIdentifier> shadowEntities = shadowEntitiesFuture
					.get();
			
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
			Future<CtxAttribute> nameAttrFuture = ctxBroker.createAttribute(fooEnt.getId(),
					"CtxSourceId");
			CtxAttribute nameAttr = nameAttrFuture.get();
			//nameAttr.setStringValue(id);
			ctxBroker.updateAttribute(nameAttr.getId(), id);

			Future<CtxAttribute> ctxTypeAttrFuture = ctxBroker.createAttribute(fooEnt.getId(),
					"CtxType");
			CtxAttribute ctxTypeAttr = ctxTypeAttrFuture.get();
			ctxBroker.updateAttribute(ctxTypeAttr.getId(), contextType);

			LOG.debug("Created entity: " + fooEnt);
		} catch (CtxException e) {
			LOG.error(e.getMessage());
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		} catch (ExecutionException e) {
			LOG.error(e.getMessage());
		}


		return new AsyncResult<String> (id);
	}

	@Override
	public void sendUpdate(String arg0, Serializable arg1) {
		sendUpdate(arg0, arg1, null);
	}

	@Override
	public void sendUpdate(String identifier, Serializable data, CtxEntity owner) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(String arg0) {
		unregisterFuture(arg0);
	}
		
	//TODO change accoring to revised API
	@Async
	public Future<Boolean> unregisterFuture(String identifier){
		if (ctxBroker == null) {
			LOG.error("Could not unregister " + identifier
					+ ": Context Broker cannot be found");
    		return new AsyncResult<Boolean>(false);
		}

		Future<List<CtxEntityIdentifier>> shadowEntitiesFuture;
    	List<CtxEntityIdentifier> shadowEntities;
    	CtxIdentifier shadowEntity = null;
		try {
			shadowEntitiesFuture = ctxBroker.lookupEntities(sensor, "CtxSourceId", identifier, identifier);
			shadowEntities = shadowEntitiesFuture.get();
	    	if (shadowEntities.size()>1){
	    		LOG.debug("Sensor-ID "+identifier+" is not unique. Sensor could not be unregistered");
	    		return new AsyncResult<Boolean>(false);
	    		//throw new Exception("Unregistering failure due to ambiguity.");
	    	}
	    	else if (shadowEntities.isEmpty()){
	    		LOG.debug("Sensor-ID "+identifier+" is not available. Sensor could not be unregistered");
	    		return new AsyncResult<Boolean>(false);
	    		//throw new Exception("Unregistering failure due to missing Registration.");
	    	}
	    	else
	    		shadowEntity = shadowEntities.get(0);

	    	ctxBroker.remove(shadowEntity);
		} catch (CtxException e) {
			//e.printStackTrace();
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
