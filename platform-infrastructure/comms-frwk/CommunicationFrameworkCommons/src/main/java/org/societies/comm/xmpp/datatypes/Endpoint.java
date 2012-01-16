package org.societies.comm.xmpp.datatypes;

import org.societies.comm.xmpp.datatypes.Identity.IdentityType;


public class Endpoint extends Identity {
	
	private String nodeIdentifier;
	
	public Endpoint(IdentityType type, String identifier,
			String domainIdentifier, String nodeIdentifier) {
		super(type, identifier, domainIdentifier);
		this.nodeIdentifier = nodeIdentifier;
	}

	public String getNodeIdentifier() {
		return nodeIdentifier;
	}
	
	@Override
	public String getJid() {
		if (type.equals(IdentityType.CSS))
			return identifier+"@"+domainIdentifier+nodeIdentifier;
		else
			return identifier+"."+domainIdentifier;
	}
}
