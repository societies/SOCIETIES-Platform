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


function onDeviceReady() {
	console.log("PhoneGap Loaded, Device Ready");
	
	//Register any PhoneGap plugins here. Example shown for illustration
	 
	PhoneGap.addConstructor(function() {
		//Register the javascript plugin with PhoneGap
		console.log("Register CoreServiceMonitorService plugin ");
		PhoneGap.addPlugin('CoreServiceMonitorService', new CoreServiceMonitorService());
	 
	});
}

/**
 * Example of a PhoneGap plugin  being created and configured 
 * @return Instance of ConnectionListener
 */
var CoreServiceMonitorService = function() { 
}

/**
 * @param directory The directory for which we want the listing
 * @param successCallback The callback which will be called when directory listing is successful
 * @param failureCallback The callback which will be called when directory listing encouters an error
*/
CoreServiceMonitorService.prototype.activeServices = function(successCallback, failureCallback) {
	var clientPackage = "org.societies.android.platform.gui";

	console.log("Call CoreServiceMonitorService - activeServices");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
	'activeServices',              //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};

var deviceInfo = function() {
	console.log("Get device information");
	
	jQuery("#phoneGapVer").text(device.phonegap);
	jQuery("#platform").text(device.platform);
	jQuery("#version").text(device.version);
	jQuery("#uuid").text(device.uuid);
	jQuery("#name").text(device.name);
	jQuery("#width").text(screen.width);
	jQuery("#height").text(screen.height);
	jQuery("#colorDepth").text(screen.colorDepth);
	jQuery("#pixelDepth").text(screen.pixelDepth);
	jQuery("#browserAgent").text(navigator.userAgent);

};

var successfulLogin = function() {
	$.mobile.changePage( ($("#menu")), { transition: "slideup"} );
};



var resetDeviceMgr = function(){
    jQuery("#connStatuslist").text("");
    jQuery("#battStatuslist").text("");
    jQuery("#locStatuslist").text("");
    
    
    
}

var refreshActiveServices = function() {
	console.log("Refresh the Active Service");

	function success(data) {
		alert(data[0].activeSince + " " + data[0].className);
		
	}
	
	function failure(data) {
		alert(data);
	}
    window.plugins.CoreServiceMonitorService.activeServices(success, failure);
	
};

/**
 * Add Javascript functions to various HTML tags using JQuery
 */

jQuery(function() {
	console.log("jQuery calls");

	document.addEventListener("deviceready", onDeviceReady, false);
	
	$('#deviceChar').click(function() {
		deviceInfo();
	});

	$('#connectXMPP').click(function() {
		successfulLogin();
	});
	
	$('#resetDeviceManager').click(function() {
		resetDeviceMgr();
	});

	$('#refreshServices').click(function() {
		refreshActiveServices();
	});

});


