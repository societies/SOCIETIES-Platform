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
package org.societies.integration.test.bit.privacypolicymanagementremote;

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
import org.junit.Ignore;
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
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Test list:
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyManagerTest implements IPrivacyPolicyManagerListener {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerTest.class.getSimpleName());

	public static Integer testCaseNumber = 0;

	private IIdentity targetedNode;
	private RequestorCis requestorCis;
	private RequestorService requestorService;
	private RequestPolicy cisPolicy;
	private RequestPolicy servicePolicy;

	@Before
	public void setUp() throws Exception {
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase1267.isDepencyInjectionDone()) {
			throw new PrivacyException("[#"+testCaseNumber+"] [Dependency Injection] PrivacyPolicyManagerTest not ready");
		}
		// Data
		targetedNode = TestCase1267.commManager.getIdManager().getThisNetworkNode();
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		cisPolicy = getRequestPolicy(requestorCis);
		servicePolicy = getRequestPolicy(requestorService);
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testGetCisPrivacyPolicyNonExisting() {
		String testTitle = new String("testGetCisPrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy expectedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		try {
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertEquals("Expected null privacy policy, but it is not.", privacyPolicy, expectedPrivacyPolicy);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicy() {
		String testTitle = new String("testGetCisPrivacyPolicy: add and retrieve a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		LOG.info(requestorCis.toString());
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicy);
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testGetServicePrivacyPolicyNonExisting() {
		String testTitle = new String("testGetServicePrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy expectedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		try {
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertEquals("Expected null privacy policy, but it is not.", privacyPolicy, expectedPrivacyPolicy);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testGetServicePrivacyPolicy() {
		String testTitle = new String("testGetServicePrivacyPolicy: add and retrieve a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(servicePolicy, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorService, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicy);
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	@Ignore
	public void testUpdatesCisPrivacyPolicy() {
		String testTitle = new String("testUpdatePrivacyPolicy: update the same privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		boolean deleteResult = false;
		try {
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertEquals("Privacy policy not created", cisPolicy, privacyPolicy1);
		assertEquals("Privacy policy not updated", cisPolicy, privacyPolicy2);
		assertEquals("Difference between same privacy policies", privacyPolicy1, privacyPolicy2);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	@Ignore
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
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy2, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertEquals("Privacy policy not created", cisPolicy, privacyPolicy1);
		assertEquals("Privacy policy not updated", cisPolicy2, privacyPolicy2);
		assertFalse("Same privacy policies but it should not", privacyPolicy1.equals(privacyPolicy2));
		assertTrue("Privacy policy not deleted.", deleteResult);
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testDeleteServicePrivacyPolicyNotExisting() {
		String testTitle = new String("testDeleteServicePrivacyPolicyNotExisting: delete a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorService, targetedNode, this);
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
	@Ignore
	public void testDeleteCisPrivacyPolicyNotExisting() {
		String testTitle = new String("testDeleteCisPrivacyPolicyNotExisting: delete a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, this);
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
	@Ignore
	public void testDeleteServicePrivacyPolicy() {
		String testTitle = new String("testDeletePrivacyPolicy: add and retrieve and delete a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicyBefore = null;
		RequestPolicy privacyPolicyAfter = null;
		boolean deleteResult = false;
		try {
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(servicePolicy, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorService, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicyBefore);
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testDeleteCisPrivacyPolicy() {
		String testTitle = new String("testDeleteCisPrivacyPolicy: add and retrieve and delete a privacy policy");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicyBefore = null;
		RequestPolicy privacyPolicyAfter = null;
		boolean deleteResult = false;
		try {
			TestCase1267.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, this);
			TestCase1267.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, this);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicyBefore);
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
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
		IIdentity requestorId = TestCase1267.commManager.getIdManager().fromJid("olivier@societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://olivier@societies.local/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://olivier@societies.local/HelloEarth"));
		} catch (URISyntaxException e) {
			LOG.error("Can't create the service ID", e);
		}
		return new RequestorService(requestorId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException{
		IIdentity otherCssId = TestCase1267.commManager.getIdManager().fromJid("olivier@societies.local");
		IIdentity cisId = TestCase1267.commManager.getIdManager().fromJid("onecis.societies.local");
		return new RequestorCis(otherCssId, cisId);
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onPrivacyPolicyRetrieved(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public void onPrivacyPolicyRetrieved(RequestPolicy privacyPolicy) {
		LOG.info("onPrivacyPolicyRetrieved: "+privacyPolicy.toXMLString());
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onOperationSucceed(java.lang.String)
	 */
	@Override
	public void onOperationSucceed(String msg) {
		LOG.info("onOperationSucceed: "+msg);
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onOperationCancelled(java.lang.String)
	 */
	@Override
	public void onOperationCancelled(String msg) {
		LOG.info("onOperationCancelled: "+msg);
	}


	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onOperationAborted(java.lang.String, java.lang.Exception)
	 */
	@Override
	public void onOperationAborted(String msg, Exception e) {
		LOG.info("onOperationAborted: "+msg, e);
	}
}
