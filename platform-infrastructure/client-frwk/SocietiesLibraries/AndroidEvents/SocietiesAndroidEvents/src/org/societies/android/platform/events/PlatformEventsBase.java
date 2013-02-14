package org.societies.android.platform.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.pubsub.helper.PubsubHelper;
import org.societies.api.identity.IIdentity;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;

public class PlatformEventsBase implements IAndroidSocietiesEvents, IServiceManager {
	
	//Logging tag
    private static final String LOG_TAG = PlatformEventsBase.class.getName();

	//Pubsub packages
    //TODO: Insert all known event classes
	private static final String CSS_MANAGER_CLASS = "org.societies.api.schema.cssmanagement.CssEvent";
	private static final String CONTEXT_CLASS = "org.societies.api.schema.context.model.CtxIdentifierBean";
	    
    private static final String ALL_EVENT_FILTER = "org.societies";
    private static final String KEY_DIVIDER = "$";

	private Context androidContext;
	private PubsubHelper pubsubClient = null;

	//Synchronised Maps - require manual synchronisation 
	private Map<String, String> subscribedToClientEvents = null;
	private Map<String, Integer> subscribedToEvents = null;
	
	private ArrayList <String> allPlatformEvents = null; 

	private String domainCommsDestination = null;

    private IIdentity cloudNodeIdentity = null;
    private ClientCommunicationMgr ccm;
    private boolean restrictBroadcast;
    private List<String> classList;
    private boolean connectedToComms;
    private boolean connectedToPubsub;


    /**
     * Default constructor
     */
    public PlatformEventsBase(Context androidContext, PubsubHelper pubsubClient, ClientCommunicationMgr ccm, boolean restrictBroadcast) {
    	Log.d(LOG_TAG, "Object created");
    	
    	this.pubsubClient = pubsubClient;
		this.ccm = ccm;
    	this.androidContext = androidContext;
    	this.restrictBroadcast = restrictBroadcast;
    	
    	this.connectedToComms = false;
    	this.connectedToPubsub = false;
    	//tracks the events subscribed to by clients
    	this.subscribedToClientEvents = Collections.synchronizedMap(new HashMap<String, String>());
    	//tracks the events subscribed to Android Pubsub
    	this.subscribedToEvents = Collections.synchronizedMap(new HashMap<String, Integer>());
    	
    	//create list of event classes for Pubsub registration 
        this.classList = new ArrayList<String>();

		this.classList.add(CONTEXT_CLASS);
		this.classList.add(CSS_MANAGER_CLASS);
		
    }

	@Override
	public boolean startService() {
		if (!this.connectedToComms && !this.connectedToPubsub) {
			this.ccm.bindCommsService(new IMethodCallback() {
				
				@Override
				public void returnAction(String result) {
				}
				
				@Override
				public void returnAction(boolean resultFlag) {
					if (resultFlag) {
						PlatformEventsBase.this.connectedToComms = true;
						PlatformEventsBase.this.pubsubClient.bindPubsubService(new IMethodCallback() {
							
							@Override
							public void returnAction(String result) {
							}
							
							@Override
							public void returnAction(boolean resultFlag) {
								if (resultFlag) {
									
									try {
										PlatformEventsBase.this.configureForPubsub();
										PlatformEventsBase.this.connectedToPubsub = true;

									} catch (ClassNotFoundException e) {
										e.printStackTrace();
										resultFlag = false;
									} finally {
										//Send intent
						        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
						        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
						        		PlatformEventsBase.this.androidContext.sendBroadcast(intent);
									}
								}
							}
						});
					}
				}
			});
		}
		return false;
	}

