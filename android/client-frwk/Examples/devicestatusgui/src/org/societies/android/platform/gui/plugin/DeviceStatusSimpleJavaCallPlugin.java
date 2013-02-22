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
package org.societies.android.platform.gui.plugin;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.android.platform.devicestatus.DeviceStatus;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.devicemonitor.IDeviceStatus;
import org.societies.android.api.internal.devicemonitor.BatteryStatus;
import org.societies.android.api.internal.devicemonitor.ProviderStatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.google.gson.Gson;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

/**
 * PhoneGap plugin to use device status
 * This class file is just an example about how to use simple Java call in a PhoneGap plugin. 
 * For the real plugin, see:
 * @see org.societies.android.platform.gui.plugin.DeviceStatusPlugin
 * To show the Javascript API: use "mvn site" on this project, and go to target/doc folder
 * @author Olivier Maridat (Trialog)
 */
public class DeviceStatusSimpleJavaCallPlugin extends Plugin {
	/** Actions List */
	public static final String ACTION_CONNECTIVITY = "getConnectivityStatus";
	public static final String ACTION_BATTERY = "getBatteryStatus";
	public static final String ACTION_BATTERY_REGISTER = "registerBatteryStatus";
	public static final String ACTION_LOCATION = "getLocationStatus";

	/** Callback to call */
	public String callbackID;

	public DeviceStatus deviceStatus;
	protected Gson jsonHelper;
	boolean batteryStatusRegistration = false;

	/*
	 * @see com.phonegap.api.Plugin#execute(java.lang.String, org.json.JSONArray, java.lang.String)
	 */
	@Override
	public PluginResult execute(String methodName, JSONArray arguments, String callbackID) {
		Log.d(this.getClass().getSimpleName(), "Plugin Called");

		// --- Save the callback
		this.callbackID = callbackID;

		// --- Prepare the result
		PluginResult result = null;
		deviceStatus = new DeviceStatus(this.ctx);
		jsonHelper = new Gson();

		try {
			// -- Manage the relevant method
			if (ACTION_CONNECTIVITY.equals(methodName)) {
				Log.d(this.getClass().getSimpleName(), "Connectivity Status");
				// Launch connectivity status retrieval
				return getConnectivityStatus();
			}
			else if (ACTION_LOCATION.equals(methodName)) {
				Log.d(this.getClass().getSimpleName(), "Location Status");
				// -- Launch location status retrieval
				return getLocationStatus();
			}
			else if (ACTION_BATTERY.equals(methodName)) {
				Log.d(this.getClass().getSimpleName(), "Battery Status");
				// Avert: async
				result = new PluginResult(Status.NO_RESULT);
				result.setKeepCallback(true);
				this.success(result, callbackID);
				// -- Launch the intent to retrieve the battery status
				IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				ctx.registerReceiver(new ServiceReceiver(), batteryLevelFilter);
			}
			else if (ACTION_BATTERY_REGISTER.equals(methodName)) {
				Log.d(this.getClass().getSimpleName(), "Register battery Status");
				// Avert: async
				result = new PluginResult(Status.NO_RESULT);
				result.setKeepCallback(true);
				this.success(result, callbackID);
				// -- Register or poll?
				JSONObject params = (JSONObject) arguments.get(0);
				batteryStatusRegistration = params.getBoolean("register");
				// -- Launch the intent to retrieve the battery status
				IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				ctx.registerReceiver(new ServiceReceiver(), batteryLevelFilter);
			}

			// -- Error: Unknown method name
			else {
				Log.d(this.getClass().getSimpleName(), "Invalid method name : "+methodName+" passed");
				result = new PluginResult(Status.INVALID_ACTION);
			}
		}
		catch (JSONException e) {
			result = new PluginResult(Status.ERROR, "Error during the JSON parsing");
			result.setKeepCallback(false);
			this.error(result, this.callbackID);
		}
		return result;
	}

	public PluginResult getConnectivityStatus() throws JSONException {
		JSONObject data = new JSONObject();
		// --- Internet
		boolean isInternetEnabled = deviceStatus.isInternetConnectivityOn(this.getClass().getPackage().getName());
		data.put("isInternetEnabled", isInternetEnabled);

		// --- Providers
		List<ProviderStatus> connectivityProviders = (List<ProviderStatus>) deviceStatus.getConnectivityProvidersStatus(this.getClass().getPackage().getName());
		JSONArray connectivityProviderList = new JSONArray();
		for(ProviderStatus provider : connectivityProviders) {
			JSONObject connectivityProvider = new JSONObject();
			connectivityProvider.put("name", provider.getName());
			connectivityProvider.put("enabled", provider.isEnabled());
			connectivityProviderList.put(connectivityProvider);
		}
		data.put("providerList", connectivityProviderList);
		Log.d(this.getClass().getSimpleName(), data.toString());

		// -- Send data
		PluginResult result = new PluginResult(Status.OK, data);
		return result;
	}

	public PluginResult getLocationStatus() throws JSONException {
		JSONObject data = new JSONObject();
		// --- Providers
		List<ProviderStatus> locationProviders = (List<ProviderStatus>) deviceStatus.getLocationProvidersStatus(this.getClass().getPackage().getName());
		JSONArray connectivityProviderList = new JSONArray();
		for(ProviderStatus provider : locationProviders) {
			JSONObject locationProvider = new JSONObject();
			locationProvider.put("name", provider.getName());
			locationProvider.put("enabled", provider.isEnabled());
			connectivityProviderList.put(locationProvider);
		}
		data.put("providerList", connectivityProviderList);
		Log.d(this.getClass().getSimpleName(), data.toString());

		// -- Send data
		PluginResult result = new PluginResult(Status.OK, data);
		return result;
	}

	public PluginResult getBatteryStatus(Intent intent) throws JSONException {
		// -- Battery
		double level = -1;
		double temperature = -1;
		double voltage = -1;
		int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int rawTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
		int rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		if (rawLevel >= 0 && scale > 0) {
			level = (rawLevel * 100) / scale;
		}
		if (rawTemperature >= 0) {
			temperature = rawTemperature/10;
		}
		if (rawVoltage >= 0) {
			voltage = rawVoltage/1000;
		}
		BatteryStatus batteryStatus = new BatteryStatus(level, scale, voltage, temperature, status, plugged);
		Log.d(this.getClass().getSimpleName(), batteryStatus.toString());

		// -- Send data
		JSONObject data = new JSONObject(jsonHelper.toJson(batteryStatus, BatteryStatus.class)); // It is better to send a JSON Object than a String JSON formatted
		PluginResult result = new PluginResult(Status.OK, data);
		result.setKeepCallback(false);
		this.success(result, this.callbackID);
		return result;
	}

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
			Log.i(this.getClass().getSimpleName(), intent.getAction());

			try {
				// -- Battery
				if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
					if (!batteryStatusRegistration) {
						context.unregisterReceiver(this);
					}
					getBatteryStatus(intent);
				}
			}
			catch (JSONException e) {
				PluginResult result = new PluginResult(Status.ERROR, "Error during the JSON parsing of the result");
				result.setKeepCallback(false);
				error(result, callbackID);
			}
		}

	}
}
