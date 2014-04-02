package org.societies.orchestration.eca;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.orchestration.eca.api.IECAManager;
import org.societies.orchestration.eca.model.CommunitySuggestion;
import org.societies.orchestration.eca.synoyms.SynonymRetriever;
import org.societies.orchestration.eca.threads.AnalyseTask;



public class ECAManager extends EventListener implements IECAManager {


	private ICSSInternalManager cssManager;
	private ICisManager cisManager;


	private Logger log = LoggerFactory.getLogger(ECAManager.class);


	//NEW

	private ICommManager commManager;
	private ICtxBroker ctxBroker;
	private RemoteContextRetriever remoteContextRetriever;
	private ICisDirectoryRemote cisDirectoryRemote;
	private IEventMgr eventManager;

	private String localJID;
	private IIdentity localIdentity;


	private Map<String, List<Participant>> cisMemberCallbackHash;

	private String symbolicLocation;
	private String interests;
	private String workPosition;

	private LocalContextListener contextListener;


	private Map<String, String> remoteCSSToContext;
	private Map<String, List<CisAdvertisementRecord>> directoryCallbackHash;
	private Set<ICommunitySuggestion> waitingSuggestions;

	public ECAManager() {

		//INIT LOCAL VARIABLES
		this.symbolicLocation=null;
		this.interests=null;
		this.workPosition=null;
		this.remoteCSSToContext = new HashMap<String, String>();
		this.directoryCallbackHash = new HashMap<String, List<CisAdvertisementRecord>>();
		this.cisMemberCallbackHash = new HashMap<String, List<Participant>>();
		this.waitingSuggestions = new HashSet<ICommunitySuggestion>();
	}

	public void initECAManager() {		
		log.debug("Init ECAManager");
		this.localIdentity = this.commManager.getIdManager().getThisNetworkNode();
		this.localJID = localIdentity.getBareJid();
		this.contextListener = new LocalContextListener(this, this.ctxBroker);

		//GET THIS CSS LOCAL CONTEXT && REGISTER TO GET MY CONTEXT UPDATES
		initContext();

		//REGISTER FOR ICO EVENTS
		registerForICOEvents();

		//SEARCH FOR RELATED REMOTE CSS's
		getRelatedCSS();

		//REQUEST TO GET REMOTE CSS CONTEXT
		this.remoteContextRetriever.registerECAManager(this);

		//NOW SETUP A TIMER TASK TO DO THIS RANDOMLY
		scheduleTask(); 
	}


