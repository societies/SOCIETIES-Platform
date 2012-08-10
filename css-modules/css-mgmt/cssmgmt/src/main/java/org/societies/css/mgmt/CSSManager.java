package org.societies.css.mgmt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.internal.css.management.CSSNode;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.utilities.DBC.Dbc;

import org.societies.api.schema.servicelifecycle.model.Service;


public class CSSManager implements ICSSLocalManager {
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
	private static final String CSS_MGMT_PACKAGE = "org.societies.api.schema.cssmanagement";
	
	private ICssRegistry cssRegistry;
	private ICssDirectoryRemote cssDirectoryRemote;
	private IServiceDiscovery serviceDiscovery;
	private ICSSRemoteManager cssManagerRemote;
    private PubsubClient pubSubManager;
    private IIdentityManager idManager;
    private ICommManager commManager;
    private IIdentity pubsubID;
    
    private Random randomGenerator;
    
	private CssRecord cssRecord;
	
	public void cssManagerInit() {
		LOG.debug("CSS Manager initialised");
		this.cssRecord = createCSSRecord();
		
        
        this.idManager = commManager.getIdManager();
        
//        
//        try {
//			pubsubID = idManager.fromJid(THIS_NODE);
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		  Supposedly, the correct way to obtain the identity
        this.pubsubID = idManager.getThisNetworkNode();
        
        //TODO re-instate when Pubsub Simple working
//        this.createPubSubNodes();
        this.subscribeToPubSubNodes();
        
        this.randomGenerator = new Random();

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
        LOG.debug("Creating PubsubNode(s) for CSSManager");
        
        try {
        	
            List<String> packageList = new ArrayList<String>();
            packageList.add(CSS_MGMT_PACKAGE);
			pubSubManager.addJaxbPackages(packageList);

			pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.ADD_CSS_NODE);
	        pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.DEPART_CSS_NODE);
	        
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	        LOG.debug(CSSManagerEnums.ADD_CSS_NODE + " PubsubNode created for CSSManager");
	        LOG.debug(CSSManagerEnums.DEPART_CSS_NODE + " PubsubNode created for CSSManager");
		}
	}

	/**
	 * Create a default CSSrecord
	 * This is a temporary measure until genuine CSSs can be created
	 * 
	 * @return CssRecord
	 */
	private CssRecord createCSSRecord() {
		
    	CssNode cssNode_1, cssNode_2;
    	CssNode archCSSNode_1, archCSSNode_2;
    	
		cssNode_1 = new CssNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new CssNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());

		archCSSNode_1 = new CssNode();
		archCSSNode_1.setIdentity(TEST_ARCHIVED_IDENTITY_1);
		archCSSNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		archCSSNode_1.setType(CSSManagerEnums.nodeType.Cloud.ordinal());

		archCSSNode_2 = new CssNode();
		archCSSNode_2.setIdentity(TEST_ARCHIVED_IDENTITY_2);
		archCSSNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		archCSSNode_2.setType(CSSManagerEnums.nodeType.Rich.ordinal());
		

		CssRecord cssProfile = new CssRecord();
		cssProfile.getCssNodes().add(cssNode_1);
		cssProfile.getCssNodes().add(cssNode_2);
		cssProfile.getArchiveCSSNodes().add(archCSSNode_1);
		cssProfile.getArchiveCSSNodes().add(archCSSNode_2);
		
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
		cssProfile.setPassword(TEST_PASSWORD);
		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setSocialURI(TEST_SOCIAL_URI);

		
		return cssProfile;
	}
	@Override
	public Future<CssInterfaceResult> changeCSSNodeStatus(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> getCssRecord() {
		CssInterfaceResult result = new CssInterfaceResult();
		
		LOG.info("%%%%%%%%%%%%%% CSS Manager getCssRecord Called");
		
		try {
			CssRecord currentCssRecord = cssRegistry.getCssRecord();
			result.setProfile(currentCssRecord);
			LOG.info(";;;;;;;;;;;;;;;;;;; CSS Manager getCssRecord Size is : " +currentCssRecord.getCssNodes().size());
			result.setResultStatus(true);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//LOG.info("%%%%%%%%%%%%%% CSS Manager getCssRecord Size is : " +result.getProfile().getCssNodes().size()); //getCssNodes().size());
		return new AsyncResult<CssInterfaceResult>(result);
	}
//	@Override
//	/**
//	 * Requires that CssRecord parameter has one node in its collection and that 
//	 * the node corresponds to the node being logged in. The CSS identity and password
//	 * must also be set to appropriate values
//	 */
//	public Future<CssInterfaceResult> loginCSS(CssRecord profile) {
//		LOG.debug("Calling loginCSS");
//
//		Dbc.require("CssRecord parameter cannot be null", profile != null);
//		Dbc.require("Cssrecord parameter must contain CSS identity",
//				profile.getCssIdentity() != null
//						&& profile.getCssIdentity().length() > 0);
//		Dbc.require("Cssrecord parameter must contain CSS password",
//				profile.getPassword() != null
//						&& profile.getPassword().length() > 0);
//
//		CssInterfaceResult result = new CssInterfaceResult();
//		result.setProfile(profile);
//		result.setResultStatus(false);
//
//		CssRecord record;
//		try {
//			record = this.cssRegistry.getCssRecord();
//			if (profile.getCssIdentity().equals(record.getCssIdentity())
//					&& profile.getPassword().equals(record.getPassword())) {
//				// add new node to login to cloud CssRecord
//				record.getCssNodes().add(profile.getCssNodes().get(0));
//				// update the CSS registry
//				this.cssRegistry.updateCssRecord(record);
//
//				result.setProfile(record);
//				result.setResultStatus(true);
//			}
//		} catch (CssRegistrationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return new AsyncResult<CssInterfaceResult>(result);
//	}
//
	@Override
	/**
	 * Requires that CssRecord parameter has one node in its collection and that 
	 * the node corresponds to the node being logged in. The CSS identity and password
	 * must also be set to appropriate values
	 */
	public Future<CssInterfaceResult> loginCSS(CssRecord profile) {
		LOG.debug("Calling loginCSS");

		Dbc.require("CssRecord parameter cannot be null", profile != null);
		Dbc.require("Cssrecord parameter must contain CSS identity",
				profile.getCssIdentity() != null
						&& profile.getCssIdentity().length() > 0);
		Dbc.require("Cssrecord parameter must contain CSS password",
				profile.getPassword() != null
						&& profile.getPassword().length() > 0);

		CssInterfaceResult result = new CssInterfaceResult();
		result.setProfile(profile);
		result.setResultStatus(false);

		if (profile.getCssIdentity().equals(this.cssRecord.getCssIdentity())
				&& profile.getPassword().equals(this.cssRecord.getPassword())) {
			// add new node to login to cloud CssRecord
			this.cssRecord.getCssNodes().add(profile.getCssNodes().get(0));

			try {
				this.cssRegistry.registerCss(cssRecord);
				LOG.debug("Registering CSS with local database");
			} catch (CssRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			result.setProfile(this.cssRecord);
			result.setResultStatus(true);
			CssEvent event = new CssEvent();
			event.setType(CSSManagerEnums.ADD_CSS_NODE);
			event.setDescription(CSSManagerEnums.ADD_CSS_NODE_DESC);
			
	        //TODO re-instate when Pubsub Simple working
			//			this.publishEvent(CSSManagerEnums.ADD_CSS_NODE, event);
		}
		
		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
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


		if (profile.getCssIdentity().equals(this.cssRecord.getCssIdentity())) {
				// remove new node to login to cloud CssRecord
				for (Iterator<CssNode> iter = this.cssRecord.getCssNodes().iterator(); iter
						.hasNext();) {
					CssNode node = (CssNode) iter.next();
					CssNode logoutNode = profile.getCssNodes().get(0);
					if (node.getIdentity().equals(logoutNode.getIdentity())
							&& node.getType() == logoutNode.getType()) {
						iter.remove();
						break;
					}
				}

				result.setProfile(this.cssRecord);
				result.setResultStatus(true);
				
				try {
					this.cssRegistry.updateCssRecord(cssRecord);
				} catch (CssRegistrationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					this.cssRegistry.updateCssRecord(cssRecord);
				} catch (CssRegistrationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				CssEvent event = new CssEvent();
				event.setType(CSSManagerEnums.DEPART_CSS_NODE);
				event.setDescription(CSSManagerEnums.DEPART_CSS_NODE_DESC);
				
		        //TODO re-instate when Pubsub Simple working
				//				this.publishEvent(CSSManagerEnums.DEPART_CSS_NODE, event);

		} 
	
		return new AsyncResult<CssInterfaceResult>(result);
	}

//	@Override
//	/**
//	 * Requires that CssRecord parameter has one node in its collection and that 
//	 * the node corresponds to the node being logged out.
//	 */
//	public Future<CssInterfaceResult> logoutCSS(CssRecord profile) {
//		LOG.debug("Calling logoutCSS");
//
//		Dbc.require("CssRecord parameter cannot be null", profile != null);
//		Dbc.require("Cssrecord parameter must contain CSS identity",
//				profile.getCssIdentity() != null
//						&& profile.getCssIdentity().length() > 0);
//
//		CssInterfaceResult result = new CssInterfaceResult();
//		result.setProfile(profile);
//		result.setResultStatus(false);
//
//		CssRecord record;
//		try {
//			record = this.cssRegistry.getCssRecord();
//
//			if (profile.getCssIdentity().equals(record.getCssIdentity())) {
//				// remove new node to login to cloud CssRecord
//				for (Iterator<CssNode> iter = record.getCssNodes().iterator(); iter
//						.hasNext();) {
//					CssNode node = (CssNode) iter.next();
//					CssNode logoutNode = profile.getCssNodes().get(0);
//					if (node.getIdentity().equals(logoutNode.getIdentity())
//							&& node.getType() == logoutNode.getType()) {
//						iter.remove();
//						break;
//					}
//				}
//				// update the CSS registry
//				this.cssRegistry.updateCssRecord(record);
//
//				result.setProfile(record);
//				result.setResultStatus(true);
//			}
//		} catch (CssRegistrationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return new AsyncResult<CssInterfaceResult>(result);
//	}

	@Override
	public Future<CssInterfaceResult> logoutXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> modifyCssRecord(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		LOG.info("%%%%%%%%%%%%%% CSS Manager modifyCSSrecord Called");
		LOG.info("%%%%%%%%%%%%%% CSS Manager cssrecord Size is : " +profile.getCssNodes().size());
		
		try {
			cssRegistry.unregisterCss(profile);
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> registerCSS(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		LOG.info("%%%%%%%%%%%%%% CSS Manager registerCSS Called");
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
		
		LOG.info("+++++++++++++ CSS Manager registerCSSNode Called");
		String nodeid = null;
		String identity = null;
		int status = 0;
		int type = 0;
		CssInterfaceResult result = new CssInterfaceResult();
		LOG.info("+++++++++++++ CssRecord passed in: " +profile);
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
			
			LOG.info("~~~~~~~~~~~~~~~ cssNodes Array Size is : " +cssNodes.size());
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

	@Override
	public Future<CssInterfaceResult> setPresenceStatus(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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
		LOG.info("+++++++++++++ CSS Manager UNregisterCSSNode Called");
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


	//Spring injection
	
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
	
		//TODO: This is called either locally or remotle
		//Locally, we can cancel pending request, or leave css's
		// remotely, it will be an accepted of the request we sent
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
	
	
	public Future<String> getthisNodeType(String nodeId) {
		String Type = null, nodeid = null;
		LOG.info("[][][][][] getthisNodeType has been called   [][][][][]: ");
		List<CSSNode> cssnodes = new ArrayList<CSSNode>();
		Future<List<CSSNode>> asyncResult = null;
		List<CSSNode> incssnodes = null;
		int android = 0;
		
		//nodeid = idManager.getThisNetworkNode().toString();
		nodeid = nodeId;
		LOG.info("[][][][][] nodeid is now   [][][][][]: " +nodeid);
	
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
				LOG.info("[][][][][] cssNode.getIdentity is returning   [][][][][]: " +cssNode.getIdentity());
				LOG.info("[][][][][] nodeid is returning   [][][][][]: " +nodeid);
				if (nodeid.equalsIgnoreCase(cssNode.getIdentity())){
					LOG.info("[][][][][] cssNode.getType() is returning   [][][][][]: " +cssNode.getType());
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
		LOG.info("[][][][][] getthisNodeType is returning   [][][][][]: " +Type);
		return new AsyncResult<String>(Type);
	}

//	@Override
	public void setNodeType(CssRecord cssrecord, String nodeId, int nodestatus, int nodetype, String cssnodemac, String interactable) {
		
		List<CssNode> cssNodes = new ArrayList<CssNode>();
		CssNode cssnode = new CssNode();
		CssNode tmpNode = new CssNode();
		LOG.info("!!!!!!!!!!!!!!!!!!! From Webapp cssNodes SIZE is: " +cssrecord.getCssNodes().size());
		/*
		try {
			cssrecord = cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		LOG.info("!!!!!!!!!!!!!!!!!!! from CSSRegistry cssNodes SIZE is: " +cssrecord.getCssNodes().size());
		
		LOG.info("!!!!!!!!!!!!!!!!!!! setNodeType nodeId passed in is: " +nodeId );
		LOG.info("!!!!!!!!!!!!!!!!!!! setNodeType nodestatus passed in is: " +nodestatus );
		LOG.info("!!!!!!!!!!!!!!!!!!! setNodeType nodetype passed in is: " +nodetype);
		LOG.info("!!!!!!!!!!!!!!!!!!! setNodeType nodeMAC passed in is: " +cssnodemac);
		LOG.info("!!!!!!!!!!!!!!!!!!! setNodeType nodeInteractable passed in is: " +interactable);
		
		
		int index = 0;
		cssnode.setIdentity(nodeId);
		cssnode.setStatus(nodestatus);
		cssnode.setType(nodetype);
		cssnode.setCssNodeMAC(cssnodemac);
		cssnode.setInteractable(interactable);
		
		//cssnode.setCssNodeMAC(cssnodemac);
		//cssnode.setInteractable(interactable);
		
		
		
		cssNodes = cssrecord.getCssNodes();
		
		LOG.info("!!!!!!!!!!!!!!!!!!! cssNodes are BEFORE : " +cssNodes);
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			LOG.info("!!!!!!!!!!!!!!!!!!! cssNode BEFORE index: " +index + " identity is now : " +cssNodes.get(index).getIdentity());
		}
		
		//cssNodes.add(tmpNode);
		cssNodes.add(cssnode);
		
		LOG.info("!!!!!!!!!!!!!!!!!!! cssNodes are AFTER : " +cssNodes);
				
		cssrecord.setCssNodes(cssNodes);
		LOG.info("!!!!!!!!!!!!!!!!!!! cssrecord cssNodes SIZE AFTER add node is: " +cssrecord.getCssNodes().size());
		
		
		LOG.info("!!!!!!!!!!!!!!!!!!! cssrecord cssNodes SIZE is now : " +cssrecord.getCssNodes().size());
		cssNodes = cssrecord.getCssNodes();
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			LOG.info("!!!!!!!!!!!!!!!!!!! cssNode index: " +index + " identity is now : " +cssNodes.get(index).getIdentity());
			LOG.info("!!!!!!!!!!!!!!!!!!! cssNode index: " +index + " Status is now : " +cssNodes.get(index).getStatus());
			LOG.info("!!!!!!!!!!!!!!!!!!! cssNode index: " +index +" type is now : " +cssNodes.get(index).getType());
			LOG.info("!!!!!!!!!!!!!!!!!!! cssNode index: " +index +" MAC is now : " +cssNodes.get(index).getCssNodeMAC());
			//LOG.info("!!!!!!!!!!!!!!!!!!! cssNode index: " +index +" type is now : " +cssNodes.get(index).isInteractable());
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
		
		
		LOG.info("~~~~~~~~~~~~~~~~ removeNode cssNodes SIZE is: " +cssrecord.getCssNodes().size());
		LOG.info("~~~~~~~~~~~~~~~~ removeNode nodeId to remove is : " +nodeId);
		int index = 0;
		
		//LOG.info("~~~~~~~~~~~~~~~~ removeNode cssNodes SIZE is now : " +cssrecord.getCssNodes().size());
		cssNodes = cssrecord.getCssNodes();
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			if (cssNodes.get(index).getIdentity().equalsIgnoreCase(nodeId)) {
				LOG.info("~~~~~~~~~~~~~~~~ removeNode loop identity : " +cssNodes.get(index).getIdentity());
				cssNodes.remove(index); 
				LOG.info("~~~~~~~~~~~~~~~ removeNode Node Removed : ");
				//tmpNodes.add(cssnode);
			}
			//tmpNodes.add(cssnode);
			LOG.info("~~~~~~~~~~~~~~~ removeNode cssNodes element is : " +cssNodes.get(index).getIdentity());
		}
		cssrecord.setCssNodes(cssNodes);
		LOG.info("~~~~~~~~~~~~~~~ removeNode cssrecord SIZE final : " +cssrecord.getCssNodes().size());
		//LOG.info("~~~~~~~~~~~~~~~ removeNode newrecord SIZE final : " +newrecord.getCssNodes().size());
		
		try {
			cssRegistry.registerCss(cssrecord);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.modifyCssRecord(cssrecord); 
	
	}
	
}
