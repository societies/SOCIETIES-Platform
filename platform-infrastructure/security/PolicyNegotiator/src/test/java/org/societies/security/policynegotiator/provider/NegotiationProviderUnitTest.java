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
package org.societies.security.policynegotiator.provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.schema.security.policynegotiator.NegotiationType;
import org.societies.api.internal.schema.security.policynegotiator.SlaBean;
import org.societies.api.internal.security.digsig.ISlaSignatureMgr;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.security.policynegotiator.provider.NegotiationProvider;
import org.societies.security.policynegotiator.provider.ProviderServiceMgr;

public class NegotiationProviderUnitTest {

	private NegotiationProvider classUnderTest;
	private ISignatureMgr signatureMgrMock;
	private INegotiationProviderRemote groupMgrMock;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		signatureMgrMock = mock(ISignatureMgr.class);
		groupMgrMock = mock(INegotiationProviderRemote.class);
		
		//classUnderTest = new NegotiationProvider(signatureMgrMock, groupMgrMock);
		classUnderTest = new NegotiationProvider();
		classUnderTest.setGroupMgr(groupMgrMock);
		classUnderTest.setSignatureMgr(signatureMgrMock);
		classUnderTest.init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSettersAndGetters() {
		
		assertNotNull(classUnderTest.getGroupMgr());
		assertSame(groupMgrMock, classUnderTest.getGroupMgr());
		
		assertNotNull(classUnderTest.getSignatureMgr());
		assertSame(signatureMgrMock, classUnderTest.getSignatureMgr());
		
		ISlaSignatureMgr slaSignatureMgr = mock(ISlaSignatureMgr.class);
		classUnderTest.setSlaSignatureMgr(slaSignatureMgr);
		assertSame(slaSignatureMgr, classUnderTest.getSlaSignatureMgr());
		
		ProviderServiceMgr providerServiceMgr = mock(ProviderServiceMgr.class);
		classUnderTest.setProviderServiceMgr(providerServiceMgr);
		assertSame(providerServiceMgr, classUnderTest.getProviderServiceMgr());
	}

	/**
	 * Test method for {@link INegotiationProvider#acceptPolicyAndGetSla(int, String, boolean)}
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testAcceptPolicyAndGetSla() throws InterruptedException, ExecutionException {
		
		int sessionId;
		String signedPolicyOption;
		boolean modified;
		Future<SlaBean> result;
		SlaBean sla;
		
		sessionId = 82943;
		signedPolicyOption = "2";
		modified = false;
		result = classUnderTest.acceptPolicyAndGetSla(sessionId, signedPolicyOption, modified);
		sla = result.get();
		assertNotNull(sla);
		assertEquals(sessionId, sla.getSessionId());
		
		// Actual signature manager would be needed for the following tests
		//assertTrue(sla.isSuccess());
		//assertNotNull(sla.getSla());
	}
	
	/**
	 * Test method for {@link INegotiationProvider#getPolicyOptions()}
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetPolicyOptions() throws InterruptedException, ExecutionException {
		
		Future<SlaBean> result1;
		Future<SlaBean> result2;
		
		result1 = classUnderTest.getPolicyOptions("1", NegotiationType.CIS);
		assertNotNull(result1.get());
		result2 = classUnderTest.getPolicyOptions("1", NegotiationType.SERVICE);
		assertNotNull(result2.get());
		
		assertTrue("Different negotiation processes got same session ID!",
				result1.get().getSessionId() != result2.get().getSessionId());
	}
	
	/**
	 * Test method for {@link INegotiationProvider#reject(int)}
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testReject() throws InterruptedException, ExecutionException {
		
		Random rnd = new Random();
		int sessionId;
		Future<SlaBean> result;
		SlaBean sla;
		
		// Test for a non-existing session
		sessionId = rnd.nextInt();
		result = classUnderTest.reject(sessionId);
		sla = result.get();
		assertFalse(sla.isSuccess());
		assertEquals(sessionId, sla.getSessionId());
		
		// Test for an existing session
//		sessionId = ;
//		result = classUnderTest.reject(sessionId);
//		sla = result.get();
//		assertTrue(sla.isSuccess());
//		assertEquals(sessionId, sla.getSessionId());
	}
}
