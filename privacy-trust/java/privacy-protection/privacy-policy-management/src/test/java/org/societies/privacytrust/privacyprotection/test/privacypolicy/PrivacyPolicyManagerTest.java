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
package org.societies.privacytrust.privacyprotection.test.privacypolicy;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager;

/**
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyManagerTest {
	IPrivacyPolicyManager privacyPolicyManager;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		privacyPolicyManager = new PrivacyPolicyManager();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		privacyPolicyManager = null;
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testGetPrivacyPolicy() {
		IIdentity cisId = null;
		RequestPolicy expected = null;
		RequestPolicy actual = null;
		try {
			actual = privacyPolicyManager.getPrivacyPolicy(cisId);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	@Ignore
	public void testUpdatePrivacyPolicy() {
		RequestPolicy expected = null;
		RequestPolicy actual = null;
		try {
			actual = privacyPolicyManager.updatePrivacyPolicy(null);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testDeletePrivacyPolicy() {
		IIdentity cisId = null;
		boolean expected = true;
		boolean actual = false;
		try {
			actual = privacyPolicyManager.deletePrivacyPolicy(cisId);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#inferPrivacyPolicy(java.util.Map, java.lang.Object)}.
	 */
	@Test
	@Ignore
	public void testInferPrivacyPolicy() {
		RequestPolicy expected = null;
		RequestPolicy actual = null;
		try {
			actual = privacyPolicyManager.inferPrivacyPolicy(0, null);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

}
