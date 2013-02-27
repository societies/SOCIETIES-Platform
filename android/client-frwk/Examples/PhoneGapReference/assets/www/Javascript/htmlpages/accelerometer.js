var watchID = null;

function updateAcceleration(a) {
	console.log("Display current acceleromter readings");

	jQuery("#accelX").text(a.x);
	jQuery("#accelY").text(a.y);
	jQuery("#accelZ").text(a.z);

}

var stopWatch = function(watchID) {
	console.log("Stop acceleromter readings");

	if (watchID !== null) {
		navigator.accelerometer.clearWatch(watchID);
		updateAcceleration({
			x : "",
			y : "",
			z : ""
		});
		watchID = null;
	}
};

var startWatch = function() {
	console.log("Start acceleromter readings");

	// Update acceleration every 3 seconds
	var options = {
		frequency : 3000
	};

	var id = navigator.accelerometer.watchAcceleration(updateAcceleration,
			function(ex) {
				console.log("accelerometer fail (" + ex.name + ": " + ex.message
						+ ")");
			}, options);

	return id;
};

var toggleAccel = function() {

	if (watchID !== null) {
		console.log("Toggle acceleromter off");
		stopWatch();
	} else {
		console.log("Toggle acceleromter on");
		watchID = startWatch();
	}
};


jQuery(function() {
	console.log("acceleromter jQuery calls");


	$('#toggleAcceleromter').click(function() {
		toggleAccel();
	});
});
