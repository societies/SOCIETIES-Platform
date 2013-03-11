package org.societies.css.mgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.css.BitCompareUtil;
import org.societies.api.css.FriendFilter;
import org.societies.api.css.directory.ICssDirectoryCallback;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.css.CSSManagerEnums;
import org.societies.api.internal.css.CSSNode;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.css.ICSSManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.css.directory.CssFriendEvent;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.utilities.DBC.Dbc;

import org.societies.api.schema.servicelifecycle.model.Service;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
//import org.societies.platform.socialdata.SocialData;

import org.apache.shindig.social.opensocial.model.Person;

import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

public class CSSManager implements ICSSLocalManager, ICSSInternalManager {
	private static Logger LOG = LoggerFactory.getLogger(CSSManager.class);

	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";
	public static final String TEST_ARCHIVED_IDENTITY_1 = "archnode11";
	public static final String TEST_ARCHIVED_IDENTITY_2 = "archnode22";

	public static final String TEST_IDENTITY = "android";
	public static final String TEST_INACTIVE_DATE = "20121029";
	public static final String TEST_REGISTERED_DATE = "20120229";
	public static final int TEST_UPTIME = 7799;
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	public static final String TEST_HOME_LOCATION = "The Hearth";
	public static final String TEST_IDENTITY_NAME = "Id Name";
	public static final String TEST_IM_ID = "somebody.tssg.org";
	public static final String TEST_NAME = "The CSS";
	public static final String TEST_PASSWORD = "androidpass";
	public static final String TEST_SOCIAL_URI = "sombody@fb.com";

	private static final String THIS_NODE = "XCManager.societies.local";
	private static final List<String> cssPubsubClassList = Collections.unmodifiableList(
		Arrays.asList("org.societies.api.schema.cssmanagement.CssEvent",
				"org.societies.api.schema.css.directory.CssFriendEvent"));

	private ICssRegistry cssRegistry;
	private ICssDirectoryRemote cssDirectoryRemote;
	private IServiceDiscovery serviceDiscovery;
	private ICSSRemoteManager cssManagerRemote;
    private PubsubClient pubSubManager;
    private IIdentityManager idManager;
    private ICommManager commManager;
    private IIdentity pubsubID;
    
    private Random randomGenerator;
    
	private boolean pubsubInitialised = false;

	private IEventMgr eventMgr = null;

	public void cssManagerInit() {
		LOG.debug("CSS Manager initialised");

        this.idManager = commManager.getIdManager();
        
        this.pubsubID = idManager.getThisNetworkNode();
        
		this.createMinimalCSSRecord(idManager.getCloudNode().getJid());
        
        this.randomGenerator = new Random();
        
		this.createPubSubNodes();
        this.subscribeToPubSubNodes();

	}

	/**
	 * Subscribe to relevant Pubsub nodes
	 */
	private void subscribeToPubSubNodes() {
        LOG.debug("Subscribing to relevant Pubsub nodes");
//        pubSubManager.subscriberSubscribe(???);

	}

