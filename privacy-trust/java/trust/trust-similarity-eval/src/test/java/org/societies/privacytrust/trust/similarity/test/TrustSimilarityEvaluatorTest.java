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
package org.societies.privacytrust.trust.similarity.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.model.IDirectTrust;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.api.similarity.ITrustSimilarityEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test cases for the TrustSimilarityEvaluator.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/TrustSimilarityEvaluatorTest-context.xml"})
public class TrustSimilarityEvaluatorTest {
	
	private static double DELTA = 0.000001d;
	
	private static TrustedEntityId trustorId;
	
	private static TrustedEntityId trusteeId;
	
	@Autowired
	@InjectMocks
	private ITrustSimilarityEvaluator trustSimilarityEvaluator;
	
	@Mock
	private ITrustRepository mockTrustRepository;
	
	@Mock
	private ITrustEvidenceRepository mockTrustEvidenceRepository;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		trustorId = new TrustedEntityId(TrustedEntityType.CSS, "trustorId");
		trusteeId = new TrustedEntityId(TrustedEntityType.CSS, "trusteeId");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		trustorId = null;
		trusteeId = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityHigh() throws Exception {
		
		// Setup test data
		final TrustedEntityId[] cssIds = new TrustedEntityId[4];
		for (int i = 0; i < cssIds.length; ++i)
			cssIds[i] = new TrustedEntityId(TrustedEntityType.CSS, "fooCss" + i);
		final TrustedEntityId[] cisIds = new TrustedEntityId[4];
		for (int i = 0; i < cisIds.length; ++i)
			cisIds[i] = new TrustedEntityId(TrustedEntityType.CIS, "fooCis" + i);
		final TrustedEntityId[] svcIds = new TrustedEntityId[4];
		for (int i = 0; i < svcIds.length; ++i)
			svcIds[i] = new TrustedEntityId(TrustedEntityType.SVC, "fooSvc" + i);
		
		final LinkedHashMap<TrustedEntityId, Double> trustorCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCssRelationships.put(cssIds[0], 0.6d);
		trustorCssRelationships.put(cssIds[1], 0.7d);
		trustorCssRelationships.put(cssIds[2], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trustorCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCisRelationships.put(cisIds[0], 0.6d);
		trustorCisRelationships.put(cisIds[1], 0.7d);
		trustorCisRelationships.put(cisIds[2], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trustorSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorSvcRelationships.put(svcIds[0], 0.6d);
		trustorSvcRelationships.put(svcIds[1], 0.7d);
		trustorSvcRelationships.put(svcIds[2], 0.6d);
		
		final LinkedHashMap<TrustedEntityId, Double> trusteeCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCssRelationships.put(cssIds[0], 0.6d);
		trusteeCssRelationships.put(cssIds[1], 0.6d);
		trusteeCssRelationships.put(cssIds[3], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCisRelationships.put(cisIds[0], 0.6d);
		trusteeCisRelationships.put(cisIds[1], 0.6d);
		trusteeCisRelationships.put(cisIds[3], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeSvcRelationships.put(svcIds[0], 0.6d);
		trusteeSvcRelationships.put(svcIds[1], 0.7d);
		trusteeSvcRelationships.put(svcIds[3], 0.6d);
		
		final Set<ITrustedEntity> trustedEntities = 
				new HashSet<ITrustedEntity>(trustorCssRelationships.size());
		for (final TrustedEntityId teid : trustorCssRelationships.keySet()) {
			final ITrustedCss mockCss = mock(ITrustedCss.class);
			when(mockCss.getTrustorId()).thenReturn(trustorId);
			when(mockCss.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCss.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCssRelationships.get(teid));
			trustedEntities.add(mockCss);
		}	
		for (final TrustedEntityId teid : trustorCisRelationships.keySet()) {
			final ITrustedCis mockCis = mock(ITrustedCis.class);
			when(mockCis.getTrustorId()).thenReturn(trustorId);
			when(mockCis.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCis.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCisRelationships.get(teid));
			trustedEntities.add(mockCis);
		}
		for (final TrustedEntityId teid : trustorSvcRelationships.keySet()) {
			final ITrustedService mockSvc = mock(ITrustedService.class);
			when(mockSvc.getTrustorId()).thenReturn(trustorId);
			when(mockSvc.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockSvc.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorSvcRelationships.get(teid));
			trustedEntities.add(mockSvc);
		}
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))
				.thenReturn(trustedEntities);
		
		final SortedSet<ITrustEvidence> trusteeEvidence = new TreeSet<ITrustEvidence>();
		for (final TrustedEntityId teid : trusteeCssRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCssRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeCisRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCisRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeSvcRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeSvcRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		when(this.mockTrustEvidenceRepository.retrieveLatestEvidence(
				trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null)).thenReturn(trusteeEvidence);
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		assertTrue(similarity > 0.9d);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityLow() throws Exception {
		
		// Setup test data
		final TrustedEntityId[] cssIds = new TrustedEntityId[4];
		for (int i = 0; i < cssIds.length; ++i)
			cssIds[i] = new TrustedEntityId(TrustedEntityType.CSS, "fooCss" + i);
		final TrustedEntityId[] cisIds = new TrustedEntityId[4];
		for (int i = 0; i < cisIds.length; ++i)
			cisIds[i] = new TrustedEntityId(TrustedEntityType.CIS, "fooCis" + i);
		final TrustedEntityId[] svcIds = new TrustedEntityId[4];
		for (int i = 0; i < svcIds.length; ++i)
			svcIds[i] = new TrustedEntityId(TrustedEntityType.SVC, "fooSvc" + i);
		
		final LinkedHashMap<TrustedEntityId, Double> trustorCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCssRelationships.put(cssIds[0], 0.5d);
		trustorCssRelationships.put(cssIds[1], 0.6d);
		trustorCssRelationships.put(cssIds[2], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trustorCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCisRelationships.put(cisIds[0], 0.5d);
		trustorCisRelationships.put(cisIds[1], 0.6d);
		trustorCisRelationships.put(cisIds[2], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trustorSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorSvcRelationships.put(svcIds[0], 0.5d);
		trustorSvcRelationships.put(svcIds[1], 0.6d);
		trustorSvcRelationships.put(svcIds[2], 0.5d);
		
		final LinkedHashMap<TrustedEntityId, Double> trusteeCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCssRelationships.put(cssIds[0], 0.1d);
		trusteeCssRelationships.put(cssIds[1], 0.8d);
		trusteeCssRelationships.put(cssIds[3], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCisRelationships.put(cisIds[0], 0.9d);
		trusteeCisRelationships.put(cisIds[1], 0.2d);
		trusteeCisRelationships.put(cisIds[3], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeSvcRelationships.put(svcIds[0], 0.9d);
		trusteeSvcRelationships.put(svcIds[1], 0.9d);
		trusteeSvcRelationships.put(svcIds[3], 0.5d);
		
		final Set<ITrustedEntity> trustedEntities = 
				new HashSet<ITrustedEntity>(trustorCssRelationships.size());
		for (final TrustedEntityId teid : trustorCssRelationships.keySet()) {
			final ITrustedCss mockCss = mock(ITrustedCss.class);
			when(mockCss.getTrustorId()).thenReturn(trustorId);
			when(mockCss.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCss.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCssRelationships.get(teid));
			trustedEntities.add(mockCss);
		}
		for (final TrustedEntityId teid : trustorCisRelationships.keySet()) {
			final ITrustedCis mockCis = mock(ITrustedCis.class);
			when(mockCis.getTrustorId()).thenReturn(trustorId);
			when(mockCis.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCis.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCisRelationships.get(teid));
			trustedEntities.add(mockCis);
		}
		for (final TrustedEntityId teid : trustorSvcRelationships.keySet()) {
			final ITrustedService mockSvc = mock(ITrustedService.class);
			when(mockSvc.getTrustorId()).thenReturn(trustorId);
			when(mockSvc.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockSvc.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorSvcRelationships.get(teid));
			trustedEntities.add(mockSvc);
		}
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))
				.thenReturn(trustedEntities);
		
		final SortedSet<ITrustEvidence> trusteeEvidence = new TreeSet<ITrustEvidence>();
		for (final TrustedEntityId teid : trusteeCssRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCssRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeCisRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCisRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeSvcRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeSvcRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		when(this.mockTrustEvidenceRepository.retrieveLatestEvidence(
				trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null)).thenReturn(trusteeEvidence);
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		assertTrue(similarity < 0.9d);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityMax() throws Exception {
		
		// Setup test data
		final TrustedEntityId[] cssIds = new TrustedEntityId[4];
		for (int i = 0; i < cssIds.length; ++i)
			cssIds[i] = new TrustedEntityId(TrustedEntityType.CSS, "fooCss" + i);
		final TrustedEntityId[] cisIds = new TrustedEntityId[4];
		for (int i = 0; i < cisIds.length; ++i)
			cisIds[i] = new TrustedEntityId(TrustedEntityType.CIS, "fooCis" + i);
		final TrustedEntityId[] svcIds = new TrustedEntityId[4];
		for (int i = 0; i < svcIds.length; ++i)
			svcIds[i] = new TrustedEntityId(TrustedEntityType.SVC, "fooSvc" + i);
		
		final LinkedHashMap<TrustedEntityId, Double> trustorCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCssRelationships.put(cssIds[0], 0.6d);
		trustorCssRelationships.put(cssIds[1], 0.7d);
		trustorCssRelationships.put(cssIds[2], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trustorCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCisRelationships.put(cisIds[0], 0.6d);
		trustorCisRelationships.put(cisIds[1], 0.7d);
		trustorCisRelationships.put(cisIds[2], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trustorSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorSvcRelationships.put(svcIds[0], 0.6d);
		trustorSvcRelationships.put(svcIds[1], 0.7d);
		trustorSvcRelationships.put(svcIds[2], 0.6d);
		
		final LinkedHashMap<TrustedEntityId, Double> trusteeCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCssRelationships.put(cssIds[0], 0.6d);
		trusteeCssRelationships.put(cssIds[1], 0.7d);
		trusteeCssRelationships.put(cssIds[3], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCisRelationships.put(cisIds[0], 0.6d);
		trusteeCisRelationships.put(cisIds[1], 0.7d);
		trusteeCisRelationships.put(cisIds[3], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeSvcRelationships.put(svcIds[0], 0.6d);
		trusteeSvcRelationships.put(svcIds[1], 0.7d);
		trusteeSvcRelationships.put(svcIds[3], 0.6d);
		
		final Set<ITrustedEntity> trustedEntities = 
				new HashSet<ITrustedEntity>(trustorCssRelationships.size());
		for (final TrustedEntityId teid : trustorCssRelationships.keySet()) {
			final ITrustedCss mockCss = mock(ITrustedCss.class);
			when(mockCss.getTrustorId()).thenReturn(trustorId);
			when(mockCss.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCss.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCssRelationships.get(teid));
			trustedEntities.add(mockCss);
		}	
		for (final TrustedEntityId teid : trustorCisRelationships.keySet()) {
			final ITrustedCis mockCis = mock(ITrustedCis.class);
			when(mockCis.getTrustorId()).thenReturn(trustorId);
			when(mockCis.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCis.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCisRelationships.get(teid));
			trustedEntities.add(mockCis);
		}
		for (final TrustedEntityId teid : trustorSvcRelationships.keySet()) {
			final ITrustedService mockSvc = mock(ITrustedService.class);
			when(mockSvc.getTrustorId()).thenReturn(trustorId);
			when(mockSvc.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockSvc.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorSvcRelationships.get(teid));
			trustedEntities.add(mockSvc);
		}
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))
				.thenReturn(trustedEntities);
		
		final SortedSet<ITrustEvidence> trusteeEvidence = new TreeSet<ITrustEvidence>();
		for (final TrustedEntityId teid : trusteeCssRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCssRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeCisRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCisRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeSvcRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeSvcRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		when(this.mockTrustEvidenceRepository.retrieveLatestEvidence(
				trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null)).thenReturn(trusteeEvidence);
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		assertEquals(1.0d, similarity, DELTA);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityMin() throws Exception {
		
		// Setup test data
		final TrustedEntityId[] cssIds = new TrustedEntityId[4];
		for (int i = 0; i < cssIds.length; ++i)
			cssIds[i] = new TrustedEntityId(TrustedEntityType.CSS, "fooCss" + i);
		final TrustedEntityId[] cisIds = new TrustedEntityId[4];
		for (int i = 0; i < cisIds.length; ++i)
			cisIds[i] = new TrustedEntityId(TrustedEntityType.CIS, "fooCis" + i);
		final TrustedEntityId[] svcIds = new TrustedEntityId[4];
		for (int i = 0; i < svcIds.length; ++i)
			svcIds[i] = new TrustedEntityId(TrustedEntityType.SVC, "fooSvc" + i);
		
		final LinkedHashMap<TrustedEntityId, Double> trustorCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCssRelationships.put(cssIds[0], 1.0d);
		trustorCssRelationships.put(cssIds[1], 1.0d);
		trustorCssRelationships.put(cssIds[2], 1.0d);
		final LinkedHashMap<TrustedEntityId, Double> trustorCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCisRelationships.put(cisIds[0], 1.0d);
		trustorCisRelationships.put(cisIds[1], 1.0d);
		trustorCisRelationships.put(cisIds[2], 1.0d);
		final LinkedHashMap<TrustedEntityId, Double> trustorSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorSvcRelationships.put(svcIds[0], 1.0d);
		trustorSvcRelationships.put(svcIds[1], 1.0d);
		trustorSvcRelationships.put(svcIds[2], 1.0d);
		
		final LinkedHashMap<TrustedEntityId, Double> trusteeCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCssRelationships.put(cssIds[0], 0.0d);
		trusteeCssRelationships.put(cssIds[1], 0.0d);
		trusteeCssRelationships.put(cssIds[3], 0.0d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCisRelationships.put(cisIds[0], 0.0d);
		trusteeCisRelationships.put(cisIds[1], 0.0d);
		trusteeCisRelationships.put(cisIds[3], 0.0d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeSvcRelationships.put(svcIds[0], 0.0d);
		trusteeSvcRelationships.put(svcIds[1], 0.0d);
		trusteeSvcRelationships.put(svcIds[3], 0.0d);
		
		final Set<ITrustedEntity> trustedEntities = 
				new HashSet<ITrustedEntity>(trustorCssRelationships.size());
		for (final TrustedEntityId teid : trustorCssRelationships.keySet()) {
			final ITrustedCss mockCss = mock(ITrustedCss.class);
			when(mockCss.getTrustorId()).thenReturn(trustorId);
			when(mockCss.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCss.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCssRelationships.get(teid));
			trustedEntities.add(mockCss);
		}	
		for (final TrustedEntityId teid : trustorCisRelationships.keySet()) {
			final ITrustedCis mockCis = mock(ITrustedCis.class);
			when(mockCis.getTrustorId()).thenReturn(trustorId);
			when(mockCis.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCis.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCisRelationships.get(teid));
			trustedEntities.add(mockCis);
		}
		for (final TrustedEntityId teid : trustorSvcRelationships.keySet()) {
			final ITrustedService mockSvc = mock(ITrustedService.class);
			when(mockSvc.getTrustorId()).thenReturn(trustorId);
			when(mockSvc.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockSvc.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorSvcRelationships.get(teid));
			trustedEntities.add(mockSvc);
		}
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))
				.thenReturn(trustedEntities);
		
		final SortedSet<ITrustEvidence> trusteeEvidence = new TreeSet<ITrustEvidence>();
		for (final TrustedEntityId teid : trusteeCssRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCssRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeCisRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCisRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeSvcRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeSvcRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		when(this.mockTrustEvidenceRepository.retrieveLatestEvidence(
				trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null)).thenReturn(trusteeEvidence);
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		assertEquals(-1.0d, similarity, DELTA);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityZeroTrustor() throws Exception {
		
		// Setup test data
		final TrustedEntityId[] cssIds = new TrustedEntityId[4];
		for (int i = 0; i < cssIds.length; ++i)
			cssIds[i] = new TrustedEntityId(TrustedEntityType.CSS, "fooCss" + i);
		final TrustedEntityId[] cisIds = new TrustedEntityId[4];
		for (int i = 0; i < cisIds.length; ++i)
			cisIds[i] = new TrustedEntityId(TrustedEntityType.CIS, "fooCis" + i);
		final TrustedEntityId[] svcIds = new TrustedEntityId[4];
		for (int i = 0; i < svcIds.length; ++i)
			svcIds[i] = new TrustedEntityId(TrustedEntityType.SVC, "fooSvc" + i);
		
		final LinkedHashMap<TrustedEntityId, Double> trustorCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCssRelationships.put(cssIds[0], 0.5d);
		trustorCssRelationships.put(cssIds[1], 0.5d);
		trustorCssRelationships.put(cssIds[2], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trustorCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCisRelationships.put(cisIds[0], 0.5d);
		trustorCisRelationships.put(cisIds[1], 0.5d);
		trustorCisRelationships.put(cisIds[2], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trustorSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorSvcRelationships.put(svcIds[0], 0.5d);
		trustorSvcRelationships.put(svcIds[1], 0.5d);
		trustorSvcRelationships.put(svcIds[2], 0.5d);
		
		final LinkedHashMap<TrustedEntityId, Double> trusteeCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCssRelationships.put(cssIds[0], 0.5d);
		trusteeCssRelationships.put(cssIds[1], 0.5d);
		trusteeCssRelationships.put(cssIds[3], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCisRelationships.put(cisIds[0], 0.5d);
		trusteeCisRelationships.put(cisIds[1], 0.5d);
		trusteeCisRelationships.put(cisIds[3], 0.6d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeSvcRelationships.put(svcIds[0], 0.5d);
		trusteeSvcRelationships.put(svcIds[1], 0.51d);
		trusteeSvcRelationships.put(svcIds[3], 0.6d);
		
		final Set<ITrustedEntity> trustedEntities = 
				new HashSet<ITrustedEntity>(trustorCssRelationships.size());
		for (final TrustedEntityId teid : trustorCssRelationships.keySet()) {
			final ITrustedCss mockCss = mock(ITrustedCss.class);
			when(mockCss.getTrustorId()).thenReturn(trustorId);
			when(mockCss.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCss.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCssRelationships.get(teid));
			trustedEntities.add(mockCss);
		}	
		for (final TrustedEntityId teid : trustorCisRelationships.keySet()) {
			final ITrustedCis mockCis = mock(ITrustedCis.class);
			when(mockCis.getTrustorId()).thenReturn(trustorId);
			when(mockCis.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCis.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCisRelationships.get(teid));
			trustedEntities.add(mockCis);
		}
		for (final TrustedEntityId teid : trustorSvcRelationships.keySet()) {
			final ITrustedService mockSvc = mock(ITrustedService.class);
			when(mockSvc.getTrustorId()).thenReturn(trustorId);
			when(mockSvc.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockSvc.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorSvcRelationships.get(teid));
			trustedEntities.add(mockSvc);
		}
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))
				.thenReturn(trustedEntities);
		
		final SortedSet<ITrustEvidence> trusteeEvidence = new TreeSet<ITrustEvidence>();
		for (final TrustedEntityId teid : trusteeCssRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCssRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeCisRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCisRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeSvcRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeSvcRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		when(this.mockTrustEvidenceRepository.retrieveLatestEvidence(
				trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null)).thenReturn(trusteeEvidence);
		
		@SuppressWarnings("unused")
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		//System.out.println(similarity); TODO
		//assertTrue(similarity > 0.9d);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityEmptyTrustor() throws Exception {
		
		// Setup test data
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))	
				.thenReturn(new HashSet<ITrustedEntity>());
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		assertEquals(0.0d, similarity, DELTA);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityEmptyTrustee() throws Exception {
		
		// Setup test data
		final TrustedEntityId[] cssIds = new TrustedEntityId[4];
		for (int i = 0; i < cssIds.length; ++i)
			cssIds[i] = new TrustedEntityId(TrustedEntityType.CSS, "fooCss" + i);
		final TrustedEntityId[] cisIds = new TrustedEntityId[4];
		for (int i = 0; i < cisIds.length; ++i)
			cisIds[i] = new TrustedEntityId(TrustedEntityType.CIS, "fooCis" + i);
		final TrustedEntityId[] svcIds = new TrustedEntityId[4];
		for (int i = 0; i < svcIds.length; ++i)
			svcIds[i] = new TrustedEntityId(TrustedEntityType.SVC, "fooSvc" + i);
		
		final LinkedHashMap<TrustedEntityId, Double> trustorCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCssRelationships.put(cssIds[0], 0.5d);
		trustorCssRelationships.put(cssIds[1], 0.6d);
		trustorCssRelationships.put(cssIds[2], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trustorCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCisRelationships.put(cisIds[0], 0.5d);
		trustorCisRelationships.put(cisIds[1], 0.6d);
		trustorCisRelationships.put(cisIds[2], 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trustorSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorSvcRelationships.put(svcIds[0], 0.5d);
		trustorSvcRelationships.put(svcIds[1], 0.6d);
		trustorSvcRelationships.put(svcIds[2], 0.5d);
		
		final Set<ITrustedEntity> trustedEntities = 
				new HashSet<ITrustedEntity>(trustorCssRelationships.size());
		for (final TrustedEntityId teid : trustorCssRelationships.keySet()) {
			final ITrustedCss mockCss = mock(ITrustedCss.class);
			when(mockCss.getTrustorId()).thenReturn(trustorId);
			when(mockCss.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCss.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCssRelationships.get(teid));
			trustedEntities.add(mockCss);
		}
		for (final TrustedEntityId teid : trustorCisRelationships.keySet()) {
			final ITrustedCis mockCis = mock(ITrustedCis.class);
			when(mockCis.getTrustorId()).thenReturn(trustorId);
			when(mockCis.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCis.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCisRelationships.get(teid));
			trustedEntities.add(mockCis);
		}
		for (final TrustedEntityId teid : trustorSvcRelationships.keySet()) {
			final ITrustedService mockSvc = mock(ITrustedService.class);
			when(mockSvc.getTrustorId()).thenReturn(trustorId);
			when(mockSvc.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockSvc.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorSvcRelationships.get(teid));
			trustedEntities.add(mockSvc);
		}
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))
				.thenReturn(trustedEntities);
		
		when(this.mockTrustEvidenceRepository.retrieveLatestEvidence(
				trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null))
				.thenReturn(new TreeSet<ITrustEvidence>());
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		assertEquals(0.0d, similarity, DELTA);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityNothingInCommon() throws Exception {
		
		// Setup test data		
		final LinkedHashMap<TrustedEntityId, Double> trustorCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trustorCssRelationships.put(trusteeId, 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trustorCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		final LinkedHashMap<TrustedEntityId, Double> trustorSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		
		final LinkedHashMap<TrustedEntityId, Double> trusteeCssRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		trusteeCssRelationships.put(trustorId, 0.5d);
		final LinkedHashMap<TrustedEntityId, Double> trusteeCisRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		final LinkedHashMap<TrustedEntityId, Double> trusteeSvcRelationships =
				new LinkedHashMap<TrustedEntityId, Double>();
		
		final Set<ITrustedEntity> trustedEntities = 
				new HashSet<ITrustedEntity>(trustorCssRelationships.size());
		for (final TrustedEntityId teid : trustorCssRelationships.keySet()) {
			final ITrustedCss mockCss = mock(ITrustedCss.class);
			when(mockCss.getTrustorId()).thenReturn(trustorId);
			when(mockCss.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCss.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCssRelationships.get(teid));
			trustedEntities.add(mockCss);
		}
		for (final TrustedEntityId teid : trustorCisRelationships.keySet()) {
			final ITrustedCis mockCis = mock(ITrustedCis.class);
			when(mockCis.getTrustorId()).thenReturn(trustorId);
			when(mockCis.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockCis.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorCisRelationships.get(teid));
			trustedEntities.add(mockCis);
		}
		for (final TrustedEntityId teid : trustorSvcRelationships.keySet()) {
			final ITrustedService mockSvc = mock(ITrustedService.class);
			when(mockSvc.getTrustorId()).thenReturn(trustorId);
			when(mockSvc.getTrusteeId()).thenReturn(teid);
			final IDirectTrust mockDirectTrust = mock(IDirectTrust.class);
			when(mockSvc.getDirectTrust()).thenReturn(mockDirectTrust);
			when(mockDirectTrust.getValue()).thenReturn(trustorSvcRelationships.get(teid));
			trustedEntities.add(mockSvc);
		}
		when(this.mockTrustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT))
				.thenReturn(trustedEntities);
		
		final SortedSet<ITrustEvidence> trusteeEvidence = new TreeSet<ITrustEvidence>();
		for (final TrustedEntityId teid : trusteeCssRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCssRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeCisRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeCisRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		for (final TrustedEntityId teid : trusteeSvcRelationships.keySet()) {
			final ITrustEvidence mockEvidence = mock(ITrustEvidence.class);
			when(mockEvidence.getSubjectId()).thenReturn(trusteeId);
			when(mockEvidence.getObjectId()).thenReturn(teid);
			when(mockEvidence.getInfo()).thenReturn(trusteeSvcRelationships.get(teid));
			trusteeEvidence.add(mockEvidence);
		}
		when(this.mockTrustEvidenceRepository.retrieveLatestEvidence(
				trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null)).thenReturn(trusteeEvidence);
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trusteeId);
		assertEquals(0.0d, similarity, DELTA);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityToSelf() throws Exception {
		
		final Double similarity = this.trustSimilarityEvaluator
				.evaluateCosineSimilarity(trustorId, trustorId);
		assertEquals(1.0d, similarity, DELTA);
	}
	
	/**
	 * Test method for {@link ITrustSimilarityEvaluator#evaluateCosineSimilarity(TrustedEntityId, TrustedEntityId)}.
	 * @throws Exception  
	 */
	@Test
	public void testEvaluateCosineSimilarityIllegalArgs() throws Exception {
		
		final TrustedEntityId cssTeid = new TrustedEntityId(TrustedEntityType.CSS, "fooCss");
		final TrustedEntityId cisTeid = new TrustedEntityId(TrustedEntityType.CIS, "fooCis");
		
		// Invalid trustorId
		boolean caughtException = false;
		try { 
			this.trustSimilarityEvaluator.evaluateCosineSimilarity(cisTeid, cssTeid);
		} catch (IllegalArgumentException iae) {
			caughtException = true;
		}
		assertTrue(caughtException);
		
		// Invalid trusteeId
		caughtException = false;
		try { 
			this.trustSimilarityEvaluator.evaluateCosineSimilarity(cssTeid, cisTeid);
		} catch (IllegalArgumentException iae) {
			caughtException = true;
		}
		assertTrue(caughtException);
		
		// Invalid trustorId and trusteeId
		caughtException = false;
		try { 
			this.trustSimilarityEvaluator.evaluateCosineSimilarity(cisTeid, cisTeid);
		} catch (IllegalArgumentException iae) {
			caughtException = true;
		}
		assertTrue(caughtException);
		
		// Null trustorId
		caughtException = false;
		try { 
			this.trustSimilarityEvaluator.evaluateCosineSimilarity(null, cssTeid);
		} catch (NullPointerException npe) {
			caughtException = true;
		}
		assertTrue(caughtException);
		
		// Null trustorId
		caughtException = false;
		try { 
			this.trustSimilarityEvaluator.evaluateCosineSimilarity(cssTeid, null);
		} catch (NullPointerException npe) {
			caughtException = true;
		}
		assertTrue(caughtException);
		
		// Null trustorId and trusteeId
		caughtException = false;
		try { 
			this.trustSimilarityEvaluator.evaluateCosineSimilarity(null, null);
		} catch (NullPointerException npe) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}
}