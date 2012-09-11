phonegapdesktop.internal.parseConfigFile("file:///C:/Users/aleckey/git/SOCIETIES-Platform/platform-infrastructure/client-frwk/SocietiesAndroidApp/assets/www/pluginjs/servicemanagement.json");

window.plugins.ServiceManagementService = {
	connectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("ServiceManagementService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('ServiceManagementService', 'connectService'));
		}
		
	},
	disconnectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("ServiceManagementService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('ServiceManagementService', 'disconnectService'));
		}
		
	},
	
	getServices: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("ServiceManagementService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('ServiceManagementService', 'getServices'));
		}
	},
	
	getMyServices: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("ServiceManagementService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('ServiceManagementService', 'getServices'));
		}
	}
	
}

var ServiceManagementServiceHelper = {
		/**
		 * @methodOf SocietiesCoreServiceMonitorHelper#
		 * @description Connect to Service Monitor native service
		 * @param {Object} function to be executed if connection successful
		 * @returns null
		 */

		connectToServiceManagement: function(actionFunction) {
			console.log("Connect to ServiceManagementService");
				
			function success(data) {
				actionFunction();
			}
			
			function failure(data) {
				alert("ServiceManagementService - failure: " + data);
			}
		    window.plugins.ServiceManagementService.connectService(success, failure);
		}
}
