/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.content;

import org.societies.android.api.contentproviders.CSSContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Helper class to create Societies Client Database
 * and manage updates to tables
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	
	public final static String CSS_RECORD_TABLE = "CssRecord";
	public final static String CURRENT_NODE_TABLE = "CssNode";
	public final static String ARCHIVED_NODE_TABLE = "ArchivedCssNode";
	public final static String USER_VCARDS_TABLE = "UserVCards";
	
	//Create constants for table columns to maintain a single reference fir each column
	public final static String ROW_ID = BaseColumns._ID;
	
	public final static String CSS_NODE_IDENTITY = CSSContentProvider.CssNodes.CSS_NODE_IDENTITY;
	public final static String CSS_NODE_TYPE = CSSContentProvider.CssNodes.CSS_NODE_TYPE;
	public final static String CSS_NODE_STATUS = CSSContentProvider.CssNodes.CSS_NODE_STATUS;
	public final static String CSS_NODE_RECORD = CSSContentProvider.CssNodes.CSS_NODE_RECORD;
	public final static String CSS_NODE_DEVICE_MAC_ADDRESS = CSSContentProvider.CssNodes.CSS_NODE_DEVICE_MAC_ADDRESS;
	public final static String CSS_NODE_INTERACTABLE = CSSContentProvider.CssNodes.CSS_NODE_INTERACTABLE;
	

	public final static String CSS_RECORD_DOMAIN_SERVER = CSSContentProvider.CssRecord.CSS_RECORD_DOMAIN_SERVER;
	public final static String CSS_RECORD_ENTITY = CSSContentProvider.CssRecord.CSS_RECORD_ENTITY;
	public final static String CSS_RECORD_FORENAME = CSSContentProvider.CssRecord.CSS_RECORD_FORENAME;
	public final static String CSS_RECORD_NAME = CSSContentProvider.CssRecord.CSS_RECORD_NAME;
	public final static String CSS_RECORD_PASSWORD = "password";
	public final static String CSS_RECORD_EMAILID = CSSContentProvider.CssRecord.CSS_RECORD_EMAILID;
	public final static String CSS_RECORD_SEX = CSSContentProvider.CssRecord.CSS_RECORD_SEX;
	public final static String CSS_RECORD_HOME_LOCATION = CSSContentProvider.CssRecord.CSS_RECORD_HOME_LOCATION;
	public final static String CSS_RECORD_CSS_IDENTITY = CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY;
	public final static String CSS_RECORD_POSITION = CSSContentProvider.CssRecord.CSS_RECORD_POSITION;
	public final static String CSS_RECORD_WORKPLACE = CSSContentProvider.CssRecord.CSS_RECORD_WORKPLACE;
	
	//AVATAR COLUMNS
	public final static String USER_IDENTITY = CSSContentProvider.UserVCards.USER_IDENTITY;
	public final static String USER_AVATAR   = CSSContentProvider.UserVCards.USER_AVATAR;
	public final static String AVATAR_HASH   = CSSContentProvider.UserVCards.AVATAR_HASH;
	
	//Create an "all columns" array. Useful for retrieving all data from a table
//	public final static String [] ALL_CSSRECORD_COLUMNS = {ROW_ID, CSS_RECORD_DOMAIN_SERVER, CSS_RECORD_CSS_HOSTING_LOCATION,
//		CSS_RECORD_ENTITY, CSS_RECORD_FORENAME, CSS_RECORD_NAME, CSS_RECORD_IDENTITY_NAME, CSS_RECORD_PASSWORD, CSS_RECORD_EMAILID,
//		CSS_RECORD_IMID, CSS_RECORD_SOCIALURI, CSS_RECORD_SEX, CSS_RECORD_HOME_LOCATION, CSS_RECORD_CSS_IDENTITY, CSS_RECORD_STATUS,
//		CSS_RECORD_REGISTRATION, CSS_RECORD_INACTIVATION, CSS_RECORD_UPTIME};
	public final static String [] ALL_CSSRECORD_COLUMNS = {ROW_ID, CSS_RECORD_DOMAIN_SERVER, CSS_RECORD_ENTITY, CSS_RECORD_FORENAME, CSS_RECORD_NAME,
														   CSS_RECORD_PASSWORD, CSS_RECORD_EMAILID, CSS_RECORD_SEX, CSS_RECORD_HOME_LOCATION,
														   CSS_RECORD_CSS_IDENTITY, CSS_RECORD_POSITION, CSS_RECORD_WORKPLACE};
	
	public final static String [] ALL_CSSNODE_COLUMNS = {ROW_ID, CSS_NODE_IDENTITY, CSS_NODE_TYPE, CSS_NODE_STATUS, CSS_NODE_RECORD, 
														CSS_NODE_DEVICE_MAC_ADDRESS, CSS_NODE_INTERACTABLE};
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	/**
	 * Create all database tables required for app
	 */
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE " + CURRENT_NODE_TABLE + " (" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				CSS_NODE_IDENTITY + " TEXT, " +
				CSS_NODE_STATUS + " INTEGER," +
				CSS_NODE_TYPE + " INTEGER," +
				CSS_NODE_RECORD + " INTEGER, " +
				CSS_NODE_DEVICE_MAC_ADDRESS + " TEXT, " +
				CSS_NODE_INTERACTABLE + " TEXT, " +
				"FOREIGN KEY (" + CSS_NODE_RECORD + ") REFERENCES CssRecord(" + ROW_ID + "));");		
		
		db.execSQL("CREATE TABLE " + ARCHIVED_NODE_TABLE + " (" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				CSS_NODE_IDENTITY + " TEXT, " +
				CSS_NODE_STATUS + " INTEGER," +
				CSS_NODE_TYPE + " INTEGER," +
				CSS_NODE_RECORD + " INTEGER, " +
				CSS_NODE_DEVICE_MAC_ADDRESS + " TEXT, " +
				CSS_NODE_INTERACTABLE + " TEXT, " +
				"FOREIGN KEY (" + CSS_NODE_RECORD + ") REFERENCES CssRecord(" + ROW_ID + "));");		
		
		db.execSQL("CREATE TABLE " + CSS_RECORD_TABLE + " (" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				CSS_RECORD_DOMAIN_SERVER + " TEXT, " + 
				CSS_RECORD_ENTITY + " INTEGER, " + 
				CSS_RECORD_FORENAME + " TEXT, " + 
				CSS_RECORD_NAME + " TEXT, " + 
				CSS_RECORD_PASSWORD + " TEXT, " + 
				CSS_RECORD_EMAILID + " TEXT, " + 
				CSS_RECORD_SEX + " INTEGER, " + 
				CSS_RECORD_HOME_LOCATION + " TEXT, " + 
				CSS_RECORD_CSS_IDENTITY + " TEXT, " + 
				CSS_RECORD_POSITION + " TEXT , " + 
				CSS_RECORD_WORKPLACE + " TEXT );" 
				);
		
		//AVATAR STORAGE
		db.execSQL("CREATE TABLE " + USER_VCARDS_TABLE + " (" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				USER_IDENTITY + " TEXT, " +
				AVATAR_HASH   + " TEXT  );");
	}

	@Override
	/**
	 * Used for upgrading the existing database tables in the event of modifications
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
