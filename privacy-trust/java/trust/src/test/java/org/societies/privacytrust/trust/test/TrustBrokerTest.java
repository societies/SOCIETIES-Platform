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

import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test cases for the TrustBroker
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
public class TrustBrokerTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static final String BASE_ID = "tbt";
	
	private static final String TRUSTOR_ID = BASE_ID + "TrustorIIdentity";
	
	private static final String TRUSTED_CSS_ID = BASE_ID + "CssIIdentity";
	
	private static final String TRUSTED_CIS_ID = BASE_ID + "CisIIdentity";
	
	private static final String TRUSTED_SERVICE_ID = BASE_ID + "ServiceResourceIdentifier";
	
	//private static final String TRUSTED_SERVICE_TYPE = BASE_ID + "ServiceType";
	
	private static TrustedEntityId cssTeid;
	
	private static TrustedEntityId cisTeid;
	
	private static TrustedEntityId serviceTeid;
	
	@Autowired
	private ITrustBroker trustBroker;
	
	@Autowired
	private ITrustRepository trustRepo;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		cssTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID);
		
		cisTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID);
		
		serviceTeid = new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.SVC, TRUSTED_SERVICE_ID);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		cssTeid = null;
		cisTeid = null;
		serviceTeid = null;
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
	 * Test method for {@link org.societies.privacytrust.trust.impl.TrustBroker#retrieveTrust(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveTrustedCssTrust() throws TrustException, InterruptedException, ExecutionException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		
		Double retrievedTrustValue;
		
		retrievedTrustValue = this.trustBroker.retrieveTrust(cssTeid).get(); 
		assertNull(retrievedTrustValue);
		
		// add trusted CSS with user perceived value set to trustValue1
		ITrustedCss trustedCss = (ITrustedCss) this.trustRepo.createEntity(cssTeid);
		trustedCss.getUserPerceivedTrust().setValue(trustValue1);
		this.trustRepo.updateEntity(trustedCss);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrust(trustedCss.getTeid()).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(trustValue1), retrievedTrustValue);
		
		// update trusted CSS with user perceived value set to trustValue2
		trustedCss = (ITrustedCss) this.trustRepo.retrieveEntity(trustedCss.getTeid());
		trustedCss.getUserPerceivedTrust().setValue(trustValue2);
		this.trustRepo.updateEntity(trustedCss);
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrust(trustedCss.getTeid()).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(trustValue2), retrievedTrustValue);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.TrustBroker#retrieveTrust(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveTrustedCisTrust() throws TrustException, InterruptedException, ExecutionException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		
		Double retrievedTrustValue;
		
		retrievedTrustValue = this.trustBroker.retrieveTrust(cisTeid).get(); 
		assertNull(retrievedTrustValue);
		
		// add trusted CIS with user perceived value set to trustValue1
		ITrustedCis trustedCis = (ITrustedCis) this.trustRepo.createEntity(cisTeid);  
		trustedCis.getUserPerceivedTrust().setValue(trustValue1);
		this.trustRepo.updateEntity(trustedCis);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrust(trustedCis.getTeid()).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(trustValue1), retrievedTrustValue);
		
		// update trusted CIS with user perceived value set to trustValue2
		trustedCis = (ITrustedCis) this.trustRepo.retrieveEntity(trustedCis.getTeid());
		trustedCis.getUserPerceivedTrust().setValue(trustValue2);
		this.trustRepo.updateEntity(trustedCis);
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrust(trustedCis.getTeid()).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(trustValue2), retrievedTrustValue);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.TrustBroker#retrieveTrust(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 * @throws TrustException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRetrieveTrustedServiceTrust() throws TrustException, InterruptedException, ExecutionException {
		
		// test params
		final double trustValue1 = 0.5d;
		final double trustValue2 = 0.8d;
		
		Double retrievedTrustValue;
		
		retrievedTrustValue = this.trustBroker.retrieveTrust(serviceTeid).get(); 
		assertNull(retrievedTrustValue);
		
		// add trusted service with user perceived value set to trustValue1 
		ITrustedService trustedService = (ITrustedService) this.trustRepo.createEntity(serviceTeid); 
		trustedService.getUserPerceivedTrust().setValue(trustValue1);
		this.trustRepo.updateEntity(trustedService);
		
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrust(trustedService.getTeid()).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(trustValue1), retrievedTrustValue);
		
		// update trusted service with user perceived value set to trustValue2
		trustedService = (ITrustedService) this.trustRepo.retrieveEntity(trustedService.getTeid());
		trustedService.getUserPerceivedTrust().setValue(trustValue2);
		this.trustRepo.updateEntity(trustedService);
		// verify
		retrievedTrustValue = this.trustBroker.retrieveTrust(trustedService.getTeid()).get();
		assertNotNull(retrievedTrustValue);
		assertEquals(new Double(trustValue2), retrievedTrustValue);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.TrustBroker#registerTrustUpdateEventListener(org.societies.api.internal.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 */
	@Test
	@Ignore
	public void testRegisterTrustUpdateEventListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.TrustBroker#unregisterTrustUpdateEventListener(org.societies.api.internal.privacytrust.trust.event.ITrustUpdateEventListener, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)}.
	 */
	@Test
	@Ignore
	public void testUnregisterTrustUpdateEventListener() {
		fail("Not yet implemented");
	}
}