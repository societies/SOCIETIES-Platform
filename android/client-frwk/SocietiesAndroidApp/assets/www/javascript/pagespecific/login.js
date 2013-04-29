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

//Browser globals
/*global clearInterval: false, clearTimeout: false, document: false, event: false, frames: false, history: false, Image: false, location: false, name: false, navigator: false, Option: false, parent: false, screen: false, setInterval: false, setTimeout: false, window: false, XMLHttpRequest: false */
//Miscellaneous globals
/*global alert: false, confirm: false, console: false, Debug: false, opera: false, prompt: false, WSH: false, $: false, jQuery: false */
//JQuery globals
/*global $: false, jQuery: false */
//Mocha testing globals
/*global exports: false */
//Specific globals
/*global SocietiesAppConfig: false */

var myIdentity;
var vcardUsers = [];

var SocietiesLogin = {
		
		/**
		 * @methodOf SocietiesLogin#
		 * @description Refresh the Login page based on current app preferences
		 * @returns void
		 */
	
	refreshWithAppPreferences: function() {
		SocietiesAppConfig.isDisplayPassword(SocietiesLogin.displayConnectionInfo);
	},
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
			SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesLogin.successfulStartAllAppServices);
		}
		
		function failure(data) {
			alert("successfulXMPPDomainLogin - failure: " + data);
		}
	    window.plugins.SocietiesLocalCSSManager.loginXMPPServer(success, failure);

	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Start all of the Societies Client app services
	 * @returns null
	 */

	successfulStartAllAppServices: function() {
		console.log("Start all Societies Client app native services");

		function success(data) {
			SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesLogin.successfulCSSCloudLogin);
		}
		
		function failure(data) {
			alert("successfulXMPPDomainLogin - failure: " + data);
		}
		window.plugins.SocietiesLocalCSSManager.startAppServices(success, failure);
	},
	/**
	 * @methodOf SocietiesLogin#
	 * @description Displays the CSS credentials
	 * @returns null
	 */

	displayConnectionInfo: function() {

		console.log("displayConnectionInfo");
		
//		console.log("Number of plugins" + window.plugins.length);
//		for (i = 0; i < window.plugins.length; i++) {
//			console.log("plugin: " + window.plugin[i])
//		};
		
		if (SocietiesAppConfig.DISPLAY_PASSWORDS_ON_ENTRY) {
			$('#loginPassword').get(0).type = 'text';
			//jQuery("#loginPassword").attr("type", "text"); doesn't work as JQuery inserts a security check !
			console.log("Set Login password to read");
		} else {
			$('#loginPassword').get(0).type = 'password';
			//jQuery("#loginPassword").attr("type", "password"); doesn't work as JQuery inserts a security check !
			console.log("Set Login password to obscure");
		}
		SocietiesLogin.getCSSIdentity();
		SocietiesLogin.getCSSIdentityPassword();
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
			jQuery("#loginUsername").val(data.value);
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
			jQuery("#loginPassword").val(data.value);
		}
		
		function failure(data) {
			alert("getCSSIdentityPassword - failure: " + data);
		}
		window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, "cssPassword");
	},
	
	/**
	 * @methodOf SocietiesLogin#
	 * @description Gets the CSS Domain Server (DNS name) app preference value
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
		window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, "daServerURI");
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
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cssIdentity", jQuery("#loginUsername").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "cssPassword", jQuery("#loginPassword").val());
		window.plugins.SocietiesAppPreferences.putStringPrefValue(success, failure, "daServerURI", jQuery("#identitydomain").val());
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
			$.mobile.loadPage("active_services.html");
			$.mobile.loadPage("myProfile.html");
			
			//MAIN NAVIGATION PAGES
			$.mobile.loadPage("myProfile.html");
			$.mobile.loadPage("settings.html");
			$.mobile.loadPage("about.html");
			//APPS SET OF PAGES
			$.mobile.loadPage("my_apps.html");
			$.mobile.loadPage("my_apps_details.html");
			//COMMUNITY SET OF PAGES
			$.mobile.loadPage("communities_list.html");
			$.mobile.loadPage("community_profile.html");
			$.mobile.loadPage("communities_result.html");
			$.mobile.loadPage("create_community.html");
			//CSS FRIEND SET OF PAGES
			$.mobile.loadPage("my_friends_list.html");
			$.mobile.loadPage("my_friends_details.html");
			$.mobile.loadPage("suggested_societies_friends_list.html");
			$.mobile.loadPage("friend_request_list.html");
			//NAVIGATION PAGES
			$.mobile.loadPage("settings.html");
			
			$.mobile.changePage("landing.html", { transition: "fade"} );
		}
		
		function failure(data) {
			alert("successfulCSSCloudLogin - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.loginCSS(success, failure);
	},
	
	/**
	 * @methodOf SocietiesLogin#
	 * @description Actions carried to populate css activity feed
	 * @returns null
	 */
	loadCssActivities: function() {
		function success(data) {
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#cssmgr_activity_feed').children().length >0 )
				$('ul#cssmgr_activity_feed li:last').remove();

			//FOREACH ACTIVITY
			if(data.length > 0) {
				console.log("data size: " + data.length);
				myIdentity = data[0].actor;
				var mLastDate = "";
				for (i=data.length-1; i >= 0 ; i--) {
					//HEADER
					//console.log("published: " + data[i].published);
					//console.log("verb: " + data[i].verb);
					var d = new Date();
					d.setTime(data[i].published); 
					var dateStr = d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate();
					if (mLastDate != dateStr) {
						mLastDate = dateStr;
						$('ul#cssmgr_activity_feed').append("<li data-role=\"list-divider\">" + dateStr + "</li>" );
					}
					//DATA
					var hours = d.getHours(),
					    minutes = d.getMinutes();
					if (minutes < 10)
						minutes = "0" + minutes	
					var suffix = "AM";
					if (hours >= 12) {
						suffix = "PM";
						hours = hours - 12;
					}
					if (hours == 0)
						hours = 12;
					//BODY FORMATTING
					//var n=data[i].actor.indexOf(".");
					//var actorStr = data[i].actor.substring(0, n);
					var tableEntry = "<li id=\"li" + data[i].published + "\"><a href=\"#\" onclick=\"return false;\">" +
									 //"<h2>"+ actorStr + "</h2>" +
						 	 		 "<p>" + data[i].verb + "</p>" + //+ " " + data[i].object + 
						 	 		"<p class=\"ui-li-aside\">" + hours + ":" + minutes + " " + suffix + "</p>" + 
						 	 		 "</a></li>";
					$('ul#cssmgr_activity_feed').append(tableEntry);
				}
			}
			$('ul#cssmgr_activity_feed').listview('refresh');
			$('ul#cssmgr_activity_feed').trigger( "collapse" );
			//EXPAND LIST IF SHORT
			//if (data.length <3)
			//	$('ul#cis_activity_feed').trigger( "expand" );
		}
		
		function failure(data) {
			var tableEntry = "<li><p>Error occurred retrieving activities: "+ data + "</p></li>";
			$('ul#cssmgr_activity_feed').append(tableEntry);
			$('ul#cssmgr_activity_feed').listview('refresh');
		}
		
		window.plugins.SocietiesLocalCSSManager.getCssActivities(success, failure);
	}	
}

//Only used by Mocha
if(typeof exports !== 'undefined') {
    exports.validateLoginCredentials = SocietiesLogin.validateLoginCredentials;
}


