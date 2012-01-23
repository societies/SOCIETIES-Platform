var Client = {
		jid: '',
		node: '',
		password: '',
		connection: null,
		handlers: {
			echoBot: false,
			handleUpdate: false,
			presence: false,
		},

		on_connect: function (status) {
			if (status == Strophe.Status.CONNECTING) {
				Controler.log('Connecting...');
				Controler.status('Connecting...', '#009900');
			}
			else if (status == Strophe.Status.CONNFAIL) {
				Controler.log('Failed to connect!');
				Controler.status('Connection failed', '#FF0000');
			}
			else if (status == Strophe.Status.DISCONNECTING) {
				Controler.log('Disconnecting...');
				Controler.status('Disconnecting...', '#CC6600');
			}
			else if (status == Strophe.Status.DISCONNECTED) {
				Controler.log('Disconnected');
				Controler.status('Disconnected', '#aa0000');
			}
			else if (status == Strophe.Status.CONNECTED) {
				Controler.log("Connected");
				Controler.status('Connected', '#009900');
				
				// -- Init
				// Handle roster updates
				RosterManager.start();
				
				// Hide connection form
				$('#page_connection').hide();
				$('#logout').fadeIn();
				
				// Send my presence
				Client.connection.send($pres().tree());
//				Client.connection.send($pres().c('priority').t('-1'));
				
				// Handle message received (if echobot enabled)
				Client.handlers.message = Client.connection.addHandler(Controler.onMessage, null, 'message', null, null,  null);
				if ($('#enableEchobot').attr('checked') == 'checked') {
					Controler.log("Enable Echotbot (we are connected now)");
					Client.handlers.echoBot = Client.connection.addHandler(Echobot.onMessage, null, 'message', null, null,  null);
				}
				// Handle presence
				Client.handlers.presence = Client.connection.addHandler(PresenceManager.onPresenceUpdated, null, 'presence', null, null, null);
				
				// Handle IQ send and received
				Client.handlers.handleUpdate = Client.connection.addHandler(Controler.handleUpdate, null, 'iq', null, null,  null);
			}
			return true;
		},

		on_disconnect: function() {
			// Hide connected data
			$('#roster div').html('');
			$('#logout').hide();
			$('#page_connection').fadeIn();
			// Nullify
			Client.connection = null;
			Client.handlers = {
				echoBot: false,
				handleUpdate: false,
				presence: false,
			};
		},
};
