phonegapdesktop.internal.parseConfigFile('pluginjs/privacypolicymanager.json');


window.plugins.PrivacyPolicyManagerService = {
		getPrivacyPolicy: function(owner, successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'getPrivacyPolicy'));
		}
		
	}
}
