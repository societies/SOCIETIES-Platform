package org.societies.android.platform.phongegap;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.societies.android.platform.cssmanager.LocalCSSManagerService;
import org.societies.android.platform.cssmanager.LocalCSSManagerService.LocalBinder;
import org.societies.android.platform.intents.AndroidCoreIntents;
import org.societies.android.platform.interfaces.IAndroidCSSManager;
import org.societies.android.platform.utilities.ServiceMethodTranslator;
import org.societies.api.android.internal.model.AndroidCSSNode;
import org.societies.api.android.internal.model.AndroidCSSRecord;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.utilities.DBC.Dbc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;

/**
 * PhoneGap plugin to allow the CSSManager service to be used by HTML web views.
 * 
 * Note: As a PhoneGap plugin is not a standard Android component a lot of assumed 
 * functionality such as creating intents and bind to services is not automatic. The 
 * Plugin class does however have an application context, this.ctx, which supplies the 
 * context to allow this functionality to operate.
 * 
 *
 */
public class PluginCSSManager extends Plugin {
	//Logging tag
	private static final String LOG_TAG = PluginCSSManager.class.getName();
	
	//Required to match method calls with callbackIds
	private HashMap<String, String> methodCallbacks;

    private IAndroidCSSManager localCSSManager;
    private boolean connectedtoCSSManager = false;
    private boolean initBinding = false;


    /**
     * Constructor
     */
    public PluginCSSManager() {
    	super();
    	this.methodCallbacks = new HashMap<String, String>();
    }

    /**
     * Bind to the target service
     */
    private void initialiseServiceBinding() {
    	//Create intent to select service to bind to
    	Intent intent = new Intent(this.ctx, LocalCSSManagerService.class);
    	//bind to the service
    	this.ctx.bindService(intent, ccsManagerConnection, Context.BIND_AUTO_CREATE);

    	//register broadcast receiver to receive CSSManager return values 
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(LocalCSSManagerService.LOGIN_CSS);
        intentFilter.addAction(LocalCSSManagerService.LOGOUT_CSS);
        this.ctx.registerReceiver(new bReceiver(), intentFilter);
    	
    }

	@Override
	/**
	 * This method is the receiving side of the Javascript-Java bridge
	 * This particular method variant caters for asynchronous method returns 
	 * in situations where the result will be determined in some undefined future instance
	 */
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		Log.d(LOG_TAG, "execute: " + action);

