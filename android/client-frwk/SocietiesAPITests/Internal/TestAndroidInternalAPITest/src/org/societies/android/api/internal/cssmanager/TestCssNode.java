package org.societies.android.api.internal.cssmanager;

import org.societies.api.schema.cssmanagement.CssNode;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

public class TestCssNode extends AndroidTestCase{

	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	@MediumTest
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
	
	@MediumTest
	public void testAlternativeConstructor() {
		CssNode cssNode = new CssNode();
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Rich.ordinal());
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeType.Rich.ordinal(), cssNode.getType());
		assertEquals(CSSManagerEnums.nodeStatus.Hibernating.ordinal(), cssNode.getStatus());
	}
	
	@MediumTest
	public void testParcelable() {
		CssNode cssNode = new CssNode();
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
        CssNode createFromParcel = CssNode.CREATOR.createFromParcel(parcel);
       
        assertEquals(cssNode.getIdentity(), createFromParcel.getIdentity());		
        assertEquals(cssNode.getStatus(), createFromParcel.getStatus());		
        assertEquals(cssNode.getType(), createFromParcel.getType());		
	}
}
