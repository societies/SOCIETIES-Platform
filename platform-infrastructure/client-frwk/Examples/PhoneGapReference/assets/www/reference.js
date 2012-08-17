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

}

var watchID = null;

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

var notifyAlert = function() {
	navigator.notification.alert("You really want to?");
};

var notifyConfirm = function() {
	navigator.notification.confirm("You really want to?", null,
			"End the World", "Yes,No");
};

var notifyBeeb = function() {
	navigator.notification.beep(2);
};

var notifyVibrate = function() {
	navigator.notification.vibrate(1000);
};

var cameraCaptureByURI = 0;
var cameraCaptureByImage = 1;

var imageCapture = function(type) {
	function onURISuccess(imageURI) {
		alert("Captured Image URI: " + imageURI);
		console.log("Captured Image URI: " + imageURI);

	}
	
	function onImageSuccess(image) {
		console.log("Captured Image");
		jQuery("#smallImage").css("display","block");
		jQuery("#smallImage").attr("src","data:image/jpeg;base64," + image);

	}

	function onFail(message) {
		console.log('Failed because: ' + message);
	}
	console.log("imageCapture called");
	
	if (cameraCaptureByURI === type) {
		navigator.camera.getPicture(onURISuccess, onFail, {
			quality : 50,
			destinationType : Camera.DestinationType.FILE_URI
		});
		
	} else {
		navigator.camera.getPicture(onImageSuccess, onFail, {
			quality : 20,
			destinationType : Camera.DestinationType.DATA_URL
		});
	}

};


var connectionStatus = function() {

	var networkState = navigator.network.connection.type, states = {};

	states[Connection.UNKNOWN] = 'Unknown connection';
	states[Connection.ETHERNET] = 'Ethernet connection';
	states[Connection.WIFI] = 'WiFi connection';
	states[Connection.CELL_2G] = 'Cell 2G connection';
	states[Connection.CELL_3G] = 'Cell 3G connection';
	states[Connection.CELL_4G] = 'Cell 4G connection';
	states[Connection.NONE] = 'No network connection';

	alert('Connection type: ' + states[networkState]);
	console.log('Connection type: ' + states[networkState]);


};

var createConnectionListener = function() {
	console.log("Create Network Connection listener");

	function success(connectionStatus) {
		alert(connectionStatus.actionMessage + " " + connectionStatus.action);
		
	}
	
	function failure(connectionStatus) {
		alert(connectionStatus);
	}
    window.plugins.ConnectionListener.createListener(success, failure);
	
};

function contacts_success(contacts) {
	alert(contacts.length +
			" contacts returned." + 
			(contacts[2] && contacts[2].name ? (" Third contact is " + contacts[2].name.formatted)
					: ""));
}

function failNoContacts() {
	alert("No Contacts");
}

var getContacts = function() {
	console.log("Get phone contacts");

	var obj = new ContactFindOptions();
	obj.filter = "";
	obj.multiple = true;
	obj.limit = 5;
	navigator.contacts.find([ "displayName", "name" ], contacts_success,
			failNoContacts, obj);
};

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

var targetFileName = "SocietiesPhoneGapTest.txt";

var translateFileErrors = function(fileError) {
	var message = "Error";

	switch (fileError.code) {
	case FileError.NOT_FOUND_ERR:
		message = "File not found";
		break;
	case FileError.SECURITY_ERR:
		message = "Security breach";
		break;
	case FileError.ABORT_ERR:
		message = "Abort";
		break;
	case FileError.NOT_READABLE_ERR:
		message = "Not readable";
		break;
	case FileError.ENCODING_ERR:
		message = "Malformed URL";
		break;
	case FileError.NO_MODIFICATION_ALLOWED_ERR:
		message = "No modification allowed";
		break;
	case FileError.INVALID_STATE_ERR:
		message = "Invalid state";
		break;
	case FileError.SYNTAX_ERR:
		message = "Syntax problem";
		break;
	case FileError.INVALID_MODIFICATION_ERR:
		message = "Invalid modification";
		break;
	case FileError.QUOTA_EXCEEDED_ERR:
		message = "Quota exceeded";
		break;
	case FileError.TYPE_MISMATCH_ERR:
		message = "File/Directory mismatch";
		break;
	case FileError.PATH_EXISTS_ERR:
		message = "File/Directory already exists";
		break;
	default:
		message = "Unexpected Error";
		break;
	}
	return message;
};

