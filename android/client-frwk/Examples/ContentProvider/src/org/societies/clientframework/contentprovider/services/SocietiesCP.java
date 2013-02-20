package org.societies.clientframework.contentprovider.services;


import org.societies.android.api.internal.contentproviders.CredentialTable;
import org.societies.android.api.internal.contentproviders.CssNodeTable;
import org.societies.android.api.internal.contentproviders.CssNodesTable;
import org.societies.android.api.internal.contentproviders.CssProfileTable;
import org.societies.android.api.internal.contentproviders.IContentProvider;
import org.societies.android.api.internal.contentproviders.ServiceTable;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


public class SocietiesCP extends ContentProvider implements IContentProvider {
	
	 /*
     * Defines a handle to the database helper object. The MainDatabaseHelper class is defined
     * in a following snippet.
     */
    private MyDatabaseHelper mOpenHelper;
    public static final String 	BASE_PATH		 = "content://"+ AUTHORITY;
    public static final Uri 	CONTENT_URI 	 = Uri.parse(BASE_PATH); 				// URI for access the Content Provider 
    
    public static final String 	DATABASE_NAME 		= "societies.db";
	public static final int 	DATABASE_VERSION 	= 1;
    
    private static final UriMatcher sURIMatcher;
    
    private static final int CREDENTIAL_ID = 1;
    private static final int SERVICE_ID    = 2;
    private static final int PROFILE_CSS   = 3;
    private static final int NODE_CSS	   = 4;
    
	
	// Holds the database object
    private SQLiteDatabase db;
	
    
    static {
    	sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sURIMatcher.addURI(AUTHORITY, CredentialTable.TABLE_NAME, CREDENTIAL_ID);
		sURIMatcher.addURI(AUTHORITY, ServiceTable.TABLE_NAME, 	  SERVICE_ID);
		sURIMatcher.addURI(AUTHORITY, CssProfileTable.TABLE_NAME, PROFILE_CSS);
		sURIMatcher.addURI(AUTHORITY, CssNodeTable.TABLE_NAME, 	  NODE_CSS);
		
		// Add other URI to be user to select more tables....
		// .....
	}
    
	@Override
	public synchronized int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		
	
		int count;
        switch (sURIMatcher.match(uri)) {
        case CREDENTIAL_ID:
            count = db.delete(CredentialTable.TABLE_NAME, where, whereArgs);
            break;

        case SERVICE_ID:
            count = db.delete(ServiceTable.TABLE_NAME, where, whereArgs);
            break;
            
        case PROFILE_CSS:
        	count = db.delete(CssProfileTable.TABLE_NAME, where, whereArgs);
        	break;
        
        case NODE_CSS:
        	count = db.delete(CssNodesTable.TABLE_NAME_NODES, where, whereArgs);
        	
        	break;
        	
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

       
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		 switch (sURIMatcher.match(uri)) {
	      
		    case CREDENTIAL_ID:
	            return BASE_PATH + "/" + CredentialTable.TABLE_NAME;

	        case SERVICE_ID:
	             return BASE_PATH + "/" + ServiceTable.TABLE_NAME;

	        case PROFILE_CSS:
	        	return BASE_PATH + "/" + CssProfileTable.TABLE_NAME;
	        	
	        case NODE_CSS:
	        	return BASE_PATH + "/" + CssNodeTable.TABLE_NAME;
	             
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	}

	@Override
	public synchronized Uri insert(Uri uri, ContentValues values) {
		
		
		db = mOpenHelper.getWritableDatabase();
		long rowId=0;
		
		switch(sURIMatcher.match(uri)){
		case CREDENTIAL_ID:// Set the table
				rowId = db.insert(CredentialTable.TABLE_NAME, null, values);
			break;
			case SERVICE_ID:// Set the table
				rowId = db.insert(ServiceTable.TABLE_NAME, null, values);
			break;
				
			case PROFILE_CSS: 
				rowId = db.insert(CssProfileTable.TABLE_NAME, null, values);
			break;
			case NODE_CSS:
				rowId = db.insert(CssNodeTable.TABLE_NAME, null, values);
			
			break;
			
			default:  throw new SQLException("Failed to insert row into " + uri);
		}
        
		
        if (rowId > 0) {
            Uri resultURI = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(resultURI, null);
            return resultURI;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		
	    mOpenHelper =  new MyDatabaseHelper(getContext());
        return true;
        
	}

	@Override
	public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		db = mOpenHelper.getWritableDatabase();
		
		Log.v("SocietiesCP", "Query "+uri.toString());
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	
		
		// Select the table where to add data
		switch(sURIMatcher.match(uri)){
			case CREDENTIAL_ID:// Set the table
				qb.setTables(CredentialTable.TABLE_NAME);
				Log.v("SocietiesCP", "Set Table "+CredentialTable.TABLE_NAME);
			break;
			
			case SERVICE_ID:// Set the table
				qb.setTables(ServiceTable.TABLE_NAME);
				Log.v("SocietiesCP", "Set Table "+ServiceTable.TABLE_NAME);
			break;
			
			case NODE_CSS: 
				 qb.setTables(CssNodeTable.TABLE_NAME);
				 Log.v("SocietiesCP", "Set Table "+CssNodeTable.TABLE_NAME);
			break;
			
			case PROFILE_CSS:
				
				 qb.setTables(CssProfileTable.TABLE_NAME);
				 Log.v("SocietiesCP", "Set Table "+CssProfileTable.TABLE_NAME);
			break;
			
			
		}
		
		// Apply the query to the underlying database. 
		
	    return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		
		
		
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int uriType = sURIMatcher.match(uri);
		db = mOpenHelper.getWritableDatabase();
		
		
		int count = 0;
		switch (uriType) {
		case CREDENTIAL_ID:
			count = db.update(CredentialTable.TABLE_NAME, values, where, whereArgs);
			break;
		case SERVICE_ID:
			count = db.update(ServiceTable.TABLE_NAME, values, where, whereArgs);
			
		case NODE_CSS:
			count = db.update(CssNodeTable.TABLE_NAME, values, where, whereArgs);
			
		case PROFILE_CSS:
			count = db.update(CssProfileTable.TABLE_NAME, values, where, whereArgs);
		
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
		
	}
	
	
	
	
	/**
	 * Helper class that actually creates and manages the provider's underlying data repository.
	 */
	protected static final class MyDatabaseHelper extends SQLiteOpenHelper {

	    /*
	     * Instantiates an open helper for the provider's SQLite data repository
	     * Do not do database creation and upgrade here.
	     */
	    MyDatabaseHelper(Context context){
	        super(context, DATABASE_NAME, null, 1);
	    }

	    /*
	     * Creates the data repository. This is called when the provider attempts to open the
	     * repository and SQLite reports that it doesn't exist.
	     */
	    public void onCreate(SQLiteDatabase db) {
	    	
	        db.execSQL(CredentialTable.CREATE_CREDENTIAL_TABLE);
	        db.execSQL(ServiceTable.CREATE_SERVICE_TABLE);
	        db.execSQL(CssNodeTable.CREATE_CSS_NODE_TABLE);
	        db.execSQL(CssProfileTable.CREATE_CSS_PROFILE_TABLE);
	        
	    }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("SocietieCP", "Upgrading database from version " + oldVersion + " to " +
								   newVersion + ", which will destroy all old data");  
			
			onCreate(db);
		}
		
		
		
	}
	
	
	

}
