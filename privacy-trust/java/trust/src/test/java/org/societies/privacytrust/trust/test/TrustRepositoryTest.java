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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.api.repo.TrustRepositoryException;
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
	
	private static final TrustedEntityType ENTITY_TYPE = TrustedEntityType.CSS;
	private static final String TRUSTEE_ID = "aFooTrusteeIIdentity";
	
	private static ITrustedEntity trustee;
	
	@Autowired
	private ITrustRepository trustRepo;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		final TrustedEntityId teid = new TrustedEntityId(TRUSTOR_ID, ENTITY_TYPE, TRUSTEE_ID);
		trustee = new TrustedCss(teid);
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
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#addEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testAddEntity() throws TrustRepositoryException {
		
		assertTrue(this.trustRepo.addEntity(trustee));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#retrieveEntity(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveEntity() throws TrustRepositoryException {
		
		ITrustedEntity trusteeFromDb = this.trustRepo.retrieveEntity(trustee.getTeid());
		assertNotNull(trusteeFromDb);
		assertNotNull(trusteeFromDb.getTeid());
		assertNotNull(trusteeFromDb.getTeid().getTrustorId());
		assertNotNull(trusteeFromDb.getTeid().getEntityType());
		assertNotNull(trusteeFromDb.getTeid().getTrusteeId());
		assertEquals(trustee, trusteeFromDb);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 */
	@Test
	@Ignore
	public void testUpdateEntity() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#removeEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 */
	@Test
	@Ignore
	public void testRemoveEntity() {
		fail("Not yet implemented");
	}
}