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
package org.societies.privacytrust.privacyprotection.test.datamanagement.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager;
import org.societies.util.commonmock.MockIdentity;
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
@ContextConfiguration(locations = { "../PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerDeprecationTest {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerDeprecationTest.class.getSimpleName());

	@Autowired
	IPrivacyDataManager privacyDataManager;
	@Autowired
	IPrivacyDataManagerInternal privacyDataManagerInternal;

	// -- Mocked data
	private DataIdentifier dataId;
	private DataIdentifier cisDataId;
	private Requestor requestor;
	private Requestor requestorCis;



	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Requestor
		IIdentity myCssId = new MockIdentity(IdentityType.CSS, "mycss","societies.local");
		IIdentity otherCssId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "cis-one", "societies.local");
		requestor = new Requestor(otherCssId);
		requestorCis = new RequestorCis(otherCssId, cisId);

		// Data Id
		try {
			dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId.getJid()+"/ENTITY/person/1/ATTRIBUTE/name/13");
			cisDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisId.getJid()+"/cis-member-list");
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e.getMessage()+"\n", e);
			fail("setUp(): DataId creation error "+e.getMessage());
		} 

		// Comm Manager
		ICommManager commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idManager = Mockito.mock(IIdentityManager.class);
		INetworkNode myCssNetworkNode = Mockito.mock(INetworkNode.class);
		Mockito.when(myCssNetworkNode.getJid()).thenReturn(myCssId.getJid());
		Mockito.when(idManager.getThisNetworkNode()).thenReturn(myCssNetworkNode);
		Mockito.when(idManager.fromJid(myCssId.getJid())).thenReturn(myCssId);
		Mockito.when(idManager.fromJid(otherCssId.getJid())).thenReturn(otherCssId);
		Mockito.when(idManager.fromJid(cisId.getJid())).thenReturn(cisId);
		Mockito.when(commManager.getIdManager()).thenReturn(idManager);

		// CIS Manager
		ICisManager cisManager = Mockito.mock(ICisManager.class);

		// Privacy Policy Manager
		((PrivacyDataManager) privacyDataManager).setCommManager(commManager);
		((PrivacyDataManager) privacyDataManager).setCisManager(cisManager);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	/* --- CHECK PERMISSION CSS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyAdded() {
		String testTitle = new String("CheckPermission: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestor, dataId, new Action(ActionConstants.READ));
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyAddedRequestorCis() {
		String testTitle = new String("CheckPermission: previously added permission, the requestor is a CIS");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyAddedRequestorCisError() {
		String testTitle = new String("CheckPermission: the requestor is a CIS, previously added permission with a CSS requestor");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action read = new Action(ActionConstants.READ);
			Action write = new Action(ActionConstants.WRITE);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			actions.add(write);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyDeleted() {
		String testTitle = new String("CheckPermission: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, dataId);
			permissions = privacyDataManager.checkPermission(requestor, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}
	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyDeletedRequestorCis() {
		String testTitle = new String("CheckPermission: permission previously deleted, it is sure that it doesn't exist. The requestor is a CIS.");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			Action read = new Action(ActionConstants.READ);
			Action write = new Action(ActionConstants.WRITE, true);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			actions.add(write);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, Decision.PERMIT);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId);
			permissions = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}

	/* --- CHECK PERMISSION CIS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyAdded() {
		String testTitle = new String("CheckPermissionCis: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, cisDataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyDeleted() {
		String testTitle = new String("CheckPermissionCis: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, cisDataId);
			permissions = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}
	

	// -- Dependency Injection
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		LOG.info("[Dependency Injection] IPrivacyDataManager injected");
	}
	public void setPrivacyDataManagerInternal(
			IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
		LOG.info("[Dependency Injection] PrivacyDataManagerInternal injected");
	}
}
