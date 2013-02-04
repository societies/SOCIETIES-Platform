package org.societies.android.platform.pubsub.helper.container.test;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.platform.pubsub.helper.PubsubHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

public class TestAndroidPubsubHelper extends AndroidTestCase {
	private static final int DELAY = 10000;
    private static final String XMPP_DOMAIN = "societies.bespoke";
    private static final String XMPP_IDENTIFIER = "alan";
    private static final String XMPP_SUCCESSFUL_CLOUD_NODE = XMPP_IDENTIFIER + "." + XMPP_DOMAIN;
    public static final String ADD_CSS_NODE = "addCSSNode";
    public static final String ADD_CSS_NODE_DESC = "Additional node available on CSS";

    public static final String DEPART_CSS_NODE = "departCSSNode";
    public static final String DEPART_CSS_NODE_DESC = "Existing node no longer available on CSS";
    private boolean testCompleted;

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@MediumTest
	public void testBindToPubsubService() throws Exception {
		this.testCompleted = false;
		final PubsubHelper helper = new PubsubHelper(getContext());
		helper.bindPubsubService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				assertTrue(resultFlag);
				if (resultFlag) {
					helper.unbindCommsService(new IMethodCallback() {
						
						@Override
						public void returnAction(String arg0) {
							fail();
						}
						
						@Override
						public void returnAction(boolean result) {
							assertTrue(result);
							TestAndroidPubsubHelper.this.testCompleted = true;
						}
					});
				}
			}
		});
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testSubscribeToNode() throws Exception {
		this.testCompleted = false;
		final PubsubHelper helper = new PubsubHelper(getContext());
		helper.bindPubsubService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				assertTrue(resultFlag);
				if (resultFlag) {
					try {
						helper.subscriberSubscribe(new TestIdentity(), ADD_CSS_NODE, new TestSubscriber(), new IMethodCallback() {
							
							@Override
							public void returnAction(String result) {
								try {
									assertNotNull(result);
									helper.subscriberUnsubscribe(new TestIdentity(), ADD_CSS_NODE, new TestSubscriber(), new IMethodCallback() {
										
										@Override
										public void returnAction(String result) {
											assertNotNull(result);
											
											helper.unbindCommsService(new IMethodCallback() {
													
												@Override
												public void returnAction(String arg0) {
													fail();
												}
												
												@Override
												public void returnAction(boolean result) {
													assertTrue(result);
													TestAndroidPubsubHelper.this.testCompleted = true;
												}
											});
										}
										
										@Override
										public void returnAction(boolean result) {
											fail();
										}
									});
								} catch (XMPPError e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									fail();
								} catch (CommunicationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									fail();
								}
							}
							
							@Override
							public void returnAction(boolean result) {
								fail();
							}
						});
					} catch (XMPPError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						fail();
					} catch (CommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						fail();
					}
				}
			}
		});
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	
	private class TestSubscriber implements ISubscriber {

		@Override
		public void pubsubEvent(IIdentity arg0, String arg1, String arg2,
				Object arg3) {
		}
		
	}
	private class TestIdentity implements IIdentity {

		@Override
		public String getBareJid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDomain() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getJid() {
			return XMPP_SUCCESSFUL_CLOUD_NODE;
		}

		@Override
		public IdentityType getType() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
