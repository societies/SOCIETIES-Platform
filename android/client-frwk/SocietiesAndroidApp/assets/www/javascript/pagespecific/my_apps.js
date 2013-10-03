/**
 * Societies Android app Societies3PServices function(s) namespace
 * 
 * @namespace Societies3PServices
 */

//Browser globals
/*global clearInterval: false, clearTimeout: false, document: false, event: false, frames: false, history: false, Image: false, location: false, name: false, navigator: false, Option: false, parent: false, screen: false, setInterval: false, setTimeout: false, window: false, XMLHttpRequest: false */
//Miscellaneous globals
/*global alert: false, confirm: false, console: false, Debug: false, opera: false, prompt: false, WSH: false, $: false, jQuery: false */
//JQuery globals
/*global $: false, jQuery: false */
//Specific globals
/*global SocietiesCoreServiceMonitorHelper: false, ServiceManagementServiceHelper: false */

var Societies3PServices = {

	mServices: {},
	mServiceObj: {},  
	
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh the 3P Service page with currently active services
	 * @returns null
	 */
	refresh3PServices: function() {
		console.log("Refreshing 3P Services");

		function success(data) {
			mServices = data;
			
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#SocietiesServicesDiv').children().length >1 )
				$('ul#SocietiesServicesDiv li:last').remove();

			//DISPLAY SERVICES
			if (data.length==0) {
				var tableEntry = '<li><a href="#"><h2>No services installed</h2></a></li>';
				$('ul#SocietiesServicesDiv').append(tableEntry);
			} else {
				for (i  = 0; i < data.length; i++) {
					var tableEntry = '<li><a href="#" onclick="Societies3PServices.showDetails(' + i + ')"><img src="images/printer_icon.png" class="profile_list" alt="logo" >' +
						'<h2>' + data[i].serviceName + '</h2>' + 
						'<p>' + data[i].serviceDescription + '</p>' + 
						'</a></li>';
					/*
					$('ul#SocietiesServicesDiv').append(
							$('<li>').append(
									$('<a>').attr('href','#appdetails').append(
											$('<img>').attr('src', '../images/printer_icon.png').append(data.serviceName) )));     
					*/
					jQuery('ul#SocietiesServicesDiv').append(tableEntry);
				}
			}
			$('#SocietiesServicesDiv').listview('refresh');
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.ServiceManagementService.getMyServices(success, failure);
	},
	
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh the 3P Service page with currently active services
	 * @returns null
	 */
	refreshLocalApps: function() {
		console.log("Refreshing Local Apps");

		function success(data) {			
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#LocalServicesDiv').children().length >1 )
				$('ul#LocalServicesDiv li:last').remove();

			//DISPLAY SERVICES
			if (data.length==0) {
				var tableEntry = '<li><a href="#"><h2>No apps installed</h2></a></li>';
				$('ul#LocalServicesDiv').append(tableEntry);
			} else {
				for (i  = 0; i < data.length; i++) {
					var tableEntry = '<li><a href="#" data-rel="dialog" onclick="Societies3PServices.startActivity(\'' + data[i].applicationName + '\', \'' + data[i].packageName + '\')">' +
										'<img src="' + data[i].icon + '" class="profile_list" alt="logo" >' + 
										'<h2>' + data[i].applicationName + '</h2>' + 
										'<p>' + data[i].packageName + '</p></a>' +  
										//'<a href="#" data-rel="dialog" data-transition="fade" onclick="Societies3PServices.startActivity(\'' + data[i].applicationName + '\', \'' + data[i].packageName + '\')">Launch</a>' +
										'</li>';
					jQuery('ul#LocalServicesDiv').append(tableEntry);
				}
			}
			$('#LocalServicesDiv').listview('refresh');
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.SocietiesCoreServiceMonitor.getInstalledApplications(success, failure);
	},
	
	startActivity: function (appName, packageName) {
		function success(data) {
			window.plugins.SocietiesCoreServiceMonitor.startActivity(packageName, failure);
		}
		
		function failure(data) {
			window.alert("Failed to start application: " + data);
		}
		
		if(window.confirm("Launch " + appName + "?"))
			SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(success, failure);
	},
	
	startStopService: function () {
		function success(data) {
			if (status == "STARTED") {
				//$('input#start_stop').val("SStart Service");
				$("input#start_stop").prop('value', 'Start Service'); 
				$('p#service_status').html("STOPPED");
			}
			else {
				//$('input#start_stop').val("SStop Service");
				$("input#start_stop").prop('value', 'Stop Service');
				$('p#service_status').html("STARTED");
			}
			$('input#start_stop').button('refresh');
		}
		
		function failure(data) {
			$('p#service_status').html( status + "Error occurred: " + data);
		}
		
		var status = $('p#service_status').html();
		if (status == "STARTED") {
			$('p#service_status').html("Stopping...");
			window.plugins.ServiceManagementService.stopService(mServiceObj.serviceIdentifier, success, failure);
		}
		else {
			$('p#service_status').html("Starting...");
			window.plugins.ServiceManagementService.startService(mServiceObj.serviceIdentifier, success, failure);
		}
	},
	
	showDetails: function (servicePos) {
		// GET SERVICE FROM ARRAY AT POSITION
		mServiceObj = mServices[ servicePos ];
		if ( mServiceObj ) {
			//VALID SERVICE OBJECT
			var markup = "<h1>" + mServiceObj.serviceName + "</h1>" + 
						 "<p>" + mServiceObj.serviceDescription + "</p>" +
						 "<p>" + mServiceObj.serviceInstance.serviceImpl.serviceProvider + "</p><br />"; 
			//INJECT
			$('#app_detail').html( markup );
			//SERVICE STATUS
			var status = mServiceObj.serviceStatus;
			$('p#service_status').html( status );
			//BUTTON TEXT
			if (status == "STARTED")
				$('input#start_stop').val("Stop Service");
			else
				$('input#start_stop').val("Start Service");
			$('input#start_stop').button('refresh');
			
			// Populate Trust Level
			var sriString = jTrustLevel.encodeServiceResourceIdentifier(mServiceObj.serviceIdentifier);
			jTrustLevel.initRating('#service-trust-level', 'SVC', sriString);
			jTrustLevel.showDetails('#service-trust-level', 'SVC', sriString);
			
			try {//REFRESH FORMATTING
				//ERRORS THE FIRST TIME AS YOU CANNOT refresh() A LISTVIEW IF NOT INITIALISED
				$('ul#app_details').listview('refresh');
			}
			catch(err) {}
			$.mobile.changePage($("#my_apps_details"), { transition: "fade"} );
		}
	}
};

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */
$(document).on('pageinit', '#my_apps', function(event) {

	$("input#start_stop").off('click').on('click', function(e){
		ServiceManagementServiceHelper.connectToServiceManagement(Societies3PServices.startStopService);
	});
	
});
