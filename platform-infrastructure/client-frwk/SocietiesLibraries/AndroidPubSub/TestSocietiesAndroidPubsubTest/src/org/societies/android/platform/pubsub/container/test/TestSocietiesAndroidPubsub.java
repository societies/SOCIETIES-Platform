package org.societies.android.platform.pubsub.container.test;

import java.util.List;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.pubsub.IPubsubService;
import org.societies.android.platform.comms.helper.test.ClientCommunicationMgr;
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
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The AVD must be configured so that the XMPP_DOMAIN value is valid
 * 4. The user XMPP_NEW_IDENTIFIER must be removed prior to running the tests as the destroyMainIdentity
 *   method is not currently functioning.
 *
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

    private static final String SIMPLE_XML_MESSAGE = "<iq from='romeo@montague.net/orchard to='juliet@capulet.com/balcony'> " +
    													"<query xmlns='http://jabber.org/protocol/disco#info'/></iq>";

	
    private static final int DELAY = 10000;
    private BroadcastReceiver receiver;
    private IPubsubService commsService;

	public TestSocietiesAndroidPubsub() {
		super(ServicePlatformPubsubTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.receiver = this.setupPubsubBroadcastReceiver();

		Intent pubsubIntent = new Intent(getContext(), ServicePlatformPubsubTest.class);
		LocalPlatformPubsubBinder binder = (LocalPlatformPubsubBinder) bindService(pubsubIntent);
    	assertNotNull(binder);
    	
    	this.commsService = (IPubsubService) binder.getService();
    	assertTrue(this.commsService instanceof IPubsubService);
    	assertNotNull(this.commsService);

	}

	protected void tearDown() throws Exception {
		unregisterReceiver(this.receiver);
		this.commsService = null;
		super.tearDown();
	}
	
	@MediumTest
	public void testRegistration() throws Exception {
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertTrue(null != ccm);
		
		ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String arg0) {
				fail("Incorrect return object");
			}
			
			public void returnAction(boolean flag) {
				assertTrue(flag);
				ccm.isConnected(new IMethodCallback() {
					
					public void returnAction(String arg0) {
						fail("Incorrect return object");
					}
					
					public void returnAction(boolean flag) {
						assertFalse(flag);
						ccm.configureAgent(XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, new IMethodCallback() {
							
							public void returnAction(String arg0) {
								fail("Incorrect return object");
							}
							
							public void returnAction(boolean flag) {
								assertTrue(flag);
								ccm.login(XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, new IMethodCallback() {
									
									public void returnAction(String loginResult) {
										assertEquals(XMPP_SUCCESSFUL_JID, loginResult);
										
										ccm.register(ELEMENT_NAMES, new ICommCallback() {
											
											public void receiveResult(Stanza stanza, Object object) {
												assertTrue((Boolean) object);
												ccm.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
													
													@Override
													public void returnAction(String arg0) {
														fail("Incorrect return object");
													}
													
													@Override
													public void returnAction(boolean result) {
														assertTrue(result);
														ccm.logout(new IMethodCallback() {
															
															public void returnAction(String arg0) {
																fail("Incorrect return object");
															}
															
															public void returnAction(boolean flag) {
																assertTrue(flag);
																ccm.UnRegisterCommManager(new IMethodCallback() {
																	
																	public void returnAction(String arg0) {
																	}
																	
																	public void returnAction(boolean result) {
																		assertTrue(result);
																		assertTrue(ccm.unbindCommsService());
																	}
																});
															}
														});
													}
												});
													

											}
											public void receiveMessage(Stanza arg0, Object arg1) {
												fail("Incorrect callback method called");
											}
											
											public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
												fail("Incorrect callback method called");
											}
											
											public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
												fail("Incorrect callback method called");
											}
											
											public void receiveError(Stanza arg0, XMPPError arg1) {
												fail("Incorrect callback method called");
											}
											
											public List<String> getXMLNamespaces() {
												return NAME_SPACES;
											}
											
											public List<String> getJavaPackages() {
												return CSS_PACKAGES;
											}
										});

									}
									
									public void returnAction(boolean arg0) {
										fail("Incorrect return object");
									}
								});
							}
						});
					}
				});
			}
		});

		Thread.sleep(DELAY);
	}

	@MediumTest
	/**
	 * Test starting the Pubsub service and the AndroidComms service
	 * @throws Exception
	 */
	public void testConnectAndroidComms() throws Exception {
    	
    	this.commsService.bindToAndroidComms(CLIENT, 34533);
    	
		Thread.sleep(DELAY);
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
//    /**
//     * Create broadcast receiver for Android Comms 
//     * and register it
//     * 
//     * @return the created broadcast receiver
//     */
//    private BroadcastReceiver setupCommsBroadcastReceiver() {
//        Log.d(LOG_TAG, "Set up Comms broadcast receiver");
//        
//        BroadcastReceiver commsReceiver = new MainAndroidCommsReceiver();
//        getContext().registerReceiver(commsReceiver, createAndroidCommsIntentFilter());    	
//        Log.d(LOG_TAG, "Register Comms broadcast receiver");
//
//        return commsReceiver;
//    }

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
			}  else if (intent.getAction().equals(IPubsubService.BIND_TO_ANDROID_COMMS)) {
				assertTrue(intent.getBooleanExtra(IPubsubService.INTENT_RETURN_VALUE_KEY, false));
				assertTrue(TestSocietiesAndroidPubsub.this.commsService.unBindFromAndroidComms(CLIENT, 1));
			} 
		}
    }

//    /**
//     * Broadcast receiver to receive intent return values from service method calls
//     */
//    private class MainAndroidCommsReceiver extends BroadcastReceiver {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Log.d(LOG_TAG, "Received action: " + intent.getAction());
//			
//			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
//				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
//			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
//				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//			} else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
//				assertEquals(XMPP_DOMAIN_AUTHORITY, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
//				Log.d(LOG_TAG, "Logged in JID: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//				assertEquals(XMPP_SUCCESSFUL_JID, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
//				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
//			} else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT)) {
//				assertTrue(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
//			}
//		}
//    }

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
