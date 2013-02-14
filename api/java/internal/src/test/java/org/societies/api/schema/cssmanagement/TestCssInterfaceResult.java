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
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	public static final String TEST_NAME = "The CSS";
	public static final String TEST_POSITION = "P455W0RD";
	public static final String TEST_WORKPLACE = "sombody@fb.com";

	private CssRecord record;
	private CssNode cssNode_1, cssNode_2;
	private ArrayList<CssNode> cssNodes;


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
				
		this.record = new CssRecord();
		this.record.setCssIdentity(TEST_IDENTITY);
		
		this.record.getCssNodes().add(cssNode_1);
		this.record.getCssNodes().add(cssNode_2);
		
		
		this.record.setEmailID(TEST_EMAIL);
		this.record.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		this.record.setForeName(TEST_FORENAME);
		
		this.record.setName(TEST_NAME);
		this.record.setPosition(TEST_POSITION);
		this.record.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		this.record.setWorkplace(TEST_WORKPLACE);
		
		
		assertEquals(TEST_IDENTITY, this.record.getCssIdentity());
		
		assertEquals(TEST_EMAIL, this.record.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), this.record.getEntity());
		assertEquals(TEST_FORENAME, this.record.getForeName());
		
		assertEquals(TEST_NAME, this.record.getName());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), this.record.getSex());
		assertEquals(TEST_WORKPLACE, this.record.getWorkplace());
		assertEquals(TEST_POSITION, this.record.getPosition());

	}

	@After
	public void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssNodes = null;
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
		
		assertEquals(TEST_IDENTITY, result.getProfile().getCssIdentity());
		
		assertEquals(cssNodes.size(), result.getProfile().getCssNodes().size());
		
		assertEquals(TEST_EMAIL, result.getProfile().getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), result.getProfile().getEntity());
		assertEquals(TEST_FORENAME, result.getProfile().getForeName());
		
		assertEquals(TEST_NAME, result.getProfile().getName());
		assertEquals(TEST_POSITION, result.getProfile().getPosition());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), result.getProfile().getSex());
		assertEquals(TEST_WORKPLACE, result.getProfile().getWorkplace());

	}

}
