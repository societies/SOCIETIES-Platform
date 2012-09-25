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
 * Provides an API to the SocialConnectorsService plugin functions
 * 
 * @namespace SocialConnectorsService
 */

var SocialConnectorsService = {
	
	/**
	 * @methodOf SocietiesCoreServiceMonitor#
	 * @description Connects the GUI to native service implementation
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns null
	 */
	connectService: function(successCallback, failureCallback) {

		console.log("Call ServiceManagementService - connectService");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginSNConnector',  //Telling PhoneGap that we want to run specified plugin
		'connectService',              //Telling the plugin, which action we want to perform
		[]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesCoreServiceMonitor#
	 * @description Disconnects the GUI to native service implementation
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns null
	 */
	disconnectService: function(successCallback, failureCallback) {

		console.log("Call CoreServiceMonitorService - disconnectService");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginSNConnector',  //Telling PhoneGap that we want to run specified plugin
		'disconnectService',              //Telling the plugin, which action we want to perform
		[]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocialConnectorsService#
	 * @description Get a list of connectors
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns List of active services
	 */
	getSocialConnectors: function(successCallback, failureCallback) {
		console.log("Call SocialConnectorsService - getSocialConnectors");
		
		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginSNConnector',  //Telling PhoneGap that we want to run specified plugin
		'getSocialConnectors',              //Telling the plugin, which action we want to perform
		["org.societies.android.platform.gui"]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesCoreServiceMonitor#
	 * @description add a connector
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns List of active apps
	 */
	getToken: function(socialNetwork, successCallback, failureCallback) {
		console.log("Call SocialConnectorsService - getToken");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginSNConnector',  //Telling PhoneGap that we want to run specified plugin
		'getToken',              //Telling the plugin, which action we want to perform
		["org.societies.android.platform.gui", socialNetwork]);        //Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf SocietiesCoreServiceMonitor#
	 * @description remove a connector
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns List of active apps
	 */
	removeSocialConnector: function(connectorId, successCallback, failureCallback) {
		console.log("Call SocialConnectorsService - removeSocialConnector");

		return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
		failureCallback,     //Callback which will be called when plugin action encounters an error
		'PluginSNConnector',  //Telling PhoneGap that we want to run specified plugin
		'removeSocialConnector',              //Telling the plugin, which action we want to perform
		["org.societies.android.platform.gui", connectorId]);        //Passing a list of arguments to the plugin
	}
};

/**
 * Provides a Helper API to the Service Monitor service
 * 
 * @namespace SocietiesCoreServiceMonitorHelper
 */

var SocialConnectorsServiceHelper = {
	/**
	 * @methodOf SocialConnectorsServiceHelper#
	 * @description Connect to Social Networking Connectors  native service
	 * @param {Object} function to be executed if connection successful
	 * @returns null
	 */

	connectToServiceManagement: function(actionFunction) {
		console.log("SocialConnectorsServiceHelper.connectToSNSconnectors()");
			
		function success(data) {
			actionFunction();
		}
		
		function failure(data) {
			alert("connectToSNSconnectors - failure: " + data);
		}
	    window.plugins.SocialConnectorsService.connectService(success, failure);
	}
};
