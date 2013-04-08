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

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.identity.util.RequestorUtils;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.internal.privacytrust.intent.PrivacyPolicyIntentHelper;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.privacytrust.policymanagement.service.PrivacyPolicyManagerLocalService;
import org.societies.android.privacytrust.policymanagement.service.PrivacyPolicyManagerLocalService.LocalBinder;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * PhoneGap plugin to use the privacy policy manager
 * To show the Javascript API: use "mvn site" on this project, and go to target/doc folder
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyPolicyManagerPlugin extends Plugin {
	private final static String TAG = PrivacyPolicyManagerPlugin.class.getSimpleName();

	/** Callback to call */
	public String methodName;
	public JSONArray arguments;
	public String callbackId;

	/** Helpers */
	protected Gson jsonHelper;
	protected String clientPackage;

	/** Local data */
	private boolean pluginIsInit = false;
	private boolean serviceIsInit = false;
	private int nbOfCall = 0;
	private IPrivacyPolicyManager privacyPolicyManagerService = null;
	private ServiceReceiver pivacyPolicyManagerReceiver = null;

	public PrivacyPolicyManagerPlugin() {
		jsonHelper = new Gson();
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.cordova.api.Plugin#execute(java.lang.String, org.json.JSONArray, java.lang.String)
	 */
	@Override
	public PluginResult execute(String methodName, JSONArray arguments, String callbackId) {
		Log.d(TAG, "Execute: "+(null != methodName ? methodName : "Unknown method"));
		Log.d(TAG, "Parameters: "+(null != arguments ? arguments.toString() : "No params"));
		nbOfCall++;
		// -- Plugin not initialized: initialize the plugin (it will be executed at the init end)
		if (!pluginIsInit && null != methodName) {
			return initPlugin(methodName, arguments, callbackId);
		}
		// -- Execute the plugin
		return executePlugin(methodName, arguments, callbackId);
	}


	/**
	 * This method is called when the plugin is called (execution) but not yet initialized
	 */
	private PluginResult initPlugin(String methodName, JSONArray arguments, String callbackId) {
		Log.d(TAG, "Plugin Called (first time: connect to service)");
		
		clientPackage = this.ctx.getPackageName();
		
		// - Inform the JS side: async mode
		PluginResult result = new PluginResult(Status.NO_RESULT);
		result.setKeepCallback(true);

		// - Create the broadcast receiver
		if (null == pivacyPolicyManagerReceiver) {
			Log.d(TAG, "Listen intents for PrivacyPolicy");
			pivacyPolicyManagerReceiver = new ServiceReceiver();
			IntentFilter intentFilter = new IntentFilter() ;
			intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			intentFilter.addAction(IPrivacyPolicyManager.INTENT_DEFAULT_ACTION);
			intentFilter.addAction(MethodType.GET_PRIVACY_POLICY.name());
			intentFilter.addAction(MethodType.UPDATE_PRIVACY_POLICY.name());
			intentFilter.addAction(MethodType.DELETE_PRIVACY_POLICY.name());
			intentFilter.addAction(MethodType.INFER_PRIVACY_POLICY.name());
			this.ctx.getApplicationContext().registerReceiver(pivacyPolicyManagerReceiver, intentFilter);
		}

		// - Connect to the Android service
		// Copy requested parameters
		this.methodName = methodName;
		this.arguments = arguments;
		this.callbackId = callbackId;
		// Connect
		Intent serviceIntent = new Intent(this.ctx.getContext(), PrivacyPolicyManagerLocalService.class);
		this.ctx.getContext().bindService(serviceIntent, androidServiceConnection, Context.BIND_AUTO_CREATE);
		return result;
	}

	/**
	 * This method is called when the plugin is called (execution) and already initialized
	 */
	private PluginResult executePlugin(String methodName, JSONArray arguments, String callbackId) {
		if (nbOfCall <= 0) {
			nbOfCall = 0;
			return null;
		}
		nbOfCall--;
		Log.d(TAG, "Plugin Called");
		
		// --- Save the callback
		this.callbackId = callbackId;

		// --- Prepare the result
		PluginResult result = null;
		try {
			// -- Manage the relevant method
			// - GET_PRIVACY_POLICY
			if (methodName.equals(MethodType.GET_PRIVACY_POLICY.value())) {
				result = executeGetPrivacyPolicy(arguments);
			}
			// -- Error: Unknown method name
			else {
				Log.d(TAG, "Invalid method name : "+methodName+" passed");
				result = new PluginResult(Status.INVALID_ACTION);
			}
		}
		catch (JsonSyntaxException e) {
			Log.e(TAG, "JsonSyntaxException", e);
			result = new PluginResult(Status.ERROR, "JsonSyntaxException");
			result.setKeepCallback(false);
			this.error(result, this.callbackId);
		} catch (JSONException e) {
			Log.e(TAG, "JSONException", e);
			result = new PluginResult(Status.ERROR, "JSONException");
			result.setKeepCallback(false);
			this.error(result, this.callbackId);
		} catch (PrivacyException e) {
			Log.e(TAG, "PrivacyException", e);
			result = new PluginResult(Status.ERROR, "PrivacyException");
			result.setKeepCallback(false);
			this.error(result, this.callbackId);
		}
		return result;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Destroyed");
		// -- Close the broadcast receiver
		if (null != pivacyPolicyManagerReceiver) {
			Log.d(TAG, "Unregister from intents");
			this.ctx.getContext().unregisterReceiver(pivacyPolicyManagerReceiver);
		}
		// -- Unlink with services
		if (pluginIsInit) {
			pluginIsInit = false;
			serviceIsInit = false;
			this.ctx.getContext().unbindService(androidServiceConnection);
		}
	}


	//--------------------------------------------------------------------------
	// LOCAL METHODS
	//--------------------------------------------------------------------------

	/**
	 * Execute a getPrivacyPolicy
	 */
	private PluginResult executeGetPrivacyPolicy(JSONArray arguments) throws JsonSyntaxException, JSONException, PrivacyException {
		// - Launch service method
		RequestorCisBean owner = jsonHelper.fromJson(arguments.getString(0), RequestorCisBean.class);
		Log.d(TAG, "Retrieving privacy policy from: "+RequestorUtils.toXmlString(owner));
		// An other way to manage this
//		JSONObject jsonRequestor = (JSONObject) arguments.get(0);
//		RequestorCisBean owner = new RequestorCisBean();
//		owner.setRequestorId(jsonRequestor.getString("requestorId"));
//		owner.setCisRequestorId(jsonRequestor.getString("cisRequestId"));
		privacyPolicyManagerService.getPrivacyPolicy(clientPackage, owner);

		// - Inform the JS side: async mode
		PluginResult result = new PluginResult(Status.NO_RESULT);
		result.setKeepCallback(true);
		this.success(result, callbackId);
		return result;
	}

	/**
	 * Broadcast receiver to receive intents from Service methods
	 */
	private class ServiceReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Intent received: "+intent.getAction());
			try {
				// -- PrivacyPolicyManagerService started
				if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
					Log.d(TAG, "Service started message received: "+intent.getAction());
					boolean ack =  intent.hasExtra(IServiceManager.INTENT_RETURN_VALUE_KEY) && intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
					boolean isPrivacyPolicyManager =  intent.hasExtra("type") && "PrivacyPolicyManager".equals(intent.getStringExtra("type"));
					// Execute the plugin
					if (ack && isPrivacyPolicyManager) {
						serviceIsInit = true;
						if (pluginIsInit && serviceIsInit) {
							executePlugin(methodName, arguments, callbackId);
						}
					}
					return;
				}
				// -- Ack
				boolean ack =  intent.hasExtra(IPrivacyPolicyManager.INTENT_RETURN_STATUS_KEY) && intent.getBooleanExtra(IPrivacyPolicyManager.INTENT_RETURN_STATUS_KEY, false);
				Log.d(TAG, "Privacy Response received: "+intent.getAction()+(ack ? " with success" : " with an error"));
				// -- Get Privacy Policy
				if (intent.getAction().equals(MethodType.GET_PRIVACY_POLICY.name())) {
					JSONArray data = new JSONArray();
					if(ack && intent.hasExtra(IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY)) {
						RequestPolicy privacyPolicy = (RequestPolicy) intent.getSerializableExtra(IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY); // TODO Parcelable
						String jsonPrivacyPolicy = jsonHelper.toJson(privacyPolicy, RequestPolicy.class);
						Log.d(TAG, "Privacy policy retrieved: "+jsonPrivacyPolicy);
//						data.put(PrivacyPolicyIntentHelper.RESULT_PRIVACY_POLICY, jsonPrivacyPolicy);
						data.put(jsonPrivacyPolicy);
					}
					// -- Send data
					PluginResult result = new PluginResult(Status.OK, data);
					result.setKeepCallback(false);
					success(result, callbackId);
				}
				else {
					Log.e(TAG, "But this action is unknown, or data are missing");
					// -- Send data
					PluginResult result = new PluginResult(Status.INVALID_ACTION, "Response received: "+intent.getAction()+", but this action is unknown, or data are missing.");
					result.setKeepCallback(false);
					error(result, callbackId);
				}
			}
			catch (Exception e) {
				PluginResult result = new PluginResult(Status.ERROR, "Error during the JSON parsing of the result");
				result.setKeepCallback(false);
				error(result, callbackId);
			}
		}
	}

	private ServiceConnection androidServiceConnection = new ServiceConnection()
	{
		public void onServiceDisconnected(ComponentName name) {
			pluginIsInit = false;
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			privacyPolicyManagerService = (IPrivacyPolicyManager) binder.getService();
			pluginIsInit = true;
			// Wait for the init of the plugin
			if (pluginIsInit && serviceIsInit) {
				executePlugin(methodName, arguments, callbackId);
			}
		}
	};
}
