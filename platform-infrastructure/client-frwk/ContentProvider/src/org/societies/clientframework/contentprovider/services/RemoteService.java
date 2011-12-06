package org.societies.clientframework.contentprovider.services;

import java.util.ArrayList;

import javax.naming.Binding;

import org.societies.clientframework.contentprovider.Constants;
import org.societies.clientframework.contentprovider.Settings;
import org.societies.clientframework.contentprovider.activities.TestActivity;
import org.societies.clientframework.contentprovider.database.StoreResultsDB;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of implementing an application service that runs in a
 * different process than the application.  Because it can be in another
 * process, we must use IPC to interact with it.  The
 * {@link Controller} and {@link Binding} classes
 * show how to interact with the service.
 * 
 * <p>Note that most applications <strong>do not</strong> need to deal with
 * the complexity shown here.  If your application simply has a service
 * running in its own process, the {@link LocalService} sample shows a much
 * simpler way to interact with it.
 */
public class RemoteService extends Service {
    /**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    final RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();

    int mValue = 0;
    StoreResultsDB  storeDB;
    

    @Override
    public void onCreate() {
        
    	
    	Log.v(Constants.TAG, "Service INITIALIZATION....");
    	
        // While this service is running, it will continually increment a
        // number.  Send the first message that is used to perform the
        // increment.
        mHandler.sendEmptyMessage(REPORT_MSG);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Remote Stopped", Toast.LENGTH_SHORT).show();

        // Unregister all callbacks.
        mCallbacks.kill();

        storeDB.close();
        // Remove the next pending message to increment the counter, stopping
        // the increment loop.
        mHandler.removeMessages(REPORT_MSG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        
    	Log.v(Constants.TAG, "onBind Remote Service Action:"+intent.getAction());
    	
    	// Init DB Connector...
        storeDB = new StoreResultsDB(this);
    	
    	// Select the interface to return.  If your service only implements
        // a single interface, you can just return it here without checking
        // the Intent.
        if (IRemoteService.class.getName().equals(intent.getAction())) {
            return mBinder;
        }
        if (IConsumer.class.getName().equals(intent.getAction())) {
            return mConsumerBinder;
        }
        
        
        return null;
    }

    /**
     * The IRemoteInterface is defined through IDL
     */
    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
       
    	public void registerCallback(IRemoteServiceCallback cb) {
            if (cb != null) mCallbacks.register(cb);
        }
        public void unregisterCallback(IRemoteServiceCallback cb) {
            if (cb != null) mCallbacks.unregister(cb);
        }
        
        
    };

    /**
     * Consumer Interface.
     */
    private final IConsumer.Stub mConsumerBinder = new IConsumer.Stub() {

    	public boolean store(String key, String value){
        	return storeDB.addValue(key, value);
        }
		public String getValue(String key) throws RemoteException {
			return storeDB.getValue(key);
		}
		public boolean removeKey(String key) throws RemoteException {
			return storeDB.removeKey(key);
		}
		public String[] getKeys() throws RemoteException {
			return storeDB.getKeys();
		}
		public void resetDB() throws RemoteException {
			storeDB.resetDB();
		}
        
    };

   
    public void onTaskRemoved(Intent rootIntent) {
        Toast.makeText(this, "Task removed: " + rootIntent, Toast.LENGTH_LONG).show();
    }

    private static final int REPORT_MSG = 1;

    /**
     * Our Handler used to execute operations on the main thread.  This is used
     * to schedule increments of our value.
     */
    private final Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {

                // It is time to bump the value!
                case REPORT_MSG: {
                    // Up it goes.
                    int value = ++mValue;

                    // Broadcast to all clients the new value.
                    final int N = mCallbacks.beginBroadcast();
                    for (int i=0; i<N; i++) {
                        try {
                            mCallbacks.getBroadcastItem(i).valueChanged(value);
                        } catch (RemoteException e) {
                            // The RemoteCallbackList will take care of removing
                            // the dead object for us.
                        }
                    }
                    mCallbacks.finishBroadcast();

                    // Repeat every 1 second.
                    sendMessageDelayed(obtainMessage(REPORT_MSG), 1*1000);
                } break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

   
}