		try {
			Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
			Log.d(LOG_TAG, "parameter 1: " + data.getJSONObject(1).getString("cssIdentity"));
			Log.d(LOG_TAG, "parameter 1: " + data.getJSONObject(1).getString("cssHostingLocation"));
			Log.d(LOG_TAG, "parameter 1: " + data.getJSONObject(1).getJSONArray("cssNodes").getJSONObject(0).getString("identity"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		PluginResult result = null;
		

		//initialise the service binding. Didn't work in constructor
		if (!initBinding) {
			this.initialiseServiceBinding();
		}

		if (this.validRemoteCall(action) && this.connectedtoCSSManager) {
			
			Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
			this.methodCallbacks.put(action, callbackId);
			
			//Call local service method
			if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 4))) {
				try {
					this.localCSSManager.loginCSS(data.getString(0), createCSSRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
    	if (connectedtoCSSManager) {
    		this.ctx.unbindService(ccsManagerConnection);
    	}
    }
	
	/**
	 * Return result to Javascript call
	 * 
	 * @param callbackId
	 * @param intent
	 * @return boolean
	 */
	private boolean sendJavascriptResult(String methodCallbackId, Intent intent, String key) {
		boolean retValue = false;
		Log.d(LOG_TAG, "returnJavascriptResult called for intent: " + intent.getAction() + " and callback ID: " + methodCallbackId);	
		
		CssInterfaceResult cssResult = (CssInterfaceResult) intent.getParcelableExtra(LocalCSSManagerService.INTENT_RETURN_KEY);

		PluginResult result = new PluginResult(PluginResult.Status.OK, convertCSSRecord((AndroidCSSRecord) cssResult.getProfile()));
		result.setKeepCallback(false);
		this.success(result, methodCallbackId);
		
		//remove callback ID for given method invocation
		PluginCSSManager.this.methodCallbacks.remove(key);

		Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
		return retValue;
		
	}
	
    /**
     * Creates a JSONObject for a given AndroidCSSNode
     * 
     * @param node
     * @return JSONObject 
     */
    private JSONObject convertCSSNode(AndroidCSSNode node) {
        JSONObject jObj = new JSONObject();
		Gson gson = new Gson();
		try {
			jObj =  (JSONObject) new JSONTokener(gson.toJson(node)).nextValue();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jObj;
    }

    /**
     * Creates a JSONObject for a given AndroidCSSNode
     * 
     * @param record
     * @return JSONObject
     */
    private JSONObject convertCSSRecord(AndroidCSSRecord record) {
        JSONObject jObj = new JSONObject();
		Gson gson = new Gson();
		try {
			jObj =  (JSONObject) new JSONTokener(gson.toJson(record)).nextValue();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jObj;
    }
    /**
     * Creates a AndroidCSSNode from a JSONObject
     * 
     * @param jRecord JSONObject representation of node
     * @return AndroidCSSNode 
     */
    private AndroidCSSNode createCSSNode(JSONObject jNode) {
    	AndroidCSSNode anode = null;

    	Gson gson = new Gson();
    	
    	anode = gson.fromJson(jNode.toString(), AndroidCSSNode.class);
    	return anode;
    }
    /**
     * Creates a AndroidCSSRecord from a JSONObject
     * 
     * @param jNode JSONObject representation of record
     * @return AndroidCSSRecord 
     */
    private AndroidCSSRecord createCSSRecord(JSONObject jRecord) {
    	Dbc.require("JSON object cannot be null", jRecord != null);
    	AndroidCSSRecord aRecord = new AndroidCSSRecord();

    	
    	try {
			aRecord.setCssHostingLocation(jRecord.getString("cssHostingLocation"));
			aRecord.setCssIdentity(jRecord.getString("cssIdentity"));
	    	aRecord.setCssInactivation(jRecord.getString("cssInactivation"));
	    	aRecord.setCssRegistration(jRecord.getString("cssRegistration"));
	    	aRecord.setCssUpTime(jRecord.getInt("cssUpTime"));
	    	aRecord.setDomainServer(jRecord.getString("domainServer"));
	    	aRecord.setEmailID(jRecord.getString("emailID"));
	    	aRecord.setEntity(jRecord.getInt("entity"));
	    	aRecord.setForeName(jRecord.getString("foreName"));
	    	aRecord.setHomeLocation(jRecord.getString("homeLocation"));
	    	aRecord.setIdentityName(jRecord.getString("identityName"));
	    	aRecord.setImID(jRecord.getString("imID"));
	    	aRecord.setName(jRecord.getString("name"));
	    	aRecord.setPassword(jRecord.getString("password"));
	    	aRecord.setPresence(jRecord.getInt("presence"));
	    	aRecord.setSex(jRecord.getInt("sex"));
	    	aRecord.setSocialURI(jRecord.getString("socialURI"));
	    	aRecord.setStatus(jRecord.getInt("status"));
	    	
	    	JSONArray cssNodes = jRecord.getJSONArray("cssNodes");
	    	AndroidCSSNode aNodes [] = new AndroidCSSNode[cssNodes.length()];
	    	
	    	for (int i = 0; i < cssNodes.length(); i++) {
	    		aNodes[i] = createCSSNode(cssNodes.getJSONObject(i));
	    	}
	    	aRecord.setCSSNodes(aNodes);
//	    	aRecord.setArchiveCSSNodes(arg0)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Log.d(LOG_TAG, "Converted CSSRecord identity: " + aRecord.getCssIdentity());
    	Log.d(LOG_TAG, "Converted CSSRecord node identity: " + aRecord.getCSSNodes()[0].getIdentity());
    	return aRecord;
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class bReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(LocalCSSManagerService.LOGIN_CSS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 4);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(LocalCSSManagerService.LOGOUT_CSS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 3);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
				
			}
		}
	};
    /**
     * CSSManager service connection
     */
    private ServiceConnection ccsManagerConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from LocalCSSManager service");
        	connectedtoCSSManager = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to LocalCSSManager service");
        	//get a local binder
            LocalBinder binder = (LocalBinder) service;
            //obtain the service's API
            localCSSManager = (IAndroidCSSManager) binder.getService();
            connectedtoCSSManager = true;
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
    	for (int i = 0; i < IAndroidCSSManager.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, i))) {
        		retValue = true;
        	}
    	}
    	if (!retValue) {
    		Log.d(LOG_TAG, "Unable to find method name for given action: " + action);
    	}

    	return retValue;
    }
}
