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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class RequestItemUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(RequestItemUtilsTest.class.getName());

	@Test
	public void testBean2Java() {
		// - Data
		Resource resource1 = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION);
		org.societies.api.privacytrust.privacy.model.privacypolicy.Resource resource2 = new org.societies.api.privacytrust.privacy.model.privacypolicy.Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION);

		List<Action> actions1 = ActionUtils.createList(ActionConstants.READ);
		List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions2 = new ArrayList<org.societies.api.privacytrust.privacy.model.privacypolicy.Action>();
		org.societies.api.privacytrust.privacy.model.privacypolicy.Action action2 = new org.societies.api.privacytrust.privacy.model.privacypolicy.Action(org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants.READ);
		actions2.add(action2);

		List<Condition> conditions1 = new ArrayList<Condition>();
		Condition condition1 = ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "1");
		conditions1.add(condition1);
		List<org.societies.api.privacytrust.privacy.model.privacypolicy.Condition> conditions2 = new ArrayList<org.societies.api.privacytrust.privacy.model.privacypolicy.Condition>();
		org.societies.api.privacytrust.privacy.model.privacypolicy.Condition condition2 = new org.societies.api.privacytrust.privacy.model.privacypolicy.Condition(org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants.MAY_BE_INFERRED, "1", false);
		conditions2.add(condition2);
		// Test Condition
		assertTrue("Different condition constant type should be equals", ConditionConstantsUtils.equal(condition1.getConditionConstant(), ConditionUtils.toConditionBean(condition2).getConditionConstant()));
		assertTrue("Different condition constant type should be equals (inverse)", condition2.getConditionName().equals(ConditionUtils.toCondition(condition1).getConditionName()));
		assertTrue("Different condition value type should be equals", StringUtils.equals(condition1.getValue(), ConditionUtils.toConditionBean(condition2).getValue()));
		assertTrue("Different condition value type should be equals (inverse)", StringUtils.equals(condition2.getValue(), ConditionUtils.toCondition(condition1).getValue()));
		assertTrue("Different condition optional type should be equals (direct)", condition2.isOptional() == condition1.isOptional());
		assertTrue("Different condition optional type should be equals", condition1.isOptional() == ConditionUtils.toConditionBean(condition2).isOptional());
		assertTrue("Different condition optional type should be equals (inverse)", condition2.isOptional() == ConditionUtils.toCondition(condition1).isOptional());
		assertTrue("Different condition type should be equals", ConditionUtils.equal(condition1, ConditionUtils.toConditionBean(condition2)));
		assertTrue("Different condition type should be equals (inverse)", condition2.equals(ConditionUtils.toCondition(condition1)));

		// -- Tests
		// - Equal RequestItem
		RequestItem requestItem1 = RequestItemUtils.create(resource1, actions1, conditions1);
		org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem requestItem2 = new org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem(resource2, actions2, conditions2);
		LOG.debug("####### RequestItem bean: "+RequestItemUtils.toXmlString(requestItem1));
		LOG.debug("####### RequestItem bean transformed: "+RequestItemUtils.toRequestItem(requestItem1).toString());
		LOG.debug("####### RequestItem     : "+requestItem2.toString());
		LOG.debug("####### RequestItem transformed: "+RequestItemUtils.toXmlString(RequestItemUtils.toRequestItemBean(requestItem2)));
		// Test RequestItem equals
		assertFalse("Different request item types should be different", RequestItemUtils.equal(requestItem1, requestItem2));
		assertFalse("Different request item types should be different (inverse)", requestItem2.equals(requestItem1));
		assertTrue("Different request item types should be equals", RequestItemUtils.equal(requestItem1, RequestItemUtils.toRequestItemBean(requestItem2)));
		assertTrue("Different request item types should be equals (inverse)", requestItem2.equals(RequestItemUtils.toRequestItem(requestItem1)));
		// Confirm this test by testing RequestItem attributes equals
		assertTrue("Different request item optional types should be equals", requestItem1.isOptional() == requestItem2.isOptional());
		assertTrue("Different request item actions types should be equals", ActionUtils.equal(requestItem1.getActions(), ActionUtils.toActionBeans(requestItem2.getActions())));
		assertTrue("Different request item conditions types should be equals", ConditionUtils.equal(requestItem1.getConditions(), ConditionUtils.toConditionBeans(requestItem2.getConditions())));
		assertTrue("Different request item resources types should be equals", ResourceUtils.equal(requestItem1.getResource(), ResourceUtils.toResourceBean(requestItem2.getResource())));

		// - RequestItem: optional is different
		requestItem1 = RequestItemUtils.create(resource1, actions1, conditions1, true);
		requestItem2 = new org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem(resource2, actions2, conditions2, false);
		LOG.debug("####### RequestItem bean: "+RequestItemUtils.toXmlString(requestItem1));
		LOG.debug("####### RequestItem bean transformed: "+RequestItemUtils.toRequestItem(requestItem1).toString());
		LOG.debug("####### RequestItem     : "+requestItem2.toString());
		LOG.debug("####### RequestItem transformed: "+RequestItemUtils.toXmlString(RequestItemUtils.toRequestItemBean(requestItem2)));
		// Test RequestItem equals
		assertFalse("Different request item types should be different", RequestItemUtils.equal(requestItem1, requestItem2));
		assertFalse("Different request item types should be different (inverse)", requestItem2.equals(requestItem1));
		assertFalse("Different request (optional) item types should not be equals", RequestItemUtils.equal(requestItem1, RequestItemUtils.toRequestItemBean(requestItem2)));
		assertFalse("Different request item (optional) types should not be equals (inverse)", requestItem2.equals(RequestItemUtils.toRequestItem(requestItem1)));
		// Confirm this test by testing RequestItem attributes equals
		assertFalse("Different request item (optional) optional types should be equals", requestItem1.isOptional() == requestItem2.isOptional());
		assertTrue("Different request item actions types should be equals", ActionUtils.equal(requestItem1.getActions(), ActionUtils.toActionBeans(requestItem2.getActions())));
		assertTrue("Different request item conditions types should be equals", ConditionUtils.equal(requestItem1.getConditions(), ConditionUtils.toConditionBeans(requestItem2.getConditions())));
		assertTrue("Different request item resources types should be equals", ResourceUtils.equal(requestItem1.getResource(), ResourceUtils.toResourceBean(requestItem2.getResource())));

		// - RequestItem: optional is different
		requestItem1 = RequestItemUtils.create(resource1, ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE), conditions1, true);
		requestItem2 = new org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem(resource2, actions2, conditions2, false);
		LOG.debug("####### RequestItem bean: "+RequestItemUtils.toXmlString(requestItem1));
		LOG.debug("####### RequestItem bean transformed: "+RequestItemUtils.toRequestItem(requestItem1).toString());
		LOG.debug("####### RequestItem     : "+requestItem2.toString());
		LOG.debug("####### RequestItem transformed: "+RequestItemUtils.toXmlString(RequestItemUtils.toRequestItemBean(requestItem2)));
		// Test RequestItem equals
		assertFalse("Different request item types should be different", RequestItemUtils.equal(requestItem1, requestItem2));
		assertFalse("Different request item types should be different (inverse)", requestItem2.equals(requestItem1));
		assertFalse("Different request (actions, optional) item types should not be equals", RequestItemUtils.equal(requestItem1, RequestItemUtils.toRequestItemBean(requestItem2)));
		assertFalse("Different request item (actions, optional) types should not be equals (inverse)", requestItem2.equals(RequestItemUtils.toRequestItem(requestItem1)));
		// Confirm this test by testing RequestItem attributes equals
		assertFalse("Different request item (actions, optional) optional types should be equals", requestItem1.isOptional() == requestItem2.isOptional());
		assertFalse("Different request item (actions, optional) actions types should be equals", ActionUtils.equal(requestItem1.getActions(), ActionUtils.toActionBeans(requestItem2.getActions())));
		assertTrue("Different request item conditions types should be equals", ConditionUtils.equal(requestItem1.getConditions(), ConditionUtils.toConditionBeans(requestItem2.getConditions())));
		assertTrue("Different request item resources types should be equals", ResourceUtils.equal(requestItem1.getResource(), ResourceUtils.toResourceBean(requestItem2.getResource())));
	}

	@Test
	public void testOptionalField() {
		// -- Empty
		RequestItem requestItem = new RequestItem();
		assertFalse("Empty request item should be mandatory", requestItem.isOptional());
		// -- Filled
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		requestItem = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ),
				conditions1);
		assertFalse("Request item 1 should be mandatory", requestItem.isOptional());
		requestItem = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ),
				conditions1, false);
		assertFalse("Request item 1 should be mandatory", requestItem.isOptional());
		requestItem = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ),
				conditions1, true);
		assertTrue("Request item 3 should be optional", requestItem.isOptional());
	}

	@Test
	public void testEqual() {
		RequestItem requestItem1 = null;
		RequestItem requestItem2 = null;
		Action notPrivacyPolicy = null;
		// -- Null Privacy Policy
		assertTrue("Same null privacy policy should be equal", RequestItemUtils.equal(requestItem1, requestItem1));
		assertTrue("Null privacy policies should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
		assertTrue("Null privacy policy and null object should be equal", RequestItemUtils.equal(requestItem1, notPrivacyPolicy));
		// -- Empty Privacy Policy
		requestItem1 = new RequestItem();
		requestItem2 = new RequestItem();
		notPrivacyPolicy = new Action();
		assertTrue("Same empty privacy policy should be equal", RequestItemUtils.equal(requestItem1, requestItem1));
		assertTrue("Empty privacy policies should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
		assertTrue("Empty privacy policies should be equal (inverse)", RequestItemUtils.equal(requestItem2, requestItem1));
		assertFalse("Empty privacy policy and empty object should not be equal", RequestItemUtils.equal(requestItem1, notPrivacyPolicy));
		// -- Privacy Policy
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		requestItem1 = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ),
				conditions1);
		assertTrue("Same privacy policy should be equal", RequestItemUtils.equal(requestItem1, requestItem1));
		assertFalse("Different privacy policies should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
		assertFalse("Different privacy policies should be equal (inverse)", RequestItemUtils.equal(requestItem2, requestItem1));
		requestItem2 = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.WRITE),
				conditions1);
		assertFalse("Different privacy policies should be equal (2)", RequestItemUtils.equal(requestItem1, requestItem2));
		assertFalse("Different privacy policies should be equal (inverse) (2)", RequestItemUtils.equal(requestItem2, requestItem1));
		requestItem2 = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ),
				conditions1);
		assertTrue("Equal privacy policies should be equal", RequestItemUtils.equal(requestItem1, requestItem2));
		assertTrue("Equal privacy policies should be equal (inverse)", RequestItemUtils.equal(requestItem2, requestItem1));
		assertFalse("Privacy policy and object should not be equal", RequestItemUtils.equal(requestItem1, notPrivacyPolicy));
		// Equality with different "optional"
		requestItem2 = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
				ActionUtils.createList(ActionConstants.READ),
				conditions1, true);
		assertFalse("Equal privacy policies should not be equal", RequestItemUtils.equal(requestItem1, requestItem2));
		assertFalse("Equal privacy policies should not be equal (inverse)", RequestItemUtils.equal(requestItem2, requestItem1));
		assertTrue("Similar privacy policies should be Similar", RequestItemUtils.equal(requestItem1, requestItem2, true));
		assertTrue("Similar privacy policies should be Similar (inverse)", RequestItemUtils.equal(requestItem2, requestItem1, true));
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
