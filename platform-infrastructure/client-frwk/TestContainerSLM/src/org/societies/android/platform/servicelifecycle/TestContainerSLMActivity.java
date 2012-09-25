package org.societies.android.platform.servicelifecycle;

import org.societies.android.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor;
import org.societies.android.api.internal.servicemonitor.InstalledAppInfo;
import org.societies.android.api.servicelifecycle.AService;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.android.api.servicelifecycle.IServiceUtilities;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.servicemonitor.CoreServiceMonitor;
import org.societies.android.platform.servicemonitor.ServiceDiscoveryLocal;
import org.societies.android.platform.servicemonitor.ServiceUtilitiesLocal;
import org.societies.android.platform.servicemonitor.ServiceUtilitiesRemote;
import org.societies.api.identity.INetworkNode;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

public class TestContainerSLMActivity extends Activity {
	
	//Enter local user credentials and domain name
	private static final String USER_NAME = "alan";
	private static final String USER_PASS = "ala";
	private static final String XMPP_DOMAIN = "societies.bespoke";
	

    private IServiceDiscovery serviceDisco;
    private boolean serviceDiscoConnected = false;
    
    private ICoreServiceMonitor coreServiceMonitor;
    private boolean connectedtoCoreMonitor = false;
    
    private IServiceUtilities serviceUtil;
    private boolean serviceUtilConnected = false;
    private boolean serviceUtilitiesRemote = false;
	private Messenger remoteUtilitiesMessenger = null;

    private static final String LOG_TAG = TestContainerSLMActivity.class.getName();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //CREATE INTENT FOR SERVICE DISCO AND BIND
        Intent intentServiceDisco = new Intent(this.getApplicationContext(), ServiceDiscoveryLocal.class);
        this.getApplicationContext().bindService(intentServiceDisco, serviceDiscoConnection, Context.BIND_AUTO_CREATE);
        
        //CREATE INTENT FOR CORE SERVICE MONITOR AND BIND
        Intent intentServiceMon = new Intent(this.getApplicationContext(), CoreServiceMonitor.class);
        this.getApplicationContext().bindService(intentServiceMon, coreServiceMonitorConnection, Context.BIND_AUTO_CREATE);
        
        //CREATE INTENT FOR CORE SERVICE MONITOR AND BIND
        Intent intentServiceUtil = new Intent(this.getApplicationContext(), ServiceUtilitiesLocal.class);
        this.getApplicationContext().bindService(intentServiceUtil, serviceUtilConnection, Context.BIND_AUTO_CREATE);
        
        //Create intent for remote Service Utilities
        Intent intentUtilitiesRemote = new Intent(this.getApplicationContext(), ServiceUtilitiesRemote.class);
        this.getApplicationContext().bindService(intentUtilitiesRemote, remoteServiceUtilities, Context.BIND_AUTO_CREATE);
        
