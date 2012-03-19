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
package org.societies.security.commsmgr;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationRequester;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class CommsClientUnitTest {

	private CommsClient classUnderTest;
	private ICommManager commMgrMock;
	private INegotiationProvider negotiationProviderMock;
	private INegotiationRequester negotiationRequesterMock;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		commMgrMock = mock(ICommManager.class);
		//assertNotNull(commMgrMock.getIdManager());
		negotiationProviderMock = mock(INegotiationProvider.class);
		negotiationRequesterMock = mock(INegotiationRequester.class);
		
		classUnderTest = new CommsClient(commMgrMock);
		classUnderTest.init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#getJavaPackages()}.
	 */
	@Test
	public void testGetJavaPackages() {

		String PACKAGE = "org.societies.api.schema.security.policynegotiator";
		List<String> result;
		
		result = classUnderTest.getJavaPackages();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains(PACKAGE));
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#getXMLNamespaces()}.
	 */
	@Test
	public void testGetXMLNamespaces() {
		
		String NAMESPACE = "http://societies.org/api/schema/security/policynegotiator";
		List<String> result;
		
		result = classUnderTest.getXMLNamespaces();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains(NAMESPACE));
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#
	 * receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)}.
	 */
	@Test
	public void testReceiveError() {
		//classUnderTest.receiveError(arg0, arg1);
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#
	 * receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)}.
	 */
	@Test
	public void testReceiveInfo() {
		//classUnderTest.receiveInfo(arg0, arg1, arg2);
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#
	 * receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)}.
	 */
	@Test
	public void testReceiveMessage() {
		//classUnderTest.receiveMessage(arg0, arg1);
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#
	 * receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)}.
	 */
	@Test
	public void testReceiveResult() {
		//classUnderTest.receiveResult(arg0, arg1);
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#
	 * receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)}.
	 */
	@Test
	public void testReceiveItems() {
		//classUnderTest.receiveItems(arg0, arg1, arg2);
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#
	 * acceptPolicyAndGetSla(int, java.lang.String, boolean, org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback)}.
	 */
	@Test
	public void testAcceptPolicyAndGetSla() {
		//classUnderTest.acceptPolicyAndGetSla(sessionId, signedPolicyOption, modified, callback);
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#
	 * getPolicyOptions(org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback)}.
	 */
	@Test
	public void testGetPolicyOptions() {
		//classUnderTest.getPolicyOptions(callback);
	}

	/**
	 * Test method for {@link org.societies.security.commsmgr.CommsClient#reject(int)}.
	 */
	@Test
	public void testReject() {

		Random rnd = new Random();
		int sessionId;

		// Test for a non-existing session
		sessionId = rnd.nextInt();
		
		// Can't test CommsClient.reject(int) because the mock CommManager.getIdManager() returns null
		//classUnderTest.reject(sessionId);
		
		// Test for an existing session
		//sessionId = ;
		//classUnderTest.reject(sessionId);
	}

}
