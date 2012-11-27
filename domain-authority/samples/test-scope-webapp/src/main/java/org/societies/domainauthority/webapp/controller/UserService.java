package org.societies.domainauthority.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.comm.ICommManagerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Maria Mannion
 * 
 */
/*//@Service */
@Scope("Session")  
public class UserService {

	@Autowired
	private ICommManager commManager;
	@Autowired
	private ICommManagerController commManagerControl;
	@Autowired
	ICISCommunicationMgrFactory ccmFactory;
	
	ICommManager localCommManager;
	
	public ICommManager getLocalCommManager() 
	{
		log.info("UserService getLocalCommManager");
		if (localCommManager == null)
			createCommMgr();
		return localCommManager;
	}

	
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

	
	public ICISCommunicationMgrFactory getCcmFactory() {
		return ccmFactory;
	}

	public void setCcmFactory(ICISCommunicationMgrFactory ccmFactory) {
		this.ccmFactory = ccmFactory;
	}


	private static Logger log = LoggerFactory.getLogger(UserService.class);
	
	public UserService() {
		log.info("UserService constructor");
	}
	
	
	public void createCommMgr()
	{
		log.info("UserService createCommMgr");
		try {
			this.localCommManager = getCcmFactory().getNewCommManager();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void destroyOnSessionEnd()
	{
		log.info("UserService destroyOnSessionEnd");
		if (localCommManager != null)
			localCommManager.UnRegisterCommManager();
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
