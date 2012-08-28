phonegapdesktop.internal.parseConfigFile('pluginjs/servicemonitor.json');


window.plugins.SocietiesCoreServiceMonitor = {
	connectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'connectService'));
		}
		
	},
	disconnectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'disconnectService'));
		}
		
	},
	activeServices: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'activeServices'));
		}
		
	},
	activeTasks: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'activeTasks'));
		}
		
	}
}

