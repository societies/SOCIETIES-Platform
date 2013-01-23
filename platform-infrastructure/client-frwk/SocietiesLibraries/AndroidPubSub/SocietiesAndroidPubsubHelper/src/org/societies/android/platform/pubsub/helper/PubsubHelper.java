package org.societies.android.platform.pubsub.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.pubsub.Affiliation;
import org.societies.android.api.pubsub.IPubsubClient;
import org.societies.android.api.pubsub.IPubsubService;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.api.pubsub.SubscriptionState;
import org.societies.android.platform.androidutils.PacketMarshaller;
import org.societies.api.identity.IIdentity;
import org.societies.utilities.DBC.Dbc;
import org.w3c.dom.Element;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;


public class PubsubHelper implements IPubsubClient {
	private static final String LOG_TAG = PubsubHelper.class.getName();
    private static final String SERVICE_ACTION = "org.societies.android.platform.comms.app.ServicePlatformPubsubRemote";
	private boolean boundToService;
	private Messenger targetService = null;
	private String clientPackageName;
	private Random randomGenerator;

	private Context androidContext;
	private PacketMarshaller marshaller = new PacketMarshaller();
	private Map<Long, ICommCallback> xmppCallbackMap;
	private Map<Long, IMethodCallback> methodCallbackMap;
	private Map<String,Class<?>> elementToClass;

	private String identityJID;
	private String domainAuthority;

	private boolean loginCompleted;
	private BroadcastReceiver receiver;
	private IMethodCallback bindCallback;

	public PubsubHelper(Context androidContext) {
		Dbc.require("Android context must be supplied", null != androidContext);
		
		Log.d(LOG_TAG, "Instantiate PubsubClient");
		this.androidContext = androidContext;
		
		this.clientPackageName = this.androidContext.getApplicationContext().getPackageName();
		this.randomGenerator = new Random(System.currentTimeMillis());
		
		this.xmppCallbackMap = Collections.synchronizedMap(new HashMap<Long, ICommCallback>());
		this.methodCallbackMap = Collections.synchronizedMap(new HashMap<Long, IMethodCallback>());
		this.elementToClass = Collections.synchronizedMap(new HashMap<String, Class<?>>());
		
		this.setupBroadcastReceiver();
	}
	
	/**
	 * Binds to Android Pubsub Service
	 * @param bindCallback callback 
	 */
	public void bindPubsubService(IMethodCallback bindCallback) {
		Dbc.require("Service Bind Callback cannot be null", null != bindCallback);
		Log.d(LOG_TAG, "Bind to Android Pubsub Service");

		this.bindCallback = bindCallback;
		this.bindToPubsubService();
	}
	/**
	 * Unbinds from the Android Pubsub service
	 * 
	 * @return true if no more requests queued
	 */
	public boolean unbindCommsService() {
		Log.d(LOG_TAG, "Unbind from Android Pubsub Service");
		boolean retValue = false;
		synchronized (this.methodCallbackMap) {
			synchronized (this.xmppCallbackMap) {
				if (this.methodCallbackMap.isEmpty() && this.xmppCallbackMap.isEmpty()) {
					this.teardownBroadcastReceiver();
					unBindService();
					retValue = true;
				} else {
					Log.d(LOG_TAG, "Methodcallback entries: " + this.methodCallbackMap.size());
					Log.d(LOG_TAG, "XmppCallbackMap entries: " + this.xmppCallbackMap.size());
				}
			}
		}
		
		return retValue;
	}


	@Override
	/**
	 * No longer required as JAXB unsupported
	 */
	public void addJaxbPackages(List<String> arg0) throws JAXBException {
	}

	@Override
	public void addSimpleClasses(List<String> classList) throws ClassNotFoundException {
		for (String c : classList) {
			Class<?> clazz = Class.forName(c);
			Root rootAnnotation = clazz.getAnnotation(Root.class);
			Namespace namespaceAnnotation = clazz.getAnnotation(Namespace.class);
			if (rootAnnotation!=null && namespaceAnnotation!=null) {
				elementToClass.put("{"+namespaceAnnotation.reference()+"}"+rootAnnotation.name(),clazz);
			}
		}
	}

