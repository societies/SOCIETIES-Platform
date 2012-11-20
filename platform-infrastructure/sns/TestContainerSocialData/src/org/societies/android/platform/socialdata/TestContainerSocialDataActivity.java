package org.societies.android.platform.socialdata;

import org.societies.android.api.internal.sns.AConnectorBean;
import org.societies.android.api.internal.sns.ISocialData;
import org.societies.android.platform.socialdata.SocialData.LocalBinder;
import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;

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
import android.widget.TextView;

public class TestContainerSocialDataActivity extends Activity {

	private static final String LOG_TAG = TestContainerSocialDataActivity.class.getName();
	
	private static final String PACKAGE_NAME = "org.societies.android.platform.socialdata";
	
    private ISocialData socialData;
    private boolean socialDataConnected = false;
    private TextView text;  
    private TestTask testTask;
    private String addedConnectorId = null;
    private AConnectorBean[] connectors = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        text = (TextView) findViewById(R.id.text);
        
        testTask = new TestTask(this);        
        
        //REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(ISocialData.ADD_SOCIAL_CONNECTOR);        
        intentFilter.addAction(ISocialData.REMOVE_SOCIAL_CONNECTOR);
        intentFilter.addAction(ISocialData.GET_SOCIAL_CONNECTORS);
        intentFilter.addAction(ISocialData.ACTION_XMPP_ERROR);
        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //CREATE INTENT FOR SERVICE AND BIND
        Intent intentSocialData = new Intent(this.getApplicationContext(), SocialData.class);
        this.getApplicationContext().bindService(intentSocialData, socialDataConnection, Context.BIND_AUTO_CREATE);
        
        Log.d(LOG_TAG, "Test in progress...");
		text.setText("Test in progress...");
    }
    
    /**
     * IServiceDiscovery service connection
     */
    private ServiceConnection socialDataConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to service");

        	try {
	        	//GET LOCAL BINDER
	            LocalBinder binder = (LocalBinder) service;
	
	            //OBTAIN SERVICE API
	            socialData = (ISocialData) binder.getService();
	            socialDataConnected = true;
	            	        	
	            testTask.execute();
        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from service");
        	socialDataConnected = false;
        }
    };
    
    private class TestTask extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestTask(Context context) {
    		this.context = context;    		
    	}
    	
    	protected Void doInBackground(Void... args) {
    		try {
    			
    			addConnector();
    			
				while(addedConnectorId == null) 
					Thread.sleep(1000);
				
				getConnectors();
				
				while(connectors == null) 
					Thread.sleep(1000);
				
				removeConnector(addedConnectorId);
    			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		return null;
    	}    	
    	
    };

    private void addConnector() {
    	
		long testValidity = 1000;
		socialData.addSocialConnector(PACKAGE_NAME, SocialNetwork.Facebook, "testToken", testValidity);
		
    }
    
    private void getConnectors() {
    	socialData.getSocialConnectors(PACKAGE_NAME);
    }
    
    private void removeConnector(String id) {
    	socialData.removeSocialConnector(PACKAGE_NAME, id);
    }
    
    private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if (intent.getAction().equals(ISocialData.ADD_SOCIAL_CONNECTOR)) {
				String id = intent.getStringExtra(ISocialData.INTENT_RETURN_KEY);
				addedConnectorId = id;
				
				Log.d(LOG_TAG, "id="+id);
				text.setText("id="+id);
			}
			else if(intent.getAction().equals(ISocialData.GET_SOCIAL_CONNECTORS)) {
				connectors = castArray(intent.getParcelableArrayExtra(ISocialData.INTENT_RETURN_KEY));
				
				for(int i=0; i<connectors.length; i++) {					
					Log.d(LOG_TAG, "connector.getID()="+connectors[i].getId());
					text.append("\nconnector.getID()="+connectors[i].getId());
				}
			} 
			else if(intent.getAction().equals(ISocialData.REMOVE_SOCIAL_CONNECTOR)) {
				Log.d(LOG_TAG, "connector removed");
				text.append("\nconnector removed");
			}
			else if(intent.getAction().equals(ISocialData.ACTION_XMPP_ERROR)) {
				String stanzaError = intent.getStringExtra(ISocialData.EXTRA_STANZA_ERROR);
				
				String logMsg = "XMPPError: "+stanzaError;
				Log.d(LOG_TAG, logMsg);
				text.append("\n"+logMsg);
			}
			
		}
	};
	
	private AConnectorBean[] castArray(Parcelable[] parcelables) {
		AConnectorBean[] rv = new AConnectorBean[parcelables.length];
		for(int i=0; i<parcelables.length; i++)
			rv[i] = (AConnectorBean)parcelables[i];
		return rv;
	}
}