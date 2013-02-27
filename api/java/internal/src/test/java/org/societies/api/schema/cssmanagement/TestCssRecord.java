package org.societies.api.schema.cssmanagement;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.CSSManagerEnums;

public class TestCssRecord {

	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "CSSProfile1";
	public static final String TEST_INACTIVE_DATE = "20121029";
	public static final String TEST_REGISTERED_DATE = "20120229";
	public static final int TEST_UPTIME = 7799;
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	public static final String TEST_HOME_LOCATION = "The Hearth";
	public static final String TEST_IDENTITY_NAME = "Id Name";
	public static final String TEST_IM_ID = "somebody.tssg.org";
	public static final String TEST_NAME = "The CSS";
	public static final String TEST_PASSWORD = "P455W0RD";
	public static final String TEST_SOCIAL_URI = "sombody@fb.com";

	CssNode cssNode_1;
	private CssNode cssNode_2;
	private ArrayList<CssNode> cssNodes;
	private ArrayList<CssNode> cssArchivedNodes;
	
	
	@Before
	public void setUp() throws Exception {
		cssNode_1 = new CssNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new CssNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
		cssNodes = new ArrayList<CssNode>();
//		cssNodes.add(cssNode_1);
//		cssNodes.add(cssNode_2);
		
		cssArchivedNodes = new ArrayList<CssNode>();
//		cssArchivedNodes.add(cssNode_1);
//		cssArchivedNodes.add(cssNode_2);
	}

	@After
	public void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssNodes = null;
		cssArchivedNodes = null;
	}

	@Test
	public void testConstructor() {
		CssRecord cssProfile = new CssRecord();
		
		cssProfile.setCssIdentity(TEST_IDENTITY);
		
//		cssProfile.getCssNodes().add(cssNode_1);
//		cssProfile.getCssNodes().add(cssNode_2);
		
//		cssProfile.getArchiveCSSNodes().add(cssNode_1);
//		cssProfile.getArchiveCSSNodes().add(cssNode_2);
		
//		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
//		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
//		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
//		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
//		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
//		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
//		cssProfile.setPassword(TEST_PASSWORD);
//		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
//		cssProfile.setSocialURI(TEST_SOCIAL_URI);
		
		
//		assertEquals(cssArchivedNodes.size(), cssProfile.getArchiveCSSNodes().size());
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
//		assertEquals(TEST_INACTIVE_DATE, cssProfile.getCssInactivation());
//		assertEquals(cssNodes.size(), cssProfile.getCssNodes().size());
//		assertEquals(TEST_REGISTERED_DATE, cssProfile.getCssRegistration());
//		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), cssProfile.getStatus());
//		assertEquals(TEST_UPTIME, cssProfile.getCssUpTime());
		assertEquals(TEST_EMAIL, cssProfile.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), cssProfile.getEntity());
		assertEquals(TEST_FORENAME, cssProfile.getForeName());
		assertEquals(TEST_HOME_LOCATION, cssProfile.getHomeLocation());
//		assertEquals(TEST_IDENTITY_NAME, cssProfile.getIdentityName());
//		assertEquals(TEST_IM_ID, cssProfile.getImID());
		assertEquals(TEST_NAME, cssProfile.getName());
//		assertEquals(TEST_PASSWORD, cssProfile.password);
//		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), cssProfile.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
//		assertEquals(TEST_SOCIAL_URI, cssProfile.getSocialURI());
	}

}
