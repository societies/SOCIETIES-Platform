package org.societies.android.remote.helper.container.test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
/**
 * The test suite tests the Societies Client app Events serverice helper class and its interaction with the remote Android Societies Events service.
 * 
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The Android Client app must have already logged in successfully
 *
 */

public class TestEventsHelper extends AndroidTestCase {
	private static final String LOG_TAG = TestEventsHelper.class.getName();
	private static final int LATCH_TIME_OUT = 10000;
	private static final String INTENTS_FILTER = "org.societies.android.css.manager";
	private static final int NUM_FILTER_EVENTS = 2;
	private static final String CSS_EVENT_TEST_DESCRIPTION = "test Css Event";
	private static final String CSS_EVENT_TEST_TYPE = "test type";
	
	private boolean testCompleted;
	private long startTime;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.startTime = System.currentTimeMillis();
	}

	protected void tearDown() throws Exception {
		Log.d(LOG_TAG, "Test duration: " + (System.currentTimeMillis() - this.startTime));

		super.tearDown();
	}
	@MediumTest
	public void testServiceConfiguration() throws Exception {
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		
		final EventsHelper helper = new EventsHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				assertTrue(resultFlag);
				helper.tearDownService(new IMethodCallback() {
					
					@Override
					public void returnAction(String result) {
						fail();
					}
					
					@Override
					public void returnAction(boolean resultFlag) {
						assertTrue(resultFlag);
						TestEventsHelper.this.testCompleted = true;
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testSubscribeToEvent() throws Exception {
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		
		final EventsHelper helper = new EventsHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				assertTrue(resultFlag);
				try {
					helper.subscribeToEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IPlatformEventsCallback() {
						
						@Override
						public void returnAction(int result) {
							fail();
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							assertTrue(resultFlag);
							try {
								helper.getNumSubscribedNodes(new IPlatformEventsCallback() {
									
									@Override
									public void returnAction(int result) {
										assertEquals(1, result);
										try {
											helper.publishEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, getCssEvent(), new IPlatformEventsCallback() {
												
												@Override
												public void returnAction(int arg0) {
												}
												
												@Override
												public void returnAction(boolean result) {
													assertTrue(result);
													try {
														helper.unSubscribeFromEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IPlatformEventsCallback() {
															
															@Override
															public void returnAction(int result) {
																fail();
															}
															
															@Override
															public void returnAction(boolean resultFlag) {
																assertTrue(resultFlag);
																helper.tearDownService(new IMethodCallback() {
																	
																	@Override
																	public void returnAction(String result) {
																		fail();
																	}
																	
																	@Override
																	public void returnAction(boolean resultFlag) {
																		assertTrue(resultFlag);
																		TestEventsHelper.this.testCompleted = true;
																		latch.countDown();
																	}
																});
															}
														});
													} catch (PlatformEventsHelperNotConnectedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													
												}
											});
										} catch (PlatformEventsHelperNotConnectedException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}
									
									@Override
									public void returnAction(boolean resultFlag) {
										fail();
									}
								});
							} catch (PlatformEventsHelperNotConnectedException e) {
								e.printStackTrace();
								fail();
							}
						}
					});
				} catch (PlatformEventsHelperNotConnectedException e) {
					e.printStackTrace();
					fail();
				}
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	@MediumTest
	public void testSubscribeToEventWithComplexPayload() throws Exception {
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		
		final EventsHelper helper = new EventsHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				assertTrue(resultFlag);
				try {
					helper.subscribeToEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IPlatformEventsCallback() {
						
						@Override
						public void returnAction(int result) {
							fail();
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							assertTrue(resultFlag);
							try {
								helper.getNumSubscribedNodes(new IPlatformEventsCallback() {
									
									@Override
									public void returnAction(int result) {
										assertEquals(1, result);
										try {
											helper.publishEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, getExpFeedbackResultBean(), new IPlatformEventsCallback() {
												
												@Override
												public void returnAction(int arg0) {
												}
												
												@Override
												public void returnAction(boolean result) {
													assertTrue(result);
													try {
														helper.unSubscribeFromEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IPlatformEventsCallback() {
															
															@Override
															public void returnAction(int result) {
																fail();
															}
															
															@Override
															public void returnAction(boolean resultFlag) {
																assertTrue(resultFlag);
																helper.tearDownService(new IMethodCallback() {
																	
																	@Override
																	public void returnAction(String result) {
																		fail();
																	}
																	
																	@Override
																	public void returnAction(boolean resultFlag) {
																		assertTrue(resultFlag);
																		TestEventsHelper.this.testCompleted = true;
																		latch.countDown();
																	}
																});
															}
														});
													} catch (PlatformEventsHelperNotConnectedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													
												}
											});
										} catch (PlatformEventsHelperNotConnectedException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}
									
									@Override
									public void returnAction(boolean resultFlag) {
										fail();
									}
								});
							} catch (PlatformEventsHelperNotConnectedException e) {
								e.printStackTrace();
								fail();
							}
						}
					});
				} catch (PlatformEventsHelperNotConnectedException e) {
					e.printStackTrace();
					fail();
				}
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	@MediumTest
	public void testSubscribeToSomeEvents() throws Exception {
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		
		final EventsHelper helper = new EventsHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				assertTrue(resultFlag);
				try {
					helper.subscribeToEvents(INTENTS_FILTER, new IPlatformEventsCallback() {
						
						@Override
						public void returnAction(int result) {
							fail();
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							assertTrue(resultFlag);
							try {
								helper.getNumSubscribedNodes(new IPlatformEventsCallback() {
									
									@Override
									public void returnAction(int result) {
										assertEquals(NUM_FILTER_EVENTS, result);
										try {
											helper.unSubscribeFromEvents(INTENTS_FILTER, new IPlatformEventsCallback() {
												
												@Override
												public void returnAction(int result) {
													fail();
												}
												
												@Override
												public void returnAction(boolean resultFlag) {
													assertTrue(resultFlag);
													helper.tearDownService(new IMethodCallback() {
														
														@Override
														public void returnAction(String result) {
															fail();
														}
														
														@Override
														public void returnAction(boolean resultFlag) {
															assertTrue(resultFlag);
															TestEventsHelper.this.testCompleted = true;
															latch.countDown();
														}
													});
												}
											});
										} catch (PlatformEventsHelperNotConnectedException e) {
											e.printStackTrace();
											fail();
										}
									}
									
									@Override
									public void returnAction(boolean resultFlag) {
										fail();
									}
								});
							} catch (PlatformEventsHelperNotConnectedException e) {
								e.printStackTrace();
								fail();
							}
						}
					});
				} catch (PlatformEventsHelperNotConnectedException e) {
					e.printStackTrace();
					fail();
				}
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	/**
	 * Try using the service with setting it up
	 * @throws Exception
	 */
	public void testIllegalServiceUsage() throws Exception {
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		
		final EventsHelper helper = new EventsHelper(getContext());
		
		try {
			helper.subscribeToAllEvents(new IPlatformEventsCallback() {
				
				@Override
				public void returnAction(int result) {
				}
				
				@Override
				public void returnAction(boolean resultFlag) {
				}
			});
			fail();
		} catch (PlatformEventsHelperNotConnectedException p) {
			assertNotNull(p);
			this.testCompleted = true;
		}
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
    private static CssEvent getCssEvent() {
    	CssEvent event = new CssEvent();
    	event.setDescription(CSS_EVENT_TEST_DESCRIPTION);
    	event.setType(CSS_EVENT_TEST_TYPE);
    	return event;
    }

    private static ExpFeedbackResultBean getExpFeedbackResultBean() {
    	ExpFeedbackResultBean bean  = new ExpFeedbackResultBean();
    	bean.setFeedback(new ArrayList<String>());
    	bean.setRequestId("requestID1111");
    	return bean;
    }
}
