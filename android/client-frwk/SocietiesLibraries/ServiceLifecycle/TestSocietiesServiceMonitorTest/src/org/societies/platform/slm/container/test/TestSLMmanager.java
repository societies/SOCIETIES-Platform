package org.societies.platform.slm.container.test;

import java.util.concurrent.CountDownLatch;

import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.platform.slm.container.ServiceManagementTest;
import org.societies.platform.slm.container.ServiceManagementTest.LocalSLMBinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * 1. Created identity must be deleted prior to test on XMPP server
 * 2. Ensure that test data in test source matches XMPP server and Virgo details
 * 
 *
 */
public class TestSLMmanager extends ServiceTestCase<ServiceManagementTest> {
	private static final String LOG_TAG = TestSLMmanager.class.getName();
	private static final String CLIENT = "org.societies.android.platform.slm.test";
	private static final int DELAY = 10000;
	private static final int TEST_END_DELAY = 2000;
	private static final String TEST_IDENTITY = "john.societies.local";
	
    private IServiceDiscovery serviceDisco;
    private long testStartTime, testEndTime;
    private CountDownLatch serviceDiscoConnected;
	
    public TestSLMmanager() {
        super(ServiceManagementTest.class);
    }

	protected void setUp() throws Exception {
		super.setUp();
		
		serviceDiscoConnected = new CountDownLatch(1);
		
        Intent commsIntent = new Intent(getContext(), ServiceManagementTest.class);
        LocalSLMBinder binder = (LocalSLMBinder) bindService(commsIntent);
        assertNotNull(binder);
        this.serviceDisco = (IServiceDiscovery) binder.getService();
        this.serviceDisco.startService();
        
        serviceDiscoConnected.await();
	}

	protected void tearDown() throws Exception {
		Thread.sleep(TEST_END_DELAY);
        //ensure that service is shutdown to test if service leakage occurs
        shutdownService();
		super.tearDown();
	}

	@MediumTest
	public void testGetMyServices() throws Exception {
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testGetMyServices start time: " + this.testStartTime);
        try {
        	this.serviceDisco.getMyServices(CLIENT);
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);

	}
	@MediumTest
	public void testGetServices() throws Exception {
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testGetServices start time: " + this.testStartTime);
        try {
        	this.serviceDisco.getServices(CLIENT, TEST_IDENTITY);
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }
        Thread.sleep(DELAY);
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
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
	
	        if (intent.getAction().equals(IServiceDiscovery.GET_MY_SERVICES) || intent.getAction().equals(IServiceDiscovery.GET_SERVICES)) {
	        	boolean notStarted = intent.getBooleanExtra(IServiceManager.INTENT_NOTSTARTED_EXCEPTION, false);
	        	if (notStarted)
	        		fail("'Service Not Started' returned from service");
	        	else {
	        		Parcelable[] returnedServices =  intent.getParcelableArrayExtra(IServiceDiscovery.INTENT_RETURN_VALUE);
	        		assertNotNull(returnedServices);
	        		for (int i=0; i< returnedServices.length; i++) {
	        			org.societies.api.schema.servicelifecycle.model.Service service = (org.societies.api.schema.servicelifecycle.model.Service) returnedServices[i]; 
	        			Log.d(LOG_TAG, service.getServiceName());
	        		}
	        	}
	        	
                TestSLMmanager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, intent.getAction() + " elapse time: " + (TestSLMmanager.this.testEndTime - TestSLMmanager.this.testStartTime));
	        }
	        else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
	        	boolean started = intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
	        	Log.d(LOG_TAG, "Service started: " + started);
	        	TestSLMmanager.this.serviceDiscoConnected.countDown();
	        }
        }
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(IServiceDiscovery.GET_MY_SERVICES);
        intentFilter.addAction(IServiceDiscovery.GET_SERVICE);
        intentFilter.addAction(IServiceDiscovery.GET_SERVICES);
        intentFilter.addAction(IServiceDiscovery.SEARCH_SERVICES);
        intentFilter.addAction(IServiceDiscovery.INTENT_NOTSTARTED_EXCEPTION);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        
        return intentFilter;
    }

}