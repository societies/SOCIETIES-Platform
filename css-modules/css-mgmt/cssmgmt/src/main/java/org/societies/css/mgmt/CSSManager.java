/**
 * Copyright (c) 2011-2013, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.css.mgmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.shindig.social.opensocial.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
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
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.WhoResponse;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.css.directory.CssFriendEvent;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.utilities.DBC.Dbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.societies.api.css.ICSSManager;

public class CSSManager implements ICSSInternalManager, ICSSManager {
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
	
	private IActivityFeed activityFeed;

	public void cssManagerInit() {
		LOG.debug("CSS Manager initialised");

        this.idManager = commManager.getIdManager();
        
        this.pubsubID = idManager.getThisNetworkNode();
        
        /**
         * added false as we don't want to create a pubsub node
         */
        
        activityFeed = getiActivityFeedManager().getOrCreateFeed(idManager.getThisNetworkNode().toString(), idManager.getThisNetworkNode().toString(), true);
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
    			LOG.error("Ops! XMPP Error", e);
    		} catch (CommunicationException e) {
    			LOG.error("Ops! Communication Exception", e);
    		} catch (Exception e) {
    			LOG.error("Ops! Exception", e);
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
		cssNode.setInteractable("false");
		try{
			InetAddress localAddress = InetAddress.getLocalHost();
			
			if (null != localAddress) {
				LOG.debug("Cloud Node IP address: " + localAddress);
				NetworkInterface networkInterface =  NetworkInterface.getByInetAddress(localAddress);
				
				if (null != networkInterface) {
					LOG.debug("Cloud Node network interface: " + networkInterface.getName());
					
					byte[] macAddress = networkInterface.getHardwareAddress();
					if (null != macAddress) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < macAddress.length; i++) {
							sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? ":" : ""));		
						}
						cssNode.setCssNodeMAC(sb.toString());
					}
				}
			}
			
		} catch (UnknownHostException e) {
			LOG.error("Ops! Unkown host Exception", e);
		} catch (SocketException e){
			LOG.error("Ops! Socket Exception", e);
		}

		try {
			//if CssRecord does not exist create new CssRecord in persistence layer

			if (!this.cssRegistry.cssRecordExists()) {

				//Minimal CSS details
				CssRecord cssProfile = new CssRecord();
				cssProfile.getCssNodes().add(cssNode);
				cssProfile.setCssIdentity(identity);
				
				cssProfile.setEmailID("");
				cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
				cssProfile.setForeName("");
				cssProfile.setHomeLocation("");
				cssProfile.setName("");
				cssProfile.setPassword("");

				cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
				cssProfile.setSex(CSSManagerEnums.entityType.Person.ordinal());

				cssProfile.setWorkplace("");
				cssProfile.setPosition("");

				try {
					this.cssRegistry.registerCss(cssProfile);
					LOG.debug("Registering CSS with local database");
				} catch (CssRegistrationException e) {
					LOG.error("Ops! Registration Exception", e);;
				}

				LOG.info("minimal CSSRecord -> Generating CSS_Record to push to context");

				this.pushtoContext(cssProfile);

				LOG.info("Generating CSS_Record_Event to notify Record has been created");

				addActivityToCSSAF("SOCIETIES profile published");
			} else {
				// if CssRecord already persisted remove all nodes and add cloud node

				CssRecord cssRecord  = this.cssRegistry.getCssRecord();

				cssRecord.getCssNodes().clear();

				cssRecord.getCssNodes().add(cssNode);

				this.updateCssRegistry(cssRecord);

			}
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
		}
	
	}
	
	/**
	 * Private method for adding activities
	 * 
	 * @param activityVerb
	 */
	private void addActivityToCSSAF(String activityVerb){
			
		IActivity iActivity = activityFeed.getEmptyIActivity();
		iActivity.setActor(idManager.getThisNetworkNode().toString());
		iActivity.setObject(idManager.getThisNetworkNode().toString());
		iActivity.setVerb(activityVerb);
			
		activityFeed.addActivity(iActivity);
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
			LOG.error("Ops! Registration Exception", e1);
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
			LOG.error("Ops! Registration Exception", e);
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
			LOG.error("Ops! Registration Exception", e);
		}
		
		this.pushtoContext(cssRecord);

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
			LOG.error("Ops! Registration Exception", e);
		}


		this.pushtoContext(cssRecord);
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
				cssRecord.setSex(profile.getSex());
				cssRecord.setHomeLocation(profile.getHomeLocation());
				cssRecord.setEntity(profile.getEntity());
				cssRecord.setWorkplace(profile.getWorkplace());
				cssRecord.setPosition(profile.getPosition());


				// internal eventing

				LOG.info("modifyCsRecord -> push to context");

				this.pushtoContext(cssRecord);

				LOG.info("Generating CSS_Record_Event to notify Record has been created");

				this.updateCssRegistry(cssRecord);
				LOG.debug("Updating CSS with local database");

				result.setProfile(cssRecord);
				result.setResultStatus(true);

			} else {
				LOG.equals("Css record does not exist");
			}


		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
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
			LOG.error("Ops! Registration Exception", e);
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> registerCSSNode(CssRecord profile) {

		LOG.info("CSS Manager registerCSSNode Called");
		
		CssInterfaceResult result = new CssInterfaceResult();
		LOG.info("CssRecord passed in: " +profile);
		List<CssNode> cssNodes = new ArrayList<CssNode>();
		cssNodes = profile.getCssNodes();
		LOG.info("cssNodes Array Size is : " +cssNodes.size());

		this.modifyCssRecord(profile);
		
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	@Async
	public Future<CssInterfaceResult> registerXMPPServer(CssRecord profile) {

		CssInterfaceResult result = new CssInterfaceResult();
		try {
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	public Future<CssInterfaceResult> setPresenceStatus(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<CssInterfaceResult> synchProfile(CssRecord profile) {
		LOG.info("Calling synchProfile");
		Dbc.require("CssRecord parameter cannot be null", profile != null);

        CssInterfaceResult result = new CssInterfaceResult();
		result.setProfile(profile);
		result.setResultStatus(false);

		CssRecord cssRecord = null;

		try {
			if (this.cssRegistry.cssRecordExists()) {
				cssRecord = this.cssRegistry.getCssRecord();

				result.setProfile(cssRecord);
				result.setResultStatus(true);

			} else {
				LOG.equals("Css record does not exist");
			}


		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
		}

		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> unregisterCSS(CssRecord profile) {

		CssInterfaceResult result = new CssInterfaceResult();
		try {
			cssRegistry.unregisterCss(profile);
			result.setResultStatus(true);

		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
		}
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	public Future<CssInterfaceResult> unregisterCSSNode(CssRecord profile) {
		LOG.info("CSS Manager UNregisterCSSNode Called");
		CssInterfaceResult result = new CssInterfaceResult();
		List<CssNode> cssNodes = new ArrayList<CssNode>();
		CssNode cssnode = new CssNode();
		cssNodes = profile.getCssNodes();
		cssNodes.remove(cssnode); 
		profile.setCssNodes(cssNodes);

		try {
			cssRegistry.registerCss(profile);
			result.setResultStatus(true);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
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
			LOG.error("Ops! Registration Exception", e);
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
				asyncResult = getServiceDiscovery().getServices(cssAdd.getId());
				cssServiceList = asyncResult.get();
				if (cssServiceList != null) {
					for (Service cssService : cssServiceList) {
						serviceList.add(cssService);
					}
					cssServiceList.clear();
				}
			} catch (InterruptedException e) {
				LOG.error("Ops!Interrupted Exception", e);
			} catch (ServiceDiscoveryException e) {
				LOG.error("Ops! Service Discovery Exception", e);
			} catch (ExecutionException e) {
				LOG.error("Ops! Execution Exception", e);
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
			LOG.error("Ops! XMPP Error", e1);
		} catch (CommunicationException e1) {
			LOG.error("Ops! Communication Exception", e1);
		}


	    try {
	    	String status = this.pubSubManager.publisherPublish(pubsubID, pubsubNodeName, Integer.toString(this.randomGenerator.nextInt()), event);
			LOG.debug("Event published: " + status);
		} catch (XMPPError e) {
			LOG.error("Ops! XMPP Error", e);
		} catch (CommunicationException e) {
			LOG.error("Ops! Communication Exception", e);
		} catch (Exception e) {
			LOG.error("Ops! Exception", e);
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
	
	private IActivityFeedManager iActivityFeedManager;

    
    public IActivityFeedManager getiActivityFeedManager() {
		return iActivityFeedManager;
	}

	public void setiActivityFeedManager(IActivityFeedManager iActivityFeedManager) {
		this.iActivityFeedManager = iActivityFeedManager;
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
	
	@Autowired
	private ICisManagerCallback ciscallback;
	
	public ICisManagerCallback getciscallback() {
		return ciscallback;
	}

	public void setciscallback(ICisManagerCallback ciscallback) {
		this.ciscallback = ciscallback;
	}
	
	@Autowired
	private ICisManager cisManager;
	
	public ICisManager getCisManager() {
		return cisManager;
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
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

		try {
			recordList = cssRegistry.getCssRequests();
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Regristration Exception", e);
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

		try {
			recordList = cssRegistry.getCssFriendRequests();
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Regristration Exception", e);
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

		//TODO: This is our response to a request by other css
		//we can accept, ignored etc
		try {
			cssRegistry.updateCssRequestRecord(request);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Regristration Exception", e);
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
			LOG.error("Ops! Regristration Exception", e);
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
		if(request.getRequestStatus() == (CssRequestStatusType.DELETEFRIEND)){
			List<String> listIDs = new ArrayList<String>();
			listIDs.add(request.getCssIdentity());
			final String who = request.getCssIdentity();
			cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
				@Override
				public void getResult(List<CssAdvertisementRecord> resultList) {
					if (resultList.size() > 0){
						addActivityToCSSAF("Removed " + resultList.get(0).getName() + " from friends");
					}else{
						addActivityToCSSAF("Removed " + who + " from friends");
					}
				}
			});
		}
		if(request.getRequestStatus() == (CssRequestStatusType.CANCELLED)){
			List<String> listIDs = new ArrayList<String>();
			listIDs.add(request.getCssIdentity());
			final String who = request.getCssIdentity();
			cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
				@Override
				public void getResult(List<CssAdvertisementRecord> resultList) {
					if (resultList.size() > 0){
						addActivityToCSSAF("Cancelled friend request to " + resultList.get(0).getName());
					}else{
						addActivityToCSSAF("Cancelled friend request to " + who);
					}
				}
			});
		}
	}



	/* (non-Javadoc)
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#sendCssFriendRequest(java.lang.String)
	 */
	@Override
	public void sendCssFriendRequest(String cssFriendId) {
		
		CssRequest request = new CssRequest();
		request.setCssIdentity(cssFriendId);
		
		//check if it exists first
		request.setRequestStatus(CssRequestStatusType.PENDING);
		try {
			cssRegistry.updateCssFriendRequestRecord(request);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Regristration Exception", e);
		}

		// This will always be initalliated locally so no need to check origin
		// db updated ow send it to friend and forget about it
		//cssManagerRemote.se
		LOG.info("~~~~~~~~~~~~~~~ sending Friend request : " +cssFriendId);
		LOG.info("sending Friend request : " +cssFriendId);
		cssManagerRemote.sendCssFriendRequest(cssFriendId);

		//UPDATE ACTIVITY FEED
		List<String> listIDs = new ArrayList<String>();
		listIDs.add(cssFriendId);
		final String who = cssFriendId;
		cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
			@Override
			public void getResult(List<CssAdvertisementRecord> resultList) {
				if (resultList.size() > 0){
					addActivityToCSSAF("Sent " + resultList.get(0).getName() + " a friend request");
				}else{
					addActivityToCSSAF("Sent friend request to " + who);
				}
			}
		});
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
				LOG.error("Ops! Regristration Exception", e);
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
				LOG.error("Ops! Regristration Exception", e);
			}

		return new AsyncResult<List<CssAdvertisementRecord>>(friendAdList);

	}

	public Future<String> getthisNodeType(String nodeId) {
		String Type = null, nodeid = null;
		LOG.info("getthisNodeType has been called: ");
		nodeid = nodeId;
		LOG.info("nodeid is now : " +nodeid);

		CssRecord currentCssRecord = null;
		try {
			currentCssRecord = cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Regristration Exception", e);
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
		LOG.info("From Webapp cssNodes SIZE is: " +cssrecord.getCssNodes().size());
		
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

		cssNodes = cssrecord.getCssNodes();

		LOG.info(" cssNodes are BEFORE : " +cssNodes);
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			LOG.info(" cssNode BEFORE index: " +index + " identity is now : " +cssNodes.get(index).getIdentity());
		}

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
		}


		this.modifyCssRecord(cssrecord); 

	}

public void removeNode(CssRecord cssrecord, String nodeId ) {

		List<CssNode> cssNodes = new ArrayList<CssNode>();

		try {
			cssRegistry.unregisterCss(cssrecord);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Regristration Exception", e);
		}

		cssNodes = cssrecord.getCssNodes();

		LOG.info("removeNode cssNodes SIZE is: " +cssrecord.getCssNodes().size());
		LOG.info("removeNode nodeId to remove is : " +nodeId);
		int index = 0;

		cssNodes = cssrecord.getCssNodes();
		for (index = 0; index < cssrecord.getCssNodes().size(); index ++) {
			if (cssNodes.get(index).getIdentity().equalsIgnoreCase(nodeId)) {
				LOG.info("removeNode loop identity : " +cssNodes.get(index).getIdentity());
				cssNodes.remove(index); 
				LOG.info("removeNode Node Removed : ");
			}
			LOG.info("removeNode cssNodes element is : " +cssNodes.get(index).getIdentity());
		}
		cssrecord.setCssNodes(cssNodes);
		LOG.info("removeNode cssrecord SIZE final : " +cssrecord.getCssNodes().size());

		try {
			cssRegistry.registerCss(cssrecord);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Regristration Exception", e);
		}

	}

@SuppressWarnings("unchecked")
public Future<List<CssAdvertisementRecord>> suggestedFriends( ) {

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


	// Generate the connector
	Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
	socialdata.updateSocialData();

	while (it.hasNext()){
	  ISocialConnector conn = it.next();
  	  
	LOG.info("SocialNetwork connector contains " +conn.getConnectorName());
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
	
    
    //compare the lists to create
    
    LOG.info("CSS Friends List contains " +cssFriends.size() +" entries");
    LOG.info("Social Friends List contains " +socialFriends.size() +" entries");
    LOG.info("common Friends List contains " +commonFriends.size() +" entries");
    
    //compare the two lists
    LOG.info("Compare the two lists to generate a common Friends list");

   
    for (CssAdvertisementRecord friend : cssFriends) {
    	LOG.info("CSS Friends iterator List contains " +friend);
        if (socialFriends.contains(friend.getName())) {
        	if (commonFriends.contains(friend)){
        		LOG.info("This friend is already added to the list:" +friend);	
        	}else {
        		commonFriends.add(friend);
        	}
        	
        }
       
    }
    LOG.info("common Friends List NOW contains " +commonFriends.size() +" entries");
	return new AsyncResult<List<CssAdvertisementRecord>>(commonFriends);

	}

	/**
	 * Get today's date
	 * 
	 * @return String today's date
	 */
/*	private String getDate() {
		Calendar today = Calendar.getInstance();

		StringBuffer date = new StringBuffer();

		date.append(Integer.toString(today.get(Calendar.YEAR)));
		date.append(Integer.toString(today.get(Calendar.MONTH)));
		date.append(Integer.toString(today.get(Calendar.DAY_OF_MONTH)));

		return date.toString();
	}*/

	@Override
	public Future<List<CssAdvertisementRecord>> getFriendRequests() {
		List<CssRequest> pendingfriendList = new ArrayList<CssRequest>();
		List<CssAdvertisementRecord> friendReqList = new ArrayList<CssAdvertisementRecord>();
		List<String> pendingList = new ArrayList<String>();	


		try {
			//pendingfriendList = cssRegistry.getCssFriendRequests();
			pendingfriendList = cssRegistry.getCssRequests();

			for (CssRequest cssrequest : pendingfriendList) {
		    	LOG.info("CSS FriendRequest iterator List contains " +pendingfriendList);
		    	LOG.info("cssrequest status is: " +cssrequest.getRequestStatus());
		        if (cssrequest.getRequestStatus().value().equalsIgnoreCase("pending")) {
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
				LOG.error("Ops! Regristration Exception", e);
			}

		return new AsyncResult<List<CssAdvertisementRecord>>(friendReqList);
	}

	public void acceptCssFriendRequest(CssRequest request) {

		//This is called either locally or remote
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
						LOG.error("error trying to internally publish SUBS CIS event", e);
					}
				}
			} catch (CssRegistrationException e) {
				LOG.error("Ops! Regristration Exception", e);
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
			List<String> listIDs = new ArrayList<String>();
			listIDs.add(request.getCssIdentity());
			final String who = request.getCssIdentity();
			cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
				@Override
				public void getResult(List<CssAdvertisementRecord> resultList) {
					if (resultList.size() > 0){
						addActivityToCSSAF("Accepted friend request from " +resultList.get(0).getName());
					}else{
						addActivityToCSSAF("Accepted friend request from " + who);
					}
				}
			});
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
			LOG.error("Ops! Regristration Exception", e);
		}

		// If this was initiated locally then inform remote css
		// We only want to sent messages to remote Css's for this function if we initiated the call locally
		if (request.getOrigin() == CssRequestOrigin.LOCAL) {
			// If we have denied the request , we won't sent message,it will just remain at pending in remote cs db
			// otherwise send message to remote css

			//called updateCssFriendRequest on remote
			request.setOrigin(CssRequestOrigin.REMOTE);
			cssManagerRemote.declineCssFriendRequest(request);
		}
		if (request.getOrigin() == CssRequestOrigin.REMOTE) {
			// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
			// otherwise send message to remote css

			//called updateCssFriendRequest on remote
			request.setOrigin(CssRequestOrigin.REMOTE);
			cssManagerRemote.updateCssFriendRequest(request); 
		}
		//ACTIVITY FEED
		List<String> listIDs = new ArrayList<String>();
		listIDs.add(request.getCssIdentity());
		final String who = request.getCssIdentity();
		cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
			@Override
			public void getResult(List<CssAdvertisementRecord> resultList) {
				if (resultList.size() > 0){
					addActivityToCSSAF(resultList.get(0).getName() + " declined your friend request");
				}else{
					addActivityToCSSAF("Declined friend request from " + who);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Future<HashMap<IIdentity, Integer>> getSuggestedFriends(
			FriendFilter filter) {

		
		Integer filt = filter.getFilterFlag();
		LOG.info("getSuggestedFriends Friends filter contains: " +filt);
		
		final Integer none	 		= 0x0000000000;
		final int facebook   		= 0x0000000001;
		final int twitter   		= 0x0000000010;
		final int linkedin   		= 0x0000000100;
		final int foursquare 		= 0x0000001000;
		final int googleplus 		= 0x0000010000;
		final int CIS_MEMBERS_BIT 	= 0x0000100000;
		
		boolean flag = BitCompareUtil.isFacebookFlagged(filt);
		
		flag = BitCompareUtil.isTwitterFlagged(filt);
		
		flag = BitCompareUtil.isLinkedinFlagged(filt);
		
		flag = BitCompareUtil.isFoursquareFlagged(filt);
		flag = BitCompareUtil.isGooglePlusFlagged(filt);
		
		List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecord> cssFriends = new ArrayList<CssAdvertisementRecord>();
		List<IIdentity> cssFriend = new ArrayList<IIdentity>();
		List<Person> snFriends = new ArrayList<Person>();
//		List<String> socialFriends = new ArrayList<String>();
		List<ICis> cisList = new ArrayList<ICis>();	
		List<String> facebookFriends = new ArrayList<String>();
		List<String> twitterFriends = new ArrayList<String>();
		List<String> linkedinFriends = new ArrayList<String>();
		List<String> foursquareFriends = new ArrayList<String>();
		List<String> googleplusFriends = new ArrayList<String>();
//		List<String> CISMembersFriends = new ArrayList<String>();
		List<String> alreadyListed = new ArrayList<String>();
		Future<List<CssAdvertisementRecordDetailed>> asynchallcss =  this.getCssAdvertisementRecordsFull();
		List<CssAdvertisementRecordDetailed> allcssDetails = new ArrayList<CssAdvertisementRecordDetailed>();
		Future<List<CssAdvertisementRecord>> asyncalreadyFriends = this.getCssFriends();
		List<CssAdvertisementRecord> alreadyFriends = new ArrayList<CssAdvertisementRecord>();
		
		HashMap<IIdentity, Integer> commonFriends = new HashMap<IIdentity, Integer>();
		HashMap<IIdentity, Integer> comparedFriends = new HashMap<IIdentity, Integer>();
		String MyId = "";	
		MyId = idManager.getThisNetworkNode().toString();
		IIdentity myIdentity = null;
		myIdentity = this.commManager.getIdManager().getThisNetworkNode();
		
		LOG.info("getSuggestedFriends checking CIS BIT: ");
		//Check if the CIS_MEMBERS_BIT is set if it is use this as the base group
		if(flag = BitCompareUtil.isCisMembersFlagged(filt)){
			//get the list of CIS members from the CIS Manager
			ServiceResourceIdentifier myServiceID = new ServiceResourceIdentifier();
			RequestorService service = new RequestorService(myIdentity, myServiceID);
			ICisManagerCallback callback1 = this.getciscallback();
			CommunityMethods result = new CommunityMethods();
			WhoResponse who = new WhoResponse();
			List<Participant> participant = new ArrayList<Participant>();
			cisList = this.cisManager.getCisList();
			LOG.info("getCisList returns : " +cisList);
			if(cisList.size() > 0){
				for(int i = 0; i < cisList.size(); i++){
					try{
						cisManager.getListOfMembers(service, this.getCommManager().getIdManager().fromJid(cisList.get(i).getCisId()), callback1);
						callback1.receiveResult(result);
						participant = result.getWhoResponse().getParticipant();
						
					}catch (InvalidFormatException e) {	
						LOG.error("Ops! Invalid Format Exception", e);
					}
					
					
				}
				
				asynchallcss = this.getCssAdvertisementRecordsFull();
				try {
					allcssDetails = asynchallcss.get();
				} catch (InterruptedException e) {
					LOG.error("Ops! Interrupted Exception", e);
				} catch (ExecutionException e) {
					LOG.error("Ops! Execution Exception", e);
				}
				
				CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

				getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
				recordList = callback.getResultList();
				
				for (Participant part : participant){
					
					if (!part.getJid().equalsIgnoreCase(MyId)) {
						for(int i = 0; i <recordList.size(); i++){
							if(recordList.get(i).getId().equalsIgnoreCase(part.getJid())){
								try {
									cssFriend.add(this.commManager.getIdManager().fromJid(recordList.get(i).getId()));
								} catch (InvalidFormatException e) {
									LOG.error("Ops! Invalid Format Exception", e);
								}
							}
						
						}
					
					}else {
						LOG.info("This is my OWN ID not adding it");
					}
				}
			}
				
			// first get all the cssdirectory records
					
					asynchallcss = this.getCssAdvertisementRecordsFull();
					try {
						allcssDetails = asynchallcss.get();
					} catch (InterruptedException e) {
						LOG.error("Ops! Interrupted Exception", e);
					} catch (ExecutionException e) {
						LOG.error("Ops! Execution Exception", e);
					}
					
					CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

					getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
					recordList = callback.getResultList();

					for (CssAdvertisementRecord cssAdd : recordList){
					
						if (cssAdd.getId().equalsIgnoreCase(MyId)) {
						LOG.info("This is my OWN ID not adding it");
						}else {
							try {
								cssFriend.add((this.commManager.getIdManager().fromJid(cssAdd.getId())));
							} catch (InvalidFormatException e) {
								LOG.error("Ops! Invalid Format Exception", e);
							}
						}
					}
			
		}else{
			// first get all the cssdirectory records
			LOG.info("getSuggestedFriends checking CSS Directory for Advertisements: ");
			CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

			getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
			recordList = callback.getResultList();
			
			try {
				alreadyFriends = asyncalreadyFriends.get();
				if (alreadyFriends.size() > 0){
					for (CssAdvertisementRecord arf : alreadyFriends){
						alreadyListed.add(arf.getName());
					}
				}
			} catch (InterruptedException e1) {
				LOG.error("Ops! Interrupted Exception", e1);
			} catch (ExecutionException e1) {
				LOG.error("Ops! Execution Exception", e1);
			}

			for (CssAdvertisementRecord cssAdd : recordList) {
			
				if (cssAdd.getId().equalsIgnoreCase(MyId)) {
				LOG.info("This is my OWN ID not adding it");
				}else {
					if (alreadyListed.contains(cssAdd.getName())){
						LOG.info("Already a friend not adding it");
					}
					else {
						try {
							cssFriend.add((this.commManager.getIdManager().fromJid(cssAdd.getId())));
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid Format Exception", e);
						}
					}
						
				
				}

			
			}
			LOG.info("CssFriend size is: " +cssFriend.size());
		}

		// Generate the connector
//		Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
		LOG.info("social connectors is " +socialdata.getSocialConnectors());
		
		LOG.info("Getting social friends");
		String domain ="";
		snFriends = (List<Person>) socialdata.getSocialPeople();
		LOG.info("Social Friends snFriends list size is " +snFriends.size());
		
		if (snFriends == null) {
			LOG.info("Social Friends is Null");
			snFriends = new ArrayList<Person>();
		}

	    Iterator<Person> itt = snFriends.iterator();
	    LOG.info("Social Friends Iterator " +itt);

	    while(itt.hasNext()){
	    	Person p =null;
	    	
	    		p = itt.next();

				String name = "Username NA";
				String id = null;
				try {
					if (p.getName() != null) {
						if (p.getName().getFormatted() != null){
							name = p.getName().getFormatted();
							LOG.info("name formatted " +name);
						}
						else {
							if (p.getName().getFamilyName() != null){
								name = p.getName().getFamilyName();
								LOG.info("name familyname " +name);
							}
							
							//Check brackets
							
							if (p.getName().getGivenName() != null) {
								if (name.length() > 0){
									name += " ";
								}
								name += p.getName().getGivenName();
								LOG.info("name givenname " +name);
							}
						}

					}

					if (p.getAccounts() != null) {
						if (p.getAccounts().size() > 0) {
							domain = p.getAccounts().get(0).getDomain();
							LOG.info("domain " +domain);
						}
					}
					id = p.getId();
					if (p.getId().contains(":")) {
						id = p.getId().split(":")[0];
						LOG.info("ID " +id);
					}
				} catch (Exception ex) {
				LOG.error("Error while parsing the Person OBJ", ex);
			}
	    	
	    				
	    				if(id.equalsIgnoreCase("facebook")){
							filter.setFilterFlag(facebook);		
		    				facebookFriends.add(name);    				
	    				}
	    				if(id.equalsIgnoreCase("twitter")){
	    					
							filter.setFilterFlag(twitter);		
							
		    				twitterFriends.add(name);    				
	    				}
	    				if(id.equalsIgnoreCase("linkedin")){
	    					
							filter.setFilterFlag(linkedin);		
		    				linkedinFriends.add(name);    				
	    				}
	    				if(id.equalsIgnoreCase("foursquare")){
	    					
							filter.setFilterFlag(foursquare);		
							
		    				foursquareFriends.add(name);    				
	    				}
	    				if(id.equalsIgnoreCase("googleplus")){
							filter.setFilterFlag(googleplus);		
							
		    				googleplusFriends.add(name);    				
	    				}
	    				
				}
	    
	    //compare the lists to create
	    
	    LOG.info("checking FACEBOOK BIT: ");
	    flag = BitCompareUtil.isFacebookFlagged(filt);
	   
	    if(flag){
	    	for (CssAdvertisementRecord friend : cssFriends) {	        	
	        	
	            if (facebookFriends.contains(friend.getName())) {
	            	if(commonFriends.containsKey(friend)){
		            	int value = commonFriends.get(friend);
	            		int value1 = (value | linkedin);
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), value1);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}
	            		LOG.info("facebook adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
	            			
	            	}else {
	            		
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), facebook);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}            		
	            	}
	            	
	            }	
	           
	        }
	    	flag = false;
	    }
	    LOG.info("checking TWITTER BIT: ");	
	    flag = BitCompareUtil.isTwitterFlagged(filt);
	    if(flag){
	    	for (CssAdvertisementRecord friend : cssFriends) {
	        	
	            if (twitterFriends.contains(friend.getName())) {
	            	if(commonFriends.containsKey(friend)){
	            		int value = commonFriends.get(friend);
	            		int value1 = (value | facebook);
	            		
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), value1);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}
	            		LOG.info("twitter adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
	            	}
	            	
	            		
	            	}else {
	            		
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), twitter);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}
	            	}
	       
	        }
	    	flag = false;
	    }
	    LOG.info("checking LINKEDIN BIT: ");	
	    flag = BitCompareUtil.isLinkedinFlagged(filt);
	    if(flag){
	    	for (CssAdvertisementRecord friend : cssFriends) {
	        
	            if (linkedinFriends.contains(friend.getName())) {
	            	if(commonFriends.containsKey(friend)){
	            		int value = commonFriends.get(friend);
	            		int value1 = (value | linkedin);
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), value1);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}
	            		LOG.info("Linkedin adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
	            	}
	            	
	            		
	            	}else {
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), linkedin);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}
	            		
	            	}
	        }
	    	flag = false;
	    }
	    	
	    LOG.info("checking FOURSQUARE BIT: ");
	    flag = BitCompareUtil.isFoursquareFlagged(filt);
	    if(flag){
	    	for (CssAdvertisementRecord friend : cssFriends) {
	        	
	            if (foursquareFriends.contains(friend.getName())) {
	            	if(commonFriends.containsKey(friend)){
	            		int value = commonFriends.get(friend);
	            		int value1 = (value | foursquare);
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), value1);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}
	            		LOG.info("foursquare adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
	            	}
	            	
	            		
	            	}else {
	            		try {
							commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), foursquare);
						} catch (InvalidFormatException e) {
							LOG.error("Ops! Invalid format Exception", e);
						}
	            		
	            	}
	        }
	    	flag = false;
	    }
	    LOG.info("checking GOOGLEPLUS BIT: ");
	    flag = BitCompareUtil.isGooglePlusFlagged(filt);
	    	

	             if(flag){
	            	 for (CssAdvertisementRecord friend : cssFriends) {
	                 	
	            		 if (linkedinFriends.contains(friend.getName())) {
	            			 if(commonFriends.containsKey(friend)){
	            				int value = commonFriends.get(friend);
	 	                 		int value1 = (value | googleplus);
	 	                 		try {
	 	     						commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), value1);
	 	     					} catch (InvalidFormatException e) {
	 	     						LOG.error("Ops! Invalid format Exception", e);
	 	     					}
	 	                 		LOG.info("googleplus adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
	            			 }
	     	            	
	                     		
	                     	}else {
	                     		try {
									commonFriends.put(this.commManager.getIdManager().fromJid(friend.getId()), googleplus);
								} catch (InvalidFormatException e) {
									LOG.error("Ops! Invalid format Exception", e);
								}
	                     		
	                     	}
	                  
	                 }
	            	 flag = false;
	             }	    
	             
	             LOG.info("getSuggestedFriends NOW Compare the 2 Lists: ");
	             //compare the two lists
	               if(commonFriends.size() != 0){
	              	 
	  	             for(int i = 0; i < cssFriend.size(); i++){
	  					for(Entry<IIdentity, Integer> entry : commonFriends.entrySet()){
	  						if(entry.getKey().equals(cssFriend.get(i))){	
	  								LOG.info("commonFriends already has this entry : "+cssFriends.get(i).getName() +" with filter value: " +entry.getValue());
	  								comparedFriends.put(cssFriend.get(i), entry.getValue());
	  							}else {
	  								if(commonFriends.containsKey((cssFriend.get(i)))){
	  									LOG.info("commonFriends has this entry already: ");
	  								}else{
	  									comparedFriends.put(cssFriend.get(i), none);
	  								}
	  							}
	  						}
	  					}
	               }else {
	              	 for(int j = 0; j < cssFriend.size(); j++){
	              		comparedFriends.put(cssFriend.get(j), none);
	              	 }
	               }
	               LOG.info("getSuggestedFriends commonFriends size is now : " +comparedFriends.size());
	    
		return new AsyncResult<HashMap<IIdentity, Integer>> (comparedFriends);
	}

	@Override

