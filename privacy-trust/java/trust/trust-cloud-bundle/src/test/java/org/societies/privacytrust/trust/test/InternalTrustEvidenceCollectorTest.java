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

import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/InternalTrustEvidenceCollectorTest-context.xml"})
public class InternalTrustEvidenceCollectorTest {
	
	private static final String BASE_ID = "tect";
	
	private static final String SUBJECT_CSS_ID = BASE_ID + "SubjectCssIIdentity";
	
	private static final String OBJECT_CSS_ID = BASE_ID + "ObjectCssIIdentity";
	
	private static final String OBJECT_CIS_ID = BASE_ID + "ObjectCisIIdentity";
	
	private static final String OBJECT_SERVICE_ID = BASE_ID + "ObjectServiceResourceIdentifier";
	
	@Autowired
	@InjectMocks
	private ITrustEvidenceCollector internalTrustEvidenceCollector;
	
	@Autowired
	private ITrustEvidenceRepository trustEvidenceRepo;
	
	@Mock
	private ITrustNodeMgr mockTrustNodeMgr;

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
		
		MockitoAnnotations.initMocks(this);
		when(mockTrustNodeMgr.isMaster()).thenReturn(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(TrustedEntityId, TrustedEntityId, TrustEvidenceType, Date, java.io.Serializable)}.
	 * @throws TrustException 
	 */
	@Test
	public void testAddDirectCssTrustRating() throws TrustException {
		
		final TrustedEntityId subjectCssTeid = new TrustedEntityId(TrustedEntityType.CSS, SUBJECT_CSS_ID);
		final TrustedEntityId objectCssTeid = new TrustedEntityId(TrustedEntityType.CSS, OBJECT_CSS_ID);
		final Date now = new Date(1000 * (new Date().getTime() / 1000));
		this.internalTrustEvidenceCollector.addDirectEvidence(subjectCssTeid, 
				objectCssTeid, TrustEvidenceType.RATED, now, new Double(0.5d));
		// verify
		final Set<ITrustEvidence> evidenceSet = 
				this.trustEvidenceRepo.retrieveEvidence(subjectCssTeid, 
						objectCssTeid, TrustEvidenceType.RATED, now, null, null);
		assertFalse(evidenceSet.isEmpty());
		ITrustEvidence directEvidence = evidenceSet.iterator().next();
		assertEquals(TrustEvidenceType.RATED, directEvidence.getType());
		assertEquals(new Double(0.5d), (Double) directEvidence.getInfo());
		assertTrue(directEvidence.getTimestamp().compareTo(now) == 0);
	}
	
	/**
	 * TODO
	 * Test method for {@link org.societies.privacytrust.trust.impl.evidence.InternalTrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity, double, java.util.Date)}.
	 * @throws TrustException 
	 */
	@Ignore
	@Test
	public void testAddTrustRatingWithIllegalParams() throws TrustException {
		
		// non-CSS trustor
		try {
			//this.trustEvidenceCollector.addTrustRating(mockTrustedCisIdentity, mockTrustedCssIdentity, 0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("trustor") != -1);
		}
		
		// non-CSS/CIS trustee
		try {
			//this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, mockInvalidTrusteeIdentity, 0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("trustee") != -1);
		}
		 
		// rating out of range
		try {
			//this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, mockTrustedCssIdentity, -0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("rating") != -1);
		}
	}
	
	/**
	 * Test method for {@link org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(TrustedEntityId, TrustedEntityId, TrustEvidenceType, Date, java.io.Serializable)}.
	 * @throws TrustException 
	 */
	@Test
	public void testAddDirectCisTrustRating() throws TrustException {
		
		final TrustedEntityId subjectCssTeid = new TrustedEntityId(TrustedEntityType.CSS, SUBJECT_CSS_ID);
		final TrustedEntityId objectCisTeid = new TrustedEntityId(TrustedEntityType.CIS, OBJECT_CIS_ID);
		final Date now = new Date(1000 * (new Date().getTime() / 1000));
		this.internalTrustEvidenceCollector.addDirectEvidence(subjectCssTeid, 
				objectCisTeid, TrustEvidenceType.RATED, now, new Double(0.5d));
		// verify
		final Set<ITrustEvidence> evidenceSet = 
				this.trustEvidenceRepo.retrieveEvidence(subjectCssTeid, 
						objectCisTeid, TrustEvidenceType.RATED, now, null, null);
		assertFalse(evidenceSet.isEmpty());
		ITrustEvidence directEvidence = evidenceSet.iterator().next();
		assertEquals(TrustEvidenceType.RATED, directEvidence.getType());
		assertEquals(new Double(0.5d), (Double) directEvidence.getInfo());
		assertTrue(directEvidence.getTimestamp().compareTo(now) == 0);
	}
	
	/**
	 * Test method for {@link org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(TrustedEntityId, TrustedEntityId, TrustEvidenceType, Date, java.io.Serializable)}.
	 * @throws TrustException 
	 */
	@Test
	public void testAddDirectServiceTrustRating() throws TrustException {
		
		final TrustedEntityId subjectCssTeid = new TrustedEntityId(TrustedEntityType.CSS, SUBJECT_CSS_ID);
		final TrustedEntityId objectServiceTeid = new TrustedEntityId(TrustedEntityType.SVC, OBJECT_SERVICE_ID);
		final Date now = new Date(1000 * (new Date().getTime() / 1000));
		this.internalTrustEvidenceCollector.addDirectEvidence(subjectCssTeid, 
				objectServiceTeid, TrustEvidenceType.RATED, now, new Double(0.5d));
		// verify
		final Set<ITrustEvidence> evidenceSet = 
				this.trustEvidenceRepo.retrieveEvidence(subjectCssTeid, 
						objectServiceTeid, TrustEvidenceType.RATED, now, null, null);
		assertFalse(evidenceSet.isEmpty());
		ITrustEvidence directEvidence = evidenceSet.iterator().next();
		assertEquals(TrustEvidenceType.RATED, directEvidence.getType());
		assertEquals(new Double(0.5d), (Double) directEvidence.getInfo());
		assertTrue(directEvidence.getTimestamp().compareTo(now) == 0);
	}
	
	/**
	 * TODO
	 * Test method for {@link org.societies.privacytrust.trust.impl.evidence.InternalTrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, double, java.util.Date)}.
	 * @throws TrustException 
	 */
	@Ignore
	@Test
	public void testAddServiceTrustRatingWithIllegalParams() throws TrustException {
		
		// non-CSS trustor
		try {
			//this.trustEvidenceCollector.addTrustRating(mockTrustedCisIdentity, mockServiceResourceIdentifier, 0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("trustor") != -1);
		}
		 
		// rating out of range
		try {
			//this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, mockServiceResourceIdentifier, 1.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("rating") != -1);
		}
	}
}