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
package org.societies.security.policynegotiator.requester;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.storage.ISecureStorage;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.security.policynegotiator.requester.NegotiationRequester;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class NegotiationRequesterUnitTest {

	private NegotiationRequester classUnderTest;
	private ISignatureMgr signatureMgrMock;
	private ISecureStorage secureStorageMock;
	private INegotiationProviderRemote groupMgrMock;
	private IIdentityManager idMgrMock;
	private IPersonalisationManager personalizationMgrMock;
	private IEventMgr eventMgrMock;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		
		//Event Manager
		eventMgrMock = mock(IEventMgr.class);
		
		// Signature manager
		signatureMgrMock = mock(ISignatureMgr.class);

		// Secure Storage
		secureStorageMock = mock(ISecureStorage.class);

		// Security comms group manager
		groupMgrMock = mock(INegotiationProviderRemote.class);
		idMgrMock = mock(IIdentityManager.class);
		when(groupMgrMock.getIdMgr()).thenReturn(idMgrMock); 
//		verify(groupMgrMock).getIdMgr();
		
		// Personalization Manager
		personalizationMgrMock = mock(IPersonalisationManager.class);

		// Class under test
		classUnderTest = new NegotiationRequester();
		classUnderTest.setGroupMgr(groupMgrMock);
		classUnderTest.setSignatureMgr(signatureMgrMock);
		classUnderTest.setSecureStorage(secureStorageMock);
		classUnderTest.setPersonalizationMgr(personalizationMgrMock);
		classUnderTest.setEventMgr(eventMgrMock);
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
		assertNotNull(classUnderTest.getPersonalizationMgr());
		assertNotNull(classUnderTest.getSecureStorage());
		assertNotNull(classUnderTest.getSignatureMgr());
		
		IPrivacyPolicyNegotiationManager privacyPolicyNegotiationMgr = mock(IPrivacyPolicyNegotiationManager.class);
		assertFalse(classUnderTest.isPrivacyPolicyNegotiationMgrAvailable());
		classUnderTest.setPrivacyPolicyNegotiationManager(privacyPolicyNegotiationMgr);
		assertSame(privacyPolicyNegotiationMgr, classUnderTest.getPrivacyPolicyNegotiationManager());
		assertTrue(classUnderTest.isPrivacyPolicyNegotiationMgrAvailable());
		
		classUnderTest.setPrivacyPolicyNegotiationIncluded(false);
		assertFalse(classUnderTest.isPrivacyPolicyNegotiationIncluded());
		classUnderTest.setPrivacyPolicyNegotiationIncluded(true);
		assertTrue(classUnderTest.isPrivacyPolicyNegotiationIncluded());

		/*
		IEventMgr eventMgr = mock(IEventMgr.class);
		classUnderTest.setEventMgr(eventMgr);
		assertSame(eventMgr, classUnderTest.getEventMgr());
		*/
	}
	
	/**
	 * Test method for {@link org.societies.security.policynegotiator.requester.NegotiationRequester#reject(int)}.
	 */
	@Test
	public void testReject() {

		Requestor provider;
		INegotiationCallback callback;

		provider = mock(Requestor.class);
		callback = new INegotiationCallback() {
			@Override
			public void onNegotiationComplete(String agreementKey, List <URI> fileUris) {
				assertNull(agreementKey);
			}
			@Override
			public void onNegotiationError(String msg) {
			}
		};
		classUnderTest.setPrivacyPolicyNegotiationIncluded(false);
		classUnderTest.startNegotiation(provider, callback);
	}
}
