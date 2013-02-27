package org.societies.api.android.internal.model;

import static org.junit.Assert.*;

//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.schema.cssmanagement.CssNode;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;


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
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
		
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), cssNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), cssNode.getType());
	}
	
	@Test
	public void testAlternativeConstructor() {
		AndroidCSSNode cssNode = new AndroidCSSNode();
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Rich.ordinal());
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeType.Rich.ordinal(), cssNode.getType());
		assertEquals(CSSManagerEnums.nodeStatus.Hibernating.ordinal(), cssNode.getStatus());
	}
	
	@Test 
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
//	@Test
//	public void testParcelable() {
//		AndroidCSSNode cssNode = new AndroidCSSNode(TEST_IDENTITY_1, CSSNode.nodeStatus.Hibernating.ordinal(), CSSNode.nodeType.Rich.ordinal());
//		
//		Parcel parcelOut = new Parcel();
//		
//		cssNode.writeToParcel(out, flags)
//		
//	}
	
//	@Test
//	/**
//	 * Tests the use GSON and JSON libraries to allow Java classes to be 
//	 * converted to and from JSON. Cannot work in this project as Android 
//	 * library is stubbed.
//	 */
//	public void testJSONConversion() {
//		AndroidCSSNode cssNode = new AndroidCSSNode();
//		assertNotNull(cssNode);
//		
//		cssNode.setIdentity(TEST_IDENTITY_1);
//		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
//		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
//		
//		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
//		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), cssNode.getStatus());
//		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), cssNode.getType());
//
//		
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String jSON = gson.toJson(cssNode);
//		System.out.println(jSON);
//		
//		gson = new Gson();
//		jSON = gson.toJson(cssNode);
//		try {
//			JSONObject jObj =  (JSONObject) new JSONTokener(jSON).nextValue();
//			assertEquals(jObj.get("identity"), cssNode.getIdentity());
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


}
