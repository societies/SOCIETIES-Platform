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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.privacytrust.trust.event.ITrustEventListener;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.engine.IDirectTrustEngine;
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
	
	private static final Map<TrustEvidenceType, Double> EVIDENCE_SCORE_MAP;
    
	static {
		
        final Map<TrustEvidenceType, Double> aMap = new HashMap<TrustEvidenceType, Double>();
        aMap.put(TrustEvidenceType.JOINED_COMMUNITY, +5.0d);
        aMap.put(TrustEvidenceType.LEFT_COMMUNITY, -50.0d);
        aMap.put(TrustEvidenceType.USED_SERVICE, +1.0d);
        EVIDENCE_SCORE_MAP = Collections.unmodifiableMap(aMap);
    }
	
	private static final String TRUSTOR_ID = BASE_ID + "TrustorIIdentity";
	
	private static final int TRUSTED_CSS_LIST_SIZE = 100;
	private static final String TRUSTED_CSS_ID_BASE = BASE_ID + "CssIIdentity";
	
	private static final int TRUSTED_CIS_LIST_SIZE = 10;
	private static final String TRUSTED_CIS_ID_BASE = BASE_ID + "CisIIdentity";
	
	private static final int TRUSTED_SERVICE_LIST_SIZE = 500;
	private static final String TRUSTED_SERVICE_ID_BASE = BASE_ID + "ServiceResourceIdentifier";
	
	private static final int TRUSTED_SERVICE_TYPE_LIST_SIZE = 10;
	private static final String TRUSTED_SERVICE_TYPE_BASE = BASE_ID + "ServiceType";
	
	private static List<ITrustedCss> trustedCssList;
	
	private static List<ITrustedCis> trustedCisList;
	
	private static List<ITrustedService> trustedServiceList;
	
	private static List<String> trustedServiceTypeList;
	
	private static ITrustEventMgr mockTrustEventMgr = mock(ITrustEventMgr.class);
	
	/** The DirectTrustEngine service reference. */
	private IDirectTrustEngine engine;

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
		
		trustedCssList = new ArrayList<ITrustedCss>(TRUSTED_CSS_LIST_SIZE);
		for (int i = 0; i < TRUSTED_CSS_LIST_SIZE; ++i) {
			final TrustedEntityId cssTeid = 
					new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID_BASE+i);
			trustedCssList.add(new TrustedCss(cssTeid));
		}
		
		trustedCisList = new ArrayList<ITrustedCis>(TRUSTED_CIS_LIST_SIZE);
		for (int i = 0; i < TRUSTED_CIS_LIST_SIZE; ++i) {
			final TrustedEntityId cisTeid =
					new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID_BASE+i);
			trustedCisList.add(new TrustedCis(cisTeid));
		}
		
		trustedServiceTypeList = new ArrayList<String>(TRUSTED_SERVICE_TYPE_LIST_SIZE);
		for (int i = 0; i < TRUSTED_SERVICE_TYPE_LIST_SIZE; ++i)
			trustedServiceTypeList.add(TRUSTED_SERVICE_TYPE_BASE+i);
	
		trustedServiceList = new ArrayList<ITrustedService>(TRUSTED_SERVICE_LIST_SIZE);
		for (int i = 0; i < TRUSTED_SERVICE_LIST_SIZE; ++i) {
			final TrustedEntityId serviceTeid = 
					new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.SVC, TRUSTED_SERVICE_ID_BASE+i);
			trustedServiceList.add(new TrustedService(serviceTeid));
		}
		
		this.engine = new DirectTrustEngine(mockTrustEventMgr);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		trustedCssList = null;
		trustedCisList = null;
		trustedServiceTypeList = null;
		trustedServiceList = null;
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCssTrustValues(List, List)}.
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateOneCssOneTrustRating() throws TrustEngineException {
		
		final ITrustedCss trustedCss = trustedCssList.get(0);
		final List<ITrustedCss> trustedCssSubList = new ArrayList<ITrustedCss>();
		trustedCssSubList.add(trustedCss);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedCss.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		this.engine.evaluateCssTrustValues(trustedCssSubList, evidenceList);
		final ITrustedCss evaluatedCss = trustedCssSubList.get(0);
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), 
				evaluatedCss.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCss.getDirectTrust().getRating());
		assertEquals(rating, evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCss.getDirectTrust().getScore());
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		//System.out.println(evaluatedCss.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCss.getDirectTrust().getValue()); // TODO
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCssTrustValues(List, List).
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateOneCssMultipleTrustRatings() throws TrustEngineException {
		
		final ITrustedCss trustedCss = trustedCssList.get(0);
		final List<ITrustedCss> trustedCssSubList = new ArrayList<ITrustedCss>();
		trustedCssSubList.add(trustedCss);
		
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
		
		this.engine.evaluateCssTrustValues(trustedCssSubList, evidenceList);
		final ITrustedCss evaluatedCss = trustedCssSubList.get(0);
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), 
				evaluatedCss.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCss.getDirectTrust().getRating());
		assertEquals(rating2, evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCss.getDirectTrust().getScore());
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		//System.out.println(evaluatedCss.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCss.getDirectTrust().getValue()); // TODO
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCssTrustValues(List, List).
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateMultipleCssMultipleTrustRatings() throws TrustEngineException {
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>(TRUSTED_CSS_LIST_SIZE);
		final List<Double> ratingsList = new ArrayList<Double>(TRUSTED_CSS_LIST_SIZE);
		final Random randomGenerator = new Random();
		for (int i = 0; i < TRUSTED_CSS_LIST_SIZE; ++i) {
			// trust rating
			Double rating = 0.1d * (Math.round(randomGenerator.nextGaussian() * (5/2) + 5));
			if (rating > 1.0d) 
				rating = 1.0d;
			else if (rating < 0.0d) 
				rating = 0.0d;
			final Date timestamp = new Date();
			final IDirectTrustEvidence evidence = new DirectTrustEvidence(
					trustedCssList.get(i).getTeid(),
					TrustEvidenceType.RATED, timestamp, rating);
			ratingsList.add(rating);
			evidenceList.add(evidence);
		}
		
		this.engine.evaluateCssTrustValues(trustedCssList, evidenceList);
		for (int i = 0; i < TRUSTED_CSS_LIST_SIZE; ++i) {
			final ITrustedCss evaluatedCss = trustedCssList.get(i);
			assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
			assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
			assertEquals(evaluatedCss.getDirectTrust().getLastModified(),
					evaluatedCss.getDirectTrust().getLastUpdated());
			assertNotNull(evaluatedCss.getDirectTrust().getRating());
			assertEquals(ratingsList.get(i), evaluatedCss.getDirectTrust().getRating());
			assertNotNull(evaluatedCss.getDirectTrust().getScore());
			assertEquals(new Double(0.0d), evaluatedCss.getDirectTrust().getScore());
			assertNotNull(evaluatedCss.getDirectTrust().getValue());
			//System.out.println(evaluatedCss.getDirectTrust().getValue());
			//assertEquals(???, evaluatedCss.getDirectTrust().getValue()); // TODO
		}
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCisTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateOneCisOneTrustRating() throws TrustEngineException {
		
		final ITrustedCis trustedCis = trustedCisList.get(0);
		final List<ITrustedCis> trustedCisSubList = new ArrayList<ITrustedCis>();
		trustedCisSubList.add(trustedCis);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedCis.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		this.engine.evaluateCisTrustValues(trustedCisSubList, evidenceList);
		final ITrustedCis evaluatedCis = trustedCisSubList.get(0);
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), 
				evaluatedCis.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCis.getDirectTrust().getRating());
		assertEquals(rating, evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		//System.out.println(evaluatedCis.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCis.getDirectTrust().getValue()); // TODO
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCisTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateOneCisMultipleTrustRatings() throws TrustEngineException {
		
		final ITrustedCis trustedCis = trustedCisList.get(0);
		final List<ITrustedCis> trustedCisSubList = new ArrayList<ITrustedCis>();
		trustedCisSubList.add(trustedCis);
		
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
		
		this.engine.evaluateCisTrustValues(trustedCisSubList, evidenceList);
		final ITrustedCis evaluatedCis = trustedCisSubList.get(0);
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), 
				evaluatedCis.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCis.getDirectTrust().getRating());
		assertEquals(rating2, evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		//System.out.println(evaluatedCis.getDirectTrust().getValue());
		//assertEquals(???, evaluatedCis.getDirectTrust().getValue()); // TODO
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCisTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateOneCisMultipleLifecycleEvents() throws TrustEngineException {
		
		final ITrustedCis trustedCis = trustedCisList.get(0);
		final List<ITrustedCis> trustedCisSubList = new ArrayList<ITrustedCis>();
		trustedCisSubList.add(trustedCis);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// Joined Community evidence
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedCis.getTeid(),
				TrustEvidenceType.JOINED_COMMUNITY, timestamp, null);
		evidenceList.add(evidence1);
		
		this.engine.evaluateCisTrustValues(trustedCisSubList, evidenceList);
		ITrustedCis evaluatedCis = trustedCisSubList.get(0);
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), 
				evaluatedCis.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertEquals(EVIDENCE_SCORE_MAP.get(evidence1.getType()),
				evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		final Double trustValueAfterEvidence1 = new Double(evaluatedCis.getDirectTrust().getValue());
		//System.out.println(trustValueAfterEvidence1);
		//assertEquals(???, trustValueAfterEvidence1); // TODO
		
		evidenceList.clear();
		
		final Date timestamp2 = new Date();
		final IDirectTrustEvidence evidence2 = new DirectTrustEvidence(trustedCis.getTeid(),
				TrustEvidenceType.LEFT_COMMUNITY, timestamp2, null);
		evidenceList.add(evidence2);
		
		this.engine.evaluateCisTrustValues(trustedCisSubList, evidenceList);
		evaluatedCis = trustedCisSubList.get(0);
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertEquals(new Double(EVIDENCE_SCORE_MAP.get(evidence1.getType())
				+ EVIDENCE_SCORE_MAP.get(evidence2.getType())),
				evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		final Double trustValueAfterEvidence2 = new Double(evaluatedCis.getDirectTrust().getValue());
		//System.out.println(trustValueAfterEvidence2);
		//assertEquals(???, trustValueAfterEvidence2); // TODO
		
		assertTrue(trustValueAfterEvidence1 >= trustValueAfterEvidence2);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCisTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateMultipleCisMultipleLifecycleEvents() throws TrustEngineException {
		
		final List<ITrustEvidence> evidenceList = 
				new ArrayList<ITrustEvidence>(TRUSTED_CIS_LIST_SIZE);
		final List<TrustEvidenceType> lifecycleEventsList = 
				new ArrayList<TrustEvidenceType>(TRUSTED_CIS_LIST_SIZE);
		for (int i = 0; i < TRUSTED_CIS_LIST_SIZE; ++i) {
			final TrustEvidenceType evidenceType = (i%4 != 0) 
					? TrustEvidenceType.JOINED_COMMUNITY 
					: TrustEvidenceType.LEFT_COMMUNITY;
			final Date timestamp = new Date();
			final IDirectTrustEvidence evidence = new DirectTrustEvidence(
					trustedCisList.get(i).getTeid(), evidenceType, timestamp, null);
			lifecycleEventsList.add(evidenceType);
			evidenceList.add(evidence);
		}
		
		this.engine.evaluateCisTrustValues(trustedCisList, evidenceList);
		for (int i = 0; i < TRUSTED_CIS_LIST_SIZE; ++i) {
			final ITrustedCis evaluatedCis = trustedCisList.get(i);
			assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
			assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
			assertEquals(evaluatedCis.getDirectTrust().getLastModified(),
					evaluatedCis.getDirectTrust().getLastUpdated());
			assertNull(evaluatedCis.getDirectTrust().getRating());
			assertNotNull(evaluatedCis.getDirectTrust().getScore());
			assertEquals(EVIDENCE_SCORE_MAP.get(lifecycleEventsList.get(i)), 
					evaluatedCis.getDirectTrust().getScore());
			assertNotNull(evaluatedCis.getDirectTrust().getValue());
			//System.out.println(evaluatedCis.getDirectTrust().getValue());
			//assertEquals(???, evaluatedCis.getDirectTrust().getValue()); // TODO
		}
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateServiceTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateOneServiceOneTrustRating() throws TrustEngineException {
		
		final ITrustedService trustedService = trustedServiceList.get(0);
		final List<ITrustedService> trustedServiceSubList = new ArrayList<ITrustedService>();
		trustedServiceSubList.add(trustedService);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedService.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		this.engine.evaluateServiceTrustValues(trustedServiceSubList, evidenceList);
		final ITrustedService evaluatedService = trustedServiceSubList.get(0);
		assertNotNull(evaluatedService.getDirectTrust().getLastModified());
		assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedService.getDirectTrust().getLastModified(), 
				evaluatedService.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedService.getDirectTrust().getRating());
		assertEquals(rating, evaluatedService.getDirectTrust().getRating());
		assertNotNull(evaluatedService.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedService.getDirectTrust().getScore());
		assertNotNull(evaluatedService.getDirectTrust().getValue());
		//System.out.println(evaluatedService.getDirectTrust().getValue());
		//assertEquals(???, evaluatedService.getDirectTrust().getValue()); // TODO
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateServiceTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateOneServiceMultipleTrustRatings() throws TrustEngineException {
		
		final ITrustedService trustedService = trustedServiceList.get(0);
		final List<ITrustedService> trustedServiceSubList = new ArrayList<ITrustedService>();
		trustedServiceSubList.add(trustedService);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.4d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedService.getTeid(),
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceList.add(evidence1);
		
		// trust rating2
		final Double rating2 = new Double(0.5d);
		final Date timestamp2 = new Date(timestamp.getTime()+1000);
		final IDirectTrustEvidence evidence2 = new DirectTrustEvidence(trustedService.getTeid(),
				TrustEvidenceType.RATED, timestamp2, rating2);
		evidenceList.add(evidence2);
		
		// trust rating3
		final Double rating3 = new Double(0.6d);
		final Date timestamp3 = new Date(timestamp.getTime()-1000);
		final IDirectTrustEvidence evidence3 = new DirectTrustEvidence(trustedService.getTeid(),
				TrustEvidenceType.RATED, timestamp3, rating3);
		evidenceList.add(evidence3);
		
		this.engine.evaluateServiceTrustValues(trustedServiceSubList, evidenceList);
		final ITrustedService evaluatedService = trustedServiceSubList.get(0);
		assertNotNull(evaluatedService.getDirectTrust().getLastModified());
		assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedService.getDirectTrust().getLastModified(), 
				evaluatedService.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedService.getDirectTrust().getRating());
		assertEquals(rating2, evaluatedService.getDirectTrust().getRating());
		assertNotNull(evaluatedService.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedService.getDirectTrust().getScore());
		assertNotNull(evaluatedService.getDirectTrust().getValue());
		//System.out.println(evaluatedService.getDirectTrust().getValue());
		//assertEquals(???, evaluatedService.getDirectTrust().getValue()); // TODO
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateServiceTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateOneServiceMultipleServiceEvents() throws TrustEngineException {
		
		final ITrustedService trustedService = trustedServiceList.get(0);
		final List<ITrustedService> trustedServiceSubList = new ArrayList<ITrustedService>();
		trustedServiceSubList.add(trustedService);
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
		// Used Service evidence
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(trustedService.getTeid(),
				TrustEvidenceType.USED_SERVICE, timestamp, null);
		evidenceList.add(evidence1);
		
		this.engine.evaluateServiceTrustValues(trustedServiceSubList, evidenceList);
		ITrustedService evaluatedService = trustedServiceSubList.get(0);
		assertNotNull(evaluatedService.getDirectTrust().getLastModified());
		assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedService.getDirectTrust().getLastModified(), 
				evaluatedService.getDirectTrust().getLastUpdated());
		assertNull(evaluatedService.getDirectTrust().getRating());
		assertNotNull(evaluatedService.getDirectTrust().getScore());
		assertEquals(EVIDENCE_SCORE_MAP.get(evidence1.getType()),
				evaluatedService.getDirectTrust().getScore());
		assertNotNull(evaluatedService.getDirectTrust().getValue());
		final Double trustValueAfterEvidence1 = new Double(evaluatedService.getDirectTrust().getValue());
		//System.out.println(trustValueAfterEvidence1);
		//assertEquals(???, trustValueAfterEvidence1); // TODO
		
		evidenceList.clear();
		
		for (int i = 0; i < 10000; ++i) {
			final Date timestampi = new Date();
			final IDirectTrustEvidence evidence = new DirectTrustEvidence(trustedService.getTeid(),
				TrustEvidenceType.USED_SERVICE, timestampi, null);
			evidenceList.add(evidence);
		}
		
		this.engine.evaluateServiceTrustValues(trustedServiceSubList, evidenceList);
		evaluatedService = trustedServiceSubList.get(0);
		assertNotNull(evaluatedService.getDirectTrust().getLastModified());
		assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
		assertNull(evaluatedService.getDirectTrust().getRating());
		assertNotNull(evaluatedService.getDirectTrust().getScore());
		assertEquals(new Double(10001 * EVIDENCE_SCORE_MAP.get(TrustEvidenceType.USED_SERVICE)),
				evaluatedService.getDirectTrust().getScore());
		assertNotNull(evaluatedService.getDirectTrust().getValue());
		final Double trustValueAfterEvidence2 = new Double(evaluatedService.getDirectTrust().getValue());
		//System.out.println(trustValueAfterEvidence2);
		//assertEquals(???, trustValueAfterEvidence2); // TODO
		
		assertTrue(trustValueAfterEvidence1 >= trustValueAfterEvidence2);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateServiceTrustValues(List, List)}.
	 */
	@Test
	public void testEvaluateMultipleServiceMultipleLifecycleEvents() throws TrustEngineException {
		
		final List<ITrustEvidence> evidenceList = 
				new ArrayList<ITrustEvidence>(TRUSTED_SERVICE_LIST_SIZE);
		final List<Integer> serviceEventsList = 
				new ArrayList<Integer>(TRUSTED_SERVICE_LIST_SIZE);
		for (int i = 0; i < TRUSTED_SERVICE_LIST_SIZE; ++i) {
			for (int j = 0; j < i; ++j) {
				final Date timestamp = new Date(new Date().getTime()+j*1000);
				final IDirectTrustEvidence evidence = new DirectTrustEvidence(
						trustedServiceList.get(i).getTeid(), TrustEvidenceType.USED_SERVICE, timestamp, null);
				evidenceList.add(evidence);
			}
			serviceEventsList.add(new Integer(i));
		}
		
		this.engine.evaluateServiceTrustValues(trustedServiceList, evidenceList);
		for (int i = 0; i < TRUSTED_SERVICE_LIST_SIZE; ++i) {
			final ITrustedService evaluatedService = trustedServiceList.get(i);
			assertNotNull(evaluatedService.getDirectTrust().getLastModified());
			assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
			assertEquals(evaluatedService.getDirectTrust().getLastModified(),
					evaluatedService.getDirectTrust().getLastUpdated());
			assertNull(evaluatedService.getDirectTrust().getRating());
			assertNotNull(evaluatedService.getDirectTrust().getScore());
			assertEquals(new Double(
					serviceEventsList.get(i) * EVIDENCE_SCORE_MAP.get(TrustEvidenceType.USED_SERVICE)), 
					evaluatedService.getDirectTrust().getScore());
			assertNotNull(evaluatedService.getDirectTrust().getValue());
			//System.out.println(evaluatedService.getDirectTrust().getValue());
			//assertEquals(???, evaluatedService.getDirectTrust().getValue()); // TODO
		}
	}
}