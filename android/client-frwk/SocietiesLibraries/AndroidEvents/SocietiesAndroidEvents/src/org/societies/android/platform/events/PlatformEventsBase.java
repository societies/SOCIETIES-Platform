package org.societies.android.platform.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.pubsub.helper.PubsubHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;


/**
 * Implementation Events service
 * TODO: Handle non-created pubsub nodes
 *
 */
public class PlatformEventsBase implements IAndroidSocietiesEvents {
	
	//Logging tag
    private static final String LOG_TAG = PlatformEventsBase.class.getName();
	    
    private static final String ALL_EVENT_FILTER = "org.societies";
    private static final String KEY_DIVIDER = "$";
    private static final String INVALID_EVENT_PAYLOAD = "Invalid payload";

	private Context androidContext;
	private PubsubHelper pubsubClient = null;

	//Synchronised Maps - require manual synchronisation 
	private Map<String, String> subscribedToClientEvents = null;
	private Map<String, Integer> subscribedToEvents = null;
	private Map<String, String> thirdPartyEvents = null;
	
	private ArrayList<ThirdPartyEventsIntents> thirdPartyEventsLookup = null;
	private ArrayList <String> allPlatformEvents = null; 

	private String cloudNodeDestination;
    private IIdentity cloudNodeIdentity;
    
    private ClientCommunicationMgr ccm;
    private boolean restrictBroadcast;
    private List<String> classList;
    private boolean connectedToComms;
    private boolean connectedToPubsub;
    private Random randomGenerator;


    /**
     * Default constructor
     */
    public PlatformEventsBase(Context androidContext, PubsubHelper pubsubClient, ClientCommunicationMgr ccm, boolean restrictBroadcast) {
    	Log.d(LOG_TAG, "PlatformEventsBase created");
    	
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

    	//tracks the events created by core services (unlikely) and 3rd party services
    	this.thirdPartyEvents = Collections.synchronizedMap(new HashMap<String, String>());
    	//a Pubsub node/Societies Intents lookup
    	this.thirdPartyEventsLookup = new ArrayList<PlatformEventsBase.ThirdPartyEventsIntents>();
    	
    	this.cloudNodeDestination = null;
        this.cloudNodeIdentity = null;

		//Create random id generator
		this.randomGenerator = new Random(System.currentTimeMillis());
		
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
										Log.e(LOG_TAG, "Class Not found exception", e);
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
	@Override
	public int getNumSubscribedNodes(String client) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Get number of subscribed to events for client: " + client);
		
		
		int numListeners = 0;
		synchronized(this.subscribedToClientEvents) {
			for (String key: PlatformEventsBase.this.subscribedToClientEvents.keySet()) {
				if (key.startsWith(client + KEY_DIVIDER)) {
					numListeners++;
				}
			}
			Intent intent = new Intent(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS);
			intent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, numListeners);

			if (this.restrictBroadcast) {
    			intent.setPackage(client);
			}
			
			Log.d(LOG_TAG, "Number of subscribed events for client: " + client + " is: " + numListeners);
			PlatformEventsBase.this.androidContext.sendBroadcast(intent);
			Log.d(LOG_TAG, "getNumSubscribedNodes return result sent");
		}

