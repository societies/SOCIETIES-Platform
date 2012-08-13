package org.societies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	private static final String DEFAULT_CONFIG_FILE_NAME = "defaultconfig.properties";
	private static final String CONFIG_FILE_NAME = "AndroidAgent.properties";
	
	private static XMPPClient xmppClient;
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
    	try {
	    	if(xmppClient == null)
	    		xmppClient = new XMPPClient(getConfig());
	    	if(skeleton == null) {
	        	skeleton = new Skeleton(xmppClient);		    		
	    	}
    	} catch (Exception e) {
	    	Log.e(LOG_TAG, e.getMessage(), e);
		}
    }
    
    @Override
    public void onDestroy()
    {
    	Log.d(LOG_TAG, "onDestroy");
    	skeleton = null;
    }
    
    private ResourceBundle getConfig() throws IOException {
    	Log.d(LOG_TAG, "getConfig");
    	ResourceBundle config = null;

    	try {
    		File file = new File(getExternalFilesDir(null), CONFIG_FILE_NAME);
    		if(file.exists())
    			config = new PropertyResourceBundle(new FileInputStream(file));
    		else
    			copyDefaultConfigToExternal();
    	} catch(IOException e) {    
    		Log.e(LOG_TAG, e.getMessage(), e);
    	}
	    
    	if(config == null) {
    		config = new PropertyResourceBundle(getAssets().open(DEFAULT_CONFIG_FILE_NAME));;    		
    	}    		
    	
    	return config;
    }	
    
    private void copyDefaultConfigToExternal() throws IOException {
    	Log.d(LOG_TAG, "copyDefaultConfigToExternal");
    	InputStream in = getAssets().open(DEFAULT_CONFIG_FILE_NAME);
    	OutputStream out = new FileOutputStream(new File(getExternalFilesDir(null), CONFIG_FILE_NAME));
    	byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }
}
