package org.societies.android.platform.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.android.platform.cssmanager.LocalCSSManagerService;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;

public class PlatformEventsBase implements IAndroidSocietiesEvents {
	
	//Logging tag
    private static final String LOG_TAG = PlatformEventsBase.class.getName();

	//Pubsub packages
    //TODO: Insert all known event classes
	private static final String CSS_MANAGER_CLASS = "org.societies.api.schema.cssmanagement.CssEvent";
    private static final List<String> classList = Collections.singletonList(CSS_MANAGER_CLASS);
    
    private static final String ALL_EVENT_FILTER = "org.societies";
    private static final String KEY_DIVIDER = "$";

	private Context androidContext;
	private PubsubClientAndroid pubsubClient = null;

	//Synchronised Maps - require manual synchronisation 
	private Map<String, String> subscribedClientEvents = null;
	private Map<String, Subscriber> pubsubSubscribes = null;
	private ArrayList <String> allPlatformEvents = null; 

	private String cloudCommsDestination = null;
	private String domainCommsDestination = null;

    private IIdentity cloudNodeIdentity = null;
    private IIdentity domainNodeIdentity = null;
    private ClientCommunicationMgr ccm;

    /**
     * Default constructor
     */
    public PlatformEventsBase(Context androidContext, PubsubClientAndroid pubsubClient, ClientCommunicationMgr ccm) {
    	Log.d(LOG_TAG, "Object created");
    	
    	this.pubsubClient = pubsubClient;
		this.ccm = ccm;
    	this.androidContext = androidContext;

    	this.subscribedClientEvents = Collections.synchronizedMap(new HashMap<String, String>());
    	this.pubsubSubscribes = Collections.synchronizedMap(new HashMap<String, Subscriber>());
    	
		
		this.assignConnectionParameters();
		this.registerForPubsub();


    }

