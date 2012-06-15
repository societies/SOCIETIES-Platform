package org.societies.android.platform.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class to create Societies Client Database
 * and manage updates to tables
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	
	public final static String CSS_RECORD_TABLE = "CssRecord";
	public final static String CURRENT_NODE_TABLE = "CssNode";
	public final static String ARCHIVED_NODE_TABLE = "ArchivedCssNode";
	
	public final static String ROW_ID = "_id";
	
	public final static String CSS_NODE_IDENTITY = "identity";
	public final static String CSS_NODE_TYPE = "type";
	public final static String CSS_NODE_STATUS = "status";
	public final static String CSS_NODE_RECORD = "record";

	public final static String CSS_RECORD_DOMAIN_SERVER = "domainServer";
	public final static String CSS_RECORD_CSS_HOSTING_LOCATION = "cssHostingLocation";
	public final static String CSS_RECORD_ENTITY = "entity";
	public final static String CSS_RECORD_FORENAME = "foreName";
	public final static String CSS_RECORD_NAME = "name";
	public final static String CSS_RECORD_IDENTITY_NAME = "identityName";
	public final static String CSS_RECORD_PASSWORD = "password";
	public final static String CSS_RECORD_EMAILID = "emailID";
	public final static String CSS_RECORD_IMID = "imID";
	public final static String CSS_RECORD_SOCIALURI = "socialURI";
	public final static String CSS_RECORD_SEX = "sex";
	public final static String CSS_RECORD_HOME_LOCATION = "homeLocation";
	public final static String CSS_RECORD_CSS_IDENTITY = "cssIdentity";
	public final static String CSS_RECORD_STATUS = "status";
	public final static String CSS_RECORD_REGISTRATION = "cssRegistration";
	public final static String CSS_RECORD_INACTIVATION = "cssInactivation";
	public final static String CSS_RECORD_UPTIME = "cssUpTime";
	

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	/**
	 * Create all database tables required for app
	 */
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE " + CURRENT_NODE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"identity TEXT, " +
				"status INTEGER," +
				"type INTEGER," +
				"record INTEGER, " +
				"FOREIGN KEY (record) REFERENCES CssRecord(_id));");		
		
		db.execSQL("CREATE TABLE " + ARCHIVED_NODE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"identity TEXT, " +
				"status INTEGER," +
				"type INTEGER," +
				"record INTEGER," +
				"FOREIGN KEY (record) REFERENCES CssRecord(_id));");		
		
		db.execSQL("CREATE TABLE " + CSS_RECORD_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				  "domainServer TEXT, " + 
				  "cssHostingLocation TEXT, " + 
				  "entity INTEGER, " + 
				  "foreName TEXT, " + 
				  "name TEXT, " + 
				  "identityName TEXT, " + 
				  "password TEXT, " + 
				  "emailID TEXT, " + 
				  "imID TEXT, " + 
				  "socialURI TEXT, " + 
				  "sex INTEGER, " + 
				  "homeLocation TEXT, " + 
				  "cssIdentity TEXT, " + 
				  "status INTEGER, " + 
				  "cssRegistration TEXT, " + 
				  "cssInactivation TEXT, " + 
				  "cssUpTime INTEGER);");
	}

	@Override
	/**
	 * Used for upgrading the existing database tables in the event of modifications
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
