package org.societies.api.internal.css.management;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.CSSManagerEnums;
import org.societies.api.internal.css.CSSNode;
import org.societies.api.internal.css.CSSRecord;

public class TestCSSRecord {

	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "CSSrecord1";
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

	private CSSNode cssNode_1, cssNode_2;
	private CSSNode cssArrayNodes[];
	private CSSNode cssArrayArchivedNodes[];
	
	
	@Before
	public void setUp() throws Exception {
		cssNode_1 = new CSSNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new CSSNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
		cssArrayNodes = new CSSNode[2];
		cssArrayNodes[0] = cssNode_1;
		cssArrayNodes[1] = cssNode_2;
		
		cssArrayArchivedNodes = new CSSNode[2];
		cssArrayArchivedNodes[0] = cssNode_1;
		cssArrayArchivedNodes[1] = cssNode_2;
	}

	@After
	public void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssArrayNodes = null;
		cssArrayArchivedNodes = null;
	}

	@Test
	public void testConstructor() {
		CSSRecord cssProfile = new CSSRecord();
		
		cssProfile.setArchiveCSSNodes(cssArrayArchivedNodes);
		cssProfile.setCssIdentity(TEST_IDENTITY);
//		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
		cssProfile.setCssNodes(cssArrayNodes);
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
		
		
		assertEquals(cssArrayArchivedNodes.length, cssProfile.getArchiveCSSNodes().length);
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
//		assertEquals(TEST_INACTIVE_DATE, cssProfile.getCssInactivation());
		assertEquals(cssArrayNodes.length, cssProfile.getCssNodes().length);
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
//		assertEquals(TEST_PASSWORD, cssProfile.getPassword());
//		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), cssProfile.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
//		assertEquals(TEST_SOCIAL_URI, cssProfile.getSocialURI());
	}

}
