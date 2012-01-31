package org.societies.clientframework.contentprovider.database;

import org.societies.clientframework.contentprovider.Constants;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class TableOneCursor extends SQLiteCursor {
	
	public static enum SortBy{
		lastAccess,
		status
	}
	
	
	/**
	 * Costruttore della classe JobsCursors
	 * @param db
	 * @param driver
	 * @param editTable
	 * @param query
	 */
	private TableOneCursor(SQLiteDatabase db, 
					   	  SQLiteCursorDriver driver, 
					   	  String editTable, 
					   	  SQLiteQuery query) 
	{
		super(db, driver, editTable, query);
	}
	
	
	
	
	public static class Factory implements SQLiteDatabase.CursorFactory{
		
		
		public Cursor newCursor(SQLiteDatabase db,
								SQLiteCursorDriver driver, 
								String editTable,
								SQLiteQuery query) {
			
			return new TableOneCursor(db, driver, editTable, query);
		}
	}
	
	
	
	
	public String getKey()
	{
		return getString(getColumnIndexOrThrow(Constants.TABLE_KEY));
		
	}
	
	public String getValue()
	{
		
		return getString(getColumnIndexOrThrow(Constants.TABLE_VALUE));
		
	}
	
	
	public String getService()
	{
		
		return getString(getColumnIndexOrThrow(Constants.TABLE_SERVICE));
		
	}
	
	public String getType(){
		return getString(getColumnIndexOrThrow(Constants.TABLE_TYPE));
	}

	
	
	
}
