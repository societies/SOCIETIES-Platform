package org.societies.domainauthority.webapp.service;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.comm.ICommManagerController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Maria Mannion
 * 
 */
public class UserIdentityLogin {

	@Autowired
	private ICommManager commManager;
	@Autowired
	private ICommManagerController commManagerControl;
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	/**
	 * @return the commManagerControl
	 */
	public ICommManagerController getCommManagerControl() {
		return commManagerControl;
	}

	/**
	 * @param commManagerControl the commManagerControl to set
	 */
	public void setCommManagerControl(ICommManagerController commManagerControl) {
		this.commManagerControl = commManagerControl;
	}

	public boolean authenticate(String userId, String subDomain,
			String password, boolean createNewAccount) {

		INetworkNode nodeDetails = getCommManager().getIdManager().getDomainAuthorityNode();
		
		// Ingoring createNewAccount for now
		INetworkNode newNodeDetails = getCommManagerControl().login(userId, nodeDetails.getDomain(), password);
		
		if (newNodeDetails == null)
		{
			// problem 
			return false;
		}
		
		return true;
	}

}
