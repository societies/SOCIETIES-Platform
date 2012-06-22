package org.societies.api.internal.comm;

import java.util.Map;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;

/**
 * This interface allows creating {@link ICommManager} instances for handling messages for a CIS hosted in this node
 * 
 * @author Joao Goncalves
 */
public interface ICISCommunicationMgrFactory {
	
	/**
	 * Create new CIS Communication Manager with a random identifier
	 * 
	 * @return
	 * @throws CommunicationException
	 */
	public ICommManager getNewCommManager() throws CommunicationException;
	
	/**
	 * Create new CIS Communication Manager with the specified identifier
	 * 
	 * @param jid
	 * @return
	 * @throws CommunicationException
	 */
	public ICommManager getNewCommManager(String jid) throws CommunicationException;
	
	/**
	 * Create new CIS Communication Manager with the specified identity and using the specified credentials to log in.
	 * 
	 * @param cisIdentity
	 * @param credentials
	 * @return
	 * @throws CommunicationException
	 */
	public ICommManager getNewCommManager(IIdentity cisIdentity, String credentials) throws CommunicationException;
	
	/**
	 * Returns all created CIS Communication Managers
	 * 
	 * @return
	 */
	public Map<IIdentity, ICommManager> getAllCISCommMgrs();
	
	/**
	 * Destroys all created CIS Communication Managers
	 */
	public void destroyAllConnections();
}
