package org.societies.platform.socialdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.api.sns.Checkin;
import org.societies.api.sns.Event;
import org.societies.api.sns.Message;
import org.societies.api.sns.SocialDataState;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;
import org.societies.platform.FoursquareConnector.impl.FoursquareConnectorImpl;
import org.societies.platform.TwitterConnector.impl.TwitterConnectorImpl;
import org.societies.platform.sns.connecor.linkedin.LinkedinConnector;
import org.societies.platform.sns.connector.googleplus.GooglePlusConnector;
import org.springframework.stereotype.Service;


@Service
public class SocialData implements ISocialData{


	private static final Logger logger = LoggerFactory.getLogger(SocialData.class);
	
	private INetworkNode 		cssNodeId;
	private IIdentity 		cssOwnerId;
	private boolean			restoreState = false;
	
	
	// css user is modeled as individualCtxEntity
	private IndividualCtxEntity individualCtxEntity;
	private SocialDataState 	state = SocialDataState.INIT;
	
	/*
	 * Platform services  
	*/
	private ICtxBroker internalCtxBroker;
	private ICommManager commsMgr;
	private IIdentityManager identityMgr;
	
	//this class provides the functionality for storing sns data to context
	private ContextUpdater ctxUpdater;
	
	public ICtxBroker getInternalCtxBroker() {
		logger.info(this.getClass().getName()+": Return ctxBroker");
		return internalCtxBroker;
	}

	public void setInternalCtxBroker(ICtxBroker ctxBroker) {
		this.internalCtxBroker = ctxBroker;
		logger.info(this.getClass().getName()+": Got ctxBroker");
	}


	public ICommManager getCommsMgr() {
		logger.info(this.getClass().getName()+": Return commsMgr");
		return commsMgr;
	}

	
	public void setCommsMgr(ICommManager commsMgr) {
		logger.info(this.getClass().getName()+": Got commsMgr");
		this.commsMgr 		= commsMgr;
		this.identityMgr	= this.commsMgr.getIdManager();
	}

	
	HashMap<String, ISocialConnector> connectors = new HashMap<String, ISocialConnector>();

	Map<String, Object> 	socialFriends;
	Map<String, Object>	socialGroups;
	Map<String, Object>	socialProfiles;
	Map<String, Object>	socialActivities;



	long lastUpate ;


	public SocialData(){

		socialFriends 			= new HashMap<String, Object>();
		socialGroups			= new HashMap<String, Object>();
		socialProfiles			= new HashMap<String, Object>();
		socialActivities		= new HashMap<String, Object>();

		lastUpate			= new Date().getTime();
		
		setState(SocialDataState.INIT);
		
	}

	HashMap <String,CtxAttributeIdentifier> connectorsInCtxBroker = new HashMap<String, CtxAttributeIdentifier>();

 private void initSocialData(){
		
		 

	setState(SocialDataState.INIT);
	logger.debug("SocialData initialization ....");
	logger.debug("Inititalize Friends, Groups, Profiles, Activities data structures... ");

	socialFriends		= new HashMap<String, Object>();
	socialGroups 		= new HashMap<String, Object>();
	socialProfiles 		= new HashMap<String, Object>();
	socialActivities	= new HashMap<String, Object>();
	
	
	lastUpate = new Date().getTime();

	logger.debug(" Start time:" + lastUpate);

	try {

	    this.cssNodeId = identityMgr.getThisNetworkNode();
	    this.cssOwnerId = identityMgr.fromJid(this.cssNodeId.getBareJid());

	    logger.info("CSS Data >>>> CssNodeId:" + this.cssNodeId
		    + " -  CssOwnerId:" + this.cssOwnerId);
	} catch (InvalidFormatException e) {
	    logger.error("Unable to read CssNode id:" + e, e);
	    e.printStackTrace();
	}

	logger.debug("Prepare CtxBroker to store Social Data ...");

	// store SNS data to context
	this.ctxUpdater = new ContextUpdater(this.internalCtxBroker,
		this.cssOwnerId);

	restoreStatusBeforeShoutDown();
	logger.info("SocialData Bundle started correctly");
    }
    
    
    
    
    /**
     *	Restore data using context   
     */
    public void restoreStatusBeforeShoutDown() {
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Save data into the context
	try {
	    logger.debug("CtxBroker instance .... " + internalCtxBroker.toString());

	    individualCtxEntity = internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
	    logger.debug("Indivudual CtxEntity: " + individualCtxEntity.getId().toString());

	    // Restore the connected connector
	    Set<CtxAttribute> connectors = individualCtxEntity
		    .getAttributes(CtxAttributeTypes.SOCIAL_NETWORK_CONNECTOR);
	   
	    if (connectors != null && !connectors.isEmpty()) {

		restoreState = true;
		for (CtxAttribute ctxConn : connectors) {
		    
		    ConnectorBean connectorBlob;
		    try {
			connectorBlob = (ConnectorBean) SerialisationHelper.deserialise(ctxConn.getBinaryValue(), this
					.getClass().getClassLoader());
			logger.info("connector bean:" + connectorBlob);

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(ISocialConnector.AUTH_TOKEN,
				connectorBlob.getToken());
			params.put(ISocialConnector.IDENTITY,
				connectorBlob.getIdentity());

			ISocialConnector conn =createConnector(
				SocialNetworkUtils.getSocialNetowkName(connectorBlob.getSnName()), params);
			

			// Add new Connector
			this.addSocialConnector(conn);

			logger.info("Restore Connector: "
				+ conn.getID() + " for "
				+ conn.getSocialNetwork().value());
			
			connectorsInCtxBroker.put(conn.getID(), ctxConn.getId());

		    } catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);

		    }

		    updateSocialData();

		}
	    } else
		logger.warn("No Connector in CtxBroker!");

	} catch (CtxException e) {
	    logger.error("Error while reading from CtxBroker:" + e);
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    restoreState = false;
	}

    }
	 

