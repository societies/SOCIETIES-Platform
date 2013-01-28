package org.societies.android.platform.pubsub.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import org.societies.android.api.utilities.ServiceMethodTranslator;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
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
	private Map<String, ISubscriber> subscriberCallbackMap;
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
		this.subscriberCallbackMap = Collections.synchronizedMap(new HashMap<String, ISubscriber>());
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
	public void addSimpleClasses(List<String> classList, IMethodCallback callback) throws ClassNotFoundException {
		Dbc.require("Class list must have at least one class", null != classList && classList.size() > 0);
		Dbc.require("Method callback cannot be null", null != callback);
		Log.d(LOG_TAG, "addSimpleClasses called");
		
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
	public List<String> discoItems(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
		return null;
	}

	@Override
	public void ownerCreate(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
	}

	@Override
	public void ownerDelete(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
	}

	@Override
	public Map<IIdentity, Affiliation> ownerGetAffiliations(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
		return null;
	}

	@Override
	public Map<IIdentity, SubscriptionState> ownerGetSubscriptions(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
		return null;
	}

	@Override
	public void ownerPurgeItems(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.pubsub.IPubsubClient#ownerSetAffiliations(org.societies.api.identity.IIdentity, java.lang.String, java.util.Map)
	 */
	@Override
	public void ownerSetAffiliations(IIdentity pubsubService, String node,Map<IIdentity, Affiliation> affiliations, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
	}

	@Override
	public void ownerSetSubscriptions(IIdentity arg0, String arg1, Map<IIdentity, SubscriptionState> arg2, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
	}

	@Override
	public void publisherDelete(IIdentity arg0, String arg1, String arg2, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
	}

	@Override
	public String publisherPublish(IIdentity arg0, String arg1, String arg2, Object arg3, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
		return null;
	}

	@Override
	public List<Element> subscriberRetrieveLast(IIdentity arg0, String arg1, String arg2, IMethodCallback callback) throws XMPPError, CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
		return null;
	}

	@Override
	public List<Element> subscriberRetrieveSpecific(IIdentity arg0, String arg1, String arg2, List<String> arg3, IMethodCallback callback) throws XMPPError,
			CommunicationException {
		Dbc.require("Method callback cannot be null", null != callback);
		Dbc.invariant("Method currently unsupported", false);
		return null;
	}

	@Override
	public boolean subscriberSubscribe(IIdentity pubsubServiceID, String node, ISubscriber callback, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
		Dbc.require("Pubsub identity cannot be null", null != pubsubServiceID);
		Dbc.require("Pubsub node must be specified", null != node && node.length() > 0);
		Dbc.require("Subscriber callback cannot be null", null != callback);
		Dbc.require("Method callback cannot be null", null != methodCallback);
		Log.d(LOG_TAG, "subscriberSubscribe called for node: " + node);
		
		final String pubsubServiceJid = pubsubServiceID.getJid();
		synchronized (this.subscriberCallbackMap) {
			long remoteCallID = this.randomGenerator.nextLong();
			this.subscriberCallbackMap.put(node, callback);
			InvokeSubscriberSubscribe invoker = new InvokeSubscriberSubscribe(this.clientPackageName, pubsubServiceJid, node, remoteCallID);
			invoker.execute();
		}
		return false;
	}

	@Override
	public boolean subscriberUnsubscribe(IIdentity pubsubServiceID, String node, ISubscriber callback, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
		Dbc.require("Pubsub identity cannot be null", null != pubsubServiceID);
		Dbc.require("Pubsub node must be specified", null != node && node.length() > 0);
		Dbc.require("Subscriber callback cannot be null", null != callback);
		Dbc.require("Method callback cannot be null", null != methodCallback);
		Log.d(LOG_TAG, "subscriberUnsubscribe called for node: " + node);
		
		final String pubsubServiceJid = pubsubServiceID.getJid();
		synchronized (this.subscriberCallbackMap) {
			long remoteCallID = this.randomGenerator.nextLong();
			this.subscriberCallbackMap.remove(node);
			InvokeSubscriberUnSubscribe invoker = new InvokeSubscriberUnSubscribe(this.clientPackageName, pubsubServiceJid, node, remoteCallID);
			invoker.execute();
		}

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
    
	/**
     * 
     * Async task to invoke Pubsub node subscription
     *
     */
    private class InvokeSubscriberSubscribe extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeSubscriberSubscribe.class.getName();
    	private String client;
    	private long remoteID;
    	private String pubsubServiceJid;
    	private String node;

   	 /**
   	  * Default Constructor
   	  * 
   	  * @param client
   	  * @param pubsubServiceJid
   	  * @param node
   	  * @param remoteID
   	  */
    	public InvokeSubscriberSubscribe(String client, String pubsubServiceJid, String node, long remoteID) {
    		this.client = client;
    		this.pubsubServiceJid = pubsubServiceJid;
    		this.node = node;
    		this.remoteID = remoteID;
    	}

    	protected Void doInBackground(Void... args) {

    		String targetMethod = IPubsubService.methodsArray[6];
    		android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);

    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.pubsubServiceJid);
    		Log.d(LOCAL_LOG_TAG, "Pubsub service JID: " + this.pubsubServiceJid);
    		
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
    		Log.d(LOCAL_LOG_TAG, "Pubsub node: " + this.node);
    		
    		outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
    		Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
    		
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);


    		try {
				PubsubHelper.this.targetService.send(outMessage);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }
	/**
     * 
     * Async task to invoke Pubsub node unsubscription
     *
     */
    private class InvokeSubscriberUnSubscribe extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeSubscriberUnSubscribe.class.getName();
    	private String client;
    	private long remoteID;
    	private String pubsubServiceJid;
    	private String node;

   	 /**
   	  * Default Constructor
   	  * 
   	  * @param client
   	  * @param pubsubServiceJid
   	  * @param node
   	  * @param remoteID
   	  */
    	public InvokeSubscriberUnSubscribe(String client, String pubsubServiceJid, String node, long remoteID) {
    		this.client = client;
    		this.pubsubServiceJid = pubsubServiceJid;
    		this.node = node;
    		this.remoteID = remoteID;
    	}

    	protected Void doInBackground(Void... args) {

    		String targetMethod = IPubsubService.methodsArray[7];
    		android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);

    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.pubsubServiceJid);
    		Log.d(LOCAL_LOG_TAG, "Pubsub service JID: " + this.pubsubServiceJid);
    		
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
    		Log.d(LOCAL_LOG_TAG, "Pubsub node: " + this.node);
    		
    		outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
    		Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
    		
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);


    		try {
				PubsubHelper.this.targetService.send(outMessage);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return null;
    	}
    }

}
