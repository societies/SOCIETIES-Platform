

/**
 * Societies Android app login function(s) namespace
 * 
 * @namespace SocietiesLogin
 */


var SocietiesLogin = {
	/**
	 * @methodOf SocietiesLogin#
	 * @description Validate that viable login credentials have been entered
	 * @param {Object} username
	 * @param {Object} password
	 * @param {Object} domain
	 * @param {Object} cloudNodeName
	 * @returns boolean true if credentials viable
	 */

	validateLoginCredentials: function(name, password, cloudNodeName, domain) {
		var retValue = true;
		console.log("validateLoginCredentials user: " + name);
		console.log("validateLoginCredentials pass: " + password);
		console.log("validateLoginCredentials cloud node: " + cloudNodeName);
		console.log("validateLoginCredentials domain: " + domain);
		
		if (name.length === 0 || password.length === 0 || domain.length === 0 || cloudNodeName.length === 0) {
			retValue  = false;
			alert("validateLoginCredentials: " + "User credentials and CSS related information must be entered");
		} 
		return retValue;
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Actions carried in the event that a successful XMPP Domain login occurs. A successful login
	 * to the XMPP server will then try to login into the defined Cloud server
	 * @returns null
	 */

	successfulXMPPDomainLogin: function() {
		console.log("Login to the chosen XMPP domain");

		function success(data) {
			SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesLogin.successfulCSSCloudLogin);
		}
		
		function failure(data) {
			alert("successfulXMPPDomainLogin - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManager.loginXMPPServer(success, failure);

	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Displays the CSS credentials
	 * @returns null
	 */

	displayConnectionInfo: function() {

		console.log("displayConnectionInfo");
		
		console.log("Number of plugins" + window.plugins.length);
		for (i = 0; i < window.plugins.length; i++) {
			console.log("plugin: " + window.plugin[i])
		};

		
		SocietiesLogin.getCSSIdentity();
		SocietiesLogin.getCSSIdentityPassword();
		SocietiesLogin.getCSSIdentityDomain();
		SocietiesLogin.getCSSCloudNode();
	},
	
	/**
	 * @methodOf SocietiesLogin#
	 * @description Gets the CSS Identity app preference value
	 * @returns null
	 */
	getCSSIdentity: function () {
		function success(data) {
			console.log("getCSSIdentity - successful: " + data.value);
			jQuery("#username").val(data.value);
		}
		
		function failure(data) {
			alert("getCSSIdentity - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, "cssIdentity");


	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Gets the CSS Identity Password app preference value
	 * @returns null
	 */

	getCSSIdentityPassword: function () {
		function success(data) {
			console.log("getCSSIdentityPassword - successful: " + data.value);
			jQuery("#userpass").val(data.value);
		}
		
		function failure(data) {
			alert("getCSSIdentityPassword - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, "cssPassword");
	},
	
	/**
	 * @methodOf SocietiesLogin#
	 * @description Gets the CSS Identity Password app preference value
	 * @returns null
	 */

	getCSSIdentityDomain: function () {
		function success(data) {
			console.log("getCSSIdentityDomain - successful: " + data.value);
			jQuery("#identitydomain").val(data.value);
		}
		
		function failure(data) {
			alert("getCSSIdentityDomain - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, "daURI");
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Gets the CSS Cloud Node preference value. This value is the name 
	 * of the Cloud node and is required as a destination point for XMPP traffic to the
	 * cloud.
	 * @returns null
	 */

	getCSSCloudNode: function () {
		function success(data) {
			console.log("getCSSCloudNode - successful: " + data.value);
			jQuery("#cloudnode").val(data.value);
		}
		
		function failure(data) {
			alert("getCSSCloudNode - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, "cloudNode");
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description updates the login user credentials and associated information for future login purposes
	 * @returns null
	 */
	updateLoginCredentialPreferences: function () {
		function success(data) {
			console.log("updateLoginCredentialPreferences - successful: " + data.value);
		}
		
		function failure(data) {
			alert("updateLoginCredentialPreferences - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cssIdentity", jQuery("#username").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cssPassword", jQuery("#userpass").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "daURI", jQuery("#identitydomain").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cloudNode", jQuery("#cloudnode").val());
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Actions carried in the event that a successful CSS Cloud login occurs
	 * @returns null
	 */

	successfulCSSCloudLogin: function() {
		console.log("Login to CSS Cloud node");

		function success(data) {
			
			SocietiesCSSRecord.populateCSSRecordpage(data);
			
			SocietiesLogin.updateLoginCredentialPreferences();
			
			console.log("Current page: " + $.mobile.activePage[0].id);
			
			$.mobile.changePage( ($("#menu")), { transition: "slideup"} );
		}
		
		function failure(data) {
			alert("successfulCSSCloudLogin - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.loginCSS(success, failure);

	}
}


