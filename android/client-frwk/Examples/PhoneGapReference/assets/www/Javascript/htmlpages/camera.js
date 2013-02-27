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


jQuery(function() {
	console.log("camera jQuery calls");

	$('#cameraPictureURI').click(function() {
		imageCapture(cameraCaptureByURI);
	});

	$('#cameraPictureDisplay').click(function() {
		imageCapture(cameraCaptureByImage);
	});
});