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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.engine.IUserPerceivedTrustEngine;
import org.societies.privacytrust.trust.api.engine.TrustEngineException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.impl.engine.UserPerceivedTrustEngine;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCis;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCss;
import org.societies.privacytrust.trust.impl.repo.model.TrustedService;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
public class UserPerceivedTrustEngineTest {

	private static final String BASE_ID = "uptet";
	
	private static final String TRUSTOR_CSS_ID = BASE_ID + "TrustorCssIIdentity";
	
	private static final int TRUSTEE_CSS_LIST_SIZE = 100;
	private static final String TRUSTEE_CSS_ID_BASE = BASE_ID + "TrusteeCssIIdentity";
	
	private static final int TRUSTEE_CIS_LIST_SIZE = 10;
	private static final String TRUSTEE_CIS_ID_BASE = BASE_ID + "TrusteeCisIIdentity";
	
	private static final int TRUSTEE_SERVICE_LIST_SIZE = 500;
	private static final String TRUSTEE_SERVICE_ID_BASE = BASE_ID + "TrusteeServiceResourceIdentifier";
	
	private static List<ITrustedCss> trusteeCssList;
	
	private static List<ITrustedCis> trusteeCisList;
	
	private static List<ITrustedService> trusteeServiceList;
	
	private static ITrustEventMgr mockTrustEventMgr = mock(ITrustEventMgr.class);
	
	/** The UserPerceivedTrustEngine service reference. */
	private IUserPerceivedTrustEngine engine;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		doNothing().when(mockTrustEventMgr).registerUpdateListener(
				any(ITrustUpdateEventListener.class),	any(String[].class));
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
		
		final TrustedEntityId trustorCssId = 
				new TrustedEntityId(TrustedEntityType.CSS, TRUSTOR_CSS_ID);
		
		trusteeCssList = new ArrayList<ITrustedCss>(TRUSTEE_CSS_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_CSS_LIST_SIZE; ++i) {
			final TrustedEntityId trusteeCssId = 
					new TrustedEntityId(TrustedEntityType.CSS, TRUSTEE_CSS_ID_BASE+i);
			trusteeCssList.add(new TrustedCss(trustorCssId, trusteeCssId));
		}
		
		trusteeCisList = new ArrayList<ITrustedCis>(TRUSTEE_CIS_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_CIS_LIST_SIZE; ++i) {
			final TrustedEntityId trusteeCisId =
					new TrustedEntityId(TrustedEntityType.CIS, TRUSTEE_CIS_ID_BASE+i);
			trusteeCisList.add(new TrustedCis(trustorCssId, trusteeCisId));
		}
	
		trusteeServiceList = new ArrayList<ITrustedService>(TRUSTEE_SERVICE_LIST_SIZE);
		for (int i = 0; i < TRUSTEE_SERVICE_LIST_SIZE; ++i) {
			final TrustedEntityId trusteeServiceId = 
					new TrustedEntityId(TrustedEntityType.SVC, TRUSTEE_SERVICE_ID_BASE+i);
			trusteeServiceList.add(new TrustedService(trustorCssId, trusteeServiceId));
		}
		
