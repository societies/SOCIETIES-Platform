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

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
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
	
	private CountDownLatch lock = new CountDownLatch(1);

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
	public void testTrustUpdateEventListener() {

		LOG.info("*** BEGIN testTrustUpdateEventListener");
		
		Double oldTrustValue1 = null;
		final MyTrustUpdateEventListener listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateEventListener(
					listener, this.myTeid, this.teid1);
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		try {
			oldTrustValue1 = this.internalTrustBroker.retrieveTrust(
					this.myTeid, this.teid1).get();
		} catch (Exception e) {
			fail("Failed to retrieve trust: " + e.getLocalizedMessage());
		}
		
		try {
			// This should should trigger a TrustUpdateEvent that should *not* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, new Date(), new Random().nextDouble());
			// This should should trigger a TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED, new Date(), new Random().nextDouble());
		} catch (TrustException te) {
			
			fail("Failed to add trust rating: " + te.getLocalizedMessage());
		}
		
		try {
			boolean isLockReleased = this.lock.await(TestCase1962.getTimeout(), TimeUnit.MILLISECONDS);
			if (isLockReleased) {
				final TrustUpdateEvent event = listener.getEvent();
				assertNotNull("Received TrustUpdateEvent was null", event);
				assertEquals("Received trustorId was incorrect", this.myTeid, event.getTrustorId());
				assertEquals("Received trusteeId was incorrect", this.teid1, event.getTrusteeId());
				assertEquals("Received trust value type was incorrect", TrustValueType.USER_PERCEIVED, event.getValueType());
				assertEquals("Received old trust value was incorrect", oldTrustValue1, event.getOldValue());
				final Double newTrustValue1 = this.internalTrustBroker.retrieveTrust(
						this.myTeid, this.teid1).get();
				assertEquals("Received new trust value was incorrect", newTrustValue1, event.getNewValue());
			} else {
				fail("TrustUpdateEvent listener never received the event in the specified timeout: "
						+ TestCase1962.getTimeout() + " msec");
			}
		} catch (InterruptedException ie) {
			fail("Interrupted while executing test: " + ie.getLocalizedMessage());
		} catch (Exception e) {
			fail("Failed to retrieve trust value: " + e.getLocalizedMessage());
		}
		
		try {
			this.internalTrustBroker.unregisterTrustUpdateEventListener(
					listener, this.myTeid, this.teid1);
		} catch (TrustException te) {
			fail("Failed to unregister TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
		
		LOG.info("*** END testTrustUpdateEventListener");
	}
	
	private class MyTrustUpdateEventListener implements ITrustUpdateEventListener {

		private TrustUpdateEvent event;
		
		private TrustUpdateEvent getEvent() {
			
			return this.event;
		}
		
		/*
		 * @see org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.privacytrust.trust.event.TrustUpdateEvent)
		 */
		@Override
		public void onUpdate(TrustUpdateEvent event) {
			
			this.event = event;
			lock.countDown();
		}
	}
}