/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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

package org.societies.cis.manager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.*;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.*;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.ParticipantRole;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.identity.IdentityImpl;
import org.societies.identity.NetworkNodeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Junit and Mockito Test for CIS
 *
 * @author Thomas Vilarinho (Sintef)
 *
 */

@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)  
@PrepareForTest( { Activity.class })
@ContextConfiguration(locations = { "../../../../CisManagerTest-context.xml" })
public class TestCisManager extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory
			.getLogger(TestCisManager.class);
	
	private CisManager cisManagerUnderTest;
	private ICisManager cisManagerUnderTestInterface;

	
	
	@Autowired
	private SessionFactory sessionFactory;
	private ICISCommunicationMgrFactory mockCcmFactory;
	private ICommManager mockCSSendpoint;
	private ICommManager mockCISendpoint1;
	private ICommManager mockCISendpoint2;
	private ICommManager mockCISendpoint3;
	private IPrivacyPolicyManager mockPrivacyPolicyManager;
	private IEventMgr mockEventMgr;
	private IServiceControlRemote mockIServCtrlRemote;
	private IPrivacyDataManager mockIPrivacyDataManager;
	private ICtxBroker mockContextBroker;
	private INegotiation mockNegotiation;
	
	private Activity mockActivity;
	
	private IActivityFeedManager mockActivityFeedManager;

	
	public static String CIS_MANAGER_CSS_ID = "testXcmanager.societies.local";
	
	//public static final String TEST_CSSID = "juca@societies.local";
	public static final String TEST_CSS_PWD = "password";
	public static final String TEST_CIS_NAME_1 = "Flamengo Futebol Clube";
	public static final String TEST_CIS_TYPW = "futebol";
	
	public static final String TEST_CISID_1 = "flamengo.societies.local";
	public static final String TEST_CISID_2 = "santos.societies.local";
	public static final String TEST_CIS_NAME_2 = "Santos Futebol Clube";
	public static final String TEST_CISID_3 = "palmeiras.societies.local";
	public static final String TEST_CIS_NAME_3 = "Palmeiras Futebol Clube";
	
	public static final String MEMBER_JID_1 = "zico@flamengo.com";
	public static final String MEMBER_ROLE_1 = "participant";

	public static final String MEMBER_JID_2 = "romario@vasco.com";
	public static final String MEMBER_ROLE_2 = "participant";

	public static final String MEMBER_JID_3 = "pele@santos.com";
	public static final String MEMBER_ROLE_3 = "admin";

	
	public static final String INVALID_USER_JID = "invalid";
	public static final String INVALID_ROLE = "invalid";
	
	
	public static final String TEST_CIS_TYPE2 = "hockey";
	public static final String TEST_CIS_DESC = "this is a CIS description";
	
	IIdentityManager mockIICisManagerId;
	INetworkNode testCisManagerId;
	
	INetworkNode testCisId_1;
	INetworkNode testCisId_2;
	INetworkNode testCisId_3;
	INetworkNode testDelCSSId;
	IIdentityManager mockIICisId_1;
	IIdentityManager mockIICisId_2;
	IIdentityManager mockIICisId_3;
	Session session = null;
	
	ICisDirectoryRemote mockICisDirRemote1;
	ICisDirectoryRemote mockICisDirRemote2;
	ICisDirectoryRemote mockICisDirRemote3;

	
	Stanza stanza;
	
	
	// context needed variable
	Future<IndividualCtxEntity> futMockCtxEntityIdentifier;
	CtxEntityIdentifier mockCtxEntityIdentifier; // ctx identifier of this CSS
	IndividualCtxEntity mockIndividualCtxEntity; // also ctx identifier of this CSS
	
	Future <CtxIdentifier> futureStatus;
	Future <CtxIdentifier> futureCity;
	Future<List<CtxIdentifier>> futureStatusList;
	List<CtxIdentifier> statusList;
	Future<List<CtxIdentifier>> futureCityList;
	List<CtxIdentifier> cityList;
    Future<List<CtxIdentifier>> futureReligiousList;
    List<CtxIdentifier> religionList;

	CtxAttributeIdentifier mockCityId;
	CtxAttributeIdentifier mockStatusId;
    CtxAttributeIdentifier mockReligionId;
	CtxAttribute mockCity;
	CtxAttribute mockStatus;
    CtxAttribute mockReligion;
	
	String city = "Paris";
	String status = "married";
    String religion = "jedi";
    
	IActivityFeed mockActivityFeed_1;
	IActivityFeed mockActivityFeed_2;
	IActivityFeed mockActivityFeed_3;
    
		
	void mockingContext() throws InterruptedException, ExecutionException, CtxException{
		 mockContextBroker = mock(ICtxBroker.class);
		 
		 mockCtxEntityIdentifier = new CtxEntityIdentifier(CIS_MANAGER_CSS_ID, "PERSON", new Long(12345));
		 mockIndividualCtxEntity = new IndividualCtxEntity(mockCtxEntityIdentifier);
		 
		 // mocking entity Identifier
		 futMockCtxEntityIdentifier = mock(Future.class);
		 mockCtxEntityIdentifier = mock(CtxEntityIdentifier.class);
		 when(futMockCtxEntityIdentifier.get()).thenReturn(mockIndividualCtxEntity );
		 
		 
		 // mocking attributes and attribute list
		 mockCityId = new CtxAttributeIdentifier(mockCtxEntityIdentifier, CtxAttributeTypes.ADDRESS_HOME_CITY, new Long(12344));
		 mockStatusId = new CtxAttributeIdentifier(mockCtxEntityIdentifier, CtxAttributeTypes.STATUS, new Long(12345));
         mockReligionId = new CtxAttributeIdentifier(mockCtxEntityIdentifier, CtxAttributeTypes.RELIGIOUS_VIEWS,new Long(12346));
		 mockCity = new CtxAttribute(mockCityId);
		 mockStatus = new CtxAttribute(mockStatusId);
         mockReligion = new CtxAttribute(mockReligionId);
		 mockCity.setStringValue(city);
		 mockStatus.setStringValue(status);
         mockReligion.setStringValue(religion);
		 statusList = new ArrayList<CtxIdentifier>();
		 cityList = new ArrayList<CtxIdentifier>();
         religionList = new ArrayList<CtxIdentifier>();
		 statusList.add(mockStatusId);
		 cityList.add(mockCityId);
         religionList.add(mockReligionId);
		 futureStatusList= mock(Future.class);
		 futureCityList= mock(Future.class);
         futureReligiousList = mock(Future.class);
		 when(futureStatusList.get()).thenReturn(statusList );
		 when(futureCityList.get()).thenReturn(cityList );
        when(futureReligiousList.get()).thenReturn(religionList );
		 
		 // mocking context broker
        LOG.info("mocking with: mockCtxEntityIdentifier = "+mockCtxEntityIdentifier.hashCode());
		 when(mockContextBroker.lookup(mockCtxEntityIdentifier, CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS)).thenReturn(futureStatusList);
         when(mockContextBroker.lookup(mockCtxEntityIdentifier, CtxModelType.ATTRIBUTE, CtxAttributeTypes.RELIGIOUS_VIEWS)).thenReturn(futureReligiousList);
		 when(mockContextBroker.lookup(mockCtxEntityIdentifier, CtxModelType.ATTRIBUTE, CtxAttributeTypes.ADDRESS_HOME_CITY)).thenReturn(futureCityList);
		 when(mockContextBroker.retrieveIndividualEntity(testCisManagerId)).thenReturn(futMockCtxEntityIdentifier);
		 when(mockContextBroker.retrieve(mockCityId)).thenReturn(new AsyncResult<CtxModelObject>(mockCity));
		 when(mockContextBroker.retrieve(mockStatusId)).thenReturn(new AsyncResult<CtxModelObject>(mockStatus));
        when(mockContextBroker.retrieve(mockReligionId)).thenReturn(new AsyncResult<CtxModelObject>(mockReligion));
		// (CtxAttribute) this.internalCtxBroker.retrieve(ctxId).get()
		 
		// when(mockCtxBroker.retrieveIndividualEntity(mockIdentity)).thenReturn(new AsyncResult<IndividualCtxEntity>(mockPersonEntity));
	}
	
	
	void setUpFactory() throws Exception {
		System.out.println("in setupFactory!");
		mockCcmFactory = mock(ICISCommunicationMgrFactory.class);
		mockIServCtrlRemote = mock(IServiceControlRemote.class);
		mockPrivacyPolicyManager = mock(IPrivacyPolicyManager.class);		
		mockEventMgr = mock(IEventMgr.class);
		mockNegotiation = mock(INegotiation.class);
		mockIPrivacyDataManager = mock(IPrivacyDataManager.class);
		

		
		mockActivityFeedManager = mock (IActivityFeedManager.class);
		
		// mocking the IcomManagers
		mockCISendpoint1 = mock (ICommManager.class);
		mockCISendpoint2 = mock (ICommManager.class);
		mockCISendpoint3 = mock (ICommManager.class);

		// mocking their Identity Manager
		mockIICisId_1 = mock (IIdentityManager.class);
		mockIICisId_2 = mock (IIdentityManager.class);
		mockIICisId_3 = mock (IIdentityManager.class);

		// mocking the IcisDirectoryRemote
		mockICisDirRemote1 = mock (ICisDirectoryRemote.class);
		//mockICisDirRemote2 = mock (ICisDirectoryRemote.class);
		//mockICisDirRemote3 = mock (ICisDirectoryRemote.class);
		
		when(mockPrivacyPolicyManager.deletePrivacyPolicy(any(org.societies.api.identity.RequestorCis.class))).thenReturn(true);
		when(mockPrivacyPolicyManager.updatePrivacyPolicy(anyString(),any(org.societies.api.identity.RequestorCis.class))).thenReturn(null);
		
		when(mockIPrivacyDataManager.checkPermission(any(Requestor.class), any(DataIdentifier.class), any(Action.class))).
		thenReturn(new ResponseItem(new RequestItem(null, null, null, true),Decision.PERMIT));
		
		doNothing().when(mockEventMgr).publishInternalEvent(any(org.societies.api.osgi.event.InternalEvent.class));
		
		doNothing().when(mockICisDirRemote1).addCisAdvertisementRecord(any(org.societies.api.schema.cis.directory.CisAdvertisementRecord.class));
		doNothing().when(mockICisDirRemote1).deleteCisAdvertisementRecord(any(org.societies.api.schema.cis.directory.CisAdvertisementRecord.class));
		
			
		
		// creating a NetworkNordImpl for each Identity Manager		
		testCisId_1 = new NetworkNodeImpl(TEST_CISID_1);
		testCisId_2 = new NetworkNodeImpl(TEST_CISID_2);
		testCisId_3 = new NetworkNodeImpl(TEST_CISID_3);
		//testCisId_3 = new NetworkNodeImpl(TEST_CISID_3);
		when(mockCISendpoint1.getIdManager()).thenReturn(mockIICisId_1);
		when(mockCISendpoint2.getIdManager()).thenReturn(mockIICisId_2);
		when(mockCISendpoint3.getIdManager()).thenReturn(mockIICisId_3);
		
		when(mockIICisId_1.getThisNetworkNode()).thenReturn(testCisId_1);
		when(mockIICisId_2.getThisNetworkNode()).thenReturn(testCisId_2);
		when(mockIICisId_3.getThisNetworkNode()).thenReturn(testCisId_3);
		
		
		when(mockCISendpoint1.UnRegisterCommManager()).thenReturn(true);
		when(mockCISendpoint2.UnRegisterCommManager()).thenReturn(true);
		when(mockCISendpoint3.UnRegisterCommManager()).thenReturn(true);
		
		//testDelCSSId = new NetworkNodeImpl("delCss@societies.org");
		doNothing().when(mockCISendpoint1).sendMessage(any(org.societies.api.comm.xmpp.datatypes.Stanza.class), any(Object.class)); // for the delete

		doNothing().when(mockCISendpoint1).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));
		doNothing().when(mockCISendpoint2).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));
		doNothing().when(mockCISendpoint3).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));
		//when(mockIICisId_1.fromJid(anyString())).thenReturn(testDelCSSId);// for the delete
		when(mockIICisId_2.fromJid(CIS_MANAGER_CSS_ID)).thenReturn(testCisManagerId);// for the delete
		when(mockIICisId_1.fromJid(CIS_MANAGER_CSS_ID)).thenReturn(testCisManagerId);// for the delete
		when(mockIICisId_3.fromJid(CIS_MANAGER_CSS_ID)).thenReturn(testCisManagerId);// for the delete

		
		when(mockCcmFactory.getNewCommManager()).thenReturn(mockCISendpoint1,mockCISendpoint2,mockCISendpoint3);
		
		this.mockingContext();
		
		

		
		// mocking activity feeds themselves
		mockActivityFeed_1  = mock (IActivityFeed.class);
		mockActivityFeed_2 = mock (IActivityFeed.class);
		mockActivityFeed_3 = mock (IActivityFeed.class);
		
		mockActivity = new Activity();
		
		when(mockActivityFeed_1.getEmptyIActivity()).thenReturn(mockActivity);
		when(mockActivityFeed_2.getEmptyIActivity()).thenReturn(mockActivity);
		when(mockActivityFeed_3.getEmptyIActivity()).thenReturn(mockActivity);
		doNothing().when(mockActivityFeed_1).addActivity(any(org.societies.api.activity.IActivity.class), any (org.societies.api.activity.IActivityFeedCallback.class));
		doNothing().when(mockActivityFeed_2).addActivity(any(org.societies.api.activity.IActivity.class), any (org.societies.api.activity.IActivityFeedCallback.class));
		doNothing().when(mockActivityFeed_3).addActivity(any(org.societies.api.activity.IActivity.class), any (org.societies.api.activity.IActivityFeedCallback.class));

		//acitivity feed mocking
		
		when(mockActivityFeedManager.deleteFeed(anyString(), anyString())).thenReturn(true);
		when(mockActivityFeedManager.getOrCreateFeed(anyString(), eq(TEST_CISID_1), eq(true))).thenReturn(mockActivityFeed_1);
		when(mockActivityFeedManager.getOrCreateFeed(anyString(), eq(TEST_CISID_2), eq(true))).thenReturn(mockActivityFeed_2);
		when(mockActivityFeedManager.getOrCreateFeed(anyString(), eq(TEST_CISID_3), eq(true))).thenReturn(mockActivityFeed_3);
				

		
		System.out.println("done mocking activities!");
	}
	
	@Before
	public void setUp() throws Exception {
		// create mocked class
		System.out.println("in setup!");
		mockCSSendpoint = mock (ICommManager.class);

		mockIICisManagerId = mock (IIdentityManager.class);
		CIS_MANAGER_CSS_ID += "newtest";
		testCisManagerId = new NetworkNodeImpl(CIS_MANAGER_CSS_ID);

		
		// mocking the CISManager
		when(mockCSSendpoint.getIdManager()).thenReturn(mockIICisManagerId);
		when(mockIICisManagerId.getThisNetworkNode()).thenReturn(testCisManagerId);
		doNothing().when(mockCSSendpoint).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));
		
		// mocking the activity feed static methods
		//PowerMockito.mockStatic(ActivityFeed.class);
		//this.session = sessionFactory.openSession();
		System.out.println("in setup! cisManagerUnderTest.getSessionFactory(): "+sessionFactory);
		//ActivityFeed.setStaticSessionFactory(sessionFactory);
		//cisManagerUnderTest.setSessionFactory(sessionFactory);
		//cisManagerUnderTest.setSessionFactory(sessionFactory);
		//Mockito.when(ActivityFeed.startUp(anyString())).thenReturn(new ActivityFeed());
		setUpFactory();
		
	}

	@After
	public void tearDown() throws Exception {
		mockCcmFactory = null;
		mockCSSendpoint = null;
		testCisManagerId = null;

//		this.deleteFromTables(new String[] { "org_societies_cis_manager_Cis"});
//		this.deleteFromTables(new String[] { "org_societies_cis_manager_CisParticipant"});
//		this.deleteFromTables(new String[] { "org_societies_cis_manager_CisRecord"});
		
		//sessionFactory.getCurrentSession().close();
		//if(sessionFactory.getCurrentSession()!=null)
		//	sessionFactory.getCurrentSession().disconnect();

	}
	
	
	////////////////////////////////////////
	// CONSTRUCTOR TESTING
	////////////////////////////////////////
	
	
	//@Ignore
	//@Rollback(true)
	@Test
	public void testConstructor() {

		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		assertEquals(CIS_MANAGER_CSS_ID, cisManagerUnderTest.cisManagerId.getJid());
		
		
	}
	
	
	///////////////////////////////////////////////////
	// Local Interface Testing
	//////////////////////////////////////////////////
	
	//@Ignore
	//@Rollback(true)
	@Test
	public void testCreateCIS() {
		
		System.out.println("testing create CIS");
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
		MembershipCriteria m = new MembershipCriteria();
		try{
			Rule r = new Rule("equals",new ArrayList<String>(Arrays.asList("married")));
			m.setRule(r);
			cisCriteria.put(CtxAttributeTypes.STATUS, m);
			r = new Rule("equals",new ArrayList<String>(Arrays.asList("Brazil")));
			m.setRule(r);
			cisCriteria.put(CtxAttributeTypes.ADDRESS_HOME_COUNTRY, m);
		}catch(InvalidParameterException e){
			// TODO: treat expection
			e.printStackTrace();
		}
		
		Future<ICisOwned> testCIS = cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW , cisCriteria,""); //TODO: test criteria and description
		try {
			assertNotNull(testCIS.get());
			assertNotNull(testCIS.get().getCisId());
			assertEquals(testCIS.get().getName(), TEST_CIS_NAME_1);
			assertEquals(testCIS.get().getCisType(), TEST_CIS_TYPW);

			// CLEANING UP
			cisManagerUnderTest.deleteCis(testCIS.get().getCisId());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("end of testing create CIS");
	
	}
	
	//@Ignore
	//@Rollback(true)
	@Test
	public void testListCIS() throws InterruptedException, ExecutionException {

		System.out.println("testing list CIS");
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned[] ciss = new ICisOwned [3]; 
		int[] cissCheck = {0,0,0};
		
		ciss[0] =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
		ciss[1] = (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_2, TEST_CIS_TYPW ,null,"")).get();
		ciss[2] = (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_3, TEST_CIS_TYPW ,null,"")).get();

		List<ICisOwned> l = cisManagerUnderTestInterface.getListOfOwnedCis();
		Iterator<ICisOwned> it = l.iterator();
		 
		while(it.hasNext()){
			ICisOwned element = it.next();
			 assertEquals(element.getOwnerId(),CIS_MANAGER_CSS_ID);
			 for(int i=0;i<ciss.length;i++){
				 if(element.getName().equals(ciss[i].getName()) 
				&& 	element.getCisId().equals(ciss[i].getCisId())
				&& 	element.getCisType().equals(ciss[i].getCisType())		 
						 )
					 cissCheck[i] = 1; // found a matching CIS
					 
			 }
			 
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		// check if it found all matching CISs
		 for(int i=0;i<ciss.length;i++){
			 assertEquals(cissCheck[i], 1);
		 }
	
		// CLEANING UP

		 for(int i=0;i<ciss.length;i++){
			 cisManagerUnderTestInterface.deleteCis(ciss[i].getCisId());
		 }

		 System.out.println("end of list CIS");
		 
	}

	//@Rollback(true)
	//@Ignore
	@Test
	public void testdeleteCIS() throws InterruptedException, ExecutionException {

		System.out.println("test delete CIS");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		//LOG.info("testdeleteCIS, sessionFactory: "+sessionFactory.hashCode());
		
		ICisOwned[] ciss = new ICisOwned [2]; 
		String jidTobeDeleted = "";
		
		ciss[0] =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
		ciss[1] = (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_2, TEST_CIS_TYPW ,null,"")).get();
		
