
/**
 * Societies Android app SocietiesCSSRecord function(s) namespace
 * 
 * @namespace SocietiesCSSRecord
 */


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
		
		window.plugins.SocietiesLocalCSSManagerService.readProfile(success, failure);
		
	},
	/**
	 * @methodOf SocietiesCSSRecord#
	 * @description Populate the CSS Record page
	 * @returns null
	 */
	populateCSSRecordpage: function(data) {
		var status = ["Available for Use", "Unavailable", "Not active but on alert"];
		var type = ["Android based client", "Cloud Node", "JVM based client"];
		
		jQuery("#cssrecordforename").val(data.foreName);
		jQuery("#cssrecordname").val(data.name);
		jQuery("#cssrecordemaildetails").val(data.emailID);
		jQuery("#cssrecordimdetails").val(data.imID);
		jQuery("#cssrecorduserlocation").val(data.homeLocation);
		jQuery("#cssrecordsnsdetails").val(data.socialURI);
		jQuery("#cssrecordidentity").val(data.cssIdentity);
		jQuery("#cssrecordorgtype").val(data.entity);
		jQuery("#cssrecordsextype").val(data.sex);
		
		//empty table
		jQuery('#cssNodesTable tbody').remove();
		
		for (i  = 0; i < data.cssNodes.length; i++) {
			var tableEntry = "<tr>" + 
			"<td>" + data.cssNodes[i].identity + "</td>" + 
			"<td>" + status[data.cssNodes[i].status] + "</td>" + 
			"<td>" + type[data.cssNodes[i].type] + "</td>" + 
				+ "</tr>"

			jQuery('#cssNodesTable').append(tableEntry);
		}

	}

}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("CSS record jQuery calls");


	$('#refreshCssRecord').click(function() {
		SocietiesCSSRecord.refreshCssProfile();
	});


});