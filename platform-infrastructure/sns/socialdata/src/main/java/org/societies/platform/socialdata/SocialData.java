package org.societies.platform.socialdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
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
import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;
import org.societies.api.internal.sns.ISocialData;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;
import org.societies.platform.FoursquareConnector.impl.FoursquareConnectorImpl;
import org.societies.platform.TwitterConnector.impl.TwitterConnectorImpl;
import org.societies.platform.sns.connecor.linkedin.LinkedinConnector;
import org.societies.platform.socialdata.converters.ActivityConverter;
import org.societies.platform.socialdata.converters.ActivityConveterFactory;
import org.societies.platform.socialdata.converters.FriendsConverter;
import org.societies.platform.socialdata.converters.FriendsConveterFactory;
import org.societies.platform.socialdata.converters.GroupConverter;
import org.societies.platform.socialdata.converters.GroupConveterFactory;
import org.societies.platform.socialdata.converters.PersonConverter;
import org.societies.platform.socialdata.converters.PersonConverterFactory;
import org.springframework.stereotype.Service;

import com.restfb.json.JsonObject;
import com.sun.org.apache.bcel.internal.generic.NEW;


@Service
public class SocialData implements ISocialData{


	private static final Logger logger = LoggerFactory.getLogger(SocialData.class);
	
	private INetworkNode 	cssNodeId;
	private IIdentity 		cssOwnerId;
	private boolean			restoreState = false;
	// css user is modeled as individualCtxEntity
	private IndividualCtxEntity individualCtxEntity;
	
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

	Map<String, Object> 			socialFriends;
	Map<String, Object>				socialGroups;
	Map<String, Object>				socialProfiles;
	Map<String, Object>	 			socialActivities;



	long lastUpate ;


	public SocialData(){

		socialFriends 			= new HashMap<String, Object>();
		socialGroups			= new HashMap<String, Object>();
		socialProfiles			= new HashMap<String, Object>();
		socialActivities		= new HashMap<String, Object>();

		lastUpate				= new Date().getTime();
		
		logger.info("SocialData Bundle is started");
		
	}

	HashMap <String,CtxAttributeIdentifier> connectorsInCtxBroker = new HashMap<String, CtxAttributeIdentifier>();

