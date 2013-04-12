package org.societies.android.platform.pubsub.container.test;

import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.pubsub.IPubsubService;
import org.societies.android.platform.pubsub.container.ServicePlatformPubsubTest;
import org.societies.android.platform.pubsub.container.ServicePlatformPubsubTest.LocalPlatformPubsubBinder;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * The test suite tests the Pubsub service and its interaction with the Android Societies Comms service.
 *
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The AVD must be configured so that the XMPP_DOMAIN value is valid
 * 4. The Societies Android Communications app must be installed.
 * 4. The Android Client or Login Tester app must have already logged in successfully
 *
 * Consult http://xmpp.org/extensions/xep-0060.html (Pubsub XMPP XEP) for more details especially 
 * on Pubsub error messages (SEND_IQ_ERROR)
 */

public class TestSocietiesAndroidPubsub  extends ServiceTestCase <ServicePlatformPubsubTest> {
	private static final String LOG_TAG = TestSocietiesAndroidPubsub.class.getName();
	private final static String CLIENT = "org.societies.android.platform.pubsub.container.test";

    //Modify these constants to suit local XMPP server
    
    private static final String XMPP_DOMAIN = "societies.bespoke";
    private static final String XMPP_DOMAIN_NODE = "danode";
    private static final String XMPP_IDENTIFIER = "alan";
    private static final String XMPP_PASSWORD = "midge";
    private static final String XMPP_BAD_IDENTIFIER = "godzilla";
    private static final String XMPP_BAD_PASSWORD = "smog";
    private static final String XMPP_NEW_IDENTIFIER = "gollum";
    private static final String XMPP_NEW_PASSWORD = "precious";
    private static final String XMPP_RESOURCE = "GalaxyNexus";
    private static final String XMPP_SUCCESSFUL_JID = XMPP_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final String XMPP_SUCCESSFUL_CLOUD_NODE = XMPP_IDENTIFIER + "." + XMPP_DOMAIN;
    private static final String XMPP_SUCCESSFUL_DA_NODE = XMPP_DOMAIN_NODE + "." + XMPP_DOMAIN;
    private static final String XMPP_NEW_JID = XMPP_NEW_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final int XMPP_PORT = 5222;
    private static final String XMPP_DOMAIN_AUTHORITY = "danode." + XMPP_DOMAIN;

    //Actual CSS Nodes
    public static final String ADD_CSS_NODE = "addCSSNode";
    public static final String ADD_CSS_NODE_DESC = "Additional node available on CSS";

    public static final String DEPART_CSS_NODE = "departCSSNode";
    public static final String DEPART_CSS_NODE_DESC = "Existing node no longer available on CSS";

    //Test Nodes
    public static final String TEST_PUBSUB_NODE_1 = "testSocietiesNode_1";
    public static final String TEST_PUBSUB_NODE_2 = "testSocietiesNode_2";

	private static final long BIND_CALLBACK_ID = 34533;
	private static final long UNBIND_CALLBACK_ID = 434335;
	private static final long SUBSCRIBE_CALLBACK_ID = 67675;
	private static final long UNSUBSCRIBE_CALLBACK_ID = 997968;
	private static final long OWNER_CREATE_ID = 87865859;
	private static final long OWNER_DELETE_ID = 658595;
	private static final long PUBLISHER_PUBLISH_ID = 76858;
	
    private static final int DELAY = 10000;
    
    private static final String PUBSUB_ITEM = "<entry xmlns='http://www.w3.org/2005/Atom'>" + 
          "<title>Soliloquy</title>" + 
          	"<summary>" + 
          		"To be, or not to be: that is the question:" + 
          		"Whether 'tis nobler in the mind to suffer..." + 
          	 "</summary>" + 
          "<link rel='alternate' type='text/html'" + 
                "href='http://denmark.lit/2003/12/13/atom03'/>" +
          "<id>tag:denmark.lit,2003:entry-32397</id>" + 
          "<published>2003-12-13T18:30:02Z</published>" +
          "<updated>2003-12-13T18:30:02Z</updated>" + 
        "</entry>";
    private IPubsubService commsService;
    private boolean testCompleted;

