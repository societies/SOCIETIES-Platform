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
import java.util.Random;
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
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.engine.IIndirectTrustEngine;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
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
 * @since 1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/IndirectTrustEngineTest-context.xml"})
public class IndirectTrustEngineTest {
	
	private static double DELTA = 0.000001d;
	
	private static final String BASE_ID = "itet";
	
	private static final String TRUSTOR_CSS_ID = BASE_ID + "TrustorCssIIdentity";
	
	private static final int TRUSTEE_CSS_LIST_SIZE = 100;
	private static final String TRUSTEE_CSS_ID_BASE = BASE_ID + "TrusteeCssIIdentity";
	
	private static final int TRUSTEE_CIS_LIST_SIZE = 10;
	private static final String TRUSTEE_CIS_ID_BASE = BASE_ID + "TrusteeCisIIdentity";
	
	private static final int TRUSTEE_SERVICE_LIST_SIZE = 200;
	private static final String TRUSTEE_SERVICE_ID_BASE = BASE_ID + "TrusteeServiceResourceIdentifier";
	
	private static final int TRUSTEE_SERVICE_TYPE_LIST_SIZE = 10;
	private static final String TRUSTEE_SERVICE_TYPE_BASE = BASE_ID + "TrusteeServiceType";
	
	private static TrustedEntityId myCssTeid;
	
	private static List<TrustedEntityId> trusteeCssTeidList;
	
	private static List<TrustedEntityId> trusteeCisTeidList;
	
	private static List<TrustedEntityId> trusteeServiceTeidList;
	
	private static List<String> trusteeServiceTypeList;
	
	/** The IIndirectTrustEngine service reference. */
	@Autowired
	@InjectMocks
	private IIndirectTrustEngine engine;
	
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
		
