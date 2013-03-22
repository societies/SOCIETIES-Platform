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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class RequestPolicyUtilsTest {
	@Test
	public void testEqual() {
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		Action notPrivacyPolicy = null;
		// -- Null Privacy Policy
		assertTrue("Same null privacy policy should be equal", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy1));
		assertTrue("Null privacy policies should be equal", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy2));
		assertTrue("Null privacy policy and null object should be equal", RequestPolicyUtils.equal(privacyPolicy1, notPrivacyPolicy));
		// -- Empty Privacy Policy
		privacyPolicy1 = new RequestPolicy();
		privacyPolicy2 = new RequestPolicy();
		notPrivacyPolicy = new Action();
		assertTrue("Same empty privacy policy should be equal", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy1));
		assertTrue("Empty privacy policies should be equal", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy2));
		assertTrue("Empty privacy policies should be equal (inverse)", RequestPolicyUtils.equal(privacyPolicy2, privacyPolicy1));
		assertFalse("Empty privacy policy and empty object should not be equal", RequestPolicyUtils.equal(privacyPolicy1, notPrivacyPolicy));
		// -- Privacy Policy
		RequestorBean requestor = RequestorUtils.create("emma.ict-societies.local", "cis-test.ict-societies.local");
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		List<Condition> conditions2 = new ArrayList<Condition>();
		conditions2.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		conditions2.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "Yes"));
		privacyPolicy1 = RequestPolicyUtils.createList(requestor,
				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
						ActionUtils.createList(ActionConstants.READ),
						conditions1),
						RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES),
								ActionUtils.createList(ActionConstants.READ),
								conditions2)
				);
		assertTrue("Same privacy policy should be equal", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy1));
		assertFalse("Different privacy policies should be equal", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy2));
		assertFalse("Different privacy policies should be equal (inverse)", RequestPolicyUtils.equal(privacyPolicy2, privacyPolicy1));
		privacyPolicy2 = RequestPolicyUtils.createList(requestor,
				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
						ActionUtils.createList(ActionConstants.READ),
						conditions1)
				);
		assertFalse("Different privacy policies should be equal (2)", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy2));
		assertFalse("Different privacy policies should be equal (inverse) (2)", RequestPolicyUtils.equal(privacyPolicy2, privacyPolicy1));
		privacyPolicy2 = RequestPolicyUtils.createList(requestor,
				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
						ActionUtils.createList(ActionConstants.READ),
						conditions1),
						RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES),
								ActionUtils.createList(ActionConstants.READ),
								conditions2)
				);
		assertTrue("Equal privacy policies should be equal", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy2));
		assertTrue("Equal privacy policies should be equal (inverse)", RequestPolicyUtils.equal(privacyPolicy2, privacyPolicy1));
		assertFalse("Privacy policy and object should not be equal", RequestPolicyUtils.equal(privacyPolicy1, notPrivacyPolicy));
	}
}
