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
package org.societies.integration.test.bit.context.hierarchy;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.integration.test.userfeedback.UserFeedbackMockResult;
import org.societies.integration.test.userfeedback.UserFeedbackType;

/**
 * 
 *
 * @author nikosk
 *
 */
public class TestLocalRetrieveByType {

	private static Logger LOG = LoggerFactory.getLogger(TestLocalRetrieveByType.class);
	
	private ICtxBroker ctxBroker;
	private ICommManager commMgr;

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;

	private RequestorService requestorService = null;
	private IIdentity userIdentity = null;
	private IIdentity serviceIdentity = null;
	private ServiceResourceIdentifier myServiceID;

	String remoteTargetID = "emma.ict-societies.eu";

	//CtxEntityIdentifier cssOwnerEntityId ;
	
	CtxEntity deviceCtxEnt;
	CtxAttribute ctxAttrDeviceNameFirst;
	CtxAttribute ctxAttrDeviceNameLast;

	@Before
	public void setUp(){

		LOG.info("*** initiallizing " );
		LOG.info("*** " +CtxDataHierarchyTestCase.getUserFeedbackMocker());

		CtxDataHierarchyTestCase.getUserFeedbackMocker().setEnabled(true);
		CtxDataHierarchyTestCase.getUserFeedbackMocker().addReply(UserFeedbackType.ACKNACK, new UserFeedbackMockResult("Allow"));

	}
	
