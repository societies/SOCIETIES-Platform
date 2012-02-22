package org.societies.api.comm.xmpp.pubsub;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.w3c.dom.Element;

public interface Subscriber {
	public void pubsubEvent(Identity pubsubService, String node, String itemId, Object item);
}
