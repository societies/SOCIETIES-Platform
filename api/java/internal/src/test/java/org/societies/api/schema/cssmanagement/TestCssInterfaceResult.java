package org.societies.api.schema.cssmanagement;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.CSSManagerEnums;

public class TestCssInterfaceResult {
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

	private CssRecord record;
	private CssNode cssNode_1, cssNode_2;
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
		cssNodes.add(cssNode_1);
		cssNodes.add(cssNode_2);
		
		cssArchivedNodes = new ArrayList<CssNode>();
		cssArchivedNodes.add(cssNode_1);
		cssArchivedNodes.add(cssNode_2);

		
		this.record = new CssRecord();
		this.record.setCssIdentity(TEST_IDENTITY);
		
		this.record.getCssNodes().add(cssNode_1);
		this.record.getCssNodes().add(cssNode_2);
		
		this.record.getArchiveCSSNodes().add(cssNode_1);
		this.record.getArchiveCSSNodes().add(cssNode_2);
		
//		this.record.setCssInactivation(TEST_INACTIVE_DATE);
//		this.record.setCssRegistration(TEST_REGISTERED_DATE);
//		this.record.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
//		this.record.setCssUpTime(TEST_UPTIME);
		this.record.setEmailID(TEST_EMAIL);
		this.record.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		this.record.setForeName(TEST_FORENAME);
		this.record.setHomeLocation(TEST_HOME_LOCATION);
//		this.record.setIdentityName(TEST_IDENTITY_NAME);
//		this.record.setImID(TEST_IM_ID);
		this.record.setName(TEST_NAME);
//		this.record.setPassword(TEST_PASSWORD);
//		this.record.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		this.record.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
//		this.record.setSocialURI(TEST_SOCIAL_URI);
		
		
		assertEquals(TEST_IDENTITY, this.record.getCssIdentity());
//		assertEquals(TEST_INACTIVE_DATE, this.record.getCssInactivation());
//		assertEquals(TEST_REGISTERED_DATE, this.record.getCssRegistration());
//		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), this.record.getStatus());
//		assertEquals(TEST_UPTIME, this.record.getCssUpTime());
		assertEquals(TEST_EMAIL, this.record.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), this.record.getEntity());
		assertEquals(TEST_FORENAME, this.record.getForeName());
		assertEquals(TEST_HOME_LOCATION, this.record.getHomeLocation());
//		assertEquals(TEST_IDENTITY_NAME, this.record.getIdentityName());
//		assertEquals(TEST_IM_ID, this.record.getImID());
		assertEquals(TEST_NAME, this.record.getName());
//		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), this.record.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), this.record.getSex());
//		assertEquals(TEST_SOCIAL_URI, this.record.getSocialURI());

	}

	@After
	public void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssNodes = null;
		cssArchivedNodes = null;
		this.record = null;
	}

	@Test
	public void testClass() {
		CssInterfaceResult result = new CssInterfaceResult();
		result.setResultStatus(true);
		result.setProfile(this.record);
		
		assertNotNull(result);
		assertEquals(true, result.isResultStatus());
		assertNotNull(result.getProfile());
		assertEquals(cssArchivedNodes.size(), result.getProfile().getArchiveCSSNodes().size());
		assertEquals(TEST_IDENTITY, result.getProfile().getCssIdentity());
//		assertEquals(TEST_INACTIVE_DATE, result.getProfile().getCssInactivation());
		assertEquals(cssNodes.size(), result.getProfile().getCssNodes().size());
//		assertEquals(TEST_REGISTERED_DATE, result.getProfile().getCssRegistration());
//		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), result.getProfile().getStatus());
//		assertEquals(TEST_UPTIME, result.getProfile().getCssUpTime());
		assertEquals(TEST_EMAIL, result.getProfile().getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), result.getProfile().getEntity());
		assertEquals(TEST_FORENAME, result.getProfile().getForeName());
		assertEquals(TEST_HOME_LOCATION, result.getProfile().getHomeLocation());
//		assertEquals(TEST_IDENTITY_NAME, result.getProfile().getIdentityName());
//		assertEquals(TEST_IM_ID, result.getProfile().getImID());
		assertEquals(TEST_NAME, result.getProfile().getName());
//		assertEquals(TEST_PASSWORD, result.getProfile().password);
//		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), result.getProfile().getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), result.getProfile().getSex());
//		assertEquals(TEST_SOCIAL_URI, result.getProfile().getSocialURI());

	}

}
