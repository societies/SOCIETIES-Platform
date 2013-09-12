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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.engine.IDirectTrustEngine;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.model.IDirectTrust;
import org.societies.privacytrust.trust.api.model.ITrust;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/DirectTrustEngineTest-context.xml"})
public class DirectTrustEngineTest {
	
	private static double EPSILON = 0.000001d;
	
	private static final String BASE_ID = "dtet";
	
	private static final String TRUSTOR_CSS_ID = BASE_ID + "TrustorCssIIdentity";
	
	private static final int TRUSTEE_CSS_LIST_SIZE = 100;
	private static final String TRUSTEE_CSS_ID_BASE = BASE_ID + "TrusteeCssIIdentity";
	
	private static final int TRUSTEE_CIS_LIST_SIZE = 10;
	private static final String TRUSTEE_CIS_ID_BASE = BASE_ID + "TrusteeCisIIdentity";
	
	private static final int TRUSTEE_SERVICE_LIST_SIZE = 500;
	private static final String TRUSTEE_SERVICE_ID_BASE = BASE_ID + "TrusteeServiceResourceIdentifier";
	
	private static final int TRUSTEE_SERVICE_TYPE_LIST_SIZE = 10;
	private static final String TRUSTEE_SERVICE_TYPE_BASE = BASE_ID + "TrusteeServiceType";
	
	private static TrustedEntityId myCssTeid;
	
	private static List<TrustedEntityId> trusteeCssTeidList;
	
	private static List<TrustedEntityId> trusteeCisTeidList;
	
	private static List<TrustedEntityId> trusteeServiceTeidList;
	
	private static List<String> trusteeServiceTypeList;
	
	/** The IDirectTrustEngine service reference. */
	@Autowired
	@InjectMocks
	private IDirectTrustEngine engine;
	
	/** The ITrustRepository service reference. */
	@Autowired
	private ITrustRepository trustRepo;
	
	/** The ITrustEvidenceRepository service reference. */
	@Autowired
	private ITrustEvidenceRepository trustEvidenceRepo;
	
	@Mock
	private ITrustNodeMgr mockTrustNodeMgr;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		myCssTeid =	new TrustedEntityId(TrustedEntityType.CSS, TRUSTOR_CSS_ID);
		
		trusteeCssTeidList = new ArrayList<TrustedEntityId>(TRUSTEE_CSS_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_CSS_LIST_SIZE; ++i) {
			final TrustedEntityId cssTeid = 
					new TrustedEntityId(TrustedEntityType.CSS, TRUSTEE_CSS_ID_BASE+i);
			trusteeCssTeidList.add(cssTeid);
		}
		
		trusteeCisTeidList = new ArrayList<TrustedEntityId>(TRUSTEE_CIS_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_CIS_LIST_SIZE; ++i) {
			final TrustedEntityId cisTeid =
					new TrustedEntityId(TrustedEntityType.CIS, TRUSTEE_CIS_ID_BASE+i);
			trusteeCisTeidList.add(cisTeid);
		}
		
		trusteeServiceTypeList = new ArrayList<String>(TRUSTEE_SERVICE_TYPE_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_SERVICE_TYPE_LIST_SIZE; ++i)
			trusteeServiceTypeList.add(TRUSTEE_SERVICE_TYPE_BASE+i);
	
