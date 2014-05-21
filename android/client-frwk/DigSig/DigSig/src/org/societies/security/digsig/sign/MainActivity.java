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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import org.societies.security.digsig.api.SigResult;
import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.api.Verify;
import org.societies.security.digsig.apiinternal.Trust;
import org.societies.security.digsig.community.CommunitySigStatusActivity;
import org.societies.security.digsig.utility.Storage;
import org.societies.security.digsig.utility.StreamUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The main {@link Activity}.
 * Includes controls to install new identities with {@link InstallIdentityActivity},
 * track community signature status with {@link CommunitySigStatusActivity}, and
 * controls to perform various tests if test mode is enabled.
 */
public class MainActivity extends Activity {

	protected static final boolean testMode = true;
	
	private final static int SIGN = 1;
	private final static int VERIFY = 2;

	private static final String TAG = MainActivity.class.getSimpleName();

	private int sessionId;
	private String signedUrl;
	private BroadcastReceiver receiver = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button installBtn = (Button) findViewById(R.id.buttonMainInstallIdentity);
		installBtn.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, InstallIdentityActivity.class);
				startActivity(i);
			}
		});
		
		((Button) findViewById(R.id.buttonMainCommunitySigStatus)).setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, CommunitySigStatusActivity.class);
				startActivity(i);
			}
		});
		
		if (testMode) {
			initTestingWidgets();
		}
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();

		if (testMode) {
			IntentFilter filter = new IntentFilter(Sign.ACTION_FINISHED);
			receiver = new Receiver();
			registerReceiver(receiver, filter);
			Log.d(TAG, "Receiver registered");
		}
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();

		if (testMode) {
			if (receiver != null) {
				unregisterReceiver(receiver);
				receiver = null;
				Log.d(TAG, "Receiver unregistered");
			}
			
			if (mBound) {
				unbindService(mConnection);
				Log.d(TAG, "Service unbound");
				mBound = false;
			}
		}
	}


	///////////////////////////////////////////////////////////////////////////
	//
	// Below is the code for testing only.
	//
	///////////////////////////////////////////////////////////////////////////

	private long start = -1;
	
	private void initTestingWidgets() {
		
		Button listBtn = (Button) findViewById(R.id.buttonMainXmlSign);
		listBtn.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {

				Log.i(TAG, "buttonXmlSign clicked");
				byte[] val = null;
				try {
					val = "<xml><a Id='Board001'>aadsads</a></xml>".getBytes("UTF-8");
				} catch (Exception e) {}

				Intent i = new Intent(Sign.ACTION);
				i.putExtra(Sign.Params.DOC_TO_SIGN, val);

				ArrayList<String> idsToSign = new ArrayList<String>();
				idsToSign.add("Board001");
				i.putStringArrayListExtra(Sign.Params.IDS_TO_SIGN, idsToSign);

				startActivityForResult(i, SIGN);
			}
		});

		listBtn = (Button) findViewById(R.id.buttonMainXmlSignUrl);
		listBtn.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {

				Log.i(TAG, "buttonXmlSignUrl clicked");

				EditText urlEditText = (EditText) findViewById(R.id.editTextMainSignUrl);
				String url = urlEditText.getText().toString();

				Intent i = new Intent(Sign.ACTION);
				i.putExtra(Sign.Params.DOC_TO_SIGN_URL, url);
				i.putExtra(Sign.Params.COMMUNITY_SIGNATURE_SERVER_URI, url);

				ArrayList<String> idsToSign = new ArrayList<String>();
				idsToSign.add("Board001");
				i.putStringArrayListExtra(Sign.Params.IDS_TO_SIGN, idsToSign);

				start = new Date().getTime();
				startActivityForResult(i, SIGN);
			}
		});

		listBtn = (Button) findViewById(R.id.buttonMainSign);
		listBtn.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {

				Log.i(TAG, "buttonSign clicked");

				try {
					MainActivity.this.testSignServiceRemote();
				} catch (Exception e) {
					Log.e(TAG, "", e);
				}
			}
		});

		Button verifyBtn = (Button) findViewById(R.id.buttonMainVerify);
		verifyBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, VerifyActivity.class);

				ByteArrayOutputStream os = new ByteArrayOutputStream();				
				InputStream is = getResources().openRawResource(R.raw.sample);

				StreamUtil.copyStream(is, os);

				i.putExtra(Sign.Params.DOC_TO_SIGN, os.toByteArray());

				startActivityForResult(i, VERIFY);
			}
		});

		CheckBox showTestingOptions = (CheckBox) findViewById(R.id.checkBoxMainShowTestingOptions);
		showTestingOptions.setVisibility(View.VISIBLE);
		showTestingOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				showTestButtons(isChecked);
			}
		});
		showTestButtons(showTestingOptions.isChecked());
	}
	
	private void showTestButtons(boolean show) {
		int visibility = show ? View.VISIBLE : View.INVISIBLE;
		findViewById(R.id.buttonMainSign).setVisibility(visibility);
		findViewById(R.id.buttonMainVerify).setVisibility(visibility);
		findViewById(R.id.buttonMainXmlSign).setVisibility(visibility);
		findViewById(R.id.buttonMainXmlSignUrl).setVisibility(visibility);
		findViewById(R.id.textViewMainSignUrl).setVisibility(visibility);
		findViewById(R.id.editTextMainSignUrl).setVisibility(visibility);
	}

	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			boolean success = intent.getBooleanExtra(Sign.Params.SUCCESS, false);
			int sid = intent.getIntExtra(Sign.Params.SESSION_ID, -1);

			long time = new Date().getTime() - start;
			Log.i(TAG, "Signature finished, time elapsed: " + time + " ms");
			
			if (success && sid == MainActivity.this.sessionId) {
				Log.d(TAG, "Received broadcast about finished signing operation.");
				try {
					InputStream is = getContentResolver().openInputStream(Uri.parse(signedUrl));
					Log.d(TAG, "External storage state = " + Environment.getExternalStorageState());
					FileOutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/signed.xml");
					int numRead;
					byte[] buf = new byte[1024];
					while ( (numRead = is.read(buf) ) >= 0) {
						os.write(buf, 0, numRead);
					}
					os.close();
					is.close();
					getContentResolver().delete(Uri.parse(signedUrl), null, null);
				} catch(Exception e) {
					Log.w(TAG, e);
				}					

				Log.d(TAG, "File signed sucessfully. Output is in signed.xml on SD card!");
				Toast.makeText(MainActivity.this, "File signed sucessfully.\nOutput is in signed.xml on SD card!", Toast.LENGTH_LONG).show();
			}
			else {
				Log.w(TAG, "Success = " + success + ", session ID = " + sid +
						", expecting session ID " + MainActivity.this.sessionId);
			}
			String record = String.valueOf(time) + "," + success + "\n";
			try {
				new Storage(MainActivity.this).writeToExternalStorage("DigSig Test.csv", record.getBytes("UTF-8"), true);
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SIGN && resultCode==RESULT_OK) {

			signedUrl = data.getStringExtra(Sign.Params.SIGNED_DOC_URL);
			sessionId = data.getIntExtra(Sign.Params.SESSION_ID, -1);
			Log.d(TAG, "URL of the signed XML: " + signedUrl);
		}
		else if (requestCode == VERIFY) {
			if (resultCode == RESULT_OK) {
				// get data
				ArrayList<SigResult> sigResults = data.getParcelableArrayListExtra(Trust.Params.RESULT);

				boolean allOk = true;

				for (int i=0;i<sigResults.size();i++) {
					SigResult result = sigResults.get(i);
					if (result.getSigStatus()==0) {
						Toast.makeText(this, String.format("Signature %d is invalid !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					} else if (result.getSigStatus()==-1) {
						Toast.makeText(this, String.format("Error while verifying signatrue number %d !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					}

					if (result.getTrustStatus()==0) {
						Toast.makeText(this, String.format("Trust status on signature number %d is invalid !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					} else if (result.getTrustStatus()==-1) {
						Toast.makeText(this, String.format("Error while verifying trust status on signatrue number %d !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					}					
				}

				if (allOk) 
					Toast.makeText(this, String.format("Successfully verified %d signatures in the file.", sigResults.size()), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Error while verifying file. Is the XML valid ?", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Handler of receiving replies.
	 */
	static class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Log.i(TAG, "handleMessage: msg.what = " + msg.what);

			if (!msg.getData().getBoolean(Verify.Params.SUCCESS)) {
				// An error occurred in the service
				return;
			}
			switch (msg.what) {
			case Verify.Methods.GENERATE_URIS:
				
				// Now upload your XML document with this URI.
				// Optionally, you can sign your XML document yourself before or after upload.
				String uploadUri = msg.getData().getString(Verify.Params.UPLOAD_URI);
				Log.i(TAG, "handleMessage: GENERATE_URIS: upload URI = " + uploadUri);
//				new RetrieveFeedTask().execute(uploadUri, "PUT", "<xml><a Id='id1'>foo</a></xml>");
				
				// After you upload the XML document, distribute the download URI to others to sign it.
				String downloadUri = msg.getData().getString(Verify.Params.DOWNLOAD_URI);
				Log.i(TAG, "handleMessage: GENERATE_URIS: download URI = " + downloadUri);
//				new RetrieveFeedTask().execute(downloadUri, "GET", "foo.xml");
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	public void testSignServiceRemote() throws Exception {

		Log.i(TAG, "testSignServiceRemote");

		if (mBound) {
			generateUris();
		} else {
			// Bind to the service
			Intent intent = new Intent(Verify.ACTION);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	/** Messenger for communicating with the service. */
	Messenger mService = null;

	/** Flag indicating whether we have called bind on the service. */
	boolean mBound;

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			mService = new Messenger(service);
			mBound = true;
			generateUris();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			mBound = false;
		}
	};

	private void generateUris() {
		if (!mBound) return;
		// Create and send a message to the service, using a supported 'what' value
		Message msg = Message.obtain(null, Verify.Methods.GENERATE_URIS, 0, 0);
		Bundle data = new Bundle();
		data.putString(Verify.Params.NOTIFICATION_ENDPOINT, "http://192.168.1.92/societies/community-signature/notify");
		data.putInt(Verify.Params.NUM_SIGNERS_THRESHOLD, 2);
		msg.setData(data);
		msg.replyTo = mMessenger;
		try {
			Log.i(TAG, "Sending message to service");
			mService.send(msg);
			Log.i(TAG, "Message sent to service");
		} catch (Exception e) {
			Log.e(TAG, "sayHello", e);
		}
	}


	///////////////////////////////////////////////////////////////////////////
	//
	// End of testing-only code.
	//
	///////////////////////////////////////////////////////////////////////////
}
