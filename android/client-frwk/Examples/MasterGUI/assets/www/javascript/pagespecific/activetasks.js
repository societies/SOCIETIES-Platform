
/**
 * Societies Android app SocietiesActiveTasks function(s) namespace
 * 
 * @namespace SocietiesActiveTasks
 */


var SocietiesActiveTasks = {
	/**
	 * @methodOf SocietiesActiveTasks#
	 * @description Refresh the Active Apps page with the current active apps
	 * @returns null
	 */

	refreshActiveTasks: function() {
		console.log("Refresh Active Tasks");

		function success(data) {
			//empty table
			jQuery('#activeTasksTable tbody').remove();

			//add rows
			for (i  = 0; i < data.length; i++) {
				var tableEntry = "<tr>" + 
				"<td>" + data[i].className + "</td>" + 
				"<td>" + data[i].numRunningActivities + "</td>" + 
					+ "</tr>"

				jQuery('#activeTasksTable').append(tableEntry);
			}
		}
		
		function failure(data) {
			alert("refreshActiveTasks - failure: " + data);
		}
		
		window.plugins.SocietiesCoreServiceMonitor.activeTasks(success, failure);
	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("Active Tasks jQuery calls");

	$('#refreshTasks').click(function() {
		SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(SocietiesActiveTasks.refreshActiveTasks);
	});

	

});