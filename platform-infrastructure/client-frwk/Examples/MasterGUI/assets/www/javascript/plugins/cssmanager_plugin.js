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
 * Provides an API to the local CSS Manager plugin functions
 * 
 * @namespace SocietiesLocalCSSManager
 */
var	SocietiesLocalCSSManager = {
			
	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Connects the GUI to native service implementation
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns null
	 */
	connectService: function(successCallback, failureCallback) {
		console.log("Call LocalCSSManagerService - connectService");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'connectService',          //Telling the plugin, which action we want to perform
		[]);        //Passing a list of arguments to the plugin
	},

	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Disconnects the GUI to native service implementation
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns null
	 */

	disconnectService: function(successCallback, failureCallback) {
		console.log("Call LocalCSSManagerService - disconnectService");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'disconnectService',          //Telling the plugin, which action we want to perform
		[]);        //Passing a list of arguments to the plugin
	},
	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Retrieve the current locally cached CSSRecord
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns AndroidCSSRecord
	 */

	readProfile: function(successCallback, failureCallback) {
		console.log("Call LocalCSSManagerService - readProfile");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'readCSSRecord',          //Telling the plugin, which action we want to perform
		[]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Log in a device to the relevant CSS
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CSSrecord an updated version 
	 */
	loginCSS: function(successCallback, failureCallback) {
		var client = "org.societies.android.platform.gui";
		var cssIdentity = jQuery("#username").val() + "@" + jQuery("#identitydomain").val();
		var cssRecord = {
				  			"archiveCSSNodes": [],
		                    "cssIdentity": cssIdentity ,
		                    "cssInactivation": null,
		                    "cssNodes": [],
		                    "cssRegistration": null,
		                    "cssHostingLocation" : null,
		                    "domainServer" : null,
		                    "cssUpTime": 0,
		                    "emailID": null,
		                    "entity": 0,
		                    "foreName": null,
		                    "homeLocation": null,
		                    "identityName": null,
		                    "imID": null,
		                    "name": null,
		                    "password": null,
		                    "presence": 0,
		                    "sex": 0,
		                    "socialURI": null,
		                    "status": 0
				                  }


		console.log("Call LocalCSSManagerService - loginCSS");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'loginCSS',          //Telling the plugin, which action we want to perform
		[client, cssRecord]);        //Passing a list of arguments to the plugin
	},
	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Register a user's identity with a chosen Identity Domain 
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CSSrecord with registered values
	 */
	registerXMPPServer: function(successCallback, failureCallback) {
		var client = "org.societies.android.platform.gui";
		var cssRecord = {
				  			"archiveCSSNodes": [],
		                    "cssIdentity": jQuery("#regUsername").val(),
		                    "cssInactivation": null,
		                    "cssNodes": [],
		                    "cssRegistration": null,
		                    "cssHostingLocation" : null,
		                    "domainServer" : jQuery("#domainServers").val(),
		                    "cssUpTime": 0,
		                    "emailID": null,
		                    "entity": 0,
		                    "foreName": null,
		                    "homeLocation": null,
		                    "identityName": null,
		                    "imID": null,
		                    "name": null,
		                    "password": jQuery("#regUserpass").val(),
		                    "presence": 0,
		                    "sex": 0,
		                    "socialURI": null,
		                    "status": 0
				                  }


		console.log("Call LocalCSSManagerService - registerXMPPServer");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'registerXMPPServer',          //Telling the plugin, which action we want to perform
		[client, cssRecord]);        //Passing a list of arguments to the plugin
	},
	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Login to a user's chosen Identity Domain 
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 */
	loginXMPPServer: function(successCallback, failureCallback) {
		var client = "org.societies.android.platform.gui";
		var cssRecord = {
				  			"archiveCSSNodes": [],
		                    "cssIdentity": jQuery("#username").val(),
		                    "cssInactivation": null,
		                    "cssNodes": [],
		                    "cssRegistration": null,
		                    "cssHostingLocation" : null,
		                    "domainServer" : jQuery("#identitydomain").val(),
		                    "cssUpTime": 0,
		                    "emailID": null,
		                    "entity": 0,
		                    "foreName": null,
		                    "homeLocation": null,
		                    "identityName": null,
		                    "imID": null,
		                    "name": null,
		                    "password": jQuery("#userpass").val(),
		                    "presence": 0,
		                    "sex": 0,
		                    "socialURI": null,
		                    "status": 0
				                  }


		console.log("Call LocalCSSManagerService - loginXMPPServer");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'loginXMPPServer',          //Telling the plugin, which action we want to perform
		[client, cssRecord]);        //Passing a list of arguments to the plugin
	},

	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Log out a device from the relevant CSS
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CSSrecord an updated version 
	 */

	logoutCSS: function(successCallback, failureCallback) {
		var client = "org.societies.android.platform.gui";
		var cssIdentity = jQuery("#username").val() + "@" + jQuery("#identitydomain").val();
		var cssRecord = {
				  			"archiveCSSNodes": [],
		                    "cssIdentity": cssIdentity,
		                    "cssInactivation": null,
		                    "cssNodes": [],
		                    "cssRegistration": null,
		                    "cssHostingLocation" : null,
		                    "domainServer" : null,
		                    "cssUpTime": 0,
		                    "emailID": null,
		                    "entity": 0,
		                    "foreName": null,
		                    "homeLocation": null,
		                    "identityName": null,
		                    "imID": null,
		                    "name": null,
		                    "password": null,
		                    "presence": 0,
		                    "sex": 0,
		                    "socialURI": null,
		                    "status": 0
				                  }


		console.log("Call LocalCSSManagerService - logoutCSS");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'logoutCSS',          //Telling the plugin, which action we want to perform
		[client, cssRecord]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesLocalCSSManager#
	 * @description Log out a device from the XMPP server previously logged into
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 */

	logoutXMPPServer: function(successCallback, failureCallback) {
		var client = "org.societies.android.platform.gui";
		console.log("Call LocalCSSManagerService - logoutXMPPServer");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
		'logoutXMPPServer',          //Telling the plugin, which action we want to perform
		[client]);        //Passing a list of arguments to the plugin
	}
};

/**
 * Provides a Helper API to the local CSS Manager
 * 
 * @namespace SocietiesLocalCSSManagerHelper
 */
var	SocietiesLocalCSSManagerHelper = {
	/**
	 * @methodOf SocietiesLocalCSSManagerHelper#
	 * @description Disconnect from CSSManager native service
	 * @returns null
	 */
	disconnectFromLocalCSSManager: function() {
		console.log("Disconnect from LocalCSSManager");
			
		function success(data) {
			$.mobile.changePage( ($("#main")), { transition: "slideup"} );

			console.log(data);
		}
		
		function failure(data) {
			alert("disconnectFromLocalCSSManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManager.disconnectService(success, failure);
	},
	/**
	 * @methodOf SocietiesLocalCSSManagerHelper#
	 * @description Connect to CSSManager native service
	 * @param {Object} function to be executed if connection successful
	 * @returns null
	 */

	connectToLocalCSSManager: function(actionFunction) {
		console.log("Connect to LocalCSSManager");
			
		function success(data) {
			console.log(data);
			actionFunction();
		}
		
		function failure(data) {
			alert("connectToLocalCSSManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManager.connectService(success, failure);
	}
};