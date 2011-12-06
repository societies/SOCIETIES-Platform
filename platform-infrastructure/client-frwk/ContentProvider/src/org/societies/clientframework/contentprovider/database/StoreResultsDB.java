package org.societies.clientframework.contentprovider.database;

import java.util.ArrayList;

import org.societies.clientframework.contentprovider.Constants;
import org.societies.clientframework.contentprovider.R;
import org.societies.clientframework.contentprovider.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreResultsDB extends SQLiteOpenHelper {
	
	
	private final  Context mContext;
	
	public StoreResultsDB(Context context){
		super(context, Settings.DATABASE_NAME, null, Settings.DATABASE_VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		Log.v(Constants.TAG, "DB initialization....");
		String[] sql = mContext.getString(R.string.db_creation).split(";");
		db.beginTransaction(); 
		try {
			// Create tables and test data 
			execMultipleSQL(db, sql); 
			db.setTransactionSuccessful();
		} 
		catch (SQLException e) 
		{ 
			Log.e(Constants.TAG, e.toString());
			throw e;
			
		}
		finally{
			db.endTransaction();
		}
		Log.v(Constants.TAG, "DB initialization completed");
	}
	
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Constants.TAG, "Upgrading database from version " + oldVersion + " to " +
				newVersion + ", which will destroy all old data");
		
		String[] sql = mContext.getString(R.string.db_creation).split("\n");
		db.beginTransaction();
		
		try {
			execMultipleSQL(db, sql);
			db.setTransactionSuccessful();
		} 
		catch (SQLException e) {
			Log.e("Error upgrading tables and debug data", e.toString());
			throw e; 
		} 
		finally {}
		db.endTransaction();
		
		// This is cheating. In the real world, you'll need to add columns, not rebuild from scratch.
		onCreate(db);
		
	}
	
	
	private void execMultipleSQL(SQLiteDatabase db, String[] sql){
		for(String s: sql){
			Log.v(Constants.TAG, "Execute SQL:"+s);
			if(s.trim().length()>0) db.execSQL(s);
		}
	}
	
	
	public void resetDB(){
		String[] whereArgs = new String[]{};
		try{ 	
			getWritableDatabase().delete(Constants.TABLE_RESULTS,   "", whereArgs);
		}	
		catch (SQLException e) {
			Log.e(Constants.TAG, e.toString());
		}
	}
	
	
	public boolean addValue(String key, String value){
		ContentValues map = new ContentValues();
		map.put(Constants.TABLE_KEY, 	 key );
		map.put(Constants.TABLE_VALUE, 	 value);
		try{ 
			getWritableDatabase().insert(Constants.TABLE_RESULTS, null, map);
			Log.v(Constants.TAG, "add pair "+key + " value:" +value+ " into DB");
			return true;
		}
		catch (SQLException e) {
			Log.e(Constants.TAG, e.toString());
		}
		return false;
	}
	
	public String getValue(String key){
		String sql = "select * from "+Constants.TABLE_RESULTS+" where key = '"+key+"'";
		SQLiteDatabase d = getReadableDatabase(); 
		ResultsCursor rc = (ResultsCursor) d.rawQueryWithFactory(new ResultsCursor.Factory(), sql, null, null);
		if (rc.getCount()>0){
			rc.moveToFirst();
			return rc.getValue();
		}
		return null;
	}
	
	public String[] getKeys(){
			
			String sql = "select * from "+ Constants.TABLE_RESULTS;
			SQLiteDatabase d = getReadableDatabase(); 
	
			ResultsCursor rc = (ResultsCursor)d.rawQueryWithFactory(new ResultsCursor.Factory(), sql, null, null);
			String[]results = new String[rc.getCount()];
			Log.v(Constants.TAG, "there are "+rc.getCount() + "keys");
			int index=1;
			rc.moveToFirst();
			if (rc.getCount()>0) results[0] = rc.getKey();
			while (rc.moveToNext()){	
				results[index] =rc.getKey();
				index++;
			}
			
			return results;
		
		
	}
	
	
	public boolean removeKey(String key){
		String[] whereArgs = new String[]{key};
		try{ 	
			getWritableDatabase().delete(Constants.TABLE_RESULTS,  "key=?", whereArgs);
		}
		catch (SQLException e) {
			Log.e(Constants.TAG, e.toString());
		}
		return false;
	}

}
