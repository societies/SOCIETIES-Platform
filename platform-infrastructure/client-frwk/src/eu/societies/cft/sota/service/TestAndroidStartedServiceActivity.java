package eu.societies.cft.sota.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestAndroidStartedServiceActivity extends Activity implements OnClickListener {
	private String TAG = "TestAndroidActivity";
	
	private Button startIntentService;
	private Button stopIntentService;
	private Button startService;
	private Button stopService;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startIntentService = (Button) findViewById(R.id.startIntentService);
        	startIntentService.setOnClickListener(this);
        stopIntentService = (Button) findViewById(R.id.stopIntentService);
        	stopIntentService.setOnClickListener(this);
    	startService = (Button) findViewById(R.id.startService);
        	startService.setOnClickListener(this);
    	stopService = (Button) findViewById(R.id.stopService);
        	stopService.setOnClickListener(this);
    }


	@Override
	public void onClick(View v) {
		Intent intent;
		Bundle bundle = new Bundle();
		switch(v.getId()) {
			case R.id.startIntentService:
				Log.i(TAG, "Click to start intent service");
				intent = new Intent(this, HelloIntentStartedService.class);
				bundle.putString("name", "you");
				intent.putExtra("data", bundle);
				startService(intent);
				break;
			case R.id.stopIntentService:
				intent = new Intent(this, HelloIntentStartedService.class);
				stopService(intent);
				break;
			case R.id.startService:
				Log.i(TAG, "Click to start service");
				intent = new Intent(this, HelloStartedService.class);
				bundle.putString("name", "me");
				intent.putExtra("data", bundle);
				startService(intent);
				break;
			case R.id.stopService:
				intent = new Intent(this, HelloStartedService.class);
				stopService(intent);
				break;
		}
	}
}