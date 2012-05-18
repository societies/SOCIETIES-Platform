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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author olivierm
 *
 */
//Run this test case using Spring jUnit
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerTest.class.getSimpleName());
	
	@Autowired
	IPrivacyDataManager privacyDataManager;
	
	@Autowired
	SessionFactory sessionFactory;
	IPrivacyDataManagerInternal privacyDataManagerInternal;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		privacyDataManagerInternal = new PrivacyDataManagerInternal(sessionFactory);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		privacyDataManagerInternal = null;
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)}.
	 * @throws PrivacyException 
	 */
	@Test
	@Ignore
	public void testObfuscateData() {
		IDataWrapper actual = new SampleWrapper(3);
		boolean expection = false;
		try {
			privacyDataManager.obfuscateData(null, null, null);
		} catch (PrivacyException e) {
			expection = true;
		}
		assertFalse(expection);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)}.
	 */
	@Test
	@Ignore
	public void testHasObfuscatedVersion() {
		CtxIdentifier actual = new CtxAttributeIdentifier(new CtxEntityIdentifier(null, null, null), null, null);
		boolean expection = false;
		try {
			actual = privacyDataManager.hasObfuscatedVersion(null, null, null);
		} catch (PrivacyException e) {
			expection = true;
		}
		assertFalse(expection);
		assertNull(actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#checkPermission(org.societies.api.internal.mock.DataIdentifier, IIdentity, IIdentity, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)}.
	 */
	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyAdded() {
		boolean dataUpdated = false;
		ResponseItem permission = null;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			Requestor requestor = new Requestor(requestorId);
			IIdentity ownerId = Mockito.mock(IIdentity.class);
			Mockito.when(ownerId.getJid()).thenReturn("me@societies.local");
			CtxIdentifier dataId = CtxIdentifierFactory.getInstance().fromString("john@societies.local/ENTITY/person/1/ATTRIBUTE/name/13");
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, ownerId, dataId, actions, decision);
			permission = privacyDataManager.checkPermission(requestor, ownerId, dataId, action);
		} catch (PrivacyException e) {
			LOG.info("PrivacyException: testCheckPermission\n", e);
		} catch (MalformedCtxIdentifierException e) {
			LOG.info("MalformedCtxIdentifierException: testCheckPermission2\n", e);
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permission);
		assertNotNull("No (real) permission retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", permission.getDecision().name(), Decision.PERMIT.name());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#checkPermission(org.societies.api.internal.mock.DataIdentifier, IIdentity, IIdentity, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)}.
	 */
	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyDeleted() {
		boolean dataDeleted = false;
		ResponseItem permission = null;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			Requestor requestor = new Requestor(requestorId);
			IIdentity ownerId = Mockito.mock(IIdentity.class);
			Mockito.when(ownerId.getJid()).thenReturn("me@societies.local");
			CtxIdentifier dataId = CtxIdentifierFactory.getInstance().fromString("john@societies.local/ENTITY/person/1/ATTRIBUTE/name/13");
			Action action = new Action(ActionConstants.READ);
			dataDeleted = privacyDataManagerInternal.deletePermission(requestor, ownerId, dataId);
			permission = privacyDataManager.checkPermission(requestor, ownerId, dataId, action);
		} catch (PrivacyException e) {
			LOG.info("PrivacyException: testCheckPermissionPreviouslyDeleted\n", e);
		} catch (MalformedCtxIdentifierException e) {
			LOG.info("MalformedCtxIdentifierException: testCheckPermissionPreviouslyDeleted\n", e);
		}
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permission);
		assertNotNull("No (real) permission retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", permission.getDecision().name(), Decision.DENY.name());
	}
	
	
	
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		LOG.info("privacyDataManager injected");
		this.privacyDataManager = privacyDataManager;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		LOG.info("sessionFactory injected");
		this.sessionFactory = sessionFactory;
	}
}
