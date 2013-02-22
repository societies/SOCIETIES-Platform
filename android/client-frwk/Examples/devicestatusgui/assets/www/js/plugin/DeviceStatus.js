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

/**
 * @author Olivier Maridat (Trialog)
 * @version 1.0
 * @class
 * @constructor
 */
var DeviceStatus = {
	/**
	 * To retrieve the connectivity provider status
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
		return PhoneGap.exec(
				successCallback,
				failureCallback,
				'DeviceStatus',
				'getConnectivityStatus',
				[parameters]);
	},


	/**
	 * To retrieve the location provider status
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
		return PhoneGap.exec(
				successCallback,
				failureCallback,
				'DeviceStatus',
				'getLocationStatus',
				[parameters]);
	},

	/**
	 * To retrieve the battery status
	 * 
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
		return PhoneGap.exec(
				successCallback,
				failureCallback,
				'DeviceStatus',
				'getBatteryStatus',
				[parameters]);
	},

	/**
	 * To register to battery status
	 * 
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
		return PhoneGap.exec(
				successCallback,
				failureCallback,
				'DeviceStatus',
				'getBatteryStatus',
				[parameters]);
	}
};


PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("DeviceStatus", DeviceStatus);
});