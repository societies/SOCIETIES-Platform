package org.societies.android.platform.phongegap;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.societies.android.platform.interfaces.ICoreServiceMonitor;
import org.societies.android.platform.servicemonitor.CoreServiceMonitor;
import org.societies.android.platform.servicemonitor.CoreServiceMonitor.LocalBinder;
import org.societies.android.platform.utilities.ServiceMethodTranslator;
import org.societies.api.android.internal.model.AndroidActiveServices;
import org.societies.api.android.internal.model.AndroidActiveTasks;

import android.app.ActivityManager;
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
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;

public class PluginCoreServiceMonitor extends Plugin {
	//Logging tag
	private static final String LOG_TAG = PluginCoreServiceMonitor.class.getName();
	
	//Required to match method calls with callbackIds
	private HashMap<String, String> methodCallbacks = new HashMap<String, String>();

    private ICoreServiceMonitor coreServiceMonitor;
    private boolean connectedtoCoreMonitor = false;
    private boolean initBinding = false;

    /**
     * Constructor
     */
    public PluginCoreServiceMonitor() {
    	super();
    	this.methodCallbacks = new HashMap<String, String>();

    }

    /**
     * Bind to the target service
     */
    private void initialiseServiceBinding() {
    	//Create intent to select service to bind to
    	Intent intent = new Intent(this.ctx, CoreServiceMonitor.class);
    	//bind to the service
    	this.ctx.bindService(intent, coreServiceMonitorConnection, Context.BIND_AUTO_CREATE);
    	//register broad
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(CoreServiceMonitor.ACTIVE_SERVICES);
        intentFilter.addAction(CoreServiceMonitor.ACTIVE_TASKS);
        this.ctx.registerReceiver(new bReceiver(), intentFilter);
        initBinding = true;
    }
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result = null;
		
		Log.d(LOG_TAG, "execute: " + action);

		//initialise the service binding. Didn't work in constructor
		if (!initBinding) {
			this.initialiseServiceBinding();
		}
		
