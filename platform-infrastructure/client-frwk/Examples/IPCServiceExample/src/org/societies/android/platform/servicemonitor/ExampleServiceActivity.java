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

package org.societies.android.platform.servicemonitor;

import java.util.List;

import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.examples.AndroidParcelable;
import org.societies.android.api.internal.examples.ICoreServiceExample;
import org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor;
import org.societies.android.platform.servicemonitor.SameProcessService.LocalBinder;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
/**
 * TODO: Work required for activity instance changes such as rotations
 * 1. Use the getApplicationContext() rather than the activity's context
 * 2. Pass the ServiceConnection object representing the service binding from the old to the 
 * new instance of the activity.
 * 
 *  
 */
public class ExampleServiceActivity extends Activity {
	private boolean ipBoundToService = false;
	private boolean opBoundToService = false;
	private boolean realBoundToService = false;
	private ICoreServiceMonitor targetIPService = null;
	private Messenger targetOPService = null;
	private Messenger targetRealService = null;
	
	private long serviceBinding;
	private long serviceInvoke;
	private static final int NUM_SERVICE_INVOKES = 1;
	private ProgressBar serviceProgress;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        serviceProgress = (ProgressBar) findViewById(R.id.pbarStartedService);
    }
    @Override
    protected void onStart() {
    	super.onStart();
    	Intent ipIntent = new Intent(this, SameProcessService.class);
    	Intent opIntent = new Intent(this, DifferentProcessService.class);
    	Intent realIntent = new Intent(this, CoreMonitor.class);
    	
    	serviceBinding = System.currentTimeMillis();
    	
    	Log.i(this.getClass().getName(), "Start in process service: " + Long.toString(serviceBinding));
    	bindService(ipIntent, inProcessServiceConnection, Context.BIND_AUTO_CREATE);
    	Log.i(this.getClass().getName(), "Start out of process service: " + Long.toString(serviceBinding - System.currentTimeMillis()));
    	bindService(opIntent, outProcessServiceConnection, Context.BIND_AUTO_CREATE);
    	Log.i(this.getClass().getName(), "Start out of process real service: " + Long.toString(serviceBinding - System.currentTimeMillis()));
    	bindService(realIntent, realProcessServiceConnection, Context.BIND_AUTO_CREATE);

    	Log.i(this.getClass().getName(), "All services started: " + Long.toString(serviceBinding - System.currentTimeMillis()));
    	
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(CoreMonitor.ACTIVE_TASKS);
        intentFilter.addAction(CoreMonitor.ACTIVE_SERVICES);
        intentFilter.addAction(CoreMonitor.GET_NODE_DETAILS);
        intentFilter.addAction(AnotherStartedService.PROGRESS_STATUS_INTENT);
        this.registerReceiver(new ServiceReceiver(), intentFilter);

    }
    
    protected void onStop() {
    	super.onStop();
    	if (ipBoundToService) {
    		unbindService(inProcessServiceConnection);
    	}
    	if (opBoundToService) {
    		unbindService(outProcessServiceConnection);
    	}
    	if (realBoundToService) {
    		unbindService(realProcessServiceConnection);
    	}
    }
    /**
     * Call an in-process service. Service consumer simply calls service API and can use 
     * return value
     *  
     * @param view
     */
    public void onButtonInprocessClick(View view) {
    	if (ipBoundToService) {
    		EditText text = (EditText) findViewById(R.id.editTxtServiceResult);
    		serviceInvoke = System.currentTimeMillis();
        	Log.i(this.getClass().getName(), "Call in process service: " + Long.toString(serviceInvoke));
        	Log.i(this.getClass().getName(), "Number of invocations: " + NUM_SERVICE_INVOKES);

			for (int i = 0; i < NUM_SERVICE_INVOKES; i++) {
//	    		text.setText(targetIPService.getGreeting("to me"));
				List services = targetIPService.activeServices(null);
//				for (Object service : services) {
//					ActivityManager.RunningServiceInfo serviceInfo = (ActivityManager.RunningServiceInfo) service;
//					Log.i(this.getClass().getName(), "In process Active Services: " + serviceInfo.service.flattenToString());
//				}
			}
        	Log.i(this.getClass().getName(), "End call in process service: " + Long.toString(serviceInvoke - System.currentTimeMillis()));
        	Log.i(this.getClass().getName(), "Average in process service call: " + Long.toString((serviceInvoke - System.currentTimeMillis())/NUM_SERVICE_INVOKES));
    	}
    }
    /**
     * Call an out-of-process service. Process involves:
     * 1. Select valid method signature
     * 2. Create message with corresponding index number
     * 3. Create a bundle (cf. http://developer.android.com/reference/android/os/Bundle.html) for restrictions on data types 
     * 4. Add parameter values. The values are held in key-value pairs with the parameter name being the key
     * 5. Send message
     * 
     * Currently no return value is returned. To do so would require a reverse binding process from the service, 
     * i.e a callback interface and handler/messenger code in the consumer. The use of intents or even selective intents (define
     * an intent that can only be intercepted by a stated application) will achieve the same result with less binding.
     * @param view
     */
    public void onButtonOutprocessClick(View view) {
    	if (opBoundToService) {
    		
    		String targetMethod = "getNumberGreeting(String appendToMessage, int number)";
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICoreServiceExample.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), "to Midge");
    		outBundle.putInt(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), 2);
    		outMessage.setData(outBundle);
    		serviceInvoke = System.currentTimeMillis();
        	Log.i(this.getClass().getName(), "Call out of process service: " + Long.toString(serviceInvoke));
        	Log.i(this.getClass().getName(), "Number of invocations: " + NUM_SERVICE_INVOKES);

    		try {
    			for (int i = 0; i < NUM_SERVICE_INVOKES; i++) {
    				targetOPService.send(outMessage);
    			}
	        	Log.i(this.getClass().getName(), "End call out of process service: " + Long.toString(serviceInvoke - System.currentTimeMillis()));
	        	Log.i(this.getClass().getName(), "Average out of process service call: " + Long.toString((serviceInvoke - System.currentTimeMillis())/NUM_SERVICE_INVOKES));

			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Call a real out-of-process service. Process involves:
     * 1. Select valid method signature
     * 2. Create message with corresponding index number
     * 3. Create a bundle (cf. http://developer.android.com/reference/android/os/Bundle.html) for restrictions on data types 
     * 4. Add parameter values. The values are held in key-value pairs with the parameter name being the key
     * 5. Send message
     * 
     * Currently no return value is returned. To do so would require a reverse binding process from the service, 
     * i.e a callback interface and handler/messenger code in the consumer. The use of intents or even selective intents (define
     * an intent that can only be intercepted by a stated application) will achieve the same result with less binding.
     * @param view
     */
    public void onButtonRealProcessClick(View view) {
    	if (realBoundToService) {
    		
//    		String targetMethod = "activeServices(String client)";
//    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICoreServiceMonitor.methodsArray, targetMethod), 0, 0);
//    		Bundle outBundle = new Bundle();
//    		/*
//    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
//    		 * only the client can receive it.
//    		 */
//    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.getPackageName());
//    		Log.d(this.getClass().getName(), "Client Package Name: " + this.getPackageName());
//    		outMessage.setData(outBundle);
//    		serviceInvoke = System.currentTimeMillis();
//        	Log.i(this.getClass().getName(), "Call out of process real service: " + Long.toString(serviceInvoke));
//        	Log.i(this.getClass().getName(), "Number of invocations: " + NUM_SERVICE_INVOKES);
//
//    		try {
//    			for (int i = 0; i < NUM_SERVICE_INVOKES; i++) {
//    				targetRealService.send(outMessage);
//    			}
//	        	Log.i(this.getClass().getName(), "End call out of process real service: " + Long.toString(serviceInvoke - System.currentTimeMillis()));
//	        	Log.i(this.getClass().getName(), "Average out of process real service call: " + Long.toString((serviceInvoke - System.currentTimeMillis())/NUM_SERVICE_INVOKES));
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
    		
    		String targetMethod = ICoreServiceMonitor.methodsArray[8];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICoreServiceMonitor.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.getPackageName());
    		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), new AndroidParcelable());
    		Log.d(this.getClass().getName(), "Client Package Name: " + this.getPackageName());
    		outMessage.setData(outBundle);
    		serviceInvoke = System.currentTimeMillis();
        	Log.i(this.getClass().getName(), "Call out of process real service: " + Long.toString(serviceInvoke));
        	Log.i(this.getClass().getName(), "Number of invocations: " + NUM_SERVICE_INVOKES);

    		try {
    			for (int i = 0; i < NUM_SERVICE_INVOKES; i++) {
    				targetRealService.send(outMessage);
    			}
	        	Log.i(this.getClass().getName(), "End call out of process real service: " + Long.toString(serviceInvoke - System.currentTimeMillis()));
	        	Log.i(this.getClass().getName(), "Average out of process real service call: " + Long.toString((serviceInvoke - System.currentTimeMillis())/NUM_SERVICE_INVOKES));
			} catch (RemoteException e) {
				e.printStackTrace();
			}

    	}
    }
    /**
     * Start a "started" service
     * 
     * @param view
     */
    public void onButtonStartStartedClick(View view) {
    	
    	serviceProgress.setProgress(0);
    			
    	Intent intent = new Intent(this, AnotherStartedService.class);
    	try {
        	startService(intent);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Stop a "started" service
     * 
     * @param view
     */
    public void onButtonStopStartedClick(View view) {
    	Intent intent = new Intent(this, AnotherStartedService.class);
    	stopService(intent);
    }
    
	private ServiceConnection inProcessServiceConnection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			ipBoundToService = false;
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			targetIPService = (ICoreServiceMonitor) binder.getService();
	    	Log.i(this.getClass().getName(), "In process service connected: " + Long.toString(serviceBinding - System.currentTimeMillis()));

			ipBoundToService = true;
			
		}
	};
	
	private ServiceConnection outProcessServiceConnection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			opBoundToService = false;
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			opBoundToService = true;
			targetOPService = new Messenger(service);
	    	Log.i(this.getClass().getName(), "Out of process service connected: " + Long.toString(serviceBinding - System.currentTimeMillis()));
			
			
		}
	};

	private ServiceConnection realProcessServiceConnection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			realBoundToService = false;
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			realBoundToService = true;
			targetRealService = new Messenger(service);
	    	Log.i(this.getClass().getName(), "Out of process real service connected: " + Long.toString(serviceBinding - System.currentTimeMillis()));
			
			
		}
	};

	/**
	 * Broadcast receiver to receive intents from Service methods
	 * 
	 * TODO: Intent Categories could be used to discriminate between 
	 * returned method intents rather than an intent per method 
	 *
	 */
	private class ServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("ServiceReceiver", intent.getAction());
			
			
			if (intent.getAction().equals(CoreMonitor.ACTIVE_TASKS)) {
		    	Log.i(this.getClass().getName(), "Out of process real service received intent - active tasks: " + Long.toString(serviceInvoke - System.currentTimeMillis()));

				Parcelable parcels [] =  intent.getParcelableArrayExtra(CoreMonitor.INTENT_RETURN_KEY);
				
				for (Parcelable parcel : parcels) {
					Log.i(this.getClass().getName(), "Tasks: " + ((ActivityManager.RunningTaskInfo) parcel).baseActivity.flattenToString() + 
														"ID: " + ((ActivityManager.RunningTaskInfo) parcel).id);
		        	Log.i(this.getClass().getName(), "Average out of process real service call -  - active tasks: " + Long.toString((serviceInvoke - System.currentTimeMillis())/NUM_SERVICE_INVOKES));
				}
			} else if (intent.getAction().equals(CoreMonitor.ACTIVE_SERVICES)) {
		    	Log.i(this.getClass().getName(), "Out of process real service received intent - active services: " + Long.toString(serviceInvoke - System.currentTimeMillis()));
	        	Log.i(this.getClass().getName(), "Average out of process real service call -  - active services: " + Long.toString((serviceInvoke - System.currentTimeMillis())/NUM_SERVICE_INVOKES));
				Parcelable parcels [] =  intent.getParcelableArrayExtra(CoreMonitor.INTENT_RETURN_KEY);
				
				for (Parcelable parcel : parcels) {
					Log.i("ServiceReceiver", "Services: " + ((ActivityManager.RunningServiceInfo) parcel).service.flattenToString() + 
													"Process ID: " + ((ActivityManager.RunningServiceInfo) parcel).pid + 
													"Process: " + ((ActivityManager.RunningServiceInfo) parcel).process);
				}
				
			} else if (intent.getAction().equals(CoreMonitor.GET_NODE_DETAILS)) {
		    	Log.i(this.getClass().getName(), "Out of process real service received intent - active services: " + Long.toString(serviceInvoke - System.currentTimeMillis()));
	        	Log.i(this.getClass().getName(), "Average out of process real service call -  - active services: " + Long.toString((serviceInvoke - System.currentTimeMillis())/NUM_SERVICE_INVOKES));
				Parcelable parcel =  intent.getParcelableExtra(CoreMonitor.INTENT_RETURN_KEY);
				
				AndroidParcelable cssNode = (AndroidParcelable) parcel;
				Log.i("ServiceReceiver", "Node: status" + cssNode.getStatus() + " type: " + cssNode.getType() + " identity: " + cssNode.getIdentity());
				
			} else if (intent.getAction().equals(AnotherStartedService.PROGRESS_STATUS_INTENT)) {
				serviceProgress.setProgress(intent.getIntExtra(AnotherStartedService.PROGRESS_STATUS_VALUE, serviceProgress.getProgress()));
			}
		}
		
	}
	
}