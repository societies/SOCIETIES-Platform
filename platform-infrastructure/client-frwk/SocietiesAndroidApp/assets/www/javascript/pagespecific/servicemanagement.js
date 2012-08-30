
/**
 * Societies Android app Societies3PServices function(s) namespace
 * 
 * @namespace Societies3PServices
 */
var Societies3PServices = {
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh the 3P Service page with currently active services
	 * @returns null
	 */

	refresh3PServices: function() {
		console.log("Refreshing 3P Services");

		function success(data) {
			//empty table
			jQuery('#societiesServicesTable tbody').remove();
			//SERVICE
			for (i  = 0; i < data.length; i++) {
				var tableEntry = "<tr>" + 
				"<td>" + data[i].serviceName + "</td>" + 
				"<td>" + data[i].serviceDescription + "</td>" + 
				</tr>"
				jQuery('#societiesServicesTable').append(tableEntry);
			}
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.CoreServiceMonitorService.getServices(success, failure);
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

	$('#List3PServices').click(function() {
		SocietiesGUI.connectToCoreServiceMonitor(Societies3PServices.refresh3PServices);;
	});
});