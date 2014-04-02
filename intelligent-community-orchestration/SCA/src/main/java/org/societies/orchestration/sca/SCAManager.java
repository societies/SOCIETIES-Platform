package org.societies.orchestration.sca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
import org.societies.api.schema.cis.community.Participant;
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
import org.societies.orchestration.sca.model.SuggestedCISInvitationRecord;
import org.societies.orchestration.sca.model.SuggestedCISRecord;
import org.societies.orchestration.sca.model.SuggestedCISImpl;
import org.societies.orchestration.sca.utils.PrivacyPolicyUtils;

public class SCAManager extends EventListener implements ISCAManager {

	private static Logger log = LoggerFactory.getLogger(SCAManager.class);

	private HashMap<String, SuggestedCISRecord> communitySuggestions;
	private HashMap<String, SuggestedCISInvitationRecord> suggestedInvitations;

	private IEventMgr eventMgr;
	private ICommManager commManager;
	private IUserFeedback userFeedback;
	private ICisManager cisManager;
	private ICisDirectoryRemote cisDirectory;
	private NotificationHandler notificationHandler;
	private ISCARemote scaRemote;


	public SCAManager() {
	}

	public void initSCAManager() {
		log.debug("Run initSCAManager()");

		communitySuggestions = new HashMap<String, SuggestedCISRecord>();
		suggestedInvitations = new HashMap<String, SuggestedCISInvitationRecord>();
		scaRemote = new SCACommsClient(commManager);
		notificationHandler = new NotificationHandler(this.userFeedback, this);

		//WE NEED TO LISTEN TO INTERNAL EVENTS
		registerForInternalEvents();
		//LETS SEND A FAKE SUGGESTED CIS
		if(commManager.getIdManager().getThisNetworkNode().getBareJid().contains("jiannis")) {
			SuggestedCISImpl cis = new SuggestedCISImpl();
			cis.setName("testCreate6");
			cis.setSuggestionType("new");
			cis.setMembersList(new ArrayList<String>() {{
				add("user1.societies.local2.macs.hw.ac.uk");
			}});
			InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);


			/*	cis = new ICommunitySuggestion();
			cis.setName("testCreate");
			cis.setSuggestionType("new");
			cis.setMembersList(new ArrayList<String>() {{
				add("user1.societies.local2.macs.hw.ac.uk");
			}});
			event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);
			 */
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
		List<ICisOwned> ownedCis = cisManager.getListOfOwnedCis();
		boolean found = false;
		for(ICisOwned cis : ownedCis) {
			if(cis.getName().equals(cisName)) {
				found = true;
				break;
			}
		}
		return found;
	}

