/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Societies Android app SocietiesXMPPRegistration function(s) namespace
 * 
 * @namespace SocietiesXMPPRegistration
 */


var SocietiesXMPPRegistration = {
	/**
	 * @methodOf SocietiesXMPPRegistration#
	 * @description Determine the app preference value for display passwords on entry
	 * and set the login password input accordingly
	 * @returns null
	 */

	displayRegisterPassword: function() {
		console.log("isDisplayRegisterPassword");
			
		if (SocietiesAppConfig.DISPLAY_PASSWORDS_ON_ENTRY) {
			$('#regUserpass').get(0).type = 'text';
			$('#repeatRegUserpass').get(0).type = 'text';
			console.log("Set Identity Registration passwords to read");
		} else {
			$('#regUserpass').get(0).type = 'password';
			$('#repeatRegUserpass').get(0).type = 'password';
			console.log("Set Identity Registration passwords to obscure");
		}
	},

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
			
			$.mobile.changePage( ($("#index")), { transition: "slideup"} );
		}
		
		function failure(data) {
			alert("xmppRegistration - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManager.registerXMPPServer(success, failure);

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
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "daServerURI", jQuery("#domainServer").val());
		
		//Update the login page with XMPP registered values
		jQuery("#loginUsername").val(jQuery("#regUsername").val());
		jQuery("#loginPassword").val(jQuery("#regUserpass").val());
		jQuery("#identitydomain").val(jQuery("#domainServer").val());

	},
	/**
	 * @methodOf SocietiesXMPPRegistration#
	 * @description Gets the XMPP Identity app preference value
	 * @returns null
	 */

	getIdentityDomain: function () {
		
		function success(data) {
			console.log("getIdentityDomain - successful: " + data.value);


			jQuery("#domainServer").val(data.value);
		}
		
		function failure(data) {
			alert("getIdentityDomain - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, "listIdentityDomain");
	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */
$(document).on('pageinit', '#new-identity', function(event) {

	console.log("jQuery pageinit action(s) for registerxmpp");
	
	//Configure password inputs
	SocietiesAppConfig.isDisplayPassword(SocietiesXMPPRegistration.displayRegisterPassword);

	$('#registerXMPP').off('click').on('click', function(){
		if (SocietiesXMPPRegistration.validateRegistrationCredentials(jQuery("#regUsername").val(), jQuery("#regUserpass").val(), jQuery("#repeatRegUserpass").val(), jQuery("#regSocietiesTerms").val())) {
			SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesXMPPRegistration.xmppRegistration);
		}
	});
	
});

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */
$(document).bind("pagechange", function(event, options) {
	//$("div[data-role*='page'] [id='activeServices']")$(document).on('pagechange',function(){

		console.log("jQuery pagechange action(s) for register XMPP domain");
		console.log("to page: " + options.toPage[0].id);
		if (options.toPage[0].id === "new-identity"){
			SocietiesXMPPRegistration.getIdentityDomain();
		}
});

