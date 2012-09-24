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

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.api.repo.TrustRepositoryException;
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
public class AdvancedTrustRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static final String BASE_ID = "atrt";
	
	private static final String TRUSTOR_ID = BASE_ID + "TrustorIIdentity";
	
	private static final String TRUSTED_CSS_ID = BASE_ID + "CssIIdentity";
	private static final String TRUSTED_CSS_ID2 = BASE_ID + "CssIIdentity2";
	
	private static final String TRUSTED_CIS_ID = BASE_ID + "CisIIdentity";
	private static final String TRUSTED_CIS_ID2 = BASE_ID + "CisIIdentity2";
	
	private static final String TRUSTED_SERVICE_ID = BASE_ID + "ServiceResourceIdentifier";
	private static final String TRUSTED_SERVICE_ID2 = BASE_ID + "ServiceResourceIdentifier2";
	
	//private static final String TRUSTED_SERVICE_TYPE = BASE_ID + "ServiceType";
	//private static final String TRUSTED_SERVICE_TYPE2 = BASE_ID + "ServiceType2";
	
	private static TrustedEntityId cssTeid;
	private static TrustedEntityId cssTeid2;
	
	private static TrustedEntityId cisTeid;
	private static TrustedEntityId cisTeid2;
	
	private static TrustedEntityId serviceTeid;
	private static TrustedEntityId serviceTeid2;
	
	@Autowired
	private ITrustRepository trustRepo;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		cssTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID);
		cssTeid2 = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID2);
		
		cisTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID);
		cisTeid2 = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID2);
		
		serviceTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.SVC, TRUSTED_SERVICE_ID);
		serviceTeid2 = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.SVC, TRUSTED_SERVICE_ID2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		cssTeid = null;
		cssTeid2 = null;
		cisTeid = null;
		cisTeid2 = null;
		serviceTeid = null;
		serviceTeid2 = null;
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
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testTrustedCssWithCis() throws TrustRepositoryException {
		
		ITrustedCss trustedCssFromDb;
		ITrustedCis trustedCisFromDb;
		ITrustedCis trustedCis2FromDb;
		
		// add CSS
		trustedCssFromDb = (ITrustedCss) this.trustRepo.createEntity(cssTeid);
		assertNotNull(trustedCssFromDb.getCommunities());
		assertTrue(trustedCssFromDb.getCommunities().isEmpty());
		
		// add CIS
		trustedCisFromDb = (ITrustedCis) this.trustRepo.createEntity(cisTeid);
		assertNotNull(trustedCisFromDb.getMembers());
		assertTrue(trustedCisFromDb.getMembers().isEmpty());
		
		// add CSS to CIS
		trustedCssFromDb.addCommunity(trustedCisFromDb);
		// verify membership from the side of the CSS
		assertNotNull(trustedCssFromDb.getCommunities());
		assertFalse(trustedCssFromDb.getCommunities().isEmpty());
		assertEquals(1, trustedCssFromDb.getCommunities().size());
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCisFromDb));
		// verify membership from the side of the CIS
		assertNotNull(trustedCisFromDb.getMembers());
		assertFalse(trustedCisFromDb.getMembers().isEmpty());
		assertEquals(1, trustedCisFromDb.getMembers().size());
		assertTrue(trustedCisFromDb.getMembers().contains(trustedCssFromDb));
		
		// persist membership
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify membership from the side of the CSS
		assertNotNull(trustedCssFromDb.getCommunities());
		assertFalse(trustedCssFromDb.getCommunities().isEmpty());
		assertEquals(1, trustedCssFromDb.getCommunities().size());
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCisFromDb));
		for (final ITrustedCis community : trustedCssFromDb.getCommunities())
			assertTrue(community.getMembers().contains(trustedCssFromDb));
		
		// verify membership from the side of the CIS
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(cisTeid);
		assertNotNull(trustedCisFromDb.getMembers());
		assertFalse(trustedCisFromDb.getMembers().isEmpty());
		assertEquals(1, trustedCisFromDb.getMembers().size());
		assertTrue(trustedCisFromDb.getMembers().contains(trustedCssFromDb));
		for (final ITrustedCss member : trustedCisFromDb.getMembers())
			assertTrue(member.getCommunities().contains(trustedCisFromDb));
		
		// add CIS2
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.createEntity(cisTeid2);
		assertNotNull(trustedCis2FromDb.getMembers());
		assertTrue(trustedCis2FromDb.getMembers().isEmpty());
		
		// add CSS to CIS2
		trustedCssFromDb.addCommunity(trustedCis2FromDb);
		// verify membership from the side of the CSS
		assertNotNull(trustedCssFromDb.getCommunities());
		assertFalse(trustedCssFromDb.getCommunities().isEmpty());
		assertEquals(2, trustedCssFromDb.getCommunities().size());
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCisFromDb));
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCis2FromDb));
		// verify membership from the side of the CIS
		assertNotNull(trustedCis2FromDb.getMembers());
		assertFalse(trustedCis2FromDb.getMembers().isEmpty());
		assertEquals(1, trustedCis2FromDb.getMembers().size());
		assertTrue(trustedCis2FromDb.getMembers().contains(trustedCssFromDb));
		
		// persist membership
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify membership from the side of the CSS
		assertNotNull(trustedCssFromDb.getCommunities());
		assertFalse(trustedCssFromDb.getCommunities().isEmpty());
		assertEquals(2, trustedCssFromDb.getCommunities().size());
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCisFromDb));
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCis2FromDb));
		for (final ITrustedCis community : trustedCssFromDb.getCommunities())
			assertTrue(community.getMembers().contains(trustedCssFromDb));
			
		// verify membership from the side of CIS2
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.retrieveEntity(cisTeid2);
		assertNotNull(trustedCis2FromDb.getMembers());
		assertFalse(trustedCis2FromDb.getMembers().isEmpty());
		assertEquals(1, trustedCis2FromDb.getMembers().size());
		assertTrue(trustedCis2FromDb.getMembers().contains(trustedCssFromDb));
		for (final ITrustedCss member : trustedCis2FromDb.getMembers())
			assertTrue(member.getCommunities().contains(trustedCisFromDb));
		
		// remove CSS from CIS2
		trustedCssFromDb.removeCommunity(trustedCis2FromDb);
		// verify membership from the side of the CSS
		assertNotNull(trustedCssFromDb.getCommunities());
		assertFalse(trustedCssFromDb.getCommunities().isEmpty());
		assertEquals(1, trustedCssFromDb.getCommunities().size());
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCisFromDb));
		// verify membership from the side of CIS2
		assertNotNull(trustedCis2FromDb.getMembers());
		assertTrue(trustedCis2FromDb.getMembers().isEmpty());
		
		// persist membership
		trustedCssFromDb = (ITrustedCss) this.trustRepo.updateEntity(trustedCssFromDb);
		// verify membership from the side of the CSS
		assertNotNull(trustedCssFromDb.getCommunities());
		assertFalse(trustedCssFromDb.getCommunities().isEmpty());
		assertEquals(1, trustedCssFromDb.getCommunities().size());
		assertTrue(trustedCssFromDb.getCommunities().contains(trustedCisFromDb));
		for (final ITrustedCis community : trustedCssFromDb.getCommunities())
			assertTrue(community.getMembers().contains(trustedCssFromDb));
				
		// verify membership from the side of CIS2
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.retrieveEntity(cisTeid2);
		assertNotNull(trustedCis2FromDb.getMembers());
		assertTrue(trustedCis2FromDb.getMembers().isEmpty());
		
		// TODO remove CIS3? from DB
		
		// remove CSS from DB
		this.trustRepo.removeEntity(cssTeid);
		// verify CSS is no longer member of CIS
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(cisTeid);
		assertNotNull(trustedCisFromDb.getMembers());
		assertTrue(trustedCisFromDb.getMembers().isEmpty());
		// verify CSS is no longer member of CIS2
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.retrieveEntity(cisTeid2);
		assertNotNull(trustedCis2FromDb.getMembers());
		assertTrue(trustedCis2FromDb.getMembers().isEmpty());
		
		// remove CIS from DB
		this.trustRepo.removeEntity(cisTeid);
		// remove CIS2 from DB
		this.trustRepo.removeEntity(cisTeid2);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#updateEntity(org.societies.privacytrust.trust.api.model.TrustedEntity)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testTrustedCssWithServices() throws TrustRepositoryException {
		
		ITrustedCss trustedCssFromDb;
		ITrustedService trustedServiceFromDb;
		ITrustedService trustedService2FromDb;
		
		// add CSS2 to DB
		trustedCssFromDb = (ITrustedCss) this.trustRepo.createEntity(cssTeid2);
		assertNotNull(trustedCssFromDb.getServices());
		assertTrue(trustedCssFromDb.getServices().isEmpty());
		
		// add SERVICE to DB
		trustedServiceFromDb = (ITrustedService) this.trustRepo.createEntity(serviceTeid);
		assertNull(trustedServiceFromDb.getProvider());
		// add SERVICE to CSS
		trustedServiceFromDb.setProvider(trustedCssFromDb);
		// verify membership from the side of SERVICE
		assertNotNull(trustedServiceFromDb.getProvider());
		assertEquals(trustedCssFromDb, trustedServiceFromDb.getProvider());
		// verify membership from the side of CSS
		assertFalse(trustedCssFromDb.getServices().isEmpty());
		assertTrue(trustedCssFromDb.getServices().contains(trustedServiceFromDb));
		
		// persist membership
		trustedServiceFromDb = (ITrustedService) this.trustRepo.updateEntity(trustedServiceFromDb);
		// verify membership from the side of SERVICE
		assertNotNull(trustedServiceFromDb.getProvider());
		assertEquals(trustedCssFromDb, trustedServiceFromDb.getProvider());
		// verify membership from the side of CSS
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCssFromDb.getTeid());
		assertFalse(trustedCssFromDb.getServices().isEmpty());
		assertTrue(trustedCssFromDb.getServices().contains(trustedServiceFromDb));
		
		// add SERVICE2 to DB
		trustedService2FromDb = (ITrustedService) this.trustRepo.createEntity(serviceTeid2);
		assertNull(trustedService2FromDb.getProvider());
		// add SERVICE2 to CSS
		trustedService2FromDb.setProvider(trustedCssFromDb);
		// verify membership from the side of SERVICE2
		assertNotNull(trustedService2FromDb.getProvider());
		assertEquals(trustedCssFromDb, trustedService2FromDb.getProvider());
		// verify membership from the side of CSS
		assertFalse(trustedCssFromDb.getServices().isEmpty());
		assertTrue(trustedCssFromDb.getServices().contains(trustedServiceFromDb));
		assertTrue(trustedCssFromDb.getServices().contains(trustedService2FromDb));
		
		// persist membership
		trustedService2FromDb = (ITrustedService) this.trustRepo.updateEntity(trustedService2FromDb);
		// verify membership from the side of SERVICE2
		assertNotNull(trustedService2FromDb.getProvider());
		assertEquals(trustedCssFromDb, trustedService2FromDb.getProvider());
		// verify membership from the side of CSS
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCssFromDb.getTeid());
		assertFalse(trustedCssFromDb.getServices().isEmpty());
		assertTrue(trustedCssFromDb.getServices().contains(trustedServiceFromDb));
		assertTrue(trustedCssFromDb.getServices().contains(trustedService2FromDb));
		
		// remove SERVICE2 from CSS
		trustedService2FromDb.setProvider(null);
		// verify CSS is no longer provider of SERVICE2
		assertNull(trustedService2FromDb.getProvider());
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCssFromDb.getTeid());
		
		// persist membership
		trustedService2FromDb = (ITrustedService) this.trustRepo.updateEntity(trustedService2FromDb);
		assertNull(trustedService2FromDb.getProvider());
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCssFromDb.getTeid());
		assertFalse(trustedCssFromDb.getServices().contains(trustedService2FromDb));
		
		// remove SERVICE2 from DB
		this.trustRepo.removeEntity(trustedService2FromDb.getTeid());
		trustedService2FromDb = (ITrustedService) this.trustRepo.retrieveEntity(trustedService2FromDb.getTeid());
		assertNull(trustedService2FromDb);
		
		// remove CSS from DB (provider of SERVICE)
		// TODO Avoid having to dissociate services from CSS  
		trustedServiceFromDb = (ITrustedService) this.trustRepo.retrieveEntity(trustedServiceFromDb.getTeid()); // ugly
		trustedServiceFromDb.setProvider(null);                                                                 // ugly
		trustedServiceFromDb = (ITrustedService) this.trustRepo.updateEntity(trustedServiceFromDb);             // ugly
		assertNull(trustedServiceFromDb.getProvider());
		this.trustRepo.removeEntity(trustedCssFromDb.getTeid());
		trustedServiceFromDb = (ITrustedService) this.trustRepo.retrieveEntity(trustedServiceFromDb.getTeid());
		assertNotNull(trustedServiceFromDb);
		assertNull(trustedServiceFromDb.getProvider());
		
		// remove SERVICE from DB
		this.trustRepo.removeEntity(trustedServiceFromDb.getTeid());
		trustedServiceFromDb = (ITrustedService) this.trustRepo.retrieveEntity(trustedServiceFromDb.getTeid());
		assertNull(trustedServiceFromDb);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#retrieveEntities(String, Class))}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveTrustedCsss() throws TrustRepositoryException {
	
		List<ITrustedCss> entities = this.trustRepo.retrieveEntities(TRUSTOR_ID, ITrustedCss.class);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add CSS to DB
		this.trustRepo.createEntity(cssTeid);
		entities = this.trustRepo.retrieveEntities(TRUSTOR_ID, ITrustedCss.class);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertEquals(1, entities.size());
		
		// add CSS2 to DB
		this.trustRepo.createEntity(cssTeid2);
		entities = this.trustRepo.retrieveEntities(TRUSTOR_ID, ITrustedCss.class);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertEquals(2, entities.size());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#retrieveEntities(String, Class))}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveTrustedCiss() throws TrustRepositoryException {
	
		List<ITrustedCis> entities = this.trustRepo.retrieveEntities(TRUSTOR_ID, ITrustedCis.class);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add CIS to DB
		this.trustRepo.createEntity(cisTeid);
		entities = this.trustRepo.retrieveEntities(TRUSTOR_ID, ITrustedCis.class);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertEquals(1, entities.size());
		
		// add CIS2 to DB
		this.trustRepo.createEntity(cisTeid2);
		entities = this.trustRepo.retrieveEntities(TRUSTOR_ID, ITrustedCis.class);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertEquals(2, entities.size());
	}
}