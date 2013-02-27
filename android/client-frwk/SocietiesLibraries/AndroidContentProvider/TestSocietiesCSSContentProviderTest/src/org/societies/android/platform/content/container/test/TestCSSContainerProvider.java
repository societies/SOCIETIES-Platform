package org.societies.android.platform.content.container.test;
import java.util.ArrayList;

import org.societies.android.api.contentproviders.CSSContentProvider;
import org.societies.android.api.internal.cssmanager.CSSManagerEnums;
import org.societies.android.platform.content.CssRecordDAO;
import org.societies.android.platform.content.ProviderImplementation;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;

import android.content.ContentProvider;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class TestCSSContainerProvider extends ProviderTestCase2<ProviderImplementation> {
	private final static String LOG_TAG = TestCSSContainerProvider.class.getName();

	private static final String PROVIDER_AUTHORITY = "org.societies.android.platform.content.androidcssmanager";
	private final static String SOCIETIES_DATABASE_NAME = "TestSocietiesAndroidClient";
	private final static int SOCIETIES_DATABASE_VERSION = 1;
	
	private final static String CURRENT_NODE_1_IDENTITY = "alan@societies.bespoke/android1";
	private final static String CURRENT_NODE_2_IDENTITY = "alan@societies.bespoke/rich1";
	private final static String CURRENT_NODE_3_IDENTITY = "alan.societies.bespoke";
	
	private final static String ARCHIVED_NODE_1_IDENTITY = "alan@societies.bespoke/android11";
	private final static String ARCHIVED_NODE_2_IDENTITY = "alan@societies.bespoke/rich1222";
	
	public static final String TEST_IDENTITY = "alan.socities.local";
	public static final String TEST_INACTIVE_DATE = "20121029";
    public static final String TEST_REGISTERED_DATE = "20120229";
    public static final int TEST_UPTIME = 7799;
    public static final String TEST_EMAIL = "somebody@tssg.org";
    public static final String TEST_FORENAME = "4Name";
    public static final String TEST_HOME_LOCATION = "The Hearth";
    public static final String TEST_HOSTING_LOCATION = "Dublin";
    public static final String TEST_IDENTITY_NAME = "Id Name";
    public static final String TEST_IM_ID = "somebody.tssg.org";
    public static final String TEST_NAME = "The CSS";
    public static final String TEST_PASSWORD = "P455W0RD";
    public static final String TEST_SOCIAL_URI = "sombody@fb.com";
    public static final String TEST_DOMAIN_SERVER = "societies.bespoke";

    //Preferences
	private static final String USER_VALUE = "paranoid";
	private static final String XMPP_SERVER = "societies.bespoke";
	private static final String DOMAIN_AUTHORITY_VALUE = "daNode.societies.bespoke";
	private static final String NODE_JID_RESOURCE = "Nexus403";

	CssRecordDAO cssDAO;
	
	public TestCSSContainerProvider() {
		super(ProviderImplementation.class, PROVIDER_AUTHORITY);
	}

	protected void setUp() throws Exception {
		super.setUp();
		//Create shared preferences for later use
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(CSSContentProvider.CssPreferences.CSS_USER_PREFERENCE, USER_VALUE);
		editor.putString(CSSContentProvider.CssPreferences.CSS_XMPP_SERVER, XMPP_SERVER);
		editor.putString(CSSContentProvider.CssPreferences.CSS_DOMAIN_AUTHORITY, DOMAIN_AUTHORITY_VALUE);
		editor.putString(CSSContentProvider.CssPreferences.CSS_CURRENT_NODE_JID, NODE_JID_RESOURCE);
		
		editor.commit();

        ContentProvider provider = getProvider();
        assertNotNull(provider);

		//populate test database
		this.cssDAO = new CssRecordDAO(getMockContext());
		this.cssDAO.insertCSSRecord(getCssRecord());
		
	}

	protected void tearDown() throws Exception {
		this.cssDAO.updateCSSRecord(new CssRecord());

		super.tearDown();
	}

	@MediumTest
	public void testUseIllegalUri() throws Exception {
        MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
        try {
            resolver.query(CSSContentProvider.CONTENT_URI, null, null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
        	assertEquals("Unknown URI: content://org.societies.android.platform.content.androidcssmanager", e.getMessage());
        }
	}
	
	@MediumTest
	public void testGetCssRecord() throws Exception {
        MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
        String columns [] = {CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY, 
        						CSSContentProvider.CssRecord.CSS_RECORD_DOMAIN_SERVER,
        						CSSContentProvider.CssRecord.CSS_RECORD_NAME};
        try {
            Cursor cursor = resolver.query(CSSContentProvider.CssRecord.CONTENT_URI, columns, null, null, null);
            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());
            assertEquals(columns.length, cursor.getColumnCount());

            cursor.moveToFirst();
            assertEquals(TEST_IDENTITY, cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY)));
            assertEquals(TEST_DOMAIN_SERVER, cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_DOMAIN_SERVER)));
            assertEquals(TEST_NAME, cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_NAME)));
            cursor.close();
        } catch (IllegalArgumentException e) {
            fail();
        }
	}
	@MediumTest
	public void testCurrentCssNodes() throws Exception {
        MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
        String columns [] = {CSSContentProvider.CssNodes.CSS_NODE_IDENTITY, 
        						CSSContentProvider.CssNodes.CSS_NODE_STATUS,
        						CSSContentProvider.CssNodes.CSS_NODE_TYPE};
		try {
            Cursor cursor = resolver.query(CSSContentProvider.CssNodes.CONTENT_URI, columns, null, null, null);
            assertNotNull(cursor);
            assertEquals(3, cursor.getCount());
            assertEquals(columns.length, cursor.getColumnCount());
            if (cursor.moveToFirst()) {
            	do {
            		Log.d(LOG_TAG, "Node identity: " + cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssNodes.CSS_NODE_IDENTITY)));
            		Log.d(LOG_TAG, "Node status: " + cursor.getInt(cursor.getColumnIndex(CSSContentProvider.CssNodes.CSS_NODE_STATUS)));
            		Log.d(LOG_TAG, "Node type: " + cursor.getInt(cursor.getColumnIndex(CSSContentProvider.CssNodes.CSS_NODE_TYPE)));
            	} while (cursor.moveToNext());
            }
            cursor.close();

        } catch (IllegalArgumentException e) {
            fail();
        }
	}
	@MediumTest
	public void testArchivedCssNodes() throws Exception {
        MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
        String columns [] = {CSSContentProvider.CssNodes.CSS_NODE_IDENTITY, 
        						CSSContentProvider.CssNodes.CSS_NODE_STATUS,
        						CSSContentProvider.CssNodes.CSS_NODE_TYPE};
		try {
            Cursor cursor = resolver.query(CSSContentProvider.CssArchivedNodes.CONTENT_URI, columns, null, null, null);
            assertNotNull(cursor);
            assertEquals(columns.length, cursor.getColumnCount());
            assertEquals(2, cursor.getCount());
            if (cursor.moveToFirst()) {
            	do {
            		Log.d(LOG_TAG, "Archived Node identity: " + cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssNodes.CSS_NODE_IDENTITY)));
            		Log.d(LOG_TAG, "Archived Node status: " + cursor.getInt(cursor.getColumnIndex(CSSContentProvider.CssNodes.CSS_NODE_STATUS)));
            		Log.d(LOG_TAG, "Archived Node type: " + cursor.getInt(cursor.getColumnIndex(CSSContentProvider.CssNodes.CSS_NODE_TYPE)));
            	} while (cursor.moveToNext());
            }
            cursor.close();

        } catch (IllegalArgumentException e) {
            fail();
        }
	}
	//Cannot be tested as MockContext fails at getPackageName()
