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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
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
	
	private static ProgressDialog mBusyDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_community_sig_status);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

//		clearDownloadUris();
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
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
	protected void updateSigStatus(int numSigners, int minNumSigners, ArrayList<String> signers, String errorMsg) {
		
		Log.d(TAG, "updateSigStatus: numSigners = " + numSigners);
		Log.d(TAG, "updateSigStatus: minNumSigners = " + minNumSigners);
		Log.d(TAG, "updateSigStatus: signers = " + signers);

		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_NUM_SIGNERS, numSigners);
		args.putInt(DummySectionFragment.ARG_MIN_NUM_SIGNERS, minNumSigners);
		args.putStringArrayList(DummySectionFragment.ARG_SIGNERS, signers);
		args.putString(DummySectionFragment.ARG_ERROR_MSG, errorMsg);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SIGNERS = "SIGNERS";
		public static final String ARG_NUM_SIGNERS = "NUM_SIGNERS";
		public static final String ARG_MIN_NUM_SIGNERS = "MIN_NUM_SIGNERS";
		public static final String ARG_ERROR_MSG = "ERROR_MSG";
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			int resource;
			View rootView;
			TextView textView;
			CheckBox checkBox;
			String str;
		
			// Arguments
			int numSigners = getArguments().getInt(ARG_NUM_SIGNERS, -1);
			int minNumSigners = getArguments().getInt(ARG_MIN_NUM_SIGNERS, -1);
			ArrayList<String> signers = getArguments().getStringArrayList(ARG_SIGNERS);
			String errorMsg = getArguments().getString(ARG_ERROR_MSG);
			
			Log.d(TAG, "onCreateView: numSigners = " + numSigners);
			Log.d(TAG, "onCreateView: minNumSigners = " + minNumSigners);
			Log.d(TAG, "onCreateView: signers = " + signers);
			Log.d(TAG, "onCreateView: localized error = " + errorMsg);

			mBusyDialog.cancel();
			
			if (errorMsg != null) {
				
				resource = R.layout.fragment_community_sig_status_nonexisting;
				rootView = inflater.inflate(resource, container, false);
				
				textView = (TextView) rootView.findViewById(R.id.communitySigStatusError);
				textView.setText(errorMsg);
				
				return rootView;
			}
			
			resource = R.layout.fragment_community_sig_status_existing;
			rootView = inflater.inflate(resource, container, false);

			checkBox = (CheckBox) rootView.findViewById(R.id.communitySigStatusThresholdReachedCheckBox);
			checkBox.setChecked(numSigners >= minNumSigners);
			
			ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.communitySigStatusProgressBar);
			int progress = Math.round(100 * ((float) numSigners) / minNumSigners);
			progress = Math.min(progress, 100);
			progressBar.setProgress(progress);

			textView = (TextView) rootView.findViewById(R.id.communitySigStatusSignedByNPartiesTextView);
			str = numSigners >= 0 ? String.valueOf(numSigners) : "?";
			textView.setText(str);
			
			textView = (TextView) rootView.findViewById(R.id.communitySigStatusRequiredTextView);
			str = minNumSigners >= 0 ? String.valueOf(minNumSigners) : "?";
			textView.setText(str);

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
