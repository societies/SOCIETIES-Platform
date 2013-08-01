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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

/**
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
public class TestTrustBasedPrefEval {

	private static Logger LOG = LoggerFactory.getLogger(TestTrustBasedPrefEval.class);
	
	private static final String USER_ID1 = "bob.societies.local";
	private static final String USER_ID2 = "alice.societies.local";
	
	/**
	 * The {@link #setUp()} method assigns trust values to {@link #USER_ID1}
	 * and {@link #USER_ID2}, such that:
	 * <ul>
	 * <li>{@link #USER_ID1} is assigned a value <b>above</b> this threshold</li>
	 * <li>{@link #USER_ID2} is assigned a value <b>below</b> this threshold</li>
	 * </ul>
	 */
	private static final double TRUST_VALUE_THRESHOLD = 0.5d;
	
	/** The rating (high) assigned to {@link #USER_ID1}. */
	private static final double TRUST_RATING_USER_1 = 0.75d;
	
	/** The rating (high) assigned to {@link #USER_ID2}. */
	private static final double TRUST_RATING_USER_2 = 0.25d;
	
	private static final long WAIT_TRUST_EVAL = 1000l;
	
	private ITrustEvidenceCollector internalTrustEvidenceCollector;
	private ITrustBroker internalTrustBroker;
	private ICommManager commMgr;

	private IIdentity myUserId;
	private IIdentity userId1;
	private IIdentity userId2;
	
	private TrustedEntityId myTeid;
	private TrustedEntityId teid1;
	private TrustedEntityId teid2;

	public TestTrustBasedPrefEval() {
	}

	@Before
	public void setUp() throws Exception {
		
		this.internalTrustEvidenceCollector = TestCase1678.getInternalTrustEvidenceCollector();
		this.internalTrustBroker = TestCase1678.getInternalTrustBroker();
		this.commMgr = TestCase1678.getCommManager();

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
		
		// setup trust values
		// User 1
		// my CSS has shared context with User 1
		this.internalTrustEvidenceCollector.addDirectEvidence(
				myTeid, teid1, TrustEvidenceType.SHARED_CONTEXT, new Date(), 
				CtxAttributeTypes.LOCATION_SYMBOLIC);
		// my CSS trusts User 1
		this.internalTrustEvidenceCollector.addDirectEvidence(
				myTeid, teid1, TrustEvidenceType.RATED, new Date(), 
				new Double(TRUST_RATING_USER_1));
		Thread.sleep(WAIT_TRUST_EVAL);
		final Double trustValue1 = this.internalTrustBroker.retrieveTrustValue(
				new TrustQuery(this.myTeid).setTrusteeId(this.teid1)
				.setTrustValueType(TrustValueType.USER_PERCEIVED)).get();
		LOG.info("*** trustValue1 = " + trustValue1);
		// User 2
		// my CSS has withheld context from User 2
		this.internalTrustEvidenceCollector.addDirectEvidence(
				this.myTeid, this.teid2, TrustEvidenceType.WITHHELD_CONTEXT, new Date(), 
				CtxAttributeTypes.LOCATION_SYMBOLIC);
		// my CSS doesn't trust User 2
		this.internalTrustEvidenceCollector.addDirectEvidence(
				this.myTeid, this.teid2, TrustEvidenceType.RATED, new Date(), 
				new Double(TRUST_RATING_USER_2));
		Thread.sleep(WAIT_TRUST_EVAL);
		final Double trustValue2 = this.internalTrustBroker.retrieveTrustValue(
				new TrustQuery(myTeid).setTrusteeId(teid2)
				.setTrustValueType(TrustValueType.USER_PERCEIVED)).get();
		LOG.info("*** trustValue2 = " + trustValue2);
		
		// TODO privacy pref setup
	}
	
	@After
	public void tearDown() throws Exception {
		
		// TODO
		// 1. remove test trust data db? currently not supported
		// 2. remove test privacy prefs 
	}

	@Test
	public void TestPrefEval() throws Exception {

		LOG.info("*** Start TestPrefEval ...");
		
		// 1. Verify that trust values have been properly setup
		// User 1
		final Double trustValue1 = this.internalTrustBroker.retrieveTrustValue(
				new TrustQuery(this.myTeid).setTrusteeId(this.teid1)
				.setTrustValueType(TrustValueType.USER_PERCEIVED)).get();
		assertNotNull(trustValue1);
		assertTrue(trustValue1 > TRUST_VALUE_THRESHOLD);
		// User 2
		final Double trustValue2 = this.internalTrustBroker.retrieveTrustValue(
				new TrustQuery(this.myTeid).setTrusteeId(this.teid2)
				.setTrustValueType(TrustValueType.USER_PERCEIVED)).get();
		assertNotNull(trustValue2);
		assertTrue(trustValue2 < TRUST_VALUE_THRESHOLD);
		
		// TODO 2. privacy pref
	}
}