package org.societies.android.platform.comms.test;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.societies.android.api.comms.Callback;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.platform.comms.AndroidCommsBase;
import org.societies.android.platform.comms.container.ServicePlatformCommsTest;
import org.societies.android.platform.comms.container.ServicePlatformCommsTest.TestPlatformCommsBinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import junit.framework.TestCase;

/**
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The AVD must be configured so that the XMPP_DOMAIN value is valid
 * 4. The user XMPP_NEW_IDENTIFIER must be removed prior to running the tests as the destroyMainIdentity
 *   method is not currently functioning.
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

    
    private static final int DELAY = 3000;
    
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
    
	public TestCommBase() {
		super(ServicePlatformCommsTest.class);
	}

    
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	@MediumTest
	public void testRegistration() throws Exception {
		BroadcastReceiver receiver = this.setupBroadcastReceiver();
		
		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	XMPPAgent commsService = (XMPPAgent) binder.getService();

		try {
			commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false);
			Thread.sleep(DELAY);

			commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD);
			Thread.sleep(DELAY);
			
			commsService.isConnected(CLIENT);
			Thread.sleep(DELAY);

			
			try {
				assertEquals(2, elementNames.length);
				assertEquals(1, nameSpaces.length);
				
				commsService.register(CLIENT, elementNames, nameSpaces, new TestCallback());
				Thread.sleep(DELAY);

//				commsService.UnRegisterCommManager(CLIENT);
//				Thread.sleep(DELAY);
				
			} catch (Exception e) {
				Log.e(LOG_TAG, "Exception thrown: " + e.getMessage(), e);
				fail();
			} finally {
				commsService.unregister(CLIENT, elementNames, nameSpaces);
				Thread.sleep(DELAY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			commsService.logout(CLIENT);
			unregisterReceiver(receiver);
			Thread.sleep(DELAY);
		}
	}
	
	@MediumTest
	public void testSuccessfulLogin() throws Exception {
		BroadcastReceiver receiver = this.setupBroadcastReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	XMPPAgent commsService = (XMPPAgent) binder.getService();

		try {
			commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false);
			Thread.sleep(DELAY);
			
			commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD);
			Thread.sleep(DELAY);
			
			commsService.isConnected(CLIENT);
			Thread.sleep(DELAY);
			
			commsService.getIdentity(CLIENT);
			Thread.sleep(DELAY);

			commsService.getDomainAuthorityNode(CLIENT);
			Thread.sleep(DELAY);			

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			commsService.logout(CLIENT);
			unregisterReceiver(receiver);
			Thread.sleep(DELAY);
		}
	}

	@MediumTest
	public void testBadUserLogin() throws Exception {
		BroadcastReceiver receiver = this.setupAlternativeReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	XMPPAgent commsService = (XMPPAgent) binder.getService();

		try {
			commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false);
			Thread.sleep(DELAY);

			commsService.login(CLIENT, XMPP_BAD_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD);
			Thread.sleep(DELAY);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {			
			commsService.logout(CLIENT);
			unregisterReceiver(receiver);
			Thread.sleep(DELAY);
		}
	}

	@MediumTest
	public void testBadPasswordLogin() throws Exception {
		BroadcastReceiver receiver = this.setupAlternativeReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	XMPPAgent commsService = (XMPPAgent) binder.getService();

		try {
			commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false);
			Thread.sleep(DELAY);

			commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_BAD_PASSWORD);
			Thread.sleep(DELAY);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {		
			commsService.logout(CLIENT);
			unregisterReceiver(receiver);
			Thread.sleep(DELAY);
		}
	}

	@MediumTest
	public void testCreateIdentity() throws Exception {
		BroadcastReceiver receiver = this.setupBroadcastReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	XMPPAgent commsService = (XMPPAgent) binder.getService();

		commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false);
		Thread.sleep(DELAY);
			
		try {
			commsService.newMainIdentity(CLIENT, XMPP_NEW_IDENTIFIER, XMPP_DOMAIN, XMPP_NEW_PASSWORD);
			Thread.sleep(DELAY);
			commsService.destroyMainIdentity(CLIENT);
			Thread.sleep(DELAY);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			unregisterReceiver(receiver);
		}
	}

	@MediumTest
	public void testSendMessage() throws Exception {
		BroadcastReceiver receiver = this.setupBroadcastReceiver();

		Intent commsIntent = new Intent(getContext(), ServicePlatformCommsTest.class);
		TestPlatformCommsBinder binder = (TestPlatformCommsBinder) bindService(commsIntent);
    	assertNotNull(binder);
    	
    	XMPPAgent commsService = (XMPPAgent) binder.getService();

		try {
			commsService.configureAgent(CLIENT, XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false);
			Thread.sleep(DELAY);
			
			commsService.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD);

			Thread.sleep(DELAY);
			commsService.isConnected(CLIENT);

			Thread.sleep(DELAY);
			commsService.sendMessage(CLIENT, SIMPLE_XML_MESSAGE);
			Thread.sleep(DELAY);


		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			commsService.logout(CLIENT);
			unregisterReceiver(receiver);
			Thread.sleep(DELAY);
		}

	}
	
	//@MediumTest
	//TODO Requires more work or a sample XML message
	public void testSendIQ() throws Exception {
		BroadcastReceiver receiver = this.setupBroadcastReceiver();

		AndroidCommsBase base = new AndroidCommsBase(getContext(), true);
		assertTrue(null != base);

		try {
			base.setResource(CLIENT, XMPP_RESOURCE);
			base.setPortNumber(CLIENT, XMPP_PORT);
			base.setDomainAuthorityNode(CLIENT, XMPP_DOMAIN_AUTHORITY);
			
			String jid = base.login(CLIENT, XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD);
			Log.d(LOG_TAG, "Logged in JID: " + jid);
			assertEquals(XMPP_SUCCESSFUL_JID, jid);

//			Thread.sleep(DELAY);
			
			assertTrue(base.isConnected(CLIENT));

			base.sendIQ(CLIENT, SIMPLE_XML_MESSAGE, new TestCallback());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			base.logout(CLIENT);
			unregisterReceiver(receiver);
			Thread.sleep(DELAY);
		}

	}

	/**
	 * Callback used with Android Comms for CSSManager
	 *
	 */
	private class TestCallback implements Callback {

		@Override
		public void receiveError(String arg0) {
			Log.d(LOG_TAG, "Callback receiveError");
		}

		@Override
		public void receiveItems(String arg0) {
			Log.d(LOG_TAG, "Callback receiveItems");
		}

		@Override
		public void receiveMessage(String arg0) {
			Log.d(LOG_TAG, "Callback receiveMessage");
		}

		@Override
		public void receiveResult(String arg0) {
			Log.d(LOG_TAG, "Callback receiveResult");
		}
	}

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupAlternativeReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up alternative broadcast receiver");
        
        receiver = new AlternativeReceiver();
        
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
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
				assertEquals(XMPP_DOMAIN_AUTHORITY, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				Log.d(LOG_TAG, "Logged in JID: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
			} else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
			}
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class AlternativeReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
				assertEquals(true, intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
				assertNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
				assertEquals(XMPP_DOMAIN_AUTHORITY, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				Log.d(LOG_TAG, "Logged in JID: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				assertNull(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
			} else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER)) {
				Log.d(LOG_TAG, "Un-Register Comm Manager: " + intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
				assertEquals(true, intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
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
        
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER);
        intentFilter.addAction(XMPPAgent.DESTROY_MAIN_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE);
        intentFilter.addAction(XMPPAgent.GET_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_ITEMS);
        intentFilter.addAction(XMPPAgent.IS_CONNECTED);
        intentFilter.addAction(XMPPAgent.LOGIN);
        intentFilter.addAction(XMPPAgent.LOGOUT);
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER);
        
        return intentFilter;

    }
}
