package org.societies.css.mgmt.testcomms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;
import org.societies.api.css.directory.ICssDirectoryCallback;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssManagerResultBean;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.utilities.DBC.Dbc;
import java.util.Random;

public class TestCommsMgmt {

	private ICSSRemoteManager remoteCSSManager;
    private PubsubClient pubSubManager;
    private IIdentityManager idManager;
    private ICommManager commManager;
    private IIdentity pubsubID;
    private ICssDirectoryRemote remoteCSSDirectory;
    private Random randomGenerator;

	
	private static Logger LOG = LoggerFactory.getLogger(TestCommsMgmt.class);
	
	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

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
	
	public static final String TEST_CSS_NAME = "Liam Marshall";
	public static final String TEST_CSS_JID = "alan.societies.bespoke";
	public static final String TEST_CSS_JID1 = "john.societies.local";

	
//	data required for Friends testing
//	insert into societiesdb.CssFriendEntry (friendIdentity, requestStatus) values ("liam@sligo.xmpp", "accepted"), ("maria@intel.xmpp", "accepted"), ("midge@home.com", "accepted");
//
//	insert into societiesdb.CssRequestEntry (cssIdentity, requestStatus) values ("eliza@hwu.xmpp", "pending"), ("sarah@hwu.org", "pending"), ("korbinian@dlr.de", "pending");
//
//
//	insert into societiesdb.CssAdvertisementRecordEntry (ID, Name, Uri) values ("liam@sligo.xmpp", "Liam Marshall", "liam@fb.com"), 
//	("maria@intel.xmpp", "Maria Mannion", "mmouse@twitter.ie"),
//	 ("midge@home.com", "Midge Baldwin", "midge@fb.co.uk"),
//	 ("eliza@hwu.xmpp", "Eliza Doolittle", "edoo@linkedin.com"),
//	 ("sarah@hwu.org", "Sarah G", "sg@myspace.com"),
//	 ("korbinian@dlr.de" , "Korby Frank", "kb@fb.de");

	
	private static final String CSS_PUBSUB_CLASS = "org.societies.api.schema.cssmanagement.CssEvent";
    private static final List<String> cssPubsubClassList = Collections.singletonList(CSS_PUBSUB_CLASS);

    /**
     * Create pubsub nodes for consumption by Android and other CSS non-cloud nodes
     * @throws InterruptedException 
     * 
     */
    public void testAndroidPubsub() throws InterruptedException {
    	LOG.info("Testing testAndroidPubsub");
        this.randomGenerator = new Random();

    	
		CssEvent event = new CssEvent();
		event.setType(CSSManagerEnums.ADD_CSS_NODE);
		event.setDescription(CSSManagerEnums.ADD_CSS_NODE_DESC);

		publishEvent(CSSManagerEnums.ADD_CSS_NODE, event);
		
		CssEvent event_1 = new CssEvent();
		event_1.setType(CSSManagerEnums.DEPART_CSS_NODE);
		event_1.setDescription(CSSManagerEnums.DEPART_CSS_NODE_DESC);
		
		this.publishEvent(CSSManagerEnums.DEPART_CSS_NODE, event_1);

    }
    
