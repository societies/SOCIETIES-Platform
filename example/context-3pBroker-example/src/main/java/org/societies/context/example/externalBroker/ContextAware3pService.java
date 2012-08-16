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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeBond;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueMetrics;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBondOriginType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/*
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author NikosK
 *
 */
@Service
public class ContextAware3pService implements IContextAware3pService{


	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	//services
	private ICommManager commsMgr;
	private IIdentityManager idMgr;
	private ICtxBroker ctxBroker;
	//private IPrivacyPreferenceManager privPrefMgr;

	// identities
	private RequestorService requestorService;
	private IIdentity userIdentity;
	private IIdentity serviceIdentity;
	private ServiceResourceIdentifier myServiceID;

	//context objects
	private List<CtxAttribute> retrievedAttributes;
	private CtxAttribute nameCtxAttribute;

	private static CtxAttributeIdentifier ctxAttrDevLocationIdentifier = null;
	private static CtxEntity deviceCtxEntity;

	@Autowired(required=true)
	public ContextAware3pService( ICtxBroker ctxBroker, ICommManager commsMgr){
		
		LOG.info("*** ContextAware3pService started");

		//services
		this.ctxBroker = ctxBroker;
		this.commsMgr = commsMgr;
		this.idMgr = commsMgr.getIdManager();
		
		LOG.info("ctxBroker: "+this.ctxBroker);
		LOG.info("commsMgr : "+this.commsMgr );
		LOG.info("idMgr : "+this.idMgr );
	
		//identities
		this.userIdentity = this.idMgr.getThisNetworkNode();
		try {
			this.serviceIdentity = this.idMgr.fromJid("nikosk@societies.org");
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myServiceID = new ServiceResourceIdentifier();
		myServiceID.setServiceInstanceIdentifier("css://nikosk@societies.org/ContextAware3pService");
		try {
			myServiceID.setIdentifier(new URI("css://nikosk@societies.org/ContextAware3pService"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestorService = new RequestorService(serviceIdentity, myServiceID);

		LOG.info("userIdentity : "+ userIdentity.getBareJid());
		LOG.info("requestor service : "+requestorService);
	}

	@Override
	public void retrieveIndividualEntityId(){

		LOG.info("*** retrieveIndividualEntityId");

		CtxEntityIdentifier cssOwnerEntityId = null;
		try {
			cssOwnerEntityId = this.ctxBroker.retrieveIndividualEntityId(requestorService, userIdentity).get();
		} catch (Exception e) {
			LOG.error("3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}
		LOG.info("*** retrieved IndividualEntityId "+ cssOwnerEntityId);
	}

	@Override
	public void createCtxEntityWithCtxAttributes(){

		LOG.info("*** createCtxEntityWithCtxAttributes");
		CtxEntity deviceCtxEnt = null;

		try {
			deviceCtxEnt = this.ctxBroker.createEntity(requestorService, userIdentity, CtxEntityTypes.DEVICE).get();

			//get the context identifier of the created entity (to be used at the next step)
			CtxEntityIdentifier deviceCtxEntityIdentifier = deviceCtxEnt.getId();
			LOG.info("Device entity created: "+ deviceCtxEnt.getId());

			// ignore this for now, it will be used in context update events example
			deviceCtxEntity = deviceCtxEnt;

			//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
			CtxAttribute ctxAttributeDeviceLocation = this.ctxBroker.createAttribute(requestorService, deviceCtxEntityIdentifier, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			ctxAttrDevLocationIdentifier = ctxAttributeDeviceLocation.getId();

			// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
			ctxAttributeDeviceLocation.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttributeDeviceLocation.setStringValue("HOME");

			// with this update the attribute is stored in Context DB
			ctxAttributeDeviceLocation = (CtxAttribute) this.ctxBroker.update(requestorService, ctxAttributeDeviceLocation).get();

			//create a ctxAttribute with a Binary value that is assigned to the deviceCtxEntity
			CtxAttribute ctxAttrWeight = this.ctxBroker.createAttribute(requestorService, deviceCtxEntity.getId(),CtxAttributeTypes.WEIGHT).get();

			// this is a mock blob class that contains the value "999"
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes;
			try {
				blobBytes = SerialisationHelper.serialise(blob);
				ctxAttrWeight.setBinaryValue(blobBytes);
				ctxAttrWeight = (CtxAttribute) this.ctxBroker.update(requestorService, ctxAttrWeight).get();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// create an attribute to model the temperature of the device
			CtxAttribute deviceTempAttr = this.ctxBroker.createAttribute(requestorService, deviceCtxEntity.getId(), CtxAttributeTypes.TEMPERATURE).get();

			// assign a double value and set value type and metric
			deviceTempAttr.setDoubleValue(25.0);
			deviceTempAttr.setValueType(CtxAttributeValueType.DOUBLE);
			deviceTempAttr.setValueMetric(CtxAttributeValueMetrics.CELSIUS);

			// update the attribute in the Context DB
			deviceTempAttr = (CtxAttribute) this.ctxBroker.update(requestorService, deviceTempAttr).get();

			// at this point the ctxEntity of type CtxEntityTypes.DEVICE is assigned with CtxAttributes of type : LOCATION, WEIGHT, TEMPERATURE
			LOG.info("*** created entity deviceCtxEnt  "+deviceCtxEnt.getId().toString());
			LOG.info("*** with attributes Location: "+ctxAttributeDeviceLocation.getId());
			LOG.info("*** with attributes Temperature: "+deviceTempAttr.getId());
			LOG.info("*** with attributes Weight: "+ctxAttrWeight.getId());

		} catch (Exception e) {
			LOG.error("*** 3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}

	}


	@Override
	public void retrieveCtxAttributeBasedOnEntity() {

		LOG.info("*** retrieveCtxAttributeBasedOnEntity");
		// if the CtxEntityID or CtxAttributeID is known the retrieval is performed using the ctxBroker.retrieve(CtxIdentifier) method
		// alternatively context identifiers can be retrieved with the help of lookup methods
		
		CtxEntityIdentifier deviceCtxEntIdentifier = null;
		try {
			//TODO access control is still causing some problems when looking up CtxEntities 
			List<CtxIdentifier> idsEntities = this.ctxBroker.lookup(requestorService, userIdentity, CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			if(idsEntities.size()>0){
				deviceCtxEntIdentifier = (CtxEntityIdentifier) idsEntities.get(0);
			}
			// the retrieved identifier is used in order to retrieve the context model object (CtxEntity)
			CtxEntity retrievedCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestorService, deviceCtxEntIdentifier).get();

			// the CtxEntity object retrieved
			LOG.info("Retrieved ctxEntity id " +retrievedCtxEntity.getId()+ " of type: "+retrievedCtxEntity.getType());

			// Now it is possible to access the CtxAttributes assigned to retrieved CtxEntity. In this example the location of the device entity will be retrieved.
			Set<CtxAttribute> ctxAttrSet = retrievedCtxEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);

			if(ctxAttrSet.size()>0) {
				List<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);
				CtxAttribute ctxAttrLocation = ctxAttrList.get(0);
				LOG.info("retrieveCtxAttributeBasedOnEntity: Retrieved ctxAttribute of type" + ctxAttrLocation.getType()	+ " with value: " + ctxAttrLocation.getStringValue()); 
				LOG.info("retrieveCtxAttributeBasedOnEntity: The value should be : HOME");
			}

		} catch (Exception e) {
			LOG.error("*** 3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}
		LOG.info("*** retrieveCtxAttributeBasedOnEntity success");
	}

	@Override
	public void lookupAndRetrieveCtxAttributes(){

		LOG.info("*** lookupAndRetrieveCtxAttributes");
		CtxAttributeIdentifier attributeId = null;
		List<CtxIdentifier> idsAttributesList;
		try {
			idsAttributesList = this.ctxBroker.lookup(requestorService, userIdentity, CtxModelType.ATTRIBUTE, CtxAttributeTypes.WEIGHT).get();

			if( idsAttributesList.size()>0 ){
				attributeId = (CtxAttributeIdentifier) idsAttributesList.get(0);			
				CtxAttribute ctxAttributeWeight = (CtxAttribute) this.ctxBroker.retrieve(requestorService, attributeId).get();
				MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(ctxAttributeWeight.getBinaryValue(), this.getClass().getClassLoader());	

				LOG.info("lookupAndRetrieveCtxAttributes : Retrieved ctxAttribute id " +ctxAttributeWeight.getId()+ "and value: "+ retrievedBlob.getSeed()+" (must be equal to '999' ");
			}

			List<CtxIdentifier> idsAttributesList2 = this.ctxBroker.lookup(requestorService, userIdentity, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			if( idsAttributesList2.size()>0 ){
				CtxAttributeIdentifier attributeId2 = (CtxAttributeIdentifier) idsAttributesList2.get(0);			
				CtxAttribute ctxAttributeLocation = (CtxAttribute) this.ctxBroker.retrieve(requestorService, attributeId2).get();
				String value = ctxAttributeLocation.getStringValue();
				LOG.info("lookupAndRetrieveCtxAttributes : Retrieved ctxAttribute id " +ctxAttributeLocation.getId()+ "and value: "+ value +" (must be equal to 'home' ");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createRetrieveCtxHistory() {

		LOG.info("*** simpleCtxHistoryTest");
		CtxAttribute ctxAttribute;
		final CtxEntity ctxDevEntity;

		try {
			ctxDevEntity = this.ctxBroker.createEntity(requestorService,userIdentity, CtxEntityTypes.SERVICE).get();

			// Create the attribute to be tested
			ctxAttribute = this.ctxBroker.createAttribute(requestorService, ctxDevEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			ctxAttribute.setHistoryRecorded(true);

			ctxAttribute.setStringValue("home");
			ctxAttribute = (CtxAttribute) this.ctxBroker.update(requestorService,ctxAttribute).get();

			ctxAttribute.setStringValue("office");
			ctxAttribute = (CtxAttribute) this.ctxBroker.update(requestorService,ctxAttribute).get();

			ctxAttribute.setStringValue("pub");
			ctxAttribute = (CtxAttribute) this.ctxBroker.update(requestorService,ctxAttribute).get();

			List<CtxHistoryAttribute> history = this.ctxBroker.retrieveHistory(requestorService, ctxAttribute.getId(), null, null).get();

			for(CtxHistoryAttribute hocAttr: history){
				LOG.info("history List id:"+hocAttr.getId()+" getLastMod:"+hocAttr.getLastModified() +" hocAttr value:"+hocAttr.getStringValue());		
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
		LOG.info("*** simpleCtxHistoryTest success");
	}

	/**
	 * This method demonstrates how to register for context change events in the context database
	 */
	@Override
	public void registerForContextChanges() {

		LOG.info("*** registerForContextChanges");

		try {
			// 1a. Register listener by specifying the context attribute identifier
			this.ctxBroker.registerForChanges(requestorService, new MyCtxChangeEventListener(), ctxAttrDevLocationIdentifier);

			// 1b. Register listener by specifying the context attribute scope and type
			this.ctxBroker.registerForChanges(requestorService, new MyCtxChangeEventListener(), deviceCtxEntity.getId(), CtxAttributeTypes.ID);

			// 2. Update attribute to see some event action
			CtxAttribute ctxAttr = (CtxAttribute) this.ctxBroker.retrieve(requestorService, ctxAttrDevLocationIdentifier).get();

			ctxAttr.setStringValue("newDeviceLocation");
			ctxAttr = (CtxAttribute) this.ctxBroker.update(requestorService,ctxAttr).get();

		} catch (CtxException ce) {
			LOG.error("*** CM sucks " + ce.getLocalizedMessage(), ce);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("*** registerForContextChanges success");
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

	@Override
	public void retrievceLookupCommunityEntAttributes(IIdentity cisID){

		CtxEntityIdentifier ctxCommunityEntityIdentifier;
		try {
			ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(requestorService, cisID).get();
			LOG.info("communityEntityIdentifier retrieved: " +ctxCommunityEntityIdentifier.toString()+ " based on cisID: "+ cisID);

			List<CtxIdentifier> communityAttrIDList = this.ctxBroker.lookup(requestorService, ctxCommunityEntityIdentifier, CtxModelType.ATTRIBUTE, CtxAttributeTypes.INTERESTS).get();
			LOG.info("lookup results communityAttrIDList: "+ communityAttrIDList);		

			if(communityAttrIDList.size()>0 ){
				CtxIdentifier communityAttrID = communityAttrIDList.get(0);
				LOG.info("communityAttrID : "+ communityAttrID);
				CtxAttribute communityAttr = (CtxAttribute) this.ctxBroker.retrieve(requestorService, communityAttrID).get();
				String communityInterestsValue = communityAttr.getStringValue();
				LOG.info("communityAttr current value:"+communityInterestsValue);

				//update a community attribute value
				communityAttr.setStringValue(communityInterestsValue+",newCommunityInterest");
				this.ctxBroker.update(requestorService, communityAttr);
			}

			List<CtxIdentifier> communityAttrIDListUpdated = this.ctxBroker.lookup(requestorService, ctxCommunityEntityIdentifier, CtxModelType.ATTRIBUTE, CtxAttributeTypes.INTERESTS).get();
			LOG.info("lookup results communityAttrIDListUpdated: "+ communityAttrIDListUpdated);		
			if(communityAttrIDListUpdated.size()>0 ){
				CtxIdentifier communityAttrIDupdtd = communityAttrIDList.get(0);
				LOG.info("communityAttrID : "+ communityAttrIDupdtd);
				CtxAttribute communityAttrUpdt = (CtxAttribute) this.ctxBroker.retrieve(requestorService, communityAttrIDupdtd).get();
				String communityInterestsUpdated = communityAttrUpdt.getStringValue();
				LOG.info("communityAttr  value updated :"+communityInterestsUpdated);
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
	}

	/**
	 * This method demonstrates how to create an association among context entities
	 */
	private void createCtxAssociation(){

		LOG.info("*** createCtxAssociation  ");
		try {
			CtxAssociation usesServiceAssoc = this.ctxBroker.createAssociation(requestorService, userIdentity, CtxAssociationTypes.USES_SERVICES).get();
			usesServiceAssoc.addChildEntity(deviceCtxEntity.getId());
			LOG.info("usesServiceAssoc "+usesServiceAssoc);

			usesServiceAssoc = (CtxAssociation) this.ctxBroker.update(requestorService, usesServiceAssoc).get();
			LOG.info("usesServiceAssoc updated: "+usesServiceAssoc);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		} 
		LOG.info("createCtxAssociation  success");
	}	

}