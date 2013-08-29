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
package org.societies.android.platform.css.friends;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.VCardParcel;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.androidutils.AndroidNotifier;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.css.directory.CssFriendEvent;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
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
public class EventService extends Service {

	private static final String LOG_TAG = EventService.class.getName();
	private static final String SERVICE_ACTION   = "org.societies.android.platform.events.ServicePlatformEventsRemote";
	private static final String CLIENT_NAME      = "org.societies.android.platform.css.friends.EventService";
	private static final String EXTRA_CSS_ADVERT = "org.societies.api.schema.css.directory.CssAdvertisementRecord";
	private static final String EXTRA_CSS_VCARD  = "org.societies.android.api.comms.xmpp.VCardParcel";
	private static final String ALL_CSS_FRIEND_INTENTS = "org.societies.android.css.friends";
	
	//TRACKING CONNECTION TO EVENTS MANAGER
	private boolean boundToEventMgrService = false;
	private Messenger eventMgrService = null;
	private BroadcastReceiver receiver;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private ClientCommunicationMgr ccm;
	
	private final Set<String> processedIncomingRequests = new HashSet<String>();

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		  
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		      
		@Override
		public void handleMessage(Message msg) {
			Log.d(this.getClass().getName(), "Message received in Friends Service thread");
			if (!boundToEventMgrService) {
				setupBroadcastReceiver();
				bindToEventsManagerService();
			}
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			//stopSelf(msg.arg1);
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>FRIENDS SERVICE LIFECYCLE METHODS>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public IBinder onBind(Intent intent) {
		return null;	//NO-ONE ALLOWED TO BIND TO THIS SERVICE
	}

	@Override
	public void onCreate() {
		Log.d(this.getClass().getName(), "Friends Service creating...");
		this.ccm = new ClientCommunicationMgr(this, true);
        this.ccm.bindCommsService(new IMethodCallback() {
			@Override
			public void returnException(String result) { 
				Log.d(LOG_TAG, "Exception binding to service: " + result);
			}
			@Override
			public void returnAction(String result) { 
				Log.d(LOG_TAG, "return Action.flag: " + result);
			}
			@Override
			public void returnAction(boolean resultFlag) { 
				Log.d(LOG_TAG, "return Action.flag: " + resultFlag);
			}
		});
		
		// START BACKGROUND THREAD FOR SERVICE
		HandlerThread thread = new HandlerThread("FriendServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		
		// Get the HandlerThread's Looper and use it for our Handler 
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// For each start request, send a message to start a job and deliver the
		//start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
	  	mServiceHandler.sendMessage(msg);
	  
	  	//If we get killed, after returning from here, restart
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Friends service terminating");
		boundToEventMgrService = false;
		this.unregisterReceiver(receiver);
		this.unbindService(serviceConnection);
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BIND TO EXTERNAL "EVENT MANAGER">>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/** Bind to the Events Manager Service */
	private void bindToEventsManagerService() {
		Log.d(LOG_TAG, "Binding to Events Manager Service...");
		Intent serviceIntent = new Intent(SERVICE_ACTION);
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
	/**Create a broadcast receiver */
    private void setupBroadcastReceiver() {
    	Log.d(LOG_TAG, "Setting up broadcast receiver...");
    	receiver = new MainReceiver();
        this.registerReceiver(receiver, createIntentFilter());
        Log.d(LOG_TAG, "Registered broadcast receiver");
    }
    
    /**Broadcast receiver to receive intent return values from EventManager service*/
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			//EVENT MANAGER INTENTS
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				Log.d(LOG_TAG, "Subscribed to event: " + intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				Log.d(LOG_TAG, "Subscribed to multiple events: " + intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				Log.d(LOG_TAG, "Un-subscribed to events: " + intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
			}
			//PUBSUB EVENTS - payload is CssFriendEvent 
			else if (intent.getAction().equals(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_RECEIVED_INTENT)) {
				Log.d(LOG_TAG, "Frient Request received event");
				CssFriendEvent eventPayload = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				//BUGFIX TO STOP MULTIPLE NOTIFICATIONS FOR SAME REQUEST (UNKNOWN ROOT CAUSE)
				String receivedID = eventPayload.getCssAdvert().getId(); 
				synchronized(processedIncomingRequests) {
					if (processedIncomingRequests.contains(receivedID)) {
						Log.d(LOG_TAG, "Ignoring duplicate request from: " + receivedID);
						return;
					}
					processedIncomingRequests.add(receivedID);
				}
				//CONTINUE - GET VCARD FOR THIS REQUEST
				ICommCallback callback = new VCardCallback(eventPayload);
		    	ccm.getVCard(eventPayload.getCssAdvert().getId(), callback);
			}
			else if (intent.getAction().equals(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_ACCEPTED_INTENT)) {
				Log.d(LOG_TAG, "Frient Request accepted event");
				CssFriendEvent eventPayload = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				String description = eventPayload.getCssAdvert().getName() + " accepted your friend request";
				addNotificationAccept(description, "Friend Request Accepted", eventPayload.getCssAdvert());
			}
		}
    }
    
    /**Create a suitable intent filter
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
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), ALL_CSS_FRIEND_INTENTS);
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


    private void addNotificationAccept(String description, String eventType, CssAdvertisementRecord advert) {
    	//CREATE ANDROID NOTIFICATION
		int notifierflags[] = new int[] {Notification.DEFAULT_SOUND, Notification.DEFAULT_VIBRATE, Notification.FLAG_AUTO_CANCEL};
		AndroidNotifier notifier = new AndroidNotifier(EventService.this.getApplicationContext(), Notification.DEFAULT_SOUND, notifierflags);
		
		//CREATE INTENT FOR LAUNCHING ACTIVITY
		//Intent intent = new Intent();
		//PackageManager manager = getPackageManager();
		//intent = manager.getLaunchIntentForPackage("org.societies.android.platform.gui");
		//intent.addCategory(Intent.CATEGORY_LAUNCHER);
		///intent.putExtra(EXTRA_CSS_ADVERT, (Parcelable)advert);
		//intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		
		notifier.notifyMessage(description, eventType, AcceptFriendActivity.class);
    }
    
    /**
	 * Callback used with Android Comms for CSSDirectory
	 *
	 */
	private class VCardCallback implements ICommCallback {
		private CssFriendEvent eventPayload;
		
		public VCardCallback(CssFriendEvent eventPayload) {
			this.eventPayload = eventPayload;
		}
		
		public List<String> getXMLNamespaces() { return null;}
		public List<String> getJavaPackages() {  return null;}
		public void receiveError(Stanza arg0, XMPPError arg1) { }
		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) { }
		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {	}
		public void receiveMessage(Stanza arg0, Object arg1) { }

		public void receiveResult(Stanza arg0, Object retValue) {
			Log.d(VCardCallback.class.getName(), "VCardCallback Callback receiveResult");
			VCardParcel vCard = (VCardParcel)retValue;
			
			//CREATE ANDROID NOTIFICATION
			int notifierflags [] = new int [1];
			notifierflags[0] = Notification.FLAG_AUTO_CANCEL;
			AndroidNotifier notifier = new AndroidNotifier(EventService.this.getApplicationContext(), Notification.DEFAULT_SOUND, notifierflags);
			
			//CREATE INTENT FOR LAUNCHING ACTIVITY
			Intent intent = new Intent(EventService.this.getApplicationContext(), AcceptFriendActivity.class);
			intent.putExtra(EXTRA_CSS_ADVERT, (Parcelable)eventPayload.getCssAdvert());
			intent.putExtra(EXTRA_CSS_VCARD, (Parcelable)vCard);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			String description = eventPayload.getCssAdvert().getName() + " sent a friend request";
			notifier.notifyMessage(description, "Friend Request", AcceptFriendActivity.class, intent, "SOCIETIES");
		}
	}
}
