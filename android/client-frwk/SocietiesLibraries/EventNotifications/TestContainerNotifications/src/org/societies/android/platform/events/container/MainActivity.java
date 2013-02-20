package org.societies.android.platform.events.container;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.events.container.R;
import org.societies.android.platform.events.notifications.FriendsService;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.css.directory.CssFriendEvent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final String SERVICE_ACTION   = "org.societies.android.platform.events.ServicePlatformEventsRemote";
	private static final String CLIENT_NAME      = "org.societies.android.platform.events.notifications.TestContainer";
	private static final String LOG_TAG = MainActivity.class.getName();
	private FriendsService friendService;
	private Messenger eventMgrService = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//START FRIENDS SERVICE
        Intent intentFriends = new Intent(this.getApplicationContext(), FriendsService.class);
        this.getApplicationContext().bindService(intentFriends, serviceFriendsConnection, Context.BIND_AUTO_CREATE);
        
        //BIND TO EVENT MANAGER SERVICE
        Intent serviceIntent = new Intent(SERVICE_ACTION);
      	bindService(serviceIntent, serviceEventsConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BIND TO "FriendService">>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private ServiceConnection serviceFriendsConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to FriendsService service");
        	try {
	        	//GET LOCAL BINDER
        		FriendsService.LocalBinder binder = (FriendsService.LocalBinder) service;
	
	            //OBTAIN SERVICE
        		friendService = binder.getService();
	            Log.d(LOG_TAG, "Successfully connected to FriendService service");

        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ServiceDiscovery service");
        }
    };
    
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BIND TO EXTERNAL "EVENT MANAGER">>>>>>>>>>>>>>>>>>>>>>>>>>>>
  	private ServiceConnection serviceEventsConnection = new ServiceConnection() {

  		public void onServiceConnected(ComponentName name, IBinder service) {
  			eventMgrService = new Messenger(service);
  			Log.d(this.getClass().getName(), "Connected to the Societies Event Mgr Service");
  			
  			//BOUND TO SERVICE - SUBSCRIBE TO RELEVANT EVENTS
  			InvokeRemoteMethod invoke  = new InvokeRemoteMethod(CLIENT_NAME);
      		invoke.execute();
  		}
  		
  		public void onServiceDisconnected(ComponentName name) {
  		}
  	};
  	
  	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>PUBLISH PUBSUB EVENT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  	/** Async task to invoke remote service method */
      private class InvokeRemoteMethod extends AsyncTask<Void, Void, Void> {

      	private final String LOCAL_LOG_TAG = InvokeRemoteMethod.class.getName();
      	private String client;

      	public InvokeRemoteMethod(String client) {
      		this.client = client;
      	}

      	protected Void doInBackground(Void... args) {
      		//METHOD: "publishEvent(String client, String societiesIntent, Object eventPayload)" - ARRAY POSITION: 6
      		String targetMethod = IAndroidSocietiesEvents.methodsArray[6];
      		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
      		Bundle outBundle = new Bundle();

      		//PAYLOAD
      		CssFriendEvent eventInfo = new CssFriendEvent();
      		CssAdvertisementRecord advert = new CssAdvertisementRecord();
      		advert.setId("jane.societies.local");
      		advert.setName("Jane Smith");
      		advert.setUri("jane.societies.local");
      		eventInfo.setCssAdvert(advert);
      		
      		//PARAMETERS
      		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);													//client
      		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_RECEIVED_INTENT);	//societiesIntent
      		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), eventInfo);												//eventPayload
      		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
      		outMessage.setData(outBundle);

      		Log.d(LOCAL_LOG_TAG, "Sending publish event message");
      		try {
      			eventMgrService.send(outMessage);
  			} catch (RemoteException e) {
  				e.printStackTrace();
  			}
      		return null;
      	}
	}
}
