phonegapdesktop.internal.parseConfigFile('pluginjs/privacypolicymanager.json');


window.plugins.PrivacyPolicyManager = {
		getPrivacyPolicy: function(owner, successCallback, failureCallback){
			console.log("Call PrivacyPolicyManager (desktop) - getPrivacyPolicy");
			if (phonegapdesktop.internal.randomException("PrivacyPolicyDesktopManagerService")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyDesktopManagerService', 'getPrivacyPolicy'));
			}
		}
}
