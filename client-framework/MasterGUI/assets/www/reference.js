function onDeviceReady() {
	console.log("PhoneGap Loaded, Device Ready");
	
	/**
	 * Register any PhoneGap plugins here. Example shown for illustration
	 
	PhoneGap.addConstructor(function() {
		//Register the javascript plugin with PhoneGap
		console.log("Register Connection Listener plugin ");
		PhoneGap.addPlugin('ConnectionListener', new ConnectionListener());
	 
	});
	*/
}

/**
 * Example of a PhoneGap plugin  being created and configured 
 * @return Instance of ConnectionListener
 
var ConnectionListener = function() { 
}
*/

/**
 * @param directory The directory for which we want the listing
 * @param successCallback The callback which will be called when directory listing is successful
 * @param failureCallback The callback which will be called when directory listing encouters an error
 
ConnectionListener.prototype.createListener = function(successCallback, failureCallback) {
 
	console.log("Create Connection Listener");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'ConnectionPlugin',  //Telling PhoneGap that we want to run specified plugin
	'createListener',              //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};
*/

var deviceInfo = function() {
	console.log("Get device information");
	
	jQuery("#phoneGapVer").text(device.phonegap);
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

/**
 * Add Javascript functions to various HTML tags using JQuery
 */

jQuery(function() {
	console.log("jQuery calls");

	document.addEventListener("deviceready", onDeviceReady, false);
	
	$('#deviceChar').click(function() {
		deviceInfo();
	});


});


