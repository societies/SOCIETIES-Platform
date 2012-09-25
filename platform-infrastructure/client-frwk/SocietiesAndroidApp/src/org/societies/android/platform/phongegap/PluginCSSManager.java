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
import org.json.JSONObject;
import org.json.JSONTokener;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.cssmanager.AndroidCSSNode;
import org.societies.android.api.internal.cssmanager.AndroidCSSRecord;
import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.android.platform.content.CssRecordDAO;
import org.societies.android.platform.cssmanager.AndroidNotifier;
import org.societies.android.platform.cssmanager.LocalCSSManagerService;
import org.societies.android.platform.cssmanager.AndroidNotifier;
import org.societies.android.platform.cssmanager.LocalCSSManagerService.LocalBinder;
import org.societies.utilities.DBC.Dbc;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;

/**
 * PhoneGap plugin to allow the CSSManager service to be used by HTML web views.
 * 
 * Note: As a PhoneGap plugin is not a standard Android component a lot of assumed 
 * functionality such as creating intents and binding to services is not automatic. The 
 * Plugin class does however have an application context, this.ctx, which supplies the 
 * context to allow this functionality to operate.
 * 
 *
 */
public class PluginCSSManager extends Plugin {
	//Logging tag
	private static final String LOG_TAG = PluginCSSManager.class.getName();

	/**
	 * Actions required to bind and unbind to any Android service(s) 
	 * required by this plugin. It is imperative that dependent 
	 * services are binded to before invoking invoking methods.
	 */
	private static final String CONNECT_SERVICE = "connectService";
	private static final String DISCONNECT_SERVICE = "disconnectService";
	/**
	 * Ancilliary functionality
	 */
	private static final String READ_CSSRECORD = "readCSSRecord";
	
	//Required to match method calls with callbackIds (used by PhoneGap)
	private HashMap<String, String> methodCallbacks;

    private IAndroidCSSManager localCSSManager;
    private boolean connectedtoCSSManager = false;


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
    	Intent intent = new Intent(this.ctx.getContext(), LocalCSSManagerService.class);
    	//bind to the service
    	this.ctx.getContext().bindService(intent, ccsManagerConnection, Context.BIND_AUTO_CREATE);

    	//register broadcast receiver to receive CSSManager return values 
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(LocalCSSManagerService.LOGIN_CSS);
        intentFilter.addAction(LocalCSSManagerService.LOGOUT_CSS);
        intentFilter.addAction(LocalCSSManagerService.REGISTER_XMPP_SERVER);
        intentFilter.addAction(LocalCSSManagerService.LOGIN_XMPP_SERVER);
        intentFilter.addAction(LocalCSSManagerService.LOGOUT_XMPP_SERVER);
        intentFilter.addAction(LocalCSSManagerService.MODIFY_ANDROID_CSS_RECORD);
        