	/**
	 * TODO: Presumably on the Cloud node CSSManager should
	 * create these PubSub nodes.
	 * 1. How will the CSSManager know that it is on a cloud node ?
	 * 2. What happens if these PubSub nodes already exist ?
	 */
	private void createPubSubNodes() {
        
        if (!this.pubsubInitialised) {
            LOG.debug("Creating PubsubNode(s) for CSSManager");

            try {
            	
    			pubSubManager.addSimpleClasses(cssPubsubClassList);

    			pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.ADD_CSS_NODE);
    	        pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.DEPART_CSS_NODE);
    	        pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.CSS_FRIEND_REQUEST_RECEIVED_EVENT);
    	        pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.CSS_FRIEND_REQUEST_ACCEPTED_EVENT);
    	        
    		} catch (XMPPError e) {
    			e.printStackTrace();
    		} catch (CommunicationException e) {
    			e.printStackTrace();
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    	        LOG.debug(CSSManagerEnums.ADD_CSS_NODE + " PubsubNode created for CSSManager");
    	        LOG.debug(CSSManagerEnums.DEPART_CSS_NODE + " PubsubNode created for CSSManager");
    	        this.pubsubInitialised = true;
    		}
        }
	}

	/**
	 * 	 
	 * Create minimal CSSRecord and register it to the database
	 * 
	 * @param identity
	 */
	private void createMinimalCSSRecord(String identity) {
		LOG.debug("Creating minimal CSSRecord");

		//cloud node details
		CssNode cssNode = new CssNode();

		cssNode.setIdentity(identity);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());

		try {
			//if CssRecord does not exist create new CssRecord in persistance layer

			if (!this.cssRegistry.cssRecordExists()) {

				//Minimal CSS details
				CssRecord cssProfile = new CssRecord();
				cssProfile.getCssNodes().add(cssNode);
				cssProfile.setCssIdentity(identity);
//				cssProfile.setCssInactivation("0");

//				cssProfile.setCssRegistration(this.getDate());

//				cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
//				cssProfile.setCssUpTime(0);
				cssProfile.setEmailID("");
				cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
				cssProfile.setForeName("");
				cssProfile.setHomeLocation("");
//				cssProfile.setIdentityName("");
//				cssProfile.setImID("");
				cssProfile.setName("");
				cssProfile.setPassword("");
//				cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
				cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
				cssProfile.setSex(CSSManagerEnums.entityType.Person.ordinal());
//				cssProfile.setSocialURI("");
				cssProfile.setWorkplace("");
				cssProfile.setPosition("");

				try {
					this.cssRegistry.registerCss(cssProfile);
					LOG.debug("Registering CSS with local database");
				} catch (CssRegistrationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// internal eventing

				LOG.info("minimal CSSRecord -> Generating CSS_Record to piush to context");

				this.pushtoContext(cssProfile);

				LOG.info("Generating CSS_Record_Event to notify Record has been created");

//				LOG.info("Generating CSS_Record_Event to notify Record has been created");
//				if(this.getEventMgr() != null){
//					InternalEvent event = new InternalEvent(EventTypes.CSS_RECORD_EVENT, "CSS Record Created", this.idManager.getThisNetworkNode().toString(), cssProfile);
//					try {
//						LOG.info("Calling PublishInternalEvent with details :" +event.geteventType() +event.geteventName() +event.geteventSource() +event.geteventInfo());
//						this.getEventMgr().publishInternalEvent(event);
//					} catch (EMSException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						LOG.error("error trying to internally publish SUBS CIS event");
//					}
//				}
			} else {
				// if CssRecord already persisted remove all nodes and add cloud node

				CssRecord cssRecord  = this.cssRegistry.getCssRecord();

				cssRecord.getCssNodes().clear();

				cssRecord.getCssNodes().add(cssNode);

				this.updateCssRegistry(cssRecord);

			}
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Workaround for existing problem with database
	 * 
	 * @param update
	 * TODO : use normal CssRegistry update method when working
	 */
	private void updateCssRegistry(CssRecord update) {
		CssRecord existing;
		try {
			existing = this.cssRegistry.getCssRecord();
			if (null != existing) {
					this.cssRegistry.unregisterCss(existing);
					this.cssRegistry.registerCss(update);
			}
		} catch (CssRegistrationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public Future<CssInterfaceResult> changeCSSNodeStatus(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> getCssRecord() {
		CssInterfaceResult result = new CssInterfaceResult();

		LOG.debug("CSS Manager getCssRecord Called");

		try {
			if (this.cssRegistry.cssRecordExists()) {
				CssRecord currentCssRecord = this.cssRegistry.getCssRecord();
				result.setProfile(currentCssRecord);
				LOG.debug("Number of Css nodes: " + currentCssRecord.getCssNodes().size());
				result.setResultStatus(true);
			} else {
				LOG.error("Unable to find CssRecord");
			}
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}
	@Override
	/**
	 * There is now no longer any validation of a node contacting a
	 * cloud node of a CSS. Since the other node has logged into the 
	 * chosen XMPP Domain server only messages from this JID domain
	 * can be routed to the cloud node.
	 */
	public Future<CssInterfaceResult> loginCSS(CssRecord profile) {
		LOG.debug("Calling loginCSS");

		Dbc.require("CssRecord parameter cannot be null", profile != null);


        CssInterfaceResult result = new CssInterfaceResult();
		result.setProfile(profile);
		result.setResultStatus(false);

		CssRecord cssRecord = null;

		try{
			if (this.cssRegistry.cssRecordExists()) {
				cssRecord = this.cssRegistry.getCssRecord();

				//check if attempted login has already been attempted
				if (!cssNodeExist(profile.getCssNodes().get(0), cssRecord)) {
					// add new node to login to cloud CssRecord
					LOG.debug("CSS Node: " + profile.getCssNodes().get(0).getIdentity() + " has not logged in");
					cssRecord.getCssNodes().add(profile.getCssNodes().get(0));

					this.updateCssRegistry(cssRecord);
					LOG.debug("Updating CSS with local database");


				} else {
					LOG.debug("CSS Node: " + profile.getCssNodes().get(0).getIdentity() + " was already logged in and is re-establishing connection");
				}
				//Send event
				CssEvent event = new CssEvent();
				event.setType(CSSManagerEnums.ADD_CSS_NODE);
				event.setDescription(CSSManagerEnums.ADD_CSS_NODE_DESC);

				this.publishEvent(CSSManagerEnums.ADD_CSS_NODE, event);
				
				result.setProfile(cssRecord);
				result.setResultStatus(true);
			} else {
				LOG.error("CSS record does not exist");
			}

		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<CssInterfaceResult>(result);
	}

	public Future<CssInterfaceResult> loginXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	/**
	 * Requires that CssRecord parameter has one node in its collection and that 
	 * the node corresponds to the node being logged out.
	 */
	public Future<CssInterfaceResult> logoutCSS(CssRecord profile) {
		LOG.debug("Calling logoutCSS");

		Dbc.require("CssRecord parameter cannot be null", profile != null);
		Dbc.require("Cssrecord parameter must contain CSS identity",
				profile.getCssIdentity() != null
						&& profile.getCssIdentity().length() > 0);

		CssInterfaceResult result = new CssInterfaceResult();
		result.setProfile(profile);
		result.setResultStatus(false);
		CssRecord cssRecord = null;

		try{
			if (this.cssRegistry.cssRecordExists()) {

				cssRecord = this.cssRegistry.getCssRecord();

				//check if attempted login has already been attempted
				if (cssNodeExist(profile.getCssNodes().get(0), cssRecord)) {
					LOG.debug("CSS Node: " + profile.getCssNodes().get(0).getIdentity() + " is already logged in");

					// remove new node to login to cloud CssRecord
					for (Iterator<CssNode> iter = cssRecord.getCssNodes().iterator(); iter
							.hasNext();) {
						CssNode node = (CssNode) iter.next();
						CssNode logoutNode = profile.getCssNodes().get(0);
						if (node.getIdentity().equals(logoutNode.getIdentity())
								&& node.getType() == logoutNode.getType()) {
							iter.remove();
							break;
						}
					}

					result.setProfile(cssRecord);
					result.setResultStatus(true);

					this.updateCssRegistry(cssRecord);

					CssEvent event = new CssEvent();
					event.setType(CSSManagerEnums.DEPART_CSS_NODE);
					event.setDescription(CSSManagerEnums.DEPART_CSS_NODE_DESC);

					this.publishEvent(CSSManagerEnums.DEPART_CSS_NODE, event);
				} else {
					LOG.error("CSS Node: " + profile.getCssNodes().get(0).getIdentity() + " has already logged out");
				}
			} else {
				LOG.error("Css Record does not exist");
			}

		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		return new AsyncResult<CssInterfaceResult>(result);
	}


	public Future<CssInterfaceResult> logoutXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> modifyCssRecord(CssRecord profile) {
		LOG.info("Calling modifyCssRecord");

		Dbc.require("CssRecord parameter cannot be null", profile != null);

        CssInterfaceResult result = new CssInterfaceResult();
		result.setProfile(profile);
		result.setResultStatus(false);

		CssRecord cssRecord = null;

		try{
			if (this.cssRegistry.cssRecordExists()) {
				cssRecord = this.cssRegistry.getCssRecord();

				// update profile information
				cssRecord.setEntity(profile.getEntity());
				cssRecord.setForeName(profile.getForeName());
				cssRecord.setName(profile.getName());
				cssRecord.setEmailID(profile.getEmailID());
//				cssRecord.setImID(profile.getImID());
//				cssRecord.setSocialURI(profile.getSocialURI());
				cssRecord.setSex(profile.getSex());
				cssRecord.setHomeLocation(profile.getHomeLocation());
				cssRecord.setEntity(profile.getEntity());
				cssRecord.setWorkplace(profile.getWorkplace());
				cssRecord.setPosition(profile.getPosition());
				cssRecord.setCssNodes(profile.getCssNodes());

				LOG.info("modifyCssRecord cssRecord Entity: " +cssRecord.getEntity());
				LOG.info("modifyCssRecord cssRecord Name : "  +cssRecord.getName());
				LOG.info("modifyCssRecord cssRecord EmailID : " +cssRecord.getEmailID());
				LOG.info("modifyCssRecord cssRecord Sex : " +cssRecord.getSex());
				LOG.info("modifyCssRecord cssRecord CSSID : " +cssRecord.getCssIdentity());
				LOG.info("modifyCssRecord cssRecord Workplace : " +cssRecord.getWorkplace());
				LOG.info("modifyCssRecord cssRecord Position : " +cssRecord.getPosition());
				
				LOG.info("modifyCssRecord cssRecord CssNodes : " +cssRecord.getCssNodes());

				// internal eventing

				LOG.info("modifyCsRecord -> push to context");

				this.pushtoContext(cssRecord);

				LOG.info("Generating CSS_Record_Event to notify Record has been created");

/*				LOG.info("Generating CSS_Record_Event to notify Record has changed");
				if(this.getEventMgr() != null){
					InternalEvent event = new InternalEvent(EventTypes.CSS_RECORD_EVENT, "CSS Record modified", this.idManager.getThisNetworkNode().toString(), cssRecord);
					try {
						LOG.info("Calling PublishInternalEvent with details :" +event.geteventType() +event.geteventName() +event.geteventSource() +event.geteventInfo());
						this.getEventMgr().publishInternalEvent(event);
					} catch (EMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LOG.error("error trying to internally publish SUBS CIS event");
					}
				}*/

				this.updateCssRegistry(cssRecord);
				LOG.debug("Updating CSS with local database");

				result.setProfile(cssRecord);
				result.setResultStatus(true);

			} else {
				LOG.equals("Css record does not exist");
			}


		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> registerCSS(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		LOG.info("CSS Manager registerCSS Called");
		try {
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> registerCSSNode(CssRecord profile) {

		LOG.info("CSS Manager registerCSSNode Called");
		String nodeid = null;
		String identity = null;
		int status = 0;
		int type = 0;
		String MAC = null;
		String Interactable = null;
		CssInterfaceResult result = new CssInterfaceResult();
		LOG.info("CssRecord passed in: " +profile);
		List<CssNode> cssNodes = new ArrayList<CssNode>();
		//nodeid = idManager.getThisNetworkNode().toString();
		//LOG.info("+++++++++++++ nodeid =: " +profile);
		//LOG.info("+++++++++++++ nodeStatus =: " +status);
		//LOG.info("+++++++++++++ nodeType =: " +type);
		//CssNode cssnode = new CssNode();
		//cssnode.setIdentity(nodeid);
		//cssnode.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		//status = cssnode.getStatus();
		//cssnode.setType(CSSManagerEnums.nodeType.Android.ordinal());
		//type = cssnode.getType();

		//LOG.info("############# nodeid =: " +nodeid);
		//LOG.info("############# nodeStatus =: " +status);
		//LOG.info("############# nodeType =: " +type);


		cssNodes = profile.getCssNodes();
		//cssNodes.add(0, cssnode);
		//profile.setCssNodes(cssNodes);
		nodeid = idManager.getThisNetworkNode().toString();
		//for (CssNode cssNode : profile.getCssNodes()) {
			//cssNode.setIdentity(identity);
			//cssNode.setStatus(status);
			//cssNode.setType(type);

			LOG.info("cssNodes Array Size is : " +cssNodes.size());
			//}

			this.modifyCssRecord(profile);
		//try {
	//		LOG.info("+++++++++++++ Calling cssRegistry Register CSSRecord ");
		//	result = cssRegistry.registerCss(profile);

			//result = true;
	//	} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
	//	}
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	@Async
	public Future<CssInterfaceResult> registerXMPPServer(CssRecord profile) {

		CssInterfaceResult result = new CssInterfaceResult();
		try {
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	public Future<CssInterfaceResult> setPresenceStatus(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<CssInterfaceResult> synchProfile(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> unregisterCSS(CssRecord profile) {

		CssInterfaceResult result = new CssInterfaceResult();
		try {
			cssRegistry.unregisterCss(profile);
			result.setResultStatus(true);

		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	public Future<CssInterfaceResult> unregisterCSSNode(CssRecord profile) {
		LOG.info("CSS Manager UNregisterCSSNode Called");
		CssInterfaceResult result = new CssInterfaceResult();
		String nodeid = null;
		List<CssNode> cssNodes = new ArrayList<CssNode>();
		nodeid = idManager.getThisNetworkNode().toString();
		CssNode cssnode = new CssNode();


		cssNodes = profile.getCssNodes();
		cssNodes.remove(cssnode); 
		profile.setCssNodes(cssNodes);

		try {
			cssRegistry.registerCss(profile);
			result.setResultStatus(true);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	public Future<CssInterfaceResult> unregisterXMPPServer(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		try {
			cssRegistry.unregisterCss(profile);
			result.setResultStatus(true);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * addAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void addAdvertisementRecord(CssAdvertisementRecord record) {
		getCssDirectoryRemote().addCssAdvertisementRecord(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * deleteAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void deleteAdvertisementRecord(CssAdvertisementRecord record) {
		getCssDirectoryRemote().deleteCssAdvertisementRecord(record);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void updateAdvertisementRecord(CssAdvertisementRecord currentRecord,
			CssAdvertisementRecord updatedRecord) {
		getCssDirectoryRemote().updateCssAdvertisementRecord(currentRecord,
				updatedRecord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public Future<List<CssAdvertisementRecord>> findAllCssAdvertisementRecords() {
		List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();

		CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

		getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
		recordList = callback.getResultList();

		return new AsyncResult<List<CssAdvertisementRecord>>(recordList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public Future<List<Service>> findAllCssServiceDetails(
			List<CssAdvertisementRecord> listCssAds) {
		List<Service> serviceList = new ArrayList<Service>();
		Future<List<Service>> asyncResult = null;
		List<Service> cssServiceList = null;


		for (CssAdvertisementRecord cssAdd : listCssAds) {
			try {
				asyncResult = getServiceDiscovery().getServices(cssAdd.getId()); // TODO
																					// on
				cssServiceList = asyncResult.get();
				if (cssServiceList != null) {
					for (Service cssService : cssServiceList) {
						serviceList.add(cssService);
					}
					cssServiceList.clear();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceDiscoveryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return new AsyncResult<List<Service>>(serviceList);
	}
	/**
	 * Create an event for a given Pubsub node
	 * 
	 * @param pubsubNodeName
	 */
	private void publishEvent(String pubsubNodeName, CssEvent event) {
		Dbc.require("Pubsub Node name must be valid", pubsubNodeName != null && pubsubNodeName.length() > 0);
		Dbc.require("Pubsub event must be valid", event !=  null);

	    LOG.debug("Publish event node: " + pubsubNodeName);


	    try {
			Map <IIdentity, SubscriptionState> subscribers = this.pubSubManager.ownerGetSubscriptions(pubsubID, CSSManagerEnums.DEPART_CSS_NODE);
			for (IIdentity identity : subscribers.keySet()) {
				LOG.debug("Subscriber : " + identity + " subscribed to: " + CSSManagerEnums.DEPART_CSS_NODE);
			}
			subscribers = this.pubSubManager.ownerGetSubscriptions(pubsubID, CSSManagerEnums.ADD_CSS_NODE);
			for (IIdentity identity : subscribers.keySet()) {
				LOG.debug("Subscriber : " + identity + " subscribed to: " + CSSManagerEnums.ADD_CSS_NODE);
			}
		} catch (XMPPError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CommunicationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	    try {
	    	String status = this.pubSubManager.publisherPublish(pubsubID, pubsubNodeName, Integer.toString(this.randomGenerator.nextInt()), event);
			LOG.debug("Event published: " + status);
		} catch (XMPPError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Autowired
	private ICtxBroker ctxBroker;

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	private ISocialData socialdata;

	private FriendFilter filter;

	//Spring injection

	public ISocialData getSocialData() {
		return socialdata;
	}

	public void setSocialData(ISocialData socialData) {
		this.socialdata = socialData;
	}

	/**
	 * @return the cssRegistry
	 */
	public ICssRegistry getCssRegistry() {
		return cssRegistry;
	}

	/**
	 * @param cssRegistry
	 *            the cssRegistry to set
	 */
	public void setCssRegistry(ICssRegistry cssRegistry) {
		this.cssRegistry = cssRegistry;
//		try {
//			this.cssRegistry.registerCss(createCSSRecord());
//		} catch (CssRegistrationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	/**
	 * @return the cssDiscoveryRemote
	 */
	public ICssDirectoryRemote getCssDirectoryRemote() {
		return cssDirectoryRemote;
	}

	/**
	 * @param cssDiscoveryRemote
	 *            the cssDiscoveryRemote to set
	 */
	public void setCssDirectoryRemote(ICssDirectoryRemote cssDirectoryRemote) {
		this.cssDirectoryRemote = cssDirectoryRemote;
	}

	/**
	 * @return the serviceDiscovery
	 */
	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	/**
	 * @param serviceDiscovery
	 *            the serviceDiscovery to set
	 */
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}


	/**
	 * @return the cssManagerRemote
	 */
	public ICSSRemoteManager getCssManagerRemote() {
		return cssManagerRemote;
	}

    public PubsubClient getPubSubManager() { 
    	return this.pubSubManager;     
    }
    public void setPubSubManager(PubsubClient pubSubManager) { 
    	this.pubSubManager = pubSubManager;
    }

    public ICommManager getCommManager() {
    	return commManager;
    }
    public void setCommManager(ICommManager commManager) {
    	this.commManager = commManager;
    }

	/**
	 * @param cssManagerRemote the cssManagerRemote to set
	 */
	public void setCssManagerRemote(ICSSRemoteManager cssManagerRemote) {
		this.cssManagerRemote = cssManagerRemote;
	}

	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public Future<List<CssRequest>> findAllCssRequests() {
		List<CssRequest> recordList = new ArrayList<CssRequest>();

		//TODO:
		try {
			recordList = cssRegistry.getCssRequests();
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<CssRequest>>(recordList);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public Future<List<CssRequest>> findAllCssFriendRequests() {
		List<CssRequest> recordList = new ArrayList<CssRequest>();

		//TODO:
		try {
			recordList = cssRegistry.getCssFriendRequests();
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<CssRequest>>(recordList);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void updateCssRequest(CssRequest request) {

		//TODO: This is our resp0onse to a request by other css
		//we can acept, ignored etc
		try {
			cssRegistry.updateCssRequestRecord(request);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		// We only want to sent messages to remote Css's for this function if we initiated the call locally
		if (request.getOrigin() == CssRequestOrigin.LOCAL)
		{

			// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
			// otherwise send message to remote css
			if (request.getRequestStatus() != CssRequestStatusType.DENIED )
			{
				//called updateCssFriendRequest on remote
				request.setOrigin(CssRequestOrigin.REMOTE);
				cssManagerRemote.updateCssFriendRequest(request);
			}	
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void updateCssFriendRequest(CssRequest request) {

		//TODO: This is called either locally or remote
		//Locally, we can cancel pending request, or leave css's
		// remotely, it will be an accepted of the request we sent
		LOG.info("updateCssFriendRequest called : ");
		LOG.info("Request  identity: " +request.getCssIdentity());
		LOG.info("Request  status: " +request.getRequestStatus());
		LOG.info("Request  origin: " +request.getOrigin());
		try {
			cssRegistry.updateCssFriendRequestRecord(request);
			cssRegistry.updateCssRequestRecord(request);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.info("updateCssFriendRequest and we're back : " );
		// If this was initiated locally then inform remote css
		// We only want to sent messages to remote Css's for this function if we initiated the call locally
		if (request.getOrigin() == CssRequestOrigin.LOCAL)
		{

			// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
			// otherwise send message to remote css

			LOG.info("INSIDE IF STATEMENT -> Request  origin: " +request.getOrigin());
				//called updateCssFriendRequest on remote
				request.setOrigin(CssRequestOrigin.REMOTE);
				cssManagerRemote.updateCssRequest(request);
		}
	}



	/* (non-Javadoc)
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#sendCssFriendRequest(java.lang.String)
	 */
	@Override
	public void sendCssFriendRequest(String cssFriendId) {
		// TODO Auto-generated method stub


		CssRequest request = new CssRequest();
		request.setCssIdentity(cssFriendId);
		//TODO : check if it exists first
		request.setRequestStatus(CssRequestStatusType.PENDING);
		try {
			cssRegistry.updateCssFriendRequestRecord(request);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// This will always be initalliated locally so no need to check origin
		// db updated ow send it to friend and forget about it
		//cssManagerRemote.se
		System.out.println("~~~~~~~~~~~~~~~ sending Friend request : " +cssFriendId);
		LOG.info("sending Friend request : " +cssFriendId);
		cssManagerRemote.sendCssFriendRequest(cssFriendId);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public Future<List<CssAdvertisementRecordDetailed>> getCssAdvertisementRecordsFull() {
		List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecordDetailed> cssDetailList = new ArrayList<CssAdvertisementRecordDetailed>();

		// first get all the cssdirectory records
		CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

		getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
		recordList = callback.getResultList();

		CssRequest cssRequest;
		CssAdvertisementRecordDetailed adDetailed = null;
		// now compare them to our css Friends
		for (CssAdvertisementRecord cssAdd : recordList) {
			try {

				adDetailed = new CssAdvertisementRecordDetailed();
				adDetailed.setResultCssAdvertisementRecord(cssAdd);
				adDetailed.setStatus(CssRequestStatusType.NOTREQUESTED); //default!

				cssRequest = cssRegistry.getCssFriendRequest(cssAdd.getId());

				if (cssRequest != null)
				{

					adDetailed.setStatus(cssRequest.getRequestStatus()); 
				}

				cssDetailList.add(adDetailed);




			} catch (CssRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

		return new AsyncResult<List<CssAdvertisementRecordDetailed>>(cssDetailList);

	}

		@Override
	public Future<List<CssAdvertisementRecord>> getCssFriends() {
		List<String> friendList = new ArrayList<String>();
		List<CssAdvertisementRecord> friendAdList = new ArrayList<CssAdvertisementRecord>();

		try {
				friendList = cssRegistry.getCssFriends();

				// Only go searching the directory is theor is something to search for
				if ((friendList != null) && (friendList.size() > 0))
				{	

					//first get all the cssdirectory records
					CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

					getCssDirectoryRemote().searchByID(friendList, callback);
					friendAdList = callback.getResultList();
				}

			} catch (CssRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return new AsyncResult<List<CssAdvertisementRecord>>(friendAdList);

	}

	public Future<String> getthisNodeType(String nodeId) {
		String Type = null, nodeid = null;
		LOG.info("getthisNodeType has been called: ");
		List<CSSNode> cssnodes = new ArrayList<CSSNode>();
		Future<List<CSSNode>> asyncResult = null;
		List<CSSNode> incssnodes = null;
		int android = 0;

		//nodeid = idManager.getThisNetworkNode().toString();
		nodeid = nodeId;
		LOG.info("nodeid is now : " +nodeid);

		CssRecord currentCssRecord = null;
		try {
			currentCssRecord = cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (currentCssRecord.getCssNodes() != null) {
			for (CssNode cssNode : currentCssRecord.getCssNodes()) {
				cssNode.getIdentity();
				LOG.info("cssNode.getIdentity is returning: " +cssNode.getIdentity());
				LOG.info("nodeid is returning: " +nodeid);
				if (nodeid.equalsIgnoreCase(cssNode.getIdentity())){
					LOG.info("cssNode.getType() is returning: " +cssNode.getType());
					if (CSSManagerEnums.nodeType.Android.ordinal() == (cssNode.getType())) {
						Type = "Android";
					} else if (CSSManagerEnums.nodeType.Rich.ordinal() == (cssNode.getType())) {
						Type = "Rich";
					} else if (CSSManagerEnums.nodeType.Cloud.ordinal() == (cssNode.getType())) {
						Type = "Cloud";
					}
				}
			}
		}
		LOG.info("getthisNodeType is returning: " +Type);
		return new AsyncResult<String>(Type);
	}

//	@Override
	public void setNodeType(CssRecord cssrecord, String nodeId, int nodestatus, int nodetype, String cssnodemac, String interactable) {

		List<CssNode> cssNodes = new ArrayList<CssNode>();
		CssNode cssnode = new CssNode();
		CssNode tmpNode = new CssNode();
		LOG.info("From Webapp cssNodes SIZE is: " +cssrecord.getCssNodes().size());
		/*
		try {
			cssrecord = cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		LOG.info("from CSSRegistry cssNodes SIZE is: " +cssrecord.getCssNodes().size());

		LOG.info("setNodeType nodeId passed in is: " +nodeId );
		LOG.info("setNodeType nodestatus passed in is: " +nodestatus );
		LOG.info("setNodeType nodetype passed in is: " +nodetype);
		LOG.info("setNodeType nodeMAC passed in is: " +cssnodemac);
		LOG.info("setNodeType nodeInteractable passed in is: " +interactable);


		int index = 0;
		cssnode.setIdentity(nodeId);
		cssnode.setStatus(nodestatus);
		cssnode.setType(nodetype);
		cssnode.setCssNodeMAC(cssnodemac);
		cssnode.setInteractable(interactable);

		//cssnode.setCssNodeMAC(cssnodemac);
		//cssnode.setInteractable(interactable);



		cssNodes = cssrecord.getCssNodes();

		LOG.info(" cssNodes are BEFORE : " +cssNodes);
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			LOG.info(" cssNode BEFORE index: " +index + " identity is now : " +cssNodes.get(index).getIdentity());
		}

		//cssNodes.add(tmpNode);
		cssNodes.add(cssnode);

		LOG.info("cssNodes are AFTER : " +cssNodes);

		cssrecord.setCssNodes(cssNodes);
		LOG.info(" cssrecord cssNodes SIZE AFTER add node is: " +cssrecord.getCssNodes().size());


		LOG.info(" cssrecord cssNodes SIZE is now : " +cssrecord.getCssNodes().size());
		cssNodes = cssrecord.getCssNodes();
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			LOG.info("cssNode index: " +index + " identity is now : " +cssNodes.get(index).getIdentity());
			LOG.info("cssNode index: " +index + " Status is now : " +cssNodes.get(index).getStatus());
			LOG.info("cssNode index: " +index +" type is now : " +cssNodes.get(index).getType());
			LOG.info("cssNode index: " +index +" MAC is now : " +cssNodes.get(index).getCssNodeMAC());
			//LOG.info(" cssNode index: " +index +" type is now : " +cssNodes.get(index).isInteractable());
		}


		this.modifyCssRecord(cssrecord); 

	}

public void removeNode(CssRecord cssrecord, String nodeId ) {

		List<CssNode> cssNodes = new ArrayList<CssNode>();
		//List<CssNode> tmpNodes = new ArrayList<CssNode>(cssNodes.size());
		CssNode cssnode = new CssNode();
		CssNode tmpNode = new CssNode();
		//CssRecord newrecord = cssrecord;

		try {
			cssRegistry.unregisterCss(cssrecord);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cssNodes = cssrecord.getCssNodes();


		LOG.info("removeNode cssNodes SIZE is: " +cssrecord.getCssNodes().size());
		LOG.info("removeNode nodeId to remove is : " +nodeId);
		int index = 0;

		//LOG.info("removeNode cssNodes SIZE is now : " +cssrecord.getCssNodes().size());
		cssNodes = cssrecord.getCssNodes();
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			if (cssNodes.get(index).getIdentity().equalsIgnoreCase(nodeId)) {
				LOG.info("removeNode loop identity : " +cssNodes.get(index).getIdentity());
				cssNodes.remove(index); 
				LOG.info("removeNode Node Removed : ");
				//tmpNodes.add(cssnode);
			}
			//tmpNodes.add(cssnode);
			LOG.info("removeNode cssNodes element is : " +cssNodes.get(index).getIdentity());
		}
		cssrecord.setCssNodes(cssNodes);
		LOG.info("removeNode cssrecord SIZE final : " +cssrecord.getCssNodes().size());
		//LOG.info("removeNode newrecord SIZE final : " +newrecord.getCssNodes().size());

		try {
			cssRegistry.registerCss(cssrecord);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.modifyCssRecord(cssrecord); 

	}

@SuppressWarnings("unchecked")
public Future<List<CssAdvertisementRecord>> suggestedFriends( ) {

	ISocialData socialData = null;

	List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();
	List<CssAdvertisementRecord> cssFriends = new ArrayList<CssAdvertisementRecord>();
	List<Person> snFriends = new ArrayList<Person>();
	List<String> socialFriends = new ArrayList<String>();
	List<CssAdvertisementRecord> commonFriends = new ArrayList<CssAdvertisementRecord>();
	String MyId = "";	
	MyId = idManager.getThisNetworkNode().toString();
	LOG.info("MyId contains " +MyId);

	LOG.info("CSSManager getFriends method called ");

	LOG.info("Contacting CSS Directory to get list of CSSs");

	// first get all the cssdirectory records
	CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

	getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
	recordList = callback.getResultList();

	for (CssAdvertisementRecord cssAdd : recordList) {
		LOG.info("Comparing Id contains " +cssAdd.getId());
		if (cssAdd.getId().equalsIgnoreCase(MyId)) {
			LOG.info("This is my OWN ID not adding it");
		}else {
			cssFriends.add((cssAdd));
		}

		LOG.info("cssAdd.getName contains " +cssAdd.getName());
		LOG.info("cssFriends contains " +cssFriends +" entries");
	}

	LOG.info("cssFriends contains " +cssFriends);
	LOG.info("CSS Directory contains " +cssFriends.size() +" entries");

	LOG.info("Contacting SN Connector to get list");
	LOG.info("getSocialData() returns " +getSocialData());

	// Generate the connector
	Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
	socialdata.updateSocialData();

	while (it.hasNext()){
	  ISocialConnector conn = it.next();
  	  
	LOG.info("SocialNetwork connector contains " +conn.getConnectorName());

	//socialdata.updateSocialData();
	}

	snFriends = (List<Person>) socialdata.getSocialPeople();
	LOG.info("snFriends size is :" +snFriends.size());
    Iterator<Person> itt = snFriends.iterator();
    int index =1;
    while(itt.hasNext()){
    	Person p =null;
    	String name = "";
    	try{
        	p = (Person) itt.next();
        	if (p.getName()!=null){
    			if (p.getName().getFormatted()!=null){
    				name = p.getName().getFormatted();
    				LOG.info(index +" Friends:" +name +" Social Network: " +socialdata.getSocialConnectors());
    				LOG.info("Social Network: " +socialdata.getSocialConnectors());
    				socialFriends.add(name);
    			}
    				
    			else {
    				if(p.getName().getFamilyName()!=null) name = p.getName().getFamilyName();
    				if(p.getName().getGivenName()!=null){
    					if (name.length()>0)  name+=" ";
    					name +=p.getName().getGivenName();
    					LOG.info(index +" Friends:" +name);
    					socialFriends.add(name);
    				}
    					  
    			
    			}
    				
    		}
    	}catch(Exception ex){name = "- NOT AVAILABLE -";}
    	index++;
    }
	//}
    
    //compare the lists to create
    
    LOG.info("CSS Friends List contains " +cssFriends.size() +" entries");
    LOG.info("Social Friends List contains " +socialFriends.size() +" entries");
    LOG.info("common Friends List contains " +commonFriends.size() +" entries");
    
    //compare the two lists
    LOG.info("Compare the two lists to generate a common Friends list");
    int i = 1;
   // for (int index =0; index < cssFriends.size(); index++)
   // {
    for (CssAdvertisementRecord friend : cssFriends) {
    	LOG.info("CSS Friends iterator List contains " +friend);
        if (socialFriends.contains(friend.getName())) {
        	if (commonFriends.contains(friend)){
        		LOG.info("This friend is already added to the list:" +friend);	
        	}else {
        		commonFriends.add(friend);
        	}
        	
        }
       // i++;
    }
    //}
    LOG.info("common Friends List NOW contains " +commonFriends.size() +" entries");
	//return commonFriends;
	return new AsyncResult<List<CssAdvertisementRecord>>(commonFriends);

	}

	/**
	 * Get today's date
	 * 
	 * @return String today's date
	 */
	private String getDate() {
		Calendar today = Calendar.getInstance();

		StringBuffer date = new StringBuffer();

		date.append(Integer.toString(today.get(Calendar.YEAR)));
		date.append(Integer.toString(today.get(Calendar.MONTH)));
		date.append(Integer.toString(today.get(Calendar.DAY_OF_MONTH)));

		return date.toString();
	}

	@Override
	public Future<List<CssAdvertisementRecord>> getFriendRequests() {
		List<CssRequest> pendingfriendList = new ArrayList<CssRequest>();
		List<CssAdvertisementRecord> friendReqList = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();
		List<String> pendingList = new ArrayList<String>();	


		try {
			//pendingfriendList = cssRegistry.getCssFriendRequests();
			pendingfriendList = cssRegistry.getCssRequests();

			for (CssRequest cssrequest : pendingfriendList) {
		    	LOG.info("CSS FriendRequest iterator List contains " +pendingfriendList);
		    	LOG.info("cssrequest status is: " +cssrequest.getRequestStatus());
		        if (cssrequest.getRequestStatus().value().equalsIgnoreCase("pending")) {
		        	//cssrequest.getCssIdentity();
		        	pendingList.add(cssrequest.getCssIdentity());
		        	LOG.info("pendingList size is now: " +pendingfriendList.size());
		        	LOG.info("pendingList entry is: " +cssrequest.getCssIdentity());

		        	}

		        }	


			// Only go searching the directory is theor is something to search for
			if ((pendingList != null) && (pendingList.size() > 0))
			{	

				//first get all the cssdirectory records
				CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

				getCssDirectoryRemote().searchByID(pendingList, callback);
				friendReqList = callback.getResultList();
			}

			} catch (CssRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return new AsyncResult<List<CssAdvertisementRecord>>(friendReqList);
	}

	public void acceptCssFriendRequest(CssRequest request) {

		//TODO: This is called either locally or remotle
		//Locally, we can cancel pending request, or leave css's
		// remotely, it will be an accepted of the request we sent
			try {
				cssRegistry.updateCssFriendRequestRecord(request);
				cssRegistry.updateCssRequestRecord(request);
				// internal eventing for notifying of new Friend
				LOG.info("Generating CSS_Friended_Event to notify CSS Friend Request has been accepted");
				if(this.getEventMgr() != null){
					InternalEvent event = new InternalEvent(EventTypes.CSS_FRIENDED_EVENT, "CSS Friend Request Accepted", this.idManager.getThisNetworkNode().toString(), request.getCssIdentity());
					try {
						LOG.info("Calling PublishInternalEvent with details :" +event.geteventType() +event.geteventName() +event.geteventSource() +event.geteventInfo());
						this.getEventMgr().publishInternalEvent(event);
					} catch (EMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LOG.error("error trying to internally publish SUBS CIS event");
					}
				}
			} catch (CssRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// If this was initiated locally then inform remote css
			// We only want to sent messages to remote Css's for this function if we initiated the call locally
			if (request.getOrigin() == CssRequestOrigin.LOCAL)
			{

				// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
				// otherwise send message to remote css

					//called updateCssFriendRequest on remote
					request.setOrigin(CssRequestOrigin.REMOTE);
					cssManagerRemote.acceptCssFriendRequest(request); 
			}
			if (request.getOrigin() == CssRequestOrigin.REMOTE)
			{

				// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
				// otherwise send message to remote css

					//called updateCssFriendRequest on remote
					request.setOrigin(CssRequestOrigin.REMOTE);
					cssManagerRemote.updateCssFriendRequest(request); 
			}
		}
	/**
	 * Determine if a CssNode object exists in the CssRecord maintained 
	 * by the CSS Cloud node
	 * 
	 * @param node
	 * @param record
	 * @return
	 */
	private boolean cssNodeExist(CssNode node, CssRecord record) {
		boolean retValue = false;

		for (CssNode element: record.getCssNodes()) {
			if (element.getIdentity().equals(node.getIdentity())
					&& element.getType() == node.getType()) {
				retValue = true;
				break;
			}
		}
		return retValue;
	}

	public void declineCssFriendRequest(CssRequest request) {


	LOG.info("Decline Css Friend Request has been called");
	LOG.info("declineCssFriendRequest status: " +request.getRequestStatus());
	LOG.info("declineCssFriendRequest Origin: " +request.getOrigin());
	LOG.info("declineCssFriendRequest ID: " +request.getCssIdentity());


			try {
				cssRegistry.updateCssFriendRequestRecord(request);


				} catch (CssRegistrationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			// If this was initiated locally then inform remote css
			// We only want to sent messages to remote Css's for this function if we initiated the call locally
			if (request.getOrigin() == CssRequestOrigin.LOCAL)
			{

				// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
				// otherwise send message to remote css

					//called updateCssFriendRequest on remote
					request.setOrigin(CssRequestOrigin.REMOTE);
					//cssManagerRemote.acceptCssFriendRequest(request); 
					cssManagerRemote.declineCssFriendRequest(request);
			}
			if (request.getOrigin() == CssRequestOrigin.REMOTE)
			{

				// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
				// otherwise send message to remote css

					//called updateCssFriendRequest on remote
					request.setOrigin(CssRequestOrigin.REMOTE);
					cssManagerRemote.updateCssFriendRequest(request); 
			}
		}

	@Override
	public Future<HashMap<IIdentity, Integer>> getSuggestedFriends(
			FriendFilter filter) {
		// TODO Auto-generated method stub






		return null;
	}

	@Override

public Future<HashMap<CssAdvertisementRecord, Integer>> getSuggestedFriendsDetails(
			FriendFilter filter) {

	ISocialData socialData = null;

	
	Integer filt = filter.getFilterFlag();
	LOG.info("Friends filter contains: " +filt);
	
	final int facebook   = 0x0000000001;
	final int twitter   =  0x0000000010;
	final int linkedin   = 0x0000000100;
	final int foursquare = 0x0000001000;
	final int googleplus = 0x0000010000;
	
	boolean flag = BitCompareUtil.isFacebookFlagged(filt);
	
	flag = BitCompareUtil.isTwitterFlagged(filt);
	
	flag = BitCompareUtil.isLinkedinFlagged(filt);
	
	flag = BitCompareUtil.isFoursquareFlagged(filt);
	flag = BitCompareUtil.isGooglePlusFlagged(filt);
	
	List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();
	List<CssAdvertisementRecord> cssFriends = new ArrayList<CssAdvertisementRecord>();
	List<Person> snFriends = new ArrayList<Person>();
	List<String> socialFriends = new ArrayList<String>();
	
	List<String> facebookFriends = new ArrayList<String>();
	List<String> twitterFriends = new ArrayList<String>();
	List<String> linkedinFriends = new ArrayList<String>();
	List<String> foursquareFriends = new ArrayList<String>();
	List<String> googleplusFriends = new ArrayList<String>();
	HashMap<CssAdvertisementRecord, Integer> commonFriends = new HashMap<CssAdvertisementRecord, Integer>();
	String MyId = "";	
	MyId = idManager.getThisNetworkNode().toString();
	

	// first get all the cssdirectory records
	CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

	getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
	recordList = callback.getResultList();

	for (CssAdvertisementRecord cssAdd : recordList) {
		
		if (cssAdd.getId().equalsIgnoreCase(MyId)) {
			LOG.info("This is my OWN ID not adding it");
		}else {
			cssFriends.add((cssAdd));
		}

		
	}

	// Generate the connector
	Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
	socialdata.updateSocialData();

	while (it.hasNext()){
	  ISocialConnector conn = it.next();
  	  

	//socialdata.updateSocialData();
	}
	//it.next().getConnectorName();
	String domain ="";
	snFriends = (List<Person>) socialdata.getSocialPeople();

    Iterator<Person> itt = snFriends.iterator();
    int index =1;
    while(itt.hasNext()){
    	Person p =null;
    	String name = "";
    	try{
        	p = (Person) itt.next();
        	if (p.getName()!=null){
    			if (p.getName().getFormatted()!=null){
    				name = p.getName().getFormatted();
    				domain = p.getAccounts().get(0).getDomain();
    				
    				
    				if(domain.equalsIgnoreCase("facebook.com")){
						filter.setFilterFlag(facebook);		
	    				facebookFriends.add(name);    				
    				}
    				if(domain.equalsIgnoreCase("twitter.com")){
    					
						filter.setFilterFlag(twitter);		
						
	    				twitterFriends.add(name);    				
    				}
    				if(domain.equalsIgnoreCase("linkedin.com")){
    					
						filter.setFilterFlag(linkedin);		
	    				linkedinFriends.add(name);    				
    				}
    				if(domain.equalsIgnoreCase("foursquare.com")){
    					
						filter.setFilterFlag(foursquare);		
						
	    				foursquareFriends.add(name);    				
    				}
    				if(domain.equalsIgnoreCase("googleplus.com")){
						filter.setFilterFlag(googleplus);		
						
	    				googleplusFriends.add(name);    				
    				}
    				
    			}
    				
    			else {
    				if(p.getName().getFamilyName()!=null) name = p.getName().getFamilyName();
    				if(p.getName().getGivenName()!=null){
    					if (name.length()>0)  name+=" ";
    					name +=p.getName().getGivenName();
    					
    					socialFriends.add(name);
    				}
    					  
    			
    			}
    				
    		}
    	}catch(Exception ex){name = "- NOT AVAILABLE -";}
    	index++;
    }
	//}
    
    //compare the lists to create
    
    
    flag = BitCompareUtil.isFacebookFlagged(filt);
   
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        	
        	boolean contains = facebookFriends.contains(friend.getName());
        	
        	
            if (facebookFriends.contains(friend.getName())) {
            	if (commonFriends.containsValue(friend)){
            			
            	}else {
            		
            		commonFriends.put(friend, filt);            		
            	}
            	
            }	
           
        }
    	flag = false;
    }
    	
    flag = BitCompareUtil.isTwitterFlagged(filt);
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        	
            if (twitterFriends.contains(friend.getName())) {
            	if (commonFriends.containsValue(friend)){
            		
            	}else {
            		
            		commonFriends.put(friend, filt);
            	}
            	
            }
       
        }
    	flag = false;
    }
    	
    flag = BitCompareUtil.isLinkedinFlagged(filt);
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        
            if (linkedinFriends.contains(friend.getName())) {
            	if (commonFriends.containsValue(friend)){
            		
            	}else {
            		commonFriends.put(friend, filt);
            		
            	}
            	
            }
        }
    	flag = false;
    }
    	

    flag = BitCompareUtil.isFoursquareFlagged(filt);
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        	
            if (foursquareFriends.contains(friend.getName())) {
            	if (commonFriends.containsValue(friend)){
            		
            	}else {
            		commonFriends.put(friend, filt);
            		
            	}
            	
            }
        }
    	flag = false;
    }
    
    flag = BitCompareUtil.isGooglePlusFlagged(filt);
    	

             if(flag){
            	 for (CssAdvertisementRecord friend : cssFriends) {
                 	
                     if (googleplusFriends.contains(friend.getName())) {
                     	if (commonFriends.containsValue(friend)){
                     		
                     	}else {
                     		commonFriends.put(friend, filt);
                     		
                     	}
                     	
                     }
                  
                 }
            	 flag = false;
             }
    	
             

    //compare the two lists
   
    int i = 1;
   // for (int index =0; index < cssFriends.size(); index++)
   // {
/*    for (CssAdvertisementRecord friend : cssFriends) {
    	LOG.info("CSS Friends iterator List contains " +friend);
        if (socialFriends.contains(friend.getName())) {
        	if (commonFriends.containsValue(friend)){
        		LOG.info("This friend is already added to the list:" +friend);
        	}else {
        		commonFriends.put(friend, filt);
        	}
        	
        }
       // i++;
    }
*/    //}
    
	//return commonFriends;
    
    
	return new AsyncResult<HashMap<CssAdvertisementRecord, Integer>> (commonFriends);
	}

	@Override
	public void sendCSSFriendRequest(IIdentity identity, RequestorService service) {
		//PUBLISH PUBSUB EVENT FOR THIS RECEIVED FRIEND REQUEST
		LOG.debug("Publishing friend request received event for: " + identity.getBareJid());
				
		//SAVE TO DATABASE
		String targetCSSid = identity.toString();
		CssRequest request = new CssRequest();
		//request.setCssIdentity(targetCSSid);
		request.setCssIdentity(service.getRequestorId().toString());
		request.setRequestStatus(CssRequestStatusType.PENDING);
		request.setOrigin(CssRequestOrigin.REMOTE);
		try {
			cssRegistry.updateCssRequestRecord(request);

		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// We only want to sent messages to remote Css's for this function if we initiated the call locally
		if (request.getOrigin() == CssRequestOrigin.LOCAL)
		{

			// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
			// otherwise send message to remote css
			if (request.getRequestStatus() != CssRequestStatusType.DENIED )
			{
				//called updateCssFriendRequest on remote
				request.setOrigin(CssRequestOrigin.REMOTE);
				cssManagerRemote.updateCssFriendRequest(request);
			}	
		}
		//GENERATE PUBSUB EVENT
		List<String> idList = new ArrayList<String>();
		idList.add(identity.getBareJid());
		cssDirectoryRemote.searchByID(idList, new ICssDirectoryCallback() {
			@Override
			public void getResult(List<CssAdvertisementRecord> records) {
				CssFriendEvent payload = new CssFriendEvent();
				payload.setCssAdvert( records.get(0));

				try {
					String status = CSSManager.this.pubSubManager.publisherPublish(pubsubID, CSSManagerEnums.CSS_FRIEND_REQUEST_RECEIVED_EVENT, Integer.toString(CSSManager.this.randomGenerator.nextInt()), payload);
					LOG.debug("Published Event Status: " + status);
				} catch (XMPPError e) {
					e.printStackTrace();
				} catch (CommunicationException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void handleExternalFriendRequest(IIdentity identity, CssRequestStatusType statusType) {
		//identity is who the request has come FROM
		CssRequest request = new CssRequest();
		request.setCssIdentity(identity.toString());
		request.setRequestStatus(statusType);
		LOG.info("handleExternalFriendRequest called : ");
		LOG.info("Request from identity: " +identity);
		LOG.info("Request  status: " +statusType);

		try {
			cssRegistry.updateCssFriendRequestRecord(request);
			cssRegistry.updateCssRequestRecord(request);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("updateCssFriendRequest and we're back : " );
		// If this was initiated locally then inform remote css
		// We only want to sent messages to remote Css's for this function if we initiated the call locally
		if (request.getOrigin() == CssRequestOrigin.LOCAL) {
			// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
			// otherwise send message to remote css
			LOG.info("INSIDE IF STATEMENT -> Request  origin: " +request.getOrigin());
			//called updateCssFriendRequest on remote
			request.setOrigin(CssRequestOrigin.REMOTE);
			cssManagerRemote.updateCssRequest(request);
		}
		
		//PUBLISH PUBSUB EVENT FOR THIS NEW FRIEND
		if (statusType.equals(CssRequestStatusType.ACCEPTED)) {
			//GENERATE PUBSUB EVENT
			List<String> idList = new ArrayList<String>();
			idList.add(identity.getBareJid());
			cssDirectoryRemote.searchByID(idList, new ICssDirectoryCallback() {
				@Override
				public void getResult(List<CssAdvertisementRecord> records) {
					CssFriendEvent payload = new CssFriendEvent();
					payload.setCssAdvert( records.get(0));

					try {
						String status = CSSManager.this.pubSubManager.publisherPublish(pubsubID, CSSManagerEnums.CSS_FRIEND_REQUEST_ACCEPTED_EVENT, Integer.toString(CSSManager.this.randomGenerator.nextInt()), payload);
						LOG.debug("Published Event Status: " + status);
					} catch (XMPPError e) {
						e.printStackTrace();
					} catch (CommunicationException e) {
						e.printStackTrace();
					}
				}
			});
		}	
	}

	@Override
	public void handleInternalFriendRequest(IIdentity identity, CssRequestStatusType statusType) {
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity(identity.toString());
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		acceptCssFriendRequest(pendingFR);
		
		//UPDATE LOCAL DATABASE WITH THIS FRIEND REQUEST
		//CssRequest request = new CssRequest();
		//request.setCssIdentity(identity.toString());
		//LOG.info("handleInternalFriendRequest called : ");
		//LOG.info("Request from identity: " +identity);
		//LOG.info("Request  status: " +statusType);
		//try {
		//	cssRegistry.updateCssFriendRequestRecord(request);
		//	//cssRegistry.updateCssFriendRequestRecord(request);
		//	cssRegistry.updateCssRequestRecord(request);
		//} catch (CssRegistrationException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
	}

	public void pushtoContext(CssRecord record) {

		final String cssIdStr = record.getCssIdentity();
		LOG.info("pushtoContext is HERE: ");
		//final String cssIdStr = "jane.societies.local";
		LOG.info("pushtoContext cssIdStr: " +cssIdStr);



		try {
			IIdentity cssId = commManager.getIdManager().fromJid(cssIdStr);
			LOG.info("pushtoContext cssId: " +cssId);
			CtxEntityIdentifier ownerCtxId = this.getCtxBroker().retrieveIndividualEntity(cssId).get().getId();

			LOG.info("pushtoContext ownerCtxId: " +ownerCtxId);

			String value;
			List<CssNode> value1;
			int value2;

			// NAME
			value = record.getName();
			LOG.info("pushtoContext NAME value: " +value);
			if (value != null && !value.isEmpty())
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME, value);

			// EMAIL
			value = record.getEmailID();
			LOG.info("pushtoContext EMAIL value: " +value);
			if (value != null && !value.isEmpty())
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.EMAIL, value);
			
			// Entity
			value2 = record.getEntity();
			LOG.info("pushtoContext ENTITY value: " +value2);
			if (value2 >= 0 && value2 <=1){
				
				if(value2 == CSSManagerEnums.entityType.Person.ordinal()){
					value = "Person";
					LOG.info("pushtoContext ENTITY value: " +value);
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.TYPE, value);
				}
				if(value2 == CSSManagerEnums.entityType.Organisation.ordinal()){
					value = "Organisation";
					LOG.info("pushtoContext ENTITY value: " +value);
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.TYPE, value);
				}
				
			}
				

			// ForeName
			value = record.getForeName();
			LOG.info("pushtoContext FORENAME value: " +value);
			if (value != null && !value.isEmpty())
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME_FIRST, value);
			
			// Sex
			value2 = record.getSex();
			LOG.info("pushtoContext SEX value: " +value2);
			//if (value2 != null && !value2.isEmpty()
			if (value2 >= 0 && value2 <=2){
							
				if(value2 == CSSManagerEnums.genderType.Male.ordinal()){
					value = "Male";
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.SEX, value);
				}
				if(value2 == CSSManagerEnums.genderType.Female.ordinal()){
					value = "Female";
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.SEX, value);
				}
				if(value2 == CSSManagerEnums.genderType.Unspecified.ordinal()){
					value = "Undefined";
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.SEX, value);
				}
				
			}
			// CSS Identity
			value = record.getCssIdentity();
			LOG.info("pushtoContext IDENTITY value: " +value);
			if (value != null && !value.isEmpty())
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.ID, value);

			// Workplace
			value = record.getWorkplace();
			LOG.info("pushtoContext WORKPLACE value: " +value);
			if (value != null && !value.isEmpty())
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.ADDRESS_WORK_CITY, value);
			
			// Position
			value = record.getPosition();
			LOG.info("pushtoContext POSITION value: " +value);
			if (value != null && !value.isEmpty())
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.WORK_POSITION, value);
			
			// CSS Nodes
//			value1 = record.getCssNodes();
//			if (record.getCssNodes() != null) {
//				for (CssNode cssNode : record.getCssNodes()) {
//					INetworkNode cssNodeId = (INetworkNode) commManager.getIdManager().fromJid(cssNode.getIdentity());
//					LOG.info("pushtoContext CSSNODES value: " +value1);
//					this.ctxBroker.createCssNode(cssNodeId);
//				}
//			}
			
			List<CssNode> cssNodes = new ArrayList<CssNode>();
			cssNodes = record.getCssNodes();
			LOG.info("pushtoContext CSSNODES value: " +cssNodes);
			for (final CssNode cssNode : cssNodes) {

			  // create INetworkNode instance from JID String representation
			  final INetworkNode cssNodeJid = commManager.getIdManager().fromFullJid(cssNode.getIdentity());

			  // the createCssNode covers the creation of the CtxAttributeTypes.ID of the CSS Node entity
			  final CtxEntity cssNodeEnt = ctxBroker.createCssNode(cssNodeJid).get();

			  // TODO Status
			  value2 = cssNode.getStatus(); 
				  
			  if (value2 >= 0 && value2 <=2) {
				  
				  if(value2 == CSSManagerEnums.nodeStatus.Available.ordinal()){
						value = "Available";
						updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_STATUS, value);
						LOG.info("pushtoContext CssNodeStatus value: " +value);
					}
					if(value2 == CSSManagerEnums.nodeStatus.Unavailable.ordinal()){
						value = "Unavailable";
						updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_STATUS, value);
						LOG.info("pushtoContext CssNodeStatus value: " +value);
					}
					if(value2 == CSSManagerEnums.nodeStatus.Unavailable.ordinal()){
						value = "Unavailable";
						updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_STATUS, value);
						LOG.info("pushtoContext CssNodeStatus value: " +value);
					}
			  }
			   // updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_STATUS, value2);
			  
			  // TODO Type
			  value2 = cssNode.getType();
			  if (value2 >= 0 && value2 <=2) {
				  
				  if(value2 == CSSManagerEnums.nodeType.Android.ordinal()){
						value = "Android";
						updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_TYPE, value);
						LOG.info("pushtoContext CssNodeType value: " +value);
					}
					if(value2 == CSSManagerEnums.nodeType.Cloud.ordinal()){
						value = "Cloud";
						updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_TYPE, value);
						LOG.info("pushtoContext CssNodeType value: " +value);
					}
					if(value2 == CSSManagerEnums.nodeType.Rich.ordinal()){
						value = "Rich";
						updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_TYPE, value);
						LOG.info("pushtoContext CssNodeType value: " +value);
					}
				  
			  }
				  
			    //updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.CSS_NODE_TYPE, value);
			  
			  // MAC Address
			  value = cssNode.getCssNodeMAC();
			  if (value != null && !value.isEmpty())
			    updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.MAC_ADDRESS, value);
			  LOG.info("pushtoContext MAC Address value: " +value);

			  // Interactable
			  value = cssNode.getInteractable();
			  if (value != null && !value.isEmpty())
			    updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.IS_INTERACTABLE, value);
			  LOG.info("pushtoContext Interactable value: " +value);
			}

			

		} catch (InvalidFormatException ife) {

			LOG.error("Invalid CSS IIdentity found in CSS record: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (Exception e) {

			LOG.error("Failed to access context data: " 
					+ e.getLocalizedMessage(), e);
		}


	}

	private void updateCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		LOG.info("updateCtxAttribute called with CSSID: " +ownerCtxId +"CtxAttributeTypes.NAME: " +CtxAttributeTypes.NAME +"value: " +value );
		if (LOG.isDebugEnabled())
			LOG.debug("Updating '" + type + "' of entity " + ownerCtxId + " to '" + value + "'");

		final List<CtxIdentifier> ctxIds = 
				this.ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		final CtxAttribute attr;
		if (!ctxIds.isEmpty())
			attr = (CtxAttribute) this.ctxBroker.retrieve(ctxIds.get(0)).get();
		else
			attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();

		attr.setStringValue(value);
		attr.setValueType(CtxAttributeValueType.STRING);
		attr.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		this.ctxBroker.update(attr);
	}
	
	private CtxAttribute retrieveCtxAttribute(CtxEntityIdentifier ownerCtxId, String type) throws Exception {

		  if (LOG.isDebugEnabled())
		    LOG.debug("Retrieving '" + type + "' attribute of entity " + ownerCtxId);
		  final List<CtxIdentifier> ctxIds = ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		  
		  if (!ctxIds.isEmpty())
		    return (CtxAttribute) this.ctxBroker.retrieve(ctxIds.get(0)).get();
		  else
		    return null;
		}

	@Override
	public FriendFilter getFriendfilter() {
		LOG.info("CSS MANAGER get friendfilter called");

		return filter;
	}

	@Override
	public void setFriendfilter(FriendFilter filter) {
		LOG.info("CSS MANAGER set friendfilter calledwith filt: " +filter.getFilterFlag());
		this.filter = filter;
		
	}
	

}