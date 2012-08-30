/**
 * Societies Android app SocietiesDeviceInfo function(s) namespace
 * 
 * @namespace SocietiesDeviceInfo
 */


var SocietiesDeviceInfo = {
	/**
	 * @methodOf SocietiesDeviceInfo#
	 * @description Populate Device Info page HTML elements with device information
	 * @returns null
	 */
	deviceInfo: function() {
		console.log("Get device information");
		
		jQuery("#phoneGapVer").text(device.cordova);
		jQuery("#platform").text(device.platform);
		jQuery("#version").text(device.version);
		jQuery("#uuid").text(device.uuid);
		jQuery("#name").text(device.name);
		jQuery("#width").text(screen.width);
		jQuery("#height").text(screen.height);
		jQuery("#colorDepth").text(screen.colorDepth);
		jQuery("#pixelDepth").text(screen.pixelDepth);
		jQuery("#browserAgent").text(navigator.userAgent);

	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("Device Info jQuery calls");
	
	$('#deviceChar').click(function() {
		SocietiesDeviceInfo.deviceInfo();
	});
});