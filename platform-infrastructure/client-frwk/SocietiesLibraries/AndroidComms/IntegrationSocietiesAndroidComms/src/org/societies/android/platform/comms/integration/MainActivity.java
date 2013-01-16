package org.societies.android.platform.comms.integration;


import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.utilities.ServiceMethodTranslator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String SERVICE_ACTION = "org.societies.android.platform.comms.app.ServicePlatformCommsRemote";
    
    //This is passed to the remote service as a parameter and used to limit the sent broadcast
    private static final String CLIENT_NAME = "org.societies.android.platform.comms.integration";
    
    //Modify these constants to suit local XMPP server
    
    private static final String XMPP_DOMAIN = "societies.bespoke";
    private static final String XMPP_IDENTIFIER = "alan";
    private static final String XMPP_PASSWORD = "midge";
    private static final String XMPP_BAD_IDENTIFIER = "godzilla";
    private static final String XMPP_BAD_PASSWORD = "smog";
    private static final String XMPP_NEW_IDENTIFIER = "gollum";
    private static final String XMPP_NEW_PASSWORD = "precious";
    private static final String XMPP_RESOURCE = "GalaxyNexus";
    private static final String XMPP_SUCCESSFUL_JID = XMPP_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final String XMPP_NEW_JID = XMPP_NEW_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final int XMPP_PORT = 5222;
    private static final String XMPP_DOMAIN_AUTHORITY = "danode." + XMPP_DOMAIN;

    private static final String SIMPLE_XML_MESSAGE = "<iq from='romeo@montague.net/orchard to='juliet@capulet.com/balcony'> " +
    													"<query xmlns='http://jabber.org/protocol/disco#info'/></iq>";
    
	private final String elementNames [] = {"cssManagerMessageBean", "cssManagerResultBean"};
    private final String nameSpaces [] = {"http://societies.org/api/schema/cssmanagement"};

    
    private boolean boundToService;
	private Messenger targetService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupBroadcastReceiver();
        bindToService();
    }
    
    protected void onStop() {
    	super.onStop();
    	if (boundToService) {
    		unbindService(serviceConnection);
    	}
    }

    
    /**
     * Call a real out-of-process service. Process involves:
     * 1. Select valid method signature
     * 2. Create message with corresponding index number
     * 3. Create a bundle (cf. http://developer.android.com/reference/android/os/Bundle.html) for restrictions on data types
     * 4. Add parameter values. The values are held in key-value pairs with the parameter name being the key
     * 5. Send message
     * 
     */
    private void userLogin() {
    	if (boundToService) {
    		InvokeRemoteMethod invoke  = new InvokeRemoteMethod(CLIENT_NAME);
    		invoke.execute();
    	}
    }
    
    private void configureService() {
    	if (boundToService) {
    		ConfigureRemoteService configure = new ConfigureRemoteService(CLIENT_NAME);
    		configure.execute();
    	}
    }
    
    private void sendMessage() {
    	if (boundToService) {
    		SendMessageAsync sendmessage = new SendMessageAsync(CLIENT_NAME);
    		sendmessage.execute();
    	}
    }
    
    private void registerElements() {
    	if (boundToService) {
    		RegisterElementsAsync register = new RegisterElementsAsync(CLIENT_NAME);
    		register.execute();
    	}
    	
    }
    
    private void bindToService() {
    	Intent serviceIntent = new Intent(SERVICE_ACTION);
    	Log.d(LOG_TAG, "Bind to Societies Android Comms Service: ");
    	bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }
    
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			boundToService = false;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			boundToService = true;
			targetService = new Messenger(service);
	    	Log.d(this.getClass().getName(), "Societies Android Comms Service connected: ");
	    	configureService();
		}
	};

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER);
        intentFilter.addAction(XMPPAgent.DESTROY_MAIN_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE);
        intentFilter.addAction(XMPPAgent.GET_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_RESULT);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_ERROR);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_EXCEPTION);
        intentFilter.addAction(XMPPAgent.SEND_IQ_RESULT);
        intentFilter.addAction(XMPPAgent.SEND_IQ_ERROR);
        intentFilter.addAction(XMPPAgent.SEND_IQ_EXCEPTION);
        intentFilter.addAction(XMPPAgent.IS_CONNECTED);
        intentFilter.addAction(XMPPAgent.LOGIN);
        intentFilter.addAction(XMPPAgent.LOGOUT);
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER);
        intentFilter.addAction(XMPPAgent.CONFIGURE_AGENT);
        intentFilter.addAction(XMPPAgent.REGISTER_RESULT);
        intentFilter.addAction(XMPPAgent.REGISTER_EXCEPTION);
        return intentFilter;
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
				Log.d(LOG_TAG, Boolean.toString(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false)));
			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
				Log.d(LOG_TAG, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
				Log.d(LOG_TAG, intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
			} else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
				Log.d(LOG_TAG, "Logged in JID: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
				registerElements();
			} else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
				Log.d(LOG_TAG, Boolean.toString(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false)));
			} else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER)) {
				Log.d(LOG_TAG, Boolean.toString(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false)));
			} else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
				Log.d(LOG_TAG, Boolean.toString(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false)));
				userLogin();
			}
		}
    }

    
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        this.registerReceiver(receiver, createTestIntentFilter());    
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    
	/**
     * 
     * Async task to invoke remote service method
     *
     */
    private class InvokeRemoteMethod extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeRemoteMethod.class.getName();
    	private String packageName;
    	private String client;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public InvokeRemoteMethod(String client) {
    		this.client = client;
    	}

    	protected Void doInBackground(Void... args) {

    		String targetMethod = XMPPAgent.methodsArray[10];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);

    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), XMPP_IDENTIFIER);
    		Log.d(LOCAL_LOG_TAG, "Identifier: " + XMPP_IDENTIFIER);
    		
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), XMPP_DOMAIN);
    		Log.d(LOCAL_LOG_TAG, "Domain: " + XMPP_DOMAIN);
    		
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), XMPP_PASSWORD);
    		Log.d(LOCAL_LOG_TAG, "Password: " + XMPP_PASSWORD);
    		
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);


    		try {
				targetService.send(outMessage);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }
    
	/**
	 * Test if callback class from one app can be used in another app
	 *
	 */

	/**
     * 
     * Async task to invoke remote service method
     *
     */
    private class ConfigureRemoteService extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = ConfigureRemoteService.class.getName();
    	private String packageName;
    	private String client;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public ConfigureRemoteService(String client) {
    		this.client = client;
    	}

    	protected Void doInBackground(Void... args) {

    		String targetMethod = XMPPAgent.methodsArray[17];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);

    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), XMPP_DOMAIN_AUTHORITY);
    		Log.d(LOCAL_LOG_TAG, "Domain Authority: " + XMPP_DOMAIN_AUTHORITY);
    		
    		outBundle.putInt(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), XMPP_PORT);
    		Log.d(LOCAL_LOG_TAG, "XMPP port" + XMPP_PORT);
    		
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), XMPP_RESOURCE);
    		Log.d(LOCAL_LOG_TAG, "JID resource: " + XMPP_RESOURCE);
    		
    		outBundle.putBoolean(ServiceMethodTranslator.getMethodParameterName(targetMethod, 4), false);
    		Log.d(LOCAL_LOG_TAG, "Debug : " + false);
    		
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);

    		try {
				targetService.send(outMessage);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }
    
	/**
     * 
     * Async task to invoke remote service method
     *
     */
    private class SendMessageAsync extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = SendMessageAsync.class.getName();
    	private String packageName;
    	private String client;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public SendMessageAsync(String client) {
    		this.client = client;
    	}

    	protected Void doInBackground(Void... args) {

    		String targetMethod = XMPPAgent.methodsArray[3];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);

    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), SIMPLE_XML_MESSAGE);
    		Log.d(LOCAL_LOG_TAG, "Simple XML message: " + XMPP_DOMAIN_AUTHORITY);
    		
     		
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);

    		try {
				targetService.send(outMessage);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }

	/**
     * 
     * Async task to invoke remote service method
     *
     */
    private class RegisterElementsAsync extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = RegisterElementsAsync.class.getName();
    	private String packageName;
    	private String client;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public RegisterElementsAsync(String client) {
    		this.client = client;
    	}

    	protected Void doInBackground(Void... args) {

    		String targetMethod = XMPPAgent.methodsArray[0];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);

    		outBundle.putStringArray(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), elementNames);
    		Log.d(LOCAL_LOG_TAG, "Element Names array: " + elementNames.length);
    		
    		outBundle.putStringArray(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), nameSpaces);
    		Log.d(LOCAL_LOG_TAG, "Namespaces array: " + nameSpaces.length);
    		
    		outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), (Parcelable) (new TestCallback()));
    		Log.d(LOCAL_LOG_TAG, "Callback");
    		
     		
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);

    		try {
				targetService.send(outMessage);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }

}
