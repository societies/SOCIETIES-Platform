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
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.model.IDirectTrust;
import org.societies.privacytrust.trust.api.model.ITrust;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.impl.evidence.repo.model.DirectTrustEvidence;
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
		when(mockTrustNodeMgr.getMyIds()).thenReturn(myIds);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateOneCssOneTrustRating() throws Exception {
		
		// trust rating
		final TrustedEntityId trusteeCssTeid = trusteeCssTeidList.get(0);
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		IDirectTrustEvidence evidence = new DirectTrustEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp, rating);
		
		final Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify
		assertNotNull(resultSet);
		assertTrue(!resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCss);
		final ITrustedCss evaluatedCss = (ITrustedCss) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCss.getDirectEvidence());
		assertTrue(evaluatedCss.getDirectEvidence().contains(evidence));
		// verify updated trust
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
		assertTrue(evaluatedCss.getDirectTrust().getValue() >= rating);
		
		// clean database
		this.trustRepo.removeEntity(myCssTeid, trusteeCssTeid);
		this.trustEvidenceRepo.removeDirectEvidence(myCssTeid, null, null, null, null);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, Set)}.
	 * @throws Exception
	 */
	@Test
	public void testEvaluateOneCssMultipleTrustRatings() throws Exception {
		
		final TrustedEntityId trusteeCssTeid = trusteeCssTeidList.get(0);
		
		final Set<IDirectTrustEvidence> evidenceSet = new HashSet<IDirectTrustEvidence>();
		// trust rating
		final Double rating = new Double(0.4d);
		// timestamp
		final Date now = new Date();
		// Ugly hack for MySQL - remove ms precision from date
		final Date timestamp = new Date(1000 * (now.getTime() / 1000));
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp, rating);
		evidenceSet.add(evidence1);
		
		// trust rating2
		final Double rating2 = new Double(0.5d);
		final Date timestamp2 = new Date(timestamp.getTime() + 1000);
		final IDirectTrustEvidence evidence2 = new DirectTrustEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp2, rating2);
		evidenceSet.add(evidence2);
		
		// trust rating3
		final Double rating3 = new Double(0.6d);
		final Date timestamp3 = new Date(timestamp.getTime() - 1000);
		final IDirectTrustEvidence evidence3 = new DirectTrustEvidence(
				myCssTeid, trusteeCssTeid,
				TrustEvidenceType.RATED, timestamp3, rating3);
		evidenceSet.add(evidence3);
		
		final Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidenceSet);
		// verify
		assertNotNull(resultSet);
		assertTrue(!resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCss);
		final ITrustedCss evaluatedCss = (ITrustedCss) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCss.getDirectEvidence());
		assertTrue(evaluatedCss.getDirectEvidence().containsAll(evidenceSet));
		// verify updated trust
		assertNotNull(evaluatedCss.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCss.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCss.getDirectTrust().getLastModified(), 
				evaluatedCss.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedCss.getDirectTrust().getRating());
		assertEquals(rating2, evaluatedCss.getDirectTrust().getRating());
		assertNotNull(evaluatedCss.getDirectTrust().getScore());
		assertEquals(new Double(IDirectTrust.INIT_SCORE), evaluatedCss.getDirectTrust().getScore());
		assertNotNull(evaluatedCss.getDirectTrust().getValue());
		//System.out.println(evaluatedCss.getDirectTrust().getValue());
		assertTrue(evaluatedCss.getDirectTrust().getValue() >= rating2);
		
		// clean database
		this.trustRepo.removeEntity(myCssTeid, trusteeCssTeid);
		this.trustEvidenceRepo.removeDirectEvidence(myCssTeid, null, null, null, null);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCssTrustValues(List, List).
	 * @throws TrustEngineException 
	 *
	@Test
	public void testEvaluateMultipleCssMultipleTrustRatings() throws TrustEngineException {
		
		final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>(TRUSTEE_CSS_LIST_SIZE);
		final List<Double> ratingsList = new ArrayList<Double>(TRUSTEE_CSS_LIST_SIZE);
		final Random randomGenerator = new Random();
		for (int i = 0; i < TRUSTEE_CSS_LIST_SIZE; ++i) {
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
		
		this.engine.evaluateCss(trustedCssList, evidenceList);
		for (int i = 0; i < TRUSTEE_CSS_LIST_SIZE; ++i) {
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
			//System.out.println("score=" + evaluatedCss.getDirectTrust().getScore()
			//		+ ", rating=" + evaluatedCss.getDirectTrust().getRating() 
			//		+ ", value=" + evaluatedCss.getDirectTrust().getValue());
			//assertTrue(evaluatedCss.getDirectTrust().getValue() >= ratingsList.get(i)); // TODO
		}
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception
	 */
	@Test
	public void testEvaluateOneCisOneTrustRating() throws Exception {
		
		// trust rating
		final TrustedEntityId trusteeCisTeid = trusteeCisTeidList.get(0);
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence = new DirectTrustEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.RATED, timestamp, rating);
		
		final Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify
		assertNotNull(resultSet);
		assertTrue(!resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedCis);
		final ITrustedCis evaluatedCis = (ITrustedCis) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedCis.getDirectEvidence());
		assertTrue(evaluatedCis.getDirectEvidence().contains(evidence));
		// verify updated trust
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
		assertTrue(evaluatedCis.getDirectTrust().getValue() >= rating);
		
		// clean database
		this.trustRepo.removeEntity(myCssTeid, trusteeCisTeid);
		this.trustEvidenceRepo.removeDirectEvidence(myCssTeid, null, null, null, null);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateOneCisMultipleLifecycleEvents() throws Exception {
		
		final TrustedEntityId trusteeCisTeid = trusteeCisTeidList.get(1);
		
		// Joined Community evidence
		// timestamp
		final Date now = new Date();
		// Ugly hack for MySQL - remove ms precision from date
		final Date timestamp = new Date(1000 * (now.getTime() / 1000));
		final IDirectTrustEvidence evidence = new DirectTrustEvidence(
				myCssTeid, trusteeCisTeid,
				TrustEvidenceType.JOINED_COMMUNITY, timestamp, null);
		
		final Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		final ITrustedCss cisMember = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, myCssTeid);
		final ITrustedCis evaluatedCis = (ITrustedCis) this.trustRepo.retrieveEntity(myCssTeid, trusteeCisTeid);
		// verify
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
		assertNotNull(cisMember.getDirectEvidence());
		assertTrue(cisMember.getDirectEvidence().isEmpty());

		// from the community's side
		assertNotNull(resultSet);
		assertTrue(!resultSet.isEmpty());
		assertTrue(resultSet.size() >= 1);
		assertTrue(resultSet.contains(evaluatedCis));
		assertFalse(evaluatedCis.getMembers().isEmpty());
		assertTrue(evaluatedCis.getMembers().contains(cisMember));
		// verify association with evidence
		assertNotNull(evaluatedCis.getDirectEvidence());
		assertTrue(evaluatedCis.getDirectEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedCis.getDirectTrust().getLastModified(), 
				evaluatedCis.getDirectTrust().getLastUpdated());
		assertNull(evaluatedCis.getDirectTrust().getRating());
		assertNotNull(evaluatedCis.getDirectTrust().getScore());
		assertEquals(cisMember.getDirectTrust().getScore(), evaluatedCis.getDirectTrust().getScore());
		assertNotNull(evaluatedCis.getDirectTrust().getValue());
		//System.out.println(evaluatedCis.getDirectTrust().getValue());
		//assertEquals(new Double(ITrust.MAX_VALUE/2d), evaluatedCis.getDirectTrust().getValue(), 0.2d);
		
		// add another member
		final TrustedEntityId trusteeCssTeid2 = trusteeCssTeidList.get(0);
		
		// Joined Community evidence
		final Date timestamp2 = new Date(timestamp.getTime() + 1000);
		final IDirectTrustEvidence evidence2 = new DirectTrustEvidence(
				trusteeCssTeid2, trusteeCisTeid,
				TrustEvidenceType.JOINED_COMMUNITY, timestamp2, null);
		
		final Set<ITrustedEntity> resultSet2 = this.engine.evaluate(myCssTeid, evidence2);
		final ITrustedCss cisMember2 = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		final ITrustedCis evaluatedCis2 = (ITrustedCis) this.trustRepo.retrieveEntity(myCssTeid, trusteeCisTeid);
		// verify
		// from the member's side
		assertNotNull(cisMember2);
		assertFalse(cisMember2.getCommunities().isEmpty());
		assertTrue(cisMember2.getCommunities().contains(evaluatedCis2));
		// verify association with evidence
		assertNotNull(cisMember2.getDirectEvidence());
		assertTrue(cisMember2.getDirectEvidence().isEmpty());
		
		// from the community's side
		assertNotNull(resultSet2);
		assertTrue(!resultSet2.isEmpty());
		assertTrue(resultSet2.size() >= 1);
		assertFalse(evaluatedCis2.getMembers().isEmpty());
		// contains first member (myself)
		assertTrue(evaluatedCis2.getMembers().contains(cisMember));
		// contains other member
		assertTrue(evaluatedCis2.getMembers().contains(cisMember2));
		// verify association with evidence
		assertNotNull(evaluatedCis2.getDirectEvidence());
		assertTrue(evaluatedCis2.getDirectEvidence().contains(evidence));
		assertTrue(evaluatedCis2.getDirectEvidence().contains(evidence2));
		// verify updated trust
		assertNotNull(evaluatedCis2.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis2.getDirectTrust().getLastUpdated());
		assertTrue(Math.abs(evaluatedCis2.getDirectTrust().getLastModified().getTime() - 
				evaluatedCis2.getDirectTrust().getLastUpdated().getTime()) < 1000);
		assertNull(evaluatedCis2.getDirectTrust().getRating());
		assertNotNull(evaluatedCis2.getDirectTrust().getScore());
		assertEquals(cisMember2.getDirectTrust().getScore(), evaluatedCis2.getDirectTrust().getScore());
		assertNotNull(evaluatedCis2.getDirectTrust().getValue());
		//System.out.println(evaluatedCis2.getDirectTrust().getValue());
		assertTrue(evaluatedCis.getDirectTrust().getValue() >= evaluatedCis2.getDirectTrust().getValue());
		
		// remove last member

		// Left Community evidence
		final Date timestamp3 = new Date(timestamp2.getTime() + 1000);
		final IDirectTrustEvidence evidence3 = new DirectTrustEvidence(
				trusteeCssTeid2, trusteeCisTeid,
				TrustEvidenceType.LEFT_COMMUNITY, timestamp3, null);

		final Set<ITrustedEntity> resultSet3 = this.engine.evaluate(myCssTeid, evidence3);
		final ITrustedCss cisMember3 = (ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		final ITrustedCis evaluatedCis3 = (ITrustedCis) this.trustRepo.retrieveEntity(myCssTeid, trusteeCisTeid);
		// verify
		// from the member's side
		assertNotNull(cisMember3);
		assertTrue(cisMember3.getCommunities().isEmpty());
		// from the community's side
		assertNotNull(resultSet3);
		assertTrue(!resultSet3.isEmpty());
		assertTrue(resultSet3.size() >= 1);
		assertFalse(evaluatedCis3.getMembers().isEmpty());
		// contains first member (myself)
		assertTrue(evaluatedCis3.getMembers().contains(cisMember));
		// should not contain other member
		assertFalse(evaluatedCis3.getMembers().contains(cisMember3));
		assertNotNull(evaluatedCis3.getDirectTrust().getLastModified());
		assertNotNull(evaluatedCis3.getDirectTrust().getLastUpdated());
		assertTrue(Math.abs(evaluatedCis3.getDirectTrust().getLastModified().getTime() - 
				evaluatedCis3.getDirectTrust().getLastUpdated().getTime()) < 1000);
		assertNull(evaluatedCis3.getDirectTrust().getRating());
		assertNotNull(evaluatedCis3.getDirectTrust().getScore());
		assertEquals(cisMember.getDirectTrust().getScore(), evaluatedCis3.getDirectTrust().getScore());
		assertNotNull(evaluatedCis3.getDirectTrust().getValue());
		//System.out.println(evaluatedCis3.getDirectTrust().getValue());
		assertTrue(evaluatedCis2.getDirectTrust().getValue() <= evaluatedCis3.getDirectTrust().getValue());
		
		// clean database
		this.trustRepo.removeEntity(myCssTeid, myCssTeid);
		this.trustRepo.removeEntity(myCssTeid, trusteeCssTeid2);
		this.trustRepo.removeEntity(myCssTeid, trusteeCisTeid);
		this.trustEvidenceRepo.removeDirectEvidence(myCssTeid, null, null, null, null);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateCis(List, List)}.
	 *
	@Test
	public void testEvaluateMultipleCisMultipleLifecycleEvents() throws TrustEngineException {
		
		final List<ITrustEvidence> evidenceList = 
				new ArrayList<ITrustEvidence>(TRUSTEE_CIS_LIST_SIZE);
		final List<TrustEvidenceType> lifecycleEventsList = 
				new ArrayList<TrustEvidenceType>(TRUSTEE_CIS_LIST_SIZE);
		
		myCss.getDirectTrust().setScore(1d);
		myCss.getDirectTrust().setRating(1d);
		for (int i = 0; i < TRUSTEE_CIS_LIST_SIZE; ++i) {
			final TrustEvidenceType evidenceType = (i%4 != 0) 
					? TrustEvidenceType.JOINED_COMMUNITY 
					: TrustEvidenceType.LEFT_COMMUNITY;
			if (TrustEvidenceType.JOINED_COMMUNITY.equals(evidenceType))
				myCss.addCommunity(trustedCisList.get(i));
			else // if (TrustEvidenceType.LEFT_COMMUNITY.equals(evidenceType))
				myCss.removeCommunity(trustedCisList.get(i));
			final Date timestamp = new Date();
			final IDirectTrustEvidence evidence = new DirectTrustEvidence(
					trustedCisList.get(i).getTeid(), evidenceType, timestamp, null);
			lifecycleEventsList.add(evidenceType);
			evidenceList.add(evidence);
		}
		
		this.engine.evaluateCis(trustedCisList, evidenceList);
		for (int i = 0; i < TRUSTEE_CIS_LIST_SIZE; ++i) {
			final ITrustedCis evaluatedCis = trustedCisList.get(i);
			assertNotNull(evaluatedCis.getDirectTrust().getLastModified());
			assertNotNull(evaluatedCis.getDirectTrust().getLastUpdated());
			assertEquals(evaluatedCis.getDirectTrust().getLastModified(),
					evaluatedCis.getDirectTrust().getLastUpdated());
			if (evaluatedCis.getMembers().contains(myCss))
				assertEquals(myCss.getDirectTrust().getRating(), evaluatedCis.getDirectTrust().getRating());
			else
				assertNull(evaluatedCis.getDirectTrust().getRating());
			assertNotNull(evaluatedCis.getDirectTrust().getScore());
			if (evaluatedCis.getMembers().contains(myCss))
				assertEquals(myCss.getDirectTrust().getScore(), evaluatedCis.getDirectTrust().getScore());
			else
				assertEquals(new Double(0d), evaluatedCis.getDirectTrust().getScore());
			assertNotNull(evaluatedCis.getDirectTrust().getValue());
			//System.out.println(evaluatedCis.getDirectTrust().getValue());
			//assertEquals(???, evaluatedCis.getDirectTrust().getValue()); // TODO
		}
	}*
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(TrustedEntityId, IDirectTrustEvidence)}.
	 * @throws Exception
	 */
	@Test
	public void testEvaluateOneServiceTrustRating() throws Exception {
		
		// trust rating
		final TrustedEntityId trusteeServiceTeid = trusteeServiceTeidList.get(0);
		final Double rating = new Double(0.5d);
		final Date timestamp = new Date();
		final IDirectTrustEvidence evidence = new DirectTrustEvidence(
				myCssTeid, trusteeServiceTeid,
				TrustEvidenceType.RATED, timestamp, rating);

		final Set<ITrustedEntity> resultSet = this.engine.evaluate(myCssTeid, evidence);
		// verify
		assertNotNull(resultSet);
		assertTrue(!resultSet.isEmpty());
		assertTrue(resultSet.size() == 1);
		assertTrue(resultSet.iterator().next() instanceof ITrustedService);
		final ITrustedService evaluatedService = (ITrustedService) resultSet.iterator().next();
		// verify association with evidence
		assertNotNull(evaluatedService.getDirectEvidence());
		assertTrue(evaluatedService.getDirectEvidence().contains(evidence));
		// verify updated trust
		assertNotNull(evaluatedService.getDirectTrust().getLastModified());
		assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedService.getDirectTrust().getLastModified(), 
				evaluatedService.getDirectTrust().getLastUpdated());
		assertNotNull(evaluatedService.getDirectTrust().getRating());
		assertEquals(rating, evaluatedService.getDirectTrust().getRating());
		assertNotNull(evaluatedService.getDirectTrust().getScore());
		assertEquals(new Double(0.0d), evaluatedService.getDirectTrust().getScore());
		assertNotNull(evaluatedService.getDirectTrust().getValue());
		//System.out.println(evaluatedCss.getDirectTrust().getValue());
		assertTrue(evaluatedService.getDirectTrust().getValue() >= rating);
		
		// clean database
		this.trustRepo.removeEntity(myCssTeid, trusteeServiceTeid);
		this.trustEvidenceRepo.removeDirectEvidence(myCssTeid, null, null, null, null);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateService(List, List)}.
	 *
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
		
		this.engine.evaluateService(trustedServiceSubList, evidenceList);
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
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateService(List, List)}.
	 *
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
		
		this.engine.evaluateService(trustedServiceSubList, evidenceList);
		ITrustedService evaluatedService = trustedServiceSubList.get(0);
		assertNotNull(evaluatedService.getDirectTrust().getLastModified());
		assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
		assertEquals(evaluatedService.getDirectTrust().getLastModified(), 
				evaluatedService.getDirectTrust().getLastUpdated());
		assertNull(evaluatedService.getDirectTrust().getRating());
		assertNotNull(evaluatedService.getDirectTrust().getScore());
		assertEquals(DirectTrustEngine.EVIDENCE_SCORE_MAP.get(evidence1.getType()),
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
		
		this.engine.evaluateService(trustedServiceSubList, evidenceList);
		evaluatedService = trustedServiceSubList.get(0);
		assertNotNull(evaluatedService.getDirectTrust().getLastModified());
		assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
		assertNull(evaluatedService.getDirectTrust().getRating());
		assertNotNull(evaluatedService.getDirectTrust().getScore());
		assertEquals(new Double(10001 * DirectTrustEngine.EVIDENCE_SCORE_MAP.get(TrustEvidenceType.USED_SERVICE)),
				evaluatedService.getDirectTrust().getScore());
		assertNotNull(evaluatedService.getDirectTrust().getValue());
		final Double trustValueAfterEvidence2 = new Double(evaluatedService.getDirectTrust().getValue());
		//System.out.println(trustValueAfterEvidence2);
		//assertEquals(???, trustValueAfterEvidence2); // TODO
		
		assertTrue(trustValueAfterEvidence1 >= trustValueAfterEvidence2);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.DirectTrustEngine#evaluateService(List, List)}.
	 *
	@Test
	public void testEvaluateMultipleServiceMultipleLifecycleEvents() throws TrustEngineException {
		
		final List<ITrustEvidence> evidenceList = 
				new ArrayList<ITrustEvidence>(TRUSTEE_SERVICE_LIST_SIZE);
		final List<Integer> serviceEventsList = 
				new ArrayList<Integer>(TRUSTEE_SERVICE_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_SERVICE_LIST_SIZE; ++i) {
			for (int j = 0; j < i; ++j) {
				final Date timestamp = new Date(new Date().getTime()+j*1000);
				final IDirectTrustEvidence evidence = new DirectTrustEvidence(
						trustedServiceList.get(i).getTeid(), TrustEvidenceType.USED_SERVICE, timestamp, null);
				evidenceList.add(evidence);
			}
			serviceEventsList.add(new Integer(i));
		}
		
		this.engine.evaluateService(trustedServiceList, evidenceList);
		for (int i = 0; i < TRUSTEE_SERVICE_LIST_SIZE; ++i) {
			final ITrustedService evaluatedService = trustedServiceList.get(i);
			assertNotNull(evaluatedService.getDirectTrust().getLastModified());
			assertNotNull(evaluatedService.getDirectTrust().getLastUpdated());
			assertEquals(evaluatedService.getDirectTrust().getLastModified(),
					evaluatedService.getDirectTrust().getLastUpdated());
			assertNull(evaluatedService.getDirectTrust().getRating());
			assertNotNull(evaluatedService.getDirectTrust().getScore());
			assertEquals(new Double(
					serviceEventsList.get(i) * DirectTrustEngine.EVIDENCE_SCORE_MAP.get(TrustEvidenceType.USED_SERVICE)), 
					evaluatedService.getDirectTrust().getScore());
			assertNotNull(evaluatedService.getDirectTrust().getValue());
			//System.out.println(evaluatedService.getDirectTrust().getValue());
			//assertEquals(???, evaluatedService.getDirectTrust().getValue()); // TODO
		}
	}*/
}