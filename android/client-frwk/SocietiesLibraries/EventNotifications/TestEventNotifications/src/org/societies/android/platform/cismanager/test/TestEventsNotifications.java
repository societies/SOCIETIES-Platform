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
package org.societies.android.platform.cismanager.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class TestEventsNotifications  extends ServiceTestCase<TestServiceFriendsLocal> {

	private static final String LOG_TAG = TestEventsNotifications.class.getName();
	private static final String CLIENT = "org.societies.android.platform.events.testnotifications";
	private static final int DELAY = 10000;
	private static final int TEST_END_DELAY = 2000;

	private long testStartTime, testEndTime;
    private boolean testCompleted;

	/**
	 * @param serviceClass
	 */
	public TestEventsNotifications() {
		super(TestServiceFriendsLocal.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
        Intent commsIntent = new Intent(getContext(), TestServiceFriendsLocal.class);
        FriendsServiceBinder binder = (FriendsServiceBinder) bindService(commsIntent);
        assertNotNull(binder);
	}
	
	protected void tearDown() throws Exception {
		Thread.sleep(TEST_END_DELAY);
        //ensure that service is shutdown to test if service leakage occurs
        shutdownService();
		super.tearDown();
	}

	@MediumTest
	public void testGetAllCisAdverts() throws Exception {
		this.testCompleted = false;
		
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testGetAllCisAdverts start time: " + this.testStartTime);
        try {
        	//this.cisDirectory.findAllCisAdvertisementRecords(CLIENT);
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
        assertTrue(this.testCompleted);
	}
	
	/**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	Log.d(LOG_TAG, "Set up Main broadcast receiver");
    	
    	BroadcastReceiver receiver = new MainReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());
        Log.d(LOG_TAG, "Register Main broadcast receiver");

        return receiver;
    }
    
    /**
     * Unregister a broadcast receiver
     * @param receiver
     */
    private void unregisterReceiver(BroadcastReceiver receiver) {
        Log.d(LOG_TAG, "Unregister broadcast receiver");
        getContext().unregisterReceiver(receiver);
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
	        Log.d(LOG_TAG, "Received action: " + intent.getAction());
	
	        /*
	        if (intent.getAction().equals(ICisDirectory.FIND_ALL_CIS)) {
	        	Parcelable[] objects = (Parcelable[])intent.getParcelableArrayExtra(ICisDirectory.INTENT_RETURN_VALUE);
	        	assertNotNull(objects);
	        	
	        	for(Parcelable object: objects) {
	        		CisAdvertisementRecord advert = (CisAdvertisementRecord) object;
	        		Log.i(LOG_TAG, advert.getId());
	        		Log.i(LOG_TAG, advert.getName());
	        	}
	        	//signal that test has completed
        		TestEventsNotifications.this.testCompleted = true;
	        }
	        */
	        TestEventsNotifications.this.testEndTime = System.currentTimeMillis();
            Log.d(LOG_TAG, intent.getAction() + " elapse time: " + (TestEventsNotifications.this.testEndTime - TestEventsNotifications.this.testStartTime));
        }
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        return intentFilter;
    }
}
