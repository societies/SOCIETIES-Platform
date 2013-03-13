/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.android.remote.helper;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IAndroidSocietiesEventsHelper;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.utilities.DBC.Dbc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
/**
 * This class provides a simple callback interface to the Societies Android Platform Events service
 * It assumes that the service has already been bound to the Android Comms and Pubsub services.
 * 
 * TODO: Insert a timer to automatically unbind from the Platform Events service after a defined amount 
 * of inactivity
 *
 */
public class EventsHelper implements IAndroidSocietiesEventsHelper {
	private final static String LOG_TAG = EventsHelper.class.getName();
	private final static int NUM_METHODS = 11;
	private final static int ILLEGAL_VALUE = -999999;
	
	//Class method enum. Ensure that it remains up to date. Order does not matter. 
    private enum classMethods {
    	subscribeToEvent,
    	subscribeToEvents,
    	subscribeToAllEvents,
    	unSubscribeFromEvent,
    	unSubscribeFromEvents,
    	unSubscribeFromAllEvents,
    	publishEvent,
    	getNumSubscribedNodes,
    	createPubsubNode,
    	deletePubsubNode
    }

	private Context context;
	private String clientPackageName;
	private boolean connectedToEvents;
	private Messenger targetService;
	private BroadcastReceiver receiver;
	private IMethodCallback startupCallback;
	
	
	//Array of queues to store callbacks for each method
	//Assumption is that similar method calls will be answered in correct order
	//as remote service call is via Messenger mechanism which is itself queue oriented
	private ConcurrentLinkedQueue<IPlatformEventsCallback> methodQueues [];

