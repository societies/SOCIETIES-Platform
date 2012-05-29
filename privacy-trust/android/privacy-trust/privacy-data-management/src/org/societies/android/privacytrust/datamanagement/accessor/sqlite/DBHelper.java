package org.societies.android.privacytrust.datamanagement.accessor.sqlite;

import org.societies.android.privacytrust.datamanagement.accessor.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	
	public DBHelper(Context context) {
    	super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCreateTableCIS = "CREATE TABLE "+Constants.TABLE_PRIVACY_PERMISSION+" ("
		+" requestorId TEXT, "
		+" subRequestorId TEXT, "
		+" permissionType TEXT, "
		+" ownerId TEXT, "
		+" dataId TEXT, "
		+" actions TEXT, "
		+" decision TEXT "
		+");";
		db.execSQL(sqlCreateTableCIS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+Constants.TABLE_PRIVACY_PERMISSION+";");
		onCreate(db);
	}
	
	public SQLiteDatabase getDbWrite() {
		return this.getWritableDatabase();
	}
	
	@Deprecated
	public SQLiteDatabase getDbRead() {
		return this.getReadableDatabase();
	}
}