    	return 0;
	}

	public synchronized boolean publishEvent(final String client, String societiesIntent, Object eventPayload) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Event Payload must be specified", null != eventPayload);
		Dbc.require("Valid Intent must be specified", null != societiesIntent && societiesIntent.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of publishEvent for client: " + client);
		
		
		final Intent returnIntent = new Intent(IAndroidSocietiesEvents.PUBLISH_EVENT);
		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
		if (PlatformEventsBase.this.restrictBroadcast) {
			returnIntent.setPackage(client);
		}

		//check if intent is invalid. If so, signal with an exception intent otherwise proceed as normal 
		if (this.isIntentValid(societiesIntent, IAndroidSocietiesEvents.PUBLISH_EVENT, client, true)) {
			try {
				PlatformEventsBase.this.pubsubClient.publisherPublish(this.cloudNodeIdentity, 
							translateAndroidIntentToEvent(societiesIntent), 
							Integer.toString(this.randomGenerator.nextInt()), 
							eventPayload, new IMethodCallback() {
					
					@Override
					public void returnAction(String result) {
		    			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, true);
		    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
		    			Log.d(LOG_TAG, "Publish event return result sent");
					}
					
					@Override
					public void returnAction(boolean resultFlag) {
					}
				});
			} catch (XMPPError e) {
				Log.e(LOG_TAG, "XMPPError", e);
				PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
				Log.d(LOG_TAG, "Publish event return result sent");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Comunication Exception", e);
				PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
				Log.d(LOG_TAG, "Publish event return result sent");
			} 
		}

		return false;
	}

	public synchronized boolean subscribeToAllEvents(String client) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of subscribeToAllEvents for client: " + client);
		
		return this.subscribeToEvents(client, ALL_EVENT_FILTER);
	}

	public boolean subscribeToEvent(String client, String intent) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Valid Intent must be specified", null != intent && intent.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of subscribeToEvent for client: " + client + " and intent: " + intent);
		assignConnectionParameters();

		//check if intent is invalid. If so, signal with an exception intent otherwise proceed as normal 
		if (this.isIntentValid(intent, IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT, client, true)) {
				//store client/event
			synchronized (this.subscribedToClientEvents) {
				Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
				
				this.subscribedToClientEvents.put(generateClientEventKey(client, intent), intent);
	
				Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
	
				ArrayList<String> events = new ArrayList<String>();
				events.add(intent);
				
		    	SubscribeToPubsub subPubSub = new SubscribeToPubsub(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT, client, this.cloudNodeIdentity); 
		    	subPubSub.execute(events);
			}
		}


    	return false;
	}

	public synchronized boolean subscribeToEvents(String client, String intentFilter) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent filter must be specified", null != intentFilter && intentFilter.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of subscribeToEvents for client: " + client + " and filter: " + intentFilter);
		assignConnectionParameters();

		ArrayList<String> targetEvents = null;
		String returnIntent;
		
		//Generate a list of all possible events or a filtered subset
		if (ALL_EVENT_FILTER.equals(intentFilter)) {
			targetEvents = this.getAllPlatformEvents();
			returnIntent = IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS;
		} else {
			targetEvents = getFilteredEvents(intentFilter);
			returnIntent = IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS;
		}
		
		
		synchronized (this.subscribedToClientEvents) {
			Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			
			ArrayList<String> unSubscribedEvents = new ArrayList<String>();
   
			//add the client/intent pair if they do not already exist
			for (String filteredEvent: targetEvents) {
				//store client/event
				if (!this.subscribedToClientEvents.containsKey(generateClientEventKey(client, filteredEvent))) {
					this.subscribedToClientEvents.put(generateClientEventKey(client, filteredEvent), filteredEvent);
					unSubscribedEvents.add(filteredEvent);
				}
			}
			Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			
			if (unSubscribedEvents.size() > 0) {
			   	SubscribeToPubsub subPubSub = new SubscribeToPubsub(returnIntent, client, this.cloudNodeIdentity); 
		    	subPubSub.execute(unSubscribedEvents);
			}
		}
		return false;
	}

	public synchronized boolean unSubscribeFromAllEvents(String client) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromAllEvents for client: " + client);
		return this.unSubscribeFromEvents(client, ALL_EVENT_FILTER);
	}

	public synchronized boolean unSubscribeFromEvent(String client, String intent) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Valid Intent must be specified", null != intent && intent.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromEvent for client: " + client + " and intent: " + intent);

		//check if intent is invalid. If so, signal with an exception intent otherwise proceed as normal 
		if (this.isIntentValid(intent, IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT, client, true)) {
			synchronized (this.subscribedToClientEvents) {
				Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
				//remove client/event
				
				Log.d(LOG_TAG, "Removed value: " + this.subscribedToClientEvents.remove(generateClientEventKey(client, intent))
						+ " for key: " + generateClientEventKey(client, intent));
				
				Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());

				ArrayList<String> events = new ArrayList<String>();
				events.add(intent);
				UnSubscribeFromPubsub unsubPubSub = new UnSubscribeFromPubsub(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT, client, this.cloudNodeIdentity); 
				unsubPubSub.execute(events);
			}
		}
		
    	return false;
	}

	public synchronized boolean unSubscribeFromEvents(String client, String intentFilter) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Intent filter must be specified", null != intentFilter && intentFilter.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of unSubscribeFromEvents for client: " + client + " and filter: " + intentFilter);

		ArrayList<String> targetEvents = null;
		String returnIntent;

		if (ALL_EVENT_FILTER.equals(intentFilter)) {
			targetEvents = this.getAllPlatformEvents();
			returnIntent = IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS;
		} else {
			targetEvents = getFilteredEvents(intentFilter);
			returnIntent = IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS;
		}
		
		
		synchronized (this.subscribedToClientEvents) {
			ArrayList<String> subscribedEvents = new ArrayList<String>();

			Log.d(LOG_TAG, "Before size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
  
			//remove the client/intent pair if they do already exist
			for (String filteredEvent: targetEvents) {
				if (this.subscribedToClientEvents.containsKey(generateClientEventKey(client, filteredEvent))) {
					//remove client/event
					Log.d(LOG_TAG, "Removed value: " + this.subscribedToClientEvents.remove(generateClientEventKey(client, filteredEvent))
							+ " for key: " + generateClientEventKey(client, filteredEvent));
					subscribedEvents.add(filteredEvent);
				}
			}

			Log.d(LOG_TAG, "After size of subscribedClientEvents: " + this.subscribedToClientEvents.size());
			
			if (subscribedEvents.size() > 0) {
				UnSubscribeFromPubsub unsubPubSub = new UnSubscribeFromPubsub(returnIntent, client, this.cloudNodeIdentity); 
				unsubPubSub.execute(subscribedEvents);
			}
		}
		
		return false;
	}

	@Override
	public boolean createEvent(String client, String pubsubNode, String societiesIntent) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be specified", null != pubsubNode && pubsubNode.length() > 0);
		Dbc.require("Societies Intent must be specified", null != societiesIntent && societiesIntent.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of createEvent for client: " + client + " and pubsub node: " + pubsubNode + " and Societies intent: " + societiesIntent);

		//check if the intent is already valid, i.e. already allocated
		if (!this.isIntentValid(societiesIntent, IAndroidSocietiesEvents.CREATE_EVENT, client, false)) {
			
			//check if the Pubsub node is already valid, i.e. already allocated
			if (!this.isPubsubNodeValid(pubsubNode, IAndroidSocietiesEvents.CREATE_EVENT, client, false)) {
				CreateEvent invokeTask = new CreateEvent(client, this.cloudNodeIdentity, pubsubNode, societiesIntent);
				invokeTask.execute();
				
			} else {
		    	//Create intent to signal exception
	    		Intent returnIntent = new Intent(IAndroidSocietiesEvents.CREATE_EVENT);
	    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
	    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, INVALID_PUBSUB_NODE_ALREADY_EXISTS);
	    		if (PlatformEventsBase.this.restrictBroadcast) {
	    			returnIntent.setPackage(client);
	    		}
				PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
				Log.d(LOG_TAG, "Pubsub Node already exists return result sent");
				
			}
		} else {
	    	//Create intent to signal exception
	    		Intent returnIntent = new Intent(IAndroidSocietiesEvents.CREATE_EVENT);
	    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
	    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, INVALID_SOCIETIES_INTENT_ALREADY_EXISTS);
	    		if (PlatformEventsBase.this.restrictBroadcast) {
	    			returnIntent.setPackage(client);
	    		}
				PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
				Log.d(LOG_TAG, "Societies Intent already exists return result sent");
		}
		return false;
	}

	@Override
	public boolean deleteEvent(String client, String pubsubNode) {
		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
		Dbc.require("Pubsub node must be specified", null != pubsubNode && pubsubNode.length() > 0);
		//Invariant condition
		Dbc.invariant("Comms services must be connected", this.connectedToComms && this.connectedToPubsub);
		Log.d(LOG_TAG, "Invocation of deleteEvent for client: " + client + " and pubsub node: " + pubsubNode);

		if (isPubsubNodeValidAndOwned(pubsubNode, IAndroidSocietiesEvents.DELETE_EVENT, client, true)) {
			DeleteEvent invokeTask = new DeleteEvent(client, this.cloudNodeIdentity, pubsubNode);
			invokeTask.execute();
		}
		return false;
	}
	

	/**
	 * Configure for Pubsub events
	 */
	private void configureForPubsub() throws ClassNotFoundException {
		
		Log.d(LOG_TAG, "Configuring Pubsub");
    	//create list of event classes for Pubsub registration 
        this.classList = new ArrayList<String>();

        for (String payload : IAndroidSocietiesEvents.pubsubPayloadClasses) {
        	this.classList.add(payload);
        }
		
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
							
							if (payload instanceof Parcelable) {
								intent.putExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY, (Parcelable) payload);
							} else {
								intent.putExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY, INVALID_EVENT_PAYLOAD);
							}
							
							if (PlatformEventsBase.this.restrictBroadcast) {
								intent.setPackage(getClient(key));
							}
							
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
    	private IIdentity pubsubService;

    	/**
    	 * Constructor
    	 * 
    	 * @param intentValue
    	 * @param client
    	 */
    	public UnSubscribeFromPubsub(String intentValue, String client, IIdentity pubsubService) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
    		Dbc.require("Intent filter must be specified", null != intentValue && intentValue.length() > 0);
    		Dbc.require("Pubsub service identity cannot be null", null != pubsubService);
    		Log.d(LOG_TAG, "UnSubscribeFromPubsub async task for client: " + client + " and intent: " + intentValue);

    		this.intentValue = intentValue;
    		this.client = client;
    		this.pubsubService = pubsubService;

		}

    	protected Boolean doInBackground(List<String>... args) {
    		
    		List<String> events = args[0];
    		Log.d(LOG_TAG, "Number of events to be un-subscribed: " + events.size());
    		
			Intent returnIntent = new Intent(intentValue);
			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
			if (PlatformEventsBase.this.restrictBroadcast) {
    			returnIntent.setPackage(this.client);
			}

    		try {
    			synchronized (PlatformEventsBase.this.subscribedToEvents) {
    				Log.d(LOG_TAG, "Before size of pubsubSubscribes: " + PlatformEventsBase.this.subscribedToEvents.size());

    				for (final String event: events) {
        				//Create a latch to allow each unsubscription to occur sequentially
    					//Failure to this caused unreliable unsubscriptions
        				final CountDownLatch endCondition = new CountDownLatch(1);
        				
    					final long unsubscription = System.currentTimeMillis();
    					
    		    		Integer numSubscriptions = PlatformEventsBase.this.subscribedToEvents.get(translateAndroidIntentToEvent(event));
    		    		
    		    		if ((null != numSubscriptions) && (1 == numSubscriptions)) {
           		    		Log.d(LOG_TAG, "Un-subscribe from Pubsub with event : " + translateAndroidIntentToEvent(event));
           		    	 
	            			PlatformEventsBase.this.subscribedToEvents.remove(translateAndroidIntentToEvent(event));
	            			
    		    			PlatformEventsBase.this.pubsubClient.subscriberUnsubscribe(this.pubsubService, 
    		    									translateAndroidIntentToEvent(event), 
    		    									new IMethodCallback() {
    							
    							@Override
    							public void returnAction(String result) {
			               			Log.d(LOG_TAG, "Pubsub un-subscription created for event: " + translateAndroidIntentToEvent(event));
			               			Log.d(LOG_TAG, "Time to subscribe event:" + Long.toString(System.currentTimeMillis() - unsubscription));
			               			//notify latch
			               			endCondition.countDown();
    							}
    							
    							@Override
    							public void returnAction(boolean resultFlag) {
    							}
    						});
        				} else {
        					PlatformEventsBase.this.subscribedToEvents.put(translateAndroidIntentToEvent(event), numSubscriptions - 1);
	               			//notify latch
	               			endCondition.countDown();
        				}
       		    		//wait for latch to release
    		    		endCondition.await();
        			}
	    			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, true);
		   			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
	    			Log.d(LOG_TAG, "UnSubscribeToPubsub return result sent");
    			}

			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to unsubscribe for Societies events", e);
	   			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
			}
    		return resultStatus;
    	}
    }
    
    /**
     * 
     * Async task to register for Societies Pubsub events
     *
     */
    private class SubscribeToPubsub extends AsyncTask<List<String>, Void, Boolean> {
    	private String intentValue;
    	private String client;
    	private IIdentity pubsubService;
    	/**
    	 * Constructor
    	 * 
    	 * @param intentValue
    	 * @param client
    	 */
    	public SubscribeToPubsub(String intentValue, String client, IIdentity pubsubService) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
    		Dbc.require("Intent filter must be specified", null != intentValue && intentValue.length() > 0);
    		Dbc.require("Pubsub service identity cannot be null", null != pubsubService);
    		Log.d(LOG_TAG, "SubscribeToPubsub async task for client: " + client + " and intent: " + intentValue);
    		
    		this.intentValue = intentValue;
    		this.client = client;
    		this.pubsubService = pubsubService;
		}
    	
		private boolean resultStatus = true;
    	
    	protected Boolean doInBackground(List<String>... args) {
    		
    		List<String> events = args[0];
    		Log.d(LOG_TAG, "Number of events to be subscribed: " + events.size());

			Intent returnIntent = new Intent(intentValue);
			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
			if (PlatformEventsBase.this.restrictBroadcast) {
    			returnIntent.setPackage(this.client);
			}

    		try {
    			
       			synchronized (PlatformEventsBase.this.subscribedToEvents) {
    				Log.d(LOG_TAG, "Before size of pubsubSubscribes: " + PlatformEventsBase.this.subscribedToEvents.size());

    				
    				for (final String eventName: events) {

        				//Create a latch to allow each subscription to occur sequentially
    					//Failure to this caused unreliable subscription
        				final CountDownLatch endCondition = new CountDownLatch(1);

    					final long unsubscription = System.currentTimeMillis();

    					Integer numSubscriptions = PlatformEventsBase.this.subscribedToEvents.get(translateAndroidIntentToEvent(eventName));
    		    		if (null == numSubscriptions) {
        		    		Log.d(LOG_TAG, "Store event : " + translateAndroidIntentToEvent(eventName));
        		    		PlatformEventsBase.this.subscribedToEvents.put(translateAndroidIntentToEvent(eventName), 1);
        		    		Log.d(LOG_TAG, "Subscribe to Pubsub with event : " + translateAndroidIntentToEvent(eventName));
                			PlatformEventsBase.this.pubsubClient.subscriberSubscribe(this.pubsubService, 
                										translateAndroidIntentToEvent(eventName),
                										new IMethodCallback() {
    							
    							@Override
    							public void returnAction(String result) {
		                			Log.d(LOG_TAG, "Pubsub subscription created for: " + translateAndroidIntentToEvent(eventName));
			               			Log.d(LOG_TAG, "Time to subscribe event:" + Long.toString(System.currentTimeMillis() - unsubscription));
			               			//notify latch
			               			endCondition.countDown();
    							}
    							
    							@Override
    							public void returnAction(boolean resultFlag) {
    							}
    						});
        				} else {
        					PlatformEventsBase.this.subscribedToEvents.put(translateAndroidIntentToEvent(eventName), numSubscriptions + 1);
	               			//notify latch
	               			endCondition.countDown();

        				}
    		    		//wait for latch to release
    		    		endCondition.await();
              		}
       			}
    			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, true);
    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
    			Log.d(LOG_TAG, "SubscribeToPubsub return result sent");

			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to register for Societies events", e);
    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
 			}
    		return resultStatus;
    	}
    }

    
    /**
     * 
     * Async task to create Societies Pubsub events
     *
     */
    private class CreateEvent extends AsyncTask<Void, Void, Boolean> {
    	private String client;
    	private String societiesIntent;
    	private IIdentity pubsubService;
    	private String pubsubNode;
    	/**
    	 * Constructor
    	 * 
    	 * @param intentValue
    	 * @param client
    	 */
    	public CreateEvent(String client, IIdentity pubsubService, String pubsubNode, String societiesIntent) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
    		Dbc.require("Pubsub node must be specified", null != pubsubNode && pubsubNode.length() > 0);
    		Dbc.require("Societies Intent must be specified", null != societiesIntent && societiesIntent.length() > 0);
    		Dbc.require("Pubsub service identity cannot be null", null != pubsubService);
    		Log.d(LOG_TAG, "CreateEvent async task for client: " + client + " and pubsub node: " + pubsubNode);
    		
    		this.societiesIntent = societiesIntent;
    		this.client = client;
    		this.pubsubService = pubsubService;
    		this.pubsubNode = pubsubNode;
		}
    	
		private boolean resultStatus = true;
    	
    	protected Boolean doInBackground(Void... args) {

			final Intent returnIntent = new Intent(IAndroidSocietiesEvents.CREATE_EVENT);
			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
			if (PlatformEventsBase.this.restrictBroadcast) {
    			returnIntent.setPackage(this.client);
			}

    		try {
    			
       			synchronized (PlatformEventsBase.this.thirdPartyEvents) {
    				Log.d(LOG_TAG, "Before size of thirdPartyEvents: " + PlatformEventsBase.this.thirdPartyEvents.size());

    				if (!PlatformEventsBase.this.thirdPartyEvents.containsKey(pubsubNode)) {
    					
    					PlatformEventsBase.this.pubsubClient.ownerCreate(pubsubService, pubsubNode, new IMethodCallback() {
							
							@Override
							public void returnAction(String result) {
								if (null != result) {
			    					PlatformEventsBase.this.thirdPartyEvents.put(pubsubNode, client);
			    					ThirdPartyEventsIntents thirdPartyEvent = new ThirdPartyEventsIntents(pubsubNode, societiesIntent);
			    					PlatformEventsBase.this.thirdPartyEventsLookup.add(thirdPartyEvent);
			    					
			    	    			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, true);
			    	    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
			    	    			Log.d(LOG_TAG, "Create event return result sent");
								}
							}
							
							@Override
							public void returnAction(boolean resultFlag) {
							}
						});
    				} else {
    	    			this.resultStatus = false;
    	    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
    	    			Log.d(LOG_TAG, "Create event return result sent");
    				}
       			}

			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to create event for Societies events", e);
    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
 			}
    		return resultStatus;
    	}
    }

    /**
     * 
     * Async task to delete Societies Pubsub events
     *
     */
    private class DeleteEvent extends AsyncTask<Void, Void, Boolean> {
    	private String client;
    	private IIdentity pubsubService;
    	private String pubsubNode;
    	/**
    	 * Constructor
    	 * 
    	 * @param intentValue
    	 * @param client
    	 */
    	public DeleteEvent(String client, IIdentity pubsubService, String pubsubNode) {
    		Dbc.require("Client subscriber must be specified", null != client && client.length() > 0);
    		Dbc.require("Pubsub node must be specified", null != pubsubNode && pubsubNode.length() > 0);
    		Dbc.require("Pubsub service identity cannot be null", null != pubsubService);
    		Log.d(LOG_TAG, "DeleteEvent async task for client: " + client + " and pubsub node: " + pubsubNode);
    		
    		this.client = client;
    		this.pubsubService = pubsubService;
    		this.pubsubNode = pubsubNode;
		}
    	
		private boolean resultStatus = true;
    	
    	protected Boolean doInBackground(Void... args) {

			final Intent returnIntent = new Intent(IAndroidSocietiesEvents.DELETE_EVENT);
			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
			if (PlatformEventsBase.this.restrictBroadcast) {
    			returnIntent.setPackage(this.client);
			}

    		try {
    			
       			synchronized (PlatformEventsBase.this.thirdPartyEvents) {
    				Log.d(LOG_TAG, "Before size of thirdPartyEvents: " + PlatformEventsBase.this.thirdPartyEvents.size());

    				if (PlatformEventsBase.this.thirdPartyEvents.containsKey(pubsubNode) &&
    						PlatformEventsBase.this.thirdPartyEvents.get(pubsubNode).equals(client)) {
    					
    					PlatformEventsBase.this.pubsubClient.ownerDelete(pubsubService, pubsubNode, new IMethodCallback() {
							
							@Override
							public void returnAction(String result) {
								if (null != result) {
			    					PlatformEventsBase.this.thirdPartyEvents.remove(pubsubNode);
			    					PlatformEventsBase.this.removeThirdPartyEvent(pubsubNode);
			    					
			    	    			returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, true);
			    	    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
			    	    			Log.d(LOG_TAG, "Delete event return result sent");
								}
							}
							
							@Override
							public void returnAction(boolean resultFlag) {
							}
						});
    				} else {
    	    			this.resultStatus = false;
    	    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
    	    			Log.d(LOG_TAG, "Create event return result sent");
    				}
       			}

			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to create event for Societies events", e);
    			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
 			}
    		return resultStatus;
    	}
    }

    /**
     * Assign connection parameters (must happen after successful XMPP login)
     */
    private void assignConnectionParameters() {
    	Log.d(LOG_TAG, "assignConnectionParameters invoked");
    	if (null == cloudNodeDestination) {
        	try {
            	this.cloudNodeDestination = this.ccm.getIdManager().getCloudNode().getJid();
        		Log.d(LOG_TAG, "Domain Authority Node: " + this.cloudNodeDestination);

            	try {
        			this.cloudNodeIdentity = IdentityManagerImpl.staticfromJid(this.cloudNodeDestination);
        			Log.d(LOG_TAG, "Domain Authority identity: " + this.cloudNodeIdentity);
        			
        		} catch (InvalidFormatException e) {
        			Log.e(LOG_TAG, "Unable to get Domain Authority identity", e);
        		}     
        	} catch (InvalidFormatException i) {
        		Log.e(LOG_TAG, "ID Manager exception", i);
        	}
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
     * Also uses the Third party created events array of events.
     * 
     * @param platformEvent
     * @return String Android Societies internal intent
     */
    private String translatePlatformEventToIntent(String platformEvent) {
    	String retValue = null;
    	
    	for (int i = 0; i < IAndroidSocietiesEvents.societiesAndroidEvents.length; i++) {
    		if (platformEvent.equals(IAndroidSocietiesEvents.societiesAndroidEvents[i])) {
    			retValue = IAndroidSocietiesEvents.societiesAndroidIntents[i];
    			break;
    		}
    	}
    	
    	if (null == retValue) {
    		for (ThirdPartyEventsIntents event: this.thirdPartyEventsLookup) {
    			if (event.getSocietiesIntent().equals(platformEvent)) {
    				retValue = event.getSocietiesIntent();
    			}
    		}
    	}

     	return retValue;
    }
    /**
     * Translate Societies Android Pubsub intent to an inter-node Societies platform Pubsub event.
     * Uses the two Events/Intents arrays to maps Android intents to Societies equivalent Pubsub events.
     * Also uses the Third party created events array of events.
     * 
     * @param androidIntent
     * @return String Pubsub inter-node event
     */
    private String translateAndroidIntentToEvent(String androidIntent) {
    	String retValue = null;
    	
    	for (int i = 0; i < IAndroidSocietiesEvents.societiesAndroidIntents.length; i++) {
    		if (androidIntent.equals(IAndroidSocietiesEvents.societiesAndroidIntents[i])) {
    			retValue = IAndroidSocietiesEvents.societiesAndroidEvents[i];
    			break;
    		}
    	}
    	
    	if (null == retValue) {
    		for (ThirdPartyEventsIntents event: this.thirdPartyEventsLookup) {
    			if (event.getSocietiesIntent().equals(androidIntent)) {
    				retValue = event.getPubsubNode();
    			}
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
    /**
     * Is a specified Pubsub node valid 
     * 
     * @param node
     * @param intent current service's return value intent
     * @param client 
     * @param sendIntent should a return intent be sent ? 
     * @return boolean 
     */
    private boolean isPubsubNodeValid(String node, String serviceIntent, String client, boolean sendIntent) {
    	boolean retValue = false;
    	
    	for (String validNode : IAndroidSocietiesEvents.societiesAndroidEvents) {
    		if (validNode.equals(node)) {
    			retValue = true;
    			break;
    		}
    	}
    	
    	synchronized (PlatformEventsBase.this.thirdPartyEvents) {
        	if (!retValue) {
        		for (ThirdPartyEventsIntents event: this.thirdPartyEventsLookup) {
        			if (event.getPubsubNode().equals(node)) {
        				retValue = true;
        				break;
        			}
        		}
        	}
    	}
    	//Create intent to signal exception
    	if (!retValue && sendIntent) {
    		Intent returnIntent = new Intent(serviceIntent);
    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, INVALID_PUBSUB_NODE);
    		if (PlatformEventsBase.this.restrictBroadcast) {
    			returnIntent.setPackage(client);
    		}
			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
			Log.d(LOG_TAG, "Invalid Pubsub node return result sent");
    	}
   	
    	return retValue;
    }
    
    /**
     * Is a specified Pubsub node valid and owned by client wishing to delete it ?
     * 
     * @param node
     * @param intent current service's return value intent
     * @param client 
     * @param sendIntent should a return intent be sent ? 
     * @return boolean 
     */
    private boolean isPubsubNodeValidAndOwned(String node, String serviceIntent, String client, boolean sendIntent) {
    	boolean retValue = false;
    	
    	synchronized (PlatformEventsBase.this.thirdPartyEvents) {
    		if (PlatformEventsBase.this.thirdPartyEvents.get(node).equals(client)) {
 				retValue = true;
    		}
    	}
    	//Create intent to signal exception
    	if (!retValue && sendIntent) {
    		Intent returnIntent = new Intent(serviceIntent);
    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, INVALID_PUBSUB_NODE);
    		if (PlatformEventsBase.this.restrictBroadcast) {
    			returnIntent.setPackage(client);
    		}
			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
			Log.d(LOG_TAG, "Invalid Pubsub node return result sent");
    	}
   	
    	return retValue;
    }

    /**
     * Is a specified intent a valid , recognised intent
     * @param intent Societies intent corresponding to a Pubsub node
     * @param intent current service's return value intent
     * @param client 
     * @param sendIntent should a return intent be sent ? 
     * @return boolean true if valid
     */
    private boolean isIntentValid(String intent, String serviceIntent, String client, boolean sendIntent) {
    	boolean retValue = false;
    	
    	for (String validIntent : IAndroidSocietiesEvents.societiesAndroidIntents) {
    		if (validIntent.equals(intent)) {
    			retValue = true;
    			break;
    		}
    	}
    	
    	synchronized (PlatformEventsBase.this.thirdPartyEvents) {
        	if (!retValue) {
        		for (ThirdPartyEventsIntents event: this.thirdPartyEventsLookup) {
        			if (event.getSocietiesIntent().equals(intent)) {
        				retValue = true;
        				break;
        			}
        		}
        	}
    	}
    	//Create intent to signal exception
    	if (!retValue && sendIntent) {
    		Intent returnIntent = new Intent(serviceIntent);
    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false);
    		returnIntent.putExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, INVALID_SOCIETIES_INTENT);
    		if (PlatformEventsBase.this.restrictBroadcast) {
    			returnIntent.setPackage(client);
    		}
			PlatformEventsBase.this.androidContext.sendBroadcast(returnIntent);
			Log.d(LOG_TAG, "Invalid Societies Intent return result sent");
    	}
    	return retValue;
    }

	/**
	 * Utility class that allow an association between a Pubsub node and Societies Android intent
	 *
	 */
	private class ThirdPartyEventsIntents {
		private String pubsubNode;
		private String societiesIntent;
		
		public ThirdPartyEventsIntents(String pubsubNode, String societiesEvent) {
			this.pubsubNode = pubsubNode;
			this.societiesIntent = societiesEvent;
		}
		
		public String getPubsubNode() {
			return pubsubNode;
		}
		
		public void setPubsubNode(String pubsubNode) {
			this.pubsubNode = pubsubNode;
		}
		
		public String getSocietiesIntent() {
			return societiesIntent;
		}
		
		public void setSocietiesIntent(String societiesIntent) {
			this.societiesIntent = societiesIntent;
		}
	}
	
	/**
	 * Remove a {@link ThirdPartyEventsIntents} object from the array of created objects
	 * 
	 * @param pubsubNode
	 */
	private void removeThirdPartyEvent(String pubsubNode) {
		for (Iterator<ThirdPartyEventsIntents> iter = this.thirdPartyEventsLookup.iterator(); iter.hasNext();) {
			if (iter.next().getPubsubNode().equals(pubsubNode)) {
				iter.remove();
				break;
			}
		}
	}
}
