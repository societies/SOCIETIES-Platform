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
package org.societies.android.platform.cistester;

import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.cis.management.AActivity;
import org.societies.android.api.cis.management.ACommunity;
import org.societies.android.api.cis.management.AParticipant;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.cis.CisManagerRemote;
import org.societies.api.schema.cis.manager.ListCrit;

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
public class TestCisRemote  extends Activity {
	private final String CLIENT_PACKAGE = "org.societies.android.platform.cis.cistester";
	
	//CIS MANAGER REMOTE
    private boolean servCisMgrRemoteConnected = false;
	private Messenger cisMgrMessenger = null;

	//CIS SUBSCRIBED REMOTE
	private boolean servCisSubscribeRemoteConnected = false;
	private Messenger cisSubscribeMessenger = null;
	
	//CIS DIRECTORY REMOTE
	private boolean servCisDirRemoteConnected = false;
	private Messenger cisDirMessenger = null;
	
	 private static final String LOG_TAG = TestCisRemote.class.getName();
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //CREATE INTENT FOR CIS MANAGER
        Intent intentCisMgrRemote = new Intent(this.getApplicationContext(), CisManagerRemote.class);
        this.getApplicationContext().bindService(intentCisMgrRemote, remoteServiceCisManager, Context.BIND_AUTO_CREATE);
        
        //CREATE INTENT FOR CIS SUBSCRIBED
        Intent intentCisSubscribeRemote = new Intent(this.getApplicationContext(), CisManagerRemote.class);
        this.getApplicationContext().bindService(intentCisSubscribeRemote, remoteServiceCisSubscribe, Context.BIND_AUTO_CREATE);
        
        //CREATE INTENT FOR CIS SUBSCRIBED
        Intent intentCisDirRemote = new Intent(this.getApplicationContext(), CisManagerRemote.class);
        this.getApplicationContext().bindService(intentCisDirRemote, remoteServiceCisDir, Context.BIND_AUTO_CREATE);
        
