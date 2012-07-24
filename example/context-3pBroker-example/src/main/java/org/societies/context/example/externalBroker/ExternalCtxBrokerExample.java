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
package org.societies.context.example.externalBroker;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueMetrics;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class ExternalCtxBrokerExample 	{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ExternalCtxBrokerExample.class);

	/** The 3P Context Broker service reference. */
	private ICtxBroker externalCtxBroker;

	private IIdentity cssOwnerId;
	private INetworkNode cssNodeId;

	private static IndividualCtxEntity owner = null;
	private static CtxEntity deviceCtxEntity;
	private static CtxAssociation usesServiceAssoc;

	private static CtxAttributeIdentifier weightAttrIdentifier = null;
	private static CtxAttributeIdentifier ctxAttributeDeviceIDIdentifier = null;

	private Requestor requestor = null;
	private IIdentity remoteTargetCss;

	@Autowired(required=true)
	public ExternalCtxBrokerExample(ICtxBroker externalCtxBroker, ICommManager commMgr) throws InvalidFormatException {

		LOG.info("*** " + this.getClass() + " instantiated");
		
		this.externalCtxBroker = externalCtxBroker;

		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);

		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);

		//IIdentity reqIdentity = commMgr.getIdManager().fromJid("foo@societies.local");
		this.requestor = new Requestor(this.cssOwnerId);
		LOG.info("*** requestor = " + this.requestor);

		// TODO this.remoteTargetCss = commMgr.getIdManager().fromJid("BOOO@societies.local");
		
		LOG.info("*** Starting examples...");
		// TODO createRemoteEntity();
		this.retrieveIndividualEntityId();
		this.createDeviceEntity();
		//createCtxAssociation();
		//registerForContextChanges();
		this.lookupContextEntities();
		this.retrieveContext();
		//simpleCtxHistoryTest();
	}



	private void createRemoteEntity() {
		LOG.info("*** createRemoteEntity");

		try {
			LOG.info("remote id :"+ this.remoteTargetCss);
			CtxEntity remoteEntity = this.externalCtxBroker.createEntity(requestor, this.remoteTargetCss, "remoteType").get();

			LOG.info("remote Entity created : "+remoteEntity );
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
	}

	private void retrieveIndividualEntityId() {
		
		LOG.info("*** retrieveIndividualEntityId");
		try {
			CtxEntityIdentifier cssOwnerEntityId = 
					this.externalCtxBroker.retrieveIndividualEntityId(this.requestor, this.cssOwnerId).get();
			LOG.info("*** Retrieved CSS owner context entity id " + cssOwnerEntityId);
		} catch (Exception e) {
			
			LOG.error("3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 */
	private void createDeviceEntity(){

		LOG.info("*** createDeviceEntity");
		//create ctxEntity of type "Device"
		try {
			deviceCtxEntity = this.externalCtxBroker.createEntity(requestor, cssOwnerId, CtxEntityTypes.DEVICE).get();
			//get the context identifier of the created entity (to be used at the next step)
			CtxEntityIdentifier deviceCtxEntityIdentifier = deviceCtxEntity.getId();

			//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
			CtxAttribute ctxAttributeDeviceID = this.externalCtxBroker.createAttribute(requestor, deviceCtxEntityIdentifier, CtxAttributeTypes.ID).get();

			// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
			ctxAttributeDeviceID.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttributeDeviceID.setStringValue("device1234");

			// with this update the attribute is stored in Context DB
			ctxAttributeDeviceID = (CtxAttribute) this.externalCtxBroker.update(requestor, ctxAttributeDeviceID).get();

			// get the updated CtxAttribute object and identifier (to be used later for retrieval purposes)

			ctxAttributeDeviceIDIdentifier = ctxAttributeDeviceID.getId();
			LOG.info("*** Device attribute identifier "+ctxAttributeDeviceIDIdentifier.toString());

			//create a ctxAttribute with a Binary value that is assigned to the deviceCtxEntity
			CtxAttribute ctxAttrWeight = this.externalCtxBroker.createAttribute(requestor, deviceCtxEntity.getId(),CtxAttributeTypes.WEIGHT).get();

			// this is a mock blob class that contains the value "999"
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes;
			try {
				blobBytes = SerialisationHelper.serialise(blob);
				ctxAttrWeight.setBinaryValue(blobBytes);
				ctxAttrWeight = (CtxAttribute) this.externalCtxBroker.update(requestor, ctxAttrWeight).get();
				// TODO create updateAttribute(requestor,modelObject,value);
				weightAttrIdentifier = ctxAttrWeight.getId();
				LOG.info("*** Weight attribute identifier "+ctxAttrWeight.getId().toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// create an attribute to model the temperature of the device
			CtxAttribute deviceTempAttr = this.externalCtxBroker.createAttribute(requestor, deviceCtxEntity.getId(), CtxAttributeTypes.TEMPERATURE).get();

			// assign a double value and set value type and metric
			deviceTempAttr.setDoubleValue(25.0);
			deviceTempAttr.setValueType(CtxAttributeValueType.DOUBLE);
			deviceTempAttr.setValueMetric(CtxAttributeValueMetrics.CELSIUS);

			// update the attribute in the Context DB
			deviceTempAttr = (CtxAttribute) this.externalCtxBroker.update(requestor, deviceTempAttr).get();

			// at this point the ctxEntity of type CtxEntityTypes.DEVICE is assigned with CtxAttributes of type : ID, WEIGHT, TEMPERATURE
			LOG.info("*** created attribute toString "+deviceTempAttr.toString());
		} catch (Exception e) {
			LOG.error("*** 3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}
	}


	/**
	 * This method demonstrates how to create an association among context entities
	 */
	private void createCtxAssociation(){

		LOG.info("usesServiceAssoc 1 "+usesServiceAssoc);
		try {
			usesServiceAssoc = this.externalCtxBroker.createAssociation(requestor, cssOwnerId, CtxAssociationTypes.USES_SERVICES).get();
			LOG.info("usesServiceAssoc 2 "+usesServiceAssoc);
			LOG.info("usesServiceAssoc 3 "+owner);
			LOG.info("usesServiceAssoc 4 "+deviceCtxEntity);

			usesServiceAssoc.addChildEntity(owner.getId());
			usesServiceAssoc.addChildEntity(deviceCtxEntity.getId());

			LOG.info("usesServiceAssoc 3 "+usesServiceAssoc);

			this.externalCtxBroker.update(requestor, usesServiceAssoc);
			LOG.info("usesServiceAssoc 4 "+usesServiceAssoc);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		} 
	}	

	/**
	 * This method demonstrates how to retrieve context data from the context database
	 */
	private void lookupContextEntities() {
		try {
			// search for ctxEntity Ids of type device
			List<CtxIdentifier> idsEntities = this.externalCtxBroker.lookup(requestor, cssOwnerId, CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			LOG.info("*** lookup results for Entity type: '" + CtxEntityTypes.DEVICE + "' " +idsEntities);

			// search for ctxAttribute Ids of type ID
			List<CtxIdentifier> idsAttribute =this.externalCtxBroker.lookup(requestor, cssOwnerId,CtxModelType.ATTRIBUTE, CtxAttributeTypes.ID).get();
			LOG.info("*** lookup results for Attribute type: '" + CtxAttributeTypes.ID + "' " +idsAttribute);
		} catch (Exception e) {
			LOG.error("*** 3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This method demonstrates how to retrieve context data from the context database
	 */
	private void retrieveContext() {

		LOG.info("*** retrieveContext");
		// if the CtxEntityID or CtxAttributeID is known the retrieval is performed by using the ctxBroker.retrieve(CtxIdentifier) method
		// alternatively context identifiers can be retrieved with the help of lookup mehtods
		CtxEntityIdentifier deviceCtxEntIdentifier = null;
		try {
			List<CtxIdentifier> idsEntities = this.externalCtxBroker.lookup(requestor, cssOwnerId, CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			if(idsEntities.size()>0){
				deviceCtxEntIdentifier = (CtxEntityIdentifier) idsEntities.get(0);
			}
			// the retrieved identifier is used in order to retrieve the context model object (CtxEntity)
			CtxEntity retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, deviceCtxEntIdentifier).get();

			LOG.info("Retrieved ctxEntity id " +retrievedCtxEntity.getId()+ " of type: "+retrievedCtxEntity.getType());

			// Retrieve CtxAttributes assigned to retrievedCtxEntity 
			Set<CtxAttribute> ctxAttrSet = retrievedCtxEntity.getAttributes(CtxAttributeTypes.ID);

			for (final CtxAttribute ctxAttr : ctxAttrSet)
				LOG.info("Resoleved ctxAttribute id " + ctxAttr.getId() 
						+ " and value: " + ctxAttr.getStringValue() 
						+ " initial value was device1234");
			
			// retrieve ctxAttribute with the binary value based on a known identifier
			CtxAttribute ctxAttributeWeight = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, weightAttrIdentifier).get();

			//deserialise object
			MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(ctxAttributeWeight.getBinaryValue(), this.getClass().getClassLoader());
			LOG.info("Retrieved ctxAttribute id " +ctxAttributeWeight.getId()+ "and value: "+ retrievedBlob.getSeed()+" initial value was 999 ");
		} catch (Exception e) {
			LOG.error("*** 3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This method demonstrates how to create and retrieve context history data
	 */
	private void simpleCtxHistoryTest() {

		CtxAttribute ctxAttribute;
		final CtxEntity ctxDevEntity;
		// Create the attribute's scope
		try {
			ctxDevEntity = this.externalCtxBroker.createEntity(requestor,cssOwnerId, CtxEntityTypes.DEVICE).get();

			// Create the attribute to be tested
			ctxAttribute = this.externalCtxBroker.createAttribute(requestor, ctxDevEntity.getId(), "attrType").get();
			ctxAttribute.setHistoryRecorded(true);

			ctxAttribute.setIntegerValue(100);
			ctxAttribute = (CtxAttribute) this.externalCtxBroker.update(requestor,ctxAttribute).get();

			ctxAttribute.setIntegerValue(200);
			ctxAttribute = (CtxAttribute) this.externalCtxBroker.update(requestor,ctxAttribute).get();

			ctxAttribute.setIntegerValue(300);
			ctxAttribute = (CtxAttribute) this.externalCtxBroker.update(requestor,ctxAttribute).get();

			List<CtxHistoryAttribute> history = this.externalCtxBroker.retrieveHistory(requestor, ctxAttribute.getId(), null, null).get();

			for(CtxHistoryAttribute hocAttr: history){
				LOG.info("history List id:"+hocAttr.getId()+" getLastMod:"+hocAttr.getLastModified() +" hocAttr value:"+hocAttr.getIntegerValue());		
			}
			//test createHistory methods
			CtxAttribute fakeAttribute = this.externalCtxBroker.createAttribute(requestor, ctxDevEntity.getId(), "historyAttribute").get();

			List<CtxHistoryAttribute> historyListRetrieved = this.externalCtxBroker.retrieveHistory(requestor, fakeAttribute.getId(), null, null).get();
			if(historyListRetrieved.equals(historyListRetrieved)) System.out.println("Succesfull Retrieval of created hoc Attributes");

			for (CtxHistoryAttribute ctxHistAttr : historyListRetrieved){
				LOG.info("Hoc attribute value:" +ctxHistAttr.getStringValue()+" time:"+ctxHistAttr.getLastModified().getTime());
			}
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
	}

	/**
	 * This method demonstrates how to register for context change events in the context database
	 */
	private void registerForContextChanges() {

		LOG.info("*** registerForContextChanges");

		try {
			// 1a. Register listener by specifying the context attribute identifier
			this.externalCtxBroker.registerForChanges(requestor, new MyCtxChangeEventListener(), ctxAttributeDeviceIDIdentifier);

			// 1b. Register listener by specifying the context attribute scope and type
			this.externalCtxBroker.registerForChanges(requestor,new MyCtxChangeEventListener(), deviceCtxEntity.getId(), CtxAttributeTypes.ID);

			// 2. Update attribute to see some event action
			CtxAttribute ctxAttr = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAttributeDeviceIDIdentifier).get();

			ctxAttr.setStringValue("newDeviceIdValue");
			ctxAttr = (CtxAttribute) this.externalCtxBroker.update(requestor,ctxAttr).get();

		} catch (CtxException ce) {
			LOG.error("*** CM sucks " + ce.getLocalizedMessage(), ce);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** CREATED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** REMOVED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** UPDATED event ***");
		}
	}
}