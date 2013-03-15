package org.societies.android.platform.cssmanager.test;

import java.util.concurrent.CountDownLatch;

import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.android.platform.cssmanager.container.TestServiceCSSManagerLocal;
import org.societies.android.platform.cssmanager.container.TestServiceCSSManagerLocal.LocalCSSManagerBinder;
import org.societies.api.schema.cssmanagement.CssRecord;

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
 * 3. Start Openfire with corresponding configuration details
 * 4. Start Virgo container with xc.properties using identity properties used by these tests
 * 
 */
public class TestSocietiesCSSManager extends ServiceTestCase<TestServiceCSSManagerLocal> {
	private static final String LOG_TAG = TestSocietiesCSSManager.class.getName();
	private static final String CLIENT = "org.societies.android.platform.cssmanager.test";
	private static final int TEST_END_DELAY = 2000;
	private static final int MULTIPLE_LOGIN_COUNT = 2;
	
	private static final String DOMAIN_AUTHORITY_SERVER_PORT = "daServerPort";
	private static final String DOMAIN_AUTHORITY_SERVER_PORT_VALUE = "5222";
	
	private static final String DOMAIN_AUTHORITY_NODE = "daNode";
	private static final String DOMAIN_AUTHORITY_NODE_VALUE = "alan.societies.bespoke";
	
	private static final String LOCAL_CSS_NODE_JID_RESOURCE = "cssNodeResource";
	private static final String LOCAL_CSS_NODE_JID_RESOURCE_VALUE = "Nexus403";
	
	private static final String XMPP_SERVER_NAME = "daServerURI";
	private static final String XMPP_SERVER_NAME_VALUE = "societies.bespoke";
	
	public static final String XMPP_SERVER_NAME_IP = "daServerIP";
	//use AVD to machine host IP address
	public static final String XMPP_SERVER_NAME_IP_VALUE = "10.0.2.2";

	
    public static final String TEST_IDENTITY_1 = "alan";
    public static final String TEST_IDENTITY_2 = "gollum";

    public static final String TEST_INACTIVE_DATE = "20121029";
    public static final String TEST_REGISTERED_DATE = "20120229";
    public static final int TEST_UPTIME = 7799;
    public static final String TEST_EMAIL = "somebody@tssg.org";
    public static final String TEST_FORENAME = "4Name";
    public static final String TEST_HOME_LOCATION = "The Hearth";
    public static final String TEST_IDENTITY_NAME = "Id Name";
    public static final String TEST_IM_ID = "somebody.tssg.org";
    public static final String TEST_NAME = "The CSS";
    public static final String TEST_PASSWORD_1 = "midge";
    public static final String TEST_PASSWORD_2 = "bilbo";
    public static final String TEST_SOCIAL_URI = "sombody@fb.com";
    public static final String TEST_DOMAIN_AUTHORITY = "societies.bespoke";

    private IAndroidCSSManager cssService;
    private long testStartTime, testEndTime;
    private int loginCount;
    private boolean testCompleted;
    private CountDownLatch testFinished;

	
    public TestSocietiesCSSManager() {
        super(TestServiceCSSManagerLocal.class);
    }

	protected void setUp() throws Exception {
		super.setUp();
		
		//Create shared preferences for later use
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DOMAIN_AUTHORITY_SERVER_PORT, DOMAIN_AUTHORITY_SERVER_PORT_VALUE);
		editor.putString(DOMAIN_AUTHORITY_NODE, DOMAIN_AUTHORITY_NODE_VALUE);
		editor.putString(LOCAL_CSS_NODE_JID_RESOURCE, LOCAL_CSS_NODE_JID_RESOURCE_VALUE);
		editor.putString(XMPP_SERVER_NAME, XMPP_SERVER_NAME_VALUE);
		
		editor.commit();