	/**
	 * Default constructor
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public EventsHelper(Context context) {
		this.context = context;
		this.clientPackageName = this.context.getApplicationContext().getPackageName();
		this.connectedToEvents = false;
		this.targetService = null;
		this.methodQueues = (ConcurrentLinkedQueue<IPlatformEventsCallback>[])new ConcurrentLinkedQueue[NUM_METHODS];
		this.startupCallback = null;
		
	}

	@Override
	public boolean setUpService(IMethodCallback callback) {
		Dbc.require("Callback object must be specified", null != callback);
		Log.d(LOG_TAG, "setUpService");
		
		if (!this.connectedToEvents) {
			this.setupBroadcastReceiver();

			this.startupCallback = callback;
        	Intent serviceIntent = new Intent(ICoreSocietiesServices.EVENTS_SERVICE_INTENT);
        	this.context.bindService(serviceIntent, eventsConnection, Context.BIND_AUTO_CREATE);
		}
		return false;
	}

	@Override
	public boolean tearDownService(IMethodCallback callback) {
		Dbc.require("Callback object must be specified", null != callback);
		Log.d(LOG_TAG, "tearDownService");
		if (this.connectedToEvents) {
			this.teardownBroadcastReceiver();
	       	this.context.unbindService(eventsConnection);
			Log.d(LOG_TAG, "tearDownService completed");
	       	callback.returnAction(true);
		}
		return false;
	}

	@Override
	public boolean subscribeToEvent(String societiesIntent, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Intent event must be specified", null != societiesIntent && societiesIntent.length() > 0);
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "subscribeToEvent called for intent: " + societiesIntent);
		
		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.subscribeToEvent.ordinal());
			this.methodQueues[classMethods.subscribeToEvent.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[0];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);

			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), societiesIntent);
			Log.d(LOG_TAG, "intent: " + societiesIntent);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.subscribeToEvent.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}

		return false;
	}

	@Override
	public boolean subscribeToEvents(String intentFilter, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Intent filter must be specified", null != intentFilter && intentFilter.length() > 0);
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "subscribeToEvents called for filter: " + intentFilter);

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.subscribeToEvents.ordinal());
			this.methodQueues[classMethods.subscribeToEvents.ordinal()].add(callback);

			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[1];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);

			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), intentFilter);
			Log.d(LOG_TAG, "intent: " + intentFilter);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.subscribeToEvents.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}
		return false;
	}

	@Override
	public boolean subscribeToAllEvents(IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "subscribeToAllEvents called");

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.subscribeToAllEvents.ordinal());
			this.methodQueues[classMethods.subscribeToAllEvents.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[2];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.subscribeToAllEvents.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}
		return false;
	}

	@Override
	public boolean unSubscribeFromEvent(String societiesIntent, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Intent event must be specified", null != societiesIntent && societiesIntent.length() > 0);
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "unSubscribeFromEvent called for intent: " + societiesIntent);

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.unSubscribeFromEvent.ordinal());
			this.methodQueues[classMethods.unSubscribeFromEvent.ordinal()].add(callback);

			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[3];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);

			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), societiesIntent);
			Log.d(LOG_TAG, "intent: " + societiesIntent);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.unSubscribeFromEvent.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}

		return false;
	}

	@Override
	public boolean unSubscribeFromEvents(String intentFilter, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Intent filter must be specified", null != intentFilter && intentFilter.length() > 0);
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "unSubscribeFromEvents called for filter: " + intentFilter);

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.unSubscribeFromEvents.ordinal());
			this.methodQueues[classMethods.unSubscribeFromEvents.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[4];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);

			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), intentFilter);
			Log.d(LOG_TAG, "intent: " + intentFilter);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.unSubscribeFromEvents.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}
		return false;
	}

	@Override
	public boolean unSubscribeFromAllEvents(IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "unSubscribeFromAllEvents called");

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.unSubscribeFromAllEvents.ordinal());
			this.methodQueues[classMethods.unSubscribeFromAllEvents.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[5];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.unSubscribeFromAllEvents.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}
		return false;
	}

	@Override
	public boolean publishEvent(String societiesIntent, Object eventPayload, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Intent event must be specified", null != societiesIntent && societiesIntent.length() > 0);
		Dbc.require("Event payload object class cannot be null", null != eventPayload);
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "publishEvent called for intent: " + societiesIntent);

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.publishEvent.ordinal());
			this.methodQueues[classMethods.publishEvent.ordinal()].add(callback);

			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[6];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), societiesIntent);
			Log.d(LOG_TAG, "intent: " + societiesIntent);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), (Parcelable) eventPayload);
			Log.d(LOG_TAG, "event payload: " + eventPayload.getClass().getName());
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.getNumSubscribedNodes.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}
		return false;
	}

	@Override
	public int getNumSubscribedNodes(IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "getNumSubscribedNodes called");

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.getNumSubscribedNodes.ordinal());
			this.methodQueues[classMethods.getNumSubscribedNodes.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[7];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.getNumSubscribedNodes.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}
		return 0;
	}

	@Override
	public boolean createEvent(String pubsubNode, String societiesIntent, IPlatformEventsCallback callback)
			throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Pubsub node must be specified", null != pubsubNode && pubsubNode.length() > 0);
		Dbc.require("Societies intent must be specified", null != societiesIntent && societiesIntent.length() > 0);
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "createEvent called");

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.createPubsubNode.ordinal());
			this.methodQueues[classMethods.createPubsubNode.ordinal()].add(callback);

			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[10];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), pubsubNode);
			Log.d(LOG_TAG, "Pubsub Node: " + pubsubNode);
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), societiesIntent);
			Log.d(LOG_TAG, "Societies Intent: " + societiesIntent);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.createPubsubNode.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}

		return false;
	}

	@Override
	public boolean deleteEvent(String pubsubNode, IPlatformEventsCallback callback)
			throws PlatformEventsHelperNotConnectedException {
		Dbc.require("Pubsub node must be specified", null != pubsubNode && pubsubNode.length() > 0);
		Dbc.require("Callback class must be specified", null != callback);
		Log.d(LOG_TAG, "deleteEvent called");

		if (this.connectedToEvents) {
			//add callback class to method queue tail
			initialiseQueue(classMethods.deletePubsubNode.ordinal());
			this.methodQueues[classMethods.deletePubsubNode.ordinal()].add(callback);

			//Select target method and create message to convey remote invocation
	   		String targetMethod = IAndroidSocietiesEvents.methodsArray[11];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), pubsubNode);
			Log.d(LOG_TAG, "Pubsub Node: " + pubsubNode);
			
	   		outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				//Retrieve callback and signal failure
				IPlatformEventsCallback retrievedCallback = this.methodQueues[classMethods.deletePubsubNode.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.returnAction(false);
				}
				Log.e(LOG_TAG, "Cannot send remote method invocation", e);
			}
		} else {
			Log.d(LOG_TAG, "Not connected to Pubsub service");
			throw new PlatformEventsHelperNotConnectedException();
		}

		return false;
	}

	
    /**
     * Events service connection
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection eventsConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform Events service");
        	
        	//Unregister broadcast receiver if service binding broken. Otherwise
        	//the next set-up of the service will one extra receiver causing possible problems.
			EventsHelper.this.teardownBroadcastReceiver();
        	connectedToEvents = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform Events service");

        	connectedToEvents = true;
        	//get a remote binder
        	EventsHelper.this.targetService = new Messenger(service);
        	Log.d(LOG_TAG, "Target service " + name.getShortClassName() + " acquired: " + EventsHelper.this.targetService.getClass().getName());
        	
			Log.d(LOG_TAG, "Retrieve setup callback");
			if (null != EventsHelper.this.startupCallback) {
				Log.d(LOG_TAG, "Setup callback valid");
				EventsHelper.this.startupCallback.returnAction(true);
			}
        }
    };

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs or queues cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class EventsHelperReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());

			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS)) {
				if (null != EventsHelper.this.methodQueues[classMethods.subscribeToAllEvents.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.subscribeToAllEvents.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				if (null != EventsHelper.this.methodQueues[classMethods.subscribeToEvent.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.subscribeToEvent.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}

			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				if (null != EventsHelper.this.methodQueues[classMethods.subscribeToEvents.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.subscribeToEvents.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS)) {
				if (null != EventsHelper.this.methodQueues[classMethods.unSubscribeFromAllEvents.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.unSubscribeFromAllEvents.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT)) {
				if (null != EventsHelper.this.methodQueues[classMethods.unSubscribeFromEvent.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.unSubscribeFromEvent.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				if (null != EventsHelper.this.methodQueues[classMethods.unSubscribeFromEvents.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.unSubscribeFromEvents.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.PUBLISH_EVENT)) {
				if (null != EventsHelper.this.methodQueues[classMethods.publishEvent.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.publishEvent.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS)) {
				if (null != EventsHelper.this.methodQueues[classMethods.getNumSubscribedNodes.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.getNumSubscribedNodes.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, ILLEGAL_VALUE));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.CREATE_EVENT)) {
				if (null != EventsHelper.this.methodQueues[classMethods.createPubsubNode.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.getNumSubscribedNodes.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.DELETE_EVENT)) {
				if (null != EventsHelper.this.methodQueues[classMethods.deletePubsubNode.ordinal()]) {
					IPlatformEventsCallback retrievedCallback = EventsHelper.this.methodQueues[classMethods.getNumSubscribedNodes.ordinal()].poll();
					if (null != retrievedCallback) {
						if (intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.returnException(intent.getIntExtra(IAndroidSocietiesEvents.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			} 
		}
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        this.receiver = new EventsHelperReceiver();
        this.context.registerReceiver(this.receiver, createIntentFilter());    
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
        Log.d(LOG_TAG, "Tear down broadcast receiver");
    	this.context.unregisterReceiver(this.receiver);
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
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
        intentFilter.addAction(IAndroidSocietiesEvents.CREATE_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.DELETE_EVENT);
        
        return intentFilter;
    }
    /**
     * Initialise a queue
     * 
     * @param int index of class methods queue array
     */
    private void initialiseQueue(int index) {
    	if (null == this.methodQueues[index]) {
    		Log.d(LOG_TAG, "Create queue for index: " + index);
    		this.methodQueues[index] = new ConcurrentLinkedQueue<IPlatformEventsCallback>();
    	}
    }
 }
