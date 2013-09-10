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
 * Provides an API to the Trust Client plugin
 * 
 * @namespace jTrustPlugin
 */
var jTrustPlugin = {
		
	/**
	 * @methodOf jTrustPlugin#
	 * @description Connects the GUI to native service implementation
	 * @param {Object} successCallback 
	 *            The callback which will be called when result is successful
	 * @param {Object} failureCallback 
	 *            The callback which will be called when result is unsuccessful
	 * @returns null
	 */
	connectService: function(successCallback, failureCallback) {
		
		console.log("jTrustPlugin: connectService");

		return cordova.exec(successCallback, // Callback which will be called when plugin action is successful
				failureCallback,             // Callback which will be called when plugin action encounters an error
				'TrustPlugin',               // Telling PhoneGap that we want to run specified plugin
				'connectService',            // Telling the plugin, which action we want to perform
				[]);                         // Passing a list of arguments to the plugin
	},

	/**
	 * @methodOf jTrustPlugin#
	 * @description Disconnects the GUI to native service implementation
	 * @param {Object} successCallback 
	 *            The callback which will be called when result is successful
	 * @param {Object} failureCallback 
	 *            The callback which will be called when result is unsuccessful
	 * @returns null
	 */
	disconnectService: function(successCallback, failureCallback) {
		
		console.log("jTrustPlugin: disconnectService");

		return cordova.exec(successCallback, // Callback which will be called when plugin action is successful
				failureCallback,             // Callback which will be called when plugin action encounters an error
				'TrustPlugin',               // Telling PhoneGap that we want to run specified plugin
				'disconnectService',         // Telling the plugin, which action we want to perform
				[]);                         // Passing a list of arguments to the plugin
	},

	/**
	 * @methodOf jTrustPlugin#
	 * @description Retrieves the trust relationships between the specified trustor and trustee
	 * @param {Object} successCallback 
	 *            The callback which will be called when result is successful
	 * @param {Object} failureCallback 
	 *            The callback which will be called when result is unsuccessful
	 * @param {Object} query
	 *            The query identifying the trust relationships to retrieve.
	 *            Specifying the query.trustorId property is mandatory.
	 * @returns the trust relationships between the specified trustor and trustee
	 */
	retrieveTrustRelationships: function(successCallback, failureCallback, query) {
		
		console.log("jTrustPlugin: retrieveTrustRelationships");
		
		var clientPackage = "org.societies.android.platform.gui";

		return cordova.exec(successCallback,  // Callback which will be called when plugin action is successful
				failureCallback,              // Callback which will be called when plugin action encounters an error
				'TrustPlugin',                // Telling PhoneGap that we want to run specified plugin
				'retrieveTrustRelationships', // Telling the plugin, which action we want to perform
				[clientPackage, query]);      // Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf jTrustPlugin#
	 * @description Retrieves the extended trust relationships between the 
	 *              specified trustor and trustee.
	 * @param {Object} successCallback 
	 *            The callback which will be called when result is successful
	 * @param {Object} failureCallback 
	 *            The callback which will be called when result is unsuccessful
	 * @param {Object} query
	 *            The query identifying the trust relationships to retrieve.
	 *            Specifying the query.trustorId property is mandatory. 
	 * @returns the extended trust relationships between the specified trustor and trustee
	 */
	retrieveExtTrustRelationships: function(successCallback, failureCallback, query) {
		
		console.log("jTrustPlugin: retrieveExtTrustRelationships");
		
		var clientPackage = "org.societies.android.platform.gui";

		return cordova.exec(successCallback,     // Callback which will be called when plugin action is successful
				failureCallback,                 // Callback which will be called when plugin action encounters an error
				'TrustPlugin',                   // Telling PhoneGap that we want to run specified plugin
				'retrieveExtTrustRelationships', // Telling the plugin, which action we want to perform
				[clientPackage, query]);         // Passing a list of arguments to the plugin
	},
	
	/**
	 * @methodOf jTrustPlugin#
	 * @description Adds the specified trust evidence between the specified subject and object
	 * @param {Object} successCallback 
	 *            The callback which will be called when result is successful
	 * @param {Object} failureCallback 
	 *            The callback which will be called when result is unsuccessful
	 * @param {Object} subjectId
	 * @param {Object} objectId
	 * @param {string} evidenceType
	 * @param {number} timestamp
	 *            The number of milliseconds since 01/01/1970.
	 * @param info
	 * @returns null
	 */
	addDirectTrustEvidence: function(successCallback, failureCallback, 
			subjectId, objectId, evidenceType, timestamp, info) {
		
		console.log("jTrustPlugin: addDirectTrustEvidence");

		var clientPackage = "org.societies.android.platform.gui";

		return cordova.exec(successCallback,            // Callback which will be called when plugin action is successful
				failureCallback,                        // Callback which will be called when plugin action encounters an error
				'TrustPlugin',                          // Telling PhoneGap that we want to run specified plugin
				'addDirectTrustEvidence',               // Telling the plugin, which action we want to perform
				[clientPackage, subjectId, objectId,    // Passing a list of arguments to the plugin 
				 evidenceType, timestamp, info]); 
	}
};

/**
 * Provides a Helper API to the Trust Client
 * 
 * @namespace jTrustPluginHelper
 */
var jTrustPluginHelper = {
		
	/**
	 * @methodOf jTrustPluginHelper#
	 * @description Connect to Trust Client native service
	 * @param {Object} function to be executed if connection successful
	 * @returns null
	 */
	connect: function(actionFunction) {
			
		console.log("jTrustPluginHelper: connect");
			
		function success(data) {
			actionFunction();
		}
			
		function failure(data) {
			alert("jTrustPluginHelper.connect - failure: " + data);
		}
		
	    window.plugins.jTrustPlugin.connectService(success, failure);
	},
		
	/**
	 * @methodOf jTrustPluginHelper#
	 * @description Disconnect from Trust Client native service
	 * @returns null
	 */
	disconnect: function() {
		
		console.log("jTrustPluginHelper: disconnect");
		
		function success(data) {
			console.log(data);
		}
		
		function failure(data) {
			alert("jTrustPluginHelper.disconnect - failure: " + data);
		}
		
	    window.plugins.jTrustPlugin.disconnectService(success, failure);
	}
};