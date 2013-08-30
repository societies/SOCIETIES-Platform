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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyUtilsTest.class.getName());

	private RequestPolicy privacyPolicy;
	private RequestPolicy privacyPolicyWithoutRequestor;
	private String privacyPolicyWithoutRequestorString;

	@Before
	public void setUp() {
		RequestorBean requestor = RequestorUtils.create("emma.ict-societies.local", "cis-test.ict-societies.local");
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		List<Condition> conditions2 = new ArrayList<Condition>();
		conditions2.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		conditions2.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "Yes"));
		privacyPolicyWithoutRequestorString = "<RequestPolicy>"+
				/* There is an expected issue in <subject></subject>. The CIS ID is badly formatted. This should return a null Requestor and not failed. */
				"<Subject>"+
				" <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\""+
				"    DataType=\"org.societies.api.identity.IIdentity\">"+
				"  <AttributeValue>othercss@societies.local</AttributeValue>"+
				" </Attribute>"+
				" <Attribute AttributeId=\"CisId\""+
				"    DataType=\"org.societies.api.identity.IIdentity\">"+
				"  <AttributeValue>onecis.societies.local</AttributeValue>"+
				" </Attribute></Subject>"+
				"<Target>"+
				"<Resource>"+
				" <Attribute AttributeId=\"contextType\""+
				"   DataType=\"http://www.w3.org/2001/XMLSchema#string\">"+
				"  <AttributeValue>fdsfsf</AttributeValue>"+
				" </Attribute>"+
				"</Resource>"+
				"<Action>"+
				" <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" "+
				"   DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants\">"+
				"  <AttributeValue>WRITE</AttributeValue>"+
				" </Attribute>"+
				"<optional>false</optional>"+
				"</Action>"+
				"<Condition>"+
				" <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" "+
				"   DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants\">"+
				"  <AttributeValue DataType=\"SHARE_WITH_3RD_PARTIES\">dfsdf</AttributeValue>"+
				" </Attribute>"+
				"<optional>true</optional>"+
				"</Condition>"+
				"<Condition>"+
				" <Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" "+
				"   DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants\">"+
				"  <AttributeValue DataType=\"DATA_RETENTION_IN_MINUTES\">412</AttributeValue>"+
				" </Attribute>"+
				"<optional>true</optional>"+
				"</Condition>"+
				"<optional>false</optional>"+
				"</Target></RequestPolicy>";
		privacyPolicyWithoutRequestor = RequestPolicyUtils.createList(null,
				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
						ActionUtils.createList(ActionConstants.READ),
						conditions1),
						RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES),
								ActionUtils.createList(ActionConstants.READ),
								conditions2)
				);
		privacyPolicy = RequestPolicyUtils.createList(requestor,
				RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION),
						ActionUtils.createList(ActionConstants.READ),
						conditions1),
						RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES),
								ActionUtils.createList(ActionConstants.READ),
								conditions2)
				);
	}


	@Test
	public void testFromXacmlStringNull() {
		String testTitle = "FromXacmlNull";
		LOG.info(testTitle);
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		try {
			privacyPolicy1 = PrivacyPolicyUtils.fromXacmlString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			privacyPolicy2 = PrivacyPolicyUtils.fromXacmlString("");
		}
		catch (Exception e) {
			LOG.info("[Test Exception] "+testTitle+": "+e.getMessage(), e);
			fail("[Error testFromXmlNull] "+testTitle+": "+e.getMessage());
		}
		assertNull("Privacy policy (only header) should be null", privacyPolicy1);
		assertNull("Privacy policy should be null", privacyPolicy2);
	}

	@Test
	public void testFromXacmlString() {
		String testTitle = "testFromXacmlString: generated a RequestPolicy from a XML privacy policy";
		LOG.info(testTitle);

		RequestPolicy retrievedPrivacyPolicy = null;
		RequestPolicy retrievedPrivacyPolicyWithoutRequestor = null;
		RequestPolicy retrievedPrivacyPolicyWithoutRequestorString = null;
		try {
			retrievedPrivacyPolicy = PrivacyPolicyUtils.fromXacmlString(RequestPolicyUtils.toXmlString(privacyPolicy));
			retrievedPrivacyPolicyWithoutRequestor = PrivacyPolicyUtils.fromXacmlString(RequestPolicyUtils.toXmlString(privacyPolicyWithoutRequestor));
			retrievedPrivacyPolicyWithoutRequestorString = PrivacyPolicyUtils.fromXacmlString(privacyPolicyWithoutRequestorString);
		} 
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("Error "+e.getMessage()+": "+testTitle);
		}
		// -- Privacy Policy with Requestor
		assertNotNull("Privacy policy generated should not be null", retrievedPrivacyPolicy);
		assertNotNull("Privacy policy original should not be null", privacyPolicy);
		//		LOG.debug("**** Original XML privacy policy ****");
		//		LOG.debug(RequestPolicyUtils.toXmlString(privacyPolicy));
		//		LOG.debug("**** Generated RequestPolicy ****");
		//		LOG.debug(RequestPolicyUtils.toXmlString(retrievedPrivacyPolicy));
		assertEquals("Privacy policy generated (xml) not equal to the original policy", RequestPolicyUtils.toXmlString(privacyPolicy), RequestPolicyUtils.toXmlString(retrievedPrivacyPolicy));
		// Class
		assertEquals("Privacy policy generated not same class as the original policy", privacyPolicy.getClass(), retrievedPrivacyPolicy.getClass());
		// Requestor
		assertTrue("Privacy policy generated and the original policy: not the same requestor", RequestorUtils.equal(privacyPolicy.getRequestor(), retrievedPrivacyPolicy.getRequestor()));
		// Request Items
		assertEquals("Privacy policy generated and the original policy: not the same request items size", privacyPolicy.getRequestItems().size(), retrievedPrivacyPolicy.getRequestItems().size());
		for(int i=0; i<retrievedPrivacyPolicy.getRequestItems().size(); i++) {
			assertTrue("Privacy policy generated and the original policy: not the same request item "+i, RequestItemUtils.equal(privacyPolicy.getRequestItems().get(i), retrievedPrivacyPolicy.getRequestItems().get(i)));
		}
		assertTrue("Privacy policy generated and the original policy: not the same request items", RequestItemUtils.equal(privacyPolicy.getRequestItems(), retrievedPrivacyPolicy.getRequestItems()));
		// All
		assertTrue("Privacy policy generated not equal to the original policy", RequestPolicyUtils.equal(privacyPolicy, retrievedPrivacyPolicy));

		// -- Privacy Policy without Requestor
		assertNotNull("Privacy policy generated should not be null (2)", retrievedPrivacyPolicyWithoutRequestor);
		assertNotNull("Privacy policy original should not be null (2)", privacyPolicyWithoutRequestor);
