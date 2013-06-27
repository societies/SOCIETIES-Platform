package org.societies.platfrom.sns.android.socialapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class SelectSNActivity extends Activity {

	private Button fb, tw, fq, lk;
	//private LinearLayout mainLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {    	
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ssolib_main);        

		fb = (Button)findViewById(R.id.fb_connector);
		tw = (Button)findViewById(R.id.tw_connector);
		fq = (Button)findViewById(R.id.fq_connector);
		lk = (Button)findViewById(R.id.lk_connector);

		fb.setOnClickListener(listener);
		fq.setOnClickListener(listener);
		tw.setOnClickListener(listener);
		lk.setOnClickListener(listener);

		//mainLayout = (LinearLayout) findViewById(R.id.social_main_layout);
		//mainLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(Constants.DEBUG_TAG, "[onStart]");
	}

	@Override
	protected void onResume() {
		Log.d(Constants.DEBUG_TAG, "[onResume]");
		super.onResume();		
	}

	private void openBrowser(String uri, int requestCode){
		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra(Constants.SSO_URL, uri);
		startActivityForResult(intent, requestCode);
	}



	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			//Due to changes in the way that Android compiles APKLIB projects, R.id.xxxx is no longer a constant but is declared as:
			//public static int
			//As a result, expressions such as R.id.xxxx are longer constants and switch statements no longer work - use if statements instead
			//cf. http://tools.android.com/tips/non-constant-fields
		
			if (v.getId() == R.id.fb_connector) {
				Log.d(Constants.DEBUG_TAG, "[onClick] fb_connector");
				openBrowser(Constants.FB_URL, Constants.FB_CODE);
			} else if (v.getId() == R.id.tw_connector) {
				Log.d(Constants.DEBUG_TAG, "[onClick] tw_connector");
				openBrowser(Constants.TW_URL, Constants.TW_CODE);
			} else if (v.getId() == R.id.lk_connector) {
				Log.d(Constants.DEBUG_TAG, "[onClick] lk_connector");		
				openBrowser(Constants.LK_URL, Constants.LK_CODE);
			}
			
//			switch(v.getId()){				
//			case R.id.fb_connector:
//				break;
//
//			case R.id.fq_connector: 
//				Log.d(Constants.DEBUG_TAG, "[onClick] fq_connector");
//				openBrowser(Constants.FQ_URL, Constants.FQ_CODE);
//				break;
//
//			case R.id.tw_connector: 
//				Log.d(Constants.DEBUG_TAG, "[onClick] tw_connector");
//				openBrowser(Constants.TW_URL, Constants.TW_CODE);
//				break;
//
//			case R.id.lk_connector: 
//				Log.d(Constants.DEBUG_TAG, "[onClick] lk_connector");		
//				openBrowser(Constants.LK_URL, Constants.LK_CODE);
//				break;
//			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		Log.v(Constants.DEBUG_TAG, "onActivityResult: " + resultCode);
		//super.onActivityResult(requestCode, resultCode, data);

		if(data == null){
			return;
		}
		String token = data.getStringExtra(Constants.ACCESS_TOKEN);
		if (requestCode == Constants.FB_CODE){
			openDialog(token, "Facebook");
		}
		else if(requestCode == Constants.TW_CODE){
			openDialog(token, "Twitter");
		}
		else if(requestCode == Constants.FQ_CODE){
			openDialog(token, "Foursquare");
		}
		else if(requestCode == Constants.LK_CODE){
			openDialog(token, "Linkedin");
		}
	}

	private void openDialog(String value, String sn){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(value)
		.setTitle(sn)
		.setCancelable(false)
		.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(SelectSNActivity.this, "Not Implemented", Toast.LENGTH_LONG).show();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		}).create().show();
	}
}