package org.societies.api.identity;

public interface IIdentity {
	String getIdentifier();
	String getDomain();
	IdentityType getType();
	String getJid();
	String getBareJid();
}
