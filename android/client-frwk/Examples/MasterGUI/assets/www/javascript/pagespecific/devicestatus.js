
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
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("Device Status jQuery calls");

	$('#connectivity').click(function() {
		window.plugins.SocietiesDeviceStatus.getConnectivityStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});
	
	$('#location').click(function() {
		window.plugins.SocietiesDeviceStatus.getLocationStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});
	
	$('#battery').click(function() {
		window.plugins.SocietiesDeviceStatus.getBatteryStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});
	$('#registerBattery').click(function() {
		window.plugins.SocietiesDeviceStatus.registerToBatteryStatus(SocietiesDeviceStatus.onSuccess, SocietiesDeviceStatus.onFailure);
	});
});