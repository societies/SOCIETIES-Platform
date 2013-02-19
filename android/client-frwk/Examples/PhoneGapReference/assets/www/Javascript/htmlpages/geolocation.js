var geoWatchID = null;

var trackLocation = function () {
	console.log("Track location");

	// Update every 3 seconds
    var options = { enableHighAccuracy : true, frequency: 10000 };

    function onSuccess(position) {
    	/*alert("Latitude: " + position.coords.latitude + "\n" + 
    		   "Longitude: " + 	position.coords.longitude);*/    	
		jQuery("#latitude").text(position.coords.latitude);
		jQuery("#longitude").text(position.coords.longitude);
    }

    function onError(error) {
        alert('code: '    + error.code    + '\n' +
              'message: ' + error.message + '\n');
    }
    
    geoWatchID = navigator.geolocation.watchPosition(onSuccess, onError, options);

};

var clearGeoWatch = function () {
	console.log("Stop tracking location");

	if (geoWatchID != null) {
	    navigator.geolocation.clearWatch(watchID);
	    geoWatchID = null;
	}
}

var getLocation = function() {
	console.log("Get current location");

	// onSuccess Callback
	//   This method accepts a `Position` object, which contains
	//   the current GPS coordinates
	//
	var onSuccess = function(position) {
		/*alert('Latitude: ' + position.coords.latitude + '\n' + 'Longitude: '
				+ position.coords.longitude + '\n' + 'Altitude: '
				+ position.coords.altitude + '\n' + 'Accuracy: '
				+ position.coords.accuracy + '\n' + 'Altitude Accuracy: '
				+ position.coords.altitudeAccuracy + '\n' + 'Heading: '
				+ position.coords.heading + '\n' + 'Speed: '
				+ position.coords.speed + '\n' + 'Timestamp: '
				+ new Date(position.timestamp) + '\n');*/
		
		jQuery("#latitude").text(position.coords.latitude);
		jQuery("#longitude").text(position.coords.longitude);

	};

	// onError Callback receives a PositionError object
	//
	function onError(error) {
		var message = 'code: ' + error.code + '\n' + 'message: ' + error.message + '\n';
		alert(message);
		console.log(message);
	}

	console.log('getLocation called and obtaining GPS co-ords');
	navigator.geolocation.getCurrentPosition(onSuccess, onError, {enableHighAccuracy : true});
};



jQuery(function() {
	console.log("geolocation jQuery calls");
	
	$('#geoLocation').click(function() {
		getLocation();
	});

	$('#geoLocWatchStop').click(function() {
		clearGeoWatch();
	});

	
	
});
