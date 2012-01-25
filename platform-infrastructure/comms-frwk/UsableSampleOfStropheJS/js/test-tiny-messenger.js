var Messenger = {
		send: function(to, message2send) {
			if (null == Client.connection) {
				Controler.showHtml('<em>You are not connected.</em>');
				return false;
			}
			if ('' != message2send && null != message2send) {
				var msg = $msg({to: to, from: Client.jid+"/"+Client.node, type: 'chat'})
					.c('body')
					.t(message2send);
				Controler.showText('> [me to '+to+'] '+message2send);
				Client.connection.send(msg.tree());
			}
			return true;
		},
}

$(document).ready(function () {
	$('#sendMessage').click(function() {
		var to = $('input[type=radio]:checked').siblings('.roster-jid').text();
		var message2send = $('#message2send').val();
		if (Messenger.send(to, message2send)) {
			$('#message2send').val('').focus();
		}
		return false;
	});
});
