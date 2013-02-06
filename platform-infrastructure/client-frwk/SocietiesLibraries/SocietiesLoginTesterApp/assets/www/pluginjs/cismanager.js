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
	createCIS: function(successCallback, failureCallback, name, description, type, criterias, privacyPolicy) {
		console.log("create CIS desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'createdCIS'));
		}
	},
	
	listCIS: function(successCallback, failureCallback, listCriteria) {
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
	},
	
	getActivityFeed: function(cis_id, successCallback, failureCallback) {
		console.log("getActivityFeed desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'ActivityFeed'));
		}
	},
	
	getMembers: function(cis_id, successCallback, failureCallback) {
		console.log("getActivityFeed desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'getMembers'));
		}
	},
	
	addActivity: function(cis_id, activity, successCallback, failureCallback) {
		console.log("addActivity desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(true);
		}
	},
	
	deleteActivity: function(cis_id, activity, successCallback, failureCallback) {
		console.log("deleteActivity desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(true);
		}
	},
	
	removeMember: function(cis_id, memberJid, successCallback, failureCallback) {
		console.log("removeMember desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(true);
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
			alert("disconnectFromLocalCISManager - failure: " + data);
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
