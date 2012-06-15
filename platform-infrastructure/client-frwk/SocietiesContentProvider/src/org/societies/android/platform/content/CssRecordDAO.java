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

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Data Access Object class for maintaining CSSRecord
 * 
 *
 */
public class CssRecordDAO {
	
	private final static String SOCIETIES_DATABASE_NAME = "SocietiesAndroidClient";
	private final static int SOCIETIES_DATABASE_VERSION = 1;
	
	private DBHelper dbHelper;
	private Context context;

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
	
	public void insertRow(AndroidCSSRecord record) {
		SQLiteDatabase database = this.openWriteable();
		
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

		long rowid = database.insert(DBHelper.CSS_RECORD_TABLE, null, values);
		
		
		AndroidCSSNode nodes [] = record.getCSSNodes();
		
		for (AndroidCSSNode node : nodes) {
			values = new ContentValues();
			
			values.put(DBHelper.CSS_NODE_IDENTITY, node.getIdentity());
			values.put(DBHelper.CSS_NODE_STATUS, node.getStatus());
			values.put(DBHelper.CSS_NODE_TYPE, node.getType());
			values.put(DBHelper.CSS_NODE_RECORD, rowid);
			
			database.insert(DBHelper.CURRENT_NODE_TABLE, null, values);
		}
		AndroidCSSNode archivedNodes [] = record.getArchivedCSSNodes();
		
		for (AndroidCSSNode node : archivedNodes) {
			values = new ContentValues();
			
			values.put(DBHelper.CSS_NODE_IDENTITY, node.getIdentity());
			values.put(DBHelper.CSS_NODE_STATUS, node.getStatus());
			values.put(DBHelper.CSS_NODE_TYPE, node.getType());
			values.put(DBHelper.CSS_NODE_RECORD, rowid);
			
			database.insert(DBHelper.ARCHIVED_NODE_TABLE, null, values);
		}
	}

}
