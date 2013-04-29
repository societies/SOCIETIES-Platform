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
 * Societies Android app SocietiesCSSRecord function(s) namespace
 * 
 * @namespace SocietiesCSSRecord
 */
//Browser globals
/*global clearInterval: false, clearTimeout: false, document: false, event: false, frames: false, history: false, Image: false, location: false, name: false, navigator: false, Option: false, parent: false, screen: false, setInterval: false, setTimeout: false, window: false, XMLHttpRequest: false */
//Miscellaneous globals
/*global alert: false, confirm: false, console: false, Debug: false, opera: false, prompt: false, WSH: false, $: false, jQuery: false */
//JQuery globals
/*global $: false, jQuery: false */
//Specific globals
/* global SocietiesLocalCSSManagerHelper: false*/

var SocietiesCSSRecord = {
	/**
	 * @methodOf SocietiesCSSRecord#
	 * @description Refresh the CSS Profile page with the current locally cached version
	 * @returns null
	 */

	refreshCssProfile: function() {
		console.log("Refresh CSS Profile");

		function success(data) {
			SocietiesCSSRecord.populateCSSRecordpage(data);
		}
		
		function failure(data) {
			alert("refreshCssProfile - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.readProfile(success, failure);
		
	},
	/**
	 * @methodOf SocietiesCSSRecord#
	 * @description Populate the CSS Record page
	 * @returns null
	 */
	populateCSSRecordpage: function(data) {
		var status = ["Available for Use", "Unavailable", "Not active but on alert"];
		var type = ["Android based client", "Cloud Node", "JVM based client"];
		
		$("#cssrecordforename").val(data.foreName);
		$("#cssrecordname").val(data.name);
		$("#cssrecordemaildetails").val(data.emailID);
		$("#cssrecordidentity").val(data.cssIdentity); 
		$("#cssrecordorgtype").val(data.entity);
		$("#cssrecordsextype").val(data.sex);
		$("#cssrecordposition").val(data.position);
		$("#cssrecordworkplace").val(data.workplace);
		$("#cssrecorduserlocation").val(data.homeLocation);
		
		//REMOVE ALL ENTRIES
		while( $('ul#cssNodesList').children().length >0 )
			$('ul#cssNodesList li:last').remove();
		//POPULATE LIST
		for (i  = 0; i < data.cssNodes.length; i++) {
			var tableEntry = '<li><h2>' + data.cssNodes[i].identity + '</h2>' + 
							 '<p>' + status[data.cssNodes[i].status] + '</p>' + 
							 '<p>' + type[data.cssNodes[i].type] + '</p>' +
							 '</li>';
			$('ul#cssNodesList').append(tableEntry);
		}
		$('#cssNodesList').listview('refresh');
		
		//SET SLIDER WIDTH TO BE LENGTH OF 'Individual'
		$('.ui-slider-switch').width(120);
	},
	
	/**
	 * @methodOf SocietiesCSSRecord#
	 * @description Update the user's profile information
	 * @returns null
	 */
	modifyCSSProfile: function(){
		console.log("calling modifyCSSProfile");
		
		var modifiedData = {
				"emailID": jQuery("#cssrecordemaildetails").val(),
				"entity": jQuery("#cssrecordidentity").val(),
				"foreName": jQuery("#cssrecordforename").val(),
				"cssIdentity": jQuery("#cssrecordidentity").val(),
				"name": jQuery("#cssrecordname").val(),
				"sex": jQuery("#cssrecordsextype").val(),
				"entity": jQuery("#cssrecordorgtype").val(),
				"position": jQuery("#cssrecordposition").val(),
				"workplace": jQuery("#cssrecordworkplace").val(),
				"homeLocation": jQuery("#cssrecorduserlocation").val()
		};
		
		
		function success(data) {
			SocietiesCSSRecord.populateCSSRecordpage(data);
		}
		
		function failure(data) {
			alert("modifyCSSProfile - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.modifyAndroidCSSRecord(success, failure, modifiedData);
	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */
$(document).on('pageinit', '#my-profile', function(event) {

	console.log("jQuery pageinit action(s) for refreshcssrecord");

	$('a#btnProfileCancel').off('click').on('click', function(){
		$.mobile.changePage($("#landing"), {transition: "fade"});
	});
	
	$('#btnProfileSave').off('click').on('click', function(e){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesCSSRecord.modifyCSSProfile);
		$.mobile.changePage($("#landing"), {transition: "fade"});
	});
	
	$('#updateProfile').off('click').on('click', function(){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesCSSRecord.refreshCssProfile);
	});

});
