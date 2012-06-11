package org.societies.identity;

import java.io.Serializable;

import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;

public class NetworkNodeImpl extends IdentityImpl implements INetworkNode, Serializable {

	protected String nodeIdentifier;
	
	public NetworkNodeImpl(IdentityType type, String identifier,
			String domainIdentifier, String nodeIdentifier) {
		super(type, identifier, domainIdentifier);
		this.nodeIdentifier = nodeIdentifier;
	}

	public NetworkNodeImpl(String fulljid) {
		super(fulljid);
	}

	
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	@Override
	public String getJid() {
		if (type.equals(IdentityType.CSS_LIGHT))
			return identifier+"@"+domainIdentifier+"/"+nodeIdentifier;
		else
			return identifier+"."+domainIdentifier;
	}
}
