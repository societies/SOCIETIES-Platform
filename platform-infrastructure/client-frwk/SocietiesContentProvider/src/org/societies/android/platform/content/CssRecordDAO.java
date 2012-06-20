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


import org.societies.android.api.internal.cssmanager.AndroidCSSNode;
import org.societies.android.api.internal.cssmanager.AndroidCSSRecord;
import org.societies.utilities.DBC.Dbc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Data Access Object class for maintaining CSSRecord
 * 
 *
 */
public class CssRecordDAO {
	private static final String LOG_TAG = CssRecordDAO.class.getName();
	
	public final static String SOCIETIES_DATABASE_NAME = "SocietiesAndroidClient";
	public final static int SOCIETIES_DATABASE_VERSION = 1;
	
	private DBHelper dbHelper;
	private Context context;
	private long cssRowId = 0;
	/**
	 * Public constructor
	 * DBHelper class takes care of creating database if it does not exist
	 * 
	 * @param context
	 */
	public CssRecordDAO(Context context) {
		this.context = context;
		this.dbHelper = new DBHelper(context, SOCIETIES_DATABASE_NAME, null, SOCIETIES_DATABASE_VERSION);
	}

	/**
	 * Get a writable database instance
	 * 
	 * @throws SQLException
	 * @returns SQLiteDatabase writeable database
	 */
	public SQLiteDatabase openWriteable() throws SQLException {
		
		return dbHelper.getWritableDatabase();
	}
	/**
	 * Get a readable database instance
	 * 
	 * @throws SQLException
	 * @returns SQLiteDatabase readable database
	 */
	public SQLiteDatabase openReadable() throws SQLException {
		
		return dbHelper.getReadableDatabase();
	}
	/**
	 * Close the database instance
	 */
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Has the CSS Record already been stored
	 * 
	 * @return boolean true if record already exists
	 */
	public boolean cssRecordExists() {
		int recordCount = 0;
		boolean retValue = false;
		
		SQLiteDatabase database = this.openReadable();

		String columns [] = new String [1];
		columns[0] = DBHelper.ROW_ID;
		
		Cursor cursor = database.query(DBHelper.CSS_RECORD_TABLE, columns, null, null, null, null, null);
		recordCount = cursor.getCount();

		if (recordCount >= 1) {
			Dbc.invariant("Can only be one CSSRecord row", 1 == recordCount);
			retValue = true;
		}
		
		cursor.close();
		
		this.close();
		
		return retValue;
	}
	
