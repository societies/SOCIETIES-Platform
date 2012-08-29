
/**
 * Societies Android app SocietiesXMPPRegistration function(s) namespace
 * 
 * @namespace SocietiesXMPPRegistration
 */


var SocietiesXMPPRegistration = {
	/**
	 * @methodOf SocietiesXMPPRegistration#
	 * @description Validate that viable registration credentials have been entered
	 * @param {Object} username
	 * @param {Object} password
	 * @param {Object} repeatPassword 
	 * @returns boolean true if credentials viable
	 */

	validateRegistrationCredentials: function(name, password, repeatPassword, termsAck) {
		var retValue = true;
		alert ("checkbox value: " + termsAck);
		
		if (name.length > 0 && password.length > 0) {
			if (repeatPassword.length === 0) {
				retValue  = false;
				alert("validateRegistrationCredentials: " + "repeat entry of password must be completed");
				
			} else if (password !== repeatPassword) {
				retValue  = false;
				alert("validateRegistrationCredentials: " + "passwords are not the same - re-enter");
			} else if (!termsAck) {
				retValue  = false;
				alert("validateRegistrationCredentials: " + "Terms & Conditions must be acknowledged");
			}
		} else {
			alert("validateRegistrationCredentials: " + "User credentials must be entered");
			retValue  = false;
		}
		return retValue;
	},
	/**
	 * @methodOf SocietiesXMPPRegistration#
	 * @description Actions carried in the event that a successful Identity Domain registration occurs
	 * @returns null
	 */

	xmppRegistration: function() {
		console.log("Regsister identity with chosen Identity domain");

		function success(data) {
			SocietiesXMPPRegistration.updateRegisteredCredentialPreferences();
			
			
			console.log("Current page: " + $.mobile.activePage[0].id);
			
			$.mobile.changePage( ($("#main")), { transition: "slideup"} );
		}
		
		function failure(data) {
			alert("xmppRegistration - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManagerService.registerXMPPServer(success, failure);

	},
	/**
	 * @methodOf SocietiesXMPPRegistration#
	 * @description updates the registered user credentials and domain server for future login purposes
	 * @returns null
	 */
	updateRegisteredCredentialPreferences: function () {
		function success(data) {
			console.log("updateRegisteredCredentialPreferences - successful: " + data.value);
		}
		
		function failure(data) {
			alert("updateRegisteredCredentialPreferences - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cssIdentity", jQuery("#regUsername").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cssPassword", jQuery("#regUserpass").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "daURI", jQuery("#domainServers").val());
		
		//Update the login page with XMPP registered values
		jQuery("#username").val(jQuery("#regUsername").val());
		jQuery("#userpass").val(jQuery("#regUserpass").val());
		jQuery("#identitydomain").val(jQuery("#domainServers").val());

	}

}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("XMPP registration jQuery calls");

	$('#registerXMPP').click(function() {
		if (SocietiesXMPPRegistration.validateRegistrationCredentials(jQuery("#regUsername").val(), jQuery("#regUserpass").val(), jQuery("#repeatRegUserpass").val(), jQuery("#regSocietiesTerms").val())) {
			SocietiesLocalCSSManagerService.connectToLocalCSSManager(SocietiesXMPPRegistration.xmppRegistration);
	}
	});

	

});