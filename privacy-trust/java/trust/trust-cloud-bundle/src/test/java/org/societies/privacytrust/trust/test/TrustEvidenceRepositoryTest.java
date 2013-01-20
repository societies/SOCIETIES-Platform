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
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.IIndirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.impl.evidence.repo.model.DirectTrustEvidence;
import org.societies.privacytrust.trust.impl.evidence.repo.model.IndirectTrustEvidence;
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
@ContextConfiguration(locations = {"classpath:META-INF/spring/TrustEvidenceRepositoryTest-context.xml"})
public class TrustEvidenceRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static final String BASE_ID = "tert";
	
	private static final String SUBJECT_CSS_ID = BASE_ID + "SubjectCssIIdentity";
	private static final String SUBJECT_CSS_ID2 = BASE_ID + "SubjectCssIIdentity2";
	
	private static final String OBJECT_CSS_ID = BASE_ID + "ObjectCssIIdentity";
	
	private static final String OBJECT_CIS_ID = BASE_ID + "ObjectCisIIdentity";
	
	private static final String OBJECT_SERVICE_ID = BASE_ID + "ObjectServiceResourceIdentifier";
	
	private static final String SOURCE_CSS_ID = BASE_ID + "SourceCssIIdentity";
	
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
	 * Tests the creation, retrieval, update and removal of Direct Trust Evidence.
	 * 
	 * @throws TrustException
	 */
	@Test
	public void testDirectTrustEvidenceCRUD() throws TrustException {
		
		// test params
		// SubjectId
		final TrustedEntityId subjectId = new TrustedEntityId(TrustedEntityType.CSS, SUBJECT_CSS_ID);
		// SubjectId2
		final TrustedEntityId subjectId2 = new TrustedEntityId(TrustedEntityType.CSS, SUBJECT_CSS_ID2);
		// ObjectId
		final TrustedEntityId objectId = new TrustedEntityId(TrustedEntityType.CSS, OBJECT_CSS_ID);
		final Date now = new Date();
		// startDate
		// Ugly hack for MySQL - remove ms precision from date
		final Date startDate = new Date(1000 * (now.getTime() / 1000));
		// endDate
		final Date endDate = new Date(startDate.getTime() + 1000);
		
		final IDirectTrustEvidence evidence1 = new DirectTrustEvidence(
				subjectId, objectId, TrustEvidenceType.RATED, startDate, new Double(0.5d));
		final IDirectTrustEvidence evidence2 = new DirectTrustEvidence(
				subjectId2, objectId, TrustEvidenceType.RATED, startDate, new Double(0.5d)); 
		final IDirectTrustEvidence evidence3 = new DirectTrustEvidence(
				subjectId, objectId, TrustEvidenceType.RATED, endDate, new Double(1.0d)); 
		
		Set<IDirectTrustEvidence> directEvidence;
		
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId, objectId);
		assertNotNull(directEvidence);
		assertTrue(directEvidence.isEmpty());
		
		this.trustEvidenceRepo.addEvidence(evidence1);
		this.trustEvidenceRepo.addEvidence(evidence2);
		this.trustEvidenceRepo.addEvidence(evidence3);
		
		// retrieve ALL evidence for subjectId,objectId
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId, objectId);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(evidence1));
		assertTrue(directEvidence.contains(evidence3));
		
		// retrieve ALL evidence for subjectId2,objectId
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId2, objectId);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
		
		// retrieve evidence for subjectId,objectId between startDate and endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId, objectId, 
				TrustEvidenceType.RATED, startDate, endDate);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(evidence1));
		assertTrue(directEvidence.contains(evidence3));
		
		// retrieve evidence for subjectId2,objectId between startDate and endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId2, objectId,
				TrustEvidenceType.RATED, startDate, endDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
		
		// retrieve evidence for subjectId,objectId after startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId, objectId,
				TrustEvidenceType.RATED, startDate, null);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(evidence1));
		assertTrue(directEvidence.contains(evidence3));
				
		// retrieve evidence for subjectId2,objectId after startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId2, objectId,
				TrustEvidenceType.RATED, startDate, null);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
		
		// retrieve evidence for subjectId,objectId after endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId, objectId,
				TrustEvidenceType.RATED, endDate, null);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence3));
						
		// retrieve evidence for subjectId2,objectId after endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId2, objectId,
				TrustEvidenceType.RATED, endDate, null);
		assertNotNull(directEvidence);
		assertEquals(0, directEvidence.size());
		
		// retrieve evidence for subjectId,objectId before startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId, objectId,
				TrustEvidenceType.RATED, null, startDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence1));
						
		// retrieve evidence for subjectId2,objectId before startDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId2, objectId,
				TrustEvidenceType.RATED, null, startDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
				
		// retrieve evidence for subjectId,objectId before endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId, objectId,
				TrustEvidenceType.RATED, null, endDate);
		assertNotNull(directEvidence);
		assertEquals(2, directEvidence.size());
		assertTrue(directEvidence.contains(evidence1));
		assertTrue(directEvidence.contains(evidence3));
								
		// retrieve evidence for subjectId2,objectId before endDate (inclusive)
		directEvidence = this.trustEvidenceRepo.retrieveDirectEvidence(subjectId2, objectId,
				TrustEvidenceType.RATED, null, endDate);
		assertNotNull(directEvidence);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
		
		// remove ALL evidence for subjectId,objectId
		this.trustEvidenceRepo.removeAllDirectEvidence(subjectId, objectId);
		// verify
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId, objectId);
		assertTrue(directEvidence.isEmpty());
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId2, objectId);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
		
		// remove ALL evidence for subjectId2,objectId
		this.trustEvidenceRepo.removeAllDirectEvidence(subjectId2, objectId);
		// verify
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId, objectId);
		assertTrue(directEvidence.isEmpty());
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId2, objectId);
		assertTrue(directEvidence.isEmpty());
		
		// re-add evidence
		this.trustEvidenceRepo.addEvidence(evidence1);
		this.trustEvidenceRepo.addEvidence(evidence2);
		this.trustEvidenceRepo.addEvidence(evidence3);
		
		// remove evidence for subjectId,objectId before startDate (inclusive)
		this.trustEvidenceRepo.removeDirectEvidence(subjectId, objectId, 
				TrustEvidenceType.RATED, null, startDate);
		// verify
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId, objectId);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence3));
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId2, objectId);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
						
		// remove evidence for subjectId,objectId after startDate (inclusive)
		this.trustEvidenceRepo.removeDirectEvidence(subjectId, objectId,
				TrustEvidenceType.RATED, startDate, null);
		// verify
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId, objectId);
		assertTrue(directEvidence.isEmpty());
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId2, objectId);
		assertEquals(1, directEvidence.size());
		assertTrue(directEvidence.contains(evidence2));
		
		// remove evidence for subjectId2,objectId between startDate and endDate (inclusive)
		this.trustEvidenceRepo.removeDirectEvidence(subjectId2, objectId,
				TrustEvidenceType.RATED, startDate, endDate);
		// verify
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId, objectId);
		assertTrue(directEvidence.isEmpty());
		directEvidence = this.trustEvidenceRepo.retrieveAllDirectEvidence(subjectId2, objectId);
		assertTrue(directEvidence.isEmpty());
	}
	
	/**
	 * Tests the creation, retrieval, update and removal of Indirect Trust Evidence.
	 * 
	 * @throws TrustException
	 */
	@Test
	public void testIndirectTrustEvidenceCRUD() throws TrustException {
		
		// test params
		// SubjectId
		final TrustedEntityId subjectId = new TrustedEntityId(TrustedEntityType.CSS, SUBJECT_CSS_ID);
		// SubjectId2
		final TrustedEntityId subjectId2 = new TrustedEntityId(TrustedEntityType.CSS, SUBJECT_CSS_ID2);
		// ObjectCisId
		final TrustedEntityId objectCisId = new TrustedEntityId(TrustedEntityType.CSS, OBJECT_CIS_ID);
		// ObjectServiceId
		final TrustedEntityId objectServiceId = new TrustedEntityId(TrustedEntityType.CSS, OBJECT_SERVICE_ID);
		// SourceId
		final TrustedEntityId sourceCssId = new TrustedEntityId(
				TrustedEntityType.CSS, SOURCE_CSS_ID);
		
		// startDate
		// Ugly hack for MySQL - remove ms precision from date
		final Date now = new Date();
		final Date startDate = new Date(1000 * (now.getTime() / 1000));
		// endDate
		final Date endDate = new Date(startDate.getTime() + 1000);
		
		final IIndirectTrustEvidence evidence1 = new IndirectTrustEvidence(
				subjectId, objectCisId, TrustEvidenceType.RATED, startDate, new Double(0.5d), sourceCssId);
		final IIndirectTrustEvidence evidence2 = new IndirectTrustEvidence(
				subjectId2, objectServiceId, TrustEvidenceType.RATED, startDate, new Double(0.5d), sourceCssId); 
		final IIndirectTrustEvidence evidence3 = new IndirectTrustEvidence(
				subjectId, objectCisId, TrustEvidenceType.RATED, endDate, new Double(1.0d), sourceCssId); 
		
		Set<IIndirectTrustEvidence> indirectEvidence;
		
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(subjectId, objectCisId);
		assertNotNull(indirectEvidence);
		assertTrue(indirectEvidence.isEmpty());
		
		this.trustEvidenceRepo.addEvidence(evidence1);
		this.trustEvidenceRepo.addEvidence(evidence2);
		this.trustEvidenceRepo.addEvidence(evidence3);
		
		// retrieve ALL evidence for subjectId, objectCisId
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(subjectId, objectCisId);
		assertNotNull(indirectEvidence);
		assertEquals(2, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence1));
		assertTrue(indirectEvidence.contains(evidence3));
		
		// retrieve ALL evidence for subjectId2, objectServiceId
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(subjectId2, objectServiceId);
		assertNotNull(indirectEvidence);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
		
		// retrieve evidence for subjectId, objectCisId between startDate and endDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId, objectCisId,	TrustEvidenceType.RATED, startDate, endDate);
		assertNotNull(indirectEvidence);
		assertEquals(2, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence1));
		assertTrue(indirectEvidence.contains(evidence3));
		
		// retrieve evidence for subjectId2, objectServiceId between startDate and endDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId2, objectServiceId, TrustEvidenceType.RATED, startDate, endDate);
		assertNotNull(indirectEvidence);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
		
		// retrieve evidence for subjectId, objectCisId after startDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId, objectCisId, TrustEvidenceType.RATED, startDate, null);
		assertNotNull(indirectEvidence);
		assertEquals(2, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence1));
		assertTrue(indirectEvidence.contains(evidence3));
				
		// retrieve evidence for subjectId2, objectServiceId after startDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId2, objectServiceId, TrustEvidenceType.RATED, startDate, null);
		assertNotNull(indirectEvidence);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
		
		// retrieve evidence for subjectId, objectCisId after endDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId, objectCisId,	TrustEvidenceType.RATED, endDate, null);
		assertNotNull(indirectEvidence);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence3));
						
		// retrieve evidence for subjectId2, objectServiceId after endDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId2, objectServiceId, TrustEvidenceType.RATED, endDate, null);
		assertNotNull(indirectEvidence);
		assertEquals(0, indirectEvidence.size());
		
		// retrieve evidence for subjectId, objectCisId before startDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId, objectCisId,	TrustEvidenceType.RATED, null, startDate);
		assertNotNull(indirectEvidence);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence1));
						
		// retrieve evidence for subjectId2, objectServiceId before startDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId2, objectServiceId, TrustEvidenceType.RATED, null, startDate);
		assertNotNull(indirectEvidence);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
				
		// retrieve evidence for subjectId, objectCisId before endDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId, objectCisId,	TrustEvidenceType.RATED, null, endDate);
		assertNotNull(indirectEvidence);
		assertEquals(2, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence1));
		assertTrue(indirectEvidence.contains(evidence3));
								
		// retrieve evidence for subjectId2, objectServiceId before endDate (inclusive)
		indirectEvidence = this.trustEvidenceRepo.retrieveIndirectEvidence(
				subjectId2, objectServiceId, TrustEvidenceType.RATED, null, endDate);
		assertNotNull(indirectEvidence);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
		
		// remove ALL evidence for subjectId, objectCisId
		this.trustEvidenceRepo.removeAllIndirectEvidence(subjectId, objectCisId);
		// verify
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId, objectCisId);
		assertTrue(indirectEvidence.isEmpty());
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId2, objectServiceId);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
		
		// remove ALL evidence for subjectId2, objectServiceId
		this.trustEvidenceRepo.removeAllIndirectEvidence(subjectId2, objectServiceId);
		// verify
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId, objectCisId);
		assertTrue(indirectEvidence.isEmpty());
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId2, objectServiceId);
		assertTrue(indirectEvidence.isEmpty());
		
		// re-add evidence
		this.trustEvidenceRepo.addEvidence(evidence1);
		this.trustEvidenceRepo.addEvidence(evidence2);
		this.trustEvidenceRepo.addEvidence(evidence3);
		
		// remove evidence for subjectId, objectCisId before startDate (inclusive)
		this.trustEvidenceRepo.removeIndirectEvidence(subjectId, objectCisId, 
				TrustEvidenceType.RATED, null, startDate);
		// verify
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId, objectCisId);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence3));
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId2, objectServiceId);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
						
		// remove evidence for subjectId, objectCisId after startDate (inclusive)
		this.trustEvidenceRepo.removeIndirectEvidence(subjectId, objectCisId,
				TrustEvidenceType.RATED, startDate, null);
		// verify
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId, objectCisId);
		assertTrue(indirectEvidence.isEmpty());
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId2, objectServiceId);
		assertEquals(1, indirectEvidence.size());
		assertTrue(indirectEvidence.contains(evidence2));
		
		// remove evidence for subjectId2, objectServiceId between startDate and endDate (inclusive)
		this.trustEvidenceRepo.removeIndirectEvidence(
				subjectId2, objectServiceId, TrustEvidenceType.RATED, startDate, endDate);
		// verify
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId, objectCisId);
		assertTrue(indirectEvidence.isEmpty());
		indirectEvidence = this.trustEvidenceRepo.retrieveAllIndirectEvidence(
				subjectId2, objectServiceId);
		assertTrue(indirectEvidence.isEmpty());
	}
} 