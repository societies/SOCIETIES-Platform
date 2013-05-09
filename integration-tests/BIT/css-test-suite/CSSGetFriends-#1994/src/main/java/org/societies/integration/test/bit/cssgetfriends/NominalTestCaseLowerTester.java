package org.societies.integration.test.bit.cssgetfriends;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.integration.test.IntegrationTestUtils;
//import org.societies.integration.test.bit.cssrecordtocontext.TestCase1992;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.css.FriendFilter;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.css.CSSManagerEnums;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.css.ICSSInternalManager;

public class NominalTestCaseLowerTester {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);
	
	public IntegrationTestUtils integrationTestUtils;
	
	CssRecord profile = null;
	CssRecord cssDetails = null;
	Future<CssInterfaceResult> interfaceResult = null;
	/**
	 * Test case number
	 */
	public static int testCaseNumber;
	public static final String TEST_IDENTITY_1 = "node55";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "cloud";
	public static final String TEST_INACTIVE_DATE = "20120202";
	public static final String TEST_REGISTERED_DATE = "2012101";
	public static final int TEST_UPTIME = 7799;
	public static final String TEST_EMAIL = "Liam@tssg.org";
	public static final String TEST_FORENAME = "Liam";
	public static final String TEST_HOME_LOCATION = "The Hearth";
	public static final String TEST_IDENTITY_NAME = "Id Name";
	public static final String TEST_IM_ID = "Liam.tssg.org";
	public static final String TEST_NAME = "Cloud CSS";
	public static final String TEST_PASSWORD = "cloudpass";
	public static final String TEST_SOCIAL_URI = "Liam@fb.com";
	
	


	public NominalTestCaseLowerTester() {
		integrationTestUtils = new IntegrationTestUtils();
	}
	
	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 * 
	 */
	@BeforeClass
	public static void initialization() {
		LOG.info("[#1994] Initialization");
		LOG.info("[#1994] Prerequisite: The PLAN file is executed");
		LOG.info("[#1994] Prerequisite: The CSS has an identity");
	}

	/**
	 * This method is called before every @Test methods.
	 * 
	 */
	@Before
	public void setUp() {
		LOG.info("[#1994] NominalTestCaseLowerTester::setUp");

		Future<CssInterfaceResult> interfaceResult = null;
		//CssRecord profile = null;
		
		profile = createCSSRecord();
		LOG.info("[#1994] CSS Record: profile:" +profile);

			// -- Create the CSS Record
			LOG.info("[#1994] Preamble: Create the CSS Record");
	}
			private CssRecord createCSSRecord() {
				
				
				LOG.info("[#1994] createCSSRecord: Create the CSS Record/////////////////////");
		    	CssNode cssNode_1;

				cssNode_1 = new CssNode();
				cssNode_1.setIdentity(TEST_IDENTITY_1);
				cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
				cssNode_1.setType(CSSManagerEnums.nodeType.Cloud.ordinal());				

				CssRecord cssProfile = new CssRecord();
				cssProfile.getCssNodes().add(cssNode_1);
				
				cssProfile.setCssIdentity(TEST_IDENTITY);
				cssProfile.setEmailID(TEST_EMAIL);
				cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
				cssProfile.setForeName(TEST_FORENAME);
				cssProfile.setHomeLocation(TEST_HOME_LOCATION);
				cssProfile.setName(TEST_NAME);
				cssProfile.setSex(CSSManagerEnums.genderType.Male.ordinal());
				
				return cssProfile;
			
			
				
				
			}
			
			
	

	/**
	 * This method is called after every @Test methods
	 * 
	 */
	@After
	public void tearDown() {
		LOG.info("[#1994] tearDown");
	}


	/**
	 * 
	 * 
	 */
	@Test
	public void bodyCreateNode() {
		LOG.info("[#1994] create CSS Cloud Node");
		String Name = null;
		Name ="Liam";
		String compareName;
		LOG.info("[#1994] ######################## .................:" +Name);
		LOG.info("[#1994] ##########@@@@@@@@@@@@@@ .................:" +profile.getForeName());
		
		assertTrue(Name.equals(profile.getForeName()));
		
		
		interfaceResult = TestCase1994.cssLocalManager.registerCSSNode(profile);
		assertNotNull(interfaceResult);
		
		try {
			cssDetails = TestCase1994.cssRegistry.getCssRecord();
			LOG.info("[#1994] &&&&&&&&&&&&&& Name of CSS is .................:" +cssDetails.getName());
		} catch (CssRegistrationException e) {
			LOG.info("[#1994] CssRegistrationException - Could not get the CSS Record from the CSS Registry");
			e.printStackTrace();
		}
		
		
		assertNotNull(cssDetails);
		//cssDetails.getName();
		
		try {
			Name = TestCase1994.cssRegistry.getCssRecord().getName();
			LOG.info("[#1994] &&&&&&&&&&&&&& Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1994] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		LOG.info("[#1994] ==================== .................:" +compareName);
		//assertTrue(Name.equals(cssDetails.getForeName())); 
		assertTrue(compareName.equalsIgnoreCase(profile.getName()));
		assertTrue(compareName.equalsIgnoreCase(Name));
		LOG.info("[#1994] create CSS Cloud Node END............finally....."); 
	}
	
	
	@Test
	public void getfriends() {
		LOG.info("[#1994] Get friends");
		String Name = null;
		Name ="liam";
		String compareName;
		assertTrue(null != Name);
		
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity("jane.societies.local");
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		//TestCase1994.cssLocalManager.acceptCssFriendRequest(pendingFR);
		try {
			TestCase1994.cssRegistry.updateCssFriendRequestRecord(pendingFR);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IIdentity cssId = null;;
		try {
			cssId = TestCase1994.commManager.getIdManager().fromJid("john.societies.local");
			LOG.info("[#1994] get friends cssId................." +cssId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CssRequest pendingFR1 = new CssRequest();
		pendingFR.setCssIdentity("john.societies.local");
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		TestCase1994.cssLocalManager.handleInternalFriendRequest(cssId, CssRequestStatusType.ACCEPTED);
		
		
		
		
		IIdentity cssId1 = null;;
		try {
			cssId1 = TestCase1994.commManager.getIdManager().fromJid("liam.societies.local");
			LOG.info("[#1994] get filtered friends cssId................." +cssId1);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CssRequest pendingFR2 = new CssRequest();
		pendingFR.setCssIdentity("liam.societies.local");
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		TestCase1994.cssLocalManager.handleInternalFriendRequest(cssId1, CssRequestStatusType.ACCEPTED);
		
		List<String> friendList = new ArrayList<String>();
				
		
		try {
			friendList = TestCase1994.cssRegistry.getCssFriends();
			LOG.info("[#1994] friendList size:" +friendList.size());
		} catch (CssRegistrationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		assertNotNull(friendList);
		assertTrue(friendList.size() == 3);
				
		//LOG.info("[#1994] CSS Details:" +cssDetails.getName());
		LOG.info("[#1994] get friends END.................");
		
	}

}
