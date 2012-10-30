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
		'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
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
		'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
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
	createCIS: function(successCallback, failureCallback, name, description, type, criterias, privacyPolicy) {
		var clientPackage = "org.societies.android.platform.gui";
		console.log("Call LocalCISManagerService - createCIS");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'createCis',          //Telling the plugin, which action we want to perform
				[clientPackage, name, type, description, criterias, privacyPolicy]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf Societies.LocalCISManagerService#
	 * @description list CISs
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CIS record array
	 */
	listCIS: function(successCallback, failureCallback, listCriteria) {
		console.log("Call LocalCISManagerService - listCIS");
		var client = "org.societies.android.platform.gui";
		if (listCriteria==undefined)
			listCriteria = "all";

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'getCisList',          //Telling the plugin, which action we want to perform
				[client, listCriteria]);        //Passing a list of arguments to the plugin
	},
	
	findForAllCis: function(searchTerm, successCallback, failureCallback) {
		console.log("findForAllCis invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'findForAllCis',          //Telling the plugin, which action we want to perform
				[client, searchTerm]);        //Passing a list of arguments to the plugin
	},
	
	findAllCisAdvertisementRecords: function(successCallback, failureCallback) {
		console.log("findAllCisAdvertisementRecords invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'findAllCisAdvertisementRecords',          //Telling the plugin, which action we want to perform
				[client]);        //Passing a list of arguments to the plugin
	},
	
	joinCis: function(cisAdvert, successCallback, failureCallback) {
		console.log("joinCis invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'Join',          //Telling the plugin, which action we want to perform
				[client, cisAdvert]);        //Passing a list of arguments to the plugin
	},
	
	getActivityFeed: function(cis_id, successCallback, failureCallback) {
		console.log("getActivityFeed: " + cis_id);
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'getActivityFeed',          //Telling the plugin, which action we want to perform
				[client, cis_id]);        //Passing a list of arguments to the plugin
	},
	
	getMembers: function(cis_id, successCallback, failureCallback) {
		console.log("getMembers invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'getMembers',          //Telling the plugin, which action we want to perform
				[client, cis_id]);        //Passing a list of arguments to the plugin
	},
	
	addActivity: function(cis_id, activity, successCallback, failureCallback) {
		console.log("addActivity invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'addActivity',          //Telling the plugin, which action we want to perform
				[client, cis_id, activity]);        //Passing a list of arguments to the plugin
	},
	
	deleteActivity: function(cis_id, activity, successCallback, failureCallback) {
		console.log("deleteActivity invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'deleteActivity',          //Telling the plugin, which action we want to perform
				[client, cis_id, activity]);        //Passing a list of arguments to the plugin
	},
	
	removeMember: function(cis_id, memberJid, successCallback, failureCallback) {
		console.log("removeMember invoked");
		var client = "org.societies.android.platform.gui";
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCISFunctions',  //Telling PhoneGap that we want to run specified plugin
				'removeMember',          //Telling the plugin, which action we want to perform
				[client, cis_id, memberJid]);        //Passing a list of arguments to the plugin
	}
	
};

/**
 * Provides a Helper API to the CIS Manager
 * 
 * @namespace SocietiesLocalCISManagerHelper
 */
var	SocietiesCISManagerHelper = {
	/**
	 * @methodOf SocietiesLocalCISManagerHelper#
	 * @description Disconnect from CSSManager native service
	 * @returns null
	 */
	disconnectFromLocalCISManager: function() {
		console.log("Disconnect from LocalCISManager");
		
		function success(data) {
			$.mobile.changePage( ($("#index")), { transition: "fade"} );
			console.log(data);
		}
		
		function failure(data) {
			alert("disconnectFromLocalCISManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCISManager.disconnectService(success, failure);
	},
	
	/**
	 * @methodOf SocietiesLocalCISManagerHelper#
	 * @description Connect to CISManager native service
	 * @param {Object} function to be executed if connection successful
	 * @returns null
	 */

	connectToLocalCISManager: function(actionFunction) {
		console.log("Connect to LocalCISManager");
			
		function success(data) {
			actionFunction();
		}
		
		function failure(data) {
			alert("connectToLocalCISManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCISManager.connectService(success, failure);
	}
};