//		LOG.info("cis 1 sessionfactory:"+((Cis)ciss[0]).getSessionFactory().hashCode());
		List<ICis> l = cisManagerUnderTestInterface.getCisList();
//		LOG.info("cis 1 sessionfactory:"+((Cis)l.get(0)).getSessionFactory());
		Iterator<ICis> it = l.iterator();
		ICis element = it.next(); 
		jidTobeDeleted = element.getCisId();
		
		boolean presence = false;
		
		presence = cisManagerUnderTestInterface.deleteCis(jidTobeDeleted);
		assertEquals(true,presence);
		
		presence = false;
		// refresh list and get a new iterator
		l = cisManagerUnderTestInterface.getCisList();
		it = l.iterator();
		
		int interactions = 0;
		while(it.hasNext()){
			 element = it.next();
			 interactions++;
			 if(element.getCisId().equals(jidTobeDeleted))		 
						presence = true; // found a matching CIS
	     }
		
		assertEquals(false,presence);
		assertEquals(1,interactions);
		
		
		// CLEANING UP
		l = cisManagerUnderTestInterface.getCisList();
		it = l.iterator();
		
		while(it.hasNext()){
			element = it.next();
			cisManagerUnderTestInterface.deleteCis( element.getCisId());
	     }

		System.out.println("end of delete CIS");
	
	}
	
	//@Ignore
	//@Rollback
	@Test
	public void testAddMemberToOwnedCIS() throws InterruptedException, ExecutionException {

		
		System.out.println("testing add member to owned CIS");
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();

	
			assertEquals(true,Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1));
			assertEquals(true,Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2));
			assertEquals(false,Iciss.addMember(MEMBER_JID_3, INVALID_ROLE));
			// assertEquals(false,Iciss.addMember(INVALID_USER_JID, MEMBER_ROLE_3).get());  NOT USE OF TESTING THAT AS IDENTITY MANAGER HAS BEEN MOCKED
		
		
		// CLEANING UP
		cisManagerUnderTestInterface.deleteCis(Iciss.getCisId());

		
	}
	
	//@Ignore
	//@Rollback
	@Test
	public void testDeleteMemberToOwnedCIS() throws InterruptedException, ExecutionException {

		System.out.println("testing delete member to owned CIS");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
		

	
			Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1);
			Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2);
			
			Iciss.removeMemberFromCIS(MEMBER_JID_1);
			
			int memberCheck = 0;
			
			Set<ICisParticipant> l = Iciss.getMemberList();
			Iterator<ICisParticipant> it = l.iterator();
			
			// search if member is still there
			while(it.hasNext()){
				ICisParticipant element = it.next();
				if(element.getMembersJid().equals(MEMBER_JID_1) )
					memberCheck = 1;
		     }
			
			// check if it found all matching CISs
				assertEquals(memberCheck, 0);
			
			
	
		
		// CLEANING UP
		cisManagerUnderTestInterface.deleteCis(Iciss.getCisId());

		
	}
	
	@Test
	public void searchCISbyName(){
		
		System.out.println("testing search CIS");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned[] ciss = new ICisOwned [3]; 
		int[] cissCheck = {0,0,0};
		
		try {
			ciss[0] =  (cisManagerUnderTestInterface.createCis(
					"alfa", TEST_CIS_TYPW ,null,"")).get();
			ciss[1] = (cisManagerUnderTestInterface.createCis(
					"alfaromeo", TEST_CIS_TYPW ,null,"")).get();
			ciss[2] = (cisManagerUnderTestInterface.createCis(
					"beta", TEST_CIS_TYPW ,null,"")).get();
			
			assertEquals(ciss[1],cisManagerUnderTest.searchCisByName("alfaromeo").get(0));
			assertEquals(2,cisManagerUnderTest.searchCisByName("alfa").size());
			assertEquals(0,cisManagerUnderTest.searchCisByName("gama").size());
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("exception");
		} catch (ExecutionException e) {

			e.printStackTrace();
			fail("exception");
		}
		
		
		// CLEANING UP

		 for(int i=0;i<ciss.length;i++){
			 cisManagerUnderTestInterface.deleteCis(ciss[i].getCisId());
		 }


	}
	
	@Test
	public void searchCISbyMember(){
		
		System.out.println("testing search CIS by member");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned[] ciss = new ICisOwned [3]; 
		
		try {
			ciss[0] =  (cisManagerUnderTestInterface.createCis(
					"alfa", TEST_CIS_TYPW ,null,"")).get();
			ciss[1] = (cisManagerUnderTestInterface.createCis(
					"alfaromeo", TEST_CIS_TYPW ,null,"")).get();
			ciss[2] = (cisManagerUnderTestInterface.createCis(
					"beta", TEST_CIS_TYPW ,null,"")).get();

			// MEMBER_JID_1
			ciss[0].addMember(MEMBER_JID_1, MEMBER_ROLE_1);
			ciss[1].addMember(MEMBER_JID_1, MEMBER_ROLE_1);
			
			// MEMBER_JID_2
			ciss[0].addMember(MEMBER_JID_2, MEMBER_ROLE_1);

			assertEquals(ciss[0],cisManagerUnderTest.searchCisByMember(new IdentityImpl(MEMBER_JID_2)).get(0));
			assertEquals(2,cisManagerUnderTest.searchCisByMember(new IdentityImpl(MEMBER_JID_1)).size());
			assertEquals(0,cisManagerUnderTest.searchCisByMember(new IdentityImpl(MEMBER_JID_3)).size());

		
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception");
		} 
	
		
		// CLEANING UP

		 for(int i=0;i<ciss.length;i++){
			 cisManagerUnderTestInterface.deleteCis(ciss[i].getCisId());
		 }


		
	}
	
	
	@Test
	public void listdMembersOnOwnedCIS() throws InterruptedException, ExecutionException {

		System.out.println("testing list members");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
				
	
			Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1);
			Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2);
			Iciss.addMember(MEMBER_JID_3, MEMBER_ROLE_3);
		
		
		
		int[] memberCheck = {0,0,0};
		
		Set<ICisParticipant> l = Iciss.getMemberList();
		Iterator<ICisParticipant> it = l.iterator();
		 
		while(it.hasNext()){
			ICisParticipant element = it.next();
			if(element.getMembersJid().equals(MEMBER_JID_1) && element.getMembershipType().equalsIgnoreCase(MEMBER_ROLE_1))
				memberCheck[0] = 1;
			if(element.getMembersJid().equals(MEMBER_JID_2) && element.getMembershipType().equalsIgnoreCase(MEMBER_ROLE_2))
				memberCheck[1] = 1;	
			if(element.getMembersJid().equals(MEMBER_JID_3) && element.getMembershipType().equalsIgnoreCase(MEMBER_ROLE_3))
				memberCheck[2] = 1;	

	     }
		
		// check if it found all matching CISs
		 for(int i=0;i<memberCheck.length;i++){
			 assertEquals(memberCheck[i], 1);
		 }	
	
	 
		// CLEANING UP
		 cisManagerUnderTestInterface.deleteCis(Iciss.getCisId());
	}
	
	@Ignore
	@Test
	public void addActivity() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
	
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
		
		
		
		IActivity iActivity = new org.societies.activity.model.Activity();
		iActivity.setActor("act");
		iActivity.setObject("obj");
		iActivity.setTarget("tgt");
		iActivity.setVerb("verb");

		IActivity iActivity2 = new org.societies.activity.model.Activity();
		iActivity2.setActor("act2");
		iActivity2.setObject("obj2");
		iActivity2.setTarget("tgt2");
		iActivity2.setVerb("verb2");

		class DummyAddActFeedCback implements IActivityFeedCallback {
			public void receiveResult(MarshaledActivityFeed activityFeedObject){
                assertEquals(activityFeedObject.getAddActivityResponse().isResult(),true);
			}
		}
		
		
		Iciss.getActivityFeed().addActivity(iActivity, new DummyAddActFeedCback());
		Iciss.getActivityFeed().addActivity(iActivity2,new DummyAddActFeedCback());
		System.out.println((System.currentTimeMillis() -20000) + " " + System.currentTimeMillis());
		
		
		class getActivitiesCallback implements IActivityFeedCallback {

			String parentJid = "";
			
			public getActivitiesCallback (String parentJid){
				super();
				this.parentJid = parentJid;
			}
			
			public void receiveResult(MarshaledActivityFeed activityFeedObject){

				int[] check = {0,0};
				
				List<MarshaledActivity> l = activityFeedObject.getGetActivitiesResponse().getMarshaledActivity();
				
				Iterator<MarshaledActivity> it = l.iterator();
				
				while(it.hasNext()){
                    MarshaledActivity element = it.next();
					if(element.getActor().equals("act") )
						check[0] = 1;
					if(element.getActor().equals("act2") )
						check[1] = 1;

			     }
				
				// check if it found all matching CISs
				 for(int i=0;i<check.length;i++){
					 assertEquals(check[i], 1);
				 }

				// CLEANING UP
				 cisManagerUnderTestInterface.deleteCis(this.parentJid);

				
			}
		}
		
		
		
		Iciss.getActivityFeed().getActivities((System.currentTimeMillis() -2000000) + " " + System.currentTimeMillis(), new getActivitiesCallback(Iciss.getCisId()));
		
		
	}
	

	
	///////////////////////////////////////////////////
	// Local Interface with Callback Testing
	//////////////////////////////////////////////////
	//@Ignore
	@Test
	public void listdMembersOnOwnedCISwithCallback() throws InterruptedException, ExecutionException {

		System.out.println("testing list w callback");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
		String cisJid = Iciss.getCisId();
				

			Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1);
			Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2);
			Iciss.addMember(MEMBER_JID_3, MEMBER_ROLE_3);

		
		// callback that will do the real test

		 class GetListCallBack implements ICisManagerCallback{

			public String cisJid = "";
			
			public GetListCallBack (String cisJid){
				super();
				this.cisJid = cisJid;
			}
			 
			public void receiveResult(boolean result){fail("should have received a Communy obj");}
			public void receiveResult(int result) {fail("should have received a Communy obj");}
			public void receiveResult(String result){fail("should have received a Communy obj");}

			public void receiveResult(CommunityMethods communityResultObject) {
				if(communityResultObject == null){
					fail("Communy obj is null");
					return;
				}
				else{
					List<Participant> l = communityResultObject.getWhoResponse().getParticipant();
					int[] memberCheck = {0,0,0};
					
					Iterator<Participant> it = l.iterator();
					
					while(it.hasNext()){
						Participant element = it.next();
						if(element.getJid().equals(MEMBER_JID_1) && element.getRole().toString().equalsIgnoreCase(MEMBER_ROLE_1))
							memberCheck[0] = 1;
						if(element.getJid().equals(MEMBER_JID_2) && element.getRole().toString().equalsIgnoreCase(MEMBER_ROLE_2))
							memberCheck[1] = 1;	
						if(element.getJid().equals(MEMBER_JID_3) && element.getRole().toString().equalsIgnoreCase(MEMBER_ROLE_3))
							memberCheck[2] = 1;	

				     }
					
					// check if it found all matching CISs
					 for(int i=0;i<memberCheck.length;i++){
						 assertEquals(memberCheck[i], 1);
					 }
				}
				
				
				// CLEANING UP
				cisManagerUnderTestInterface.deleteCis(this.cisJid);
				
			}


		}
		
		// end of callback
		
		Iciss.getListOfMembers(new Requestor(testCisManagerId)
				 ,new GetListCallBack(cisJid));

	
	
	}
	//@Ignore
	@Test
	public void getInfoWithCallback() throws InterruptedException, ExecutionException {

		System.out.println("get info with callback");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned IcissOwned =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
		ICis icssRemote = IcissOwned;
				
		
		// callback that will do the real test

		 class GetInfoCallBack implements ICisManagerCallback{
			 
			 ICisOwned IcissOwned;
				
			public GetInfoCallBack(ICisOwned IcissOwned){
					this.IcissOwned = IcissOwned;
			}
			 
			public void receiveResult(boolean result){fail("should have received a Communy obj");}
			public void receiveResult(int result) {fail("should have received a Communy obj");}
			public void receiveResult(String result){fail("should have received a Communy obj");}

			public void receiveResult(CommunityMethods communityResultObject) {
				if(communityResultObject == null || communityResultObject.getGetInfoResponse() == null ||
						communityResultObject.getGetInfoResponse().getCommunity() == null){
					fail("Communy obj is null");
					return;
				}
				else{

					Community c = communityResultObject.getGetInfoResponse().getCommunity();
					// check vs input on create
					assertEquals(c.getCommunityName(), TEST_CIS_NAME_1);
					assertEquals(c.getCommunityType(), TEST_CIS_TYPW);
					// TODO: add criteria test
					// check between non-callback interface
					assertEquals(c.getCommunityName(), IcissOwned.getName());
					assertEquals(c.getCommunityJid(), IcissOwned.getCisId());
					
					assertEquals(c.getParticipant().size(),1);
					assertEquals(c.getParticipant().get(0).getJid(),CIS_MANAGER_CSS_ID);
					assertEquals(c.getParticipant().get(0).getRole(), ParticipantRole.OWNER);
					
				}
				
				
				// CLEANING UP
				cisManagerUnderTestInterface.deleteCis(this.IcissOwned.getCisId());
				
			}
		}		
		// end of callback
		// call and wait for callback
		 Requestor req = new Requestor(testCisManagerId);
		 icssRemote.getInfo(req,new GetInfoCallBack(IcissOwned));
	
	}
	//@Ignore
	@Test
	public void setInfoWithCallback() throws InterruptedException, ExecutionException {

		System.out.println("testing set info with callback");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned IcissOwned =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW ,null,"")).get();
		ICis icssRemote = IcissOwned;
				
		
		// callback that will do the real test

		 class SetInfoCallBack implements ICisManagerCallback{
			 
			 ICisOwned IcissOwned;
				
			public SetInfoCallBack(ICisOwned IcissOwned){
					this.IcissOwned = IcissOwned;
			}
			 
			public void receiveResult(boolean result){fail("should have received a Communy obj");}
			public void receiveResult(int result) {fail("should have received a Communy obj");}
			public void receiveResult(String result){fail("should have received a Communy obj");}

			public void receiveResult(CommunityMethods result) {
				if(result == null || result.getSetInfoResponse() == null || result.getSetInfoResponse().getCommunity() == null){
					fail("Communy obj is null");
					return;
				}
				else{
					
					Community communityResultObject = result.getSetInfoResponse().getCommunity();

					assertTrue(result.getSetInfoResponse().isResult().booleanValue());
				
					// check vs input on create
					assertEquals(communityResultObject.getCommunityName(), TEST_CIS_NAME_1);
					assertEquals(communityResultObject.getCommunityType(), TEST_CIS_TYPE2);
					//assertEquals(communityResultObject.getMembershipMode().intValue(), TEST_CIS_MODE); TODO: add criteria test
					assertEquals(communityResultObject.getDescription(), TEST_CIS_DESC);
					// check between non-callback interface
					assertEquals(communityResultObject.getCommunityName(), IcissOwned.getName());
					assertEquals(communityResultObject.getCommunityJid(), IcissOwned.getCisId());
					assertEquals(communityResultObject.getDescription(), IcissOwned.getDescription());
				}
				
				
				// CLEANING UP
				cisManagerUnderTestInterface.deleteCis(IcissOwned.getCisId());
				
			}
		}		
		// end of callback
		// call and wait for callback
		 Community c = new Community();
		 c.setCommunityType(TEST_CIS_TYPE2);
		 c.setDescription(TEST_CIS_DESC);
		 
		 
		 icssRemote.setInfo(c,new SetInfoCallBack(IcissOwned));
	
	}


	@Test
	public void checkCriteria() throws InterruptedException, ExecutionException {

		System.out.println("testing check criteria");
		
		cisManagerUnderTest = new CisManager();
		this.setMockingOnCISManager(cisManagerUnderTest);
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned IcissOwned =  (cisManagerUnderTestInterface.createCis(
				TEST_CIS_NAME_1, TEST_CIS_TYPW , null,"")).get();
		
		MembershipCriteria m = new MembershipCriteria();
		Rule r = new Rule();
		r.setOperation("equals");
		ArrayList<String> a = new ArrayList<String>();
		a.add("Brazil");
		r.setValues(a);
		m.setRule(r);
		
		assertTrue(IcissOwned.addCriteria("location", m));		

		
		m = new MembershipCriteria();
		r = new Rule();
		r.setOperation("differentFrom");
		a = new ArrayList<String>();
		a.add("married");
		r.setValues(a);
		m.setRule(r);
		assertTrue(IcissOwned.addCriteria("status", m));		

		
		//setting the user qualification
		HashMap<String,String> q1 = new HashMap<String,String>();
		q1.put("status","married");
		q1.put("music","rock");
		q1.put("hair","blond");
		q1.put("location","Brazil");
		assertFalse(IcissOwned.checkQualification(q1));
		

		HashMap<String,String> q2 = new HashMap<String,String>();
		q2.put("location","Brazil");
		assertFalse(IcissOwned.checkQualification(q2));

		HashMap<String,String> q3 = new HashMap<String,String>();
		q3.put("status","divorced");
		q3.put("music","rock");
		q3.put("hair","blond");
		q3.put("location","Brazil");		
		assertTrue(IcissOwned.checkQualification(q3));
	
	}
	
	private void setMockingOnCISManager(CisManager cisManagerUnderTest){
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setEventMgr(mockEventMgr); cisManagerUnderTest.setInternalCtxBroker(mockContextBroker);
		cisManagerUnderTest.setNegotiator(mockNegotiation);cisManagerUnderTest.setiActivityFeedManager(mockActivityFeedManager);
		cisManagerUnderTest.setPrivacyDataManager(mockIPrivacyDataManager);
		
		cisManagerUnderTest.init();
		
	}
	

	
}
