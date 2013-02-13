package org.societies.css.mgmt;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;


public class TestCSSManager {
	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "CSSProfile1";
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	public static final String TEST_NAME = "TestCSS";
	public static final String TEST_POSITION = "Senior Software Engineer";
	public static final String TEST_WORKPLACE = "Lake Communications";
	private ICssRegistry cssRegistry;
	


	@Before
	public void setUp() throws Exception {
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor() {
		CSSManager manager = new CSSManager();
		assertNotNull(manager);
	}

	@Test
	public void testXMPPRegistration() {
		CssRecord profile = new CssRecord();
		
		profile.setCssIdentity(TEST_IDENTITY);
		profile.setEmailID(TEST_EMAIL);
		profile.setForeName(TEST_FORENAME);
		profile.setPosition(TEST_POSITION);
		profile.setWorkplace(TEST_WORKPLACE);
		
		assertEquals(TEST_IDENTITY, profile.getCssIdentity());
		assertEquals(TEST_EMAIL, profile.getEmailID());
		assertEquals(TEST_FORENAME, profile.getForeName());
		assertEquals(TEST_POSITION, profile.getPosition());
		assertEquals(TEST_WORKPLACE, profile.getWorkplace());
		
		
		CSSManager manager = new CSSManager();
		
		
		manager.setCssRegistry(cssRegistry);
		
		//Future<CssInterfaceResult> result = manager.registerXMPPServer(profile);
		//ICSSRemoteManager cssManagerRemote = null;
		manager.sendCssFriendRequest(TEST_IDENTITY_2);
		//cssManagerRemote.sendCssFriendRequest(TEST_IDENTITY_2);
		
		
		//assertNotNull(result);
		/*
		try {
			CssInterfaceResult  interfaceResult = result.get();
			assertTrue(interfaceResult.isResultStatus());
			
			assertEquals(TEST_IDENTITY, interfaceResult.getProfile().getCssIdentity());
			assertEquals(TEST_EMAIL, interfaceResult.getProfile().getEmailID());
			assertEquals(TEST_FORENAME, interfaceResult.getProfile().getForeName());
			assertEquals(TEST_HOME_LOCATION, interfaceResult.getProfile().getHomeLocation());
			assertEquals(TEST_IDENTITY_NAME, interfaceResult.getProfile().getIdentityName());
			assertEquals(TEST_IM_ID, interfaceResult.getProfile().getImID());
			assertEquals(TEST_NAME, interfaceResult.getProfile().getName());
			assertEquals(TEST_SOCIAL_URI, interfaceResult.getProfile().getSocialURI());
			assertEquals(TEST_REGISTERED_DATE, interfaceResult.getProfile().getCssRegistration());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
}
