package org.societies.android.platform.events.realtest;

import java.util.concurrent.CountDownLatch;

import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.internal.servicelifecycle.IServiceControl;
import org.societies.android.platform.events.realcontainer.RealPlatformEventsTest;
import org.societies.android.platform.events.realcontainer.RealPlatformEventsTest.RealPlatformEventsBinder;
import org.societies.api.schema.cssmanagement.CssEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * Required for test
 * 1. Virgo and Openfire running and correctly configured.
 * 2. Societies Login tester app running and successfully logged in
 * 
 *
 */
public class TestRealPlatformEvents extends ServiceTestCase <RealPlatformEventsTest>  {
	//Logging tag
	private static final String LOG_TAG = TestRealPlatformEvents.class.getName();
	private static final int ALL_EVENTS_COUNT = 13;

	private static final String CLIENT_PARAM_1 = "org.societies.android.platform.events.test";
	private static final String CLIENT_PARAM_2 = "org.societies.android.platform.events.test.alternate";
	private static final String CLIENT_PARAM_3 = "org.societies.android.platform.events.test.other";
	private static final String INTENT_FILTER = "org.societies.android.css.manager";
	
	private static final String CSS_EVENT_TEST_DESCRIPTION = "test Css Event";
	private static final String CSS_EVENT_TEST_TYPE = "test type";
	
	private static final int DELAY = 10000;

	
	private boolean testCompleted;
	private IAndroidSocietiesEvents eventService;
	private CountDownLatch endCondition;

	public TestRealPlatformEvents() {
		super(RealPlatformEventsTest.class);
	}
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

    /**
     * The name 'test preconditions' is a convention to signal that if this
     * test doesn't pass, the test case was not set up properly and it might
     * explain any and all failures in other tests.  This is not guaranteed
     * to run before other tests, as JUnit uses reflection to find the tests.
     */
    @MediumTest
    public void testPreconditions() {
    }

    @MediumTest
	public void testSubscribeToSingleEvent() throws Exception {
		this.endCondition = new CountDownLatch(1);

		this.testCompleted = false;

		BroadcastReceiver receiver = setupSingleEventReceiver();

		Intent eventsIntent = new Intent(getContext(), RealPlatformEventsTest.class);
		RealPlatformEventsBinder binder = (RealPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	this.eventService = (IAndroidSocietiesEvents) binder.getService();

    	this.eventService.startService();

    	this.endCondition.await();
    	this.unregisterReceiver(receiver);
    	assertTrue(this.testCompleted);
    	
	}
//    @MediumTest
	public void testSubscribeToAllEvents() throws Exception {
		this.endCondition = new CountDownLatch(1);

		this.testCompleted = false;

		BroadcastReceiver receiver = setupAllEventsReceiver();

		Intent eventsIntent = new Intent(getContext(), RealPlatformEventsTest.class);
		RealPlatformEventsBinder binder = (RealPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	this.eventService = (IAndroidSocietiesEvents) binder.getService();

    	this.eventService.startService();

    	this.endCondition.await();
    	this.unregisterReceiver(receiver);
    	assertTrue(this.testCompleted);
    	
	}
    @MediumTest
	public void testSubscribeToMultipleEvents() throws Exception {
		this.endCondition = new CountDownLatch(1);
		this.testCompleted = false;

		BroadcastReceiver receiver = setupMultipleEventsReceiver();

		Intent eventsIntent = new Intent(getContext(), RealPlatformEventsTest.class);
		RealPlatformEventsBinder binder = (RealPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	this.eventService = (IAndroidSocietiesEvents) binder.getService();

    	this.eventService.startService();

    	this.endCondition.await();
    	this.unregisterReceiver(receiver);
    	assertTrue(this.testCompleted);
    	
	}

    
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class SingleEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestRealPlatformEvents.this.eventService.getNumSubscribedNodes(CLIENT_PARAM_1);

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestRealPlatformEvents.this.eventService.stopService();
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
		    	TestRealPlatformEvents.this.eventService.subscribeToEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);

			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				
				TestRealPlatformEvents.this.endCondition.countDown();
				TestRealPlatformEvents.this.testCompleted = true;
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS)) {
				assertEquals(1, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
				TestRealPlatformEvents.this.eventService.publishEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, getCssEvent());
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.PUBLISH_EVENT)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT)) {
				Log.d(LOG_TAG, "Received event: " + IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);
				CssEvent event = (CssEvent) intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				assertEquals(CSS_EVENT_TEST_TYPE, event.getType());
				assertEquals(CSS_EVENT_TEST_DESCRIPTION, event.getDescription());
				TestRealPlatformEvents.this.eventService.unSubscribeFromEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);
			}
		}
    }
    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class AllEventReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestRealPlatformEvents.this.eventService.getNumSubscribedNodes(CLIENT_PARAM_1);

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestRealPlatformEvents.this.eventService.stopService();
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
		    	TestRealPlatformEvents.this.eventService.subscribeToAllEvents(CLIENT_PARAM_1);

			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				
				TestRealPlatformEvents.this.endCondition.countDown();
				TestRealPlatformEvents.this.testCompleted = true;
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS)) {
				assertEquals(1, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
				TestRealPlatformEvents.this.eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
			}
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MultipleEventsReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestRealPlatformEvents.this.eventService.getNumSubscribedNodes(CLIENT_PARAM_1);

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestRealPlatformEvents.this.eventService.stopService();
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
		    	TestRealPlatformEvents.this.eventService.subscribeToEvents(CLIENT_PARAM_1, INTENT_FILTER);

			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestRealPlatformEvents.this.endCondition.countDown();
				TestRealPlatformEvents.this.testCompleted = true;
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS)) {
				assertEquals(2, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
				TestRealPlatformEvents.this.eventService.unSubscribeFromEvents(CLIENT_PARAM_1, INTENT_FILTER);
			}
		}
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupMultipleEventsReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        BroadcastReceiver receiver = new MultipleEventsReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register multiple event broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupAllEventsReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        BroadcastReceiver receiver = new AllEventReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register all event broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupSingleEventReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        BroadcastReceiver receiver = new SingleEventReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register single event broadcast receiver");

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
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.PUBLISH_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_NOTSTARTED_EXCEPTION);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_EXCEPTION_INFO);
        
        intentFilter.addAction(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);
        return intentFilter;
    }
    
    private static CssEvent getCssEvent() {
    	CssEvent event = new CssEvent();
    	event.setDescription(CSS_EVENT_TEST_DESCRIPTION);
    	event.setType(CSS_EVENT_TEST_TYPE);
    	return event;
    }

}
