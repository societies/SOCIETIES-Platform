package org.societies.api.schema.cssmanagement;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.management.CSSManagerEnums;

public class TestCssNode {

	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor() throws Exception {
		CssNode cssNode = new CssNode();
		assertNotNull(cssNode);
		
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
		
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), cssNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), cssNode.getType());
	}
	
	@Test
	public void testAlternativeConstructor() {
		CssNode cssNode = new CssNode();
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Rich.ordinal());
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeType.Rich.ordinal(), cssNode.getType());
		assertEquals(CSSManagerEnums.nodeStatus.Hibernating.ordinal(), cssNode.getStatus());

		
	}

}