////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void addSocialConnector(ISocialConnector socialConnector) throws Exception {
		
		if (connectors.containsKey(socialConnector.getID())){
			logger.warn("the connector id :"+socialConnector.getID() +" is already loaded");
			throw new Exception("Connetor already exists");
		}
		
		connectors.put(socialConnector.getID(), socialConnector);
		if (!connectorsInCtxBroker.containsKey(socialConnector.getID())){
			
			
			if (socialConnector.getToken()!=null && !restoreState && individualCtxEntity!=null){
				
				ConnectorBean bean = new ConnectorBean();
				bean.setExipres(socialConnector.getTokenExpiration());
				bean.setToken(socialConnector.getToken());
				bean.setId(socialConnector.getID());
				bean.setSnName(socialConnector.getConnectorName());
				bean.setIdentity(individualCtxEntity.getId().toUriString());  
				
				logger.info("[ADD] Connector ID:"+socialConnector.getID()+ " for "+socialConnector.getConnectorName() + " in CtxBroker");
				logger.info("connector bean:"+bean);
				CtxAttribute connectorCtxAttr = internalCtxBroker.createAttribute(individualCtxEntity.getId(), CtxAttributeTypes.SOCIAL_NETWORK_CONNECTOR).get();
				connectorCtxAttr.setBinaryValue(SerialisationHelper.serialise(bean));
				
				internalCtxBroker.update(connectorCtxAttr);
				connectorsInCtxBroker.put(socialConnector.getID(), connectorCtxAttr.getId());
				logger.info("Stored into the CtxBroker with id:"+connectorCtxAttr.getId());
			}
			else{
				logger.error("Missing TOKEN in the connector bean!!!!");
			}
		}
		
		logger.debug("Add connector "+socialConnector.getID());
	}


	
	@Override
	public void removeSocialConnector(String connectorId) throws Exception{

	    	logger.debug("Check if the id "+connectorId + " is associated to a connector ...");
		if (connectors.containsKey(connectorId)){
			removeSocialConnector(connectors.get(connectorId));
		}
		else {
		    
		    logger.error("The provided id is not associated to any of the connector available.");
		}

	}
	
    @Override
    public void removeSocialConnector(ISocialConnector connector) throws Exception {

	if (connector == null)
	    throw new Exception("Invalid Connector - IS NULL");
	
	logger.debug("Remove " + connector.getConnectorName() + " connector...");

	if (connectorsInCtxBroker.containsKey(connector.getID())) {
	    logger.debug("Remove data from ctxBroker...");
	    internalCtxBroker.remove(connectorsInCtxBroker.get(connector.getID()));
	    connectorsInCtxBroker.remove(connector.getID());
	    
	    if (ctxUpdater != null){
		    ctxUpdater.removeConnectorData(connector);
		    logger.debug("CtxBroker cleaned");
	    }
	    else logger.warn("CtxBroker is not instanciated correctly");
	  
		
	} 
	else
	    logger.warn("ctxBroker doesn't have any connector with this ID");
	
	
	

	logger.debug("Remove to the list of connectors (INTERNAL)");
	if (connectors.containsKey(connector.getID())) {
	    connectors.remove(connector.getID());
	    logger.debug("Removed succesfully");
	}

	updateSocialData();

    }
	


	@Override
	public List<ISocialConnector> getSocialConnectors() {
		Iterator<ISocialConnector>it = connectors.values().iterator();
		List <ISocialConnector> list = new ArrayList<ISocialConnector>();
		while (it.hasNext()){
			list.add(it.next());
		}
		return list;
	}


	
	@Override
	public List<Object> getSocialPeople() {
	    
	     return new ArrayList(socialFriends.values());
	}

	@Override
	public List<Object> getSocialActivity() {
		List friends = new ArrayList();
		Iterator it = socialActivities.values().iterator();
		while (it.hasNext()){
			Collection acts = (Collection)it.next();
			friends.addAll(acts);
		}
		return friends;
	}

	@Override
	public List<Object> getSocialGroups() {
		return new ArrayList(socialGroups.values());
	}

	@Override
	public List<Object> getSocialProfiles() {
		return new ArrayList(socialProfiles.values());
	}

	
	@Override 
	public void updateSocialData() {
		
	    
	    	new PullDataFromSN(this).execute();
	    	
//		setState(SocialDataState.DOWNLOADING_FROM_SN);
//		
//		socialFriends 			= new HashMap<String, Object>();
//		socialGroups			= new HashMap<String, Object>();
//		socialProfiles			= new HashMap<String, Object>();
//		socialActivities		= new HashMap<String, Object>();
//
//		Iterator<ISocialConnector>it = connectors.values().iterator();
//		
//		socialActivities = new HashMap<String, Object>();  // reset old Activities
//		
//		
//		
//		while (it.hasNext()){
//			
//			
//			ISocialConnector connector = it.next();
//			logger.debug("Update " + connector.getConnectorName()  + " data...");
//			getActivities(connector);
//			updateProfile(connector);
//			updateGroups(connector);
//			updateFriends(connector);
//			
//			
//		}
//
//
//		logger.debug("Update Context Broker with Sodical data Info...");
//		// store SNS data to context
//		try{
//			if(this.ctxUpdater != null ) {
//				List<ISocialConnector> list = new ArrayList<ISocialConnector>();
//				list.addAll(connectors.values());
//				this.ctxUpdater.updateSocialData(list);
//			}
//		} catch (Exception e) {
//			logger.error("Context DB not updated due to "+e, e);
//		}
//		/// UPDATE ALL DATA
//		lastUpate = new Date().getTime();
//		logger.debug("Social data completed @"+ new Date(lastUpate).toGMTString());

	}

	
	
	void updateContextBroker(){
	    	logger.debug("Update Context Broker with Social updates...");
		try{
			if(this.ctxUpdater != null ) {
				List<ISocialConnector> list = new ArrayList<ISocialConnector>();
				list.addAll(connectors.values());
				this.ctxUpdater.updateSocialData(list);
			}
		} catch (Exception e) {
			logger.error("Context DB not updated due to "+e, e);
		}
		/// UPDATE ALL DATA
		lastUpate = new Date().getTime();
		logger.debug("Social data completed @"+ new Date(lastUpate).toGMTString());
		
		if (connectors.size()==0)
		    setState(SocialDataState.ANY_CONNECTOR);
		else
		    setState(SocialDataState.WITH_SOME_SOCIAL_DATA);
		
		logger.debug("Social data State:" + state);
		
	}
	
	
	


