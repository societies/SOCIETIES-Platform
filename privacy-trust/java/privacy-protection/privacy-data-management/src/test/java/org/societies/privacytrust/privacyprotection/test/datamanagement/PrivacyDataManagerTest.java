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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.DataIdentifierFactory;
import org.societies.api.identity.DataIdentifierUtil;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Name;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
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
@ContextConfiguration(locations = { "PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerTest.class.getSimpleName());

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
	public void CheckPermissionPreviouslyAdded() {
		String testTitle = new String("CheckPermission: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		ResponseItem permission = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, decision);
			permission = privacyDataManager.checkPermission(requestor, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permission);
		LOG.debug("Permission retrieved: "+permission.toString());
		assertNotNull("No permission decision retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission.getDecision().name());
	}

	@Test
	@Rollback(true)
	public void CheckPermissionPreviouslyAddedRequestorCis() {
		String testTitle = new String("CheckPermission: previously added permission, the requestor is a CIS");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		ResponseItem permission = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, decision);
			permission = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permission);
		LOG.debug("Permission retrieved: "+permission.toString());
		assertNotNull("No permission decision retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission.getDecision().name());
	}

	@Test
	@Rollback(true)
	public void CheckPermissionPreviouslyAddedRequestorCisError() {
		String testTitle = new String("CheckPermission: the requestor is a CIS, previously added permission with a CSS requestor");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		ResponseItem permission = null;
		try {
			Action read = new Action(ActionConstants.READ);
			Action write = new Action(ActionConstants.WRITE);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			actions.add(write);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, decision);
			permission = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permission);
		LOG.debug("Permission retrieved: "+permission.toString());
		assertNotNull("No permission decision retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permission.getDecision().name());
	}

	@Test
	@Rollback(true)
	public void CheckPermissionPreviouslyDeleted() {
		String testTitle = new String("CheckPermission: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		ResponseItem permission = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, dataId);
			permission = privacyDataManager.checkPermission(requestor, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permission);
		LOG.info("Permission retrieved: "+permission.toString());
		assertNotNull("No permission decision retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permission.getDecision().name());
	}
	@Test
	@Rollback(true)
	public void CheckPermissionPreviouslyDeletedRequestorCis() {
		String testTitle = new String("CheckPermission: permission previously deleted, it is sure that it doesn't exist. The requestor is a CIS.");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		boolean dataDeleted = false;
		ResponseItem permission = null;
		try {
			Action read = new Action(ActionConstants.READ);
			Action write = new Action(ActionConstants.WRITE, true);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			actions.add(write);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, Decision.PERMIT);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId);
			permission = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permission);
		LOG.info("Permission retrieved: "+permission.toString());
		assertNotNull("No permission decision retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permission.getDecision().name());
	}

	/* --- CHECK PERMISSION CIS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyAdded() {
		String testTitle = new String("CheckPermissionCis: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		ResponseItem permission = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, cisDataId, actions, decision);
			permission = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permission);
		LOG.info("Permission retrieved: "+permission.toString());
		assertNotNull("No permission decision retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission.getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyDeleted() {
		String testTitle = new String("CheckPermissionCis: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		ResponseItem permission = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, cisDataId);
			permission = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permission);
		LOG.info("Permission retrieved: "+permission.toString());
		assertNotNull("No permission decision retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permission.getDecision().name());
	}

	/* --- OBFUSCATION --- */

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)}.
	 * @throws PrivacyException 
	 */
	@Test
	@Ignore("PrivacyPreferenceManager not ready yet")
	public void testObfuscateData() {
		String testTitle = new String("testObfuscateData");
		LOG.info("[TEST] "+testTitle);
		IDataWrapper<Name> wrapper = DataWrapperFactory.getNameWrapper("Olivier", "Maridat");
		wrapper.setDataId(dataId);
		IDataWrapper<Name> obfuscatedDataWrapper = null;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			Requestor requestor = new Requestor(requestorId);
			Future<IDataWrapper> obfuscatedDataWrapperAsync = privacyDataManager.obfuscateData(requestor, wrapper);
			obfuscatedDataWrapper = obfuscatedDataWrapperAsync.get();
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle+": "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (InterruptedException e) {
			LOG.error("[Test InterruptedException] "+testTitle+": Async interrupted error "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: Async interrupted error "+e.getMessage());
		} catch (ExecutionException e) {
			LOG.error("[Test ExecutionException] "+testTitle+": Async execution error "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: Async execution error "+e.getMessage());
		}

		// Verify
		LOG.info("### Orginal name:\n"+wrapper.getData().toString());
		LOG.info("### Obfuscated name:\n"+obfuscatedDataWrapper.getData().toString());
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)}.
	 */
	@Test
	@Ignore
	public void testHasObfuscatedVersion() {
		String testTitle = new String("testHasObfuscatedVersion");
		LOG.info("[TEST] "+testTitle);
		String actual = "";
		boolean expection = false;
		try {
			actual = privacyDataManager.hasObfuscatedVersion(null, null, null);
		} catch (PrivacyException e) {
			expection = true;
		}
		assertFalse(expection);
		assertNull(actual);
	}


	/* --- Data Id --- */
	@Test
	public void testFromUriString() {
		String testTitle = new String("testFromUriString: multiple test of DataId parsing");
		LOG.info("[TEST] "+testTitle);

		String ownerId = "owner@domain.com";
		String dataId1 = "context://"+ownerId+"/locationSymbolic/";
		String dataId2 = "context://owner@domain.com/locationSymbolic";
		String dataId3 = "context:///locationSymbolic/";
		String dataId4 = "context:///locationSymbolic";
		String dataId5 = "context:///";

		assertNotNull("Data id from "+dataId1+" should not be null", DataIdentifierUtil.fromUri(dataId1));
		assertEquals("Owner id from "+dataId1+" not retrieved", ownerId, DataIdentifierUtil.fromUri(dataId1).getOwnerId());

		assertNotNull("Data id from "+dataId2+" should not be null", DataIdentifierUtil.fromUri(dataId2));
		assertEquals("Owner id from "+dataId2+" not retrieved", ownerId, DataIdentifierUtil.fromUri(dataId2).getOwnerId());

		assertNotNull("Data id from "+dataId3+" should not be null", DataIdentifierUtil.fromUri(dataId3));
		assertEquals("Owner id from "+dataId3+" not retrieved", "", DataIdentifierUtil.fromUri(dataId3).getOwnerId());

		assertNotNull("Data id from "+dataId4+" should not be null", DataIdentifierUtil.fromUri(dataId4));
		assertEquals("Owner id from "+dataId4+" not retrieved", "", DataIdentifierUtil.fromUri(dataId4).getOwnerId());
		assertEquals("Data type from "+dataId4+" not retrieved", "locationSymbolic", DataIdentifierUtil.fromUri(dataId4).getType());

		assertNotNull("Data id from "+dataId5+" should not be null", DataIdentifierUtil.fromUri(dataId5));
		assertEquals("Owner id from "+dataId5+" not retrieved", "", DataIdentifierUtil.fromUri(dataId5).getOwnerId());
		assertEquals("Data type from "+dataId5+" not retrieved", "", DataIdentifierUtil.fromUri(dataId5).getType());
	}
	
	@Test
	public void testSchemes() {
		String testTitle = new String("testSchemes: multiple test on DataIdentifierScheme");
		LOG.info("[TEST] "+testTitle);

		String ownerId = "owner@domain.com";
		String dataId1 = "context://"+ownerId+"/locationSymbolic/";
		String dataId2 = "context://owner@domain.com/locationSymbolic";
		String dataId3 = "context:///locationSymbolic/";
		String dataId4 = "context:///locationSymbolic";
		String dataId5 = "context:///";
		DataIdentifierScheme schemeCtx1 = DataIdentifierScheme.CONTEXT;
		DataIdentifierScheme schemeCtx2 = DataIdentifierScheme.CONTEXT;
		DataIdentifierScheme schemeCis = DataIdentifierScheme.CIS;

		assertEquals("Schemes should be equals", schemeCtx1, schemeCtx2);
		assertEquals("Schemes name should be equals", schemeCtx1.name(), schemeCtx2.name());
		assertTrue("Schemes should not be equals", !schemeCtx1.equals(schemeCis));
		assertTrue("Schemes name should not be equals", !schemeCtx1.name().equals(schemeCis.name()));
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
