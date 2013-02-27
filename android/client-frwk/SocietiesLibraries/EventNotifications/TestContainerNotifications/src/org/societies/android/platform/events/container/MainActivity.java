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

	private static final String EVENTS_SERVICE_REMOTE   = "org.societies.android.platform.events.ServicePlatformEventsRemote";
	private static final String CLIENT_NAME      = "org.societies.android.platform.events.notifications.TestContainer";
	private static final String LOG_TAG = MainActivity.class.getName();
	private Messenger eventMgrService = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//START EVENT MANAGER SERVICE
        Intent serviceIntent = new Intent(EVENTS_SERVICE_REMOTE);
      	bindService(serviceIntent, serviceEventsConnection, Context.BIND_AUTO_CREATE);
      	
  		//START FRIENDS SERVICE
        Intent intentFriends = new Intent(this.getApplicationContext(), FriendsService.class);
        startService(intentFriends);
        
        //SLEEP FOR 10 SECONDS BEFORE PUBLISHING EVENT
        try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        //PUBLISH EVENT TO TEST SERVICE
        InvokePublishEvent publish  = new InvokePublishEvent();
  		publish.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.d(LOG_TAG, "Test Activity for Notificaitons terminating");
		this.unbindService(serviceEventsConnection);	
	}
	
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BIND TO EXTERNAL "EVENT MANAGER">>>>>>>>>>>>>>>>>>>>>>>>>>>>
  	private ServiceConnection serviceEventsConnection = new ServiceConnection() {

  		public void onServiceConnected(ComponentName name, IBinder service) {
  			eventMgrService = new Messenger(service);
  			Log.d(LOG_TAG, "Connected to the Societies Event Mgr Service");
  			
  			//BOUND TO SERVICE - INVOKE THE startService() FOR COMMS
  			InvokeStartService invoke  = new InvokeStartService();
      		invoke.execute();
  		}
  		
  		public void onServiceDisconnected(ComponentName name) {
  		}
  	};
  	
  	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START EVENTS MANAGER SERVICE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  	/** Async task to invoke StartService method */
	private class InvokeStartService extends AsyncTask<Void, Void, Void> {

      	private final String LOCAL_LOG_TAG = InvokeStartService.class.getName();

      	protected Void doInBackground(Void... args) {
      		//METHOD: "startService()" - ARRAY POSITION: 8
      		String targetMethod = IAndroidSocietiesEvents.methodsArray[8];
      		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
      		Bundle outBundle = new Bundle();
      		outMessage.setData(outBundle);
      		Log.d(LOCAL_LOG_TAG, "Sending StartService() event message");
      		try {
      			eventMgrService.send(outMessage);
  			} catch (RemoteException e) {
  				e.printStackTrace();
  			}
      		return null;
		}
	}

  	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>PUBLISH PUBSUB EVENT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/** Async task to invoke PublishEvent method */
	private class InvokePublishEvent extends AsyncTask<Void, Void, Void> {

      	private final String LOCAL_LOG_TAG = InvokePublishEvent.class.getName();

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
      		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), MainActivity.CLIENT_NAME);										//client
      		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_RECEIVED_INTENT);	//societiesIntent
      		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), eventInfo);												//eventPayload
      		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + MainActivity.CLIENT_NAME);
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
