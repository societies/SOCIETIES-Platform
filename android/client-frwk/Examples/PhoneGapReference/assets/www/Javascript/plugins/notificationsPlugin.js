/**
 *  
 * @return Instance of ConnectionListener
 */
var ConnectionListener = function() { 
}

/**
 * @param directory The directory for which we want the listing
 * @param successCallback The callback which will be called when directory listing is successful
 * @param failureCallback The callback which will be called when directory listing encouters an error
 */
ConnectionListener.prototype.createListener = function(successCallback, failureCallback) {
 
	console.log("Create Connection Listener");

	return cordova.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'ConnectionPlugin',  //Telling PhoneGap that we want to run specified plugin
	'createListener',              //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};
