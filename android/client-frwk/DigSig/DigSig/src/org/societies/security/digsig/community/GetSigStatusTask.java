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
package org.societies.security.digsig.community;

import java.net.URI;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.security.digsig.utility.Net;

import android.os.AsyncTask;
import android.util.Log;

public class GetSigStatusTask extends AsyncTask<String, Void, String> {
	
	private static final String TAG = GetSigStatusTask.class.getSimpleName();
	
	private CommunitySigStatusActivity activity;
	
	public GetSigStatusTask(CommunitySigStatusActivity activity) {
		this.activity = activity;
	}
	
	/* Parameters:
	 * - download URL
	 */ 
	@Override
	protected String doInBackground(String... params) {

		Log.i(TAG, "doInBackground: " + params[0]);
		
		try {
			URI uri = new URI(params[0]);
			Net net = new Net(uri);
			return net.getString();
		} catch (Exception e) {
			Log.w(TAG, "doInBackground", e);
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		
		Log.i(TAG, "onPostExecute: result = \"" + result + "\"");

		JSONObject json;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			Log.w(TAG, e);
			return;
		}
		
		int numSigners = getNumSigners(json);
		int minNumSigners = getMinNumSigners(json);
		ArrayList<String> signers = getSigners(json);
		
		activity.updateSigStatus(numSigners, minNumSigners, signers);
	}
	
	private int getNumSigners(JSONObject json) {
		
		int result;
		
		try {
			result = json.getInt("numSigners");
			Log.d(TAG, "JSON: meeting ID = " + result);
		} catch (JSONException e) {
			Log.w(TAG, e);
			return -1;
		}
		return result;
	}
	
	private int getMinNumSigners(JSONObject json) {
		
		int result;
		
		try {
			result = json.getInt("minNumSigners");
			Log.d(TAG, "JSON: meeting subject = " + result);
		} catch (JSONException e) {
			Log.w(TAG, e);
			return -1;
		}
		return result;
	}
	
	private ArrayList<String> getSigners(JSONObject json) {
		
		JSONArray signers;
		ArrayList<String> names = new ArrayList<String>();
		
		try {
			signers = json.getJSONArray("signers");
		} catch (JSONException e) {
			Log.w(TAG, e);
			return new ArrayList<String>();
		}
		try {
			for (int k = 0; k < signers.length(); k++) {
				String name = signers.getString(k);
				names.add(name);
				Log.d(TAG, "JSON: signer = " + name);
			}
		} catch (Exception e) {
			Log.w(TAG, e);
		}
		
		return names;
	}
}
