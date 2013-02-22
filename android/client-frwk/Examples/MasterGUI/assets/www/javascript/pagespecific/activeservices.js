
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
		
	}

}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("Active Services jQuery calls");

	$('#refreshServices').click(function() {
		SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(SocietiesActiveServices.refreshActiveServices);
	});

});