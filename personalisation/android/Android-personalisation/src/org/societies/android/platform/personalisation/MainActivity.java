package org.societies.android.platform.personalisation;

import org.societies.android.api.personalisation.IPersonalisationManagerAndroid;
import org.societies.android.platform.personalisation.PersonalisationManagerAndroid.LocalBinder;


import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private boolean connectedToService = false;
	private IPersonalisationManagerAndroid personalisationService = null;
	private final static String LOG_TAG = MainActivity.class.getName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.bindToService();
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	private void bindToService(){
		//Create intent to select service to bind to
		Intent bindIntent = new Intent(this, PersonalisationManagerAndroid.class);
		//bind to service
		bindService(bindIntent, uaConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection uaConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			personalisationService = ((LocalBinder) service).getService();
			connectedToService = true;
			Log.d(LOG_TAG, "Main activity connected to personalisation service");
			
			//personalisationService.getPreference(arg0, arg1, arg2, arg3, arg4)
		}

		public void onServiceDisconnected(ComponentName className) {
			// As our service is in the same process, this should never be called
			connectedToService = false;
			Log.d(LOG_TAG, "Main activity disconnected from personalisation service");
		}
	};
	
	

}
