phonegapdesktop.internal.parseConfigFile('pluginjs/snsconnectors.json');


window.plugins.SocialConnectorsService = {
	connectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("SocialConnectorsService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('SocialConnectorsService', 'connectService'));
		}
		
	},
	disconnectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("SocialConnectorsService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('SocialConnectorsService', 'disconnectService'));
		}
		
	},
	getSocialConnectors: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("SocialConnectorsService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('SocialConnectorsService', 'getSocialConnectors'));
		}
		
	},
	getToken: function(connectorType, successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("SocialConnectorsService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('SocialConnectorsService', 'addSocialConnector'));
		}
		
	},
	removeSocialConnector: function(connectorId, successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("SocialConnectorsService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('SocialConnectorsService', 'removeSocialConnector'));
		}
	}	
}

var SocialConnectorsServiceHelper = {
		/**
		 * @methodOf SocietiesCoreServiceMonitorHelper#
		 * @description Connect to Service Monitor native service
		 * @param {Object} function to be executed if connection successful
		 * @returns null
		 */

		connectToSNConnectorService: function(actionFunction) {
			console.log("Connect to SocialConnectorsServiceHelper");
				
			function success(data) {
				actionFunction();
			}
			
			function failure(data) {
				alert("SocialConnectorsServiceHelper - failure: " + data);
			}

			window.plugins.SocialConnectorsService.connectService(success, failure);
		}
}