		this.engine = new UserPerceivedTrustEngine(mockTrustEventMgr); 
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.UserPerceivedTrustEngine#evaluateCssTrustValues(java.util.List)}.
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateCssTrustValues() throws TrustEngineException {
		
		final ITrustedCss trustedCss = trusteeCssList.get(0);
		final List<ITrustedCss> trustedCssSubList = new ArrayList<ITrustedCss>();
		trustedCssSubList.add(trustedCss);
		
		// set direct trust value
		final Double directTrustValue = 1.0d;
		trustedCss.getDirectTrust().setValue(directTrustValue);
		this.engine.evaluateCssTrustValues(trustedCssSubList);
		assertEquals(trustedCss.getUserPerceivedTrust().getLastModified().getTime(),
				trustedCss.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedCss.getUserPerceivedTrust().getValue());
		assertEquals(directTrustValue, trustedCss.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue = 0.0d;
		trustedCss.getIndirectTrust().setValue(indirectTrustValue);
		// set indirect trust value confidence
		final Double indirectTrustConfidence = 0.5d;
		trustedCss.getIndirectTrust().setConfidence(indirectTrustConfidence);
		this.engine.evaluateCssTrustValues(trustedCssSubList);
		assertEquals(trustedCss.getUserPerceivedTrust().getLastModified().getTime(),
				trustedCss.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedCss.getUserPerceivedTrust().getValue());
		final Double userPerceivedTrustValue = 
				(1-indirectTrustConfidence) * directTrustValue;
		assertEquals(userPerceivedTrustValue, trustedCss.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue2 = 0.5d;
		trustedCss.getIndirectTrust().setValue(indirectTrustValue2);
		// set indirect trust value confidence
		final Double indirectTrustConfidence2 = 1.0d;
		trustedCss.getIndirectTrust().setConfidence(indirectTrustConfidence2);
		this.engine.evaluateCssTrustValues(trustedCssSubList);
		assertEquals(trustedCss.getUserPerceivedTrust().getLastModified().getTime(),
				trustedCss.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedCss.getUserPerceivedTrust().getValue());
		final Double userPerceivedTrustValue2 = 
				(1-indirectTrustConfidence2) * directTrustValue + indirectTrustConfidence2 * indirectTrustValue2;
		assertEquals(userPerceivedTrustValue2, trustedCss.getUserPerceivedTrust().getValue());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.UserPerceivedTrustEngine#evaluateCisTrustValues(java.util.List)}.
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateCisTrustValues() throws TrustEngineException {
		
		final ITrustedCis trustedCis = trusteeCisList.get(0);
		final List<ITrustedCis> trustedCisSubList = new ArrayList<ITrustedCis>();
		trustedCisSubList.add(trustedCis);
		
		// set direct trust value
		final Double directTrustValue = 1.0d;
		trustedCis.getDirectTrust().setValue(directTrustValue);
		this.engine.evaluateCisTrustValues(trustedCisSubList);
		assertEquals(trustedCis.getUserPerceivedTrust().getLastModified().getTime(),
				trustedCis.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedCis.getUserPerceivedTrust().getValue());
		assertEquals(directTrustValue, trustedCis.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue = 0.0d;
		trustedCis.getIndirectTrust().setValue(indirectTrustValue);
		// set indirect trust value confidence
		final Double indirectTrustConfidence = 0.5d;
		trustedCis.getIndirectTrust().setConfidence(indirectTrustConfidence);
		this.engine.evaluateCisTrustValues(trustedCisSubList);
		assertEquals(trustedCis.getUserPerceivedTrust().getLastModified().getTime(),
				trustedCis.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedCis.getUserPerceivedTrust().getValue());
		final Double userPerceivedTrustValue = 
				(1-indirectTrustConfidence) * directTrustValue;
		assertEquals(userPerceivedTrustValue, trustedCis.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue2 = 0.5d;
		trustedCis.getIndirectTrust().setValue(indirectTrustValue2);
		// set indirect trust value confidence
		final Double indirectTrustConfidence2 = 1.0d;
		trustedCis.getIndirectTrust().setConfidence(indirectTrustConfidence2);
		this.engine.evaluateCisTrustValues(trustedCisSubList);
		assertEquals(trustedCis.getUserPerceivedTrust().getLastModified().getTime(),
				trustedCis.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedCis.getUserPerceivedTrust().getValue());
		final Double userPerceivedTrustValue2 = 
				(1-indirectTrustConfidence2) * directTrustValue + indirectTrustConfidence2 * indirectTrustValue2;
		assertEquals(userPerceivedTrustValue2, trustedCis.getUserPerceivedTrust().getValue());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.impl.engine.UserPerceivedTrustEngine#evaluateServiceTrustValues(java.util.List)}.
	 * @throws TrustEngineException 
	 */
	@Test
	public void testEvaluateServiceTrustValues() throws TrustEngineException {
		
		final ITrustedService trustedService = trusteeServiceList.get(0);
		final List<ITrustedService> trustedServiceSubList = new ArrayList<ITrustedService>();
		trustedServiceSubList.add(trustedService);
		
		// set direct trust value
		final Double directTrustValue = 1.0d;
		trustedService.getDirectTrust().setValue(directTrustValue);
		this.engine.evaluateServiceTrustValues(trustedServiceSubList);
		assertEquals(trustedService.getUserPerceivedTrust().getLastModified().getTime(),
				trustedService.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedService.getUserPerceivedTrust().getValue());
		assertEquals(directTrustValue, trustedService.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue = 0.0d;
		trustedService.getIndirectTrust().setValue(indirectTrustValue);
		// set indirect trust value confidence
		final Double indirectTrustConfidence = 0.5d;
		trustedService.getIndirectTrust().setConfidence(indirectTrustConfidence);
		this.engine.evaluateServiceTrustValues(trustedServiceSubList);
		assertEquals(trustedService.getUserPerceivedTrust().getLastModified().getTime(),
				trustedService.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedService.getUserPerceivedTrust().getValue());
		final Double userPerceivedTrustValue = 
				(1-indirectTrustConfidence) * directTrustValue;
		assertEquals(userPerceivedTrustValue, trustedService.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue2 = 0.5d;
		trustedService.getIndirectTrust().setValue(indirectTrustValue2);
		// set indirect trust value confidence
		final Double indirectTrustConfidence2 = 1.0d;
		trustedService.getIndirectTrust().setConfidence(indirectTrustConfidence2);
		this.engine.evaluateServiceTrustValues(trustedServiceSubList);
		assertEquals(trustedService.getUserPerceivedTrust().getLastModified().getTime(),
				trustedService.getUserPerceivedTrust().getLastUpdated().getTime(), 1000);
		assertNotNull(trustedService.getUserPerceivedTrust().getValue());
		final Double userPerceivedTrustValue2 = 
				(1-indirectTrustConfidence2) * directTrustValue + indirectTrustConfidence2 * indirectTrustValue2;
		assertEquals(userPerceivedTrustValue2, trustedService.getUserPerceivedTrust().getValue());
	}
}