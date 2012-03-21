package dlr.stressrecognition.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * DBAdapter for easy access to DB functions
 * 
 * @author Michael Gross
 *
 */
public class DBAdapter {
	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_SCORE= "score";
	private static final String DATABASE_TABLE = "highscore";
	
	private Context context;
	private SQLiteDatabase database;
	private DBHelper dbHelper;

	public DBAdapter(Context context) {
		this.context = context;
	}

	public DBAdapter open() throws SQLException {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new Highscore
	 * If the Highscore is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long createHighscore(String name, int score) {
		
		ContentValues initialValues = createContentValues(name, score);
		Cursor mCursor = database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_NAME, KEY_SCORE}, null, null, null,
				null, KEY_SCORE + " DESC");
		
		// Only store the best 10 highscores
		if(mCursor.getCount() == 10) {
			// Check whether the lowest score so far is lower than the current score
			mCursor.moveToLast();
			int index = mCursor.getColumnIndex(KEY_SCORE);
			int oldScore = mCursor.getInt(index);
			int id = mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID));
			if(score > oldScore) {		
				return database.update(DATABASE_TABLE, initialValues, KEY_ROWID + "="
						+ id, null);
			}
		} else {
			return database.insert(DATABASE_TABLE, null, initialValues);
		}
		return -1;
	}

	/**
	 * Update the Highscore
	 */
	public boolean updateHighscore(long rowId, String name, int score) {
		ContentValues updateValues = createContentValues(name, score);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	/**
	 * Delete Highscore
	 */
	public boolean deleteHighscore(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all Highscores in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllHighscores() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_NAME, KEY_SCORE}, null, null, null,
				null, KEY_SCORE + " DESC");
	}

	/**
	 * Return a Cursor positioned at the defined Highscore
	 */
	public Cursor fetchHighscore(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_SCORE},
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String name, int score) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_SCORE, score);
		return values;
	}
}
