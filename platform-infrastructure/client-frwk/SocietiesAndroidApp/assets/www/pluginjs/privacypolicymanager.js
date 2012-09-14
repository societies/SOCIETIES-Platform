phonegapdesktop.internal.parseConfigFile('pluginjs/privacypolicymanager.json');


window.plugins.PrivacyPolicyManagerService = {
	getPrivacyPolicy: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'getPrivacyPolicy'));
		}
		
	},
	updatePrivacyPolicy: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'updatePrivacyPolicy'));
		}
		
	},
	deletePrivacyPolicy: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'deletePrivacyPolicy'));
		}
		
	},
	inferPrivacyPolicy: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'inferPrivacyPolicy'));
		}
		
	},
	privacyPolicyToXml: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'privacyPolicyToXml'));
		}
	},
	privacyPolicyFromXml: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("PrivacyPolicyManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('PrivacyPolicyManagerService', 'privacyPolicyFromXml'));
		}
	}
	
}
