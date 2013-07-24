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
package org.societies.privacytrust.privacyprotection.test.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.model.CisAttributeTypes;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.model.PrivacyPermission;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPermissionTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPermissionTest.class.getSimpleName());

	public PrivacyPermission privacyPermission;
	public static List<Action> actions0;
	public static List<Action> actions1;
	public static List<Action> actions2;

	@BeforeClass
	public static void setUpClass() {
		actions0 = new ArrayList<Action>();

		actions1 = new ArrayList<Action>();
		actions1.add(ActionUtils.create(ActionConstants.READ));

		actions2 = new ArrayList<Action>();
		actions2.add(ActionUtils.create(ActionConstants.READ, true));
		actions2.add(ActionUtils.create(ActionConstants.WRITE, false));
	}

	@Before
	public void setUp() {
		privacyPermission = new PrivacyPermission();
	}

	@After
	public void tearDown() {
		privacyPermission = null;
	}


	@Test
	public void testSet1Action() {
		String testTitle = new String("testSet1Action");
		LOG.info("[Test] "+testTitle);

		// -- 1 action in the list
		LOG.info("Expected actions:");
		for(Action action : actions1) {
			LOG.info(action.getActionConstant().name()+":"+action.isOptional());
		}
		privacyPermission.setActionsToData(actions1);
		LOG.info("Setted actions: "+privacyPermission.getActions());
		List<Action> retrievedActions1 = privacyPermission.getActionsFromData();
		LOG.info("Retrieved actions: ");
		for(Action action : retrievedActions1) {
			LOG.info(action.getActionConstant().name()+":"+action.isOptional());
		}
		assertTrue("Expected same but are not", ActionUtils.equal(actions1, retrievedActions1));
	}

	@Test
	public void testSet2Actions() {
		String testTitle = new String("testSet2Actions");
		LOG.info("[Test] "+testTitle);
		// -- 1 action in the list
		LOG.info("Expected actions:");
		for(Action action : actions2) {
			LOG.info(action.getActionConstant().name()+":"+action.isOptional());
		}
		privacyPermission.setActionsToData(actions2);
		LOG.info("Setted actions: "+privacyPermission.getActions());
		LOG.info("Setted action optional flags: "+privacyPermission.getActionOptionalFlags());
		int posOptional = 0;
		int endOptional = privacyPermission.getActionOptionalFlags().indexOf('/', posOptional);
		assertTrue("Should be true but string was "+privacyPermission.getActionOptionalFlags().substring(posOptional, endOptional), "true".equals(privacyPermission.getActionOptionalFlags().substring(posOptional, endOptional)));
		List<Action> retrievedActions2 = privacyPermission.getActionsFromData();
		LOG.info("Retrieved actions: ");
		for(Action action : retrievedActions2) {
			LOG.info(action.getActionConstant().name()+":"+action.isOptional());
		}
		assertTrue("Expected same but are not", ActionUtils.equal(actions2, retrievedActions2));
	}

	@Test
	public void testSet0Action() {
		String testTitle = new String("testSet0Action");
		LOG.info("[Test] "+testTitle);

		// -- 1 action in the list
		LOG.info("Expected actions:");
		for(Action action : actions0) {
			LOG.info(action.getActionConstant().name()+":"+action.isOptional());
		}
		privacyPermission.setActionsToData(actions0);
		LOG.info("Setted actions: "+privacyPermission.getActions());
		List<Action> retrievedActions0 = privacyPermission.getActionsFromData();
		LOG.info("Retrieved actions: ");
		for(Action action : retrievedActions0) {
			LOG.info(action.getActionConstant().name()+":"+action.isOptional());
		}
		assertTrue("Expected same but are not", ActionUtils.equal(actions0, retrievedActions0));
	}
	
	@Test
	public void testNotNominalCases() {
		String testTitle = new String("testNotNominalCases");
		LOG.info("[Test] "+testTitle);
		
		assertNull("Should be null", PrivacyPermission.createResponseItems(null));
		assertNull("Should be null 2", PrivacyPermission.createResponseItems(new ArrayList<PrivacyPermission>()));
		
		PrivacyPermission internalPermission = new PrivacyPermission(RequestorUtils.create("myCss.societies.local"), null, new ArrayList<Action>(), Decision.PERMIT);
		ResponseItem responseItem = internalPermission.createResponseItem();
		assertNull("Resource should be null", responseItem.getRequestItem().getResource());
		
		internalPermission = new PrivacyPermission("myCss.societies.local", DataIdentifierScheme.CIS+"://myCss.societies.local/"+CisAttributeTypes.MEMBER_LIST, "read/write/", "false/false/", Decision.PERMIT);
		responseItem = internalPermission.createResponseItem();
		assertNotNull("Resource should not be null", responseItem.getRequestItem().getResource());
		List<Action> expectedActions = new ArrayList<Action>();
		expectedActions.add(ActionUtils.create(ActionConstants.READ));
		expectedActions.add(ActionUtils.create(ActionConstants.WRITE));
		List<Action> retrievedActions0 = internalPermission.getActionsFromData();
		LOG.info("Retrieved actions: ");
		for(Action action : retrievedActions0) {
			LOG.info(action.getActionConstant().name()+":"+action.isOptional());
		}
		assertTrue("Expected same but are not", ActionUtils.equal(expectedActions, retrievedActions0));
	}
}
