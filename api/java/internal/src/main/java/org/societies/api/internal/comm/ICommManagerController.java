package org.societies.api.internal.comm;

import java.util.Set;

import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.INetworkNode;

public interface ICommManagerController {
	// Simple Identity Methods
	INetworkNode newMainIdentity(String identifier, String domain, String password) throws XMPPError; // TODO this takes no credentials in a private/public key case
	INetworkNode login(String identifier, String domain, String password);
	INetworkNode loginFromConfig();
	boolean logout();
	boolean destroyMainIdentity();
	
	// CSS Node Discovery
	//String getOtherNodes(ICommCallback callback);
	Set<INetworkNode> getOtherNodes();
	
}
