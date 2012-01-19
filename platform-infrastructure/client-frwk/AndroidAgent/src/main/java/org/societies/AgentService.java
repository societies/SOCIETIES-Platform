package org.societies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.impl.XMPPClient;
import org.societies.ipc.Skeleton;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AgentService extends Service {
	
	private static final Logger log = LoggerFactory.getLogger(AgentService.class);
	
	private Skeleton skeleton;
	
	@Override
    public IBinder onBind(Intent intent) {  
    	log.debug("onBind");      
    	return skeleton.messenger.getBinder();    			
    }
    
    @Override
    public void onCreate()
    {
    	log.debug("onCreate");      
    	skeleton = new Skeleton(new XMPPClient());    	
    }
    
    @Override
    public void onDestroy()
    {
    	log.debug("onDestroy");      
    }
}
