/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.android.platform;


import org.societies.android.api.cis.SocialContract;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * This adapter will implement a local cache of Social Data.
 * TODO: Currently it is used for testing purposes.
 * All SQLite-related code is inside here.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
public class LocalDBAdapter implements ISocialAdapter {
	//For logging:
    private static final String TAG = "LocalDBAdapter";

	//Constants for DB names and table names:
	private static final String DB_NAME = "societies_social.db";
	private static final String ME_TABLE_NAME = "me";
	private static final String COMMUNITIES_TABLE_NAME = "communities";
	private static final String MY_COMMUNITIES_TABLE_NAME = "mycommunities";
	private static final String PEOPLE_TABLE_NAME = "people";
	private static final String SERVICES_TABLE_NAME = "services";

	private static final int DB_VERSION = 1;

	private SQLiteDatabase db;
	private Context context;

	
	/**
	 * This takes care of setting up the DBs.
	 * 
	 * @author Babak.Farshchian@sintef.no
	 *
	 */
	private static class SocialDBOpenHelper extends SQLiteOpenHelper {
		
		//For logging:
	    private static final String TAG = "SocialDBOpenHelper";

		//SQL query for creating the Community table:
		private static final String ME_TABLE_CREATE = "create table " + ME_TABLE_NAME
				+ " (" + 
				SocialContract.Me._ID + " integer primary key autoincrement, " +
				SocialContract.Me.GLOBAL_ID + " text not null, " +
				SocialContract.Me.NAME + " text not null," +
				SocialContract.Me.DISPLAY_NAME + " text not null );";

		private static final String COMMUNITIES_TABLE_CREATE = "create table " + COMMUNITIES_TABLE_NAME
				+ " (" + 
				SocialContract.Community._ID + " integer primary key autoincrement, " +
				SocialContract.Community.GLOBAL_ID + " text not null, " +
				SocialContract.Community.TYPE + " text not null," +
				SocialContract.Community.NAME + " text not null, " + 
				SocialContract.Community.DISPLAY_NAME + " text not null, " + 
				SocialContract.Community.OWNER_ID + " text not null, " +
				SocialContract.Community.CREATION_DATE + " text not null, " +
				SocialContract.Community.MEMBERSHIP_TYPE + " text not null, " +
				SocialContract.Community.DIRTY + " text not null );";

