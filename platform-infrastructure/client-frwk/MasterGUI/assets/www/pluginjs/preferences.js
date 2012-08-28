phonegapdesktop.internal.parseConfigFile('pluginjs/preferences.json');


window.plugins.SocietiesAppPreferences = {
		getStringPrefValue: function(successCallback, errorCallback, prefName){
			if (phonegapdesktop.internal.randomException("Preferences")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'getStringPrefValue'));
			}
		},
		getIntegerPrefValue: function(successCallback, errorCallback, prefName){
			if (phonegapdesktop.internal.randomException("Preferences")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'getIntegerPrefValue'));
			}
		},
		getLongPrefValue: function(successCallback, errorCallback, prefName){
			if (phonegapdesktop.internal.randomException("Preferences")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'getLongPrefValue'));
			}
		},
		getFloatPrefValue: function(successCallback, errorCallback, prefName){
			if (phonegapdesktop.internal.randomException("Preferences")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'getFloatPrefValue'));
			}
		},
		getBooleanPrefValue: function(successCallback, errorCallback, prefName){
			if (phonegapdesktop.internal.randomException("Preferences")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'getBooleanPrefValue'));
			}
		},
		putStringPrefValue: function(successCallback, errorCallback, prefName, value){
			if (phonegapdesktop.internal.randomException("Preferences")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('Preferences', 'putStringPrefValue'));
			}
		}
}

