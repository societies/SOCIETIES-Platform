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
package org.societies.android.security.test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.common.ADate;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.security.digsig.IDigSigClient;
import org.societies.android.security.container.TestServiceSecurityClientLocal;
import org.societies.android.security.container.TestServiceSecurityClientLocal.LocalSecurityClientBinder;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * 1. Ensure that {@link TestInternalTrustClient#TEST_TRUSTOR_ID} matches the
 * JID of the cloud node of the android client.
 */
public class TestInternalSecurityClient extends ServiceTestCase<TestServiceSecurityClientLocal> {

	private static final String TAG = TestInternalSecurityClient.class.getName();
	private static final String CLIENT = "org.societies.android.privacytrust.trust.container";
	private static final long DELAY = 5000l;

	// TEST VALUES
	private static final String TEST_TRUSTOR_ID = "university.ict-societies.eu"; // MUST MATCH THE CLOUD NODE!
	private static final String TEST_TRUSTEE_ID = "bob.societies.local"; // ANY STRING WILL DO 
	private static final String TEST_TRUSTEE_ID2 = "arthur.societies.local"; // ANY STRING WILL DO
	private static final double TEST_TRUST_RATING = 1.0d;
	private static final double TEST_TRUST_VALUE_THRESHOLD = 0.4d;

	/** The IIinternalTrustClient service reference. */
	private IDigSigClient trustClient;

	/** The BroadcastReceiver to receive the results via Intents. */
	private BroadcastReceiver receiver;
	
	/** The expected TrustRelationships. */
	private TrustRelationshipBean[] expectedTrustRelationships;
	
	/** The expected TrustRelationship. */
	private TrustRelationshipBean expectedTrustRelationship;
	
	private long testStartTime;
	private long testEndTime;
	private CountDownLatch serviceStartedSignal;
	private CountDownLatch testDoneSignal;

	public TestInternalSecurityClient() {

		super(TestServiceSecurityClientLocal.class);
	}

	protected void setUp() throws Exception {

		super.setUp();

		this.receiver = this.setupBroadcastReceiver();
		this.serviceStartedSignal = new CountDownLatch(1);
		Intent commsIntent = new Intent(getContext(), TestServiceSecurityClientLocal.class);
		LocalSecurityClientBinder binder = (LocalSecurityClientBinder) bindService(commsIntent);
		assertNotNull(binder);
		this.trustClient = (IDigSigClient) binder.getService();
		this.trustClient.startService();
		assertTrue(this.serviceStartedSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}

	protected void tearDown() throws Exception {

		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
		this.unregisterReceiver(this.receiver);
		Thread.sleep(DELAY);
		//ensure that service is shutdown to test if service leakage occurs
		super.shutdownService();
		super.tearDown();
	}

	/**
	 * MUST be run after {@link #testAddDirectTrustEvidence()}. 
	 * 
	 * @throws Exception
	 */
	@MediumTest
	public void testRetrieveTrustRelationship() throws Exception {

		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);

		final TrustedEntityIdBean trusteeId =
				new TrustedEntityIdBean();
		trusteeId.setEntityId(TEST_TRUSTEE_ID);
		trusteeId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustValueTypeBean trustValueType =
				TrustValueTypeBean.USER_PERCEIVED;
		
		// setup expected result
		this.expectedTrustRelationship = new TrustRelationshipBean();
		// USER_PERCEIVED with TEST_TRUSTEE
		this.expectedTrustRelationship.setTrustorId(trustorId);
		this.expectedTrustRelationship.setTrusteeId(trusteeId);
		this.expectedTrustRelationship.setTrustValueType(trustValueType);
		this.expectedTrustRelationship.setTrustValue(new Double(TEST_TRUST_VALUE_THRESHOLD));

		Log.d(TAG, "testRetrieveTrustRelationship start time: " + this.testStartTime);
		String xml = "<xml/>";  // FIXME
		String xmlNodeId = "a";  // FIXME
		try {
			this.trustClient.signXml(xml, xmlNodeId);
		} catch (Exception e) {
			Log.e(TAG, "Failed to retrieve trust relationship: " + e.getLocalizedMessage(), e);
		}
		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
	/**
	 * Create a broadcast receiver
	 * 
	 * @return the created broadcast receiver
	 */
	private BroadcastReceiver setupBroadcastReceiver() {

		Log.d(TAG, "Setting up main broadcast receiver");
		BroadcastReceiver receiver = new MainReceiver();
		getContext().registerReceiver(receiver, this.createTestIntentFilter());
		Log.d(TAG, "Registered main broadcast receiver");

		return receiver;
	}

	/**
	 * Unregister a broadcast receiver
	 * @param receiver
	 */
	private void unregisterReceiver(BroadcastReceiver receiver) {

		Log.d(TAG, "Unregistering broadcast receiver");
		getContext().unregisterReceiver(receiver);
	}

	/**
	 * Broadcast receiver to receive intent return values from service method calls
	 */
	private class MainReceiver extends BroadcastReceiver {

		/*
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d(TAG, "Received action: " + intent.getAction());

			if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				
				final boolean serviceStarted = intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
				Log.d(TAG, "Service started: " + serviceStarted);
				assertTrue("Service not started", serviceStarted);
				TestInternalSecurityClient.this.serviceStartedSignal.countDown();
				return;
				
			} else if (intent.getAction().equals(IDigSigClient.VERIFY_XML)) {

				final Double defaultTrustValue = -1.0d;
				final Double trustValue = intent.getDoubleExtra(
						IDigSigClient.INTENT_RETURN_VALUE_KEY, defaultTrustValue);
				Log.d(TAG, "Retrieved trust value: " + trustValue);
				assertNotNull(trustValue);
				assertFalse(trustValue.equals(defaultTrustValue));
				assertTrue(trustValue > new Double(TEST_TRUST_VALUE_THRESHOLD));

			} 
			TestInternalSecurityClient.this.testEndTime = System.currentTimeMillis();
			Log.d(TAG, intent.getAction() + " elapse time: " 
					+ (TestInternalSecurityClient.this.testEndTime - TestInternalSecurityClient.this.testStartTime));
			TestInternalSecurityClient.this.testDoneSignal.countDown();
		}
	}

	/**
	 * Create a suitable intent filter
	 * @return IntentFilter
	 */
	private IntentFilter createTestIntentFilter() {
		// register broadcast receiver to receive Trust Client return values 
		IntentFilter intentFilter = new IntentFilter();
		Log.d(TAG, "intentFilter.addAction " + IServiceManager.INTENT_SERVICE_STARTED_STATUS); 
		intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
		Log.d(TAG, "intentFilter.addAction " + IDigSigClient.SIGN_XML); 
		intentFilter.addAction(IDigSigClient.SIGN_XML);
		Log.d(TAG, "intentFilter.addAction " + IDigSigClient.VERIFY_XML); 
		intentFilter.addAction(IDigSigClient.VERIFY_XML);

		Log.d(TAG, "created test intentFilter " + intentFilter); 
		return intentFilter;
	}
}
