
/**
 * Societies Android app SocietiesDeviceManager function(s) namespace
 * 
 * @namespace SocietiesDeviceManager
 */


var SocietiesDeviceManager = {
	/**
	 * @methodOf SocietiesDeviceManager#
	 * @description Reset the Device Manager page HTML elements 
	 * @returns null
	 */

	resetDeviceMgr: function(){
	    jQuery("#connStatuslist").text("");
	    jQuery("#battStatuslist").text("");
	    jQuery("#locStatuslist").text("");
	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("Device Manager jQuery calls");

	$('#resetDeviceManager').click(function() {
		SocietiesDeviceManager.resetDeviceMgr();
	});
});