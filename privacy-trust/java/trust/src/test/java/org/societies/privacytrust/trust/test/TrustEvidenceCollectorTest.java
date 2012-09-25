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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.impl.evidence.TrustEvidenceCollector;
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
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
public class TrustEvidenceCollectorTest {
	
	private static final String BASE_ID = "tect";
	
	private static final String TRUSTOR_ID = BASE_ID + "TrustorIIdentity";
	
	private static final String TRUSTED_CSS_ID = BASE_ID + "CssIIdentity";
	private static final String TRUSTED_CSS_ID2 = BASE_ID + "CssIIdentity2";
	
	private static final String TRUSTED_CIS_ID = BASE_ID + "CisIIdentity";
	private static final String TRUSTED_CIS_ID2 = BASE_ID + "CisIIdentity2";
	
	private static final String TRUSTED_SERVICE_ID = BASE_ID + "ServiceResourceIdentifier";
	private static final String TRUSTED_SERVICE_ID2 = BASE_ID + "ServiceResourceIdentifier2";
	
	private static ICommManager mockCommMgr = mock(ICommManager.class);
	private static IIdentityManager mockIdentityMgr = mock(IIdentityManager.class);
	
	private static IIdentity mockTrustorCssIdentity = mock(IIdentity.class);
	
	private static IIdentity mockTrustedCssIdentity = mock(IIdentity.class);
	private static IIdentity mockTrustedCssIdentity2 = mock(IIdentity.class);
	
	private static IIdentity mockTrustedCisIdentity = mock(IIdentity.class);
	private static IIdentity mockTrustedCisIdentity2 = mock(IIdentity.class);
	
	private static ServiceResourceIdentifier mockServiceResourceIdentifier = mock(ServiceResourceIdentifier.class);
	private static ServiceResourceIdentifier mockServiceResourceIdentifier2 = mock(ServiceResourceIdentifier.class);
	
	@Autowired
	private ITrustEvidenceCollector trustEvidenceCollector;
	
	@Autowired
	private ITrustEvidenceRepository trustEvidenceRepo;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		when(mockCommMgr.getIdManager()).thenReturn(mockIdentityMgr);
		when(mockIdentityMgr.fromJid(TRUSTOR_ID)).thenReturn(mockTrustorCssIdentity);;
		when(mockIdentityMgr.isMine(mockTrustorCssIdentity)).thenReturn(true);
		
		when(mockTrustorCssIdentity.toString()).thenReturn(TRUSTOR_ID);
		when(mockTrustorCssIdentity.getType()).thenReturn(IdentityType.CSS);
		
		when(mockTrustedCssIdentity.toString()).thenReturn(TRUSTED_CSS_ID);
		when(mockTrustedCssIdentity.getType()).thenReturn(IdentityType.CSS);
		
		when(mockTrustedCssIdentity2.toString()).thenReturn(TRUSTED_CSS_ID2);
		when(mockTrustedCssIdentity2.getType()).thenReturn(IdentityType.CSS);
		
		when(mockTrustedCisIdentity.toString()).thenReturn(TRUSTED_CIS_ID);
		when(mockTrustedCisIdentity.getType()).thenReturn(IdentityType.CIS);
		
		when(mockTrustedCisIdentity2.toString()).thenReturn(TRUSTED_CIS_ID2);
		when(mockTrustedCisIdentity2.getType()).thenReturn(IdentityType.CIS);
		
		when(mockServiceResourceIdentifier.toString()).thenReturn(TRUSTED_SERVICE_ID);
		
		when(mockServiceResourceIdentifier2.toString()).thenReturn(TRUSTED_SERVICE_ID2);
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
		
		((TrustEvidenceCollector) this.trustEvidenceCollector).setCommMgr(mockCommMgr);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.evidence.TrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity, double, java.util.Date)}.
	 * @throws TrustException 
	 */
	@Test
	public void testAddTrustRating() throws TrustException {
		
		final Date now = new Date();
		this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, 
				mockTrustedCssIdentity, 0.5d, null);
		// verify
		final TrustedEntityId teid = new TrustedEntityId(TRUSTOR_ID, 
				TrustedEntityType.CSS, TRUSTED_CSS_ID);
		final Set<IDirectTrustEvidence> evidenceSet = 
				this.trustEvidenceRepo.retrieveDirectEvidence(teid, 
						TrustEvidenceType.RATED, now, null);
		assertFalse(evidenceSet.isEmpty());
		IDirectTrustEvidence directEvidence = evidenceSet.iterator().next();
		assertEquals(TrustEvidenceType.RATED, directEvidence.getType());
		assertEquals(new Double(0.5d), (Double) directEvidence.getInfo());
		assertTrue(directEvidence.getTimestamp().compareTo(now) >= 0);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.evidence.TrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity, double, java.util.Date)}.
	 * @throws TrustException 
	 */
	@Test
	public void testAddTrustRatingWithIllegalParams() throws TrustException {
		
		// non-CSS trustor
		try {
			this.trustEvidenceCollector.addTrustRating(mockTrustedCisIdentity, mockTrustedCssIdentity, 0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("trustor") != -1);
		}
		
		// non-CSS/CIS trustee
		final IIdentity mockInvalidTrusteeIdentity = mock(IIdentity.class);
		when(mockInvalidTrusteeIdentity.getType()).thenReturn(IdentityType.CSS_LIGHT);
		try {
			this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, mockInvalidTrusteeIdentity, 0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("trustee") != -1);
		}
		 
		// rating out of range
		try {
			this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, mockTrustedCssIdentity, -0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("rating") != -1);
		}
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.evidence.TrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, double, java.util.Date)}.
	 * @throws TrustException 
	 */
	@Test
	public void testAddServiceTrustRating() throws TrustException {
		
		final Date now = new Date();
		this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, 
				mockServiceResourceIdentifier, 0.5d, null);
		// verify
		final TrustedEntityId teid = new TrustedEntityId(TRUSTOR_ID, 
				TrustedEntityType.SVC, TRUSTED_SERVICE_ID);
		final Set<IDirectTrustEvidence> evidenceSet = 
				this.trustEvidenceRepo.retrieveDirectEvidence(teid, 
						TrustEvidenceType.RATED, now, null);
		assertFalse(evidenceSet.isEmpty());
		IDirectTrustEvidence directEvidence = evidenceSet.iterator().next();
		assertEquals(TrustEvidenceType.RATED, directEvidence.getType());
		assertEquals(new Double(0.5d), (Double) directEvidence.getInfo());
		assertTrue(directEvidence.getTimestamp().compareTo(now) >= 0);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.evidence.TrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, double, java.util.Date)}.
	 * @throws TrustException 
	 */
	@Test
	public void testAddServiceTrustRatingWithIllegalParams() throws TrustException {
		
		// non-CSS trustor
		try {
			this.trustEvidenceCollector.addTrustRating(mockTrustedCisIdentity, mockServiceResourceIdentifier, 0.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("trustor") != -1);
		}
		 
		// rating out of range
		try {
			this.trustEvidenceCollector.addTrustRating(mockTrustorCssIdentity, mockServiceResourceIdentifier, 1.5d, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ile) {	
			assertTrue(ile.getMessage().indexOf("rating") != -1);
		}
	}
}