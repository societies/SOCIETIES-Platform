/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.cis;

import org.societies.api.cis.management.ICisRecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
class SocialDatabaseAdapter {
	private static final String DB_NAME = "societies_social.db";
	private static final String GROUP_TABLE_NAME = "groups";
	private static final String PEOPLE_TABLE_NAME = "people";
	
	private static final int DB_VERSION = 1;
	
	//DB column names. These are moved to SocialContract.Groups
//	private static final String KEY_ID = "_id"; //Key
//	private static final String KEY_NAME = "name"; //Name of the group
//	private static final String KEY_JID = "jid"; //unique JID of the group
//	private static final String KEY_OWNER = "owner"; //Owner of the group
//	private static final String KEY_CREATION_DATE = "creation_date";	
	
	
	private SQLiteDatabase db;
	private final Context context;

	private static class SocialDBOpenHelper extends SQLiteOpenHelper {
		
		//SQL query for creating the Group DB:
		private static final String GROUP_DB_CREAT = "create table " + GROUP_TABLE_NAME
				+ " (" + 
				SocialContract.Groups._ID + " integer primary key autoincrement, " +
				SocialContract.Groups.NAME + " text not null, " + 
				SocialContract.Groups.DISPLAY_NAME + " text not null, " + 
				SocialContract.Groups.OWNER_ID + " text not null, " +
				SocialContract.Groups.CREATION_DATE + "text not null);";
		//TODO: Need the same for other DBs, e.g. people.

		
		/**
		 * @param context
		 * @param name
		 * @param factory
		 * @param version
		 */
		public SocialDBOpenHelper(Context _context, String _name,
				CursorFactory _factory, int _version) {
		    super(_context, _name, _factory, _version);
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(GROUP_DB_CREAT);
			//TODO: Do the same for all other tables.
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			// Drop the old table:
			_db.execSQL("drop table if exists " + GROUP_TABLE_NAME);
			//TODO: Do the same for all other tables.
			// Create a new table:
			onCreate(_db);
		}
	}
	private SocialDBOpenHelper dbHelper;
	
	public SocialDatabaseAdapter(Context _context){
		this.context = _context;
		dbHelper = new SocialDBOpenHelper(context, DB_NAME, null, DB_VERSION);
	}
	
	public void closeDB(){
		db.close();
	}
	
	public void openDB() throws SQLiteException {
		try{
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex){
			db = dbHelper.getReadableDatabase();
		}
	}
	

	/**
	 * Insert a row with CIS data into the database.
	 * @param _community
	 * @return
	 */
	public long insertCis(ICisRecord _cis){
		// Create a new row of values to insert: 
		ContentValues newValues = new ContentValues();
		newValues.put(SocialContract.Groups._ID, _cis.getCisId());
		newValues.put(SocialContract.Groups.NAME, _cis.getName());
		newValues.put(SocialContract.Groups.OWNER_ID, _cis.getOwnerId());
		newValues.put(SocialContract.Groups.DISPLAY_NAME, _cis.getUserDefineName());
//		newValues.put(KEY_CREATION_DATE, _cis.getCreationDate());
		//Insert the row:
		return db.insert(GROUP_TABLE_NAME, null, newValues);
	}
	
	/**
	 * Remove a CIS from the database.
	 * @param _rowIndex
	 * @return
	 */
	public boolean deleteCis(long _rowIndex){
		return db.delete(GROUP_TABLE_NAME, SocialContract.Groups._ID + "=" + _rowIndex, null) > 0;
	}
	
	/**
	 * Update the name of an existing CIS.
	 * @param _rowIndex
	 * @param _name
	 * @return
	 */
	public boolean updateCis(long _rowIndex, String _name){
		ContentValues newValues = new ContentValues();
		newValues.put(SocialContract.Groups.NAME, _name);
		return db.update(GROUP_TABLE_NAME, newValues, 
			SocialContract.Groups._ID + "=" + _rowIndex, null) >0;
	}
	
	/**
	 * Return a cursor for all of the CISs in the
	 * database.
	 * 
	 * @return
	 */
	public Cursor getAllCisCursor(){
		return db.query(GROUP_TABLE_NAME, 
				new String[]{
			SocialContract.Groups._ID,
			SocialContract.Groups.NAME, 
			SocialContract.Groups.OWNER_ID,
			SocialContract.Groups.DISPLAY_NAME }, 
					null, null, null, null, null);
	}
	
	public Cursor setCursorToCis(long _rowIndex) throws SQLException {
		Cursor result = db.query(true, GROUP_TABLE_NAME,
			new String[] {SocialContract.Groups._ID, 
			SocialContract.Groups.DISPLAY_NAME},
			SocialContract.Groups._ID + "=" + _rowIndex, 
			null, null, null, null, null);
		
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("No CISs found for row: " + _rowIndex);
		}
		return result;
	}
	
	public ICisRecord getCis(long _rowIndex) throws SQLException {
		Cursor cursor = db.query(true, 
				GROUP_TABLE_NAME, 
				new String[] {
			SocialContract.Groups._ID,
			SocialContract.Groups.NAME, 
			SocialContract.Groups.OWNER_ID,
			SocialContract.Groups.DISPLAY_NAME},
			SocialContract.Groups._ID + "=" + _rowIndex,
			null, null, null, null, null);
		
		if((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLException("No groups found for row: " + _rowIndex);
		}
		
		String name = cursor.getString(1);
		String cisId = cursor.getString(2);
		String owner = cursor.getString(3);
		String creationDate = cursor.getString(4);
		ICisRecord community = new CisRecord(cisId,name,owner,creationDate);
		return community;
	}
}
