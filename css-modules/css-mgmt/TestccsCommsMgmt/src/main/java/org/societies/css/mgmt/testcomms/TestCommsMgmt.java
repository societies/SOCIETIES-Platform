package org.societies.css.mgmt.testcomms;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;

public class TestCommsMgmt {

	private ICSSRemoteManager remoteCSSManager;
    private PubsubClient pubSubManager;
    private IIdentityManager idManager;
    private ICommManager commManager;
    private IIdentity pubsubID;

	
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

	private static final String THIS_NODE = "XCManager.societies.local";

	public void testPubSub() {
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
            List<String> packageList = new ArrayList<String>();

            packageList.add("org.societies.api.schema.cssmanagement");
            try {
				pubSubManager.addJaxbPackages(packageList);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        LOG.info("Subscribing to pubsub");
	        //try internal class
	        pubSubManager.subscriberSubscribe(pubsubID, CSSManagerEnums.ADD_CSS_NODE, new pubsubReceiver());
			pubSubManager.subscriberSubscribe(pubsubID, CSSManagerEnums.DEPART_CSS_NODE, new pubsubReceiver());

	        LOG.info("Querying list of Nodes again");

	        listTopics = pubSubManager.discoItems(pubsubID, null);
	        for (String s: listTopics) {
	        	LOG.info("Node: " + s);
	        }
	        
	        this.remoteCSSManager.suggestedFriends(new ICSSManagerCallback()  {
				
				public void receiveResult(CssInterfaceResult result) {
					LOG.info("Received result from remote call");
					//LOG.info("Result Status: " + result.isResultStatus());
					
				}
			});

		
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
				
				public void receiveResult(CssInterfaceResult result) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + result.isResultStatus());
					
				}
			});
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.remoteCSSManager.logoutCSS(createCSSRecord(), new ICSSManagerCallback() {
				
				public void receiveResult(CssInterfaceResult result) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + result.isResultStatus());
					
				}
			});
			
			
			
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
			LOG.debug("Received Pubsub event: " + node + " itemId: " + itemId);

			if (payload instanceof CssEvent) {
				LOG.debug("Received event is :" + ((CssEvent) payload).getType());
			}
		}
		
	}
	


}
