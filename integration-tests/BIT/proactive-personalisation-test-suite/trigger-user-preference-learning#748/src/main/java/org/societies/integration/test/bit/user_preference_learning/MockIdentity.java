package org.societies.integration.test.bit.user_preference_learning;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;

public class MockIdentity implements IIdentity{
	
	IdentityType type;
	String identifier;
	String domainIdentifier;

	public MockIdentity(IdentityType type, String identifier,
			String domainIdentifier) {
		this.type = type;
		this.identifier = identifier;
		this.domainIdentifier = domainIdentifier;
	}

	@Override
	public String getJid() {
		return this.identifier+"@"+this.domainIdentifier;
	}

	@Override
	public String getBareJid() {
		return this.identifier+"@"+this.domainIdentifier;
	}

	@Override
	public String getDomain() {
		return domainIdentifier;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public IdentityType getType() {
		return type;
	}

}
