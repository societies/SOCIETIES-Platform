phonegapdesktop.internal.parseConfigFile('pluginjs/preferences.json');


window.plugins.AppPreferences = {
		getStringPrefValue: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("Preferences")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'getStringPrefValue'));
		}
		
	}

}

