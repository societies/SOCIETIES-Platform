package org.societies.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.domainauthority.registry.DaRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Maria Mannion
 * 
 */
@Service
@Scope("Session")  
public class UserService {

	@Autowired
	private ICommManager commManager;
	@Autowired
	private ICommManagerController commManagerControl;
	@Autowired
	ICISCommunicationMgrFactory ccmFactory;
	
	@Autowired
	DaRegistry daregistry;
	
	
	ICommManager localCommManager;
	String userjid;
	boolean userLoggedIn;
	
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
	
	public DaRegistry getDaregistry() {
		return daregistry;
	}

	public void setDaregistry(DaRegistry daregistry) {
		this.daregistry = daregistry;
	}


	public boolean isUserLoggedIn() {
		return userLoggedIn;
	}


	public void setUserLoggedIn(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}


	public String getUserjid() {
		return userjid;
	}


	public void setUserjid(String userjid) {
		this.userjid = userjid;
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
			
			// Update the Domain Register with out new Comms Manager ID ( so that User container can check it out
			getDaregistry().updateUserSessionCommsId(userjid,this.localCommManager.getIdManager().getThisNetworkNode().getBareJid());
			
			// Let the User GUI Server know that we have a new comms manager, so they can check validity
			
			
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void destroyOnSessionEnd()
	{
		log.info("UserService destroyOnSessionEnd");
		
		// TODO : We need a way of clearing all the callback clients waiting??
		
		// Update the Domain Register setting comms manager id to null from this user
		
		// Tell the User GUI Server running on user container that session invalid
		
		
		
		// Unregister our session Comms manager
		
		if (localCommManager != null)
			localCommManager.UnRegisterCommManager();
	}
	
}