//	private void updateGroups(ISocialConnector connector) {
//		GroupConverter parser = GroupConveterFactory.getPersonConverter(connector);
//		List<Group> groups = parser.load(connector.getUserGroups());
//		Iterator<Group> it = groups.iterator();
//		while (it.hasNext()){
//			Group g = it.next();
//			if (socialGroups.containsKey(g.getId())){
//				socialGroups.remove(g.getId().getGroupId());
//				// Send notification of UPDATE?
//			}	
//			else {
//				// Send Notitication of NEW PROFILE?
//			}
//			socialGroups.put(g.getId().getGroupId(), g);
//		}
//
//	}
//
//
//	private void getActivities(ISocialConnector connector) {
//
//		ActivityConverter parser = ActivityConveterFactory.getActivityConverter(connector);
//		List<?> activities = parser.load(connector.getUserActivities());
//		socialActivities.put(connector.getID(), activities);
//	}
//
//
//	private void updateFriends(ISocialConnector connector) {
//
//		FriendsConverter parser = FriendsConveterFactory.getPersonConverter(connector);
//		List<Person> friends = parser.load(connector.getUserFriends());
//		Iterator<Person> it = friends.iterator();
//
//		while (it.hasNext()){
//			Person friend = it.next();
//			if (socialFriends.containsKey(friend.getId())){
//				socialFriends.remove(friend.getId());
//				// Send notification of UPDATE?
//			}	
//			else {
//				// Send Notitication of NEW PROFILE?
//			}
//			socialFriends.put(friend.getId(), friend);
//		}
//	}


