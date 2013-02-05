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

/**
 * Provides an API to retrieve application preferences
 * 
 * @namespace SocietiesAppPreferences
 */

var	SocietiesAppPreferences = {
		
	/**
	 * @methodOf SocietiesAppPreferences#
	 * @description Retrieve a String preference value for a given preference name
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @param {String} prefName The name of preference
	 * @returns value of preference
	 */
	getStringPrefValue: function(successCallback, failureCallback, prefName) {
		console.log("Call Preferences - getStringPrefValue");
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginPreferences',  //Telling PhoneGap that we want to run specified plugin
		'getStringPrefValue',          //Telling the plugin, which action we want to perform
		[prefName]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesAppPreferences#
	 * @description Retrieve a String preference value for a given preference name
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @param {String} prefName The name of preference
	 * @returns value of preference
	 */
	putStringPrefValue: function(successCallback, failureCallback, prefName, value) {
		console.log("Call Preferences - putStringPrefValue for preference: " + prefName + " value: " + value);
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginPreferences',  //Telling PhoneGap that we want to run specified plugin
		'putStringPrefValue',          //Telling the plugin, which action we want to perform
		[prefName, value]);        //Passing a list of arguments to the plugin
	},

	/**
	 * @methodOf SocietiesAppPreferences#
	 * @description Retrieve an Integer preference value for a given preference name
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @param {String} prefName The name of preference
	 * @returns value of preference
	 */
	getIntegerPrefValue: function(successCallback, failureCallback, prefName) {
		console.log("Call Preferences - getIntegerPrefValue");
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginPreferences',  //Telling PhoneGap that we want to run specified plugin
		'getIntegerPrefValue',          //Telling the plugin, which action we want to perform
		[prefName]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesAppPreferences#
	 * @description Retrieve a Long preference value for a given preference name
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @param {String} prefName The name of preference
	 * @returns value of preference
	 */
	getLongPrefValue: function(successCallback, failureCallback, prefName) {
		console.log("Call Preferences - getLongPrefValue");
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginPreferences',  //Telling PhoneGap that we want to run specified plugin
		'getLongPrefValue',          //Telling the plugin, which action we want to perform
		[prefName]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesAppPreferences#
	 * @description Retrieve a Float preference value for a given preference name
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @param {String} prefName The name of preference
	 * @returns value of preference
	 */
	getFloatPrefValue: function(successCallback, failureCallback, prefName) {
		console.log("Call Preferences - getFloatPrefValue");
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginPreferences',  //Telling PhoneGap that we want to run specified plugin
		'getFloatPrefValue',          //Telling the plugin, which action we want to perform
		[prefName]);        //Passing a list of arguments to the plugin
	}, 
	
	/**
	 * @methodOf SocietiesAppPreferences#
	 * @description Retrieve a Boolean preference value for a given preference name
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @param {String} prefName The name of preference
	 * @returns value of preference
	 */
	getBooleanPrefValue: function(successCallback, failureCallback, prefName) {
		console.log("Call Preferences - getBooleanPrefValue");
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginPreferences',  //Telling PhoneGap that we want to run specified plugin
		'getBooleanPrefValue',          //Telling the plugin, which action we want to perform
		[prefName]);        //Passing a list of arguments to the plugin
	}
};