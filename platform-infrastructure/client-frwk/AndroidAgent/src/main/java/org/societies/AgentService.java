package org.societies;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.impl.XMPPClient;
import org.societies.comm.android.ipc.Skeleton;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class AgentService extends Service {
	
	private static final Logger log = LoggerFactory.getLogger(AgentService.class);
	
	private static Skeleton skeleton;   
	
	@Override
    public IBinder onBind(Intent intent) {  
		Dbc.assertion(true);
    	log.debug("onBind"); 
    	if(skeleton != null)
    		return skeleton.messenger().getBinder();    
    	else
    		return null;
    }
    
    @Override
    public void onCreate()
    {
    	log.debug("onCreate");   
    	if(skeleton == null) {
//    		(new AsyncTask<Void, Void, Void>() {
//    			@Override
//    			protected Void doInBackground(Void... params) {
    		try {
    			ResourceBundle config = new PropertyResourceBundle(getAssets().open("config.properties"));
        		skeleton = new Skeleton(new XMPPClient(config));	
    		} catch (Exception e) {
    			log.error(e.getMessage(), e);
			}
//    		return null;
//    			}
//    		}).execute();
    	}
    }
    
    @Override
    public void onDestroy()
    {
    	log.debug("onDestroy");      
    }
}
