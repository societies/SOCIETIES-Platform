package org.societies.orchestration.sca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.orchestration.sca.api.INotificationHandler;
import org.societies.orchestration.sca.api.ISCAManager;
import org.societies.orchestration.sca.api.ISCARemote;
import org.societies.orchestration.sca.comms.SCACommsClient;
import org.societies.orchestration.sca.enums.CisCallbackType;
import org.societies.orchestration.sca.model.SCAInvitation;
import org.societies.orchestration.sca.model.SuggestedCISInvitationRecord;
import org.societies.orchestration.sca.model.SuggestedCISRecord;
import org.societies.orchestration.sca.model.SuggestedCISImpl;
import org.societies.orchestration.sca.utils.PrivacyPolicyUtils;

public class SCAManager extends EventListener implements ISCAManager {

	private static Logger log = LoggerFactory.getLogger(SCAManager.class);

	private HashMap<String, SuggestedCISRecord> communitySuggestions;
	private HashMap<String, SuggestedCISInvitationRecord> suggestedInvitations;
	
	private Set<String> callbackMap;

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

		communitySuggestions = new HashMap<String, SuggestedCISRecord>();
		suggestedInvitations = new HashMap<String, SuggestedCISInvitationRecord>();
		
		callbackMap = new HashSet<String>();


		ownedCis = new ArrayList<ICisOwned>();
		remoteCis= new ArrayList<ICis>();


		scaRemote = new SCACommsClient(commManager);



		notificationHandler = new NotificationHandler(this.userFeedback, this);
		//WE NEED TO LISTEN TO INTERNAL EVENTS
		registerForInternalEvents();
		//LETS SEND A FAKE SUGGESTED CIS
		if(commManager.getIdManager().getThisNetworkNode().getBareJid().contains("jane")) {
			SuggestedCISImpl cis = new SuggestedCISImpl();
			cis.setName("StuartCIS10");
			cis.setSuggestionType("new");
			cis.setMembersList(new ArrayList<String>() {{
				add("eliza.societies.local2.macs.hw.ac.uk");
			}});
			InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);

			log.debug("Sending fake CIS!");
			try {
				this.eventMgr.publishInternalEvent(event);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("Waiting!");

	}

