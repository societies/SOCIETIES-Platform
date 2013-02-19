package org.societies.android.platform.servicemonitor;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * This service extends the IntentService class to ensure that the 
 * service is threadsafe as intents are queued and that the service work
 * is carried out in a different thread than the main application thread.
 */
public class StartedService extends IntentService {
	private static final int FIBONACCI_LIMIT = 10;
	
	public StartedService() {

		super("StartedService");
		
		Log.i(this.getClass().getName(), "Current Thread" + Thread.currentThread().getName());
		Log.i(this.getClass().getName(), "Current Thread" + Thread.activeCount());
	}

    @Override
    public void onCreate() {
		Log.i(this.getClass().getName(), "Service being created");
		Log.i(this.getClass().getName(), "Current Thread" + Thread.currentThread().getName());
		Log.i(this.getClass().getName(), "Current Thread" + Thread.activeCount());
        super.onCreate();
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(this.getClass().getName(), "Service being started");
    	
    	return super.onStartCommand(intent, flags, startId);
    }
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(this.getClass().getName(), "Current Thread " + Thread.currentThread().getName());
		Log.i(this.getClass().getName(), "Current Thread " + Thread.activeCount());
		
        for (int i = 1; i <= FIBONACCI_LIMIT; i++)
            Log.i(this.getClass().getName(), "Fibonacci number " + i + ": " + fib(i));

	}

	/**
	 * Calculate Fibonacci number
	 * @param n
	 * @return
	 */
    public long fib(int n) {
//		Log.i(this.getClass().getName(), "Current Thread " + Thread.currentThread().getName());
//		Log.i(this.getClass().getName(), "Current Thread " + Thread.activeCount());
		
        if (n <= 1) return n;
        else return fib(n-1) + fib(n-2);
    }

    @Override
    public void onDestroy() {
		Log.i(this.getClass().getName(), "Service terminated");
		Log.i(this.getClass().getName(), "Current Thread" + Thread.currentThread().getName());
		Log.i(this.getClass().getName(), "Current Thread" + Thread.activeCount());
        super.onDestroy();
        
    }
}
