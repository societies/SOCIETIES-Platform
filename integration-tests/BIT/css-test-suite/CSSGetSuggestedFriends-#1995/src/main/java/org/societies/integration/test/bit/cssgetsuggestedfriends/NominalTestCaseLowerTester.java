package org.societies.integration.test.bit.cssgetsuggestedfriends;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
		LOG.info("[#1995] Initialization");
		LOG.info("[#1995] Prerequisite: The PLAN file is executed");
		LOG.info("[#1995] Prerequisite: The CSS has an identity");
	}

	/**
	 * This method is called before every @Test methods.
	 * 
	 */
	@Before
	public void setUp() {
		LOG.info("[#1995] NominalTestCaseLowerTester::setUp");

		Future<CssInterfaceResult> interfaceResult = null;
		//CssRecord profile = null;
		
		profile = createCSSRecord();
		LOG.info("[#1995] CSS Record: profile:" +profile);

			// -- Create the CSS Record
			LOG.info("[#1994] Preamble: Create the CSS Record");
	}
			private CssRecord createCSSRecord() {
				
				
				LOG.info("[#1995] createCSSRecord: Create the CSS Record/////////////////////");
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
		LOG.info("[#1995] tearDown");
	}


	/**
	 * 
	 * 
	 */
	@Test
	public void bodyCreateNode() {
		LOG.info("[#1995] create CSS Cloud Node");
		String Name = null;
		Name ="Liam";
		String compareName;
		LOG.info("[#1995] ######################## .................:" +Name);
		LOG.info("[#1995] ##########@@@@@@@@@@@@@@ .................:" +profile.getForeName());
		
		assertTrue(Name.equals(profile.getForeName()));
		
		
		interfaceResult = TestCase1995.cssLocalManager.registerCSSNode(profile);
		assertNotNull(interfaceResult);
		
		try {
			cssDetails = TestCase1995.cssRegistry.getCssRecord();
			LOG.info("[#1995] &&&&&&&&&&&&&& Name of CSS is .................:" +cssDetails.getName());
		} catch (CssRegistrationException e) {
			LOG.info("[#1995] CssRegistrationException - Could not get the CSS Record from the CSS Registry");
			e.printStackTrace();
		}
		
		
		assertNotNull(cssDetails);
		//cssDetails.getName();
		
		try {
			Name = TestCase1995.cssRegistry.getCssRecord().getName();
			LOG.info("[#1995] &&&&&&&&&&&&&& Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1995] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		LOG.info("[#1995] ==================== .................:" +compareName);
		//assertTrue(Name.equals(cssDetails.getForeName())); 
		assertTrue(compareName.equalsIgnoreCase(profile.getName()));
		assertTrue(compareName.equalsIgnoreCase(Name));
		LOG.info("[#1995] create CSS Cloud Node END............finally....."); 
	}
	
	@Ignore
	public void bodyDeleteNode() {
		LOG.info("[#1995] Delete CSS Cloud Node");
		String Name = null;
		Name ="liam";
		String compareName;
		assertTrue(null != Name);
		
		//interfaceResult = TestCase755.cssLocalManager.registerCSSNode(profile);
		//assertNotNull(interfaceResult);
		//assertTrue(null != interfaceResult);
		try {
			cssDetails = TestCase1995.cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			LOG.info("[#1995] CssRegistrationException - Could not add node to the CSS Registry");
			e.printStackTrace();
		}
		
		assertTrue(null != cssDetails);
		
		
		try {
			Name = TestCase1995.cssRegistry.getCssRecord().getName();
			interfaceResult = TestCase1995.cssLocalManager.getCssRecord();
			LOG.info("[#1995] deleteNode Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1995] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		assertTrue(Name.equals(compareName));
				
		//cssDetails = null;
		TestCase1995.cssLocalManager.unregisterCSSNode(profile);
		try {
			cssDetails = TestCase1995.cssRegistry.getCssRecord();
			
		} catch (CssRegistrationException e) {
			LOG.info("[#1995] CssRegistrationException - Could not delete node from the CSS Registry");
			e.printStackTrace();
		}
		//assertNull(cssDetails);
		LOG.info("[#1995] CSS Details:" +cssDetails.getName());
		LOG.info("[#1995] Delete CSS Cloud Node END.................");
		TestCase1995.cssLocalManager.getSuggestedFriends(null);
		
	}
	
	@Test
	public void getsuggestedfriends() {
		LOG.info("[#1995] Get Filtered friends");
		String Name = null;
		Name ="liam";
		String compareName;
		assertTrue(null != Name);
		
		LOG.info("[#1995] createCssAdvertisementRecord: Create the Css Advertisement Records/////////////////////");			

		CssAdvertisementRecord AdRecord = new CssAdvertisementRecord();
		AdRecord.setId(TEST_IDENTITY);
		AdRecord.setName(TEST_NAME);
		AdRecord.setUri(TEST_SOCIAL_URI);
		
		CssAdvertisementRecord AdRecord1 = new CssAdvertisementRecord();
		AdRecord1.setId("john.societies.local");
		AdRecord1.setName("John CSS");
		AdRecord1.setUri("//john");
		
		CssAdvertisementRecord AdRecord2 = new CssAdvertisementRecord();
		AdRecord2.setId("liam.societies.local");
		AdRecord2.setName("Liam CSS");
		AdRecord2.setUri("//liam");
		
		TestCase1995.cssLocalManager.addAdvertisementRecord(AdRecord);
		TestCase1995.cssLocalManager.addAdvertisementRecord(AdRecord1);
		TestCase1995.cssLocalManager.addAdvertisementRecord(AdRecord2);
		
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity("jane.societies.local");
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		//TestCase1994.cssLocalManager.acceptCssFriendRequest(pendingFR);
		try {
			TestCase1995.cssRegistry.updateCssFriendRequestRecord(pendingFR);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IIdentity cssId = null;;
		try {
			cssId = TestCase1995.commManager.getIdManager().fromJid("john.societies.local");
			LOG.info("[#1995] get filtered friends cssId................." +cssId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CssRequest pendingFR1 = new CssRequest();
		pendingFR.setCssIdentity("john.societies.local");
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		TestCase1995.cssLocalManager.handleInternalFriendRequest(cssId, CssRequestStatusType.ACCEPTED);
		
		
		
		
		IIdentity cssId1 = null;;
		try {
			cssId1 = TestCase1995.commManager.getIdManager().fromJid("liam.societies.local");
			LOG.info("[#1994] get filtered friends cssId................." +cssId1);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CssRequest pendingFR2 = new CssRequest();
		pendingFR.setCssIdentity("liam.societies.local");
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		TestCase1995.cssLocalManager.handleInternalFriendRequest(cssId1, CssRequestStatusType.ACCEPTED);
		
		List<String> friendList = new ArrayList<String>();
		List<CssAdvertisementRecord> friendAdList = new ArrayList<CssAdvertisementRecord>();
		
		
		try {
			friendList = TestCase1995.cssRegistry.getCssFriends();
			LOG.info("[#1995] friendList size:" +friendList.size());
		} catch (CssRegistrationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		assertNotNull(friendList);
		assertTrue(friendList.size() == 3);
				
		//LOG.info("[#1994] CSS Details:" +cssDetails.getName());
		
		
		//Add in different filter values to return individual SN and one for ALL and one for none
		int facebook =		0x0000000001;
		
		
		
		FriendFilter filter = new FriendFilter();
		filter.setFilterFlag(facebook);
		HashMap<CssAdvertisementRecord,Integer> snsSuggestedFriends = null;
		Future<HashMap<CssAdvertisementRecord, Integer>> asynchSnsSuggestedFriends = TestCase1995.cssLocalManager.getSuggestedFriendsDetails(filter); //suggestedFriends();
		try {
			snsSuggestedFriends = asynchSnsSuggestedFriends.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LOG.info("[#1995] get snsSuggested friends size is ................." +snsSuggestedFriends.size());
		LOG.info("[#1995] get snsSuggested friends contains ................." +snsSuggestedFriends);
		assertNotNull(snsSuggestedFriends);
		assertTrue(snsSuggestedFriends.size() == 3);
		
		for(Entry<CssAdvertisementRecord, Integer> entry : snsSuggestedFriends.entrySet()){
			friendList.add(entry.getKey().getId());
		}
		
		assertTrue(friendList.contains(AdRecord.getId())); 
		assertTrue(friendList.contains(AdRecord1.getId())); 
		assertTrue(friendList.contains(AdRecord2.getId())); 
		
		
		LOG.info("[#1995] get Suggested friends END.................");
		
	}

}