	public TestSocietiesAndroidPubsub() {
		super(ServicePlatformPubsubTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		Intent pubsubIntent = new Intent(getContext(), ServicePlatformPubsubTest.class);
		LocalPlatformPubsubBinder binder = (LocalPlatformPubsubBinder) bindService(pubsubIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (IPubsubService) binder.getService();
    	assertTrue(this.commsService instanceof IPubsubService);
    	assertNotNull(this.commsService);

	}

	protected void tearDown() throws Exception {
		this.commsService = null;
		super.tearDown();
	}
	
	@MediumTest
	/**
	 * Test starting the Pubsub service and the AndroidComms service
	 * @throws Exception
	 */
	public void testConnectAndroidComms() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupPubsubBroadcastReceiver();
    	this.commsService.bindToAndroidComms(CLIENT, BIND_CALLBACK_ID);
    	
		Thread.sleep(DELAY);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testSubscribeToNode() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupSubscribeBroadcastReceiver();
    	this.commsService.bindToAndroidComms(CLIENT, BIND_CALLBACK_ID);
    	
		Thread.sleep(DELAY);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
		
	}
	@MediumTest
	public void testOwnerCreateNode() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupFullCycleBroadcastReceiver();
    	this.commsService.bindToAndroidComms(CLIENT, BIND_CALLBACK_ID);

    	Thread.sleep(DELAY);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
	@MediumTest
	public void testSendNodeEvent() throws Exception {
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupSendEventBroadcastReceiver();
    	this.commsService.bindToAndroidComms(CLIENT, BIND_CALLBACK_ID);

    	Thread.sleep(DELAY);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
    /**
     * Create broadcast receiver for Android Pubsub 
     * and register it
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupPubsubBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up Pubsub broadcast receiver");
        
        BroadcastReceiver pubsubReceiver = new MainPubsubReceiver();
        getContext().registerReceiver(pubsubReceiver, createPubsubIntentFilter());    	
        Log.d(LOG_TAG, "Register Pubsub broadcast receiver");

        return pubsubReceiver;
    }
    /**
     * Create broadcast receiver for Android Pubsub 
     * and register it
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupSubscribeBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up Pubsub Subscribe broadcast receiver");
        
        BroadcastReceiver pubsubReceiver = new SubscribePubsubReceiver();
        getContext().registerReceiver(pubsubReceiver, createPubsubIntentFilter());    	
        Log.d(LOG_TAG, "Register Pubsub Subscribe broadcast receiver");

        return pubsubReceiver;
    }
    /**
     * Create broadcast receiver for Android Pubsub 
     * and register it
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupFullCycleBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up Pubsub Full Cycle broadcast receiver");
        
        BroadcastReceiver pubsubReceiver = new FullCycleReceiver();
        getContext().registerReceiver(pubsubReceiver, createPubsubIntentFilter());    	
        Log.d(LOG_TAG, "Register Pubsub Full Cycle broadcast receiver");

        return pubsubReceiver;
    }

    /**
     * Create broadcast receiver for Android Pubsub 
     * and register it
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupSendEventBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up Pubsub Send Event broadcast receiver");
        
        BroadcastReceiver pubsubReceiver = new SendEventReceiver();
        getContext().registerReceiver(pubsubReceiver, createPubsubIntentFilter());    	
        Log.d(LOG_TAG, "Register Pubsub Send Event broadcast receiver");

        return pubsubReceiver;
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
    private class MainPubsubReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IPubsubService.DISCO_ITEMS)) {
				assertNotNull(intent.getStringArrayExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_CREATE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_DELETE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_PURGE_ITEMS)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_DELETE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_PUBLISH)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_SUBSCRIBE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_UNSUBSCRIBE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.BIND_TO_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(BIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.unBindFromAndroidComms(CLIENT, UNBIND_CALLBACK_ID);
			} else if (intent.getAction().equals(IPubsubService.UNBIND_FROM_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(UNBIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.testCompleted = true;
			}
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class SubscribePubsubReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IPubsubService.DISCO_ITEMS)) {
				assertNotNull(intent.getStringArrayExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_CREATE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_DELETE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_PURGE_ITEMS)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_DELETE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_PUBLISH)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_SUBSCRIBE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				assertEquals(SUBSCRIBE_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				Log.d(LOG_TAG, "Subscribe response: " + intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				TestSocietiesAndroidPubsub.this.commsService.subscriberUnsubscribe(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, ADD_CSS_NODE, UNSUBSCRIBE_CALLBACK_ID);

			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_UNSUBSCRIBE)) {
				assertEquals(UNSUBSCRIBE_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				TestSocietiesAndroidPubsub.this.commsService.unBindFromAndroidComms(CLIENT, UNBIND_CALLBACK_ID);

			} else if (intent.getAction().equals(IPubsubService.BIND_TO_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(BIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.subscriberSubscribe(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, ADD_CSS_NODE, SUBSCRIBE_CALLBACK_ID);
			} else if (intent.getAction().equals(IPubsubService.UNBIND_FROM_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(UNBIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.testCompleted = true;

			}
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class FullCycleReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IPubsubService.DISCO_ITEMS)) {
				assertNotNull(intent.getStringArrayExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_CREATE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				assertEquals(OWNER_CREATE_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.subscriberSubscribe(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_1, SUBSCRIBE_CALLBACK_ID);

			} else if (intent.getAction().equals(IPubsubService.OWNER_DELETE)) {
				assertEquals(OWNER_DELETE_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				TestSocietiesAndroidPubsub.this.commsService.unBindFromAndroidComms(CLIENT, UNBIND_CALLBACK_ID);
			} else if (intent.getAction().equals(IPubsubService.OWNER_PURGE_ITEMS)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_DELETE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_PUBLISH)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_SUBSCRIBE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				assertEquals(SUBSCRIBE_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.subscriberUnsubscribe(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_1, UNSUBSCRIBE_CALLBACK_ID);

			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_UNSUBSCRIBE)) {
				assertEquals(UNSUBSCRIBE_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				TestSocietiesAndroidPubsub.this.commsService.ownerDelete(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_1, OWNER_DELETE_ID);

			} else if (intent.getAction().equals(IPubsubService.BIND_TO_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(BIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.ownerCreate(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_1, OWNER_CREATE_ID);

			} else if (intent.getAction().equals(IPubsubService.UNBIND_FROM_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(UNBIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.testCompleted = true;
			}
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class SendEventReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IPubsubService.DISCO_ITEMS)) {
				assertNotNull(intent.getStringArrayExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.OWNER_CREATE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				assertEquals(OWNER_CREATE_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.subscriberSubscribe(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_2, SUBSCRIBE_CALLBACK_ID);

			} else if (intent.getAction().equals(IPubsubService.OWNER_DELETE)) {
				assertEquals(OWNER_DELETE_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				TestSocietiesAndroidPubsub.this.commsService.unBindFromAndroidComms(CLIENT, UNBIND_CALLBACK_ID);
			} else if (intent.getAction().equals(IPubsubService.OWNER_PURGE_ITEMS)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_DELETE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(IPubsubService.PUBLISHER_PUBLISH)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				assertEquals(PUBLISHER_PUBLISH_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));

			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_SUBSCRIBE)) {
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				assertEquals(SUBSCRIBE_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.publisherPublish(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_2, "ID11", PUBSUB_ITEM, PUBLISHER_PUBLISH_ID);

			} else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_UNSUBSCRIBE)) {
				assertEquals(UNSUBSCRIBE_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				assertNotNull(intent.getStringExtra(IPubsubService.INTENT_RETURN_VALUE_KEY));
				TestSocietiesAndroidPubsub.this.commsService.ownerDelete(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_2, OWNER_DELETE_ID);

			} else if (intent.getAction().equals(IPubsubService.BIND_TO_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(BIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.commsService.ownerCreate(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_2, OWNER_CREATE_ID);

			} else if (intent.getAction().equals(IPubsubService.UNBIND_FROM_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(UNBIND_CALLBACK_ID, intent.getLongExtra(IPubsubService.INTENT_RETURN_CALL_ID_KEY, 0));
				TestSocietiesAndroidPubsub.this.testCompleted = true;
			} else if (intent.getAction().equals(XMPPAgent.PUBSUB_EVENT)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				Log.d(LOG_TAG, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				TestSocietiesAndroidPubsub.this.commsService.subscriberUnsubscribe(CLIENT, XMPP_SUCCESSFUL_CLOUD_NODE, TEST_PUBSUB_NODE_2, UNSUBSCRIBE_CALLBACK_ID);
			}
		}
    }


    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createPubsubIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IPubsubService.DISCO_ITEMS);
        intentFilter.addAction(IPubsubService.OWNER_CREATE);
        intentFilter.addAction(IPubsubService.OWNER_DELETE);
        intentFilter.addAction(IPubsubService.OWNER_PURGE_ITEMS);
        intentFilter.addAction(IPubsubService.PUBLISHER_DELETE);
        intentFilter.addAction(IPubsubService.PUBLISHER_PUBLISH);
        intentFilter.addAction(IPubsubService.SUBSCRIBER_SUBSCRIBE);
        intentFilter.addAction(IPubsubService.SUBSCRIBER_UNSUBSCRIBE);
        intentFilter.addAction(IPubsubService.BIND_TO_ANDROID_COMMS);
        intentFilter.addAction(IPubsubService.UNBIND_FROM_ANDROID_COMMS);
        intentFilter.addAction(XMPPAgent.PUBSUB_EVENT);
         
        return intentFilter;
    }
    
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createAndroidCommsIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT);
        intentFilter.addAction(XMPPAgent.DESTROY_MAIN_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE);
        intentFilter.addAction(XMPPAgent.GET_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_RESULT);
        intentFilter.addAction(XMPPAgent.IS_CONNECTED);
        intentFilter.addAction(XMPPAgent.LOGIN);
        intentFilter.addAction(XMPPAgent.LOGOUT);
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_EXCEPTION);
        
        return intentFilter;

    }


}
