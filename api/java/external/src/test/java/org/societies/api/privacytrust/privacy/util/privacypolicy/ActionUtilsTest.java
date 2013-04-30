/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class ActionUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(ActionUtilsTest.class.getName());
	
	@Test
	public void testEqual() {
		String testTitle = "Equal";
		LOG.info("[Test] "+testTitle);
		Action action1 = null;
		Action action2 = null;
		RequestPolicy notAction = null;
		// -- Null Privacy Policy
		assertTrue("Same null action should be equal", ActionUtils.equal(action1, action1));
		assertTrue("Null actions should be equal", ActionUtils.equal(action1, action2));
		assertTrue("Null action and null object should be equal", ActionUtils.equal(action1, notAction));
		// -- Empty Privacy Policy
		action1 = new Action();
		action2 = new Action();
		notAction = new RequestPolicy();
		assertTrue("Same empty action should be equal", ActionUtils.equal(action1, action1));
		assertTrue("Empty actions should be equal", ActionUtils.equal(action1, action2));
		assertTrue("Empty actions should be equal (inverse)", ActionUtils.equal(action2, action1));
		assertFalse("Empty action and empty object should not be equal", ActionUtils.equal(action1, notAction));
		// -- Privacy Policy
		action1 = ActionUtils.create(ActionConstants.READ);
		assertTrue("Same action should be equal", ActionUtils.equal(action1, action1));
		assertFalse("Different actions should be equal", ActionUtils.equal(action1, action2));
		assertFalse("Different actions should be equal (inverse)", ActionUtils.equal(action2, action1));
		action2 = ActionUtils.create(ActionConstants.WRITE);
		assertFalse("Different actions should not be equal (2)", ActionUtils.equal(action1, action2));
		assertFalse("Different actions should not be equal (inverse) (2)", ActionUtils.equal(action2, action1));
		action2 = ActionUtils.create(ActionConstants.READ, true);
		assertFalse("Different actions should not be equal (2)", ActionUtils.equal(action1, action2));
		assertFalse("Different actions should not be equal (inverse) (2)", ActionUtils.equal(action2, action1));
		action2 = ActionUtils.create(ActionConstants.READ);
		assertTrue("Equal actions should be equal", ActionUtils.equal(action1, action2));
		assertTrue("Equal actions should be equal (inverse)", ActionUtils.equal(action2, action1));
		assertFalse("action and object should not be equal", ActionUtils.equal(action1, notAction));
	}
	
	@Test
	public void testContainAllMandotory() {
		String testTitle = "Contain all mandatory actions";
		LOG.info("[Test] "+testTitle);
		// NULL
		List<Action> requestedActions = null;
		List<Action> providedActions = null;
		assertTrue("Null requested actions means nothing is mandatory", ActionUtils.containAllMandotory(providedActions, requestedActions));
		
		// Empty
		requestedActions = new ArrayList<Action>();
		assertTrue("Empty requested actions means nothing is mandatory (1/2)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions = new ArrayList<Action>();
		assertTrue("Empty requested actions means nothing is mandatory (2/2)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		
		// Only mandatory
		requestedActions = ActionUtils.createList(ActionConstants.READ);
		assertFalse("Only Mandatory requested actions (1/2)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions = ActionUtils.createList(ActionConstants.READ);
		assertTrue("Empty requested actions means nothing is mandatory (2/4)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		requestedActions = ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE);
		assertFalse("Only Mandatory requested actions (3/4)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions = ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE);
		assertTrue("Empty requested actions means nothing is mandatory (4/4)", ActionUtils.containAllMandotory(providedActions, requestedActions));

		// With optional fields
		requestedActions.add(ActionUtils.create(ActionConstants.DELETE, true));
		assertTrue("With option field, it should still match (1/3)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions.add(ActionUtils.create(ActionConstants.DELETE, true));
		assertTrue("With option field, it should still match (2/3)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions = ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE, ActionConstants.DELETE);
		assertTrue("Empty requested actions means nothing is mandatory (3/3)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		
		// Only optional fields
		requestedActions = new ArrayList<Action>();
		requestedActions.add(ActionUtils.create(ActionConstants.DELETE, true));
		providedActions = null;
		assertTrue("Only optional field, it should still match (1/4)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions = new ArrayList<Action>();
		assertTrue("Only optional field, it should still match (2/4)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions.add(ActionUtils.create(ActionConstants.DELETE, true));
		assertTrue("Only optional field, it should still match (3/4)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		providedActions = ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE, ActionConstants.DELETE);
		assertTrue("Only optional field, it should still match (4/4)", ActionUtils.containAllMandotory(providedActions, requestedActions));
		
	}
	
	@Test
	public void testGetFriendlyName() {
		String testTitle = "Get friendly name";
		LOG.info("[Test] "+testTitle);
		String expectedFriendlyName = "access";
		String actualFriendlyName = ActionUtils.getFriendlyName(ActionUtils.create(ActionConstants.READ));
		assertEquals("Friendly names should be equal", expectedFriendlyName, actualFriendlyName);
		
		String expectedFriendlyDescription1 = "access";
		String actualFriendlyDescription1 = ActionUtils.getFriendlyDescription(ActionUtils.createList(ActionConstants.READ));
		assertEquals("Friendly descriptions 1 should be equal", expectedFriendlyDescription1, actualFriendlyDescription1);
		
		String expectedFriendlyDescription2 = "access and update";
		String actualFriendlyDescription2 = ActionUtils.getFriendlyDescription(ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE));
		assertEquals("Friendly descriptions 2 should be equal", expectedFriendlyDescription2, actualFriendlyDescription2);
		
		String expectedFriendlyDescription3 = "access, update and delete";
		String actualFriendlyDescription3 = ActionUtils.getFriendlyDescription(ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE, ActionConstants.DELETE));
		assertEquals("Friendly descriptions 3 should be equal", expectedFriendlyDescription3, actualFriendlyDescription3);
	}
}
