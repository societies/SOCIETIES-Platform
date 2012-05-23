phonegapdesktop.internal.parseConfigFile('pluginjs/preferences.json');


window.plugins.Preferences = {
		getStringPref: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("Preferences")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'getStringPref'));
		}
		
	},

}

