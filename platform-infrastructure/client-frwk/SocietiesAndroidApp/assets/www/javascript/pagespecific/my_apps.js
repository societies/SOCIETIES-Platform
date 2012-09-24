/**
 * Societies Android app Societies3PServices function(s) namespace
 * 
 * @namespace Societies3PServices
 */
var Societies3PServices = {

	mServices: {},
	
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
			for (i  = 0; i < data.length; i++) {
				var tableEntry = '<li><a href="#" data-rel="dialog" onclick="Societies3PServices.startActivity(\'' + data[i].applicationName + '\', \'' + data[i].packageName + '\')">' +
									'<img src="' + data[i].icon + '" class="profile_list" alt="logo" >' + 
									'<h2>' + data[i].applicationName + '</h2>' + 
									'<p>' + data[i].packageName + '</p></a>' +  
									//'<a href="#" data-rel="dialog" data-transition="fade" onclick="Societies3PServices.startActivity(\'' + data[i].applicationName + '\', \'' + data[i].packageName + '\')">Launch</a>' +
									'</li>';
				jQuery('ul#LocalServicesDiv').append(tableEntry);
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
			window.alert("Failed to start application!");
		}
		
		if(window.confirm("Launch " + appName + "?"))
			SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(success);
	},
	
	startStopService: function () {
		Societies3PServices.startStopService();
		$('#service_status').html( status );
		//BUTTON TEXT
		if (status == "started")
			$('#start_stop').value="Stop";
		else
			$('#start_stop').value="Start";
	},
	
	showDetails: function (servicePos) {
		// GET SERVICE FROM ARRAY AT POSITION
		var serviceObj = mServices[ servicePos ];
		if ( serviceObj ) {
			//VALID SERVICE OBJECT
			var markup = "<h1>" + serviceObj.serviceName + "</h1>" + 
						 "<p>" + serviceObj.serviceDescription + "</p>" +
						 "<p>" + serviceObj.serviceInstance.serviceImpl.serviceProvider + "</p><br />"; 
			//INJECT
			$('#app_detail').html( markup );
			//SERVICE STATUS
			var status = serviceObj.serviceStatus;
			$('#service_status').html( status );
			//BUTTON TEXT
			if (status == "started")
				$('#start_stop').value="Stop";
			else
				$('#start_stop').value="Start";
						
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
$(document).bind('pageinit',function(){

	$("input#start_stop").off('click').on('click', function(e){
		ServiceManagementServiceHelper.connectToServiceManagement(Societies3PServices.refresh3PServices);
	});
	
});