		// clean trust database
		this.trustRepo.removeEntities(null, null, null);
		// clean trust evidence database
		this.trustEvidenceRepo.removeEvidence(null, null, null, null, null, null);
	}

	/*
	 * Test method for {@link org.societies.privacytrust.trust.api.engine.IIndirectTrustEngine#evaluate(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.privacytrust.trust.api.evidence.model.IIndirectTrustEvidence)}.
	 * @throws Exception 
	 */
	@Test
	public void testEvaluate() throws Exception {
		
		// setup test data
		
		// add direct trust myCSS -> CSS
		final TrustedEntityId trusteeCssTeid = trusteeCssTeidList.get(0);
		ITrustedCss trusteeCss = (ITrustedCss) 
				this.trustRepo.createEntity(myCssTeid, trusteeCssTeid);
		trusteeCss.getDirectTrust().setValue(0.5);
		trusteeCss = (ITrustedCss) this.trustRepo.updateEntity(trusteeCss);
		
		// add trust opinion CSS -> SVC
		final TrustedEntityId trusteeSvcTeid = trusteeServiceTeidList.get(0);
		ITrustEvidence evidence = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid, trusteeSvcTeid, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(0.75), trusteeCssTeid);
		
		// verify indirect trust myCSS -> SVC
		this.engine.evaluate(myCssTeid, evidence);
		ITrustedService trusteeSvc = (ITrustedService) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeSvcTeid);
		assertNotNull(trusteeSvc);
		// verify evidence association
		assertNotNull(trusteeSvc.getEvidence());
		assertFalse(trusteeSvc.getEvidence().isEmpty());
		assertTrue(trusteeSvc.getEvidence().contains(evidence));
		// verify value
		assertNull(trusteeSvc.getDirectTrust().getValue());
		assertNotNull(trusteeSvc.getIndirectTrust().getValue());
		// indirect trust = mean of direct trust values of myCSS
		assertEquals(this.trustRepo.retrieveMeanTrustValue(myCssTeid, TrustValueType.DIRECT, null),
				trusteeSvc.getIndirectTrust().getValue(), DELTA);
		assertEquals(0.25, trusteeSvc.getIndirectTrust().getConfidence(), DELTA);
		// verify similarity with CSS
		Double similarity = ((ITrustedCss) this.trustRepo.retrieveEntity(
				myCssTeid, trusteeCssTeid)).getSimilarity(); 
		assertEquals(0.0d, similarity, DELTA);
		
		// add direct trust myCSS -> SVC (same as CSS -> SVC)
		trusteeSvc.getDirectTrust().setValue((Double) evidence.getInfo());
		trusteeSvc = (ITrustedService) this.trustRepo.updateEntity(trusteeSvc);
		
		// add direct trust myCSS -> CSS2 (same as CSS -> CSS)
		final TrustedEntityId trusteeCssTeid2 = trusteeCssTeidList.get(1);
		ITrustedCss trusteeCss2 = (ITrustedCss) 
				this.trustRepo.createEntity(myCssTeid, trusteeCssTeid2);
		trusteeCss2.getDirectTrust().setValue(trusteeCss.getDirectTrust().getValue());
		trusteeCss2 = (ITrustedCss) this.trustRepo.updateEntity(trusteeCss2);
		
		// add trust opinion CSS2 -> SVC2 with same value as CSS -> SVC 
		final TrustedEntityId trusteeSvcTeid2 = trusteeServiceTeidList.get(1);
		ITrustEvidence evidence2 = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid2, trusteeSvcTeid2, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), evidence.getInfo(), trusteeCssTeid2);
		
		// verify indirect trust myCSS -> SVC2
		this.engine.evaluate(myCssTeid, evidence2);
		ITrustedService trusteeSvc2 = (ITrustedService) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeSvcTeid2);
		assertNotNull(trusteeSvc2);
		// verify evidence association
		assertNotNull(trusteeSvc2.getEvidence());
		assertFalse(trusteeSvc2.getEvidence().isEmpty());
		assertTrue(trusteeSvc2.getEvidence().contains(evidence2));
		// verify value
		assertNull(trusteeSvc2.getDirectTrust().getValue());
		assertNotNull(trusteeSvc2.getIndirectTrust().getValue());
		// indirect trust = mean of direct trust values of myCSS
		assertEquals(this.trustRepo.retrieveMeanTrustValue(myCssTeid, TrustValueType.DIRECT, null),
				trusteeSvc2.getIndirectTrust().getValue(), DELTA);
		assertEquals(0.25, trusteeSvc2.getIndirectTrust().getConfidence(), DELTA);
		// verify similarity with CSS2
		Double similarity2 = ((ITrustedCss) this.trustRepo.retrieveEntity(
				myCssTeid, trusteeCssTeid2)).getSimilarity(); 
		assertEquals(0.0d, similarity2, DELTA);
		
		// add trust opinion CSS2 -> SVC2 with same value as CSS -> SVC 
		ITrustEvidence evidence3 = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid, trusteeSvcTeid2, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), evidence.getInfo(), trusteeCssTeid);

		// verify indirect trust myCSS -> SVC2
		this.engine.evaluate(myCssTeid, evidence3);
		trusteeSvc2 = (ITrustedService) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeSvcTeid2);
		assertNotNull(trusteeSvc2);
		// verify evidence association
		assertNotNull(trusteeSvc2.getEvidence());
		assertFalse(trusteeSvc2.getEvidence().isEmpty());
		assertTrue(trusteeSvc2.getEvidence().contains(evidence2));
		assertTrue(trusteeSvc2.getEvidence().contains(evidence3));
		// verify value
		assertNull(trusteeSvc2.getDirectTrust().getValue());
		assertNotNull(trusteeSvc2.getIndirectTrust().getValue());
		// indirect trust = mean of direct trust values of myCSS + (trust opinion - mean = 0) 
		assertEquals(this.trustRepo.retrieveMeanTrustValue(myCssTeid, TrustValueType.DIRECT, null),
				trusteeSvc2.getIndirectTrust().getValue(), DELTA);
		assertEquals(0.5, trusteeSvc2.getIndirectTrust().getConfidence(), DELTA);
		// verify similarity with CSS2 - nothing in common
		similarity = ((ITrustedCss) this.trustRepo.retrieveEntity(
				myCssTeid, trusteeCssTeid2)).getSimilarity();
		assertEquals(0.0d, similarity2, DELTA);
		
		// Add similar trust preferences between myCSS - CSS
		// Add dissimilar trust preferences between myCSS - CSS2
		for (int i = 100; i < 150; ++i) {
			
			final TrustedEntityId trusteeSvcTeidX = trusteeServiceTeidList.get(i);
			final Double commonValue = new Double(0.8);
			final Double differentValue = new Random().nextDouble();
			
			// add direct trust myCSS -> SVCX (same as CSS -> SVC)
			ITrustedService trusteeSvcX = (ITrustedService) 
					this.trustRepo.createEntity(myCssTeid, trusteeSvcTeidX);
			trusteeSvcX.getDirectTrust().setValue(commonValue);
			trusteeSvcX = (ITrustedService) this.trustRepo.updateEntity(trusteeSvcX);
			
			// add similar trust opinion CSS -> SVCX
			final ITrustEvidence evidenceCss = this.trustEvidenceRepo.addEvidence(
					trusteeCssTeid, trusteeSvcTeidX, TrustEvidenceType.DIRECTLY_TRUSTED,
					new Date(), commonValue, trusteeCssTeid);
			
			// normally evidenceX would cause an event 
			// but for testing we invoke evaluate on-demand
			this.engine.evaluate(myCssTeid, evidenceCss);
			
			// add dissimilar trust opinion CSS2 -> SVCX
			final ITrustEvidence evidenceCss2 = this.trustEvidenceRepo.addEvidence(
					trusteeCssTeid2, trusteeSvcTeidX, TrustEvidenceType.DIRECTLY_TRUSTED,
					new Date(), differentValue, trusteeCssTeid2);

			// normally evidenceX would cause an event 
			// but for testing we invoke evaluate on-demand
			this.engine.evaluate(myCssTeid, evidenceCss2);
		}
		// verify similarity with CSS is max
		Double similarityCss = ((ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid)).getSimilarity();
		assertEquals(1.0, similarityCss, DELTA);
		Double similarityCss2 = ((ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2)).getSimilarity();
		// verify similarity with CSS is higher than that with CSS2
		assertTrue(similarityCss.compareTo(similarityCss2) > 0);
		
		// add trust opinion CSS -> SVC3
		final TrustedEntityId trusteeSvcTeid3 = trusteeServiceTeidList.get(2);
		evidence3 = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid, trusteeSvcTeid3, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(1.0), trusteeCssTeid);
		this.engine.evaluate(myCssTeid, evidence3);
		
		// verify indirect trust with SVC3
		ITrustedService trusteeSvc3 = (ITrustedService) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeSvcTeid3);
		assertNotNull(trusteeSvc3);
		assertNull(trusteeSvc3.getDirectTrust().getValue());
		assertNotNull(trusteeSvc3.getIndirectTrust().getValue());
		// indirect trust = mean of direct trust values of myCSS + (trust opinion - mean > 0)
		assertTrue(trusteeSvc3.getIndirectTrust().getValue().compareTo(
				this.trustRepo.retrieveMeanTrustValue(myCssTeid, TrustValueType.DIRECT, null)) > 0);
		assertEquals(0.5, trusteeSvc3.getIndirectTrust().getConfidence(), DELTA);
		// verify similarity with CSS is max
		similarityCss = ((ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid)).getSimilarity();
		assertEquals(1.0, similarityCss, DELTA);
		
		// add trust opinion CSS2 -> SVC4
		final TrustedEntityId trusteeSvcTeid4 = trusteeServiceTeidList.get(3);
		final ITrustEvidence evidence4 = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid2, trusteeSvcTeid4, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(1.0), trusteeCssTeid2);
		this.engine.evaluate(myCssTeid, evidence4);

		// verify indirect trust with SVC4
		ITrustedService trusteeSvc4 = (ITrustedService) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeSvcTeid4);
		assertNotNull(trusteeSvc4);
		assertNull(trusteeSvc4.getDirectTrust().getValue());
		assertNotNull(trusteeSvc4.getIndirectTrust().getValue());
		// verify indirect trust with SVC4 < SVC3
		/*assertTrue(trusteeSvc4.getIndirectTrust().getValue().compareTo(
				trusteeSvc3.getIndirectTrust().getValue()) < 0);
		assertEquals(0.5, trusteeSvc4.getIndirectTrust().getConfidence(), DELTA);
		// verify similarity with CSS2 is lower than that with CSS
		similarityCss2 = ((ITrustedCss) this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2)).getSimilarity();
		assertTrue(similarityCss.compareTo(similarityCss2) > 0);*/
	}
	
	@Test
	public void testRelevantEvidence() throws Exception {
		
		// add trust opinion CSS -> CSS2
		final TrustedEntityId trusteeCssTeid = trusteeCssTeidList.get(0);
		final TrustedEntityId trusteeCssTeid2 = trusteeCssTeidList.get(1);
		ITrustEvidence evidence = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid, trusteeCssTeid2, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(0.75), trusteeCssTeid);
		Set<ITrustedEntity> entities = this.engine.evaluate(myCssTeid, evidence);
		// verify evidence is relevant
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertEquals(1, entities.size());
		assertNotNull(entities.iterator().next().getIndirectTrust().getValue());
		ITrustedCss trusteeCss = (ITrustedCss) 
				this.trustRepo.retrieveEntity(myCssTeid, trusteeCssTeid2);
		assertNotNull(trusteeCss);
		
		// add trust opinion CSS -> CSS
		evidence = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid, trusteeCssTeid, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(0.75), trusteeCssTeid);
		entities = this.engine.evaluate(myCssTeid, evidence);
		// verify evidence is ignored
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add trust opinion myCSS -> myCSS
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, myCssTeid, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(0.75), trusteeCssTeid);
		entities = this.engine.evaluate(myCssTeid, evidence);
		// verify evidence is ignored
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add trust opinion myCSS -> CSS
		evidence = this.trustEvidenceRepo.addEvidence(
				myCssTeid, trusteeCssTeid, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(0.75), myCssTeid);
		entities = this.engine.evaluate(myCssTeid, evidence);
		// verify evidence is ignored
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add trust opinion CSS -> myCSS
		evidence = this.trustEvidenceRepo.addEvidence(
				trusteeCssTeid, myCssTeid, TrustEvidenceType.DIRECTLY_TRUSTED,
				new Date(), new Double(0.75), trusteeCssTeid);
		entities = this.engine.evaluate(myCssTeid, evidence);
		// verify evidence is ignored
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
	}
}