package org.societies;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.societies.comm.android.ipc.Skeleton;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.societies.impl.XMPPClient;


public class AgentService extends Service {
	
	private static final String LOG_TAG = AgentService.class.getName();
	
	private static Skeleton skeleton;   
	
	@Override
    public IBinder onBind(Intent intent) {  
    	Log.d(LOG_TAG, "onBind");
    	if(skeleton != null)
    		return skeleton.messenger().getBinder();    
    	else
    		return null;
    }
    
    @Override
    public void onCreate()
    {
    	Log.d(LOG_TAG, "onCreate");
    	if(skeleton == null) {
    		try {
    			ResourceBundle config = new PropertyResourceBundle(getAssets().open("config.properties"));
        		skeleton = new Skeleton(new XMPPClient(config));	
    		} catch (Exception e) {
    	    	Log.e(LOG_TAG, e.getMessage(), e);
			}
    	}
    }
    
    @Override
    public void onDestroy()
    {
    	Log.d(LOG_TAG, "onDestroy");
    	skeleton = null;
    }
}
