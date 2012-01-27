package org.societies.comm.xmpp.datatypes;

import org.societies.api.comm.xmpp.datatypes.Endpoint;
import org.societies.api.comm.xmpp.datatypes.IdentityType;

public class EndpointImpl extends Endpoint {

	public EndpointImpl(IdentityType type, String identifier,
			String domainIdentifier, String nodeIdentifier) {
		super(type, identifier, domainIdentifier, nodeIdentifier);
	}

	@Override
	public String getJid() {
		if (type.equals(IdentityType.CSS_LIGHT))
			return identifier+"@"+domainIdentifier+"/"+nodeIdentifier;
		else
			return identifier+"."+domainIdentifier;
	}

}
