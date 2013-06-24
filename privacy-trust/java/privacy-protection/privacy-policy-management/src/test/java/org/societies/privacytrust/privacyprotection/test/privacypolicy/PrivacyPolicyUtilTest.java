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
package org.societies.privacytrust.privacyprotection.test.privacypolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.util.commonmock.MockIdentity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Olivier Maridat (Trialog)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "PrivacyPolicyManagerTest-context.xml" })
public class PrivacyPolicyUtilTest extends AbstractJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyUtilTest.class.getSimpleName());

	private RequestorCis requestorCis;
	private RequestPolicy cisPolicy;

	@Before
	public void setUp() throws Exception {
		// Data
		requestorCis = getRequestorCis();
		cisPolicy = getRequestPolicy(requestorCis);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testToXmlNull() {
		String testTitle = "testToXmlNull";
		LOG.info(testTitle+": start");
		String privacyPolicyXml = null;
		try {
			privacyPolicyXml = PrivacyPolicyUtils.toXmlString(null);
		} catch (Exception e) {
			LOG.info("[Exception] "+testTitle+": "+e.getMessage(), e);
			fail("[Exception] "+testTitle+": "+e.getMessage());
		}
		assertEquals("Privacy policy generated not equal to the original policy", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RequestPolicy></RequestPolicy>", privacyPolicyXml.replaceAll("[\n\t]", ""));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testToXml() {
		String testTitle = "testToXml";
		LOG.info(testTitle+": start");
		String privacyPolicyXml = null;
		String exepectedPrivacyPolicyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+cisPolicy.toXMLString();
		try {
			privacyPolicyXml = PrivacyPolicyUtils.toXmlString(RequestPolicyUtils.toRequestPolicyBean(cisPolicy));
		} catch (Exception e) {
			LOG.info("[Exception] "+testTitle+": "+e.getMessage(), e);
			fail("[Exception] "+testTitle+": "+e.getMessage());
		}
		LOG.info("***** Original Privacy Policy *****");
		LOG.info(exepectedPrivacyPolicyXml.replaceAll("[\n\t ]", ""));
		LOG.info("***** Generated Privacy Policy *****");
		LOG.info(privacyPolicyXml.replaceAll("[\n\t ]", ""));
		assertEquals("Privacy policy generated not equal to the original policy", exepectedPrivacyPolicyXml.replaceAll("[\n\t ]", ""), privacyPolicyXml.replaceAll("[\n\t ]", ""));
	}



	/* --- Tools --- */
	private RequestPolicy getRequestPolicy(Requestor requestor) {
		RequestPolicy requestPolicy;
		List<RequestItem> requestItems = getRequestItems();
		requestPolicy = new RequestPolicy(requestor, requestItems);
		return requestPolicy;
	}

	private List<RequestItem> getRequestItems() {
		List<RequestItem> items = new ArrayList<RequestItem>();
		Resource locationResource = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		RequestItem rItem = new RequestItem(locationResource, actions, conditions, false);
		items.add(rItem);
		Resource someResource = new Resource(DataIdentifierScheme.CONTEXT, "someResource");
		List<Condition> extendedConditions = new ArrayList<Condition>();
		extendedConditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		extendedConditions.add(new Condition(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA, "YES"));
		List<Action> extendedActions = new ArrayList<Action>();
		extendedActions.add(new Action(ActionConstants.READ));
		extendedActions.add(new Action(ActionConstants.CREATE));
		extendedActions.add(new Action(ActionConstants.WRITE));
		extendedActions.add(new Action(ActionConstants.DELETE));
		RequestItem someItem = new RequestItem(someResource, extendedActions, extendedConditions, false);
		items.add(someItem);
		return items;
	}

	private RequestorCis getRequestorCis(){
		IIdentity otherCssId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "cis-one", "societies.local");
		return new RequestorCis(otherCssId, cisId);
	}
}
