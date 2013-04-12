package org.societies.android.platform.pubsub.helper.container.test;

import java.util.Collections;
import java.util.List;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.pubsub.IPubsubClient;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.platform.pubsub.helper.PubsubHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.schema.cssmanagement.CssEvent;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * The test suite tests the Pubsub helper class and its interaction with the remote Android Societies Pubsub service.
 * 
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The AVD must be configured so that the XMPP_DOMAIN value is valid
 * 4. The Android Client or Login Tester app must have already logged in successfully
 *
 * Consult http://xmpp.org/extensions/xep-0060.html (Pubsub XMPP XEP) for more details especially 
 * on Pubsub error messages (SEND_IQ_ERROR)
 */

public class TestAndroidPubsubHelper extends AndroidTestCase {
	private static final int DELAY = 10000;
	private static final String LOG_TAG = TestAndroidPubsubHelper.class.getName();
	
    private static final String XMPP_DOMAIN = "societies.bespoke";
    private static final String XMPP_IDENTIFIER = "alan";
    private static final String XMPP_SUCCESSFUL_CLOUD_NODE = XMPP_IDENTIFIER + "." + XMPP_DOMAIN;
    public static final String ADD_CSS_NODE = "addCSSNode";
    public static final String ADD_CSS_NODE_DESC = "Additional node available on CSS";

    public static final String DEPART_CSS_NODE = "departCSSNode";
    public static final String DEPART_CSS_NODE_DESC = "Existing node no longer available on CSS";
    //Test Nodes
    public static final String TEST_PUBSUB_NODE_1 = "testSocietiesNode_11";
    public static final String TEST_PUBSUB_NODE_2 = "testSocietiesNode_21";
    public static final String PUBSUB_NODE_ITEM_ID = "testPubsub2013";

    private static final String PUBSUB_CLASS = "org.societies.api.schema.cssmanagement.CssEvent";
    private static final List<String> classList = Collections.singletonList(PUBSUB_CLASS);

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
		helper.setSubscriberCallback(new EventSubscriber());
		helper.addSimpleClasses(classList);
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