        Intent commsIntent = new Intent(getContext(), TestServiceCSSManagerLocal.class);
        LocalCSSManagerBinder binder = (LocalCSSManagerBinder) bindService(commsIntent);
        assertNotNull(binder);
        this.cssService = (IAndroidCSSManager) binder.getService();
	}

	protected void tearDown() throws Exception {
		Thread.sleep(TEST_END_DELAY);
        //ensure that service is shutdown to test if service leakage occurs
        shutdownService();
		super.tearDown();
	}

//	@MediumTest
	public void testConnectToService() throws Exception {
		testFinished = new CountDownLatch(1);
		
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		
		Log.d(LOG_TAG, "testConnectToService start time: " + this.testStartTime);
        try {
        	this.cssService.loginXMPPServer(CLIENT, getCssRecord());
        } catch (Exception e) {
        	Log.d(LOG_TAG, "");
        }

        this.testFinished.await();
        
        //ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
	
//	@MediumTest
	public void testCreateNewIdentity() throws Exception {
		testFinished = new CountDownLatch(1);
		this.testCompleted = false;

		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		CssRecord cssRecord = new CssRecord();
		cssRecord.setCssIdentity(TEST_IDENTITY_2);
		cssRecord.setDomainServer(TEST_DOMAIN_AUTHORITY);
		cssRecord.setPassword(TEST_PASSWORD_2);

		
		Log.d(LOG_TAG, "testCreateNewIdentity start time: " + this.testStartTime);
        this.cssService.registerXMPPServer(CLIENT, cssRecord);
        
        this.testFinished.await();
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
		assertTrue(this.testCompleted);

	}
	
//	@MediumTest
	public void testLoginCSS() throws Exception {
		testFinished = new CountDownLatch(1);

		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupLoginCSSBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testLoginCSS start time: " + this.testStartTime);

		this.cssService.loginXMPPServer(CLIENT, getCssRecord());

	    this.testFinished.await();
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
		assertTrue(this.testCompleted);

	}
	
	@MediumTest
	public void testLoginXMPPServer() throws Exception {
		testFinished = new CountDownLatch(1);

		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupLoginXMPPBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testLoginXMPP start time: " + this.testStartTime);
		//try two successive login/out cycles
		this.loginCount = 1;

		this.cssService.loginXMPPServer(CLIENT, getCssRecord());

	    this.testFinished.await();
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
	
//	@MediumTest
//	public void testStartServices() throws Exception {
//		testFinished = new CountDownLatch(1);
//
//		this.testCompleted = false;
//		BroadcastReceiver receiver = this.setupAppServicesBroadcastReceiver();
//		this.testStartTime = System.currentTimeMillis();
//		this.testEndTime = this.testStartTime;
//		
//		Log.d(LOG_TAG, "testLoginXMPP start time: " + this.testStartTime);
//
//		this.cssService.startAppServices(CLIENT);
//
//	    this.testFinished.await();
//		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
//        unregisterReceiver(receiver);
//		assertTrue(this.testCompleted);
//	}
	
//	@MediumTest
	public void testLoginCSSUsingIP() throws Exception {
		testFinished = new CountDownLatch(1);

		//Create shared preferences for later use
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(XMPP_SERVER_NAME_IP, XMPP_SERVER_NAME_IP_VALUE);
		
		editor.commit();

		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupLoginCSSIPAddressBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testLoginCSS start time: " + this.testStartTime);

		this.cssService.loginXMPPServer(CLIENT, getCssRecord());

	    this.testFinished.await();
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
		assertTrue(this.testCompleted);

	}

//	@MediumTest
	public void testCreateNewIdentityIPAddress() throws Exception {
		testFinished = new CountDownLatch(1);
		this.testCompleted = false;

		//Create shared preferences for later use
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(XMPP_SERVER_NAME_IP, XMPP_SERVER_NAME_IP_VALUE);
		
		editor.commit();
		
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		CssRecord cssRecord = new CssRecord();
		cssRecord.setCssIdentity(TEST_IDENTITY_2);
		cssRecord.setDomainServer(TEST_DOMAIN_AUTHORITY);
		cssRecord.setPassword(TEST_PASSWORD_2);

		
		Log.d(LOG_TAG, "testCreateNewIdentity start time: " + this.testStartTime);
        this.cssService.registerXMPPServer(CLIENT, cssRecord);
        
        this.testFinished.await();
		//ensure that the broadcast receiver is shutdown to prevent more than one active receiver
        unregisterReceiver(receiver);
		assertTrue(this.testCompleted);

	}

	@MediumTest
	/**
	 * Tests for multiple logins/logout sequences
	 * @throws Exception
	 */
	public void testMultipleLoginCSS() throws Exception {
		testFinished = new CountDownLatch(1);

		this.testCompleted = false;
		this.loginCount = 1;
		BroadcastReceiver receiver = this.setupMultipleLoginCSSBroadcastReceiver();
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;
		
		Log.d(LOG_TAG, "testLoginCSS start time: " + this.testStartTime);
        
		this.cssService.loginXMPPServer(CLIENT, getCssRecord());
         
        this.testFinished.await();
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
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupLoginCSSBroadcastReceiver() {
        BroadcastReceiver receiver = null;

        Log.d(LOG_TAG, "Set up LoginCSSReceiver broadcast receiver");

        receiver = new LoginCSSReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());
        Log.d(LOG_TAG, "Register LoginCSSReceiver broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupLoginXMPPBroadcastReceiver() {
        BroadcastReceiver receiver = null;

        Log.d(LOG_TAG, "Set up LoginXMPPReceiver broadcast receiver");

        receiver = new LoginXMPPReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());
        Log.d(LOG_TAG, "Register LoginXMPPReceiver broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupAppServicesBroadcastReceiver() {
        BroadcastReceiver receiver = null;

        Log.d(LOG_TAG, "Set up App Services Receiver broadcast receiver");

        receiver = new StartServicesReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());
        Log.d(LOG_TAG, "Register App Services Receiver broadcast receiver");

        return receiver;
    }
    
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupLoginCSSIPAddressBroadcastReceiver() {
        BroadcastReceiver receiver = null;

        Log.d(LOG_TAG, "Set up LoginCSSIPAddressReceiver broadcast receiver");

        receiver = new LoginCSSReceiverIPAddress();
        getContext().registerReceiver(receiver, createTestIntentFilter());
        Log.d(LOG_TAG, "Register LoginCSSIPAddressReceiver broadcast receiver");

        return receiver;
    }
    
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupMultipleLoginCSSBroadcastReceiver() {
        BroadcastReceiver receiver = null;

        Log.d(LOG_TAG, "Set up Multiple LoginCSSReceiver broadcast receiver");

        receiver = new MultipleLoginCSSReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());
        Log.d(LOG_TAG, "Register Multiple LoginCSSReceiver broadcast receiver");

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
    private class LoginCSSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();

                Log.d(LOG_TAG, "Login XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
        		TestSocietiesCSSManager.this.cssService.startAppServices(CLIENT);
 
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER_EXCEPTION)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getStringExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Login XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
				TestSocietiesCSSManager.this.testFinished.countDown();
 
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGIN_CSS)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Login CSS elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
	        	
                CssRecord cssRecord = new CssRecord();
        		cssRecord.setCssIdentity(TEST_IDENTITY_1 + "@" + TEST_DOMAIN_AUTHORITY);

        		TestSocietiesCSSManager.this.cssService.logoutCSS(CLIENT, cssRecord);
        		
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_CSS)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout CSS elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                TestSocietiesCSSManager.this.cssService.stopAppServices(CLIENT);
               
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
				TestSocietiesCSSManager.this.testCompleted = true;
				TestSocietiesCSSManager.this.testFinished.countDown();

	        } else if (intent.getAction().equals(IAndroidCSSManager.START_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Startup services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
                CssRecord cssRecord = new CssRecord();
        		cssRecord.setCssIdentity(TEST_IDENTITY_1 + "@" + TEST_DOMAIN_AUTHORITY);
        		
                TestSocietiesCSSManager.this.cssService.loginCSS(CLIENT, cssRecord);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.STOP_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Stop services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));

                TestSocietiesCSSManager.this.cssService.logoutXMPPServer(CLIENT);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER_EXCEPTION)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getStringExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Login XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
				TestSocietiesCSSManager.this.testFinished.countDown();
	        } 
		}
    }
    
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class StartServicesReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(IAndroidCSSManager.START_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Startup services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
                TestSocietiesCSSManager.this.cssService.stopAppServices(CLIENT);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.STOP_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Stop services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));

                TestSocietiesCSSManager.this.testCompleted = true;
				TestSocietiesCSSManager.this.testFinished.countDown();
	        }
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class LoginXMPPReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();

                Log.d(LOG_TAG, "Login XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
        		TestSocietiesCSSManager.this.cssService.logoutXMPPServer(CLIENT);
 
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
                if (TestSocietiesCSSManager.this.loginCount < MULTIPLE_LOGIN_COUNT) {
                    TestSocietiesCSSManager.this.loginCount++;
                    try {
                    	TestSocietiesCSSManager.this.cssService.loginXMPPServer(CLIENT, getCssRecord());
                    } catch (Exception e) {
                    	Log.d(LOG_TAG, "");
                    	fail();
                    }
                } else {
    				TestSocietiesCSSManager.this.testCompleted = true;
    				TestSocietiesCSSManager.this.testFinished.countDown();
                }
	        } 
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class LoginCSSReceiverIPAddress extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();

                Log.d(LOG_TAG, "Login XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
        		TestSocietiesCSSManager.this.cssService.startAppServices(CLIENT);
 
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGIN_CSS)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Login CSS elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
	        	
                CssRecord cssRecord = new CssRecord();
        		cssRecord.setCssIdentity(TEST_IDENTITY_1 + "@" + TEST_DOMAIN_AUTHORITY);

        		TestSocietiesCSSManager.this.cssService.logoutCSS(CLIENT, cssRecord);
        		
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_CSS)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout CSS elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                TestSocietiesCSSManager.this.cssService.stopAppServices(CLIENT);
               
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
				TestSocietiesCSSManager.this.testCompleted = true;
				TestSocietiesCSSManager.this.testFinished.countDown();

	        } else if (intent.getAction().equals(IAndroidCSSManager.START_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Startup services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
                CssRecord cssRecord = new CssRecord();
        		cssRecord.setCssIdentity(TEST_IDENTITY_1 + "@" + TEST_DOMAIN_AUTHORITY);
        		
                TestSocietiesCSSManager.this.cssService.loginCSS(CLIENT, cssRecord);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.STOP_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Stop services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));

                TestSocietiesCSSManager.this.cssService.logoutXMPPServer(CLIENT);
	        }
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MultipleLoginCSSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Login XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                TestSocietiesCSSManager.this.cssService.startAppServices(CLIENT);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGIN_CSS)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Login CSS elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
	        	
                CssRecord cssRecord = new CssRecord();
        		cssRecord.setCssIdentity(TEST_IDENTITY_1 + "@" + TEST_DOMAIN_AUTHORITY);

        		TestSocietiesCSSManager.this.cssService.logoutCSS(CLIENT, cssRecord);
        		
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_CSS)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout CSS elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                TestSocietiesCSSManager.this.cssService.stopAppServices(CLIENT);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                if (TestSocietiesCSSManager.this.loginCount < MULTIPLE_LOGIN_COUNT) {
                    TestSocietiesCSSManager.this.loginCount++;
                    try {
                    	TestSocietiesCSSManager.this.cssService.loginXMPPServer(CLIENT, getCssRecord());
                    } catch (Exception e) {
                    	Log.d(LOG_TAG, "");
                    	fail();
                    }
                } else {
    				TestSocietiesCSSManager.this.testCompleted = true;
    				TestSocietiesCSSManager.this.testFinished.countDown();
                }
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.START_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));
                
                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Startup services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                
                CssRecord cssRecord = new CssRecord();
        		cssRecord.setCssIdentity(TEST_IDENTITY_1 + "@" + TEST_DOMAIN_AUTHORITY);
        		
                TestSocietiesCSSManager.this.cssService.loginCSS(CLIENT, cssRecord);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.STOP_APP_SERVICES)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Stop services elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));

                TestSocietiesCSSManager.this.cssService.logoutXMPPServer(CLIENT);
	        }
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
	        Log.d(LOG_TAG, "Received action: " + intent.getAction());
	
	        if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Login XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
                TestSocietiesCSSManager.this.cssService.logoutXMPPServer(CLIENT);
                
	        } else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Logout XMPP elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
				TestSocietiesCSSManager.this.testCompleted = true;
				TestSocietiesCSSManager.this.testFinished.countDown();
				
	        } else if (intent.getAction().equals(IAndroidCSSManager.REGISTER_XMPP_SERVER)) {
                assertTrue(intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false));
                assertNotNull(intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY));

                TestSocietiesCSSManager.this.testEndTime = System.currentTimeMillis();
                Log.d(LOG_TAG, "Register identity elapse time: " + (TestSocietiesCSSManager.this.testEndTime - TestSocietiesCSSManager.this.testStartTime));
				TestSocietiesCSSManager.this.testCompleted = true;
				TestSocietiesCSSManager.this.testFinished.countDown();
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

        intentFilter.addAction(IAndroidCSSManager.ACCEPT_FRIEND_REQUEST);
        intentFilter.addAction(IAndroidCSSManager.CHANGE_CSS_NODE_STATUS);
        intentFilter.addAction(IAndroidCSSManager.GET_ANDROID_CSS_RECORD);
        intentFilter.addAction(IAndroidCSSManager.GET_CSS_FRIENDS);
        intentFilter.addAction(IAndroidCSSManager.GET_FRIEND_REQUESTS);
        intentFilter.addAction(IAndroidCSSManager.LOGIN_CSS);
        intentFilter.addAction(IAndroidCSSManager.LOGIN_XMPP_SERVER);
        intentFilter.addAction(IAndroidCSSManager.LOGIN_XMPP_SERVER_EXCEPTION);
        intentFilter.addAction(IAndroidCSSManager.LOGOUT_CSS);
        intentFilter.addAction(IAndroidCSSManager.LOGOUT_XMPP_SERVER);
        intentFilter.addAction(IAndroidCSSManager.LOGOUT_XMPP_SERVER_EXCEPTION);
        intentFilter.addAction(IAndroidCSSManager.MODIFY_ANDROID_CSS_RECORD);
        intentFilter.addAction(IAndroidCSSManager.READ_PROFILE_REMOTE);
        intentFilter.addAction(IAndroidCSSManager.REGISTER_CSS);
        intentFilter.addAction(IAndroidCSSManager.REGISTER_CSS_DEVICE);
        intentFilter.addAction(IAndroidCSSManager.REGISTER_XMPP_SERVER);
        intentFilter.addAction(IAndroidCSSManager.REGISTER_XMPP_SERVER_EXCEPTION);
        intentFilter.addAction(IAndroidCSSManager.SEND_FRIEND_REQUEST);
        intentFilter.addAction(IAndroidCSSManager.SET_PRESENCE_STATUS);
        intentFilter.addAction(IAndroidCSSManager.SUGGESTED_FRIENDS);
        intentFilter.addAction(IAndroidCSSManager.SYNCH_PROFILE);
        intentFilter.addAction(IAndroidCSSManager.UNREGISTER_CSS);
        intentFilter.addAction(IAndroidCSSManager.UNREGISTER_XMPP_SERVER);
        intentFilter.addAction(IAndroidCSSManager.START_APP_SERVICES);
        intentFilter.addAction(IAndroidCSSManager.STOP_APP_SERVICES);

        return intentFilter;
    }

    private static CssRecord getCssRecord() {
		CssRecord cssRecord = new CssRecord();
		cssRecord.setCssIdentity(TEST_IDENTITY_1);
		cssRecord.setDomainServer(TEST_DOMAIN_AUTHORITY);
		cssRecord.setPassword(TEST_PASSWORD_1);
		return cssRecord;
    }
}