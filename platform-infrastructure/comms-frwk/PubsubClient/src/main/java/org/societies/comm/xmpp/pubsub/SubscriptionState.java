package org.societies.comm.xmpp.pubsub;

public enum SubscriptionState {
	SUBSCRIBED("subscribed"),
	UNCONFIGURED("unconfigured"),
	NONE("none");
	
	private String str;
	
	private SubscriptionState(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}
}
