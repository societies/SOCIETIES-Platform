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
 * Societies PhoneGap/Cordova plugins namespace
 * 
 * @namespace Societies
 */

var Societies = {
		
		/**
		 * Provides an API to access device status indicators
		 * 
		 * @memberOf Societies
		 * @namespace Societies.DeviceStatus
		 */
		DeviceStatus: {
				/**
				 * 
				 * @methodOf Societies.DeviceStatus#
				 * @description To retrieve the connectivity provider status
				 * 
				 * @param {Object} successCallback The callback which will be called when result is successful.
				 * Example of JSON result:
				 * <pre>
				 * {"isInternetEnabled":true, "providerList":[{"name":"WiFi", "enabled":true}, {"name":"mobile mms", "enabled":false}]}
				 * </pre>
				 * Schema of the JSON result:
				 * <pre>
				 * {
				 *  	"name":"ConnectivityProviderStatus",
				 *  	"properties":{
				 *  		"isInternetEnabled":{
				 *  			"required":true,
				 *  			"type":"boolean",
				 *  			"description":"To know if Internet is available or not"
				 *  		},
				 *  		"providerList":{
				 *  			"required":false,
				 *  			"type":"array",
				 *  			"description":"List of connectivity providers",
				 *  			"items":{
				 *  				"name":{
				 *  					"required":true,
				 *  					"type":"string",
				 *  					"description":"Name of the connectivity provider"
				 *  				},
				 *  				"enabled":{
				 *  					"required":true,
				 *  					"type":"boolean",
				 *  					"description":"To know if this provider is available or not"
				 *  				}
				 *  			}
				 *  		}
				 *  	}
				 * }
				 * </pre>
				 * @param {Object} failureCallback The callback which will be called when result encounters an error. (String result)
				 */
				getConnectivityStatus: function(successCallback, failureCallback){
					var parameters = null;
					return cordova.exec(
							successCallback,
							failureCallback,
							'DeviceStatus',
							'getConnectivityStatus',
							[parameters]);
				},


				/**
				 * @methodOf Societies.DeviceStatus#
				 * @description To retrieve the location provider status
				 * 
				 * @param {Object} successCallback The callback which will be called when result is successful.
				 * Example of JSON result:
				 * <pre>
				 * {"providerList":[{"name":"gps", "enabled":true}, {"name":"network", "enabled":false}]}
				 * </pre>
				 * Schema of the JSON result:
				 * <pre>
				 * {
				 *  	"name":"LocationProviderStatus",
				 *  	"properties":{
				 *  		"providerList":{
				 *  			"required":false,
				 *  			"type":"array",
				 *  			"description":"List of location providers",
				 *  			"items":{
				 *  				"name":{
				 *  					"required":true,
				 *  					"type":"string",
				 *  					"description":"Name of the location provider"
				 *  				},
				 *  				"enabled":{
				 *  					"required":true,
				 *  					"type":"boolean",
				 *  					"description":"To know if this provider is available or not"
				 *  				}
				 *  			}
				 *  		}
				 *  	}
				 * }
				 * </pre>
				 * @param {Object} failureCallback The callback which will be called when result encounters an error. (String result)
				 */
				getLocationStatus: function(successCallback, failureCallback){
					var parameters = null;
					return cordova.exec(
							successCallback,
							failureCallback,
							'DeviceStatus',
							'getLocationStatus',
							[parameters]);
				},

				/**
				 * 
				 * @methodOf Societies.DeviceStatus#
				 * @description To retrieve the battery status
				 * @param {Object} successCallback The callback which will be called when result is successful.
				 * Example of JSON result:
				 * <pre>
				 * {"scale":100,"plugged":1,"level":50,"status":2,"voltage":0,"temperature":0}
				 * </pre>
				 * Schema of the JSON result:
				 * <pre>
				 * {
				 *  	"name":"BatteryStatus",
				 *  	"properties":{
				 *  		"scale":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"min":0,
				 *  			"description":"Scale"
				 *  		},
				 *  		"plugged":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"enum": [BATTERY_NOT_PLUGGED, BATTERY_PLUGGED_AC, BATTERY_PLUGGED_USB],
				 *  			"description":"To know if the mobile is plugged or not"
				 *  		},
				 *  		"level":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"min":0,
				 *  			"max":100,
				 *  			"description":"Level of battery (%)"
				 *  		},
				 *  		"status":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"min":1,
				 *  			"max":5,
				 *  			"enum": [BATTERY_STATUS_UNKNOWN, BATTERY_STATUS_CHARGING, BATTERY_STATUS_DISCHARGING, BATTERY_STATUS_NOT_CHARGING, BATTERY_STATUS_FULL],
				 *  			"description":"Level of battery (%)"
				 *  		},
				 *  		"voltage":{
				 *  			"required":false,
				 *  			"type":"nomber",
				 *  			"description":"Voltage"
				 *  		},
				 *  		"temperature":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"description":"Temperatue (°C)"
				 *  		}
				 *  	}
				 * }
				 * </pre>
				 * @param {Object} failureCallback The callback which will be called when result encounters an error. (String result)
				 */
				getBatteryStatus: function(successCallback, failureCallback){
					var parameters = null;
					return cordova.exec(
							successCallback,
							failureCallback,
							'DeviceStatus',
							'getBatteryStatus',
							[parameters]);
				},

				/**
				 * 
				 * @methodOf Societies.DeviceStatus#
				 * @description To register to battery status
				 * @param {Object} successCallback The callback which will be called when result is successful.
				 * Example of JSON result:
				 * <pre>
				 * {"scale":100,"plugged":1,"level":50,"status":2,"voltage":0,"temperature":0}
				 * </pre>
				 * Schema of the JSON result:
				 * <pre>
				 * {
				 *  	"name":"BatteryStatus",
				 *  	"properties":{
				 *  		"scale":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"min":0,
				 *  			"description":"Scale"
				 *  		},
				 *  		"plugged":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"enum": [BATTERY_NOT_PLUGGED, BATTERY_PLUGGED_AC, BATTERY_PLUGGED_USB],
				 *  			"description":"To know if the mobile is plugged or not"
				 *  		},
				 *  		"level":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"min":0,
				 *  			"max":100,
				 *  			"description":"Level of battery (%)"
				 *  		},
				 *  		"status":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"min":1,
				 *  			"max":5,
				 *  			"enum": [BATTERY_STATUS_UNKNOWN, BATTERY_STATUS_CHARGING, BATTERY_STATUS_DISCHARGING, BATTERY_STATUS_NOT_CHARGING, BATTERY_STATUS_FULL],
				 *  			"description":"Level of battery (%)"
				 *  		},
				 *  		"voltage":{
				 *  			"required":false,
				 *  			"type":"nomber",
				 *  			"description":"Voltage"
				 *  		},
				 *  		"temperature":{
				 *  			"required":false,
				 *  			"type":"number",
				 *  			"description":"Temperatue (°C)"
				 *  		}
				 *  	}
				 * }
				 * </pre>
				 * @param {Object} failureCallback The callback which will be called when result encounters an error. (String result)
				 */
				registerToBatteryStatus: function(successCallback, failureCallback){
					var parameters = {"register":true};
					return cordova.exec(
							successCallback,
							failureCallback,
							'DeviceStatus',
							'getBatteryStatus',
							[parameters]);
				}
		},
		/**
		 * Provides an API to retrieve application preferences
		 * 
		 * @memberOf Societies
		 * @namespace Societies.AppPreferences
		 */

		AppPreferences: {
			/**
			 * @methodOf Societies.AppPreferences#
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
			 * @methodOf Societies.AppPreferences#
			 * @description Retrieve a String preference value for a given preference name
			 * @param {Object} successCallback The callback which will be called when result is successful
			 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
			 * @param {String} prefName The name of preference
			 * @returns value of preference
			 */
			putStringPrefValue: function(successCallback, failureCallback, prefName, value) {
				console.log("Call Preferences - putStringPrefValue");
				
				return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginPreferences',  //Telling PhoneGap that we want to run specified plugin
				'putStringPrefValue',          //Telling the plugin, which action we want to perform
				[prefName, value]);        //Passing a list of arguments to the plugin
			},

				/**
				 * @methodOf Societies.AppPreferences#
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
				 * @methodOf Societies.AppPreferences#
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
				 * @methodOf Societies.AppPreferences#
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
				 * @methodOf Societies.AppPreferences#
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
		},
		
		
		/**
		 * Provides an API to the local CIS Manager
		 * 
		 * @memberOf Societies
		 * @namespace Societies.LocalCISManagerService
		 */
		LocalCISManagerService: {
			
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
	                    "cisName": jQuery("#cisname").val(),
	                    "cisType": "futebol",
	                    "cisCriteria": [{
	                        "attribute": "location",
	                        "operation": "equals",
	                        "value": "Paris"}],
	                    "cisDescription": "desc",
	                    "cisJid" : null
	                    }

				console.log("Call LocalCISManagerService - createCIS");


				return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
						failureCallback,     //Callback which will be called when plugin action encounters an error
						'PluginCISManager',  //Telling PhoneGap that we want to run specified plugin
						'createCIS',          //Telling the plugin, which action we want to perform
						[cisRecord]);        //Passing a list of arguments to the plugin
			}
		
		

		
		
		},
			
		
		
		/**
		 * Provides an API to the local CSS Manager
		 * 
		 * @memberOf Societies
		 * @namespace Societies.LocalCSSManagerService
		 */
		LocalCSSManagerService: {
			
			/**
			 * @methodOf Societies.LocalCSSManagerService#
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
			 * @methodOf Societies.LocalCSSManagerService#
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
			 * @methodOf Societies.LocalCSSManagerService#
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
			 * @methodOf Societies.LocalCSSManagerService#
			 * @description Log in a device to the relevant CSS
			 * @param {Object} successCallback The callback which will be called when result is successful
			 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
			 * @returns CSSrecord an updated version 
			 */
			loginCSS: function(successCallback, failureCallback) {
				var client = "org.societies.android.platform.gui";
				var cssRecord = {
						  			"archiveCSSNodes": [],
				                    "cssIdentity": jQuery("#username").val(),
				                    "cssInactivation": null,
				                    "cssNodes": [{
				                        "identity": "android@societies.local/androidOne",
				                        "status": 0,
				                        "type": 0}],
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
				                    "password": jQuery("#userpass").val(),
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
			 * @methodOf Societies.LocalCSSManagerService#
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
			 * @methodOf Societies.LocalCSSManagerService#
			 * @description Log out a device from the relevant CSS
			 * @param {Object} successCallback The callback which will be called when result is successful
			 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
			 * @returns CSSrecord an updated version 
			 */

			logoutCSS: function(successCallback, failureCallback) {
				var client = "org.societies.android.platform.gui";
				var cssRecord = {
						  			"archiveCSSNodes": [],
				                    "cssIdentity": "android",
				                    "cssInactivation": null,
				                    "cssNodes": [{
				                        "identity": "android@societies.local/androidOne",
				                        "status": 0,
				                        "type": 0}],
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
				                    "password": "androidpass",
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
			}
		},

		/**
		 * Provides an API to the Service Monitor
		 * 
		 * @memberOf Societies
		 * @namespace Societies.CoreServiceMonitorService
		 */

		CoreServiceMonitorService: {
			
			/**
			 * @methodOf Societies.CoreServiceMonitorService#
			 * @description Connects the GUI to native service implementation
			 * @param {Object} successCallback The callback which will be called when result is successful
			 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
			 * @returns null
			 */
			connectService: function(successCallback, failureCallback) {

				console.log("Call CoreServiceMonitorService - connectService");

				return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
				'connectService',              //Telling the plugin, which action we want to perform
				[]);        //Passing a list of arguments to the plugin
			},
			
			/**
			 * @methodOf Societies.CoreServiceMonitorService#
			 * @description Disconnects the GUI to native service implementation
			 * @param {Object} successCallback The callback which will be called when result is successful
			 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
			 * @returns null
			 */
			disconnectService: function(successCallback, failureCallback) {

				console.log("Call CoreServiceMonitorService - disconnectService");

				return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
				'disconnectService',              //Telling the plugin, which action we want to perform
				[]);        //Passing a list of arguments to the plugin
			},
			
			/**
			 * @methodOf Societies.CoreServiceMonitorService#
			 * @description Get a list of active services
			 * @param {Object} successCallback The callback which will be called when result is successful
			 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
			 * @returns List of active services
			 */
			activeServices: function(successCallback, failureCallback) {

				console.log("Call CoreServiceMonitorService - activeServices");

				return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
				'activeServices',              //Telling the plugin, which action we want to perform
				["org.societies.android.platform.gui"]);        //Passing a list of arguments to the plugin
			},
			
			/**
			 * @methodOf Societies.CoreServiceMonitorService#
			 * @description Get a list of active apps
			 * @param {Object} successCallback The callback which will be called when result is successful
			 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
			 * @returns List of active apps
			 */
			activeTasks: function(successCallback, failureCallback) {
				var clientPackage = "org.societies.android.platform.gui";

				console.log("Call CoreServiceMonitorService - activeTasks");

				return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
				failureCallback,     //Callback which will be called when plugin action encounters an error
				'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
				'activeTasks',              //Telling the plugin, which action we want to perform
				["org.societies.android.platform.gui"]);        //Passing a list of arguments to the plugin
			}
		}
};


