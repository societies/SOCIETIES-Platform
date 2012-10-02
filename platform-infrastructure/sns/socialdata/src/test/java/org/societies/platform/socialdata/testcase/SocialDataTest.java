package org.societies.platform.socialdata.testcase;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.MockAwareVerificationMode;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;
import org.societies.platform.socialdata.SocialData;
import org.springframework.scheduling.annotation.AsyncResult;


public class SocialDataTest {


    private static String 			access_token = "";
	private static ISocialConnector fbConnector=null;
	private static SocialData		socialData =null;
	private static final Logger		 logger   = Logger.getLogger(SocialDataTest.class.getSimpleName());


	private static final String OWNER_IDENTITY_STRING = "myFooIIdentity@societies.local";
	private static final String NETWORK_NODE_STRING = "myFooIIdentity@societies.local/node";
	private static final String CIS_IDENTITY_STRING = "FooCISIIdentity@societies.local";
	//myFooIIdentity@societies.local
	private static final List<String> INF_TYPES_LIST = new ArrayList<String>(); 


	private static CtxEntityIdentifier mockCtxEntityId = mock (CtxEntityIdentifier.class);
	private static IndividualCtxEntity mockCtxEntity   = mock (IndividualCtxEntity.class);
	private static CtxAttributeIdentifier mockCtxAttributeId = mock(CtxAttributeIdentifier.class);


	private static IIdentityManager mockIdentityMgr = mock(IIdentityManager.class);
	private static IIdentity 		cssMockIdentity = mock(IIdentity.class);
	private static ICtxBroker		mockCtxBroker	= mock(ICtxBroker.class);

	private static INetworkNode mockNetworkNode = mock(INetworkNode.class);



	private static ISocialConnector mockedSocialConnector; 
	private static ICtxBroker broker = Mockito.mock(ICtxBroker.class);


