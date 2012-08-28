phonegapdesktop.internal.parseConfigFile('pluginjs/cssmanager.json');


window.plugins.SocietiesLocalCSSManager = {
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
	},
	registerXMPPServer: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'registerXMPPServer'));
		}
	},
	loginXMPPServer: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'loginXMPPServer'));
		}
	},
	logoutXMPPServer: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'logoutXMPPServer'));
		}
	}
}

