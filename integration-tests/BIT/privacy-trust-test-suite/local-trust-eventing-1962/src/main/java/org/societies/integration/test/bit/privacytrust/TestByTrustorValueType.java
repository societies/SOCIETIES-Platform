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
import org.societies.api.privacytrust.trust.TrustQuery;
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
public class TestByTrustorValueType {

	private static Logger LOG = LoggerFactory.getLogger(TestByTrustorValueType.class);
	
	private static final String SERVICE_ID1 = "svc1.societies.local";
	private static final String SERVICE_ID2 = "svc2.societies.local";

	private ITrustEvidenceCollector internalTrustEvidenceCollector;
	private ITrustBroker internalTrustBroker;
	private ICommManager commMgr;

	private IIdentity myUserId;
	
	private TrustedEntityId myTeid;
	private TrustedEntityId teid1;
	private TrustedEntityId teid2;
	
	private MyTrustUpdateEventListener listener;
	private CountDownLatch lock;

	public TestByTrustorValueType() {
	}

	/**
	 * The {@link #setUp()} method instantiates the TrustedEntityIds for the
	 * running CSS, as well as, {@link #SERVICE_ID1} and {@link #SERVICE_ID2}.
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
		
		// setup Service 1 IDs
		this.teid1 = new TrustedEntityId(TrustedEntityType.SVC, SERVICE_ID1);
		LOG.info("*** teid1 = " + this.teid1);
		
		// setup Service 2 IDs
		this.teid2 = new TrustedEntityId(TrustedEntityType.SVC, SERVICE_ID2);
		LOG.info("*** teid2 = " + this.teid2);
		
		// This should establish a DIRECT trust relationship with Service 1
		this.internalTrustEvidenceCollector.addDirectEvidence(
				this.myTeid, this.teid1, TrustEvidenceType.USED_SERVICE, 
				new Date(), null);
		// This should establish a DIRECT trust relationship with Service 2
		this.internalTrustEvidenceCollector.addDirectEvidence(
				this.myTeid, this.teid2, TrustEvidenceType.USED_SERVICE, 
				new Date(), null);

		Thread.sleep(TestCase1962.getTimeout());
		
		this.listener = new MyTrustUpdateEventListener();
		try {
			this.internalTrustBroker.registerTrustUpdateListener(
					this.listener, new TrustQuery(this.myTeid)
					.setTrustValueType(TrustValueType.USER_PERCEIVED));
		} catch (TrustException te) {
			fail("Failed to register TrustUpdateEvent listener: "
					+ te.getLocalizedMessage());
		}
	}
	
	@After
	public void tearDown() throws Exception {
		
		if (this.myTeid != null) {
			if (this.teid1 != null) {
				this.internalTrustBroker.removeTrustRelationships(
						new TrustQuery(this.myTeid).setTrusteeId(this.teid1));
			}
			if (this.teid2 != null) {
				this.internalTrustBroker.removeTrustRelationships(
						new TrustQuery(this.myTeid).setTrusteeId(this.teid2));
			}
			if (this.listener != null) {
				try {
					this.internalTrustBroker.unregisterTrustUpdateListener(
							this.listener, new TrustQuery(this.myTeid)
							.setTrustValueType(TrustValueType.USER_PERCEIVED));
				} catch (TrustException te) {
					fail("Failed to unregister TrustUpdateEvent listener: "
							+ te.getLocalizedMessage());
				}
			}
		}
	}
	
	@Test
	public void testTrustUpdateListenerByTrustorAndValueType() {

		LOG.info("*** testTrustUpdateListenerByTrustorAndValueType BEGIN");
		
		this.lock = new CountDownLatch(2);
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndValueType adding trust ratings");
		try {
			// This should should trigger one TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid2, TrustEvidenceType.RATED, 
					new Date(), new Random().nextDouble());
			// This should should trigger one TrustUpdateEvent that *must* be caught by the listener
			this.internalTrustEvidenceCollector.addDirectEvidence(
					this.myTeid, this.teid1, TrustEvidenceType.RATED,
					new Date(), new Random().nextDouble());
		} catch (Exception e) {
			
			fail("Failed to add trust rating: " + e.getLocalizedMessage());
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
								new TrustQuery(this.myTeid).setTrusteeId(this.teid1)
								.setTrustValueType(TrustValueType.USER_PERCEIVED)).get();
						assertEquals("Received new trust value was incorrect", trustValue, 
								event.getTrustRelationship().getTrustValue());
						assertNotNull("Received timestamp was null", 
								event.getTrustRelationship().getTimestamp());
						foundUserPerceived1 = true;
						
					// verify USER_PERCEIVED trust update event for User 2
					} else if (this.teid2.equals(event.getTrustRelationship().getTrusteeId())
							&& TrustValueType.USER_PERCEIVED == event.getTrustRelationship().getTrustValueType()) {
				
						trustValue = this.internalTrustBroker.retrieveTrustValue(
								new TrustQuery(this.myTeid).setTrusteeId(this.teid2)
								.setTrustValueType(TrustValueType.USER_PERCEIVED)).get();
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
		
		LOG.info("*** testTrustUpdateListenerByTrustorAndValueType END");
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