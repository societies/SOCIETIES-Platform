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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.security.comms.policynegotiator.CommsServer;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class CommsServerUnitTest {

	private CommsServer classUnderTest;
	private ICommManager commMgrMock;
	private INegotiationProvider negotiationProviderMock;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		commMgrMock = mock(ICommManager.class);
		negotiationProviderMock = mock(INegotiationProvider.class);
	
		//classUnderTest = new CommsServer(commMgrMock, negotiationRequesterMock, negotiationProviderMock);
		classUnderTest = new CommsServer();
		classUnderTest.setCommMgr(commMgrMock);
		classUnderTest.setNegotiationProvider(negotiationProviderMock);
		classUnderTest.init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.security.comms.policynegotiator.CommsServer#getJavaPackages()}.
	 */
	@Test
	public void testGetJavaPackages() {
		
		String PACKAGE = "org.societies.api.internal.schema.security.policynegotiator";
		List<String> result;
		
		result = classUnderTest.getJavaPackages();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains(PACKAGE));
	}

	/**
	 * Test method for {@link org.societies.security.comms.policynegotiator.CommsServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)}.
	 */
	@Test
	public void testGetQuery() {
		//classUnderTest.getQuery(stanza, messageBean);
	}

	/**
	 * Test method for {@link org.societies.security.comms.policynegotiator.CommsServer#getXMLNamespaces()}.
	 */
	@Test
	public void testGetXMLNamespaces() {
		
		String NAMESPACE = "http://societies.org/api/internal/schema/security/policynegotiator";
		List<String> result;
		
		result = classUnderTest.getXMLNamespaces();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains(NAMESPACE));
	}

	/**
	 * Test method for {@link org.societies.security.comms.policynegotiator.CommsServer#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)}.
	 */
	@Test
	public void testReceiveMessage() {
		//classUnderTest.receiveMessage(stanza, messageBean);
	}

	/**
	 * Test method for {@link org.societies.security.comms.policynegotiator.CommsServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)}.
	 */
	@Test
	public void testSetQuery() {
		//classUnderTest.setQuery(stanza, messageBean);
	}

}
