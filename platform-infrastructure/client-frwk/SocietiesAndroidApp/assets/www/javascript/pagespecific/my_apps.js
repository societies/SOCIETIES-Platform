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
			
			//EMPTY TABLE
			$('ul#SocietiesServicesDiv li:last').remove();
			//DISPLAY SERVICES
			for (i  = 0; i < data.length; i++) {
				var tableEntry = '<li><a href="#category-item?pos=' + i + '"><img src="../images/printer_icon.png" class="profile_list" alt="logo" >' +
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
		
		window.plugins.ServiceManagementService.getServices(success, failure);
	},

	// Load the data for a specific category, based on
	// the URL passed in. Generate markup for the items in the
	// category, inject it into an embedded page, and then make
	// that page the current active page.
	//   #category-items?category=animals
	//<div id="category-items" data-role="page">
	//  <div data-role="header"><h1></h1></div>
	//  <div data-role="content"></div>
	//</div>
	showCategory: function( urlObj, options ) {
		var servicePos = urlObj.hash.replace( /.*pos=/, "" ),
			// GET SERVICE FROM ARRAY AT POSITION
			serviceObj = mServices[ servicePos ],
			// GET PAGE IN DOM FROM URL (before the '?')
			pageSelector = urlObj.hash.replace( /\?.*$/, "" );

		if ( serviceObj ) {
			// Get the page we are going to dump our content into.
			var $page = $( pageSelector ),

				// SERVICE DETAILS
				markup = "<h1>" + serviceObj.serviceName + "</h1>" + 
						 "<p>" + serviceObj.serviceDescription + "</p>" +
						 "<p>" + serviceObj.serviceInstance.serviceImpl.serviceProvider + "</p>" + 
						 "<p>" + serviceObj.serviceStatus + "</p>";
			//INJECT
			$('#app_detail').html( markup );
			try {//REFRESH FORMATTING
				//ERRORS THE FIRST TIME AS YOU CANNOT refresh() A LISTVIEW IF NOT INITIALISED
				$('ul#app_details').listview('refresh');
			}
			catch(err) {}
			// Pages are lazily enhanced. We call page() on the page
			// element to make sure it is always enhanced before we
			// attempt to enhance the listview markup we just injected.
			// Subsequent calls to page() are ignored since a page/widget
			// can only be enhanced once.
			//$page.page();

			// Now call changePage() and tell it to switch to the page we just modified.
			$.mobile.changePage( $page, options );
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
	console.log("Active Services jQuery calls");

	//$(document).ready(function() {
	//	SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(Societies3PServices.refresh3PServices);
	//}); 
	
	$('#List3PServices').click(function() {
		ServiceManagementServiceHelper.connectToServiceManagement(Societies3PServices.refresh3PServices);
	});
	
	setTimeout(function(){
		ServiceManagementServiceHelper.connectToServiceManagement(Societies3PServices.refresh3PServices);
    }, 500);
	
	//Listen for any attempts to call changePage().
	$(document).bind( "pagebeforechange", function( e, data ) {
	
		// We only want to handle changePage() calls where the caller is
		// asking us to load a page by URL.
		if ( typeof data.toPage === "string" ) {
	
			// We are being asked to load a page by URL, but we only
			// want to handle URLs that request the data for a specific
			// category.
			var u = $.mobile.path.parseUrl( data.toPage ),
				re = /^#category-item/;
	
			if ( u.hash.search(re) !== -1 ) {
	
				// We're being asked to display the items for a specific category.
				// Call our internal method that builds the content for the category
				// on the fly based on our in-memory category data structure.
				Societies3PServices.showCategory( u, data.options );
	
				// Make sure to tell changePage() we've handled this call so it doesn't
				// have to do anything.
				e.preventDefault();
			}
		}
	});
	
});