	@After
	public void tearDown(){

		try {
			LOG.info("*** tear down **** " );
			this.ctxBroker.remove(this.requestorService, this.deviceCtxEnt.getId());
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Test
	public void Test(){

		this.ctxBroker = CtxDataHierarchyTestCase.getCtxBroker();
		this.commMgr = CtxDataHierarchyTestCase.getCommManager();




		LOG.info("*** " + this.getClass() + " instantiated");

		try {
			this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + this.cssNodeId);

			final String cssOwnerStr = this.cssNodeId.getBareJid();
			this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
			LOG.info("*** cssOwnerId = " + this.cssOwnerId);

			this.serviceIdentity = commMgr.getIdManager().fromJid("nikosk@societies.org");
			myServiceID = new ServiceResourceIdentifier();
			myServiceID.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
			myServiceID.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.requestorService = new RequestorService(serviceIdentity, myServiceID);

		LOG.info("*** requestor service = " + this.requestorService);

		LOG.info("*** Starting examples... ");

		LOG.info("*** addCtxData ");
		this.addCtxDataLocal();
		//this.addCtxDataRemote();

		LOG.info("*** retrieveCtxData");
		this.retrieveCtxDataLocal();

		//this.retrieveCtxDataRemote();
	}


	private void addCtxDataLocal(){

		try {
			
			this.deviceCtxEnt = this.ctxBroker.createEntity(this.requestorService, this.cssOwnerId, CtxEntityTypes.DEVICE).get();

			CtxAttribute ctxAttrDeviceNameFirstTemp = this.ctxBroker.createAttribute(this.requestorService, this.deviceCtxEnt.getId(), CtxAttributeTypes.NAME_FIRST).get();
			ctxAttrDeviceNameFirstTemp.setStringValue("MyFirstNameLocal");

			CtxAttribute ctxAttrDeviceNameLastTemp = this.ctxBroker.createAttribute(this.requestorService, this.deviceCtxEnt.getId(), CtxAttributeTypes.NAME_LAST).get();
			ctxAttrDeviceNameLastTemp.setStringValue("MyLastNameLocal");

			// with this update the attribute is stored in Context DB
			this.ctxAttrDeviceNameFirst = (CtxAttribute) this.ctxBroker.update(requestorService, ctxAttrDeviceNameFirstTemp).get();
			this.ctxAttrDeviceNameLast = (CtxAttribute) this.ctxBroker.update(requestorService, ctxAttrDeviceNameLastTemp).get();

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




	private void retrieveCtxDataLocal(){

		LOG.info("*** retrieveCtxDataLocal : test new lookup method*** ");
		try {
			List<CtxIdentifier> ctxIdList = this.ctxBroker.lookup(this.requestorService, this.cssOwnerId, CtxAttributeTypes.NAME).get();
			LOG.info("lookable list: "+ ctxIdList);
			LOG.info("lookable list size : "+ ctxIdList.size());


			List<CtxModelObject> ctxDataObjList = this.ctxBroker.retrieve(this.requestorService, ctxIdList).get();

			CtxAttribute ctxAttributeNameFirstRetrieved = null;
			CtxAttribute ctxAttributeNameLastRetrieved = null;
			
			LOG.info("ctxDataObjList : "+ ctxDataObjList);
			LOG.info("ctxDataObjList : "+ ctxDataObjList.size());
			
			for(CtxModelObject  ctxDataObj : ctxDataObjList){

				if( ctxDataObj instanceof CtxAttribute) {
					CtxAttribute attrTemp = (CtxAttribute) ctxDataObj; 

					if(attrTemp.getType().equalsIgnoreCase("nameFirst")){
						ctxAttributeNameFirstRetrieved = attrTemp;
						assertEquals(this.ctxAttrDeviceNameFirst.getId(), ctxAttributeNameFirstRetrieved.getId());
						assertEquals("MyFirstNameLocal", ctxAttributeNameFirstRetrieved.getStringValue());
						
					} else if( attrTemp.getType().equalsIgnoreCase("nameLast")){
						ctxAttributeNameLastRetrieved = attrTemp;
						assertEquals(this.ctxAttrDeviceNameLast, ctxAttributeNameLastRetrieved);
						assertEquals("MyLastNameLocal", ctxAttributeNameLastRetrieved.getStringValue());}
				}
			}

			for(CtxIdentifier id :ctxIdList ){

				if( id instanceof CtxAttributeIdentifier ){
					CtxAttribute ctxAttribute = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService, id).get();	

					if(ctxAttribute.getType().equalsIgnoreCase("nameFirst")){
						LOG.info("retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						assertEquals("MyFirstNameLocal", ctxAttribute.getStringValue());
				
					} if(ctxAttribute.getType().equalsIgnoreCase("nameLast")){
						LOG.info("retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						assertEquals("MyLastNameLocal", ctxAttribute.getStringValue());
					}

				} else if( id instanceof CtxAssociationIdentifier ){
					CtxAssociation ctxAssociation = (CtxAssociation) this.ctxBroker.retrieve(this.requestorService, id).get();	
					LOG.info("retrieved ctxAssociation id : "+ ctxAssociation.getId());
				}
			}





			/*	
			LOG.info("*** test lookup with null model type *** ");
			List<CtxIdentifier> ctxIdListNullModelType = this.ctxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME).get();	
			LOG.info("ctxIdListNullModelType list: "+ ctxIdList);
			LOG.info("ctxIdListNullModelType list size : "+ ctxIdList.size());

			for(CtxIdentifier id :ctxIdListNullModelType ){

				if( id instanceof CtxAttributeIdentifier ){
					CtxAttribute ctxAttribute = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService, id).get();	

					if(ctxAttribute.getType().equalsIgnoreCase("nameFirst")){
						LOG.info("retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						Assert.assertEquals("MyFirstNameLocal", ctxAttribute.getStringValue());
					} if(ctxAttribute.getType().equalsIgnoreCase("nameLast")){
						LOG.info("retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						Assert.assertEquals("MyLastNameLocal", ctxAttribute.getStringValue());
					}

				} else if( id instanceof CtxAssociationIdentifier ){
					CtxAssociation ctxAssociation = (CtxAssociation) this.ctxBroker.retrieve(this.requestorService, id).get();	
					LOG.info("retrieved ctxAssociation id : "+ ctxAssociation.getId());
				}
			}

			 */	


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


	/*
	 * Ignore following tests, not complete yet
	 */


	private void retrieveCtxDataRemote(){

		LOG.info("*** retrieveCtxDataRemote test new lookup method remote *** ");
		try {
			IIdentity remoteTarget = this.commMgr.getIdManager().fromJid(this.remoteTargetID);

			List<CtxIdentifier> ctxIdList = this.ctxBroker.lookup(this.requestorService, remoteTarget, CtxAttributeTypes.NAME).get();
			LOG.info("remote lookable list: "+ ctxIdList);
			LOG.info("remote lookable list size : "+ ctxIdList.size());

			for(CtxIdentifier id :ctxIdList ){

				if( id instanceof CtxAttributeIdentifier ){
					CtxAttribute ctxAttribute = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService, id).get();	

					if(ctxAttribute.getType().equalsIgnoreCase("nameFirst")){
						LOG.info("remote retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("remote retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						assertEquals("MyFirstNameRemote", ctxAttribute.getStringValue());
					} if(ctxAttribute.getType().equalsIgnoreCase("nameLast")){
						LOG.info("remote retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("remote retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						assertEquals("MyLastNameRemote", ctxAttribute.getStringValue());
					}

				} else if( id instanceof CtxAssociationIdentifier ){
					CtxAssociation ctxAssociation = (CtxAssociation) this.ctxBroker.retrieve(this.requestorService, id).get();	
					LOG.info("retrieved ctxAssociation id : "+ ctxAssociation.getId());
				}
			}


			LOG.info("*** remote test lookup with null model type *** ");
			List<CtxIdentifier> ctxIdListNullModelType = this.ctxBroker.lookup(this.requestorService, remoteTarget, CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME).get();	

			LOG.info("remote ctxIdListNullModelType list: "+ ctxIdListNullModelType);
			LOG.info("remote ctxIdListNullModelType list size : "+ ctxIdListNullModelType.size());

			for(CtxIdentifier id :ctxIdListNullModelType ){

				LOG.info("remote retrieved ctxAttribute id : "+ id);

				if( id instanceof CtxAttributeIdentifier ){
					CtxAttribute ctxAttribute = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService, id).get();	

					if(ctxAttribute.getType().equalsIgnoreCase("nameFirst")){
						LOG.info("remote retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("remote retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						Assert.assertEquals("MyFirstNameRemote", ctxAttribute.getStringValue());
					} if(ctxAttribute.getType().equalsIgnoreCase("nameLast")){
						LOG.info("remote retrieved ctxAttribute id : "+ ctxAttribute.getId());
						LOG.info("remote retrieved ctxAttribute value : "+ ctxAttribute.getStringValue());
						Assert.assertEquals("MyLastNameRemote", ctxAttribute.getStringValue());
					}

				} else if( id instanceof CtxAssociationIdentifier ){
					CtxAssociation ctxAssociation = (CtxAssociation) this.ctxBroker.retrieve(this.requestorService, id).get();	
					LOG.info("retrieved ctxAssociation id : "+ ctxAssociation.getId());
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

	}



	private void addCtxDataRemote(){

		try {
			IIdentity remoteTarget = this.commMgr.getIdManager().fromJid(this.remoteTargetID);

			CtxEntity deviceCtxEnt = this.ctxBroker.createEntity(this.requestorService, remoteTarget, CtxEntityTypes.DEVICE).get();

			/*
			 * Set<String> postAddressWorkChildren = new HashSet<String>();
			postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_STREET_NUMBER);
			postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_STREET_NAME);
			postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_CITY);
			postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_COUNTRY);
			addChildren("ADDRESS_WORK", postAddressWorkChildren);
			 */

			CtxAttribute ctxAttrDeviceNameFirst = this.ctxBroker.createAttribute(this.requestorService, deviceCtxEnt.getId(), CtxAttributeTypes.NAME_FIRST).get();
			ctxAttrDeviceNameFirst.setStringValue("MyFirstNameRemote");

			CtxAttribute ctxAttrDeviceNameLast = this.ctxBroker.createAttribute(this.requestorService, deviceCtxEnt.getId(), CtxAttributeTypes.NAME_LAST).get();
			ctxAttrDeviceNameLast.setStringValue("MyLastNameRemote");

			// with this update the attribute is stored in Context DB
			this.ctxBroker.update(requestorService, ctxAttrDeviceNameFirst).get();
			this.ctxBroker.update(requestorService, ctxAttrDeviceNameLast).get();

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
}