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


jQuery(function() {
	console.log("connections jQuery calls");

	$('#connectStatus').click(function() {
		connectionStatus();
	});
	
	$('#connectStatusListener').click(function() {
		createConnectionListener();
	});



});
