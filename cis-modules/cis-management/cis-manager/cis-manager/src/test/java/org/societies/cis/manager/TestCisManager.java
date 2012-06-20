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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivity;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.cis.management.ICis;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Participant;
import org.societies.identity.NetworkNodeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;

/**
 * Junit and Mockito Test for CIS
 *
 * @author Thomas Vilarinho (Sintef)
 *
 */
//@RunWith(PowerMockRunner.class)
@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)  
@PrepareForTest( { ActivityFeed.class })
@ContextConfiguration(locations = { "../../../../CisManagerTest-context.xml" })
public class TestCisManager extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory
			.getLogger(TestCisManager.class);
	//@Autowired
	private CisManager cisManagerUnderTest;
	private ICisManager cisManagerUnderTestInterface;
	@Autowired
	private SessionFactory sessionFactory;
	private ICISCommunicationMgrFactory mockCcmFactory;
	private ICommManager mockCSSendpoint;
	private ICommManager mockCISendpoint1;
	private ICommManager mockCISendpoint2;
	private ICommManager mockCISendpoint3;
	
	private IServiceDiscoveryRemote mockIServDiscRemote;
	private IServiceControlRemote mockIServCtrlRemote;
	
	public static final String CIS_MANAGER_CSS_ID = "testXcmanager.societies.local";
	
	//public static final String TEST_CSSID = "juca@societies.local";
	public static final String TEST_CSS_PWD = "password";
	public static final String TEST_CIS_NAME_1 = "Flamengo Futebol Clube";
	public static final String TEST_CIS_TYPW = "futebol";
	public static final int TEST_CIS_MODE = 0;
	
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
	
	
	////////////////
	// mocking a remote CIS Manager and CISs
	
	private CisManager remoteCisManagerUnderTest;
	private ICISCommunicationMgrFactory mockRemoteCcmFactory;
	private ICommManager mockRemoteCSSendpoint;
	
	IIdentityManager mockRemoteIICisManagerIdManager;
	INetworkNode remoteCisManagerId;
	public static final String REMOTE_CIS_MANAGER_ID = "testXcmanager1.societies.local";
	
	public static final String REMOTER_CISID_1 = "gama.societies.local";
	INetworkNode remoteCisId_1;
	ICommManager mockRemoteCISendpoint1;
	IIdentityManager mockRemoteIICisId_1;
	
	// mockedDB
	SessionFactory mockedSessionFactory;
	org.hibernate.classic.Session mockedSession;
	Transaction mockedTransaction;
	org.hibernate.Criteria mockedCriteria;
	
	
	void mockingDB(){
		mockedSessionFactory = mock(org.hibernate.SessionFactory.class);
		mockedSession = mock(org.hibernate.classic.Session.class);
		mockedTransaction = mock(org.hibernate.Transaction.class);
		
		mockedCriteria = mock(org.hibernate.Criteria.class);
		
		when(mockedSessionFactory.openSession()).thenReturn(mockedSession);
		when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
		when(mockedSession.createCriteria(any(Class.class))).thenReturn(mockedCriteria);
		doNothing().when(mockedSession).close();
		doNothing().when(mockedSession).save(anyObject());
		doNothing().when(mockedSession).delete(anyObject());
		doNothing().when(mockedSession).update(anyObject());
		doNothing().when(mockedTransaction).commit();
		
		doNothing().when(mockedCriteria).setResultTransformer(any(org.hibernate.transform.ResultTransformer.class));
		
		List<Cis> l1 = new ArrayList<Cis>();
		List<CisSubscribedImp> l2 = new ArrayList<CisSubscribedImp>();
		
		when(mockedCriteria.list()).thenReturn(l1,l2);
		
				
	}
	
	
	
	void setUpRemoteNode() throws Exception{
		remoteCisManagerUnderTest = new CisManager();

		
		mockRemoteCcmFactory =  mock(ICISCommunicationMgrFactory.class);
		mockRemoteCSSendpoint = mock (ICommManager.class);

		mockRemoteIICisManagerIdManager = mock (IIdentityManager.class);
		remoteCisManagerId = new NetworkNodeImpl(REMOTE_CIS_MANAGER_ID);
		
		// mocking the CISManager
		when(mockRemoteCSSendpoint.getIdManager()).thenReturn(mockRemoteIICisManagerIdManager);
		when(mockRemoteIICisManagerIdManager.getThisNetworkNode()).thenReturn(remoteCisManagerId);
		doNothing().when(mockRemoteCSSendpoint).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));
		
		// others interpretation of the remote CIS Manager
		when(mockIICisId_1.fromJid(REMOTE_CIS_MANAGER_ID)).thenReturn(remoteCisManagerId);
		when(mockIICisId_2.fromJid(REMOTE_CIS_MANAGER_ID)).thenReturn(remoteCisManagerId);
		when(mockIICisId_3.fromJid(REMOTE_CIS_MANAGER_ID)).thenReturn(remoteCisManagerId);
		
		// mocking a remote CIS
		
		// mocking the IcomManagers
		mockRemoteCISendpoint1 = mock (ICommManager.class);
		mockRemoteIICisId_1 = mock (IIdentityManager.class);
		
		// creating a NetworkNordImpl for each Identity Manager		
		remoteCisId_1 = new NetworkNodeImpl(REMOTER_CISID_1);
		when(mockRemoteCISendpoint1.getIdManager()).thenReturn(mockRemoteIICisId_1);		
		when(mockRemoteIICisId_1.getThisNetworkNode()).thenReturn(remoteCisId_1);		
		
		//mocking methods of CIS Manager
		when(mockRemoteCISendpoint1.UnRegisterCommManager()).thenReturn(true);
		doNothing().when(mockRemoteCISendpoint1).sendMessage(any(org.societies.api.comm.xmpp.datatypes.Stanza.class), any(Object.class)); // for the delete
		doNothing().when(mockRemoteCISendpoint1).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));

		when(mockRemoteIICisId_1.fromJid(REMOTER_CISID_1)).thenReturn(remoteCisId_1);// for the delete		
		when(mockRemoteCcmFactory.getNewCommManager()).thenReturn(mockRemoteCISendpoint1);
			
		// mocking sendIQGet from main CSS endpoint
		
		Answer<String> sendIQGetAnser = new Answer<String>() {
			@Override 
			public String answer(InvocationOnMock invocation) {
		         Object[] args = invocation.getArguments();
		         Object mock = invocation.getMock();
		         
		         Stanza sta = (Stanza)args[0];
		         Object payload = args[1];
		         IIdentity oldFrom = sta.getFrom();
		         IIdentity oldTo = sta.getTo();
		         sta.setTo(oldFrom);
		         sta.setFrom(oldTo);
		         
		         try {
					remoteCisManagerUnderTest.getQuery(sta, payload);
				} catch (XMPPError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		         
		         
		         return "called with arguments: " + args;
		     }
		 };
		


			doAnswer(sendIQGetAnser).when(mockCSSendpoint).sendIQGet(
					any(org.societies.api.comm.xmpp.datatypes.Stanza.class),
					anyObject(), any(org.societies.api.comm.xmpp.interfaces.ICommCallback.class)
					);

		
	/*	
		ICisOwned LocalCISbutOnRemotePeer =  (targetCisManagerUnderTest.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_2, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		
		
		when(mockCISendpoint3.UnRegisterCommManager()).thenReturn(true);
		
		//testDelCSSId = new NetworkNodeImpl("delCss@societies.org");
		doNothing().when(mockCISendpoint1).sendMessage(any(org.societies.api.comm.xmpp.datatypes.Stanza.class), any(Object.class)); // for the delete	
*/
			remoteCisManagerUnderTest.setICommMgr(mockRemoteCSSendpoint);
			remoteCisManagerUnderTest.setCcmFactory(mockRemoteCcmFactory);
			remoteCisManagerUnderTest.setSessionFactory(sessionFactory);
			remoteCisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
			remoteCisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);
			remoteCisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
			remoteCisManagerUnderTest.init();
	
	}
	
	
	
	
	
	
	void setUpFactory() throws Exception {
		System.out.println("in setupFactory!");
		mockCcmFactory = mock(ICISCommunicationMgrFactory.class);
		mockIServDiscRemote = mock(IServiceDiscoveryRemote.class);
		mockIServCtrlRemote = mock(IServiceControlRemote.class);
		
		
		
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
		
		doNothing().when(mockICisDirRemote1).addCisAdvertisementRecord(any(org.societies.api.schema.cis.directory.CisAdvertisementRecord.class));
		//doNothing().when(mockICisDirRemote2).addCisAdvertisementRecord(any(org.societies.api.schema.cis.directory.CisAdvertisementRecord.class));
		//doNothing().when(mockICisDirRemote3).addCisAdvertisementRecord(any(org.societies.api.schema.cis.directory.CisAdvertisementRecord.class));
		
		
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
		
		
		
	}
	
	@Before
	public void setUp() throws Exception {
		// create mocked class
		System.out.println("in setup!");
		mockCSSendpoint = mock (ICommManager.class);

		mockIICisManagerId = mock (IIdentityManager.class);
		
		testCisManagerId = new NetworkNodeImpl(CIS_MANAGER_CSS_ID);

		
		// mocking the CISManager
		when(mockCSSendpoint.getIdManager()).thenReturn(mockIICisManagerId);
		when(mockIICisManagerId.getThisNetworkNode()).thenReturn(testCisManagerId);
		doNothing().when(mockCSSendpoint).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));
		
		// mocking the activity feed static methods
		//PowerMockito.mockStatic(ActivityFeed.class);
		//this.session = sessionFactory.openSession();
		System.out.println("in setup! cisManagerUnderTest.getSessionFactory(): "+sessionFactory);
		ActivityFeed.setStaticSessionFactory(sessionFactory);
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
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		assertEquals(CIS_MANAGER_CSS_ID, cisManagerUnderTest.cisManagerId.getJid());
		
		
	}
	
	
	///////////////////////////////////////////////////
	// Local Interface Testing
	//////////////////////////////////////////////////
	
	//@Ignore
	//@Rollback(true)
	@Test
	public void testCreateCIS() {
		
		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		Future<ICisOwned> testCIS = cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE);
		try {
			assertNotNull(testCIS.get());
			assertNotNull(testCIS.get().getCisId());
			assertEquals(testCIS.get().getName(), TEST_CIS_NAME_1);
			assertEquals(testCIS.get().getCisType(), TEST_CIS_TYPW);
			assertEquals(testCIS.get().getMembershipCriteria(), TEST_CIS_MODE);

			// CLEANING UP
			cisManagerUnderTest.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, testCIS.get().getCisId());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	// TODO: double check that the owner has been added as participant
		
		//this.deleteFromTables(new String[] { "org_societies_cis_manager_Cis"});		
	}
	
	
	//@Ignore
	//@Rollback(true)
	@Test
	public void testListCIS() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory);cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1); 
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote); 
		
		cisManagerUnderTest.init();
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned[] ciss = new ICisOwned [3]; 
		int[] cissCheck = {0,0,0};
		
		ciss[0] =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1+"aa", TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		ciss[1] = (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_2, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		ciss[2] = (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_3, TEST_CIS_TYPW , TEST_CIS_MODE)).get();

		List<ICisOwned> l = cisManagerUnderTestInterface.getListOfOwnedCis();
		Iterator<ICisOwned> it = l.iterator();
		 
		while(it.hasNext()){
			ICisOwned element = it.next();
			 assertEquals(element.getOwnerId(),CIS_MANAGER_CSS_ID);
			 for(int i=0;i<ciss.length;i++){
				 if(element.getName().equals(ciss[i].getName()) 
				&& 	element.getCisId().equals(ciss[i].getCisId())
				&& 	element.getCisType().equals(ciss[i].getCisType())
				&& 	(element.getMembershipCriteria() == ciss[i].getMembershipCriteria())		 
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
			 cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, ciss[i].getCisId());
		 }

	}

	//@Rollback(true)
	@Test
	public void testdeleteCIS() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		//LOG.info("testdeleteCIS, sessionFactory: "+sessionFactory.hashCode());
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		//LOG.info("testdeleteCIS, sessionFactory: "+sessionFactory.hashCode());
		
		ICisOwned[] ciss = new ICisOwned [2]; 
		String jidTobeDeleted = "";
		
		ciss[0] =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		ciss[1] = (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_2, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		
		LOG.info("cis 1 sessionfactory:"+((Cis)ciss[0]).getSessionFactory().hashCode());
		List<ICis> l = cisManagerUnderTestInterface.getCisList();
		LOG.info("cis 1 sessionfactory:"+((Cis)l.get(0)).getSessionFactory());
		Iterator<ICis> it = l.iterator();
		ICis element = it.next(); 
		jidTobeDeleted = element.getCisId();
		
		boolean presence = false;
		
		presence = cisManagerUnderTestInterface.deleteCis("", "", jidTobeDeleted);
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
			cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, element.getCisId());
	     }

	
	}
	
	//@Ignore
	//@Rollback
	@Test
	public void testAddMemberToOwnedCIS() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();

		try {
			assertEquals(true,Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1).get());
			assertEquals(true,Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2).get());
			assertEquals(false,Iciss.addMember(MEMBER_JID_3, INVALID_ROLE).get());
			// assertEquals(false,Iciss.addMember(INVALID_USER_JID, MEMBER_ROLE_3).get());  NOT USE OF TESTING THAT AS IDENTITY MANAGER HAS BEEN MOCKED
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	
		
		// CLEANING UP
		cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, Iciss.getCisId());

		
	}
	
	//@Ignore
	//@Rollback
	@Test
	public void testDeleteMemberToOwnedCIS() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		

		try {
			Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1);
			Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2);
			
			Iciss.removeMemberFromCIS(MEMBER_JID_1);
			
			int memberCheck = 0;
			
			Set<ICisParticipant> l = (Iciss.getMemberList()).get();
			Iterator<ICisParticipant> it = l.iterator();
			
			// search if member is still there
			while(it.hasNext()){
				ICisParticipant element = it.next();
				if(element.getMembersJid().equals(MEMBER_JID_1) )
					memberCheck = 1;
		     }
			
			// check if it found all matching CISs
				assertEquals(memberCheck, 0);
			
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	
		
		// CLEANING UP
		cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, Iciss.getCisId());

		
	}
	
	
	
	@Test
	public void listdMembersOnOwnedCIS() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
				
		try {
			Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1).get();
			Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2).get();
			Iciss.addMember(MEMBER_JID_3, MEMBER_ROLE_3).get();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int[] memberCheck = {0,0,0};
		
		Set<ICisParticipant> l = (Iciss.getMemberList()).get();
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
		 cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, Iciss.getCisId());
	}
	
	@Ignore
	@Test
	public void addActivity() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
	
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		
		
		
		IActivity iActivity = new org.societies.activity.model.Activity();
		iActivity.setActor("act");
		iActivity.setObject("obj");
		iActivity.setTarget("tgt");
		iActivity.setPublished((System.currentTimeMillis() -55) + "");
		iActivity.setVerb("verb");

		IActivity iActivity2 = new org.societies.activity.model.Activity();
		iActivity.setActor("act2");
		iActivity.setObject("obj2");
		iActivity.setTarget("tgt2");
		iActivity.setPublished((System.currentTimeMillis() -500) + "");
		iActivity.setVerb("verb2");

		
		Iciss.getActivityFeed().addCisActivity(iActivity);
		Iciss.getActivityFeed().addCisActivity(iActivity2);
		System.out.println((System.currentTimeMillis() -20000) + " " + System.currentTimeMillis());
		List <IActivity> l = Iciss.getActivityFeed().getActivities((System.currentTimeMillis() -20000) + " " + System.currentTimeMillis());
		
		
		int[] check = {0,0};
		
		Iterator<IActivity> it = l.iterator();
		
		while(it.hasNext()){
			IActivity element = it.next();
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
		 cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, Iciss.getCisId());
	}
	
	///////////////////////////////////////////////////
	// XMPP stanza Testing
	//////////////////////////////////////////////////

	@Ignore // DO NOT REMOVE THE IGNORE YET; checking how to test the incoming stanzas
	@Test
	public void testJoin() throws Exception {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		
		setUpRemoteNode();

		ICisOwned localInstOfRemoteCIS =  (remoteCisManagerUnderTest.createCis(REMOTE_CIS_MANAGER_ID, TEST_CSS_PWD,
				"name", TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		

		cisManagerUnderTest.joinRemoteCIS(REMOTER_CISID_1, new JoinCallBack());


		
		// TODO add real test

		
		// CLEANING UP
		cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, Iciss.getCisId());

		
	}	
	
	
	public class JoinCallBack implements ICisManagerCallback{
		
		//ICisManager cisClient;
		
		public JoinCallBack(){
			
		}

		public void receiveResult(Community communityResultObject) {
			 
			if(communityResultObject == null){
				LOG.info("null return on JoinCallBack");
				return;
			}
			else{
				LOG.info("good return on JoinCallBack");
				LOG.info("Result Status: joined CIS " + communityResultObject.getCommunityJid());

			}

			
			
		}
			

	}
	
	
	
	///////////////////////////////////////////////////
	// Local Interface with Callback Testing
	//////////////////////////////////////////////////
	
	@Test
	public void listdMembersOnOwnedCISwithCallback() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned Iciss =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
				
		try {
			Iciss.addMember(MEMBER_JID_1, MEMBER_ROLE_1).get();
			Iciss.addMember(MEMBER_JID_2, MEMBER_ROLE_2).get();
			Iciss.addMember(MEMBER_JID_3, MEMBER_ROLE_3).get();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// callback that will do the real test

		 class GetListCallBack implements ICisManagerCallback{
			public void receiveResult(boolean result){fail("should have received a Communy obj");}
			public void receiveResult(int result) {fail("should have received a Communy obj");}
			public void receiveResult(String result){fail("should have received a Communy obj");}

			public void receiveResult(Community communityResultObject) {
				if(communityResultObject == null){
					fail("Communy obj is null");
					return;
				}
				else{
					List<Participant> l = communityResultObject.getWho().getParticipant();
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
				cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, communityResultObject.getCommunityJid());
				
			}


		}
		
		// end of callback
		

		// call and wait for callback
		Iciss.getListOfMembers(new GetListCallBack());

	
	
	}
	
	@Test
	public void getInfoWithCallback() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned IcissOwned =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
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

			public void receiveResult(Community communityResultObject) {
				if(communityResultObject == null){
					fail("Communy obj is null");
					return;
				}
				else{

				
					// check vs input on create
					assertEquals(communityResultObject.getCommunityName(), TEST_CIS_NAME_1);
					assertEquals(communityResultObject.getCommunityType(), TEST_CIS_TYPW);
					assertEquals(communityResultObject.getMembershipMode().intValue(), TEST_CIS_MODE);
					// check between non-callback interface
					assertEquals(communityResultObject.getCommunityName(), IcissOwned.getName());
					assertEquals(communityResultObject.getCommunityJid(), IcissOwned.getCisId());
				}
				
				
				// CLEANING UP
				cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, communityResultObject.getCommunityJid());
				
			}
		}		
		// end of callback
		// call and wait for callback
		 icssRemote.getInfo(new GetInfoCallBack(IcissOwned));
	
	}
	
	@Test
	public void setInfoWithCallback() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager();
		cisManagerUnderTest.setICommMgr(mockCSSendpoint); cisManagerUnderTest.setCcmFactory(mockCcmFactory); cisManagerUnderTest.setSessionFactory(sessionFactory);cisManagerUnderTest.setiCisDirRemote(mockICisDirRemote1);
		cisManagerUnderTest.setiServDiscRemote(mockIServDiscRemote);cisManagerUnderTest.setiServCtrlRemote(mockIServCtrlRemote);
		cisManagerUnderTest.init();
		
		cisManagerUnderTestInterface = cisManagerUnderTest;
		ICisOwned IcissOwned =  (cisManagerUnderTestInterface.createCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
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

			public void receiveResult(Community communityResultObject) {
				if(communityResultObject == null){
					fail("Communy obj is null");
					return;
				}
				else{

					assertTrue(communityResultObject.getSetInfoResponse().isResult().booleanValue());
				
					// check vs input on create
					assertEquals(communityResultObject.getCommunityName(), TEST_CIS_NAME_1);
					assertEquals(communityResultObject.getCommunityType(), TEST_CIS_TYPE2);
					assertEquals(communityResultObject.getMembershipMode().intValue(), TEST_CIS_MODE);
					assertEquals(communityResultObject.getDescription(), TEST_CIS_DESC);
					// check between non-callback interface
					assertEquals(communityResultObject.getCommunityName(), IcissOwned.getName());
					assertEquals(communityResultObject.getCommunityJid(), IcissOwned.getCisId());
					assertEquals(communityResultObject.getDescription(), IcissOwned.getDescription());
				}
				
				
				// CLEANING UP
				cisManagerUnderTestInterface.deleteCis(CIS_MANAGER_CSS_ID, TEST_CSS_PWD, communityResultObject.getCommunityJid());
				
			}
		}		
		// end of callback
		// call and wait for callback
		 Community c = new Community();
		 c.setCommunityType(TEST_CIS_TYPE2);
		 c.setDescription(TEST_CIS_DESC);
		 
		 
		 icssRemote.setInfo(c,new SetInfoCallBack(IcissOwned));
	
	}

	
}
