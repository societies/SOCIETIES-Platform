var compassInfo = function() {
	console.log("Get compass information");

	// onSuccess: Get the current heading
	//
	function onSuccess(heading) {
		alert('Current compass heading: ' + heading);
		//		jQuery("#compassHeading").text();    
	}

	// onError: Failed to get the heading
	//
	function onError() {
		alert('Failed to get current compass heading!');
	}
	console.log("Get Compass Heading");
	navigator.compass.getCurrentHeading(onSuccess, onError);

};

jQuery(function() {
	console.log("compass jQuery calls");

	$('#compassReading').click(function() {
		compassInfo();
	});
});