	public void registerForInternalEvents() {
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

	private String getCISID(String cisName) {
		List<CisAdvertisementRecord> allCIS = new ArrayList<CisAdvertisementRecord>();
		allCIS = getAllCISAdvertisements();
		for(CisAdvertisementRecord cisAdv : allCIS) {
			if(cisAdv.getName().equals(cisName)) {
				return cisAdv.getId();
			}
		}
		return null;
	}

	private void handleSuggestedJoin(SuggestedCISRecord suggestedCIS) {
		//FIRST LETS CHECK THAT THE CIS EXISTS
		List<CisAdvertisementRecord> records = getAllCISAdvertisements();

		if(records.size()>0) {
			CisAdvertisementRecord targetCis = null;
			for(CisAdvertisementRecord cisAdd : records) {
				if (cisAdd.getName().equals(suggestedCIS.getCisSuggestion().getName())) {
					targetCis = cisAdd;
					break;
				}
			}


			if(null!=targetCis) {
				log.debug("The CIS Exists!");
				//SEND A NOTIFICATION
				String id = UUID.randomUUID().toString();
				String message = "Would you like to join CIS: " + targetCis.getName();

				notificationHandler.sendJoinNotification(id, suggestedCIS.getCisSuggestion().getName(), suggestedCIS.getCisSuggestion().getMembersList());


				if(!checkIfOwnedByName(suggestedCIS.getCisSuggestion().getName()) && !checkIfRemoteByName(suggestedCIS.getCisSuggestion().getName())) {
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

	private void addSuggestedCISRecord(String id, SuggestedCISRecord suggestedCIS) {
		log.debug("Adding new pending notification with ID: " + id);
		synchronized (communitySuggestions) {;
		communitySuggestions.put(id, suggestedCIS);
		}
	}

	private void handleSuggestedCreate(String requestID, SuggestedCISRecord suggestedCIS) {
		//first check that the suggested cis does not exist
		log.debug("In create method!");
		List<CisAdvertisementRecord> records = getAllCISAdvertisements();
		boolean cisFound = false;
		for(CisAdvertisementRecord cisAdd : records) {
			if(cisAdd.getName().equals(suggestedCIS.getCisSuggestion().getName()))
			{
				cisFound = true;
				break;
			}
		}
		if(!cisFound) {
			notificationHandler.sendCreateNotification(requestID, suggestedCIS.getCisSuggestion().getName(), suggestedCIS.getCisSuggestion().getMembersList());
		}
		else {
			log.debug("The suggested cis already exists");
			//CIS EXISTS return false
		}
	}

	private void handleSuggestedLeave(SuggestedCISRecord suggestedCIS) {
		//FIRST LETS CHECK IT IS A REMOTE CIS
		if(checkIfRemoteByName(suggestedCIS.getCisSuggestion().getName())) {
			log.debug("I am a member of the cis");
			String cis = "cisID";
			//	scaRemote.sendLeaveSuggestion("jane.societies.", "cisID");
		}
		else {
			log.debug("I am not a member of this cis - ignore return false");
		}
	}

	private void handleSuggestedDelete(SuggestedCISRecord suggestedCIS) {
		//FIRST CHECK THAT WE OWN THE CIS
		if(checkIfOwnedByName(suggestedCIS.getCisSuggestion().getName())) {
			log.debug("I own this cis, proceed");
		}
		else {
			log.debug("I dont own this cis - return false");
		}
	}

	private void sendInternalFeedbackEvent(SuggestedCISRecord suggestedCIS, boolean accepted) {
		String feedbackType = "suggestionFailed";
		if(accepted) {
			feedbackType = "suggestionAccepted";
		}
		SuggestedCISImpl cis = (SuggestedCISImpl) suggestedCIS.getCisSuggestion();
		InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, feedbackType, "org/societies/ico/sca", cis);

		log.debug("Sending fake CIS!");
		try {
			this.eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//THEY ARE INTERNAL EVENTS!!!
	//CPA 
	@Override
	public void handleInternalEvent(InternalEvent arg0) {
		if(arg0.geteventType().equals(EventTypes.ICO_RECOMMENDTION_EVENT)) {
			log.debug("We have recieved a ICO Reomm event");

			SuggestedCISRecord suggestedCIS = new SuggestedCISRecord((SuggestedCISImpl) arg0.geteventInfo());
			String suggestionType = ((SuggestedCISImpl) arg0.geteventInfo()).getSuggestionType();

			String requestID = UUID.randomUUID().toString();


			if(suggestionType.equalsIgnoreCase("join")) {
				log.debug("We have a JOIN CIS suggestion");
				suggestedCIS.setMethodType(SCASuggestedMethodType.JOIN);
				addSuggestedCISRecord(requestID, suggestedCIS);
				handleSuggestedJoin(suggestedCIS);
			}
			else if(suggestionType.equalsIgnoreCase("leave")) {
				log.debug("We have a LEAVE CIS suggestion");
				suggestedCIS.setMethodType(SCASuggestedMethodType.LEAVE);
				addSuggestedCISRecord(requestID, suggestedCIS);
				handleSuggestedLeave(suggestedCIS);
			}
			else if (suggestionType.equalsIgnoreCase("delete")) {
				log.debug("We have a DELETE CIS suggestion");
				suggestedCIS.setMethodType(SCASuggestedMethodType.DELETE);
				addSuggestedCISRecord(requestID, suggestedCIS);
				handleSuggestedDelete(suggestedCIS);
			}
			else if (suggestionType.equalsIgnoreCase("new")) {
				log.debug("We have a new CIS suggestion");
				suggestedCIS.setMethodType(SCASuggestedMethodType.CREATE);
				addSuggestedCISRecord(requestID, suggestedCIS);
				handleSuggestedCreate(requestID, suggestedCIS);
			}
			else {
				log.debug("We have got a suggestion which we do not know how to handle");
			}
		}
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	private String createNewCommunity(String cisName) {

		PrivacyPolicyBehaviourConstants policyType = PrivacyPolicyBehaviourConstants.MEMBERS_ONLY;
		String pPolicy = "membersOnly";


		//GENERATE MEMBERSHIP CRITERIA
		Hashtable<String, MembershipCriteria> h = null;
		MembershipCrit m = new MembershipCrit();
		log.info("create MembershipCrit: " +m);

		//MembershipCrit m = create.getCommunity().getMembershipCrit();
		if (m!=null && m.getCriteria() != null && m.getCriteria().size()>0){
			h =new Hashtable<String, MembershipCriteria>();

			// populate the hashtable
			for (Criteria crit : m.getCriteria()) {
				MembershipCriteria meb = new MembershipCriteria();
				log.info("create MembershipCriteria: " +meb);
				meb.setRank(crit.getRank());
				Rule r = new Rule();
				if( r.setOperation(crit.getOperator()) == false);
				ArrayList<String> a = new ArrayList<String>();
				a.add(crit.getValue1());


				if (crit.getValue2() != null && !crit.getValue2().isEmpty()) 
					a.add(crit.getValue2()); 
				if( r.setValues(a) == false){
					meb.setRule(r);
					h.put(crit.getAttrib(), meb);
				}

			}
		}
		log.info("about to create Create");
		Create create = new Create(); 
		create.setPrivacyPolicy(pPolicy);
		//POLICY RECEIVED IS ENUM VALUE, CONVERT TO POLICY XML
		//String pPolicy = "membersOnly"; //DEFAULT VALUE
		String privacyPolicyXml = "<RequestPolicy />";
		if(create.getPrivacyPolicy() != null && create.getPrivacyPolicy().isEmpty() == false){
			pPolicy = create.getPrivacyPolicy();
			log.info("pPolicy = " +pPolicy);
		} 
		//PrivacyPolicyBehaviourConstants policyType = PrivacyPolicyBehaviourConstants.MEMBERS_ONLY; //DEFAULT
		PrivacyPolicyUtils utility = new PrivacyPolicyUtils();
		log.info("Create new PrivacyPolicyUtils: " +utility );
		RequestPolicy policyObj = null;
		log.info("Create RequestPolicy: " +policyObj );
		try {
			policyType = PrivacyPolicyBehaviourConstants.fromValue(pPolicy);
		} catch (IllegalArgumentException ex) {
			//IGNORE - DEFAULT TO MEMBERS_ONLY
			log.error("Exception parsing: " + pPolicy + ". " + ex);
		}
		//CALL POLICY UTILS TO CREATE XML FOR STORAGE
		try {
			log.info("call to PrivacyPolicyUtils: " );
			//org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy policyObj = PrivacyPolicyUtils.inferCisPrivacyPolicy(policyType, m);
			policyObj = PrivacyPolicyUtils.inferCisPrivacyPolicy(policyType, m);
			privacyPolicyXml =  PrivacyPolicyUtils.toXacmlString(policyObj);
			log.info("@@@@@@@########### privacyPolicyXml contains: " +privacyPolicyXml);
		} catch (PrivacyException pEx) {
			pEx.printStackTrace();
		}
		
		try {
			return cisManager.createCis(cisName, "", new Hashtable<String, MembershipCriteria>(), privacyPolicyXml).get().getCisId();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void handleUserResponse(String ID, SuggestedCISRecord suggestedCIS, SCASuggestedResponseType response) {
		switch(suggestedCIS.getMethodType()) {
		case CREATE :
			log.debug("In the create branch!");
			if(response.equals(SCASuggestedResponseType.ACCEPTED)) {
				//CREATE THE CIS
				SuggestedCISImpl cis = (SuggestedCISImpl) suggestedCIS.getCisSuggestion();
				String cisID = createNewCommunity(cis.getName());

				//CHECK IF OTHER MEMBERS AFFECTED NEED CONTACTED
				if(cis.getMembersList()!=null && !cis.getMembersList().isEmpty()) {
					SuggestedCISInvitationRecord cisInvitationRecord = new SuggestedCISInvitationRecord();
					cisInvitationRecord.setRequestID(ID);
					cisInvitationRecord.setAffectedMembers(cis.getMembersList());
					cisInvitationRecord.setMethodType(SCASuggestedMethodType.JOIN);
					synchronized (suggestedInvitations) {
						suggestedInvitations.put(ID, cisInvitationRecord);
					}

					//SEND EACH USER A NOTIFICATION
					for(String user : cis.getMembersList()) {
						scaRemote.sendJoinSuggestion(ID, user, cis.getName(), cisID);
					}
				}
				else {
					suggestionComplete(ID);
				}

			} else {
				sendInternalFeedbackEvent(suggestedCIS, false);
				suggestionComplete(ID);
			}
			break;
		case JOIN :
			break;
		case LEAVE :
			/*if(suggestedCisFeedback.getSuggestedCIS().getSuggestionType().equalsIgnoreCase("leave")) {
				if(suggestedCisFeedback.getResult().equals(SCASuggestedResponseType.ACCEPTED)) {
					//GET ID
					String cisID = getCISID(suggestedCisFeedback.getSuggestedCIS().getName());
					if(null!=cisID) {
						ICisManagerCallback callback = new CisCallback();
						cisManager.leaveRemoteCIS(cisID, callback); 
						log.debug("We have left CIS with ID: " + cisID);
					}
				}
			}*/
			break;
		case DELETE :
			break;

		}
	}

	public void suggestionComplete(String requestID) {
		log.debug("Setting request with ID " + requestID + " as completed. Removing from maps.");
		synchronized (suggestedInvitations) {
			suggestedInvitations.remove(requestID);
		}
		synchronized (communitySuggestions) {
			communitySuggestions.remove(requestID);
		}
	}

	@Override
	public void feedbackResult(String id, SCASuggestedResponseType feedbackResult) {
		log.debug("I have recieved a feedback result with ID:" + id);
		SuggestedCISRecord communitySuggestion = null;
		synchronized (communitySuggestions) {
			if(communitySuggestions.containsKey(id)) {
				communitySuggestion = communitySuggestions.get(id);
			}
		}

		if(null!=communitySuggestion) {
			handleUserResponse(id, communitySuggestion, feedbackResult);
		}

	}

	@Override
	public void handleInvitationResponse(String requestID, String fromJID, SCASuggestedResponseType response) {
		SuggestedCISInvitationRecord invitationRecord =null;
		synchronized (suggestedInvitations) {
			invitationRecord = suggestedInvitations.get(requestID);
		}
		if(invitationRecord!=null) {

			SuggestedCISRecord suggestedCIS = null;			
			synchronized(communitySuggestions) {
				suggestedCIS = communitySuggestions.get(requestID);
			}

			String message = fromJID + " has " + response.toString().toLowerCase() + " your invitation to " +
					invitationRecord.getMethodType().toString().toLowerCase() +
					" the CIS " + suggestedCIS.getCisSuggestion().getName() + ".";

			notificationHandler.sendMessage(message);

			if(invitationRecord.setUserResponse(fromJID, response)) {
				suggestionComplete(requestID);
			}
		}
	}


	@Override
	public void joinCIS(String cisID) {
		List<CisAdvertisementRecord> cisAdverts = getAllCISAdvertisements();
		for(CisAdvertisementRecord advert : cisAdverts) {
			if(advert.getId().equals(cisID)) {
				CisMgrCallback callback = new CisMgrCallback(cisID, CisCallbackType.JOIN_CIS);
				this.cisManager.joinRemoteCIS(advert, callback);
			}
		}

	}
	
	public void recieveCISMgrCallback(String requestID, CommunityMethods cisMethods) {
		log.debug("I have recieved a callback, I can now continue what I am doing");
		boolean areWeWaitingForThisID = false;
		synchronized (callbackMap) {
			if(callbackMap.contains(requestID)) {
				areWeWaitingForThisID = true;
				callbackMap.remove(requestID);
			}
		}
		if(areWeWaitingForThisID) {
			//CONTINUE WHAT WE WERE DOING....
			log.debug(cisMethods.getGetInfo().toString());
		}
						
	}

	public class CisMgrCallback implements ICisManagerCallback {
		
		private String requestID;
		private CisCallbackType callbackType ;
		
		public CisMgrCallback(String requestID, CisCallbackType callbackType) {
			this.requestID = requestID;
			this.callbackType = callbackType;
		}

		@Override
		public void receiveResult(CommunityMethods arg0) {
			switch(this.callbackType) {
			case GET_REMOTE_CIS_MEMBERS :
				//yourMethodHere(requestID, arg0);
				break;
			case JOIN_CIS :
				//a differentMethodHere();
				break;
			}

		}

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
	public INotificationHandler getNotificationHandler() {
		return this.notificationHandler;
	}

	@Override
	public ISCARemote getSCARemote() {
		return this.scaRemote;
	}


}