	@Override
	public boolean stopService() {
		if (this.connectedToComms && this.connectedToPubsub) {
			this.pubsubClient.unbindCommsService(new IMethodCallback() {
				
				@Override
				public void returnAction(String result) {
				}
				
				@Override
				public void returnAction(boolean resultFlag) {
					if (resultFlag) {
						PlatformEventsBase.this.connectedToPubsub = false;
						boolean result = PlatformEventsBase.this.ccm.unbindCommsService();
						if (result) {
							PlatformEventsBase.this.connectedToComms = false;
						}
						//Send intent
			    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
			    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, result);
			    		PlatformEventsBase.this.androidContext.sendBroadcast(intent);
					}
				}
			});
		}
		return false;
	}

	public synchronized boolean publishEvent(String client, String societiesIntent, Object eventPayload, Class eventClass) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Event Payload must be specified", null != eventPayload);
		Dbc.require("Event Payload Class type must be specified", null != eventClass);
		Log.d(LOG_TAG, "Invocation of publishEvent for client: " + client);
		return false;
	}

	public synchronized int subscribeToAllEvents(String client) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "Invocation of subscribeToAllEvents for client: " + client);
		
		return this.subscribeToEvents(client, ALL_EVENT_FILTER);
	}

	public int subscribeToEvent(String client, String intent) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent must be specified", null != intent && intent.length() > 0);
		Log.d(LOG_TAG, "Invocation of subscribeToEvent for client: " + client + " and intent: " + intent);

		//store client/event
		synchronized (this.subscribedToClientEvents) {
			Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			
			this.subscribedToClientEvents.put(this.generateClientEventKey(client, intent), intent);

			Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());

			ArrayList<String> events = new ArrayList<String>();
			events.add(intent);
			
	    	SubscribeToPubsub subPubSub = new SubscribeToPubsub(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT, client); 
	    	subPubSub.execute(events);
		}

    	return 0;
	}

	public synchronized int subscribeToEvents(String client, String intentFilter) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent filter must be specified", null != intentFilter && intentFilter.length() > 0);
		Log.d(LOG_TAG, "Invocation of subscribeToEvents for client: " + client + " and filter: " + intentFilter);

		ArrayList<String> targetEvents = null;
		String returnIntent;
		
		//Generate a list of all possible events or a filtered subset
		if (ALL_EVENT_FILTER.equals(intentFilter)) {
			targetEvents = this.getAllPlatformEvents();
			returnIntent = IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS;
		} else {
			targetEvents = this.getFilteredEvents(intentFilter);
			returnIntent = IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS;
		}
		
		
		synchronized (this.subscribedToClientEvents) {
			Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			
			ArrayList<String> unSubscribedEvents = new ArrayList<String>();
   
			//add the client/intent pair if they do not already exist
			for (String filteredEvent: targetEvents) {
				//store client/event
				if (!this.subscribedToClientEvents.containsKey(this.generateClientEventKey(client, filteredEvent))) {
					this.subscribedToClientEvents.put(this.generateClientEventKey(client, filteredEvent), filteredEvent);
					unSubscribedEvents.add(filteredEvent);
				}
			}
			Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			
			if (unSubscribedEvents.size() > 0) {
			   	SubscribeToPubsub subPubSub = new SubscribeToPubsub(returnIntent, client); 
		    	subPubSub.execute(unSubscribedEvents);
			}
		}
		return 0;
	}

	public synchronized int unSubscribeFromAllEvents(String client) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromAllEvents for client: " + client);
		return this.unSubscribeFromEvents(client, ALL_EVENT_FILTER);
	}

	public synchronized int unSubscribeFromEvent(String client, String intent) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent must be specified", null != intent && intent.length() > 0);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromEvent for client: " + client + " and intent: " + intent);

		synchronized (this.subscribedToClientEvents) {
			Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			//remove client/event
			
			Log.d(LOG_TAG, "Removed value: " + this.subscribedToClientEvents.remove(this.generateClientEventKey(client, intent))
					+ " for key: " + this.generateClientEventKey(client, intent));
			
			Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());

			ArrayList<String> events = new ArrayList<String>();
			events.add(intent);
			UnSubscribeFromPubsub unsubPubSub = new UnSubscribeFromPubsub(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT, client); 
			unsubPubSub.execute(events);
		}
		
    	return 0;
	}

	public synchronized int unSubscribeFromEvents(String client, String intentFilter) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent filter must be specified", null != intentFilter && intentFilter.length() > 0);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromEvents for client: " + client + " and filter: " + intentFilter);

		ArrayList<String> targetEvents = null;
		String returnIntent;

		if (ALL_EVENT_FILTER.equals(intentFilter)) {
			targetEvents = this.getAllPlatformEvents();
			returnIntent = IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS;
		} else {
			targetEvents = this.getFilteredEvents(intentFilter);
			returnIntent = IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS;
		}
		
		
		synchronized (this.subscribedToClientEvents) {
			ArrayList<String> subscribedEvents = new ArrayList<String>();

			Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
  
			//remove the client/intent pair if they do already exist
			for (String filteredEvent: targetEvents) {
				if (this.subscribedToClientEvents.containsKey(this.generateClientEventKey(client, filteredEvent))) {
					//remove client/event
					Log.d(LOG_TAG, "Removed value: " + this.subscribedToClientEvents.remove(this.generateClientEventKey(client, filteredEvent))
							+ " for key: " + this.generateClientEventKey(client, filteredEvent));
					subscribedEvents.add(filteredEvent);
				}
			}

			Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			
			if (subscribedEvents.size() > 0) {
				UnSubscribeFromPubsub unsubPubSub = new UnSubscribeFromPubsub(returnIntent, client); 
				unsubPubSub.execute(subscribedEvents);
			}
		}
		
		return 0;
	}

	/**
	 * Configure for Pubsub events
	 * @throws ClassNotFoundException 
	 */
	private void configureForPubsub() throws ClassNotFoundException {
		
		Log.d(LOG_TAG, "Configuring Pubsub");
		
		this.pubsubClient.addSimpleClasses(classList);
		this.pubsubClient.setSubscriberCallback(createSubscriber());
	}
	
	/**
	 * Create a new Subscriber object for Pubsub
	 * @return Subscriber
	 */
	private ISubscriber createSubscriber() {
		ISubscriber subscriber = new ISubscriber() {
		
			public void pubsubEvent(IIdentity identity, String node, String itemId, Object payload) {
				Log.d(LOG_TAG, "Received Pubsub event: " + node + " itemId: " + itemId);
				
				String intentTarget = translatePlatformEventToIntent(node);
				//TODO: put in asynctask
				synchronized (PlatformEventsBase.this.subscribedToClientEvents) {
					for (String key : PlatformEventsBase.this.subscribedToClientEvents.keySet()) {
						if (key.contains(intentTarget)) {
							Intent intent = new Intent(intentTarget);
							intent.putExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY, (Parcelable) payload);
							intent.setPackage(getClient(key));
							
							PlatformEventsBase.this.androidContext.sendBroadcast(intent);

							Log.d(LOG_TAG, "Android Intent " + intentTarget + " sent for client: " + getClient(key));
						}
					}
				}
			}
		};
		return subscriber;
	}

	/**
     * 
     * Async task to un-register from Pubsub events
     *
     */
    private class UnSubscribeFromPubsub extends AsyncTask<List<String>, Void, Boolean> {
		private boolean resultStatus = true;
    	
    	private String intentValue;
    	private String client;

    	/**
    	 * Constructor
    	 * 
    	 * @param intentValue
    	 * @param client
    	 */
    	public UnSubscribeFromPubsub(String intentValue, String client) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
    		Dbc.require("Intent filter must be specified", null != intentValue && intentValue.length() > 0);
    		Log.d(LOG_TAG, "UnSubscribeFromPubsub async task for client: " + client + " and filter: " + intentValue);

    		this.intentValue = intentValue;
    		this.client = client;
		}

    	protected Boolean doInBackground(List<String>... args) {
    		
    		List<String> events = args[0];
    		Log.d(LOG_TAG, "Number of events to be un-subscribed: " + events.size());

    		IIdentity pubsubService = PlatformEventsBase.this.cloudNodeIdentity;

    		try {
    			synchronized (PlatformEventsBase.this.subscribedToEvents) {
    				Log.d(LOG_TAG, "Before size of pubsubSubscribes: " + PlatformEventsBase.this.subscribedToEvents.size());

    				for (final String event: events) {

    		    		Integer numSubscriptions = PlatformEventsBase.this.subscribedToEvents.get(translateAndroidIntentToEvent(event));
    		    		
    		    		if ((null != numSubscriptions) && (1 == numSubscriptions)) {
           		    		Log.d(LOG_TAG, "Un-subscribe from Pubsub with event : " + translateAndroidIntentToEvent(event));
           		    	 
    		    			PlatformEventsBase.this.pubsubClient.subscriberUnsubscribe(pubsubService, 
    		    									translateAndroidIntentToEvent(event), 
    		    									new IMethodCallback() {
								
								@Override
								public void returnAction(String result) {
								}
								
								@Override
								public void returnAction(boolean resultFlag) {
									if (resultFlag) {
				            			PlatformEventsBase.this.subscribedToEvents.remove(translateAndroidIntentToEvent(event));
				            			
				               			Log.d(LOG_TAG, "Pubsub un-subscription created for event: " + translateAndroidIntentToEvent(event));
									}
								}
							});
        				} else {
        					PlatformEventsBase.this.subscribedToEvents.put(translateAndroidIntentToEvent(event), numSubscriptions - 1);
        				}
        			}
    				Log.d(LOG_TAG, "After size of pubsubSubscribes: " + PlatformEventsBase.this.subscribedToEvents.size());
    			}
    			
				Intent returnIntent = new Intent(intentValue);
    			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, this.numEventListeners(this.client));

    			if (PlatformEventsBase.this.restrictBroadcast) {
        			returnIntent.setPackage(this.client);
    			}
    			
    			Log.d(LOG_TAG, "UnSubscribeToPubsub return result sent");

    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
    			
			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to unsubscribe for Societies events", e);

			}
    		return resultStatus;
    	}
        /**
         * Get number of subscribed to events for a given client
         * @param client
         * @return int number of subscribed events
         */
        private int numEventListeners(String client) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);

    		int numListeners = 0;
        	
    		Log.d(LOG_TAG, "Invocation of numEventListeners for client: " + client);
    		
			for (String key: PlatformEventsBase.this.subscribedToClientEvents.keySet()) {
				if (key.startsWith(client + KEY_DIVIDER)) {
					numListeners++;
				}
			}
			Log.d(LOG_TAG, "Number of subscribed events for client: " + client + " is: " + numListeners);
 
        	return numListeners;
        }

    }

    /**
     * 
     * Async task to register for Societies Pubsub events
     * Note: The Subscriber objects used to subscribe to the relevant Pubsub nodes
     * are required to be used when un-registering - hence the use of the Map to store them.
     *
     */
    private class SubscribeToPubsub extends AsyncTask<List<String>, Void, Boolean> {
    	private String intentValue;
    	private String client;

    	/**
    	 * Constructor
    	 * 
    	 * @param intentValue
    	 * @param client
    	 */
    	public SubscribeToPubsub(String intentValue, String client) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
    		Dbc.require("Intent filter must be specified", null != intentValue && intentValue.length() > 0);
    		Log.d(LOG_TAG, "SubscribeToPubsub async task for client: " + client + " and filter: " + intentValue);
    		
    		this.intentValue = intentValue;
    		this.client = client;
    		
    		PlatformEventsBase.this.assignConnectionParameters();
		}
    	
		private boolean resultStatus = true;
    	
    	protected Boolean doInBackground(List<String>... args) {
    		
    		List<String> events = args[0];
    		Log.d(LOG_TAG, "Number of events to be subscribed: " + events.size());

    		IIdentity pubsubService = PlatformEventsBase.this.cloudNodeIdentity;

    		try {
    			
       			synchronized (PlatformEventsBase.this.subscribedToEvents) {
    				Log.d(LOG_TAG, "Before size of pubsubSubscribes: " + PlatformEventsBase.this.subscribedToEvents.size());
    				
        			for (final String eventName: events) {
    		    		Log.d(LOG_TAG, "Does event exist: " + translateAndroidIntentToEvent(eventName));
    		    		
    		    		Integer numSubscriptions = PlatformEventsBase.this.subscribedToEvents.get(translateAndroidIntentToEvent(eventName));
    		    		if (null == numSubscriptions) {
        		    		Log.d(LOG_TAG, "Store event : " + translateAndroidIntentToEvent(eventName));
        		    		PlatformEventsBase.this.subscribedToEvents.put(translateAndroidIntentToEvent(eventName), 1);
        		    		Log.d(LOG_TAG, "Subscribe to Pubsub with event : " + translateAndroidIntentToEvent(eventName));
                			PlatformEventsBase.this.pubsubClient.subscriberSubscribe(pubsubService, 
                										translateAndroidIntentToEvent(eventName),
                										new IMethodCallback() {
								
								@Override
								public void returnAction(String result) {
								}
								
								@Override
								public void returnAction(boolean resultFlag) {
									if (resultFlag) {
			                			Log.d(LOG_TAG, "Pubsub subscription created for: " + translateAndroidIntentToEvent(eventName));
									}
								}
							});
        				} else {
        					PlatformEventsBase.this.subscribedToEvents.put(translateAndroidIntentToEvent(eventName), numSubscriptions + 1);
        				}
         			}
    				Log.d(LOG_TAG, "After size of pubsubSubscribes: " + PlatformEventsBase.this.subscribedToEvents.size());
       			}

    			Intent returnIntent = new Intent(intentValue);
    			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, this.numEventListeners(this.client));

    			if (PlatformEventsBase.this.restrictBroadcast) {
        			returnIntent.setPackage(this.client);
    			}
    			
    			Log.d(LOG_TAG, "SubscribeToPubsub return result sent");

    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);

			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to register for Societies events", e);

			}
    		return resultStatus;
    	}
        /**
         * Get number of subscribed to events for a given client
         * @param client
         * @return int number of subscribed events
         */
        private int numEventListeners(String client) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);

    		int numListeners = 0;
        	
    		Log.d(LOG_TAG, "Invocation of numEventListeners for client: " + client);
    		
			for (String key: PlatformEventsBase.this.subscribedToClientEvents.keySet()) {
				if (key.startsWith(client + KEY_DIVIDER)) {
					numListeners++;
				}
			}
			Log.d(LOG_TAG, "Number of subscribed events for client: " + client + " is: " + numListeners);
 
        	return numListeners;
        }
    }

    /**
     * Assign connection parameters (must happen after successful XMPP login)
     */
    private void assignConnectionParameters() {
    	Log.d(LOG_TAG, "assignConnectionParameters invoked");
    	try {
        	if (null == this.domainCommsDestination) {
            	Log.d(LOG_TAG, "determine destinations");

            	this.domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
            	Log.d(LOG_TAG, "Domain Authority Node: " + this.domainCommsDestination);
        	}
    		
    	} catch (Exception e ) {
    		Log.e(LOG_TAG, e.getMessage(), e);
    	}
    }

    /**
     * Retrieve a list of events that match the filter. If a Societies intent starts with filter, the event will be included
     * 
     * @param filter
     * @return ArrayList<String> of filtered events 
     */
    private static ArrayList<String> getFilteredEvents(String filter) {
    	ArrayList<String> filteredEvents = new ArrayList<String>();
    	
    	for (String event : IAndroidSocietiesEvents.societiesAndroidIntents) {
    		if (event.startsWith(filter)) {
    			filteredEvents.add(event);
    		}
    	}
    	return filteredEvents;
    }
    
    /**
     * Retrieve a list of all platform events
     * 
     * @return ArrayList<String> List of all Platform events
     */
    private ArrayList<String> getAllPlatformEvents() {
    	if (null == this.allPlatformEvents) {
    		this.allPlatformEvents = getFilteredEvents(ALL_EVENT_FILTER);
    	}
    	return this.allPlatformEvents;
    }
    
