package org.societies.api.comm.xmpp.pubsub;

import org.societies.api.identity.IIdentity;

public interface Subscriber {
	public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item);
}