//	private void updateProfile(ISocialConnector connector) {
//		
//		
//		logger.debug("Update "+ connector.getConnectorName() + " profile ... ");
//		
//		PersonConverter parser = PersonConverterFactory.getPersonConverter(connector);
//		Person profile = parser.load(connector.getUserProfile());
//
//		if (socialProfiles.containsKey(profile.getId())){
//			socialProfiles.remove(profile.getId());
//			// Send notification of UPDATE?
//		}	
//		else {
//			// Send Notitication of NEW PROFILE?
//			logger.debug("Add a new Profile in the list");
//		}
//		socialProfiles.put(profile.getId(), profile);
//		logger.debug("Add a new Profile in the list");
//			
//	}


	@Override
	public long getLastUpdate() {
		return lastUpate;
	}


	

	@Override
	public boolean isAvailable(ISocialConnector connector) {

		if (connector==null) return false;

		return (connectors.containsKey(connector.getID()));
	}


	

//	@Override
//	public ISocialConnector createConnector(SocialNetwork socialNetwork, Map<String, String> params) {
//		
//		String name="me";
//		try{
//			name=this.cssOwnerId.getJid();
//			if (params.containsKey(ISocialConnector.IDENTITY)) name=params.get(ISocialConnector.IDENTITY);
//		}
//		catch(Exception ex){}
//
//		logger.info("Create a new connector with "+ socialNetwork + " name");
//		switch(socialNetwork) {
//			case FACEBOOK:
//			    	return (ISocialConnector) new FacebookConnectorImpl(params.get(ISocialConnector.AUTH_TOKEN), name);
//	
//			case TWITTER:
//			    	return (ISocialConnector) new TwitterConnectorImpl (params.get(ISocialConnector.AUTH_TOKEN), name);
//	
//			case FOURSQUARE:
//				return (ISocialConnector) new FoursquareConnectorImpl(params.get(ISocialConnector.AUTH_TOKEN), name);
//	
//			case LINKEDIN:
//				return (ISocialConnector) new LinkedinConnector(params.get(ISocialConnector.AUTH_TOKEN), name);
//	
//			default : 
//			    return null;
//		}
//	}
//	
	public void postData(SocialNetwork snName, Map<String, ?> data) {
		String message = SocialNetworkUtils.genJsonPostMessage(data);
		logger.info("Post "+message+" to "+ snName + " SN");
		List<ISocialConnector> results = getConnectorsByName(snName);
		Iterator<ISocialConnector> it = results.iterator();
		while (it.hasNext()){
			ISocialConnector conn = it.next();
			logger.debug("Posting using "+conn.getID() +" connector");
			conn.post(message);
		}

	}


    @Override
    public ISocialConnector createConnector( SocialNetwork socialNetwork, Map<String, String> params) {

	//to debug....
	for(String key: params.keySet()) logger.debug(" --- " +key +" ->" + params.get(key));
	
	String name = "me";
	try {
	    name = this.cssOwnerId.getJid();
	    if (params.containsKey(ISocialConnector.IDENTITY))
		name = params.get(ISocialConnector.IDENTITY);
	} catch (Exception ex) {
	    logger.warn("Identity not found: " + ex, ex);
	}

	
	switch (socialNetwork) {

	case FACEBOOK:
	    logger.debug("Create FACEBOOK connector...");
	    return (ISocialConnector) new FacebookConnectorImpl(
		    params.get(ISocialConnector.AUTH_TOKEN), name);

	case TWITTER:
	    logger.debug("Create TWITTER connector...");
	    return (ISocialConnector) new TwitterConnectorImpl(
		    params.get(ISocialConnector.AUTH_TOKEN), name);

	case FOURSQUARE:
	    logger.debug("Create FOURSQUARE connector...");
	    return (ISocialConnector) new FoursquareConnectorImpl(
		    params.get(ISocialConnector.AUTH_TOKEN), name);

	case LINKEDIN:
	    logger.debug("Create LINKEDIN connector...");
	    return (ISocialConnector) new LinkedinConnector(
		    params.get(ISocialConnector.AUTH_TOKEN), name);
	    
	case GOOGLEPLUS:
	    logger.debug("Create GOOGLEPLUS connector...");
	    return (ISocialConnector) new GooglePlusConnector(
		    params.get(ISocialConnector.AUTH_TOKEN), name);

	default:
	    logger.error("Hope to don't reach this part otherwise is an error...");
	    return null;
	}

    }
    

    public void postMessage(SocialNetwork snName, String data) {
	logger.info("Post a Message to " + snName + " SN");
	List<ISocialConnector> results = SocialNetworkUtils
		.getConnectorsByName(snName, connectors.values());
	Iterator<ISocialConnector> it = results.iterator();
	while (it.hasNext()) {
	    ISocialConnector conn = it.next();
	    conn.post(data);
	    logger.debug("Posting using " + conn.getID() + " connector");
	}

    }

	
  
	
