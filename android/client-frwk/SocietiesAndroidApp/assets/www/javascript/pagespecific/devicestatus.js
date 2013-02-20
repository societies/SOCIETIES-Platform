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
 * Societies Android app SocietiesDeviceStatus function(s) namespace
 * 
 * @namespace SocietiesDeviceStatus
 */


var SocietiesDeviceStatus = {
	/**
	 * @methodOf SocietiesDeviceStatus#
	 * @description Success action 
	 * @param {Object} data
	 * @returns null
	 */

	onSuccess: function(data) {
		console.log("JS Success");
		console.log(JSON.stringify(data));
		$('.result').remove();
		$('.error').remove();
		$('<span>').addClass('result').html("Result: "+JSON.stringify(data)).appendTo('#main article[data-role=content]');
	},
	
	/**
	 * @methodOf SocietiesDeviceStatus#
	 * @description Failure action 
	 * @param {Object} error
	 * @returns null
	 */
	onFailure: function(e) {
		console.log("JS Error");
		console.log(e);
		$('.result').remove();
		$('.error').remove();
		$('<span>').addClass('error').html(e).appendTo('#main article[data-role=content]');
	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions and/or event handlers to various HTML tags using JQuery on pageinit
 * N.B. this event is fired once per page load
 * @returns null
 */
$(document).on('pageinit', '#???', function(event) {

	console.log("jQuery pageinit action(s) for device status");
	$('#connectivity').off('click').on('click', function(){
		window.plugins.SocietiesDeviceStatus.getConnectivityStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});
	
	$('#location').off('click').on('click', function(){
		window.plugins.SocietiesDeviceStatus.getLocationStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});
	
	$('#battery').off('click').on('click', function(){
		window.plugins.SocietiesDeviceStatus.getBatteryStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});
	$('#registerBattery').off('click').on('click', function(){
		window.plugins.SocietiesDeviceStatus.registerToBatteryStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});

});

