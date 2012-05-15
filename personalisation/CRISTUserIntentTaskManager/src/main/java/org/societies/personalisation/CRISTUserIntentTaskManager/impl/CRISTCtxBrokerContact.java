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
package org.societies.personalisation.CRISTUserIntentTaskManager.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is responsible to communicate with CtxDB using CtxBroker in OSGi on behalf of CRIST.
 * 
 * @author Zhiyong Yu
 * 
 */

public class CRISTCtxBrokerContact {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private ICtxBroker internalCtxBroker;
	private IndividualCtxEntity operator;

	@Autowired(required = true)
	public CRISTCtxBrokerContact() {

		LOG.info("CRISTCtxBrokerContact instantiated");
		if (this.internalCtxBroker == null) {
			LOG.error("ctxBroker is null");
			return;
		}

		try {
			operator = this.internalCtxBroker
					.retrieveCssOperator().get();
			LOG.info(" operator: " + operator);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}

		CtxAttributeIdentifier ctxAttributeIdentifier = this.createContext("Device", "DeviceID", "device1234");
		String stringValue = this.retrieveContext(ctxAttributeIdentifier);
		LOG.info(ctxAttributeIdentifier + ": " + stringValue);
		List<CtxIdentifier> idsEntities = this.lookupContextEntity("Device");
		LOG.info("lookup results for Entity type: 'Device' " + idsEntities);
		List<CtxIdentifier> idsAttribute = this.lookupContextAttr("DeviceID");
		LOG.info("lookup results for Attribute type: 'DeviceID' " + idsAttribute);
		//this.registerForContextChangeEvent();


	}

	/**
	 * At this point a CtxEntity of type "Device" is created with an attribute
	 * of type "DeviceID" with a string value "device1234".
	 * create is different to update, how to update?
	 * 
	 */
	public CtxAttributeIdentifier createContext(String ctxEntityType, String ctxAttrType, String ctxAttrValue) {

		LOG.info("*** createContext");

		// create ctxEntity of type "Device"
		Future<CtxEntity> futureEnt;
		try {
			futureEnt = this.internalCtxBroker.createEntity(ctxEntityType);
			CtxEntity ctxEntity = (CtxEntity) futureEnt.get();

			// get the context identifier of the created entity (to be used at
			// the next step)
			CtxEntityIdentifier ctxEntityIdentifier = ctxEntity.getId();

			// create ctxAttribute with a String value that it is assigned to
			// the previously created ctxEntity
			Future<CtxAttribute> futureCtxAttrString = this.internalCtxBroker
					.createAttribute(ctxEntityIdentifier, ctxAttrType);
			// get the object of the created CtxAttribute
			CtxAttribute ctxAttribute = (CtxAttribute) futureCtxAttrString
					.get();

			// by setting this flag to true the CtxAttribute values will be
			// stored to Context History Database upon update
			ctxAttribute.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttribute.setStringValue(ctxAttrValue);

			// with this update the attribute is stored in Context DB
			Future<CtxModelObject> futureAttrUpdated = this.internalCtxBroker
					.update(ctxAttribute);

			// get the updated CtxAttribute object and identifier (to be used
			// later for retrieval purposes)
			CtxAttribute updatedCtxAttribute = (CtxAttribute) futureAttrUpdated.get();
			
			return updatedCtxAttribute.getId();


		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method demonstrates how to look up context Ids from the context
	 * database
	 */
	
	private List<CtxIdentifier> lookupContextEntity(String ctxEntityType) {
		try {

			List<CtxIdentifier> idsEntities = this.internalCtxBroker.lookup(
					CtxModelType.ENTITY, ctxEntityType).get();
			return idsEntities;

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<CtxIdentifier> lookupContextAttr(String ctxAttrType) {
		try {
			List<CtxIdentifier> idsAttribute = this.internalCtxBroker.lookup(
					CtxModelType.ATTRIBUTE, ctxAttrType).get();
			return idsAttribute;

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method demonstrates how to retrieve context data from the context
	 * database
	 */
	public String retrieveContext(CtxAttributeIdentifier ctxAttributeIdentifier) {

		LOG.info("*** retrieveContext");

		try {

			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.internalCtxBroker
					.retrieve(ctxAttributeIdentifier);
			CtxAttribute retrievedCtxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture
					.get();
			return retrievedCtxAttribute.getStringValue();

		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CtxAttribute retrieveContextAttr(CtxAttributeIdentifier ctxAttributeIdentifier)
	{
		try {

			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.internalCtxBroker
					.retrieve(ctxAttributeIdentifier);
			return (CtxAttribute) ctxAttributeRetrievedStringFuture.get();

		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onCreation
		 * (org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** CREATED event ***");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onModification
		 * (org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onRemoval(
		 * org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** REMOVED event ***");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onUpdate(org
		 * .societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** UPDATED event ***");
		}
	}
	
	
	
	
	

	public ICtxBroker getCtxBroker() {
		System.out.println(this.getClass().getName() + ": Return ctxBroker");
		return internalCtxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		System.out.println(this.getClass().getName() + ": Got ctxBroker");
		this.internalCtxBroker = ctxBroker;
	}
	
	//internalCtxBroker.registerForChanges
	void registerForContextChangeEvent(){



		CtxAttributeIdentifier uiModelAttributeId = null;
		IndividualCtxEntity operator;
		try {
			operator = this.internalCtxBroker.retrieveCssOperator().get();
			List<CtxIdentifier> ls = this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CRIST_MODEL).get();
			if (ls.size()>0) {
				uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
			} else {
				CtxAttribute attr = this.internalCtxBroker.createAttribute(operator.getId(), CtxAttributeTypes.CRIST_MODEL).get();
				uiModelAttributeId = attr.getId();
			}
			if (uiModelAttributeId != null){
				this.internalCtxBroker.registerForChanges(new MyCtxChangeEventListener(),uiModelAttributeId);	
			}		

			LOG.info("registration for context attribute updates of type "+uiModelAttributeId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}			
	}
	
	
	
	
	
	
	

}
