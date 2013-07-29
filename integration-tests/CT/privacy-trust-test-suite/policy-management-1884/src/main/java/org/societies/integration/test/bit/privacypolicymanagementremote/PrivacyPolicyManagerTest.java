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
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Test list:
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyManagerTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerTest.class);
	public static Integer testCaseNumber = 0;

	private IPrivacyPolicyManagerListener privacyPolicyListener;
	private CountDownLatch lock;
	private boolean succeed;
	private String resultMsg;
	private Exception errorException;
	private RequestPolicy retrievedPrivacyPolicy;

	private IIdentity currentNode;
	private IIdentity targetedNode;
	private RequestorCis requestorCis;
	private RequestorService requestorService;
	private RequestPolicy cisPolicy;
	private RequestPolicy servicePolicy;

	@Before
	public void setUp() {
		LOG.info("[#"+testCaseNumber+"] setUp");
		// Dependency injection not ready
		if (!TestCase.isDepencyInjectionDone()) {
			LOG.error("[#"+testCaseNumber+"] [Dependency Injection] PrivacyPolicyManagerTest not ready");
			fail("[Dependency Injection] PrivacyPolicyManagerTest not ready");
		}

		// Listener
		lock = new CountDownLatch(1);
		privacyPolicyListener = new IPrivacyPolicyManagerListener() {

			@Override
			public void onPrivacyPolicyRetrieved(RequestPolicy privacyPolicy) {
				succeed = true;
				retrievedPrivacyPolicy = privacyPolicy;
				resultMsg = "Privacy policy retrieved or updated!";
//				LOG.debug("onPrivacyPolicyRetrieved"+resultMsg);
				lock.countDown();
			}

			@Override
			public void onPrivacyPolicyRetrieved(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy) {
				succeed = true;
				try {
					retrievedPrivacyPolicy = RequestPolicyUtils.toRequestPolicy(privacyPolicy, TestCase.commManager.getIdManager());
				} catch (InvalidFormatException e) {
					onOperationAborted("Privacy policy retrieved, but it is ununderstandable", e);
				}
				resultMsg = "Privacy policy retrieved or updated!";
				//				LOG.debug("onPrivacyPolicyRetrieved"+resultMsg);
				lock.countDown();
			}
			
			@Override
			public void onOperationSucceed(String msg) {
				succeed = true;
				resultMsg = "Privacy Policy action succeed: "+msg;
//				LOG.debug("onOperationSucceed"+resultMsg);
				lock.countDown();
			}

			@Override
			public void onOperationCancelled(String msg) {
				succeed = false;
				resultMsg = "Privacy Policy action cancelled. "+msg;
//				LOG.debug("onOperationCancelled"+resultMsg);
				lock.countDown();
			}

			@Override
			public void onOperationAborted(String msg, Exception e) {
				succeed = false;
				resultMsg = "Privacy Policy action aborted. "+msg;
				errorException = e;
//				LOG.debug("onOperationAborted"+resultMsg);
				lock.countDown();
			}
		};

		// Data
		try {
			currentNode = TestCase.commManager.getIdManager().getThisNetworkNode();
			targetedNode = TestCase.commManager.getIdManager().fromJid(TestCase.getReceiverJid());
			requestorCis = getRequestorCis();
			requestorService = getRequestorService();
			cisPolicy = getRequestPolicy(requestorCis);
			servicePolicy = getRequestPolicy(requestorService);
		} catch (InvalidFormatException e) {
			LOG.error("[#"+testCaseNumber+"] Error during setup", e);
			fail("Error during setup");
		}
	}

	@Before
	public void tearDown() {
		LOG.info("[#"+testCaseNumber+"] tearDown");
		succeed = false;
		resultMsg = "";
		retrievedPrivacyPolicy = null;
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicyNonExisting() {
		String testTitle = new String("Get CIS Privacy Policy: retrieve a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);
		try {
			// -- Try to retrieve
			LOG.debug("[#"+testCaseNumber+"] Try to retrieve...");
			TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(RequestorUtils.toRequestor(RequestorUtils.create(currentNode.getJid(), "cis-"+(new Random().nextInt()+".ict-societies.eu")), TestCase.commManager.getIdManager()),
					targetedNode,
					privacyPolicyListener);
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy retrieving aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNull("Retrieved privacy policy should be null", retrievedPrivacyPolicy);
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error: "+e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicy() {
		String testTitle = new String("Get CIS Privacy Policy: add and retrieve the CIS privacy policy ("+requestorCis+") from "+targetedNode);
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);
		try {
			// -- Create privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Creation...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, privacyPolicyListener);
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy updated aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", cisPolicy, retrievedPrivacyPolicy);
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", cisPolicy.toString(), retrievedPrivacyPolicy.toString());
			succeed = false;
			retrievedPrivacyPolicy = null;

			// -- Retrieve Privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Try to retrieve...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy retrieving aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", cisPolicy, retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", cisPolicy.toString(), retrievedPrivacyPolicy.toString());

			// -- Delete this privacy policy
			LOG.debug("[#"+testCaseNumber+"] Delete...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy deletion aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}

			// -- Try to retrieve
			LOG.debug("[#"+testCaseNumber+"] Try to retrieve again...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy retrieving aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNull("Retrieved privacy policy should be null", retrievedPrivacyPolicy);
		} 
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error: "+e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicyNonExisting() {
		String testTitle = new String("Get Service Privacy Policy: retrieve a non-existing privacy policy");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);
		try {
			// -- Delete this privacy policy (to be sure it doesn't exist)
			LOG.debug("[#"+testCaseNumber+"] Delete...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorService, targetedNode, privacyPolicyListener);
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy deletion aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}

			// -- Try to retrieve
			LOG.debug("[#"+testCaseNumber+"] Try to retrieve...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy retrieving aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNull("Retrieved privacy policy should be null", retrievedPrivacyPolicy);
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error: "+e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicy() {
		String testTitle = new String("Get Service Privacy Policy: add and retrieve a privacy policy");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);
		try {
			// -- Create privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Create...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.updatePrivacyPolicy(servicePolicy, targetedNode, privacyPolicyListener);
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy updated aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", servicePolicy, retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", servicePolicy.toString(), retrievedPrivacyPolicy.toString());
			succeed = false;
			retrievedPrivacyPolicy = null;

			// -- Retrieve Privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Try to retrieve...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy retrieving aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", servicePolicy, retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", servicePolicy.toString(), retrievedPrivacyPolicy.toString());

			// -- Delete this privacy policy
			LOG.debug("[#"+testCaseNumber+"] Delete...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorService, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy deletion aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}

			// -- Try to retrieve
			LOG.debug("[#"+testCaseNumber+"] Try to retrieve again...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(requestorService, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy retrieving aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNull("Retrieved privacy policy should be null", retrievedPrivacyPolicy);
		} 
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error: "+e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicy() {
		String testTitle = new String("Update Privacy Policy: update the same privacy policy several times");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);
		try {
			// -- Create privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Creation...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, privacyPolicyListener);
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy creation aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", cisPolicy, retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", cisPolicy.toString(), retrievedPrivacyPolicy.toString());
			RequestPolicy retrievedPrivacyPolicy1 = retrievedPrivacyPolicy;
			succeed = false;
			retrievedPrivacyPolicy = null;

			// -- Create privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Creation again...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy creation aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", cisPolicy, retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", cisPolicy.toString(), retrievedPrivacyPolicy.toString());
			assertEquals("Previous and retrieved privacy policy should be equals", retrievedPrivacyPolicy1, retrievedPrivacyPolicy);
			assertEquals("Previous and retrieved privacy policy strings should be equals", retrievedPrivacyPolicy1.toString(), retrievedPrivacyPolicy.toString());

			// -- Delete this privacy policy
			LOG.debug("[#"+testCaseNumber+"] Delete...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy deletion aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error: "+e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicySeveral() {
		String testTitle = new String("Update Privacy Policy: update the same privacy policy several times with updates");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);
		RequestItem requestItem = new RequestItem(cisPolicy.getRequests().get(0).getResource(), cisPolicy.getRequests().get(0).getActions(), cisPolicy.getRequests().get(0).getConditions());
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		requestItems.add(requestItem);
		RequestPolicy cisPolicy2 = new RequestPolicy(requestItems);
		cisPolicy2.setRequestor(requestorCis);
		try {
			// -- Create privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Creation...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy, targetedNode, privacyPolicyListener);
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy creation aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", cisPolicy, retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", cisPolicy.toString(), retrievedPrivacyPolicy.toString());
			RequestPolicy retrievedPrivacyPolicy1 = retrievedPrivacyPolicy;
			succeed = false;
			retrievedPrivacyPolicy = null;

			// -- Update this privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Update...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.updatePrivacyPolicy(cisPolicy2, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy creation aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy should be equals", cisPolicy2, retrievedPrivacyPolicy);
			assertEquals("Created and retrieved privacy policy strings should be equals", cisPolicy2.toString(), retrievedPrivacyPolicy.toString());
			assertFalse("Previous and retrieved privacy policy should not be equals", retrievedPrivacyPolicy1.equals(retrievedPrivacyPolicy));
			assertFalse("Previous and retrieved privacy policy strings should be equals", retrievedPrivacyPolicy1.toString().equals(retrievedPrivacyPolicy.toString()));
			RequestPolicy retrievedPrivacyPolicy2 = retrievedPrivacyPolicy;
			succeed = false;
			retrievedPrivacyPolicy = null;

			// -- Retrieve Privacy Policy
			LOG.debug("[#"+testCaseNumber+"] Try to retrieve...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy retrieving aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
			assertNotNull("Retrieved privacy policy should not be null", retrievedPrivacyPolicy);
			assertEquals("Updated (raw) and retrieved privacy policy should be equals", cisPolicy2, retrievedPrivacyPolicy);
			assertEquals("Updated (raw) and retrieved privacy policy strings should be equals", cisPolicy2.toString(), retrievedPrivacyPolicy.toString());
			assertEquals("Updated and retrieved privacy policy should be equals", retrievedPrivacyPolicy2, retrievedPrivacyPolicy);
			assertEquals("Updated and retrieved privacy policy strings should be equals", retrievedPrivacyPolicy2.toString(), retrievedPrivacyPolicy.toString());


			// -- Delete this privacy policy
			LOG.debug("[#"+testCaseNumber+"] Delete...");
			lock = new CountDownLatch(1);
			TestCase.privacyPolicyManagerRemote.deletePrivacyPolicy(requestorCis, targetedNode, privacyPolicyListener);
			releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				resultMsg = "Privacy policy deletion aborted due to timeout";
				errorException = new TimeoutException(resultMsg+": more than "+TestCase.getTimeout()+"ms to do this operation.");
			}
			// Verify
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+resultMsg, errorException);
				fail("Error: "+resultMsg);
			}
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error: "+e.getMessage());
		}
	}




	/* --- Tools --- */
	private RequestPolicy getRequestPolicy(Requestor requestor) {
		List<RequestItem> requestItems = getRequestItems();
		RequestPolicy requestPolicy = new RequestPolicy(requestor, requestItems);
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
		Resource someResource = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES);
		List<Condition> extendedConditions = new ArrayList<Condition>();
		extendedConditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"0"));
		extendedConditions.add(new Condition(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA, "1"));
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
		IIdentity requestorId = TestCase.commManager.getIdManager().fromJid("olivier.societies.local");
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
		IIdentity otherCssId = TestCase.commManager.getIdManager().fromJid("olivier.societies.local");
		IIdentity cisId = TestCase.commManager.getIdManager().fromJid("cis-one.societies.local");
		return new RequestorCis(otherCssId, cisId);
	}
}
