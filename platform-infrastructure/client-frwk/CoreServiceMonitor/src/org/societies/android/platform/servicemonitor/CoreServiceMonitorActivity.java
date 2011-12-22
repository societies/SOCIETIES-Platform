package org.societies.android.platform.servicemonitor;

import org.societies.android.platform.interfaces.ICoreServiceMonitor;
import org.societies.android.platform.interfaces.ServiceMethodTranslator;
import org.societies.android.platform.servicemonitor.CoreServiceMonitorSameProcess.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
    /**
     * Call an in-process service. Service consumer simply calls service API and can use 
     * return value
     *  
     * @param view
     */
    public void onButtonInprocessClick(View view) {
    	if (ipBoundToService) {
    		EditText text = (EditText) findViewById(R.id.editTxtServiceResult);
    		text.setText(targetIPService.getGreeting("to me"));
    	}
    }
    /**
     * Call an out-of-process service. Process involves:
     * 1. Select valid method signature
     * 2. Create message with corresponding index number
     * 3. Create a bundle (cf. http://developer.android.com/reference/android/os/Bundle.html) for restrictions on data types 
     * 4. Add parameter values. The values are held in key-value pairs with the parameter name being the key
     * 5. Send message
     * 
     * Currently no return value is returned. To do so would require a reverse binding process from the service, 
     * i.e a callback interface and handler/messenger code in the consumer. The use of intents or even selective intents (define
     * an intent that can only be intercepted by a stated application) will achieve the same result with less binding.
     * @param view
     */
    public void onButtonOutprocessClick(View view) {
    	if (opBoundToService) {
    		
    		String targetMethod = "getNumberGreeting(String appendToMessage, int number)";
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICoreServiceMonitor.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), "to Midge");
    		outBundle.putInt(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), 2);
    		outMessage.setData(outBundle);
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