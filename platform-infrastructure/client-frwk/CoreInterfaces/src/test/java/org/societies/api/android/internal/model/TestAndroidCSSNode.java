package org.societies.api.android.internal.model;

import static org.junit.Assert.*;

import javax.swing.text.html.CSS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.management.CSSNode;

import android.os.Parcel;

public class TestAndroidCSSNode {

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
		AndroidCSSNode cssNode = new AndroidCSSNode();
		assertNotNull(cssNode);
		
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSNode.nodeStatus.Available.ordinal());
		cssNode.setType(CSSNode.nodeType.Cloud.ordinal());
		
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSNode.nodeStatus.Available.ordinal(), cssNode.getStatus());
		assertEquals(CSSNode.nodeType.Cloud.ordinal(), cssNode.getType());
	}
	
	@Test
	public void testAlternativeConstructor() {
		AndroidCSSNode cssNode = new AndroidCSSNode(TEST_IDENTITY_1, CSSNode.nodeStatus.Hibernating.ordinal(), CSSNode.nodeType.Rich.ordinal());
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSNode.nodeType.Rich.ordinal(), cssNode.getType());
		assertEquals(CSSNode.nodeStatus.Hibernating.ordinal(), cssNode.getStatus());
	}
	
//	@Test
//	public void testParcelable() {
//		AndroidCSSNode cssNode = new AndroidCSSNode(TEST_IDENTITY_1, CSSNode.nodeStatus.Hibernating.ordinal(), CSSNode.nodeType.Rich.ordinal());
//		
//		Parcel parcelOut = new Parcel();
//		
//		cssNode.writeToParcel(out, flags)
//		
//	}


}
