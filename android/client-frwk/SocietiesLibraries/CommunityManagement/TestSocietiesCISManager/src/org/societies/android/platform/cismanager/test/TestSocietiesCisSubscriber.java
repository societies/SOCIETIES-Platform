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

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.platform.cismanager.container.TestServiceCISManagerLocal;
import org.societies.android.platform.cismanager.container.TestServiceCISManagerLocal.LocalCISManagerBinder;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;

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
 * 
 *
 */
public class TestSocietiesCisSubscriber extends ServiceTestCase<TestServiceCISManagerLocal> {
	private static final String LOG_TAG = TestSocietiesCISManager.class.getName();
	private static final String CLIENT = "org.societies.android.platform.cissubscriber.test";
	private static final int DELAY = 10000;
	private static final int TEST_END_DELAY = 2000;
	
	//TEST VALUES
	private static final String TEST_COMMUNITY_NAME = "Test NameXYZ";
	private static final String TEST_COMMUNITY_DESC = "Test description for community XYZ";
	private static final String TEST_COMMUNITY_TYPE = "community type";
	private static final String TEST_PRIVACY_POLICY = "<privacy />";
	
    private ICisSubscribed cisSubscriber;
    private ICisManager cisManager;
    private String cisId;
    private long testStartTime, testEndTime;
    private boolean testCompleted;
	
    public TestSocietiesCisSubscriber() {
        super(TestServiceCISManagerLocal.class);
    }

	protected void setUp() throws Exception {
		super.setUp();
		
        Intent commsIntent = new Intent(getContext(), TestServiceCISManagerLocal.class);
        LocalCISManagerBinder binder = (LocalCISManagerBinder) bindService(commsIntent);
        assertNotNull(binder);
        this.cisManager = (ICisManager) binder.getService();
        this.cisSubscriber = (ICisSubscribed) binder.getService();
        cisManager.startService();
        cisSubscriber.startService();
        
        Thread.sleep(DELAY);
        createCIS();
	}

	protected void tearDown() throws Exception {
		Thread.sleep(TEST_END_DELAY);
        //ensure that service is shutdown to test if service leakage occurs
        shutdownService();
		super.tearDown();
	}

	public void createCIS() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Criteria crit1 = new Criteria();
		crit1.setAttrib("Location");
		crit1.setAttrib("=");
		crit1.setRank(1);
		crit1.setValue1("Paris");
		crit1.setValue2("Paris");
		
		List<Criteria> crits = new ArrayList<Criteria>();
		MembershipCrit membership =  new MembershipCrit();
		membership.setCriteria(crits);
        try {
        	this.cisManager.createCis(CLIENT, TEST_COMMUNITY_NAME, TEST_COMMUNITY_TYPE, TEST_COMMUNITY_DESC, membership, TEST_PRIVACY_POLICY);
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
        assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testGetCisInfo() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testGetCisInfo start time: " + this.testStartTime);
        try {
        	this.cisSubscriber.getCisInformation(CLIENT, cisId);
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
        assertTrue(this.testCompleted);
	}

	@MediumTest
	public void testGetActivities() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testGetActivities start time: " + this.testStartTime);
        try {
        	this.cisSubscriber.getActivityFeed(CLIENT, cisId);
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
        assertTrue(this.testCompleted);
	}

	
	@MediumTest
	public void testAddActivity() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		Log.d(LOG_TAG, "testAddActivity start time: " + this.testStartTime);
		MarshaledActivity activity = new MarshaledActivity();
		activity.setActor("john.societies.local");
		activity.setVerb("posted");
		activity.setTarget(cisId);
		activity.setObject("Testing a test testcase");
		activity.setPublished("true");
        try {
        	this.cisSubscriber.addActivity(CLIENT, cisId, activity);
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
        BroadcastReceiver receiver = null;

        Log.d(LOG_TAG, "Set up Main broadcast receiver");

        receiver = new MainReceiver();
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
	        Log.d(LOG_TAG, "CisSubscriberTest Received action: " + intent.getAction());
	
	        if (intent.getAction().equals(ICisManager.CREATE_CIS)) {
	        	Community createdCommunity = intent.getParcelableExtra(ICisManager.INTENT_RETURN_VALUE);
	        	cisId = createdCommunity.getCommunityJid();
	        	assertNotNull("Created Community returned was null", createdCommunity);
	        	assertNotNull("Community Jid was null", createdCommunity.getCommunityJid());
	        } 
	        else if (intent.getAction().equals(ICisSubscribed.GET_CIS_INFO)) {
	        	Community info = (Community) intent.getParcelableExtra(ICisSubscribed.INTENT_RETURN_VALUE);
	        	assertNotNull("Community returned was null", info);
	        	Log.d(LOG_TAG, "Community Jid: " + info.getCommunityJid());
	        	assertEquals("Community ID's don't match", info.getCommunityJid(), cisId);
	        } 
	        else if (intent.getAction().equals(ICisSubscribed.GET_ACTIVITY_FEED)) {
	        	MarshaledActivity[] activities = (MarshaledActivity[]) intent.getParcelableArrayExtra(ICisSubscribed.INTENT_RETURN_VALUE);
	        	assertNotNull("Activities Array returned was null", activities);
	        	assertTrue("Count of activities >0", activities.length > 0);
	        	
	        	MarshaledActivity activity = activities[0];
	        	Log.d(LOG_TAG, "Activity: " + activity.getActor() + " " + activity.getVerb() + " " + activity.getObject() + " " + activity.getTarget());
	        	assertNotNull("Activity actor is null", activity.getActor());
	        }
	        else if (intent.getAction().equals(ICisSubscribed.ADD_ACTIVITY)) {
	        	boolean added = intent.getBooleanExtra(ICisSubscribed.INTENT_RETURN_VALUE, false);
	        	assertTrue("Activity not added successfully", added);
	        }
	        //signal that test has completed
        	TestSocietiesCisSubscriber.this.testCompleted = true;
	        TestSocietiesCisSubscriber.this.testEndTime = System.currentTimeMillis();
            Log.d(LOG_TAG, intent.getAction() + " elapse time: " + (TestSocietiesCisSubscriber.this.testEndTime - TestSocietiesCisSubscriber.this.testStartTime));
        }
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ICisSubscribed.GET_CIS_INFO);
        intentFilter.addAction(ICisSubscribed.GET_ACTIVITY_FEED);
        intentFilter.addAction(ICisSubscribed.ADD_ACTIVITY);
        intentFilter.addAction(ICisSubscribed.DELETE_ACTIVITY);
        intentFilter.addAction(ICisSubscribed.GET_CIS_INFO);
        return intentFilter;
    }

}