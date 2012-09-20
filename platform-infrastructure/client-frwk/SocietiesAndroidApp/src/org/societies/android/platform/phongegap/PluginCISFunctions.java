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
import org.societies.android.api.cis.management.AActivity;
import org.societies.android.api.cis.management.ACommunity;
import org.societies.android.api.cis.management.ACriteria;
import org.societies.android.api.cis.management.AJoinResponse;
import org.societies.android.api.cis.management.AParticipant;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.api.cis.directory.ACisAdvertisementRecord;
import org.societies.android.api.servicelifecycle.AService;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.cis.CisDirectoryRemote;
import org.societies.android.platform.cis.CommunityManagement;
import org.societies.android.platform.cis.CommunityManagement.LocalBinder;
import org.societies.android.platform.servicemonitor.ServiceManagement;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
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

	private ICisManager serviceCISManager;
    private boolean serviceCISManagerConnected = false;
    
    private ICisSubscribed serviceCISsubscribe;
    private boolean serviceCISsubscribeConnected = false;

    private ICisDirectory serviceCISdir;
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
        	//get a local binder
        	LocalBinder binder = (LocalBinder) service;
            //OBTAIN ICisManager SERVICE API
        	serviceCISManager = (ICisManager) binder.getService();
            serviceCISManagerConnected = true;
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
        	//GET LOCAL BINDER
            LocalBinder binder = (LocalBinder) service;

            //OBTAIN ICisSubscribed API
            serviceCISsubscribe = (ICisSubscribed) binder.getService();
            serviceCISsubscribeConnected = true;
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
        	//get a local binder
        	org.societies.android.platform.cis.CisDirectoryRemote.LocalBinder binder = (org.societies.android.platform.cis.CisDirectoryRemote.LocalBinder) service;
            //obtain the service's API
        	serviceCISdir = (ICisDirectory) binder.getService();
        	serviceCISdirConnected = true;
            Log.d(LOG_TAG, "Successfully connected to ICisDirectory service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ICisDirectory service");
        	serviceCISsubscribeConnected = false;
        }
    };
    
    /**
     * Bind to the target service
     */
    private void initialiseServiceBinding() {
    	//CREATE INTENT FOR EACH SERVICE
    	Intent intentCisManager = new Intent(this.ctx.getContext(), CommunityManagement.class);
    	Intent intentCisSubscribe = new Intent(this.ctx.getContext(), CommunityManagement.class);
    	
    	//BIND TO SERVICES
    	this.ctx.getContext().bindService(intentCisManager, cisManagerConnection, Context.BIND_AUTO_CREATE);
    	this.ctx.getContext().bindService(intentCisSubscribe, cisSubscribeConnection, Context.BIND_AUTO_CREATE);
    	
    	//REGISTER BROADCAST
    	//CIS MANAGER
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(CommunityManagement.CREATE_CIS);
        intentFilter.addAction(CommunityManagement.DELETE_CIS);
        intentFilter.addAction(CommunityManagement.GET_CIS_LIST);
//        intentFilter.addAction(CommunityManagement.SUBSCRIBE_TO_CIS);
//        intentFilter.addAction(CommunityManagement.UNSUBSCRIBE_FROM_CIS);
        intentFilter.addAction(CommunityManagement.REMOVE_MEMBER);
        //CIS SUBSCRIBED
        intentFilter.addAction(CommunityManagement.JOIN_CIS);
        intentFilter.addAction(CommunityManagement.LEAVE_CIS);
        intentFilter.addAction(CommunityManagement.GET_MEMBERS);
        intentFilter.addAction(CommunityManagement.GET_CIS_INFO);
        intentFilter.addAction(CommunityManagement.GET_ACTIVITY_FEED);
        intentFilter.addAction(CommunityManagement.ADD_ACTIVITY);
        intentFilter.addAction(CommunityManagement.DELETE_ACTIVITY);
        intentFilter.addAction(CommunityManagement.CLEAN_ACTIVITIES);
        //CIS DIRECTORY
        intentFilter.addAction(CisDirectoryRemote.FIND_ALL_CIS);
        intentFilter.addAction(CisDirectoryRemote.FILTER_CIS);
        intentFilter.addAction(CisDirectoryRemote.FIND_CIS_ID);
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
					List<ACriteria> criteriaList = CreateCriteriaList(jArray);
					this.serviceCISManager.createCis(data.getString(0), data.getString(1), data.getString(2), data.getString(3), criteriaList, data.getString(5));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 1))) {
				try { // DELETE CIS
					this.serviceCISManager.deleteCis(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 2))) {
				try {// GET CIS LISTING
					this.serviceCISManager.getCisList(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} /*else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 3))) {
				try { //SUBSCRIBE TO CIS
					this.serviceCISManager.subscribeToCommunity(data.getString(0), data.getString(1), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 4))) {
				try { //UN-SUBSCRIBE FROM CIS
					this.serviceCISManager.unsubscribeFromCommunity(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}*/ else if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 3))) {
				try { //REMOVE PARTICIPANT FROM CIS
					this.serviceCISManager.removeMember(data.getString(0), data.getString(1), data.getString(2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			//>>>>>>>>>  ICisSubscribed METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 0))) {
				try { //JOIN A CIS
					// TODO check that the conversion method used for the 2nd parameter is NOT IMPLEMENTED
					this.serviceCISsubscribe.Join(data.getString(0),  CreateACISAdvRecFromJSON(data.getJSONObject(1)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 1))) {
				try { //LEAVE A CIS
					this.serviceCISsubscribe.Leave(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 3))) {
				try { //GET MEMBERS LIST
					this.serviceCISsubscribe.getMembers(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 4))) {
				try { //GET ACTIVITY FEED 
					this.serviceCISsubscribe.getActivityFeed(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 5))) {
				try { //ADD AN ACTIVITY TO THE FEED
					JSONObject jObj = data.getJSONObject(2);
					AActivity activity = CreateActivity(jObj); 
					this.serviceCISsubscribe.addActivity(data.getString(0), data.getString(1), activity);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 6))) {
				try { //DELETE AN ACTIVITY FROM THE FEED
					JSONObject jObj = data.getJSONObject(2);
					AActivity activity = CreateActivity(jObj); 
					this.serviceCISsubscribe.deleteActivity(data.getString(0), data.getString(1), activity);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 7))) {
				try { //CLEAN UP THE ACTIVITY FEED
					this.serviceCISsubscribe.cleanActivityFeed(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			//>>>>>>>>>  ICisDirectory METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (action.equals(ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 0))) {
				try { //findAllCisAdvertisementRecords
					this.serviceCISdir.findAllCisAdvertisementRecords(data.getString(0));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 1))) {
				try { //findForAllCis
					this.serviceCISdir.findForAllCis(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICisDirectory.methodsArray, 2))) {
				try { //searchByID
					this.serviceCISdir.searchByID(data.getString(0), data.getString(1));
				} catch (JSONException e) {
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

	/**
	 * Creates an AActivity from a JSON object
	 * @param jObj
	 * @return
	 * @throws JSONException
	 */
	private AActivity CreateActivity(JSONObject jObj) throws JSONException {
		AActivity act = new AActivity();
		act.setActor(jObj.getString("actor"));
		act.setObject(jObj.getString("object"));
		act.setPublished(jObj.getString("published"));
		act.setTarget(jObj.getString("target"));
		act.setVerb(jObj.getString("verb"));
		
		return act;
	}
	
	/**
	 * Creates an ACisAdvertisementRecord from a JSON object
	 * @param jObj
	 * @return
	 * @throws JSONException
	 */
	private ACisAdvertisementRecord CreateACISAdvRecFromJSON(JSONObject jObj) throws JSONException {
		ACisAdvertisementRecord a = new ACisAdvertisementRecord();
		// TODO: add real code here!!
		
		return a;
	}
	
	/** Convert a JSON Array of ACriteria objects to a List<ACriteria>
	 * @param jArray
	 * @return
	 * @throws JSONException 
	 */
	private List<ACriteria> CreateCriteriaList(JSONArray jArray) {
		List<ACriteria> criteriaList = new ArrayList<ACriteria>();
		for(int i=0; i< jArray.length(); i++) {
			try {
				ACriteria crit = CreateCriteria(jArray.getJSONObject(i));
				criteriaList.add(crit);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return criteriaList;
	}

	/**Creates an ACriteria from a JSON object
	 * @param jCriteria
	 * @return
	 * @throws JSONException
	 */
	private ACriteria CreateCriteria(JSONObject jCriteria) throws JSONException {
		ACriteria aCrit = new ACriteria();
		aCrit.setAttrib(jCriteria.getString("attrib"));
		aCrit.setOperator(jCriteria.getString("operator"));
		aCrit.setRank(jCriteria.getInt("rank"));
		aCrit.setValue1(jCriteria.getString("value1"));
		aCrit.setValue2(jCriteria.getString("value2"));
		
		return aCrit;
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
			if (intent.getAction().equals(CommunityManagement.CREATE_CIS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 0);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//unmarshall intent
					Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
					ACommunity cis = (ACommunity) parcel;
					//RETURN A JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, createCommunityJSON(cis));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CommunityManagement.DELETE_CIS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 1);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//unmarshall intent
					Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
					Boolean deleted = Boolean.parseBoolean(parcel.toString());
					//RETURN A JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, deleted);
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CommunityManagement.GET_CIS_LIST)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 2);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//UNMARSHALL THE SERVICES FROM Parcels BACK TO Services
					Parcelable parcels[] =  intent.getParcelableArrayExtra(CommunityManagement.INTENT_RETURN_VALUE);
					ACommunity communities[] = new ACommunity[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						communities[i] = (ACommunity) parcels[i];
					}
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertACommunitiesToJSONArray(communities));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} 
			//>>>>>>>>>  ICisSubscribed METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (intent.getAction().equals(CommunityManagement.JOIN_CIS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 0);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE RESPONSE FROM Parcel
					Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
					AJoinResponse response = (AJoinResponse) parcel;
					//RETURN A JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, createJoinResponseJSON(response));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CommunityManagement.GET_MEMBERS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 2);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(CommunityManagement.INTENT_RETURN_VALUE);
					AParticipant members[] = new AParticipant[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						members[i] = (AParticipant) parcels[i];
					}
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertAPartipantToJSONArray(members));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CommunityManagement.GET_CIS_INFO)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 3);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITY FROM Parcel
					Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
					ACommunity cis = (ACommunity) parcel;
					//RETURN JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, createCommunityJSON(cis));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CommunityManagement.GET_ACTIVITY_FEED)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, 4);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE ACTIVITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(CommunityManagement.INTENT_RETURN_VALUE);
					AActivity activities[] = new AActivity[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						activities[i] = (AActivity) parcels[i];
					}
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertAActiviesToJSONArray(activities));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CommunityManagement.ADD_ACTIVITY)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, 5);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {					
					//unmarshall intent
					Parcelable parcel =  intent.getParcelableExtra(CommunityManagement.INTENT_RETURN_VALUE);
					Boolean deleted = Boolean.parseBoolean(parcel.toString());
					//RETURN A JSON OBJECT
					PluginResult result = new PluginResult(PluginResult.Status.OK, deleted);
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			}
			//>>>>>>>>>  ICisDirectory METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (intent.getAction().equals(CisDirectoryRemote.FIND_ALL_CIS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(CisDirectoryRemote.methodsArray, 0);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(CisDirectoryRemote.INTENT_RETURN_VALUE);
					ACommunity communities[] = new ACommunity[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						communities[i] = (ACommunity) parcels[i];
					}
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertACommunitiesToJSONArray(communities));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CisDirectoryRemote.FILTER_CIS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(CisDirectoryRemote.methodsArray, 1);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(CisDirectoryRemote.INTENT_RETURN_VALUE);
					ACommunity communities[] = new ACommunity[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						communities[i] = (ACommunity) parcels[i];
					}
					//RETURN JSON ARRAY
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertACommunitiesToJSONArray(communities));
					result.setKeepCallback(false);
					PluginCISFunctions.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginCISFunctions.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} else if (intent.getAction().equals(CisDirectoryRemote.FIND_CIS_ID)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(CisDirectoryRemote.methodsArray, 2);
				
				String methodCallbackId = PluginCISFunctions.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//UNMARSHALL THE COMMUNITIES FROM Parcels
					Parcelable parcels[] =  intent.getParcelableArrayExtra(CisDirectoryRemote.INTENT_RETURN_VALUE);
					if (parcels.length > 0) {
						ACommunity foundCIS = (ACommunity) parcels[0];
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
	public JSONArray convertAPartipantToJSONArray(AParticipant[] members) {
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
	private JSONArray convertACommunitiesToJSONArray(ACommunity[] communities) {
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
	private JSONArray convertAActiviesToJSONArray(AActivity[] array) {
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
     * Creates a JSONObject for a given AJoinResponse object
     * 
     * @param resp AJoinResponse object to convert
     * @return JSONObject 
     */
    private JSONObject createJoinResponseJSON(AJoinResponse resp) {
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
     * Creates a JSONObject for a given ACommunity object
     * 
     * @param cis ACommunity object to convert
     * @return JSONObject 
     */
    private JSONObject createCommunityJSON(ACommunity cis) {
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
    	//CHECK IServiceDisovery METHODS
    	for (int i = 0; i < ICisManager.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ICisManager.methodsArray, i))) {
        		return true;
        	}
    	}
    	//CHECK ICoreServiceMonitor METHODS
    	for (int i = 0; i < ICisSubscribed.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ICisSubscribed.methodsArray, i))) {
        		return true;
        	}
    	}
    	
    	if (!retValue) {
    		Log.d(LOG_TAG, "Unable to find method name for given action: " + action);
    	}
    	return retValue;
    }
}