	public synchronized boolean publishEvent(String client, String societiesIntent, Object eventPayload, Class eventClass) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Event Payload must be specified", null != eventPayload);
		Dbc.require("Event Payload Class type must be specified", null != eventClass);
		Log.d(LOG_TAG, "Invocation of publishEvent for client: " + client);
		return false;
	}

	public synchronized boolean subscribeToAllEvents(String client) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "Invocation of subscribeToAllEvents for client: " + client);
		
		return this.subscribeToEvents(client, ALL_EVENT_FILTER);
	}

	public boolean subscribeToEvent(String client, String intent) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent must be specified", null != intent && intent.length() > 0);
		Log.d(LOG_TAG, "Invocation of subscribeToEvent for client: " + client + " and intent: " + intent);

		//store client/event
		synchronized (this.subscribedClientEvents) {
			this.subscribedClientEvents.put(this.generateClientEventKey(client, intent), intent);
		}
		
		synchronized (this.pubsubSubscribes) {
			if (!this.pubsubSubscribes.keySet().contains(intent)) {
				ArrayList<String> events = new ArrayList<String>();
				events.add(intent);
		    	SubscribeToPubsub subPubSub = new SubscribeToPubsub(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT, client); 
		    	subPubSub.execute(events);
			}
		}
    	return false;
	}

	public synchronized boolean subscribeToEvents(String client, String intentFilter) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent filter must be specified", null != intentFilter && intentFilter.length() > 0);
		Log.d(LOG_TAG, "Invocation of subscribeToEvents for client: " + client + " and filter: " + intentFilter);

		ArrayList<String> targetEvents = null;
		String returnIntent;
		
		if (ALL_EVENT_FILTER.equals(intentFilter)) {
			targetEvents = this.getAllPlatformEvents();
			returnIntent = IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS;
		} else {
			targetEvents = this.getFilteredEvents(intentFilter);
			returnIntent = IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS;
		}
		
		ArrayList<String> unSubscribedEvents = new ArrayList<String>();
		
		for (String filteredEvent: targetEvents) {
			//store client/event
			synchronized (this.subscribedClientEvents) {
				this.subscribedClientEvents.put(this.generateClientEventKey(client, filteredEvent), filteredEvent);
			}

			synchronized (this.pubsubSubscribes) {
				if (!this.pubsubSubscribes.keySet().contains(filteredEvent)) {
					unSubscribedEvents.add(filteredEvent);
				}
			}
		}
		if (unSubscribedEvents.size() > 0) {
		   	SubscribeToPubsub subPubSub = new SubscribeToPubsub(returnIntent, client); 
	    	subPubSub.execute(unSubscribedEvents);
		}
 		
		return false;
	}

	public synchronized boolean unSubscribeFromAllEvents(String client) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromAllEvents for client: " + client);
		return this.unSubscribeFromEvents(client, ALL_EVENT_FILTER);
	}

	public synchronized boolean unSubscribeFromEvent(String client, String intent) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent must be specified", null != intent && intent.length() > 0);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromEvent for client: " + client + " and intent: " + intent);

		synchronized (this.subscribedClientEvents) {
			//remove client/event
			this.subscribedClientEvents.remove(this.generateClientEventKey(client, intent));
		}

		
		synchronized (this.pubsubSubscribes) {
			if (this.pubsubSubscribes.keySet().contains(intent) && !this.otherClientsSubscribed(client, intent)) {
				ArrayList<String> events = new ArrayList<String>();
				events.add(intent);
				UnSubscribeFromPubsub unsubPubSub = new UnSubscribeFromPubsub(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT, client); 
				unsubPubSub.execute(events);
			}
		}
    	return false;
	}

	public synchronized boolean unSubscribeFromEvents(String client, String intentFilter) {
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
		
		ArrayList<String> subscribedEvents = new ArrayList<String>();
		
		for (String filteredEvent: targetEvents) {
			synchronized (this.subscribedClientEvents) {
				//remove client/event
				this.subscribedClientEvents.remove(this.generateClientEventKey(client, filteredEvent));
			}
			
			synchronized (this.pubsubSubscribes) {
				if (this.pubsubSubscribes.keySet().contains(filteredEvent) && !this.otherClientsSubscribed(client, filteredEvent)) {
					subscribedEvents.add(filteredEvent);
				}
			}
		}
		if (subscribedEvents.size() > 0) {
			UnSubscribeFromPubsub unsubPubSub = new UnSubscribeFromPubsub(returnIntent, client); 
			unsubPubSub.execute(subscribedEvents);
		}
		return false;
	}

	/**
	 * Unregister from already subscribed to pubsub events
	 */
	private void  unregisterFromPubsub() {
		Log.d(LOG_TAG, "Starting Pubsub un-registration: " + System.currentTimeMillis());
		//TODO: unbind from Pubsub
	}

	/**
	 * Register for Pubsub events
	 */
	private void registerForPubsub() {
		
		Log.d(LOG_TAG, "Starting Pubsub registration: " + System.currentTimeMillis());
		
        try {
            this.pubsubClient.addSimpleClasses(classList);

            Log.d(LOG_TAG, "Subscribing to pubsub");
	        
        } catch (ClassNotFoundException e) {
                Log.e(LOG_TAG, "ClassNotFoundException loading " + Arrays.toString(classList.toArray()), e);
        }
	}
	/**
	 * Create a new Subscriber object for Pubsub
	 * @return Subscriber
	 */
	private Subscriber createSubscriber() {
		Subscriber subscriber = new Subscriber() {
		
			public void pubsubEvent(IIdentity identity, String node, String itemId,
					Object payload) {
				Log.d(LOG_TAG, "Received Pubsub event: " + node + " itemId: " + itemId);
				
				String intentTarget = PlatformEventsBase.this.translatePlatformEvent(node);
				//TODO: put in asynctask
				synchronized (PlatformEventsBase.this.subscribedClientEvents) {
					for (String key : PlatformEventsBase.this.subscribedClientEvents.keySet()) {
						if (key.contains(intentTarget)) {
							Intent intent = new Intent(intentTarget);
							intent.putExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY, (Parcelable) payload);
							intent.setPackage(PlatformEventsBase.this.getClient(key));
							
							PlatformEventsBase.this.androidContext.sendBroadcast(intent);

							Log.d(LOG_TAG, "Android Intent " + intentTarget + " sent for client: " + PlatformEventsBase.this.getClient(key));
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
    		this.intentValue = intentValue;
    		this.client = client;
		}

    	protected Boolean doInBackground(List<String>... args) {
    		
    		List<String> events = args[0];
    		PubsubClientAndroid pubsubAndClient = PlatformEventsBase.this.pubsubClient;    	

    		IIdentity pubsubService = null;
    		
    		pubsubService = PlatformEventsBase.this.cloudNodeIdentity;

    		try {
    			synchronized (PlatformEventsBase.this.pubsubSubscribes) {
        			for (String event: events) {
               			pubsubAndClient.subscriberUnsubscribe(pubsubService, event, PlatformEventsBase.this.pubsubSubscribes.get(event));
            			PlatformEventsBase.this.pubsubSubscribes.remove(event);

               			Log.d(LOG_TAG, "Pubsub un-subscription created for event: " + event);
        			}
        			Intent returnIntent = new Intent(intentValue);
        			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, resultStatus);
        			returnIntent.setPackage(this.client);
        			
        			Log.d(LOG_TAG, "UnSubscribeToPubsub return result sent");

        			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
    			}
    			
			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to unsubscribe for Societies events", e);

			}
    		return resultStatus;
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
    		this.intentValue = intentValue;
    		this.client = client;
		}
    	
		private boolean resultStatus = true;
    	
    	protected Boolean doInBackground(List<String>... args) {
    		
    		List<String> events = args[0];
    		PubsubClientAndroid pubsubAndClient = PlatformEventsBase.this.pubsubClient;    	

    		IIdentity pubsubService = null;
    		
			pubsubService = PlatformEventsBase.this.cloudNodeIdentity;

    		try {
    			
       			synchronized (PlatformEventsBase.this.pubsubSubscribes) {
        			for (String eventName: events) {
            			PlatformEventsBase.this.pubsubSubscribes.put(eventName, PlatformEventsBase.this.createSubscriber());
            			pubsubAndClient.subscriberSubscribe(pubsubService, eventName, PlatformEventsBase.this.pubsubSubscribes.get(eventName));

            			Log.d(LOG_TAG, "Pubsub subscription created for: " + eventName);
         			}
        			Intent returnIntent = new Intent(intentValue);
        			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, resultStatus);
        			returnIntent.setPackage(this.client);
        			
        			Log.d(LOG_TAG, "SubscribeToPubsub return result sent");

        			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
       			}
			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to register for Societies events", e);

			}
    		return resultStatus;
    	}
    }

    /**
     * Assign connection parameters (must happen after successful XMPP login)
     */
    private void assignConnectionParameters() {
		//Get the Cloud destination
    	this.cloudCommsDestination = this.ccm.getIdManager().getCloudNode().getJid();
		Log.d(LOG_TAG, "Cloud Node: " + this.cloudCommsDestination);

    	this.domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
    	Log.d(LOG_TAG, "Domain Authority Node: " + this.domainCommsDestination);
    			
    	try {
			this.cloudNodeIdentity = IdentityManagerImpl.staticfromJid(this.cloudCommsDestination);
			Log.d(LOG_TAG, "Cloud node identity: " + this.cloudNodeIdentity);
			
			this.domainNodeIdentity = IdentityManagerImpl.staticfromJid(this.domainCommsDestination);
			Log.d(LOG_TAG, "Domain node identity: " + this.cloudNodeIdentity);
			
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, "Unable to get CSS Node identity", e);
			throw new RuntimeException(e);
		}     
    }

    /**
     * Retrieve a list of events that match the filter. If a Societies intent starts with filter, the event will be included
     * 
     * @param filter
     * @return ArrayList<String> of filtered events 
     */
    private ArrayList<String> getFilteredEvents(String filter) {
    	ArrayList<String> filteredEvents = new ArrayList<String>();
    	
    	for (String event : IAndroidSocietiesEvents.societiesEvents) {
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
    		this.allPlatformEvents = this.getFilteredEvents(ALL_EVENT_FILTER);
    	}
    	return this.allPlatformEvents;
    }
    
    /**
     * Is an event still subscribed to by other clients ?
     * 
     * @param client
     * @param event
     * @return boolean true if event subscribed to by another client
     */
    private boolean otherClientsSubscribed(String client, String event) {
    	synchronized (this.subscribedClientEvents) {
        	return this.subscribedClientEvents.containsValue(event);
		}
    }
    
    /**
     * Generate the Map key for client/event pair
     * 
     * @param client
     * @param event
     * @return
     */
    private String generateClientEventKey(String client, String event) {
    	return client + KEY_DIVIDER + event;
    }
    
    /**
     * Translate Societies platform inter-node Pubsub event to an internal Android Societies internal intent
     * 
     * @param platformEvent
     * @return String Android Societies internal intent
     */
    private String translatePlatformEvent(String platformEvent) {
    	String retValue = null;
    	
    	for (String event : this.getAllPlatformEvents()) {
    		if (event.contains(platformEvent)) {
    			retValue = event;
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
    private String getClient(String key) {
    	return key.substring(0, key.indexOf(KEY_DIVIDER));
    }
}
