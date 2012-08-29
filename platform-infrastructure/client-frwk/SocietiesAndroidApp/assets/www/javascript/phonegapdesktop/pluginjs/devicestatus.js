phonegapdesktop.internal.parseConfigFile('pluginjs/devicestatus.json');


window.plugins.SocietiesDeviceStatusPlugin = {
		getConnectivityStatus : function(successCallback, errorCallback){
			if (phonegapdesktop.internal.randomException("DeviceStatusService")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('DeviceStatusService', 'connectivityStatus'));
			}
			
		},
		getLocationStatus: function(successCallback, errorCallback){
			if (phonegapdesktop.internal.randomException("DeviceStatusService")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('DeviceStatusService', 'locationStatus'));
			}
			
		},
		getBatteryStatus : function(successCallback, errorCallback){
			if (phonegapdesktop.internal.randomException("DeviceStatusService")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('DeviceStatusService', 'batteryStatus'));
			}
			
		},
		registerToBatteryStatus : function(successCallback, errorCallback){
			if (phonegapdesktop.internal.randomException("DeviceStatusService")) {
				errorCallback('A random error was generated');
			}
			else {
				successCallback(phonegapdesktop.internal.getDebugValue('DeviceStatusService', 'registerToBatteryStatus'));
			}
			
		}
}

