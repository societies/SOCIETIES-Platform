package org.societies.android.api.internal.cssmanager;

import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.schema.cssmanagement.CssNode;

import android.os.Parcel;
import android.test.AndroidTestCase;



public class TestAndroidCSSNode extends AndroidTestCase{

	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		AndroidCSSNode cssNode = new AndroidCSSNode();
		assertNotNull(cssNode);
		
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
		
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), cssNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), cssNode.getType());
	}
	
	public void testAlternativeConstructor() {
		AndroidCSSNode cssNode = new AndroidCSSNode();
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Rich.ordinal());
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeType.Rich.ordinal(), cssNode.getType());
		assertEquals(CSSManagerEnums.nodeStatus.Hibernating.ordinal(), cssNode.getStatus());
	}
	
	public void testConversion() {
		CssNode cssNode = new CssNode();
		assertNotNull(cssNode);
		
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
		
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), cssNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), cssNode.getType());

		AndroidCSSNode aNode = AndroidCSSNode.convertCssNode(cssNode);

		assertEquals(TEST_IDENTITY_1, aNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), aNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), aNode.getType());
		
	}
	
	public void testParcelable() {
		AndroidCSSNode cssNode = new AndroidCSSNode();
		assertNotNull(cssNode);
		
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());

		
		assertEquals(0, cssNode.describeContents());
		
        Parcel parcel = Parcel.obtain();
        cssNode.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        AndroidCSSNode createFromParcel = AndroidCSSNode.CREATOR.createFromParcel(parcel);
       
        assertEquals(cssNode.getIdentity(), createFromParcel.getIdentity());		
        assertEquals(cssNode.getStatus(), createFromParcel.getStatus());		
        assertEquals(cssNode.getType(), createFromParcel.getType());		
	}
}