		private static final String MY_COMMUNITIES_TABLE_CREATE = "create table " + MY_COMMUNITIES_TABLE_NAME
				+ " (" + 
				SocialContract.MyCommunity._ID + " integer primary key autoincrement, " +
				SocialContract.MyCommunity.GLOBAL_ID + " text not null, " +
				SocialContract.MyCommunity.OWNER_ID + " text not null, " +
				SocialContract.MyCommunity.DISPLAY_NAME + " text not null );";

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
			//onUpgrade(_db, 1, 1);
			_db.execSQL(ME_TABLE_CREATE);
			android.util.Log.d(TAG, ": Me table created");
			_db.execSQL(MY_COMMUNITIES_TABLE_CREATE);
			android.util.Log.d(TAG, ": My communities table created");
			_db.execSQL(COMMUNITIES_TABLE_CREATE);
			android.util.Log.d(TAG, ": Communities table created");
			//TODO: Do the same for all other tables.
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			// Drop the old table:
			_db.execSQL("drop table if exists " + COMMUNITIES_TABLE_NAME);
			_db.execSQL("drop table if exists " + ME_TABLE_NAME);
			_db.execSQL("drop table if exists " + MY_COMMUNITIES_TABLE_NAME);
			//TODO: Do the same for all other tables.
			// Create a new table:
			onCreate(_db);
		}
	}
	
	private SocialDBOpenHelper dbHelper;

	public LocalDBAdapter(Context _context){
		context = _context;
		dbHelper = new SocialDBOpenHelper(context, DB_NAME, null, DB_VERSION);
	}
	/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Do a case on uri and call proper private method:
		
		return queryCommunities(projection, selection, selectionArgs, sortOrder);
	}
	
	protected Cursor queryMe(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return db.query(ME_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);		
	}

	protected Cursor queryCommunities(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return db.query(COMMUNITIES_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
	}

	protected Cursor queryMyCommunities(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return db.query(MY_COMMUNITIES_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
	}
/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Make a case and call proper private method with values.
		
		Uri withAppendedPath = uri.withAppendedPath(uri, Long.toString(insertCommunities(values)));
		return withAppendedPath;
	}
	
	protected long insertMe(ContentValues _values){
		//TODO: We cannot have multiple Me's so this method needs to
		// update after first insert. Or use only update?
		//return Uri.withAppendedPath(SocialContract.Me.CONTENT_URI, Long.toString(db.insert(ME_TABLE_NAME, null, values)));
		//Never insert me always update me on row 0:
		return db.insert(ME_TABLE_NAME, null, _values);
		//return Uri.withAppendedPath(SocialContract.Me.CONTENT_URI, Long.toString(updateMe(_values, SocialContract.Me._ID + "= 0", null)));
	}
	
	protected long insertCommunities(ContentValues values) {
		// TODO Auto-generated method stub
		return db.insert(COMMUNITIES_TABLE_NAME, null, values);
		//return uri.withAppendedPath(uri, Long.toString(rowNumber));
		
	}
	
	protected long insertMyCommunity(ContentValues values) {
		return db.insert(MY_COMMUNITIES_TABLE_NAME, null, values);
		//return uri.withAppendedPath(uri, Long.toString(rowNumber));
	}
		protected long insertPerson(ContentValues values) {
		// TODO Auto-generated method stub
		return db.insert(PEOPLE_TABLE_NAME, null, values);
		
	}

	protected long insertService(ContentValues values) {
		// TODO Auto-generated method stub
		return db.insert(SERVICES_TABLE_NAME, null, values);
		
	}
	/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	

	protected int updateMe(ContentValues values) {
		//Always update first row.
		//TODO: Fix this.
		db = dbHelper.getWritableDatabase();
		return db.update(ME_TABLE_NAME, values, SocialContract.Me._ID + "=1", null);
	}

	protected int updateMyCommunities(ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		db = dbHelper.getWritableDatabase();
		return db.update(MY_COMMUNITIES_TABLE_NAME, values, selection, selectionArgs);
	}

	protected int updateCommunities(ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		db = dbHelper.getWritableDatabase();
		return db.update(COMMUNITIES_TABLE_NAME, values, selection, selectionArgs);
	}

	/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Total delete. Delete table and create again. This gives
	 * index 1 to Me.
	 * 
	 * @return
	 */
	protected int deleteMe(){
		db.execSQL("drop table if exists " + ME_TABLE_NAME);
		db.execSQL("create table " + ME_TABLE_NAME
				+ " (" + 
				SocialContract.Me._ID + " integer primary key autoincrement, " +
				SocialContract.Me.GLOBAL_ID + " text not null, " +
				SocialContract.Me.NAME + " text not null," +
				SocialContract.Me.DISPLAY_NAME + " text not null );");
		return db.delete(ME_TABLE_NAME, null, null);
	}

	protected int deleteMyCommunities(String _selection, String[] _selectionArgs){
		return db.delete(COMMUNITIES_TABLE_NAME, _selection, _selectionArgs);
	}
/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#isOnline()
	 */
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int connect(){
		try{
			db = dbHelper.getWritableDatabase();
			return 1;
		} catch (SQLiteException ex){
			return android.util.Log.e("XXXXXXXXXXXXXXX", ex.getMessage());
		}

	}
	public int disconnect(){
		db.close();
		return 1;
	}

	/* 
	 * This method calls connect() and discards username and password.
	 * Local DB does not need username and password. 
	 * 
	 * (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#connect(java.lang.String, java.lang.String)
	 */
	@Override
	public int connect(String username, String password) {
		// TODO Auto-generated method stub
		return connect();
	}
}
