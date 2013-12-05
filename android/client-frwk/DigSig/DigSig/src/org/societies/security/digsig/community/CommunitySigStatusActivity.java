package org.societies.security.digsig.community;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.societies.security.digsig.apiinternal.Community;
import org.societies.security.digsig.sign.R;
import org.societies.security.digsig.sign.R.id;
import org.societies.security.digsig.sign.R.layout;
import org.societies.security.digsig.sign.R.menu;
import org.societies.security.digsig.sign.R.string;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommunitySigStatusActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private static final String TAG = CommunitySigStatusActivity.class.getSimpleName();
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private List<String> documentTitles = new ArrayList<String>();
	private List<String> downloadUris = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_community_sig_status);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	}
	
	@Override
	protected void onResume() {

		super.onResume();

		clearDownloadUris();
		store("title-1", "http://192.168.1.92:8080/rest/xmldocs/foo.xml?sig=5F068B13A9184C4773809426EAB845A99A2258724887CA67475B263283E92B7D680B39865B1622F066FB03283772AB6C69347D1400D8F8CEE5CCC4F5DE80FD5F3B8C3272B5B7DC08EBB3BC45D11F66590DA25742193BCEF20619DCF0D8B33812424153BCFDBCDE081B48867F90BB544F593EEA9BA825C425A4A6D2650E0A17C6&operation=status");
 		store("title-2", "http://192.168.1.92/tmp/societies/test.json?sig=foo");
		store("title-3", "http://192.168.1.92/tmp/societies/test2.json?sig=foo");
		
		restore();
		Log.d(TAG, documentTitles.size() + " existing documents found");

		final ActionBar actionBar = getActionBar();

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, documentTitles), this);
	}
	
	// FIXME: remove this testing method
	private void store(String key, String value) {
		SharedPreferences preferences = getSharedPreferences(Community.Preferences.DOWNLOAD_URIS, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
		Log.d(TAG, "Stored key value pair: " + key + " = " + value);
	}

	private void clearDownloadUris() {
		
		SharedPreferences preferences = getSharedPreferences(Community.Preferences.DOWNLOAD_URIS, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
		Log.d(TAG, "Download URIs cleared");
	}

	private void restore() {
		
		SharedPreferences preferences = getSharedPreferences(Community.Preferences.DOWNLOAD_URIS, MODE_PRIVATE);
		Map<String, String> all = (Map<String, String>) preferences.getAll();
		
		documentTitles.clear();
		downloadUris.clear();

		Iterator<String> iter = all.keySet().iterator();
		String key;
		while (iter.hasNext()) {
			key = iter.next();
			documentTitles.add(key);
			downloadUris.add(all.get(key));
		}
		Log.d(TAG, "Restored " + downloadUris.size() + " URIs");
	}

	private String getDownloadUri(String documentTitle) {
		
		SharedPreferences preferences = getSharedPreferences(Community.Preferences.DOWNLOAD_URIS, MODE_PRIVATE);
		
		String uri = preferences.getString(documentTitle, null);
		Log.d(TAG, "Restored URI: " + uri);
		return uri;
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.community_sig_status, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Log.d(TAG, "position = " + position + ", id = " + id);
		new GetSigStatusTask(this).execute(downloadUris.get(position));
		return true;
	}
	
	protected void updateSigStatus(int numSigners, int minNumSigners, ArrayList<String> signers) {
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putStringArrayList(DummySectionFragment.ARG_TITLE, signers);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_TITLE = "TITLE";
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_community_sig_status_dummy, container,
					false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			
			ArrayList<String> signers = getArguments().getStringArrayList(ARG_TITLE);
			String signersStr = "";
			for (String s : signers) {
				signersStr += s + System.getProperty("line.separator");
			}
			dummyTextView.setText(signersStr);
			
			return rootView;
		}
	}

}
