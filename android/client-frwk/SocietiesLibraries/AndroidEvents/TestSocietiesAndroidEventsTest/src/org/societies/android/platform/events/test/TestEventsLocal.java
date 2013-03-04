package org.societies.android.platform.events.test;

import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.platform.eventscontainer.ServicePlatformEventsTest;
import org.societies.android.platform.eventscontainer.ServicePlatformEventsTest.TestPlatformEventsBinder;
import org.societies.api.schema.cssmanagement.CssEvent;

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
	private static final int SLEEP_DELAY = 10000;
	
	private static final int ALL_EVENTS_COUNT = 15;
	
	private boolean testCompleted;
	private IAndroidSocietiesEvents eventService;
	
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
		this.testCompleted = false;

		BroadcastReceiver receiver = setupAllEventReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	this.eventService = (IAndroidSocietiesEvents) binder.getService();
    	this.eventService.startService();

     	Thread.sleep(SLEEP_DELAY);
    	
    	this.unregisterReceiver(receiver);
    	assertTrue(this.testCompleted);
	}
	
    @MediumTest
	public void testSubscribeToOneEvent() throws Exception {
		this.testCompleted = false;
        
		BroadcastReceiver receiver = setupSingleEventReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	this.eventService = (IAndroidSocietiesEvents) binder.getService();
    	this.eventService.startService();
 
    	Thread.sleep(SLEEP_DELAY);

    	this.unregisterReceiver(receiver);
    	assertTrue(this.testCompleted);

	}
	@MediumTest
	public void testSubscribeToEvents() throws Exception {
		this.testCompleted = false;
		
		BroadcastReceiver receiver = setupMultipleEventReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	this.eventService = (IAndroidSocietiesEvents) binder.getService();
    	this.eventService.startService();
     	
    	Thread.sleep(SLEEP_DELAY);

    	this.unregisterReceiver(receiver);
    	assertTrue(this.testCompleted);
	}
    @MediumTest
	public void testThreeClients() throws Exception {
		this.testCompleted = false;
       
		BroadcastReceiver receiver = setupAlternateBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	this.eventService = (IAndroidSocietiesEvents) binder.getService();
    	this.eventService.startService();

    	Thread.sleep(SLEEP_DELAY);

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
				TestEventsLocal.this.eventService.getNumSubscribedNodes(CLIENT_PARAM_1);
			    
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.stopService();
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS)) {
				assertEquals(1, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
				TestEventsLocal.this.eventService.publishEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, getCssEvent());
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.PUBLISH_EVENT)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.unSubscribeFromEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.subscribeToEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.testCompleted = true;
			}
		}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MultipleEventReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.getNumSubscribedNodes(CLIENT_PARAM_1);
			    
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.stopService();

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS)) {
				assertEquals(2, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
				TestEventsLocal.this.eventService.unSubscribeFromEvents(CLIENT_PARAM_1, INTENT_FILTER);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.subscribeToEvents(CLIENT_PARAM_1, INTENT_FILTER);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.testCompleted = true;
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
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.getNumSubscribedNodes(CLIENT_PARAM_1);
			    
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.stopService();

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS)) {
				assertEquals(ALL_EVENTS_COUNT, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
				TestEventsLocal.this.eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.subscribeToAllEvents(CLIENT_PARAM_1);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.subscribeToAllEvents(CLIENT_PARAM_1);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.testCompleted = true;
			}
		}
    }

/**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class AlternativeReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.subscribeToEvents(CLIENT_PARAM_2, INTENT_FILTER);

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
			    
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.subscribeToEvent(CLIENT_PARAM_3, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.unSubscribeFromEvents(CLIENT_PARAM_2, INTENT_FILTER);

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.stopService();
				
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				assertTrue(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
		    	eventService.unSubscribeFromEvent(CLIENT_PARAM_3, IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);
		    	
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.eventService.subscribeToAllEvents(CLIENT_PARAM_1);
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				assertTrue(intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false));
				TestEventsLocal.this.testCompleted = true;
			}
		}
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
        Log.d(LOG_TAG, "Register single broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupMultipleEventReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        BroadcastReceiver receiver = new MultipleEventReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register multiple broadcast receiver");

        return receiver;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupAllEventReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        BroadcastReceiver receiver = new AllEventReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register all broadcast receiver");

        return receiver;
    }
    
    /**
     * Create an alternate broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupAlternateBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        BroadcastReceiver receiver = new AlternativeReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register alternative broadcast receiver");

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
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        
        return intentFilter;
    }
    
    private static CssEvent getCssEvent() {
    	CssEvent event = new CssEvent();
    	event.setDescription("test Css Event");
    	event.setType("test");
    	return event;
    }
}
