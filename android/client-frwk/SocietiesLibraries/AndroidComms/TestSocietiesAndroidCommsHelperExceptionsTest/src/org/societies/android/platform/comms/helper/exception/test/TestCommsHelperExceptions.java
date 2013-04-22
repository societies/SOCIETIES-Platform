package org.societies.android.platform.comms.helper.exception.test;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class TestCommsHelperExceptions extends AndroidTestCase {
	private final static String LOG_TAG = TestCommsHelperExceptions.class.getName(); 
    private static final int LATCH_TIMEOUT = 10000;
	
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

    private boolean testCompleted;
    private CountDownLatch latch;

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

//	@MediumTest
	/**
	 * Test if Societies Android Comms service not available. Requires that the SocietiesAndroidCommsApp
	 * is not installed in the target AVD
	 * @throws Exception
	 */
	public void testIsConnected() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;

		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertNotNull(ccm);

		ccm.bindCommsService(new IMethodCallback() {
			
			@Override
			public void returnException(String exception) {
				assertNotNull(exception);
				Log.d(LOG_TAG, "Bind Exception message: " + exception);
				TestCommsHelperExceptions.this.testCompleted = true;
				TestCommsHelperExceptions.this.latch.countDown();
			}
			
			@Override
			public void returnAction(String arg0) {
				fail("Incorrect return option");
			}
			
			@Override
			public void returnAction(boolean flag) {
				fail("Incorrect return option");
			}
		});
	
		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
//	@MediumTest
	/**
	 * Test if XMPP server is not available.Requires that the SocietiesAndroidCommsApp
	 * is installed in the target AVD
	 * @throws Exception
	 */
	public void testLoginWithNoXMPPServer() throws Exception {
		this.latch = new CountDownLatch(1);
		this.testCompleted = false;

		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertNotNull(ccm);

		ccm.bindCommsService(new IMethodCallback() {
			
			@Override
			public void returnException(String arg0) {
				fail("Incorrect return option");
			}
			
			@Override
			public void returnAction(String arg0) {
				fail("Incorrect return option");
			}
			
			@Override
			public void returnAction(boolean flag) {
				assertTrue(flag);
				ccm.isConnected(new IMethodCallback() {
					
					@Override
					public void returnException(String arg0) {
						fail("Incorrect return option");
					}
					
					@Override
					public void returnAction(String arg0) {
						fail("Incorrect return option");
					}
					
					@Override
					public void returnAction(boolean flag) {
						assertFalse(flag);
						ccm.configureAgent(XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, new IMethodCallback() {
							
							@Override
							public void returnException(String arg0) {
								fail("Incorrect return option");
							}
							
							@Override
							public void returnAction(String arg0) {
								fail("Incorrect return option");
							}
							
							@Override
							public void returnAction(boolean flag) {
								assertTrue(flag);
								ccm.login(XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, new IMethodCallback() {
									
									@Override
									public void returnException(String exception) {
										assertNotNull(exception);
										TestCommsHelperExceptions.this.testCompleted = true;
										TestCommsHelperExceptions.this.latch.countDown();
									}
									
									@Override
									public void returnAction(String arg0) {
										fail("Incorrect return option");
									}
									
									@Override
									public void returnAction(boolean arg0) {
										fail("Incorrect return option");
									}
								});
							}
						});
					}
				});
			}
		});
	
		this.latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}

}
