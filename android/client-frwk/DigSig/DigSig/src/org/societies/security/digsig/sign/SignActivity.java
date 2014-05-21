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
package org.societies.security.digsig.sign;

import java.util.Random;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.sign.contentprovider.DocContentProvider;
import org.societies.security.digsig.utility.RandomString;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This {@link Activity} is started by other (3rd party) apps when they request to sign something.
 * The {@link SignActivity} gathers information about the calling {@link Activity}, displays that
 * information and prompts the user to confirm or reject the signing.
 * <br>
 * If the document to be signed is given as a URL, then the user can preview it.
 * Preview of embedded documents is not implemented yet.
 */
public class SignActivity extends Activity {
	
	private final static int SELECT_IDENTITY = 1;
	private static final String TAG = SignActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.sign);
		initWidgets();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onStart()");
		
		Log.d(TAG, "Extra " + Sign.Params.DOC_TO_SIGN + " = " + getIntent().getByteArrayExtra(Sign.Params.DOC_TO_SIGN));
		Log.d(TAG, "Extra " + Sign.Params.DOC_TO_SIGN_URL + " = " + getIntent().getStringExtra(Sign.Params.DOC_TO_SIGN_URL));
		
		if (MainActivity.testMode) {
			Button btn = (Button) findViewById(R.id.buttonSignOk);
			btn.performClick();
		}
	}
	
	private void initWidgets() {
		
		TextView textView;
        Button btn;

        textView = (TextView) findViewById(R.id.textViewSignIsRequestingToSign);
        textView.setText("\"" + getCallerApp() + "\" " + getText(R.string.isRequestingToSign));
        
        btn = (Button) findViewById(R.id.buttonSignOk);
        btn.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Log.d(TAG, "OK button clicked");
				selectIdentity();
			}
		});

        btn = (Button) findViewById(R.id.buttonSignCancel);
        btn.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Log.d(TAG, "Cancel button clicked");
				setResult(RESULT_CANCELED);
				finish();
			}
		});

        btn = (Button) findViewById(R.id.buttonSignViewDoc);
        if (getIntent().getStringExtra(Sign.Params.DOC_TO_SIGN_URL) == null) {
        	// TODO: remove when showRawDocumentInternally() is implemented
        	btn.setVisibility(View.INVISIBLE);
        }
        btn.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Log.d(TAG, "View raw document button clicked");
				showRawDocument();
			}
		});
	}
	
	private String getCallerApp() {
		
		String caller;
		ComponentName callingActivity = getCallingActivity();
		if (callingActivity == null) {
			Log.w(TAG, "Could not get calling Activity.");
			return getText(R.string.unknownApp).toString();
		}
		String packageName = callingActivity.getPackageName();
		
		try {
			ApplicationInfo callerInfo;

			callerInfo = getPackageManager().getApplicationInfo(packageName, 0);
			caller = getPackageManager().getApplicationLabel(callerInfo).toString();
			Log.d(TAG, "Caller app: " + caller);
			return caller;
		} catch (NameNotFoundException e) {
			Log.w(TAG, e);
			return getText(R.string.unknownApp).toString();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "onActivityResult");

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SELECT_IDENTITY && resultCode == RESULT_OK) {
			
			String signedDocPath = RandomString.getRandomNumberString();
			int sid = Math.abs(new Random().nextInt());
			
			Intent intent = new Intent(this, SignService.class);
			intent.putExtras(getIntent());
			Log.d(TAG, "Extra " + Sign.Params.DOC_TO_SIGN + " = " + getIntent().getByteArrayExtra(Sign.Params.DOC_TO_SIGN));
			Log.d(TAG, "Extra " + Sign.Params.DOC_TO_SIGN_URL + " = " + getIntent().getStringExtra(Sign.Params.DOC_TO_SIGN_URL));
			intent.putExtra(Sign.Params.IDENTITY, data.getIntExtra(Sign.Params.IDENTITY, -1));
			intent.putExtra(Sign.Params.SIGNED_DOC_URL, signedDocPath);
			intent.putExtra(Sign.Params.SESSION_ID, sid);
			startService(intent);

			Intent returnIntent = new Intent(getIntent());
			returnIntent.putExtra(Sign.Params.SIGNED_DOC_URL, DocContentProvider.localPath2UriString(signedDocPath));
			returnIntent.putExtra(Sign.Params.SESSION_ID, sid);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}

	private void selectIdentity() {
		Intent i = new Intent(this, ListIdentitiesActivity.class);
		startActivityForResult(i, SELECT_IDENTITY);
	}
	
	private void showRawDocument() {
		
		byte[] doc = getIntent().getByteArrayExtra(Sign.Params.DOC_TO_SIGN);
		String docUri = getIntent().getStringExtra(Sign.Params.DOC_TO_SIGN_URL);
		
		if (docUri != null) {
			showRawDocumentInBrowser(docUri);
		}
		else if (doc != null) {
			showRawDocumentInternally(doc);
		}
	}
	
	private void showRawDocumentInBrowser(String uri) {
		
		Log.i(TAG, "Showing the raw XML document in browser. Document URI = " + uri);
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(uri));
		startActivity(i);
	}
	
	private void showRawDocumentInternally(byte[] doc) {

		Log.i(TAG, "Showing the raw XML document.");
		
		Log.w(TAG, "Showing the raw XML document internally is not supported yet.");
		// TODO
	}
}
