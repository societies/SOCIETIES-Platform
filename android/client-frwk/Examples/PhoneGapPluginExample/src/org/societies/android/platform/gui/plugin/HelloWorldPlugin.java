/**
 * Copyright (c) 2011, SOCIETIES Consortium
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

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

/**
 * PhoneGap plugin to say hello
 * It can be involved in following manner from javascript
 * <p>
 * Result example: "Hello <your name>!"
 * </p>
 * <pre>
 * {@code
 * HelloWorldPluginNameInConfiguration.sayHello("your name", successCallback, failureCallback);
 * successCallback = function(result){
 *     //result is a json
 * }
 * failureCallback = function(error){
 *     //error is error message
 * }
 * }
 * </pre>
 * @author Olivier Maridat (Trialog)
*/
public class HelloWorldPlugin extends Plugin {

	/** Actions List */
	public static final String ACTION_HELLO = "sayHello";
	public static final String ACTION_HELLO_LIST = "sayHelloList";
	
	/*
	 * @see com.phonegap.api.Plugin#execute(java.lang.String, org.json.JSONArray, java.lang.String)
	 */
	@Override
	public PluginResult execute(String methodName, JSONArray arguments, String callbackID) {
		Log.d(this.getClass().getSimpleName(), "Plugin Called");

		PluginResult result = null;
		try {
			// -- Manage the relevant method
			if (ACTION_HELLO.equals(methodName)) {
				String name = arguments.getString(0);
				Log.d(this.getClass().getSimpleName(), name);
				if (null == name || "".equals(name)) {
					name = "World";
				}
				String helloSentance = "Hello "+name+"!";
				Log.d(this.getClass().getSimpleName(), "Returning "+ helloSentance);
				result = new PluginResult(Status.OK, helloSentance);
			}
			else if (ACTION_HELLO_LIST.equals(methodName)) {
				JSONArray names = arguments.getJSONArray(0);
				StringBuffer helloSentance = new StringBuffer();
				for(int i=0; i<names.length(); i++) {
					String name = names.get(i).toString();
					if (null == name || "".equals(name)) {
						name = "World";
					}
					helloSentance.append("Hello "+name+"!\n");
				}
				Log.d(this.getClass().getSimpleName(), "Returning "+ helloSentance);
				result = new PluginResult(Status.OK, helloSentance.toString());
			}
			// -- Error: Unknown method name
			else {
				result = new PluginResult(Status.INVALID_ACTION);
				Log.d(this.getClass().getSimpleName(), "Invalid method name : "+methodName+" passed");
			}
		}
		// -- Error
		catch (JSONException jsonEx) {
			Log.d(this.getClass().getSimpleName(), "Got JSON Exception "+ jsonEx.getMessage());
			result = new PluginResult(Status.JSON_EXCEPTION);
		}
		return result;
	}

}