	public void testPubSub() {
		LOG.info("Testing Login/Pubsub");
		
		this.idManager = commManager.getIdManager();

//        try {
//			pubsubID = idManager.fromJid(THIS_NODE);
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

        IIdentity pubsubID = idManager.getThisNetworkNode();
        
        List<String> listTopics;
		try {
    		
	        LOG.info("Subscribing to pubsub");
	        //try internal class
	        pubSubManager.subscriberSubscribe(pubsubID, CSSManagerEnums.ADD_CSS_NODE, new pubsubReceiver());
			pubSubManager.subscriberSubscribe(pubsubID, CSSManagerEnums.DEPART_CSS_NODE, new pubsubReceiver());

	        LOG.info("Querying list of Nodes again");

	        listTopics = pubSubManager.discoItems(pubsubID, null);
	        for (String s: listTopics) {
	        	LOG.info("Node: " + s);
	        }
	        
	        this.remoteCSSManager.loginCSS(createCSSRecord(), new ICSSManagerCallback() {
				
				@Override
				public void receiveResult(CssManagerResultBean resultBean) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + resultBean.getResult().isResultStatus());
				}

			});
				
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		} catch (XMPPError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void testXMPPComms() {
		LOG.info("Calling remote CSSManager server for method loginCSS");
		for (int i = 0; i < 1; i++) {
			
//			this.remoteCSSManager.registerXMPPServer(new CssRecord(), new ICSSManagerCallback() {
			this.remoteCSSManager.loginCSS(createCSSRecord(), new ICSSManagerCallback() {
				
				public void receiveResult(CssManagerResultBean resultBean) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + resultBean.getResult().isResultStatus());
					
				}
			});
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.remoteCSSManager.logoutCSS(createCSSRecord(), new ICSSManagerCallback() {
				
				public void receiveResult(CssManagerResultBean resultBean) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + resultBean.getResult().isResultStatus());
					
				}
			});
			
			
			
		}
	}
	/**
	 * Test get CSSRecord
	 */
	public void testGetCssRecord() {
		LOG.info("Testing testGetCssRecord");
		this.remoteCSSManager.getCssRecord(new ICSSManagerCallback() {
		public void receiveResult(CssManagerResultBean resultBean) {
				LOG.info("Received result from remote call");
				LOG.info("Result Status: " + resultBean.getResult().isResultStatus());
				
				List<CssNode> nodes = resultBean.getResult().getProfile().getCssNodes();
				for (CssNode node: nodes) {
					LOG.info("Received node: " + node.getIdentity() + " status: " + node.getStatus() + " type: " + node.getType());
				}
			}
		});
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	
	/**
	 * Test friends specific CSS methods
	 */
	public void testFriendsFunctionality() {
		LOG.info("Calling remote CSSManager server for method getCssFriends");
			
		this.remoteCSSManager.getCssFriends(new ICSSManagerCallback() {
			
			public void receiveResult(CssManagerResultBean resultBean) {
				LOG.info("Received result from remote call for getCssFriends");
				LOG.info("Number of friends: " + resultBean.getResultAdvertList().size());
			}
		});

		LOG.info("Calling remote CSSManager server for method getFriendRequests");
		this.remoteCSSManager.getFriendRequests(new ICSSManagerCallback() {
			
			public void receiveResult(CssManagerResultBean resultBean) {
				LOG.info("Received result from remote call for getFriendRequests");
				LOG.info("Number of friend requests: " + resultBean.getResultCssRequestList().size());
			}
		});

		LOG.info("Calling remote CSSManager server for method suggestedFriends");
		this.remoteCSSManager.suggestedFriends(new ICSSManagerCallback() {
			
			public void receiveResult(CssManagerResultBean resultBean) {
				LOG.info("Received result from remote call for suggestedFriends");
				LOG.info("Number of suggested friends: " + resultBean.getResultAdvertList().size());
			}
		});
		
		LOG.info("Calling remote CSSManager server for method SEND_CSS_FRIEND_REQUEST_INTERNAL");
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.SEND_CSS_FRIEND_REQUEST_INTERNAL);
		messageBean.setRequestStatus(CssRequestStatusType.PENDING);
		messageBean.setTargetCssId("jane.societies.local");
		try {
			Stanza stanza = new Stanza(commManager.getIdManager().fromJid(TEST_CSS_JID1));
			this.commManager.sendMessage(stanza, messageBean);
		} catch (InvalidFormatException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.remoteCSSManager.sendCssFriendRequest(TEST_CSS_JID);

		LOG.info("Calling remote CSSDirectory server for method findAllCssAdvertisementRecords");
		this.remoteCSSDirectory.findAllCssAdvertisementRecords(new ICssDirectoryCallback() {
			
			@Override
			public void getResult(List<CssAdvertisementRecord> adverts) {
				LOG.info("Received result from remote call for findAllCssAdvertisementRecords");
				LOG.info("Number of CSSs: " + adverts.size());
				
			}
		});

		LOG.info("Calling remote CSSDirectory server for method findForAllCss");
		this.remoteCSSDirectory.findForAllCss(createCssAdvertisement(), new ICssDirectoryCallback() {
			
			@Override
			public void getResult(List<CssAdvertisementRecord> adverts) {
				LOG.info("Received result from remote call for findForAllCss");
				LOG.info("Number of filtered CSSs: " + adverts.size());
				
			}
		});
		
		/*
		LOG.info("Calling remote CSSManager server for method Accept Friends request");
		messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.ACCEPT_CSS_FRIEND_REQUEST_INTERNAL);
		messageBean.setRequestStatus(CssRequestStatusType.ACCEPTED);
		messageBean.setTargetCssId("jane.societies.local");
		try {
			Stanza stanza = new Stanza(commManager.getIdManager().fromJid(TEST_CSS_JID1));
			this.commManager.sendMessage(stanza, messageBean);
		} catch (InvalidFormatException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	/**
	 * Test the intermittent problem that causes the backing MySQL tables
	 * to have deleted rows.
	 * 
	 * @throws Exception
	 */
	public void testCSSMgmtPersistence() throws Exception {
		int NUM_ATTEMPTS = 10;
		
		for (int i = 0; i < NUM_ATTEMPTS; i++) {
			LOG.info("Testing testCSSMgmtPersistence");
			
			this.remoteCSSManager.loginCSS(this.createLoginCSSRecord(), new ICSSManagerCallback() {
			public void receiveResult(CssManagerResultBean resultBean) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + resultBean.getResult().isResultStatus());
					
					List<CssNode> nodes = resultBean.getResult().getProfile().getCssNodes();
					for (CssNode node: nodes) {
						LOG.info("Received node: " + node.getIdentity() + " status: " + node.getStatus() + " type: " + node.getType());
					}
				}
			});

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			this.remoteCSSManager.logoutCSS(this.createLoginCSSRecord(), new ICSSManagerCallback() {
			public void receiveResult(CssManagerResultBean resultBean) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + resultBean.getResult().isResultStatus());
					
					List<CssNode> nodes = resultBean.getResult().getProfile().getCssNodes();
					for (CssNode node: nodes) {
						LOG.info("Received node: " + node.getIdentity() + " status: " + node.getStatus() + " type: " + node.getType());
					}
				}
			});
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}



	}
	
	/**
	 * Test the intermittent problem that causes the backing MySQL tables
	 * to have deleted rows by submitting more than one login attempt
	 * 
	 * @throws Exception
	 */
	public void testCSSMgmtMultipleLogins() throws Exception {
		int NUM_ATTEMPTS = 2;
		
		for (int i = 0; i < NUM_ATTEMPTS; i++) {
			LOG.info("Testing testCSSMgmtPersistence");
			
			this.remoteCSSManager.loginCSS(this.createLoginCSSRecord(), new ICSSManagerCallback() {
			public void receiveResult(CssManagerResultBean resultBean) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + resultBean.getResult().isResultStatus());
					
					List<CssNode> nodes = resultBean.getResult().getProfile().getCssNodes();
					for (CssNode node: nodes) {
						LOG.info("Received node: " + node.getIdentity() + " status: " + node.getStatus() + " type: " + node.getType());
					}
				}
			});

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		cssNode_1 = new CssNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new CssNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		

		CssRecord cssProfile = new CssRecord();
		cssProfile.getCssNodes().add(cssNode_1);
		cssProfile.getCssNodes().add(cssNode_2);
		cssProfile.getArchiveCSSNodes().add(cssNode_1);
		cssProfile.getArchiveCSSNodes().add(cssNode_2);
		
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

		LOG.info("created CSS Record identity: " + cssProfile.getCssIdentity());
		LOG.info("created CSS Record password: " + cssProfile.getPassword());
		
		return cssProfile;
	}

	
	/**
	 * Create a default CSSrecord
	 * This is a temporary measure until genuine CSSs can be created
	 * 
	 * @return CssRecord
	 */
	private CssRecord createLoginCSSRecord() {
		
    	CssNode cssNode;

		cssNode = new CssNode();
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Android.ordinal());

		CssNode cssArchivedNode;

		cssArchivedNode = new CssNode();
		cssArchivedNode.setIdentity(TEST_IDENTITY_2);
		cssArchivedNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssArchivedNode.setType(CSSManagerEnums.nodeType.Android.ordinal());
		

		CssRecord cssProfile = new CssRecord();
		cssProfile.getCssNodes().add(cssNode);
		cssProfile.getArchiveCSSNodes().add(cssArchivedNode);
		
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

		LOG.info("created CSS Record identity: " + cssProfile.getCssIdentity());
		LOG.info("created CSS Record password: " + cssProfile.getPassword());
		
		return cssProfile;
	}

	
	/**
	 * Create Pubsub nodes
	 */
	private void createPubSubNodes() {
        
            LOG.debug("Creating PubsubNode(s) for CSSManager");

            try {
    			pubSubManager.addSimpleClasses(cssPubsubClassList);

    			pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.ADD_CSS_NODE);
    	        pubSubManager.ownerCreate(pubsubID, CSSManagerEnums.DEPART_CSS_NODE);
    	        
    		} catch (XMPPError e) {
    			e.printStackTrace();
    		} catch (CommunicationException e) {
    			e.printStackTrace();
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    	        LOG.debug(CSSManagerEnums.ADD_CSS_NODE + " PubsubNode created for CSSManager");
    	        LOG.debug(CSSManagerEnums.DEPART_CSS_NODE + " PubsubNode created for CSSManager");
    		}
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

	private CssAdvertisementRecord createCssAdvertisement() {
		CssAdvertisementRecord advert = new CssAdvertisementRecord();
		
		advert.setName(TEST_CSS_NAME);
		return advert;
	}
	//Spring injection methods

	public ICSSRemoteManager getRemoteCSSManager() {
		return remoteCSSManager;
	}

	public void setRemoteCSSManager(ICSSRemoteManager remoteCSSManager) {
		this.remoteCSSManager = remoteCSSManager;
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

	private class pubsubReceiver implements Subscriber {

	    //Subscriber implementation
		@Override
		public void pubsubEvent(IIdentity identity, String node, String itemId,
				Object payload) {
			LOG.debug("Received Pubsub event: " + node + " itemId: " + itemId + " payload class: " + payload.getClass().getName());

			if (payload instanceof CssEvent) {
				LOG.debug("Received event is :" + ((CssEvent) payload).getType());
			}
		}
	}

	public ICssDirectoryRemote getRemoteCSSDirectory() {
		return remoteCSSDirectory;
	}

	public void setRemoteCSSDirectory(ICssDirectoryRemote remoteCSSDirectory) {
		this.remoteCSSDirectory = remoteCSSDirectory;
	}
}
