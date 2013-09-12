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
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
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
@RunWith(SpringJUnit4ClassRunner.class)
// Search context configuration file in classpath:<ClassName>-context.xml
@ContextConfiguration(locations = { "PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerInternalTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerInternalTest.class.getSimpleName());

	@Autowired
	IPrivacyDataManagerInternal privacyDataManagerInternal;

	// -- Mocked data
	private DataIdentifier dataId1;
	private DataIdentifier dataId2;
	private RequestorBean requestor;
	private RequestorBean requestorCis;



	@Before
	public void setUp() throws Exception {
		// Requestor
		IIdentity myCssId = new MockIdentity(IdentityType.CSS, "mycss","societies.local");
		IIdentity requestorId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity requestorCisId = new MockIdentity(IdentityType.CIS, "cis-one", "societies.local");
		requestor = RequestorUtils.create(requestorId.getJid());
		requestorCis = RequestorUtils.create(requestorId.getJid(), requestorCisId.getJid());

		// Data Id
		try {
			Random randomer = new Random((new Date()).getTime()); 
			int random1 = randomer.nextInt(200);
			int random2 = randomer.nextInt(200);
			while(random2 == random1) {
				random2 = randomer.nextInt(200);
			}
			dataId1 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId.getJid()+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_FIRST+"/"+random1);
			dataId2 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId.getJid()+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_LAST+"/"+random2);
		}
		catch (Exception e) {
			LOG.error("setUp(): DataId creation error "+e+"\n", e);
			fail("setUp(): DataId creation error "+e);
		} 
	}

	@After
	public void tearDown() {
		try {
			boolean dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, dataId1);
			assertTrue("Data should be deleted (requestor, dataId1)", dataDeleted);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, dataId2);
			assertTrue("Data should be deleted (requestor, dataId2)", dataDeleted);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId1);
			assertTrue("Data should be deleted (requestorCis, dataId1)", dataDeleted);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId2);
			assertTrue("Data should be deleted (requestorCis, dataId2)", dataDeleted);
		}
		catch (Exception e) {
			LOG.error("setUp(): privacy permissions deletion error "+e+"\n", e);
			fail("setUp(): privacy permissions deletion error "+e);
		} 
	}


	/* ************
	 * Update
	 * ********** */

	@Test
	@Rollback(true)
	public void testUpdatePermission() {
		String testTitle = new String("testUpdatePermission: update one time a permission");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision permission = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId1, actions, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated", dataUpdated);
	}

	@Test
	@Rollback(true)
	public void testUpdatePermission2Times() {
		String testTitle = new String("testUpdatePermission2Times: update 2 times the same data permission");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision permission = Decision.PERMIT;
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions, permission);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated", dataUpdated1);
		assertTrue("Data not updated the second time", dataUpdated2);
	}

	@Test
	@Rollback(true)
	public void testUpdatePermissionSeveralData() {
		String testTitle = new String("testUpdatePermissionSeveralData: update 2 data at the same time");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated01 = false;
		boolean dataUpdated02 = false;
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		try {
			List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
			dataIds.add(dataId1);
			dataIds.add(dataId2);
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision1 = Decision.PERMIT;
			List<Decision> decisions = new ArrayList<Decision>();
			decisions.add(Decision.PERMIT);
			decisions.add(Decision.DENY);
			dataUpdated01 = privacyDataManagerInternal.updatePermissions(requestorCis, dataIds, actions, new ArrayList<Decision>());
			dataUpdated02 = privacyDataManagerInternal.updatePermissions(requestorCis, null, actions, decision1);
			dataUpdated1 = privacyDataManagerInternal.updatePermissions(requestorCis, dataIds, actions, decision1);
			dataUpdated2 = privacyDataManagerInternal.updatePermissions(requestorCis, dataIds, actions, decisions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertFalse("Data wrongly updated", dataUpdated01);
		assertFalse("Data wrongly updated", dataUpdated02);
		assertTrue("Data not updated", dataUpdated1);
		assertTrue("Data not updated the second time", dataUpdated1);
		assertTrue("Data not updated", dataUpdated2);
		assertTrue("Data not updated the second time", dataUpdated2);
	}

	@Test
	@Rollback(true)
	public void testUpdatePermissionResponseItem() {
		String testTitle = new String("testUpdatePermissionResponseItem: update using a ResponseItem");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			Resource resource = ResourceUtils.create(dataId1);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, new ArrayList<Condition>());
			ResponseItem permission = ResponseItemUtils.create(decision, requestItem);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated", dataUpdated);
	}

	@Test
	@Rollback(true)
	public void testUpdatePermissionResponseItemDataType() {
		String testTitle = new String("testUpdatePermissionResponseItemDataType: update a privacy permission with data type");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision decision = Decision.PERMIT;
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT,CtxAttributeTypes.LOCATION_SYMBOLIC);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, new ArrayList<Condition>());
			ResponseItem permission = ResponseItemUtils.create(decision, requestItem);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data has not been updated", dataUpdated);
	}

	@Test(expected = PrivacyException.class)  
	@Rollback(true)
	public void testUpdatePermissionNoRequestor() throws PrivacyException {
		String testTitle = new String("testUpdatePermissionNoRequestor: try to update with no requestor");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<Action> actions = new ArrayList<Action>();
		actions.add(ActionUtils.create(ActionConstants.READ));
		Decision permission = Decision.PERMIT;
		dataUpdated = privacyDataManagerInternal.updatePermission(null, dataId1, actions, permission);
		assertFalse("Data has been updated but should not", dataUpdated);
	}

	@Test(expected = PrivacyException.class)  
	@Rollback(true)
	public void testUpdatePermissionNoDataId() throws PrivacyException {
		String testTitle = new String("testUpdatePermissionNoRequestor: try to update with no data id");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<Action> actions = new ArrayList<Action>();
		actions.add(ActionUtils.create(ActionConstants.READ));
		Decision permission = Decision.PERMIT;
		dataUpdated = privacyDataManagerInternal.updatePermission(requestor, null, actions, permission);
		assertFalse("Data has been updated but should not", dataUpdated);
	}

	@Test(expected = PrivacyException.class)  
	@Rollback(true)
	public void testUpdatePermissionNoActions() throws PrivacyException {
		String testTitle = new String("testUpdatePermissionNoActions: try to update with an empty action list");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<Action> actions = new ArrayList<Action>();
		Decision permission = Decision.PERMIT;
		dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId1, actions, permission);
		assertFalse("Data has been updated but should not", dataUpdated);
	}


	@Test(expected = PrivacyException.class)  
	@Rollback(true)
	public void testUpdatePermissionNoDecision() throws PrivacyException {
		String testTitle = new String("testUpdatePermissionNoRequestor: try to update with no data id");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<Action> actions = new ArrayList<Action>();
		actions.add(ActionUtils.create(ActionConstants.READ));
		dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId1, actions, null);
		assertFalse("Data has been updated but should not", dataUpdated);
	}


	/* ************
	 * Get
	 * ********** */

	@Test
	@Rollback(true)
	public void testGetPermission1Action() {
		String testTitle = new String("testGetPermission1Action: only 1 action");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> responseItems = null;
		Decision permission = Decision.PERMIT;
		List<Action> actions = new ArrayList<Action>();
		actions.add(ActionUtils.create(ActionConstants.READ));
		try {
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions, permission);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions);
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
		assertTrue("Permission action list not as expected", ActionUtils.equal(actions, responseItems.get(0).getRequestItem().getActions()));
	}

	@Test
	@Rollback(true)
	public void testGetPermission3Actions() {
		String testTitle = new String("testGetPermission3Actions: 3 actions with 1 optional, then try with a different order in the action list");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> responseItems1 = null;
		List<ResponseItem> responseItems2 = null;
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(ActionUtils.create(ActionConstants.READ));
		actions1.add(ActionUtils.create(ActionConstants.WRITE));
		actions1.add(ActionUtils.create(ActionConstants.CREATE, true));
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(ActionUtils.create(ActionConstants.WRITE));
		actions2.add(ActionUtils.create(ActionConstants.CREATE, true));
		actions2.add(ActionUtils.create(ActionConstants.READ));
		Decision permission = Decision.PERMIT;
		try {
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions1, permission);
			responseItems1 = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions1);
			responseItems2 = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions2);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated", dataUpdated);
		assertNotNull("ResponseItem permission can't be retrieved", responseItems1);
		assertTrue("ResponseItem permission can't be retrieved", responseItems1.size() > 0);
		assertEquals("Permission result not as expected", permission.name(), responseItems1.get(0).getDecision().name());
		assertTrue("Permission action list not as expected", ActionUtils.equal(actions1, responseItems1.get(0).getRequestItem().getActions()));

		// Inverse
		assertNotNull("ResponseItem permission can't be retrieved (inverse)", responseItems2);
		assertTrue("ResponseItem permission can't be retrieved (inverse)", responseItems2.size() > 0);
		assertEquals("Permission result not as expected (inverse)", permission.name(), responseItems2.get(0).getDecision().name());
		assertTrue("Permission action list not as expected (inverse)", ActionUtils.equal(actions1, responseItems2.get(0).getRequestItem().getActions()));
	}


	@Test
	@Rollback(true)
	public void testGetPermissionDeny() {
		String testTitle = new String("testGetPermissionDeny: add permissions DENY/PERMIT with different actions and retrieve them");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		List<ResponseItem> responseItems1 = null;
		List<ResponseItem> responseItems2 = null;
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(ActionUtils.create(ActionConstants.READ));
		actions1.add(ActionUtils.create(ActionConstants.WRITE));
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(ActionUtils.create(ActionConstants.CREATE, true));
		actions2.add(ActionUtils.create(ActionConstants.READ));
		try {
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions1, Decision.DENY);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions2, Decision.PERMIT);
			responseItems1 = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions1);
			responseItems2 = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions2);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated", dataUpdated1);
		assertTrue("Data not updated", dataUpdated2);
		assertNotNull("ResponseItem permission can't be retrieved", responseItems1);
		assertTrue("ResponseItem permission can't be retrieved", responseItems1.size() > 0);
		assertEquals("Permission result not as expected", Decision.DENY.name(), responseItems1.get(0).getDecision().name());
		assertTrue("Permission action list not as expected", ActionUtils.equal(actions1, responseItems1.get(0).getRequestItem().getActions()));

		// Inverse
		assertNotNull("ResponseItem permission can't be retrieved (inverse)", responseItems2);
		assertTrue("ResponseItem permission can't be retrieved (inverse)", responseItems2.size() > 0);
		assertEquals("Permission result not as expected (inverse)", Decision.PERMIT.name(), responseItems2.get(0).getDecision().name());
		assertTrue("Permission action list not as expected (inverse)", ActionUtils.equal(actions2, responseItems2.get(0).getRequestItem().getActions()));
	}

	@Test
	@Rollback(true)
	public void testGetPermission2TimesDifferentActions() {
		String testTitle = new String("testUpdatePermission2TimesDifferentActions: update 2 times the same data permission but with differant actions");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		List<ResponseItem> responseItems1 = new ArrayList<ResponseItem>();
		List<ResponseItem> responseItems2 = new ArrayList<ResponseItem>();
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(ActionUtils.create(ActionConstants.READ));
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(ActionUtils.create(ActionConstants.WRITE));
		Decision decision1 = Decision.PERMIT;
		Decision decision2 = Decision.DENY;
		try {
			dataUpdated1 = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions1, decision1);
			dataUpdated2 = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions2, decision2);
			responseItems1 = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions1);
			responseItems2 = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions2);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated", dataUpdated1);
		assertTrue("Data not updated the second time", dataUpdated2);

		// 1
		assertNotNull("ResponseItem permission can't be retrieved (1)", responseItems1);
		assertTrue("ResponseItem permission can't be retrieved (1)", responseItems1.size() > 0);
		int nbExpected = 1;
		assertTrue("Not the nb expected, expected"+nbExpected+" (1)", responseItems1.size() == nbExpected);
		ResponseItem responseItem = ResponseItemUtils.findResponseItem(dataId1, responseItems1).get(0);
		assertEquals("Permission result not as expected (1)", decision1.name(), responseItem.getDecision().name());
		assertTrue("Permission action list not as expected (1)", ActionUtils.equal(actions1, responseItem.getRequestItem().getActions()));

		// 2
		assertNotNull("ResponseItem permission can't be retrieved (2)", responseItems2);
		assertTrue("ResponseItem permission can't be retrieved (2)", responseItems2.size() > 0);
		assertTrue("Not the nb expected, expected"+nbExpected+" (2)", responseItems2.size() == nbExpected);
		responseItem = ResponseItemUtils.findResponseItem(dataId1, responseItems2).get(0);
		assertEquals("Permission result not as expected (2)", decision2.name(), responseItem.getDecision().name());
		assertTrue("Permission action list not as expected (2)", ActionUtils.equal(actions2, responseItem.getRequestItem().getActions()));
	}

	@Test
	@Rollback(true)
	public void testGetPermissionSeveralData() {
		String testTitle = new String("testGetPermissionSeveralData: store several data with different actions / decisions and retrieve them");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated1 = false;
		boolean dataUpdated2 = false;
		boolean dataDeleted1 = false;
		boolean dataDeleted2 = false;
		List<ResponseItem> responseItems0 = null;
		List<ResponseItem> responseItems1 = null;
		List<ResponseItem> responseItems2 = null;
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(ActionUtils.create(ActionConstants.READ));
		actions1.add(ActionUtils.create(ActionConstants.CREATE, true));
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(ActionUtils.create(ActionConstants.WRITE));
		List<Decision> decisions = new ArrayList<Decision>();
		try {
			List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
			dataIds.add(dataId1);
			dataIds.add(dataId2);
			decisions.add(Decision.DENY);
			decisions.add(Decision.PERMIT);
			dataUpdated1 = privacyDataManagerInternal.updatePermissions(requestorCis, dataIds, actions1, decisions);
			dataUpdated2 = privacyDataManagerInternal.updatePermissions(requestorCis, dataIds, actions2, Decision.PERMIT);
			responseItems0 = privacyDataManagerInternal.getPermissions(requestorCis, dataId1);
			responseItems1 = privacyDataManagerInternal.getPermissions(requestorCis, dataIds, actions1);
			responseItems2 = privacyDataManagerInternal.getPermissions(requestorCis, dataIds, actions2);
			dataDeleted1 = privacyDataManagerInternal.deletePermissions(requestor, dataId1);
			dataDeleted2 = privacyDataManagerInternal.deletePermissions(requestor, dataId2);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data not updated 1", dataUpdated1);
		assertTrue("Data not updated 2", dataUpdated2);
		assertTrue("Data not deleted 1", dataDeleted1);
		assertTrue("Data not deleted 2", dataDeleted2);
		//0
		assertNotNull("ResponseItem permission can't be retrieved (0)", responseItems0);
		assertTrue("ResponseItem permission can't be retrieved (0)", responseItems0.size() > 0);
		List<ResponseItem> responseItems = ResponseItemUtils.findResponseItem(dataId1, responseItems0);
		LOG.info("getPermissions for all actions: "+ResponseItemUtils.toString(responseItems));
		if (ActionUtils.equal(actions1, responseItems.get(0).getRequestItem().getActions())) {
			assertEquals("Permission result not as expected (0)", decisions.get(0).name(), responseItems.get(0).getDecision().name());
			assertEquals("Permission result not as expected (0)", Decision.PERMIT.name(), responseItems.get(1).getDecision().name());
		}
		else if (ActionUtils.equal(actions2, responseItems.get(0).getRequestItem().getActions())) {
			assertEquals("Permission result not as expected (0)", decisions.get(0).name(), responseItems.get(1).getDecision().name());
			assertEquals("Permission result not as expected (0)", Decision.PERMIT.name(), responseItems.get(0).getDecision().name());
		}
		else {
			fail("Should match the actions");
		}

		// 1
		assertNotNull("ResponseItem permission can't be retrieved (1)", responseItems1);
		assertTrue("ResponseItem permission can't be retrieved (1)", responseItems1.size() > 0);
		LOG.info("getPermissions for one data+actions1 (1): "+ResponseItemUtils.toString(responseItems1));
		responseItems = ResponseItemUtils.findResponseItem(dataId1, responseItems1);
		assertNotNull("This data id should have been retrieved "+DataIdentifierUtils.toUriString(dataId1)+" (1)", responseItems);
		ResponseItem responseItem = responseItems.get(0);
		assertEquals("Permission result not as expected (1)", decisions.get(0).name(), responseItem.getDecision().name());
		assertTrue("Permission action list not as expected (1)", ActionUtils.equal(actions1, responseItem.getRequestItem().getActions()));
		responseItem = ResponseItemUtils.findResponseItem(dataId2, responseItems1).get(0);
		assertEquals("Permission result not as expected (1)", decisions.get(1).name(), responseItem.getDecision().name());
		assertTrue("Permission action list not as expected (1)", ActionUtils.equal(actions1, responseItem.getRequestItem().getActions()));

		// 2
		assertNotNull("ResponseItem permission can't be retrieved (2)", responseItems2);
		assertTrue("ResponseItem permission can't be retrieved (2)", responseItems2.size() > 0);
		responseItem = ResponseItemUtils.findResponseItem(dataId1, responseItems2).get(0);
		assertEquals("Permission result not as expected (2)", Decision.PERMIT.name(), responseItem.getDecision().name());
		assertTrue("Permission action list not as expected (2)", ActionUtils.equal(actions2, responseItem.getRequestItem().getActions()));
		responseItem = ResponseItemUtils.findResponseItem(dataId2, responseItems2).get(0);
		assertEquals("Permission result not as expected (2)", Decision.PERMIT.name(), responseItem.getDecision().name());
		assertTrue("Permission action list not as expected (2)", ActionUtils.equal(actions2, responseItem.getRequestItem().getActions()));
	}

	@Test
	@Rollback(true)
	public void testGetPermissionPreviouslyAddedRequestorCisError() {
		String testTitle = new String("testGetPermissionPreviouslyAddedRequestorCisError: 2 actions, PERMIT for requestor so must be DENY for requestorCis");
		LOG.info("[Test] "+testTitle);
		boolean dataDeleted = false;
		boolean dataUpdated = false;
		List<ResponseItem> responseItems = null;
		List<Action> actions = new ArrayList<Action>();
		actions.add(ActionUtils.create(ActionConstants.READ));
		actions.add(ActionUtils.create(ActionConstants.WRITE));
		Decision permission = Decision.PERMIT;
		try {
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId1, actions);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId1, actions, permission);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions);
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



	/* ************
	 * Delete
	 * ********** */

	@Test
	@Rollback(true)
	public void testDeletePermission() {
		String testTitle = new String("testDeletePermission");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		boolean dataDeleted = false;
		List<ResponseItem> responseItems = null;
		try {
			List<Action> actions = new ArrayList<Action>();
			actions.add(ActionUtils.create(ActionConstants.READ));
			Decision permission = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId1, actions, permission);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId1);
			responseItems = privacyDataManagerInternal.getPermissions(requestorCis, dataId1, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
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
