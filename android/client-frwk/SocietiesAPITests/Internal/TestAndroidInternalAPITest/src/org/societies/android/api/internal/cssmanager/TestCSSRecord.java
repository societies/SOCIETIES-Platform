package org.societies.android.api.internal.cssmanager;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class TestCSSRecord extends AndroidTestCase{

	private static final String LOG_TAG = TestCSSRecord.class.getName();
	
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

	private CssNode cssNode_1, cssNode_2;
	private List<CssNode> cssNodes;
	private List<CssNode> cssArchivedNodes;
	
	protected void setUp() throws Exception {
		super.setUp();
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
	}

	protected void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssNodes = null;
		cssArchivedNodes = null;
		
		super.tearDown();
	}


	@MediumTest
	public void testConstructor() {
		CssRecord cssProfile = new CssRecord();
		cssProfile.setCssNodes(cssNodes);
		cssProfile.setArchiveCSSNodes(cssArchivedNodes);
		cssProfile.setCssIdentity(TEST_IDENTITY);
//		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
//		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
//		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
//		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
//		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
//		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
		cssProfile.setPassword(TEST_PASSWORD);
//		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
//		cssProfile.setSocialURI(TEST_SOCIAL_URI);
		
		
		assertEquals(cssArchivedNodes.size(), cssProfile.getArchiveCSSNodes().size());
		assertEquals(cssArchivedNodes.get(0).getIdentity(), cssProfile.getArchiveCSSNodes().get(0).getIdentity());
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
//		assertEquals(TEST_INACTIVE_DATE, cssProfile.getCssInactivation());
		assertEquals(cssNodes.size(), cssProfile.getCssNodes().size());
		assertEquals(cssNodes.get(0).getIdentity(), cssProfile.getCssNodes().get(0).getIdentity());
//		assertEquals(TEST_REGISTERED_DATE, cssProfile.getCssRegistration());
//		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), cssProfile.getStatus());
//		assertEquals(TEST_UPTIME, cssProfile.getCssUpTime());
		assertEquals(TEST_EMAIL, cssProfile.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), cssProfile.getEntity());
		assertEquals(TEST_FORENAME, cssProfile.getForeName());
		assertEquals(TEST_HOME_LOCATION, cssProfile.getHomeLocation());
//		assertEquals(TEST_IDENTITY_NAME, cssProfile.getIdentityName());
//		assertEquals(TEST_IM_ID, cssProfile.getImID());
		assertEquals(TEST_NAME, cssProfile.getName());
		assertEquals(TEST_PASSWORD, cssProfile.getPassword());
//		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), cssProfile.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
//		assertEquals(TEST_SOCIAL_URI, cssProfile.getSocialURI());
	}

	@MediumTest
	public void testArrays() {
		CssRecord cssProfile = new CssRecord();
		assertEquals(0, cssProfile.getArchiveCSSNodes().size());
		assertEquals(0, cssProfile.getCssNodes().size());
	}
	
	@MediumTest
	public void testParcelable() {
		CssRecord cssRecord = new CssRecord();
		assertNotNull(cssRecord);
		
		cssRecord.getCssNodes().add(cssNode_1);
		cssRecord.getCssNodes().add(cssNode_2);
		cssRecord.getArchiveCSSNodes().add(cssNode_1);
		cssRecord.getArchiveCSSNodes().add(cssNode_2);
		
		cssRecord.setCssIdentity(TEST_IDENTITY);
//		cssRecord.setCssInactivation(TEST_INACTIVE_DATE);
//		cssRecord.setCssRegistration(TEST_REGISTERED_DATE);
//		cssRecord.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
//		cssRecord.setCssUpTime(TEST_UPTIME);
		cssRecord.setEmailID(TEST_EMAIL);
		cssRecord.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssRecord.setForeName(TEST_FORENAME);
		cssRecord.setHomeLocation(TEST_HOME_LOCATION);
//		cssRecord.setIdentityName(TEST_IDENTITY_NAME);
//		cssRecord.setImID(TEST_IM_ID);
		cssRecord.setName(TEST_NAME);
		cssRecord.setPassword(TEST_PASSWORD);
//		cssRecord.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssRecord.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
//		cssRecord.setSocialURI(TEST_SOCIAL_URI);

		
		assertEquals(0, cssRecord.describeContents());
		
		Log.d(LOG_TAG, "Start Parcelable Serialise : " + System.currentTimeMillis());

        Parcel parcel = Parcel.obtain();
        cssRecord.writeToParcel(parcel, 0);
		
        Log.d(LOG_TAG, "End Parcelable Serialise : " + System.currentTimeMillis());

		//done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        CssRecord createFromParcel = CssRecord.CREATOR.createFromParcel(parcel);
        
		Log.d(LOG_TAG, "Finish Serialise : " + System.currentTimeMillis());
//        assertEquals(cssRecord.getCssHostingLocation(), createFromParcel.getCssHostingLocation());		
        assertEquals(cssRecord.getCssIdentity(), createFromParcel.getCssIdentity());		
//        assertEquals(cssRecord.getCssInactivation(), createFromParcel.getCssInactivation());		
//        assertEquals(cssRecord.getCssRegistration(), createFromParcel.getCssRegistration());		
//        assertEquals(cssRecord.getCssUpTime(), createFromParcel.getCssUpTime());		
        assertEquals(cssRecord.getDomainServer(), createFromParcel.getDomainServer());		
        assertEquals(cssRecord.getEmailID(), createFromParcel.getEmailID());		
        assertEquals(cssRecord.getEntity(), createFromParcel.getEntity());		
        assertEquals(cssRecord.getForeName(), createFromParcel.getForeName());		
        assertEquals(cssRecord.getHomeLocation(), createFromParcel.getHomeLocation());		
//        assertEquals(cssRecord.getIdentityName(), createFromParcel.getIdentityName());		
//        assertEquals(cssRecord.getImID(), createFromParcel.getImID());		
        assertEquals(cssRecord.getName(), createFromParcel.getName());		
        assertEquals(cssRecord.getPassword(), createFromParcel.getPassword());		
//        assertEquals(cssRecord.getPresence(), createFromParcel.getPresence());		
        assertEquals(cssRecord.getSex(), createFromParcel.getSex());		
//        assertEquals(cssRecord.getSocialURI(), createFromParcel.getSocialURI());		
//        assertEquals(cssRecord.getStatus(), createFromParcel.getStatus());		
        assertEquals(cssRecord.getArchiveCSSNodes().size(), createFromParcel.getArchiveCSSNodes().size());
        assertEquals(cssRecord.getCssNodes().size(), createFromParcel.getCssNodes().size());
	}
