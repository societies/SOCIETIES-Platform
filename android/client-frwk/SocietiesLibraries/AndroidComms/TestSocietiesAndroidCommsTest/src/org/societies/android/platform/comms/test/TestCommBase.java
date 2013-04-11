package org.societies.android.platform.comms.test;

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


/**
 * This test suite tests the Android Comms service which provides the gateway to the XMPP Societies communications
 * using asmack.
 * 
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The AVD must be configured so that the XMPP_DOMAIN value is valid
 * 4. The user XMPP_NEW_IDENTIFIER must be removed prior to running the tests as the destroyMainIdentity
 *   method is not currently functioning.
 * 5. Ensure that Android Profiling is not being used, i.e. comment out Debug calls
 *
 */
public class TestCommBase extends ServiceTestCase <ServicePlatformCommsTest> {
	private static final String LOG_TAG = TestCommBase.class.getName();

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

    private static final int LATCH_TIMEOUT = 10000;
    private static final int TEST_SEPARATION = 2000;
    
    //Modify these constants to suit local XMPP server
    
    private static final String XMPP_DOMAIN = "societies.bespoke";
    private static final String XMPP_IDENTIFIER = "alan";
    private static final String XMPP_PASSWORD = "midge";
    private static final String XMPP_BAD_IDENTIFIER = "godzilla";
    private static final String XMPP_BAD_PASSWORD = "smog";
    private static final String XMPP_NEW_IDENTIFIER = "gollum";
    private static final String XMPP_NEW_PASSWORD = "precious";
    private static final String XMPP_RESOURCE = "GalaxyNexus";
    private static final String XMPP_SUCCESSFUL_JID = XMPP_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final String XMPP_NEW_JID = XMPP_NEW_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final int XMPP_PORT = 5222;
    private static final String XMPP_DOMAIN_AUTHORITY = "danode." + XMPP_DOMAIN;

    private static final String SIMPLE_XML_MESSAGE = "<iq from='romeo@montague.net/orchard to='juliet@capulet.com/balcony'> " +
    													"<query xmlns='http://jabber.org/protocol/disco#info'/></iq>";
    
    private CountDownLatch latch;
    private XMPPAgent commsService;
    
	public TestCommBase() {
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
	
	
	@MediumTest
	public void testRegistration() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupRegistrationReceiver();
		
		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testSuccessfulLogin() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupLoginReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}

	@MediumTest
	public void testBadUserLogin() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBadUserReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, this.random.nextLong());
		
		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}

	@MediumTest
	public void testBadPasswordLogin() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupBadPasswordReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, this.random.nextLong());
		
		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}

	@MediumTest
	public void testCreateIdentity() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupCreateIdentityReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}

	@MediumTest
	public void testSendMessage() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;
		BroadcastReceiver receiver = this.setupSendMessageReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (XMPPAgent) binder.getService();
		commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, this.random.nextLong());

		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		unregisterReceiver(receiver);
		assertTrue(this.testCompleted);
	}
	
	//@MediumTest
	//TODO Requires more work or a sample XML message
