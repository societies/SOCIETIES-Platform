var Echobot = {
		onMessage: function(msg) {
			var to = msg.getAttribute('to');
			var from = msg.getAttribute('from');
			var type = msg.getAttribute('type');
			var elems = msg.getElementsByTagName('body');

			if (type == "chat" && elems.length > 0) {
				var body = elems[0];
//				Controler.showText('ECHOBOT: I got a message from ' + from + ': ' +  Strophe.getText(body));
				Controler.log('ECHOBOT: I got a message from ' + from + ': ' +  Strophe.getText(body));
				var reply = $msg({to: from, from: to, type: 'chat'}) .cnode(Strophe.copyElement(body));
				Client.connection.send(reply.tree());
				Controler.showText('> [me:ECHOBOT to '+from+'] '+Strophe.getText(body));
				Controler.log('ECHOBOT: I sent ' + from + ': ' + Strophe.getText(body));
			}

			// we must return true to keep the handler alive.  
			// returning false would remove it after it finishes.
			return true;
		},
}

$(document).ready(function () {
});
