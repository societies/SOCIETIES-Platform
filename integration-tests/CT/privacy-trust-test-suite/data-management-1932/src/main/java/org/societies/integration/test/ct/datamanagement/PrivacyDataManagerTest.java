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
package org.societies.integration.test.ct.datamanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.LocationCoordinatesUtils;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.NameUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.integration.test.IntegrationTest;
import org.societies.integration.test.userfeedback.UserFeedbackMockResult;
import org.societies.integration.test.userfeedback.UserFeedbackType;


/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerTest extends IntegrationTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerTest.class);


	private RequestorBean requestor;
	private CountDownLatch lock;
	private boolean succeed;
	private String errorMsg;
	private Exception errorException;
	private DataWrapper obfuscatedDataWrapper;
	private ResponseItem retrievedPermission;
	
	private long timestampSetUp;
	private long timestampTearDown;


	@Before
	public void setUp() {
		timestampSetUp = System.currentTimeMillis();
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase.isDepencyInjectionDone()) {
			fail("[Dependency Injection] "+getClass().getSimpleName()+" not ready");
		}
		// Data
		requestor = RequestorUtils.create(TestCase.getReceiverJid());

		// Init
		lock = new CountDownLatch(1);
		succeed = false;
		errorMsg = "";
		errorException = null;
		obfuscatedDataWrapper = null;
	}
	
	@After
	public void tearDown() {
		LOG.info("[#"+testCaseNumber+"] tearDown");
		timestampTearDown = System.currentTimeMillis();
		LOG.info("[#"+testCaseNumber+"] Lasts: "+(timestampTearDown-timestampSetUp)+"ms");
	}


	/* --- ACCESS CONTROL --- */

	@Test
	public void testCheckPermission()
	{
		String testTitle = new String("CheckPermission: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		try {
			// Owner ID
			IIdentity currentJid = TestCase.commManager.getIdManager().getThisNetworkNode();
			// Random Data ID
			//			DataIdentifier dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+currentJid+"/ENTITY/person/1/ATTRIBUTE/name/13");
			Random randomer = new Random((new Date()).getTime()); 
			String randomValue = ""+randomer.nextInt(200);
			DataIdentifier randomDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+currentJid+"/"+randomValue);
			// Action list
			List<Action> actionsRead = new ArrayList<Action>();
			actionsRead.add(new Action(ActionConstants.READ));
			// Prepare UserFeedback
			TestCase.getUserFeedbackMocker().addReply(UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
			// -- Call
			TestCase.privacyDataManagerRemote.checkPermission(RequestorUtils.toRequestor(requestor, TestCase.commManager.getIdManager()), randomDataId, actionsRead, new IPrivacyDataManagerListener() {
				@Override
				public void onAccessControlChecked(ResponseItem permission) {
					succeed = true;
					retrievedPermission = permission;
					lock.countDown();
				}
				@Override
				public void onAccessControlCancelled(String msg) {
					succeed = false;
					errorMsg = "Access control cancelled. "+msg;
					lock.countDown();
				}
				@Override
				public void onAccessControlAborted(String msg, Exception e) {
					succeed = false;
					errorMsg = "Access control aborted. "+msg;
					errorException = e;
					lock.countDown();
				}
				@Override
				public void onAccessControlChecked(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> permissions) {
					succeed = true;
					try {
						retrievedPermission = ResponseItemUtils.toResponseItem(permissions.get(0));
					}
					catch(Exception e) {
						succeed = false;
						onAccessControlAborted("No permission retrieved", e);
					}
					finally {
						lock.countDown();
					}
				}
			});

			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				errorMsg = "Access control aborted due to timeout";
				errorException = new TimeoutException("Access control aborted due to timeout: more then "+TestCase.getTimeout()+"ms to do this operation.");
			}

			// -- Verify
			// Error
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+errorMsg, errorException);
				fail("Error: "+errorMsg);
			}
			// Success
			assertNotNull("No permission retrieved", retrievedPermission);
			assertNotNull("No (real) permission retrieved", retrievedPermission.getDecision());
			assertEquals("Bad permission retrieved", Decision.PERMIT.name(), retrievedPermission.getDecision().name());
			LOG.info("[#"+testCaseNumber+"] Retrieved permission: "+retrievedPermission.toXMLString());
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException access control error] "+testTitle, e);
			fail("PrivacyException access control error ("+e.getMessage()+") "+testTitle);
		} catch (InterruptedException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException access control error] "+testTitle, e);
			fail("InterruptedException access control error ("+e.getMessage()+") "+testTitle);
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("[#"+testCaseNumber+"] [MalformedCtxIdentifierException access control error] "+testTitle, e);
			fail("MalformedCtxIdentifierException access control error ("+e.getMessage()+") "+testTitle);
		} catch (InvalidFormatException e) {
			LOG.error("[#"+testCaseNumber+"] [InvalidFormatException access control error] "+testTitle, e);
			fail("InvalidFormatException access control error ("+e.getMessage()+") "+testTitle);
		}
	}


	/* --- OBFUSCATION --- */

	@Test
	public void testObfuscateDataName()
	{
		String testTitle = "ObfuscateData: name";
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		try {
			DataWrapper wrapper = DataWrapperFactory.getNameWrapper("Olivier", "Maridat");
			TestCase.privacyDataManagerRemote.obfuscateData(requestor, wrapper, new IDataObfuscationListener()
			{
				@Override
				public void onObfuscationDone(DataWrapper data) {
					succeed = true;
					obfuscatedDataWrapper = data;
					lock.countDown();
				}
				@Override
				public void onObfuscationCancelled(String msg) {
					succeed = false;
					errorMsg = msg;
					lock.countDown();
				}
				@Override
				public void onObfuscationAborted(String msg, Exception e) {
					succeed = false;
					errorMsg = msg;
					errorException = e;
					lock.countDown();
				}
			});

			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				errorMsg = "Obfuscation aborted due to timeout";
				errorException = new TimeoutException("Obfuscation aborted due to timeout: more then "+TestCase.getTimeout()+"ms to do this operation.");
			}

			// -- Verify
			// Error
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+errorMsg, errorException);
				fail("Error: "+errorMsg);
			}
			// Success
			assertNotNull("Obfuscated data wrapper should not be null", obfuscatedDataWrapper);
			Name originalData = DataWrapperFactory.retrieveName(wrapper);
			Name obfuscatedData = DataWrapperFactory.retrieveName(obfuscatedDataWrapper);
			LOG.info("[#"+testCaseNumber+"] Orginal name: "+NameUtils.toString(originalData));
			LOG.info("[#"+testCaseNumber+"] Obfuscated name: "+NameUtils.toString(obfuscatedData));
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		} catch (InterruptedException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException obfuscator error] "+testTitle, e);
			fail("InterruptedException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
	}

	@Test
	public void testObfuscateDataLocationCoordinates()
	{
		String testTitle = new String("ObfuscateData: coordinate location");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		try {
			DataWrapper dataWrapper = DataWrapperFactory.getLocationCoordinatesWrapper(48.856666, 2.350987, 542.0);
			TestCase.privacyDataManagerRemote.obfuscateData(requestor, dataWrapper, new IDataObfuscationListener()
			{
				@Override
				public void onObfuscationDone(DataWrapper data) {
					succeed = true;
					obfuscatedDataWrapper = data;
					lock.countDown();
				}
				@Override
				public void onObfuscationCancelled(String msg) {
					succeed = false;
					errorMsg = msg;
					lock.countDown();
				}
				@Override
				public void onObfuscationAborted(String msg, Exception e) {
					succeed = false;
					errorMsg = msg;
					errorException = e;
					lock.countDown();
				}
			});

			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				succeed = false;
				errorMsg = "Obfuscation aborted due to timeout";
				errorException = new TimeoutException("Obfuscation aborted due to timeout: more then "+TestCase.getTimeout()+"ms to do this operation.");
			}

			// -- Verify
			// Error
			if (!succeed) {
				LOG.error("[#"+testCaseNumber+"] Error: "+errorMsg, errorException);
				fail("Error: "+errorMsg);
			}
			// Success
			assertNotNull("Obfuscated data wrapper should not be null", obfuscatedDataWrapper);
			LocationCoordinates originalData = DataWrapperFactory.retrieveLocationCoordinates(dataWrapper);
			LocationCoordinates obfuscatedData = DataWrapperFactory.retrieveLocationCoordinates(obfuscatedDataWrapper);
			LOG.info("[#"+testCaseNumber+"] Orginal name: "+LocationCoordinatesUtils.toJsonString(originalData));
			LOG.info("[#"+testCaseNumber+"] Obfuscated name: "+LocationCoordinatesUtils.toJsonString(obfuscatedData));
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		} catch (InterruptedException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException obfuscator error] "+testTitle, e);
			fail("InterruptedException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
	}

}
