
//Alter JQueryMobile default behaviour

$(document).bind("mobileinit", function(){
	//force caching of loaded pages
	$.mobile.page.prototype.options.domCache = true;
});