		if (this.validRemoteCall(action) && connectedtoCoreMonitor) {
			
			Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
			this.methodCallbacks.put(action, callbackId);
			
			//Call local service method
			if (action.equals(ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, 2))) {
				this.coreServiceMonitor.activeServices("org.societies.android.platform.gui");
			} else if (action.equals(ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, 0))) {
				this.coreServiceMonitor.activeTasks("org.societies.android.platform.gui");
			}
			
            // Don't return any result now, since status results will be sent when events come in from broadcast receiver 
            result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
		} 
		return result;	
	}

	
	@Override
	/**
	 * Unbind from service to prevent service being kept alive
	 */
	public void onDestroy() {
    	if (connectedtoCoreMonitor) {
    		this.ctx.unbindService(coreServiceMonitorConnection);
    	}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
	private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if (intent.getAction().equals(CoreServiceMonitor.ACTIVE_SERVICES)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, 2);
				
				String methodCallbackId = PluginCoreServiceMonitor.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					
					//unmarshall intent extra
					Parcelable parcels [] =  intent.getParcelableArrayExtra(CoreServiceMonitor.INTENT_RETURN_KEY);
					ActivityManager.RunningServiceInfo services [] = new ActivityManager.RunningServiceInfo[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						services[i] = (ActivityManager.RunningServiceInfo) parcels[i];
					}


					PluginResult result = new PluginResult(PluginResult.Status.OK, convertAndroidActiveServices(getActiveServices(services)));
					result.setKeepCallback(false);
					PluginCoreServiceMonitor.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCoreServiceMonitor.this.methodCallbacks.remove(mapKey);

					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);

				}
			} else if (intent.getAction().equals(CoreServiceMonitor.ACTIVE_TASKS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, 0);
				
				String methodCallbackId = PluginCoreServiceMonitor.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					
					//unmarshall intent extra
					Parcelable parcels [] =  intent.getParcelableArrayExtra(CoreServiceMonitor.INTENT_RETURN_KEY);
					ActivityManager.RunningTaskInfo tasks [] = new ActivityManager.RunningTaskInfo[parcels.length];
					for (int i = 0; i < parcels.length; i++) {
						tasks[i] = (ActivityManager.RunningTaskInfo) parcels[i];
					}


					PluginResult result = new PluginResult(PluginResult.Status.OK, convertAndroidActiveTasks(getActiveTasks((tasks))));
					result.setKeepCallback(false);
					PluginCoreServiceMonitor.this.success(result, methodCallbackId);
					
					//remove callback ID for given method invocation
					PluginCoreServiceMonitor.this.methodCallbacks.remove(mapKey);

					Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);

				}
			} 

		}
	};
	
	/**
	 * Create an Active services model for the GUI
	 * @param services
	 * @return AndroidActiveServices array
	 */
	private AndroidActiveServices [] getActiveServices(ActivityManager.RunningServiceInfo services []) {
		AndroidActiveServices activeServices [] = new AndroidActiveServices[services.length];
		
		for (int i = 0; i < services.length; i++) {
			AndroidActiveServices element = new AndroidActiveServices();
			
			element.setActiveSince(services[i].activeSince);
			element.setClassName(extractServiceName(services[i].service.getClassName()));
			element.setPackageName(services[i].service.getPackageName());
			element.setProcess(services[i].process);
			activeServices[i] = element;
		}
		return activeServices;
	}
	/**
	 * Create an Active tasks model for the GUI
	 * @param tasks
	 * @return AndroidActiveTasks array
	 */
	private AndroidActiveTasks [] getActiveTasks(ActivityManager.RunningTaskInfo tasks []) {
		AndroidActiveTasks activeTasks [] = new AndroidActiveTasks[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			AndroidActiveTasks element = new AndroidActiveTasks();
			
			element.setClassName(extractServiceName(tasks[i].baseActivity.getClassName()));
			element.setPackageName(tasks[i].baseActivity.getPackageName());
			element.setNumRunningActivities(tasks[i].numRunning);
			activeTasks[i] = element;
		}

		return activeTasks;
	}
 
    /**
     * Creates a JSONArray for a given AndroidActiveServices array
     * 
     * @param node
     * @return JSONArray 
     */
    private JSONArray convertAndroidActiveServices(AndroidActiveServices services []) {
    	JSONArray jObj = new JSONArray();
		Gson gson = new Gson();
		try {
			jObj =  (JSONArray) new JSONTokener(gson.toJson(services)).nextValue();
			
			Log.d(LOG_TAG, jObj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jObj;
    }
    /**
     * Creates a JSONArray for a given AndroidActiveTasks array
     * 
     * @param node
     * @return JSONArray 
     */
    private JSONArray convertAndroidActiveTasks(AndroidActiveTasks tasks []) {
    	JSONArray jObj = new JSONArray();
		Gson gson = new Gson();
		try {
			jObj =  (JSONArray) new JSONTokener(gson.toJson(tasks)).nextValue();
			
			Log.d(LOG_TAG, jObj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jObj;
    }

	
    /**
     * CoreServiceMonitor service connection
     */
    private ServiceConnection coreServiceMonitorConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from CoreServiceMonitor service");
        	connectedtoCoreMonitor = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to CoreServiceMonitor service");
        	//get a local binder
            LocalBinder binder = (LocalBinder) service;
            //obtain the service's API
            coreServiceMonitor = (ICoreServiceMonitor) binder.getService();
            connectedtoCoreMonitor = true;
        }
    };

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
    	for (int i = 0; i < ICoreServiceMonitor.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, i))) {
        		retValue = true;
        	}
    	}
    	if (!retValue) {
    		Log.d(LOG_TAG, "Unable to find method name for given action: " + action);
    	}
    	return retValue;
    }

    private String extractServiceName(String className) {
    	Log.d(LOG_TAG, "extractService for class: " + className );
    	String serviceName = className;
    	
    	String tokens [] = className.split("\\.");
    	if (tokens.length > 0) {
        	serviceName = tokens[tokens.length - 1];
    	}
    	return serviceName;
    }
}
