phonegapdesktop.internal.parseConfigFile('pluginjs/cssmanager.json');


window.plugins.LocalCSSManagerService = {
	connectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'connectService'));
		}
		
	},
	disconnectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'disconnectService'));
		}
		
	},
	loginCSS: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'loginCSS'));
		}
		
	},
	logoutCSS: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'logoutCSS'));
		}
		
	}
}