	@BeforeClass
	public static void  setUp() throws Exception {


		when(mockIdentityMgr.getThisNetworkNode()).thenReturn(mockNetworkNode);
		when(mockNetworkNode.getBareJid()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockIdentityMgr.fromJid(OWNER_IDENTITY_STRING)).thenReturn(cssMockIdentity);
		when(mockNetworkNode.toString()).thenReturn(NETWORK_NODE_STRING);

		when(cssMockIdentity.toString()).thenReturn(OWNER_IDENTITY_STRING);
		when(cssMockIdentity.getType()).thenReturn(IdentityType.CSS);

		when(mockCtxBroker.retrieveIndividualEntity(cssMockIdentity)).thenReturn(new AsyncResult( new IndividualCtxEntity(mockCtxEntityId)));
		when(mockCtxBroker.createAttribute(mockCtxEntityId, CtxAttributeTypes.SOCIAL_NETWORK_CONNECTOR)).thenReturn(new AsyncResult( new CtxAttribute(mockCtxAttributeId)));

		//IIdentity scopeID = this.idMgr.fromJid(communityCtxEnt.getOwnerId());








		  mockedSocialConnector = mock(ISocialConnector.class);
		  stub(mockedSocialConnector.getConnectorName()).toReturn("facebook");
		  stub(mockedSocialConnector.getID()).toReturn("facebook_0001");
		  stub(mockedSocialConnector.getUserFriends()).toReturn(readFileAsString("mocks/friends.txt"));
		  stub(mockedSocialConnector.getUserActivities()).toReturn(readFileAsString("mocks/activities.txt"));
		  stub(mockedSocialConnector.getUserGroups()).toReturn(readFileAsString("mocks/groups.txt"));
		  stub(mockedSocialConnector.getUserProfile()).toReturn(readFileAsString("mocks/profile.txt"));



		 // fbConnector = new FacebookConnectorImpl(access_token, null);
		  logger.info("Start loading socialdata bundle");
		  socialData  = new SocialData(mockIdentityMgr, mockCtxBroker);

		//  when(broker.retrieveIndividualEntity(cssMockIdentity)).thenReturn(new AsyncResult(new IndividualCtxEntity(ctxEntityId)));

//		  ctxEntityId 	= new CtxEntityIdentifier(cssMockIdentity.getJid(), "Person", new Long(1));
//		  ctxEntity 	= new IndividualCtxEntity(ctxEntityId);	
//		  


		  assertNotNull("Social Data is null", socialData);
		  assertNotNull("Moked FB Connector is null", mockedSocialConnector);



		  logger.info("Created new FB Connector with id:" + mockedSocialConnector.getID());
		  logger.info("Created SocialData Container"+socialData.getLastUpdate());


		  try {

			//socialData.addSocialConnector(mockedSocialConnector);



//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put(ISocialConnector.AUTH_TOKEN, "");
//			ISocialConnector tw_con = socialData.createConnector(SocialNetwork.twitter, map);
//			socialData.addSocialConnector(tw_con);

			logger.info("Add Social Connector");
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getMessage());
		}


	}

	private static String readFileAsString(String filePath)
		    throws java.io.IOException{
		        StringBuffer fileData = new StringBuffer(1000);
		        BufferedReader reader = new BufferedReader(
		                new FileReader(filePath));
		        char[] buf = new char[1024];
		        int numRead=0;
		        while((numRead=reader.read(buf)) != -1){
		            String readData = String.valueOf(buf, 0, numRead);
		            fileData.append(readData);
		            buf = new char[1024];
		        }
		        reader.close();
		        return fileData.toString();
		    }


	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		mockedSocialConnector = null;

	}


	@Before
	public void updateSocialDataTest(){
		try{
			socialData.updateSocialData();
			logger.info("SocialData ready in memory");
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}

	}

	@Test
	public void printProfiles(){
		List<?> profiles = null;
		profiles = socialData.getSocialProfiles();
		assertNotNull("Profiles are null", profiles);
	    Iterator it = profiles.iterator();
	    int index =1;
	    while(it.hasNext()){
	    	Person p = (Person) it.next();
	    	logger.info("--- Profile "+index +" ID:" +p.getId() + " -->"+p.getName().getFormatted() );
	    	index++;
	    }

	}

	@Test
	public void printFriends(){
		List<?> friends = null;
		friends = socialData.getSocialPeople();
		assertNotNull("Friends list is null", friends);
	    Iterator it = friends.iterator();
	    int index =1;
	    while(it.hasNext()){
	    	Person p =null;
	    	try{
	    	p = (Person) it.next();
	    	logger.info(index +" FriendsID:" +p.getId() + " -->"+p.getName().getFormatted() );
	    	index++;
	    	}
	    	catch(Exception e){
	    	    e.printStackTrace();
	    	}
	    }

	}

	@Test
	public void printGroups(){
		List<?> groups = null;
		groups = socialData.getSocialGroups();
		assertNotNull("Social Group list is null", groups);
	    Iterator it = groups.iterator();
	    int index =1;
	    while(it.hasNext()){
	    	Group g = (Group) it.next();
	    	logger.info(index +" GroupID:" +g.getId() + " Title: "+g.getTitle() +  " descr:"+g.getDescription());
	    	index++;
	    }

	}

	@Test
	public void printActivities(){
		List<?> activities = null;
		activities = socialData.getSocialActivity();
		assertNotNull("Social Activity list is null", activities);
	    Iterator it = activities.iterator();
	    int index =1;
	    while(it.hasNext()){
	    	ActivityEntry a = (ActivityEntry) it.next();
	    	logger.info(index +"] " +a.getActor().getDisplayName() + " made "+a.getVerb() + " TO "+a.getContent());
	    	index++;
	    }

	}


	@Test
	public void printConnectors(){
		List<ISocialConnector> connectors = null;
		connectors = socialData.getSocialConnectors();
		assertNotNull("Connector list is null", connectors);
	    Iterator<ISocialConnector> it = connectors.iterator();
	    int index =1;
	    while(it.hasNext()){
	    	ISocialConnector conn = it.next();
	    	logger.info(index +"] Connector for " +conn.getConnectorName() + " ID: "+conn.getID());
	    	index++;
	    }

	}


	@Test
	public void postMessage(){
		socialData.postMessage(SocialNetwork.Facebook, "This is a JUNIT Test POST!");
	}


	/*
	@Test
	public void postCheckin(){
		
		HashMap<String, String> data = new HashMap<String, String>();
		
		data.put(ISocialData.POST_TYPE, ISocialData.CHECKIN);
		data.put(ISocialData.POST_PLACE, "171512719546772");
		data.put(ISocialData.POST_MESSAGE, "I'm doing a TEST checking @Trinity Capital Hotel Dublin");
		data.put(ISocialData.POST_LAT, "53.345149444145");
		data.put(ISocialData.POST_LON, "-6.2539714878708");
		
		
		
		socialData.postData(SocialNetwork.Facebook, data);
	}
	
	
	@Test
	public void postEvent(){
		
		HashMap<String, String> data = new HashMap<String, String>();
		
		data.put(ISocialData.POST_TYPE, ISocialData.EVENT);
		data.put(ISocialData.POST_DESCR, "This is an automatic test generated by JUNIT");
		data.put(ISocialData.POST_NAME, "Test Event for SOCIETIES");
		data.put(ISocialData.POST_LOCATION, "Nowhere, it's just a test");
		data.put(ISocialData.POST_FROM,  "2013-08-01 10:00");
		data.put(ISocialData.POST_TO,	 "2013-08-02 10:00");
		
		
		socialData.postData(SocialNetwork.Facebook, data);
	}
	*/




}