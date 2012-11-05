package org.societies.android.platform.events.test;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.platform.events.ServicePlatformEventsTest;
import org.societies.android.platform.events.ServicePlatformEventsTest.TestPlatformEventsBinder;

import android.content.Intent;
import android.test.ServiceTestCase;


public class TestEventsLocal extends ServiceTestCase <ServicePlatformEventsTest> {
	//Logging tag
	private static final String LOG_TAG = TestEventsLocal.class.getName();
	private static final String CLIENT_PARAM = "org.societies.android.platform.events.test";
	private static final String INTENT_FILTER = "org.societies.android";

//    private boolean connectedtoEvents = false;
//    private IAndroidSocietiesEvents localEvents;

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
     * to run before other tests, as junit uses reflection to find the tests.
     */
    public void testPreconditions() {
    }

	public void testSubscribeToAllEvents() throws Exception {
		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToAllEvents(CLIENT_PARAM);
	}
	
	public void testUnSubscribeToAllEvents() throws Exception {
		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.unSubscribeFromAllEvents(CLIENT_PARAM);
	}
	
	public void testSubscribeToEvent() throws Exception {
		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToEvent(CLIENT_PARAM, IAndroidSocietiesEvents.societiesEvents[0]);
	}
	
	public void testUnSubscribeToEvent() throws Exception {
		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.unSubscribeFromEvent(CLIENT_PARAM, IAndroidSocietiesEvents.societiesEvents[0]);
	}
	
	public void testSubscribeToEvents() throws Exception {
		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToEvents(CLIENT_PARAM, INTENT_FILTER);
	}
	
	public void testUnSubscribeToEvents() throws Exception {
		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.unSubscribeFromEvents(CLIENT_PARAM, INTENT_FILTER);
	}
	
}
