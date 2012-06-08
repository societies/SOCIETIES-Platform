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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.context.broker.ICtxBroker;
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
public class ExternalCtxBrokerExample 	{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ExternalCtxBrokerExample.class);

	/** The Internal Context Broker service reference. */
	private ICtxBroker externalCtxBroker;

	private IIdentity cssOwnerId;
	private INetworkNode cssNodeId;

	private CtxEntityIdentifier ctxEntityIdentifier = null;
	private CtxIdentifier ctxAttributeStringIdentifier = null;
	private CtxIdentifier ctxAttributeBinaryIdentifier = null;
	private IIdentity targetCss = null;
	private Requestor requestor = null;


	@Autowired(required=true)
	public ExternalCtxBrokerExample(ICtxBroker externalCtxBroker, ICommManager commMgr) throws InvalidFormatException {

		this.externalCtxBroker = externalCtxBroker;
		LOG.info("*** CtxBrokerExample instantiated "+ this.externalCtxBroker);
		
		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);

		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);
		this.targetCss = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		this.requestor = new Requestor(this.targetCss);

		LOG.info("*** Starting examples...");
		//this.retrieveCssOperator();
		//this.retrieveCssNode();
		this.createContext();
		//this.registerForContextChanges();
		this.retrieveContext();
		this.lookupContext();
		this.simpleCtxHistoryTest();
		//this.tuplesCtxHistoryTest();
	}

	private void retrieveAdministratingCSS() {

		LOG.info("*** retrieveCssOperator");
		try {
			CtxEntity ctxentity = this.externalCtxBroker.createEntity(requestor, targetCss, "device").get();
			LOG.info("*** CSS Operator context entity id: " + ctxentity.getId());

		} catch (Exception e) {
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}


	/**
	 */
	private void createContext(){

		LOG.info("*** createContext");

		//create ctxEntity of type "Device"
		CtxEntity ctxEntity;
		try {
			ctxEntity = this.externalCtxBroker.createEntity(requestor, targetCss,CtxEntityTypes.DEVICE).get();

			//get the context identifier of the created entity (to be used at the next step)
			this.ctxEntityIdentifier = ctxEntity.getId();

			//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
			CtxAttribute ctxAttributeString = this.externalCtxBroker.createAttribute(requestor, this.ctxEntityIdentifier, CtxAttributeTypes.ID).get();

			// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
			ctxAttributeString.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttributeString.setStringValue("device1234");

			// with this update the attribute is stored in Context DB
			ctxAttributeString = (CtxAttribute) this.externalCtxBroker.update(requestor,ctxAttributeString).get();

			// get the updated CtxAttribute object and identifier (to be used later for retrieval purposes)
			this.ctxAttributeStringIdentifier = ctxAttributeString.getId();
			
			//create a ctxAttribute with a Binary value that is assigned to the same CtxEntity
			CtxAttribute ctxAttrBinary = this.externalCtxBroker.createAttribute(requestor,ctxEntity.getId(),CtxAttributeTypes.WEIGHT).get();


			// this is a mock blob class that contains the value "999"
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes;
			try {
				blobBytes = SerialisationHelper.serialise(blob);
				ctxAttrBinary.setBinaryValue(blobBytes);
				ctxAttrBinary = (CtxAttribute) this.externalCtxBroker.update(requestor, ctxAttrBinary).get();

				// TODO create updateAttribute(requestor,modelObject,value);

				this.ctxAttributeBinaryIdentifier = ctxAttrBinary.getId();
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// at this point the ctxEntity of type CtxEntityTypes.DEVICE that is assigned with
			// a ctxAttribute of type CtxAttributeTypes.ID with a string value
			// and a ctxAttribute of type "CustomData" with a binary value
			LOG.info("*** created attribute toString "+ctxAttrBinary.toString());
		} catch (Exception e) {

			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	
		
	}

	/**
	 * This method demonstrates how to retrieve context data from the context database
	 */
	private void lookupContext() {
		try {

			List<CtxIdentifier> idsEntities =this.externalCtxBroker.lookup(requestor, targetCss, CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			LOG.info("*** lookup results for Entity type: '" + CtxEntityTypes.DEVICE + "' " +idsEntities);

			List<CtxIdentifier> idsAttribute =this.externalCtxBroker.lookup(requestor, targetCss,CtxModelType.ATTRIBUTE, CtxAttributeTypes.ID).get();
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
			Future<CtxModelObject> ctxEntityRetrievedFuture = this.externalCtxBroker.retrieve(requestor,this.ctxEntityIdentifier);
			CtxEntity retrievedCtxEntity = (CtxEntity) ctxEntityRetrievedFuture.get();

			LOG.info("Retrieved ctxEntity id " +retrievedCtxEntity.getId()+ " of type: "+retrievedCtxEntity.getType());

			// retrieve the CtxAttribute contained in the CtxEntity with the string value
			// again the retrieval is based on an known identifier, it is possible to retrieve it based on type.This will be demonstrated in a later example.
			CtxAttribute retrievedCtxAttribute = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, this.ctxAttributeStringIdentifier).get();
			
			LOG.info("Retrieved ctxAttribute id " +retrievedCtxAttribute.getId()+ " and value: "+retrievedCtxAttribute.getStringValue());

			// retrieve ctxAttribute with the binary value
			Future<CtxModelObject> ctxAttributeRetrievedBinaryFuture = this.externalCtxBroker.retrieve(requestor, this.ctxAttributeBinaryIdentifier);
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

		final CtxEntity ctxDevEntity;
		// Create the attribute's scope

		try {
			ctxDevEntity = this.externalCtxBroker.createEntity(requestor,targetCss, CtxEntityTypes.DEVICE).get();

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
				System.out.println("history List id:"+hocAttr.getId()+" getLastMod:"+hocAttr.getLastModified() +" hocAttr value:"+hocAttr.getIntegerValue());		
			}

			//test createHistory methods
			CtxAttribute fakeAttribute = this.externalCtxBroker.createAttribute(requestor, ctxDevEntity.getId(), "historyAttribute").get();
			List<CtxHistoryAttribute> historyList = new ArrayList<CtxHistoryAttribute>();

			List<CtxHistoryAttribute> historyListRetrieved = this.externalCtxBroker.retrieveHistory(requestor, fakeAttribute.getId(), null, null).get();
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

		/*
		try {
			ctxEntity = (CtxEntity) this.externalCtxBroker.createEntity(requestor,targetCss,"entType").get();

			// Create the attribute to be tested
			primaryAttribute = (CtxAttribute) this.externalCtxBroker.createAttribute(requestor,ctxEntity.getId(), "primaryAttribute").get();
			primaryAttribute.setHistoryRecorded(true);
			primaryAttribute.setStringValue("fistValue");
			this.externalCtxBroker.update(primaryAttribute);

			escortingAttribute1 = (CtxAttribute) this.externalCtxBroker.createAttribute(requestor,ctxEntity.getId(), "escortingAttribute1").get();
			escortingAttribute2 = (CtxAttribute) this.externalCtxBroker.createAttribute(requestor,ctxEntity.getId(), "escortingAttribute2").get();

			
			escortingAttribute1 =  this.externalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingAttribute1_firstValue").get();
			escortingAttribute2 =  this.externalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingAttribute2_firstValue").get();

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
*/
	}

	

	/*
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
	 */
	/**
	 * This method demonstrates how to register for context change events in the context database
	 */
	private void registerForContextChanges() {

		LOG.info("*** registerForContextChanges");

		try {
			// 1a. Register listener by specifying the context attribute identifier
			this.externalCtxBroker.registerForChanges(requestor, new MyCtxChangeEventListener(), this.ctxAttributeStringIdentifier);

			// 1b. Register listener by specifying the context attribute scope and type
			this.externalCtxBroker.registerForChanges(requestor,new MyCtxChangeEventListener(), this.ctxEntityIdentifier, CtxAttributeTypes.ID);

			// 2. Update attribute to see some event action
			CtxAttribute ctxAttr = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, this.ctxAttributeStringIdentifier).get();

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