//    /**
//     * Is an event still subscribed to by other clients ?
//     * 
//     * @param client
//     * @param event
//     * @return boolean true if event subscribed to by another client
//     */
//    private boolean otherClientsSubscribed(String client, String event) {
//    	boolean retValue = false;
//    	
//		for (String value : this.subscribedClientEvents.values()) {
//			if (value.equals(event)) {
//				retValue = true;
//				break;
//			}
//		}
//    	return retValue;
//    }
    
    /**
     * Generate the Map key for client/event pair
     * 
     * @param client
     * @param event
     * @return
     */
    private static String generateClientEventKey(String client, String event) {
    	return client + KEY_DIVIDER + event;
    }
    
    /**
     * Translate Societies platform inter-node Pubsub event to an internal Android Societies internal intent.
     * Uses the two Events/Intents arrays to maps inter-node events to Android equivalent intents
     * 
     * @param platformEvent
     * @return String Android Societies internal intent
     */
    private static String translatePlatformEventToIntent(String platformEvent) {
    	String retValue = null;
    	
    	for (int i = 0; i < IAndroidSocietiesEvents.societiesAndroidEvents.length; i++) {
    		if (platformEvent.equals(IAndroidSocietiesEvents.societiesAndroidEvents[i])) {
    			retValue = IAndroidSocietiesEvents.societiesAndroidIntents[i];
    			break;
    		}
    	}
     	return retValue;
    }
    /**
     * Translate Societies Android Pubsub intent to an inter-node Societies platform Pubsub event.
     * Uses the two Events/Intents arrays to maps Android intents to Societies equivalent Pubsub events
     * 
     * @param androidIntent
     * @return String Pubsub inter-node event
     */
    private static String translateAndroidIntentToEvent(String androidIntent) {
    	String retValue = null;
    	
    	for (int i = 0; i < IAndroidSocietiesEvents.societiesAndroidIntents.length; i++) {
    		if (androidIntent.equals(IAndroidSocietiesEvents.societiesAndroidIntents[i])) {
    			retValue = IAndroidSocietiesEvents.societiesAndroidEvents[i];
    			break;
    		}
    	}
     	return retValue;
    }
    /**
     * Retrieve client part of key
     * 
     * @param key
     * @return client part of key
     */
    private static String getClient(String key) {
    	return key.substring(0, key.indexOf(KEY_DIVIDER));
    }
    

    /**
     * Determine if an event is contained in a set of events
     * 
     * @param keys
     * @param event
     * @return boolean true if event is contained
     */
    private static boolean isValueEquals(Set<String> keys, String event) {
    	boolean retValue = false;

    	for (String key : keys) {
    		if (key.equals(event)) {
    			retValue = true;
    			break;
    		}
    	}
    	return retValue;
    }

}
