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

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.societies.android.platform.gui.MasterPreferences;
import org.societies.utilities.DBC.Dbc;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This plugin allows Preferences maintained by {@link MasterPreferences} to be retrieved
 */

public class PreferencesPlugin extends Plugin {
	//Logging tag
	private static final String LOG_TAG = PluginCSSManager.class.getName();
	private static final String UNDEFINED_PREF_STRING_VALUE = "undefined";
	private static final int UNDEFINED_PREF_INTEGER_VALUE = -999999;
	private static final long UNDEFINED_PREF_LONG_VALUE = -99999999999L;
	private static final float UNDEFINED_PREF_FLOAT_VALUE = -99999999999.99999F;
	private static final boolean UNDEFINED_PREF_BOOLEAN_VALUE = false;

	private SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.ctx.getContext());
	/**
	 * Actions required to be executed
	 */
	private static final String GET_ALL_PREFS = "getAllPrefs";
	private static final String getPrefValue = "getPrefValue";
	private static final String getPrefNames = "getPrefNames";
	

	@Override
	public PluginResult execute(String action, JSONArray parameters, String callbackId) {
		Log.d(LOG_TAG, "execute: " + action + " for callback: " + callbackId);

		PluginResult result = null;
		
		if (action.equals("getStringPref")) {
			try {
				result = new PluginResult(PluginResult.Status.OK, this.getStringPrefValue(parameters.getString(0)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else if (action.equals("getIntegerPref")) {
			try {
				result = new PluginResult(PluginResult.Status.OK, this.getIntegerPrefValue(parameters.getString(0)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else if (action.equals("getLongPref")) {
			try {
				result = new PluginResult(PluginResult.Status.OK, this.getLongPrefValue(parameters.getString(0)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else if (action.equals("getFloatPref")) {
			try {
				result = new PluginResult(PluginResult.Status.OK, this.getFloatPrefValue(parameters.getString(0)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else if (action.equals("getBooleanPref")) {
			try {
				result = new PluginResult(PluginResult.Status.OK, this.getBooleanPrefValue(parameters.getString(0)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else	
			//if method does not exist send synchronous error result
	        result = new PluginResult(PluginResult.Status.ERROR);
	        result.setKeepCallback(false);

		return result;
	}

	/**
	 * Get all of the preferences
	 * 
	 * @return HashMap<String,?> all preferences
	 */
	private HashMap<String,?> getAllPrefs() {
		HashMap<String,?> allPrefs = null;
		allPrefs = (HashMap<String, ?>) this.sharedPrefs.getAll();
		return allPrefs;
	}
	
	/**
	 * Get all of the preference names
	 * 
	 * @return ArrayList<String> of preference names
	 */
	private ArrayList<String> getPrefNames() {
		ArrayList<String> prefNames = null;
		
		HashMap<String,?> allPrefs  = this.getAllPrefs();
		prefNames = (ArrayList<String>) allPrefs.keySet();
		return prefNames;
	}
	/**
	 * Get a String preference value for a given preference name
	 * 
	 * @param prefName
	 * @return String preference value
	 */
	private String getStringPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		String retValue = null;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = this.sharedPrefs.getString(prefName, UNDEFINED_PREF_STRING_VALUE);
		}
		return retValue;
	}
	/**
	 * Get an Integer preference value for a given preference name
	 * 
	 * @param prefName
	 * @return int preference value
	 */
	private int getIntegerPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		int retValue = 0;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = this.sharedPrefs.getInt(prefName, UNDEFINED_PREF_INTEGER_VALUE);
		}
		return retValue;
	}
	/**
	 * Get an Long preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	private long getLongPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		long retValue = 0;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = this.sharedPrefs.getLong(prefName, UNDEFINED_PREF_LONG_VALUE);
		}
		return retValue;
	}
	/**
	 * Get an Float preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	private float getFloatPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		float retValue = 0;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = this.sharedPrefs.getFloat(prefName, UNDEFINED_PREF_FLOAT_VALUE);
		}
		return retValue;
	}
	/**
	 * Get a boolean preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	private boolean getBooleanPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		boolean retValue = false;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = this.sharedPrefs.getBoolean(prefName, UNDEFINED_PREF_BOOLEAN_VALUE);
		}
		return retValue;
	}
}
