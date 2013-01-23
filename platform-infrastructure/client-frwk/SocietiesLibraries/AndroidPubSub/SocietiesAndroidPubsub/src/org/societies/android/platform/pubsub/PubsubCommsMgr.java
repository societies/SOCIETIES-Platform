package org.societies.android.platform.pubsub;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.platform.androidutils.PacketMarshaller;
import org.societies.android.platform.comms.ServicePlatformCommsLocal;
import org.societies.android.platform.comms.ServicePlatformCommsLocal.LocalPlatformCommsBinder;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;
import org.jivesoftware.smack.packet.IQ;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class PubsubCommsMgr {
	
	private static final String LOG_TAG = PubsubCommsMgr.class.getName();
	private boolean boundToService;
	private String clientPackageName;
	private Random randomGenerator;
	private PacketMarshaller marshaller = new PacketMarshaller();

	private Context androidContext;
	private Map<Long, ICommCallback> xmppCallbackMap;
	private Map<Long, IMethodCallback> methodCallbackMap;
	
	private String identityJID;
	private String domainAuthority;
	private IIdentityManager idManager;

	private BroadcastReceiver receiver;
	private XMPPAgent localAndroidComms;
	private long bindCallbackID;
	/**
	 * Default constructor
	 * 
	 * @param androidContext
	 * @param loginCompleted true if XMPP login has taken place
	 */
	public PubsubCommsMgr(Context androidContext) {
		Dbc.require("Android context must be supplied", null != androidContext);
		
		Log.d(LOG_TAG, "Instantiate PubsubCommsMgr");
		this.androidContext = androidContext;
		
		this.clientPackageName = this.androidContext.getApplicationContext().getPackageName();
		
		this.randomGenerator = new Random(System.currentTimeMillis());
		
		this.xmppCallbackMap = Collections.synchronizedMap(new HashMap<Long, ICommCallback>());
		this.methodCallbackMap = Collections.synchronizedMap(new HashMap<Long, IMethodCallback>());
		
		this.identityJID = null;
		this.domainAuthority = null;
		this.idManager = null;
		this.receiver = null;
		this.bindCallbackID = 0;
		
		this.setupBroadcastReceiver();
	}
	

	/**
	 * Binds to Android Comms Service
	 * @param bindCallback callback 
	 */
	public void bindCommsService(IMethodCallback bindCallback) {
		Dbc.require("Service Bind Callback cannot be null", null != bindCallback);
		Log.d(LOG_TAG, "Bind to Android Comms Service");
		
		long callbackID = this.randomGenerator.nextLong();

		synchronized(this.methodCallbackMap) {
			//store callback in order to activate required methods
			this.methodCallbackMap.put(callbackID, bindCallback);
		}
		this.bindCallbackID = callbackID;
		this.bindToServiceAfterLogin();

	}
	/**
	 * Unbinds from the Android Comms service
	 * 
	 * @return true if no more requests queued
	 */
	public boolean unbindCommsService() {
		Log.d(LOG_TAG, "Unbind from Android Comms Service");
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
	
	public void sendIQ(Stanza stanza, IQ.Type type, Object payload, ICommCallback callback) throws CommunicationException {
		Dbc.require("Stanza must be specified", null != stanza);
		Dbc.require("IQ Type must be specified", null != type);
		Dbc.require("Payload must be specified", null != payload);
		Dbc.require("Callback object must be supplied", null != callback);

		long callbackID = this.randomGenerator.nextLong();

		synchronized(this.xmppCallbackMap) {
			//store callback in order to activate required methods
			this.xmppCallbackMap.put(callbackID, callback);
		}
		
		try {
			stanza.setFrom(getIdManager().getThisNetworkNode());
			Log.d(LOG_TAG, "sendIQ IQtype: " + type.toString() + " from: " + stanza.getFrom() + " to: " + stanza.getTo());
			String xml = marshaller.marshallIQ(stanza, type, payload);

			sendIQ(xml, callbackID);
			
		} catch (Exception e) {
			throw new CommunicationException("Error sending IQ message", e);
		}
	}
	/**
	 * Get a user's XMPP identity. Can be called synchronously.
	 * 
	 * @param callback
	 * @return identity or null if identity not available
	 * 
	 * @throws InvalidFormatException
	 */
	public IIdentity getIdentity() throws InvalidFormatException {
		Log.d(LOG_TAG, "getIdentity");
		
		IIdentity returnIdentity = null;
		
		if (null != this.identityJID) {
			Log.d(LOG_TAG, "getIdentity identity: " + this.identityJID);
				returnIdentity =  IdentityManagerImpl.staticfromJid(this.identityJID);
		}
		return returnIdentity;
	}

	/**
	 * 	Get the Identity Manager. Can be called synchronously.
	 * 
	 * @param callback
	 * @return IIdentityManager or null if identity not available
	 * @throws InvalidFormatException 
	 */
	public IIdentityManager getIdManager() throws InvalidFormatException {
		Log.d(LOG_TAG, "getIdManager");
		
		if(null == this.idManager && null != this.domainAuthority && null != this.identityJID) {
			this.idManager = createIdentityManager(this.identityJID, this.domainAuthority);
		}
		return this.idManager;
	}
	
	/**
	 * Get XMPP items
	 * 
	 * @param entity
	 * @param node
	 * @param callback
	 * @return
	 * @throws CommunicationException
	 */
	public String getItems(final IIdentity entity, final String node, final ICommCallback callback) throws CommunicationException {
		Dbc.require("Entity cannot be null", null != entity);
//		Dbc.require("Node must be specified", null != node && node.length() > 0);
		Dbc.require("Callback object must be supplied", null != callback);
		
		Log.d(LOG_TAG, "getItems for entity: " + entity + " and node " + node);

		long callbackID = this.randomGenerator.nextLong();

		synchronized(this.xmppCallbackMap) {
			//store callback in order to activate required methods
			this.xmppCallbackMap.put(callbackID, callback);
		}
		
		InvokeGetItems invoke = new InvokeGetItems(this.clientPackageName, entity.getJid(), node, callbackID);
		invoke.execute();
		
		return null;
	}
	
	private void sendIQ(final String xml, long remoteCallID) {

		InvokeSendIQ invoke = new InvokeSendIQ(this.clientPackageName, xml, remoteCallID);
		invoke.execute();
	}
	
	private String getIdentityJid(long callbackID) {
		Log.d(LOG_TAG, "getIdentityJid");
		
		InvokeGetIdentityJid invoke  = new InvokeGetIdentityJid(this.clientPackageName, callbackID);
		invoke.execute();
		
		return null;
	}
	
	private String getDomainAuthorityNode(long callbackID) {
		Log.d(LOG_TAG, "getDomainAuthorityNode");

		InvokeGetDomainAuthorityNode invoke  = new InvokeGetDomainAuthorityNode(this.clientPackageName, callbackID);
		invoke.execute();
		
		return null;
	}

	public boolean isConnected(IMethodCallback callback) {
		Dbc.require("Method callback object must be specified", null != callback);
		Log.d(LOG_TAG, "isConnected");
		
		long callbackID = this.randomGenerator.nextLong();

		synchronized(this.methodCallbackMap) {
			//store callback in order to activate required methods
			this.methodCallbackMap.put(callbackID, callback);
			Dbc.ensure("Callback has to be added to map", this.methodCallbackMap.containsKey(callbackID));
		}
		
		InvokeIsConnected invoke = new InvokeIsConnected(this.clientPackageName, callbackID);
		invoke.execute();
		
		return false;
	}
	
	
	private static IIdentityManager createIdentityManager(String thisNode, String daNode) throws InvalidFormatException {
		IIdentityManager idManager;
		if(daNode == null)
			idManager = new IdentityManagerImpl(thisNode);
		else
			idManager = new IdentityManagerImpl(thisNode, daNode);
		return idManager;
	}	
	
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        this.receiver = new MainReceiver();
        this.androidContext.registerReceiver(this.receiver, createIntentFilter());    
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
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			Log.d(LOG_TAG, "Received action CALL_ID_KEY: " + intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0));
			long callbackId = intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0);

			if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
				synchronized(PubsubCommsMgr.this.methodCallbackMap) {
					IMethodCallback callback = PubsubCommsMgr.this.methodCallbackMap.get(callbackId);
					if (null != callback) {
						PubsubCommsMgr.this.methodCallbackMap.remove(callbackId);
						callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
					}
				}
				
			} else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
				if (PubsubCommsMgr.this.methodCallbackMap.containsKey(callbackId)) {
					PubsubCommsMgr.this.identityJID = intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY);
					synchronized(PubsubCommsMgr.this.methodCallbackMap) {
						IMethodCallback callback = PubsubCommsMgr.this.methodCallbackMap.get(callbackId);
						PubsubCommsMgr.this.methodCallbackMap.remove(callbackId);
						callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
					}
				}
			} else if (intent.getAction().equals(XMPPAgent.GET_ITEMS_RESULT)) {
				synchronized(PubsubCommsMgr.this.xmppCallbackMap) {
					ICommCallback callback = PubsubCommsMgr.this.xmppCallbackMap.get(callbackId);
					if (null != callback) {
						PubsubCommsMgr.this.xmppCallbackMap.remove(callbackId);
						callback.receiveResult(null, XMPPAgent.INTENT_RETURN_VALUE_KEY);
//						try {
//							Packet packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//							Entry<String, List<String>> nodeMap = marshaller.parseItemsResult(packet);
//							callback.receiveItems(stanzaFromPacket(packet), nodeMap.getKey(), nodeMap.getValue());
//						} catch (Exception e) {
//							Log.e(LOG_TAG, e.getMessage(), e);
//						}
					}
				}

			} else if (intent.getAction().equals(XMPPAgent.GET_ITEMS_ERROR)) {
				synchronized(PubsubCommsMgr.this.xmppCallbackMap) {
					ICommCallback callback = PubsubCommsMgr.this.xmppCallbackMap.get(callbackId);
					if (null != callback) {
						PubsubCommsMgr.this.xmppCallbackMap.remove(callbackId);
						callback.receiveMessage(null, XMPPAgent.INTENT_RETURN_VALUE_KEY);

//						try {
//							Packet packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//							Entry<String, List<String>> nodeMap = marshaller.parseItemsResult(packet);
//							callback.receiveItems(stanzaFromPacket(packet), nodeMap.getKey(), nodeMap.getValue());
//						} catch (Exception e) {
//							Log.e(LOG_TAG, e.getMessage(), e);
//						}
					}
				}
				
			} else if (intent.getAction().equals(XMPPAgent.GET_ITEMS_EXCEPTION)) {
				
			} else if (intent.getAction().equals(XMPPAgent.SEND_IQ_RESULT)) {
				synchronized(PubsubCommsMgr.this.xmppCallbackMap) {
					ICommCallback callback = PubsubCommsMgr.this.xmppCallbackMap.get(callbackId);
					if (null != callback) {
						PubsubCommsMgr.this.xmppCallbackMap.remove(callbackId);
						Log.d(LOG_TAG, "Received result: " +  intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
						callback.receiveResult(null, XMPPAgent.INTENT_RETURN_VALUE_KEY);
						//						Packet packet;
//						try {
//							packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//							Object payload = marshaller.unmarshallPayload(packet);
//							callback.receiveResult(stanzaFromPacket(packet), payload);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
					}
				}
			} else if (intent.getAction().equals(XMPPAgent.SEND_IQ_ERROR)) {
				synchronized(PubsubCommsMgr.this.xmppCallbackMap) {
					ICommCallback callback = PubsubCommsMgr.this.xmppCallbackMap.get(callbackId);
					if (null != callback) {
						PubsubCommsMgr.this.xmppCallbackMap.remove(callbackId);
						Log.d(LOG_TAG, "Received result: " +  intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
						callback.receiveMessage(null, XMPPAgent.INTENT_RETURN_VALUE_KEY);
						
//						Packet packet;
//						try {
//							packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
//							Object payload = marshaller.unmarshallPayload(packet);
//							callback.receiveResult(stanzaFromPacket(packet), payload);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
					}
				}
			} else if (intent.getAction().equals(XMPPAgent.SEND_IQ_EXCEPTION)) {
				
			}
		}
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE);
        intentFilter.addAction(XMPPAgent.GET_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_RESULT);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_ERROR);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_EXCEPTION);
        intentFilter.addAction(XMPPAgent.SEND_IQ_RESULT);
        intentFilter.addAction(XMPPAgent.SEND_IQ_ERROR);
        intentFilter.addAction(XMPPAgent.SEND_IQ_EXCEPTION);
        intentFilter.addAction(XMPPAgent.IS_CONNECTED);
        return intentFilter;
    }
    
    private void bindToServiceAfterLogin() {
    	Intent serviceIntent = new Intent(this.androidContext, ServicePlatformCommsLocal.class);
    	Log.d(LOG_TAG, "Bind to Societies Android Comms Service after Login");
    	this.androidContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbind from Android Comms service
     */
    private void unBindService() {
    	Log.d(LOG_TAG, "Unbind from Societies Android Comms Service");
		if (this.boundToService) {
        	this.androidContext.unbindService(serviceConnection);
		}
     }
    
    /**
     * Create local Service Connection to remote service. Assumes that XMPP login and configuration 
     * has taken place
     */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			PubsubCommsMgr.this.boundToService = false;
	    	Log.d(LOG_TAG, "Societies Android Comms Service disconnected");
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			PubsubCommsMgr.this.boundToService = true;
			LocalPlatformCommsBinder binder = (LocalPlatformCommsBinder) service;
			localAndroidComms = (XMPPAgent) binder.getService();
			Log.d(LOG_TAG, "Societies Android Comms Service connected");
			
			if (PubsubCommsMgr.this.methodCallbackMap.containsKey(PubsubCommsMgr.this.bindCallbackID)) {
				synchronized(PubsubCommsMgr.this.methodCallbackMap) {
					IMethodCallback callback = PubsubCommsMgr.this.methodCallbackMap.get(PubsubCommsMgr.this.bindCallbackID);
					PubsubCommsMgr.this.methodCallbackMap.remove(PubsubCommsMgr.this.bindCallbackID);
					callback.returnAction(true);
				}
			}

		}
	};
	

	/**
     * 
     * Async task to invoke remote service method unregister
     *
     */
    private class InvokeSendIQ extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeSendIQ.class.getName();
    	private String client;
    	private String xml;
    	private long remoteCallID;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public InvokeSendIQ(String client, String xml, long remoteCallID) {
    		this.client = client;
    		this.xml = xml;
    		this.remoteCallID = remoteCallID;
    	}

    	protected Void doInBackground(Void... args) {

			PubsubCommsMgr.this.localAndroidComms.sendIQ(client, xml, remoteCallID);
	    		
	    	return null;
    	}
    }

	/**
     * 
     * Async task to invoke remote service method unregister
     *
     */
    private class InvokeGetItems extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeGetItems.class.getName();
    	private String client;
    	private String entity;
    	private String node;
    	private long remoteCallId;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public InvokeGetItems(String client, String entity, String node, long remoteCallId) {
    		this.client = client;
    	   	this.entity = entity;
        	this.node = node;
        	this.remoteCallId = remoteCallId;
    	}

    	protected Void doInBackground(Void... args) {

    		PubsubCommsMgr.this.localAndroidComms.getItems(client, entity, node, remoteCallId);
    		
    		return null;
    	}
    }

	/**
     * 
     * Async task to invoke remote service method unregister
     *
     */
    private class InvokeGetIdentityJid extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeGetIdentityJid.class.getName();
    	private String client;
    	private long remoteCallId;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public InvokeGetIdentityJid(String client, long remoteCallId) {
    		this.client = client;
        	this.remoteCallId = remoteCallId;

    	}

    	protected Void doInBackground(Void... args) {

    		PubsubCommsMgr.this.localAndroidComms.getIdentity(client, remoteCallId);
    		
    		return null;
    	}
    }

	/**
     * 
     * Async task to invoke remote service method unregister
     *
     */
    private class InvokeGetDomainAuthorityNode extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeGetDomainAuthorityNode.class.getName();
    	private String client;
    	private long remoteCallId;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public InvokeGetDomainAuthorityNode(String client, long remoteCallId) {
    		this.client = client;
        	this.remoteCallId = remoteCallId;

    	}

    	protected Void doInBackground(Void... args) {

    		PubsubCommsMgr.this.localAndroidComms.getDomainAuthorityNode(client, remoteCallId);
    		return null;
    	}
    }

	/**
     * 
     * Async task to invoke remote service method unregister
     *
     */
    private class InvokeIsConnected extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeIsConnected.class.getName();
    	private String client;
    	private long remoteCallId;

    	/**
    	 * Default Constructor
    	 * 
    	 * @param packageName
    	 * @param client
    	 */
    	public InvokeIsConnected(String client, long remoteCallId) {
    		this.client = client;
    		this.remoteCallId = remoteCallId;
    	}

    	protected Void doInBackground(Void... args) {
    		PubsubCommsMgr.this.localAndroidComms.isConnected(client, remoteCallId);
    		return null;
    	}
    }

}
