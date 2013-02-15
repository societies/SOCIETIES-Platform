package org.societies.android.api.events;

import org.societies.android.api.css.manager.IServiceManager;

/**
 * Interface defines how 3rd party and optionally, platform components, can 
 * subscribe/publish to Societies platform events. The base eventing system used in Societies
 * is based on XMPP Pubsub event nodes. These event nodes form the basis of inter-node
 * messaging. 
 * 
 * This interface allows local Android components to subscribe/publish XMPP events using a 
 * an Android Intents/ XMPP Pubsub events translation.
 * 
 * This interface should not be used for Android intra-node eventing - use Intents.  
 *
 */
public interface IAndroidSocietiesEvents extends IServiceManager{
	
	/**
	 * Societies Events intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.events.ReturnValue";

	public static final String PUBLISH_EVENT = "org.societies.android.platform.events.PUBLISH_EVENT";
	public static final String SUBSCRIBE_TO_ALL_EVENTS = "org.societies.android.platform.events.SUBSCRIBE_TO_ALL_EVENTS";
	public static final String SUBSCRIBE_TO_EVENT = "org.societies.android.platform.events.SUBSCRIBE_TO_EVENT";
	public static final String SUBSCRIBE_TO_EVENTS = "org.societies.android.platform.events.SUBSCRIBE_TO_EVENTS";
	public static final String UNSUBSCRIBE_FROM_ALL_EVENTS = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_ALL_EVENTS";
	public static final String UNSUBSCRIBE_FROM_EVENT = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_EVENT";
	public static final String UNSUBSCRIBE_FROM_EVENTS = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_EVENTS";
	public static final String NUM_EVENT_LISTENERS = "org.societies.android.platform.events.NUM_EVENT_LISTENERS";

	/**
	 * Android intent Societies platform Pubsub related intents
	 * 
	 */
	//Device Manager intents
	final static String DEVICE_MANAGER_DEVICE_REGISTERED_INTENT = "org.societies.android.device.manager.DEVICE_CONNECTED";
	final static String DEVICE_MANAGER_DEVICE_DISCONNECTED_INTENT = "org.societies.android.device.manager.DEVICE_DISCONNECTED";
	final static String DEVICE_MANAGER_EVENTING_NODE_NAME_INTENT = "org.societies.android.device.manager.EVENTING_NODE_NAME";

	//CSS Manager intents
	final static String CSS_MANAGER_ADD_CSS_NODE_INTENT = "org.societies.android.css.manager.addCSSNode";
	final static String CSS_MANAGER_DEPART_CSS_NODE_INTENT = "org.societies.android.css.manager.departCSSNode";
	
	//Context Manager intents
	final static String CONTEXT_MANAGER_CREATED_INTENT = "org.societies.android.context.CREATED";
	final static String CONTEXT_MANAGER_UPDATED_INTENT = "org.societies.android.context.UPDATED";
	final static String CONTEXT_MANAGER_MODIFIED_INTENT = "org.societies.android.context.MODIFIED";
	final static String CONTEXT_MANAGER_REMOVED_INTENT = "org.societies.android.context.REMOVED";
	
	
	//Array of Societies Android Pubsub Intents
	//N.B. Must be in same order as societiesAndroidEvents array to allow successful translation
	final static String societiesAndroidIntents [] = {CONTEXT_MANAGER_CREATED_INTENT,
										 CONTEXT_MANAGER_UPDATED_INTENT,
										 CONTEXT_MANAGER_MODIFIED_INTENT,
										 CONTEXT_MANAGER_REMOVED_INTENT,
										 CSS_MANAGER_ADD_CSS_NODE_INTENT,
										 CSS_MANAGER_DEPART_CSS_NODE_INTENT,
										 DEVICE_MANAGER_DEVICE_REGISTERED_INTENT,
										 DEVICE_MANAGER_DEVICE_DISCONNECTED_INTENT,
										 DEVICE_MANAGER_EVENTING_NODE_NAME_INTENT
										 };
	
	/**
	 * Android intent Societies platform Pubsub related events
	 * 
	 */
	//Device Manager intents
	final static String DEVICE_MANAGER_DEVICE_REGISTERED_EVENT = "DEVICE_CONNECTED";
	final static String DEVICE_MANAGER_DEVICE_DISCONNECTED_EVENT = "DEVICE_DISCONNECTED";
	final static String DEVICE_MANAGER_EVENTING_NODE_NAME_EVENT = "EVENTING_NODE_NAME";

