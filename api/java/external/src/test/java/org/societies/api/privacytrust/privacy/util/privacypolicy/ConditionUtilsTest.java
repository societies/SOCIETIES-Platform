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
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class ConditionUtilsTest {
	@Test
	public void testEqual() {
		Condition condition1 = null;
		Condition condition2 = null;
		RequestPolicy notCondition = null;
		// -- Null Privacy Policy
		assertTrue("Same null condition should be equal", ConditionUtils.equal(condition1, condition1));
		assertTrue("Null conditions should be equal", ConditionUtils.equal(condition1, condition2));
		assertTrue("Null condition and null object should be equal", ConditionUtils.equal(condition1, notCondition));
		// -- Empty Privacy Policy
		condition1 = new Condition();
		condition2 = new Condition();
		notCondition = new RequestPolicy();
		assertTrue("Same empty condition should be equal", ConditionUtils.equal(condition1, condition1));
		assertTrue("Empty conditions should be equal", ConditionUtils.equal(condition1, condition2));
		assertTrue("Empty conditions should be equal (inverse)", ConditionUtils.equal(condition2, condition1));
		assertFalse("Empty condition and empty object should not be equal", ConditionUtils.equal(condition1, notCondition));
		// -- Privacy Policy
		condition1 = ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes");
		assertTrue("Same condition should be equal", ConditionUtils.equal(condition1, condition1));
		assertFalse("Different conditions should be equal", ConditionUtils.equal(condition1, condition2));
		assertFalse("Different conditions should be equal (inverse)", ConditionUtils.equal(condition2, condition1));
		condition2 = ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_HOURS, "Yes");
		assertFalse("Different conditions should be equal (2)", ConditionUtils.equal(condition1, condition2));
		assertFalse("Different conditions should be equal (inverse) (2)", ConditionUtils.equal(condition2, condition1));
		condition2 = ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes", true);
		assertFalse("Different conditions should be equal (2)", ConditionUtils.equal(condition1, condition2));
		assertFalse("Different conditions should be equal (inverse) (2)", ConditionUtils.equal(condition2, condition1));
		condition2 = ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes");
		assertTrue("Equal conditions should be equal", ConditionUtils.equal(condition1, condition2));
		assertTrue("Equal conditions should be equal (inverse)", ConditionUtils.equal(condition2, condition1));
		assertFalse("condition and object should not be equal", ConditionUtils.equal(condition1, notCondition));
	}
}
