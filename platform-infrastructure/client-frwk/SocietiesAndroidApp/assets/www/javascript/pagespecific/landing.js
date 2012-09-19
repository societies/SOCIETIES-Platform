

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */
$(document).bind('pageinit',function(){
	console.log("pageinit: Landing jQuery calls");

	$("a#friends_anchor").off('click').on('click', function(){
		$.mobile.changePage("friends_landing.html", { transition: "fade"} );
	});
	
	$("a#communities_anchor").off('click').on('click', function(){
		$.mobile.changePage("communities_landing.html", { transition: "fade"} );
	});
	
	$("a#myapps_anchor").off('click').on('click', function(e){
		//e.preventDefault(); 
		
		ServiceManagementServiceHelper.connectToServiceManagement(Societies3PServices.refresh3PServices);
		SocietiesCoreServiceMonitorHelper.connectToCoreServiceMonitor(Societies3PServices.refreshLocalApps);
		//GOTO NEWLY POPULATED PAGE
		$.mobile.changePage($("#my_apps"), { transition: "fade"} );
	});
	
});
