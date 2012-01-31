package org.societies.clientframework.contentprovider.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.societies.android.platform.interfaces.IContentProvider;
import org.societies.clientframework.contentprovider.Constants;
import org.societies.clientframework.contentprovider.R;
import org.societies.clientframework.contentprovider.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreResultsDB extends SQLiteOpenHelper {
	
	
	private final  Context mContext;
	private String selected_table = Constants.TABLE_RESULTS;
	
	
	public StoreResultsDB(Context context){
		super(context, Settings.DATABASE_NAME, null, Settings.DATABASE_VERSION);
		this.mContext = context;
	}

	public void setTable(String table){
		Log.i(Constants.TAG, "DATABASE: Set Table "+ table);
		this.selected_table = table;
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
	
	
	public void resetDB(String dbName){
		String[] whereArgs = new String[]{};
		try{ 	
			getWritableDatabase().delete(dbName,   "", whereArgs);
		}	
		catch (SQLException e) {
			Log.e(Constants.TAG, e.toString());
		}
	}
	
	
	public void storeData(Map<String, ?>data, String serviceName){
		Iterator <String> it = data.keySet().iterator();
		
		while (it.hasNext()){
			String key = it.next();
			ContentValues map  = new ContentValues();
			map.put(Constants.TABLE_KEY, 		IContentProvider.CREDENTIAL_USERNAME);
			map.put(Constants.TABLE_SERVICE, 	IContentProvider.SERVICE_COMM_FWK);
			map.put(Constants.TABLE_TYPE, 	    data.get(key).getClass().toString());
			map.put(Constants.TABLE_VALUE, 		data.get(key).toString());
			addElementInTable(map);
		}
		
	}
	
	public Map<String, ?> getData(String serviceName){
		Map<String,Object> data = new HashMap();
		String sql = "select * from "+selected_table+" where service '"+serviceName+"'";
		SQLiteDatabase d = getReadableDatabase(); 
		ContentProviderCursor cur = (ContentProviderCursor) d.rawQueryWithFactory(new ContentProviderCursor.Factory(), sql, null, null);
		if (cur!=null){
			if (cur.getCount()>0){
				 cur.moveToFirst();
				 do{
					 data.put(cur.getKey(),translate(cur.getType(), cur.getValue()));
				 }while(!cur.moveToNext());
			}
		}
		return data;
	}
	
	
	Object translate(String type, String value){
		if (type.equals(int.class.toString())){
			return Integer.parseInt(value);
		}
		else if (type.equals(float.class.toString()))
			return Float.parseFloat(value);
		
		if (type.equals(double.class.toString()))
			return Double.parseDouble(value);
		
		if (type.equals(boolean.class.toString()))
			return Boolean.parseBoolean(value);
		
		if (type.equals(short.class.toString()))
			return Short.parseShort(value);
		
		return value;
	}
	
	
	public boolean addElementInTable(ContentValues map){
		
		try{ 
			getWritableDatabase().insert(selected_table, null, map);
			return true;
		}
		catch (SQLException e) {
			Log.e(Constants.TAG, e.toString());
		}
		return false;
	}
	
	public Cursor getElement(String key, String service){
		
		String serviceQuery="";
		if (service!=null){
			serviceQuery = " and service = '"+service+"'";
		}
		String sql = "select * from "+selected_table+" where key = '"+key+"'" + serviceQuery;
		SQLiteDatabase d = getReadableDatabase(); 
		return d.rawQueryWithFactory(new ResultsCursor.Factory(), sql, null, null);
		
	}
	
	
	
	public String[] getServices(){
		String[] services = null;
		String sql = "select DISTINCT service from " + Constants.TABLE_ONE + "";
		SQLiteDatabase d = getReadableDatabase(); 
		ContentProviderCursor toc = (ContentProviderCursor)d.rawQueryWithFactory(new ResultsCursor.Factory(), sql, null, null);
		
		if(toc.getCount()>0){
			services = new String[toc.getCount()];
			toc.moveToFirst();
			services[0] = toc.getService();
			int index=1;
			while (toc.moveToNext()){
				services[index]=toc.getService();
				index++;
			}
			
		}
		return services;
	}
	
	public Cursor getElement(String key){
		return getElement(key, null);
	}
	
	
	public String[] getKeys(){
		return getKeys(null);
	}
	
	public String[] getKeys(String service){
			
			String serviceQuery="";
			if (service!=null){
				serviceQuery = " where service = '"+service+"'";
			}
			String sql = "select * from "+ selected_table + serviceQuery;
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
		return removeKey(null);	
	}
	
	public boolean removeKey(String key, String service){
		
		
		String where="key=?";
		String[] whereArgs = new String[]{key};
		if (service!=null){
			where = " and service=?";
			whereArgs = new String[]{key, service};
		}
		
		
		try{ 	
			getWritableDatabase().delete(selected_table,  where, whereArgs);
			
		}
		catch (SQLException e) {
			Log.e(Constants.TAG, e.toString());
		}
		return false;
	}

}
