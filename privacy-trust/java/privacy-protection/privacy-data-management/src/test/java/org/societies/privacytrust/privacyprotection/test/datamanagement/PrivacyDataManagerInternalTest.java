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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
//@ContextConfiguration(locations = { "../../../../../../META-INF/PrivacyPermissionRegistryTest-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PrivacyDataManagerInternalTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger log = LoggerFactory.getLogger(PrivacyDataManagerInternalTest.class.getSimpleName());
	
	@Autowired
	IPrivacyDataManagerInternal privacyDataManagerInternal;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		privacyDataManagerInternal = new PrivacyDataManagerInternal();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
//		privacyDataManagerInternal = null;
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.privacytrust.privacyprotection.mock.DataIdentifier, java.lang.String, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.comm.xmpp.datatypes.Identity)}.
	 */
	@Test
	@Rollback(false)
	public void testUpdatePermission() {
		boolean dataUpdated = false;
		try {
			Requestor requestor = null;
			IIdentity ownerId = null;
			CtxIdentifier dataId = null;
			List<Action> actions = null;
			PrivacyOutcomeConstants permission = PrivacyOutcomeConstants.ALLOW;
			if (null == privacyDataManagerInternal) {
				log.info("privacyDataManagerInternal null");
			}
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, ownerId, dataId, actions, permission);
		} catch (PrivacyException e) {
			log.info("PrivacyException", e);
			e.printStackTrace();
		}
		assertTrue(dataUpdated);
	}

	
	/**
	 * @return the privacyDataManagerInternal
	 */
	public IPrivacyDataManagerInternal getPrivacyDataManagerInternal() {
		return privacyDataManagerInternal;
	}

	/**
	 * @param privacyDataManagerInternal the privacyDataManagerInternal to set
	 */
	public void setPrivacyDataManagerInternal(
			IPrivacyDataManagerInternal privacyDataManagerInternal) {
		log.info("privacyDataManagerInternal injected");
		this.privacyDataManagerInternal = privacyDataManagerInternal;
	}
}