        //REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(IServiceDiscovery.GET_SERVICES);
        intentFilter.addAction(IServiceDiscovery.GET_MY_SERVICES);
        intentFilter.addAction(IServiceDiscovery.GET_SERVICE);
        intentFilter.addAction(IServiceDiscovery.SEARCH_SERVICES);
        intentFilter.addAction(IServiceUtilities.GET_MY_SERVICE_ID);
        intentFilter.addAction(CoreServiceMonitor.INSTALLED_APPLICATIONS);
        intentFilter.addAction(CoreServiceMonitor.ACTIVE_SERVICES);
        
        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //TEST THE SLM COMPONENT
        TestSLM task = new TestSLM(this);
        task.execute();
    }
    
    /**
     * IServiceDiscovery service connection
     */
    private ServiceConnection serviceDiscoConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to IServiceDiscovery service");
        	try {
	        	//GET LOCAL BINDER
        		ServiceDiscoveryLocal.LocalBinder binder = (ServiceDiscoveryLocal.LocalBinder) service;
	
	            //OBTAIN SERVICE DISCOVERY API
	            serviceDisco = (IServiceDiscovery) binder.getService();
	            serviceDiscoConnected = true;
	            Log.d(LOG_TAG, "Successfully connected to IServiceDiscovery service");
	            
        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ServiceDiscovery service");
        	serviceDiscoConnected = false;
        }
    };
    
    /**
     * CoreServiceMonitor service connection
     */
    private ServiceConnection coreServiceMonitorConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to CoreServiceMonitor service");
        	//get a local binder
        	org.societies.android.platform.servicemonitor.CoreServiceMonitor.LocalBinder binder = (org.societies.android.platform.servicemonitor.CoreServiceMonitor.LocalBinder) service;
            //obtain the service's API
            coreServiceMonitor = (ICoreServiceMonitor) binder.getService();
            connectedtoCoreMonitor = true;
            Log.d(LOG_TAG, "Successfully connected to ICoreServiceMonitor service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from CoreServiceMonitor service");
        	connectedtoCoreMonitor = false;
        }
    };

    /**
     * serviceUtilConnection service connection
     */
    private ServiceConnection serviceUtilConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to serviceUtil service");
        	//get a local binder
        	ServiceUtilitiesLocal.LocalBinder binder = (ServiceUtilitiesLocal.LocalBinder) service;
            //obtain the service's API
        	serviceUtil = (IServiceUtilities) binder.getService();
        	serviceUtilConnected = true;
        	Log.d(LOG_TAG, "Successfully connected to IServiceUtilities service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from serviceUtil service");
        	serviceUtilConnected = false;
        }
    };
    
	private ServiceConnection remoteServiceUtilities = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			serviceUtilitiesRemote = false;
        	Log.d(LOG_TAG, "Disconnecting from ServiceUtilitiesRemote");
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceUtilitiesRemote = true;
			remoteUtilitiesMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "ServiceUtilitiesRemote connected");
		}
	};

    
    public void btnSendMessage_onClick(View view) {
    	Log.d(LOG_TAG, ">>>>>>>>btnSendMessage_onClick - serviceDiscoConnected: " + serviceDiscoConnected);
		if (serviceDiscoConnected)
			serviceDisco.getMyServices("org.societies.android.platform.servicelifecycle");
    }
    
    private class TestSLM extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestSLM(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {
    		
    		loginXMPPServer(USER_NAME, USER_PASS, XMPP_DOMAIN);

    		try {//	WAIT TILL ALL THE SERVICES ARE CONNECTED
				Thread.currentThread();
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		//TEST SERVICE DISCO API
    		Log.d(LOG_TAG, ">>>>>>>>serviceDiscoConnected: " + serviceDiscoConnected);
    		if (serviceDiscoConnected)
    			serviceDisco.getMyServices("org.societies.android.platform.servicelifecycle");
    		
    		//TEST CORE SERVICE MONITOR API
    		Log.d(LOG_TAG, ">>>>>>>>connectedtoCoreMonitor: " + connectedtoCoreMonitor);
    		if (connectedtoCoreMonitor) {
	            coreServiceMonitor.activeServices("org.societies.android.platform.servicelifecycle");
	            coreServiceMonitor.getInstalledApplications("org.societies.android.platform.servicelifecycle");
	            coreServiceMonitor.startActivity("org.societies.android.platform.servicelifecycle", "org.societies.AndroidAgentTester");
    		}
    		
    		//TEST SERVICE UTILITIES API
    		Log.d(LOG_TAG, ">>>>>>>>serviceUtilConnected: " + serviceUtilConnected);
    		if (serviceUtilConnected)
    			serviceUtil.getMyServiceId("org.societies.android.platform.servicelifecycle");    		
    		
    		//Test Remote Service Utilities
    		Log.d(LOG_TAG, ">>>>>>>>connected to ServiceUtilitiesRemote: " + serviceUtilitiesRemote);
    		try {
        		String targetMethod = IServiceUtilities.methodsArray[0];
        		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IServiceUtilities.methodsArray, targetMethod), 0, 0);

        		Bundle outBundle = new Bundle();
        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), "org.societies.android.platform.servicelifecycle");
        		outMessage.setData(outBundle);

    			remoteUtilitiesMessenger.send(outMessage);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
 
    		return null;
    	}
    }
    
    private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if ((intent.getAction().equals(IServiceDiscovery.GET_SERVICES)) || (intent.getAction().equals(IServiceDiscovery.GET_MY_SERVICES))) {
				//UNMARSHALL THE SERVICES FROM Parcels BACK TO Services
				Parcelable parcels[] =  intent.getParcelableArrayExtra(IServiceDiscovery.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					AService aservice = (AService) parcels[i];
					Log.d(LOG_TAG, ">>>>>GET MY SERVICES RESULTS:\nService Name: " + aservice.getServiceName());
					Log.d(LOG_TAG, "Service Description: " + aservice.getServiceDescription());
				}
			}
			
			if (intent.getAction().equals(IServiceUtilities.GET_MY_SERVICE_ID)) {
				//UNMARSHALL THE ID FROM THE RETURNED PARCEL
				Parcelable parcel =  intent.getParcelableExtra(IServiceUtilities.INTENT_RETURN_VALUE);
				AServiceResourceIdentifier sri =  (AServiceResourceIdentifier) parcel;
				Log.d(LOG_TAG, ">>>>>GET MY SERVICE ID RESULTS:\nSRI.identifier: " + sri.getIdentifier().toString());
				Log.d(LOG_TAG, "SRI.ServiceInstanceId: " + sri.getServiceInstanceIdentifier());
			}
			
			if (intent.getAction().equals(CoreServiceMonitor.INSTALLED_APPLICATIONS)) {
				//UNMARSHALL THE APPS FROM Parcels BACK TO InstalledAppInfo
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CoreServiceMonitor.INTENT_RETURN_KEY);
				for (int i = 0; i < parcels.length; i++) {
					InstalledAppInfo app = (InstalledAppInfo) parcels[i];
					Log.d(LOG_TAG, ">>>>>INSTALLED APPS RESULTS:\nApp Name: " + app.getApplicationName());
					Log.d(LOG_TAG, "Package Name: " + app.getPackageName());
				}
			}
			
			if (intent.getAction().equals(CoreServiceMonitor.ACTIVE_SERVICES)) {
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CoreServiceMonitor.INTENT_RETURN_KEY);
				for (int i = 0; i < parcels.length; i++) {
					ActivityManager.RunningServiceInfo info = (ActivityManager.RunningServiceInfo) parcels[i];
					Log.d(LOG_TAG, ">>>>>ACTIVE SERVICES RESULTS:\nName: " + info.clientLabel);
					Log.d(LOG_TAG, "Package: " + info.clientPackage);
				}
			}
			
			if (intent.getAction().equals(CoreServiceMonitor.START_ACTIVITY)) {
				Parcelable parcel =  intent.getParcelableExtra(CoreServiceMonitor.INTENT_RETURN_KEY);
				Log.d(LOG_TAG, ">>>>>START ACTIVITY: " + parcel.toString() );
			}
		}
	};
	
	/**
	 * Login to Domain server
	 * 
	 * @param userName
	 * @param userPassword
	 * @param domain
	 */
	public void loginXMPPServer(String userName, String userPassword, String domain) {
		Log.d(LOG_TAG, "loginXMPPServer user: " + userName + " pass: " + userPassword + " domain: " + domain);

		ClientCommunicationMgr ccm = new ClientCommunicationMgr(this);
		
		INetworkNode networkNode = ccm.login(userName, domain, userPassword);
	}

}