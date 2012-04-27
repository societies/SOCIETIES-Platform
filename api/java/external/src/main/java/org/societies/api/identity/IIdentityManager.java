package org.societies.api.identity;

import java.util.Set;

/**
 * MISSING_ANNOTATION
 * MISSING_JAVADOCS
 */
public interface IIdentityManager {
	IIdentity fromJid(String jid)  throws InvalidFormatException;
	INetworkNode fromFullJid(String jid) throws InvalidFormatException;
	
	// TODO these methods should be Internal
	INetworkNode getThisNetworkNode();
	
	// Ctx Requirement
	IIdentityContextMapper getIdentityContextMapper();
	
	// Pseudonym check methods
	Set<IIdentity> getPublicIdentities();
	boolean isMine(IIdentity identity);
	
	// Pseudonym mgmt methods
	IIdentity newMemorableIdentity(String memorableIdentifier);
	boolean releaseMemorableIdentity(IIdentity memorableIdentity);
	IIdentity newTransientIdentity();
	
	// TODO this should be the External method (available to 3rd party services)
	// 3rd parties should run in an identity sandbox
	//IIdentity getIdentity();
}
