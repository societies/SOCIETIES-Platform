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

package org.societies.integration.test.bit.ctx_3pBroker;



import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.context.broker.ICtxBroker;


/**
 * 
 *
 * @author nikosk
 *
 */
public class Tester {

	private ICtxBroker externalCtxBroker;
	private ICommManager commMgr;

	private static Logger LOG = LoggerFactory.getLogger(Tester.class);

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;

	private RequestorService requestorService = null;
	private IIdentity userIdentity = null;
	private IIdentity serviceIdentity = null;
	private ServiceResourceIdentifier myServiceID;


	CtxEntityIdentifier cssOwnerEntityId ;

	public Tester(){

	}

	@Before
	public void setUp(){

	}


	@Test
	public void Test(){

		this.externalCtxBroker = Test1337.getCtxBroker();
		this.commMgr = Test1337.getCommManager();

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

		LOG.info("*** Starting examples...");
		// TODO createRemoteEntity();
		this.retrieveIndividualEntityId();
		
		// doesn't work
		this.updateOperatorAttributeLocation();
		
		///works
		this.createOperatorAttributeBirthday();
	}



	private void updateOperatorAttributeLocation(){
		LOG.info("*** updateOperatorAttributes : updates an existing  Location attribute");

		CtxEntityIdentifier cssOwnerEntityId;
		try {
			cssOwnerEntityId = this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();

			List<CtxIdentifier> listAttrIds =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 

			if(listAttrIds.size() == 0){
				//create location attribute
				LOG.info("location attribute doesn't exist ... creating");
				this.externalCtxBroker.createAttribute(this.requestorService, cssOwnerEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 
			}

			List<CtxIdentifier> listAttrIds2 =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 
			LOG.info("location attribute identifiers "+listAttrIds2);

			CtxAttributeIdentifier locationAttributeId = null;

			//the test will run correct for only one location attribute
			if(listAttrIds2.size() == 1){
				locationAttributeId = (CtxAttributeIdentifier) listAttrIds2.get(0);  
				CtxAttribute locationAttributeRetrieved = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId).get();
				LOG.info("locationAttributeRetrieved  :" + locationAttributeRetrieved.getId());
				String locationSymbolicValue  = locationAttributeRetrieved.getStringValue();
				LOG.info("locationAttributeRetrieved value (should be null) :" + locationSymbolicValue);

				locationAttributeRetrieved.setStringValue("ATHENS");
				LOG.info("value set...."+locationAttributeRetrieved.getStringValue()+" trying to update location attribute :" +locationAttributeRetrieved.getId());
				this.externalCtxBroker.update(this.requestorService, locationAttributeRetrieved );				
				LOG.info("update successfull");
			}	

			LOG.info("retrieve location attribute based on existing identifier:"+ locationAttributeId);
			CtxAttribute locationAttributeWithValue = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId).get();
			LOG.info("locationAttributeWithValue value :" + locationAttributeWithValue.getStringValue());

			LOG.info("retrieve location attribute based on lookup ids ************* ");
			List<CtxIdentifier> listAttrIds3 =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 
			LOG.info("retrieve location attribute based on lookup ids"+listAttrIds3);
			if(listAttrIds3.size() == 1){
				CtxAttributeIdentifier locationAttributeId3 = (CtxAttributeIdentifier) listAttrIds3.get(0); 
				CtxAttribute locationAttributeRetrieved3 = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId3).get();
				LOG.info("locationAttributeRetrieved  :" + locationAttributeRetrieved3.getId());
				String locationSymbolicValue  = locationAttributeRetrieved3.getStringValue();
				LOG.info("locationAttributeRetrieved value :" + locationSymbolicValue);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createOperatorAttributeBirthday(){

		LOG.info("createOperatorAttributeBirthday");
		try {
			CtxEntityIdentifier cssOwnerEntityId = 
					this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();
			LOG.info("*** Retrieved CSS owner context entity id " + cssOwnerEntityId);

			LOG.info("*** lookup for birthday attribute ");
			List<CtxIdentifier> listAttrBDays =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.BIRTHDAY).get(); 
			LOG.info("this list should be zero :" + listAttrBDays); 

			//create attribute birthday
			LOG.info("*** create attribute birthday ");
			CtxAttribute ctxAttrBirthday = this.externalCtxBroker.createAttribute(this.requestorService, cssOwnerEntityId, CtxAttributeTypes.BIRTHDAY).get(); 

			CtxAttributeIdentifier bDayId = ctxAttrBirthday.getId();


			LOG.info("*** set value of attribute birthday and update");
			ctxAttrBirthday.setStringValue("today"); 
			ctxAttrBirthday.setValueType(CtxAttributeValueType.STRING);

			this.externalCtxBroker.update(this.requestorService, ctxAttrBirthday).get();

			LOG.info("*** lookup for birthday attribute 2 (after creation-update)");
			List<CtxIdentifier> listAttrBDays2 =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.BIRTHDAY).get(); 
			LOG.info("should not be zero listAttrBDays :" + listAttrBDays2); 


			CtxAttribute ctxAttrBirthdayRetrieved1 = null;
			CtxAttribute ctxAttrBirthdayRetrieved2 = null;
			LOG.info("*** retrieve birthday attribute from db ");

			//this one works
			ctxAttrBirthdayRetrieved1 = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, bDayId).get();
			LOG.info("withoutLookup ctxAttrBirthdayRetrieved:" + ctxAttrBirthdayRetrieved1.getId());
			String ctxAttrBirthdayRetrievedValue  = ctxAttrBirthdayRetrieved1.getStringValue();
			LOG.info("withoutLookup ctxAttrBirthdayRetrieved value :" + ctxAttrBirthdayRetrievedValue);
			assertEquals("today",ctxAttrBirthdayRetrievedValue);
			//try this one
			if(listAttrBDays2.size()>0){
				CtxAttributeIdentifier attrId = (CtxAttributeIdentifier) listAttrBDays2.get(0);
				ctxAttrBirthdayRetrieved2 = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService,attrId ).get();	
			}


			LOG.info("after lookup ctxAttrBirthdayRetrieved:" + ctxAttrBirthdayRetrieved2.getId());
			String ctxAttrBirthdayRetrievedValue2  = ctxAttrBirthdayRetrieved2.getStringValue();
			LOG.info("ctxAttrBirthdayRetrieved value :" + ctxAttrBirthdayRetrievedValue2);
			assertEquals("today",ctxAttrBirthdayRetrievedValue2);
		} catch (Exception e) {

			LOG.error("3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}	
	}

	


	private void retrieveIndividualEntityId() {

		LOG.info("*** retrieveIndividualEntityId");
		try {
			CtxEntityIdentifier cssOwnerEntityId = 
					this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();
			LOG.info("*** Retrieved CSS owner context entity id " + cssOwnerEntityId);
			//assertEquals("xcmanager.societies.local/ENTITY/person/0 ",cssOwnerEntityId.toString());

		} catch (Exception e) {
			LOG.error("3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}
	}
	
		
	
	/*
	 * private void lookupRetrieveLocationAttributeValue(){
		List<CtxIdentifier> listAttrIds2;
		try {
			listAttrIds2 = this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			if(listAttrIds2.size() > 0){
				CtxAttributeIdentifier locationAttributeId = (CtxAttributeIdentifier) listAttrIds2.get(0);  
				CtxAttribute locationAttributeRetrieved = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId).get();
				LOG.info("locationAttributeRetrieved  :" + locationAttributeRetrieved.getId());
				String locationSymbolicValue  = locationAttributeRetrieved.getStringValue();
				LOG.info("locationAttributeRetrieved value :" + locationSymbolicValue);
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
	 */
	
}