	 private void initSocialData(){
		
		 /*
		 	socialFriends 			= new HashMap<String, Object>();
			socialGroups			= new HashMap<String, Object>();
			socialProfiles			= new HashMap<String, Object>();
			socialActivities		= new HashMap<String, Object>();

			lastUpate				= new Date().getTime();
	    	*/
	    	

	    	try  {

	    		this.cssNodeId	 	= identityMgr.getThisNetworkNode();
	    		this.cssOwnerId 	= identityMgr.fromJid(this.cssNodeId.getBareJid());
	 	    		
	    		logger.info("CssNodeId:"+this.cssNodeId + " -  CssOwnerId:"+this.cssOwnerId);
	    	} 
	    	catch (InvalidFormatException e) {
	    		e.printStackTrace();
	    	}
	    
	    	// store SNS data to context
			this.ctxUpdater = new ContextUpdater(this.internalCtxBroker,this.cssOwnerId);
	    	
	    	
	    	logger.info("SocialData Bundle is started");
	    	
	    	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    	
	    	// Save data into the context
			try {
				logger.info(internalCtxBroker.toString());
				
				
				individualCtxEntity  = internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
				logger.info("Indivudual Context Entity: " + individualCtxEntity.getId().toString());
				
				
				// Restore the connected connector
				Set<CtxAttribute> connectors= individualCtxEntity.getAttributes(CtxAttributeTypes.SOCIAL_NETWORK_CONNECTOR);
				if (connectors != null && !connectors.isEmpty()){
					
					restoreState = true;
					for(CtxAttribute ctxConn : connectors){
						ConnectorBean connectorBlob;
						try {
							connectorBlob = (ConnectorBean) SerialisationHelper.deserialise(ctxConn.getBinaryValue(), this.getClass().getClassLoader());
							logger.info("connector bean:"+connectorBlob);
							
							HashMap<String, String> params = new HashMap<String, String>();
							params.put(ISocialConnector.AUTH_TOKEN, connectorBlob.getToken());
							params.put(ISocialConnector.IDENTITY, connectorBlob.getIdentity());
							
							connectorsInCtxBroker.put(connectorBlob.getId(), ctxConn.getId());
							this.addSocialConnector(createConnector(getSocialNetowkName(connectorBlob.getSnName()), params));
							
							logger.info("Restore Connector: "+connectorBlob.getId()+ " for "+connectorBlob.getSnName());
							
						} 
						catch (Exception e) {
							logger.error(e.getLocalizedMessage(), e);
							
						}
						
					updateSocialData();
						
					}
				}
				else logger.warn("No SocialNetworkConnector stored in the Broker!");
				
			}
			catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				restoreState=false;
			}

	 }
	 
	 
	 private ISocialConnector.SocialNetwork getSocialNetowkName(String name){
			
			
			if ("facebook".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.Facebook;
			if ("twitter".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.twitter;
			if ("foursquare".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.Foursquare;
			if ("linkedin".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.linkedin;
			if ("googleplus".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.googleplus;
			
			
			return null;
		}
	 
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

	 
	
	@Override
	public void addSocialConnector(ISocialConnector socialConnector) throws Exception {
		
		if (connectors.containsKey(socialConnector.getID())){
			logger.warn("the connector id :"+socialConnector.getID() +" is already loaded");
			throw new Exception("this connetor already exists");
		}
		
		connectors.put(socialConnector.getID(), socialConnector);
		if (!connectorsInCtxBroker.containsKey(socialConnector.getID())){
			
			
			if (socialConnector.getToken()!=null && !restoreState){
				
				ConnectorBean bean = new ConnectorBean();
				bean.setExipres(socialConnector.getTokenExpiration());
				bean.setToken(socialConnector.getToken());
				bean.setId(socialConnector.getID());
				bean.setSnName(socialConnector.getConnectorName());
				bean.setIdentity("");
				
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
		
		log("Add connector "+socialConnector.getID());
	}



	@Override
	public void removeSocialConnector(String connectorId) throws Exception{

		if (connectors.containsKey(connectorId)){
			connectors.remove(connectorId);
			
			if (connectorsInCtxBroker.containsKey(connectorId)){
				internalCtxBroker.remove(connectorsInCtxBroker.get(connectorId));
				connectorsInCtxBroker.remove(connectorId);
				logger.info("Removed Connector ID:"+connectorId + " from CtxBroker");
			}
			
		}
		else throw new Exception("This connector not found");

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
		List activities = new ArrayList();
		Iterator it = socialActivities.values().iterator();
		while (it.hasNext()){
			Collection acts = (Collection)it.next();
			activities.addAll(acts);
		}
		return activities;
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
		
		Iterator<ISocialConnector>it = connectors.values().iterator();
		
		socialActivities = new HashMap<String, Object>();  // reset old Activities
		while (it.hasNext()){
			ISocialConnector connector = it.next();
			
			getActivities(connector);
			updateProfile(connector);
			updateGroups(connector);
			updateFriends(connector);
			/// UPDATE ALL DATA

		}



		lastUpate = new Date().getTime();


	}



	private void updateGroups(ISocialConnector connector) {
		GroupConverter parser = GroupConveterFactory.getPersonConverter(connector);
		List<Group> groups = parser.load(connector.getUserGroups());
		Iterator<Group> it = groups.iterator();
		while (it.hasNext()){
			Group g = it.next();
			if (socialGroups.containsKey(g.getId())){
				socialGroups.remove(g.getId().getGroupId());
				// Send notification of UPDATE?
			}	
			else {
				// Send Notitication of NEW PROFILE?
			}
			socialGroups.put(g.getId().getGroupId(), g);
		}

	}


	private void getActivities(ISocialConnector connector) {

		ActivityConverter parser = ActivityConveterFactory.getActivityConverter(connector);
		List<?> activities = parser.load(connector.getUserActivities());
		socialActivities.put(connector.getID(), activities);
	}


	private void updateFriends(ISocialConnector connector) {

		FriendsConverter parser = FriendsConveterFactory.getPersonConverter(connector);
		List<Person> friends = parser.load(connector.getUserFriends());
		Iterator<Person> it = friends.iterator();

		while (it.hasNext()){
			Person friend = it.next();
			if (socialFriends.containsKey(friend.getId())){
				socialFriends.remove(friend.getId());
				// Send notification of UPDATE?
			}	
			else {
				// Send Notitication of NEW PROFILE?
			}
			socialFriends.put(friend.getId(), friend);
		}
	}


	private void updateProfile(ISocialConnector connector) {
		
		PersonConverter parser = PersonConverterFactory.getPersonConverter(connector);
		Person profile = parser.load(connector.getUserProfile());

		
		if (socialProfiles.containsKey(profile.getId())){
			socialProfiles.remove(profile.getId());
			// Send notification of UPDATE?
		}	
		else {
			// Send Notitication of NEW PROFILE?
		}
		socialProfiles.put(profile.getId(), profile);
		
		// store SNS data to context
		try{
			if(this.ctxUpdater != null ) this.ctxUpdater.updateCtxProfile(connector);	
		
		} catch (Exception e) {
			logger.info("context DB not updated");
		}
		
				
	}


	@Override
	public long getLastUpdate() {
		return lastUpate;
	}


	private void log(String out){
		logger.info("SocialData - " +out);
	}

	@Override
	public boolean isAvailable(ISocialConnector connector) {

		if (connector==null) return false;

		return (connectors.containsKey(connector.getID()));
	}


	@Override
	public void removeSocialConnector(ISocialConnector connector)
			throws Exception {


		if (connector==null) throw new Exception("Connector is null");

		if (connectors.containsKey(connector.getID())) 
			connectors.remove(connector.getID());
		else throw new Exception("Connector not found");

	}

	@Override
	public ISocialConnector createConnector(ISocialConnector.SocialNetwork snName, Map<String, String> params) {
		
		String name="me";
		try{
			name=this.cssOwnerId.getJid();
			if (params.containsKey(ISocialConnector.IDENTITY)) name=params.get(ISocialConnector.IDENTITY);
		}
		catch(Exception ex){}

		logger.info("Create a new connector with "+snName + " name");
		switch(snName){
		case Facebook:   return (ISocialConnector) new FacebookConnectorImpl(params.get(ISocialConnector.AUTH_TOKEN), name);

		case twitter:    
			// Just for now that we don't have a way to use our persona token
			//return (ISocialConnector) new TwitterConnectorImpl();
			return (ISocialConnector) new TwitterConnectorImpl (params.get(ISocialConnector.AUTH_TOKEN), name);

		case Foursquare: 

			// Just for now ...
			// return (ISocialConnector) new FoursquareConnectorImpl();
			return (ISocialConnector) new FoursquareConnectorImpl(params.get(ISocialConnector.AUTH_TOKEN), name);

		case linkedin: 


			return (ISocialConnector) new LinkedinConnector(params.get(ISocialConnector.AUTH_TOKEN), name);

		default : return null;
		}

	}


	@Override
	public void postData(SocialNetwork snName, Map<String, ?> data) {
		String message = genJsonPostMessage(data);
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
	public void postMessage(SocialNetwork snName, String data) {
		logger.info("Post a Message to "+ snName + " SN");
		List<ISocialConnector> results = getConnectorsByName(snName);
		Iterator<ISocialConnector> it = results.iterator();
		while (it.hasNext()){
			ISocialConnector conn = it.next();
			conn.post(data);
			logger.debug("Posting using "+conn.getID() +" connector");
		}

	}


	private List<ISocialConnector> getConnectorsByName(SocialNetwork name){
		Iterator <ISocialConnector> it = connectors.values().iterator();
		ArrayList<ISocialConnector> results = new ArrayList<ISocialConnector>();
		while (it.hasNext()){
			ISocialConnector conn = it.next();
			if (conn.getID().contains(name.toString())){
				results.add(conn);
			}
		}
		return results;
	}

	private String genJsonPostMessage(Map<String,?> map){
		String type = map.get(ISocialData.POST_TYPE).toString();
		JsonObject result= new JsonObject(type);
		if (type.equals(ISocialData.CHECKIN)){

			//			Example:
			//			String value="{ \"checkin\": {"+
			//			        "\"lat\": \"45.473272\","+
			//			        "\"lon\": \"9.187519\","+
			//			        "\"message\": \"Milano City\","+
			//			        "\"place\": 1234}"+
			//			        "}";

			if (map.containsKey(ISocialData.POST_DESCR))
				result.put(ISocialData.POST_DESCR, map.get(ISocialData.POST_DESCR).toString());
			result.put(ISocialData.POST_LAT, map.get(ISocialData.POST_LAT).toString());
			result.put(ISocialData.POST_LON, map.get(ISocialData.POST_LON).toString());
			if (map.containsKey(ISocialData.POST_MESSAGE))
				result.put(ISocialData.POST_MESSAGE, map.get(ISocialData.POST_MESSAGE).toString());
			if (map.containsKey(ISocialData.POST_PLACE))
				result.put(ISocialData.POST_PLACE, map.get(ISocialData.POST_PLACE).toString());
		}
		else{

			//			Example:
			//			String value="{ \"event\": {"+
			//			        "\"name\": \"Party\","+
			//			        "\"from\": \"2013-08-12 10:45\","+
			//			        "\"to\": \"2013-08-12 18:45\","+
			//			        "\"location\": \"HOME\","+
			//			        "\"description\": \"My Birthday Party\"}"+
			//			        "}";

			if (map.containsKey(ISocialData.POST_NAME))
				result.put(ISocialData.POST_NAME, map.get(ISocialData.POST_NAME).toString());
			result.put(ISocialData.POST_FROM, map.get(ISocialData.POST_FROM).toString());
			result.put(ISocialData.POST_TO, map.get(ISocialData.POST_TO).toString());
			if (map.containsKey(ISocialData.POST_DESCR))
				result.put(ISocialData.POST_DESCR, map.get(ISocialData.POST_DESCR).toString());
			if (map.containsKey(ISocialData.POST_LOCATION))
				result.put(ISocialData.POST_LOCATION, map.get(ISocialData.POST_LOCATION).toString());
			if (map.containsKey(ISocialData.POST_PLACE))
				result.put(ISocialData.POST_PLACE, map.get(ISocialData.POST_PLACE).toString());
		}

		return result.toString(1);
	}




}
