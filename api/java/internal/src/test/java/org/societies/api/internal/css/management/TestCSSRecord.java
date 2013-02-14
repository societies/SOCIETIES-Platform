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
	
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	
	public static final String TEST_NAME = "The CSS";
	public static final String TEST_POSITION = "P455W0RD";
	public static final String TEST_WORKPLACE = "sombody@fb.com";

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
		
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setCssNodes(cssArrayNodes);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setName(TEST_NAME);
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setWorkplace(TEST_WORKPLACE);
		cssProfile.setPosition(TEST_POSITION);
		
		
		
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
		assertEquals(cssArrayNodes.length, cssProfile.getCssNodes().length);
		assertEquals(TEST_EMAIL, cssProfile.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), cssProfile.getEntity());
		assertEquals(TEST_FORENAME, cssProfile.getForeName());
		assertEquals(TEST_NAME, cssProfile.getName());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
		assertEquals(TEST_WORKPLACE, cssProfile.getWorkplace());
		assertEquals(TEST_POSITION, cssProfile.getPosition());
	}

}
