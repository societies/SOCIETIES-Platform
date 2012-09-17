

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */
$(document).bind('pageinit',function(){
	console.log("pageinit: Landing jQuery calls");

	$("#friends_anchor").off('click').on('click', function(){
		$.mobile.changePage("friends_landing.html", { transition: "fade"} );
	});
	
	$("#communities_anchor").off('click').on('click', function(){
		$.mobile.changePage("communities_landing.html", { transition: "fade"} );
	});
	
	$("#myapps_anchor").off('click').on('click', function(e){
		window.alert("clicked");
		e.preventDefault(); 
		$.mobile.loadPage("my_apps_details.html");
		$.mobile.changePage("my_apps.html", { transition: "fade"} );
		
	});
	
});