//		LOG.info("**** Original XML privacy policy ****");
//		LOG.info(RequestPolicyUtils.toXmlString(privacyPolicyWithoutRequestor));
//		LOG.info("**** Generated RequestPolicy ****");
//		LOG.info(RequestPolicyUtils.toXmlString(retrievedPrivacyPolicyWithoutRequestor));
		assertEquals("Privacy policy generated (xml) not equal to the original policy (2)", RequestPolicyUtils.toXmlString(privacyPolicyWithoutRequestor), RequestPolicyUtils.toXmlString(retrievedPrivacyPolicyWithoutRequestor));
		// Class
		assertEquals("Privacy policy generated not same class as the original policy (2)", privacyPolicyWithoutRequestor.getClass(), retrievedPrivacyPolicyWithoutRequestor.getClass());
		// Requestor
		assertNull("Privacy policy generated should have a null requestor (2)", retrievedPrivacyPolicyWithoutRequestor.getRequestor());
		assertTrue("Privacy policy generated and the original policy: not the same requestor (2)", RequestorUtils.equal(privacyPolicyWithoutRequestor.getRequestor(), retrievedPrivacyPolicyWithoutRequestor.getRequestor()));
		// Request Items
		assertEquals("Privacy policy generated and the original policy: not the same request items size (2)", privacyPolicyWithoutRequestor.getRequestItems().size(), retrievedPrivacyPolicyWithoutRequestor.getRequestItems().size());
		for(int i=0; i<retrievedPrivacyPolicy.getRequestItems().size(); i++) {
			assertTrue("Privacy policy generated and the original policy: not the same request item  (2)"+i, RequestItemUtils.equal(privacyPolicyWithoutRequestor.getRequestItems().get(i), retrievedPrivacyPolicyWithoutRequestor.getRequestItems().get(i)));
		}
		assertTrue("Privacy policy generated and the original policy: not the same request items (2)", RequestItemUtils.equal(privacyPolicyWithoutRequestor.getRequestItems(), retrievedPrivacyPolicyWithoutRequestor.getRequestItems()));
		// All
		assertTrue("Privacy policy generated not equal to the original policy (2)", RequestPolicyUtils.equal(privacyPolicyWithoutRequestor, retrievedPrivacyPolicyWithoutRequestor));

		// -- Privacy Policy without Requestor (originaly a string)
		assertNotNull("Privacy policy generated should not be null (3)", retrievedPrivacyPolicyWithoutRequestorString);
		assertNotNull("Privacy policy original should not be null (3)", privacyPolicyWithoutRequestorString);
		LOG.info("**** Original XML privacy policy ****");
		LOG.info(privacyPolicyWithoutRequestorString);
		LOG.info("**** Generated RequestPolicy ****");
		LOG.info(RequestPolicyUtils.toXmlString(retrievedPrivacyPolicyWithoutRequestorString));
		// Requestor
		assertNull("Privacy policy generated should have a null requestor (3)", retrievedPrivacyPolicyWithoutRequestorString.getRequestor());
	}

	@Test
	public void testGetDataTypesWithScheme() {
		RequestPolicy privacyPolicy = null;
		DataIdentifierScheme schemeFilter = DataIdentifierScheme.CONTEXT;
		// -- Null Privacy Policy
		List<String> dataTypes = PrivacyPolicyUtils.getDataTypes(schemeFilter, privacyPolicy);
		assertNull("Data type list of an null privacy policy should be null", dataTypes);
		// -- Empty Privacy Policy
		privacyPolicy = new RequestPolicy();
		dataTypes = PrivacyPolicyUtils.getDataTypes(schemeFilter, privacyPolicy);
		assertNull("Data type list of an empty privacy policy should be null", dataTypes);
		// -- Privacy Policy
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		requestItems.add(RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ABOUT), ActionUtils.createList(ActionConstants.READ), conditions));
		privacyPolicy.setRequestItems(requestItems);
		dataTypes = PrivacyPolicyUtils.getDataTypes(schemeFilter, privacyPolicy);
		assertNotNull("Data type list of an empty privacy policy should be null", dataTypes);
	}

	@Test
	public void testInferCisPrivacyPolicy() {
		String testTitle = "InferCisPrivacyPolicy: create a pre-privacy policy";
		LOG.info(testTitle);

		RequestPolicy privacyPolicyPrivate = null;
		RequestPolicy privacyPolicyMembersOnly = null;
		RequestPolicy privacyPolicyPublic = null;
		try {
			privacyPolicyPrivate = PrivacyPolicyUtils.inferCisPrivacyPolicy(PrivacyPolicyBehaviourConstants.PRIVATE, null);
			privacyPolicyMembersOnly = PrivacyPolicyUtils.inferCisPrivacyPolicy(PrivacyPolicyBehaviourConstants.MEMBERS_ONLY, null);
			privacyPolicyPublic = PrivacyPolicyUtils.inferCisPrivacyPolicy(PrivacyPolicyBehaviourConstants.PUBLIC, null);
		} catch (PrivacyException e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("Error "+e.getMessage()+": "+testTitle);
		}
		assertNotNull("Private privacy policy should not be null", privacyPolicyPrivate);
		assertNotNull("Members only privacy policy should not be null", privacyPolicyMembersOnly);
		assertNotNull("Public privacy policy should not be null", privacyPolicyPublic);
		//		Log.debug("Private privacy policy: "+PrivacyPolicyUtils.toXacmlString(privacyPolicyPrivate)+"\n*******");
		//		Log.debug("Members only privacy policy: "+PrivacyPolicyUtils.toXacmlString(privacyPolicyMembersOnly)+"\n*******");
		//		Log.debug("Public privacy policy: "+PrivacyPolicyUtils.toXacmlString(privacyPolicyPublic)+"\n*******");
	}
}
