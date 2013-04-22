package org.societies.android.platform.comms.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPConnection;
import org.societies.android.platform.comms.state.IConnectionState;
import org.societies.android.platform.comms.state.IConnectionState.ConnectionState;
import org.societies.android.platform.comms.state.XMPPConnectionManager;
import org.societies.android.platform.comms.state.XMPPConnectionProperties;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
/**
 * This test suite tests the Android Comms XMPPConnectionManager which models the aSmack {@link XMPPConnection} as a 
 * Finite State Machine.
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

public class TestXMPPConnectionManager extends AndroidTestCase {
	private static final String LOG_TAG = TestXMPPConnectionManager.class.getName();
	private static final int TEST_TIMEOUT = 10000;
	private static final int TEST_SMALL_DELAY = 2000;
	
	
	private final static String TEST_CLIENT = "testClient";
	private final static long TEST_ENABLE_REMOTE_CALL_ID = 8989898;
	private final static long TEST_DISABLE_REMOTE_CALL_ID = 9898696;
	
	// Localised constants. Modify to suit local conditions
	
	private static final String TEST_USER_NAME = "alan";
	private static final String TEST_USER_PASSWORD = "midge";
	private static final String TEST_INVALID_USER_NAME = "mozilla";
	private static final String TEST_INVALID_USER_PASSWORD = "smog";
	private static final String TEST_XMPP_SERVICE_NAME = "societies.bespoke";
	private static final String TEST_INVALID_XMPP_SERVICE_NAME = "societies.war";
	private static final int TEST_XMPP_SERVICE_PORT = 5222;
	private static final boolean TEST_DEBUG_FLAG = false;
	private static final String TEST_NODE_RESOURCE = "testResource";
	private static final String TEST_XMMP_SERVER_IP = null;
	private static final String TEST_USER_JID = TEST_USER_NAME + "/" + TEST_NODE_RESOURCE;
	
	
	private boolean testComplete;
	private CountDownLatch testLatch;
	private XMPPConnectionManager xmppConnMgr;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.xmppConnMgr = null;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	@MediumTest
	public void testGetInstance() throws Exception {
		XMPPConnectionManager xmppConnMgr = new XMPPConnectionManager();
		assertNotNull(xmppConnMgr);
	}
	
	@MediumTest
	public void testEnableConnection() throws Exception {
		this.testComplete = false;
		this.testLatch = new CountDownLatch(1);
		
		BroadcastReceiver receiver = setupBroadcastReceiver();

		this.xmppConnMgr = new XMPPConnectionManager();
		assertNotNull(xmppConnMgr);
		
		xmppConnMgr.enableConnection(createValidConnectionProperties(), getContext(), TEST_USER_JID, TEST_CLIENT, TEST_ENABLE_REMOTE_CALL_ID);
		
		this.testLatch.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
		teardownBroadcastReceiver(receiver);
		assertTrue(this.testComplete);
	}
	
	@MediumTest
	public void testBadCredentials() throws Exception {
		this.testComplete = false;
		this.testLatch = new CountDownLatch(1);
		
		BroadcastReceiver receiver = setupBadCredentialsReceiver();

		this.xmppConnMgr = new XMPPConnectionManager();
		assertNotNull(xmppConnMgr);
		
		xmppConnMgr.enableConnection(createInValidConnectionProperties(), getContext(), TEST_USER_JID, TEST_CLIENT, TEST_ENABLE_REMOTE_CALL_ID);
		
		this.testLatch.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
		teardownBroadcastReceiver(receiver);
		assertTrue(this.testComplete);
	}
	
//	@MediumTest
	/**
	 * Requires that XMPP server is not operational
	 * @throws Exception
	 */
	public void testBadXMPPServer() throws Exception {
		this.testComplete = false;
		this.testLatch = new CountDownLatch(1);
		
		BroadcastReceiver receiver = setupBadXMPPServerReceiver();

		this.xmppConnMgr = new XMPPConnectionManager();
		assertNotNull(xmppConnMgr);
		
		xmppConnMgr.enableConnection(createValidConnectionProperties(), getContext(), TEST_USER_JID, TEST_CLIENT, TEST_ENABLE_REMOTE_CALL_ID);
		
		this.testLatch.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
		teardownBroadcastReceiver(receiver);
		assertTrue(this.testComplete);
	}
	
	private XMPPConnectionProperties createValidConnectionProperties() {
		XMPPConnectionProperties connectionProps = new XMPPConnectionProperties();
		connectionProps.setDebug(TEST_DEBUG_FLAG);
		connectionProps.setHostIP(TEST_XMMP_SERVER_IP);
		connectionProps.setNodeResource(TEST_NODE_RESOURCE);
		connectionProps.setServiceName(TEST_XMPP_SERVICE_NAME);
		connectionProps.setServicePort(TEST_XMPP_SERVICE_PORT);
		connectionProps.setUserName(TEST_USER_NAME);
		connectionProps.setPassword(TEST_USER_PASSWORD);
		return connectionProps;
	}
	
	private XMPPConnectionProperties createInValidConnectionProperties() {
		XMPPConnectionProperties connectionProps = new XMPPConnectionProperties();
		connectionProps.setDebug(TEST_DEBUG_FLAG);
		connectionProps.setHostIP(TEST_XMMP_SERVER_IP);
		connectionProps.setNodeResource(TEST_NODE_RESOURCE);
		connectionProps.setServiceName(TEST_XMPP_SERVICE_NAME);
		connectionProps.setServicePort(TEST_XMPP_SERVICE_PORT);
		connectionProps.setUserName(TEST_INVALID_USER_NAME);
		connectionProps.setPassword(TEST_INVALID_USER_PASSWORD);
		return connectionProps;
	}
	
	private XMPPConnectionProperties createInValidXMPPServerProperties() {
		XMPPConnectionProperties connectionProps = new XMPPConnectionProperties();
		connectionProps.setDebug(TEST_DEBUG_FLAG);
		connectionProps.setHostIP(TEST_XMMP_SERVER_IP);
		connectionProps.setNodeResource(TEST_NODE_RESOURCE);
		connectionProps.setServiceName(TEST_INVALID_XMPP_SERVICE_NAME);
		connectionProps.setServicePort(TEST_XMPP_SERVICE_PORT);
		connectionProps.setUserName(TEST_USER_NAME);
		connectionProps.setPassword(TEST_USER_PASSWORD);
		return connectionProps;
	}
	
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up connectivity changes broadcast receiver");
        
        BroadcastReceiver receiver = new AndroidCommsReceiver();
        getContext().registerReceiver(receiver, createIntentFilter());    
	    Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBadCredentialsReceiver() {
        Log.d(LOG_TAG, "Set up bad credentials broadcast receiver");
        
        BroadcastReceiver receiver = new BadCredentialsReceiver();
        getContext().registerReceiver(receiver, createIntentFilter());    
	    Log.d(LOG_TAG, "Register bad credentials broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBadXMPPServerReceiver() {
        Log.d(LOG_TAG, "Set up bad XMPP server broadcast receiver");
        
        BroadcastReceiver receiver = new BadXMPPServiceReceiver();
        getContext().registerReceiver(receiver, createIntentFilter());    
	    Log.d(LOG_TAG, "Register bad XMPP server broadcast receiver");

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver(BroadcastReceiver receiver) {
    	Log.d(LOG_TAG, "Tear down broadcast receiver");
    	getContext().unregisterReceiver(receiver);
    }

	
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IConnectionState.XMPP_CONNECTION_CHANGED);
        intentFilter.addAction(IConnectionState.XMPP_AUTHENTICATION_FAILURE);
        intentFilter.addAction(IConnectionState.XMPP_NO_NETWORK_FOUND_FAILURE);
        intentFilter.addAction(IConnectionState.XMPP_CONNECTIVITY_FAILURE);
        return intentFilter;
    }

    /**
	 * Broadcast receiver to receive intent return values from ConnectionManager
	 * 
    */
   private class AndroidCommsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			if (intent.getAction().equals(IConnectionState.XMPP_CONNECTION_CHANGED)) {
				if (intent.getIntExtra(IConnectionState.INTENT_CURRENT_CONNECTION_STATE, IConnectionState.INVALID_INTENT_INTEGER_EXTRA_VALUE) == ConnectionState.Connected.ordinal()) {
					try {
						Thread.sleep(TEST_SMALL_DELAY);
						TestXMPPConnectionManager.this.xmppConnMgr.disableConnection(TEST_CLIENT, TEST_DISABLE_REMOTE_CALL_ID);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (intent.getIntExtra(IConnectionState.INTENT_CURRENT_CONNECTION_STATE, IConnectionState.INVALID_INTENT_INTEGER_EXTRA_VALUE) == ConnectionState.Disconnected.ordinal()) {
					TestXMPPConnectionManager.this.testComplete = true;
					TestXMPPConnectionManager.this.testLatch.countDown();
				}
			}
		}
   }
   /**
	 * Broadcast receiver to receive intent return values from ConnectionManager
	 * 
   */
  private class BadCredentialsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			if (intent.getAction().equals(IConnectionState.XMPP_AUTHENTICATION_FAILURE)) {
				assertEquals(IConnectionState.AUTHENTICATION_FAILURE_MESSAGE, intent.getStringExtra(IConnectionState.INTENT_FAILURE_DESCRIPTION));
					TestXMPPConnectionManager.this.testComplete = true;
					TestXMPPConnectionManager.this.testLatch.countDown();
			}
		}
  	}
  /**
	 * Broadcast receiver to receive intent return values from ConnectionManager
	 * 
  */
 private class BadXMPPServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			if (intent.getAction().equals(IConnectionState.XMPP_CONNECTIVITY_FAILURE)) {
				assertEquals(IConnectionState.CONNECTIVITY_FAILURE_MESSAGE, intent.getStringExtra(IConnectionState.INTENT_FAILURE_DESCRIPTION));
					TestXMPPConnectionManager.this.testComplete = true;
					TestXMPPConnectionManager.this.testLatch.countDown();
			}
		}
 	}
}
