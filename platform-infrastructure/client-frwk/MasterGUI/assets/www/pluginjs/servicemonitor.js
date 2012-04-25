phonegapdesktop.internal.parseConfigFile('pluginjs/servicemonitor.json');

if (!window.plugins){
	window.plugins = {};
}

window.plugins.CoreServiceMonitorService = {
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
}phonegapdesktop.internal.parseConfigFile('pluginjs/servicemonitor.json');

if (!window.plugins){
	window.plugins = {};
}

window.plugins.barcodeScanner = {
	scan: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("barcodescanner")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('barcodescanner', 'scans'));
		}
		
	},
	
	encode : function(type, data, successCallback, errorCallback, options) {}
}
