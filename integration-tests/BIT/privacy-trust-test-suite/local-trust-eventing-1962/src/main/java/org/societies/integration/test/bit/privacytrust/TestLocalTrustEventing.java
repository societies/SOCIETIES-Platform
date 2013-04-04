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
package org.societies.integration.test.bit.privacytrust;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

/**
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
public class TestLocalTrustEventing {

	private static Logger LOG = LoggerFactory.getLogger(TestLocalTrustEventing.class);
	
	private static final String USER_ID1 = "bob.societies.local";
	private static final String USER_ID2 = "alice.societies.local";

	private ITrustEvidenceCollector internalTrustEvidenceCollector;
	private ITrustBroker internalTrustBroker;
	private ICommManager commMgr;

	private IIdentity myUserId;
	private IIdentity userId1;
	private IIdentity userId2;
	
	private TrustedEntityId myTeid;
	private TrustedEntityId teid1;
	private TrustedEntityId teid2;
	
	private CountDownLatch lock;
	
	/** Hack to overcome MySQL inability to store millisecond info. */
	private static int count = 0;

	public TestLocalTrustEventing() {
	}

	/**
	 * The {@link #setUp()} method instantiates the TrustedEntityIds for the
	 * running CSS, as well as, {@link #USER_ID1} and {@link #USER_ID2}.
	 */
	@Before
	public void setUp() throws Exception {
		
		this.internalTrustEvidenceCollector = TestCase1962.getInternalTrustEvidenceCollector();
		this.internalTrustBroker = TestCase1962.getInternalTrustBroker();
		this.commMgr = TestCase1962.getCommManager();

		// setup my IDs
		final String myUserIdStr = 
				this.commMgr.getIdManager().getThisNetworkNode().getBareJid();
		this.myUserId = this.commMgr.getIdManager().fromJid(myUserIdStr);
		LOG.info("*** myUserId = " + this.myUserId);
		this.myTeid = new TrustedEntityId(TrustedEntityType.CSS, this.myUserId.toString());
		LOG.info("*** myTeid = " + this.myTeid);
		
		// setup User 1 IDs
		this.userId1 = this.commMgr.getIdManager().fromJid(USER_ID1);
		LOG.info("*** userId1 = " + this.userId1);
		this.teid1 = new TrustedEntityId(TrustedEntityType.CSS, this.userId1.toString());
		LOG.info("*** teid1 = " + this.teid1);
		
		// setup User 2 IDs
		this.userId2 = this.commMgr.getIdManager().fromJid(USER_ID2);
		LOG.info("*** userId2 = " + this.userId2);
		this.teid2 = new TrustedEntityId(TrustedEntityType.CSS, this.userId2.toString());
		LOG.info("*** teid2 = " + this.teid2);
	}
	
	@After
	public void tearDown() throws Exception {
		
		// TODO
		// 1. remove test trust data db? currently not supported
	}

	@Test
	public void testTrustUpdateListenerByTrustor() {

		LOG.info("*** testTrustUpdateListenerByTrustor BEGIN");
		
		this.lock = new CountDownLatch(4);
		
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					listener, this.myTeid);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustor adding trust ratings");
		try {
			// This should should trigger two TrustUpdateEvents that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
			// This should should trigger two TrustUpdateEvents that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			boolean isLockReleased = this.lock.await(TestCase1962.getTimeout(), TimeUnit.MILLISECONDS);
			if (isLockReleased) {
				
				// verify four events were received
				assertEquals("Did not receive expected event(s)", 4, listener.events.size());
				
				boolean foundDirect1 = false;
				boolean foundUserPerceived1 = false;
				boolean foundDirect2 = false;
				boolean foundUserPerceived2 = false;
				Double trustValue = null;
				
				for (final TrustUpdateEvent event : listener.events) {
				
					assertNotNull("Received TrustRelationship was null", event.getTrustRelationship());
					assertEquals("Received trustorId was incorrect", this.myTeid, 
							event.getTrustRelationship().getTrustorId());
					
					// verify DIRECT trust update event for User 1
					if (this.teid1.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.DIRECT == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid1, TrustValueType.DIRECT).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundDirect1 = true;
						
					// verify USER_PERCEIVED trust update event for User 1
					} else if (this.teid1.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid1, TrustValueType.USER_PERCEIVED).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundUserPerceived1 = true;
						
					// verify DIRECT trust update event for User 2
					} else if (this.teid2.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.DIRECT == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid2, TrustValueType.DIRECT).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundDirect2 = true;
						
					// verify USER_PERCEIVED trust update event for User 2
					} else if (this.teid2.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid2, TrustValueType.USER_PERCEIVED).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundUserPerceived2 = true;
					}
				}
				
				assertTrue("Did not receive DIRECT TrustUpdateEvent for User 1", foundDirect1);
				assertTrue("Did not receive USER_PERCEIVED TrustUpdateEvent for User 1", foundUserPerceived1);
				assertTrue("Did not receive DIRECT TrustUpdateEvent for User 2", foundDirect2);
				assertTrue("Did not receive USER_PERCEIVED TrustUpdateEvent for User 2", foundUserPerceived2);
				
			} else {
				fail("TrustUpdateEvent listener never received the event(s) in the specified timeout: "
						+ TestCase1962.getTimeout() + " msec");
			}
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		} catch (ExecutionException ee) {
			fail("Interrupted while retrieving trust value: " + ee.getLocalizedMessage());
		} catch (TrustException te) {
			fail("Failed to retrieve trust value: " + te.getLocalizedMessage());
		}
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateListener(
					listener, this.myTeid);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustor END");
	}
	
	@Test
	public void testTrustUpdateListenerByTrustorAndTrustee() {

		LOG.info("*** testTrustUpdateListenerByTrustorAndTrustee BEGIN");
		
		this.lock = new CountDownLatch(2);
		
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					listener, this.myTeid, this.teid1);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndTrustee adding trust ratings");
		try {
			// This should should trigger two TrustUpdateEvents that should *not* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
			// This should should trigger two TrustUpdateEvents that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED,
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			boolean isLockReleased = this.lock.await(TestCase1962.getTimeout(), TimeUnit.MILLISECONDS);
			if (isLockReleased) {
				
				// verify two events were received
				assertEquals("Did not receive expected event(s)", 2, listener.events.size());
				
				// verify DIRECT trust update event
				TrustUpdateEvent event = listener.events.get(0);
				assertNotNull("Received TrustUpdateEvent was null", event);
				assertNotNull("Received TrustRelationship was null", event.getTrustRelationship());
				assertEquals("Received trustorId was incorrect", this.myTeid, 
						event.getTrustRelationship().getTrustorId());
				assertEquals("Received trusteeId was incorrect", this.teid1, 
						event.getTrustRelationship().getTrusteeId());
				assertEquals("Received trust value type was incorrect", TrustValueType.DIRECT,
						event.getTrustRelationship().getTrustValueType());
				Double newTrustValue1 = this.internalTrustBroker.retrieveTrustValue(
						this.myTeid, this.teid1, TrustValueType.DIRECT).get();
				assertEquals("Received DIRECT trust value was incorrect", newTrustValue1, 
						event.getTrustRelationship().getTrustValue());
				assertNotNull("Received timestamp was null", 
						event.getTrustRelationship().getTimestamp());
				
				// verify USER_PERCEIVED trust update event
				event = listener.events.get(1);
				assertNotNull("Received TrustUpdateEvent was null", event);
				assertNotNull("Received TrustRelationship was null", event.getTrustRelationship());
				assertEquals("Received trustorId was incorrect", this.myTeid, 
						event.getTrustRelationship().getTrustorId());
				assertEquals("Received trusteeId was incorrect", this.teid1, 
						event.getTrustRelationship().getTrusteeId());
				assertEquals("Received trust value type was incorrect", TrustValueType.USER_PERCEIVED,
						event.getTrustRelationship().getTrustValueType());
				newTrustValue1 = this.internalTrustBroker.retrieveTrustValue(
						this.myTeid, this.teid1, TrustValueType.USER_PERCEIVED).get();
				assertEquals("Received USER_PERCEIVED trust value was incorrect", newTrustValue1, 
						event.getTrustRelationship().getTrustValue());
				assertNotNull("Received timestamp was null", 
						event.getTrustRelationship().getTimestamp());
				
			} else {
				fail("TrustUpdateEvent listener never received the event(s) in the specified timeout: "
						+ TestCase1962.getTimeout() + " msec");
			}
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		} catch (ExecutionException ee) {
			fail("Interrupted while retrieving trust value: " + ee.getLocalizedMessage());
		} catch (TrustException te) {
			fail("Failed to retrieve trust value: " + te.getLocalizedMessage());
		}
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateListener(
					listener, this.myTeid, this.teid1);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndTrustee END");
	}
	
	@Test
	public void testTrustUpdateEventListenerByTrustorAndTrusteeAndValueType() {

		LOG.info("*** testTrustUpdateEventListenerByTrustorAndTrusteeAndValueType BEGIN");
		
		this.lock = new CountDownLatch(1);
		
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					listener, this.myTeid, this.teid1, TrustValueType.DIRECT);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndTrusteeAndValueType adding trust ratings");
		try {
			// This should should trigger a DIRECT TrustUpdateEvent that should *not* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
			// This should should trigger a DIRECT TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			boolean isLockReleased = this.lock.await(TestCase1962.getTimeout(), TimeUnit.MILLISECONDS);
			if (isLockReleased) {
				
				// verify event were received
				assertEquals("Did not receive expected event(s)", 1, listener.events.size());
				
				// verify DIRECT trust update event
				TrustUpdateEvent event = listener.events.get(0);
				assertNotNull("Received TrustUpdateEvent was null", event);
				assertNotNull("Received TrustRelationship was null", event.getTrustRelationship());
				assertEquals("Received trustorId was incorrect", this.myTeid, 
						event.getTrustRelationship().getTrustorId());
				assertEquals("Received trusteeId was incorrect", this.teid1, 
						event.getTrustRelationship().getTrusteeId());
				assertEquals("Received trust value type was incorrect", TrustValueType.DIRECT,
						event.getTrustRelationship().getTrustValueType());
				Double newTrustValue1 = this.internalTrustBroker.retrieveTrustValue(
						this.myTeid, this.teid1, TrustValueType.DIRECT).get();
				assertEquals("Received new trust value was incorrect", newTrustValue1, 
						event.getTrustRelationship().getTrustValue());
				assertNotNull("Received timestamp was null", 
						event.getTrustRelationship().getTimestamp());
				
			} else {
				fail("TrustUpdateEvent listener never received the event(s) in the specified timeout: "
						+ TestCase1962.getTimeout() + " msec");
			}
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		} catch (ExecutionException ee) {
			fail("Interrupted while retrieving trust value: " + ee.getLocalizedMessage());
		} catch (TrustException te) {
			fail("Failed to retrieve trust value: " + te.getLocalizedMessage());
		}
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateListener(
					listener, this.myTeid, this.teid1, TrustValueType.DIRECT);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateEventListenerByTrustorAndTrusteeAndValueType END");
	}
	
	@Test
	public void testTrustUpdateListenerByTrustorAndCssType() {

		LOG.info("*** testTrustUpdateListenerByTrustorAndCssType BEGIN");
		
		this.lock = new CountDownLatch(4);
		
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					listener, this.myTeid, TrustedEntityType.CSS);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndCssType adding trust ratings");
		try {
			// This should should trigger two TrustUpdateEvents that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
			// This should should trigger two TrustUpdateEvents that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			boolean isLockReleased = this.lock.await(TestCase1962.getTimeout(), TimeUnit.MILLISECONDS);
			if (isLockReleased) {
				
				// verify four events were received
				assertEquals("Did not receive expected event(s)", 4, listener.events.size());
				
				boolean foundDirect1 = false;
				boolean foundUserPerceived1 = false;
				boolean foundDirect2 = false;
				boolean foundUserPerceived2 = false;
				Double trustValue = null;
				
				for (final TrustUpdateEvent event : listener.events) {
				
					assertNotNull("Received TrustRelationship was null", event.getTrustRelationship());
					assertEquals("Received trustorId was incorrect", this.myTeid, 
							event.getTrustRelationship().getTrustorId());
					
					// verify DIRECT trust update event for User 1
					if (this.teid1.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.DIRECT == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid1, TrustValueType.DIRECT).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundDirect1 = true;
						
					// verify USER_PERCEIVED trust update event for User 1
					} else if (this.teid1.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid1, TrustValueType.USER_PERCEIVED).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundUserPerceived1 = true;
						
					// verify DIRECT trust update event for User 2
					} else if (this.teid2.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.DIRECT == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid2, TrustValueType.DIRECT).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundDirect2 = true;
						
					// verify USER_PERCEIVED trust update event for User 2
					} else if (this.teid2.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid2, TrustValueType.USER_PERCEIVED).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundUserPerceived2 = true;
					}
				}
				
				assertTrue("Did not receive DIRECT TrustUpdateEvent for User 1", foundDirect1);
				assertTrue("Did not receive USER_PERCEIVED TrustUpdateEvent for User 1", foundUserPerceived1);
				assertTrue("Did not receive DIRECT TrustUpdateEvent for User 2", foundDirect2);
				assertTrue("Did not receive USER_PERCEIVED TrustUpdateEvent for User 2", foundUserPerceived2);
				
			} else {
				fail("TrustUpdateEvent listener never received the event(s) in the specified timeout: "
						+ TestCase1962.getTimeout() + " msec");
			}
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		} catch (ExecutionException ee) {
			fail("Interrupted while retrieving trust value: " + ee.getLocalizedMessage());
		} catch (TrustException te) {
			fail("Failed to retrieve trust value: " + te.getLocalizedMessage());
		}
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateListener(
					listener, this.myTeid, TrustedEntityType.CSS);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndCssType END");
	}
	
	@Test
	public void testTrustUpdateListenerByTrustorAndCisType() {

		LOG.info("*** testTrustUpdateListenerByTrustorAndCisType BEGIN");
		
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					listener, this.myTeid, TrustedEntityType.CIS);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndCisType adding trust ratings");
		try {
			// This should should trigger two TrustUpdateEvents that must *not* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
			// This should should trigger two TrustUpdateEvents that must *not* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			Thread.sleep(TestCase1962.getTimeout());	
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		}
		
		// verify no events were received
		assertEquals("Received unexpected event(s)", 0, listener.events.size());
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateListener(
					listener, this.myTeid, TrustedEntityType.CIS);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndCisType END");
	}
	
	@Test
	public void testTrustUpdateListenerByTrustorAndValueType() {

		LOG.info("*** testTrustUpdateListenerByTrustorAndValueType BEGIN");
		
		this.lock = new CountDownLatch(2);
		
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					listener, this.myTeid, TrustValueType.USER_PERCEIVED);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndValueType adding trust ratings");
		try {
			// This should should trigger one TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
			// This should should trigger one TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED,
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			boolean isLockReleased = this.lock.await(TestCase1962.getTimeout(), TimeUnit.MILLISECONDS);
			if (isLockReleased) {
				
				// verify two events were received
				assertEquals("Did not receive expected event(s)", 2, listener.events.size());
				
				boolean foundUserPerceived1 = false;
				boolean foundUserPerceived2 = false;
				Double trustValue = null;
				
				for (final TrustUpdateEvent event : listener.events) {
				
					assertNotNull("Received TrustRelationship was null", event.getTrustRelationship());
					assertEquals("Received trustorId was incorrect", this.myTeid, 
							event.getTrustRelationship().getTrustorId());
						
					// verify USER_PERCEIVED trust update event for User 1
					if (this.teid1.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid1, TrustValueType.USER_PERCEIVED).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundUserPerceived1 = true;
						
					// verify USER_PERCEIVED trust update event for User 2
					} else if (this.teid2.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid2, TrustValueType.USER_PERCEIVED).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundUserPerceived2 = true;
					}
				}
				
				assertTrue("Did not receive USER_PERCEIVED TrustUpdateEvent for User 1", foundUserPerceived1);
				assertTrue("Did not receive USER_PERCEIVED TrustUpdateEvent for User 2", foundUserPerceived2);
				
			} else {
				fail("TrustUpdateEvent listener never received the event(s) in the specified timeout: "
						+ TestCase1962.getTimeout() + " msec");
			}
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		} catch (ExecutionException ee) {
			fail("Interrupted while retrieving trust value: " + ee.getLocalizedMessage());
		} catch (TrustException te) {
			fail("Failed to retrieve trust value: " + te.getLocalizedMessage());
		}
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateListener(
					listener, this.myTeid, TrustValueType.USER_PERCEIVED);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndValueType END");
	}
	
	@Test
	public void testTrustUpdateListenerByTrustorAndTrusteeTypeAndValueType() {

		LOG.info("*** testTrustUpdateListenerByTrustorAndTrusteeTypeAndValueType BEGIN");
		
		this.lock = new CountDownLatch(2);
		
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					listener, this.myTeid, TrustedEntityType.CSS, TrustValueType.DIRECT);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndTrusteeTypeAndValueType adding trust ratings");
		try {
			// This should should trigger one TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
			// This should should trigger one TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED,
					new Date(new Date().getTime() + 1000 * count++), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			boolean isLockReleased = this.lock.await(TestCase1962.getTimeout(), TimeUnit.MILLISECONDS);
			if (isLockReleased) {
				
				// verify two events were received
				assertEquals("Did not receive expected event(s)", 2, listener.events.size());
				
				boolean foundDirect1 = false;
				boolean foundDirect2 = false;
				Double trustValue = null;
				
				for (final TrustUpdateEvent event : listener.events) {
				
					assertNotNull("Received TrustRelationship was null", event.getTrustRelationship());
					assertEquals("Received trustorId was incorrect", this.myTeid, 
							event.getTrustRelationship().getTrustorId());
						
					// verify DIRECT trust update event for User 1
					if (this.teid1.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.DIRECT == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid1, TrustValueType.DIRECT).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundDirect1 = true;
						
					// verify DIRECT trust update event for User 2
					} else if (this.teid2.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.DIRECT == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								this.myTeid, this.teid2, TrustValueType.DIRECT).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundDirect2 = true;
					}
				}
				
				assertTrue("Did not receive DIRECT TrustUpdateEvent for User 1", foundDirect1);
				assertTrue("Did not receive DIRECT TrustUpdateEvent for User 2", foundDirect2);
				
			} else {
				fail("TrustUpdateEvent listener never received the event(s) in the specified timeout: "
						+ TestCase1962.getTimeout() + " msec");
			}
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		} catch (ExecutionException ee) {
			fail("Interrupted while retrieving trust value: " + ee.getLocalizedMessage());
		} catch (TrustException te) {
			fail("Failed to retrieve trust value: " + te.getLocalizedMessage());
		}
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateListener(
					listener, this.myTeid, TrustedEntityType.CSS, TrustValueType.DIRECT);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndTrusteeTypeAndValueType END");
	}
	
	private class MyTrustUpdateEventListener implements ITrustUpdateEventListener {

		private final List<TrustUpdateEvent> events = new ArrayList<TrustUpdateEvent>();
		
		/*
		 * @see org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.privacytrust.trust.event.TrustUpdateEvent)
		 */
		@Override
		public void onUpdate(TrustUpdateEvent event) {
			
			LOG.info("*** " + this + " received event " + event + " at " + new Date());
			this.events.add(event);
			lock.countDown();
		}
	}
}