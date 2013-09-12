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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test cases for the InternalTrustBroker
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/TrustBrokerTest-context.xml"})
public class TrustBrokerTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static final String BASE_ID = "tbt";
	
	private static final String TRUSTOR_CSS_ID = BASE_ID + "TrustorCssIIdentity";
	
	private static final String TRUSTED_CSS_ID = BASE_ID + "TrusteeCssIIdentity";
	
	private static final String TRUSTED_CIS_ID = BASE_ID + "TrusteeCisIIdentity";
	
	private static final String TRUSTED_SERVICE_ID = BASE_ID + "TrusteeServiceResourceIdentifier";
	
	//private static final String TRUSTED_SERVICE_TYPE = BASE_ID + "ServiceType";
	
	private static TrustedEntityId trustorCssTeid;
	
	private static TrustedEntityId trusteeCssTeid;
	
	private static TrustedEntityId trusteeCisTeid;
	
	private static TrustedEntityId trusteeServiceTeid;
	
	private static Requestor requestor;
	
	@Autowired
	private ITrustBroker trustBroker;
	
	@Autowired
	@InjectMocks
	private org.societies.api.internal.privacytrust.trust.ITrustBroker internalTrustBroker;
	
	@Autowired
	private ITrustRepository trustRepo;
	
	@Mock
	private ITrustNodeMgr mockTrustNodeMgr;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		trustorCssTeid = new TrustedEntityId(TrustedEntityType.CSS, TRUSTOR_CSS_ID);
		trusteeCssTeid = new TrustedEntityId(TrustedEntityType.CSS, TRUSTED_CSS_ID);
		trusteeCisTeid = new TrustedEntityId(TrustedEntityType.CIS, TRUSTED_CIS_ID);
		trusteeServiceTeid = new TrustedEntityId(TrustedEntityType.SVC, TRUSTED_SERVICE_ID);
		requestor = mock(Requestor.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		trustorCssTeid = null;
		trusteeCssTeid = null;
		trusteeCisTeid = null;
		trusteeServiceTeid = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		final Collection<TrustedEntityId> myIds = new HashSet<TrustedEntityId>();
		myIds.add(trustorCssTeid);
		when(mockTrustNodeMgr.getMyIds()).thenReturn(myIds);
		when(mockTrustNodeMgr.isMaster()).thenReturn(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// remove created entities
		this.trustRepo.removeEntities(null, null, null);
	}
	
	/**
	 * Test method for {@link org.societies.api.internal.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveAllTrustRelationships() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(6, retrievedTrustRelationships.size());
		
		// verify (trustor, trustedCis, direct)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.DIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// verify (trustor, trustedCis, indirect)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// verify (trustor, trustedCis, user perceived)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(9, retrievedTrustRelationships.size());

		// verify (trustor, trustedService, direct)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.DIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());

		// verify (trustor, trustedService, indirect)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());

		// verify (trustor, trustedService, user perceived)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustedEntityId)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveCssTrustRelationships() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double indirectTrustValue1 = 0.3d;
		final double userPerceivedTrustValue1 = 0.5d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, trusteeCssTeid).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(directTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(indirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustedEntityId)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveCisTrustRelationships() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double indirectTrustValue1 = 0.3d;
		final double userPerceivedTrustValue1 = 0.5d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, trusteeCisTeid).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CIS with direct trust value
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(directTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CIS with indirect trust value
		trustedCis = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustedCis.getTrustorId(), trustedCis.getTrusteeId());
		trustedCis.getIndirectTrust().setValue(indirectTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CIS with user perceived trust value
		trustedCis = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustedCis.getTrustorId(), trustedCis.getTrusteeId());
		trustedCis.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustedEntityId)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveServiceTrustRelationships() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double indirectTrustValue1 = 0.3d;
		final double userPerceivedTrustValue1 = 0.5d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, trusteeServiceTeid).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CIS with direct trust value
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(directTrustValue1);
		this.trustRepo.updateEntity(trustedService);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted Service with indirect trust value
		trustedService = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedService.getTrustorId(), trustedService.getTrusteeId());
		trustedService.getIndirectTrust().setValue(indirectTrustValue1);
		this.trustRepo.updateEntity(trustedService);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted Service with user perceived trust value
		trustedService = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedService.getTrustorId(), trustedService.getTrusteeId());
		trustedService.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId()).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(TrustedEntityId, TrustedEntityId, TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveCssTrustRelationship() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double directTrustValue2 = 0.2d;
		final double indirectTrustValue1 = 0.3d;
		final double indirectTrustValue2 = 0.4d;
		final double userPerceivedTrustValue1 = 0.5d;
		final double userPerceivedTrustValue2 = 0.6d;
		
		TrustRelationship retrievedTrustRelationship;
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeCssTeid, TrustValueType.DIRECT).get(); 
		assertNull(retrievedTrustRelationship);
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeCssTeid, TrustValueType.INDIRECT).get(); 
		assertNull(retrievedTrustRelationship);
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeCssTeid, TrustValueType.USER_PERCEIVED).get(); 
		assertNull(retrievedTrustRelationship);
		
		// add trusted CSS with trust values
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(directTrustValue1);
		trustedCss.getIndirectTrust().setValue(indirectTrustValue1);
		trustedCss.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with user perceived value set to trustValue2
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getDirectTrust().setValue(directTrustValue2);
		trustedCss.getIndirectTrust().setValue(indirectTrustValue2);
		trustedCss.getUserPerceivedTrust().setValue(userPerceivedTrustValue2);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(TrustedEntityId, TrustedEntityId, TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveCisTrustRelationship() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double directTrustValue2 = 0.2d;
		final double indirectTrustValue1 = 0.3d;
		final double indirectTrustValue2 = 0.4d;
		final double userPerceivedTrustValue1 = 0.5d;
		final double userPerceivedTrustValue2 = 0.6d;
		
		TrustRelationship retrievedTrustRelationship;
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeCisTeid, TrustValueType.DIRECT).get(); 
		assertNull(retrievedTrustRelationship);
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeCisTeid, TrustValueType.INDIRECT).get(); 
		assertNull(retrievedTrustRelationship);
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeCisTeid, TrustValueType.USER_PERCEIVED).get(); 
		assertNull(retrievedTrustRelationship);
		
		// add trusted CSS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(directTrustValue1);
		trustedCis.getIndirectTrust().setValue(indirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with user perceived value set to trustValue2
		trustedCis = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustedCis.getTrustorId(), trustedCis.getTrusteeId());
		trustedCis.getDirectTrust().setValue(directTrustValue2);
		trustedCis.getIndirectTrust().setValue(indirectTrustValue2);
		trustedCis.getUserPerceivedTrust().setValue(userPerceivedTrustValue2);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationship(TrustedEntityId, TrustedEntityId, TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveServiceTrustRelationship() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double directTrustValue2 = 0.2d;
		final double indirectTrustValue1 = 0.3d;
		final double indirectTrustValue2 = 0.4d;
		final double userPerceivedTrustValue1 = 0.5d;
		final double userPerceivedTrustValue2 = 0.6d;
		
		TrustRelationship retrievedTrustRelationship;
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeServiceTeid, TrustValueType.DIRECT).get(); 
		assertNull(retrievedTrustRelationship);
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeServiceTeid, TrustValueType.INDIRECT).get(); 
		assertNull(retrievedTrustRelationship);
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustorCssTeid, trusteeServiceTeid, TrustValueType.USER_PERCEIVED).get(); 
		assertNull(retrievedTrustRelationship);
		
		// add trusted CSS with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(directTrustValue1);
		trustedService.getIndirectTrust().setValue(indirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);
		
		// verify
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with user perceived value set to trustValue2
		trustedService = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedService.getTrustorId(), trustedService.getTrusteeId());
		trustedService.getDirectTrust().setValue(directTrustValue2);
		trustedService.getIndirectTrust().setValue(indirectTrustValue2);
		trustedService.getUserPerceivedTrust().setValue(userPerceivedTrustValue2);
		this.trustRepo.updateEntity(trustedService);
		
		// verify
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(directTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(indirectTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		retrievedTrustRelationship = this.trustBroker.retrieveTrustRelationship(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(userPerceivedTrustValue2), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}

	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustValue(TrustedEntityId, TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveCssTrustValue() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double directTrustValue2 = 0.2d;
		final double indirectTrustValue1 = 0.3d;
		final double indirectTrustValue2 = 0.4d;
		final double userPerceivedTrustValue1 = 0.5d;
		final double userPerceivedTrustValue2 = 0.6d;
		
		Double retrievedTrustValue;
		
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeCssTeid, TrustValueType.DIRECT).get(); 
		assertNull(retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeCssTeid, TrustValueType.INDIRECT).get(); 
		assertNull(retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeCssTeid, TrustValueType.USER_PERCEIVED).get(); 
		assertNull(retrievedTrustValue);
		
		// add trusted CSS with trust values
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(directTrustValue1);
		trustedCss.getIndirectTrust().setValue(indirectTrustValue1);
		trustedCss.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(directTrustValue1), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(indirectTrustValue1), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustValue);
		
		// update trusted CSS with user perceived value set to trustValue2
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getDirectTrust().setValue(directTrustValue2);
		trustedCss.getIndirectTrust().setValue(indirectTrustValue2);
		trustedCss.getUserPerceivedTrust().setValue(userPerceivedTrustValue2);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(directTrustValue2), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(indirectTrustValue2), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCss.getTrustorId(), trustedCss.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(userPerceivedTrustValue2), retrievedTrustValue);
	}

	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustValue(TrustedEntityId, TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveCisTrustValue() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double directTrustValue2 = 0.2d;
		final double indirectTrustValue1 = 0.3d;
		final double indirectTrustValue2 = 0.4d;
		final double userPerceivedTrustValue1 = 0.5d;
		final double userPerceivedTrustValue2 = 0.6d;
		
		Double retrievedTrustValue;
		
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeCisTeid, TrustValueType.DIRECT).get(); 
		assertNull(retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeCisTeid, TrustValueType.INDIRECT).get(); 
		assertNull(retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeCisTeid, TrustValueType.USER_PERCEIVED).get(); 
		assertNull(retrievedTrustValue);
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(directTrustValue1);
		trustedCis.getIndirectTrust().setValue(indirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(directTrustValue1), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(indirectTrustValue1), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustValue);
		
		// update trusted Cis with user perceived value set to trustValue2
		trustedCis = (ITrustedCis) this.trustRepo.retrieveEntity(
				trustedCis.getTrustorId(), trustedCis.getTrusteeId());
		trustedCis.getDirectTrust().setValue(directTrustValue2);
		trustedCis.getIndirectTrust().setValue(indirectTrustValue2);
		trustedCis.getUserPerceivedTrust().setValue(userPerceivedTrustValue2);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(directTrustValue2), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(indirectTrustValue2), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedCis.getTrustorId(), trustedCis.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(userPerceivedTrustValue2), retrievedTrustValue);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustValue(TrustedEntityId, TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveServiceTrustValue() throws Exception {
		
		// test params
		final double directTrustValue1 = 0.1d;
		final double directTrustValue2 = 0.2d;
		final double indirectTrustValue1 = 0.3d;
		final double indirectTrustValue2 = 0.4d;
		final double userPerceivedTrustValue1 = 0.5d;
		final double userPerceivedTrustValue2 = 0.6d;
		
		Double retrievedTrustValue;
		
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeServiceTeid, TrustValueType.DIRECT).get(); 
		assertNull(retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeServiceTeid, TrustValueType.INDIRECT).get(); 
		assertNull(retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustorCssTeid, trusteeServiceTeid, TrustValueType.USER_PERCEIVED).get(); 
		assertNull(retrievedTrustValue);
		
		// add trusted CIS with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(directTrustValue1);
		trustedService.getIndirectTrust().setValue(indirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(userPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(directTrustValue1), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(indirectTrustValue1), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(userPerceivedTrustValue1), retrievedTrustValue);
		
		// update trusted Service with user perceived value set to trustValue2
		trustedService = (ITrustedService) this.trustRepo.retrieveEntity(
				trustedService.getTrustorId(), trustedService.getTrusteeId());
		trustedService.getDirectTrust().setValue(directTrustValue2);
		trustedService.getIndirectTrust().setValue(indirectTrustValue2);
		trustedService.getUserPerceivedTrust().setValue(userPerceivedTrustValue2);
		this.trustRepo.updateEntity(trustedService);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(directTrustValue2), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(indirectTrustValue2), retrievedTrustValue);
		retrievedTrustValue = this.trustBroker.retrieveTrustValue(
				requestor, trustedService.getTrustorId(), trustedService.getTrusteeId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(userPerceivedTrustValue2), retrievedTrustValue);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustedEntityType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveAllCssTrustRelationships() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, TrustedEntityType.CSS).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CSS).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CSS).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CSS).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CSS).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
				
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CSS).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustedEntityType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveAllCisTrustRelationships() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, TrustedEntityType.CIS).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CIS).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CIS).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CIS).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CIS).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
		
		// verify (trustor, trustedCis, direct)
		TrustRelationship retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.DIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// verify (trustor, trustedCis, indirect)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// verify (trustor, trustedCis, user perceived)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.CIS).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustedEntityType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveAllServiceTrustRelationships() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, TrustedEntityType.SVC).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.SVC).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.SVC).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.SVC).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.SVC).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustedEntityType.SVC).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());

		// verify (trustor, trustedService, direct)
		TrustRelationship retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.DIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());

		// verify (trustor, trustedService, indirect)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());

		// verify (trustor, trustedService, user perceived)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveAllDirectTrustRelationships() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, TrustValueType.DIRECT).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		
		// verify (trustor, trustedCis, direct)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.DIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.DIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());

		// verify (trustor, trustedService, direct)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.DIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveAllIndirectTrustRelationships() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, TrustValueType.INDIRECT).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		
		// verify (trustor, trustedCis, indirect)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.INDIRECT).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());

		// verify (trustor, trustedService, indirect)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.INDIRECT == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveAllUserPerceivedTrustRelationships() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustorCssTeid, TrustValueType.USER_PERCEIVED).get(); 
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationships);
		assertTrue(retrievedTrustRelationships.isEmpty());
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(1, retrievedTrustRelationships.size());
		TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustorId());
		assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
		assertNotNull(retrievedTrustRelationship.getTrusteeId());
		assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
		assertNotNull(retrievedTrustRelationship.getTrustValueType());
		assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cssUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(2, retrievedTrustRelationships.size());
		
		// verify (trustor, trustedCis, user perceived)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeCisTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(cisUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
		
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
				requestor, trustedCss.getTrustorId(), TrustValueType.USER_PERCEIVED).get();
		assertNotNull(retrievedTrustRelationships);
		assertFalse(retrievedTrustRelationships.isEmpty());
		assertEquals(3, retrievedTrustRelationships.size());

		// verify (trustor, trustedService, user perceived)
		retrievedTrustRelationship = null;
		for (final TrustRelationship tr : retrievedTrustRelationships) {
			if (trustorCssTeid.equals(tr.getTrustorId()) 
					&& trusteeServiceTeid.equals(tr.getTrusteeId()) 
					&& TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
				retrievedTrustRelationship = tr;
				break;
			}
		}
		assertNotNull(retrievedTrustRelationship);
		assertNotNull(retrievedTrustRelationship.getTrustValue());
		assertEquals(new Double(serviceUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
		assertNotNull(retrievedTrustRelationship.getTimestamp());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.ITrustBroker#retrieveTrustRelationships(Requestor, TrustedEntityId, TrustedEntityType, TrustValueType)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveTrustRelationshipsByType() throws Exception {
		
		// test params
		final double cssDirectTrustValue1 = 0.1d;
		final double cssIndirectTrustValue1 = 0.2d;
		final double cssUserPerceivedTrustValue1 = 0.3d;
		final double cisDirectTrustValue1 = 0.3d;
		final double cisIndirectTrustValue1 = 0.5d;
		final double cisUserPerceivedTrustValue1 = 0.6d;
		final double serviceDirectTrustValue1 = 0.7d;
		final double serviceIndirectTrustValue1 = 0.8d;
		final double serviceUserPerceivedTrustValue1 = 0.9d;
		
		Set<TrustRelationship> retrievedTrustRelationships;
		
		for (final TrustedEntityType entityType : TrustedEntityType.values()) {
			for (final TrustValueType valueType : TrustValueType.values()) {
				if (TrustedEntityType.LGC == entityType)
					continue;
				retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
						requestor, trustorCssTeid, entityType, valueType).get(); 
				assertNotNull(retrievedTrustRelationships);
				assertTrue(retrievedTrustRelationships.isEmpty());
			}
		}
		
		// add trusted CSS with direct trust value
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCssTeid);
		trustedCss.getDirectTrust().setValue(cssDirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		for (final TrustedEntityType entityType : TrustedEntityType.values()) {
			for (final TrustValueType valueType : TrustValueType.values()) {
				if (TrustedEntityType.LGC == entityType)
					continue;
				retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
						requestor, trustorCssTeid, entityType, valueType).get(); 
				if (TrustedEntityType.CSS == entityType && TrustValueType.DIRECT == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
					TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
					assertNotNull(retrievedTrustRelationship);
					assertNotNull(retrievedTrustRelationship.getTrustorId());
					assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
					assertNotNull(retrievedTrustRelationship.getTrusteeId());
					assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
					assertNotNull(retrievedTrustRelationship.getTrustValueType());
					assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
					assertNotNull(retrievedTrustRelationship.getTrustValue());
					assertEquals(new Double(cssDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
					assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else {
					assertNotNull(retrievedTrustRelationships);
					assertTrue(retrievedTrustRelationships.isEmpty());
				}
			}
		}
		
		// update trusted CSS with indirect trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getIndirectTrust().setValue(cssIndirectTrustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		for (final TrustedEntityType entityType : TrustedEntityType.values()) {
			for (final TrustValueType valueType : TrustValueType.values()) {
				if (TrustedEntityType.LGC == entityType)
					continue;
				retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
						requestor, trustorCssTeid, entityType, valueType).get(); 
				if (TrustedEntityType.CSS == entityType && TrustValueType.INDIRECT == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
					TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
					assertNotNull(retrievedTrustRelationship);
					assertNotNull(retrievedTrustRelationship.getTrustorId());
					assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
					assertNotNull(retrievedTrustRelationship.getTrusteeId());
					assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
					assertNotNull(retrievedTrustRelationship.getTrustValueType());
					assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
					assertNotNull(retrievedTrustRelationship.getTrustValue());
					assertEquals(new Double(cssIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
					assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else if (TrustedEntityType.CSS == entityType && TrustValueType.DIRECT == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
				} else {
					assertNotNull(retrievedTrustRelationships);
					assertTrue(retrievedTrustRelationships.isEmpty());
				}
			}
		}
		
		// update trusted CSS with user perceived trust value
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				trustedCss.getTrustorId(), trustedCss.getTrusteeId());
		trustedCss.getUserPerceivedTrust().setValue(cssUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCss);

		// verify
		for (final TrustedEntityType entityType : TrustedEntityType.values()) {
			for (final TrustValueType valueType : TrustValueType.values()) {
				if (TrustedEntityType.LGC == entityType)
					continue;
				retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
						requestor, trustorCssTeid, entityType, valueType).get(); 
				if (TrustedEntityType.CSS == entityType && TrustValueType.USER_PERCEIVED == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
					TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
					assertNotNull(retrievedTrustRelationship);
					assertNotNull(retrievedTrustRelationship.getTrustorId());
					assertEquals(trustedCss.getTrustorId(), retrievedTrustRelationship.getTrustorId());
					assertNotNull(retrievedTrustRelationship.getTrusteeId());
					assertEquals(trustedCss.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
					assertNotNull(retrievedTrustRelationship.getTrustValueType());
					assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
					assertNotNull(retrievedTrustRelationship.getTrustValue());
					assertEquals(new Double(cssUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
					assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else if (TrustedEntityType.CSS == entityType 
						&& (TrustValueType.DIRECT == valueType 
						|| TrustValueType.INDIRECT == valueType)) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
				} else {
					assertNotNull(retrievedTrustRelationships);
					assertTrue(retrievedTrustRelationships.isEmpty());
				}
			}
		}
		
		// add trusted CIS with trust values
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(
				trustorCssTeid, trusteeCisTeid);
		trustedCis.getDirectTrust().setValue(cisDirectTrustValue1);
		trustedCis.getIndirectTrust().setValue(cisIndirectTrustValue1);
		trustedCis.getUserPerceivedTrust().setValue(cisUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		for (final TrustedEntityType entityType : TrustedEntityType.values()) {
			for (final TrustValueType valueType : TrustValueType.values()) {
				if (TrustedEntityType.LGC == entityType)
					continue;
				retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
						requestor, trustorCssTeid, entityType, valueType).get();
				if (TrustedEntityType.CIS == entityType && TrustValueType.DIRECT == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
					TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
					assertNotNull(retrievedTrustRelationship);
					assertNotNull(retrievedTrustRelationship.getTrustorId());
					assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
					assertNotNull(retrievedTrustRelationship.getTrusteeId());
					assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
					assertNotNull(retrievedTrustRelationship.getTrustValueType());
					assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
					assertNotNull(retrievedTrustRelationship.getTrustValue());
					assertEquals(new Double(cisDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
					assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else if (TrustedEntityType.CIS == entityType && TrustValueType.INDIRECT == valueType) {
						assertNotNull(retrievedTrustRelationships);
						assertFalse(retrievedTrustRelationships.isEmpty());
						assertEquals(1, retrievedTrustRelationships.size());
						TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
						assertNotNull(retrievedTrustRelationship);
						assertNotNull(retrievedTrustRelationship.getTrustorId());
						assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
						assertNotNull(retrievedTrustRelationship.getTrusteeId());
						assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
						assertNotNull(retrievedTrustRelationship.getTrustValueType());
						assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
						assertNotNull(retrievedTrustRelationship.getTrustValue());
						assertEquals(new Double(cisIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
						assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else if (TrustedEntityType.CIS == entityType && TrustValueType.USER_PERCEIVED == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
					TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
					assertNotNull(retrievedTrustRelationship);
					assertNotNull(retrievedTrustRelationship.getTrustorId());
					assertEquals(trustedCis.getTrustorId(), retrievedTrustRelationship.getTrustorId());
					assertNotNull(retrievedTrustRelationship.getTrusteeId());
					assertEquals(trustedCis.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
					assertNotNull(retrievedTrustRelationship.getTrustValueType());
					assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
					assertNotNull(retrievedTrustRelationship.getTrustValue());
					assertEquals(new Double(cisUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
					assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else if (TrustedEntityType.CSS == entityType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
				} else {
					assertNotNull(retrievedTrustRelationships);
					assertTrue(retrievedTrustRelationships.isEmpty());
				}
			}
		}
		
		// add trusted Service with trust values
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(
				trustorCssTeid, trusteeServiceTeid);
		trustedService.getDirectTrust().setValue(serviceDirectTrustValue1);
		trustedService.getIndirectTrust().setValue(serviceIndirectTrustValue1);
		trustedService.getUserPerceivedTrust().setValue(serviceUserPerceivedTrustValue1);
		this.trustRepo.updateEntity(trustedService);

		// verify
		for (final TrustedEntityType entityType : TrustedEntityType.values()) {
			for (final TrustValueType valueType : TrustValueType.values()) {
				if (TrustedEntityType.LGC == entityType)
					continue;
				retrievedTrustRelationships = this.trustBroker.retrieveTrustRelationships(
						requestor, trustorCssTeid, entityType, valueType).get();
				if (TrustedEntityType.SVC == entityType && TrustValueType.DIRECT == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
					TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
					assertNotNull(retrievedTrustRelationship);
					assertNotNull(retrievedTrustRelationship.getTrustorId());
					assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
					assertNotNull(retrievedTrustRelationship.getTrusteeId());
					assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
					assertNotNull(retrievedTrustRelationship.getTrustValueType());
					assertEquals(TrustValueType.DIRECT, retrievedTrustRelationship.getTrustValueType());
					assertNotNull(retrievedTrustRelationship.getTrustValue());
					assertEquals(new Double(serviceDirectTrustValue1), retrievedTrustRelationship.getTrustValue());
					assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else if (TrustedEntityType.SVC == entityType && TrustValueType.INDIRECT == valueType) {
						assertNotNull(retrievedTrustRelationships);
						assertFalse(retrievedTrustRelationships.isEmpty());
						assertEquals(1, retrievedTrustRelationships.size());
						TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
						assertNotNull(retrievedTrustRelationship);
						assertNotNull(retrievedTrustRelationship.getTrustorId());
						assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
						assertNotNull(retrievedTrustRelationship.getTrusteeId());
						assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
						assertNotNull(retrievedTrustRelationship.getTrustValueType());
						assertEquals(TrustValueType.INDIRECT, retrievedTrustRelationship.getTrustValueType());
						assertNotNull(retrievedTrustRelationship.getTrustValue());
						assertEquals(new Double(serviceIndirectTrustValue1), retrievedTrustRelationship.getTrustValue());
						assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else if (TrustedEntityType.SVC == entityType && TrustValueType.USER_PERCEIVED == valueType) {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
					TrustRelationship retrievedTrustRelationship = retrievedTrustRelationships.iterator().next();
					assertNotNull(retrievedTrustRelationship);
					assertNotNull(retrievedTrustRelationship.getTrustorId());
					assertEquals(trustedService.getTrustorId(), retrievedTrustRelationship.getTrustorId());
					assertNotNull(retrievedTrustRelationship.getTrusteeId());
					assertEquals(trustedService.getTrusteeId(), retrievedTrustRelationship.getTrusteeId());
					assertNotNull(retrievedTrustRelationship.getTrustValueType());
					assertEquals(TrustValueType.USER_PERCEIVED, retrievedTrustRelationship.getTrustValueType());
					assertNotNull(retrievedTrustRelationship.getTrustValue());
					assertEquals(new Double(serviceUserPerceivedTrustValue1), retrievedTrustRelationship.getTrustValue());
					assertNotNull(retrievedTrustRelationship.getTimestamp());
				} else {
					assertNotNull(retrievedTrustRelationships);
					assertFalse(retrievedTrustRelationships.isEmpty());
					assertEquals(1, retrievedTrustRelationships.size());
				}
			}
		}
	}
}