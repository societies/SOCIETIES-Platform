package org.societies.security.digsig.sign;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.utility.RandomString;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignActivity extends Activity {
	
	private final static int SELECT_IDENTITY = 1;
	private static final String TAG = SignActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.sign);
		initWidgets();
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
			
			Intent intent = new Intent(this, SignService.class);
			intent.putExtras(getIntent());
			intent.putExtra(Sign.Params.IDENTITY, data.getIntExtra(Sign.Params.IDENTITY, -1));
			intent.putExtra(Sign.Params.SIGNED_DOC_URL, signedDocPath);
			startService(intent);

			Intent returnIntent = new Intent(getIntent());
			returnIntent.putExtra(Sign.Params.SIGNED_DOC_URL, localPath2Url(signedDocPath));
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}
	
	private String localPath2Url(String path) {
		return "content://org.societies.security.digsig.provider/" + path;
	}

	private void selectIdentity() {
		Intent i = new Intent(this, ListIdentitiesActivity.class);
		startActivityForResult(i, SELECT_IDENTITY);
	}
}
