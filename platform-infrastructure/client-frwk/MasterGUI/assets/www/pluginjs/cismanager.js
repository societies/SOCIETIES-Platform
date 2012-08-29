phonegapdesktop.internal.parseConfigFile('pluginjs/cismanager.json');


window.plugins.LocalCISManagerService = {
	createCIS: function(successCallback, failureCallback) {
		if (phonegapdesktop.internal.randomException("CISManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISManagerService', 'createCIS'));
		}
	}	

}