package org.societies.security.digsig.sign;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.societies.security.digsig.api.SigResult;
import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.api.Verify;
import org.societies.security.digsig.apiinternal.Trust;
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
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final static int SIGN = 1;
	private final static int VERIFY = 2;

	private static final String TAG = MainActivity.class.getSimpleName();

	private int sessionId;
	private String signedUrl;

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
		Button listBtn = (Button) findViewById(R.id.buttonMainXmlSign);
		listBtn.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {

				Log.i(TAG, "buttonXmlSign clicked");
				byte[] val = null;
				try {
					val = "<xml><miki Id='Miki1'>aadsads</miki></xml>".getBytes("UTF-8");
				} catch (Exception e) {}

				Intent i = new Intent(Sign.ACTION);
				i.putExtra(Sign.Params.DOC_TO_SIGN, val);

				ArrayList<String> idsToSign = new ArrayList<String>();
				idsToSign.add("Miki1");
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

				ArrayList<String> idsToSign = new ArrayList<String>();
				idsToSign.add("Miki1");
				i.putStringArrayListExtra(Sign.Params.IDS_TO_SIGN, idsToSign);

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

		registerBroadcastReceiver();
	}

	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter(Sign.ACTION_FINISHED);
		registerReceiver(new Receiver(), filter);
	}

	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			boolean success = intent.getBooleanExtra(Sign.Params.SUCCESS, false);
			int sid = intent.getIntExtra(Sign.Params.SESSION_ID, -1);

			if (success && sid == MainActivity.this.sessionId) {
				Log.d(TAG, "Received broadcast about finished signing operation.");
				try {
					InputStream is = getContentResolver().openInputStream(Uri.parse(signedUrl));
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
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SIGN && resultCode==RESULT_OK) {

			signedUrl = data.getStringExtra(Sign.Params.SIGNED_DOC_URL);
			sessionId = data.getIntExtra(Sign.Params.SESSION_ID, -1);
			Log.d(TAG, "URL of the signed XML: " + signedUrl);
		} if (requestCode == VERIFY) {
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
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
    	
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: msg.what = " + msg.what + ", replyTo = " + msg.replyTo);
            if (!msg.getData().getBoolean(Verify.Params.SUCCESS)) {
            	// Error
            	return;
            }
            switch (msg.what) {
                case Verify.Methods.GENERATE_URIS:
                	Log.i(TAG, "handleMessage: GENERATE_URIS: upload URI = " +
                			msg.getData().getString(Verify.Params.UPLOAD_URI));
                	Log.i(TAG, "handleMessage: GENERATE_URIS: download URI = " +
                			msg.getData().getString(Verify.Params.DOWNLOAD_URI));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

	public void testSignServiceRemote() throws Exception {

		Log.i(TAG, "testSignServiceRemote");

		// Bind to the service
    	Intent intent = new Intent(Verify.ACTION);
    	bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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

    public void generateUris() {
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


}