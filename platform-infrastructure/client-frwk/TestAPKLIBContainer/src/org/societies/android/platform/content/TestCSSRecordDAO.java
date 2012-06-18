package org.societies.android.platform.content;

import org.societies.android.api.internal.cssmanager.AndroidCSSNode;
import org.societies.android.api.internal.cssmanager.AndroidCSSRecord;
import org.societies.api.internal.css.management.CSSManagerEnums;

import android.test.AndroidTestCase;

public class TestCSSRecordDAO extends AndroidTestCase {

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

	private AndroidCSSNode cssNode_1, cssNode_2;
	private AndroidCSSNode cssNodes [];
	private AndroidCSSNode cssArchivedNodes [];
	private AndroidCSSRecord cssProfile;

	protected void setUp() throws Exception {
		super.setUp();
		
		getContext().deleteDatabase(CssRecordDAO.SOCIETIES_DATABASE_NAME);

		cssNode_1 = new AndroidCSSNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new AndroidCSSNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
		cssNodes = new AndroidCSSNode[2];
		cssNodes[0] = cssNode_1;
		cssNodes[1] = cssNode_2;
		
		cssArchivedNodes = new AndroidCSSNode[2];
		cssArchivedNodes[0] = cssNode_1;
		cssArchivedNodes[1] = cssNode_2;

		cssProfile = new AndroidCSSRecord();
		cssProfile.setCSSNodes(cssNodes);
		cssProfile.setArchiveCSSNodes(cssArchivedNodes);
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
		cssProfile.setPassword(TEST_PASSWORD);
		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setSocialURI(TEST_SOCIAL_URI);
		
		
		assertEquals(cssArchivedNodes.length, cssProfile.getArchivedCSSNodes().length);
		assertEquals(cssArchivedNodes[0].getIdentity(), cssProfile.getArchivedCSSNodes()[0].getIdentity());
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
		assertEquals(TEST_INACTIVE_DATE, cssProfile.getCssInactivation());
		assertEquals(cssNodes.length, cssProfile.getCSSNodes().length);
		assertEquals(cssNodes[0].getIdentity(), cssProfile.getCSSNodes()[0].getIdentity());
		assertEquals(TEST_REGISTERED_DATE, cssProfile.getCssRegistration());
		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), cssProfile.getStatus());
		assertEquals(TEST_UPTIME, cssProfile.getCssUpTime());
		assertEquals(TEST_EMAIL, cssProfile.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), cssProfile.getEntity());
		assertEquals(TEST_FORENAME, cssProfile.getForeName());
		assertEquals(TEST_HOME_LOCATION, cssProfile.getHomeLocation());
		assertEquals(TEST_IDENTITY_NAME, cssProfile.getIdentityName());
		assertEquals(TEST_IM_ID, cssProfile.getImID());
		assertEquals(TEST_NAME, cssProfile.getName());
		assertEquals(TEST_PASSWORD, cssProfile.getPassword());
		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), cssProfile.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
		assertEquals(TEST_SOCIAL_URI, cssProfile.getSocialURI());

	}

	protected void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssNodes = null;
		cssArchivedNodes = null;
		cssProfile = null;
		
		super.tearDown();
	}

	public void testCRUCssRecord() throws Exception {
		CssRecordDAO cssRecordDAO = new CssRecordDAO(getContext());
		assertTrue(null != cssRecordDAO);
		
		cssRecordDAO.insertCSSRecord(this.cssProfile);
		
		assertTrue(cssRecordDAO.cssRecordExists());
		
		AndroidCSSRecord storedRecord = cssRecordDAO.readCSSrecord();
		
		assertNotNull(storedRecord);
		
		assertEquals(this.cssProfile.getCssHostingLocation(), storedRecord.getCssHostingLocation());
		assertEquals(this.cssProfile.getCSSNodes().length, storedRecord.getCSSNodes().length);
		assertEquals(this.cssProfile.getArchivedCSSNodes().length, storedRecord.getArchivedCSSNodes().length);
	}

}