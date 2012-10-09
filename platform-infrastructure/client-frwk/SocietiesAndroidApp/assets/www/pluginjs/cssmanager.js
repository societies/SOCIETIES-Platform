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
	},
	readProfile: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'readProfile'));
		}
	},
	modifyAndroidCSSRecord: function(successCallback, errorCallback, data){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'modifyAndroidCSSRecord'));
		}
	},
	getMyFriendsList: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'getMyFriends'));
		}
	},
	readRemoteCSSProfile: function(css_id, successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'readProfile'));
		}
	},
	getSuggestedFriends: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'listAdvertisementRecords'));
		}
	},
	sendFriendRequest: function(css_id, successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'readProfile'));
		}
	},
	findForAllCss: function(searchTerm, successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'listAdvertisementRecords'));
		}
	},
	findAllCssAdvertisementRecords: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'listAdvertisementRecords'));
		}
	}
}

/**
 * Provides a Helper API to the local CSS Manager
 * 
 * @namespace SocietiesLocalCSSManagerHelper
 */
var	SocietiesLocalCSSManagerHelper = {
	/**
	 * @methodOf SocietiesLocalCSSManagerHelper#
	 * @description Disconnect from CSSManager native service
	 * @returns null
	 */
	disconnectFromLocalCSSManager: function() {
		console.log("Disconnect from LocalCSSManager");
			
		function success(data) {
			$.mobile.changePage( ($("#index")), { transition: "slideup"} );

			console.log(data);
		}
		
		function failure(data) {
			alert("disconnectFromLocalCSSManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManager.disconnectService(success, failure);
	},
	/**
	 * @methodOf SocietiesLocalCSSManagerHelper#
	 * @description Connect to CSSManager native service
	 * @param {Object} function to be executed if connection successful
	 * @returns null
	 */

	connectToLocalCSSManager: function(actionFunction) {
		console.log("Connect to LocalCSSManager");
			
		function success(data) {
			actionFunction();
		}
		
		function failure(data) {
			alert("connectToLocalCSSManager - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManager.connectService(success, failure);
	}
};
