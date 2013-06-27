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

import android.util.Log;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.android.platform.androidutils.AppPreferences;
import org.societies.android.platform.gui.MasterPreferences;

/**
 * This plugin allows Preferences maintained by {@link MasterPreferences} to be retrieved
 * and updated
 */

public class PreferencesPlugin extends Plugin {
    //Logging tag
    private static final String LOG_TAG = PluginCSSManager.class.getCanonicalName();

    /**
     * Actions required to be executed
     */
    private static final String GET_ALL_PREFS = "getAllPrefs";
    private static final String GET_STRING_PREF_VALUE = "getStringPrefValue";
    private static final String PUT_STRING_PREF_VALUE = "putStringPrefValue";
    private static final String GET_BOOLEAN_PREF_VALUE = "getBooleanPrefValue";
    private static final String GET_INTEGER_PREF_VALUE = "getIntegerPrefValue";
    private static final String GET_LONG_PREF_VALUE = "getLongPrefValue";
    private static final String GET_FLOAT_PREF_VALUE = "getFloatPrefValue";
    private static final String GET_PREF_NAMES = "getPrefNames";

    private static final String JSON_RETURN_VALUE = "value";

    private AppPreferences appPreferences = null;


    @Override
    public PluginResult execute(String action, JSONArray parameters, String callbackId) {
        if (null == this.appPreferences) {
            this.appPreferences = new AppPreferences(this.ctx.getContext());
        }

        try {
            Log.d(LOG_TAG, "execute: " + action + " parameters: " + parameters.getString(0) + " for callback: " + callbackId);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        PluginResult result = new PluginResult(PluginResult.Status.ERROR);

        if (action.equals(GET_STRING_PREF_VALUE)) {
            try {
                //uncomment to log all existing preferences
//				Set<String> allPrefNames = this.getPrefNames();
//				
//				for (String prefName : allPrefNames) {
//					Log.d(LOG_TAG, "Pref name: " + prefName);
//				}
                result = new PluginResult(PluginResult.Status.OK, new JSONObject().put(JSON_RETURN_VALUE, this.appPreferences.getStringPrefValue(parameters.getString(0))));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else if (action.equals(GET_INTEGER_PREF_VALUE)) {
            try {
                result = new PluginResult(PluginResult.Status.OK, new JSONObject().put(JSON_RETURN_VALUE, this.appPreferences.getIntegerPrefValue(parameters.getString(0))));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else if (action.equals(GET_LONG_PREF_VALUE)) {
            try {
                result = new PluginResult(PluginResult.Status.OK, new JSONObject().put(JSON_RETURN_VALUE, this.appPreferences.getLongPrefValue(parameters.getString(0))));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else if (action.equals(GET_FLOAT_PREF_VALUE)) {
            try {
                result = new PluginResult(PluginResult.Status.OK, new JSONObject().put(JSON_RETURN_VALUE, this.appPreferences.getFloatPrefValue(parameters.getString(0))));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else if (action.equals(GET_BOOLEAN_PREF_VALUE)) {
            try {
                result = new PluginResult(PluginResult.Status.OK, new JSONObject().put(JSON_RETURN_VALUE, this.appPreferences.getBooleanPrefValue(parameters.getString(0))));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else if (action.equals(PUT_STRING_PREF_VALUE)) {
            try {
                result = new PluginResult(PluginResult.Status.OK, new JSONObject().put(JSON_RETURN_VALUE, this.appPreferences.putStringPrefValue(parameters.getString(0), parameters.getString(1))));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else {
            Log.d(LOG_TAG, "Undefined action: " + action);
        }
        result.setKeepCallback(false);
        return result;
    }

}