public Future<HashMap<CssAdvertisementRecord, Integer>> getSuggestedFriendsDetails(
			FriendFilter filter) {

	
	Integer filt = filter.getFilterFlag();
	LOG.info("Friends filter contains: " +filt);
	
	final Integer none	 = 0x0000000000;
	final int facebook   = 0x0000000001;
	final int twitter   =  0x0000000010;
	final int linkedin   = 0x0000000100;
	final int foursquare = 0x0000001000;
	final int googleplus = 0x0000010000;
	final int CISMember	 = 0x0000100000;
	
	boolean flag = BitCompareUtil.isFacebookFlagged(filt);
	
	flag = BitCompareUtil.isTwitterFlagged(filt);
	
	flag = BitCompareUtil.isLinkedinFlagged(filt);
	
	flag = BitCompareUtil.isFoursquareFlagged(filt);
	flag = BitCompareUtil.isGooglePlusFlagged(filt);
	flag = BitCompareUtil.isCisMembersFlagged(filt);
	
	List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();
	List<CssAdvertisementRecord> cssFriends = new ArrayList<CssAdvertisementRecord>();
	List<Person> snFriends = new ArrayList<Person>();
	List<String> socialFriends = new ArrayList<String>();
	List<String> alreadyListed = new ArrayList<String>();
	
	List<String> facebookFriends = new ArrayList<String>();
	List<String> twitterFriends = new ArrayList<String>();
	List<String> linkedinFriends = new ArrayList<String>();
	List<String> foursquareFriends = new ArrayList<String>();
	List<String> googleplusFriends = new ArrayList<String>();
	
	List<ICis> cisList = new ArrayList<ICis>();
	Future<List<CssAdvertisementRecordDetailed>> asynchallcss =  this.getCssAdvertisementRecordsFull();
	List<CssAdvertisementRecordDetailed> allcssDetails = new ArrayList<CssAdvertisementRecordDetailed>();
	Future<List<CssAdvertisementRecord>> asyncalreadyFriends = this.getCssFriends();
	List<CssAdvertisementRecord> alreadyFriends = new ArrayList<CssAdvertisementRecord>();
	HashMap<CssAdvertisementRecord, Integer> commonFriends = new HashMap<CssAdvertisementRecord, Integer>();
	HashMap<CssAdvertisementRecord, Integer> comparedFriends = new HashMap<CssAdvertisementRecord, Integer>();
	
	
	
	String MyId = "";	
	MyId = idManager.getThisNetworkNode().toString();
	IIdentity myIdentity = null;
	myIdentity = this.commManager.getIdManager().getThisNetworkNode();
	flag = BitCompareUtil.isCisMembersFlagged(filt);
	//Check if the CIS_MEMBERS_BIT is set if it is use this as the base group
	if(flag = BitCompareUtil.isCisMembersFlagged(filt)){
		//get the list of CIS members from the CIS Manager
		ServiceResourceIdentifier myServiceID = new ServiceResourceIdentifier();
		RequestorService service = new RequestorService(myIdentity, myServiceID);
		ICisManagerCallback callback1 = this.getciscallback();
		CommunityMethods result = new CommunityMethods();
		WhoResponse who = new WhoResponse();
		List<Participant> participant = new ArrayList<Participant>();
		cisList = this.cisManager.getCisList();
		LOG.info("getCisList returns : " +cisList);
		if(cisList.size() > 0){
			for(int i = 0; i < cisList.size(); i++){
				try{
					cisManager.getListOfMembers(service, this.getCommManager().getIdManager().fromJid(cisList.get(i).getCisId()), callback1);
					callback1.receiveResult(result);
					participant = result.getWhoResponse().getParticipant();
					
				}catch (InvalidFormatException e) {	
					LOG.error("Ops! Invalid format Exception", e);
				}
				
				
			}
			
			asynchallcss = this.getCssAdvertisementRecordsFull();
			try {
				allcssDetails = asynchallcss.get();
			} catch (InterruptedException e) {
				LOG.error("Ops! Interrupted Exception", e);
			} catch (ExecutionException e) {
				LOG.error("Ops! Execution Exception", e);
			}
			
			CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

			getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
			recordList = callback.getResultList();
			
			for (Participant part : participant){
				
				if (!part.getJid().equalsIgnoreCase(MyId)) {
					for(int i = 0; i <recordList.size(); i++){
						if(recordList.get(i).getId().equalsIgnoreCase(part.getJid())){
							cssFriends.add(recordList.get(i));
						}
					
					}
				
				}else {
					LOG.info("This is my OWN ID not adding it");
				}
			}
		}
			
		// first get all the cssdirectory records
				
				asynchallcss = this.getCssAdvertisementRecordsFull();
				try {
					allcssDetails = asynchallcss.get();
				} catch (InterruptedException e) {
					LOG.error("Ops! Interrupted Exception", e);
				} catch (ExecutionException e) {
					LOG.error("Ops! Execution Exception", e);
				}
				
				CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

				getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
				recordList = callback.getResultList();

				for (CssAdvertisementRecord cssAdd : recordList){
				
					if (cssAdd.getId().equalsIgnoreCase(MyId)) {
					LOG.info("This is my OWN ID not adding it");
					}else {
						cssFriends.add((cssAdd));
					}
				}
		
	}else{
		// first get all the cssdirectory records
		LOG.info("checking CSS Directory for Adverts: ");
		asynchallcss = this.getCssAdvertisementRecordsFull();
		try {
			allcssDetails = asynchallcss.get();
		} catch (InterruptedException e) {
			LOG.error("Ops! Interrupted Exception", e);
		} catch (ExecutionException e) {
			LOG.error("Ops! Execution Exception", e);
		}
		
		CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

		getCssDirectoryRemote().findAllCssAdvertisementRecords(callback);
		recordList = callback.getResultList();
		
		try {
			alreadyFriends = asyncalreadyFriends.get();
			if (alreadyFriends.size() > 0){
				for (CssAdvertisementRecord arf : alreadyFriends){
					alreadyListed.add(arf.getName());
				}
			}
		} catch (InterruptedException e1) {
			LOG.error("Ops! Interrupted Exception", e1);
		} catch (ExecutionException e1) {
			LOG.error("Ops! Execution Exception", e1);
		}

		for (CssAdvertisementRecord cssAdd : recordList){
			if (cssAdd.getId().equalsIgnoreCase(MyId)) {
			LOG.info("@@@@@@@ This is my OWN ID not adding it");
			}else {
				if (alreadyListed.contains(cssAdd.getName())){
					LOG.info("Already a friend not adding it");
				}
				else {
					cssFriends.add((cssAdd));
				}
					
			}
		}
		LOG.info("###### cssFriends size is now: " +cssFriends.size());
		
	}

	// Generate the connector
//	Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
	LOG.info("social connectors is " +socialdata.getSocialConnectors());
	
	LOG.info("Getting social friends");
	String domain ="";
	snFriends = (List<Person>) socialdata.getSocialPeople();
	LOG.info("Social Friends snFriends list size is " +snFriends.size());
	if (snFriends == null) {
		LOG.info("Social Friends is Null");
		snFriends = new ArrayList<Person>();
	}

    Iterator<Person> itt = snFriends.iterator();
    LOG.info("Social Friends Iterator " +itt);
   
    while(itt.hasNext()){
    	Person p =null;
    	
    		p = itt.next();

			String name = "Username NA";

			String id = null;
			try {
				if (p.getName() != null) {
					if (p.getName().getFormatted() != null){
						name = p.getName().getFormatted();
						LOG.debug("name formatted " +name);
					}
					else {
						if (p.getName().getFamilyName() != null){
							name = p.getName().getFamilyName();
							LOG.debug("name familyname " +name);
						}
						if (p.getName().getGivenName() != null) {
							if (name.length() > 0){
								name += " ";
							}
							name += p.getName().getGivenName();
							LOG.debug("name givenname " +name);
						}
					}

				}

				if (p.getAccounts() != null) {
					if (p.getAccounts().size() > 0) {
						domain = p.getAccounts().get(0).getDomain();
						LOG.debug("domain " +domain);
					}
				}
				id = p.getId();
				if (p.getId().contains(":")) {
					id = p.getId().split(":")[0];
					LOG.debug("ID " +id);
				}
			} catch (Exception ex) {
			LOG.error("Error while parsing the Person OBJ", ex);
		}
    	
    				
    				if(id.equalsIgnoreCase("facebook")){
						filter.setFilterFlag(facebook);	
	    				facebookFriends.add(name);    	
	    				LOG.debug("facebookFriends size is " +facebookFriends.size());
    				}
    				if(id.equalsIgnoreCase("twitter")){
    					
						filter.setFilterFlag(twitter);		
						
	    				twitterFriends.add(name);
	    				LOG.debug("twitterFriends size is " +facebookFriends.size());
    				}
    				if(id.equalsIgnoreCase("linkedin")){
    					
						filter.setFilterFlag(linkedin);		
	    				linkedinFriends.add(name);
	    				LOG.debug("linkedinFriends size is " +linkedinFriends.size());
    				}
    				if(id.equalsIgnoreCase("foursquare")){
    					
						filter.setFilterFlag(foursquare);		
						
	    				foursquareFriends.add(name); 
	    				LOG.debug("foursquareFriends size is " +facebookFriends.size());
    				}
    				if(id.equalsIgnoreCase("googleplus")){
						filter.setFilterFlag(googleplus);		
						
	    				googleplusFriends.add(name);    
	    				LOG.debug("googleplusFriends size is " +facebookFriends.size());
    				}
    				
			}
    
    //compare the lists to create
      
    
    flag = BitCompareUtil.isFacebookFlagged(filt);
   
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        	
            if (facebookFriends.contains(friend.getName())) {
            	if (commonFriends.containsKey(friend)){
            		int value = commonFriends.get(friend);
                	int value1 = (value | facebook);
                	commonFriends.put(friend, value1);
                	LOG.info("facebook adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
            			
            	}else {
            		LOG.info("facebook adding to commonfriends: " +friend.getName() +"with filter setting: " +facebook);
            		commonFriends.put(friend, facebook);            		
            	}
            	
            }	
           
        }
    	flag = false;
    }
    	
    flag = BitCompareUtil.isTwitterFlagged(filt);
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        	
            if (twitterFriends.contains(friend.getName())) {
            	if (commonFriends.containsKey(friend)){
            		int value = commonFriends.get(friend);
            		int value1 = (value | twitter);
            		commonFriends.put(friend, value1);
            		LOG.info("twitter adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
            		
            	}else {
            		LOG.info("twitter adding to commonfriends: " +friend.getName() +"with filter setting: " +twitter);
            		commonFriends.put(friend, twitter);
            	}
            	
            }
       
        }
    	flag = false;
    }
    	
    flag = BitCompareUtil.isLinkedinFlagged(filt);
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        
            if (linkedinFriends.contains(friend.getName())) {
            	if (commonFriends.containsKey(friend)){
            		int value = commonFriends.get(friend);
            		int value1 = (value | linkedin);
            		commonFriends.put(friend, value1);   
            		LOG.info("linkedin adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
            		
            	}else {
            		LOG.info("linkedin adding to commonfriends: " +friend.getName() +"with filter setting: " +linkedin);
            		commonFriends.put(friend, linkedin);
            		
            	}
            	
            }
        }
    	flag = false;
    }
    	

    flag = BitCompareUtil.isFoursquareFlagged(filt);
    if(flag){
    	for (CssAdvertisementRecord friend : cssFriends) {
        	
            if (foursquareFriends.contains(friend.getName())) {
            	if (commonFriends.containsKey(friend)){
            		int value = commonFriends.get(friend);
            		int value1 = (value | foursquare);
            		commonFriends.put(friend, value1);
            		LOG.info("4Square adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
            		
            	}else {
            		LOG.info("4Square adding to commonfriends: " +friend.getName() +"with filter setting: " +foursquare);
            		commonFriends.put(friend, foursquare);
            		
            	}
            	
            }
        }
    	flag = false;
    }
    
    flag = BitCompareUtil.isGooglePlusFlagged(filt);
    	

             if(flag){
            	 for (CssAdvertisementRecord friend : cssFriends) {
                 	
                     if (googleplusFriends.contains(friend.getName())) {
                    	 if (commonFriends.containsKey(friend)){
                     		int value = commonFriends.get(friend);
                    		int value1 = (value | googleplus);
                    		commonFriends.put(friend, value1);
                    		LOG.info("googleplus adding to commonfriends: " +friend.getName() +"with filter setting: " +value1);
                     		
                     	}else {
                     		LOG.info("googleplus adding to commonfriends: " +friend.getName() +"with filter setting: " +googleplus);
                     		commonFriends.put(friend, googleplus);
                     		
                     	}
                     	
                     }
                  
                 }
            	 flag = false;
             }
    	
           //compare the two lists
             LOG.info(" NOW compare the two lists ");
             
             if(commonFriends.size() != 0){
            	 
	             for(int i = 0; i < cssFriends.size(); i++){
	            	 LOG.info(" Outer for statement i = " +i);
					for(Entry<CssAdvertisementRecord, Integer> entry : commonFriends.entrySet()){
						if(entry.getKey().equals(cssFriends.get(i))){	
								LOG.info("commonFriends already has this entry : "+cssFriends.get(i).getName() +" with filter value: " +entry.getValue());
								LOG.info("Adding this to the compared list : "+cssFriends.get(i).getName() +" with filter value: " +entry.getValue());
								comparedFriends.put(cssFriends.get(i), entry.getValue());
							}else {
								if(commonFriends.containsKey((cssFriends.get(i)))){
									LOG.info("commonFriends has this entry already: ");
								}else{
									//commonFriends.put(cssFriends.get(i), none );
									comparedFriends.put(cssFriends.get(i), none);
									LOG.info("Putting this entry in comparedFriends: "+cssFriends.get(i).getName() +" with filter value: " +none);
								}
							}
						}
					}
             }else {
            	 
            	 for(int j = 0; j < cssFriends.size(); j++){
            		 LOG.info("Adding friends to comparedfriends list " +cssFriends.get(j).getName());
            		 comparedFriends.put(cssFriends.get(j), none );
            	 }
             }
             
             
             
    
	return new AsyncResult<HashMap<CssAdvertisementRecord, Integer>> (comparedFriends);
	}

	@Override
	public void sendCSSFriendRequest(IIdentity identity, RequestorService service) {
		//PUBLISH PUBSUB EVENT FOR THIS RECEIVED FRIEND REQUEST
		LOG.debug("Publishing friend request received event for: " + identity.getBareJid());
				
		//SAVE TO DATABASE
		String targetCSSid = identity.toString();
		CssRequest request = new CssRequest();
		request.setCssIdentity(service.getRequestorId().toString());
		request.setRequestStatus(CssRequestStatusType.PENDING);
		request.setOrigin(CssRequestOrigin.REMOTE);
		try {
			cssRegistry.updateCssRequestRecord(request);

		} catch (CssRegistrationException e) {
			
			LOG.error("Ops! Registration Exception", e);
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
					LOG.error("Ops! EMPP error", e);
				} catch (CommunicationException e) {
					LOG.error("Ops! Communication Exception", e);
				}
			}
		});
		//ACTIVITY FEED
		List<String> listIDs = new ArrayList<String>();
		listIDs.add(targetCSSid);
		final String who = targetCSSid;
		cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
			@Override
			public void getResult(List<CssAdvertisementRecord> resultList) {
				if (resultList.size() > 0){
					addActivityToCSSAF(resultList.get(0).getName() + " sent you a friend request");
				}else{
					addActivityToCSSAF("Recieved friend request from " + who);
				}
			}
		});
	}

	@Override
	public void handleExternalFriendRequest(IIdentity identity, CssRequestStatusType statusType) {
		//identity is who the request has come FROM
		final CssRequest request = new CssRequest();
		request.setCssIdentity(identity.toString());
		request.setRequestStatus(statusType);
		LOG.debug("handleExternalFriendRequest called : ");
		LOG.debug("Request from identity: " +identity);
		LOG.debug("Request  status: " +statusType);
		LOG.debug("Request  Origin: " +request.getOrigin());
		request.setOrigin(CssRequestOrigin.REMOTE);

		try {
			cssRegistry.updateCssFriendRequestRecord(request);
			cssRegistry.updateCssRequestRecord(request);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
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
						LOG.error("Ops! XMPP error", e);
					} catch (CommunicationException e) {
						LOG.error("Ops! Communication Exception", e);
					}
				}
			});
		}
		//ACTIVITY FEED
		List<String> listIDs = new ArrayList<String>();
		listIDs.add(request.getCssIdentity());
		LOG.debug("External Handle listIDs is : " +listIDs);
		final String who = request.getCssIdentity();
		cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
			@Override
			public void getResult(List<CssAdvertisementRecord> resultList) {
				if (resultList.size() > 0){
					addActivityToCSSAF("Friend request " + request.getRequestStatus() + " by " + resultList.get(0).getName());
				}else{
					addActivityToCSSAF("Friend request " + request.getRequestStatus() + " by " + who);
				}
				
			}
		});
	}

	@Override
	public void handleInternalFriendRequest(IIdentity identity, CssRequestStatusType statusType) {
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity(identity.toString());
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		acceptCssFriendRequest(pendingFR);
		
		LOG.debug("+++++++++++++handleInternalFriendRequest called : ");
		LOG.debug("+++++++++++++Request from identity: " +identity);
		LOG.debug("+++++++++++++Request  status: " +statusType);
		
		//ACTIVITY FEED
		List<String> listIDs = new ArrayList<String>();
		listIDs.add(identity.toString());
		LOG.debug("listIDs is : " +listIDs);
		final String who = identity.toString();
		cssDirectoryRemote.searchByID(listIDs, new ICssDirectoryCallback() {
			@Override
			public void getResult(List<CssAdvertisementRecord> resultList) {
				if (resultList.size() > 0){
					addActivityToCSSAF(" Accepted friend request from " +resultList.get(0).getName());
			
				}else{
					addActivityToCSSAF("Friend request accepted by " + who);
				}
				
			}
		});
	}
	
	@Override
	public void handleExternalUpdateRequest(IIdentity identity, CssRequestStatusType statusType) {
		//identity is who the request has come FROM
		final CssRequest request = new CssRequest();
		request.setCssIdentity(identity.toString());
		request.setRequestStatus(statusType);
		LOG.debug("handleExternalUpdateRequest called : ");
		LOG.debug("Request from identity: " +identity);
		LOG.debug("Request  status: " +statusType);
		request.setOrigin(CssRequestOrigin.REMOTE);

		try {
			cssRegistry.updateCssFriendRequestRecord(request);
			cssRegistry.updateCssRequestRecord(request);
		} catch (CssRegistrationException e) {
			LOG.error("Ops! Registration Exception", e);
		}
		// If this was initiated locally then inform remote css
		// We only want to sent messages to remote Css's for this function if we initiated the call locally
		if (request.getOrigin() == CssRequestOrigin.LOCAL) {
			// If we have denied the requst , we won't sent message,it will just remain at pending in remote cs db
			// otherwise send message to remote css
			LOG.debug("INSIDE IF STATEMENT -> Request  origin: " +request.getOrigin());
			//called updateCssFriendRequest on remote
			request.setOrigin(CssRequestOrigin.REMOTE);
			cssManagerRemote.updateCssRequest(request);
		}
		
	}

	public void pushtoContext(CssRecord record) {

		final String cssIdStr = record.getCssIdentity();
		LOG.info("pushtoContext is HERE: ");
		LOG.info("pushtoContext cssIdStr: " +cssIdStr);



		try {
			IIdentity cssId = commManager.getIdManager().fromJid(cssIdStr);
			LOG.info("pushtoContext cssId: " +cssId);
			CtxEntityIdentifier ownerCtxId = this.getCtxBroker().retrieveIndividualEntity(cssId).get().getId();

			LOG.info("pushtoContext ownerCtxId: " +ownerCtxId);

			String value;
			int value2;

			// NAME
			value = record.getName();
			LOG.info("pushtoContext NAME value: " +value);
			if (value != null && !value.isEmpty()){
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME, value);
			}

			// EMAIL
			value = record.getEmailID();
			LOG.info("pushtoContext EMAIL value: " +value);
			if (value != null && !value.isEmpty()){
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.EMAIL, value);
			}
			
			// HomeLocation
			value = record.getHomeLocation();
			LOG.info("pushtoContext HomeLocation value: " +value);
			if (value != null && !value.isEmpty()){
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.ADDRESS_HOME_CITY, value);
			}
			
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
			if (value != null && !value.isEmpty()){
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME_FIRST, value);
			}
			
			// Sex
			value2 = record.getSex();
			LOG.info("pushtoContext SEX value: " +value2);
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
			if (value != null && !value.isEmpty()){
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.ID, value);
				}

			// Workplace
			value = record.getWorkplace();
			LOG.info("pushtoContext WORKPLACE value: " +value);
			if (value != null && !value.isEmpty()){
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.ADDRESS_WORK_CITY, value);
			}
			
			// Position
			value = record.getPosition();
			LOG.info("pushtoContext POSITION value: " +value);
			if (value != null && !value.isEmpty()){
				updateCtxAttribute(ownerCtxId, CtxAttributeTypes.WORK_POSITION, value);
			}
			
			// CSS Nodes
			
			List<CssNode> cssNodes = new ArrayList<CssNode>();
			cssNodes = record.getCssNodes();
			for(int i = 0; i < cssNodes.size(); i++){
				LOG.info("pushtoContext CSSNODES value: " +cssNodes.get(i).getIdentity());
			}
			
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
			  
			  // MAC Address
			  value = cssNode.getCssNodeMAC();
			  if (value != null && !value.isEmpty()){
				  updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.MAC_ADDRESS, value);	
			  }  
			  LOG.info("pushtoContext MAC Address value: " +value);

			  // Interactable
			  value = cssNode.getInteractable();
			  if (value != null && !value.isEmpty()){
				  updateCtxAttribute(cssNodeEnt.getId(), CtxAttributeTypes.IS_INTERACTABLE, value);
			  } 
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
		if (!ctxIds.isEmpty()){
			attr = (CtxAttribute) this.ctxBroker.retrieve(ctxIds.get(0)).get();
		}else{
			attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
		}

		attr.setStringValue(value);
		attr.setValueType(CtxAttributeValueType.STRING);
		attr.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		this.ctxBroker.update(attr);
	}
	
	private CtxAttribute retrieveCtxAttribute(CtxEntityIdentifier ownerCtxId, String type) throws Exception {

		  if (LOG.isDebugEnabled()){
		    LOG.debug("Retrieving '" + type + "' attribute of entity " + ownerCtxId);
		  }
		  final List<CtxIdentifier> ctxIds = ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		  
		  if (!ctxIds.isEmpty()){
		    return (CtxAttribute) this.ctxBroker.retrieve(ctxIds.get(0)).get();
		  }else{
		    return null;
		  }
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

	/* @see org.societies.api.internal.css.ICSSInternalManager#getActivities(java.lang.String) */
	@Override
	public Future<List<MarshaledActivity>> getActivities(String timePeriod, int limitResults) {
		LOG.info("CSS MANAGER getActivities called");
		List<MarshaledActivity> listSchemaActivities = new ArrayList<MarshaledActivity>();  
		
		try {
			Future<List<IActivity>> result = activityFeed.getActivities(timePeriod, limitResults);
			listSchemaActivities = ConvertIActivities(result.get());
		} catch (InterruptedException e) {
			LOG.error("Ops! Interrupted Exception", e);
		} catch (ExecutionException e) {
			LOG.error("Ops! Execution Exception", e);
		}
		return new AsyncResult(listSchemaActivities); 
	}
	
	private List<MarshaledActivity> ConvertIActivities(List<IActivity> listActivities) {
		LOG.debug("CSS MANAGER ConvertIActivities: " + listActivities.size());
		List<MarshaledActivity> listSchemaActivities = new ArrayList<MarshaledActivity>(); 
		for (IActivity activity: listActivities) {
			try {
				org.societies.api.schema.activity.MarshaledActivity ma = new org.societies.api.schema.activity.MarshaledActivity();
				ma.setActor(activity.getActor());
				ma.setVerb(activity.getVerb());
		        if(activity.getObject()!=null && activity.getObject().isEmpty() == false ){
		        	ma.setObject(activity.getObject());
		        }
		        if(activity.getPublished()!=null && activity.getPublished().isEmpty() == false ){
		        	ma.setPublished(activity.getPublished());
		        }
	
		        if(activity.getTarget()!=null && activity.getTarget().isEmpty() == false ){
		        	ma.setTarget(activity.getTarget());
		        }
		        listSchemaActivities.add(ma);
			} catch (Exception ex) {
				LOG.error("Exception converting to MarshaledActivity: " + ex);
			}
		}
		return listSchemaActivities;
	}
	
}
