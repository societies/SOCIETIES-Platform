package org.societies.integration.test.bit.createcloudnode;


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
		LOG.info("[#1867] Initialization");
		LOG.info("[#1867] Prerequisite: The PLAN file is executed");
		LOG.info("[#1867] Prerequisite: The CSS has an identity");
	}

	/**
	 * This method is called before every @Test methods.
	 * 
	 */
	@Before
	public void setUp() {
		LOG.info("[#1867] NominalTestCaseLowerTester::setUp");

		Future<CssInterfaceResult> interfaceResult = null;
		//CssRecord profile = null;
		
		profile = createCSSRecord();
		LOG.info("[#1867] CSS Record: profile:" +profile);

			// -- Create the CSS Record
			LOG.info("[#1867] Preamble: Create the CSS Record");
	}
			private CssRecord createCSSRecord() {
				
				
				LOG.info("[#1867] createCSSRecord: Create the CSS Record/////////////////////");
		    	CssNode cssNode_1;

				cssNode_1 = new CssNode();
				cssNode_1.setIdentity(TEST_IDENTITY_1);
				cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
				cssNode_1.setType(CSSManagerEnums.nodeType.Cloud.ordinal());

				//cssNode_2 = new CssNode();
				//cssNode_2.setIdentity(TEST_IDENTITY_2);
				//cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
				//cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
				

				CssRecord cssProfile = new CssRecord();
				cssProfile.getCssNodes().add(cssNode_1);
				//cssProfile.getCssNodes().add(cssNode_2);
				//cssProfile.getArchiveCSSNodes().add(cssNode_1);
				//cssProfile.getArchiveCSSNodes().add(cssNode_2);
				
				cssProfile.setCssIdentity(TEST_IDENTITY);
//				cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
//				cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
//				cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
//				cssProfile.setCssUpTime(TEST_UPTIME);
				cssProfile.setEmailID(TEST_EMAIL);
				cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
				cssProfile.setForeName(TEST_FORENAME);
				cssProfile.setHomeLocation(TEST_HOME_LOCATION);
//				cssProfile.setIdentityName(TEST_IDENTITY_NAME);
//				cssProfile.setImID(TEST_IM_ID);
				cssProfile.setName(TEST_NAME);
//				cssProfile.setPassword(TEST_PASSWORD);
//				cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
				cssProfile.setSex(CSSManagerEnums.genderType.Male.ordinal());
//				cssProfile.setSocialURI(TEST_SOCIAL_URI);

				
				return cssProfile;
			
			
			}
	

	/**
	 * This method is called after every @Test methods
	 * 
	 */
	@After
	public void tearDown() {
		LOG.info("[#1867] tearDown");
	}


	/**
	 * 
	 * 
	 */
	@Test
	public void bodyCreateNode() {
		LOG.info("[#1867] create CSS Cloud Node");
		String Name = null;
		Name ="Liam";
		String compareName;
		LOG.info("[#1867] ######################## .................:" +Name);
		LOG.info("[#1867] ##########@@@@@@@@@@@@@@ .................:" +profile.getForeName());
		
		assertTrue(Name.equals(profile.getForeName()));
		
		
		interfaceResult = TestCase755.cssLocalManager.registerCSSNode(profile);
		assertNotNull(interfaceResult);
		
		try {
			cssDetails = TestCase755.cssRegistry.getCssRecord();
			LOG.info("[#1867] &&&&&&&&&&&&&& Name of CSS is .................:" +cssDetails.getName());
		} catch (CssRegistrationException e) {
			LOG.info("[#1867] CssRegistrationException - Could not get the CSS Record from the CSS Registry");
			e.printStackTrace();
		}
		
		
		assertNotNull(cssDetails);
		//cssDetails.getName();
		
		try {
			Name = TestCase755.cssRegistry.getCssRecord().getName();
			LOG.info("[#1867] &&&&&&&&&&&&&& Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1867] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		LOG.info("[#1867] ==================== .................:" +compareName);
		//assertTrue(Name.equals(cssDetails.getForeName())); 
		assertTrue(compareName.equalsIgnoreCase(profile.getName()));
		assertTrue(compareName.equalsIgnoreCase(Name));
		LOG.info("[#1867] create CSS Cloud Node END............finally....."); 
	}
	
	@Test
	public void bodyDeleteNode() {
		LOG.info("[#1867] Delete CSS Cloud Node");
		String Name = null;
		Name ="liam";
		String compareName;
		assertTrue(null != Name);
		
		//interfaceResult = TestCase755.cssLocalManager.registerCSSNode(profile);
		//assertNotNull(interfaceResult);
		//assertTrue(null != interfaceResult);
		try {
			cssDetails = TestCase755.cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			LOG.info("[#1867] CssRegistrationException - Could not add node to the CSS Registry");
			e.printStackTrace();
		}
		
		assertTrue(null != cssDetails);
		
		
		try {
			Name = TestCase755.cssRegistry.getCssRecord().getName();
			interfaceResult = TestCase755.cssLocalManager.getCssRecord();
			LOG.info("[#1867] deleteNode Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1867] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		assertTrue(Name.equals(compareName));
				
		//cssDetails = null;
		TestCase755.cssLocalManager.unregisterCSSNode(profile);
		try {
			cssDetails = TestCase755.cssRegistry.getCssRecord();
			
		} catch (CssRegistrationException e) {
			LOG.info("[#1867] CssRegistrationException - Could not delete node from the CSS Registry");
			e.printStackTrace();
		}
		//assertNull(cssDetails);
		LOG.info("[#1867] CSS Details:" +cssDetails.getName());
		LOG.info("[#1867] Delete CSS Cloud Node END.................");
		
	}

}
