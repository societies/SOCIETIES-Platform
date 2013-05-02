package org.societies.integration.test.bit.cssrecordtocontext;


import static org.junit.Assert.*;

import java.util.ArrayList;
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
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.css.CSSManagerEnums;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;

public class NominalTestCaseLowerTester {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);
	
	public IntegrationTestUtils integrationTestUtils;
	
	CssRecord profile = null;
	CssRecord cssDetails = null;
	Future<CssInterfaceResult> interfaceResult = null;
	ICtxBroker ctxBroker;
	/**
	 * Test case number
	 */
	public static int testCaseNumber;
	public static final String TEST_IDENTITY_2 = "jane.societies.local";
	public static final String TEST_IDENTITY_1 = "jane.societies.local";

	public static final String TEST_IDENTITY = "cloud";
	public static final String TEST_EMAIL = "Liam@tssg.org";
	public static final String TEST_FORENAME = "Liam";
	public static final String TEST_HOME_LOCATION = "Sligo";
	public static final String TEST_IDENTITY_NAME = "Id Name";
	public static final String TEST_NAME = "Cloud CSS";
	public static final String TEST_PASSWORD = "cloudpass";
	public static final String TEST_WORKPLACE = "TSSG";
	public static final String TEST_POSITION = "poster boy";
	
	


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
		LOG.info("[#1992] Initialization");
		LOG.info("[#1992] Prerequisite: The PLAN file is executed");
		LOG.info("[#1992] Prerequisite: The CSS has an identity");
		LOG.info("[#1992] Prerequisite: The Context Broker is in place");
		LOG.info("[#1992] Prerequisite: The context identity");
		
	}

	/**
	 * This method is called before every @Test methods.
	 * 
	 */
	@Before
	public void setUp() {
		LOG.info("[#1992] NominalTestCaseLowerTester::setUp");
		
		

		Future<CssInterfaceResult> interfaceResult = null;
		//CssRecord profile = null;
		
		profile = createCSSRecord();
		LOG.info("[#1992] CSS Record: profile:" +profile);

			// -- Create the CSS Record
			LOG.info("[#1992] Preamble: Create the CSS Record");
	}
			private CssRecord createCSSRecord() {
				
				
				LOG.info("[#1992] createCSSRecord: Create the CSS Record/////////////////////");
		    	CssNode cssNode_1;

				cssNode_1 = new CssNode();
				cssNode_1.setIdentity(TEST_IDENTITY_2);
				cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
				cssNode_1.setType(CSSManagerEnums.nodeType.Cloud.ordinal());				

				CssRecord cssProfile = new CssRecord();
				cssProfile.getCssNodes().add(cssNode_1);
				
				cssProfile.setCssIdentity(TEST_IDENTITY_1);
				cssProfile.setEmailID(TEST_EMAIL);
				cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
				cssProfile.setForeName(TEST_FORENAME);
				cssProfile.setHomeLocation(TEST_HOME_LOCATION);
				cssProfile.setName(TEST_NAME);
				cssProfile.setSex(CSSManagerEnums.genderType.Male.ordinal());
				cssProfile.setWorkplace(TEST_WORKPLACE);
				cssProfile.setPosition(TEST_POSITION);
				
				return cssProfile;
			
			
			}
	

	/**
	 * This method is called after every @Test methods
	 * 
	 */
	@After
	public void tearDown() {
		LOG.info("[#1992] tearDown");
		try {
			TestCase1992.cssRegistry.unregisterCss(cssDetails);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * 
	 */
	@Ignore
	public void bodyCreateNode() {
		LOG.info("[#1992] create CSS Cloud Node");
		String Name = null;
		Name ="Liam";
		String compareName;
		LOG.info("[#1992] ######################## .................:" +Name);
		LOG.info("[#1992] ##########@@@@@@@@@@@@@@ .................:" +profile.getForeName());
		
		assertTrue(Name.equals(profile.getForeName()));
		
		
		interfaceResult = TestCase1992.cssLocalManager.registerCSSNode(profile);
		assertNotNull(interfaceResult);
		
		try {
			cssDetails = TestCase1992.cssRegistry.getCssRecord();
			LOG.info("[#1992] &&&&&&&&&&&&&& Name of CSS is .................:" +cssDetails.getName());
		} catch (CssRegistrationException e) {
			LOG.info("[#1992] CssRegistrationException - Could not get the CSS Record from the CSS Registry");
			e.printStackTrace();
		}
		
		
		assertNotNull(cssDetails);
		//cssDetails.getName();
		
		try {
			Name = TestCase1992.cssRegistry.getCssRecord().getName();
			LOG.info("[#1992] &&&&&&&&&&&&&& Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#1992] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		LOG.info("[#1992] ==================== .................:" +compareName);
		//assertTrue(Name.equals(cssDetails.getForeName())); 
		assertTrue(compareName.equalsIgnoreCase(profile.getName()));
		assertTrue(compareName.equalsIgnoreCase(Name));
		LOG.info("[#1992] create CSS Cloud Node END............finally....."); 
	}
	
	@Test
	public void pushrecordtocontext() {
		LOG.info("[#1992] Push CSS Record to Context");
		String Name = null;
		Name ="liam";
		String compareName;
		assertTrue(null != Name);
		
		cssDetails = this.createCSSRecord();
		assertTrue(null != cssDetails);
		LOG.info("[#1992] PushtoContext Name of CSS is .................:" +cssDetails.getName());
		LOG.info("[#1992] PushtoContext identity of CSS is .................:" +cssDetails.getCssIdentity());
		
/*		
		try {
			Name = TestCase1992.cssRegistry.getCssRecord().getName();
			interfaceResult = TestCase1992.cssLocalManager.getCssRecord();
			
		} catch (CssRegistrationException e) {
			LOG.info("[#1992] CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		assertTrue(Name.equals(compareName));
				
		//cssDetails = null;
		TestCase1992.cssLocalManager.unregisterCSSNode(profile);
		try {
			cssDetails = TestCase1992.cssRegistry.getCssRecord();
			
		} catch (CssRegistrationException e) {
			LOG.info("[#1992] CssRegistrationException - Could not delete node from the CSS Registry");
			e.printStackTrace();
		} */
		TestCase1992.cssLocalManager.pushtoContext(cssDetails);
		
		String cssIdStr = cssDetails.getCssIdentity();
		LOG.info("[#1992] Push To Context cssIdStr................." +cssIdStr);
		
		IIdentity cssId = null;;
		try {
			cssId = TestCase1992.commManager.getIdManager().fromJid(cssIdStr);
			LOG.info("[#1992] Push To Context cssId................." +cssId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//cssDetails.setCssIdentity(cssId);
		CtxEntityIdentifier ownerCtxId = null;
		try {
			ownerCtxId = TestCase1992.getCtxBroker().retrieveIndividualEntity(cssId).get().getId();
			LOG.info("[#1992] Push To Context ownerCtxId................." +ownerCtxId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
		//String ident = (this.retrieveCtxAttribute(ownerCtxId, CtxAttributeTypes.ID).toString());
		String ident = (this.retrieveCtxAttribute(ownerCtxId, CtxAttributeTypes.ID).getStringValue());
		LOG.info("[#1992] Push To Context ident................." +ident);
		assertNotNull(ident);
		assertTrue(ident.equalsIgnoreCase(cssDetails.getCssIdentity()));
		String email = (this.retrieveCtxAttribute(ownerCtxId, CtxAttributeTypes.EMAIL).getStringValue());
		LOG.info("[#1992] Push To Context email................." +email);
		assertNotNull(email);
		assertTrue(email.equalsIgnoreCase(cssDetails.getEmailID()));
		String name = (this.retrieveCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME).getStringValue());
		LOG.info("[#1992] Push To Context name................." +name);
		assertNotNull(name);
		assertTrue(name.equalsIgnoreCase(cssDetails.getName()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//assertNull(cssDetails);
		LOG.info("[#1992] CSS Details:" +cssDetails.getName());
		LOG.info("[#1992] Push To Context END.................");
		
		
		
	}
	
	private CtxAttribute retrieveCtxAttribute(CtxEntityIdentifier ownerCtxId, String type) throws Exception {

		LOG.info("[#1992] Push To Context retrieveCtxAttribute called .................");
		LOG.info("Retrieving '" + type + "' attribute of entity " + ownerCtxId);
		  if (LOG.isDebugEnabled())
		    LOG.debug("Retrieving '" + type + "' attribute of entity " + ownerCtxId);
		  
		final List<CtxIdentifier> ctxIds = TestCase1992.getCtxBroker().lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		  
		  if (!ctxIds.isEmpty())
		    return (CtxAttribute) TestCase1992.getCtxBroker().retrieve(ctxIds.get(0)).get();
		  else
		    return null;
		}

}
