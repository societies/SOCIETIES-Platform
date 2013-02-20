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
package org.societies.android.platform.servicelifecycle;

import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.android.api.servicelifecycle.IServiceUtilities;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.servicemonitor.ServiceUtilitiesRemote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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
public class TestUtilitiesRemote extends Activity {

    private boolean servUtilRemoteConnected = false;
	private Messenger remoteUtilitiesMessenger = null;
	
	 private static final String LOG_TAG = TestUtilitiesRemote.class.getName();
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Create intent for remote Service Utilities
        Intent intentUtilitiesRemote = new Intent(this.getApplicationContext(), ServiceUtilitiesRemote.class);
        this.getApplicationContext().bindService(intentUtilitiesRemote, remoteServiceUtilities, Context.BIND_AUTO_CREATE);
        
        //REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(IServiceUtilities.GET_MY_SERVICE_ID);
        
        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //TEST THE SLM COMPONENT
        TestRemoteUtility task = new TestRemoteUtility(this);
        task.execute();
    }
    
    
	private ServiceConnection remoteServiceUtilities = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			servUtilRemoteConnected = false;
        	Log.d(LOG_TAG, "Disconnecting from ServiceUtilitiesRemote");
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			servUtilRemoteConnected = true;
			remoteUtilitiesMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "ServiceUtilitiesRemote connected");
		}
	};
	
	
	private class TestRemoteUtility extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestRemoteUtility(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {
    		
    		Log.d(LOG_TAG, ">>>>>>>>ServiceUtilitiesRemote connected: " + servUtilRemoteConnected);
    		if (! servUtilRemoteConnected) {
	    		try {//	WAIT TILL SERVICE IS CONNECTED - OCCURS ASYNCHRONOUSLY
					Thread.currentThread();
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		
    		if (servUtilRemoteConnected) {
	    		//TEST SERVICE UTILITIES REMOTE
	    		try {
	        		String targetMethod = IServiceUtilities.methodsArray[0];
	        		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IServiceUtilities.methodsArray, targetMethod), 0, 0);
	
	        		Bundle outBundle = new Bundle();
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), "org.societies.android.platform.servicelifecycle");
	        		outMessage.setData(outBundle);
	
	    			remoteUtilitiesMessenger.send(outMessage);
	
				} catch (RemoteException e) {
					e.printStackTrace();
				}
    		} else {
    			Log.d(LOG_TAG, "Still not connected to ServiceUtilitiesRemote");
    		}
 
    		return null;
    	}
    }
    
    private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if (intent.getAction().equals(IServiceUtilities.GET_MY_SERVICE_ID)) {
				//UNMARSHALL THE ID FROM THE RETURNED PARCEL
				Parcelable parcel =  intent.getParcelableExtra(IServiceUtilities.INTENT_RETURN_VALUE);
				AServiceResourceIdentifier sri =  (AServiceResourceIdentifier) parcel;
				Log.d(LOG_TAG, ">>>>>GET MY SERVICE ID RESULTS:\nSRI.identifier: " + sri.getIdentifier().toString());
				Log.d(LOG_TAG, "SRI.ServiceInstanceId: " + sri.getServiceInstanceIdentifier());
			}
		}
	};

}
