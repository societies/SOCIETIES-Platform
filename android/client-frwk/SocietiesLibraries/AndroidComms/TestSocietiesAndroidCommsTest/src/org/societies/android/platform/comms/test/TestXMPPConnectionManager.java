package org.societies.android.platform.comms.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

public class TestXMPPConnectionManager extends AndroidTestCase {
	private static final String LOG_TAG = TestXMPPConnectionManager.class.getName();
	private static final int TEST_TIMEOUT = 10000;
	private static final int TEST_SMALL_DELAY = 2000;
	private static final int INVALID_INTENT_INTEGER_EXTRA_VALUE = -999;
	
	// Localised constants. Modify to suit local conditions
	
	private static final String TEST_USER_NAME = "alan";
	private static final String TEST_USER_PASSWORD = "midge";
	private static final String TEST_XMPP_SERVICE_NAME = "societies.bespoke";
	private static final int TEST_XMPP_SERVICE_PORT = 5222;
	private static final boolean TEST_DEBUG_FLAG = false;
	private static final String TEST_NODE_RESOURCE = "testResource";
	private static final String TEST_XMMP_SERVER_IP = null;
	
	
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
		XMPPConnectionManager xmppConnMgr = XMPPConnectionManager.getInstance(getContext());
		assertNotNull(xmppConnMgr);
	}
	
	@MediumTest
	public void testEnableConnection() throws Exception {
		this.testComplete = false;
		this.testLatch = new CountDownLatch(1);
		
		BroadcastReceiver receiver = setupBroadcastReceiver();

		this.xmppConnMgr = XMPPConnectionManager.getInstance(getContext());
		assertNotNull(xmppConnMgr);
		
		xmppConnMgr.enableConnection(createConnectionProperties());
		
		this.testLatch.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
		teardownBroadcastReceiver(receiver);
		assertTrue(this.testComplete);
	}
	
	private XMPPConnectionProperties createConnectionProperties() {
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
        return intentFilter;
    }

    /**
     * Broadcast receiver to receive intent return values from ConnectionManager
     * 
     * TODO: If base API increases extra notification information can be displayed
     * in the more detailed notification style
     */
    private class AndroidCommsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(IConnectionState.XMPP_CONNECTION_CHANGED)) {
				if (intent.getIntExtra(IConnectionState.INTENT_CURRENT_CONNECTION_STATE, INVALID_INTENT_INTEGER_EXTRA_VALUE) == ConnectionState.Connected.ordinal()) {
					try {
						Thread.sleep(TEST_SMALL_DELAY);
						TestXMPPConnectionManager.this.xmppConnMgr.disableConnection();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (intent.getIntExtra(IConnectionState.INTENT_CURRENT_CONNECTION_STATE, INVALID_INTENT_INTEGER_EXTRA_VALUE) == ConnectionState.Disconnected.ordinal()) {
					TestXMPPConnectionManager.this.testComplete = true;
					TestXMPPConnectionManager.this.testLatch.countDown();
				}
			}
		}
    }

}