//	@MediumTest
//	public void testPreferences() throws Exception {
//        MockContentResolver resolver = getMockContentResolver();
//        assertNotNull(resolver);
//        
//    	String columns [] = {CSSContentProvider.CssPreferences.CSS_USER_PREFERENCE,
//				CSSContentProvider.CssPreferences.CSS_XMPP_SERVER,
//				CSSContentProvider.CssPreferences.CSS_DOMAIN_AUTHORITY,
//				CSSContentProvider.CssPreferences.CSS_CURRENT_NODE_JID};
//		try {
//            Cursor cursor = resolver.query(CSSContentProvider.CssPreferences.CONTENT_URI, columns, null, null, null);
//            assertNotNull(cursor);
//            assertEquals(columns.length, cursor.getColumnCount());
//            assertEquals(1, cursor.getCount());
//            cursor.moveToFirst();
//            assertEquals(USER_VALUE, cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssPreferences.CSS_USER_PREFERENCE)));
//            assertEquals(XMPP_SERVER, cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssPreferences.CSS_XMPP_SERVER)));
//            assertEquals(DOMAIN_AUTHORITY_VALUE, cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssPreferences.CSS_DOMAIN_AUTHORITY)));
//            assertEquals(NODE_JID_RESOURCE, cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssPreferences.CSS_CURRENT_NODE_JID)));
//            cursor.close();
//
//        } catch (IllegalArgumentException e) {
//            fail();
//        }
//	}

	/**
	 * Get a populated CssRecord
	 * 
	 * @return {@link CssRecord}
	 */
	private CssRecord getCssRecord() {
		CssNode currentNode_1 = new CssNode(); 
		currentNode_1.setIdentity(CURRENT_NODE_1_IDENTITY);
		currentNode_1.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		currentNode_1.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
		CssNode currentNode_2 = new CssNode(); 
		currentNode_2.setIdentity(CURRENT_NODE_2_IDENTITY);
		currentNode_2.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		currentNode_2.setType(CSSManagerEnums.nodeType.Rich.ordinal());
		
		CssNode currentNode_3 = new CssNode(); 
		currentNode_3.setIdentity(CURRENT_NODE_3_IDENTITY);
		currentNode_3.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		currentNode_3.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
		
		ArrayList<CssNode> currentNodes = new ArrayList<CssNode>();
		currentNodes.add(currentNode_1);
		currentNodes.add(currentNode_2);
		currentNodes.add(currentNode_3);
		
		CssNode archivedNode_1 = new CssNode(); 
		archivedNode_1.setIdentity(ARCHIVED_NODE_1_IDENTITY);
		archivedNode_1.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		archivedNode_1.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
		CssNode archivedNode_2 = new CssNode(); 
		archivedNode_2.setIdentity(ARCHIVED_NODE_2_IDENTITY);
		archivedNode_2.setStatus(CSSManagerEnums.nodeStatus.Unavailable.ordinal());
		archivedNode_2.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		ArrayList<CssNode> archivedNodes = new ArrayList<CssNode>();
		archivedNodes.add(archivedNode_1);
		archivedNodes.add(archivedNode_2);
		
		CssRecord cssRecord = new CssRecord();
		cssRecord.setArchiveCSSNodes(archivedNodes);
		cssRecord.setCssNodes(currentNodes);
		cssRecord.setCssIdentity(TEST_IDENTITY);
//		cssRecord.setCssHostingLocation(TEST_HOSTING_LOCATION);
		cssRecord.setDomainServer(TEST_DOMAIN_SERVER);
		cssRecord.setEmailID(TEST_EMAIL);
		cssRecord.setEntity(CSSManagerEnums.entityType.Person.ordinal());
		cssRecord.setForeName(TEST_FORENAME);
		cssRecord.setHomeLocation(TEST_HOME_LOCATION);
//		cssRecord.setIdentityName(TEST_IDENTITY_NAME);
//		cssRecord.setImID(TEST_IM_ID);
		cssRecord.setName(TEST_NAME);
		cssRecord.setPassword(TEST_PASSWORD);
//		cssRecord.setPresence(CSSManagerEnums.presenceType.Available.ordinal());
		cssRecord.setSex(CSSManagerEnums.genderType.Male.ordinal());
//		cssRecord.setSocialURI(TEST_SOCIAL_URI);
//		cssRecord.setStatus(CSSManagerEnums.cssStatus.Active.ordinal());
		
		return cssRecord;
	}
}
