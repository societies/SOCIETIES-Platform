/*global ContactFindOptions,FileWriter,FileReader,LocalFileSystem,window,Connection,Camera,device,screen,document,navigator,$,onDeviceReady,jQuery,FileError */

// PhoneGap is loaded and it is now safe to make calls PhoneGap methods
//
function onDeviceReady() {
	console.log("PhoneGap Loaded, Device Ready");
	
	cordova.addConstructor(function() {
	//Register the javascript plugin with PhoneGap
		console.log("Register Connection Listener plugin ");
		cordova.addPlugin('ConnectionListener', new ConnectionListener());
	 
	//Register the native class of plugin with PhoneGap : Not required anymore with 1.0.0
		//Use plugins.xml
		/*alert("Register Connection Listener plugin Java classes");

		navigator.add.addService("ConnectionPlugin","org.tssg.awalsh.ConnectionPlugin");*/
	});
	
	//handle the Android Back button 
	//PhoneGap/ HTML views break semantics of Back button unless
	//app intercepts button and simulates back button behaviour
	document.addEventListener("backbutton", function(e){
	    if($.mobile.activePage.is('#main')){
	        e.preventDefault();
	        navigator.app.exitApp();
	    }
	    else {
	        navigator.app.backHistory()
	    }
	}, false);
	
	console.log("type of ConnectionListener plugin: " + typeof window.plugins.ConnectionListener);

}



jQuery(function() {
	console.log("jQuery calls");

	document.addEventListener("deviceready", onDeviceReady, false);
	

});


