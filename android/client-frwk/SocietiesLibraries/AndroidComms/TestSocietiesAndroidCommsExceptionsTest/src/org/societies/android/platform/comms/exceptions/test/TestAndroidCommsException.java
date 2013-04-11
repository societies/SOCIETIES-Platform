package org.societies.android.platform.comms.exceptions.test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.platform.comms.container.ServicePlatformCommsTest;
import org.societies.android.platform.comms.container.ServicePlatformCommsTest.TestPlatformCommsBinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class TestAndroidCommsException extends ServiceTestCase <ServicePlatformCommsTest> {
	private static final String LOG_TAG = TestAndroidCommsException.class.getName();

    private static final int LATCH_TIMEOUT = 60000;
    private static final int TEST_SEPARATION = 2000;

    //Test specific paramaters and properties. Adjust to suit local conditions
    private static final String XMPP_VALID_DOMAIN = "societies.bespoke";
    private static final String XMPP_INVALID_DOMAIN = "alien.nation";
    private static final String XMPP_VALID_IDENTIFIER = "alan";
    private static final String XMPP_PASSWORD = "midge";
    private static final String XMPP_BAD_IDENTIFIER = "godzilla";
    private static final String XMPP_BAD_PASSWORD = "smog";
    private static final String XMPP_NEW_IDENTIFIER = "gollum";
    private static final String XMPP_NEW_PASSWORD = "precious";
    private static final String XMPP_RESOURCE = "GalaxyNexus";
    private static final String XMPP_SUCCESSFUL_JID = XMPP_VALID_IDENTIFIER + "@" + XMPP_VALID_DOMAIN + "/" + XMPP_RESOURCE;
    private static final String XMPP_NEW_JID = XMPP_NEW_IDENTIFIER + "@" + XMPP_VALID_DOMAIN + "/" + XMPP_RESOURCE;
    private static final int XMPP_VALID_PORT = 5222;
    private static final int XMPP_INVALID_PORT = 61345;
    private static final String XMPP_VALID_DOMAIN_AUTHORITY = "danode." + XMPP_VALID_DOMAIN;
    private static final String XMPP_INVALID_DOMAIN_AUTHORITY = "danode." + XMPP_INVALID_DOMAIN;

	private final static String CLIENT = "org.comms.test";
	private final String elementNames [] = {"cssManagerMessageBean", "cssManagerResultBean"};
    private final String nameSpaces [] = {"http://societies.org/api/schema/cssmanagement"};
    private static final List<String> CSS_PACKAGES = Arrays.asList("org.societies.api.schema.cssmanagement");
    
    private final List<String> JABBER_ELEMENT_NAMES = Arrays.asList("pubsub", "event", "query");
    private final List<String> JABBER_NAME_SPACES = Arrays.asList(
                                        "http://jabber.org/protocol/pubsub",
                                "http://jabber.org/protocol/pubsub#errors",
                                "http://jabber.org/protocol/pubsub#event",
                                "http://jabber.org/protocol/pubsub#owner",
                                "http://jabber.org/protocol/disco#items");
    private final List<String> JABBER_PACKAGES = Arrays.asList(
                                        "org.jabber.protocol.pubsub",
                                        "org.jabber.protocol.pubsub.errors",
                                        "org.jabber.protocol.pubsub.owner",
                                        "org.jabber.protocol.pubsub.event");

    private Random random;
    private boolean testCompleted;
    private CountDownLatch latch;
    private XMPPAgent commsService;

	public TestAndroidCommsException() {
		super(ServicePlatformCommsTest.class);
		this.random = new Random();
	}


	protected void setUp() throws Exception {
		super.setUp();
		this.latch = null;
		this.commsService = null;
	}

	protected void tearDown() throws Exception {
		Thread.sleep(TEST_SEPARATION);
		super.tearDown();
	}

//	@MediumTest
	/**
	 * Try to login without an XMPP server. Requires that specified XMPP server is not available
	 * 
	 * @throws Exception
	 */
	public void testLoginWithNoXMPPServer() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupNoXMPPServerReceiver();
		
		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_VALID_DOMAIN_AUTHORITY, XMPP_VALID_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
//	@MediumTest
	/**
	 * Try to login with an invalid XMPP server. Requires that specified XMPP server is not available
	 * 
	 * @throws Exception
	 */
	public void testLoginWithInvalidXMPPServer() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupInvalidXMPPServerReceiver();
		
		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_INVALID_DOMAIN_AUTHORITY, XMPP_VALID_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
	
//	@MediumTest
	/**
	 * Try to register a new identity with a valid XMPP server. Requires that specified XMPP server is not available
	 * 
	 * @throws Exception
	 */
	public void testRegisterIdentityWithNoXMPPServer() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setCreateIdentityReceiver();
		
		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_VALID_DOMAIN_AUTHORITY, XMPP_VALID_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}

	@MediumTest
	/**
	 * Try to login with and invalid XMPP server port. Ensure that valid XMPP server is available
	 * 
	 * @throws Exception
	 */
	public void testLoginWithInvalidXMPPServerPort() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupInvalidXMPPServerPortReceiver();
		
		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_VALID_DOMAIN_AUTHORITY, XMPP_INVALID_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}

	
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class NoXMPPServerReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.LOGIN_EXCEPTION)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				Log.d(LOG_TAG, "Login Exception: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				commsService.isConnected(CLIENT, TestAndroidCommsException.this.random.nextLong());
				TestAndroidCommsException.this.testCompleted = true;
				TestAndroidCommsException.this.latch.countDown();

			}  else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY));
				Log.d(LOG_TAG, "Login: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY));
				commsService.isConnected(CLIENT, TestAndroidCommsException.this.random.nextLong());
				TestAndroidCommsException.this.latch.countDown();

			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_VALID_IDENTIFIER, XMPP_VALID_DOMAIN, XMPP_PASSWORD, TestAndroidCommsException.this.random.nextLong());
			} 
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class InvalidXMPPServerReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.LOGIN_EXCEPTION)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				Log.d(LOG_TAG, "Login Exception: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				commsService.isConnected(CLIENT, TestAndroidCommsException.this.random.nextLong());
				TestAndroidCommsException.this.testCompleted = true;
				TestAndroidCommsException.this.latch.countDown();

			}  else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY));
				Log.d(LOG_TAG, "Login: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY));
				commsService.isConnected(CLIENT, TestAndroidCommsException.this.random.nextLong());
				TestAndroidCommsException.this.latch.countDown();

			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_VALID_IDENTIFIER, XMPP_INVALID_DOMAIN, XMPP_PASSWORD, TestAndroidCommsException.this.random.nextLong());
			} 
		}
    }
    
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class InvalidXMPPServerPortReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.LOGIN_EXCEPTION)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				Log.d(LOG_TAG, "Login Exception: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				commsService.isConnected(CLIENT, TestAndroidCommsException.this.random.nextLong());
				TestAndroidCommsException.this.testCompleted = true;
				TestAndroidCommsException.this.latch.countDown();

			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY));
				Log.d(LOG_TAG, "Login: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY));
				commsService.isConnected(CLIENT, TestAndroidCommsException.this.random.nextLong());
				TestAndroidCommsException.this.latch.countDown();

			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_VALID_IDENTIFIER, XMPP_VALID_DOMAIN, XMPP_PASSWORD, TestAndroidCommsException.this.random.nextLong());
			} 
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class CreateIdentityReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.NEW_MAIN_IDENTITY_EXCEPTION)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				Log.d(LOG_TAG, "Identity Registration Exception: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				TestAndroidCommsException.this.testCompleted = true;
				TestAndroidCommsException.this.latch.countDown();
				
			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.newMainIdentity(CLIENT, XMPP_NEW_IDENTIFIER, XMPP_VALID_DOMAIN, XMPP_NEW_PASSWORD, TestAndroidCommsException.this.random.nextLong(), null);
			}
		}
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupNoXMPPServerReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up registration receiver");
        
        receiver = new NoXMPPServerReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupInvalidXMPPServerReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up registration receiver");
        
        receiver = new InvalidXMPPServerReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupInvalidXMPPServerPortReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up registration receiver");
        
        receiver = new InvalidXMPPServerPortReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setCreateIdentityReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up registration receiver");
        
        receiver = new CreateIdentityReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

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
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT);
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_EXCEPTION);
        intentFilter.addAction(XMPPAgent.DESTROY_MAIN_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE);
        intentFilter.addAction(XMPPAgent.GET_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_RESULT);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_ERROR);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_EXCEPTION);
        intentFilter.addAction(XMPPAgent.SEND_IQ_RESULT);
        intentFilter.addAction(XMPPAgent.SEND_IQ_ERROR);
        intentFilter.addAction(XMPPAgent.SEND_IQ_EXCEPTION);
        intentFilter.addAction(XMPPAgent.SEND_MESSAGE_RESULT);
        intentFilter.addAction(XMPPAgent.SEND_MESSAGE_EXCEPTION);
        intentFilter.addAction(XMPPAgent.IS_CONNECTED);
        intentFilter.addAction(XMPPAgent.LOGIN);
        intentFilter.addAction(XMPPAgent.LOGIN_EXCEPTION);
        intentFilter.addAction(XMPPAgent.LOGOUT);
        intentFilter.addAction(XMPPAgent.CONFIGURE_AGENT);
        intentFilter.addAction(XMPPAgent.REGISTER_RESULT);
        intentFilter.addAction(XMPPAgent.REGISTER_EXCEPTION);
        intentFilter.addAction(XMPPAgent.UNREGISTER_RESULT);
        intentFilter.addAction(XMPPAgent.UNREGISTER_EXCEPTION);
        intentFilter.addAction(XMPPAgent.NEW_MAIN_IDENTITY);
        intentFilter.addAction(XMPPAgent.NEW_MAIN_IDENTITY_EXCEPTION);
        
        return intentFilter;
    }
}
