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

import java.util.Set;
import java.util.SortedSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
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
@ContextConfiguration(locations = {"classpath:META-INF/spring/TrustRepositoryTest-context.xml"})
public class AdvancedTrustRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static double DELTA = 0.000001d;
	
	private static final String BASE_ID = "atrt";
	
	private static final String TRUSTOR_CSS_ID = BASE_ID + "TrustorIIdentity";
	private static final String TRUSTOR_CSS_ID2 = BASE_ID + "TrustorIIdentity2";
	
	private static final String TRUSTEE_CSS_ID = BASE_ID + "CssIIdentity";
	private static final String TRUSTEE_CSS_ID2 = BASE_ID + "CssIIdentity2";
	private static final String TRUSTEE_CSS_ID3 = BASE_ID + "CssIIdentity3";
	private static final String TRUSTEE_CSS_ID4 = BASE_ID + "CssIIdentity4";
	
	private static final String TRUSTEE_CIS_ID = BASE_ID + "CisIIdentity";
	private static final String TRUSTEE_CIS_ID2 = BASE_ID + "CisIIdentity2";
	
	private static final String TRUSTEE_SERVICE_ID = BASE_ID + "ServiceResourceIdentifier";
	private static final String TRUSTEE_SERVICE_ID2 = BASE_ID + "ServiceResourceIdentifier2";
	
	//private static final String TRUSTED_SERVICE_TYPE = BASE_ID + "ServiceType";
	//private static final String TRUSTED_SERVICE_TYPE2 = BASE_ID + "ServiceType2";
	
	private static TrustedEntityId trustorCssTeid;
	private static TrustedEntityId trustorCssTeid2;
	
	private static TrustedEntityId trusteeCssTeid;
	private static TrustedEntityId trusteeCssTeid2;
	private static TrustedEntityId trusteeCssTeid3;
	private static TrustedEntityId trusteeCssTeid4;
	
	private static TrustedEntityId trusteeCisTeid;
	private static TrustedEntityId trusteeCisTeid2;
	
	private static TrustedEntityId trusteeServiceTeid;
	private static TrustedEntityId trusteeServiceTeid2;
	
	@Autowired
	private ITrustRepository trustRepo;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		trustorCssTeid = new TrustedEntityId(TrustedEntityType.CSS, TRUSTOR_CSS_ID);
		trustorCssTeid2 = new TrustedEntityId(TrustedEntityType.CSS, TRUSTOR_CSS_ID2);
		
		trusteeCssTeid = new TrustedEntityId(TrustedEntityType.CSS, TRUSTEE_CSS_ID);
		trusteeCssTeid2 = new TrustedEntityId(TrustedEntityType.CSS, TRUSTEE_CSS_ID2);
		trusteeCssTeid3 = new TrustedEntityId(TrustedEntityType.CSS, TRUSTEE_CSS_ID3);
		trusteeCssTeid4 = new TrustedEntityId(TrustedEntityType.CSS, TRUSTEE_CSS_ID4);
		
		trusteeCisTeid = new TrustedEntityId(TrustedEntityType.CIS, TRUSTEE_CIS_ID);
		trusteeCisTeid2 = new TrustedEntityId(TrustedEntityType.CIS, TRUSTEE_CIS_ID2);
		
		trusteeServiceTeid = new TrustedEntityId(TrustedEntityType.SVC, TRUSTEE_SERVICE_ID);
		trusteeServiceTeid2 = new TrustedEntityId(TrustedEntityType.SVC, TRUSTEE_SERVICE_ID2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		trustorCssTeid = null;
		trustorCssTeid2 = null;
		trusteeCssTeid = null;
		trusteeCssTeid2 = null;
		trusteeCssTeid3 = null;
		trusteeCssTeid4 = null;
		trusteeCisTeid = null;
		trusteeCisTeid2 = null;
		trusteeServiceTeid = null;
		trusteeServiceTeid2 = null;
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
		
		this.trustRepo.removeEntities(null, null, null);
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
		trustedCssFromDb = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		assertNotNull(trustedCssFromDb.getCommunities());
		assertTrue(trustedCssFromDb.getCommunities().isEmpty());
		
		// add CIS
		trustedCisFromDb = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
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
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustorCssTeid, trusteeCisTeid);
		assertNotNull(trustedCisFromDb.getMembers());
		assertFalse(trustedCisFromDb.getMembers().isEmpty());
		assertEquals(1, trustedCisFromDb.getMembers().size());
		assertTrue(trustedCisFromDb.getMembers().contains(trustedCssFromDb));
		for (final ITrustedCss member : trustedCisFromDb.getMembers())
			assertTrue(member.getCommunities().contains(trustedCisFromDb));
		
		// add CIS2
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid2);
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
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustorCssTeid, trusteeCisTeid2);
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
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustorCssTeid, trusteeCisTeid2);
		assertNotNull(trustedCis2FromDb.getMembers());
		assertTrue(trustedCis2FromDb.getMembers().isEmpty());
		
		// TODO remove CIS3? from DB
		
		// remove CSS from DB
		this.trustRepo.removeEntity(trustorCssTeid, trusteeCssTeid);
		// verify CSS is no longer member of CIS
		trustedCisFromDb = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustorCssTeid, trusteeCisTeid);
		assertNotNull(trustedCisFromDb.getMembers());
		assertTrue(trustedCisFromDb.getMembers().isEmpty());
		// verify CSS is no longer member of CIS2
		trustedCis2FromDb = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustorCssTeid, trusteeCisTeid2);
		assertNotNull(trustedCis2FromDb.getMembers());
		assertTrue(trustedCis2FromDb.getMembers().isEmpty());
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
		trustedCssFromDb = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid2);
		assertNotNull(trustedCssFromDb.getServices());
		assertTrue(trustedCssFromDb.getServices().isEmpty());
		
		// add SERVICE to DB
		trustedServiceFromDb = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
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
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCssFromDb.getTrustorId(), trustedCssFromDb.getTrusteeId());
		assertFalse(trustedCssFromDb.getServices().isEmpty());
		assertTrue(trustedCssFromDb.getServices().contains(trustedServiceFromDb));
		
		// add SERVICE2 to DB
		trustedService2FromDb = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid2);
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
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCssFromDb.getTrustorId(), trustedCssFromDb.getTrusteeId());
		assertFalse(trustedCssFromDb.getServices().isEmpty());
		assertTrue(trustedCssFromDb.getServices().contains(trustedServiceFromDb));
		assertTrue(trustedCssFromDb.getServices().contains(trustedService2FromDb));
		
		// remove SERVICE2 from CSS
		trustedService2FromDb.setProvider(null);
		// verify CSS is no longer provider of SERVICE2
		assertNull(trustedService2FromDb.getProvider());
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCssFromDb.getTrustorId(), trustedCssFromDb.getTrusteeId());
		
		// persist membership
		trustedService2FromDb = (ITrustedService) this.trustRepo.updateEntity(trustedService2FromDb);
		assertNull(trustedService2FromDb.getProvider());
		trustedCssFromDb = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCssFromDb.getTrustorId(), trustedCssFromDb.getTrusteeId());
		assertFalse(trustedCssFromDb.getServices().contains(trustedService2FromDb));
		
		// remove SERVICE2 from DB
		this.trustRepo.removeEntity(
				trustedService2FromDb.getTrustorId(), trustedService2FromDb.getTrusteeId());
		trustedService2FromDb = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedService2FromDb.getTrustorId(), trustedService2FromDb.getTrusteeId());
		assertNull(trustedService2FromDb);
		
		// remove CSS from DB (provider of SERVICE)
		// TODO Avoid having to dissociate services from CSS  
		trustedServiceFromDb = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedServiceFromDb.getTrustorId(), trustedServiceFromDb.getTrusteeId()); // ugly
		trustedServiceFromDb.setProvider(null);                                                                 // ugly
		trustedServiceFromDb = (ITrustedService) this.trustRepo.updateEntity(trustedServiceFromDb);             // ugly
		assertNull(trustedServiceFromDb.getProvider());
		this.trustRepo.removeEntity(
				trustedCssFromDb.getTrustorId(), trustedCssFromDb.getTrusteeId());
		trustedServiceFromDb = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedServiceFromDb.getTrustorId(), trustedServiceFromDb.getTrusteeId());
		assertNotNull(trustedServiceFromDb);
		assertNull(trustedServiceFromDb.getProvider());
		
		// remove SERVICE from DB
		this.trustRepo.removeEntity(
				trustedServiceFromDb.getTrustorId(), trustedServiceFromDb.getTrusteeId());
		trustedServiceFromDb = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedServiceFromDb.getTrustorId(), trustedServiceFromDb.getTrusteeId());
		assertNull(trustedServiceFromDb);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#retrieveEntities(String, Class))}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveTrustedEntities() throws TrustRepositoryException {
	
		// retrieve CSSs
		Set<ITrustedEntity> entities = this.trustRepo.retrieveEntities(
				trustorCssTeid, TrustedEntityType.CSS, null);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add CSS to DB
		ITrustedEntity css = 
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.CSS, null);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(css));
		assertEquals(1, entities.size());
		
		// add CSS2 to DB
		ITrustedEntity css2 =
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid2);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.CSS, null);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(css));
		assertTrue(entities.contains(css2));
		assertEquals(2, entities.size());
		
		// add direct trust value to CSS
		css.getDirectTrust().setValue(0.5d);
		css = this.trustRepo.updateEntity(css);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.CSS, TrustValueType.DIRECT);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(css));
		assertEquals(1, entities.size());
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.CSS, TrustValueType.INDIRECT);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.CSS, TrustValueType.USER_PERCEIVED);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
	
		// retrieve CISs
		entities = this.trustRepo.retrieveEntities(
				trustorCssTeid, TrustedEntityType.CIS, null);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add CIS to DB
		ITrustedEntity cis =
				this.trustRepo.createEntity(trustorCssTeid, trusteeCisTeid);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.CIS, null);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(cis));
		assertEquals(1, entities.size());
		
		// add CIS2 to DB
		ITrustedEntity cis2 =
				this.trustRepo.createEntity(trustorCssTeid, trusteeCisTeid2);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.CIS, null);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(cis));
		assertTrue(entities.contains(cis2));
		assertEquals(2, entities.size());
		
		// retrieve all entities with direct trust values
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, null, TrustValueType.DIRECT);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(css));
		assertEquals(1, entities.size());
		// retrieve all entities with indirect trust values
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, null, TrustValueType.INDIRECT);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		// retrieve all entities with user-perceived trust values
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, null, TrustValueType.USER_PERCEIVED);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// retrieve all entities
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, null, null);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(css));
		assertTrue(entities.contains(css2));
		assertTrue(entities.contains(cis));
		assertTrue(entities.contains(cis2));
		assertEquals(4, entities.size());
		
		// retrieve Services
		entities = this.trustRepo.retrieveEntities(
				trustorCssTeid, TrustedEntityType.SVC, null);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());

		// add Service to DB
		ITrustedEntity svc = 
				this.trustRepo.createEntity(trustorCssTeid, trusteeServiceTeid);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.SVC, null);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(svc));
		assertEquals(1, entities.size());

		// add Service2 to DB
		ITrustedEntity svc2 =
				this.trustRepo.createEntity(trustorCssTeid, trusteeServiceTeid2);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.SVC, null);
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(svc));
		assertTrue(entities.contains(svc2));
		assertEquals(2, entities.size());

		// add indirect trust value to Service
		svc.getIndirectTrust().setValue(0.5d);
		svc = this.trustRepo.updateEntity(svc);
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.SVC, TrustValueType.DIRECT);
		assertNotNull(entities);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.SVC, TrustValueType.INDIRECT);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(svc));
		assertEquals(1, entities.size());
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, TrustedEntityType.SVC, TrustValueType.USER_PERCEIVED);
		assertNotNull(entities);
		assertTrue(entities.isEmpty());
		
		// add user-perceived trust value to Service
		svc.getUserPerceivedTrust().setValue(0.25d);
		svc = this.trustRepo.updateEntity(svc);
		// retrieve all entities with user-perceived trust values
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, null, TrustValueType.USER_PERCEIVED);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(svc));
		assertEquals(1, entities.size());
		
		// add user-perceived trust value to CSS
		css.getUserPerceivedTrust().setValue(0.25d);
		css = this.trustRepo.updateEntity(css);
		// retrieve all entities with user-perceived trust values
		entities = this.trustRepo.retrieveEntities(trustorCssTeid, null, TrustValueType.USER_PERCEIVED);
		assertFalse(entities.isEmpty());
		assertTrue(entities.contains(css));
		assertTrue(entities.contains(svc));
		assertEquals(2, entities.size());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.repo.ITrustRepository#retrieveMeanTrustValue(TrustedEntityId, TrustValueType, TrustedEntityType)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveMeanTrustValue() throws TrustRepositoryException {
	
		// retrieve CSSs
		double meanTrustValue =	this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0d, meanTrustValue, DELTA);
		
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0d, meanTrustValue, DELTA);
	
		// add CSS to DB
		ITrustedCss css = (ITrustedCss) 
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0d, meanTrustValue, DELTA);		
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0d, meanTrustValue, DELTA);
		// assign direct trust value to CSS
		css.getDirectTrust().setValue(0.5);
		css = (ITrustedCss) this.trustRepo.updateEntity(css);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.5d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.5d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0d, meanTrustValue, DELTA);
		
		// add CSS2 to DB
		ITrustedCss css2 = (ITrustedCss) 
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid2);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.5d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.5d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0d, meanTrustValue, DELTA);
		// assign direct trust value to CSS2
		css2.getDirectTrust().setValue(1.0);
		css2 = (ITrustedCss) this.trustRepo.updateEntity(css2);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0d, meanTrustValue, DELTA);
		
		// add CIS to DB
		ITrustedCis cis = (ITrustedCis) 
				this.trustRepo.createEntity(trustorCssTeid, trusteeCisTeid);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0d, meanTrustValue, DELTA);
		// assign direct trust value to CIS
		cis.getDirectTrust().setValue(0.3);
		cis = (ITrustedCis) this.trustRepo.updateEntity(cis);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.6d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0.3d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.SVC);
		assertEquals(0.0d, meanTrustValue, DELTA);
		
		// add Service to DB
		ITrustedService svc = (ITrustedService) 
				this.trustRepo.createEntity(trustorCssTeid, trusteeServiceTeid);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.6d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0.3d, meanTrustValue, DELTA);
		// assign direct trust value to Service
		svc.getDirectTrust().setValue(0.2);
		svc = (ITrustedService) this.trustRepo.updateEntity(svc);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.5d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0.3d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.SVC);
		assertEquals(0.2d, meanTrustValue, DELTA);
		
		// assign indirect trust value to CSS
		css.getIndirectTrust().setValue(0.4);
		css = (ITrustedCss) this.trustRepo.updateEntity(css);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.5d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, null);
		assertEquals(0.4d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, TrustedEntityType.CSS);
		assertEquals(0.4d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0.3d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, TrustedEntityType.CIS);
		assertEquals(0.0d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.SVC);
		assertEquals(0.2d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, TrustedEntityType.SVC);
		assertEquals(0.0d, meanTrustValue, DELTA);
		
		// assign user-perceived trust value to Service
		svc.getUserPerceivedTrust().setValue(0.9);
		svc = (ITrustedService) this.trustRepo.updateEntity(svc);
		// verify
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, null);
		assertEquals(0.5d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, null);
		assertEquals(0.4d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.USER_PERCEIVED, null);
		assertEquals(0.9d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CSS);
		assertEquals(0.75d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, TrustedEntityType.CSS);
		assertEquals(0.4d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, TrustedEntityType.SVC);
		assertEquals(0.0d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.CIS);
		assertEquals(0.3d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, TrustedEntityType.CIS);
		assertEquals(0.0d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.USER_PERCEIVED, TrustedEntityType.CIS);
		assertEquals(0.0d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.DIRECT, TrustedEntityType.SVC);
		assertEquals(0.2d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.INDIRECT, TrustedEntityType.SVC);
		assertEquals(0.0d, meanTrustValue, DELTA);
		meanTrustValue = this.trustRepo.retrieveMeanTrustValue(
				trustorCssTeid, TrustValueType.USER_PERCEIVED, TrustedEntityType.SVC);
		assertEquals(0.9d, meanTrustValue, DELTA);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.repo.ITrustRepository#retrieveCssBySimilarity(org.societies.api.privacytrust.trust.model.TrustedEntityId, java.lang.Integer, java.lang.Double)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRetrieveCssBySimilarity() throws TrustRepositoryException {
	
		// retrieve CSSs
		SortedSet<ITrustedCss> cssSet = 
				this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, null);
		assertNotNull(cssSet);
		assertTrue(cssSet.isEmpty());
		
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, 0.5d, null);
		assertNotNull(cssSet);
		assertTrue(cssSet.isEmpty());
		
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, 5);
		assertNotNull(cssSet);
		assertTrue(cssSet.isEmpty());
		
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, 0.55, 5);
		assertNotNull(cssSet);
		assertTrue(cssSet.isEmpty());
	
		// add CSS to DB
		ITrustedCss css = (ITrustedCss) 
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		// verify
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertEquals(1, cssSet.size());
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, 0.5d, null);
		assertNotNull(cssSet);
		assertTrue(cssSet.isEmpty());
		// assign similarity to CSS
		css.setSimilarity(0.5d);
		css = (ITrustedCss) this.trustRepo.updateEntity(css);
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, 0.5d, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertEquals(1, cssSet.size());
		
		// add CSS2 to DB
		ITrustedCss css2 = (ITrustedCss)
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid2);
		// verify
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertTrue(cssSet.contains(css2));
		assertEquals(2, cssSet.size());
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, 0.5d, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertEquals(1, cssSet.size());
		// assign similarity to CSS2
		css2.setSimilarity(0.9d);
		css2 = (ITrustedCss) this.trustRepo.updateEntity(css2);
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, 0.8d, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css2));
		assertEquals(1, cssSet.size());
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertTrue(cssSet.contains(css2));
		assertEquals(2, cssSet.size());
		assertTrue(cssSet.first().equals(css));
		assertTrue(cssSet.last().equals(css2));
		
		// add CSS3 to DB
		ITrustedCss css3 = (ITrustedCss)
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid3);
		// verify
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertTrue(cssSet.contains(css2));
		assertTrue(cssSet.contains(css3));
		assertEquals(3, cssSet.size());
		assertTrue(cssSet.first().equals(css3));
		assertTrue(cssSet.last().equals(css2));
		
		// add CSS4 to DB
		ITrustedCss css4 = (ITrustedCss)
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid4);
		// assign similarity to CSS2
		css4.setSimilarity(0.1d);
		css4 = (ITrustedCss) this.trustRepo.updateEntity(css4);
		// verify
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertTrue(cssSet.contains(css2));
		assertTrue(cssSet.contains(css3));
		assertTrue(cssSet.contains(css4));
		assertEquals(4, cssSet.size());
		assertTrue(cssSet.first().equals(css3));
		assertTrue(cssSet.last().equals(css2));
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, null, 2);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertTrue(cssSet.contains(css2));
		assertEquals(2, cssSet.size());
		assertTrue(cssSet.first().equals(css));
		assertTrue(cssSet.last().equals(css2));
		cssSet = this.trustRepo.retrieveCssBySimilarity(trustorCssTeid, 0.5d, null);
		assertNotNull(cssSet);
		assertFalse(cssSet.isEmpty());
		assertTrue(cssSet.contains(css));
		assertTrue(cssSet.contains(css2));
		assertEquals(2, cssSet.size());
		assertTrue(cssSet.first().equals(css));
		assertTrue(cssSet.last().equals(css2));
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.repo.TrustRepository#removeEntities(TrustedEntityId, TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType)}.
	 * @throws TrustRepositoryException 
	 */
	@Test
	public void testRemoveEntities() throws TrustRepositoryException {
		
		this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		assertTrue(this.trustRepo.removeEntities(null, null, null));
		assertFalse(this.trustRepo.removeEntities(null, null, null));
		
		this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		assertFalse(this.trustRepo.removeEntities(trustorCssTeid2, null, null));
		assertTrue(this.trustRepo.removeEntities(trustorCssTeid, null, null));
		assertFalse(this.trustRepo.removeEntities(trustorCssTeid, null, null));
		assertFalse(this.trustRepo.removeEntities(null, null, null));
		
		this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		assertFalse(this.trustRepo.removeEntities(null, TrustedEntityType.CIS, null));
		assertFalse(this.trustRepo.removeEntities(null, TrustedEntityType.SVC, null));
		assertTrue(this.trustRepo.removeEntities(null, TrustedEntityType.CSS, null));
		assertFalse(this.trustRepo.removeEntities(null, TrustedEntityType.CSS, null));
		assertFalse(this.trustRepo.removeEntities(null, null, null));
		
		this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		this.trustRepo.createEntity(trustorCssTeid, trusteeCisTeid2);
		this.trustRepo.createEntity(trustorCssTeid2, trusteeCssTeid);
		assertFalse(this.trustRepo.removeEntities(null, TrustedEntityType.SVC, null));
		assertTrue(this.trustRepo.removeEntities(null, TrustedEntityType.CIS, null));
		assertFalse(this.trustRepo.removeEntities(null, TrustedEntityType.CIS, null));
		assertTrue(this.trustRepo.removeEntities(null, TrustedEntityType.CSS, null));
		assertFalse(this.trustRepo.removeEntities(null, TrustedEntityType.CSS, null));
		assertFalse(this.trustRepo.removeEntities(null, null, null));
		
		final ITrustedEntity trusteeCssByCss = 
				this.trustRepo.createEntity(trustorCssTeid, trusteeCssTeid);
		trusteeCssByCss.getDirectTrust().setValue(.5d);
		this.trustRepo.updateEntity(trusteeCssByCss);
		final ITrustedEntity trusteeCisByCss =
				this.trustRepo.createEntity(trustorCssTeid, trusteeCisTeid);
		trusteeCisByCss.getIndirectTrust().setValue(.25d);
		this.trustRepo.updateEntity(trusteeCisByCss);
		this.trustRepo.createEntity(trustorCssTeid2, trusteeCssTeid);
		assertFalse(this.trustRepo.removeEntities(null, null, TrustValueType.USER_PERCEIVED));
		assertFalse(this.trustRepo.removeEntities(trustorCssTeid2, null, TrustValueType.DIRECT));
		assertTrue(this.trustRepo.removeEntities(trustorCssTeid, null, TrustValueType.DIRECT));
		assertFalse(this.trustRepo.removeEntities(trustorCssTeid, null, TrustValueType.DIRECT));
		assertFalse(this.trustRepo.removeEntities(null, TrustedEntityType.CSS, TrustValueType.INDIRECT));
		assertTrue(this.trustRepo.removeEntities(null, TrustedEntityType.CIS, TrustValueType.INDIRECT));
		assertFalse(this.trustRepo.removeEntities(trustorCssTeid2, TrustedEntityType.CSS, TrustValueType.INDIRECT));
		assertTrue(this.trustRepo.removeEntities(trustorCssTeid2, TrustedEntityType.CSS, null));
		assertFalse(this.trustRepo.removeEntities(null, null, null));
	}
}