/**
 * Societies GUI utility functions namespace
 * 
 * @namespace SocietiesGUI
 */


var SocietiesGUI = {
		/**
		 * @methodOf SocietiesGUI#
		 * @description Disconnect from CSSManager native service
		 * @returns null
		 */
		disconnectFromLocalCSSManager: function() {
			console.log("Disconnect from LocalCSSManager");
				
			function success(data) {
				console.log(data);
			}
			
			function failure(data) {
				alert("disconnectFromLocalCSSManager - failure: " + data);
			}
		    window.plugins.LocalCSSManagerService.disconnectService(success, failure);
		},

		/**
		 * @methodOf SocietiesGUI#
		 * @description Carry out the logout process
		 * @returns null
		 */
		successfulLogout: function() {
			console.log("Logout from CSS");

			function success(data) {
				jQuery("#username").val("");
				jQuery("#userpass").val("");
				SocietiesGUI.disconnectFromLocalCSSManager();

			}

			function failure(data) {
				alert("successfulLogout : " + "failure: " + data);
			}
			
		    window.plugins.LocalCSSManagerService.logoutCSS(success, failure);

		},

		/**
		 * @methodOf SocietiesGUI#
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
		    window.plugins.LocalCSSManagerService.connectService(success, failure);
		},
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Android Backbutton handler
		 * @param {Object} backbutton event
		 * @returns null
		 */

		backButtonHandler: function(e) {
			console.log("Back button handling");
			
			console.log("Back button handling on page: " + $.mobile.activePage[0].id );
			
		    if ($.mobile.activePage[0].id === "main"){
		        e.preventDefault();
		        navigator.app.exitApp();
		    }
		    else if ($.mobile.activePage[0].id === "menu"){
		        e.preventDefault();
		        SocietiesGUI.connectToLocalCSSManager(SocietiesGUI.successfulLogout);
		    } else {
		        navigator.app.backHistory();
		    }
		},
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Registers PhoneGap/Cordova plugins when PhoneGap is loaded and ready. N.B. Ensure that res/xml/plugins.xml file is updated.
		 * @returns null
		 */
		onDeviceReady: function() {
			console.log("PhoneGap Loaded, Device Ready");
			
			//Register any PhoneGap plugins here. Example shown for illustration
			 
			cordova.addConstructor(function() {
				//Register the javascript plugin with PhoneGap
				console.log("Register CoreServiceMonitorService plugin ");
				cordova.addPlugin('CoreServiceMonitorService', Societies.CoreServiceMonitorService);
				
				console.log("Register LocalCSSManagerService plugin ");
				cordova.addPlugin('LocalCSSManagerService', Societies.LocalCSSManagerService);
				
				console.log("Register LocalCISManagerService plugin ");
				cordova.addPlugin('LocalCISManagerService', Societies.LocalCISManagerService);
				
				console.log("Register DeviceStatus Service plugin ");
				cordova.addPlugin("DeviceStatus", Societies.DeviceStatus);

				console.log("Register Preferences plugin ");
				cordova.addPlugin("AppPreferences", Societies.AppPreferences);

			});
			
			//handle the Android Back button 
			//PhoneGap/ HTML views break semantics of Back button unless
			//app intercepts button and simulates back button behaviour
			document.addEventListener("backbutton", SocietiesGUI.backButtonHandler, false);
			
			SocietiesGUI.displayConnectionInfo();

		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Displays the CSS credentials
		 * @returns null
		 */

		displayConnectionInfo: function() {

			console.log("displayConnectionInfo");
			
			SocietiesGUI.getCSSIdentity();
			SocietiesGUI.getCSSIdentityPassword();
			SocietiesGUI.getCSSIdentityDomain();
		},
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Gets the CSS Identity app preference value
		 * @returns null
		 */
		getCSSIdentity: function () {
			function success(data) {
				console.log("getCSSIdentity - successful: " + data.value);
				jQuery("#username").val(data.value);
			}
			
			function failure(data) {
				alert("getCSSIdentity - failure: " + data);
			}

			window.plugins.AppPreferences.getStringPrefValue(success, failure, "cssIdentity");

		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Gets the CSS Identity Password app preference value
		 * @returns null
		 */

		getCSSIdentityPassword: function () {
			function success(data) {
				console.log("getCSSIdentityPassword - successful: " + data.value);
				jQuery("#userpass").val(data.value);
			}
			
			function failure(data) {
				alert("getCSSIdentityPassword - failure: " + data);
			}
			window.plugins.AppPreferences.getStringPrefValue(success, failure, "cssPassword");
		},
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Gets the CSS Identity Password app preference value
		 * @returns null
		 */

		getCSSIdentityDomain: function () {
			function success(data) {
				console.log("getCSSIdentityDomain - successful: " + data.value);
				jQuery("#identityDomain").val(data.value);
			}
			
			function failure(data) {
				alert("getCSSIdentityDomain - failure: " + data);
			}
			window.plugins.AppPreferences.getStringPrefValue(success, failure, "daURI");
		},
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description updates the registered user credentials and domain server for future login purposes
		 * @returns null
		 */
		updateCredentialPreferences: function () {
			function success(data) {
				console.log("updateCredentialPreferences - successful: " + data.value);
			}
			
			function failure(data) {
				alert("updateCredentialPreferences - failure: " + data);
			}
			window.plugins.AppPreferences.putStringPrefValue(success, failure, "cssIdentity", jQuery("#regUsername").val());
			window.plugins.AppPreferences.putStringPrefValue(success, failure, "cssPassword", jQuery("#regUserpass").val());
			window.plugins.AppPreferences.putStringPrefValue(success, failure, "daURI", jQuery("#domainServers").val());
		},
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Populate Device Info page HTML elements with device information
		 * @returns null
		 */
		deviceInfo: function() {
			console.log("Get device information");
			
			jQuery("#phoneGapVer").text(device.cordova);
			jQuery("#platform").text(device.platform);
			jQuery("#version").text(device.version);
			jQuery("#uuid").text(device.uuid);
			jQuery("#name").text(device.name);
			jQuery("#width").text(screen.width);
			jQuery("#height").text(screen.height);
			jQuery("#colorDepth").text(screen.colorDepth);
			jQuery("#pixelDepth").text(screen.pixelDepth);
			jQuery("#browserAgent").text(navigator.userAgent);

		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Actions carried in the event that a successful CSS login occurs
		 * @returns null
		 */

		successfulLogin: function() {
			console.log("Login to CSS");

			function success(data) {
				
				SocietiesGUI.populateCSSRecordpage(data);
				
				console.log("Current page: " + $.mobile.activePage[0].id);

				
				$.mobile.changePage( ($("#menu")), { transition: "slideup"} );
			}
			
			function failure(data) {
				alert("successfulLogin - failure: " + data);
			}
		    window.plugins.LocalCSSManagerService.loginCSS(success, failure);

		},
		
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Actions carried in the event that a successful create CIS
		 * @returns null
		 */

		guiCreateCIS: function() {
			console.log("create CIS");

			function success(data) {
				
				SocietiesGUI.populateCISRecordpage(data);
				
				console.log("Current page: " + $.mobile.activePage[0].id);

				
				$.mobile.changePage( ($("#menu")), { transition: "slideup"} );
			}
			
			function failure(data) {
				alert("createCIS - failure: " + data);
			}
		    window.plugins.LocalCISManagerService.createCIS(success, failure);

		},
		
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Actions carried in the event that a successful Identity Domain registration occurs
		 * @returns null
		 */

		xmppRegistration: function() {
			console.log("Regsister identity with chosen Identity domain");

			function success(data) {
				SocietiesGUI.updateCredentialPreferences();
				
				
				console.log("Current page: " + $.mobile.activePage[0].id);

				
				$.mobile.changePage( ($("#menu")), { transition: "slideup"} );
			}
			
			function failure(data) {
				alert("xmppRegistration - failure: " + data);
			}
		    window.plugins.LocalCSSManagerService.registerXMPPServer(success, failure);

		},

		/**
		 * @methodOf SocietiesGUI#
		 * @description Reset the Device Manager page HTML elements 
		 * @returns null
		 */

		resetDeviceMgr: function(){
		    jQuery("#connStatuslist").text("");
		    jQuery("#battStatuslist").text("");
		    jQuery("#locStatuslist").text("");
		    
		    
		    
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Connect to Service Monitor native service
		 * @param {Object} function to be executed if connection successful
		 * @returns null
		 */

		connectToCoreServiceMonitor: function(actionFunction) {
			console.log("Connect to CoreServiceMonitor");
				
			function success(data) {
				actionFunction();
			}
			
			function failure(data) {
				alert("connectToCoreServiceMonitor - failure: " + data);
			}
		    window.plugins.CoreServiceMonitorService.connectService(success, failure);
		},
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Refresh the CSS Profile page with the current locally cached version
		 * @returns null
		 */

		refreshCssProfile: function() {
			console.log("Refresh CSS Profile");

			function success(data) {
				SocietiesGUI.populateCSSRecordpage(data);
			}
			
			function failure(data) {
				alert("refreshCssProfile - failure: " + data);
			}
			
			window.plugins.LocalCSSManagerService.readProfile(success, failure);
			
		},

		/**
		 * @methodOf SocietiesGUI#
		 * @description Refresh the Active Service page with currently active services
		 * @returns null
		 */

		refreshActiveServices: function() {
			console.log("Refresh Active Service");

			function success(data) {
				//empty table
				jQuery('#activeServicesTable tbody').remove();
				
				for (i  = 0; i < data.length; i++) {
					var tableEntry = "<tr>" + 
					"<td>" + data[i].className + "</td>" + 
					"<td>" + SocietiesGUI.convertMilliseconds(data[i].activeSince) + "</td>" + 
						+ "</tr>"

					jQuery('#activeServicesTable').append(tableEntry);
				}
			}
			
			function failure(data) {
				alert("refreshActiveServices - failure: " + data);
			}
			
			window.plugins.CoreServiceMonitorService.activeServices(success, failure);
			
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Refresh the Active Apps page with the current active apps
		 * @returns null
		 */

		refreshActiveTasks: function() {
			console.log("Refresh Active Tasks");

			function success(data) {
				//empty table
				jQuery('#activeTasksTable tbody').remove();

				//add rows
				for (i  = 0; i < data.length; i++) {
					var tableEntry = "<tr>" + 
					"<td>" + data[i].className + "</td>" + 
					"<td>" + data[i].numRunningActivities + "</td>" + 
						+ "</tr>"

					jQuery('#activeTasksTable').append(tableEntry);
				}
			}
			
			function failure(data) {
				alert("refreshActiveTasks - failure: " + data);
			}
			
			window.plugins.CoreServiceMonitorService.activeTasks(success, failure);
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Convert an uptime in milliseconds to conventional time units
		 * @param {Object} uptime in milliseconds
		 * @returns String uptime
		 */

		convertMilliseconds: function(milliseconds) {
			value = milliseconds / 1000
			seconds = value % 60
			value /= 60
			minutes = value % 60
			value /= 60
			hours = value % 24
			value /= 24
			days = value	
			return "d: " + days + " h: " + hours + " m:" + minutes;
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Validate that viable login credentials have been entered
		 * @param {Object} username
		 * @param {Object} password
		 * @returns boolean true if credentials viable
		 */

		validateLoginCredentials: function(name, password) {
			var retValue = true;

			if (name.length === 0 || password.length === 0) {
				retValue  = false;
				alert("validateLoginCredentials: " + "User credentials must be entered");
			} 
			return retValue;
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Validate that viable registration credentials have been entered
		 * @param {Object} username
		 * @param {Object} password
		 * @param {Object} repeatPassword 
		 * @returns boolean true if credentials viable
		 */

		validateRegistrationCredentials: function(name, password, repeatPassword, termsAck) {
			var retValue = true;
			alert ("checkbox value: " + termsAck);
			if (SocietiesGUI.validateLoginCredentials(name, password)) {
				if (repeatPassword.length === 0) {
					retValue  = false;
					alert("validateRegistrationCredentials: " + "repeat entry of password must be completed");
					
				} else if (password !== repeatPassword) {
					retValue  = false;
					alert("validateRegistrationCredentials: " + "passwords are not the same - re-enter");
				} else if (!termsAck) {
					retValue  = false;
					alert("validateRegistrationCredentials: " + "Terms & Conditions must be acknowledged");
				}
			} else {
				retValue  = false;
			}
			return retValue;
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Success action 
		 * @param {Object} data
		 * @returns null
		 */

		onSuccess: function(data) {
			console.log("JS Success");
			console.log(JSON.stringify(data));
			$('.result').remove();
			$('.error').remove();
			$('<span>').addClass('result').html("Result: "+JSON.stringify(data)).appendTo('#main article[data-role=content]');
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Failure action 
		 * @param {Object} error
		 * @returns null
		 */
		onFailure: function(e) {
			console.log("JS Error");
			console.log(e);
			$('.result').remove();
			$('.error').remove();
			$('<span>').addClass('error').html(e).appendTo('#main article[data-role=content]');
		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Get an app preference value
		 * @returns null
		 */

		getAppPref: function() {
			console.log("Get app preference");
			
			jQuery("#prefValue").val("");
			prefName = jQuery("#prefName").val();
			type = jQuery("#prefType").val();

			function success(data) {
				console.log("getAppPref - successful: " + data.value);
				jQuery("#prefValue").val(data.value);
				
			};
			
			function failure(data) {
				console.log("getAppPref - failure: " + data.value);
			};

			console.log("Preference type: " + type);
			
			switch(type)
			{
			case "string":
				window.plugins.AppPreferences.getStringPrefValue(success, failure, prefName);
				break;
			case "integer":
				window.plugins.AppPreferences.getIntegerPrefValue(success, failure, prefName);
				break;
			case "long":
				window.plugins.AppPreferences.getLongPrefValue(success, failure, prefName);
				break;
			case "float":
				window.plugins.AppPreferences.getFloatPrefValue(success, failure, prefName);
				break;
			case "boolean":
				window.plugins.AppPreferences.getBooleanPrefValue(success, failure, prefName);
				break;
			default:
			  console.log("Error - Preference type is not defined");
			}

		},
		/**
		 * @methodOf SocietiesGUI#
		 * @description Populate the CSS Record page
		 * @returns null
		 */
		populateCSSRecordpage: function(data) {
			var status = ["Available for Use", "Unavailable", "Not active but on alert"];
			var type = ["Android based client", "Cloud Node", "JVM based client"];
			
			jQuery("#cssrecordforename").val(data.foreName);
			jQuery("#cssrecordname").val(data.name);
			jQuery("#cssrecordemaildetails").val(data.emailID);
			jQuery("#cssrecordimdetails").val(data.imID);
			jQuery("#cssrecorduserlocation").val(data.homeLocation);
			jQuery("#cssrecordsnsdetails").val(data.socialURI);
			jQuery("#cssrecordidentity").val(data.cssIdentity);
			jQuery("#cssrecordorgtype").val(data.entity);
			jQuery("#cssrecordsextype").val(data.sex);
			
			//empty table
			jQuery('#cssNodesTable tbody').remove();
			
			for (i  = 0; i < data.cssNodes.length; i++) {
				var tableEntry = "<tr>" + 
				"<td>" + data.cssNodes[i].identity + "</td>" + 
				"<td>" + status[data.cssNodes[i].status] + "</td>" + 
				"<td>" + type[data.cssNodes[i].type] + "</td>" + 
					+ "</tr>"

				jQuery('#cssNodesTable').append(tableEntry);
			}

		},
		
		
		/**
		 * @methodOf SocietiesGUI#
		 * @description Populate the CIS Record page
		 * @returns null
		 */
		populateCISRecordpage: function(data) {
			var status = ["Available for Use", "Unavailable", "Not active but on alert"];
			var type = ["Android based client", "Cloud Node", "JVM based client"];
			
			jQuery("#cisowner").val(data.cisOwner);
			jQuery("#cisrecordidentity").val(data.cisRecordIdentity);
			jQuery("#cisname").val(data.cisName);

		}

};



/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("jQuery calls");

	document.addEventListener("deviceready", SocietiesGUI.onDeviceReady, false);
	

	
	$('#deviceChar').click(function() {
		SocietiesGUI.deviceInfo();
	});

	$('#connectXMPP').click(function() {
		if (SocietiesGUI.validateLoginCredentials(jQuery("#username").val(), jQuery("#userpass").val())) {
			SocietiesGUI.connectToLocalCSSManager(SocietiesGUI.successfulLogin);
		}
	});

	$('#registerXMPP').click(function() {
		if (SocietiesGUI.validateRegistrationCredentials(jQuery("#regUsername").val(), jQuery("#regUserpass").val(), jQuery("#repeatRegUserpass").val(), jQuery("#regSocietiesTerms").val())) {
			SocietiesGUI.connectToLocalCSSManager(SocietiesGUI.xmppRegistration);
	}
	});

	$('#createCIS').click(function() {
			SocietiesGUI.guiCreateCIS();
	});

/*
	$('#listCIS').click(function() {
		if (SocietiesGUI.validateLoginCredentials(jQuery("#username").val(), jQuery("#userpass").val())) {
			SocietiesGUI.connectToLocalCSSManager(SocietiesGUI.successfulLogin);
		}
	});
*/
	
	$('#refreshCssRecord').click(function() {
		SocietiesGUI.refreshCssProfile();
	});
	
	$('#resetDeviceManager').click(function() {
		SocietiesGUI.resetDeviceMgr();
	});

	$('#refreshServices').click(function() {
		SocietiesGUI.connectToCoreServiceMonitor(SocietiesGUI.refreshActiveServices);
	});

	$('#refreshTasks').click(function() {
		SocietiesGUI.connectToCoreServiceMonitor(SocietiesGUI.refreshActiveTasks);
	});
	$("#logoutIcon").click(function() {
		SocietiesGUI.connectToLocalCSSManager(SocietiesGUI.successfulLogout);
	});
	
	$('#connectivity').click(function() {
		window.plugins.DeviceStatus.getConnectivityStatus(SocietiesGUI.onSuccess, SocietiesGUI.onFailure);
	});
	
	$('#location').click(function() {
		window.plugins.DeviceStatus.getLocationStatus(SocietiesGUI.onSuccess, SocietiesGUI.onFailure);
	});
	
	$('#battery').click(function() {
		window.plugins.DeviceStatus.getBatteryStatus(SocietiesGUI.onSuccess, SocietiesGUI.onFailure);
	});
	$('#registerBattery').click(function() {
		window.plugins.DeviceStatus.registerToBatteryStatus(SocietiesGUI.onSuccess, SocietiesGUI.onFailure);
	});

	$('#getPref').click(function() {
		SocietiesGUI.getAppPref();
	});

});