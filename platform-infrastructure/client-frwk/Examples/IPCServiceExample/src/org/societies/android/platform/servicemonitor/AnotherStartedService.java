package org.societies.android.platform.servicemonitor;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

/**
 * This is an example of a "started" service which use AsyncTask to create a new thread (from a thread pool
 * maintained by Asynctask) and carry out a task. This service will not terminate itself and must be
 * terminated by another component
 * 
 *
 */
public class AnotherStartedService extends Service {
	public static final String PROGRESS_STATUS_INTENT = "org.societies.android.platform.servicemonitor.AnotherStartedService.PROGRESS_BAR";
	public static final String PROGRESS_STATUS_VALUE = "progessValue";
	
	private static final int FIBONACCI_LIMIT = 35;

	public void onCreate() {
		Log.i(this.getClass().getName(), "Current Threadname " + Thread.currentThread().getName());
		Log.i(this.getClass().getName(), "Current Thread count " + Thread.activeCount());
	}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(this.getClass().getName(), "Service being started");
		new CarryOutServiceTask().execute((Void []) null);
		return START_NOT_STICKY;
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

    @Override
    public void onDestroy() {
		Log.i(this.getClass().getName(), "Service terminated");
		Log.i(this.getClass().getName(), "Current Threadname" + Thread.currentThread().getName());
		Log.i(this.getClass().getName(), "Current Thread count" + Thread.activeCount());
    }

    private class CarryOutServiceTask extends AsyncTask<Void, Integer, Void> {

		@Override
		//Perform actual service task - runs in new thread
		protected Void doInBackground(Void... params) {
			Log.i(this.getClass().getName(), "Current Threadname " + Thread.currentThread().getName());
			Log.i(this.getClass().getName(), "Current Thread count" + Thread.activeCount());
			
	        for (int i = 1; i <= FIBONACCI_LIMIT; i++) {
	            Log.i(this.getClass().getName(), "Fibonacci number " + i + ": " + fib(i));
	            //Update progress
	        	publishProgress((int) ((i/(float)FIBONACCI_LIMIT)*100));
	        }
	        publishProgress(100);
			return null;
		}
    	//Update progress by use of Intent
		protected void onProgressUpdate(Integer... progress) {
			Log.i(this.getClass().getName(), "Progress: " + progress[0]);
	         Intent intent = new Intent(PROGRESS_STATUS_INTENT);
	         intent.putExtra(PROGRESS_STATUS_VALUE, progress[0]);
	         AnotherStartedService.this.sendBroadcast(intent);
	     }
		/**
		 * Calculate Fibonacci number
		 * @param n
		 * @return
		 */
	    public long fib(int n) {
//			Log.i(this.getClass().getName(), "Current Thread " + Thread.currentThread().getName());
//			Log.i(this.getClass().getName(), "Current Thread " + Thread.activeCount());
			
	        if (n <= 1) return n;
	        else return fib(n-1) + fib(n-2);
	    }

    }
}
