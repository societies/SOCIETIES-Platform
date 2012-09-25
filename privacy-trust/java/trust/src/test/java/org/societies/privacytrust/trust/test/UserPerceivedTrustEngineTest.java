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
import org.societies.api.privacytrust.trust.event.ITrustEventListener;
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
	
	private static final String TRUSTOR_ID = BASE_ID + "TrustorIIdentity";
	
	private static final int TRUSTED_CSS_LIST_SIZE = 100;
	private static final String TRUSTED_CSS_ID_BASE = BASE_ID + "CssIIdentity";
	
	private static final int TRUSTED_CIS_LIST_SIZE = 10;
	private static final String TRUSTED_CIS_ID_BASE = BASE_ID + "CisIIdentity";
	
	private static final int TRUSTED_SERVICE_LIST_SIZE = 500;
	private static final String TRUSTED_SERVICE_ID_BASE = BASE_ID + "ServiceResourceIdentifier";
	
	private static List<ITrustedCss> trustedCssList;
	
	private static List<ITrustedCis> trustedCisList;
	
	private static List<ITrustedService> trustedServiceList;
	
	private static ITrustEventMgr mockTrustEventMgr = mock(ITrustEventMgr.class);
	
	/** The UserPerceivedTrustEngine service reference. */
	private IUserPerceivedTrustEngine engine;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		doNothing().when(mockTrustEventMgr).registerListener(any(ITrustEventListener.class),
				any(String[].class), any(TrustedEntityId.class));
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
		
		trustedCssList = new ArrayList<ITrustedCss>(TRUSTED_CSS_LIST_SIZE);
		for (int i = 0; i < TRUSTED_CSS_LIST_SIZE; ++i) {
			final TrustedEntityId cssTeid = 
					new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CSS, TRUSTED_CSS_ID_BASE+i);
			trustedCssList.add(new TrustedCss(cssTeid));
		}
		
		trustedCisList = new ArrayList<ITrustedCis>(TRUSTED_CIS_LIST_SIZE);
		for (int i = 0; i < TRUSTED_CIS_LIST_SIZE; ++i) {
			final TrustedEntityId cisTeid =
					new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.CIS, TRUSTED_CIS_ID_BASE+i);
			trustedCisList.add(new TrustedCis(cisTeid));
		}
	
		trustedServiceList = new ArrayList<ITrustedService>(TRUSTED_SERVICE_LIST_SIZE);
		for (int i = 0; i < TRUSTED_SERVICE_LIST_SIZE; ++i) {
			final TrustedEntityId serviceTeid = 
					new TrustedEntityId(TRUSTOR_ID, TrustedEntityType.SVC, TRUSTED_SERVICE_ID_BASE+i);
			trustedServiceList.add(new TrustedService(serviceTeid));
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
		
		final ITrustedCss trustedCss = trustedCssList.get(0);
		final List<ITrustedCss> trustedCssSubList = new ArrayList<ITrustedCss>();
		trustedCssSubList.add(trustedCss);
		
		// set direct trust value
		final Double directTrustValue = 1.0d;
		trustedCss.getDirectTrust().setValue(directTrustValue);
		this.engine.evaluateCssTrustValues(trustedCssSubList);
		assertEquals(trustedCss.getUserPerceivedTrust().getLastModified(),
				trustedCss.getUserPerceivedTrust().getLastUpdated());
		assertNotNull(trustedCss.getUserPerceivedTrust().getValue());
		assertEquals(directTrustValue, trustedCss.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue = 0.0d;
		trustedCss.getIndirectTrust().setValue(indirectTrustValue);
		// set indirect trust value confidence
		final Double indirectTrustConfidence = 0.5d;
		trustedCss.getIndirectTrust().setConfidence(indirectTrustConfidence);
		this.engine.evaluateCssTrustValues(trustedCssSubList);
		assertEquals(trustedCss.getUserPerceivedTrust().getLastModified(),
				trustedCss.getUserPerceivedTrust().getLastUpdated());
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
		assertEquals(trustedCss.getUserPerceivedTrust().getLastModified(),
				trustedCss.getUserPerceivedTrust().getLastUpdated());
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
		
		final ITrustedCis trustedCis = trustedCisList.get(0);
		final List<ITrustedCis> trustedCisSubList = new ArrayList<ITrustedCis>();
		trustedCisSubList.add(trustedCis);
		
		// set direct trust value
		final Double directTrustValue = 1.0d;
		trustedCis.getDirectTrust().setValue(directTrustValue);
		this.engine.evaluateCisTrustValues(trustedCisSubList);
		assertEquals(trustedCis.getUserPerceivedTrust().getLastModified(),
				trustedCis.getUserPerceivedTrust().getLastUpdated());
		assertNotNull(trustedCis.getUserPerceivedTrust().getValue());
		assertEquals(directTrustValue, trustedCis.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue = 0.0d;
		trustedCis.getIndirectTrust().setValue(indirectTrustValue);
		// set indirect trust value confidence
		final Double indirectTrustConfidence = 0.5d;
		trustedCis.getIndirectTrust().setConfidence(indirectTrustConfidence);
		this.engine.evaluateCisTrustValues(trustedCisSubList);
		assertEquals(trustedCis.getUserPerceivedTrust().getLastModified(),
				trustedCis.getUserPerceivedTrust().getLastUpdated());
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
		assertEquals(trustedCis.getUserPerceivedTrust().getLastModified(),
				trustedCis.getUserPerceivedTrust().getLastUpdated());
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
		
		final ITrustedService trustedService = trustedServiceList.get(0);
		final List<ITrustedService> trustedServiceSubList = new ArrayList<ITrustedService>();
		trustedServiceSubList.add(trustedService);
		
		// set direct trust value
		final Double directTrustValue = 1.0d;
		trustedService.getDirectTrust().setValue(directTrustValue);
		this.engine.evaluateServiceTrustValues(trustedServiceSubList);
		assertEquals(trustedService.getUserPerceivedTrust().getLastModified(),
				trustedService.getUserPerceivedTrust().getLastUpdated());
		assertNotNull(trustedService.getUserPerceivedTrust().getValue());
		assertEquals(directTrustValue, trustedService.getUserPerceivedTrust().getValue());
		
		// set indirect trust value
		final Double indirectTrustValue = 0.0d;
		trustedService.getIndirectTrust().setValue(indirectTrustValue);
		// set indirect trust value confidence
		final Double indirectTrustConfidence = 0.5d;
		trustedService.getIndirectTrust().setConfidence(indirectTrustConfidence);
		this.engine.evaluateServiceTrustValues(trustedServiceSubList);
		assertEquals(trustedService.getUserPerceivedTrust().getLastModified(),
				trustedService.getUserPerceivedTrust().getLastUpdated());
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
		assertEquals(trustedService.getUserPerceivedTrust().getLastModified(),
				trustedService.getUserPerceivedTrust().getLastUpdated());
		assertNotNull(trustedService.getUserPerceivedTrust().getValue());
		final Double userPerceivedTrustValue2 = 
				(1-indirectTrustConfidence2) * directTrustValue + indirectTrustConfidence2 * indirectTrustValue2;
		assertEquals(userPerceivedTrustValue2, trustedService.getUserPerceivedTrust().getValue());
	}
}