	private CtxAttribute retrieveContext(CtxIdentifier ctxIdentifier) {
		if(ctxIdentifier!=null) {
			CtxAttribute attribute = null;
			try {
				attribute = (CtxAttribute) this.ctxBroker.retrieve(ctxIdentifier).get();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(attribute!=null) {
				return attribute;
			}
		}
		return null;

	}

	private CtxIdentifier getContextID(String ctxAttributeType) {
		List<CtxIdentifier> ctxIdentifiers = new ArrayList<CtxIdentifier>();
		try {
			ctxIdentifiers = this.ctxBroker.lookup(this.localIdentity, ctxAttributeType).get();

			if(ctxIdentifiers.size()>0) {
				return ctxIdentifiers.get(0);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void checkLocalContext() {
		if(!this.contextListener.isWatching(CtxAttributeTypes.LOCATION_SYMBOLIC)) {
			CtxIdentifier locationID = getContextID(CtxAttributeTypes.LOCATION_SYMBOLIC);
			if(locationID!=null) {
				setLocalContext(retrieveContext(locationID));
				this.contextListener.registerForUpdates(locationID);
			} else {
				log.debug("Location is not available (not stored in context)");
			}
		}
		if(!this.contextListener.isWatching(CtxAttributeTypes.INTERESTS)) {
			CtxIdentifier interestID = getContextID(CtxAttributeTypes.INTERESTS);
			if(interestID!=null) {
				setLocalContext(retrieveContext(interestID));
				this.contextListener.registerForUpdates(interestID);
			} else {
				log.debug("Interests is not available (not stored in context)");
			}
		}
		if(!this.contextListener.isWatching(CtxAttributeTypes.WORK_POSITION)) {
			CtxIdentifier workPositionID = getContextID(CtxAttributeTypes.WORK_POSITION);
			if(workPositionID!=null) {
				setLocalContext(retrieveContext(workPositionID));	
				this.contextListener.registerForUpdates(workPositionID);
			} else {
				log.debug("Work Position is not available (not stored in context)");
			}
		}
	}

	private void initContext() {		
		CtxIdentifier locationID = getContextID(CtxAttributeTypes.LOCATION_SYMBOLIC);
		CtxIdentifier interestID = getContextID(CtxAttributeTypes.INTERESTS);
		CtxIdentifier workPositionID = getContextID(CtxAttributeTypes.WORK_POSITION);
		if(locationID!=null) {
			setLocalContext(retrieveContext(locationID));
			this.contextListener.registerForUpdates(locationID);
		}
		if(interestID!=null) {
			setLocalContext(retrieveContext(interestID));
			this.contextListener.registerForUpdates(interestID);
		}
		if(workPositionID!=null) {
			setLocalContext(retrieveContext(workPositionID));	
			this.contextListener.registerForUpdates(workPositionID);
		}
	}

	@Override
	public void getRelatedCSS() {
		log.debug("Searching for related CSS's");
		List<CssAdvertisementRecord> cssAdverts = new ArrayList<CssAdvertisementRecord>();
		Set<String> relatedCSS = new HashSet<String>();
		try {
			cssAdverts = this.cssManager.getCssFriends().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(CssAdvertisementRecord cssAdv :cssAdverts) {
			if(!cssAdv.getId().equals(this.localJID))
				relatedCSS.add(cssAdv.getId());
		}
		Requestor requestor = new Requestor(this.localIdentity);
		for(ICis cis : this.cisManager.getCisList()) {
			log.debug("Searching for members of cis " + cis.getName());
			String uuid = UUID.randomUUID().toString();
			CisMembersCallback callback = new CisMembersCallback(uuid);
			cis.getListOfMembers(requestor, callback);
			synchronized (cisMemberCallbackHash) {
				while(!cisMemberCallbackHash.containsKey(uuid)) {
					try {
						cisMemberCallbackHash.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for(Participant remoteCSS : this.cisMemberCallbackHash.remove(uuid)) {
					relatedCSS.add(remoteCSS.getJid());
				}
			}
		}
		log.debug("Related CSS's : " + relatedCSS.toString());
		this.remoteContextRetriever.registerRemoteCSS(relatedCSS);
	}

	class CisMembersCallback implements ICisManagerCallback {

		private String uuid;

		public CisMembersCallback(String uuid) {
			this.uuid = uuid;
			log.debug("Callback init");
		}

		@Override
		public void receiveResult(CommunityMethods communityResultObject) {
			log.debug("Got result! : " + communityResultObject.toString());//.getWhoResponse().getParticipant());
			Iterator<Participant> it = communityResultObject.getWhoResponse().getParticipant().iterator();
			while(it.hasNext()) {
				if(it.next().getJid().equals(localJID)) {
					it.remove();
				}
			}
			synchronized (cisMemberCallbackHash) {
				cisMemberCallbackHash.put(uuid, communityResultObject.getWhoResponse().getParticipant());
				cisMemberCallbackHash.notify();
			}
		}

	}

	@Override
	public void addUsersContext(List<CtxAttribute> attributes) {
		//LIST OF ATTS COME FROM ONLY ONE USER
		String remoteCSS = attributes.get(0).getOwnerId();
		String context = null;
		Map<String, String> remoteContextCopy;

		for(CtxAttribute att : attributes) {

			if(att.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)) {
				//DO SOMETHING WITH LOCATION
			} else if(att.getType().equals(CtxAttributeTypes.INTERESTS)) {
				String value = att.getStringValue();
				if(value!=null && value.trim().length()>0) {
					if(context==null) {
						context=value;
					} else {
						context = context.concat(", " + value);
					}
				}
			} else if(att.getType().equals(CtxAttributeTypes.WORK_POSITION)) {
				String value = att.getStringValue();
				if(value!=null && value.trim().length()>0) {
					if(context==null) {
						context=value;
					} else {
						context = context.concat(", " + value);
					}
				}
			}
		}


		synchronized(remoteCSSToContext) {
			if(context!=null) {
				remoteCSSToContext.put(remoteCSS, context);
			} else {
				remoteCSSToContext.remove(remoteCSS);
			}
			remoteContextCopy = new HashMap<String, String>(remoteCSSToContext);
		}

		String localContext = getLocalContext();
		if(localContext!=null ) {
			getContextSuggestions(localContext, remoteContextCopy);
		}
	}

	private synchronized String getLocalContext() {
		if(this.interests==null && this.workPosition==null) {
			return null;
		} else if (this.interests==null) {
			return this.workPosition;
		} else if (this.workPosition==null) {
			return this.interests;
		}
		return this.interests.concat(", " + this.workPosition);
	}

	@Override
	public synchronized void setLocalContext(CtxAttribute ctxAtt) {
		if(ctxAtt!=null) {
			String type = ctxAtt.getType();
			if(type.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)) {
				this.symbolicLocation = ctxAtt.getStringValue();
			}else if(type.equals(CtxAttributeTypes.WORK_POSITION)) {
				this.workPosition = ctxAtt.getStringValue();			
			}else if(type.equals(CtxAttributeTypes.INTERESTS)) {
				this.interests = ctxAtt.getStringValue();
			}
		}
	}

	private void registerForICOEvents() {
		this.eventManager.subscribeInternalEvent(this, new String[]{EventTypes.ICO_RECOMMENDTION_EVENT}, null);
	}



	private void scheduleTask() {
		//RANDOM ANALYSIS SO MULTIPLE CSS's DON'T COME TO THE SAME CONCLUSION
		//AT THE SAME TIME
		log.debug("Scheduling analysis task");
		long now = Calendar.getInstance().getTimeInMillis();
		long week = now + (7*24*60*60*1000);
		Random r = new Random();
		long schedule = now+((long)(r.nextDouble()*(week-now)));
		Timer analyseTimer = new Timer();
		AnalyseTask analyseTask = new AnalyseTask(this);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, +1);
		analyseTimer.scheduleAtFixedRate(analyseTask, cal.getTime(), 2*60*1000);//new Date(schedule), (7*24*60*60*1000));
	}




	//TODO GETCIS DOES NOT WORK FOR REMOTE CIS's
	//ADD SOME STUFF TO CATCH MY REMOTE CIS'S

	private void getContextSuggestions(String localContext, Map<String, String> userToContext) {
		Map<String, Set<String>> decisions = SynonymRetriever.getSuggestions(localContext, userToContext);
		log.debug("Retrieved " + decisions.size() + " decisions" + decisions.keySet().toString());
		for(String decision : decisions.keySet()) {
			log.debug("Checking cis 's for " + decision);
			String cisID = checkSimilarCISExists(decision);
			log.debug("We have id: " + cisID);
			if(cisID!=null) {
				if(!isMemberOfCIS(cisID)) {
					log.debug(" I am not a member cis");
//					Set<String> remoteCSS = decisions.get(decision);
//					ICis cis = this.cisManager.getCis(cisID);
//					log.debug("I have cis " + cis.getName());
//					Requestor requestor = new Requestor(this.localIdentity);
//					String uuid = UUID.randomUUID().toString();
//					CisMembersCallback callback = new CisMembersCallback(uuid);
//					log.debug("Calling callback");
//					cis.getListOfMembers(requestor, callback);
//					synchronized (this.cisMemberCallbackHash) {
//						log.debug("in sync");
//						while(!cisMemberCallbackHash.containsKey(uuid)) {
//							try {
//								log.debug("Waiting for cis members");
//								this.cisMemberCallbackHash.wait();
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//
//						List<Participant> participants = this.cisMemberCallbackHash.remove(uuid);
//
//						for(Participant p : participants) {
//							if(remoteCSS.contains(p.getJid())) {
//								remoteCSS.remove(p.getJid());
//							}
//						}
//					}
//					log.debug("out of sync");
//					log.debug("CIS for " + decision + " already exisits. Suggest to join");
					log.debug("Getting cis name");
					String cisName = getCisName(cisID);//this.cisManager.getCis(cisID).getName();
					log.debug("Got cis name");
					ICommunitySuggestion cisSuggestion = createSuggestion(cisName, new ArrayList<String>(), new ArrayList<String>(), "join");
					sendSuggestion(cisSuggestion);
				} else 
				{
					log.debug("Decision : " + decision + ". This CSS is a member of a similar CIS.");
				}
			} else { //CREATE CIS
				log.debug("CIS for suggestion " + decision + " does not exist. Suggest to create");
				ICommunitySuggestion cisSuggestion = createSuggestion(decision+" community", new ArrayList<String>(decisions.get(decision)), new ArrayList<String>(), "new");
				sendSuggestion(cisSuggestion);		
			}
		}
		//WITH THE SUGGESTIONS, CHECK THAT CIS EXIST/DONT EXIST FOR THE SUGGESTIONS THEN
		//CREATE ICISSUGGESTION AND SEND IT!!
	}
	
	private String getCisName(String cisID) {
		String cisName = null;
		String uuid = UUID.randomUUID().toString();
		CisDirectoryCallback callback = new CisDirectoryCallback(uuid);
		this.cisDirectoryRemote.findAllCisAdvertisementRecords(callback);
		synchronized (this.directoryCallbackHash) {
			while(!this.directoryCallbackHash.containsKey(uuid)) {
				try {
					this.directoryCallbackHash.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(CisAdvertisementRecord cisAdv : this.directoryCallbackHash.remove(uuid)) {
				if(cisAdv.getId().equals(cisID)) {
					cisName = cisAdv.getName();
					break;
				}
			}
		}
		return cisName;
	}

	private boolean checkSuggestion(ICommunitySuggestion cisSuggestion) {
		log.debug("Sending the suggestion");
		boolean sendSuggestion = true;
		synchronized (waitingSuggestions) {
			for(ICommunitySuggestion cis : waitingSuggestions) {
				if(cis.getName().equals(cisSuggestion.getName())) {
					sendSuggestion = false;
				}
			}
			if(sendSuggestion) {
				waitingSuggestions.add(cisSuggestion);
			}

		}
		return sendSuggestion;
	}

	private void sendSuggestion(ICommunitySuggestion cisSuggestion) {
		if(checkSuggestion(cisSuggestion)) {
			InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", (Serializable) cisSuggestion);
			try {
				this.eventManager.publishInternalEvent(event);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.debug("This suggestion is waiting result");
		}
	}

	private ICommunitySuggestion createSuggestion(String name, ArrayList<String> members, ArrayList<String> conditions, String type) {
		CommunitySuggestion cisSuggestion = new CommunitySuggestion();
		cisSuggestion.setName(name);
		cisSuggestion.setMembersList(members);
		cisSuggestion.setConditionsList(conditions);
		cisSuggestion.setSuggestionType(type);
		return cisSuggestion;
	}

	private boolean isMemberOfCIS(String cisID) {
		for(ICis cis : this.cisManager.getCisList()) {
			if(cis.getCisId().equals(cisID)) {
				return true;
			}
		}
		return false;
	}


	private String checkSimilarCISExists(String decision) {
		List<CisAdvertisementRecord> cisAdvs = new ArrayList<CisAdvertisementRecord>();
		String uuid = UUID.randomUUID().toString();
		CisDirectoryCallback callback = new CisDirectoryCallback(uuid);

		this.cisDirectoryRemote.findAllCisAdvertisementRecords(callback);
		synchronized (directoryCallbackHash) {
			while(!directoryCallbackHash.containsKey(uuid)) {
				try {
					directoryCallbackHash.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cisAdvs = directoryCallbackHash.remove(uuid);
		}

		for(CisAdvertisementRecord cis : cisAdvs) {
			//NO FIELDS TO GET CIS DESCRIPTION?!?!
			//SO JUST CHECK THE NAME FOR THE 
			if(cis.getName().contains(decision)) {
				return cis.getId();
			}
		}
		return null;
	}

	class CisDirectoryCallback implements ICisDirectoryCallback {
		private String uuid;

		public CisDirectoryCallback(String uuid) {
			this.uuid=uuid;
		}

		@Override
		public void getResult(List<CisAdvertisementRecord> arg0) {
			synchronized (directoryCallbackHash) {
				directoryCallbackHash.put(uuid, arg0);
				directoryCallbackHash.notify();
			}

		}

	}


	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ICSSInternalManager getCssManager() {
		return cssManager;
	}

	public void setCssManager(ICSSInternalManager cssManager) {
		this.cssManager = cssManager;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public RemoteContextRetriever getRemoteContextRetriever() {
		return remoteContextRetriever;
	}

	public void setRemoteContextRetriever(
			RemoteContextRetriever remoteContextRetriever) {
		this.remoteContextRetriever = remoteContextRetriever;
	}

	public ICisDirectoryRemote getCisDirectoryRemote() {
		return cisDirectoryRemote;
	}

	public void setCisDirectoryRemote(ICisDirectoryRemote cisDirectoryRemote) {
		this.cisDirectoryRemote = cisDirectoryRemote;
	}

	public IEventMgr getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventMgr eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInternalEvent(InternalEvent arg0) {
		if(arg0.geteventType().equals(EventTypes.ICO_RECOMMENDTION_EVENT)) {
			log.debug("Caught ICO event");
			if(arg0.geteventName().equals("suggestionFailed")) {
				log.debug("We have a failed suggestion event!");
				ICommunitySuggestion suggestion = (ICommunitySuggestion) arg0.geteventInfo();
				synchronized (waitingSuggestions) {
					if(waitingSuggestions.remove(suggestion)) {
						log.debug("Suggestion has been removed!");
					} else {
						log.debug("Suggestion was not in the waiting list!");
					}
				}
			} else if (arg0.geteventName().equals("suggestionAccepted")) {
				log.debug("We have an accepted suggestion event!");
				ICommunitySuggestion suggestion = (ICommunitySuggestion) arg0.geteventInfo();
				synchronized (waitingSuggestions) {
					if(waitingSuggestions.remove(suggestion)) {
						log.debug("Suggestion has been removed!");
					} else {
						log.debug("Suggestion was not in the waiting list!");
					}
				}
			}
		}

	}









}
