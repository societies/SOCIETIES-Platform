var registerEvents = function() {
	console.log("Register for background/foreground events");

	function onPause() {
		alert("application put into background");
	}
	function onResume() {
		alert("application put into foreground");
	}

	document.addEventListener("pause", onPause(), false);
	document.addEventListener("resume", onResume(), false);
};

jQuery(function() {
	console.log("events jQuery calls");


	$('#registerEvents').click(function() {
		registerEvents();
	});

})