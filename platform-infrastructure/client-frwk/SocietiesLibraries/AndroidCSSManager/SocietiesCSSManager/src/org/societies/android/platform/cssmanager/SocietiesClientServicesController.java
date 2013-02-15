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
package org.societies.android.platform.cssmanager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.ServiceMethodTranslator;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class SocietiesClientServicesController {
	private final static String LOG_TAG = SocietiesClientServicesController.class.getName();
	//timeout for bind, start and stop all services
	private final static long TASK_TIMEOUT = 10000;
	
	//Service intents for relevant services
	private final static String EVENTS_SERVICE_INTENT = "org.societies.android.platform.events.ServicePlatformEventsRemote";
	private final static String PERSONALISATION_SERVICE_INTENT = "org.societies.android.platform.personalisation.impl.PersonalisationManagerAndroidRemote";
	private final static int NUM_SERVICES = 1;
	private final static int EVENT_SERVICE = 0;
	
	private boolean connectedToEvents;
	private Context context;
	private CountDownLatch servicesBinded;
	private CountDownLatch servicesStarted;
	private CountDownLatch servicesStopped;

	private BroadcastReceiver receiver;

	private Messenger allMessengers [];
	
	public SocietiesClientServicesController(Context context) {
		this.connectedToEvents = false;
		this.context = context;
		allMessengers = new Messenger[NUM_SERVICES];
		setupBroadcastReceiver();
	}
	
	/**
	 * Bind to the app services. Assumes that login has already taken place
	 * 
	 * @param IMethodCallback callback
	 */
	public void bindToServices(IMethodCallback callback) {
		InvokeBindAllServices invoker = new InvokeBindAllServices(callback);
		invoker.execute();
	}
	/**
	 * Unbind from app services
	 * 
	 */
	public void unbindFromServices() {
		
	   	Log.d(LOG_TAG, "Unbind from Societies Android Events Service");
		if (this.connectedToEvents) {
        	this.context.unbindService(eventsConnection);
		}
	}
	/**
	 * Start all Societies Client app services
	 * @param callback
	 */
	public void startAllServices(IMethodCallback callback) {
		InvokeStartAllServices invoker = new InvokeStartAllServices(callback);
		invoker.execute();
	}
	/**
	 * Stop all Societies Client app services
	 * @param callback
	 */
	public void stopAllServices(IMethodCallback callback) {
		InvokeStopAllServices invoker = new InvokeStopAllServices(callback);
		invoker.execute();
	}
	
	/**
	 * Service Connection objects
	 */
	
    /**
     * Events service connection
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection eventsConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform Events service");
        	connectedToEvents = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform Events service");

        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[EVENT_SERVICE] = new Messenger(service);
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
    /**
     * AsyncTasks to carry out asynchronous processing
     */
    
	/**
     * 
     * Async task to bind to all relevant Societies Client app services
     *
     */
    private class InvokeBindAllServices extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeBindAllServices.class.getName();
    	private IMethodCallback callback;
   	 /**
   	 * Default Constructor
   	 * @param IMethodCallback callback
   	 */
    	public InvokeBindAllServices(IMethodCallback callback) {
    		this.callback = callback;
    	}

    	protected Void doInBackground(Void... args) {

    		SocietiesClientServicesController.this.servicesBinded = new CountDownLatch(NUM_SERVICES);
    		
    		boolean retValue = true;
        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android Events Service");
        	Intent serviceIntent = new Intent(EVENTS_SERVICE_INTENT);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, eventsConnection, Context.BIND_AUTO_CREATE);

        	try {
        		//To prevent hanging this latch uses a timeout
        		SocietiesClientServicesController.this.servicesBinded.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
    		} catch (InterruptedException e) {
    			retValue = false;
    			e.printStackTrace();
    		} finally {
    			callback.returnAction(retValue);
    		}

    		return null;
    	}
    }
    
	/**
     * 
     * Async task to start all relevant Societies Client app services
     *
     */
    private class InvokeStartAllServices extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeStartAllServices.class.getName();
    	private IMethodCallback callback;

   	 /**
   	 * Default Constructor
   	 * @param serviceMessenger
   	 */
    	public InvokeStartAllServices(IMethodCallback callback) {
    		this.callback = callback;
    	}

    	protected Void doInBackground(Void... args) {
    		SocietiesClientServicesController.this.servicesStarted = new CountDownLatch(NUM_SERVICES);
    		
    		boolean retValue = true;
    		
    		for (int i  = 0; i < SocietiesClientServicesController.this.allMessengers.length; i++) {
    			
        		String targetMethod = IServiceManager.methodsArray[0];
        		android.os.Message outMessage = getRemoteMessage(targetMethod, i);
           		Bundle outBundle = new Bundle();
           		outMessage.setData(outBundle);
        		Log.d(LOCAL_LOG_TAG, "Call service start method: " + targetMethod);

        		try {
        			SocietiesClientServicesController.this.allMessengers[i].send(outMessage);
    			} catch (RemoteException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
     		}
    		
    		try {
				SocietiesClientServicesController.this.servicesStarted.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				retValue = false;
				e.printStackTrace();
			} finally {
    			callback.returnAction(retValue);
    		}

    		return null;
    	}
    }
	/**
     * 
     * Async task to stop all relevant Societies Client app services
     *
     */
    private class InvokeStopAllServices extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeStopAllServices.class.getName();
    	private IMethodCallback callback;

   	 /**
   	 * Default Constructor
   	 * @param serviceMessenger
   	 */
    	public InvokeStopAllServices(IMethodCallback callback) {
    		this.callback = callback;
    	}

    	protected Void doInBackground(Void... args) {
    		SocietiesClientServicesController.this.servicesStopped = new CountDownLatch(NUM_SERVICES);
    		
    		boolean retValue = true;
    		
    		for (int i  = 0; i < SocietiesClientServicesController.this.allMessengers.length; i++) {
    			
        		String targetMethod = IServiceManager.methodsArray[1];
        		android.os.Message outMessage = getRemoteMessage(targetMethod, i);
           		Bundle outBundle = new Bundle();
           		outMessage.setData(outBundle);

        		Log.d(LOCAL_LOG_TAG, "Call service stop method: " + targetMethod);

        		try {
        			SocietiesClientServicesController.this.allMessengers[i].send(outMessage);
    			} catch (RemoteException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
     		}
    		
    		try {
				SocietiesClientServicesController.this.servicesStopped.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				retValue = false;
				e.printStackTrace();
			} finally {
    			callback.returnAction(retValue);
    		}

    		return null;
    	}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());

			if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				//As each service starts decrement the latch
				SocietiesClientServicesController.this.servicesStopped.countDown();
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				//As each service starts decrement the latch
				SocietiesClientServicesController.this.servicesStarted.countDown();
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_EXCEPTION_INFO)) {
				
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
        
        this.receiver = new MainReceiver();
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
        
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_EXCEPTION_INFO);
        return intentFilter;
    }
    /**
     * Create the correct message for remote method invocation
     * 
     * @param targetMethod
     * @param index
     * @return android.os.Message
     */
    private android.os.Message getRemoteMessage(String targetMethod, int index) {
		android.os.Message retValue = null;
		
		switch (index) {
		case EVENT_SERVICE:
			retValue = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
			break;
		default:
		}
		return retValue;
    }
}
