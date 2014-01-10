package org.societies.orchestration.sca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedBean;
import org.societies.orchestration.communitylifecyclemanagementbean.Cis;
import org.societies.orchestration.sca.api.ISCAManager;
import org.societies.orchestration.sca.api.ISCARemote;
import org.societies.orchestration.sca.comms.SCACommsClient;

public class SCAManager extends EventListener implements ISCAManager {

	private static Logger log = LoggerFactory.getLogger(SCAManager.class);

	private HashMap<String, ICommunitySuggestion> pendingJoinSuggestions;
	private HashMap<String, List<CisAdvertisementRecord>> cisRecords;

	private List<String> pendingNotifications;

	private List<ICisOwned> ownedCis;
	private List<ICis> remoteCis;

	private IEventMgr eventMgr;
	private ICommManager commManager;
	private IUserFeedback userFeedback; //WILL NEED THIS AT SOMEPOINT?!?!
	private ICisManager cisManager;
	private ICisDirectoryRemote cisDirectory;

	private NotificationHandler notificationHandler;

	private ISCARemote scaRemote;


	public SCAManager() {
		log.debug("Public Constructor called");

	}

	public void initSCAManager() {
		log.debug("Run initSCAManager()");

		//TODO : add pending suggestions to DB so pending can be retrieved on container restart
		pendingJoinSuggestions = new HashMap<String, ICommunitySuggestion>();
		cisRecords = new HashMap<String, List<CisAdvertisementRecord>>();
		pendingNotifications = new ArrayList<String>();
		ownedCis = new ArrayList<ICisOwned>();
		remoteCis= new ArrayList<ICis>();

		ownedCis = cisManager.getListOfOwnedCis();
		remoteCis = cisManager.getRemoteCis();

		scaRemote = new SCACommsClient(commManager);

		notificationHandler = new NotificationHandler(userFeedback, this);

		//WE NEED TO LISTEN TO INTERNAL EVENTS
		registerForInternalEvents();

		if(!commManager.getIdManager().getThisNetworkNode().getBareJid().equals("eliza.societies.local2.macs.hw.ac.uk")) {
			
			log.debug("Sending msh!!");
			scaRemote.sendJoinSuggestion("eliza.societies.local2.macs.hw.ac.uk", "someCISID");
		}

		//LETS SEND A FAKE SUGGESTED CIS
	/*	SuggestedCISImpl cis = new SuggestedCISImpl();
		cis.setName("Stuart");
		cis.setSuggestionType("join");
		InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);

		log.debug("Sending fake CIS!");
		try {
			this.eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cis.setName("John");
		event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);

		log.debug("Sending fake CIS!");
		try {
			this.eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		cis.setSuggestionType("new");
		 event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);

		log.debug("Sending fake CIS!");
		try {
			this.eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cis.setSuggestionType("delete");
		 event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);

		log.debug("Sending fake CIS!");
		try {
			this.eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	public void registerForInternalEvents() {
		/*String eventFilter = "(&" + 
					"(" + CSSEventConstants.EVENT_NAME + "=" + DeviceMgmtEventConstants.RFID_READER_EVENT + ")" +
					"(" + CSSEventConstants.EVENT_SOURCE + "=" + deviceId + ")" +
					")"; */
		this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.ICO_RECOMMENDTION_EVENT}, null);


	}

	private boolean checkIfOwnedByName(String cisName) {
		this.ownedCis = cisManager.getListOfOwnedCis();
		boolean found = false;
		for(ICisOwned cis : ownedCis) {
			if(cis.getName().equals(cisName)) {
				found = true;
				break;
			}
		}
		return found;
	}

	private boolean checkIfRemoteByName(String cisName) {
		this.remoteCis = cisManager.getRemoteCis();
		boolean found = false;
		for(ICis cis: this.remoteCis) {
			if(cis.getName().equals(cisName)) {
				found = true;
				break;
			}
		}
		return found;
	}

	private List<CisAdvertisementRecord> getAllCISAdvertisements() {		
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		cisDirectory.findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		log.debug("I have all records: " + adverts);
		return adverts;
	}

	private void handleSuggestedJoin(ICommunitySuggestion suggestedCIS) {
		//FIRST LETS CHECK THAT THE CIS EXISTS
		List<CisAdvertisementRecord> records = getAllCISAdvertisements();

		if(records.size()>0) {
			CisAdvertisementRecord targetCis = null;
			for(CisAdvertisementRecord cisAdd : records) {
				if (cisAdd.getName().equals(suggestedCIS.getName())) {
					targetCis = cisAdd;
					break;
				}
			}


			if(null!=targetCis) {
				log.debug("The CIS Exists!");
				//SEND A NOTIFICATION
				String id = UUID.randomUUID().toString();
				String message = "Would you like to join CIS: " + targetCis.getName();
				synchronized (pendingNotifications) {
					pendingNotifications.add(id);
				}
				notificationHandler.sendUserFeedback(id, message);

				if(!checkIfOwnedByName(suggestedCIS.getName()) && !checkIfRemoteByName(suggestedCIS.getName())) {
					log.debug("I am not a member - proceed with checks");
				}
				else {
					log.debug("I am a member - return false");
				}

			}
			else {
				//THE TARGET CIS DOES NOT EXIST - NOW RETURN FAILURE!

			}

		}
		//Something went wrong, or there are no cis - the suggestion has failed
		else {

		}

	}

	private void handleSuggestedCreate(ICommunitySuggestion suggestedCIS) {
		//first check that the suggested cis does not exist
		List<CisAdvertisementRecord> records = getAllCISAdvertisements();
		boolean cisFound = false;
		for(CisAdvertisementRecord cisAdd : records) {
			if(cisAdd.getName().equals(suggestedCIS.getName()))
			{
				cisFound = true;
				break;
			}
		}
		if(!cisFound) {
			log.debug("The cis does not exist");
			//proceed
		}
		else {
			log.debug("The suggested cis already exists");
			//CIS EXISTS return false
		}
	}

	private void handleSuggestedLeave(ICommunitySuggestion suggestedCIS) {
		//FIRST LETS CHECK IT IS A REMOTE CIS
		if(checkIfRemoteByName(suggestedCIS.getName())) {
			log.debug("I am a member of the cis");
			String cis = "cisID";
			scaRemote.sendLeaveSuggestion("jane.societies.", "cisID");
		}
		else {
			log.debug("I am not a member of this cis - ignore return false");
		}
	}

	private void handleSuggestedDelete(ICommunitySuggestion suggestedCIS) {
		//FIRST CHECK THAT WE OWN THE CIS
		if(checkIfOwnedByName(suggestedCIS.getName())) {
			log.debug("I own this cis, proceed");
		}
		else {
			log.debug("I dont own this cis - return false");
		}
	}


	//THEY ARE INTERNAL EVENTS!!!
	//CPA 
	@Override
	public void handleInternalEvent(InternalEvent arg0) {
		if(arg0.geteventType().equals(EventTypes.ICO_RECOMMENDTION_EVENT)) {
			log.debug("We have recieved a ICO Reomm event");

			ICommunitySuggestion suggestedCIS = (ICommunitySuggestion) arg0.geteventInfo();
			String suggestionType = suggestedCIS.getSuggestionType();

			if(suggestionType.equalsIgnoreCase("join")) {
				log.debug("We have a JOIN CIS suggestion");
				handleSuggestedJoin(suggestedCIS);
			}
			else if(suggestionType.equalsIgnoreCase("leave")) {
				log.debug("We have a LEAVE CIS suggestion");
				handleSuggestedLeave(suggestedCIS);
			}
			else if (suggestionType.equalsIgnoreCase("delete")) {
				log.debug("We have a DELETE CIS suggestion");
				handleSuggestedDelete(suggestedCIS);
			}
			else if (suggestionType.equalsIgnoreCase("new")) {
				log.debug("We have a new CIS suggestion");
				handleSuggestedCreate(suggestedCIS);
			}
			else {
				log.debug("We have got a suggestion which we do not know how to handle");
			}

		}
		// TODO Auto-generated method stub
		/*	log.debug("Event listener has been called!");
		log.debug("We have been given a suggestion from another component, lets check it then send to user!");
		log.debug("The event is: " + arg0.geteventName());
		log.debug("Lets pretend it only comes from CPA just now ....");
		ICommunitySuggestion suggestedCis = (ICommunitySuggestion) arg0.geteventInfo();

		//DOES THIS suggestComm make any sense to make?
		//IF SO, UF TO ASK THE USER TO EDIT/CREATE/JOIN/ETC OR
		//TELL COMPONENT THAT ITS NOT GOOD? 
		//IF USER REJECTS UF- SEND BACK TO COMPONENT THAT SENT IT?!?!

		//LETS SEND IT TO UF AND ASK USER IF THIS IS WHAT THEY WANT
		ExpProposalContent content = new ExpProposalContent("Would you like to create a new CIS: " + suggestedCis.getName(), new String[] {"Yes", "No"});
		List<String> reply = new ArrayList<String>();
		try {
			reply = this.userFeedback.getExplicitFB(org.societies.api.internal.useragent.model.ExpProposalType.ACKNACK, content).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Got: " + reply.get(0));
		List<ICisOwned> ownedCIS = new ArrayList<ICisOwned>();
		if(reply.get(0).equals("Yes")) {
			try {
				ownedCIS =  (List<ICisOwned>) this.cisManager.createCis(suggestedCis.getName(), suggestedCis.getSuggestionType(), null, null).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(ICisOwned cis : ownedCIS) {
				log.debug("I own: " + cis.getName());
			}

			List<ICis> remoteCis = this.cisManager.getRemoteCis();
			for(ICis otherCis : remoteCis){ 
				log.debug("I am a member of " + otherCis.getName());
			}
		}*/

	}

	//INJECTION
	public IEventMgr getEventMgr() {
		return eventMgr;
	}
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public ICisDirectoryRemote getCisDirectory() {
		return cisDirectory;
	}

	public void setCisDirectory(ICisDirectoryRemote cisDirectory) {
		this.cisDirectory = cisDirectory;
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void feedbackResult(String id, String msg) {
		log.debug("I have recieved a feedback result with ID:" + id);
		synchronized (pendingNotifications) {
			if(pendingNotifications.contains(id)) {
				log.debug("It is in the the set!");
				//USER HAS ACCEPTED OR DECLINED UF
			}
		}

	}




}
