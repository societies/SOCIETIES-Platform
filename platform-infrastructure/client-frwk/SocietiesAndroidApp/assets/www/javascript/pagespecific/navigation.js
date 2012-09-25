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
 * Societies Android app SocietiesActiveServices function(s) namespace
 * 
 * @namespace SocietiesActiveServices
 */


var SocietiesNavigation = {
	/**
	 * @methodOf SocietiesNavigation#
	 * @description home page navigation actions
	 * @returns null
	 */

	homeAction: function() {
		$.mobile.changePage($("#landing"), {transition: "slideup"});
	},
	/**
	 * @methodOf SocietiesNavigation#
	 * @description profile page navigation actions
	 * @returns null
	 */

	profileAction: function() {
		$.mobile.changePage($("#my-profile"), {transition: "slideup"});
	},
	/**
	 * @methodOf SocietiesNavigation#
	 * @description settings page navigation actions
	 * @returns null
	 */

	settingsAction: function() {
		//ADD LIST OF PRE-POPULATED CONENT FOR SETTINGS PAGE HERE
		SocialConnectorsServiceHelper.connectToSNConnectorService(SocialNetworksConnectors.refreshConnectors);
		
		//CHANGE TO THE SETTINGS PAGE
		$.mobile.changePage($("#settings"), {transition: "slideup"});
	},
	/**
	 * @methodOf SocietiesNavigation#
	 * @description about page navigation actions
	 * @returns null
	 */

	aboutAction: function() {
		$.mobile.changePage($("#about"), {transition: "slideup"});

	}
}
/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */
$(document).bind("pagechange", function(event, options) {

	console.log("jQuery pagechange action(s) for navigation bar");

	$(".ui-page-active #home-button").off('click').on('click', function(){
		SocietiesNavigation.homeAction();
	});

	$(".ui-page-active #profile-button").off('click').on('click', function(){
		SocietiesNavigation.profileAction();
		
	});

	$(".ui-page-active #settings-button").off('click').on('click', function(){
		SocietiesNavigation.settingsAction();
	});

	$(".ui-page-active #about-button").off('click').on('click', function(){
		SocietiesNavigation.aboutAction();
	});
	
	if (options.toPage[0].id === "my-profile"){
		SocietiesCSSRecord.refreshCssProfile();
	}
});
