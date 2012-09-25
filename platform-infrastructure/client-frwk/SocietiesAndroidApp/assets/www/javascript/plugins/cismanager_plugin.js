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
 * Provides an API to the local CIS Manager plugin functions
 * 
 * @namespace SocietiesLocalCISManager
 */
var	SocietiesLocalCISManager = {
	/**
	 * @methodOf SocietiesLocalCISManager#
	 * @description Connects the GUI to native service implementation
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns null
	 */
	connectService: function(successCallback, failureCallback) {
		console.log("Call LocalCISManagerService - connectService");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
		'connectService',          //Telling the plugin, which action we want to perform
		[]);        //Passing a list of arguments to the plugin
	},

	/**
	 * @methodOf SocietiesLocalCISManager#
	 * @description Disconnects the GUI to native service implementation
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns null
	 */
	disconnectService: function(successCallback, failureCallback) {
		console.log("Call LocalCISManagerService - disconnectService");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
		'disconnectService',          //Telling the plugin, which action we want to perform
		[]);        //Passing a list of arguments to the plugin
	},

	/**
	 * @methodOf Societies.LocalCISManagerService#
	 * @description create a CIS
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CIS record
	 */
	createCIS: function(successCallback, failureCallback) {
		var clientPackage = "org.societies.android.platform.gui";
		
		var cisRecord = {
                "cisName": jQuery("#cisNameOnCisCreate").val(),
                "cisType": jQuery("#cisCategoryOnCisCreate").val(),
                "cisCriteria": [{
                    "attribute": "location",
                    "operation": "equals",
                    "value": "Paris"}],
                "cisDescription": jQuery("#cisDescOnCisCreate").val(),
                "cisJid" : null
                };

		console.log("Call LocalCISManagerService - createCIS with cisRecod = ");// + cisRecord.cisName);

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
				'createCIS',          //Telling the plugin, which action we want to perform
				[cisRecord]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf Societies.LocalCISManagerService#
	 * @description list CISs
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CIS record array
	 */
	listCIS: function(successCallback, failureCallback) {
		console.log("Call LocalCISManagerService - listCIS");
		var client = "org.societies.android.platform.gui";

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
				'listCIS',          //Telling the plugin, which action we want to perform
				[client]);        //Passing a list of arguments to the plugin
	},
	
	findForAllCis: function(searchTerm, successCallback, failureCallback) {
		console.log("findForAllCis invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
				'findForAllCis',          //Telling the plugin, which action we want to perform
				[client, searchTerm]);        //Passing a list of arguments to the plugin
	},
	
	findAllCisAdvertisementRecords: function(successCallback, failureCallback) {
		console.log("findAllCisAdvertisementRecords desktop invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
				'findAllCisAdvertisementRecords',          //Telling the plugin, which action we want to perform
				[client]);        //Passing a list of arguments to the plugin
	},
	
	joinCis: function(cis_id, successCallback, failureCallback) {
		console.log("joinCis desktop invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
				'joinCis',          //Telling the plugin, which action we want to perform
				[client, cis_id]);        //Passing a list of arguments to the plugin
	}
	
};