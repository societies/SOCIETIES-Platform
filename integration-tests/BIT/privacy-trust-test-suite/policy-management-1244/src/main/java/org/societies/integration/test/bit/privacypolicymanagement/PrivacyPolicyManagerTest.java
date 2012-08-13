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
package org.societies.integration.test.bit.privacypolicymanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Test list:
 * - get a not existing cis privacy policy
 * - add, get and delete a cis privacy policy
 * - get a not existing service privacy policy
 * - add, get and delete a service privacy policy
 * - update two times the same cis privacy policy
 * - update two times a cis privacy policy (but the second time it is transform into a service privacy policy)
 * - delete a not existing cis privacy policy
 * - delete a not existing service privacy policy
 * - delete a cis privacy policy
 * - delete a service privacy policy
 * - generate a privacy policy from XML string
 * - generate a privacy policy from empty XML string
 * - transform a privacy policy to a XML string
 * - transform an empty privacy policy to a XML string
 * - equality between two RequestPolicy
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyManagerTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerTest.class.getSimpleName());

	public static Integer testCaseNumber = 0;

	private RequestorCis requestorCis;
	private RequestorService requestorService;
	private RequestPolicy cisPolicy;
	private RequestPolicy servicePolicy;

	@Before
	public void setUp() throws Exception {
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase1244.isDepencyInjectionDone()) {
			throw new PrivacyException("[#"+testCaseNumber+"] [Dependency Injection] PrivacyPolicyManagerTest not ready");
		}
		// Data
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		cisPolicy = getRequestPolicy(requestorCis);
		servicePolicy = getRequestPolicy(requestorService);
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicyNonExisting() {
		String testTitle = new String("testGetCisPrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy expectedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		//Modified by rafik
		//before:
		//assertEquals("Expected null privacy policy, but it is not.", privacyPolicy, expectedPrivacyPolicy);
		//After:
		assertNull("Expected null privacy policy, but it is not.", privacyPolicy);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicy() {
		String testTitle = new String("testGetCisPrivacyPolicy: add and retrieve a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicy = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicy);
		
		//Modified by rafik
		//before:
		//assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy, addedPrivacyPolicy);
		//After:
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy.toXMLString(), addedPrivacyPolicy.toXMLString());
		
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicyNonExisting() {
		String testTitle = new String("testGetServicePrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy expectedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		
		//Modified by rafik
		//before:
		//assertEquals("Expected null privacy policy, but it is not.", privacyPolicy, expectedPrivacyPolicy);
		//After:
		assertNull("Expected null privacy policy, but it is not.", privacyPolicy);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicy() {
		String testTitle = new String("testGetServicePrivacyPolicy: add and retrieve a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(servicePolicy);
			privacyPolicy = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorService);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicy);
		
		//Modified by Rafik
		//before:
		//assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy, addedPrivacyPolicy);
		//After:
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy.toXMLString(), addedPrivacyPolicy.toXMLString());
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicy() {
		String testTitle = new String("testUpdatePrivacyPolicy: update the same privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		boolean deleteResult = false;
		try {
			privacyPolicy1 = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicy2 = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		LOG.info("$$$$$$ Rafik 1");
		assertEquals("Privacy policy not created", cisPolicy, privacyPolicy1);
		LOG.info("$$$$$$ Rafik 2");
		assertEquals("Privacy policy not updated", cisPolicy, privacyPolicy2);
		LOG.info("$$$$$$ Rafik 3");
		assertEquals("Difference between same privacy policies", privacyPolicy1, privacyPolicy2);
		LOG.info("$$$$$$ Rafik 4");
		assertTrue("Privacy policy not deleted.", deleteResult);
		LOG.info("$$$$$$ Rafik 5");
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicies() {
		String testTitle = new String("testUpdatePrivacyPolicy: update the same privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		RequestItem requestItem = new RequestItem(cisPolicy.getRequests().get(0).getResource(), cisPolicy.getRequests().get(0).getActions(), cisPolicy.getRequests().get(0).getConditions());
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		requestItems.add(requestItem);
		RequestPolicy cisPolicy2 = new RequestPolicy(requestItems);
		cisPolicy2.setRequestor(requestorCis);
		boolean deleteResult = false;
		try {
			privacyPolicy1 = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicy2 = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(cisPolicy2);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		//Modified by Rafik
		//before:
		//assertEquals("Privacy policy not created", cisPolicy, privacyPolicy1);
		//After:
		assertEquals("Privacy policy not created", cisPolicy.toXMLString(), privacyPolicy1.toXMLString());
		
		//Modified by Rafik
		//before:
		//assertEquals("Privacy policy not updated", cisPolicy2, privacyPolicy2);
		//After:
		assertEquals("Privacy policy not updated", cisPolicy2.toXMLString(), privacyPolicy2.toXMLString());
		
		//Modified by Rafik
		//before:
		//assertFalse("Same privacy policies but it should not", privacyPolicy1.equals(privacyPolicy2));
		//After:
		assertFalse("Same privacy policies but it should not", privacyPolicy1.toXMLString().equals(privacyPolicy2.toXMLString()));

		assertTrue("Privacy policy not deleted.", deleteResult);

	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteServicePrivacyPolicyNotExisting() {
		String testTitle = new String("testDeleteServicePrivacyPolicyNotExisting: delete a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorService);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNull("This privacy policy exists!", privacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteCisPrivacyPolicyNotExisting() {
		String testTitle = new String("testDeleteCisPrivacyPolicyNotExisting: delete a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNull("This privacy policy exists!", privacyPolicy);
		if (null != privacyPolicy) {
			LOG.info(privacyPolicy.toXMLString());
		}
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteServicePrivacyPolicy() {
		String testTitle = new String("testDeletePrivacyPolicy: add and retrieve and delete a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicyBefore = null;
		RequestPolicy privacyPolicyAfter = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(servicePolicy);
			privacyPolicyBefore = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorService);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorService);
			privacyPolicyAfter = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicyBefore);
		
		//Modified by Rafik
		//before:
		//assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore, addedPrivacyPolicy);
		//After:
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore.toXMLString(), addedPrivacyPolicy.toXMLString());
		
		
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteCisPrivacyPolicy() {
		String testTitle = new String("testDeleteCisPrivacyPolicy: add and retrieve and delete a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicyBefore = null;
		RequestPolicy privacyPolicyAfter = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = TestCase1244.privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicyBefore = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = TestCase1244.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
			privacyPolicyAfter = TestCase1244.privacyPolicyManager.getPrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicyBefore);
		
		//Modified by Rafik
		//before:
		//assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore, addedPrivacyPolicy);
		//After:
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore.toXMLString(), addedPrivacyPolicy.toXMLString());
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testFromXmlNull() {
		String testTitle = new String("testFromXml: null");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.fromXMLString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNull("Privacy policy not null, but it should", privacyPolicy);
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testFromXml() {
		String testTitle = new String("testFromXml");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.fromXMLString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+cisPolicy.toXMLString());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		//Modified by Rafik
		//before:
		//assertEquals("Privacy policy generated not equal to the original policy", cisPolicy, privacyPolicy);
		//After:
		assertEquals("Privacy policy generated not equal to the original policy", cisPolicy.toXMLString(), privacyPolicy.toXMLString());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testToXmlNull() {
		String testTitle = new String("testToXmlNull");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		String privacyPolicy = null;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.toXMLString(null);
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertEquals("Privacy policy generated not equal to the original policy", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RequestPolicy></RequestPolicy>", privacyPolicy);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testToXml() {
		String testTitle = new String("testToXml");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		String privacyPolicy = null;
		try {
			privacyPolicy = TestCase1244.privacyPolicyManager.toXMLString(cisPolicy);
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertEquals("Privacy policy generated not equal to the original policy", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+cisPolicy.toXMLString(), privacyPolicy);
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#inferPrivacyPolicy()}.
	 */
	@Test
	public void testInferPrivacyPolicy() {
		String testTitle = new String("testInferPrivacyPolicy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy expected = new RequestPolicy(new ArrayList<RequestItem>());
		RequestPolicy actual = null;
		try {
			actual = TestCase1244.privacyPolicyManager.inferPrivacyPolicy(PrivacyPolicyTypeConstants.CIS, null);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		//Modified by Rafik
		//before:
		//assertEquals(expected, actual);
		//After:
		assertEquals(expected.toXMLString(), actual.toXMLString());
	}


	/* --- Tools --- */
	private RequestPolicy getRequestPolicy(Requestor requestor) {
		List<RequestItem> requestItems = getRequestItems();
		RequestPolicy requestPolicy = new RequestPolicy(requestor, requestItems);
		return requestPolicy;
	}

	private List<RequestItem> getRequestItems() {
		List<RequestItem> items = new ArrayList<RequestItem>();
		Resource locationResource = new Resource(CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		RequestItem rItem = new RequestItem(locationResource, actions, conditions, false);
		items.add(rItem);
		Resource someResource = new Resource("someResource");
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

	private RequestorService getRequestorService() throws InvalidFormatException{
		IIdentity requestorId = TestCase1244.commManager.getIdManager().fromJid("red@societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://red@societies.local/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://red@societies.local/HelloEarth"));
		} catch (URISyntaxException e) {
			LOG.error("Can't create the service ID", e);
		}
		return new RequestorService(requestorId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException{
		IIdentity otherCssId = TestCase1244.commManager.getIdManager().fromJid("red@societies.local");
		IIdentity cisId = TestCase1244.commManager.getIdManager().fromJid("onecis.societies.local");
		return new RequestorCis(otherCssId, cisId);
	}
}