	//CSS Manager intents
	final static String CSS_MANAGER_ADD_CSS_NODE_EVENT = "addCSSNode";
	final static String CSS_MANAGER_DEPART_CSS_NODE_EVENT = "departCSSNode";
	
	//Context Manager intents
	final static String CONTEXT_MANAGER_CREATED_EVENT = "org/societies/context/change/event/CREATED";
	final static String CONTEXT_MANAGER_UPDATED_EVENT = "org/societies/context/change/event/UPDATED";
	final static String CONTEXT_MANAGER_MODIFIED_EVENT = "org/societies/context/change/event/MODIFIED";
	final static String CONTEXT_MANAGER_REMOVED_EVENT = "org/societies/context/change/event/REMOVED";

	//N.B. Must be in same order as societiesAndroidIntents array to allow successful translation
	final static String societiesAndroidEvents [] = {CONTEXT_MANAGER_CREATED_EVENT,
										 CONTEXT_MANAGER_UPDATED_EVENT,
										 CONTEXT_MANAGER_MODIFIED_EVENT,
										 CONTEXT_MANAGER_REMOVED_EVENT,
										 CSS_MANAGER_ADD_CSS_NODE_EVENT,
										 CSS_MANAGER_DEPART_CSS_NODE_EVENT,
										 DEVICE_MANAGER_DEVICE_REGISTERED_EVENT,
										 DEVICE_MANAGER_DEVICE_DISCONNECTED_EVENT,
										 DEVICE_MANAGER_EVENTING_NODE_NAME_EVENT
										 };

	
	//Array of interface method signatures
	//N.B. Must include any extended interface(s) method arrays
	final static String methodsArray [] = {"subscribeToEvent(String client, String societiesIntent)",
			"subscribeToEvents(String client, String intentFilter)",
			"subscribeToAllEvents(String client)",
			"unSubscribeFromEvent(String client, String societiesIntent)",
			"unSubscribeFromEvents(String client, String intentFilter)",
			"unSubscribeFromAllEvents(String client)",
			"publishEvent(String client, String societiesIntent, Object eventPayload, Class eventClass)",
			"getNumScubscribedNodes(String client)",
			"startService()",
			"stopService()"
	};

	final static String GENERIC_INTENT_PAYLOAD_KEY = "Pubsub_Payload_Key";
	
	/**
	 * Subscribe to a specified Societies platform event (Android Intent)
	 * 
	 * @param client app package 
	 * @param societiesIntent specific event intent
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToEvent(String client, String societiesIntent);
	
	/**
	 * Subscribe to Societies platform events (Android Intent), specified with a filter. All platform events 
	 * that start with the filter will be subscribed to.
	 * 
	 * @param client app package 
	 * @param intentFilter event filter
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToEvents(String client, String intentFilter);
	
	/**
	 * Subscribe to all platform events. This should only be used if really required.
	 * 
	 * @param client app package 
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToAllEvents(String client);
	
	/**
	 * Un-subscribe from a specified Societies platform event (Android Intent)
	 * 
	 * @param client app package 
	 * @param societiesIntent specific event intent
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromEvent(String client, String societiesIntent);
	
	/**
	 * Un-subscribe from Societies platform events (Android Intent), specified with a filter. All platform events 
	 * that start with the filter will be un-subscribed from.
	 * 
	 * @param client app package 
	 * @param intentFilter event filter
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromEvents(String client, String intentFilter);
	
	/**
	 * Un-subscribe from all current platform event subscriptions.
	 * 
	 * @param client app package 
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromAllEvents(String client);
	
	/**
	 * Publish an event to the Societies platform for consumption by other CSS nodes
	 * 
	 * @param client app package
	 * @param societiesIntent specific event intent
	 * @param eventPayload event object
	 * @param eventClass class of event object
	 * @return boolean - returned via Android intent
	 */
	boolean publishEvent(String client, String societiesIntent, Object eventPayload, Class eventClass);
	
	/**
	 * Obtain the current number of subscribed to events
	 * 
	 * @param client
	 * @return int number of subscribed to events
	 */
	int getNumSubscribedNodes(String client);
}
