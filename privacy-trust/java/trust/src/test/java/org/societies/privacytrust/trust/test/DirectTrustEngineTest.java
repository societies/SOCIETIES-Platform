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
package org.societies.privacytrust.trust.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.internal.privacytrust.trust.event.ITrustEventListener;
import org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.engine.TrustEngineException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.impl.engine.DirectTrustEngine;
import org.societies.privacytrust.trust.impl.evidence.repo.model.DirectTrustEvidence;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCis;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCss;
import org.societies.privacytrust.trust.impl.repo.model.TrustedService;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
public class DirectTrustEngineTest {
	
private static final String BASE_ID = "dtet";
	
	private static final String TRUSTOR_ID = BASE_ID + "TrustorIIdentity";
	
	private static final String TRUSTED_CSS_ID = BASE_ID + "CssIIdentity";
	private static final String TRUSTED_CSS_ID2 = BASE_ID + "CssIIdentity2";
	
	private static final String TRUSTED_CIS_ID = BASE_ID + "CisIIdentity";
	private static final String TRUSTED_CIS_ID2 = BASE_ID + "CisIIdentity2";
	
	private static final String TRUSTED_SERVICE_ID = BASE_ID + "ServiceResourceIdentifier";
	private static final String TRUSTED_SERVICE_ID2 = BASE_ID + "ServiceResourceIdentifier2";
	
	private static final String TRUSTED_SERVICE_TYPE = BASE_ID + "ServiceType";
	private static final String TRUSTED_SERVICE_TYPE2 = BASE_ID + "ServiceType2";
	
	private static ITrustedCss trustedCss;
	@SuppressWarnings("unused")
	private static ITrustedCss trustedCss2;
	
	private static ITrustedCis trustedCis;
	@SuppressWarnings("unused")
	private static ITrustedCis trustedCis2;
	
	@SuppressWarnings("unused")
	private static ITrustedService trustedService;
	@SuppressWarnings("unused")
	private static ITrustedService trustedService2;
	
	private static ITrustEventMgr mockTrustEventMgr = mock(ITrustEventMgr.class);
	
	/** The DirectTrustEngine service reference. */
	private DirectTrustEngine engine;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		doNothing().when(mockTrustEventMgr).registerListener(any(ITrustEventListener.class),
				any(String[].class), any(TrustedEntityId.class));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		final TrustedEntityId cssTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID);
		trustedCss = new TrustedCss(cssTeid);
		final TrustedEntityId cssTeid2 = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID2);
		trustedCss2 = new TrustedCss(cssTeid2);
		
		final TrustedEntityId cisTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID);
		trustedCis = new TrustedCis(cisTeid);
		final TrustedEntityId cisTeid2 = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID2);
		trustedCis2 = new TrustedCis(cisTeid2);
		
		final TrustedEntityId serviceTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.SVC, TRUSTED_SERVICE_ID);
		trustedService = new TrustedService(serviceTeid, TRUSTED_SERVICE_TYPE);
		final TrustedEntityId serviceTeid2 = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.SVC, TRUSTED_SERVICE_ID2);
		trustedService2 = new TrustedService(serviceTeid2, TRUSTED_SERVICE_TYPE2);
		
		this.engine = new DirectTrustEngine(mockTrustEventMgr);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCssTrustValues(List, List)}.
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateCssBasedOnOneTrustRating() throws TrustEngineException {
		
		final List<ITrustedCss> cssList = new ArrayList<ITrustedCss>();
		cssList.add(trustedCss);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedCss.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		this.engine.evaluateCssTrustValues(cssList, evidenceList);
		final ITrustedCss evaluatedCss = cssList.get(0);
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), evaluatedCss.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCss.getDirectTrust().getRating());
		assertEquals(rating, evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCss.getDirectTrust().getScore());
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCss.getDirectTrust().getValue()); // TODO
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCssTrustValues(List, List).
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateCssBasedOnMultipleTrustRatings() throws TrustEngineException {
		
		final List<ITrustedCss> cssList = new ArrayList<ITrustedCss>();
		cssList.add(trustedCss);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.4d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedCss.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		// trust rating2
		final Double rating2 = new Double(0.5d);
		final Date timestamp2 = new Date(timestamp.getTime()+1000);
		final IDirectTrustEvidence evidence2 = new DirectTrustEvidence(trustedCss.getTeid(),
				TrustEvidenceType.RATED, timestamp2, rating2);
		evidenceList.add(evidence2);
		
		// trust rating3
		final Double rating3 = new Double(0.6d);
		final Date timestamp3 = new Date(timestamp.getTime()-1000);
		final IDirectTrustEvidence evidence3 = new DirectTrustEvidence(trustedCss.getTeid(),
				TrustEvidenceType.RATED, timestamp3, rating3);
		evidenceList.add(evidence3);
		
		this.engine.evaluateCssTrustValues(cssList, evidenceList);
		final ITrustedCss evaluatedCss = cssList.get(0);
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), evaluatedCss.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCss.getDirectTrust().getRating());
		assertEquals(rating2, evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCss.getDirectTrust().getScore());
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCss.getDirectTrust().getValue()); // TODO
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCisTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateCisBasedOnOneTrustRating() throws TrustEngineException {
		
		final List<ITrustedCis> cssList = new ArrayList<ITrustedCis>();
		cssList.add(trustedCis);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedCis.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		this.engine.evaluateCisTrustValues(cssList, evidenceList);
		final ITrustedCis evaluatedCis = cssList.get(0);
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), evaluatedCis.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCis.getDirectTrust().getRating());
		assertEquals(rating, evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCis.getDirectTrust().getValue()); // TODO
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCisTrustValues(List, List)}.
	 */
	public void testEvaluateCisBasedOnMultipleTrustRatings() throws TrustEngineException {
		
		final List<ITrustedCis> cssList = new ArrayList<ITrustedCis>();
		cssList.add(trustedCis);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.4d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedCis.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		// trust rating2
		final Double rating2 = new Double(0.5d);
		final Date timestamp2 = new Date(timestamp.getTime()+1000);
		final IDirectTrustEvidence evidence2 = new DirectTrustEvidence(trustedCis.getTeid(),
				TrustEvidenceType.RATED, timestamp2, rating2);
		evidenceList.add(evidence2);
		
		// trust rating3
		final Double rating3 = new Double(0.6d);
		final Date timestamp3 = new Date(timestamp.getTime()-1000);
		final IDirectTrustEvidence evidence3 = new DirectTrustEvidence(trustedCis.getTeid(),
				TrustEvidenceType.RATED, timestamp3, rating3);
		evidenceList.add(evidence3);
		
		this.engine.evaluateCisTrustValues(cssList, evidenceList);
		final ITrustedCis evaluatedCis = cssList.get(0);
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), evaluatedCis.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCis.getDirectTrust().getRating());
		assertEquals(rating2, evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCis.getDirectTrust().getValue()); // TODO
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateServiceTrustValues(List, List)}.
	 */
	@Test
	@Ignore
	public void testEvaluateITrustedServiceSetOfITrustEvidence() {
		fail("Not yet implemented");
	}

}
