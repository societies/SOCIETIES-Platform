package org.societies.comms.xmpp.pubsub.event;

import java.util.HashMap;
import org.osgi.service.event.Event;
import org.societies.comm.xmpp.datatypes.Endpoint;

public class PubsubEvent extends Event {

	// to publish an event from osgi, to put notification in osgi events
	public PubsubEvent(Endpoint endpoint, String topic, Object payload) {
		super("", new HashMap());
		// TODO
	}
}
