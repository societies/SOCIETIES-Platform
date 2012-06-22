package org.societies.api.internal.comm;

import java.util.Set;

import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;

/**
 * This interface allows to control the main {@link ICommManager} instance of a network node.
 * 
 * @author Joao Goncalves
 */
public interface ICommManagerController {
	
	/**
	 * Create a new identity and set of credentials to authenticate with in a Domain Authority
	 * 
	 * @param identifier
	 * @param domain
	 * @param password
	 * @return
	 * @throws XMPPError
	 */
	INetworkNode newMainIdentity(String identifier, String domain, String password) throws XMPPError; // TODO this takes no credentials in a private/public key case
	
	/**
	 * Use a set of credentials to authenticate with in a Domain Authority
	 * 
	 * @param identifier
	 * @param domain
	 * @param password
	 * @return
	 */
	INetworkNode login(String identifier, String domain, String password);
	
	/**
	 * Use the configuration supplied credentials to authenticate with in a Domain Authority
	 *
	 * @return
	 */
	INetworkNode loginFromConfig();
	
	/**
	 * Disconnect from the Domain Authority
	 * 
	 * @return
	 */
	boolean logout();
	
	/**
	 * Destroy an identity and set of credentials used to authenticate with in a Domain Authority
	 * 
	 * @return
	 */
	boolean destroyMainIdentity();
	

	//String getOtherNodes(ICommCallback callback);
	
	/**
	 * Get other network nodes also logged in with the same main identity
	 * 
	 * @return
	 */
	Set<INetworkNode> getOtherNodes();
	
}
