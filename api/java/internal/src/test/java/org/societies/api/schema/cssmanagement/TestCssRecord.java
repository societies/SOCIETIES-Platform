package org.societies.api.schema.cssmanagement;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.management.CSSManagerEnums;

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
	public static final String TEST_POSITION = "P455W0RD";
	public static final String TEST_WORKPLACE = "sombody@fb.com";

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
		
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		
		cssProfile.setName(TEST_NAME);
		
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setWorkplace(TEST_WORKPLACE);
		cssProfile.setPosition(TEST_POSITION);
		
		
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
		
		assertEquals(TEST_EMAIL, cssProfile.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), cssProfile.getEntity());
		assertEquals(TEST_FORENAME, cssProfile.getForeName());
		
		assertEquals(TEST_NAME, cssProfile.getName());
		assertEquals(TEST_POSITION, cssProfile.getPosition());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
		assertEquals(TEST_WORKPLACE, cssProfile.getWorkplace());
	}

}
