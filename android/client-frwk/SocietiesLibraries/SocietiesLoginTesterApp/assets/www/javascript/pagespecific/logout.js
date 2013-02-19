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
 * Societies Android app SocietiesLogout function(s) namespace
 * 
 * @namespace SocietiesLogout
 */


var SocietiesLogout = {
	/**
	 * @methodOf SocietiesLogout#
	 * @description Carry out the logout process from the Cloud CSS node
	 * @returns null
	 */
	successfulCSSCloudLogout: function() {
		console.log("Logout from CSS");

		function success(data) {
			jQuery("#loginUsername").val("");
			jQuery("#loginPassword").val("");
			SocietiesLogout.successfulXMPPServerLogout();

		}

		function failure(data) {
			alert("successfulCSSCloudLogout : " + "failure: " + data);
			success(data);
		}
		
	    window.plugins.SocietiesLocalCSSManager.logoutCSS(success, failure);

	},
	/**
	 * @methodOf SocietiesLogout#
	 * @description Carry out the logout process from the XMPP server domain
	 * @returns null
	 */
	successfulXMPPServerLogout: function() {
		console.log("Logout from XMPP server");

		function success(data) {
			SocietiesLocalCSSManagerHelper.disconnectFromLocalCSSManager();

		}

		function failure(data) {
			alert("successfulXMPPServerLogout : " + "failure: " + data);
		}
		
	    window.plugins.SocietiesLocalCSSManager.logoutXMPPServer(success, failure);
	}
}


/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */

$(document).on('pageinit', '#index', function(event) {
	console.log("jQuery pageinit action(s) for logout");

	$('#slide-logout').off('click').on('click', function(){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesLogout.successfulCSSCloudLogout);
		$(".ui-page-active span[class='ui-btn-inner']").trigger('click');
	});
});
