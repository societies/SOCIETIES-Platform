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

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * 
 *
 * @author nikosk
 *
 */
public class Tester {

	private static Logger LOG = LoggerFactory.getLogger(Tester.class);
	
	private ICtxBroker internalCtxBroker;
	private ICommManager commMgr;

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;
	
	private CtxAttributeIdentifier userSymbolicLocationAttrId;
	private CtxAttributeIdentifier pzNodeSymbolicLocationAttrId;
	private CtxAttributeIdentifier rfidNodeSymbolicLocationAttrId;

	public Tester() {
	}

	@Before
	public void setUp() throws Exception {
		
		this.internalCtxBroker = TestLocationRefiner.getCtxBroker();
		this.commMgr = TestLocationRefiner.getCommManager();

		this.cssNodeId = this.commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);
		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssOwnerId = this.commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);
		this.userSymbolicLocationAttrId = this.getUserSymbolicLocationAttributeId();
		assertNotNull(this.userSymbolicLocationAttrId);
		LOG.info("*** userSymbolicLocationAttrId = " + this.userSymbolicLocationAttrId);
		this.createCSSNodeLocationAttributes();
		assertNotNull(this.pzNodeSymbolicLocationAttrId);
		LOG.info("*** pzNodeSymbolicLocationAttrId = " + this.pzNodeSymbolicLocationAttrId);
		assertNotNull(this.rfidNodeSymbolicLocationAttrId);
		LOG.info("*** rfidNodeSymbolicLocationAttrId = " + this.rfidNodeSymbolicLocationAttrId);
	}

	@Test
	public void TestLocationRefinement() throws Exception {

		LOG.info("*** Start testing ...");
		//1. retrieve symbolic location (on-demand inference)
		this.testOnDemandInference();

		//2. registerForChanges(locationAttrId)
		this.testContiniousInferenceByAttrId();

		//3. registerForChanges(indiEntId, LOCATION_SYMBOLIC)
		//this.testContiniousInferenceByAttrType();
	}

	private void testOnDemandInference() throws Exception {

		LOG.info("start testing testOnDemandInference...");

		CtxAttribute userSymbolicLocationAttr = this.internalCtxBroker.retrieveAttribute(
				this.userSymbolicLocationAttrId, false).get();
		assertNotNull(userSymbolicLocationAttr);

		LOG.info("1 indi entity current location :"+ userSymbolicLocationAttr.getStringValue());

		LOG.info("----- 1st set of updates: PZ should be fresh -----");
		// 1 update pz
		LOG.info("1 PZ will update the CSS node entity with location value:Room1PZ and FreqId:1/5");
		CtxAttribute locationCssNodeAttrPZ = this.updateLocationCSSNode("room1PZ", CtxSourceNames.PZ, 1d/5);
		assertEquals("room1PZ", locationCssNodeAttrPZ.getStringValue());
		assertTrue(locationCssNodeAttrPZ.getSourceId().contains(CtxSourceNames.PZ));
		assertEquals(1d/5, locationCssNodeAttrPZ.getQuality().getUpdateFrequency(), 1e-3);
		
		// 1 update rfid
		LOG.info("1 RFID will update the CSS node entity with location value:Room1RFID and FreqId:1/2");
		CtxAttribute locationCssNodeAttrRFID = this.updateLocationCSSNode("room1RFID", CtxSourceNames.RFID, 1d/2);
		assertEquals("room1RFID", locationCssNodeAttrRFID.getStringValue());
		assertTrue(locationCssNodeAttrRFID.getSourceId().contains(CtxSourceNames.RFID));
		assertEquals(1d/2, locationCssNodeAttrRFID.getQuality().getUpdateFrequency(), 1e-3);

		LOG.info("---- waiting 3 sec ----");
		Thread.sleep(3000);
		
		// 1 verify
		LOG.info("1 Calling internalCtxBroker.retrieve(" + this.userSymbolicLocationAttrId + ") to trigger on-demand inference"); 
		userSymbolicLocationAttr = (CtxAttribute) 
				this.internalCtxBroker.retrieve(this.userSymbolicLocationAttrId).get();
		LOG.info("1-------> individual entity current location:" 
				+ userSymbolicLocationAttr.getStringValue() + " should be " + locationCssNodeAttrPZ.getStringValue());
		assertEquals(locationCssNodeAttrPZ.getStringValue(), userSymbolicLocationAttr.getStringValue());
		assertEquals(CtxOriginType.INFERRED, userSymbolicLocationAttr.getQuality().getOriginType());
		assertEquals(locationCssNodeAttrPZ.getQuality().getUpdateFrequency(),
				userSymbolicLocationAttr.getQuality().getUpdateFrequency(), 1e-3);
		
		LOG.info("----- 2nd set of updates: Both PZ and RFID should be fresh -----");
		// 2 update pz
		LOG.info("2 PZ will update the CSS node entity with location value:Room2PZ and FreqId:1/60");
		locationCssNodeAttrPZ = this.updateLocationCSSNode("room2PZ", CtxSourceNames.PZ, 1d/60);
		assertEquals("room2PZ", locationCssNodeAttrPZ.getStringValue());
		assertTrue(locationCssNodeAttrPZ.getSourceId().contains(CtxSourceNames.PZ));
		assertEquals(1d/60, locationCssNodeAttrPZ.getQuality().getUpdateFrequency(), 1e-3);
				
		// 2 update rfid
		LOG.info("2 RFID will update the CSS node entity with location value:Room2RFID and FreqId:1/60");
		locationCssNodeAttrRFID = this.updateLocationCSSNode("room2RFID", CtxSourceNames.RFID, 1d/60);
		assertEquals("room2RFID", locationCssNodeAttrRFID.getStringValue());
		assertTrue(locationCssNodeAttrRFID.getSourceId().contains(CtxSourceNames.RFID));
		assertEquals(1d/60, locationCssNodeAttrRFID.getQuality().getUpdateFrequency(), 1e-3);

		LOG.info("---- waiting 3 sec ----");
		Thread.sleep(3000);
		
		// 2 verify
		LOG.info("2 Calling internalCtxBroker.retrieve(" + this.userSymbolicLocationAttrId + ") to trigger on-demand inference"); 
		userSymbolicLocationAttr = (CtxAttribute) 
				this.internalCtxBroker.retrieve(this.userSymbolicLocationAttrId).get();
		LOG.info("2-------> individual entity current location:" 
				+ userSymbolicLocationAttr.getStringValue() + " should be " + locationCssNodeAttrRFID.getStringValue());
		assertEquals(locationCssNodeAttrRFID.getStringValue(), userSymbolicLocationAttr.getStringValue());
		assertEquals(CtxOriginType.INFERRED, userSymbolicLocationAttr.getQuality().getOriginType());
		assertEquals(locationCssNodeAttrRFID.getQuality().getUpdateFrequency(),
				userSymbolicLocationAttr.getQuality().getUpdateFrequency(), 1e-3);
	}

	private void testContiniousInferenceByAttrId() throws Exception {

		LOG.info("start testing testContiniousInferenceByAttrID...");
		//1. register for changes on individual entity location 
		//2. update location in css node
		//3. receive update in listener and verify value

		final MyCtxChangeEventListener listener = new MyCtxChangeEventListener("room3RFID");
		LOG.info("registering for changes of " + this.userSymbolicLocationAttrId);
		this.internalCtxBroker.registerForChanges(listener,	this.userSymbolicLocationAttrId);

		this.updateLocationCSSNode("room3RFID", CtxSourceNames.RFID, 1d/60);

		LOG.info("at this point a location update event is expected to be received");
		LOG.info("*** Sleeping for 10 sec");
		Thread.sleep(10000);
		final CtxAttribute userSymLocAttr = 
				this.internalCtxBroker.retrieveAttribute(this.userSymbolicLocationAttrId, false).get();
		final String userSymLocValue = userSymLocAttr.getStringValue(); 
		LOG.info("unregistering from changes of " + this.userSymbolicLocationAttrId);
		this.internalCtxBroker.unregisterFromChanges(listener, this.userSymbolicLocationAttrId);
		assertEquals("room3RFID", userSymLocValue);
	}
	
	// helper methods
	/*
	 * retrieve css node entity and update symbolic location
	 */
	private CtxAttribute updateLocationCSSNode(String locationValue, String source,
			Double updateFreq) throws Exception {

		LOG.info("*** updateLocationCSSNode: locationValue=" + locationValue + ", source=" + source);
		CtxAttribute result = null;
		if (CtxSourceNames.PZ.equals(source))
			result = this.internalCtxBroker.retrieveAttribute(this.pzNodeSymbolicLocationAttrId, false).get();
		else if (CtxSourceNames.RFID.equals(source))
			result = this.internalCtxBroker.retrieveAttribute(this.rfidNodeSymbolicLocationAttrId, false).get();
		else
			fail("Failed to update SYMBOLIC_LOCATION under CSS node entity");
	
		result.setStringValue(locationValue);
		result.getQuality().setUpdateFrequency(updateFreq);
		
		return (CtxAttribute) this.internalCtxBroker.update(result).get();
	}

	private void createCSSNodeLocationAttributes() throws Exception {

		boolean createLocAttrRFID = true;
		boolean createLocAttrPZ = true;

		final CtxEntity cssNodeEntity = this.internalCtxBroker.retrieveCssNode(this.cssNodeId).get();

		final Set<CtxAttribute> attrLocSet = cssNodeEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);

		for (final CtxAttribute attrLoc : attrLocSet) {
			
			if(attrLoc.getSourceId().contains(CtxSourceNames.RFID)) {
				this.rfidNodeSymbolicLocationAttrId = attrLoc.getId();
				createLocAttrRFID = false;
			} else if(attrLoc.getSourceId().contains(CtxSourceNames.PZ)) {
				this.pzNodeSymbolicLocationAttrId = attrLoc.getId();
				createLocAttrPZ = false;
			}
		}

		if (createLocAttrRFID) {
			LOG.info("create RFID location attribute");
			final CtxAttribute loc_rfid = 
					this.internalCtxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			loc_rfid.setSourceId(CtxSourceNames.RFID+"0");
			this.internalCtxBroker.update(loc_rfid);
			this.rfidNodeSymbolicLocationAttrId = loc_rfid.getId();
		}

		if (createLocAttrPZ) {
			LOG.info("create PZ location attribute");
			final CtxAttribute loc_pz = 
					this.internalCtxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			loc_pz.setSourceId(CtxSourceNames.PZ+"0");
			this.internalCtxBroker.update(loc_pz);
			this.pzNodeSymbolicLocationAttrId = loc_pz.getId();
		}
	}

	/*
	 * retrieve individual entity symbolic location attribute id
	 */
	private CtxAttributeIdentifier getUserSymbolicLocationAttributeId() throws Exception {

		final IndividualCtxEntity cssOwnerEntity = 
				this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
		final Set<CtxAttribute> locationAttrSet = cssOwnerEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
		assertFalse("CSS owner entity does not contain LOCATION_SYMBOLIC attribute",
				locationAttrSet.isEmpty());
		assertTrue("CSS owner entity should contain exactly one LOCATION_SYMBOLIC attribute",
				locationAttrSet.size() == 1);
		return locationAttrSet.iterator().next().getId();
	}

	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		private final String expectedValue;

		private MyCtxChangeEventListener(String value) {
			
			this.expectedValue = value;
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
				CtxAttribute locationAttr = (CtxAttribute) internalCtxBroker.retrieve(event.getId()).get();
				LOG.info("location value expected:"+ expectedValue);
				LOG.info("location value received:"+ locationAttr.getStringValue());
				assertEquals(this.expectedValue, locationAttr.getStringValue());

			} catch (Exception e) {
				
				fail("onUpdate threw exception: " + e.getLocalizedMessage());
			} 
		}
	}
}