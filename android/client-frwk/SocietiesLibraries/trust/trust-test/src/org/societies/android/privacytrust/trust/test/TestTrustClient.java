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
package org.societies.android.privacytrust.trust.test;

import java.util.Date;

import org.societies.android.api.common.ADate;
import org.societies.android.api.privacytrust.trust.ITrustClient;
import org.societies.android.privacytrust.trust.container.TestServiceTrustClientLocal;
import org.societies.android.privacytrust.trust.container.TestServiceTrustClientLocal.LocalTrustClientBinder;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * 1. Created identity must be deleted prior to test on XMPP server
 * 2. Ensure that test data in test source matches XMPP server and Virgo details
 */
public class TestTrustClient extends ServiceTestCase<TestServiceTrustClientLocal> {

	private static final String TAG = TestTrustClient.class.getName();
	private static final String CLIENT = "org.societies.android.privacytrust.trust.test";
	private static final int DELAY = 10000;
	private static final int TEST_END_DELAY = 2000;
	
	// TEST VALUES
	private static final String TEST_TRUSTOR_ID = "jane.societies.local";
	private static final String TEST_TRUSTEE_ID = "john.societies.local";
	private static final Double TEST_TRUST_RATING = 1.0d;
	
    private ITrustClient trustClient;
    
    private long testStartTime;
    private long testEndTime;
    private boolean testCompleted;
	
    public TestTrustClient() {

        super(TestServiceTrustClientLocal.class);
    }

	protected void setUp() throws Exception {
		
		super.setUp();
		
        Intent commsIntent = new Intent(getContext(), TestServiceTrustClientLocal.class);
        LocalTrustClientBinder binder = (LocalTrustClientBinder) bindService(commsIntent);
        assertNotNull(binder);
        this.trustClient = (ITrustClient) binder.getService();
        trustClient.startService();
        Thread.sleep(DELAY);
	}

	protected void tearDown() throws Exception {
		
		Thread.sleep(TEST_END_DELAY);
        //ensure that service is shutdown to test if service leakage occurs
        super.shutdownService();
		super.tearDown();
	}

	@MediumTest
	public void testAddDirectTrustEvidence() throws Exception {
		
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		final TrustedEntityIdBean subjectId =
				new TrustedEntityIdBean();
		subjectId.setEntityId(TEST_TRUSTOR_ID);
		subjectId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityIdBean objectId =
				new TrustedEntityIdBean();
		objectId.setEntityId(TEST_TRUSTEE_ID);
		objectId.setEntityType(TrustedEntityTypeBean.CSS);
		
		Log.d(TAG, "testAddDirectTrustEvidence start time: " + this.testStartTime);
        try {
        	this.trustClient.addDirectTrustEvidence(CLIENT, subjectId, objectId, 
        			TrustEvidenceTypeBean.RATED, new ADate(new Date()), TEST_TRUST_RATING);
        } catch (Exception e) {
        	Log.e(TAG, "Failed to add direct trust evidence: " + e.getLocalizedMessage(), e);
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        this.unregisterReceiver(receiver);
        assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrust() throws Exception {
		
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
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
		
		Log.d(TAG, "testRetrieveTrust start time: " + this.testStartTime);
        try {
        	this.trustClient.retrieveTrust(CLIENT, trustorId, trusteeId);
        } catch (Exception e) {
        	Log.e(TAG, "Failed to retrieve trust: " + e.getLocalizedMessage(), e);
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        this.unregisterReceiver(receiver);
        assertTrue(this.testCompleted);
	}
	
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {

        Log.d(TAG, "Setting up main broadcast receiver");
        BroadcastReceiver receiver = new MainReceiver();
        super.getContext().registerReceiver(receiver, this.createTestIntentFilter());
        Log.d(TAG, "Registered main broadcast receiver");

        return receiver;
    }
    
    /**
     * Unregister a broadcast receiver
     * @param receiver
     */
    private void unregisterReceiver(BroadcastReceiver receiver) {
    	
        Log.d(TAG, "Unregistering broadcast receiver");
        super.getContext().unregisterReceiver(receiver);
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
	
	        if (intent.getAction().equals(ITrustClient.RETRIEVE_TRUST_VALUE)) {
	        	
	        	final Double trustValueThreshold = 0.4d;
	        	final Double defaultTrustValue = -1.0d;
	        	final Double trustValue = intent.getDoubleExtra(
	        			ITrustClient.INTENT_RETURN_VALUE_KEY, defaultTrustValue);
	        	Log.d(TAG, "Retrieved trust value: " + trustValue);
	        	assertNotNull(trustValue);
	        	assertFalse(trustValue.equals(defaultTrustValue));
	        	assertTrue(trustValue > trustValueThreshold);
                
	        } else if (intent.getAction().equals(ITrustClient.ADD_DIRECT_TRUST_EVIDENCE)) {
	        	
	        	Log.d(TAG, "Added direct trust evidence");
	        	// TODO the void is method. what to do?
	        } 
	        //signal that test has completed
	        TestTrustClient.this.testCompleted = true;
	        TestTrustClient.this.testEndTime = System.currentTimeMillis();
            Log.d(TAG, intent.getAction() + " elapse time: " 
            		+ (TestTrustClient.this.testEndTime - TestTrustClient.this.testStartTime));
        }
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
        // register broadcast receiver to receive Trust Client return values 
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ITrustClient.RETRIEVE_TRUST_VALUE);
        intentFilter.addAction(ITrustClient.ADD_DIRECT_TRUST_EVIDENCE);
        
        return intentFilter;
    }
}