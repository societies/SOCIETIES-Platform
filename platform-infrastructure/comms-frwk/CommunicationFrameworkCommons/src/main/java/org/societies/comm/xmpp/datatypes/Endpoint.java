package org.societies.comm.xmpp.datatypes;

import org.societies.comm.xmpp.datatypes.Identity.IdentityType;
import org.xmpp.packet.JID;


public class Endpoint {
	private Identity identity;
	private String nodeIdentifier; //TODO if CIS, this is null?

	public static Endpoint fromJID(JID jid) {
		Identity i = new Identity(IdentityType.CSS, jid.getNode(),
				jid.getDomain()); // TODO hardcoded for now
		Endpoint e = new Endpoint(i, jid.getResource());
		return e;
	}
	
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
