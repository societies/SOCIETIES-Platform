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


var SocietiesActiveServices = {
	/**
	 * @methodOf SocietiesActiveServices#
	 * @description Refresh the Active Service page with currently active services
	 * @returns null
	 */

	refreshActiveServices: function() {
		console.log("Refresh Active Service");

		function success(data) {
			//empty table
			jQuery('#activeServicesTable tbody').remove();
			
			for (i  = 0; i < data.length; i++) {
				var tableEntry = "<tr>" + 
				"<td>" + data[i].className + "</td>" + 
				"<td>" + SocietiesUtility.convertMilliseconds(data[i].activeSince) + "</td>" + 
					+ "</tr>"

				jQuery('#activeServicesTable').append(tableEntry);
			}
		}
		
		function failure(data) {
			alert("refreshActiveServices - failure: " + data);
		}
		
		window.plugins.SocietiesCoreServiceMonitor.activeServices(success, failure);
	},
	/**
	 * @methodOf SocietiesActiveServices#
	 * @description Refresh the Active Service page with currently active services
	 * @returns null
	 */

	getActiveServices: function() {
		console.log("Get Active Services");

		function success(serviceData) {
			SocietiesActiveServices.populateServicesHTML(serviceData);
		}
		
		function failure(data) {
			alert("getActiveServices - failure: " + data);
		}
		
		window.plugins.SocietiesCoreServiceMonitor.activeServices(success, failure);
	},

	/**
	 * @methodOf SocietiesActiveServices#
	 * @description Populate Active Service page with currently active services
	 * @returns null
	 */
	populateServicesHTML: function(data){
		console.log("Populate Active Service page");
		
		
		//Empty list
		$('ul#activeServicesList li:last').remove();
		//Populate services
		for (i  = 0; i < data.length; i++) {
			var tableEntry = '<li><span>' + data[i].className + '</span><span>' + SocietiesUtility.convertMilliseconds(data[i].activeSince)+ '</span></li>';
			jQuery('ul#activeServicesList').append(tableEntry);
		}

		$.mobile.changePage($("#activeServices"), {transition: "slideup"});
		$('ul#activeServicesList').listview();

	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */
$(document).on('pageinit', '#activeServices', function(event) {

	console.log("jQuery pageinit action(s) for activeservices");

//	$('#refreshServices').off('click').on('click', function(){
//		SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(SocietiesActiveServices.refreshActiveServices);
//	});
	
	$('#temp-active-services').off('click').on('click', function(){
		SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(SocietiesActiveServices.getActiveServices);
		$(".ui-page-active span[class='ui-btn-inner']").trigger('click');

	});

});

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pagechange
 * @returns null
 */
//$(document).bind("pagechange", function(event, options) {
//	//$("div[data-role*='page'] [id='activeServices']")$(document).on('pagechange',function(){
//
//		console.log("jQuery pagechange action(s) for activeservices");
//		console.log("to page: " + options.toPage[0].id);
////		if (options.toPage[0].id === "activeServices"){
////			$('ul#activeServicesList').listview();
////
////		}
//});

//$(document).bind("pagebeforechange", function(event, options) {
//
//	console.log("jQuery pagechange action(s) for activeservices");
//});
//
//$(document).bind("pagebeforeload", function(event, options) {
//
//	console.log("jQuery pagebeforeload action(s) for activeservices");
//});
//
//$(document).bind("pageload", function(event, options) {
//
//	console.log("jQuery pageload action(s) for activeservices");
//});
//
//$(document).bind("pagebeforeshow", function(event, options) {
//
//	console.log("jQuery pagebeforeshow action(s) for activeservices");
//});
//
//$(document).bind("pageshow", function(event, options) {
//
//	console.log("jQuery pageshow action(s) for activeservices");
//});
//
//$(document).bind("pagebeforecreate", function(event, options) {
//
//	console.log("jQuery pagebeforecreate action(s) for activeservices");
//});
//
//$(document).bind("pagecreate", function(event, options) {
//
//	console.log("jQuery pagecreate action(s) for activeservices");
//});



