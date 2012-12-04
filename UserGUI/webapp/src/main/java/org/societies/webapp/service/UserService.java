package org.societies.webapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.api.internal.schema.usergui.UserGuiBeanResult;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.webapp.comms.UserGuiCommsClient;
import org.societies.webapp.comms.UserGuiCommsClientCallback;
import org.societies.webapp.comms.UserGuiCommsResult;
import org.societies.webapp.model.CisInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
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
	@Autowired
	ICisDirectory cisDirectory;
	
	
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


	public ICisDirectory getCisDirectory() {
		return cisDirectory;
	}


	public void setCisDirectory(ICisDirectory cisDirectory) {
		this.cisDirectory = cisDirectory;
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
			log.info("CommMgr we are using is " + this.localCommManager.getIdManager().getThisNetworkNode().getJid());
			
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

	@SuppressWarnings("unchecked")
	@Async
	public Future<List<CisInfo>> getMyCisList()
	{
		List<CisInfo> cisList = new ArrayList<CisInfo>();
		
		// first, get a list of the cis from the user cis manager, we only want the cis id, as we can get the
		// other information locally from the cisdirectory
		
		log.info("UserService getMyCisList Start");
		log.info("CommMgr we are using is " + this.localCommManager.getIdManager().getThisNetworkNode().getJid());
		UserGuiCommsClient commsclient = new UserGuiCommsClient(this.localCommManager);
		UserGuiCommsResult commsresult = new UserGuiCommsResult();
		log.info("UserService getMyCisList Sending Message");
		commsclient.getMyCisList(getUserjid(), commsresult);
		
		try {
			//TODO fix:
			while (commsresult.getResultBean() == null)
			{
				Thread.sleep(1000);
			}
			Thread.sleep(1000); // give it one more second to populate
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		UserGuiBeanResult resultbean = commsresult.getResultBean();
		List<String> idList = resultbean.getStringList();
		
		List<CisAdvertisementRecord> cisAdsList = null;
		//TODO : Should be able to give cisdorectory a list of id's!!
		//For now, just get them all 
		
		Future<List<CisAdvertisementRecord>> cisAdsFut = getCisDirectory().findAllCisAdvertisementRecords();
		try {
			cisAdsList = cisAdsFut.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for ( int i = 0; i < idList.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				if (cisAdsList.get(j).getId().contains(idList.get(i)))
						bFound = true;
				else
					j++;
			} while ((bFound == false) && (j < cisAdsList.size()));
			
			if (bFound)
			{
				CisInfo cisInfo = new CisInfo();
				cisInfo.setCisid(idList.get(i));
				cisInfo.setCisname(cisAdsList.get(j).getName());
				cisList.add(cisInfo);
			}
			
		}
			

		
		
		
		return new AsyncResult<List<CisInfo>>(cisList);
		
		
		
	}

	
	@SuppressWarnings("unchecked")
	@Async
	public Future<List<CisInfo>> getSuggestedCisList()
	{
		List<CisInfo> cisList = new ArrayList<CisInfo>();
		
		// first, get a list of the cis from the user cis manager, we only want the cis id, as we can get the
		// other information locally from the cisdirectory
		
		log.info("UserService getSuggestedCisList Start");
		log.info("CommMgr we are using is " + this.localCommManager.getIdManager().getThisNetworkNode().getJid());
		UserGuiCommsClient commsclient = new UserGuiCommsClient(this.localCommManager);
		UserGuiCommsResult commsresult = new UserGuiCommsResult();
		log.info("UserService getSuggestedCisList Sending Message");
		commsclient.getSuggestedCisList(getUserjid(), commsresult);
		
		try {
			//TODO fix:
			while (commsresult.getResultBean() == null)
			{
				Thread.sleep(1000);
			}
			Thread.sleep(1000); // give it one more second to populate
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		UserGuiBeanResult resultbean = commsresult.getResultBean();
		List<String> idList = resultbean.getStringList();
		
		List<CisAdvertisementRecord> cisAdsList = null;
		//TODO : Should be able to give cisdorectory a list of id's!!
		//For now, just get them all 
		
		Future<List<CisAdvertisementRecord>> cisAdsFut = getCisDirectory().findAllCisAdvertisementRecords();
		try {
			cisAdsList = cisAdsFut.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for ( int i = 0; i < idList.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				if (cisAdsList.get(j).getId().contains(idList.get(i)))
						bFound = true;
				else
					j++;
			} while ((bFound == false) && (j < cisAdsList.size()));
			
			if (bFound)
			{
				CisInfo cisInfo = new CisInfo();
				cisInfo.setCisid(idList.get(i));
				cisInfo.setCisname(cisAdsList.get(j).getName());
				cisList.add(cisInfo);
			}
			
		}
			

		
		
		
		return new AsyncResult<List<CisInfo>>(cisList);
		
		
		
	}

	
	
}
