package org.societies.comm.xmpp;

import org.societies.comm.identity.Identity;

public class Endpoint {
	private Identity identity;
	private String nodeIdentifier; //TODO if CIS, this is null?
	
	public Endpoint(Identity identity, String nodeIdentifier) {
		this.identity = identity;
		this.nodeIdentifier = nodeIdentifier;
	}

	public Identity getIdentity() {
		return identity;
	}

	public String getNodeIdentifier() {
		return nodeIdentifier;
	}
}
