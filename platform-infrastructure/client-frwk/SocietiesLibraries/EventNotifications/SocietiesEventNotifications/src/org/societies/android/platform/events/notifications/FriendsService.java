/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.events.notifications;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.androidutils.AndroidNotifier;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class FriendsService extends Service {

	private static final String LOG_TAG = FriendsService.class.getName();
	//THIS LOCAL SERVICE
	private IBinder binder = null;
	
	//TRACKING CONNECTION TO EVENTS MANAGER
	private boolean boundToEventMgrService = false;
	private Messenger eventMgrService = null;
	private static final String SERVICE_ACTION = "org.societies.android.platform.events.ServicePlatformEventsRemote";
	private static final String CLIENT_NAME    = "org.societies.android.platform.events.notifications.FriendsService";
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>STARTING THIS SERVICE>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}
	
	@Override
	public void onCreate () {
		this.binder = new LocalBinder();
		Log.d(LOG_TAG, "Friends service starting");
		
		setupBroadcastReceiver();
		bindToEventsManagerService();
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Friends service terminating");
	}
	
	/**Create Binder object for local service invocation */
	public class LocalBinder extends Binder {
		public FriendsService getService() {
			return FriendsService.this;
		}
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BIND TO EXTERNAL "EVENT MANAGER">>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/** Bind to the Events Manager Service */
	private void bindToEventsManagerService() {
    	Intent serviceIntent = new Intent(SERVICE_ACTION);
    	Log.d(LOG_TAG, "Binding to Events Manager Service: ");
    	bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			boundToEventMgrService = true;
			eventMgrService = new Messenger(service);
			Log.d(this.getClass().getName(), "Connected to the Societies Event Mgr Service");
			
			//BOUND TO SERVICE - SUBSCRIBE TO RELEVANT EVENTS
			InvokeRemoteMethod invoke  = new InvokeRemoteMethod(CLIENT_NAME);
    		invoke.execute();
		}
		
		public void onServiceDisconnected(ComponentName name) {
			boundToEventMgrService = false;
		}
	};

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>REGISTER FOR EVENTS>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**Create a broadcast receiver
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        this.registerReceiver(receiver, createIntentFilter());
        Log.d(LOG_TAG, "Registered broadcast receiver");

        return receiver;
    }
    
    /**Broadcast receiver to receive intent return values from service method calls only*/
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			//EVENT MANAGER INTENTS
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				Log.d(LOG_TAG, "Subscribed to all event - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				Log.d(LOG_TAG, "Subscribed to events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				Log.d(LOG_TAG, "Un-subscribed from events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			}
			//PUBSUB EVENTS
			else if (intent.getAction().equals(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_RECEIVED_EVENT)) {
				Log.d(LOG_TAG, "Frient Request received: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
				Parcelable payload = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
			}
			else if (intent.getAction().equals(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_ACCEPTED_EVENT)) {
				Log.d(LOG_TAG, "Frient Request accepted: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
				Parcelable payload = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
			}
			
		}
    }
    
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        //EVENT MANAGER INTENTS
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS);
        //PUBSUB INTENTS
        intentFilter.addAction(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_RECEIVED_INTENT);
        intentFilter.addAction(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_ACCEPTED_INTENT);
        return intentFilter;
    }
    
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>SUBSCRIBE TO PUBSUB EVENTS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/** Async task to invoke remote service method */
    private class InvokeRemoteMethod extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeRemoteMethod.class.getName();
    	private String client;

    	public InvokeRemoteMethod(String client) {
    		this.client = client;
    	}

    	protected Void doInBackground(Void... args) {
    		//METHOD: subscribeToEvents(String client, String intentFilter) - ARRAY POSITION: 1
    		String targetMethod = IAndroidSocietiesEvents.methodsArray[1];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();

    		//PARAMETERS
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_RECEIVED_INTENT);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Sending event registration");
    		try {
    			eventMgrService.send(outMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }

    private void addNotification(Context context, String description, String eventType) {
    	//CREATE ANDROID NOTIFICATION
		int notifierflags [] = new int [1];
		notifierflags[0] = Notification.FLAG_AUTO_CANCEL;
		AndroidNotifier notifier = new AndroidNotifier(FriendsService.this.getApplicationContext(), Notification.DEFAULT_SOUND, notifierflags);
		
		//CREATE INTENT FOR LAUNCHING ACTIVITY
		Intent intent = new Intent();
		notifier.notifyMessage(description, eventType, FriendsActivity.class);
	}
}