	@Override
	public List<String> discoItems(IIdentity arg0, String arg1)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ownerCreate(IIdentity arg0, String arg1) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ownerDelete(IIdentity arg0, String arg1) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<IIdentity, Affiliation> ownerGetAffiliations(IIdentity arg0,
			String arg1) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IIdentity, SubscriptionState> ownerGetSubscriptions(
			IIdentity arg0, String arg1) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ownerPurgeItems(IIdentity arg0, String arg1) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.pubsub.IPubsubClient#ownerSetAffiliations(org.societies.api.identity.IIdentity, java.lang.String, java.util.Map)
	 */
	@Override
	public void ownerSetAffiliations(IIdentity pubsubService, String node,
			Map<IIdentity, Affiliation> affiliations) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ownerSetSubscriptions(IIdentity arg0, String arg1, Map<IIdentity, SubscriptionState> arg2) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publisherDelete(IIdentity arg0, String arg1, String arg2)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String publisherPublish(IIdentity arg0, String arg1, String arg2, Object arg3) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Element> subscriberRetrieveLast(IIdentity arg0, String arg1, String arg2) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Element> subscriberRetrieveSpecific(IIdentity arg0, String arg1, String arg2, List<String> arg3) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean subscriberSubscribe(IIdentity arg0, String arg1, ISubscriber arg2) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean subscriberUnsubscribe(IIdentity arg0, String arg1, ISubscriber arg2) throws XMPPError, CommunicationException {
		return false;
	}
	
	/**
	 * Bind to remote Android Pubsub Service
	 */
    private void bindToPubsubService() {
    	Intent serviceIntent = new Intent(SERVICE_ACTION);
    	Log.d(LOG_TAG, "Bind to Societies Android Pubsub Service");
    	this.androidContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbind from remote Android Pubsub service
     */
    private void unBindService() {
    	Log.d(LOG_TAG, "Unbind from Societies Android Pubsub Service");
		if (this.boundToService) {
        	this.androidContext.unbindService(serviceConnection);
		}
    }
    
    /**
     * Create Service Connection to remote service. Assumes that XMPP login and configuration 
     * has taken place
     */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			PubsubHelper.this.boundToService = false;
	    	Log.d(LOG_TAG, "Societies Android Pubsub Service disconnected");
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			PubsubHelper.this.boundToService = true;
			targetService = new Messenger(service);
	    	Log.d(LOG_TAG, "Societies Android Pubsub Service connected");
		}
	};


    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        this.receiver = new MainReceiver();
        this.androidContext.registerReceiver(this.receiver, createTestIntentFilter());    
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
        Log.d(LOG_TAG, "Tear down broadcast receiver");
    	this.androidContext.unregisterReceiver(this.receiver);
    }
    
    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Pubsub. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			Log.d(LOG_TAG, "Received action CALL_ID_KEY: " + intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0));
			long callbackId = intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0);

//			if (intent.getAction().equals(IPubsubService.???)) {
//				synchronized(PubsubClient.this.methodCallbackMap) {
//					IMethodCallback callback = PubsubClient.this.methodCallbackMap.get(callbackId);
//					if (null != callback) {
//						PubsubClient.this.methodCallbackMap.remove(callbackId);
//						callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
//					}
//				}
//			}
		}
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IPubsubService.BIND_TO_ANDROID_COMMS);
        intentFilter.addAction(IPubsubService.UNBIND_FROM_ANDROID_COMMS);
        intentFilter.addAction(IPubsubService.DISCO_ITEMS);
        intentFilter.addAction(IPubsubService.OWNER_CREATE);
        intentFilter.addAction(IPubsubService.OWNER_DELETE);
        intentFilter.addAction(IPubsubService.OWNER_PURGE_ITEMS);
        intentFilter.addAction(IPubsubService.PUBLISHER_DELETE);
        intentFilter.addAction(IPubsubService.PUBLISHER_PUBLISH);
        intentFilter.addAction(IPubsubService.SUBSCRIBER_SUBSCRIBE);
        intentFilter.addAction(IPubsubService.SUBSCRIBER_UNSUBSCRIBE);
        
        return intentFilter;
    }
}