        this.ctx.getContext().registerReceiver(new bReceiver(), intentFilter);
    	
    }
    
    /**
     * Unbind from service
     */
    private void disconnectServiceBinding() {
    	if (connectedtoCSSManager) {
    		Log.d(LOG_TAG,"Still connected - disconnecting service");
    		this.ctx.getContext().unbindService(ccsManagerConnection);
    	}
    }


	@Override
	/**
	 * This method is the receiving side of the Javascript-Java bridge
	 * This particular method variant caters for asynchronous method returns 
	 * in situations where the result will be determined in some undefined future instance
	 */
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		Log.d(LOG_TAG, "execute: " + action + " for callback: " + callbackId);


		PluginResult result = null;

		if (action.equals(CONNECT_SERVICE)) {

			if (!connectedtoCSSManager) {
				Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
				this.methodCallbacks.put(action, callbackId);

				result = new PluginResult(PluginResult.Status.NO_RESULT);
	            result.setKeepCallback(true);

				this.initialiseServiceBinding();
			} else {
	            result = new PluginResult(PluginResult.Status.OK, "connected");
	            result.setKeepCallback(false);
			}
            
            return result;
		} 

		//uses synchronous call to ensure that service is unbound
		if (action.equals(DISCONNECT_SERVICE)) {

			this.disconnectServiceBinding();

            result = new PluginResult(PluginResult.Status.OK, "disconnected");
            result.setKeepCallback(false);

            return result;
		} 

		//uses synchronous call to current local cached CSSRecord
		if (action.equals(READ_CSSRECORD)) {
			CssRecordDAO cssRecordDAO = new CssRecordDAO(this.ctx.getContext());
			
			AndroidCSSRecord record = cssRecordDAO.readCSSrecord();
			if (null != record) {
	            result = new PluginResult(PluginResult.Status.OK, convertCSSRecord(cssRecordDAO.readCSSrecord()));
			} else {
	            result = new PluginResult(PluginResult.Status.ERROR, "no CSS Record");
			}
            result.setKeepCallback(false);

            return result;
			
		}
		
		//uses asynchronous calls
		if (this.validRemoteCall(action) && this.connectedtoCSSManager) {

			
			Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
			this.methodCallbacks.put(action, callbackId);
			
			//Call local service method
			if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 4))) {
				
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 1 - identity: " + data.getJSONObject(1).getString("cssIdentity"));
					Log.d(LOG_TAG, "parameter 1 - hosting location: " + data.getJSONObject(1).getString("cssHostingLocation"));
					Log.d(LOG_TAG, "parameter 1 - domain server: " + data.getJSONObject(1).getString("domainServer"));
					Log.d(LOG_TAG, "parameter 1 - password: " + data.getJSONObject(1).getString("password"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					Log.d(LOG_TAG, "parameter 1 - nodes: " + data.getJSONObject(1).getJSONArray("cssNodes").getJSONObject(0).getString("identity"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					this.localCSSManager.loginCSS(data.getString(0), createCSSRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 5))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 1 - identity: " + data.getJSONObject(1).getString("cssIdentity"));
					Log.d(LOG_TAG, "parameter 1 - hosting location: " + data.getJSONObject(1).getString("cssHostingLocation"));
					Log.d(LOG_TAG, "parameter 1 - domain server: " + data.getJSONObject(1).getString("domainServer"));
					Log.d(LOG_TAG, "parameter 1 - password: " + data.getJSONObject(1).getString("password"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					this.localCSSManager.logoutCSS(data.getString(0), createCSSRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 0))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					this.localCSSManager.registerXMPPServer(data.getString(0), createCSSRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 2))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 1 - identity: " + data.getJSONObject(1).getString("cssIdentity"));
					Log.d(LOG_TAG, "parameter 1 - password: " + data.getJSONObject(1).getString("password"));
					Log.d(LOG_TAG, "parameter 1 - domain server: " + data.getJSONObject(1).getString("domainServer"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					this.localCSSManager.loginXMPPServer(data.getString(0), createCSSRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 3))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					this.localCSSManager.logoutXMPPServer(data.getString(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 11))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 2 - forename: " + data.getJSONObject(1).getString("foreName"));
					Log.d(LOG_TAG, "parameter 3 - name: " + data.getJSONObject(1).getString("name"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					this.localCSSManager.modifyAndroidCSSRecord(data.getString(0), createCSSRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// Don't return any result now, since status results will be sent when events come in from broadcast receiver 
            result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
		} else {
			Log.d(LOG_TAG, "Plugin target method not supported");
			//if method does not exist send synchronous error result
            result = new PluginResult(PluginResult.Status.ERROR);
            result.setKeepCallback(false);
		}
		
		return result;	
	}

	@Override
	/**
	 * Unbind from service to prevent service being kept alive
	 */
	public void onDestroy() {
		disconnectServiceBinding();
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
		
		AndroidCSSRecord cssRecord = (AndroidCSSRecord) intent.getParcelableExtra(LocalCSSManagerService.INTENT_RETURN_VALUE_KEY);
		boolean resultStatus = intent.getBooleanExtra(LocalCSSManagerService.INTENT_RETURN_STATUS_KEY, false);
		
		Log.d(LOG_TAG, "Result status of remote call: " + resultStatus);
		
		if (resultStatus) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, convertCSSRecord(cssRecord));
			result.setKeepCallback(false);
			this.success(result, methodCallbackId);
		} else {
			PluginResult result = new PluginResult(PluginResult.Status.ERROR);
			result.setKeepCallback(false);
			this.error(result, methodCallbackId);
		}
			
		
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
     * Creates a AndroidCSSRecord from a JSONObject. Required as GSON
     * could not cope with class structure.
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
				
				//Create Android Notification
				int notifierflags [] = new int [1];
				notifierflags[0] = Notification.FLAG_AUTO_CANCEL;
				AndroidNotifier notifier = new AndroidNotifier(PluginCSSManager.this.ctx.getContext(), Notification.DEFAULT_SOUND, notifierflags);

				notifier.notifyMessage("Successful", intent.getAction(), org.societies.android.platform.gui.MainActivity.class);

			} else if (intent.getAction().equals(LocalCSSManagerService.LOGOUT_CSS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 5);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
				
			} else if (intent.getAction().equals(LocalCSSManagerService.REGISTER_XMPP_SERVER)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 0);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
				
			} else if (intent.getAction().equals(LocalCSSManagerService.LOGIN_XMPP_SERVER)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 2);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(LocalCSSManagerService.LOGOUT_XMPP_SERVER)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 3);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(LocalCSSManagerService.MODIFY_ANDROID_CSS_RECORD)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 11);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} 
		}
	};
    /**
     * CSSManager service connection
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
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
            
			String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(CONNECT_SERVICE);

			PluginResult result = new PluginResult(PluginResult.Status.OK, "connected");
			result.setKeepCallback(false);
			PluginCSSManager.this.success(result, methodCallbackId);

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
