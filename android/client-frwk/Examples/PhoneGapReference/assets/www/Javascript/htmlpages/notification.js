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


jQuery(function() {
	console.log("notification jQuery calls");

	$('#notifyAlert').click(function() {
		notifyAlert();
	});

	$('#notifyConfirm').click(function() {
		notifyConfirm();
	});

	$('#notifyVibrate').click(function() {
		notifyVibrate();
	});
});
