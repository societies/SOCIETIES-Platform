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

package org.societies.integration.test.bit.ctx_locationRefiner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;

//import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * 
 *
 * @author nikosk
 *
 */
public class Tester {

	private ICtxBroker internalCtxBroker;
	private ICommManager commMgr;

	private static Logger LOG = LoggerFactory.getLogger(Tester.class);

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;

	private CtxEntityIdentifier indiEntityId = null;

	CtxEntityIdentifier cssOwnerEntityId ;

	public Tester(){

	}

	@Before
	public void setUp(){

	}


	@Test
	public void TestLocationRefinement(){

		this.internalCtxBroker = TestLocationRefiner.getCtxBroker();
		this.commMgr = TestLocationRefiner.getCommManager();

		try {
			this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();

			final String cssOwnerStr = this.cssNodeId.getBareJid();
			this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
			LOG.info("*** cssOwnerId = " + this.cssOwnerId);

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		LOG.info("*** Start testing ...");

		//test 
		//1. retrieve symbolic location (on-demand inference)
		this.testOnDemandInference();

		//2. registerForChanges(locationAttrId)
		this.testContiniousInferenceByAttrID();

		//3. registerForChanges(indiEntId, LOCATION_SYMBOLIC)
		//this.testContiniousInferenceByAttrType();

	}



	private void testContiniousInferenceByAttrID(){

		LOG.info("start testing testContiniousInferenceByAttrID ");
		//1. register for changes on individual entity location 
		//2. update location in css node
		//3. receive update in listener and verify value

		//indiEntityId
		CtxAttribute individualLocationAttr = this.getIndiEntityLocation();

		try {

			this.internalCtxBroker.registerForChanges(new MyCtxChangeEventListener(this.internalCtxBroker,"room3RFID"), individualLocationAttr.getId());

			this.updateLocationCSSNode("room3RFID", CtxSourceNames.RFID, 1d/60);

			LOG.info("at this point a location update event is expected to be received");

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void testOnDemandInference(){

		LOG.info("start testing testOnDemandInference ");
	
		this.createCSSNodeLocationAttributes();

		CtxAttribute indiLocation = this.getIndiEntityLocation();

		LOG.info("1 indi entity current location :"+ indiLocation.getStringValue() +" should be null");

		// 1 update pz
		LOG.info("----- first update -----");
		LOG.info("1 PZ will update the CSS node entity with location value:RoomPZ and FreqId:1/60 with ctxSource name PZ0 ");
		CtxAttribute locationCssNodeAttrPZ = this.updateLocationCSSNode("room1PZ", CtxSourceNames.PZ+"0", 1d/60);
		LOG.info("1 update performed, check values");
		LOG.info("1 CSS node location value: "+locationCssNodeAttrPZ.getStringValue());
		LOG.info("1 CSS node location sourceID: "+locationCssNodeAttrPZ.getSourceId());
		LOG.info("1 CSS node location update freq: "+locationCssNodeAttrPZ.getQuality().getUpdateFrequency().toString());

		try {
			long start = System.currentTimeMillis();
			Thread.sleep(4000);
			long delay = System.currentTimeMillis() - start;
			LOG.info (" delayed for :"+ delay);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//check if indi entity is updated 
		indiLocation = this.getIndiEntityLocation();
		LOG.info("1-------> indi entity current location :"+ indiLocation.getStringValue()+" should be room1PZ");
		assertEquals("room1PZ",indiLocation.getStringValue());
		
		
		// 2 update rfid
		LOG.info("----- second update -----");
		LOG.info("2 RFID will update the CSS node entity with location value:Room1RFID and FreqId:1/60 with ctxSource name RFID0 ");
		CtxAttribute locationCssNodeAttrRFID = this.updateLocationCSSNode("room1RFID", CtxSourceNames.RFID+"0", 1d/60);
		LOG.info("2 update performed, check values");
		LOG.info("2 CSS node location value: "+locationCssNodeAttrRFID.getStringValue());
		LOG.info("2 CSS node location sourceID: "+locationCssNodeAttrRFID.getSourceId());
		LOG.info("2 CSS node location update freq: "+locationCssNodeAttrRFID.getQuality().getUpdateFrequency().toString());

		try {
			LOG.info ("start delay "+ System.currentTimeMillis());
			Thread.sleep(4000);
			LOG.info ("end delay "+ System.currentTimeMillis());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//check if indi entity is updated 
		indiLocation = this.getIndiEntityLocation();
		LOG.info("2-----> indi entity current location :"+ indiLocation.getStringValue()+" should be room1RFID");
		assertEquals("room1RFID",indiLocation.getStringValue());
	}


	// helper classes
	/*
	 * retrieve css node entity and update symbolic location
	 */
	private CtxAttribute updateLocationCSSNode(String locationValue, String sourceId, Double updateFreq){

		LOG.info("*** updateLocationCSSNode : updates an existing  Location attribute in CSS node");

		CtxEntity cssNodeEntity = null ;
		CtxAttribute locationCssNodeAttr_pz = null;
		CtxAttribute locationCssNodeAttr_rfid = null;
		CtxAttribute locationCssNodeAttrNull = null;

		try {

			cssNodeEntity = this.internalCtxBroker.retrieveCssNode(this.cssNodeId).get();
			Set<CtxAttribute> attrLocNodeSet = cssNodeEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
					List<CtxAttribute> attrLocList = new ArrayList<CtxAttribute>(attrLocNodeSet);

			if(attrLocList.size() > 0){

				for(CtxAttribute locationCssNodeAttr : attrLocList){
					LOG.info("update  location attribute "+locationCssNodeAttr.getId()  +" with source id "+locationCssNodeAttr.getSourceId()+" for source "+sourceId );
								
					if(locationCssNodeAttr.getSourceId().contains(CtxSourceNames.PZ) && sourceId.contains(CtxSourceNames.PZ)){
						//LOG.info("update PZ location attribute with value "+locationValue);
						locationCssNodeAttr_pz = locationCssNodeAttr;
						locationCssNodeAttr_pz.setStringValue(locationValue);
						locationCssNodeAttr_pz.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr_pz = (CtxAttribute) this.internalCtxBroker.update(locationCssNodeAttr_pz).get();
						return locationCssNodeAttr_pz;
					}
					
					if(locationCssNodeAttr.getSourceId().contains(CtxSourceNames.RFID) && sourceId.contains(CtxSourceNames.RFID)){
						LOG.info("update rfid location attribute with value"+locationValue);
						locationCssNodeAttr_rfid = locationCssNodeAttr;
						locationCssNodeAttr_rfid.setStringValue(locationValue);
						locationCssNodeAttr_rfid.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr_rfid = (CtxAttribute) this.internalCtxBroker.update(locationCssNodeAttr_rfid).get();
						return locationCssNodeAttr_rfid;					
					}
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
		}

		LOG.info("something went wrong when updating value "+locationValue+ " sourceId"+sourceId+" updateFreq"+updateFreq);

		return locationCssNodeAttrNull;
	}


	private void createCSSNodeLocationAttributes(){

		boolean createLocAttrRFID = true;
		boolean createLocAttrPZ = true;

		try {
			CtxEntity cssNodeEntity = this.internalCtxBroker.retrieveCssNode(this.cssNodeId).get();

			Set<CtxAttribute> attrLocSet = cssNodeEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC); 
			List<CtxAttribute> attrLocList = new ArrayList<CtxAttribute>(attrLocSet);

			for(CtxAttribute attrLoc : attrLocList){
					if(attrLoc.getSourceId().contains(CtxSourceNames.RFID)) createLocAttrRFID = false;
				
			}

			for(CtxAttribute attrLoc : attrLocList){
			
				if(attrLoc.getSourceId().contains(CtxSourceNames.PZ)) createLocAttrPZ = false;
				
			}

			if(createLocAttrRFID) {
				LOG.info("create RFID location attribute");
				CtxAttribute loc_rfid = this.internalCtxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
				loc_rfid.setSourceId(CtxSourceNames.RFID);
				this.internalCtxBroker.update(loc_rfid);
			}

			if(createLocAttrPZ) {
				LOG.info("create PZ location attribute");
				CtxAttribute loc_pz = this.internalCtxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
				loc_pz.setSourceId(CtxSourceNames.PZ);
				this.internalCtxBroker.update(loc_pz);
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



	/*
	 * retrieve individual entity symbolic location
	 */
	private CtxAttribute getIndiEntityLocation(){

		IndividualCtxEntity cssOwnerEntity;
		CtxAttribute locationIndiEntAttr = null;

		try {
			cssOwnerEntity = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();

			this.indiEntityId = cssOwnerEntity.getId();

			Set<CtxAttribute> locationAttrSet = cssOwnerEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
			if (locationAttrSet.iterator().hasNext()) locationIndiEntAttr = locationAttrSet.iterator().next();
			//LOG.info("Indi entity location attribute "+locationIndiEntAttr.getStringValue());

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

		return locationIndiEntAttr;
	}


	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		ICtxBroker ctxbroker = null;
		String expectedValue = null;

		MyCtxChangeEventListener(ICtxBroker ctxbroker, String value){
			this.ctxbroker = ctxbroker;
			expectedValue = value;
		}


		@Override
		public void onCreation(CtxChangeEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");

		}

		@Override
		public void onRemoval(CtxChangeEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpdate(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** UPDATED event ***");
			try {
				CtxAttribute locationAttr = (CtxAttribute) this.ctxbroker.retrieve(event.getId()).get();
				LOG.info("location value received:"+ locationAttr.getStringValue());
				LOG.info("location value expected:"+ expectedValue);
				//	TODO add assertEquals 


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

	}
}