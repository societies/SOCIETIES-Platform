phonegapdesktop.internal.parseConfigFile('pluginjs/privacypolicymanager.json');


window.plugins.PrivacyPolicyManager = {
		getPrivacyPolicy: function(owner, successCallback, errorCallback){
			console.log("getPrivacyPolicy called", owner);
			if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'getPrivacyPolicy'));
			}
		}
}
