package org.societies.android.api.internal.cssmanager;

import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.schema.cssmanagement.CssRecord;

import android.os.Parcel;
import android.test.AndroidTestCase;


public class TestAndroidCSSRecord extends AndroidTestCase{

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

	private AndroidCSSNode cssNode_1, cssNode_2;
	private AndroidCSSNode cssNodes [];
	private AndroidCSSNode cssArchivedNodes [];
	
	protected void setUp() throws Exception {
		super.setUp();
		cssNode_1 = new AndroidCSSNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new AndroidCSSNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
		cssNodes = new AndroidCSSNode[2];
		cssNodes[0] = cssNode_1;
		cssNodes[1] = cssNode_2;
		
		cssArchivedNodes = new AndroidCSSNode[2];
		cssArchivedNodes[0] = cssNode_1;
		cssArchivedNodes[1] = cssNode_2;
	}

	protected void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssNodes = null;
		cssArchivedNodes = null;
		
		super.tearDown();
	}


	public void testConstructor() {
		AndroidCSSRecord cssProfile = new AndroidCSSRecord();
		cssProfile.setCSSNodes(cssNodes);
		cssProfile.setArchiveCSSNodes(cssArchivedNodes);
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
		cssProfile.setPassword(TEST_PASSWORD);
		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setSocialURI(TEST_SOCIAL_URI);
		
		
		assertEquals(cssArchivedNodes.length, cssProfile.getArchivedCSSNodes().length);
		assertEquals(cssArchivedNodes[0].getIdentity(), cssProfile.getArchivedCSSNodes()[0].getIdentity());
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
		assertEquals(TEST_INACTIVE_DATE, cssProfile.getCssInactivation());
		assertEquals(cssNodes.length, cssProfile.getCSSNodes().length);
		assertEquals(cssNodes[0].getIdentity(), cssProfile.getCSSNodes()[0].getIdentity());
		assertEquals(TEST_REGISTERED_DATE, cssProfile.getCssRegistration());
		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), cssProfile.getStatus());
		assertEquals(TEST_UPTIME, cssProfile.getCssUpTime());
		assertEquals(TEST_EMAIL, cssProfile.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), cssProfile.getEntity());
		assertEquals(TEST_FORENAME, cssProfile.getForeName());
		assertEquals(TEST_HOME_LOCATION, cssProfile.getHomeLocation());
		assertEquals(TEST_IDENTITY_NAME, cssProfile.getIdentityName());
		assertEquals(TEST_IM_ID, cssProfile.getImID());
		assertEquals(TEST_NAME, cssProfile.getName());
		assertEquals(TEST_PASSWORD, cssProfile.getPassword());
		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), cssProfile.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
		assertEquals(TEST_SOCIAL_URI, cssProfile.getSocialURI());
	}

	public void testArrays() {
		AndroidCSSRecord cssProfile = new AndroidCSSRecord();
		assertEquals(0, cssProfile.getArchivedCSSNodes().length);
		assertEquals(0, cssProfile.getCSSNodes().length);
	}
	
	public void testConversion() {
		CssRecord cssProfile = new CssRecord();
		
		cssProfile.getCssNodes().add(cssNode_1);
		cssProfile.getCssNodes().add(cssNode_2);
		cssProfile.getArchiveCSSNodes().add(cssNode_1);
		cssProfile.getArchiveCSSNodes().add(cssNode_2);
		
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setCssInactivation(TEST_INACTIVE_DATE);
		cssProfile.setCssRegistration(TEST_REGISTERED_DATE);
		cssProfile.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		cssProfile.setCssUpTime(TEST_UPTIME);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setHomeLocation(TEST_HOME_LOCATION);
		cssProfile.setIdentityName(TEST_IDENTITY_NAME);
		cssProfile.setImID(TEST_IM_ID);
		cssProfile.setName(TEST_NAME);
		cssProfile.setPassword(TEST_PASSWORD);
		cssProfile.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssProfile.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssProfile.setSocialURI(TEST_SOCIAL_URI);
		
		
		assertEquals(2, cssProfile.getArchiveCSSNodes().size());
		assertEquals(TEST_IDENTITY, cssProfile.getCssIdentity());
		assertEquals(TEST_INACTIVE_DATE, cssProfile.getCssInactivation());
		assertEquals(2, cssProfile.getCssNodes().size());
		assertEquals(TEST_REGISTERED_DATE, cssProfile.getCssRegistration());
		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), cssProfile.getStatus());
		assertEquals(TEST_UPTIME, cssProfile.getCssUpTime());
		assertEquals(TEST_EMAIL, cssProfile.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), cssProfile.getEntity());
		assertEquals(TEST_FORENAME, cssProfile.getForeName());
		assertEquals(TEST_HOME_LOCATION, cssProfile.getHomeLocation());
		assertEquals(TEST_IDENTITY_NAME, cssProfile.getIdentityName());
		assertEquals(TEST_IM_ID, cssProfile.getImID());
		assertEquals(TEST_NAME, cssProfile.getName());
		assertEquals(TEST_PASSWORD, cssProfile.getPassword());
		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), cssProfile.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), cssProfile.getSex());
		assertEquals(TEST_SOCIAL_URI, cssProfile.getSocialURI());
		
		AndroidCSSRecord aRecord = AndroidCSSRecord.convertCssRecord(cssProfile);
		
		assertEquals(2, aRecord.getArchivedCSSNodes().length);
		assertEquals(TEST_IDENTITY, aRecord.getCssIdentity());
		assertEquals(TEST_INACTIVE_DATE, aRecord.getCssInactivation());
		assertEquals(2, aRecord.getCSSNodes().length);
		assertEquals(TEST_REGISTERED_DATE, aRecord.getCssRegistration());
		assertEquals(CSSManagerEnums.cssStatus.Active.ordinal(), aRecord.getStatus());
		assertEquals(TEST_UPTIME, aRecord.getCssUpTime());
		assertEquals(TEST_EMAIL, aRecord.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), aRecord.getEntity());
		assertEquals(TEST_FORENAME, aRecord.getForeName());
		assertEquals(TEST_HOME_LOCATION, aRecord.getHomeLocation());
		assertEquals(TEST_IDENTITY_NAME, aRecord.getIdentityName());
		assertEquals(TEST_IM_ID, aRecord.getImID());
		assertEquals(TEST_NAME, aRecord.getName());
		assertEquals(TEST_PASSWORD, aRecord.getPassword());
		assertEquals(CSSManagerEnums.presenceType.Available.ordinal(), aRecord.getPresence());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), aRecord.getSex());
		assertEquals(TEST_SOCIAL_URI, aRecord.getSocialURI());

	}
	public void testParcelable() {
		AndroidCSSRecord cssRecord = new AndroidCSSRecord();
		assertNotNull(cssRecord);
		
		cssRecord.getCssNodes().add(cssNode_1);
		cssRecord.getCssNodes().add(cssNode_2);
		cssRecord.getArchiveCSSNodes().add(cssNode_1);
		cssRecord.getArchiveCSSNodes().add(cssNode_2);
		
		cssRecord.setCssIdentity(TEST_IDENTITY);
		cssRecord.setCssInactivation(TEST_INACTIVE_DATE);
		cssRecord.setCssRegistration(TEST_REGISTERED_DATE);
		cssRecord.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		cssRecord.setCssUpTime(TEST_UPTIME);
		cssRecord.setEmailID(TEST_EMAIL);
		cssRecord.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssRecord.setForeName(TEST_FORENAME);
		cssRecord.setHomeLocation(TEST_HOME_LOCATION);
		cssRecord.setIdentityName(TEST_IDENTITY_NAME);
		cssRecord.setImID(TEST_IM_ID);
		cssRecord.setName(TEST_NAME);
		cssRecord.setPassword(TEST_PASSWORD);
		cssRecord.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssRecord.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		cssRecord.setSocialURI(TEST_SOCIAL_URI);

		
		assertEquals(0, cssRecord.describeContents());
		
        Parcel parcel = Parcel.obtain();
        cssRecord.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        AndroidCSSRecord createFromParcel = AndroidCSSRecord.CREATOR.createFromParcel(parcel);
       
        assertEquals(cssRecord.getCssHostingLocation(), createFromParcel.getCssHostingLocation());		
        assertEquals(cssRecord.getCssIdentity(), createFromParcel.getCssIdentity());		
        assertEquals(cssRecord.getCssInactivation(), createFromParcel.getCssInactivation());		
        assertEquals(cssRecord.getCssRegistration(), createFromParcel.getCssRegistration());		
        assertEquals(cssRecord.getCssUpTime(), createFromParcel.getCssUpTime());		
        assertEquals(cssRecord.getDomainServer(), createFromParcel.getDomainServer());		
        assertEquals(cssRecord.getEmailID(), createFromParcel.getEmailID());		
        assertEquals(cssRecord.getEntity(), createFromParcel.getEntity());		
        assertEquals(cssRecord.getForeName(), createFromParcel.getForeName());		
        assertEquals(cssRecord.getHomeLocation(), createFromParcel.getHomeLocation());		
        assertEquals(cssRecord.getIdentityName(), createFromParcel.getIdentityName());		
        assertEquals(cssRecord.getImID(), createFromParcel.getImID());		
        assertEquals(cssRecord.getName(), createFromParcel.getName());		
        assertEquals(cssRecord.getPassword(), createFromParcel.getPassword());		
        assertEquals(cssRecord.getPresence(), createFromParcel.getPresence());		
        assertEquals(cssRecord.getSex(), createFromParcel.getSex());		
        assertEquals(cssRecord.getSocialURI(), createFromParcel.getSocialURI());		
        assertEquals(cssRecord.getStatus(), createFromParcel.getStatus());		
        assertEquals(cssRecord.getArchivedCSSNodes().length, createFromParcel.getArchivedCSSNodes().length);
        assertEquals(cssRecord.getCSSNodes().length, createFromParcel.getCSSNodes().length);
	}
}
