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
package org.societies.android.platform.phongegap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.androidutils.AndroidNotifier;
import org.societies.android.platform.cis.CisDirectoryRemote;
import org.societies.android.platform.cis.CisManagerRemote;
import org.societies.android.platform.cis.CisSubscribedRemote;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;

import android.app.Notification;
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
import com.google.gson.Gson;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class PluginCISFunctions extends Plugin {

	//Logging tag
	private static final String LOG_TAG = PluginCISFunctions.class.getName();

	/**
	 * Actions required to bind and unbind to any Android service(s) 
	 * required by this plugin. It is imperative that dependent 
	 * services are binded to before invoking invoking methods.
	 */
	private static final String CONNECT_SERVICE = "connectService";
	private static final String DISCONNECT_SERVICE = "disconnectService";
	
	//Required to match method calls with callbackIds
	private HashMap<String, String> methodCallbacks;;

	private Messenger messengerCISManager;
    private boolean serviceCISManagerConnected = false;
    
    private Messenger messengerCISsubscribe;
    private boolean serviceCISsubscribeConnected = false;

    private Messenger messengerCISdir;
    private boolean serviceCISdirConnected = false;
    /**
     * Constructor
     */
    public PluginCISFunctions() {
    	super();
    	this.methodCallbacks = new HashMap<String, String>();
    }

    /**
     * CoreServiceMonitor service connection
     */
    private ServiceConnection cisManagerConnection = new ServiceConnection() {

    	public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to CISManager service");
        	//GET REMOTE BINDER
        	messengerCISManager = new Messenger(service);
            serviceCISManagerConnected = true;
            Log.d(LOG_TAG, "Successfully connected to CisManager service");
        }
    	
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from CIS Manager service");
        	serviceCISManagerConnected = false;
        }
    };
    
    /**
     * IServiceDiscovery service connection
     */
    private ServiceConnection cisSubscribeConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ICisSubsribed service");
        	//GET REMOTE BINDER
        	messengerCISsubscribe = new Messenger(service);
            serviceCISsubscribeConnected = true;
            Log.d(LOG_TAG, "Successfully connected to CisSubscribed service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ICisSubsribed service");
        	serviceCISsubscribeConnected = false;
        }
    };

    /**
     * ICisDirectory service connection
     */
    private ServiceConnection cisDirConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ICisDirectory service");
        	//GET REMOTE BINDER
        	messengerCISdir = new Messenger(service);
        	serviceCISdirConnected = true;
            Log.d(LOG_TAG, "Successfully connected to CisDirectory service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ICisDirectory service");
        	serviceCISdirConnected = false;
        }
    };
    
    /**
     * Bind to the target service
     */
    private void initialiseServiceBinding() {
    	//CREATE INTENT FOR EACH SERVICE
    	Intent intentCisManager = new Intent(this.ctx.getContext(), CisManagerRemote.class);
    	Intent intentCisSubscribe = new Intent(this.ctx.getContext(), CisSubscribedRemote.class);
    	Intent intentCisDir = new Intent(this.ctx.getContext(), CisDirectoryRemote.class);
    	
    	//BIND TO SERVICES
    	this.ctx.getContext().bindService(intentCisManager, cisManagerConnection, Context.BIND_AUTO_CREATE);
    	this.ctx.getContext().bindService(intentCisSubscribe, cisSubscribeConnection, Context.BIND_AUTO_CREATE);
    	this.ctx.getContext().bindService(intentCisDir, cisDirConnection, Context.BIND_AUTO_CREATE);
    	
    	//REGISTER BROADCAST
    	//CIS MANAGER
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(ICisManager.CREATE_CIS);
        intentFilter.addAction(ICisManager.DELETE_CIS);
        intentFilter.addAction(ICisManager.GET_CIS_LIST);
        intentFilter.addAction(ICisManager.REMOVE_MEMBER);
        intentFilter.addAction(ICisManager.JOIN_CIS);
        intentFilter.addAction(ICisManager.LEAVE_CIS);
        //CIS SUBSCRIBED
        intentFilter.addAction(ICisSubscribed.GET_MEMBERS);
        intentFilter.addAction(ICisSubscribed.GET_CIS_INFO);
        intentFilter.addAction(ICisSubscribed.GET_ACTIVITY_FEED);
        intentFilter.addAction(ICisSubscribed.ADD_ACTIVITY);
        intentFilter.addAction(ICisSubscribed.DELETE_ACTIVITY);
        intentFilter.addAction(ICisSubscribed.CLEAN_ACTIVITIES);
        //CIS DIRECTORY
        intentFilter.addAction(ICisDirectory.FIND_ALL_CIS);
        intentFilter.addAction(ICisDirectory.FILTER_CIS);
        intentFilter.addAction(ICisDirectory.FIND_CIS_ID);
        this.ctx.getContext().registerReceiver(new bReceiver(), intentFilter);
    }
    
    /**
     * Unbind from service
     */
    private void disconnectServiceBinding() {
    	if (serviceCISManagerConnected) {
    		this.ctx.getContext().unbindService(cisManagerConnection);
    	}
    	if (serviceCISsubscribeConnected) {
    		this.ctx.getContext().unbindService(cisSubscribeConnection);
    	}
    }
    

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		Log.d(LOG_TAG, "Phonegap Plugin executing: " + action);
		PluginResult result = null;
		
		if (action.equals(CONNECT_SERVICE)) {
			if (!serviceCISManagerConnected) {
				this.initialiseServiceBinding();
			}
            result = new PluginResult(PluginResult.Status.OK, "connected");
            result.setKeepCallback(false);
            return result;
		} 

		if (action.equals(DISCONNECT_SERVICE)) {
			this.disconnectServiceBinding();
            result = new PluginResult(PluginResult.Status.OK, "disconnected");
            result.setKeepCallback(false);
            return result;
		} 
		
		if (this.validRemoteCall(action) && serviceCISManagerConnected) {
			try {
				Log.d(LOG_TAG, "parameters: " + data.getString(0));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
			this.methodCallbacks.put(action, callbackId);
			
			//>>>>>>>>>  ICisManager METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 0))) {
				try { //CREATE CIS
					JSONArray jArray = data.getJSONArray(4); //ListCriteria
					List<Criteria> criteriaList = CreateCriteriaList(jArray);
					MembershipCrit membCrit = new MembershipCrit();
					membCrit.setCriteria(criteriaList);
					
					//createCis(data.getString(0), data.getString(1), data.getString(2), data.getString(3), membCrit, data.getString(5)); position 0
	        		Message outMessage = Message.obtain(null, 0, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisManager.methodsArray, 0);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 2), data.getString(2));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 3), data.getString(3));
	        		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(methodSignature, 4), membCrit);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 5), data.getString(5));
	        		
	        		outMessage.setData(outBundle);
	    			messengerCISManager.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 1))) {
				try { // DELETE CIS
					//deleteCis(data.getString(0), data.getString(1)); position 1
	        		Message outMessage = Message.obtain(null, 1, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisManager.methodsArray, 1);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	    			messengerCISManager.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 2))) {
				try {// GET CIS LISTING - position 2
					//getCisList(data.getString(0), data.getString(1));
					Message outMessage = Message.obtain(null, 2, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisManager.methodsArray, 2);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	    			messengerCISManager.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 3))) {
				try { //REMOVE PARTICIPANT FROM CIS
					//removeMember(data.getString(0), data.getString(1), data.getString(2)); position 3
					Message outMessage = Message.obtain(null, 3, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisManager.methodsArray, 3);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 2), data.getString(2));

	        		outMessage.setData(outBundle);
	    			messengerCISManager.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 4))) {
				try { //JOIN A CIS
					//Join(data.getString(0), createCisAdvertFromJSON(data.getJSONObject(1))); position 4
					Message outMessage = Message.obtain(null, 4, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisManager.methodsArray, 4);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), createCisAdvertFromJSON(data.getJSONObject(1)));

	        		outMessage.setData(outBundle);
	    			messengerCISManager.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 5))) {
				try { //LEAVE A CIS
					//Leave(data.getString(0), data.getString(1)); position 5
					Message outMessage = Message.obtain(null, 5, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisManager.methodsArray, 5);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	    			messengerCISManager.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			//>>>>>>>>>  ICisSubscribed METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 0))) {
				try { //GET MEMBERS LIST
					//getMembers(data.getString(0), data.getString(1)); position 0
					Message outMessage = Message.obtain(null, 0, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisSubscribed.methodsArray, 0);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	    			messengerCISsubscribe.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 2))) {
				try { //GET ACTIVITY FEED 
					//getActivityFeed(data.getString(0), data.getString(1)); position 2
					Message outMessage = Message.obtain(null, 2, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisSubscribed.methodsArray, 2);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	    			messengerCISsubscribe.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 3))) {
				try { //ADD AN ACTIVITY TO THE FEED
					JSONObject jObj = data.getJSONObject(2);
					MarshaledActivity activity = CreateActivityFromJSON(jObj); 
					//addActivity(data.getString(0), data.getString(1), activity); position 3
					Message outMessage = Message.obtain(null, 3, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisSubscribed.methodsArray, 3);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));
	        		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(methodSignature, 2), activity);

	        		outMessage.setData(outBundle);
	    			messengerCISsubscribe.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 4))) {
				try { //DELETE AN ACTIVITY FROM THE FEED
					JSONObject jObj = data.getJSONObject(2);
					MarshaledActivity activity = CreateActivityFromJSON(jObj); 
					//deleteActivity(data.getString(0), data.getString(1), activity); postition 4
					Message outMessage = Message.obtain(null, 4, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisSubscribed.methodsArray, 4);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));
	        		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(methodSignature, 2), activity);

	        		outMessage.setData(outBundle);
	    			messengerCISsubscribe.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 5))) {
				try { //CLEAN UP THE ACTIVITY FEED
					//cleanActivityFeed(data.getString(0), data.getString(1)); position 5
					Message outMessage = Message.obtain(null, 5, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisSubscribed.methodsArray, 5);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	    			messengerCISsubscribe.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			//>>>>>>>>>  ICisDirectory METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (action.equals(ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 0))) {
				try { //findAllCisAdvertisementRecords
					//findAllCisAdvertisementRecords(data.getString(0)); position 0
					Message outMessage = Message.obtain(null, 0, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisDirectory.methodsArray, 0);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));

	        		outMessage.setData(outBundle);
	    			messengerCISdir.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 1))) {
				try { //findForAllCis
					//findForAllCis(data.getString(0), data.getString(1)); position 1
					Message outMessage = Message.obtain(null, 1, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisDirectory.methodsArray, 1);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	        		messengerCISdir.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 2))) {
				try { //searchByID
					//searchByID(data.getString(0), data.getString(1)); position 2
					Message outMessage = Message.obtain(null, 2, 0, 0);
	        		Bundle outBundle = new Bundle();
	        		String methodSignature = ServiceMethodTranslator.getMethodSignature(ICisDirectory.methodsArray, 2);
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 0), data.getString(0));
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(methodSignature, 1), data.getString(1));

	        		outMessage.setData(outBundle);
	        		messengerCISdir.send(outMessage);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
            // Don't return any result now, since status results will be sent when events come in from broadcast receiver 
            result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
		} else {
            result = new PluginResult(PluginResult.Status.ERROR);
            result.setKeepCallback(false);
		}
		return result;	
	}
	
	/** Convert a JSON Array of ACriteria objects to a List<ACriteria>
	 * @param jArray
	 * @return
	 * @throws JSONException 
	 */
	private List<Criteria> CreateCriteriaList(JSONArray jArray) {
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		for(int i=0; i< jArray.length(); i++) {
			try {
				Criteria crit = createCriteriaFromJSON(jArray.getJSONObject(i));
				criteriaList.add(crit);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return criteriaList;
	}
	
	/**
	 * Unbind from service to prevent service being kept alive
	 */
	@Override
	public void onDestroy() {
		disconnectServiceBinding();
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
	private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			//>>>>>>>>>  ICisManager METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			if (intent.getAction().equals(ICisManager.CREATE_CIS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 0);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//unmarshall intent
					Community cis =  intent.getParcelableExtra(ICisManager.INTENT_RETURN_VALUE);
					//RETURN A JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, createCommunityJSON(cis));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisManager.DELETE_CIS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 1);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//unmarshall intent
					Boolean deleted = intent.getBooleanExtra(ICisManager.INTENT_RETURN_VALUE, true);
					//RETURN A JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, deleted);
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisManager.GET_CIS_LIST)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 2);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//UNMARSHALL THE SERVICES FROM Parcels BACK TO Services
					Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisManager.INTENT_RETURN_VALUE);
					Community communities[] = new Community [parcels.length];
					System.arraycopy(parcels, 0, communities, 0, parcels.length);

					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertCommunitiesToJSONArray(communities));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisManager.REMOVE_MEMBER)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 3);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE RESPONSE FROM intent
					boolean bRemoved = intent.getBooleanExtra(ICisManager.INTENT_RETURN_VALUE, true);
					PluginResult result = new PluginResult(PluginResult.Status.OK, bRemoved);
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			}  else if (intent.getAction().equals(ICisManager.JOIN_CIS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 4);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE RESPONSE FROM Parcel
					boolean bJoined = intent.getBooleanExtra(ICisManager.INTENT_RETURN_BOOLEAN, false);
					if (bJoined) {
						JoinResponse response = intent.getParcelableExtra(ICisManager.INTENT_RETURN_VALUE);;
						//RETURN A JSON OBJECT
						PluginResult result = new PluginResult(PluginResult.Status.OK, createJoinResponseJSON(response));
						result.setKeepCallback(false);
						PluginCISFunctions.this.success(result, methodCallbackId);
						//CREATE ANDROID NOTIFICATION
						int notifierflags [] = new int [1];
						notifierflags[0] = Notification.FLAG_AUTO_CANCEL;
						AndroidNotifier notifier = new AndroidNotifier(PluginCISFunctions.this.ctx.getContext(), Notification.DEFAULT_SOUND, notifierflags);
						notifier.notifyMessage("Joined new community", intent.getAction(), org.societies.android.platform.gui.MainActivity.class);
					} else {
						//JOIN FAILED
						PluginResult result = new PluginResult(PluginResult.Status.ERROR);
						result.setKeepCallback(false);
						PluginCISFunctions.this.error(result, methodCallbackId);
					}
					//CLEAN UP: remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			}  
			//>>>>>>>>>  ICisSubscribed METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (intent.getAction().equals(ICisSubscribed.GET_MEMBERS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 0);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisSubscribed.INTENT_RETURN_VALUE);
					Participant members[] = new Participant [parcels.length];
					System.arraycopy(parcels, 0, members, 0, parcels.length);
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertPartipantToJSONArray(members));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisSubscribed.GET_CIS_INFO)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 1);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITY FROM Parcel
					Community cis =  intent.getParcelableExtra(ICisSubscribed.INTENT_RETURN_VALUE);
					//RETURN JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, createCommunityJSON(cis));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisSubscribed.GET_ACTIVITY_FEED)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 2);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE ACTIVITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisSubscribed.INTENT_RETURN_VALUE);
					MarshaledActivity activities[] = new MarshaledActivity [parcels.length];
					System.arraycopy(parcels, 0, activities, 0, parcels.length);
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertActiviesToJSONArray(activities));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisSubscribed.ADD_ACTIVITY)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 3);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//unmarshall intent
					boolean bAdded = intent.getBooleanExtra(ICisSubscribed.INTENT_RETURN_VALUE, true);
					PluginResult result = new PluginResult(PluginResult.Status.OK, bAdded);
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisSubscribed.DELETE_ACTIVITY)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 4);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//unmarshall intent
					boolean bDeleted = intent.getBooleanExtra(ICisSubscribed.INTENT_RETURN_VALUE, true);
					PluginResult result = new PluginResult(PluginResult.Status.OK, bDeleted);
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			}
			//>>>>>>>>>  ICisDirectory METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (intent.getAction().equals(ICisDirectory.FIND_ALL_CIS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 0);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisDirectory.INTENT_RETURN_VALUE);
					CisAdvertisementRecord adverts[] = new CisAdvertisementRecord [parcels.length];
					System.arraycopy(parcels, 0, adverts, 0, parcels.length);
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertCisAdvertisementRecordToJSONArray(adverts));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisDirectory.FILTER_CIS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 1);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisDirectory.INTENT_RETURN_VALUE);
					CisAdvertisementRecord adverts[] = new CisAdvertisementRecord [parcels.length];
					System.arraycopy(parcels, 0, adverts, 0, parcels.length);
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertCisAdvertisementRecordToJSONArray(adverts));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(ICisDirectory.FIND_CIS_ID)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 2);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(ICisDirectory.INTENT_RETURN_VALUE);
					if (parcels.length > 0) {
						Community foundCIS = (Community) parcels[0];
						//RETURN JSON OBJECT - ASSUMED RESULT WAS IN POSTITION 0
						PluginResult result = new PluginResult(PluginResult.Status.OK, createCommunityJSON(foundCIS));
						result.setKeepCallback(false);
						PluginCISFunctions.this.success(result, methodCallbackId);
						Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
					} else {
						PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Community not found for provided cis");
						PluginCISFunctions.this.error(result, methodCallbackId);
						Log.d(LOG_TAG, "Plugin failure method called, target: " + methodCallbackId);
					}
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
				}
			}
		}
	};
 
	/**
	 * @param members
	 * @return
	 */
	public JSONArray convertPartipantToJSONArray(Participant[] members) {
		JSONArray jObj = new JSONArray();
		Gson gson = new Gson();
		try {
			jObj =  (JSONArray) new JSONTokener(gson.toJson(members)).nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return jObj;
	}
	
	/**
	 * @param communities
	 * @return
	 */
	private JSONArray convertCisAdvertisementRecordToJSONArray(CisAdvertisementRecord[] adverts) {
		JSONArray jObj = new JSONArray();
		Gson gson = new Gson();
		try {
			jObj =  (JSONArray) new JSONTokener(gson.toJson(adverts)).nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return jObj;
	}
	
	
	/**
	 * @param communities
	 * @return
	 */
	private JSONArray convertCommunitiesToJSONArray(Community[] communities) {
		JSONArray jObj = new JSONArray();
		Gson gson = new Gson();
		try {
			jObj =  (JSONArray) new JSONTokener(gson.toJson(communities)).nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return jObj;
	}
	
	/**
	 * @param communities
	 * @return
	 */
	private JSONArray convertActiviesToJSONArray(MarshaledActivity[] array) {
		JSONArray jObj = new JSONArray();
		Gson gson = new Gson();
		try {
			jObj =  (JSONArray) new JSONTokener(gson.toJson(array)).nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return jObj;
	}
	
	/**
     * Creates a JSONObject for a given JoinResponse object
     * 
     * @param resp AJoinResponse object to convert
     * @return JSONObject 
     */
    private JSONObject createJoinResponseJSON(JoinResponse resp) {
    	JSONObject jObj = new JSONObject();
		Gson gson = new Gson();
		try {
			jObj =  (JSONObject) new JSONTokener(gson.toJson(resp)).nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return jObj;
    }
    
	/**
     * Creates a JSONObject for a given Community object
     * 
     * @param cis ACommunity object to convert
     * @return JSONObject 
     */
    private JSONObject createCommunityJSON(Community cis) {
    	JSONObject jObj = new JSONObject();
		Gson gson = new Gson();
		try {
			jObj =  (JSONObject) new JSONTokener(gson.toJson(cis)).nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jObj;
    }

    /**
	 * Creates an CisAdvertisementRecord from a JSON object
	 * @param jObj
	 * @return
	 * @throws JSONException
	 */
	private CisAdvertisementRecord createCisAdvertFromJSON(JSONObject jObj) throws JSONException {
		CisAdvertisementRecord advert = new CisAdvertisementRecord();
		advert.setId(jObj.getString("id"));
		advert.setCssownerid(jObj.getString("cssownerid"));
		advert.setName(jObj.getString("name"));
		advert.setType(jObj.getString("type"));
		//advert.setPassword(jObj.getString("password"));
		//crit.setACriteria(CreateCriteriaList(jObj.getJSONArray("criteria")));
		//advert.setMembershipCrit(crit);
		
		return advert;
	}
	
	/**
	 * Creates an MarshaledActivity from a JSON object
	 * @param jObj
	 * @return
	 * @throws JSONException
	 */
	private MarshaledActivity CreateActivityFromJSON(JSONObject jObj) throws JSONException {
		MarshaledActivity act = new MarshaledActivity();
		act.setActor(jObj.getString("actor"));
		act.setObject(jObj.getString("object"));
		act.setPublished(jObj.getString("published"));
		act.setTarget(jObj.getString("target"));
		act.setVerb(jObj.getString("verb"));
		
		return act;
	}

	/**Creates an Criteria from a JSON object
	 * @param jCriteria
	 * @return
	 * @throws JSONException
	 */
	private Criteria createCriteriaFromJSON(JSONObject jCriteria) throws JSONException {
		Criteria aCrit = new Criteria();
		aCrit.setAttrib(jCriteria.getString("attrib"));
		aCrit.setOperator(jCriteria.getString("operator"));
		aCrit.setRank(jCriteria.getInt("rank"));
		aCrit.setValue1(jCriteria.getString("value1"));
		aCrit.setValue2(jCriteria.getString("value2"));
		
		return aCrit;
	}
	
    /**
     * Determine if the Javascript action is a valid.
     * 
     * N.B. Assumes that the Javascript method name is the exact same as the 
     * Java implementation. 
     * 
     * @param action
     * @return boolean
     */
    private boolean validRemoteCall(String action) {
    	boolean retValue = false;
    	//CHECK ICisManager METHODS
    	for (int i = 0; i < ICisManager.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, i))) {
        		return true;
        	}
    	}
    	//CHECK ICisSubscribed METHODS
    	for (int i = 0; i < ICisSubscribed.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, i))) {
        		return true;
        	}
    	}
    	//CHECK ICisDirectory METHODS
    	for (int i = 0; i < ICisDirectory.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, i))) {
        		return true;
        	}
    	}
    	
    	if (!retValue) {
    		Log.d(LOG_TAG, "Unable to find method name for given action: " + action);
    	}
    	return retValue;
    }
}
