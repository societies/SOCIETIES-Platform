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
	startAppServices: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'startAppServices'));
		}
	},
	stopAppServices: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'stopAppServices'));
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
		var cssRecord = {
	  			"archiveCSSNodes": [],
                "cssIdentity": data.cssIdentity,
                "cssInactivation": null,
                "cssNodes": [],
                "cssRegistration": null,
                "cssHostingLocation" : null,
                "domainServer" : null,
                "cssUpTime": 0,
                "emailID": data.emailID,
                "entity": data.entity,
                "foreName": data.foreName,
                "homeLocation": null,
                "identityName": data.cssIdentity,
                "imID": data.imID,
                "name": data.name,
                "password": null,
                "presence": 0,
                "sex": data.sex,
                "socialURI": null,
                "status": 0
	            };
		console.log(cssRecord);
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
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'readProfileRemote'));
		}
	},
	getSuggestedFriends: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CssManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'getSuggestedFriends'));
		}
	},
	getFriendRequests: function(successCallback, errorCallback){
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
			successCallback(true);
		}
	},
	acceptFriendRequest: function(css_id, successCallback, errorCallback){
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
	},
	getCssActivities: function(successCallback, errorCallback){
		//if (phonegapdesktop.internal.randomException("getCssActivities")) {
		//	errorCallback('A random error was generated');
		//}
		//else {
			successCallback(phonegapdesktop.internal.getDebugValue('CssManagerService', 'cssActivityFeedFull'));
		//}
	},
	getVCardUser: function(userID, successCallback, errorCallback){
		
		for (j=0; j<vcardUsers.length; j++) {
			if (vcardUsers[j].id == userID) {
				successCallback(vcardUsers[j].vcard);
				return;
			}
		}
		//NOT FOUND - GOTO SERVER
		var jsonMethod = "";
		if (userID=="john.societies.local")
			jsonMethod = "VCardJohn";
		else if (userID=="brian.societies.local")
			jsonMethod = "VCardBrian";
		else if (userID=="dan.societies.local")
			jsonMethod = "VCardDan";
		else if (userID=="eduard.societies.local")
			jsonMethod = "VCardEduard";	
		else if (userID=="george.societies.local")
			jsonMethod = "VCardGeorge";
		else if (userID=="harry.societies.local")
			jsonMethod = "VCardHarry";
		else if (userID=="helen.societies.local")
			jsonMethod = "VCardHelen";
		else if (userID=="michael.societies.local")
			jsonMethod = "VCardMichael";
		else if (userID=="paul.societies.local")
			jsonMethod = "VCardPaul";
		else if (userID=="stephen.societies.local")
			jsonMethod = "VCardStephen";
		else if (userID=="tim.societies.local")
			jsonMethod = "VCardTim";
		else
			errorCallback("user vcard not found!");
		
		var userVCard = phonegapdesktop.internal.getDebugValue('CssManagerService', jsonMethod);
		successCallback(userVCard);
		console.log("Caching image...");
		//CACHE LOCALLY
		var vcardData = {"id": userID, "vcard": userVCard};
		//vcardUsers[countVCards] == vcardData;
		vcardUsers.push(vcardData);
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