//	public void testSendIQ() throws Exception {
//		this.latch = new CountDownLatch(1);
//		this.testCompleted = false;
//		BroadcastReceiver receiver = this.setupBroadcastReceiver();
//
//		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
//		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
//    	assertNotNull(binder);
//    	
//	this.commsService = (XMPPAgent) binder.getService();
//
//		try {
//			commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, this.random.nextLong());
//			
//			String jid = commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, this.random.nextLong());
//			Log.d(LOG_TAG, "Logged in JID: " + jid);
//			assertEquals(XMPP_SUCCESSFUL_JID, jid);
//
////			Thread.sleep(DELAY);
//			
//			assertTrue(commsService.isConnected(CLIENT, this.random.nextLong()));
//
//			commsService.sendIQ(CLIENT, SIMPLE_XML_MESSAGE, this.random.nextLong());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		} finally {
//			commsService.logout(CLIENT, this.random.nextLong());
//			unregisterReceiver(receiver);
//			Thread.sleep(DELAY);
//			assertTrue(this.testCompleted);
//
//		}
//
//	}


    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupRegistrationReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up registration receiver");
        
        receiver = new RegistrationReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupLoginReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up login receiver");
        
        receiver = new LoginReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBadPasswordReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up bad password receiver");
        
        receiver = new BadPasswordReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBadUserReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up Bad User broadcast receiver");
        
        receiver = new BadUserReceiver();
        
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register alternative broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupCreateIdentityReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up Create Identity broadcast receiver");
        
        receiver = new CreateIdentityReceiver();
        
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register alternative broadcast receiver");

        return receiver;
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupSendMessageReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up Send Message broadcast receiver");
        
        receiver = new SendMessageReceiver();
        
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register alternative broadcast receiver");

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
    private class RegistrationReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(2, elementNames.length);
				assertEquals(1, nameSpaces.length);
				commsService.register(CLIENT, elementNames, nameSpaces, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				
			} else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
				assertEquals(XMPP_DOMAIN_AUTHORITY, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				
			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				commsService.isConnected(CLIENT, TestCommBase.this.random.nextLong());

			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));

				TestCommBase.this.testCompleted = true;
				TestCommBase.this.latch.countDown();
				
			} else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				
			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.REGISTER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.unregister(CLIENT, elementNames, nameSpaces, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.UNREGISTER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.logout(CLIENT, TestCommBase.this.random.nextLong());
			}
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class LoginReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(2, elementNames.length);
				assertEquals(1, nameSpaces.length);
				commsService.getIdentity(CLIENT, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				commsService.getDomainAuthorityNode(CLIENT, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
				assertEquals(XMPP_DOMAIN_AUTHORITY, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				commsService.logout(CLIENT, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				commsService.isConnected(CLIENT, TestCommBase.this.random.nextLong());

			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));

				TestCommBase.this.testCompleted = true;
				TestCommBase.this.latch.countDown();
				
			} else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				
			} else if (intent.getAction().equals(XMPPAgent.NEW_MAIN_IDENTITY)) {
				assertEquals(XMPP_NEW_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				TestCommBase.this.testCompleted = true;
				
			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.REGISTER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.unregister(CLIENT, elementNames, nameSpaces, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.UNREGISTER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.logout(CLIENT, TestCommBase.this.random.nextLong());
			}
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class BadUserReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.LOGIN_EXCEPTION)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				Log.d(LOG_TAG, intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				TestCommBase.this.testCompleted = true;
				TestCommBase.this.latch.countDown();

			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));

			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_BAD_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, TestCommBase.this.random.nextLong());
			}
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class BadPasswordReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.LOGIN_EXCEPTION)) {
				assertNotNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				Log.d(LOG_TAG, intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
				TestCommBase.this.testCompleted = true;
				TestCommBase.this.latch.countDown();

			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));

			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_BAD_PASSWORD, TestCommBase.this.random.nextLong());
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
			
			if (intent.getAction().equals(XMPPAgent.NEW_MAIN_IDENTITY)) {
				assertEquals(XMPP_NEW_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				TestCommBase.this.testCompleted = true;
				TestCommBase.this.latch.countDown();
				
			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.newMainIdentity(CLIENT, XMPP_NEW_IDENTIFIER, XMPP_DOMAIN, XMPP_NEW_PASSWORD, TestCommBase.this.random.nextLong(), null);
			}
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class SendMessageReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(2, elementNames.length);
				assertEquals(1, nameSpaces.length);
				commsService.register(CLIENT, elementNames, nameSpaces, TestCommBase.this.random.nextLong());
			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				commsService.getDomainAuthorityNode(CLIENT, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
				assertEquals(XMPP_DOMAIN_AUTHORITY, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				commsService.logout(CLIENT, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				commsService.isConnected(CLIENT, TestCommBase.this.random.nextLong());

			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));

				TestCommBase.this.testCompleted = true;
				TestCommBase.this.latch.countDown();
				
			} else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				
			} else if (intent.getAction().equals(XMPPAgent.NEW_MAIN_IDENTITY)) {
				assertEquals(XMPP_NEW_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				TestCommBase.this.testCompleted = true;
				
			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.REGISTER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.sendMessage(CLIENT, SIMPLE_XML_MESSAGE, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.UNREGISTER_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.logout(CLIENT, TestCommBase.this.random.nextLong());
				
			} else if (intent.getAction().equals(XMPPAgent.SEND_MESSAGE_RESULT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				commsService.unregister(CLIENT, elementNames, nameSpaces, TestCommBase.this.random.nextLong());
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