//    @Override
//    public void postCheckin(SocialNetwork name, Checkin bean) {
//
//	Map<String, String> params = new HashMap<String, String>();
//	params.put(ISocialData.POST_DESCR, bean.getMessage());
//	params.put(ISocialData.POST_LAT, "" + bean.getLatitude());
//	params.put(ISocialData.POST_LAT, "" + bean.getLatitude());
//	params.put(ISocialData.POST_PLACE, "" + bean.getPlaceId());
//	postData(name, params);
//
//    }
//
//    @Override
//    public void postEvent(SocialNetwork name, Event bean) {
//
//	Map<String, String> params = new HashMap<String, String>();
//	params.put(ISocialData.POST_NAME, bean.getName());
//	params.put(ISocialData.POST_FROM, bean.getFromDate());
//	params.put(ISocialData.POST_TO, bean.getToDate());
//	params.put(ISocialData.POST_PLACE, bean.getPlace());
//	params.put(ISocialData.POST_DESCR, bean.getDescription());
//	postData(name, params);
//
//    }
//
//    @Override
//    public void postMessage(SocialNetwork name, Message bean) {
//	Map<String, String> params = new HashMap<String, String>();
//	params.put(ISocialData.POST_MESSAGE, bean.getData());
//
//    }

   
    
    @Override
    public SocialDataState getStatus() {
	return state;
    }
    
    
    void setState(SocialDataState newState){
	logger.debug("SocialData is goint from "+state+ " to "+newState);
	this.state=newState;
    }

    
    
    
	private List<ISocialConnector> getConnectorsByName(SocialNetwork name){
		
	    Iterator <ISocialConnector> it = connectors.values().iterator();
		ArrayList<ISocialConnector> results = new ArrayList<ISocialConnector>();
		while (it.hasNext()){
			ISocialConnector conn = it.next();
			if (conn.getSocialNetwork().equals(name)){
				results.add(conn);
			}
		}
		return results;
	}

	

	// New Post Methods
	@Override
	public boolean isConnected(SocialNetwork name) {
		for (ISocialConnector c : connectors.values()){
		    if (c.getSocialNetwork().equals(name)) return true;
		}
		return false;
	}

	@Override
	public void postCheckin(SocialNetwork name, Checkin bean) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(ISocialData.POST_DESCR, bean.getMessage());
    	params.put(ISocialData.POST_LAT,  ""+bean.getLatitude());
    	params.put(ISocialData.POST_LAT,   ""+bean.getLatitude());
    	params.put(ISocialData.POST_PLACE, ""+bean.getPlaceId());
    	postData(name, params);
	}

	@Override
	public void postEvent(SocialNetwork name, Event bean) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(ISocialData.POST_NAME, bean.getName());
    	params.put(ISocialData.POST_FROM, bean.getFromDate());
    	params.put(ISocialData.POST_TO,   bean.getToDate());
    	params.put(ISocialData.POST_PLACE, bean.getPlace());
    	params.put(ISocialData.POST_DESCR, bean.getDescription());
    	postData(name, params);
	}

	@Override
	public void postMessage(SocialNetwork name, Message bean) {
	    	Map<String, String> params = new HashMap<String, String>();
    		params.put(ISocialData.POST_MESSAGE, bean.getData());		
	}
}
