var PresenceManager = {
		onPresenceUpdated: function(presence) {
			var jid = Strophe.getBareJidFromJid($(presence).attr('from'));
			var status = $(presence).find('status').text();
			var show = $(presence).find('show').text();
			if (Client.jid != jid) {
				$('#roster div ul li').each(function() {
					if ($(this).children('.roster-jid').html() == jid) {
						$(this).children('.roster-show').html(show);
						$(this).children('.roster-status').html(status);
					}
				});
			}
			
			// Return true to continue to handle presence updates
			return true;
		},
}
