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

package org.societies.android.platform.androidutils;

import java.util.HashMap;
import java.util.Set;

import org.societies.android.api.apppreferences.IAppPreferences;
import org.societies.android.api.internal.apppreferences.IModifyAppPreferences;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppPreferences implements IAppPreferences, IModifyAppPreferences {
	private static final String LOG_TAG = AppPreferences.class.getName();
	
	private static final String UNDEFINED_PREF_STRING_VALUE = "undefined";
	private static final String UNDEFINED_PREF_INTEGER_VALUE = "-99999";
	private static final String UNDEFINED_PREF_LONG_VALUE = "-9999999";
	private static final String UNDEFINED_PREF_FLOAT_VALUE = "-9999999.99999";
	private static final boolean UNDEFINED_PREF_BOOLEAN_VALUE = false;

	private SharedPreferences sharedPrefs = null;

	public AppPreferences(Context context) {
		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}
	
	/**
	 * Get all of the preferences
	 * 
	 * @return HashMap<String,?> all preferences
	 */
	public HashMap<String,?> getAllPrefs() {
		Log.d(LOG_TAG, "getAllPrefs");
		HashMap<String,?> allPrefs = null;
		allPrefs = (HashMap<String, ?>) this.sharedPrefs.getAll();
		return allPrefs;
	}
	
	/**
	 * Get all of the preference names
	 * 
	 * @return Set<String> of preference names
	 */
	public Set<String> getPrefNames() {
		Log.d(LOG_TAG, "getPrefNames");
		Set<String> prefNames = null;
		
		HashMap<String,?> allPrefs  = this.getAllPrefs();
		prefNames = allPrefs.keySet();
		return prefNames;
	}
	/**
	 * Get a String preference value for a given preference name
	 * 
	 * @param prefName
	 * @return String preference value
	 */
	public String getStringPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		Log.d(LOG_TAG, "getStringPrefValue for: " + prefName);
		String retValue = null;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = this.sharedPrefs.getString(prefName, UNDEFINED_PREF_STRING_VALUE);
			Log.d(LOG_TAG, "getStringPrefValue value: " + retValue);
		}
		return retValue;
	}
	/**
	 * Update a string preference for a given preference name
	 * @param prefName
	 * @param editValue
	 * @return String preference value
	 */
	public String putStringPrefValue(String prefName, String editValue) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		Dbc.require("Preference value must be supplied", editValue != null && editValue.length() > 0);
		Log.d(LOG_TAG, "putStringPrefValue for: " + prefName + " value: " + editValue);
		
		String retValue = null;

		if (this.sharedPrefs.contains(prefName)) {
			SharedPreferences.Editor editor = this.sharedPrefs.edit();
			
			editor.putString(prefName, editValue);
			editor.commit();
			retValue = this.sharedPrefs.getString(prefName, UNDEFINED_PREF_STRING_VALUE);
			Log.d(LOG_TAG, "putStringPrefValue updated value: " + retValue);
			
		} else {
			Log.d(LOG_TAG, "Unable to find preference: " + prefName);
			
		}
		return retValue;
		
	}
	/**
	 * Get an Integer preference value for a given preference name
	 * 
	 * @param prefName
	 * @return int preference value
	 */
	public int getIntegerPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		Log.d(LOG_TAG, "getIntegerPrefValue for: " + prefName);
		int retValue = 0;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = Integer.parseInt(this.sharedPrefs.getString(prefName, UNDEFINED_PREF_INTEGER_VALUE));
			Log.d(LOG_TAG, "getIntegerPrefValue value: " + retValue);
		}
		return retValue;
	}
	/**
	 * Get an Long preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	public long getLongPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		Log.d(LOG_TAG, "getLongPrefValue for: " + prefName);
		long retValue = 0;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = Long.parseLong(this.sharedPrefs.getString(prefName, UNDEFINED_PREF_LONG_VALUE));
			Log.d(LOG_TAG, "getLongPrefValue value: " + retValue);
		}
		return retValue;
	}
	/**
	 * Get an Float preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	public float getFloatPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		Log.d(LOG_TAG, "getFloatPrefValue for: " + prefName);
		float retValue = 0;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = Float.parseFloat(this.sharedPrefs.getString(prefName, UNDEFINED_PREF_FLOAT_VALUE));
			Log.d(LOG_TAG, "getFloatPrefValue value: " + retValue);
		}
		return retValue;
	}
	/**
	 * Get a boolean preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	public boolean getBooleanPrefValue(String prefName) {
		Dbc.require("Preference name must be supplied", prefName != null && prefName.length() > 0);
		Log.d(LOG_TAG, "getBooleanPrefValue for: " + prefName);
		boolean retValue = false;
		if (this.sharedPrefs.contains(prefName)) {
			retValue = this.sharedPrefs.getBoolean(prefName, UNDEFINED_PREF_BOOLEAN_VALUE);
			Log.d(LOG_TAG, "getBooleanPrefValue value: " + retValue);
		}
		return retValue;
	}

}
