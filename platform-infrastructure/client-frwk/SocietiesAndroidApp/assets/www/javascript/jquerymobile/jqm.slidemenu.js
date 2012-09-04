$(document).on("pageinit",$("div[data-role='page'] [class*='ui-page-active']"), function(){

	console.log("slide menu page init");

	$(":jqmData(slidemenu)").addClass('slidemenu_btn');
	var sm = $($(":jqmData(slidemenu)").data('slidemenu'));
	sm.addClass('slidemenu');

	$(document).on("swipeleft",$("div[data-role='page'] [class*='ui-page-active']"), function(){
		console.log("slide menu swipe left");
		slidemenu(sm);
	});
	$(document).on("swiperight",$("div[data-role='page'] [class*='ui-page-active']"), function(){
		console.log("slide menu swipe right");
		slidemenu(sm);
	});
	$(document).on("click", ":jqmData(slidemenu)", function(event) {
		console.log("slide menu click");
		event.stopImmediatePropagation();
		slidemenu(sm);
	});
	$(document).on("click", "a:not(:jqmData(slidemenu))", function(e) {
		only_close = true;
		slidemenu(sm, only_close);
	});

	$(window).on('resize', function(){

		if ($(":jqmData(slidemenu)").data('slideopen')) {

			var sm = $($(":jqmData(slidemenu)").data('slidemenu'));
			var w = '240px';

			sm.css('width', w);
			sm.height(viewport().height);

			$("div[data-role='page'] [class*='ui-page-active']").css('left', w);
		}

	});

});

function slidemenu(sm, only_close) {
	console.log("Current page: " + $.mobile.activePage[0].id);

	sm.height(viewport().height);

	if (!$(this).data('slideopen') && !only_close) {

		sm.show();
		var w = '240px';
		sm.animate({width: w, avoidTransforms: false, useTranslate3d: true}, 'fast');
		$("div[data-role='page'] [class*='ui-page-active']").css('left', w);
		$(this).data('slideopen', true);

		if ($(":jqmData(role='header')").data('position') == 'fixed') {
			$(":jqmData(slidemenu)").css('margin-left', parseInt(w.split('px')[0]) + 10 + 'px');
		} else {
			$(":jqmData(slidemenu)").css('margin-left', '10px');
		}

	} else {
		var w = '0px';
		sm.animate({width: w, avoidTransforms: false, useTranslate3d: true}, 'fast', function(){sm.hide()});
		$("div[data-role='page'] [class*='ui-page-active']").css('left', w);
		$(this).data('slideopen', false);
		$(":jqmData(slidemenu)").css('margin-left', '0px');
	}
}

function viewport(){
	var e = window;
	var a = 'inner';
	if (!('innerWidth' in window)) {
		a = 'client';
		e = document.documentElement || document.body;
	}
	return { width : e[ a+'Width' ] , height : e[ a+'Height' ] }
}