var clearFileTags = function() {
	jQuery("#fileErrors").text("");
	jQuery("#fileStatus").text("");
};

var fileExists = function() {
	console.log("Does file exist?");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileStatus").text("False");
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function success() {
		jQuery("#fileStatus").text("True");
	}

	function onFileSystemSuccess(fileSystem) {
		jQuery("#fileSystemData").text(
				fileSystem.name + " " + fileSystem.root.fullPath);

		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var deleteFile = function() {
	console.log("Delete file");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileStatus").text("False");
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function onRemoveSuccess() {
		jQuery("#fileStatus").text("Deleted");

	}

	function success(fileEntry) {
		fileEntry.remove(onRemoveSuccess, fail);
	}

	function onFileSystemSuccess(fileSystem) {

		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);
};

var createFile = function() {
	console.log("Create file");

	clearFileTags();

	function success(fileEntry) {
		alert(fileEntry.name + " created");
	}

	function fail(fileError) {
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function onFileSystemSuccess(fileSystem) {
		fileSystem.root.getFile(targetFileName, {
			create : true,
			exclusive : true
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var writeToFile = function() {
	console.log("Write to file");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	/* Append text*/
	function writeFile(writer) {
		writer.seek(writer.length);
		writer
				.write("Some day Societies will work\nbut it may take a long time\n");
	}

	function success(fileEntry) {
		fileEntry.createWriter(writeFile, fail);
	}

	function onFileSystemSuccess(fileSystem) {
		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var displayFile = function() {
	console.log("Display file");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function gotFile(file) {
		var reader = new FileReader();

		reader.onloadend = function(evt) {
			jQuery("#fileContents").text(evt.target.result);
		};

		reader.readAsText(file);
	}

	function success(fileEntry) {
		fileEntry.file(gotFile, fail);
	}

	function onFileSystemSuccess(fileSystem) {
		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var listFiles = function(root) {
	console.log("List files for root: " + root);

	var directoryReader = root.createReader();

	function fail(fileError) {
		alert(translateFileErrors(fileError));
	}

	function onReadSuccess(files) {
		var i;
		for (i = 0; i < files.length; i++) {
			if (files[i].isFile) {
				jQuery("#fileStatus").text(files[i].fullPath);
			}
		}
	}

	directoryReader.readEntries(onReadSuccess, fail);
};

jQuery(function() {
	console.log("jQuery calls");

	document.addEventListener("deviceready", onDeviceReady, false);
	

	$('#toggleAcceleromter').click(function() {
		toggleAccel();
	});
	
	$('#geoLocation').click(function() {
		getLocation();
	});

	$('#geoLocationWatch').click(function() {
		trackLocation();
	});

	$('#geoLocWatchStop').click(function() {
		clearGeoWatch();
	});

	$('#deviceChar').click(function() {
		deviceInfo();
	});

	$('#compassReading').click(function() {
		compassInfo();
	});

	$('#notifyAlert').click(function() {
		notifyAlert();
	});

	$('#notifyConfirm').click(function() {
		notifyConfirm();
	});

	$('#notifyVibrate').click(function() {
		notifyVibrate();
	});

	$('#cameraPictureURI').click(function() {
		imageCapture(cameraCaptureByURI);
	});

	$('#cameraPictureDisplay').click(function() {
		imageCapture(cameraCaptureByImage);
	});

	$('#connectStatus').click(function() {
		connectionStatus();
	});

	$('#connectStatusListener').click(function() {
		createConnectionListener();
	});

	$('#userContacts').click(function() {
		getContacts();
	});

	$('#registerEvents').click(function() {
		registerEvents();
	});

	$('#createFile').click(function() {
		createFile();
	});
	$('#fileExists').click(function() {
		fileExists();
	});
	$('#displayFile').click(function() {
		displayFile();
	});
	$('#writeToFile').click(function() {
		writeToFile();
	});
	$('#deleteFile').click(function() {
		deleteFile();
	});

});


