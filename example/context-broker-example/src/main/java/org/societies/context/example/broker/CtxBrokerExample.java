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
package org.societies.context.example.broker;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class CtxBrokerExample 	{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerExample.class);

	/** The Internal Context Broker service reference. */
	private ICtxBroker internalCtxBroker;
	
	private IIdentity cssOwnerId;
	private INetworkNode cssNodeId;

	private CtxEntityIdentifier ctxEntityIdentifier = null;
	private CtxIdentifier ctxAttributeStringIdentifier = null;
	private CtxIdentifier ctxAttributeBinaryIdentifier = null;

	@Autowired(required=true)
	public CtxBrokerExample(ICtxBroker internalCtxBroker, ICommManager commMgr) throws InvalidFormatException {

		LOG.info("*** CtxBrokerExample instantiated");
		this.internalCtxBroker = internalCtxBroker;
		
		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);
		
		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);

		LOG.info("*** Starting examples...");
		this.retrieveIndividualEntity();
		this.retrieveCssNode();
		this.createContext();
		this.registerForContextChanges();
		this.retrieveContext();
		this.lookupContext();
		this.simpleCtxHistoryTest();
		this.tuplesCtxHistoryTest();
	}
	
	private void retrieveIndividualEntity() {
		
		LOG.info("*** retrieveIndividualEntity");
		
		try {
			final IndividualCtxEntity operator = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
			LOG.info("*** CSS owner context entity id: " + operator.getId());
		
		} catch (Exception e) {
			
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}
	
	private void retrieveCssNode() {
		
		LOG.info("*** retrieveCssNode");
		
		try {
			CtxEntity cssNodeEnt = this.internalCtxBroker.retrieveCssNode(this.cssNodeId).get();
			LOG.info("*** CSS node context entity id: " + cssNodeEnt.getId());
		
		} catch (Exception e) {
			
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * At this point a CtxEntity of type {@link CtxEntityTypes.DEVICE} is created with an attribute
	 * of type {@link CtxAttributeTypes.ID} with a string value "device1234".
	 */
	private void createContext(){

		LOG.info("*** createContext");

		//create ctxEntity of type "Device"
		Future<CtxEntity> futureEnt;
		try {
			futureEnt = this.internalCtxBroker.createEntity(CtxEntityTypes.DEVICE);
			CtxEntity ctxEntity = (CtxEntity) futureEnt.get();

			//get the context identifier of the created entity (to be used at the next step)
			this.ctxEntityIdentifier = ctxEntity.getId();

			//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
			Future<CtxAttribute> futureCtxAttrString = this.internalCtxBroker.createAttribute(this.ctxEntityIdentifier, CtxAttributeTypes.ID);
			// get the object of the created CtxAttribute
			CtxAttribute ctxAttributeString = (CtxAttribute) futureCtxAttrString.get();

			// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
			ctxAttributeString.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttributeString.setStringValue("device1234");

			// with this update the attribute is stored in Context DB
			Future<CtxModelObject> futureAttrUpdated = this.internalCtxBroker.update(ctxAttributeString);

			// get the updated CtxAttribute object and identifier (to be used later for retrieval purposes)
			ctxAttributeString = (CtxAttribute) futureAttrUpdated.get();
			this.ctxAttributeStringIdentifier = ctxAttributeString.getId();


			//create a ctxAttribute with a Binary value that is assigned to the same CtxEntity
			Future<CtxAttribute> futureCtxAttrBinary = this.internalCtxBroker.createAttribute(ctxEntity.getId(), "CustomData");
			CtxAttribute ctxAttrBinary = (CtxAttribute) futureCtxAttrBinary.get();

			// this is a mock blob class that contains the value "999"
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes;
			try {
				blobBytes = SerialisationHelper.serialise(blob);

				Future<CtxAttribute> futureCtxAttrBinaryUpdated = this.internalCtxBroker.updateAttribute(ctxAttrBinary.getId(), blobBytes);
				ctxAttrBinary = (CtxAttribute) futureCtxAttrBinaryUpdated.get();

				this.ctxAttributeBinaryIdentifier = ctxAttrBinary.getId();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// at this point the ctxEntity of type CtxEntityTypes.DEVICE that is assigned with
			// a ctxAttribute of type CtxAttributeTypes.ID with a string value
			// and a ctxAttribute of type "CustomData" with a binary value

		} catch (Exception e) {
			
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This method demonstrates how to retrieve context data from the context database
	 */
	private void lookupContext() {
		try {

			List<CtxIdentifier> idsEntities =this.internalCtxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			LOG.info("*** lookup results for Entity type: '" + CtxEntityTypes.DEVICE + "' " +idsEntities);

			List<CtxIdentifier> idsAttribute =this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.ID).get();
			LOG.info("*** lookup results for Attribute type: '" + CtxAttributeTypes.ID + "' " +idsAttribute);

		} catch (Exception e) {
			
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}



	/**
	 * This method demonstrates how to retrieve context data from the context database
	 */
	private void retrieveContext() {

		LOG.info("*** retrieveContext");

		// if the CtxEntityID or CtxAttributeID is known the retrieval is performed by using the ctxBroker.retrieve(CtxIdentifier) method
		try {
			// retrieve ctxEntity
			// This retrieval is performed based on the known CtxEntity identifier
			// Retrieve is also possible to be performed based on the type of the CtxEntity. This will be demonstrated in a later example.
			Future<CtxModelObject> ctxEntityRetrievedFuture = this.internalCtxBroker.retrieve(this.ctxEntityIdentifier);
			CtxEntity retrievedCtxEntity = (CtxEntity) ctxEntityRetrievedFuture.get();

			LOG.info("Retrieved ctxEntity id " +retrievedCtxEntity.getId()+ " of type: "+retrievedCtxEntity.getType());

			// retrieve the CtxAttribute contained in the CtxEntity with the string value
			// again the retrieval is based on an known identifier, it is possible to retrieve it based on type.This will be demonstrated in a later example.
			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.internalCtxBroker.retrieve(this.ctxAttributeStringIdentifier);
			CtxAttribute retrievedCtxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture.get();
			LOG.info("Retrieved ctxAttribute id " +retrievedCtxAttribute.getId()+ " and value: "+retrievedCtxAttribute.getStringValue());

			// retrieve ctxAttribute with the binary value
			Future<CtxModelObject> ctxAttributeRetrievedBinaryFuture = this.internalCtxBroker.retrieve(this.ctxAttributeBinaryIdentifier);
			CtxAttribute ctxAttributeRetrievedBinary = (CtxAttribute) ctxAttributeRetrievedBinaryFuture.get();

			//deserialise object
			MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(ctxAttributeRetrievedBinary.getBinaryValue(), this.getClass().getClassLoader());
			LOG.info("Retrieved ctxAttribute id " +ctxAttributeRetrievedBinary.getId()+ "and value: "+ retrievedBlob.toString());

		} catch (Exception e) {
			
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}

	}



	/**
	 * This method demonstrates how to create and retrieve context history data
	 */
	private void simpleCtxHistoryTest() {

		CtxAttribute ctxAttribute;
		
		final CtxEntity ctxEntity;
		// Create the attribute's scope

		try {
			ctxEntity = internalCtxBroker.createEntity("entType").get();

			// Create the attribute to be tested
			ctxAttribute = internalCtxBroker.createAttribute(ctxEntity.getId(), "attrType").get();
			ctxAttribute.setHistoryRecorded(true);

			ctxAttribute.setIntegerValue(100);
			ctxAttribute = (CtxAttribute) internalCtxBroker.update(ctxAttribute).get();

			ctxAttribute.setIntegerValue(200);
			ctxAttribute = (CtxAttribute) internalCtxBroker.update(ctxAttribute).get();

			ctxAttribute.setIntegerValue(300);
			ctxAttribute = (CtxAttribute) internalCtxBroker.update(ctxAttribute).get();

			List<CtxHistoryAttribute> history = internalCtxBroker.retrieveHistory(ctxAttribute.getId(), null, null).get();

			for(CtxHistoryAttribute hocAttr: history){
				System.out.println("history List id:"+hocAttr.getId()+" getLastMod:"+hocAttr.getLastModified() +" hocAttr value:"+hocAttr.getIntegerValue());		
			}
			
			//test createHistory methods
			CtxAttribute fakeAttribute = internalCtxBroker.createAttribute(ctxEntity.getId(), "historyAttribute").get();
			List<CtxHistoryAttribute> historyList = new ArrayList<CtxHistoryAttribute>();
			
			Date date = new Date();
			date.setTime(1000);
			CtxHistoryAttribute hocAttr1 = internalCtxBroker.createHistoryAttribute(fakeAttribute.getId(), date, "one", CtxAttributeValueType.STRING).get();
			date.setTime(2000);
			CtxHistoryAttribute hocAttr2 = internalCtxBroker.createHistoryAttribute(fakeAttribute.getId(), date, "two", CtxAttributeValueType.STRING).get();
			date.setTime(3000);
			CtxHistoryAttribute hocAttr3 = internalCtxBroker.createHistoryAttribute(fakeAttribute.getId(), date, "three", CtxAttributeValueType.STRING).get();
			historyList.add(hocAttr1);
			historyList.add(hocAttr2);
			historyList.add(hocAttr3);
			
			List<CtxHistoryAttribute> historyListRetrieved = internalCtxBroker.retrieveHistory(fakeAttribute.getId(), null, null).get();
			if(historyListRetrieved.equals(historyListRetrieved)) System.out.println("Succesfull Retrieval of created hoc Attributes");
			
			for (CtxHistoryAttribute ctxHistAttr : historyListRetrieved){
				System.out.println("Hoc attribute value:" +ctxHistAttr.getStringValue()+" time:"+ctxHistAttr.getLastModified().getTime());
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
	 * This method demonstrates how to create and retrieve context history data in tuples, and how to update the tuple types.
	 */
	private void tuplesCtxHistoryTest() {

		final CtxEntity ctxEntity;
		CtxAttribute primaryAttribute;
		CtxAttribute escortingAttribute1;
		CtxAttribute escortingAttribute2;
		CtxAttribute escortingAttribute3;

		try {
			ctxEntity = (CtxEntity)internalCtxBroker.createEntity("entType").get();

			// Create the attribute to be tested
			primaryAttribute = (CtxAttribute) internalCtxBroker.createAttribute(ctxEntity.getId(), "primaryAttribute").get();
			primaryAttribute.setHistoryRecorded(true);
			primaryAttribute.setStringValue("fistValue");
			internalCtxBroker.update(primaryAttribute);

			escortingAttribute1 = (CtxAttribute)internalCtxBroker.createAttribute(ctxEntity.getId(), "escortingAttribute1").get();
			escortingAttribute2 = (CtxAttribute)internalCtxBroker.createAttribute(ctxEntity.getId(), "escortingAttribute2").get();

			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingAttribute1_firstValue").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingAttribute2_firstValue").get();

			List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			listOfEscortingAttributeIds.add(escortingAttribute1.getId());
			listOfEscortingAttributeIds.add(escortingAttribute2.getId());
			internalCtxBroker.setHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds).get();	

			//this update stores also the attributes in tuples
			internalCtxBroker.update(primaryAttribute);

			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingAttribute1_secondValue").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingAttribute2_secondValue").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"secondValue").get();

			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingAttribute1_thirdValue").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingAttribute2_thirdValue").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"thirdValue").get();

			//primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"forthValue").get();
			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds, null, null).get();
			printHocTuples(tupleResults);
			System.out.println("add new attribute in an existing tuple");

			escortingAttribute3 = (CtxAttribute) internalCtxBroker.createAttribute(ctxEntity.getId(),"escortingAttribute3").get();
			//escortingAttribute3.setHistoryRecorded(true);
			List<CtxAttributeIdentifier> newlistOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			newlistOfEscortingAttributeIds.add(escortingAttribute1.getId());
			newlistOfEscortingAttributeIds.add(escortingAttribute2.getId());
			newlistOfEscortingAttributeIds.add(escortingAttribute3.getId());
			internalCtxBroker.updateHistoryTuples(primaryAttribute.getId(), newlistOfEscortingAttributeIds);

			// newly add attribute doesn't contain any value yet, a null ref should be added in tuple
			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingAttribute1_forthValue").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingAttribute2_forthValue").get();
			//escortingAttribute3 =  internalCtxBroker.updateAttribute(escortingAttribute3.getId(),(Serializable)"escortingValue3_forthValue").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"forthValue").get();

			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingAttribute1_fifthValue").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingAttribute2_fifthValue").get();
			escortingAttribute3 =  internalCtxBroker.updateAttribute(escortingAttribute3.getId(),(Serializable)"escortingAttribute3_fifthValue").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"fifthValue").get();

			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> updatedTupleResults = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds, null, null).get();
			printHocTuples(updatedTupleResults);

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

	protected void printHocTuples(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults){

		int i = 0;
		for (CtxHistoryAttribute primary : tupleResults.keySet()){
			String primaryValue = null;
			if (primary.getStringValue() != null) primaryValue =primary.getStringValue();
			String escValueTotal = "";
			for(CtxHistoryAttribute escortingAttr: tupleResults.get(primary)){
				String escValue = "";
				if (escortingAttr.getStringValue() != null )  escValue =escortingAttr.getStringValue();	
				escValueTotal = escValueTotal+" "+escValue; 
			}
			System.out.println(i+ " primaryValue: "+primaryValue+ " escValues: "+escValueTotal);
			i++;
		}
	}	

	/**
	 * This method demonstrates how to register for context change events in the context database
	 */
	private void registerForContextChanges() {

		LOG.info("*** registerForContextChanges");

		try {
			// 1a. Register listener by specifying the context attribute identifier
			this.internalCtxBroker.registerForChanges(new MyCtxChangeEventListener(), this.ctxAttributeStringIdentifier);

			// 1b. Register listener by specifying the context attribute scope and type
			this.internalCtxBroker.registerForChanges(new MyCtxChangeEventListener(), this.ctxEntityIdentifier, CtxAttributeTypes.ID);

			// 2. Update attribute to see some event action
			this.internalCtxBroker.updateAttribute((CtxAttributeIdentifier) this.ctxAttributeStringIdentifier, "newDeviceIdValue");

		} catch (CtxException ce) {

			LOG.error("*** CM sucks " + ce.getLocalizedMessage(), ce);
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