	/**
	 * Read the CSS Record
	 * 
	 * @return AndroidCSSRecord if database contains CSSRecord, null otherwise
	 */
	public AndroidCSSRecord readCSSrecord() {
		AndroidCSSRecord record  = null;
		
		if (this.cssRecordExists()) {
			record  = new AndroidCSSRecord();
			
			SQLiteDatabase database = this.openReadable();
			
			Cursor cursor = database.query(DBHelper.CSS_RECORD_TABLE, DBHelper.ALL_CSSRECORD_COLUMNS, null, null, null, null, null);
			cursor.moveToFirst();
			
			record.setCssHostingLocation(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_CSS_HOSTING_LOCATION)));
			record.setCssIdentity(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_CSS_IDENTITY)));
			record.setCssInactivation(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_INACTIVATION)));
			record.setCssRegistration(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_REGISTRATION)));
			record.setCssUpTime(cursor.getInt(cursor.getColumnIndex(DBHelper.CSS_RECORD_UPTIME)));
			record.setDomainServer(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_DOMAIN_SERVER)));
			record.setEmailID(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_EMAILID)));
			record.setEntity(cursor.getInt(cursor.getColumnIndex(DBHelper.CSS_RECORD_ENTITY)));
			record.setForeName(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_FORENAME)));
			record.setHomeLocation(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_HOME_LOCATION)));
			record.setIdentityName(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_IDENTITY_NAME)));
			record.setImID(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_IMID)));
			record.setName(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_NAME)));
			record.setPassword(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_PASSWORD)));
			record.setSex(cursor.getInt(cursor.getColumnIndex(DBHelper.CSS_RECORD_SEX)));
			record.setSocialURI(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_RECORD_SOCIALURI)));
			record.setStatus(cursor.getInt(cursor.getColumnIndex(DBHelper.CSS_RECORD_STATUS)));

			cursor.close();
			
			record.setCSSNodes(readNodes(DBHelper.CURRENT_NODE_TABLE, false, database));
			record.setArchiveCSSNodes(readNodes(DBHelper.ARCHIVED_NODE_TABLE, false, database));
			
			Dbc.invariant("Can only be one CSSRecord row", 1 == cursor.getCount());
			
			this.close();
		}
		
		return record;
		
	}
	/**
	 * Read AndroidCSSNodes from relevant table
	 * 
	 * @param table
	 * @param openDatabase database open required
	 * @param database existing database
	 * @return
	 */
	public AndroidCSSNode [] readNodes(String table, boolean openDatabase, SQLiteDatabase database) {
		Dbc.require("Valid Node table required", table.equals(DBHelper.CURRENT_NODE_TABLE) || table.equals(DBHelper.ARCHIVED_NODE_TABLE));
		Dbc.require("Database instance cannot be null", null != database);
		
		SQLiteDatabase dbInstance = null;
		
		if (openDatabase) {
			dbInstance = this.openReadable();
		} else {
			dbInstance = database;
		}

		Cursor cursor = dbInstance.query(table, DBHelper.ALL_CSSNODE_COLUMNS, null, null, null, null, null);

		AndroidCSSNode node [] = new AndroidCSSNode [cursor.getCount()];
		
		cursor.moveToFirst();
		
		Log.d(LOG_TAG, "Number of CSSNode record(s): " + cursor.getCount());
		
		for (int i = 0; i < cursor.getCount(); i++) {
			node[i] = new AndroidCSSNode();
			
			node[i].setIdentity(cursor.getString(cursor.getColumnIndex(DBHelper.CSS_NODE_IDENTITY)));
			node[i].setStatus(cursor.getInt(cursor.getColumnIndex(DBHelper.CSS_NODE_STATUS)));
			node[i].setType(cursor.getInt(cursor.getColumnIndex(DBHelper.CSS_NODE_TYPE)));
			
			cursor.moveToNext();
		}
		cursor.close();

		if (openDatabase) {
			this.close();			
		}
		
		return node;
	}
	
	/**
	 * Update CSRecord
	 * 
	 * @param record
	 * @return boolean true id original CSSrecord found
	 */
	public boolean updateCSSRecord(AndroidCSSRecord record) {
		boolean retValue = false;
		
		if (this.cssRecordExists()) {
			retValue = true;

			SQLiteDatabase database = this.openWriteable();
			
			String whereValues [] = new String [1];
			whereValues[0] = Long.toString(this.cssRowId);
			
			database.update(DBHelper.CSS_RECORD_TABLE, this.populateCSSRecord(record), DBHelper.ROW_ID + " = ?"  , whereValues);
			
			this.updateNodes(record, database);
			
			this.close();
		}

		
		return retValue;
	}
	
	private void updateNodes(AndroidCSSRecord record, SQLiteDatabase database) {
		Dbc.require("AndroidCSSRecord cannnot be null", null != record);
		Dbc.require("Database instance cannot be null", null != database);
		
		//Remove all current record(s)
		database.delete(DBHelper.CURRENT_NODE_TABLE, null, null);
		//Remove all current record(s)
		database.delete(DBHelper.ARCHIVED_NODE_TABLE, null, null);
		
		this.insertNodes(record, database);

	}
	
	/**
	 * Insert the CSSRecord 
	 * 
	 * @param record AndroidCSSRecord object
	 * @return boolean true if CSSRecord table is empty
	 */
	public boolean insertCSSRecord(AndroidCSSRecord record) {
		boolean retValue = false;
		
		if (!this.cssRecordExists()) {
			retValue = true;
			SQLiteDatabase database = this.openWriteable();
			

			this.cssRowId = database.insert(DBHelper.CSS_RECORD_TABLE, null, this.populateCSSRecord(record));
			
			this.insertNodes(record, database);
			
			this.close();
			
		}
		return retValue;
		
	}
	
	/**
	 * Insert nodes into Node tables
	 * 
	 * @param record
	 * @param database
	 */
	private void insertNodes(AndroidCSSRecord record, SQLiteDatabase database) {
		AndroidCSSNode nodes [] = record.getCSSNodes();
		
		for (AndroidCSSNode node : nodes) {
			
			database.insert(DBHelper.CURRENT_NODE_TABLE, null, populateCSSNode(node, cssRowId));
		}
		AndroidCSSNode archivedNodes [] = record.getArchivedCSSNodes();
		
		for (AndroidCSSNode node : archivedNodes) {
			
			database.insert(DBHelper.ARCHIVED_NODE_TABLE, null, populateCSSNode(node, cssRowId));
		}

	}

	/**
	 * Populate ContentValues with relevant data
	 * 
	 * @param record
	 * @return ContentValues
	 */
	private ContentValues populateCSSRecord(AndroidCSSRecord record) {
		ContentValues values = new ContentValues();
		
		values.put(DBHelper.CSS_RECORD_CSS_HOSTING_LOCATION, record.getCssHostingLocation());
		values.put(DBHelper.CSS_RECORD_CSS_IDENTITY, record.getCssIdentity());
		values.put(DBHelper.CSS_RECORD_DOMAIN_SERVER, record.getDomainServer());
		values.put(DBHelper.CSS_RECORD_EMAILID, record.getEmailID());
		values.put(DBHelper.CSS_RECORD_ENTITY, record.getEntity());
		values.put(DBHelper.CSS_RECORD_FORENAME, record.getForeName());
		values.put(DBHelper.CSS_RECORD_HOME_LOCATION, record.getHomeLocation());
		values.put(DBHelper.CSS_RECORD_IDENTITY_NAME, record.getIdentityName());
		values.put(DBHelper.CSS_RECORD_IMID, record.getImID());
		values.put(DBHelper.CSS_RECORD_INACTIVATION, record.getCssInactivation());
		values.put(DBHelper.CSS_RECORD_NAME, record.getName());
		values.put(DBHelper.CSS_RECORD_PASSWORD, record.getPassword());
		values.put(DBHelper.CSS_RECORD_REGISTRATION, record.getCssRegistration());
		values.put(DBHelper.CSS_RECORD_SEX, record.getSex());
		values.put(DBHelper.CSS_RECORD_SOCIALURI, record.getSocialURI());
		values.put(DBHelper.CSS_RECORD_STATUS, record.getStatus());
		values.put(DBHelper.CSS_RECORD_UPTIME, record.getCssUpTime());
		
		return values;
	}
	/**
	 * Populate ContentValues with relevant data
	 * 
	 * @param node
	 * @param rowid
	 * @return ContentValues
	 */
	private ContentValues populateCSSNode(AndroidCSSNode node, long rowid) {
		ContentValues values = new ContentValues();

		values.put(DBHelper.CSS_NODE_IDENTITY, node.getIdentity());
		values.put(DBHelper.CSS_NODE_STATUS, node.getStatus());
		values.put(DBHelper.CSS_NODE_TYPE, node.getType());

		//only populate foreign key on add record
		if (-1 !=rowid) {
			values.put(DBHelper.CSS_NODE_RECORD, rowid);
		}

		return values;
	}
}
