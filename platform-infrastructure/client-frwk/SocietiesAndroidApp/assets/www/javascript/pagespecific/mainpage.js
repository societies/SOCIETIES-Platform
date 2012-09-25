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
 * Societies GUI utility functions namespace
 * 
 * @namespace SocietiesUtility
 */


var SocietiesUtility = {
		
	/**
	 * @methodOf SocietiesUtility#
	 * @description Android Backbutton handler
	 * @param {Object} backbutton event
	 * @returns null
	 */

	backButtonHandler: function(e) {
		console.log("Back button handling");
		
		console.log("Back button handling on page: " + $.mobile.activePage[0].id );
		
	    if ($.mobile.activePage[0].id === "index"){
	        e.preventDefault();
	        navigator.app.exitApp();
	    }
	    else if ($.mobile.activePage[0].id === "landing"){
	        e.preventDefault();
	        SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesLogout.successfulCSSCloudLogout);
	    } else {
	        navigator.app.backHistory();
	    }
	},
	
	/**
	 * @methodOf SocietiesUtility#
	 * @description Registers PhoneGap/Cordova plugins when PhoneGap is loaded and ready. N.B. Ensure that res/xml/plugins.xml file is updated.
	 * @returns null
	 */
	onDeviceReady: function() {
		console.log("PhoneGap Loaded, Device Ready");
		
		//Register any PhoneGap plugins here. 
		 
		cordova.addConstructor(function() {
			//Register the javascript plugin with PhoneGap
			console.log("Register CoreServiceMonitorService plugin ");
			cordova.addPlugin('SocietiesCoreServiceMonitor', SocietiesCoreServiceMonitor);
			
			console.log("Register LocalCSSManagerService plugin ");
			cordova.addPlugin('SocietiesLocalCSSManager', SocietiesLocalCSSManager);
			
			console.log("Register DeviceStatus Service plugin ");
			cordova.addPlugin("SocietiesDeviceStatusPlugin", SocietiesDeviceStatusPlugin);

			console.log("Register Preferences plugin ");
			cordova.addPlugin("SocietiesAppPreferences", SocietiesAppPreferences);
			
			console.log("Register Preferences plugin ");
			cordova.addPlugin("SocietiesAppPreferences", SocietiesAppPreferences);
			
			console.log("Register CIS Manager plugin ");
			cordova.addPlugin("SocietiesLocalCISManager", SocietiesLocalCISManager);
			
			console.log("Register Service Management plugin ");
			cordova.addPlugin("ServiceManagementService", ServiceManagementService);
			
			console.log("Register SNS plugin ");
			cordova.addPlugin("SocialConnectorsService", SocialConnectorsService);
		});
		
		//handle the Android Back button 
		//PhoneGap/ HTML views break semantics of Back button unless
		//app intercepts button and simulates back button behaviour
		document.addEventListener("backbutton", SocietiesUtility.backButtonHandler, false);

		SocietiesLogin.displayConnectionInfo();
		


	},

	/**
	 * @methodOf SocietiesUtility#
	 * @description Convert an uptime in milliseconds to conventional time units
	 * @param {Object} uptime in milliseconds
	 * @returns String uptime
	 */

	convertMilliseconds: function(milliseconds) {
		value = milliseconds / 1000
		seconds = value % 60
		value /= 60
		minutes = value % 60
		value /= 60
		hours = value % 24
		value /= 24
		days = value	
		return "d: " + days + " h: " + hours + " m:" + minutes;
	}
};


/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on document.ready
 * N.B. this event is only fired once, i.e. on the first page's loading
 * @returns null
 */

jQuery(function() {
	console.log("jQuery document ready action(s)");

	document.addEventListener("deviceready", SocietiesUtility.onDeviceReady, false);

});

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */


$(document).bind('pageinit',function(){

	console.log("jQuery pageinit action(s) for mainpage");

	$('#connectXMPP').off('click').on('click', function(){
		if (SocietiesLogin.validateLoginCredentials(jQuery("#username").val(), jQuery("#password").val(), jQuery("#identitydomain").val())) {
			SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesLogin.successfulXMPPDomainLogin);
		}
	});

	$('#username').off('focus').on('focus', function(){
		SocietiesLogin.clearElementValue('#username')
	});

	$('#password').off('focus').on('focus', function(){
		SocietiesLogin.clearElementValue('#password')
	});
});
