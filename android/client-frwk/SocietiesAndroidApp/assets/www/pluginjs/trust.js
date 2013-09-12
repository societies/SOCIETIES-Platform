phonegapdesktop.internal.parseConfigFile('pluginjs/trust.json');

window.plugins.jTrustPlugin = {
		
	connectService: function(successCallback, errorCallback) {
		
		if (phonegapdesktop.internal.randomException("jTrustPlugin")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('jTrustPlugin', 'connectService'));
		}
	},
	
	disconnectService: function(successCallback, errorCallback) {
		
		if (phonegapdesktop.internal.randomException("jTrustPlugin")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('jTrustPlugin', 'disconnectService'));
		}
	},
	
	retrieveTrustRelationships: function(successCallback, failureCallback, 
			query) {
		
		console.log("retrieveTrustRelationships");
		
		if (phonegapdesktop.internal.randomException("jTrustPlugin")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('jTrustPlugin', 'retrieveTrustRelationships'));
		}
	},
	
	retrieveExtTrustRelationships: function(successCallback, failureCallback, 
			query) {
		
		console.log("retrieveExtTrustRelationships");
		
		if (phonegapdesktop.internal.randomException("jTrustPlugin")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('jTrustPlugin', 'retrieveExtTrustRelationships'));
		}
	},
	
	addDirectTrustEvidence: function(successCallback, failureCallback, 
			subjectId, objectId, evidenceType, timestamp, info) {
		
		console.log("addDirectTrustEvidence");
		
		if (phonegapdesktop.internal.randomException("jTrustPlugin")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('jTrustPlugin', 'addDirectTrustEvidence'));
		}
	} 
}

/**
 * Provides a Helper API to the Trust Client
 * 
 * @namespace jTrustPluginHelper
 */
var	jTrustPluginHelper = {
		
	/**
	 * @methodOf jTrustPluginHelper#
	 * @description Connect to Trust Client native service
	 * @param {Object} function to be executed if connection successful
	 * @returns null
	 */
	connect: function(actionFunction) {
			
		console.log("jTrustPluginHelper.connect");
			
		function success(data) {
			actionFunction();
		}
			
		function failure(data) {
			alert("jTrustPluginHelper.connect - failure: " + data);
		}
		
	    window.plugins.jTrustPlugin.connectService(success, failure);
	}
		
	/**
	 * @methodOf jTrustPluginHelper#
	 * @description Disconnect from Trust Client native service
	 * @returns null
	 */
	disconnect: function() {
		
		console.log("jTrustPluginHelper.disconnect");
		
		function success(data) {
			console.log(data);
		}
		
		function failure(data) {
			alert("jTrustPluginHelper.disconnect - failure: " + data);
		}
		
	    window.plugins.jTrustPlugin.disconnectService(success, failure);
	}
};