package org.societies.integration.test.bit.cssgetadvertisements;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.integration.test.IntegrationTestUtils;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.css.CSSManagerEnums;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.css.mgmt.CssDirectoryRemoteClient;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

public class NominalTestCaseLowerTester {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);
	
	public IntegrationTestUtils integrationTestUtils;
	
	CssRecord profile = null;
	CssRecord cssDetails = null;
	Future<CssInterfaceResult> interfaceResult = null;
	CssAdvertisementRecord AdRecord;
	/**
	 * Test case number
	 */
	public static int testCaseNumber;
	public static final String TEST_IDENTITY_1 = "node55";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "jane.societies.local";
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
		LOG.info("[#1993] Initialization");
		LOG.info("[#1993] Prerequisite: The PLAN file is executed");
		LOG.info("[#1993] Prerequisite: The CSS has an identity");
	}

	/**
	 * This method is called before every @Test methods.
	 * 
	 */
	@Before
	public void setUp() {
		LOG.info("[#1993] NominalTestCaseLowerTester::setUp");

		Future<CssInterfaceResult> interfaceResult = null;
		//CssRecord profile = null;
		
		profile = createCSSRecord();
		
		AdRecord = createAdRecord();
		
		LOG.info("[#1993] CSS Record: profile:" +profile);

			// -- Create the CSS Record
			LOG.info("[#1993] Preamble: Create the CSS Record");
	}
			private CssRecord createCSSRecord() {
				
				
				LOG.info("[#1993] createCSSRecord: Create the CSS Record/////////////////////");
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
			
				private CssAdvertisementRecord createAdRecord() {
				
				
				LOG.info("[#1993] createCssAdvertisementRecord: Create the Css Advertisement Record/////////////////////");			

				CssAdvertisementRecord AdRecord = new CssAdvertisementRecord();
				AdRecord.setId(TEST_IDENTITY);
				AdRecord.setName(TEST_NAME);
				AdRecord.setUri(TEST_SOCIAL_URI);
				
				return AdRecord;
			
			
			}
	

	/**
	 * This method is called after every @Test methods
	 * 
	 */
	@After
	public void tearDown() {
		LOG.info("[#1993] tearDown");
	}


	/**
	 * 
	 * 
	 */
	@Test
	public void bodyCreateNode() {
		LOG.info("[#1993] create CSS Cloud Node");
		String Name = null;
		Name ="Liam";
		String compareName;
		LOG.info("[#1993] ######################## .................:" +Name);
		LOG.info("[#1993] ##########@@@@@@@@@@@@@@ .................:" +profile.getForeName());
		
		assertTrue(Name.equals(profile.getForeName()));
		
		
		interfaceResult = TestCase1993.cssLocalManager.registerCSSNode(profile);
		assertNotNull(interfaceResult);
		
		try {
			cssDetails = TestCase1993.cssRegistry.getCssRecord();
			LOG.info("[#1993] &&&&&&&&&&&&&& Name of CSS is .................:" +cssDetails.getName());
		} catch (CssRegistrationException e) {
			LOG.info("[#1993] CssRegistrationException - Could not get the CSS Record from the CSS Registry");
			e.printStackTrace();
		}
		
		
		assertNotNull(cssDetails);
		//cssDetails.getName();
		
		try {
			Name = TestCase1993.cssRegistry.getCssRecord().getName();
			LOG.info("[#1993] &&&&&&&&&&&&&& Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1993] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		LOG.info("[#1993] ==================== .................:" +compareName);
		//assertTrue(Name.equals(cssDetails.getForeName())); 
		assertTrue(compareName.equalsIgnoreCase(profile.getName()));
		assertTrue(compareName.equalsIgnoreCase(Name));
		LOG.info("[#1993] create CSS Cloud Node END............finally....."); 
	}
	
	@Ignore
	public void bodyDeleteNode() {
		LOG.info("[#1993] add advertisement");
		String Name = null;
		Name ="liam";
		String compareName;
		assertTrue(null != Name);
		
		//interfaceResult = TestCase755.cssLocalManager.registerCSSNode(profile);
		//assertNotNull(interfaceResult);
		//assertTrue(null != interfaceResult);
		try {
			cssDetails = TestCase1993.cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			LOG.info("[#1993] CssRegistrationException - Could not add node to the CSS Registry");
			e.printStackTrace();
		}
		
		assertTrue(null != cssDetails);
		
		
		try {
			Name = TestCase1993.cssRegistry.getCssRecord().getName();
			interfaceResult = TestCase1993.cssLocalManager.getCssRecord();
			LOG.info("[#1993] deleteNode Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1993] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		assertTrue(Name.equals(compareName));
				
		//cssDetails = null;
		TestCase1993.cssLocalManager.unregisterCSSNode(profile);
		try {
			cssDetails = TestCase1993.cssRegistry.getCssRecord();
			
		} catch (CssRegistrationException e) {
			LOG.info("[#1993] CssRegistrationException - Could not delete node from the CSS Registry");
			e.printStackTrace();
		}
		//assertNull(cssDetails);
		LOG.info("[#1993] CSS Details:" +cssDetails.getName());
		LOG.info("[#1993] Delete CSS Cloud Node END.................");
		
		
	}
	
	@Test
	public void getadvertisements() {
		LOG.info("[#1993] add advertisement .................");
		
		String adID = null;
		String adName = null;
		String adUri = null;
		
		assertNotNull(AdRecord);
		TestCase1993.cssDirectoryRemote.addCssAdvertisementRecord(AdRecord);
		
		
		LOG.info("[#1993] createCssAdvertisementRecord: Create another Css Advertisement Record and add to directory");			

		CssAdvertisementRecord AdRecord1 = new CssAdvertisementRecord();
		AdRecord1.setId("john.societies.local");
		AdRecord1.setName("John CSS");
		AdRecord1.setUri("//john");
		
		
		assertNotNull(AdRecord1);
		TestCase1993.cssDirectoryRemote.addCssAdvertisementRecord(AdRecord1);
		
		LOG.info("[#1993] createCssAdvertisementRecord: Create another Css Advertisement Record and add to directory");			

		CssAdvertisementRecord AdRecord2 = new CssAdvertisementRecord();
		AdRecord2.setId("liam.societies.local");
		AdRecord2.setName("Liam CSS");
		AdRecord2.setUri("//Liam");
		
		assertNotNull(AdRecord2);
		TestCase1993.cssDirectoryRemote.addCssAdvertisementRecord(AdRecord2);
		
		
		CssDirectoryRemoteClient cssDirCallback = new CssDirectoryRemoteClient();
		
		TestCase1993.cssDirectoryRemote.findAllCssAdvertisementRecords(cssDirCallback);
		List<CssAdvertisementRecord> returnlist = cssDirCallback.getResultList();
		List<String> returnlistIDs = new ArrayList <String>();
		
		LOG.info("[#1993] Get Advertisements returnlist size................." +returnlist.size());
		
		if (returnlist.size() > 0){
			for (int i = 0; i < returnlist.size(); i++ ){
				adID = returnlist.get(i).getId();
				assertNotNull(adID);
				adName = returnlist.get(i).getName();
				assertNotNull(adName);
				adUri = returnlist.get(i).getUri();
				assertNotNull(adUri);
				returnlistIDs.add(returnlist.get(i).getId());
			}
			
			LOG.info("[#1993] Get Advertisements adID................." +adID);
			LOG.info("[#1993] Get Advertisements adName................." +adName);
			LOG.info("[#1993] Get Advertisements adUri................." +adUri);
			
			assertTrue(adID.equalsIgnoreCase(AdRecord2.getId()));
			assertTrue(adName.equalsIgnoreCase(AdRecord2.getName()));
			assertTrue(adUri.equalsIgnoreCase(AdRecord2.getUri()));
			
			assertTrue(returnlistIDs.contains(AdRecord2.getId()));
			assertTrue(returnlistIDs.contains(AdRecord1.getId()));
			assertTrue(returnlistIDs.contains(AdRecord.getId()));
			
		}
		
		LOG.info("[#1993] Get Advertisements END.................");
	}

}
