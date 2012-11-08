package org.societies.android.platform.events.test;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.platform.eventscontainer.ServicePlatformEventsTest;
import org.societies.android.platform.eventscontainer.ServicePlatformEventsTest.TestPlatformEventsBinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;


public class TestEventsLocal extends ServiceTestCase <ServicePlatformEventsTest> {
	//Logging tag
	private static final String LOG_TAG = TestEventsLocal.class.getName();
	private static final String CLIENT_PARAM_1 = "org.societies.android.platform.events.test";
	private static final String CLIENT_PARAM_2 = "org.societies.android.platform.events.test.alternate";
	private static final String CLIENT_PARAM_3 = "org.societies.android.platform.events.test.other";
	private static final String INTENT_FILTER = "org.societies.android.css.manager";
	private static final int SLEEP_DELAY = 5000;

	public TestEventsLocal() {
		super(ServicePlatformEventsTest.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
	public void testSubscribeToAllEvents() throws Exception {
        
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	getContext().unregisterReceiver(receiver);
    	
	}
	
    @MediumTest
	public void testSubscribeToOneEvent() throws Exception {
        
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);
    	
    	eventService.unSubscribeFromEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);
    	
       	getContext().unregisterReceiver(receiver);

	}
	@MediumTest
	public void testSubscribeToEvents() throws Exception {
		
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToEvents(CLIENT_PARAM_1, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);
    	
    	eventService.unSubscribeFromEvents(CLIENT_PARAM_1, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);
    	
       	getContext().unregisterReceiver(receiver);
	}
    @MediumTest
	public void testTwoClients() throws Exception {
        
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.subscribeToEvents(CLIENT_PARAM_2, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.subscribeToEvent(CLIENT_PARAM_3, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.unSubscribeFromEvents(CLIENT_PARAM_2, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);
    	
    	eventService.unSubscribeFromEvent(CLIENT_PARAM_3, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);

    	getContext().unregisterReceiver(receiver);
    	
	}

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS)) {
				assertEquals(3, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				assertEquals(1, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				assertEquals(2, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS)) {
				assertEquals(0, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT)) {
				assertEquals(0, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				assertEquals(0, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			}
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
     * Create an alternate broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupAlternateBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
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
        
        return intentFilter;

    }
}
