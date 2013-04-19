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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class RequestItemUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(RequestItemUtilsTest.class.getName());
	
	@Test
	public void testEqual() {
		String testTitle = "Equal";
		LOG.info("[Test] "+testTitle);
		
		RequestItem requestItem1 = null;
		RequestItem requestItem2 = null;
		Action notPrivacyPolicy = null;
		// -- Null
		assertTrue("Same null item should be equal", RequestItemUtils.equal(requestItem1, requestItem1));
		assertTrue("Null items should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
		assertTrue("Null item and null object should be equal", RequestItemUtils.equal(requestItem1, notPrivacyPolicy));
		// -- Empty
		requestItem1 = new RequestItem();
		requestItem2 = new RequestItem();
		notPrivacyPolicy = new Action();
		assertTrue("Same empty item should be equal", RequestItemUtils.equal(requestItem1, requestItem1));
		assertTrue("Empty items should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
		assertTrue("Empty items should be equal (inverse)", RequestItemUtils.equal(requestItem2, requestItem1));
		assertFalse("Empty item and empty object should not be equal", RequestItemUtils.equal(requestItem1, notPrivacyPolicy));
		
//		// -- Privacy Policy
//		RequestorBean requestor = RequestorUtils.create("emma.ict-societies.local", "cis-test.ict-societies.local");
//		List<Condition> conditions1 = new ArrayList<Condition>();
//		conditions1.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
//		List<Condition> conditions2 = new ArrayList<Condition>();
//		conditions2.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
//		conditions2.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "Yes"));
//		requestItem1 = RequestItemUtils.createList(requestor,
//				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
//						ActionUtils.createList(ActionConstants.READ),
//						conditions1),
//						RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES),
//								ActionUtils.createList(ActionConstants.READ),
//								conditions2)
//				);
//		assertTrue("Same item should be equal", RequestItemUtils.equal(requestItem1, requestItem1));
//		assertFalse("Different items should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
//		assertFalse("Different items should be equal (inverse)", RequestItemUtils.equal(requestItem2, requestItem1));
//		requestItem2 = RequestItemUtils.createList(requestor,
//				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
//						ActionUtils.createList(ActionConstants.READ),
//						conditions1)
//				);
//		assertFalse("Different items should be equal (2)", RequestItemUtils.equal(requestItem1, requestItem2));
//		assertFalse("Different items should be equal (inverse) (2)", RequestItemUtils.equal(requestItem2, requestItem1));
//		requestItem2 = RequestItemUtils.createList(requestor,
//				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
//						ActionUtils.createList(ActionConstants.READ),
//						conditions1),
//						RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES),
//								ActionUtils.createList(ActionConstants.READ),
//								conditions2)
//				);
//		assertTrue("Equal items should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
//		assertTrue("Equal items should be equal (inverse)", RequestItemUtils.equal(requestItem2, requestItem1));
//		assertFalse("Privacy policy and object should not be equal", RequestItemUtils.equal(requestItem1, notPrivacyPolicy));
	}
	
	@Test
	public void testEqualList() {
		String testTitle = "Equal: list";
		LOG.info("[Test] "+testTitle);
		
		List<RequestItem> requestItems1 = null;
		List<RequestItem> requestItems2 = null;
		Action notPrivacyPolicy = null;
		// -- Null
		assertTrue("Same null item should be equal", RequestItemUtils.equal(requestItems1, requestItems1));
		assertTrue("Null items should be equal", RequestItemUtils.equal(requestItems1, requestItems2));
		assertTrue("Null item and null object should be equal", RequestItemUtils.equal(requestItems1, notPrivacyPolicy));
		
		// -- Empty
		requestItems1 = new ArrayList<RequestItem>();
		requestItems2 = new ArrayList<RequestItem>();
		notPrivacyPolicy = new Action();
		assertTrue("Same empty item should be equal", RequestItemUtils.equal(requestItems1, requestItems1));
		assertTrue("Empty items should be equal", RequestItemUtils.equal(requestItems1, requestItems2));
		assertTrue("Empty items should be equal (inverse)", RequestItemUtils.equal(requestItems2, requestItems1));
		assertFalse("Empty item and empty object should not be equal", RequestItemUtils.equal(requestItems1, notPrivacyPolicy));
		
		// Filled
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		List<Condition> conditions2 = new ArrayList<Condition>();
		conditions2.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		conditions2.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "Yes"));
		requestItems1.add(RequestItemUtils.create(
				ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ), 
				conditions1));
		assertTrue("Same item should be equal", RequestItemUtils.equal(requestItems1, requestItems1));
		assertFalse("Different items should not be equal", RequestItemUtils.equal(requestItems1, requestItems2));
		assertFalse("Different items should not be equal (inverse)", RequestItemUtils.equal(requestItems2, requestItems1));
		requestItems2.add(RequestItemUtils.create(
				ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ), 
				conditions1));
		assertTrue("Same item should be equal", RequestItemUtils.equal(requestItems1, requestItems2));
		assertTrue("Same item should be equal (inverse)", RequestItemUtils.equal(requestItems2, requestItems1));
	}
}
