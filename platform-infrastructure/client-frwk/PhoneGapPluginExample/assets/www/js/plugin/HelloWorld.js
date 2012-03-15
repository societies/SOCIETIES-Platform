/**
 *  
 * @return Object literal singleton instance of DirectoryListing
 */
var HelloWorldJS = function() {
};

/**
 * @param directory The directory for which we want the listing
 * @param successCallback The callback which will be called when directory listing is successful
 * @param failureCallback The callback which will be called when directory listing encouters an error
 */
HelloWorldJS.prototype.sayHello = function(name, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,	//Success callback from the plugin
			failureCallback,	//Error callback from the plugin
			'HelloWorldPlugin',	//Tell PhoneGap to run "DirectoryListingPlugin" Plugin
			'sayHello',			//Tell plugin, which action we want to perform
			[name]);			//Passing list of args to the plugin

};
HelloWorldJS.prototype.sayHelloList = function(names, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,	//Success callback from the plugin
			failureCallback,	//Error callback from the plugin
			'HelloWorldPlugin',	//Tell PhoneGap to run "DirectoryListingPlugin" Plugin
			'sayHelloList',			//Tell plugin, which action we want to perform
			[names]);			//Passing list of args to the plugin
	
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("HelloWorld", new HelloWorldJS());
});