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
package org.societies.integration.test.bit.context.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
public class TestLocalUserCtxEventing {

	private static Logger LOG = LoggerFactory.getLogger(TestLocalUserCtxEventing.class);
	
	private static final String FOO_ENT_TYPE = "fooEntType";
	private static final String FOO_ATTR_TYPE = "fooAttrType";
	private static final String FOO_ATTR_VALUE = "fooAttrValue";
	private static final String FOO_ATTR_VALUE2 = "fooAttrValue2";
	
	private ICtxBroker internalCtxBroker;
	private ICommManager commMgr;

	private IIdentity cssOwnerId;
	
	private CtxAttributeIdentifier fooAttrId;
	private CtxAttributeIdentifier fooAttrId2;

	public TestLocalUserCtxEventing() {
	}

	@Before
	public void setUp() throws Exception {
		
		this.internalCtxBroker = TestCase.getCtxBroker();
		this.commMgr = TestCase.getCommManager();

		final String cssOwnerStr = 
				this.commMgr.getIdManager().getThisNetworkNode().getBareJid();
		this.cssOwnerId = this.commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);
		final CtxEntityIdentifier fooEntId =
				this.internalCtxBroker.createEntity(FOO_ENT_TYPE).get().getId();
		LOG.info("*** created fooEnt = " + fooEntId);
		this.fooAttrId = 
				this.internalCtxBroker.createAttribute(fooEntId, FOO_ATTR_TYPE).get().getId();
		LOG.info("*** created fooAttr = " + this.fooAttrId);
		this.fooAttrId2 = 
				this.internalCtxBroker.createAttribute(fooEntId, FOO_ATTR_TYPE).get().getId();
		LOG.info("*** created fooAttr2 = " + this.fooAttrId2);
	}
	
	@After
	public void tearDown() throws Exception {
		
		if (this.fooAttrId != null) 
				this.internalCtxBroker.remove(this.fooAttrId);
		LOG.info("*** removed fooAttr = " + this.fooAttrId);
		if (this.fooAttrId2 != null) 
			this.internalCtxBroker.remove(this.fooAttrId2);
		LOG.info("*** removed fooAttr2 = " + this.fooAttrId2);
	}

	@Test
	public void TestByAttrId() throws Exception {

		LOG.info("*** Start TestByAttrId ...");
		
		final MyCtxAttrChangeEventListener listener = 
				new MyCtxAttrChangeEventListener(); 
		LOG.info("*** registering for updates of fooAttr = " + this.fooAttrId);
		this.internalCtxBroker.registerForChanges(listener, this.fooAttrId);
		LOG.info("*** updating fooAttr = " + this.fooAttrId + " with value " + FOO_ATTR_VALUE);
		this.internalCtxBroker.updateAttribute(this.fooAttrId, FOO_ATTR_VALUE);
		LOG.info("*** updating fooAttr2 = " + this.fooAttrId2 + " with value " + FOO_ATTR_VALUE2);
		this.internalCtxBroker.updateAttribute(this.fooAttrId2, FOO_ATTR_VALUE2);
		// wait 2 secs
		Thread.sleep(2000);
		assertEquals(this.fooAttrId, listener.getReceivedId());
		assertEquals(FOO_ATTR_VALUE, listener.getReceivedValue());
		LOG.info("*** unregistering from updates of fooAttr = " + this.fooAttrId);
		this.internalCtxBroker.unregisterFromChanges(listener, this.fooAttrId);
	}
	
	@Test
	public void TestByAttrScopeType() throws Exception {

		LOG.info("*** Start TestByAttrScopeType ...");
		
		final MyCtxAttrChangeEventListener listener = 
				new MyCtxAttrChangeEventListener(); 
		LOG.info("*** registering for updates of '" + FOO_ATTR_TYPE + "' attributes under entity " + this.fooAttrId.getScope());
		this.internalCtxBroker.registerForChanges(listener, this.fooAttrId.getScope(), FOO_ATTR_TYPE);
		LOG.info("*** updating fooAttr = " + this.fooAttrId + " with value " + FOO_ATTR_VALUE);
		this.internalCtxBroker.updateAttribute(this.fooAttrId, FOO_ATTR_VALUE);
		// wait 2 secs
		Thread.sleep(2000);
		assertEquals(this.fooAttrId, listener.getReceivedId());
		assertEquals(FOO_ATTR_VALUE, listener.getReceivedValue());
		LOG.info("*** updating fooAttr2 = " + this.fooAttrId2 + " with value " + FOO_ATTR_VALUE2);
		this.internalCtxBroker.updateAttribute(this.fooAttrId2, FOO_ATTR_VALUE2);
		// wait 2 secs
		Thread.sleep(2000);
		assertEquals(this.fooAttrId2, listener.getReceivedId());
		assertEquals(FOO_ATTR_VALUE2, listener.getReceivedValue());
		LOG.info("*** unregistering from updates of '" + FOO_ATTR_TYPE + "' attributes under entity " + this.fooAttrId.getScope());
		this.internalCtxBroker.unregisterFromChanges(listener, this.fooAttrId.getScope(), FOO_ATTR_TYPE);
	}
	
	@Test
	public void TestByOwnerId() throws Exception {

		LOG.info("*** Start TestByOwnerId ...");
		
		final MyCtxAttrChangeEventListener listener = 
				new MyCtxAttrChangeEventListener(); 
		LOG.info("*** registering for updates under IIdentity " + this.cssOwnerId);
		this.internalCtxBroker.registerForChanges(listener, this.cssOwnerId);
		LOG.info("*** updating fooAttr = " + this.fooAttrId + " with value " + FOO_ATTR_VALUE);
		this.internalCtxBroker.updateAttribute(this.fooAttrId, FOO_ATTR_VALUE);
		// wait 2 secs
		Thread.sleep(2000);
		assertEquals(this.fooAttrId, listener.getReceivedId());
		assertEquals(FOO_ATTR_VALUE, listener.getReceivedValue());
		LOG.info("*** updating fooAttr2 = " + this.fooAttrId2 + " with value " + FOO_ATTR_VALUE2);
		this.internalCtxBroker.updateAttribute(this.fooAttrId2, FOO_ATTR_VALUE2);
		// wait 2 secs
		Thread.sleep(2000);
		assertEquals(this.fooAttrId2, listener.getReceivedId());
		assertEquals(FOO_ATTR_VALUE2, listener.getReceivedValue());
		LOG.info("*** unregistering from updates under IIdentity " + this.cssOwnerId);
		this.internalCtxBroker.unregisterFromChanges(listener, this.cssOwnerId);
	}

	private class MyCtxAttrChangeEventListener implements CtxChangeEventListener {

		private CtxIdentifier receivedId;
		private String receivedValue;

		private MyCtxAttrChangeEventListener() {
		}

		@Override
		public void onCreation(CtxChangeEvent event) {
			
			// TODO Auto-generated method stub
		}

		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");
			try {
				if (!event.getId().equals(fooAttrId) && !event.getId().equals(fooAttrId2))
						return;
				final CtxAttribute fooAttr = 
						(CtxAttribute) internalCtxBroker.retrieve(event.getId()).get();
				this.receivedId = fooAttr.getId();
				LOG.info("*** attribute id received:"+ this.receivedId);
				this.receivedValue = fooAttr.getStringValue();
				LOG.info("*** attribute value received:"+ this.receivedValue);

			} catch (Exception e) {
				
				fail("onModicication threw exception: " + e.getLocalizedMessage());
			}
		}

		@Override
		public void onRemoval(CtxChangeEvent event) {
			
			// TODO Auto-generated method stub
		}

		@Override
		public void onUpdate(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** UPDATED event ***");
			try {
				if (!event.getId().equals(fooAttrId) && !event.getId().equals(fooAttrId2))
					return;
				final CtxAttribute fooAttr =
						(CtxAttribute) internalCtxBroker.retrieve(event.getId()).get();
				this.receivedId = fooAttr.getId();
				LOG.info("*** attribute id received:"+ this.receivedId);
				this.receivedValue = fooAttr.getStringValue();
				LOG.info("*** attribute value received:"+ this.receivedValue);

			} catch (Exception e) {
				
				fail("onUpdate threw exception: " + e.getLocalizedMessage());
			} 
		}
		
		private CtxIdentifier getReceivedId() {
			
			return this.receivedId;
		}
		
		private String getReceivedValue() {
			
			return this.receivedValue;
		}
	}
}