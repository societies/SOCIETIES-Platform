package org.societies.api.identity;

import java.util.Set;

public interface IIdentityManager {
	IIdentity fromJid(String jid)  throws InvalidFormatException;
	INetworkNode fromFullJid(String jid) throws InvalidFormatException;
	INetworkNode getThisNetworkNode();
	Set<IIdentity> getPublicIdentities();
	boolean isMine(IIdentity identity);
	
	IIdentityContextMapper getIdentityContextMapper();
}
