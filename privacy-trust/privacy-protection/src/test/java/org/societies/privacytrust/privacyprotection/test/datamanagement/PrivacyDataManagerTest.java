/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.test.datamanagement;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.mock.DataIdentifier;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager;
import org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper;

/**
 * @author olivierm
 *
 */
public class PrivacyDataManagerTest {
	IPrivacyDataManager privacyDataManager;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		privacyDataManager = new PrivacyDataManager();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		privacyDataManager = null;
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)}.
	 * @throws PrivacyException 
	 */
	@Test
	public void testObfuscateData() {
		IDataWrapper actual = new SampleWrapper(3);
		boolean expection = false;
		try {
			actual = privacyDataManager.obfuscateData(null, 1, null);
		} catch (PrivacyException e) {
			expection = true;
		}
		assertFalse(expection);
		assertNull(actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)}.
	 */
	@Test
	public void testHasObfuscatedVersion() {
		CtxIdentifier actual = new CtxAttributeIdentifier(new CtxEntityIdentifier(null, null, null), null, null);
		boolean expection = false;
		try {
			actual = privacyDataManager.hasObfuscatedVersion(null, 0, null);
		} catch (PrivacyException e) {
			expection = true;
		}
		assertFalse(expection);
		assertNull(actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#checkPermission(org.societies.api.internal.mock.DataIdentifier, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)}.
	 */
	@Test
	public void testCheckPermissionDataIdentifierIdentityIdentityIServiceResourceIdentifier() {
		ServiceResourceIdentifier sri = null;
		ResponseItem expected = null;
		ResponseItem actual = privacyDataManager.checkPermission(null, null, null, sri);
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#checkPermission(org.societies.api.internal.mock.DataIdentifier, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.comm.xmpp.datatypes.Identity)}.
	 */
	@Test
	public void testCheckPermissionDataIdentifierIdentityIdentityIdentity() {
		Identity id = null;
		ResponseItem expected = null;
		ResponseItem actual = privacyDataManager.checkPermission(null, null, null, id);
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#checkPermission(org.societies.api.internal.mock.DataIdentifier, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testCheckPermissionDataIdentifierIdentityIdentityRequestPolicy() {
		RequestPolicy policy = null;
		ResponseItem expected = null;
		ResponseItem actual = privacyDataManager.checkPermission(null, null, null, policy);
		assertEquals(expected, actual);
	}

}
