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
package org.societies.android.platform.phongegap;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.societies.android.api.internal.sns.AConnectorBean;
import org.societies.android.api.internal.sns.ISocialData;
import org.societies.android.api.internal.sns.ISocialTokenManager;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.socialdata.SocialData;
import org.societies.android.platform.socialdata.SocialTokenManager;
import org.societies.api.internal.sns.ISocialConnector;

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
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
/**
 * PhoneGap plugin to allow the CoreMonitor service to be used by HTML web views.
 * 
 * Note: As a PhoneGap plugin is not a standard Android component a lot of assumed 
 * functionality such as creating intents and bind to services is not automatic. The 
 * Plugin class does however have an application context, this.ctx, which supplies the 
 * context to allow this functionality to operate.
 * 
 *
 */

public class PluginSNConnector extends Plugin {
	//Logging tag
	private static final String LOG_TAG = PluginSNConnector.class.getName();

	/**
	 * Actions required to bind and unbind to any Android service(s) 
	 * required by this plugin. It is imperative that dependent 
	 * services are binded to before invoking invoking methods.
	 */
	private static final String CONNECT_SERVICE = "connectService";
	private static final String DISCONNECT_SERVICE = "disconnectService";
	
	//Required to match method calls with callbackIds
	private HashMap<String, String> methodCallbacks;;

    private ISocialData snService;
    private boolean snServiceConnected = false;
    
    private ISocialTokenManager snActivityManager;
    private boolean snActivityManagerConnected = false;

    /**
     * Constructor
     */
    public PluginSNConnector() {
    	super();
    	this.methodCallbacks = new HashMap<String, String>();
    }

    /**
     * ISocialData service connection
     */
    private ServiceConnection snServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ISocialData service");
        	snServiceConnected = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ISocialData service");
        	//get a local binder
        	SocialData.LocalBinder binder = (SocialData.LocalBinder) service;
            //obtain the service's API
        	snService = (ISocialData) binder.getService();
            snServiceConnected = true;
        }
    };
    
    /**
     * ISocialTokenManager service connection
     */
    private ServiceConnection cnActivityManagerConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ISocialTokenManager service");

        	//GET LOCAL BINDER
        	SocialTokenManager.LocalBinder binder = (SocialTokenManager.LocalBinder) service;

            //OBTAIN SERVICE DISCOVERY API
        	snActivityManager = (ISocialTokenManager) binder.getService();
        	snActivityManagerConnected = true;
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ISocialTokenManager service");
        	snActivityManagerConnected = false;
        }
    };
    
    /**
     * Bind to the target service
     */
    private void initialiseServiceBinding() {
    	//CREATE INTENT FOR EACH SERVICE
    	Intent intentSnManager = new Intent(this.ctx.getContext(), SocialData.class);
    	Intent intentSnActivity = new Intent(this.ctx.getContext(), SocialTokenManager.class);
    	
    	//BIND TO SERVICES
    	this.ctx.getContext().bindService(intentSnManager, snServiceConnection, Context.BIND_AUTO_CREATE);
    	this.ctx.getContext().bindService(intentSnActivity, cnActivityManagerConnection, Context.BIND_AUTO_CREATE);
    	
    	//REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(ISocialData.GET_SOCIAL_CONNECTORS);
        intentFilter.addAction(ISocialData.REMOVE_SOCIAL_CONNECTOR);
        intentFilter.addAction(ISocialTokenManager.GET_TOKEN);
        
        this.ctx.getContext().registerReceiver(new bReceiver(), intentFilter);
    }
    
    /**
     * Unbind from service
     */
    private void disconnectServiceBinding() {
    	if (snServiceConnected) {
    		this.ctx.getContext().unbindService(snServiceConnection);
    	}
    	if (snActivityManagerConnected) {
    		this.ctx.getContext().unbindService(cnActivityManagerConnection);
    	}
    }
    

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		Log.d(LOG_TAG, "Phonegap Plugin executing: " + action);
		PluginResult result = null;
		
		if (action.equals(CONNECT_SERVICE)) {
			if (!snServiceConnected) {
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
		
		if (this.validRemoteCall(action) && snServiceConnected) {
			try {
				Log.d(LOG_TAG, "parameters: " + data.getString(0));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
			this.methodCallbacks.put(action, callbackId);
			
			//>>>>>>>>>  ISocialTokenManager METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			if (action.equals(ServiceMethodTranslator.getMethodName(ISocialTokenManager.methodsArray, 0))) {
				try {
					this.snActivityManager.getToken(data.getString(0), ISocialConnector.SocialNetwork.fromValue(data.getString(1)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			//>>>>>>>>>  ISocialData METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (action.equals(ServiceMethodTranslator.getMethodName(ISocialData.methodsArray, 1))) {
				try {
					this.snService.removeSocialConnector(data.getString(0), data.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ISocialData.methodsArray, 2))) {
				try {
					this.snService.getSocialConnectors(data.getString(0));
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
			
			//>>>>>>>>>  ISocialTokenManager METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			if (intent.getAction().equals(ISocialTokenManager.GET_TOKEN)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ISocialTokenManager.methodsArray, 0);
				
				String methodCallbackId = PluginSNConnector.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					//unmarshall intent extra
					Parcelable parcel =  intent.getParcelableExtra(ISocialTokenManager.EXTRA_EXPIRES);
					PluginResult result = new PluginResult(PluginResult.Status.OK, parcel.toString());
					result.setKeepCallback(false);
					PluginSNConnector.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginSNConnector.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			} 
			//>>>>>>>>>  ISocialData METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (intent.getAction().equals(ISocialData.GET_SOCIAL_CONNECTORS)) { 
				String mapKey = ServiceMethodTranslator.getMethodName(ISocialData.methodsArray, 0);
				
				String methodCallbackId = PluginSNConnector.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					
					//UNMARSHALL THE SERVICES FROM Parcels BACK TO Services
					Parcelable parcels[] =  intent.getParcelableArrayExtra(ISocialData.INTENT_RETURN_KEY);
					AConnectorBean connectors[] = new AConnectorBean[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						connectors[i] = (AConnectorBean) parcels[i];
					}
					PluginResult result = new PluginResult(PluginResult.Status.OK, convertAConnectorBeanToJSONArray(connectors));
					result.setKeepCallback(false);
					PluginSNConnector.this.success(result, methodCallbackId);
					//remove callback ID for given method invocation
					PluginSNConnector.this.methodCallbacks.remove(mapKey);
					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
				}
			}
		}
	};
	
	/**
     * Creates a JSONArray for a given InstalledAppInfo array
     * 
     * @param array of InstalledAppInfo
     * @return JSONArray 
     */
    private JSONArray convertAConnectorBeanToJSONArray(AConnectorBean array[]) {
    	JSONArray jObj = new JSONArray();
		Gson gson = new Gson();
		try {
			jObj =  (JSONArray) new JSONTokener(gson.toJson(array)).nextValue();
			Log.d(LOG_TAG, jObj.toString());
		} catch (JSONException e) {
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
    	for (int i = 0; i < ISocialData.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ISocialData.methodsArray, i))) {
        		return true;
        	}
    	}
    	//CHECK ICoreServiceMonitor METHODS
    	for (int i = 0; i < ISocialTokenManager.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ISocialTokenManager.methodsArray, i))) {
        		return true;
        	}
    	}
    	
    	if (!retValue) {
    		Log.d(LOG_TAG, "Unable to find method name for given action: " + action);
    	}
    	return retValue;
    }
}
