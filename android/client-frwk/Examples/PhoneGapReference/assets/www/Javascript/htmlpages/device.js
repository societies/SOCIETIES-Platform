
var deviceInfo = function() {
	console.log("Get device information");
	
	jQuery("#phoneGapVer").text(device.cordova);
	jQuery("#platform").text(device.platform);
	jQuery("#version").text(device.version);
	jQuery("#uuid").text(device.uuid);
	jQuery("#name").text(device.name);
	jQuery("#width").text(screen.width);
	jQuery("#height").text(screen.height);
	jQuery("#colorDepth").text(screen.colorDepth);
	jQuery("#pixelDepth").text(screen.pixelDepth);
	jQuery("#browserAgent").text(navigator.userAgent);

};



jQuery(function() {
	console.log("device jQuery calls");


	$('#deviceChar').click(function() {
		deviceInfo();
	});


});


