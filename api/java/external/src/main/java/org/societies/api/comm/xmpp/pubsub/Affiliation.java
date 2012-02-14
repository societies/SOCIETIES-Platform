package org.societies.api.comm.xmpp.pubsub;

public enum Affiliation {
	OWNER("owner"), 
	MEMBER("member"), 
	PUBLISHER("publisher"), 
	PUBLISH_ONLY("publish-only"),
	OUTCAST("outcast"),
	NONE("none");
	
	private String str;
	
	private Affiliation(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}
	
}
