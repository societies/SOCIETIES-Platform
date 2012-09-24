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
	 * @returns boolean true if credentials viable
	 */

	validateLoginCredentials: function(name, password, domain) {
		var retValue = true;
		console.log("validateLoginCredentials user: " + name);
		console.log("validateLoginCredentials pass: " + password);
		console.log("validateLoginCredentials domain: " + domain);
		
		if (name.length === 0 || password.length === 0 || domain.length === 0) {
			retValue  = false;
			alert("validateLoginCredentials: " + "User credentials and Identity Domain must be entered");
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

		
//		SocietiesLogin.getCSSIdentity();
//		SocietiesLogin.getCSSIdentityPassword();
		SocietiesLogin.getCSSIdentityDomain();
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
			jQuery("#password").val(data.value);
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
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cssPassword", jQuery("#password").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "daURI", jQuery("#identitydomain").val());
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description clears the value of a given HTML element specified by its id
	 * @returns null
	 */
	clearElementValue: function(elementId) {
		jQuery(elementId).val("");
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description appends a value the value of a given HTML element specified by its id
	 * @returns null
	 */
	appendElementValue: function(elementId, postfix) {
		var currentValue = jQuery(elementId).val();
		jQuery(elementId).val(currentValue + ": " + postfix);
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Actions carried in the event that a successful CSS Cloud login occurs
	 * @returns null
	 */

	successfulCSSCloudLogin: function() {
		console.log("Login to CSS Cloud node");

		function success(data) {
			
			//SocietiesCSSRecord.populateCSSRecordpage(data);
			
			SocietiesLogin.updateLoginCredentialPreferences();
			
			console.log("Current page: " + $.mobile.activePage[0].id);
			
			//pre-fetch pre-populated pages
			$.mobile.loadPage("html/active_services.html");
			$.mobile.loadPage("html/myProfile.html");
			
			//MAIN NAVIGATION PAGES
			$.mobile.loadPage("html/myProfile.html");
			$.mobile.loadPage("html/settings.html");
			$.mobile.loadPage("html/about.html");
			//APPS SET OF PAGES
			$.mobile.loadPage("html/my_apps.html");
			$.mobile.loadPage("html/my_apps_details.html");
			//COMMUNITY SET OF PAGES
			$.mobile.loadPage("html/communities_list.html");
			$.mobile.loadPage("html/community_profile.html");
			$.mobile.loadPage("html/communities_result.html");
			$.mobile.loadPage("html/create_community.html");
			//CSS FRIEND SET OF PAGES
			$.mobile.loadPage("html/friends_landing.html");
			$.mobile.loadPage("html/my_friends_list.html");
			$.mobile.loadPage("html/my_friends_details.html");
			$.mobile.loadPage("html/suggested_societies_friends_list.html");
			//NAVIGATION PAGES
			$.mobile.loadPage("html/settings.html");
			
			$.mobile.changePage("html/landing.html", { transition: "fade"} );
		}
		
		function failure(data) {
			alert("successfulCSSCloudLogin - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.loginCSS(success, failure);

	}
}