	private boolean checkIfOwnCIS(String cisID) {
		List<ICisOwned> ownedCISs = cisManager.getListOfOwnedCis();
		for(ICisOwned ownCIS : ownedCISs) {
			if(ownCIS.getCisId().equals(cisID)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIfRemoteCIS(String cisID) {
		List<ICis> remoteCISs = cisManager.getRemoteCis();
		for(ICis remoteCIS : remoteCISs) {
			if(remoteCIS.getCisId().equals(cisID)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIfRemoteByName(String cisName) {
		List<ICis> remoteCis = cisManager.getRemoteCis();
		boolean found = false;
		for(ICis cis: remoteCis) {
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

	private void handleSuggestedJoin(String requestID, SuggestedCISRecord suggestedCIS) {

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
				if(!checkIfOwnCIS(targetCis.getId()) && !checkIfRemoteCIS(targetCis.getId())) {
					log.debug("I am not a member - proceed with checks");
					notificationHandler.sendJoinNotification(requestID, suggestedCIS.getCisSuggestion().getName(), suggestedCIS.getCisSuggestion().getMembersList());
				}
				else {
					log.debug("I am a member - return false");
					suggestionComplete(requestID);
					sendInternalFeedbackEvent(suggestedCIS, false);
				}
			}
			else {
				suggestionComplete(requestID);
				sendInternalFeedbackEvent(suggestedCIS, false);
			}
		}
		else {
			suggestionComplete(requestID);
			sendInternalFeedbackEvent(suggestedCIS, false);
		}

	}

	private void addSuggestedCISRecord(String id, SuggestedCISRecord suggestedCIS) {
		log.debug("Adding new pending notification with ID: " + id);
		synchronized (communitySuggestions) {;
		communitySuggestions.put(id, suggestedCIS);
		}
	}

	private void handleSuggestedCreate(String requestID, SuggestedCISRecord suggestedCIS) {
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
			sendInternalFeedbackEvent(suggestedCIS, false);
			suggestionComplete(requestID);
		}
	}

	private void handleSuggestedLeave(String requestID, SuggestedCISRecord suggestedCIS) {
		if(checkIfRemoteByName(suggestedCIS.getCisSuggestion().getName())) {
			String cisID = getCISID(suggestedCIS.getCisSuggestion().getName());
			suggestedCIS.setCisID(cisID);
			synchronized (communitySuggestions) {
				communitySuggestions.put(requestID, suggestedCIS);
			}
			notificationHandler.sendLeaveNotification(requestID, suggestedCIS.getCisSuggestion().getName(), suggestedCIS.getCisSuggestion().getMembersList());
		}
		else {
			sendInternalFeedbackEvent(suggestedCIS, false);
			suggestionComplete(requestID);
		}
	}

	private void handleSuggestedDelete(String requestID, SuggestedCISRecord suggestedCIS) {
		//FIRST CHECK THAT WE OWN THE CIS
		if(checkIfOwnedByName(suggestedCIS.getCisSuggestion().getName())) {
			String cisID = getCISID(suggestedCIS.getCisSuggestion().getName());
			suggestedCIS.setCisID(cisID);
			//ADD CIS ID
			synchronized (communitySuggestions) {
				communitySuggestions.put(requestID, suggestedCIS);
			}
			notificationHandler.sendDeleteNotification(requestID, suggestedCIS.getCisSuggestion().getName(), suggestedCIS.getCisSuggestion().getMembersList());
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
		ICommunitySuggestion cis = (ICommunitySuggestion) suggestedCIS.getCisSuggestion();
		InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, feedbackType, "org/societies/ico/sca", (Serializable) cis);
		try {
			this.eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handleInternalEvent(InternalEvent arg0) {
		if(arg0.geteventType().equals(EventTypes.ICO_RECOMMENDTION_EVENT)) {
			log.debug("We have recieved a ICO Reomm event");
			if(!arg0.geteventName().equals("suggestionFailed") && !arg0.geteventName().equals("suggestionAccepted")) {
				if(arg0.geteventInfo() instanceof ICommunitySuggestion) {
					ICommunitySuggestion cisSuggestion = (ICommunitySuggestion) arg0.geteventInfo();
					SuggestedCISRecord suggestedCIS = new SuggestedCISRecord(cisSuggestion);
					String suggestionType = cisSuggestion.getSuggestionType();

					String requestID = UUID.randomUUID().toString();


					if(suggestionType.equalsIgnoreCase("join")) {
						log.debug("We have a JOIN CIS suggestion");
						suggestedCIS.setMethodType(SCASuggestedMethodType.JOIN);
						addSuggestedCISRecord(requestID, suggestedCIS);
						handleSuggestedJoin(requestID, suggestedCIS);
					}
					else if(suggestionType.equalsIgnoreCase("leave")) {
						log.debug("We have a LEAVE CIS suggestion");
						suggestedCIS.setMethodType(SCASuggestedMethodType.LEAVE);
						addSuggestedCISRecord(requestID, suggestedCIS);
						handleSuggestedLeave(requestID, suggestedCIS);
					}
					else if (suggestionType.equalsIgnoreCase("delete")) {
						log.debug("We have a DELETE CIS suggestion");
						suggestedCIS.setMethodType(SCASuggestedMethodType.DELETE);
						addSuggestedCISRecord(requestID, suggestedCIS);
						handleSuggestedDelete(requestID, suggestedCIS);
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
			privacyPolicyXml = PrivacyPolicyUtils.toXacmlString(policyObj);
			log.info("@@@@@@@########### privacyPolicyXml contains: " +privacyPolicyXml);
		} catch (PrivacyException pEx) {
			pEx.printStackTrace();
		}

		try {
			return cisManager.createCis(cisName, "Community Suggestion", h,cisName+" community", privacyPolicyXml).get().getCisId();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void handleUserResponse(String requestID, SuggestedCISRecord suggestedCIS, SCASuggestedResponseType response) {
		switch(suggestedCIS.getMethodType()) {
		case CREATE :
			if(response.equals(SCASuggestedResponseType.ACCEPTED)) {
				//CREATE THE CIS
				ICommunitySuggestion cis = (ICommunitySuggestion) suggestedCIS.getCisSuggestion();
				String cisID = createNewCommunity(cis.getName());

				//CHECK IF OTHER MEMBERS AFFECTED NEED CONTACTED
				if(cis.getMembersList()!=null && !cis.getMembersList().isEmpty()) {
					SuggestedCISInvitationRecord cisInvitationRecord = new SuggestedCISInvitationRecord();
					cisInvitationRecord.setRequestID(requestID);
					cisInvitationRecord.setAffectedMembers(cis.getMembersList());
					cisInvitationRecord.setMethodType(SCASuggestedMethodType.JOIN);
					synchronized (suggestedInvitations) {
						suggestedInvitations.put(requestID, cisInvitationRecord);
					}

					//SEND EACH USER A NOTIFICATION
					for(String user : cis.getMembersList()) {
						scaRemote.sendJoinSuggestion(requestID, user, cis.getName(), cisID);
					}
				}
				else {
					suggestionComplete(requestID);
					sendInternalFeedbackEvent(suggestedCIS, true);
				}

			} else {
				sendInternalFeedbackEvent(suggestedCIS, false);
				suggestionComplete(requestID);
			}
			break;
		case JOIN :
			if(response.equals(SCASuggestedResponseType.ACCEPTED)) {
				ICommunitySuggestion cis = (ICommunitySuggestion) suggestedCIS.getCisSuggestion();
				String cisID = getCISID(cis.getName());
				joinCIS(cisID);
				if(cis.getMembersList()!=null && !cis.getMembersList().isEmpty()) {
					SuggestedCISInvitationRecord cisInvitationRecord = new SuggestedCISInvitationRecord();
					cisInvitationRecord.setRequestID(requestID);
					cisInvitationRecord.setAffectedMembers(cis.getMembersList());
					cisInvitationRecord.setMethodType(SCASuggestedMethodType.JOIN);
					synchronized (suggestedInvitations) {
						suggestedInvitations.put(requestID, cisInvitationRecord);
					}
					for(String user : cis.getMembersList()) {
						scaRemote.sendJoinSuggestion(requestID, user, cis.getName(), cisID);
					}
				}
			} else {
				sendInternalFeedbackEvent(suggestedCIS, false);
				suggestionComplete(requestID);
			}
			break;
		case LEAVE :
			if(response.equals(SCASuggestedResponseType.ACCEPTED)) {
				String cisID = getCISID(suggestedCIS.getCisSuggestion().getName());
				if(null!=cisID) {
					CisMgrCallback callback = new CisMgrCallback(requestID, CisCallbackType.GET_REMOTE_CIS_MEMBERS);
					cisManager.leaveRemoteCIS(cisID, callback);
					if(suggestedCIS.getCisSuggestion().getMembersList()!=null && !suggestedCIS.getCisSuggestion().getMembersList().isEmpty()) {
						SuggestedCISInvitationRecord invitationRecord = new SuggestedCISInvitationRecord();
						invitationRecord.setAffectedMembers(suggestedCIS.getCisSuggestion().getMembersList());
						invitationRecord.setMethodType(SCASuggestedMethodType.LEAVE);
						invitationRecord.setRequestID(requestID);
						synchronized (suggestedInvitations) {
							suggestedInvitations.put(requestID, invitationRecord);
						}
						for(String userJID : suggestedCIS.getCisSuggestion().getMembersList()) {
							scaRemote.sendLeaveSuggestion(requestID, userJID, suggestedCIS.getCisSuggestion().getName(), suggestedCIS.getCisID(), false);
						}
					} else {
						sendInternalFeedbackEvent(suggestedCIS, true);
						suggestionComplete(requestID);
					}
				}
			}
			else {
				sendInternalFeedbackEvent(suggestedCIS, false);
				suggestionComplete(requestID);
			}
			break;
		case DELETE :
			if(response.equals(SCASuggestedResponseType.ACCEPTED)) {
				String cisID = suggestedCIS.getCisID();
				CisMgrCallback callback = new CisMgrCallback(requestID, CisCallbackType.GET_REMOTE_CIS_MEMBERS);
				try {
					cisManager.getListOfMembers(new Requestor(commManager.getIdManager().getThisNetworkNode()), commManager.getIdManager().fromJid(cisID), callback);
				} catch (InvalidFormatException e) {
					sendInternalFeedbackEvent(suggestedCIS, false);
					suggestionComplete(requestID);
				}

			} else {
				sendInternalFeedbackEvent(suggestedCIS, false);
				suggestionComplete(requestID);
			}
			break;
		}
	}

	public void receiveAllRemoteMembers(String requestID, List<Participant> cisParticipants){
		if(cisParticipants!=null && !cisParticipants.isEmpty()) {
			SuggestedCISRecord suggestedCIS = null;
			synchronized (communitySuggestions) {
				suggestedCIS = communitySuggestions.get(requestID);
			}
			if(suggestedCIS!=null) {
				ArrayList<String> participantIDList = new ArrayList<String>();

				for(Participant participant : cisParticipants){
					participantIDList.add(participant.getJid());
				}
				for(String userId : participantIDList){
					log.debug("Sending a message to userID: " + userId);
					scaRemote.sendLeaveSuggestion(requestID, userId, suggestedCIS.getCisSuggestion().getName(), suggestedCIS.getCisID(), true);
				}
				//TODO handle if not a force leave
				if(cisManager.deleteCis(suggestedCIS.getCisID()))
				{
					sendInternalFeedbackEvent(suggestedCIS, true);
				} else {
					sendInternalFeedbackEvent(suggestedCIS, false);
				}
				suggestionComplete(requestID);
			}

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
		} else {
			//SOMETHING WENT WRONG
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
				sendInternalFeedbackEvent(suggestedCIS, true);
			}
		} else {
			//SOMETHING WENT WRONG
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
				List<Participant> cisParticipants = arg0.getWhoResponse().getParticipant();
				receiveAllRemoteMembers(requestID, cisParticipants);
				break;
			case JOIN_CIS :
				//a differentMethodHere();
				break;
			case LEAVE_CIS :
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

	/* (non-Javadoc)
	 * @see org.societies.orchestration.sca.api.ISCAManager#leaveCIS(java.lang.String)
	 */
	@Override
	public void leaveCISByInvitation(String requestID, String cisID) {
		CisMgrCallback callback = new CisMgrCallback(requestID, CisCallbackType.LEAVE_CIS);
		this.cisManager.leaveRemoteCIS(cisID, callback);		
	}


}