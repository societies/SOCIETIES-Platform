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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.model.CisAttributeTypes;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager;
import org.societies.util.commonmock.MockIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Olivier Maridat (Trialog)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerDataHierarchyTest {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerDataHierarchyTest.class.getSimpleName());

	@Autowired
	IPrivacyDataManager privacyDataManager;
	@Autowired
	IPrivacyDataManagerInternal privacyDataManagerInternal;

	// -- Mocked data
	private DataIdentifier dataId1;
	private DataIdentifier dataId2;
	private DataIdentifier cisDataId;
	private List<DataIdentifier> dataIdsCtx;
	private List<DataIdentifier> dataIdsAll;
	private RequestorBean requestor;
	private RequestorBean requestorCis;


	@Before
	public void setUp() throws Exception {
		// Requestor
		IIdentity myCssId = new MockIdentity(IdentityType.CSS, "mycss","societies.local");
		IIdentity otherCssId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "cis-one", "societies.local");
		requestor = RequestorUtils.create(otherCssId.getJid());
		requestorCis = RequestorUtils.create(otherCssId.getJid(), cisId.getJid());

		// Data Id
		try {
			Random randomer = new Random((new Date()).getTime()); 
			dataId1 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId.getJid()+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_FIRST+"/"+randomer.nextInt(200));
			dataId2 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId.getJid()+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_LAST+"/"+randomer.nextInt(200));
			cisDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisId.getJid()+"/"+CisAttributeTypes.MEMBER_LIST);
			dataIdsAll = new ArrayList<DataIdentifier>();
			dataIdsCtx = new ArrayList<DataIdentifier>();
			dataIdsAll.add(dataId1);
			dataIdsAll.add(dataId2);
			dataIdsAll.add(cisDataId);
			dataIdsCtx.add(dataId1);
			dataIdsCtx.add(dataId2);
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e+"\n", e);
			fail("setUp(): DataId creation error "+e);
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


	/* --- CHECK PERMISSION CSS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionRequestor() {
		String testTitle = new String("testCheckPermissionRequestor: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		List<ResponseItem> permissions3 = null;
		List<Action> actions = null;
		try {
			actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestor, dataId1, actions, decision);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestor, dataId2, actions, decision);
			permissions1 = privacyDataManager.checkPermission(requestor, dataId1, ActionUtils.create(ActionConstants.READ));
			permissions2 = privacyDataManager.checkPermission(requestor, dataId2, ActionUtils.create(ActionConstants.READ));
			permissions3 = privacyDataManager.checkPermission(requestor, dataIdsCtx, ActionUtils.create(ActionConstants.READ));
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data permission not updated (1)", dataUpdated1);
		assertTrue("Data permission not updated (2)", dataUpdated2);
		// DataId1
		assertNotNull("Empty permission should not be retrieved (1)", permissions1);
		assertTrue("No permission retrieved (1)", permissions1.size() > 0);
		assertNotNull("No permission decision retrieved (1)", permissions1.get(0).getDecision());
		assertEquals("Bad permission retrieved (1)", Decision.PERMIT.name(), permissions1.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (1)", ActionUtils.equal(actions, permissions1.get(0).getRequestItem().getActions()));
		// DataId2
		assertNotNull("Empty permission retrieved (2)", permissions2);
		assertTrue("No permission retrieved (2)", permissions2.size() > 0);
		assertNotNull("No permission decision retrieved (2)", permissions2.get(0).getDecision());
		assertEquals("Bad permission retrieved (2)", Decision.PERMIT.name(), permissions2.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (2)", ActionUtils.equal(actions, permissions2.get(0).getRequestItem().getActions()));
		// DataId1 + DataId2
		assertNotNull("Empty permission retrieved (1+2)", permissions3);
		assertTrue("No permission retrieved (1+2)", permissions3.size() > 0);
		assertTrue("No good permissions size retrieved (1+2) expected "+dataIdsCtx.size()+" but was "+permissions3.size(), permissions3.size() == dataIdsCtx.size());
		assertNotNull("No permission decision retrieved (1+2 -> 1)", permissions3.get(0).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 1)", Decision.PERMIT.name(), permissions3.get(0).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions1 (1+2 -> 1)", permissions1.get(0).getDecision().name(), permissions3.get(0).getDecision().name());
		assertNotNull("No permission decision retrieved (1+2 -> 2)", permissions3.get(1).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 2)", Decision.PERMIT.name(), permissions3.get(1).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions2 (1+2 -> 2)", permissions2.get(0).getDecision().name(), permissions3.get(1).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionRequestorCis() {
		String testTitle = new String("testCheckPermissionRequestorCis: previously added permission, the requestor is a CIS");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		List<ResponseItem> permissions3 = null;
		List<Action> actions = null;
		try {
			actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions, decision);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestorCis, dataId2, actions, decision);
			permissions1 = privacyDataManager.checkPermission(requestorCis, dataId1, actions);
			permissions2 = privacyDataManager.checkPermission(requestorCis, dataId2, actions);
			permissions3 = privacyDataManager.checkPermission(requestorCis, dataIdsCtx, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data permission not updated (1)", dataUpdated1);
		assertTrue("Data permission not updated (2)", dataUpdated2);
		// DataId1
		assertNotNull("Empty permission should not be retrieved (1)", permissions1);
		assertTrue("No permission retrieved (1)", permissions1.size() > 0);
		assertNotNull("No permission decision retrieved (1)", permissions1.get(0).getDecision());
		assertEquals("Bad permission retrieved (1)", Decision.PERMIT.name(), permissions1.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (1)", ActionUtils.equal(actions, permissions1.get(0).getRequestItem().getActions()));
		// DataId2
		assertNotNull("Empty permission retrieved (2)", permissions2);
		assertTrue("No permission retrieved (2)", permissions2.size() > 0);
		assertNotNull("No permission decision retrieved (2)", permissions2.get(0).getDecision());
		assertEquals("Bad permission retrieved (2)", Decision.PERMIT.name(), permissions2.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (2)", ActionUtils.equal(actions, permissions2.get(0).getRequestItem().getActions()));
		// DataId1 + DataId2
		assertNotNull("Empty permission retrieved (1+2)", permissions3);
		assertTrue("No permission retrieved (1+2)", permissions3.size() > 0);
		assertTrue("No good permissions size retrieved (1+2) expected "+dataIdsCtx.size()+" but was "+permissions3.size(), permissions3.size() == dataIdsCtx.size());
		assertNotNull("No permission decision retrieved (1+2 -> 1)", permissions3.get(0).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 1)", Decision.PERMIT.name(), permissions3.get(0).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions1 (1+2 -> 1)", permissions1.get(0).getDecision().name(), permissions3.get(0).getDecision().name());
		assertNotNull("No permission decision retrieved (1+2 -> 2)", permissions3.get(1).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 2)", Decision.PERMIT.name(), permissions3.get(1).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions2 (1+2 -> 2)", permissions2.get(0).getDecision().name(), permissions3.get(1).getDecision().name());
	}
	
	@Test
	@Rollback(true)
	public void testCheckPermissionRequestorCisError() {
		String testTitle = new String("testCheckPermissionRequestorCis: the requestor is a CIS, previously added permission with a CSS requestor");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		List<ResponseItem> permissions3 = null;
		List<Action> actions = null;
		try {
			actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestor, dataId1, actions, decision);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestor, dataId2, actions, decision);
			permissions1 = privacyDataManager.checkPermission(requestorCis, dataId1, actions);
			permissions2 = privacyDataManager.checkPermission(requestorCis, dataId2, actions);
			assertTrue("Expected 2 (before)", dataIdsCtx.size() == 2);
			permissions3 = privacyDataManager.checkPermission(requestorCis, dataIdsCtx, actions);
			assertTrue("Expected 2 (after)", dataIdsCtx.size() == 2);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data permission not updated (1)", dataUpdated1);
		assertTrue("Data permission not updated (2)", dataUpdated2);
		// DataId1
		assertNotNull("Empty permission should not be retrieved (1)", permissions1);
		assertTrue("No permission retrieved (1)", permissions1.size() > 0);
		assertNotNull("No permission decision retrieved (1)", permissions1.get(0).getDecision());
		assertEquals("Bad permission retrieved (1)", Decision.DENY.name(), permissions1.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (1)", ActionUtils.equal(new ArrayList<Action>(), permissions1.get(0).getRequestItem().getActions()));
		// DataId2
		assertNotNull("Empty permission retrieved (2)", permissions2);
		assertTrue("No permission retrieved (2)", permissions2.size() > 0);
		assertNotNull("No permission decision retrieved (2)", permissions2.get(0).getDecision());
		assertEquals("Bad permission retrieved (2)", Decision.DENY.name(), permissions2.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (2)", ActionUtils.equal(new ArrayList<Action>(), permissions2.get(0).getRequestItem().getActions()));
		// DataId1 + DataId2
		assertNotNull("Empty permission retrieved (1+2)", permissions3);
		assertTrue("No permission retrieved (1+2)", permissions3.size() > 0);
		assertTrue("No good permissions size retrieved (1+2) expected "+dataIdsCtx.size()+" but was "+permissions3.size(), permissions3.size() == dataIdsCtx.size());
		assertNotNull("No permission decision retrieved (1+2 -> 1)", permissions3.get(0).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 1)", Decision.DENY.name(), permissions3.get(0).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions1 (1+2 -> 1)", permissions1.get(0).getDecision().name(), permissions3.get(0).getDecision().name());
		assertNotNull("No permission decision retrieved (1+2 -> 2)", permissions3.get(1).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 2)", Decision.DENY.name(), permissions3.get(1).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions2 (1+2 -> 2)", permissions2.get(0).getDecision().name(), permissions3.get(1).getDecision().name());
	}
	
	@Test
	@Rollback(true)
	public void testCheckPermissionRequestorCisErrorMix() {
		String testTitle = new String("testCheckPermissionRequestorCisErrorMix: the requestor is a CIS, previously added permission with a CSS requestor and one other with requestor");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		boolean dataDeleted1 = false;
		boolean dataDeleted2 = false;
		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		List<ResponseItem> permissions3 = null;
		List<Action> actions = null;
		try {
			actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			dataDeleted1 = privacyDataManagerInternal.deletePermissions(requestor, dataId1, actions);
			dataDeleted2 = privacyDataManagerInternal.deletePermissions(requestor, dataId2, actions);
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestor, dataId1, actions, decision);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestorCis, dataId2, actions, decision);
			permissions1 = privacyDataManager.checkPermission(requestorCis, dataId1, actions);
			permissions2 = privacyDataManager.checkPermission(requestorCis, dataId2, actions);
			assertTrue("Expected 2 (before)", dataIdsCtx.size() == 2);
			permissions3 = privacyDataManager.checkPermission(requestorCis, dataIdsCtx, actions);
			assertTrue("Expected 2 (after)", dataIdsCtx.size() == 2);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data permission not deleted (1)", dataDeleted1);
		assertTrue("Data permission not deleted (2)", dataDeleted2);
		assertTrue("Data permission not updated (1)", dataUpdated1);
		assertTrue("Data permission not updated (2)", dataUpdated2);
		// DataId1
		assertNotNull("Empty permission should not be retrieved (1)", permissions1);
		assertTrue("No permission retrieved (1)", permissions1.size() > 0);
		assertNotNull("No permission decision retrieved (1)", permissions1.get(0).getDecision());
		assertEquals("Bad permission retrieved (1)", Decision.DENY.name(), permissions1.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (1)", ActionUtils.equal(new ArrayList<Action>(), permissions1.get(0).getRequestItem().getActions()));
		// DataId2
		assertNotNull("Empty permission retrieved (2)", permissions2);
		assertTrue("No permission retrieved (2)", permissions2.size() > 0);
		assertNotNull("No permission decision retrieved (2)", permissions2.get(0).getDecision());
		assertEquals("Bad permission retrieved (2)", Decision.PERMIT.name(), permissions2.get(0).getDecision().name());
		assertTrue("Same actions should be retrieved (2)", ActionUtils.equal(actions, permissions2.get(0).getRequestItem().getActions()));
		// DataId1 + DataId2
		assertNotNull("Empty permission retrieved (1+2)", permissions3);
		assertTrue("No permission retrieved (1+2)", permissions3.size() > 0);
		assertTrue("No good permissions size retrieved (1+2) expected "+dataIdsCtx.size()+" but was "+permissions3.size(), permissions3.size() == dataIdsCtx.size());
		LOG.info("Permissions retrieved for dataIdsCtx: "+ResponseItemUtils.toString(permissions3));
		assertNotNull("No permission decision retrieved (1+2 -> 1)", permissions3.get(0).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 1)", Decision.DENY.name(), ResponseItemUtils.findResponseItem(dataId1, permissions3).get(0).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions1 (1+2 -> 1)", permissions1.get(0).getDecision().name(),ResponseItemUtils.findResponseItem(dataId1, permissions3).get(0).getDecision().name());
		assertNotNull("No permission decision retrieved (1+2 -> 2)", permissions3.get(1).getDecision());
		assertEquals("Bad permission retrieved (1+2 -> 2)", Decision.PERMIT.name(), ResponseItemUtils.findResponseItem(dataId2, permissions3).get(0).getDecision().name());
		assertEquals("Bad permission retrieved compared to permissions2 (1+2 -> 2)", permissions2.get(0).getDecision().name(), ResponseItemUtils.findResponseItem(dataId2, permissions3).get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyDeleted() {
		String testTitle = new String("testCheckPermissionPreviouslyDeleted: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, dataId1);
			permissions = privacyDataManager.checkPermission(requestor, dataId1, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString((permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyDeletedRequestorCis() {
		String testTitle = new String("testCheckPermissionPreviouslyDeletedRequestorCis: permission previously deleted, it is sure that it doesn't exist. The requestor is a CIS.");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			actions.add(ActionUtils.create(ActionConstants.WRITE, true));
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions, Decision.PERMIT);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId1);
			permissions = privacyDataManager.checkPermission(requestorCis, dataId1, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString((permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}

	/* --- CHECK PERMISSION CIS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyAdded() {
		String testTitle = new String("testCheckPermissionCisPreviouslyAdded: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, cisDataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString((permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyDeleted() {
		String testTitle = new String("testCheckPermissionCisPreviouslyDeleted: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, cisDataId);
			permissions = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		assertTrue("Data permission not updated", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString((permissions)));
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
