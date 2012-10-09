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


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This adapter will implement a local cache of Social Data.
 * TODO: Currently it is used for testing purposes.
 * All SQLite-related code is inside here.
 * 
 * @author Babak dot Farshchian at sintef dot no
 *
 */
public class LocalDBAdapter implements ISocialAdapter {
	//For logging:
    private static final String TAG = "LocalDBAdapter";


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

		/**
		 * @param context
		 * @param name
		 * @param factory
		 * @param version
		 */
		public SocialDBOpenHelper(Context _context, String _name,
				CursorFactory _factory, int _version) {
		    	super(_context, _name, _factory, _version);
		}
		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(SQLiteContract.ME_TABLE_CREATE);
			android.util.Log.d(TAG, ": Me table created");
			_db.execSQL(SQLiteContract.PEOPLE_TABLE_CREATE);
			android.util.Log.d(TAG, ": People table created");
			_db.execSQL(SQLiteContract.COMMUNITIES_TABLE_CREATE);
			android.util.Log.d(TAG, ": Communities table created");
			_db.execSQL(SQLiteContract.SERVICES_TABLE_CREATE);
			android.util.Log.d(TAG, ": Services table created");
			_db.execSQL(SQLiteContract.RELATIONSHIP_TABLE_CREATE);
			android.util.Log.d(TAG, ": Relationship table created");
			_db.execSQL(SQLiteContract.MEMBERSHIP_TABLE_CREATE);
			android.util.Log.d(TAG, ": Membership table created");
			_db.execSQL(SQLiteContract.SHARING_TABLE_CREATE);
			android.util.Log.d(TAG, ": Sharing table created");
			_db.execSQL(SQLiteContract.PEOPLE_ACTIVITIY_TABLE_CREATE);
			android.util.Log.d(TAG, ": People activity table created");
			_db.execSQL(SQLiteContract.COMMUNITIES_ACTIVITIY_TABLE_CREATE);
			android.util.Log.d(TAG, ": Communities activity table created");
			_db.execSQL(SQLiteContract.SERVICES_ACTIVITIY_TABLE_CREATE);
			android.util.Log.d(TAG, ": Services activity table created");
		}

		/* 
		 * This method currently deletes the old tables and their contents and
		 * creates new tables. It should do a real upgrade in the future.
		 * 
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			
			android.util.Log.d(TAG, ": Upgrading DB...");

			// Drop the old table:
			_db.execSQL("drop table if exists " + SQLiteContract.ME_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.PEOPLE_ACTIVITIY_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.COMMUNITIES_ACTIVITIY_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.SERVICES_ACTIVITIY_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.RELATIONSHIP_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.MEMBERSHIP_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.SHARING_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.PEOPLE_ACTIVITIY_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.COMMUNITIES_ACTIVITIY_TABLE_NAME);
			_db.execSQL("drop table if exists " + SQLiteContract.SERVICES_ACTIVITIY_TABLE_NAME);
			// Create a new table:
			onCreate(_db);
		}
	}
	
	private SocialDBOpenHelper dbHelper;

	public LocalDBAdapter(Context _context){
		context = _context;
		dbHelper = new SocialDBOpenHelper(context, SQLiteContract.DB_NAME, null, SQLiteContract.DB_VERSION);
	}
	
	/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#insertPeople(android.content.ContentValues)
	 */
	public long insertPeople(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.PEOPLE_TABLE_NAME, null, _values);	
	}

	public Cursor queryPeople(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder) {
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.PEOPLE_ACTIVITIY_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}

	public int updatePeople(ContentValues values, String selection,
			String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.PEOPLE_TABLE_NAME, values, selection, selectionArgs);
	}

	public int deletePeople(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.PEOPLE_TABLE_NAME, _selection, _selectionArgs);
	}

	public long insertCommunities(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.COMMUNITIES_TABLE_NAME, null, _values);
	}

	public Cursor queryCommunities(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder) {
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.COMMUNITIES_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}

	public int updateCommunities(ContentValues _values, String _selection,
			String[] _selectionArgs) {
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.COMMUNITIES_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}

	public int deleteCommunities(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.COMMUNITIES_TABLE_NAME,
				_selection, _selectionArgs);
	}

	public long insertServices(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.SERVICES_TABLE_NAME, null, _values);	
	}

	public Cursor queryServices(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder) {
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.SERVICES_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	
	public int updateServices(ContentValues _values, String _selection,
			String[] _selectionArgs) {
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.SERVICES_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}

	public int deleteServices(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.SERVICES_TABLE_NAME, _selection, _selectionArgs);
	}

	public long insertRelationship(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.RELATIONSHIP_TABLE_NAME, null, _values);	
	}
	
	public Cursor queryRelationship(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder){
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.RELATIONSHIP_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	public int updateRelationship(ContentValues _values, String _selection,
			String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.RELATIONSHIP_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}
	public int deleteRelationship(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.RELATIONSHIP_TABLE_NAME,
				_selection, _selectionArgs);
	}
	public long insertMembership(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.MEMBERSHIP_TABLE_NAME, null, _values);	
	}
	public Cursor queryMembership(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder){
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.MEMBERSHIP_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	public int updateMembership(ContentValues _values, String _selection,
			String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.MEMBERSHIP_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}
	public int deleteMembership(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.MEMBERSHIP_TABLE_NAME,
				_selection, _selectionArgs);
	}
	public long insertSharing(ContentValues values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.SHARING_TABLE_NAME, null, values);	
	}
	public Cursor querySharing(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder){
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.SHARING_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	public int updateSharing(ContentValues _values, String _selection,
			String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.SHARING_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}
	public int deleteSharing(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.SHARING_TABLE_NAME,
				_selection, _selectionArgs);
	}
	public long insertPeopleActivity(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.PEOPLE_ACTIVITIY_TABLE_NAME, null, _values);	
	}
	public Cursor queryPeopleActivity(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder){
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.PEOPLE_ACTIVITIY_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	public int updatePeopleActivity(ContentValues _values, String _selection,
			String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.PEOPLE_ACTIVITIY_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}
	public int deletePeopleActivity(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.PEOPLE_ACTIVITIY_TABLE_NAME,
				_selection, _selectionArgs);
	}
	public long insertCommunityActivity(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.COMMUNITIES_ACTIVITIY_TABLE_NAME, null, _values);	
	}
	public Cursor queryCommunityActivity(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder){
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.COMMUNITIES_ACTIVITIY_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	public int updateCommunityActivity(ContentValues _values, String _selection,
			String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.COMMUNITIES_ACTIVITIY_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}
	public int deleteCommunityActivity(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.COMMUNITIES_ACTIVITIY_TABLE_NAME,
				_selection, _selectionArgs);
	}
	public long insertServiceActivity(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.SERVICES_ACTIVITIY_TABLE_NAME, null, _values);	
	}
	public Cursor queryServiceActivity(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder){
		db = dbHelper.getWritableDatabase();
		return db.query(SQLiteContract.SERVICES_ACTIVITIY_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	public int updateServiceActivity(ContentValues _values, String _selection,
			String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.SERVICES_ACTIVITIY_TABLE_NAME, 
				_values, _selection, _selectionArgs);
	}
	public int deleteServiceActivity(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.SERVICES_ACTIVITIY_TABLE_NAME,
				_selection, _selectionArgs);
	}
	public long insertMe(ContentValues _values) {
		db = dbHelper.getWritableDatabase();
		return db.insert(SQLiteContract.ME_TABLE_NAME, null, _values);
	}
	public Cursor queryMe(String[] _projection, String _selection,
			String[] _selectionArgs, String _sortOrder) {
		db = dbHelper.getReadableDatabase();
		return db.query(SQLiteContract.ME_TABLE_NAME, 
				_projection, _selection, _selectionArgs, null, null, _sortOrder);
	}
	public int updateMe(ContentValues _values, String _selection,
			String[] _selectionArgs) {
		db = dbHelper.getWritableDatabase();
		return db.update(SQLiteContract.ME_TABLE_NAME, _values, _selection, _selectionArgs);
	}
	public int deleteMe(String _selection, String[] _selectionArgs){
		db = dbHelper.getWritableDatabase();
		return db.delete(SQLiteContract.ME_TABLE_NAME, _selection, _selectionArgs);
	}

	/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#isOnline()
	 */
	public boolean isConnected() {
		try{
			db = dbHelper.getWritableDatabase();
			db.close();
			return true;
		} catch (SQLiteException ex){
			
			android.util.Log.e(TAG, ex.getMessage());
			return false;
		}
	}
	
	/* 
	 * Return 1 if connection 
	 * (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#connect()
	 */
	public int connect() {
		//TODO: try not to keep the DB open. just test getWritable here. then let methods open and close.
		try{
			db = dbHelper.getWritableDatabase();
			db.close();
			return 1;
		} catch (SQLiteException ex){
			
			android.util.Log.e(TAG, ex.getMessage());
			return 0;
		}

	}
	/* 
	 * This method calls connect() and discards username and password.
	 * Local DB does not need username and password. 
	 * 
	 * (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#connect(java.lang.String, java.lang.String)
	 */
	public int connect(String username, String password) {
		// TODO Auto-generated method stub
		return connect();
	}

	/* 
	 * Return 1 if db was open and is now closed.
	 * Return 0 if the db was already closed.
	 * (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#disconnect()
	 */
	public int disconnect(){
		if (db.isOpen()){
		db.close();
		return 1;
		}
		return 0;
	}

	/* 
	 * Tries to open the DB specified in {@link SQLiteContract}. If the DB exists
	 * returns false. If the DB does not exist return true.
	 * (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#firstRun()
	 */
	public boolean firstRun(){
		try{
			db = SQLiteDatabase.openDatabase(SQLiteContract.DB_PATH+SQLiteContract.DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
			db.close();
			return false;}
		catch (SQLiteException e){
			android.util.Log.d(TAG, ": DB does not exist, i.e. first run: "+e.getMessage());
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.android.platform.ISocialAdapter#getListOfOwnedCis(org.societies.android.platform.ISocialAdapterCallback)
	 */
	public void getListOfOwnedCis(ISocialAdapterCallback callback) {
		// TODO Auto-generated method stub
		
	}
}
