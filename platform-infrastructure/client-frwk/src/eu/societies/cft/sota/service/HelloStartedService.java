package eu.societies.cft.sota.service;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

/**
 * @author olivierm
 * @date 24 oct. 2011
 */
public class HelloStartedService extends Service {
	private String TAG = "HelloService";

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	/* ---
	 * The following methods MUST be overwritten
	 * --- */

	/**
	 * Handler that receives messages from the thread
	 * @author olivierm
	 * @date 24 oct. 2011
	 */
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
			Log.i(TAG, "Hello "+msg.getData().getString("name")+"!");
			Log.i(TAG, "Waiting 5s");
			showNotification("Hello "+msg.getData().getString("name")+"!");
			long endTime = System.currentTimeMillis() + 5*1000;
			while (System.currentTimeMillis() < endTime) {
				synchronized (this) {
					try {
						wait(endTime - System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
			}
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}
	
	@Override
	public void onCreate() {
		// Do something on the creation of the service
		Log.i(TAG, "Create HelloService Service");
				
		// Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
	    HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    
	    // Get the HandlerThread's Looper and use it for our Handler 
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // Do something on the beginning of the service
		Toast.makeText(this, "Service HelloService starting", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Service HelloService starting");

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.setData(intent.getBundleExtra("data"));
		mServiceHandler.sendMessage(msg);

		
		// If we get killed, after returning from here, recreate the service with a null intent
	    return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}
	
	/**
     * Show a notification while this service is running.
     */
    private void showNotification(String text) {
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
    	int icon = R.drawable.arrow_down_float;
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
	 * but be sure to call the super implementation
	 * ---
	 */
	
	@Override
	public void onDestroy() {
		// Do something on the end of the service
		Log.i(TAG, "Destroy HelloService Service");
	}
}
