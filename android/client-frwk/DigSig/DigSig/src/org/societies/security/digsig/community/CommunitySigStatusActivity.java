/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.security.digsig.community;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.societies.security.digsig.apiinternal.Community;
import org.societies.security.digsig.sign.R;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The {@link Activity} to show current status of any community signature process for documents
 * this Android device has been involved in since the last clearing of the document list.
 *
 * @author Mitja Vardjan
 */
public class CommunitySigStatusActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private static final String TAG = CommunitySigStatusActivity.class.getSimpleName();

	public static final String ACTION_SIG_STATUS = "SIG_STATUS";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
//	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private List<String> documentTitles = new ArrayList<String>();
	private List<String> downloadUris = new ArrayList<String>();
	
	private static ProgressDialog mBusyDialog;
	
	private SigStatusReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_community_sig_status);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Button to view the whole raw ugly XML document
		Button button = (Button) findViewById(R.id.communitySigStatusViewRawDocumentButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selected = getActionBar().getSelectedNavigationIndex();
				String uri = downloadUris.get(selected);
				Log.d(TAG, "Selected URI of document to view = " + uri);
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(uri));
				startActivity(i);
			}
		});
		
		// Button to copy URL of the whole raw ugly XML document to clipboard
		button = (Button) findViewById(R.id.communitySigStatusCopyRawDocumentUrlButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selected = getActionBar().getSelectedNavigationIndex();
				String uri = downloadUris.get(selected);
				Log.d(TAG, "Selected URI to copy = " + uri);
				copyToClipboard(uri);
			}
		});
	}
	
	private void enableOrDisableButtons() {
		Button viewRawDocumentButton = (Button) findViewById(R.id.communitySigStatusViewRawDocumentButton);
		Button copyRawDocumentUrlButton = (Button) findViewById(R.id.communitySigStatusCopyRawDocumentUrlButton);
		boolean enable = (downloadUris.size() > 0);
		viewRawDocumentButton.setEnabled(enable);
		copyRawDocumentUrlButton.setEnabled(enable);
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
		restore();
		Log.d(TAG, documentTitles.size() + " existing documents found");
		
		loadActionBar();

		IntentFilter filter = new IntentFilter(ACTION_SIG_STATUS);
		receiver = new SigStatusReceiver();
		registerReceiver(receiver, filter);
		Log.d(TAG, "Receiver registered");
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
			Log.d(TAG, "Receiver unregistered");
		}

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
		
		SharedPreferencesHelper preferences = new SharedPreferencesHelper(this);
		preferences.clear();

		documentTitles.clear();
		downloadUris.clear();
		enableOrDisableButtons();
		loadActionBar();
		
		Log.d(TAG, "Download URIs cleared");
		
//		// FIXME: remove this testing code
//		SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
//		prefs.store("Completed example", "http://192.168.1.92/tmp/societies/collab-sig/completed.json?sig=foo");
//		prefs.store("In progress example", "http://192.168.1.92/tmp/societies/collab-sig/in-progress.json?sig=foo");
//		prefs.store("Not started yet example", "http://192.168.1.92/tmp/societies/collab-sig/non-existing.json?sig=foo");
//		prefs.store("Invalid response example", "http://192.168.1.92/tmp/societies/collab-sig/invalid.json?sig=foo");
//		prefs.store("Network error example", "http://192.168.1.312/collab-sig/invalid-ip-address.json?sig=foo");
//		restore();
	}

	private void store() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int selected = getActionBar().getSelectedNavigationIndex();
		editor.putInt(Community.SELECTED_DOCUMENT_INDEX, selected);
		editor.commit();
		Log.d(TAG, "Activity state stored. selected = " + selected);
	}

	private void restore() {
		
		SharedPreferencesHelper preferences = new SharedPreferencesHelper(this);
		Map<String, String> all = preferences.getAll();
		
		documentTitles.clear();
		downloadUris.clear();

		Iterator<String> iter = all.keySet().iterator();
		String key;
		while (iter.hasNext()) {
			key = iter.next();
			documentTitles.add(key);
			downloadUris.add(all.get(key));
		}
		enableOrDisableButtons();
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
		
		if (mBusyDialog != null) {
			Log.d(TAG, "Cancelling previous busy dialog");
			mBusyDialog.cancel();
		}
		mBusyDialog = new ProgressDialog(getActionBarThemedContextCompat());
		mBusyDialog.setMessage(getText(R.string.fetchingStatus));
		mBusyDialog.show();
		
		String uri = appendGetStatusParameter(downloadUris.get(position));
		new GetSigStatusTask(getApplicationContext()).execute(uri);
		return true;
	}
	
	private String appendGetStatusParameter(String uri) {
		String delimiter = uri.contains("?") ? "&" : "?";
		return uri + delimiter + Community.SERVER_PARAMETER_GET_STATUS;
	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_RETRIEVAL_STATUS = "RETRIEVAL_STATUS";
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
			RetrievalStatus retrievalStatus = (RetrievalStatus) getArguments().getSerializable(ARG_RETRIEVAL_STATUS);
			
			Log.d(TAG, "onCreateView: retrieval status = " + retrievalStatus);
			Log.d(TAG, "onCreateView: numSigners = " + numSigners);
			Log.d(TAG, "onCreateView: minNumSigners = " + minNumSigners);
			Log.d(TAG, "onCreateView: signers = " + signers);
			
			// Inflate the appropriate GUI
			switch (retrievalStatus) {
			case SUCCESS_AND_NOT_STARTED:
				resource = R.layout.fragment_community_sig_status_nonexisting;
				rootView = inflater.inflate(resource, container, false);
				break;
			case SUCCESS_AND_STARTED:
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
				break;
			case ERROR_COULD_NOT_CONNECT_TO_SERVER:
				resource = R.layout.fragment_community_sig_status_error;
				rootView = inflater.inflate(resource, container, false);
				textView = (TextView) rootView.findViewById(R.id.communitySigStatusErrorTextView);
				textView.setText(R.string.errorNetwork);
				break;
			case ERROR_INVALID_RESPONSE:
				resource = R.layout.fragment_community_sig_status_error;
				rootView = inflater.inflate(resource, container, false);
				textView = (TextView) rootView.findViewById(R.id.communitySigStatusErrorTextView);
				textView.setText(R.string.errorReceivedData);
				break;
			default:
				// Should never happen
				Log.w(TAG, "Unknown retrieval status: " + retrievalStatus);
				rootView = null;
			}
			return rootView;
		}
	}

	private class SigStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.i(TAG, "Broadcast received");
			
			if (mBusyDialog != null) {
				mBusyDialog.cancel();
			}
			mBusyDialog = null;

			Fragment fragment = new DummySectionFragment();
			fragment.setArguments(intent.getExtras());
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment).commit();
		}
	}

	private void copyToClipboard(String str) {
		
//		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
//			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//			clipboard.setText(str);
//		} else {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("Copied Text", str);
			clipboard.setPrimaryClip(clip);
//		}
	}
}
