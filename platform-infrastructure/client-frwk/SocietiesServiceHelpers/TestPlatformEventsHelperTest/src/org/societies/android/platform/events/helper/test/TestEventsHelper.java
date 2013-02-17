package org.societies.android.platform.events.helper.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.platform.events.helper.EventsHelper;

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
					helper.subscribeToEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IMethodCallback() {
						
						@Override
						public void returnAction(String result) {
							fail();
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							assertTrue(resultFlag);
							try {
								helper.getNumSubscribedNodes(new IMethodCallback() {
									
									@Override
									public void returnAction(String result) {
										assertEquals(1, Integer.parseInt(result));
										try {
											helper.unSubscribeFromEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IMethodCallback() {
												
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
					helper.subscribeToEvents(INTENTS_FILTER, new IMethodCallback() {
						
						@Override
						public void returnAction(String result) {
							fail();
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							assertTrue(resultFlag);
							try {
								helper.getNumSubscribedNodes(new IMethodCallback() {
									
									@Override
									public void returnAction(String result) {
										assertEquals(NUM_FILTER_EVENTS, Integer.parseInt(result));
										try {
											helper.unSubscribeFromEvents(INTENTS_FILTER, new IMethodCallback() {
												
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
}