//	@MediumTest
//	public void testSimpleSerialisation() throws Exception {
//		try {
//			
//			CssRecord cssRecord = new CssRecord();
//			assertNotNull(cssRecord);
//			
//			cssRecord.getCssNodes().add(cssNode_1);
//			cssRecord.getCssNodes().add(cssNode_2);
//			cssRecord.getArchiveCSSNodes().add(cssNode_1);
//			cssRecord.getArchiveCSSNodes().add(cssNode_2);
//			
//			cssRecord.setCssIdentity(TEST_IDENTITY);
//			cssRecord.setCssInactivation(TEST_INACTIVE_DATE);
//			cssRecord.setCssRegistration(TEST_REGISTERED_DATE);
//			cssRecord.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
//			cssRecord.setCssUpTime(TEST_UPTIME);
//			cssRecord.setEmailID(TEST_EMAIL);
//			cssRecord.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
//			cssRecord.setForeName(TEST_FORENAME);
//			cssRecord.setHomeLocation(TEST_HOME_LOCATION);
//			cssRecord.setIdentityName(TEST_IDENTITY_NAME);
//			cssRecord.setImID(TEST_IM_ID);
//			cssRecord.setName(TEST_NAME);
//			cssRecord.setPassword(TEST_PASSWORD);
//			cssRecord.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
//			cssRecord.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
//			cssRecord.setSocialURI(TEST_SOCIAL_URI);
// 
//			SocietiesSerialiser serialiser = new SocietiesSerialiser();
//			
//			Log.d(LOG_TAG, "Start Simple Serialise : " + System.currentTimeMillis());
//			//serialise record
//			String xmlRecord = serialiser.Write(cssRecord);
//			
//			Log.d(LOG_TAG, "End Simple Serialise : " + System.currentTimeMillis());
//
//			Log.d(LOG_TAG, "Size of XML: " + xmlRecord.length());
//
//			
//			//de-serialise record
//			CssRecord record = (CssRecord) serialiser.Read(CssRecord.class, xmlRecord);
//			Log.d(LOG_TAG, "Finish Serialise : " + System.currentTimeMillis());
//			
//			SocietiesSerialiser serialiser_1 = new SocietiesSerialiser();
//			
//			
//			Log.d(LOG_TAG, "Start Simple Serialise : " + System.currentTimeMillis());
//			//serialise record
//			String xmlRecord_1 = serialiser_1.Write(cssRecord);
//			
//			Log.d(LOG_TAG, "End Simple Serialise : " + System.currentTimeMillis());
//
//			Log.d(LOG_TAG, "Size of XML: " + xmlRecord_1.length());
//
//			
//			//de-serialise record
//			CssRecord record_1 = (CssRecord) serialiser_1.Read(CssRecord.class, xmlRecord_1);
//			Log.d(LOG_TAG, "Finish Serialise : " + System.currentTimeMillis());
//
//			
//	        assertEquals(cssRecord.getCssHostingLocation(), record.getCssHostingLocation());		
//	        assertEquals(cssRecord.getCssIdentity(), record.getCssIdentity());		
//	        assertEquals(cssRecord.getCssInactivation(), record.getCssInactivation());		
//	        assertEquals(cssRecord.getCssRegistration(), record.getCssRegistration());		
//	        assertEquals(cssRecord.getCssUpTime(), record.getCssUpTime());		
//	        assertEquals(cssRecord.getDomainServer(), record.getDomainServer());		
//	        assertEquals(cssRecord.getEmailID(), record.getEmailID());		
//	        assertEquals(cssRecord.getEntity(), record.getEntity());		
//	        assertEquals(cssRecord.getForeName(), record.getForeName());		
//	        assertEquals(cssRecord.getHomeLocation(), record.getHomeLocation());		
//	        assertEquals(cssRecord.getIdentityName(), record.getIdentityName());		
//	        assertEquals(cssRecord.getImID(), record.getImID());		
//	        assertEquals(cssRecord.getName(), record.getName());		
//	        assertEquals(cssRecord.getPassword(), record.getPassword());		
//	        assertEquals(cssRecord.getPresence(), record.getPresence());		
//	        assertEquals(cssRecord.getSex(), record.getSex());		
//	        assertEquals(cssRecord.getSocialURI(), record.getSocialURI());		
//	        assertEquals(cssRecord.getStatus(), record.getStatus());		
//	        assertEquals(cssRecord.getArchiveCSSNodes().size(), record.getArchiveCSSNodes().size());
//	        assertEquals(cssRecord.getCssNodes().size(), record.getCssNodes().size());
//
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
}
