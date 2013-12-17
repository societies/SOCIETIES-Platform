package org.societies.security.digsig.community;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.societies.security.digsig.apiinternal.Community;
import org.societies.security.digsig.sign.R;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CommunitySigStatusActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private static final String TAG = CommunitySigStatusActivity.class.getSimpleName();
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
//	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private List<String> documentTitles = new ArrayList<String>();
	private List<String> downloadUris = new ArrayList<String>();
	
	private static ProgressDialog mBusyDialog;
	
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
		
		restore();
		Log.d(TAG, documentTitles.size() + " existing documents found");
		
		loadActionBar();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		
		store();
	}
	
	private void loadActionBar() {

		final ActionBar actionBar = getActionBar();

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, documentTitles), this);

		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		int previouslySelected = preferences.getInt(Community.SELECTED_DOCUMENT_INDEX, 0);
		getActionBar().setSelectedNavigationItem(previouslySelected);
		Log.d(TAG, "Activity state restored. previouslySelected = " + previouslySelected);
	}
	
	private void clearDownloadUris() {
		
		SharedPreferences preferences = getSharedPreferences(Community.Preferences.DOWNLOAD_URIS, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();

		documentTitles.clear();
		downloadUris.clear();
		loadActionBar();
		
		Log.d(TAG, "Download URIs cleared");
		
//		// FIXME: remove this testing code and store(String, String) method
//		store("completed", "http://192.168.1.92/tmp/societies/test.json?sig=foo");
//		store("in progress", "http://192.168.1.92/tmp/societies/test2.json?sig=foo");
//		store("not started yet", "http://192.168.1.92/tmp/societies/non-existing.json?sig=foo");
//		store("network error", "http://192.168.1.312/invalid-ip-address.json?sig=foo");
	}

	private void store() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int selected = getActionBar().getSelectedNavigationIndex();
		editor.putInt(Community.SELECTED_DOCUMENT_INDEX, selected);
		editor.commit();
		Log.d(TAG, "Activity state stored. selected = " + selected);
	}

//	private void store(String key, String value) {
//		SharedPreferences preferences = getSharedPreferences(Community.Preferences.DOWNLOAD_URIS, MODE_PRIVATE);
//		SharedPreferences.Editor editor = preferences.edit();
//		editor.putString(key, value);
//		editor.commit();
//		Log.d(TAG, "Stored key value pair: " + key + " = " + value);
//	}

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

//	@Override
//	public void onRestoreInstanceState(Bundle savedInstanceState) {
//		
//		Log.d(TAG, "onRestoreInstanceState");
//		
//		// Restore the previously serialized current dropdown position.
//		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
//			getActionBar().setSelectedNavigationItem(
//					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
//		}
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//
//		Log.d(TAG, "onSaveInstanceState");
//
//		// Serialize the current dropdown position.
//		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
//				.getSelectedNavigationIndex());
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.community_sig_status, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
		
		if (item.getTitle().equals(getText(R.string.action_clear))) {
			clearDownloadUris();
			Log.d(TAG, "The documents list cleared");
		}
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Log.d(TAG, "position = " + position + ", id = " + id);
		
		mBusyDialog = new ProgressDialog(getActionBarThemedContextCompat());
		mBusyDialog.setMessage(getText(R.string.fetchingStatus));
		mBusyDialog.show();
		
		new GetSigStatusTask(this).execute(downloadUris.get(position));
		return true;
	}
	
	/**
	 * 
	 * @param numSigners Value extracted from downloaded document, or negative value for error
	 * @param minNumSigners Value extracted from downloaded document, or negative value for error
	 * @param signers List of signers extracted from downloaded document, or null for error
	 * @param errorMsg Localized error to be displayed to the user, or null for no error
	 */
	protected void updateSigStatus(boolean success, boolean started,
			int numSigners, int minNumSigners, ArrayList<String> signers) {
		
		Log.d(TAG, "updateSigStatus: numSigners = " + numSigners);
		Log.d(TAG, "updateSigStatus: minNumSigners = " + minNumSigners);
		Log.d(TAG, "updateSigStatus: signers = " + signers);

		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putBoolean(DummySectionFragment.ARG_SUCCESS, success);
		args.putBoolean(DummySectionFragment.ARG_STARTED, started);
		args.putInt(DummySectionFragment.ARG_NUM_SIGNERS, numSigners);
		args.putInt(DummySectionFragment.ARG_MIN_NUM_SIGNERS, minNumSigners);
		args.putStringArrayList(DummySectionFragment.ARG_SIGNERS, signers);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SUCCESS = "SUCCESS";
		public static final String ARG_STARTED = "STARTED";
		public static final String ARG_SIGNERS = "SIGNERS";
		public static final String ARG_NUM_SIGNERS = "NUM_SIGNERS";
		public static final String ARG_MIN_NUM_SIGNERS = "MIN_NUM_SIGNERS";
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			int resource;
			View rootView;
			TextView textView;
			String str;
		
			// Arguments
			int numSigners = getArguments().getInt(ARG_NUM_SIGNERS, -1);
			int minNumSigners = getArguments().getInt(ARG_MIN_NUM_SIGNERS, -1);
			ArrayList<String> signers = getArguments().getStringArrayList(ARG_SIGNERS);
			boolean success = getArguments().getBoolean(ARG_SUCCESS);
			boolean started = getArguments().getBoolean(ARG_STARTED);
			
			Log.d(TAG, "onCreateView: success = " + success);
			Log.d(TAG, "onCreateView: started = " + started);
			Log.d(TAG, "onCreateView: numSigners = " + numSigners);
			Log.d(TAG, "onCreateView: minNumSigners = " + minNumSigners);
			Log.d(TAG, "onCreateView: signers = " + signers);

			mBusyDialog.cancel();
			
			// Inflate the appropriate GUI
			if (!success) {
				resource = R.layout.fragment_community_sig_status_error;
				rootView = inflater.inflate(resource, container, false);
				return rootView;
			}
			else if (!started) {
				resource = R.layout.fragment_community_sig_status_nonexisting;
				rootView = inflater.inflate(resource, container, false);
				return rootView;
			}
			resource = R.layout.fragment_community_sig_status_existing;
			rootView = inflater.inflate(resource, container, false);

			// Display appropriate icon and main text
			textView = (TextView) rootView.findViewById(R.id.communitySigStatusMainTextView);
			if (numSigners >= minNumSigners) {
				textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ok, 0, 0, 0);
				textView.setText(R.string.signatureThresholdReached);
			}
			else {
				textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.construction, 0, 0, 0);
				textView.setText(R.string.signatureThresholdNotReached);
			}
			
			// Set progress bar progress
			ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.communitySigStatusProgressBar);
			int progress = Math.round(100 * ((float) numSigners) / minNumSigners);
			progress = Math.min(progress, 100);
			progressBar.setProgress(progress);

			// Display current number of signers
			textView = (TextView) rootView.findViewById(R.id.communitySigStatusSignedByNPartiesTextView);
			str = numSigners >= 0 ? String.valueOf(numSigners) : "?";
			textView.setText(str);
			
			// Display minimal required number of signers
			textView = (TextView) rootView.findViewById(R.id.communitySigStatusRequiredTextView);
			str = minNumSigners >= 0 ? String.valueOf(minNumSigners) : "?";
			textView.setText(str);

			// List all current signers
			textView = (TextView) rootView.findViewById(R.id.communitySigStatusCurrentSignersTextView);
			String signersStr = "";
			for (String s : signers) {
				signersStr += s + System.getProperty("line.separator");
			}
			textView.setText(signersStr);
			
			return rootView;
		}
	}

}
