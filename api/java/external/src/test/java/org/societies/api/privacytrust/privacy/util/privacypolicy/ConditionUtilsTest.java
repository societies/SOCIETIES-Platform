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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class ConditionUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(ConditionUtilsTest.class.getName());

	@Test
	public void testEqual() {
		String testTitle = "Equal";
		LOG.info("[Test] "+testTitle);
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


	@Test
	public void testContainAllMandotory() {
		String testTitle = "Contain all mandatory actions";
		LOG.info("[Test] "+testTitle);
		// NULL
		List<Condition> requestedConditions = null;
		List<Condition> providedConditions = null;
		assertTrue("Null requested conditions means nothing is mandatory", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));

		// Empty
		requestedConditions = new ArrayList<Condition>();
		assertTrue("Empty requested conditions means nothing is mandatory (1/2)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions = new ArrayList<Condition>();
		assertTrue("Empty requested conditions means nothing is mandatory (2/2)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));

		// Only mandatory
		requestedConditions.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		assertFalse("Only Mandatory requested conditions (1/5)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		assertTrue("Only Mandatory requested conditions  (2/5)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		requestedConditions.add(ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_MINUTES, "25"));
		assertFalse("Only Mandatory requested conditions (3/5)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions.add(ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_MINUTES, "25"));
		assertTrue("Only Mandatory requested conditions  (4/5)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions.add(ConditionUtils.create(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA, "1"));
		assertTrue("Only Mandatory requested conditions, even with provided more data (5/5)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));

		// With optional fields
		requestedConditions.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "0", true));
		assertTrue("With option field, it should still match (1/3)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "0", true));
		assertTrue("With option field, it should still match (2/3)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions = new ArrayList<Condition>();
		providedConditions.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "0"));
		providedConditions.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		providedConditions.add(ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_MINUTES, "25"));
//		LOG.debug("Requested conditions:\n"+ConditionUtils.toString(requestedConditions));
//		LOG.debug("Provided conditions:\n"+ConditionUtils.toString(providedConditions));
		assertTrue("With option field, it should still match (3/3)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));

		// Only optional fields
		requestedConditions = new ArrayList<Condition>();
		requestedConditions.add(ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_HOURS, "10", true));
		providedConditions = null;
		assertTrue("Only optional field, it should still match (1/4)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions = new ArrayList<Condition>();
		assertTrue("Only optional field, it should still match (2/4)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions.add(ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_HOURS, "10", true));
		assertTrue("Only optional field, it should still match (3/4)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));
		providedConditions = new ArrayList<Condition>();
		providedConditions.add(ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_MINUTES, "25"));
		providedConditions.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "0", true));
		assertTrue("Only optional field, it should still match (4/4)", ConditionUtils.containAllMandotory(providedConditions, requestedConditions));

	}

	@Test
	public void testGetFriendlyName() {
		String testTitle = "Get friendly name";
		LOG.info("[Test] "+testTitle);
		String expectedFriendlyName = "Shared with the world";
		String actualFriendlyName = ConditionUtils.getFriendlyName(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		assertEquals("Friendly names should be equal", expectedFriendlyName, actualFriendlyName);

		expectedFriendlyName = "Data retention in hours";
		actualFriendlyName = ConditionUtils.getFriendlyName(ConditionUtils.create(ConditionConstants.DATA_RETENTION_IN_HOURS, "1"));
		assertEquals("Friendly names should be equal", expectedFriendlyName, actualFriendlyName);
	}
}