						@Override
						public void returnException(String arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	@MediumTest
	public void testOwnerCreateDelete() throws Exception {
		this.testCompleted = false;
		final PubsubHelper helper = new PubsubHelper(getContext());
		helper.setSubscriberCallback(new EventSubscriber());
		helper.addSimpleClasses(classList);
		helper.bindPubsubService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean flag) {
				assertTrue(flag);
				try {
					helper.ownerCreate(new TestIdentity(), TEST_PUBSUB_NODE_1, new IMethodCallback() {
						
						@Override
						public void returnAction(String result) {
							assertNotNull(result);
							try {
								helper.ownerDelete(new TestIdentity(), TEST_PUBSUB_NODE_1, new IMethodCallback() {
									
									@Override
									public void returnAction(String result) {
										assertNotNull(result);
										helper.unbindCommsService(new IMethodCallback() {
											
											@Override
											public void returnAction(String arg0) {
											}
											
											@Override
											public void returnAction(boolean result) {
												assertTrue(result);
												TestAndroidPubsubHelper.this.testCompleted = true;
											}

											@Override
											public void returnException(
													String arg0) {
												// TODO Auto-generated method stub
												
											}
										});
									}
									
									@Override
									public void returnAction(boolean arg0) {
										fail();
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
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

						@Override
						public void returnException(String arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				} catch (XMPPError e) {
					e.printStackTrace();
					fail();
				} catch (CommunicationException e) {
					e.printStackTrace();
					fail();
				}
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	@MediumTest
	public void testSubscribeToNode() throws Exception {
		this.testCompleted = false;
		final PubsubHelper helper = new PubsubHelper(getContext());
		helper.setSubscriberCallback(new EventSubscriber());
		helper.addSimpleClasses(classList);
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
						helper.subscriberSubscribe(new TestIdentity(), ADD_CSS_NODE, new IMethodCallback() {
							
							@Override
							public void returnAction(String result) {
								try {
									assertNotNull(result);
									helper.subscriberUnsubscribe(new TestIdentity(), ADD_CSS_NODE, new IMethodCallback() {
										
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

												@Override
												public void returnException(
														String arg0) {
													// TODO Auto-generated method stub
													
												}
											});
										}
										
										@Override
										public void returnAction(boolean result) {
											fail();
										}

										@Override
										public void returnException(String arg0) {
											// TODO Auto-generated method stub
											
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

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
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

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	@MediumTest
	public void testSendCatchEvent() throws Exception {
		this.testCompleted = false;
		final PubsubHelper helper = new PubsubHelper(getContext());
		helper.setSubscriberCallback(new EventSubscriber());
		helper.addSimpleClasses(classList);
		helper.bindPubsubService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean flag) {
				assertTrue(flag);
				try {
					helper.ownerCreate(new TestIdentity(), TEST_PUBSUB_NODE_2, new IMethodCallback() {
						
						@Override
						public void returnAction(String result) {
							assertNotNull(result);
							try {
								helper.subscriberSubscribe(new TestIdentity(), TEST_PUBSUB_NODE_2, new IMethodCallback() {
									
									@Override
									public void returnAction(boolean result) {
										fail();
									}
									
									@Override
									public void returnAction(String result) {
										assertNotNull(result);
										try {
											helper.publisherPublish(new TestIdentity(), TEST_PUBSUB_NODE_2, PUBSUB_NODE_ITEM_ID, getCssEvent(), new IMethodCallback() {
												
												@Override
												public void returnAction(String result) {
													assertNotNull(result);
													try {
														
														helper.subscriberUnsubscribe(new TestIdentity(), TEST_PUBSUB_NODE_2, new IMethodCallback() {
															
															@Override
															public void returnAction(boolean result) {
																fail();
															}
															
															@Override
															public void returnAction(String result) {
																assertNotNull(result);
																try {
																	helper.ownerDelete(new TestIdentity(), TEST_PUBSUB_NODE_2, new IMethodCallback() {
																		
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

																				@Override
																				public void returnException(
																						String arg0) {
																					// TODO Auto-generated method stub
																					
																				}
																			});
																		}
																		
																		@Override
																		public void returnAction(boolean arg0) {
																		}

																		@Override
																		public void returnException(
																				String arg0) {
																			// TODO Auto-generated method stub
																			
																		}
																	});
																} catch (XMPPError e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																} catch (CommunicationException e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																}
															}

															@Override
															public void returnException(
																	String arg0) {
																// TODO Auto-generated method stub
																
															}
														});
														
													} catch (XMPPError e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													} catch (CommunicationException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}
												
												@Override
												public void returnAction(boolean arg0) {
													fail();
												}

												@Override
												public void returnException(
														String arg0) {
													// TODO Auto-generated method stub
													
												}
											});
										} catch (XMPPError e) {
											e.printStackTrace();
											fail();
										} catch (CommunicationException e) {
											e.printStackTrace();
											fail();
										}
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							} catch (XMPPError e) {
								e.printStackTrace();
								fail();
							} catch (CommunicationException e) {
								e.printStackTrace();
								fail();
							}
						}
						
						@Override
						public void returnAction(boolean arg0) {
							fail();
						}

						@Override
						public void returnException(String arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				} catch (XMPPError e) {
					e.printStackTrace();
					fail();
				} catch (CommunicationException e) {
					e.printStackTrace();
					fail();
				}
				
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}

	private CssEvent getCssEvent() {
		CssEvent event = new CssEvent();
		event.setDescription("TestCssEvent");
		event.setType("PubsubTester");
		return event;
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
	
	private class EventSubscriber implements ISubscriber {
		@Override
		public void pubsubEvent(IIdentity pubsubServiceID, String node, String itemId, Object item) {
			Log.d(TestAndroidPubsubHelper.LOG_TAG, "Event Node: " + node);
			Log.d(TestAndroidPubsubHelper.LOG_TAG, "Event ID: " + itemId);
			Log.d(TestAndroidPubsubHelper.LOG_TAG, "Event Payload: " + Object.class.getName());
		}
	}
	
}
