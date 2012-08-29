package org.societies.android.platform.servicelifecycle;

import org.societies.android.api.internal.servicelifecycle.AService;
import org.societies.android.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.android.platform.servicemonitor.ServiceManagement;
import org.societies.android.platform.servicemonitor.CoreServiceMonitor.LocalBinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

public class TestContainerSLMActivity extends Activity {

    private IServiceDiscovery serviceDisco;
    private boolean serviceDiscoConnected = false;
    private static final String LOG_TAG = TestContainerSLMActivity.class.getName();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //CREATE INTENT FOR SERVICE AND BIND
        Intent intentServiceDisco = new Intent(this.getApplicationContext(), ServiceManagement.class);
        this.getApplicationContext().bindService(intentServiceDisco, serviceDiscoConnection, Context.BIND_AUTO_CREATE);
        
        //REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(ServiceManagement.GET_SERVICES);
        intentFilter.addAction(ServiceManagement.GET_SERVICE);
        intentFilter.addAction(ServiceManagement.SEARCH_SERVICES);
        
        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //TEST THE SLM COMPONENT
        TestSLM task = new TestSLM(this);
        task.execute();
    }
    
    /**
     * IServiceDiscovery service connection
     */
    private ServiceConnection serviceDiscoConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to IServiceDiscovery service");

        	try {
	        	//GET LOCAL BINDER
	            LocalBinder binder = (LocalBinder) service;
	
	            //OBTAIN SERVICE DISCOVERY API
	            serviceDisco = (IServiceDiscovery) binder.getService();
	            serviceDiscoConnected = true;
        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ServiceDiscovery service");
        	serviceDiscoConnected = false;
        }
    };
    
    private class TestSLM extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestSLM(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {
    		if (serviceDiscoConnected)
    			serviceDisco.getServices("TestContainerSLMActivity", "john.societies.local");
    		return null;
    	}
    }
    
    private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if (intent.getAction().equals(ServiceManagement.GET_SERVICES)) {
				//UNMARSHALL THE SERVICES FROM Parcels BACK TO Services
				Parcelable parcels[] =  intent.getParcelableArrayExtra(ServiceManagement.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					AService aservice = (AService) parcels[i];
					Log.d(LOG_TAG, "Service Name: " + aservice.getServiceName());
					Log.d(LOG_TAG, "Service Description: " + aservice.getServiceDescription());
				}
			}
		}
	};
}