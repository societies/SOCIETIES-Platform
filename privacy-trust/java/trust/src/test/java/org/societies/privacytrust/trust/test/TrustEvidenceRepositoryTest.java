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

import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustOpinion;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.impl.evidence.repo.model.DirectTrustOpinion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test cases for the TrustRepository
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.6
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
public class TrustEvidenceRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static final String TRUSTOR_ID = "aFooTrustorIIdentity";
	private static final String TRUSTOR_ID2 = "aBarTrustorIIdentity";
	
	private static final String TRUSTED_CSS_ID = "aFooCssIIdentity";
	
	private static final String TRUSTED_CIS_ID = "aFooCisIIdentity";
	
	private static final String TRUSTED_SERVICE_ID = "aFooServiceResourceIdentifier";
	
	private static final String TRUSTED_SERVICE_TYPE = "aFooServiceType";
	
	@Autowired
	private ITrustEvidenceRepository trustEvidenceRepo;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests the creation, retrieval, update and removal of trusted CSSs.
	 * 
	 * @throws TrustException
	 */
	@Test
	public void testDirectTrustOpinionCRUD() throws TrustException {
		
		// test params
		// TEID1
		final TrustedEntityId teid1 = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID);
		// TEID2
		final TrustedEntityId teid2 = new TrustedEntityId(TRUSTOR_ID2, TrustedEntityType.CSS, TRUSTED_CSS_ID);
		// startDate
		final Date startDate = new Date();
		// endDate
		final Date endDate = new Date(startDate.getTime() + 1000);
		// DirectTrustOpinion on teid1 at startDate
		final IDirectTrustOpinion directOpinion1 = new DirectTrustOpinion(teid1, startDate, new Double(0.5d));
		final IDirectTrustOpinion directOpinion2 = new DirectTrustOpinion(teid2, startDate, new Double(0.5d)); 
		final IDirectTrustOpinion directOpinion3 = new DirectTrustOpinion(teid1, endDate, new Double(1.0d)); 
		
		Set<IDirectTrustEvidence> directEvidence;
		
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(teid1);
		assertNotNull(directEvidence);
		assertTrue(directEvidence.isEmpty());
		
		this.trustEvidenceRepo.addEvidence(directOpinion1);
		this.trustEvidenceRepo.addEvidence(directOpinion2);
		this.trustEvidenceRepo.addEvidence(directOpinion3);
		
		// retrieve ALL evidence for teid1
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(teid1);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion1));
		assertTrue(directEvidence.contains(directOpinion3));
		
		// retrieve ALL evidence for teid2
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(teid2);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion2));
		
		// retrieve evidence for teid1 between startDate and endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid1, startDate, endDate);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion1));
		assertTrue(directEvidence.contains(directOpinion3));
		
		// retrieve evidence for teid2 between startDate and endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid2, startDate, endDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion2));
		
		// retrieve evidence for teid1 after startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid1, startDate, null);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion1));
		assertTrue(directEvidence.contains(directOpinion3));
				
		// retrieve evidence for teid2 after startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid2, startDate, null);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion2));
		
		// retrieve evidence for teid1 after endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid1, endDate, null);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion3));
						
		// retrieve evidence for teid2 after endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid2, endDate, null);
		assertNotNull(directEvidence);
		assertEquals(0, directEvidence.size());
		
		// retrieve evidence for teid1 before startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid1, null, startDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion1));
						
		// retrieve evidence for teid2 before startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid2, null, startDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion2));
				
		// retrieve evidence for teid1 before endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid1, null, endDate);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion1));
		assertTrue(directEvidence.contains(directOpinion3));
								
		// retrieve evidence for teid2 before endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(teid2, null, endDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(directOpinion2));
		
		this.trustEvidenceRepo.removeAllDirectEvidence(teid1);
	}
}