package org.societies.clientframework.contentprovider.services;

import org.societies.clientframework.contentprovider.Constants;
import org.societies.clientframework.contentprovider.database.ResultsCursor;
import org.societies.clientframework.contentprovider.database.StoreResultsDB;
import org.societies.clientframework.contentprovider.database.TableOneCursor;

import android.app.Service;
import android.content.ContentValues;
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
    
	
	StoreResultsDB  storeDB;
    

    @Override
    public void onCreate() {
        
    	
    	Log.v(Constants.TAG, "Content Provider Service is started ....");
    	
    	// Init DB Connector...
        storeDB = new StoreResultsDB(this);
        Log.v(Constants.TAG, "DB initialized to store societies data");
        
        
    }

    @Override
    public void onDestroy() {
        
    	Log.v(Constants.TAG, "Content Provider Service is going to be destroied ....");
    	Toast.makeText(this, "Remote Stopped", Toast.LENGTH_SHORT).show();

        storeDB.close();
        Log.v(Constants.TAG, "DB closed");
        Log.v(Constants.TAG, "Content Provider ends");
    }

    @Override
    public IBinder onBind(Intent intent) {
        
    	Log.v(Constants.TAG, "Binding Content Provider [Action:"+intent.getAction()+"]");
    	
    	Log.v(Constants.TAG, "It is possibile to do different action based on the action selected!");
    	
        if (IConsumer.class.getName().equals(intent.getAction())) {
        	Log.v(Constants.TAG, "Return IConsumer BINDER");
        	storeDB.setTable(Constants.TABLE_RESULTS);
            return mConsumerBinder;
        }
        else if (ISocietiesConsumer.class.getName().equals(intent.getAction())){
        	Log.v(Constants.TAG, "Return ISocietiesConsumer BINDER");
        	storeDB.setTable(Constants.TABLE_ONE);
        	return mConsumer2Binder;
        }
        
        
        Log.v(Constants.TAG, "No STUB for this action ==> RETURN NULL");
        
        
        return null;
    }

//    

    /**
     * Consumer Interface.
     */
    private final IConsumer.Stub mConsumerBinder = new IConsumer.Stub() {

    	public boolean store(String key, String value){
    		ContentValues map = new ContentValues();
    		map.put(Constants.TABLE_VALUE, value);
    		return storeDB.addElementInTable(map);
        }
		public String getValue(String key) throws RemoteException {
			ResultsCursor cursor = (ResultsCursor) storeDB.getElement(key);
			if (cursor.getCount()>0){
				cursor.moveToFirst();
				return cursor.getKey();
			}
			return null;
		}
		public boolean removeKey(String key) throws RemoteException {
			return storeDB.removeKey(key);
		}
		public String[] getKeys() throws RemoteException {
			return storeDB.getKeys();
		}
		public void resetDB() throws RemoteException {
			storeDB.resetDB(Constants.TABLE_RESULTS);
		}
        
    };

    
    private final ISocietiesConsumer.Stub mConsumer2Binder = new ISocietiesConsumer.Stub() {
		
		public void storeCredential(String username, String password,String serviceName) throws RemoteException {
			
		}
		
		public String[] getServices() throws RemoteException {
			return storeDB.getServices();
		}
		
		public String getCredentialUsename(String serviceName) throws RemoteException {
			TableOneCursor cursor = (TableOneCursor) storeDB.getElement(Constants.USERNAME, serviceName);
			if (cursor==null) 			return null;
			if (cursor.getCount()==0) 	return null;
			return cursor.getValue();
		}
		
		public String getCredentialPassword(String serviceName) throws RemoteException {
			TableOneCursor cursor = (TableOneCursor) storeDB.getElement(Constants.PASSWORD, serviceName);
			if (cursor==null) 			return null;
			if (cursor.getCount()==0) 	return null;
			return cursor.getValue();
		}

		public void storeCommFwk(String server, String port) throws RemoteException {
			ContentValues map  = new ContentValues();
			map.put(Constants.TABLE_KEY, Constants.SERVERNAME);
			map.put(Constants.TABLE_SERVICE, Constants.CLIENT_FWK_SERVICE);
			map.put(Constants.TABLE_TYPE, String.class.toString());
			map.put(Constants.TABLE_VALUE, server);
			storeDB.addElementInTable(map);
			
			map  = new ContentValues();
			map.put(Constants.TABLE_KEY, Constants.SERVERPORT);
			map.put(Constants.TABLE_SERVICE, Constants.CLIENT_FWK_SERVICE);
			map.put(Constants.TABLE_TYPE, Integer.class.toString());
			map.put(Constants.TABLE_VALUE, port);
			storeDB.addElementInTable(map);
			
			
		}

		public String[] getCommFwkEndpoint() throws RemoteException {
			
			String[] endpoint = new String[2];
			endpoint[0] = "";
			endpoint[1] = "";
			
			TableOneCursor cursor = (TableOneCursor) storeDB.getElement(Constants.SERVERNAME, Constants.CLIENT_FWK_SERVICE);
			if (cursor!=null){
				if (cursor.getCount()==1){
					endpoint[0] =  cursor.getKey();
					cursor = (TableOneCursor) storeDB.getElement(Constants.SERVERPORT, Constants.CLIENT_FWK_SERVICE);
					endpoint[1] =  cursor.getKey();
				}
			}
			
			
			return endpoint;
		}
	};
    
   
    public void onTaskRemoved(Intent rootIntent) {
        Toast.makeText(this, "Task removed: " + rootIntent, Toast.LENGTH_LONG).show();
    }

    private static final int REPORT_MSG = 1;

   

   
}