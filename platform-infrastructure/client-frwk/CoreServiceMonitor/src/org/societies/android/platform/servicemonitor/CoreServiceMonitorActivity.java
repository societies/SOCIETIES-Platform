package org.societies.android.platform.servicemonitor;

import org.societies.android.platform.interfaces.CoreMessages;
import org.societies.android.platform.interfaces.ICoreServiceMonitor;
import org.societies.android.platform.servicemonitor.CoreServiceMonitorSameProcess.LocalBinder;

import android.R.bool;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;

public class CoreServiceMonitorActivity extends Activity {
	private boolean ipBoundToService = false;
	private boolean opBoundToService = false;
	private ICoreServiceMonitor targetIPService = null;
	private Messenger targetOPService = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    @Override
    protected void onStart() {
    	super.onStart();
    	Intent ipIntent = new Intent(this, CoreServiceMonitorSameProcess.class);
    	Intent opIntent = new Intent(this, CoreServiceMonitorDifferentProcess.class);
    	bindService(ipIntent, inProcessServiceConnection, Context.BIND_AUTO_CREATE);
    	bindService(opIntent, outProcessServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    protected void onStop() {
    	super.onStop();
    	if (ipBoundToService) {
    		unbindService(inProcessServiceConnection);
    	}
    	if (opBoundToService) {
    		unbindService(outProcessServiceConnection);
    	}
    }
    
    public void onButtonInprocessClick(View view) {
    	if (ipBoundToService) {
    		EditText text = (EditText) findViewById(R.id.editTxtServiceResult);
    		text.setText(targetIPService.getGreeting());
    	}
    }
    
    public void onButtonOutprocessClick(View view) {
    	if (opBoundToService) {
    		Message outMessage = Message.obtain(null, CoreMessages.MESSAGE_HELLO, 0, 0);
    		try {
				targetOPService.send(outMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    }
    
    
    
	private ServiceConnection inProcessServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			ipBoundToService = false;
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			targetIPService = (ICoreServiceMonitor) binder.getService();
			ipBoundToService = true;
			
		}
	};
	
	private ServiceConnection outProcessServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			opBoundToService = false;
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			opBoundToService = true;
			targetOPService = new Messenger(service);
			
			
		}
	};

}