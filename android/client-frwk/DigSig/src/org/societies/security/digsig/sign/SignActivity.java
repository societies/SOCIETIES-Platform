package org.societies.security.digsig.sign;

import org.societies.security.digsig.api.Sign;

import android.app.Activity;
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
		
		try {
			ApplicationInfo callerInfo;

			callerInfo = getPackageManager().getApplicationInfo(getCallingActivity().getPackageName(), 0);
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
			Intent intent = new Intent(this, SignService.class);
			intent.putExtras(getIntent());
			intent.putExtra(Sign.Params.IDENTITY, data.getIntExtra("SELECTED", -1));
			startService(intent);
		}
	}
	
	private void selectIdentity() {
		Intent i = new Intent(this, ListIdentitiesActivity.class);
		startActivityForResult(i, SELECT_IDENTITY);
	}
}
