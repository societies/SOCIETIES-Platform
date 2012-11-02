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



import java.net.URI;
import java.net.URISyntaxException;
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
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
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
		//2. registerForChanges(locationAttrId)
		//3. registerForChanges(indiEntId, LOCATION_SYMBOLIC)


		this.testOnDemandInference();
		//this.testContiniousInferenceByAttrID();
		//this.testContiniousInferenceByAttrType();

	}



	private void testOnDemandInference(){

		LOG.info("start testing testOnDemandInference ");

		CtxAttribute indiLocation = this.getIndiEntityLocation();
		LOG.info("1 indi entity current location :"+ indiLocation.getStringValue());

		
		// 1 update pz
		CtxAttribute locationCssNodeAttr = this.updateLocationCSSNode("room1PZ", CtxSourceNames.PZ+"0", 1d/60);
		LOG.info("1 PZ updated the CSS node entity with location value:Room1 and FreqId:1/60 with ctxSource name PZ0 ");
		LOG.info("1 CSS node location value: "+locationCssNodeAttr.getStringValue());
		LOG.info("1 CSS node location sourceID: "+locationCssNodeAttr.getSourceId());
		LOG.info("1 CSS node location update freq: "+locationCssNodeAttr.getQuality().getUpdateFrequency().toString());
		
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
		LOG.info("1 indi entity current location :"+ indiLocation.getStringValue()+" should be room1PZ");
		
		
		// 2 update rfid
		locationCssNodeAttr = this.updateLocationCSSNode("room1", CtxSourceNames.RFID+"0", 1d/60);
		LOG.info("2 PZ updated the CSS node entity with location value:Room1 and FreqId:1/60 with ctxSource name RFID ");
		LOG.info("2 CSS node location value: "+locationCssNodeAttr.getStringValue());
		LOG.info("2 CSS node location sourceID: "+locationCssNodeAttr.getSourceId());
		LOG.info("2 CSS node location update freq: "+locationCssNodeAttr.getQuality().getUpdateFrequency().toString());
		
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
		LOG.info("2 indi entity current location :"+ indiLocation.getStringValue()+" should be room1PZ");
			
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
		CtxAttribute locationCssNodeAttr = null;

		try {

			cssNodeEntity = this.internalCtxBroker.retrieveCssNode(this.cssNodeId).get();
			LOG.info("css node entity :"+cssNodeEntity.getId());
		
			Set<CtxAttribute> attrLocNodeSet = cssNodeEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
			LOG.info("css node  Set attribute :"+attrLocNodeSet);

			if (attrLocNodeSet.iterator().hasNext()) {

				locationCssNodeAttr = attrLocNodeSet.iterator().next();

				if(locationCssNodeAttr != null ){
					LOG.info("update  location attribute for source "+sourceId );
					if(locationCssNodeAttr.getSourceId().contains(CtxSourceNames.PZ)){
						LOG.info("update PZ location attribute");
						locationCssNodeAttr_pz = locationCssNodeAttr;
						locationCssNodeAttr_pz.setStringValue(locationValue);
						locationCssNodeAttr_pz.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr = (CtxAttribute) this.internalCtxBroker.update(locationCssNodeAttr_pz).get();
						
					} else if(locationCssNodeAttr.getSourceId().contains(CtxSourceNames.RFID)) {
						LOG.info("update rfid location attribute");
						locationCssNodeAttr_rfid = locationCssNodeAttr;
						locationCssNodeAttr_rfid.setStringValue(locationValue);
						locationCssNodeAttr_rfid.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr = (CtxAttribute) this.internalCtxBroker.update(locationCssNodeAttr_rfid).get();
					}
						
				} 
				
			
				
			} else if (locationCssNodeAttr == null) {
					LOG.info("create  location attribute for source "+sourceId );
					
					if(sourceId.contains(CtxSourceNames.PZ)){
						LOG.info("create PZ location attribute");
						CtxAttribute loc_pz = this.internalCtxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
						loc_pz.setStringValue(locationValue);
						loc_pz.setSourceId(sourceId);
						loc_pz.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr = (CtxAttribute) this.internalCtxBroker.update(loc_pz).get();	
					} else if(sourceId.contains(CtxSourceNames.RFID) ){
						LOG.info("create RFID location attribute");
						CtxAttribute loc_rfid = this.internalCtxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
						loc_rfid.setStringValue(locationValue);
						loc_rfid.setSourceId(sourceId);
						loc_rfid.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr = (CtxAttribute) this.internalCtxBroker.update(loc_rfid).get();	
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

		return locationCssNodeAttr;
	}


	/*
	 * retrieve individual entity and get symbolic location
	 */
	private CtxAttribute getIndiEntityLocation(){

		IndividualCtxEntity cssOwnerEntity;
		CtxAttribute locationIndiEntAttr = null;

		try {
			cssOwnerEntity = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();

			Set<CtxAttribute> locationAttrSet = cssOwnerEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
			if (locationAttrSet.iterator().hasNext()) locationIndiEntAttr = locationAttrSet.iterator().next();
			LOG.info("location attribute "+locationIndiEntAttr.getStringValue());

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
}