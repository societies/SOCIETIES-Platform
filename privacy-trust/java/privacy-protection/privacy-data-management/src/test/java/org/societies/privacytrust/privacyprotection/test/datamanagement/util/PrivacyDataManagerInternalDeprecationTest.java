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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.util.commonmock.MockIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
// Run this test case using Spring jUnit
@RunWith(SpringJUnit4ClassRunner.class)
// Search context configuration file in classpath:<ClassName>-context.xml
@ContextConfiguration(locations = { "../PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerInternalDeprecationTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerInternalDeprecationTest.class.getSimpleName());

	@Autowired
	IPrivacyDataManagerInternal privacyDataManagerInternal;

	// -- Mocked data
	private DataIdentifier dataId;
	private Requestor requestor;
	private Requestor requestorCis;



	@Before
	public void setUp() throws Exception {
		// Requestor
		IIdentity myCssId = new MockIdentity(IdentityType.CSS, "mycss","societies.local");
		IIdentity requestorId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity requestorCisId = new MockIdentity(IdentityType.CIS, "cis-one", "societies.local");
		requestor = new Requestor(requestorId);
		requestorCis = new RequestorCis(requestorId, requestorCisId);

		// Data Id
		try {
			dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId.getJid()+"/ENTITY/person/1/ATTRIBUTE/name/13");
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e.getMessage()+"\n", e);
			fail("setUp(): DataId creation error "+e.getMessage());
		} 
	}

	@After
	public void tearDown() throws Exception {
		//		privacyDataManagerInternal = null;
	}


	@Test(expected = PrivacyException.class)  
	@Rollback(true)
	public void testUpdatePermissionNoActions() throws PrivacyException {
		String testTitle = new String("testUpdatePermission: with an empty action list");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<Action> actions = new ArrayList<Action>();
		Decision permission = Decision.PERMIT;
		dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, permission);
		assertFalse("Data has been updated but should not", dataUpdated);
	}
	@Test
	@Rollback(true)
	public void testUpdatePermission() {
		String testTitle = new String("testUpdatePermission");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.READ));
			Decision permission = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data not updated", dataUpdated);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity)}.
	 */
	@Test
	@Rollback(true)
	public void testUpdatePermission2Times() {
		String testTitle = new String("testUpdatePermission2Times");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.READ));
			Decision permission = Decision.PERMIT;
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, permission);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data not updated", dataUpdated1);
		assertTrue("Data not updated the second time", dataUpdated2);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.identity.IIdentity, org.societies.api.schema.privacytrust.privacy.model.privacypolicy.privacyprotection.model.privacypolicy.ResponseItem)}.
	 * Using ResponseItem
	 */
	@Test
	@Rollback(true)
	public void testUpdatePermissionResponseItem() {
		String testTitle = new String("testUpdatePermissionResponseItem");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			Resource resource = new Resource(dataId);
			RequestItem requestItem = new RequestItem(resource, actions, new ArrayList<Condition>());
			ResponseItem permission = new ResponseItem(requestItem, decision);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data not updated", dataUpdated);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.identity.IIdentity, org.societies.api.schema.privacytrust.privacy.model.privacypolicy.privacyprotection.model.privacypolicy.ResponseItem)}.
	 * Using ResponseItem
	 */
	@Test
	@Rollback(true)
	public void testUpdatePermissionResponseItemDataType() {
		String testTitle = new String("testUpdatePermissionResponseItemDataType: update a privacy permission with data type");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			Resource resource = new Resource(DataIdentifierScheme.CONTEXT,CtxAttributeTypes.LOCATION_SYMBOLIC);
			RequestItem requestItem = new RequestItem(resource, actions, new ArrayList<Condition>());
			ResponseItem permission = new ResponseItem(requestItem, decision);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data has not been updated", dataUpdated);
	}


	@Test
	@Rollback(true)
	public void testGetPermission1Action() {
		String testTitle = new String("testGetPermission: only 1 action");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = null;
		Decision permission = Decision.PERMIT;
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		try {
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, permission);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated", dataUpdated);
		assertNotNull("ResponseItem permission can't be retrieved", responseItems);
		assertTrue("ResponseItem permission can't be retrieved", responseItems.size() > 0);
		assertEquals("Permission result not as expected", permission.name(), responseItems.get(0).getDecision().name());
		assertTrue("Permission action list not as expected", ActionUtils.equal(ActionUtils.toActionBeans(actions), responseItems.get(0).getRequestItem().getActions()));
	}
	
	@Test
	@Rollback(true)
	public void testGetPermission3Actions() {
		String testTitle = new String("testGetPermission: 3 actions with 1 optional");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = null;
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		actions.add(new Action(ActionConstants.WRITE));
		actions.add(new Action(ActionConstants.CREATE, true));
		Decision permission = Decision.PERMIT;
		try {
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, permission);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data not updated", dataUpdated);
		assertNotNull("ResponseItem permission can't be retrieved", responseItems);
		assertTrue("ResponseItem permission can't be retrieved", responseItems.size() > 0);
		assertEquals("Permission result not as expected", permission.name(), responseItems.get(0).getDecision().name());
		assertTrue("Permission action list not as expected", ActionUtils.equal(ActionUtils.toActionBeans(actions), responseItems.get(0).getRequestItem().getActions()));
	}
	

	@Test
	@Rollback(true)
	public void testGetPermission3ActionsInverse() {
		String testTitle = new String("testGetPermission: 3 actions with 1 optional, different order between update and get");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = null;
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(new Action(ActionConstants.WRITE));
		actions1.add(new Action(ActionConstants.READ));
		actions1.add(new Action(ActionConstants.CREATE, true));
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(new Action(ActionConstants.CREATE, true));
		actions2.add(new Action(ActionConstants.READ));
		actions2.add(new Action(ActionConstants.WRITE));
		Decision permission = Decision.PERMIT;
		try {
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions1, permission);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId, actions2);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data not updated", dataUpdated);
		assertNotNull("ResponseItem permission can't be retrieved", responseItems);
		assertTrue("ResponseItem permission can't be retrieved", responseItems.size() > 0);
		assertEquals("Permission result not as expected", permission.name(), responseItems.get(0).getDecision().name());
		assertTrue("Permission action list not as expected", ActionUtils.equal(ActionUtils.toActionBeans(actions1), responseItems.get(0).getRequestItem().getActions()));
	}


	@Test
	@Rollback(true)
	public void testGetPermissionPreviouslyAddedRequestorCisError() {
		String testTitle = new String("testGetPermission: 2 actions, PERMIT for requestor so must be deny for requestorCis");
		LOG.info("[Test] "+testTitle);
		boolean dataDeleted = false;
		boolean dataUpdated = false;
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = null;
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		actions.add(new Action(ActionConstants.WRITE));
		Decision permission = Decision.PERMIT;
		try {
			dataDeleted = privacyDataManagerInternal.deletePermission(requestorCis, dataId, actions);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, permission);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not deleted", dataDeleted);
		assertTrue("Data not updated", dataUpdated);
		assertNull("ResponseItem permission should not be retrieved", responseItems);
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#deletePermissionsorg.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity)}.
	 */
	@Test
	@Rollback(true)
	public void testDeletePermission() {
		String testTitle = new String("testDeletePermission");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		boolean dataDeleted = false;
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = null;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.READ));
			Decision permission = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, permission);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Privacy permission not added", dataUpdated);
		assertTrue("Privacy permission not deleted", dataDeleted);
		assertNull("ResponseItem permission still available", responseItems);
	}


	/* --- Dependency Injection -- */

	public void setPrivacyDataManagerInternal(
			IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
		LOG.info("[Dependency Injection] PrivacyDataManagerInternal injected");
	}
}
