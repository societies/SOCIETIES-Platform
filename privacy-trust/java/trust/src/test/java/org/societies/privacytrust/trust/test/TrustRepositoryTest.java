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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.api.repo.TrustRepositoryException;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCis;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Test cases for the TrustRepository
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.6
 */
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
public class TrustRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static final String TRUSTOR_ID = "aFooTrustorIIdentity";
	
	private static final String TRUSTED_CSS_ID = "aFooCssIIdentity";
	
	private static final String TRUSTED_CIS_ID = "aFooCisIIdentity";
	
	private static ITrustedCss trustedCss;
	
	private static ITrustedCis trustedCis;
	
	@Autowired
	private ITrustRepository trustRepo;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		final TrustedEntityId cssTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID);
		trustedCss = new TrustedCss(cssTeid);
		
		final TrustedEntityId cisTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID);
		trustedCis = new TrustedCis(cisTeid);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		trustedCss = null;
		trustedCis = null;
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
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#addEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testAddTrustedCss() throws TrustRepositoryException {
		
		assertTrue(this.trustRepo.addEntity(trustedCss));
		assertFalse(this.trustRepo.addEntity(trustedCss));
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#addEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testAddTrustedCis() throws TrustRepositoryException {
		
		assertTrue(this.trustRepo.addEntity(trustedCis));
		assertFalse(this.trustRepo.addEntity(trustedCis));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#retrieveEntity(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveTrustedCss() throws TrustRepositoryException {
		
		ITrustedCss trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		assertNotNull(trustedCssFromDb);
		assertEquals(trustedCss.getTeid(), trustedCssFromDb.getTeid());
		assertEquals(trustedCss, trustedCssFromDb);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#retrieveEntity(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveTrustedCiss() throws TrustRepositoryException {
		
		ITrustedCis trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		assertNotNull(trustedCisFromDb);
		assertEquals(trustedCis.getTeid(), trustedCisFromDb.getTeid());
		assertEquals(trustedCis, trustedCisFromDb);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testUpdateTrustedCssDirectTrust() throws TrustRepositoryException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		final Date lastModified1;
		final Date lastUpdated1;
		final Date lastModified2;
		final Date lastUpdated2;
		final Date lastModified3;
		final Date lastUpdated3;
		
		ITrustedCss trustedCssFromDb;
		
		//this.trustRepo.addEntity(trustedCss);
		
		// set direct trust to trustValue1
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getDirectTrust().setValue(trustValue1);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getDirectTrust().getValue());
		assertEquals(new Double(trustValue1), trustedCssFromDb.getDirectTrust().getValue());
		lastModified1 = trustedCssFromDb.getDirectTrust().getLastModified(); 
		lastUpdated1 = trustedCssFromDb.getDirectTrust().getLastUpdated();
		assertNotNull(lastModified1);
		assertNotNull(lastUpdated1);
		assertEquals(lastModified1, lastUpdated1);
		
		// update direct trust with new value, i.e. trustValue2
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getDirectTrust().setValue(trustValue2);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getDirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCssFromDb.getDirectTrust().getValue());
		lastModified2 = trustedCssFromDb.getDirectTrust().getLastModified(); 
		lastUpdated2 = trustedCssFromDb.getDirectTrust().getLastUpdated();
		assertNotNull(lastModified2);
		assertNotNull(lastUpdated2);
		assertEquals(lastModified2, lastUpdated2);
		// verify update of lastModified/Updated props
		assertTrue(lastModified2.getTime() > lastModified1.getTime());
		assertTrue(lastUpdated2.getTime() > lastUpdated1.getTime());
		
		// update direct trust with same value, i.e. trustValue2
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getDirectTrust().setValue(trustValue2);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getDirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCssFromDb.getDirectTrust().getValue());
		lastModified3 = trustedCssFromDb.getDirectTrust().getLastModified(); 
		lastUpdated3 = trustedCssFromDb.getDirectTrust().getLastUpdated();
		assertNotNull(lastModified3);
		assertNotNull(lastUpdated3);
		assertFalse(lastModified3.equals(lastUpdated3));
		// Verify update of lastModified/Updated props
		assertTrue(lastModified3.getTime() == lastModified2.getTime());
		assertTrue(lastUpdated3.getTime() > lastUpdated2.getTime());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testUpdateTrustedCisDirectTrust() throws TrustRepositoryException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		final Date lastModified1;
		final Date lastUpdated1;
		final Date lastModified2;
		final Date lastUpdated2;
		final Date lastModified3;
		final Date lastUpdated3;
		
		ITrustedCis trustedCisFromDb;
		
		//this.trustRepo.addEntity(trustedCis);
		
		// set direct trust to trustValue1
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getDirectTrust().setValue(trustValue1);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getDirectTrust().getValue());
		assertEquals(new Double(trustValue1), trustedCisFromDb.getDirectTrust().getValue());
		lastModified1 = trustedCisFromDb.getDirectTrust().getLastModified(); 
		lastUpdated1 = trustedCisFromDb.getDirectTrust().getLastUpdated();
		assertNotNull(lastModified1);
		assertNotNull(lastUpdated1);
		assertEquals(lastModified1, lastUpdated1);
		
		// update direct trust with new value, i.e. trustValue2
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getDirectTrust().setValue(trustValue2);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getDirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCisFromDb.getDirectTrust().getValue());
		lastModified2 = trustedCisFromDb.getDirectTrust().getLastModified(); 
		lastUpdated2 = trustedCisFromDb.getDirectTrust().getLastUpdated();
		assertNotNull(lastModified2);
		assertNotNull(lastUpdated2);
		assertEquals(lastModified2, lastUpdated2);
		// verify update of lastModified/Updated props
		assertTrue(lastModified2.getTime() > lastModified1.getTime());
		assertTrue(lastUpdated2.getTime() > lastUpdated1.getTime());
		
		// update direct trust with same value, i.e. trustValue2
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getDirectTrust().setValue(trustValue2);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getDirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCisFromDb.getDirectTrust().getValue());
		lastModified3 = trustedCisFromDb.getDirectTrust().getLastModified(); 
		lastUpdated3 = trustedCisFromDb.getDirectTrust().getLastUpdated();
		assertNotNull(lastModified3);
		assertNotNull(lastUpdated3);
		assertFalse(lastModified3.equals(lastUpdated3));
		// Verify update of lastModified/Updated props
		assertTrue(lastModified3.getTime() == lastModified2.getTime());
		assertTrue(lastUpdated3.getTime() > lastUpdated2.getTime());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testUpdateTrustedCssIndirectTrust() throws TrustRepositoryException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		final Date lastModified1;
		final Date lastUpdated1;
		final Date lastModified2;
		final Date lastUpdated2;
		final Date lastModified3;
		final Date lastUpdated3;
		
		ITrustedCss trustedCssFromDb;
		
		//this.trustRepo.addEntity(trustedCss);
		
		// set indirect trust to trustValue1
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getIndirectTrust().setValue(trustValue1);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getIndirectTrust().getValue());
		assertEquals(new Double(trustValue1), trustedCssFromDb.getIndirectTrust().getValue());
		lastModified1 = trustedCssFromDb.getIndirectTrust().getLastModified(); 
		lastUpdated1 = trustedCssFromDb.getIndirectTrust().getLastUpdated();
		assertNotNull(lastModified1);
		assertNotNull(lastUpdated1);
		assertEquals(lastModified1, lastUpdated1);
		
		// update indirect trust with new value, i.e. trustValue2
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getIndirectTrust().setValue(trustValue2);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getIndirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCssFromDb.getIndirectTrust().getValue());
		lastModified2 = trustedCssFromDb.getIndirectTrust().getLastModified(); 
		lastUpdated2 = trustedCssFromDb.getIndirectTrust().getLastUpdated();
		assertNotNull(lastModified2);
		assertNotNull(lastUpdated2);
		assertEquals(lastModified2, lastUpdated2);
		// verify update of lastModified/Updated props
		assertTrue(lastModified2.getTime() > lastModified1.getTime());
		assertTrue(lastUpdated2.getTime() > lastUpdated1.getTime());
		
		// update indirect trust with same value, i.e. trustValue2
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getIndirectTrust().setValue(trustValue2);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getIndirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCssFromDb.getIndirectTrust().getValue());
		lastModified3 = trustedCssFromDb.getIndirectTrust().getLastModified(); 
		lastUpdated3 = trustedCssFromDb.getIndirectTrust().getLastUpdated();
		assertNotNull(lastModified3);
		assertNotNull(lastUpdated3);
		assertFalse(lastModified3.equals(lastUpdated3));
		// Verify update of lastModified/Updated props
		assertTrue(lastModified3.getTime() == lastModified2.getTime());
		assertTrue(lastUpdated3.getTime() > lastUpdated2.getTime());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testUpdateTrustedCisIndirectTrust() throws TrustRepositoryException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		final Date lastModified1;
		final Date lastUpdated1;
		final Date lastModified2;
		final Date lastUpdated2;
		final Date lastModified3;
		final Date lastUpdated3;
		
		ITrustedCis trustedCisFromDb;
		
		//this.trustRepo.addEntity(trustedCis);
		
		// set indirect trust to trustValue1
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getIndirectTrust().setValue(trustValue1);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getIndirectTrust().getValue());
		assertEquals(new Double(trustValue1), trustedCisFromDb.getIndirectTrust().getValue());
		lastModified1 = trustedCisFromDb.getIndirectTrust().getLastModified(); 
		lastUpdated1 = trustedCisFromDb.getIndirectTrust().getLastUpdated();
		assertNotNull(lastModified1);
		assertNotNull(lastUpdated1);
		assertEquals(lastModified1, lastUpdated1);
		
		// update indirect trust with new value, i.e. trustValue2
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getIndirectTrust().setValue(trustValue2);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getIndirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCisFromDb.getIndirectTrust().getValue());
		lastModified2 = trustedCisFromDb.getIndirectTrust().getLastModified(); 
		lastUpdated2 = trustedCisFromDb.getIndirectTrust().getLastUpdated();
		assertNotNull(lastModified2);
		assertNotNull(lastUpdated2);
		assertEquals(lastModified2, lastUpdated2);
		// verify update of lastModified/Updated props
		assertTrue(lastModified2.getTime() > lastModified1.getTime());
		assertTrue(lastUpdated2.getTime() > lastUpdated1.getTime());
		
		// update indirect trust with same value, i.e. trustValue2
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getIndirectTrust().setValue(trustValue2);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getIndirectTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCisFromDb.getIndirectTrust().getValue());
		lastModified3 = trustedCisFromDb.getIndirectTrust().getLastModified(); 
		lastUpdated3 = trustedCisFromDb.getIndirectTrust().getLastUpdated();
		assertNotNull(lastModified3);
		assertNotNull(lastUpdated3);
		assertFalse(lastModified3.equals(lastUpdated3));
		// Verify update of lastModified/Updated props
		assertTrue(lastModified3.getTime() == lastModified2.getTime());
		assertTrue(lastUpdated3.getTime() > lastUpdated2.getTime());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testUpdateTrustedCssUserPerceivedTrust() throws TrustRepositoryException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		final Date lastModified1;
		final Date lastUpdated1;
		final Date lastModified2;
		final Date lastUpdated2;
		final Date lastModified3;
		final Date lastUpdated3;
		
		ITrustedCss trustedCssFromDb;
		
		//this.trustRepo.addEntity(trustedCss);
		
		// set user-perceived trust to trustValue1
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getUserPerceivedTrust().setValue(trustValue1);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getUserPerceivedTrust().getValue());
		assertEquals(new Double(trustValue1), trustedCssFromDb.getUserPerceivedTrust().getValue());
		lastModified1 = trustedCssFromDb.getUserPerceivedTrust().getLastModified(); 
		lastUpdated1 = trustedCssFromDb.getUserPerceivedTrust().getLastUpdated();
		assertNotNull(lastModified1);
		assertNotNull(lastUpdated1);
		assertEquals(lastModified1, lastUpdated1);
		
		// update user-perceived trust with new value, i.e. trustValue2
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getUserPerceivedTrust().setValue(trustValue2);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getUserPerceivedTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCssFromDb.getUserPerceivedTrust().getValue());
		lastModified2 = trustedCssFromDb.getUserPerceivedTrust().getLastModified(); 
		lastUpdated2 = trustedCssFromDb.getUserPerceivedTrust().getLastUpdated();
		assertNotNull(lastModified2);
		assertNotNull(lastUpdated2);
		assertEquals(lastModified2, lastUpdated2);
		// verify update of lastModified/Updated props
		assertTrue(lastModified2.getTime() > lastModified1.getTime());
		assertTrue(lastUpdated2.getTime() > lastUpdated1.getTime());
		
		// update user-perceived trust with same value, i.e. trustValue2
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCssFromDb.getUserPerceivedTrust().setValue(trustValue2);
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify update
		assertNotNull(trustedCssFromDb.getUserPerceivedTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCssFromDb.getUserPerceivedTrust().getValue());
		lastModified3 = trustedCssFromDb.getUserPerceivedTrust().getLastModified(); 
		lastUpdated3 = trustedCssFromDb.getUserPerceivedTrust().getLastUpdated();
		assertNotNull(lastModified3);
		assertNotNull(lastUpdated3);
		assertFalse(lastModified3.equals(lastUpdated3));
		// Verify update of lastModified/Updated props
		assertTrue(lastModified3.getTime() == lastModified2.getTime());
		assertTrue(lastUpdated3.getTime() > lastUpdated2.getTime());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testUpdateTrustedCisUserPerceivedTrust() throws TrustRepositoryException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		final Date lastModified1;
		final Date lastUpdated1;
		final Date lastModified2;
		final Date lastUpdated2;
		final Date lastModified3;
		final Date lastUpdated3;
		
		ITrustedCis trustedCisFromDb;
		
		//this.trustRepo.addEntity(trustedCis);
		
		// set user-perceived trust to trustValue1
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getUserPerceivedTrust().setValue(trustValue1);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getUserPerceivedTrust().getValue());
		assertEquals(new Double(trustValue1), trustedCisFromDb.getUserPerceivedTrust().getValue());
		lastModified1 = trustedCisFromDb.getUserPerceivedTrust().getLastModified(); 
		lastUpdated1 = trustedCisFromDb.getUserPerceivedTrust().getLastUpdated();
		assertNotNull(lastModified1);
		assertNotNull(lastUpdated1);
		assertEquals(lastModified1, lastUpdated1);
		
		// update user-perceived trust with new value, i.e. trustValue2
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getUserPerceivedTrust().setValue(trustValue2);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getUserPerceivedTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCisFromDb.getUserPerceivedTrust().getValue());
		lastModified2 = trustedCisFromDb.getUserPerceivedTrust().getLastModified(); 
		lastUpdated2 = trustedCisFromDb.getUserPerceivedTrust().getLastUpdated();
		assertNotNull(lastModified2);
		assertNotNull(lastUpdated2);
		assertEquals(lastModified2, lastUpdated2);
		// verify update of lastModified/Updated props
		assertTrue(lastModified2.getTime() > lastModified1.getTime());
		assertTrue(lastUpdated2.getTime() > lastUpdated1.getTime());
		
		// update user-perceived trust with same value, i.e. trustValue2
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCisFromDb.getUserPerceivedTrust().setValue(trustValue2);
		trustedCisFromDb = (ITrustedCis) this.trustRepo.updateEntity(trustedCisFromDb);
		// verify update
		assertNotNull(trustedCisFromDb.getUserPerceivedTrust().getValue());
		assertEquals(new Double(trustValue2), trustedCisFromDb.getUserPerceivedTrust().getValue());
		lastModified3 = trustedCisFromDb.getUserPerceivedTrust().getLastModified(); 
		lastUpdated3 = trustedCisFromDb.getUserPerceivedTrust().getLastUpdated();
		assertNotNull(lastModified3);
		assertNotNull(lastUpdated3);
		assertFalse(lastModified3.equals(lastUpdated3));
		// Verify update of lastModified/Updated props
		assertTrue(lastModified3.getTime() == lastModified2.getTime());
		assertTrue(lastUpdated3.getTime() > lastUpdated2.getTime());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#removeEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRemoveCss() throws TrustRepositoryException {
		
		this.trustRepo.addEntity(trustedCss);
		this.trustRepo.removeEntity(trustedCss.getTeid());
		assertNull(this.trustRepo.retrieveEntity(trustedCss.getTeid()));
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#removeEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRemoveCis() throws TrustRepositoryException {
		
		this.trustRepo.addEntity(trustedCis);
		this.trustRepo.removeEntity(trustedCis.getTeid());
		assertNull(this.trustRepo.retrieveEntity(trustedCis.getTeid()));
	}
}