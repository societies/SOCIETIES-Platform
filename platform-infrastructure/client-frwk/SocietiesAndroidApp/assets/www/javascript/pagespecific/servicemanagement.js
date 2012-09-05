
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
			//jQuery('#SocietiesServicesDiv tbody').remove();
			//SERVICE
			for (i  = 0; i < data.length; i++) {
				var tableEntry = "<li><a href=""#appdetails""><img src=""../images/printer_icon.png"" class=""profile_list"" alt=""logo"" />" +
				"<h2>" + data[i].serviceName + "</h2>" + 
				"<h3>" + data[i].serviceDescription + "</h3>" + 
				"<br/></a></li>";
				jQuery('#SocietiesServicesDiv').add(tableEntry);
			}
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.SocietiesCoreServiceMonitor.getServices(success, failure);
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

	$(document).ready(function() {
		SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(Societies3PServices.refresh3PServices);;
	}); 
	
	$('#List3PServices').click(function() {
		SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(Societies3PServices.refresh3PServices);;
	});
});