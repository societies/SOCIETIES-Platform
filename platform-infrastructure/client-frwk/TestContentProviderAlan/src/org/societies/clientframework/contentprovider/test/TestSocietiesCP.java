package org.societies.clientframework.contentprovider.test;


import org.societies.clientframework.contentprovider.services.SocietiesCP;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class TestSocietiesCP extends ProviderTestCase2<SocietiesCP> {
 
	
	
	private static final String AUTHORITY = "org.societies.android.platform.contentprovider";
	// Database table
	public static final String CREDENTIAL_TABLE_NAME		= "credential";

	public static final String CREDENTIAL_KEY_ID 			 = "_id";
	public static final String CREDENTIAL_KEY_USERNAME 		 = "username";
	public static final String CREDENTIAL_KEY_PASSWORD 		 = "password" ;
	public static final String CREDENTIAL_KEY_SERVICE 	  	 = "service";
	public static final String CREDENTIAL_KEY_FIRSTNAME 	 = "firstname";
	public static final String CREDENTIAL_KEY_LASTNAME	 	 = "lastname";
	public static final String CREDENTIAL_KEY_COMPANY	 	 = "company";
	public static final String CREDENTIAL_KEY_APPLICATION 	 = "app";
	public static final String CREDENTIAL_KEY_TOKEN		 	 = "token";
	public static final String CREDENTIAL_KEY_USEPROXY 		 = "useproxy";
	public static final String CREDENTIAL_KEY_PROXY_URL		 = "proxy_url";
	
	
	public static String CREDENTIAL_DATA_URI		 =	"content://"+ AUTHORITY + "/" + CREDENTIAL_TABLE_NAME;

	public TestSocietiesCP() {
		super(SocietiesCP.class, AUTHORITY);
		
	}
	// Database table
	public static final String TABLE_NAME		 = "service_data";
	public static final String KEY_ID 			 = "_ID";
	public static final String KEY_SERVICE 		 = "service";
	public static final String KEY_NAME 		 = "key";
	public static final String KEY_VALUE      	 = "value" ;
	
	
	public static String SERVICE_DATA_URI		 =	"content://" + AUTHORITY + "/" + TABLE_NAME;
	
	
	// Database creation SQL statement
	public static final String CREATE_SERVICE_TABLE = 
			"CREATE TABLE " 
			+ TABLE_NAME
			+ "(" 
			+ KEY_ID 		+ " integer primary key autoincrement, " 
			+ KEY_SERVICE 	+ " text not null, " 
			+ KEY_NAME	 	+ " text not null, " 
			+ KEY_VALUE		+ " text not null " 
			+ ");";

	

	protected void setUp() throws Exception {
		super.setUp();	
		
		
		Uri credentialUri = Uri.parse(CREDENTIAL_DATA_URI);
		ContentProviderClient client = getContext().getContentResolver().acquireContentProviderClient(credentialUri);
		Log.v("TestContentProvider", "Start test ");
		
	}
	
	
	

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	@SmallTest
	public void testOnCreate() {
		Uri credentialUri = Uri.parse(CREDENTIAL_DATA_URI);
		ContentProviderClient client = getContext().getContentResolver().acquireContentProviderClient(credentialUri);
		assertNotNull(client);
		
	}

	@SmallTest
	public void testDeleteUriStringStringArray() {
		Uri credentialUri = Uri.parse(CREDENTIAL_DATA_URI);
		int count = getContext().getContentResolver().delete(credentialUri, "service = ?", new String[]{"test"});
		assertNotNull(count);
	}

	@SmallTest
	public void testGetTypeUri() {
		Uri serUri = Uri.parse(SERVICE_DATA_URI); 
		String uri = getContext().getContentResolver().getType(serUri);
		assertNotNull(uri);
	}

	@SmallTest
	public void testInsertUriContentValues() {
		Uri credentialUri = Uri.parse(CREDENTIAL_DATA_URI);
		ContentValues data = new ContentValues();
		data.put(CREDENTIAL_KEY_APPLICATION, "test1");
		data.put(CREDENTIAL_KEY_COMPANY,     "TI");
		data.put(CREDENTIAL_KEY_FIRSTNAME,   "AAAA");
		data.put(CREDENTIAL_KEY_LASTNAME,    "BBBBB");
		data.put(CREDENTIAL_KEY_PASSWORD,    "CCCCc");
		data.put(CREDENTIAL_KEY_PROXY_URL,   "");
		data.put(KEY_SERVICE,     "XXXXX");
		data.put(CREDENTIAL_KEY_TOKEN,       "");
		data.put(CREDENTIAL_KEY_USEPROXY,    false);
		data.put(CREDENTIAL_KEY_USERNAME,    "zzzzzzz");
		Uri res = getContext().getContentResolver().insert(credentialUri, data);
		assertNotNull(res);
	}

	@SmallTest
	public void testQueryUriStringArrayStringStringArrayString() {
		Uri credentialUri = Uri.parse(CREDENTIAL_DATA_URI);
		Cursor c = getContext().getContentResolver().query(credentialUri,null,null,null,null);
		assertNotNull(c);
	}

	@SmallTest
	public void testUpdateUriContentValuesStringStringArray() {
		Uri credentialUri = Uri.parse(CREDENTIAL_DATA_URI);
		
		ContentValues data = new ContentValues();
		data.put(CREDENTIAL_KEY_APPLICATION, "test3");
		data.put(CREDENTIAL_KEY_COMPANY, "TI");
		data.put(CREDENTIAL_KEY_FIRSTNAME, "AAAA");
		data.put(CREDENTIAL_KEY_LASTNAME, "BBBBB");
		data.put(CREDENTIAL_KEY_PASSWORD, "CCCCc");
		data.put(CREDENTIAL_KEY_PROXY_URL, "");
		data.put(KEY_SERVICE, "XXXXX");
		data.put(CREDENTIAL_KEY_TOKEN, "");
		data.put(CREDENTIAL_KEY_USEPROXY, false);
		data.put(CREDENTIAL_KEY_USERNAME, "zzzzzzz");
		int c = getContext().getContentResolver().update(credentialUri, data, CREDENTIAL_KEY_COMPANY +" = ?", new String[]{"TI"});
		assertNotNull(c);
	}

}
