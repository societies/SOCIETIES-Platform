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
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
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
// Run this test case using Spring jUnit
@RunWith(SpringJUnit4ClassRunner.class)
// Search context configuration file in classpath:<ClassName>-context.xml
@ContextConfiguration(locations = { "PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerInternalTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerInternalTest.class.getSimpleName());
	
	@Autowired
	IPrivacyDataManagerInternal privacyDataManagerInternal;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
//		privacyDataManagerInternal = null;
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity)}.
	 */
	@Test
	@Rollback(true)
	public void testGetPermission() {
		String testTitle = new String("testGetPermission");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		ResponseItem responseItem = null;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			Requestor requestor = new Requestor(requestorId);
			IIdentity ownerId = Mockito.mock(IIdentity.class);
			Mockito.when(ownerId.getJid()).thenReturn("me@societies.local");
			CtxIdentifier dataId = Mockito.mock(CtxIdentifier.class);
			Mockito.when(dataId.getUri()).thenReturn("john@societies.local/ENTITY/person/1/ATTRIBUTE/name/13");
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.READ));
			Decision permission = Decision.PERMIT;
			if (null == privacyDataManagerInternal) {
				LOG.info("privacyDataManagerInternal null");
			}
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, ownerId, dataId, actions, permission);
			responseItem = privacyDataManagerInternal.getPermission(requestor, ownerId, dataId);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data not updated", dataUpdated);
		assertNotNull("ResponseItem permission can't be retrieved", responseItem);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity)}.
	 */
	@Test
	@Rollback(true)
	public void testUpdatePermission() {
		String testTitle = new String("testUpdatePermission");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			Requestor requestor = new Requestor(requestorId);
			IIdentity ownerId = Mockito.mock(IIdentity.class);
			Mockito.when(ownerId.getJid()).thenReturn("me@societies.local");
			CtxIdentifier dataId = Mockito.mock(CtxIdentifier.class);
			Mockito.when(dataId.getUri()).thenReturn("john@societies.local/ENTITY/person/1/ATTRIBUTE/name/13");
			List<Action> actions = new ArrayList<Action>();
			Decision permission = Decision.PERMIT;
			if (null == privacyDataManagerInternal) {
				LOG.info("privacyDataManagerInternal null");
			}
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, ownerId, dataId, actions, permission);
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
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.identity.IIdentity, org.societies.api.privacytrust.privacyprotection.model.privacypolicy.ResponseItem)}.
	 * Using ResponseItem
	 */
	@Test
	@Rollback(true)
	public void testUpdatePermissionResponseItem() {
		String testTitle = new String("testUpdatePermissionResponseItem");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			Requestor requestor = new Requestor(requestorId);
			IIdentity ownerId = Mockito.mock(IIdentity.class);
			Mockito.when(ownerId.getJid()).thenReturn("me@societies.local");
			CtxAttributeIdentifier dataId = Mockito.mock(CtxAttributeIdentifier.class);
			Mockito.when(dataId.getUri()).thenReturn("john@societies.local/ENTITY/person/1/ATTRIBUTE/name/13");
			List<Action> actions = new ArrayList<Action>();
			Decision decision = Decision.PERMIT;
			Resource resource = new Resource(dataId);
			RequestItem requestItem = new RequestItem(resource, actions, new ArrayList<Condition>());
			ResponseItem permission = new ResponseItem(requestItem, decision);
			if (null == privacyDataManagerInternal) {
				LOG.info("privacyDataManagerInternal null");
			}
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, ownerId, permission);
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
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManagerInternal#updatePermissions(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.identity.IIdentity, org.societies.api.privacytrust.privacyprotection.model.privacypolicy.ResponseItem)}.
	 * Using ResponseItem
	 */
	@Test
	@Rollback(true)
	public void testUpdatePermissionResponseItemDataType() {
		String testTitle = new String("testUpdatePermissionResponseItemDataType: update a privacy permission with data type");
		LOG.info("[Test] "+testTitle);
		boolean dataUpdated = false;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			IIdentity requestorCisId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorCisId.getJid()).thenReturn("cis.societies.local");
			RequestorCis requestor = new RequestorCis(requestorId, requestorCisId);
			IIdentity ownerId = Mockito.mock(IIdentity.class);
			Mockito.when(ownerId.getJid()).thenReturn("me@societies.local");
			List<Action> actions = new ArrayList<Action>();
			Decision decision = Decision.PERMIT;
			Resource resource = new Resource("locationSymbolic");
			RequestItem requestItem = new RequestItem(resource, actions, new ArrayList<Condition>());
			ResponseItem permission = new ResponseItem(requestItem, decision);
			if (null == privacyDataManagerInternal) {
				LOG.info("privacyDataManagerInternal null");
			}
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, ownerId, permission);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data has not been updated", dataUpdated);
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
		ResponseItem responseItem = null;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			Requestor requestor = new Requestor(requestorId);
			IIdentity ownerId = Mockito.mock(IIdentity.class);
			Mockito.when(ownerId.getJid()).thenReturn("me@societies.local");
			CtxIdentifier dataId = Mockito.mock(CtxIdentifier.class);
			Mockito.when(dataId.getUri()).thenReturn("me@societies.local/ENTITY/person/1/ATTRIBUTE/name/13");
			List<Action> actions = null;
			Decision permission = Decision.PERMIT;
			if (null == privacyDataManagerInternal) {
				LOG.info("privacyDataManagerInternal null");
			}
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, ownerId, dataId, actions, permission);
			dataDeleted = privacyDataManagerInternal.deletePermission(requestor, ownerId, dataId);
			responseItem = privacyDataManagerInternal.getPermission(requestor, ownerId, dataId);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Privacy permission not added", dataUpdated);
		assertTrue("Privacy permission not deleted", dataDeleted);
		assertNull("ResponseItem permission still available", responseItem);
	}

	
	public void setPrivacyDataManagerInternal(
			IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
		LOG.info("[Dependency Injection] PrivacyDataManagerInternal injected");
	}
	
}
