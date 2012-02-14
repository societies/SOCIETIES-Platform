package org.societies.comm.xmpp.event;

import java.util.HashMap;
import java.util.Map;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;

public abstract class PubsubEventFactory {
	
	private static final Map<Identity, PubsubEventFactory> instances = new HashMap<Identity, PubsubEventFactory>();
	
	public static PubsubEventFactory getInstance(Identity localIdentity) {
		return instances.get(localIdentity);
	}
	
	protected static void newFactory(Identity localIdentity, PubsubEventFactory newFactory) {
		instances.put(localIdentity, newFactory);
	}
	
	public abstract PubsubEventStream getStream(Identity pubsubService, String node);
}