		trusteeServiceTeidList = new ArrayList<TrustedEntityId>(TRUSTEE_SERVICE_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_SERVICE_LIST_SIZE; ++i) {
			final TrustedEntityId serviceTeid = 
					new TrustedEntityId(TrustedEntityType.SVC, TRUSTEE_SERVICE_ID_BASE+i);
			trusteeServiceTeidList.add(serviceTeid);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		myCssTeid = null;
		trusteeCssTeidList = null;
		trusteeCisTeidList = null;
		trusteeServiceTypeList = null;
		trusteeServiceTeidList = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		final Collection<TrustedEntityId> myIds = new HashSet<TrustedEntityId>();
		myIds.add(myCssTeid);
		when(this.mockTrustNodeMgr.getMyIds()).thenReturn(myIds);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		// clean trust database
		this.trustRepo.removeEntities(null, null, null);
		// clean trust evidence database
		this.trustEvidenceRepo.removeEvidence(null, null, null, null, null, null);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateCssTrustRating() throws Exception {
		
		// trust rating
		final TrustedEntityId trusteeCssTeid = trusteeCssTeidList.get(0);
		final Double rating = new Double(0.8d);
		final Date timestamp = new Date();
		ITrustEvidence evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp, rating, null);
		
		Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify this has no effect as there is no direct trust relationship with the CSS yet
		assertNotNull(resultSet);
		assertTrue(resultSet.isEmpty());
		
		// create direct trust relationship with CSS
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.FRIENDED_USER, timestamp, null, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify direct trust relationship with CSS
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCss);
		ITrustedCss evaluatedCss = (ITrustedCss) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCss.getEvidence());
		assertTrue(evaluatedCss.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), 
				evaluatedCss.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertTrue(evaluatedCss.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		
		// trust rating
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp, rating, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify updated direct trust relationship with CSS
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCss);
		evaluatedCss = (ITrustedCss) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCss.getEvidence());
		assertTrue(evaluatedCss.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), 
				evaluatedCss.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCss.getDirectTrust().getRating());
		assertEquals(rating, evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertTrue(evaluatedCss.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		assertTrue(evaluatedCss.getDirectTrust().getValue() >= 0.6d);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateCssSharedContext() throws Exception {
		
		// CSS shared context 1
		final TrustedEntityId trusteeCssTeid = trusteeCssTeidList.get(0);
		final ITrustEvidence cssSharedCtxevidence1 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.SHARED_CONTEXT, new Date(), "name", null);
		
		Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, cssSharedCtxevidence1);
		ITrustedCss evaluatedCss = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid);
		// verify direct trust relationship with CSS
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.contains(evaluatedCss));
		// verify CSS association with evidence
		assertNotNull(evaluatedCss.getEvidence());
		assertTrue(evaluatedCss.getEvidence().contains(cssSharedCtxevidence1));
		// verify CSS trust
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertTrue(evaluatedCss.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		assertTrue(evaluatedCss.getDirectTrust().getValue() > 0.5d);
		
		// CSS2 shared context 1
		final TrustedEntityId trusteeCssTeid2 = trusteeCssTeidList.get(1);
		final ITrustEvidence css2SharedCtxevidence1 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid2,
				TrustEvidenceType.SHARED_CONTEXT, new Date(), "name", null);
		
		resultSet = this.engine.evaluate(myCssTeid, css2SharedCtxevidence1);
		evaluatedCss = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid);
		ITrustedCss evaluatedCss2 = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		// verify direct trust relationship with CSS and CSS2
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 2);
		assertTrue(resultSet.contains(evaluatedCss));
		assertTrue(resultSet.contains(evaluatedCss2));
		// verify CSS association with evidence
		assertNotNull(evaluatedCss.getEvidence());
		assertTrue(evaluatedCss.getEvidence().contains(cssSharedCtxevidence1));
		// verify CSS2 association with evidence
		assertNotNull(evaluatedCss2.getEvidence());
		assertTrue(evaluatedCss2.getEvidence().contains(css2SharedCtxevidence1));
		// verify CSS2 trust
		assertNotNull(evaluatedCss2.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss2.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCss2.getDirectTrust().getRating());
		assertNotNull(evaluatedCss2.getDirectTrust().getScore());
		assertTrue(evaluatedCss2.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCss2.getDirectTrust().getValue());
		assertTrue(evaluatedCss2.getDirectTrust().getValue() > 0.5d);
		// verify CSS trust == CSS2 trust
		assertEquals(evaluatedCss.getDirectTrust().getValue(), evaluatedCss2.getDirectTrust().getValue(), EPSILON);
		
		// CSS shared context 2
		final ITrustEvidence cssSharedCtxevidence2 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.SHARED_CONTEXT, new Date(), "email", null);
		
		resultSet = this.engine.evaluate(myCssTeid, cssSharedCtxevidence2);
		evaluatedCss = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid);
		evaluatedCss2 = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		// verify direct trust relationship with CSS and CSS2
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 2);
		assertTrue(resultSet.contains(evaluatedCss));
		assertTrue(resultSet.contains(evaluatedCss2));
		// verify CSS association with evidence
		assertNotNull(evaluatedCss.getEvidence());
		assertTrue(evaluatedCss.getEvidence().contains(cssSharedCtxevidence1));
		assertTrue(evaluatedCss.getEvidence().contains(cssSharedCtxevidence2));
		// verify CSS2 association with evidence
		assertNotNull(evaluatedCss2.getEvidence());
		assertTrue(evaluatedCss2.getEvidence().contains(css2SharedCtxevidence1));
		// verify CSS trust
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertTrue(evaluatedCss.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		assertTrue(evaluatedCss.getDirectTrust().getValue() > 0.5d);
		// verify CSS trust > CSS2 trust
		assertTrue(evaluatedCss.getDirectTrust().getValue() > evaluatedCss2.getDirectTrust().getValue());
		
		// CSS withheld context 1
		final ITrustEvidence cssWithheldCtxevidence1 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.WITHHELD_CONTEXT, new Date(), "location", null);
		
		resultSet = this.engine.evaluate(myCssTeid, cssWithheldCtxevidence1);
		evaluatedCss = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid);
		evaluatedCss2 = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		// verify direct trust relationship with CSS and CSS2
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 2);
		assertTrue(resultSet.contains(evaluatedCss));
		assertTrue(resultSet.contains(evaluatedCss2));
		// verify CSS association with evidence
		assertNotNull(evaluatedCss.getEvidence());
		assertTrue(evaluatedCss.getEvidence().contains(cssSharedCtxevidence1));
		assertTrue(evaluatedCss.getEvidence().contains(cssSharedCtxevidence2));
		assertTrue(evaluatedCss.getEvidence().contains(cssWithheldCtxevidence1));
		// verify CSS2 association with evidence
		assertNotNull(evaluatedCss2.getEvidence());
		assertTrue(evaluatedCss2.getEvidence().contains(css2SharedCtxevidence1));
		// verify CSS trust
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertTrue(evaluatedCss.getDirectTrust().getScore() < IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		assertTrue(evaluatedCss.getDirectTrust().getValue() < 0.5d);
		// verify CSS trust < CSS2 trust
		assertTrue(evaluatedCss.getDirectTrust().getValue() < evaluatedCss2.getDirectTrust().getValue());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, Set)}.
	 * @throws Exception
	 */
	@Test
	public void testEvaluateCssMultipleEvidence() throws Exception {
		
		final TrustedEntityId trusteeCssTeid = trusteeCssTeidList.get(0);
		
		final Set<ITrustEvidence> evidenceSet = new HashSet<ITrustEvidence>();
		// trust rating
		final Double rating = new Double(0.1d);
		// timestamp
		final Date now = new Date();
		// Ugly hack for MySQL - remove ms precision from date
		final Date timestamp = new Date(1000 * (now.getTime() / 1000));
		final ITrustEvidence evidence1 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp, rating, null);
		evidenceSet.add(evidence1);
		
		// trust rating2
		final Double rating2 = new Double(0.8d);
		final Date timestamp2 = new Date(timestamp.getTime() + 1000);
		final ITrustEvidence evidence2 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp2, rating2, null);
		evidenceSet.add(evidence2);
		
		// friendship
		final Date timestamp3 = new Date(timestamp.getTime() - 2000);
		final ITrustEvidence evidence3 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.FRIENDED_USER, timestamp3, null, null);
		evidenceSet.add(evidence3);
		
		// trust rating3
		final Double rating3 = new Double(0.2d);
		final Date timestamp4 = new Date(timestamp.getTime() - 1000);
		final ITrustEvidence evidence4 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp4, rating3, null);
		evidenceSet.add(evidence4);
		
		final Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidenceSet);
		// verify
		assertNotNull(resultSet);
		assertTrue(!resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCss);
		final ITrustedCss evaluatedCss = (ITrustedCss) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCss.getEvidence());
		assertTrue(evaluatedCss.getEvidence().containsAll(evidenceSet));
		// verify updated trust
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), 
				evaluatedCss.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCss.getDirectTrust().getRating());
		assertEquals(rating2, evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertTrue(evaluatedCss.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		assertTrue(evaluatedCss.getDirectTrust().getValue() >= 0.6d);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception
	 */
	@Test
	public void testEvaluateCisTrustRating() throws Exception {
		
		// trust rating
		final TrustedEntityId trusteeCisTeid = trusteeCisTeidList.get(0);
		final Double highRating = new Double(1.0d);
		final Double mediumRating = new Double(0.6d);
		final Double lowRating = new Double(0.2d);
		// timestamp
		final Date now = new Date();
		// Ugly hack for MySQL - remove ms precision from date
		final Date timestamp = new Date(1000 * (now.getTime() / 1000));
		ITrustEvidence evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.RATED, timestamp, highRating, null);

		Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify this has no effect as there is no direct trust relationship with the CSS yet
		assertNotNull(resultSet);
		assertTrue(resultSet.isEmpty());

		// create direct trust relationship with CSS
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.JOINED_COMMUNITY, timestamp, null, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify direct trust relationship with CSS
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCis);
		final ITrustedCis evaluatedCis = (ITrustedCis) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCis.getEvidence());
		assertTrue(evaluatedCis.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), 
				evaluatedCis.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertTrue(evaluatedCis.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		assertTrue(evaluatedCis.getDirectTrust().getValue() > 0.5d);

		// high trust rating
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.RATED, new Date(timestamp.getTime()+1000l), highRating, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify updated direct trust relationship with CSS
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCis);
		final ITrustedCis evaluatedCis2 = (ITrustedCis) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCis2.getEvidence());
		assertTrue(evaluatedCis2.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCis2.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis2.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis2.getDirectTrust().getLastModified(), 
				evaluatedCis2.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCis2.getDirectTrust().getRating());
		assertEquals(highRating, evaluatedCis2.getDirectTrust().getRating());
		assertNotNull(evaluatedCis2.getDirectTrust().getScore());
		assertTrue(evaluatedCis2.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCis2.getDirectTrust().getValue());
		assertTrue(evaluatedCis2.getDirectTrust().getValue() > evaluatedCis.getDirectTrust().getValue());
		
		// medium trust rating
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.RATED, new Date(timestamp.getTime()+2000l), mediumRating, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify updated direct trust relationship with CSS
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCis);
		final ITrustedCis evaluatedCis3 = (ITrustedCis) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCis3.getEvidence());
		assertTrue(evaluatedCis3.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCis3.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis3.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis3.getDirectTrust().getLastModified(), 
				evaluatedCis3.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCis3.getDirectTrust().getRating());
		assertEquals(mediumRating, evaluatedCis3.getDirectTrust().getRating());
		assertNotNull(evaluatedCis3.getDirectTrust().getScore());
		assertTrue(evaluatedCis3.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCis3.getDirectTrust().getValue());
		assertTrue(evaluatedCis3.getDirectTrust().getValue() < evaluatedCis2.getDirectTrust().getValue());
		assertEquals(evaluatedCis.getDirectTrust().getValue(), evaluatedCis3.getDirectTrust().getValue(), EPSILON);
		
		// low trust rating
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.RATED, new Date(timestamp.getTime()+3000l), lowRating, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify updated direct trust relationship with CSS
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCis);
		final ITrustedCis evaluatedCis4 = (ITrustedCis) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCis4.getEvidence());
		assertTrue(evaluatedCis4.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCis4.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis4.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis4.getDirectTrust().getLastModified(), 
				evaluatedCis4.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCis4.getDirectTrust().getRating());
		assertEquals(lowRating, evaluatedCis4.getDirectTrust().getRating());
		assertNotNull(evaluatedCis4.getDirectTrust().getScore());
		assertTrue(evaluatedCis4.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedCis4.getDirectTrust().getValue());
		assertTrue(evaluatedCis4.getDirectTrust().getValue() < 0.5d);
		assertTrue(evaluatedCis4.getDirectTrust().getValue() < evaluatedCis2.getDirectTrust().getValue());
		assertTrue(evaluatedCis4.getDirectTrust().getValue() < evaluatedCis3.getDirectTrust().getValue());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateCisMultipleEvidence() throws Exception {
		
		final TrustedEntityId trusteeCisTeid = trusteeCisTeidList.get(0);
		
		// Joined Community evidence
		// timestamp
		final Date now = new Date();
		// Ugly hack for MySQL - remove ms precision from date
		final Date timestamp = new Date(1000 * (now.getTime() / 1000));
		final ITrustEvidence evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.JOINED_COMMUNITY, timestamp, null, null);
		
		final Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		final ITrustedCss cisMember = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, myCssTeid);
		final ITrustedCis evaluatedCis = (ITrustedCis) this.trustRepo.retrieveEntity(myCssTeid, trusteeCisTeid);
		// verify
		assertNotNull(resultSet);
		assertTrue(!resultSet.isEmpty());
		assertEquals(1, resultSet.size());
		assertTrue(resultSet.contains(evaluatedCis));
		// from the member's side
		assertNotNull(cisMember);
		assertNotNull(cisMember.getDirectTrust().getRating());
		assertEquals(new Double(IDirectTrust.MAX_RATING), cisMember.getDirectTrust().getRating());
		assertNotNull(cisMember.getDirectTrust().getScore());
		assertEquals(new Double(IDirectTrust.MAX_SCORE), cisMember.getDirectTrust().getScore());
		assertNotNull(cisMember.getDirectTrust().getValue());
		assertEquals(new Double(ITrust.MAX_VALUE), cisMember.getDirectTrust().getValue());
		assertFalse(cisMember.getCommunities().isEmpty());
		assertTrue(cisMember.getCommunities().contains(evaluatedCis));
		// verify association with evidence
		assertNotNull(cisMember.getEvidence());
		assertTrue(cisMember.getEvidence().isEmpty());

		// from the community's side
		assertFalse(evaluatedCis.getMembers().isEmpty());
		assertTrue(evaluatedCis.getMembers().contains(cisMember));
		// verify association with evidence
		assertNotNull(evaluatedCis.getEvidence());
		assertTrue(evaluatedCis.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), 
				evaluatedCis.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		assertTrue(evaluatedCis.getDirectTrust().getValue() > 0.5d);
		
		// add another member
		final TrustedEntityId trusteeCssTeid2 = trusteeCssTeidList.get(0);
		
		// Joined Community evidence
		final Date timestamp2 = new Date(timestamp.getTime() + 1000);
		final ITrustEvidence evidence2 = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid2, trusteeCisTeid,
				TrustEvidenceType.JOINED_COMMUNITY, timestamp2, null, null);
		
		final Set<ITrustedEntity> resultSet2 = this.engine.evaluate(myCssTeid, evidence2);
		final ITrustedCss cisMember2 = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		final ITrustedCis updatedCis = (ITrustedCis) this.trustRepo.retrieveEntity(myCssTeid, trusteeCisTeid);
		// verify
		assertNotNull(resultSet2);
		assertTrue(!resultSet2.isEmpty());
		assertEquals(1, resultSet2.size());
		assertTrue(resultSet2.contains(updatedCis));
		// from the member's side
		assertNotNull(cisMember2);
		assertFalse(cisMember2.getCommunities().isEmpty());
		assertTrue(cisMember2.getCommunities().contains(updatedCis));
		// verify association with evidence
		assertNotNull(cisMember2.getEvidence());
		assertTrue(cisMember2.getEvidence().isEmpty());
		
		// from the community's side
		assertFalse(updatedCis.getMembers().isEmpty());
		// contains first member (myself)
		assertTrue(updatedCis.getMembers().contains(cisMember));
		// contains other member
		assertTrue(updatedCis.getMembers().contains(cisMember2));
		// verify association with evidence
		assertNotNull(updatedCis.getEvidence());
		assertTrue(updatedCis.getEvidence().contains(evidence));
		assertTrue(updatedCis.getEvidence().contains(evidence2));
		// verify updated trust
		assertNotNull(updatedCis.getDirectTrust().getLastModified());
		assertNotNull(updatedCis.getDirectTrust().getLastUpdated());
		assertTrue(Math.abs(updatedCis.getDirectTrust().getLastModified().getTime() - 
				updatedCis.getDirectTrust().getLastUpdated().getTime()) < 1000);
		assertNull(updatedCis.getDirectTrust().getRating());
		assertNotNull(updatedCis.getDirectTrust().getScore());
		assertTrue(updatedCis.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(updatedCis.getDirectTrust().getValue());
		assertEquals(evaluatedCis.getDirectTrust().getValue(), updatedCis.getDirectTrust().getValue(), EPSILON);
		
		final int NUM_EXTRA_CSS = 10;
		final int NUM_EXTRA_CIS = 6;
		
		// Add DIRECTLY trusted CSSs to the trust DB
		for (int i = 1; i <= NUM_EXTRA_CSS; ++i) {
			ITrustedEntity entity = this.trustRepo.createEntity(myCssTeid, trusteeCssTeidList.get(i));
			entity.getDirectTrust().setValue(0.5d);
			this.trustRepo.updateEntity(entity);
		}
		
		// Add INDIRECTLY trusted CSSs to the trust DB
		for (int i = NUM_EXTRA_CSS+1; i <= 2*NUM_EXTRA_CSS; ++i) {
			ITrustedEntity entity = this.trustRepo.createEntity(myCssTeid, trusteeCssTeidList.get(i));
			entity.getIndirectTrust().setValue(0.5d);
			this.trustRepo.updateEntity(entity);
		}
		
		// Add INDIRECTLY trusted CISs to the trust DB
		for (int i = 1; i <= NUM_EXTRA_CIS; ++i) {
			ITrustedEntity entity = this.trustRepo.createEntity(myCssTeid, trusteeCisTeidList.get(i));
			entity.getIndirectTrust().setValue(0.5d);
			this.trustRepo.updateEntity(entity);
		}
		
		// Updated trust in CIS member evidence
		final Date timestamp3 = new Date(timestamp2.getTime() + 1000);
		final ITrustEvidence evidence3 = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid2,
				TrustEvidenceType.WITHHELD_CONTEXT, timestamp3, null, null);

		final Set<ITrustedEntity> resultSet3 = this.engine.evaluate(myCssTeid, evidence3);
		final ITrustedCss updatedCisMember2 = (ITrustedCss) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		final ITrustedCis updated2Cis = (ITrustedCis) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeCisTeid);
		// verify
		assertNotNull(resultSet3);
		assertTrue(!resultSet3.isEmpty());
		assertEquals(NUM_EXTRA_CSS+2, resultSet3.size());
		assertTrue(resultSet3.contains(updatedCisMember2));
		assertTrue(resultSet3.contains(updated2Cis));
		// from the member's side
		assertNotNull(updatedCisMember2);
		assertFalse(updatedCisMember2.getCommunities().isEmpty());
		assertTrue(updatedCisMember2.getCommunities().contains(updatedCis));
		// verify association with evidence
		assertNotNull(updatedCisMember2.getEvidence());
		assertFalse(updatedCisMember2.getEvidence().isEmpty());
		assertTrue(updatedCisMember2.getEvidence().contains(evidence3));

		// from the community's side
		assertFalse(updated2Cis.getMembers().isEmpty());
		// contains first member (myself)
		assertTrue(updated2Cis.getMembers().contains(cisMember));
		// contains other member
		assertTrue(updated2Cis.getMembers().contains(updatedCisMember2));
		// verify association with evidence
		assertNotNull(updated2Cis.getEvidence());
		assertTrue(updated2Cis.getEvidence().contains(evidence));
		assertTrue(updated2Cis.getEvidence().contains(evidence2));
		// verify updated trust
		assertNotNull(updated2Cis.getDirectTrust().getLastModified());
		assertNotNull(updated2Cis.getDirectTrust().getLastUpdated());
		assertEquals(updated2Cis.getDirectTrust().getLastModified().getTime(), 
				updated2Cis.getDirectTrust().getLastUpdated().getTime());
		assertNull(updated2Cis.getDirectTrust().getRating());
		assertNotNull(updated2Cis.getDirectTrust().getScore());
		assertTrue(updated2Cis.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(updated2Cis.getDirectTrust().getValue());
		assertEquals(updatedCisMember2.getDirectTrust().getValue(), updated2Cis.getDirectTrust().getValue(), EPSILON);
		
		// remove last member

		// Left Community evidence
		final Date timestamp4 = new Date(timestamp3.getTime() + 1000);
		final ITrustEvidence evidence4 = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid2, trusteeCisTeid,
				TrustEvidenceType.LEFT_COMMUNITY, timestamp4, null, null);

		final Set<ITrustedEntity> resultSet4 = this.engine.evaluate(myCssTeid, evidence4);
		final ITrustedCss updated2CisMember2 = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		final ITrustedCis updated3Cis = (ITrustedCis) this.trustRepo.retrieveEntity(myCssTeid, trusteeCisTeid);
		// verify
		assertNotNull(resultSet4);
		assertFalse(resultSet4.isEmpty());
		assertEquals(1, resultSet4.size());
		assertTrue(resultSet4.contains(updated3Cis));
		// from the member's side
		assertNotNull(updated2CisMember2);
		assertTrue(updated2CisMember2.getCommunities().isEmpty());
		// from the community's side
		assertFalse(updated3Cis.getMembers().isEmpty());
		// contains first member (myself)
		assertTrue(updated3Cis.getMembers().contains(cisMember));
		// should not contain other member
		assertFalse(updated3Cis.getMembers().contains(updated2CisMember2));
		assertNotNull(updated3Cis.getDirectTrust().getLastModified());
		assertNotNull(updated3Cis.getDirectTrust().getLastUpdated());
		assertNull(updated3Cis.getDirectTrust().getRating());
		assertNotNull(updated3Cis.getDirectTrust().getScore());
		assertTrue(updated3Cis.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(updated3Cis.getDirectTrust().getValue());
		assertEquals(evaluatedCis.getDirectTrust().getValue(), updated3Cis.getDirectTrust().getValue(), EPSILON);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception
	 */
	@Test
	public void testEvaluateServiceTrustRating() throws Exception {
		
		// trust rating
		final TrustedEntityId trusteeSvcTeid = trusteeServiceTeidList.get(0);
		final Double rating = new Double(0.8d);
		final Date timestamp = new Date();
		ITrustEvidence evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeSvcTeid,
				TrustEvidenceType.RATED, timestamp, rating, null);

		Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify this has no effect as there is no direct trust relationship with the Service yet
		assertNotNull(resultSet);
		assertTrue(resultSet.isEmpty());

		// create direct trust relationship with Service
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeSvcTeid,
				TrustEvidenceType.USED_SERVICE, timestamp, null, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify direct trust relationship with Service
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedService);
		ITrustedService evaluatedSvc = (ITrustedService) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedSvc.getEvidence());
		assertTrue(evaluatedSvc.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedSvc.getDirectTrust().getLastModified());
		assertNotNull(evaluatedSvc.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedSvc.getDirectTrust().getLastModified(), 
				evaluatedSvc.getDirectTrust().getLastUpdated());
		assertNull(evaluatedSvc.getDirectTrust().getRating());
		assertNotNull(evaluatedSvc.getDirectTrust().getScore());
		assertTrue(evaluatedSvc.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedSvc.getDirectTrust().getValue());

		// trust rating
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeSvcTeid,
				TrustEvidenceType.RATED, timestamp, rating, null);
		resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify updated direct trust relationship with Service
		assertNotNull(resultSet);
		assertFalse(resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedService);
		evaluatedSvc = (ITrustedService) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedSvc.getEvidence());
		assertTrue(evaluatedSvc.getEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedSvc.getDirectTrust().getLastModified());
		assertNotNull(evaluatedSvc.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedSvc.getDirectTrust().getLastModified(), 
				evaluatedSvc.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedSvc.getDirectTrust().getRating());
		assertEquals(rating, evaluatedSvc.getDirectTrust().getRating());
		assertNotNull(evaluatedSvc.getDirectTrust().getScore());
		assertTrue(evaluatedSvc.getDirectTrust().getScore() > IDirectTrust.INIT_SCORE);
		assertNotNull(evaluatedSvc.getDirectTrust().getValue());
		assertTrue(evaluatedSvc.getDirectTrust().getValue() >= 0.6d);
	}
}