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
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class DecisionUtilsTest {
	@Test
	public void testEqual() {
		Decision decision1 = null;
		Decision decision2 = null;
		RequestPolicy notDecision = null;
		// -- Null Privacy Policy
		assertTrue("Same null decision should be equal", DecisionUtils.equal(decision1, decision1));
		assertTrue("Null decisions should be equal", DecisionUtils.equal(decision1, decision2));
		assertTrue("Null decision and null object should be equal", DecisionUtils.equal(decision1, notDecision));
		// -- Empty Privacy Policy
		decision1 = Decision.DENY;
		decision2 = Decision.INDETERMINATE;
		notDecision = new RequestPolicy();
		assertTrue("Same empty decision should be equal", DecisionUtils.equal(decision1, decision1));
		// -- Privacy Policy
		assertTrue("Same decision should be equal", DecisionUtils.equal(decision1, decision1));
		assertFalse("Different decisions should be equal", DecisionUtils.equal(decision1, decision2));
		assertFalse("Different decisions should be equal (inverse)", DecisionUtils.equal(decision2, decision1));
		decision2 = Decision.DENY;
		assertTrue("Equal decisions should be equal", DecisionUtils.equal(decision1, decision2));
		assertTrue("Equal decisions should be equal (inverse)", DecisionUtils.equal(decision2, decision1));
	}
}
