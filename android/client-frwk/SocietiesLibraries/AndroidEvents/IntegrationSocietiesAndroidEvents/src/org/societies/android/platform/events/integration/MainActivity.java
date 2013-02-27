package org.societies.android.platform.events.integration;


import java.util.List;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.api.identity.IIdentity;
import org.societies.utilities.DBC.Dbc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String TARGET_PACKAGE = "org.societies.android.platform.gui";
    private static final String TARGET_CLASS = "org.societies.android.platform.events.ServicePlatformEventsRemote";
    private static final String SERVICE_ACTION = "org.societies.android.platform.events.ServicePlatformEventsRemote";
    
    private static final String CLIENT_NAME = "org.societies.android.platform.events.integration";
    
    private boolean boundToService;
	private Messenger targetService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupBroadcastReceiver();
        bindToService();
    }
    
    protected void onStop() {
    	super.onStop();
    	if (boundToService) {
    		unbindService(serviceConnection);
    	}
    }

    
    /**
     * Call a real out-of-process service. Process involves:
     * 1. Select valid method signature
     * 2. Create message with corresponding index number
     * 3. Create a bundle (cf. http://developer.android.com/reference/android/os/Bundle.html) for restrictions on data types
     * 4. Add parameter values. The values are held in key-value pairs with the parameter name being the key
     * 5. Send message
     * 
     */
    private void subscribeToAllEvents() {
    	if (boundToService) {
    		InvokeRemoteMethod invoke  = new InvokeRemoteMethod(CLIENT_NAME);
    		invoke.execute();
    	}
    }
    
    private void bindToService() {
//    	Intent serviceIntent = new Intent();
//    	serviceIntent.setComponent(new ComponentName(TARGET_PACKAGE, TARGET_CLASS));
    	Intent serviceIntent = new Intent(SERVICE_ACTION);
    	Log.d(LOG_TAG, "Bind to Societies Android Service: ");
    	bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }
    
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			boundToService = false;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			boundToService = true;
			targetService = new Messenger(service);
	    	Log.d(this.getClass().getName(), "Societies Android Service connected: ");
	    	subscribeToAllEvents();
		}
	};

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS)) {
				Log.d(LOG_TAG, "Subscribed to all events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				Log.d(LOG_TAG, "Subscribed to all event - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				Log.d(LOG_TAG, "Subscribed to events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS)) {
				Log.d(LOG_TAG, "Un-subscribed from all events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT)) {
				Log.d(LOG_TAG, "Un-subscribed from event - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				Log.d(LOG_TAG, "Un-subscribed from events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			}
		}
    }
    
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.PUBLISH_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS);
        
        return intentFilter;
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        this.registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    
	/**
     * 
     * Async task to invoke remote service method
     *
     */
    private class InvokeRemoteMethod extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeRemoteMethod.class.getName();
    	private String packageName;
    	private String client;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public InvokeRemoteMethod(String client) {
    		this.client = client;
    	}

    	protected Void doInBackground(Void... args) {

    		String targetMethod = IAndroidSocietiesEvents.methodsArray[2];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Call Societies Android Service: ");

    		try {
				targetService.send(outMessage);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }
}
