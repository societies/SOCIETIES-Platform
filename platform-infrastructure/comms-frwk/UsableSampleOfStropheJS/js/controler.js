var Controler = {
		show_raw: true,
		show_log: true,

		/**
		 * Log to console if available
		 */ 
		log: function (msg) {
			if (Controler.show_log && window.console) { console.log(msg); }
		},

		/**
		 * Show the raw XMPP information coming in
		 */ 
		raw_input: function (data) {
			if (Controler.show_raw) {
				Controler.log('RECV: ' + data);
			}
		},

		/**
		 * Show the raw XMPP information going out
		 */
		raw_output: function (data) {
			if (Controler.show_raw) {
				Controler.log('SENT: ' + data);
			}
		},

		/**
		 * Simplify connection status messages
		 */ 
		status: function(msg, col) {
			$('#status').html(msg).css('color', col);
		},

		/**
		 * Inject text
		 */
		showText: function (m) {
			$('<p>').html(m).insertBefore('#message .reply');
		},

		/**
		 * Inject html
		 */
		showHtml: function (m) {
			$('<p>').html(m).insertBefore('#message .reply');
		},
		
		/**
		 * Decide what to do with an incoming message
		 */
		handleUpdate: function (data) {
//			Controler.log(data);
			// Return true to continue to handle updates
			return true;
		},
		/**
		 * Decide what to do with an incoming message
		 */
		onMessage: function (data) {
			var to = data.getAttribute('to');
			var from = data.getAttribute('from');
			var type = data.getAttribute('type');
			var elems = data.getElementsByTagName('body');

			if (type == "chat" && elems.length > 0) {
				var body = elems[0];
				Controler.showText('< ['+from+'] '+ Strophe.getText(body));
			}

			// we must return true to keep the handler alive.  
			// returning false would remove it after it finishes.
			return true;
		},
};


$(document).ready(function () {
	// -- Init
	$('#logout').hide();
	Controler.status('Disconnected', '#aa0000');

	// -- If login action
	$('#login').live('click', function () {
		// Retrieve values
		Client.jid = $('#jid').get(0).value;
		Client.node = 'here';
		Client.password = $('#password').get(0).value;
		// Connect using Strophe.Connection
		var conn = new Strophe.Connection(Config.BOSH_SERVICE);
		Client.connection = conn;
		Client.connection.rawInput = Controler.raw_input;
		Client.connection.rawOutput = Controler.raw_output;
		Client.connection.connect(
				Client.jid+"/"+Client.node,
				Client.password,
				Client.on_connect,
				Client.on_connect_error
		);
		return false;
	});

	// -- If logout action
	$('#logout').live('click', function () {
		if (null != Client.connection) {
			Client.connection.disconnect("Log-out");
		}
		Client.on_disconnect();
		return false;
	});
	
	// -- If echotbot enabled/disabled
	$('#enableEchobot').live('click', function () {
		if (null != Client.connection) {
			if ($('#enableEchobot').attr('checked') == 'checked') {
				Controler.log("Enable Echotbot");
				Client.handlers.echoBot = Client.connection.addHandler(Echobot.onMessage, null, 'message', null, null,  null);
			}
			else {
				Controler.log("Disable Echotbot");
				Client.connection.deleteHandler(Client.handlers.echoBot);
			}
		}
	});
});
