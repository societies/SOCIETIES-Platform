phonegapdesktop.internal.parseConfigFile('pluginjs/cismanager.json');

window.plugins.SocietiesLocalCISManager = {
	connectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'connectService'));
		}
	},
	disconnectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'disconnectService'));
		}
	},
	createCIS: function(successCallback, failureCallback) {
		console.log("create CIS desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'createdCIS'));
		}
	},
	
	listCIS: function(successCallback, failureCallback) {
		console.log("list CIS desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'listOfCISs'));
		}
	},
	
	findForAllCis: function(searchTerm, successCallback, failureCallback) {
		console.log("searchCisDir desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'CISDirResults'));
		}
	},
	
	findAllCisAdvertisementRecords: function(successCallback, failureCallback) {
		console.log("findAllCisAdvertisementRecords desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'CISDirResults'));
		}
	},
	
	joinCis: function(cis_id, successCallback, failureCallback) {
		console.log("joinCis desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'JoinResponse'));
		}
	}
}

/**
 * Provides a Helper API to the CIS Manager
 * 
 * @namespace SocietiesLocalCISManagerHelper
 */
var	SocietiesCISManagerHelper = {
	/**
	 * @methodOf SocietiesLocalCISManagerHelper#
	 * @description Disconnect from CSSManager native service
	 * @returns null
	 */
	disconnectFromLocalCISManager: function() {
		console.log("Disconnect from LocalCISManager");
		
		function success(data) {
			$.mobile.changePage( ($("#index")), { transition: "fade"} );
			console.log(data);
		}
		
		function failure(data) {
			alert("disconnectFromLocalCSSManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCISManager.disconnectService(success, failure);
	},
	
	/**
	 * @methodOf SocietiesLocalCISManagerHelper#
	 * @description Connect to CISManager native service
	 * @param {Object} function to be executed if connection successful
	 * @returns null
	 */

	connectToLocalCISManager: function(actionFunction) {
		console.log("Connect to LocalCISManager");
			
		function success(data) {
			actionFunction();
		}
		
		function failure(data) {
			alert("connectToLocalCSSManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCISManager.connectService(success, failure);
	}
};
