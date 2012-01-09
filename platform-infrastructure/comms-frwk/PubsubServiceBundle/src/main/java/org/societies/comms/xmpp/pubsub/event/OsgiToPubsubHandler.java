package org.societies.comms.xmpp.pubsub.event;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class OsgiToPubsubHandler implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		// listens all xmpp/* topics and when a publish comes from OSGi it transfers it to XMPP Pubsub
		// when a notification comes from xmpp... TODO
	}

}
