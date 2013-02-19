/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


$(document).on("pagechange", function(){


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
	$(document).on("click", $(".ui-page-active span[class='ui-btn-inner']"), function(event) {
		console.log("slide menu click");
		if (event.target.className === "ui-btn-inner") {
			event.stopImmediatePropagation();
			slidemenu(sm);
		}
	});
//	$(document).on("click", "a:not(:jqmData(slidemenu))", function(e) {
//		only_close = true;
//		slidemenu(sm, only_close);
//	});

	$(window).on('resize', function(){

		if ($("#slidemenu").data('slideopen')) {

			var sm = $($("#slidemenu").data('slidemenu'));
			var w = '240px';

			$("#slidemenu").css('width', w);
			$("#slidemenu").height(viewport().height);

			$("div[data-role='page'] [class*='ui-page-active']").css('left', w);
		}

	});

});

function slidemenu(sm, only_close) {
	console.log("Current page: " + $.mobile.activePage[0].id);

	sm.height(viewport().height);
	console.log($(this));
	if (!$(this).data('slideopen') && !only_close) {

		$("#slidemenu").show();
		var w = '240px';
		$("#slidemenu").animate({width: w, avoidTransforms: false, useTranslate3d: true}, 'fast');
		$("div[data-role='page'] [class*='ui-page-active']").css('left', w);
		$(this).data('slideopen', true);

		if ($(":jqmData(role='header')").data('position') == 'fixed') {
			$(":jqmData(slidemenu)").css('margin-left', parseInt(w.split('px')[0]) + 10 + 'px');
		} else {
			$("#slidemenu").css('margin-left', '10px');
		}

	} else {
		var w = '0px';
		$("#slidemenu").animate({width: w, avoidTransforms: false, useTranslate3d: true}, 'fast', function(){sm.hide()});
		$("div[data-role='page'] [class*='ui-page-active']").css('left', w);
		$(this).data('slideopen', false);
		$("#slidemenu").css('margin-left', '0px');
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
