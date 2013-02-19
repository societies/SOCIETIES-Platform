package org.societies.android.platform.cismanager.test;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.platform.cismanager.container.TestServiceCISManagerLocal;
import org.societies.android.platform.cismanager.container.TestServiceCISManagerLocal.LocalCISManagerBinder;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * 1. Created identity must be deleted prior to test on XMPP server
 * 2. Ensure that test data in test source matches XMPP server and Virgo details
 * 
 *
 */
public class TestSocietiesCISManager extends ServiceTestCase<TestServiceCISManagerLocal> {
	private static final String LOG_TAG = TestSocietiesCISManager.class.getName();
	private static final String CLIENT = "org.societies.android.platform.cismanager.test";
	private static final int DELAY = 10000;
	private static final int TEST_END_DELAY = 2000;
	
	//TEST VALUES
	private static final String TEST_COMMUNITY_NAME = "Test NameXYZ";
	private static final String TEST_COMMUNITY_DESC = "Test description for community XYZ";
	private static final String TEST_COMMUNITY_TYPE = "community type";
	private static final String TEST_PRIVACY_POLICY = "<privacy />";
	
    private ICisManager cisManager;
    private long testStartTime, testEndTime;
    private boolean testCompleted;
	
    public TestSocietiesCISManager() {
        super(TestServiceCISManagerLocal.class);
    }

	protected void setUp() throws Exception {
		super.setUp();
		
        Intent commsIntent = new Intent(getContext(), TestServiceCISManagerLocal.class);
        LocalCISManagerBinder binder = (LocalCISManagerBinder) bindService(commsIntent);
        assertNotNull(binder);
        this.cisManager = (ICisManager) binder.getService();
        cisManager.startService();
        Thread.sleep(DELAY);
	}

	protected void tearDown() throws Exception {
		Thread.sleep(TEST_END_DELAY);
        //ensure that service is shutdown to test if service leakage occurs
        shutdownService();
		super.tearDown();
	}

	@MediumTest
	public void testCreateCIS() throws Exception {
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
		
		Log.d(LOG_TAG, "testCreateCIS start time: " + this.testStartTime);
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
	public void testCreateCISMembershipNull() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testCreateCISMembershipNull start time: " + this.testStartTime);
        try {
        	this.cisManager.createCis(CLIENT, TEST_COMMUNITY_NAME + "memberNull", TEST_COMMUNITY_TYPE, TEST_COMMUNITY_DESC, null, TEST_PRIVACY_POLICY);
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
        assertTrue(this.testCompleted);
	}

	@MediumTest
	public void testCreateCISCriteriaNull() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		MembershipCrit membership =  new MembershipCrit();
		
		Log.d(LOG_TAG, "testCreateCIS start time: " + this.testStartTime);
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
	public void testListCommunities() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testCreateNewIdentity start time: " + this.testStartTime);
        try {
        	this.cisManager.getCisList(CLIENT, "all");
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
	        Log.d(LOG_TAG, "Received action: " + intent.getAction());
	
	        if (intent.getAction().equals(ICisManager.CREATE_CIS)) {
	        	Community createdCommunity = intent.getParcelableExtra(ICisManager.INTENT_RETURN_VALUE);
	        	assertNotNull(createdCommunity);
                
	        } else if (intent.getAction().equals(ICisManager.GET_CIS_LIST)) {
	        	Community[] listing = (Community[]) intent.getParcelableArrayExtra(ICisManager.INTENT_RETURN_VALUE);
	        	assertNotNull(listing);
	        	for(Community cis: listing) {
	        		Log.i(LOG_TAG, cis.getCommunityJid());
	        		Log.i(LOG_TAG, cis.getCommunityName());
	        	}
	        	assertTrue(listing.length > 0);
	        } 
	        //signal that test has completed
	        TestSocietiesCISManager.this.testCompleted = true;
	        TestSocietiesCISManager.this.testEndTime = System.currentTimeMillis();
            Log.d(LOG_TAG, intent.getAction() + " elapse time: " + (TestSocietiesCISManager.this.testEndTime - TestSocietiesCISManager.this.testStartTime));
        }
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ICisManager.CREATE_CIS);
        intentFilter.addAction(ICisManager.GET_CIS_LIST);
        intentFilter.addAction(ICisManager.GET_CIS_LIST);
        intentFilter.addAction(ICisManager.INTENT_NOTSTARTED_EXCEPTION);
        return intentFilter;
    }

}