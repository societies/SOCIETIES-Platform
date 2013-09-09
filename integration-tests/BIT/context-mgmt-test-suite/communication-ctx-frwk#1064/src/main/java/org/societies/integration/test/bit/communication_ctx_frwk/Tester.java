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

package org.societies.integration.test.bit.communication_ctx_frwk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;


import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.context.broker.ICtxBroker;


/**
 * Utility class that creates mock actions
 *
 * @author Nikos
 *
 */
public class Tester {

	private static Logger LOG = LoggerFactory.getLogger(Test1064.class);
	Requestor requestor = null;

	public ICtxBroker ctxBroker;
	public org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker;
	public ICommManager commMgr;

	public IIdentity currentCISId;

	// run test in university's container
	private String targetEmma= "emma.ict-societies.eu";


	public Tester(){
		LOG.info("*** " + this.getClass() + " starting");
	}

	@Before
	public void setUp(){

		this.ctxBroker =  Test1064.getCtxBroker();
		this.internalCtxBroker = Test1064.getInternalCtxBroker();
		this.commMgr = Test1064.getCommManager();
	}

	@Test
	public void testRemotePrediction(){

		LOG.info("*** REMOTE CM TEST STARTING ***");
		LOG.info("*** " + this.getClass() + " instantiated");
		LOG.info("*** ctxBroker service :"+Test1064.getCtxBroker());

		try {	
			INetworkNode cssNodeId = Test1064.getCommManager().getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = Test1064.getCommManager().getIdManager().fromJid(cssOwnerStr);
			this.requestor = new Requestor(cssOwnerId);
			LOG.info("*** requestor = " + this.requestor);

			IIdentity cssIDEmma =  Test1064.getCommManager().getIdManager().fromJid(targetEmma);

			List<CtxIdentifier> entityList = Test1064.getCtxBroker().lookup(requestor, cssIDEmma, CtxModelType.ENTITY ,CtxEntityTypes.DEVICE).get();

			if(entityList.isEmpty() ){

				CtxEntity entityEmmaDevice = Test1064.getCtxBroker().createEntity(requestor, cssIDEmma, CtxEntityTypes.DEVICE).get();
				LOG.info("entity DEVICE created based on 3p broker "+entityEmmaDevice.getId());
				assertNotNull(entityEmmaDevice.getId());	
				assertEquals("device", entityEmmaDevice.getType().toLowerCase());

				List<CtxIdentifier> attrList = Test1064.getCtxBroker().lookup(requestor, cssIDEmma, CtxModelType.ATTRIBUTE ,CtxAttributeTypes.TEMPERATURE).get();

				if(attrList.isEmpty() ){
					CtxAttribute attrEmmaTemperature = Test1064.getCtxBroker().createAttribute(requestor, entityEmmaDevice.getId(), CtxAttributeTypes.TEMPERATURE).get();
					LOG.info("Attribute TEMPERATURE created in remote container "+attrEmmaTemperature.getId());
					assertNotNull(attrEmmaTemperature.getId());	
				
					attrEmmaTemperature.setStringValue("hot");
					Test1064.getCtxBroker().update(requestor, attrEmmaTemperature);
				}

			}
			
			List<CtxIdentifier> entityList2 = Test1064.getCtxBroker().lookup(requestor, cssIDEmma,  CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			LOG.info("remote entity list ids:" +entityList2);
			assertNotNull(entityList2);

			CtxEntity entityDevRetrieved = (CtxEntity) Test1064.getCtxBroker().retrieve(requestor, entityList2.get(0)).get();
			LOG.info("remote entity id:" +entityDevRetrieved.getId());
			assertNotNull(entityDevRetrieved.getId());

			List<CtxIdentifier> attrList2 = Test1064.getCtxBroker().lookup(requestor, cssIDEmma, CtxModelType.ATTRIBUTE ,CtxAttributeTypes.TEMPERATURE).get();

			LOG.info("remote attribute list ids:" +attrList2);
			// verify that two methods return the same results


			CtxAttribute remoteAttrTemp = (CtxAttribute) Test1064.getCtxBroker().retrieve(requestor,attrList2.get(0)).get();
			LOG.info("remoteAttrTemp :" +remoteAttrTemp);


			LOG.info("STARTING FUTURE RETRIEVAL ..... ");
			Date date = new Date();
			List<CtxAttribute> remoteAttrTemp2 = Test1064.getCtxBroker().retrieveFuture(requestor,(CtxAttributeIdentifier) attrList2.get(0),date).get();
			CtxAttribute attr = remoteAttrTemp2.get(0);
			attr.getLastModified();
			
			LOG.info("remoteAttrTemp2 :" +remoteAttrTemp2);
			LOG.info("ENDING FUTURE RETRIEVAL ..... ");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	@Ignore
	@Test
	public void testRemoteRetrievals(){

		LOG.info("*** REMOTE CM TEST STARTING ***");
		LOG.info("*** " + this.getClass() + " instantiated");
		LOG.info("*** ctxBroker service :"+Test1064.getCtxBroker());

		try {								
			INetworkNode cssNodeId = Test1064.getCommManager().getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = Test1064.getCommManager().getIdManager().fromJid(cssOwnerStr);
			this.requestor = new Requestor(cssOwnerId);
			LOG.info("*** requestor = " + this.requestor);

			IIdentity cssIDEmma =  Test1064.getCommManager().getIdManager().fromJid(targetEmma);
			CtxEntity entityEmmaDevice = Test1064.getCtxBroker().createEntity(requestor, cssIDEmma, CtxEntityTypes.DEVICE).get();

			LOG.info("entity DEVICE created based on 3p broker "+entityEmmaDevice.getId());
			assertNotNull(entityEmmaDevice.getId());	
			assertEquals("device", entityEmmaDevice.getType().toLowerCase());

			CtxAttribute attrEmmaTemperature = Test1064.getCtxBroker().createAttribute(requestor, entityEmmaDevice.getId(), CtxAttributeTypes.TEMPERATURE).get();
			LOG.info("Attribute TEMPERATURE created in remote container "+attrEmmaTemperature.getId());
			assertNotNull(attrEmmaTemperature.getId());	


			//test binary
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes;

			try {
				blobBytes = SerialisationHelper.serialise(blob);
				CtxAttribute ctxAttrBinary = Test1064.getCtxBroker().createAttribute(requestor, entityEmmaDevice.getId(), CtxAttributeTypes.ACTIVITIES).get();
				ctxAttrBinary.setBinaryValue(blobBytes);

				Test1064.getCtxBroker().update(requestor, ctxAttrBinary);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			// entity and attribute created

			// perform remote look up and retrieve
			// ENTITY
			List<CtxIdentifier> entityList = Test1064.getCtxBroker().lookup(requestor, cssIDEmma,  CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			LOG.info("remote entity list ids:" +entityList);
			assertNotNull(entityList);

			CtxEntity entityDevRetrieved = (CtxEntity) Test1064.getCtxBroker().retrieve(requestor, entityList.get(0)).get();
			LOG.info("remote entity id:" +entityDevRetrieved.getId());
			assertNotNull(entityDevRetrieved.getId());

			// ATTRIBUTE
			List<CtxIdentifier> attrList1 = Test1064.getCtxBroker().lookup(requestor, entityDevRetrieved.getId(), CtxModelType.ATTRIBUTE ,CtxAttributeTypes.TEMPERATURE).get();
			List<CtxIdentifier> attrList2 = Test1064.getCtxBroker().lookup(requestor, cssIDEmma, CtxModelType.ATTRIBUTE ,CtxAttributeTypes.TEMPERATURE).get();
			LOG.info("remote attribute list ids:" +attrList1);
			LOG.info("remote attribute list ids:" +attrList2);
			// verify that two methods return the same results
			assertEquals(attrList1,attrList2);

			CtxAttribute remoteAttrTemp = (CtxAttribute) Test1064.getCtxBroker().retrieve(requestor,attrList1.get(0)).get();
			LOG.info("remote CtxAttribute id:" +remoteAttrTemp.getId());
			assertNotNull(remoteAttrTemp.getId());
			assertEquals("temperature", remoteAttrTemp.getType().toLowerCase());


			//List<CtxIdentifier> attrListAction = Test1064.getCtxBroker().lookup(requestor, entityDevRetrieved.getId(), CtxModelType.ATTRIBUTE ,CtxAttributeTypes.ACTION).get();
			List<CtxIdentifier> attrListAction = Test1064.getCtxBroker().lookup(requestor, cssIDEmma, CtxModelType.ATTRIBUTE ,CtxAttributeTypes.ACTIVITIES).get();
			CtxAttribute remoteAttrAction = (CtxAttribute) Test1064.getCtxBroker().retrieve(requestor,attrListAction.get(0)).get();
			LOG.info("remote CtxAttribute id:" +remoteAttrAction.getId());
			assertNotNull(remoteAttrAction.getId());
			assertEquals("activities", remoteAttrAction.getType().toLowerCase());
			LOG.info("remote CtxAttribute binary value:" +remoteAttrAction.getBinaryValue() +"  -- "+this.getClass().getClassLoader() );

			MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(remoteAttrAction.getBinaryValue(), this.getClass().getClassLoader());
			assertNotNull(retrievedBlob);


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
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

	@Ignore
	@Test
	public void testPrivacyPolicyStructure(){


		try {
			INetworkNode cssNodeId = Test1064.getCommManager().getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = Test1064.getCommManager().getIdManager().fromJid(cssOwnerStr);
			requestor = new Requestor(cssOwnerId);


			LOG.info("*** internalCtxBroker = " + internalCtxBroker);

			IndividualCtxEntity indiEnt =  this.internalCtxBroker.retrieveIndividualEntity(cssOwnerId).get();

			LOG.info("*** create privacy policy structure");

			List<CtxIdentifier> assocList = this.internalCtxBroker.lookup(CtxModelType.ASSOCIATION, CtxAssociationTypes.HAS_SERVICE_PRIVACY_POLICIES).get();

			CtxAssociation hasPrivacyPol = null;

			if(assocList.size() == 0 ){
				hasPrivacyPol = this.internalCtxBroker.createAssociation(cssOwnerId,  CtxAssociationTypes.HAS_SERVICE_PRIVACY_POLICIES).get();
			} else {
				hasPrivacyPol = (CtxAssociation) this.internalCtxBroker.retrieve(assocList.get(0)).get();
			}


			hasPrivacyPol.setParentEntity(indiEnt.getId());

			CtxEntity servPrivacyPolicyEnt1 = createServicePrivacyPol(cssOwnerId, "serviceID123");
			CtxEntity servPrivacyPolicyEnt2 = createServicePrivacyPol(cssOwnerId, "serviceID456");
			CtxEntity servPrivacyPolicyEnt3 = createServicePrivacyPol(cssOwnerId, "serviceID789");

			hasPrivacyPol.addChildEntity(servPrivacyPolicyEnt1.getId());
			hasPrivacyPol.addChildEntity(servPrivacyPolicyEnt2.getId());
			hasPrivacyPol.addChildEntity(servPrivacyPolicyEnt3.getId());

			this.internalCtxBroker.update(hasPrivacyPol);

			LOG.info("*** privacy policy structure created");


			LOG.info("*** retrieve privacy policy ");

			List<CtxEntityIdentifier> entIdList =	this.internalCtxBroker.lookupEntities(CtxEntityTypes.SERVICE_PRIVACY_POLICY, CtxAttributeTypes.ID, "serviceID123", "serviceID123").get();

			if( !entIdList.isEmpty()){
				LOG.info("size of results: "+entIdList.size());
				CtxEntityIdentifier entPolID = entIdList.get(0);
				CtxEntity servPrivPolEnt =	(CtxEntity) this.internalCtxBroker.retrieve( entPolID).get();
				Set<CtxAttribute> priPolAttrSet = servPrivPolEnt.getAttributes(CtxAttributeTypes.PRIVACY_POLICY);

				for(CtxAttribute priPolAttr : priPolAttrSet){

					MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(priPolAttr.getBinaryValue(), this.getClass().getClassLoader());
					LOG.info("*** retrievedBlob " +retrievedBlob.getSeed());				
					assertEquals(999,retrievedBlob.getSeed());
				}
			}

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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


	public CtxEntity createServicePrivacyPol(IIdentity cssOwnerId, String serviceID){

		CtxEntity servPrivacyPolicyEnt = null;
		try {
			servPrivacyPolicyEnt = this.internalCtxBroker.createEntity( cssOwnerId, CtxEntityTypes.SERVICE_PRIVACY_POLICY).get();
			CtxAttribute  serviceIDAttr = this.internalCtxBroker.createAttribute(servPrivacyPolicyEnt.getId(), CtxAttributeTypes.ID).get();
			serviceIDAttr.setStringValue(serviceID);
			this.internalCtxBroker.update(serviceIDAttr).get();

			CtxAttribute  privPolAttr = this.internalCtxBroker.createAttribute( servPrivacyPolicyEnt.getId(), CtxAttributeTypes.PRIVACY_POLICY).get();
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes = SerialisationHelper.serialise(blob);
			privPolAttr.setBinaryValue(blobBytes);
			this.internalCtxBroker.update(privPolAttr);

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

		return servPrivacyPolicyEnt;

	}


	/*
	 * scenario...
	 * a cis is created in uni css node 
	 * 
	 * emma performs lookup to this node 
	 */
	@Ignore
	@Test
	public void testRemoteRetrievalsCIS(){

		try {
			INetworkNode cssNodeId = Test1064.getCommManager().getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = Test1064.getCommManager().getIdManager().fromJid(cssOwnerStr);
			this.requestor = new Requestor(cssOwnerId);
			LOG.info("*** requestor = " + this.requestor);


			List<CtxEntityIdentifier> cisList = retrieveBelongingCIS();

			LOG.info("*** retrieveBelongingCIS = " + cisList);

			List<CtxIdentifier> listCommAttributesName1 = this.internalCtxBroker.lookup(requestor, cisList.get(0), CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME).get();	
			LOG.info("*** remote comm lookups based on commEntID = " + listCommAttributesName1);

			if(currentCISId != null){
				List<CtxIdentifier> listCommAttributesName2 = this.internalCtxBroker.lookup(requestor, currentCISId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME).get();
				LOG.info("*** remote comm lookups based on cisID = " + listCommAttributesName2);


				CtxAttribute commAttr =  (CtxAttribute) this.internalCtxBroker.retrieve(listCommAttributesName2.get(0)).get();
				LOG.info("*** remote comm Attr value = " + commAttr.getStringValue());

				List<CtxIdentifier> listCommAssocs = this.internalCtxBroker.lookup(requestor, currentCISId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
				LOG.info("*** remote comm Assoc value = " + listCommAssocs.get(0));


				List<CtxIdentifier> listCommAttributesName3 = this.internalCtxBroker.lookup(currentCISId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME).get();
				LOG.info("*** remote comm Attr list = " + listCommAttributesName3);

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
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}






	public List<CtxEntityIdentifier> retrieveBelongingCIS(){

		List<CtxEntityIdentifier> commEntIDList = new ArrayList<CtxEntityIdentifier>();

		List<CtxIdentifier> listISMemberOf = new ArrayList<CtxIdentifier>();
		try {
			listISMemberOf = this.internalCtxBroker.lookup(CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
			LOG.debug(".............listISMemberOf................." +listISMemberOf);

			if(!listISMemberOf.isEmpty() ){
				CtxAssociation assoc = (CtxAssociation) this.internalCtxBroker.retrieve(listISMemberOf.get(0)).get();
				Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();

				for(CtxEntityIdentifier entId : entIDSet){
					IIdentity cisId = this.commMgr.getIdManager().fromJid(entId.getOwnerId());
					LOG.debug("cis id : "+cisId );
					currentCISId = cisId;
					CtxEntityIdentifier commId = this.internalCtxBroker.retrieveCommunityEntityId(cisId).get();
					commEntIDList.add(commId);
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
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commEntIDList;
	}


}