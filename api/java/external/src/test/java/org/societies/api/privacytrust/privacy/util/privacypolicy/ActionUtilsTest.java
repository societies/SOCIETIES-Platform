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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class ActionUtilsTest {
	@Test
	public void testEqual() {
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
}
