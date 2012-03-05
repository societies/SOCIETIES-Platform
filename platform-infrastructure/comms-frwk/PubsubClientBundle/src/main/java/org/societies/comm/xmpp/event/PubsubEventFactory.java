package org.societies.comm.xmpp.event;

import java.util.HashMap;
import java.util.Map;

import org.societies.api.identity.IIdentity;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;

public abstract class PubsubEventFactory {
	
	private static final Map<IIdentity, PubsubEventFactory> instances = new HashMap<IIdentity, PubsubEventFactory>();
	
	public static PubsubEventFactory getInstance(IIdentity localIIdentity) {
		return instances.get(localIIdentity);
	}
	
	protected static void newFactory(IIdentity localIIdentity, PubsubEventFactory newFactory) {
		instances.put(localIIdentity, newFactory);
	}
	
	public abstract PubsubEventStream getStream(IIdentity pubsubService, String node);
}
