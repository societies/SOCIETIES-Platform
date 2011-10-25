/**
 * 
 */
package eu.societies.cft.sota.service;

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author olivierm
 * @date 24 oct. 2011
 */
public class HelloIntentStartedService extends IntentService {
	private String TAG = "HelloIntentService";
	
	
	/* ---
	 * The following methods MUST be overwritten
	 * --- */
	
	/** 
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public HelloIntentStartedService() {
		super("HelloIntentService");

	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns, IntentService
	 * stops the service, as appropriate.
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// Normally we would do some work here, like download a file.
		// For our sample, we just sleep for 5 seconds.
		Log.i(TAG, "Hello "+intent.getBundleExtra("data").getString("name")+"!");
		showNotification("Hello "+intent.getBundleExtra("data").getString("name")+"!");
		Log.i(TAG, "Waiting 5s");
		long endTime = System.currentTimeMillis() + 5*1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}
	}
	
	/**
     * Show a notification while this service is running.
     */
    private void showNotification(String text) {
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
    	int icon = R.drawable.arrow_up_float;
    	CharSequence tickerText = "Hello";
    	long when = System.currentTimeMillis();

    	Notification notification = new Notification(icon, tickerText, when);
    	Context context = getApplicationContext();
    	CharSequence contentTitle = "My notification";
    	CharSequence contentText = text;
    	Intent notificationIntent = new Intent(this, HelloStartedService.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

    	mNotificationManager.notify(1, notification);
    }
	
	
	/* ---
	 * If needed, following methods may be overwritten
	 * but be sure to call the super implementation,
	 * so that the IntentService can properly handle the life of the worker thread
	 * ---
	 */
	
	@Override
	public void onCreate() {
		// SHOULD call the super implementation
		super.onCreate();
		
		// Do something on the creation of the service
		Log.i(TAG, "Create HelloIntentService Service");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // Do something on the beginning of the service
		Toast.makeText(this, "Service HelloIntentService starting", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Service HelloIntentService starting");
		
	    // SHOULD return the super implementation
	    return super.onStartCommand(intent,flags,startId);
	}
	
	@Override
	public void onDestroy() {
		// SHOULD call the super implementation
		super.onDestroy();
		
		// Do something on the end of the service
		Log.i(TAG, "Destroy HelloIntentService Service");
	}
}
