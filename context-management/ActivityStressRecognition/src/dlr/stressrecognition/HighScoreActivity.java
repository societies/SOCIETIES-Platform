package dlr.stressrecognition;

import dlr.stressrecognition.utils.DBAdapter;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * Highscore for the CWT.
 * 
 * @author Michael Gross
 *
 */
public class HighScoreActivity extends ListActivity  {
	private DBAdapter dbHelper;
	private Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscore);
		this.getListView().setDividerHeight(2);
		dbHelper = new DBAdapter(this);
		dbHelper.open();
		fillData();
	}
	
	private void fillData() {
		cursor = dbHelper.fetchAllHighscores();
		startManagingCursor(cursor);

		String[] from = new String[] { DBAdapter.KEY_NAME, DBAdapter.KEY_SCORE };
		int[] to = new int[] { R.id.name, R.id.score};

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.highscore_row, cursor, from, to);
		setListAdapter(notes);
	}
}