        //REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        //CIS Manager
        intentFilter.addAction(ICisManager.CREATE_CIS);
        intentFilter.addAction(ICisManager.DELETE_CIS);
        intentFilter.addAction(ICisManager.GET_CIS_LIST);
        intentFilter.addAction(ICisManager.JOIN_CIS);
        //CIS Subscriber
        intentFilter.addAction(ICisSubscribed.GET_MEMBERS);
        intentFilter.addAction(ICisSubscribed.GET_ACTIVITY_FEED);
        intentFilter.addAction(ICisSubscribed.ADD_ACTIVITY);
        //CIS DIRECTORY
        intentFilter.addAction(ICisDirectory.FIND_ALL_CIS);
        intentFilter.addAction(ICisDirectory.FILTER_CIS);
        intentFilter.addAction(ICisDirectory.FIND_CIS_ID);
        
        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //TEST THE SLM COMPONENT
        TestRemoteUtility task = new TestRemoteUtility(this);
        task.execute();
    }
    
	private ServiceConnection remoteServiceCisManager = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			servCisMgrRemoteConnected = false;
        	Log.d(LOG_TAG, "Disconnecting from remoteServiceCisManager");
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			servCisMgrRemoteConnected = true;
			cisMgrMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "remoteServiceCisManager connected");
		}
	};
	
	private ServiceConnection remoteServiceCisSubscribe = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			servCisMgrRemoteConnected = false;
        	Log.d(LOG_TAG, "Disconnecting from remoteServiceCisManager");
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			servCisMgrRemoteConnected = true;
			cisMgrMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "remoteServiceCisManager connected");
		}
	};
	
	private ServiceConnection remoteServiceCisDir = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			servCisDirRemoteConnected = false;
        	Log.d(LOG_TAG, "Disconnecting from remoteServiceCisDir");
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			servCisDirRemoteConnected = true;
			cisDirMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "remoteServiceCisDir connected");
		}
	};
	
	private class TestRemoteUtility extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestRemoteUtility(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {
    		
    		Log.d(LOG_TAG, ">>>>>>>>ServiceUtilitiesRemote connected: " + servCisMgrRemoteConnected);
    		if (! servCisMgrRemoteConnected) {
	    		try {//	WAIT TILL SERVICE IS CONNECTED - OCCURS ASYNCHRONOUSLY
					Thread.currentThread();
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		
    		if (servCisMgrRemoteConnected) {
	    		//TEST CIS DIRECTORY REMOTE
	    		try {
	    			//TEST: GET ALL CIS ADVERTISEMENTS FROM DIRECTORY
	    			Log.d(LOG_TAG, "GET ALL CIS ADVERTISEMENTS FROM DIRECTORY");
	    			String targetMethod = ICisDirectory.methodsArray[0]; // "findAllCisAdvertisementRecords(String client)"
	        		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisDirectory.methodsArray, targetMethod), 0, 0);
	        		Bundle outBundle = new Bundle();
	        		//PARAMETERS
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), CLIENT_PACKAGE);
	        		outMessage.setData(outBundle);	
	        		cisDirMessenger.send(outMessage);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
    		} else {
    			Log.d(LOG_TAG, "Still not connected to servCisMgrRemoteConnected");
    		}
 
    		return null;
    	}
    }
	
	private void continueTests(String cis_id, String cis_name) {
		//TEST: LIST CIS'S (OWNED + SUBSCRIBED)
		Log.d(LOG_TAG, "TEST: LIST CIS'S (OWNED + SUBSCRIBED)");
		String targetMethod = ICisManager.methodsArray[2]; // "getCisList(String client, String query)",
		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisManager.methodsArray, targetMethod), 0, 0);
		Bundle outBundle = new Bundle();
		//PARAMETERS
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), CLIENT_PACKAGE);
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), ListCrit.ALL.toString());
		outMessage.setData(outBundle);	
		try {
			cisDirMessenger.send(outMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	
		//TEST: GET MEMBERS OF CIS
		Log.d(LOG_TAG, "TEST: GET MEMBERS OF CIS");
		targetMethod = ICisSubscribed.methodsArray[0]; // "getMembers(String client, String cisId)",
		outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisSubscribed.methodsArray, targetMethod), 0, 0);
		outBundle = new Bundle();
		//PARAMETERS
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), CLIENT_PACKAGE);
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), ListCrit.ALL.toString());
		outMessage.setData(outBundle);	
		try {
			cisSubscribeMessenger.send(outMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	
		//TEST: ADD ACTIVITY
		Log.d(LOG_TAG, "TEST: ADD ACTIVITY");
		AActivity activity = new AActivity();
		activity.setActor("Alec"); activity.setVerb("went"); activity.setTarget("mad"); activity.setTarget("late");
		//serviceCISsubscribe.addActivity(CLIENT_PACKAGE, cis_id, activity); 
		
		targetMethod = ICisSubscribed.methodsArray[3]; // "addActivity(String client, String cisId, AActivity activity)",
		outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisSubscribed.methodsArray, targetMethod), 0, 0);
		outBundle = new Bundle();
		//PARAMETERS
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), CLIENT_PACKAGE);
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), cis_id);
		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), activity);
		outMessage.setData(outBundle);	
		try {
			cisSubscribeMessenger.send(outMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		//TEST: GET ACTIVITIES
		Log.d(LOG_TAG, "TEST: GET ACTIVITIES");
		targetMethod = ICisSubscribed.methodsArray[2]; //"getActivityFeed(String client, String cisId)"
		outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisSubscribed.methodsArray, targetMethod), 0, 0);
		outBundle = new Bundle();
		//PARAMETERS
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), CLIENT_PACKAGE);
		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), cis_id);
		outMessage.setData(outBundle);	
		try {
			cisSubscribeMessenger.send(outMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if (intent.getAction().equals(ICisManager.CREATE_CIS)) {
				boolean result = intent.getBooleanExtra(ICisManager.INTENT_RETURN_BOOLEAN,false);
				Log.d(LOG_TAG, ">>>>>CIS Creation RESULT:\n>>>>>: " + result);
				if(true == result){
					//UNMARSHALL THE COMMUNITY FROM Parcel BACK TO COMMUNITY
					Parcelable parcel =  intent.getParcelableExtra(ICisManager.INTENT_RETURN_VALUE);
					ACommunity cis =  (ACommunity) parcel;
					Log.d(LOG_TAG, ">>>>>CREATE COMMUNITY  RESULT:\nCIS ID: " + cis.getCommunityJid());
					continueTests(cis.getCommunityJid(), cis.getCommunityName());
				}
			}
			
			if (intent.getAction().equals(ICisDirectory.FIND_ALL_CIS)) {
				//UNMARSHALL THE ADVERTS FROM THE RETURNED PARCELS
				Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisDirectory.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					ACommunity cis = (ACommunity) parcels[i];
					Log.d(LOG_TAG, ">>>>>CIS DIRECTORY RESULTS:\nCIS ID: " + cis.getCommunityJid());
					continueTests(cis.getCommunityJid(), cis.getCommunityName());
				}
			}
			
			if (intent.getAction().equals(ICisManager.GET_CIS_LIST)) {
				//UNMARSHALL THE ID FROM THE RETURNED PARCEL
				Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisManager.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					ACommunity cis = (ACommunity) parcels[i];
					Log.d(LOG_TAG, ">>>>>GET CIS RESULTS:\nCIS ID: " + cis.getCommunityJid());
				}
			}
			
			if (intent.getAction().equals(ICisManager.JOIN_CIS)) {
				//UNMARSHALL THE result
				boolean result = intent.getBooleanExtra(ICisSubscribed.INTENT_RETURN_BOOLEAN,false);
				Log.d(LOG_TAG, ">>>>>CIS JOIN RESULT:\n>>>>>Allowed to join: " + result);
				if(true == result){
					//UNMARSHALL THE community FROM Parcel
					Parcelable parcel =  intent.getParcelableExtra(ICisSubscribed.INTENT_RETURN_VALUE);
					ACommunity resp = (ACommunity) parcel;
					Log.d(LOG_TAG, ">>>>>Community Joined: " + resp.getCommunityName() + "\n" + resp.getDescription());
				}
			}
			
			if (intent.getAction().equals(ICisSubscribed.GET_MEMBERS)) {
				//UNMARSHALL THE PARTICIPANTS FROM THE RETURNED PARCELS
				Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisSubscribed.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					AParticipant member = (AParticipant) parcels[i];
					Log.d(LOG_TAG, ">>>>>CIS Member Listing RESULTS:\nMEMBER ID: " + member.getJid());
					Log.d(LOG_TAG, ">>>>>MEMBER ROLE: " + member.getRole().toString());
				}
			}
			
			if (intent.getAction().equals(ICisSubscribed.ADD_ACTIVITY)) {
				//UNMARSHALL THE RESULT FROM Parcel 
				Parcelable parcel =  intent.getParcelableExtra(ICisSubscribed.INTENT_RETURN_VALUE);
				Log.d(LOG_TAG, ">>>>>ADD ACTIVIY RESULTS:\npublished: " + parcel.toString());
			}
			
			if (intent.getAction().equals(ICisSubscribed.GET_ACTIVITY_FEED)) {
				//UNMARSHALL THE ACTIVITIES FROM Parcels 
				Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisSubscribed.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					AActivity activity = (AActivity) parcels[i];
					Log.d(LOG_TAG, ">>>>>GET ACTIVIY FEED RESULTS:\npublish: " + activity.getPublished());
				}
			}
		}
	};

}
