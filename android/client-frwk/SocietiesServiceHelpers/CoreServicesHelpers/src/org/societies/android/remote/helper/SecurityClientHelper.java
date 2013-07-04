/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.remote.helper;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.privacytrust.trust.ITrustClient;
import org.societies.android.api.security.digsig.IDigSigClient;
import org.societies.android.api.security.digsig.IDigSigClientCallback;
import org.societies.android.api.security.digsig.IDigSigClientHelper;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.android.api.utilities.ServiceMethodTranslator;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class provides a simple callback interface to the Societies Android Platform Events service
 * It assumes that the service has already been bound to the Android Comms and Pubsub services.
 * 
 * TODO: Insert a timer to automatically unbind from the Platform Events service after a defined amount 
 * of inactivity
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
public class SecurityClientHelper implements IDigSigClientHelper {
	
	private final static String TAG = SecurityClientHelper.class.getName();
	
	/** Service method enum. Order does not matter. */ 
    private enum classMethods {
    	signXml
    }

	private Context context;
	
	/** The client to use for all service method invocations. */
	private String client;
	
	private boolean connectedToTrustClient;
	private Messenger targetService;
	
	/** The BroadcastReceiver to receive the results via Intents. */
	private BroadcastReceiver receiver;
	
	private IMethodCallback startupCallback;
	
	/**
	 * The array of queues to store callbacks for each method.
	 * <p>
	 * Assumption is that similar method calls will be answered in correct 
	 * order as remote service call is via Messenger mechanism which is itself
	 * queue oriented.
	 */
	private ConcurrentLinkedQueue<IDigSigClientCallback> methodQueues[];
	
	/**
     * Security Client service connection.
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection securityClientConnection = new ServiceConnection() {

    	/*
    	 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
    	 */
    	@Override
    	public void onServiceConnected(ComponentName name, IBinder service) {
    		
        	Log.d(TAG, "Connecting to Trust Client service");
        	SecurityClientHelper.this.connectedToTrustClient = true;
        	// get a remote binder
        	SecurityClientHelper.this.targetService = new Messenger(service);
        	Log.d(TAG, "Target service " + name.getShortClassName() 
        			+ " acquired: " + SecurityClientHelper.this.targetService.getClass().getName());
        	
			Log.d(TAG, "Retrieve setup callback");
			if (null != SecurityClientHelper.this.startupCallback) {
				Log.d(TAG, "Setup callback valid");
				SecurityClientHelper.this.startupCallback.returnAction(true);
			}
        }
    	
    	/*
    	 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
    	 */
    	@Override
        public void onServiceDisconnected(ComponentName name) {
        	
        	Log.d(TAG, "Disconnecting from Trust Client service");
        	//Unregister broadcast receiver if service binding broken. Otherwise
        	//the next set-up of the service will one extra receiver causing possible problems.
			SecurityClientHelper.this.teardownBroadcastReceiver();
			SecurityClientHelper.this.connectedToTrustClient = false;
        }
    };

	/**
	 * Default constructor
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public SecurityClientHelper(Context context) {
		
		this.context = context;
		this.client = this.context.getApplicationContext().getPackageName();
		this.connectedToTrustClient = false;
		this.targetService = null;
		this.methodQueues = (ConcurrentLinkedQueue[]) 
				new ConcurrentLinkedQueue[classMethods.values().length];
		this.startupCallback = null;
	}

	/*
	 * @see org.societies.android.api.services.ICoreSocietiesServices#setUpService(org.societies.android.api.comms.IMethodCallback)
	 */
	@Override
	public boolean setUpService(IMethodCallback callback) {
		
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "setUpService");
		
		if (!this.connectedToTrustClient) {
			this.setupBroadcastReceiver();
			this.startupCallback = callback;
        	Intent serviceIntent = new Intent(
        			ICoreSocietiesServices.DIGSIG_CLIENT_SERVICE_INTENT);
        	this.context.bindService(serviceIntent, this.securityClientConnection,
        			Context.BIND_AUTO_CREATE);
		}
		return false;
	}

	/*
	 * @see org.societies.android.api.services.ICoreSocietiesServices#tearDownService(org.societies.android.api.comms.IMethodCallback)
	 */
	@Override
	public boolean tearDownService(IMethodCallback callback) {
		
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "tearDownService");
		if (this.connectedToTrustClient) {
			this.teardownBroadcastReceiver();
	       	this.context.unbindService(this.securityClientConnection);
			Log.d(TAG, "tearDownService completed");
	       	callback.returnAction(true);
		}
		return false;
	}

	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustRelationships(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void signXml(String xml, String xmlNodeId, final IDigSigClientCallback callback) {
		
		if (xml == null)
			throw new NullPointerException("xml can't be null");
		if (xmlNodeId == null)
			throw new NullPointerException("xmlNodeId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "signXml: xmlNodeId = " + xmlNodeId + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.signXml.ordinal());
			this.methodQueues[classMethods.signXml.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[2];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
//			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
//					targetMethod, 1), requestor);
//			Log.d(TAG, "requestor: " + requestor.getRequestorId());
//			
//			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
//					targetMethod, 2), trustorId);
//			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			//FIXME
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final IDigSigClientCallback retrievedCallback = 
						this.methodQueues[classMethods.signXml.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new DigSigClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new DigSigClientNotConnectedException());
		}
	}

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs or queues cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class DigSigClientHelperReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.d(TAG, "Received action: " + intent.getAction());

			if (intent.getAction().equals(IDigSigClient.RETRIEVE_TRUST_VALUE)) {

				if (null != SecurityClientHelper.this.methodQueues[classMethods.signXml.ordinal()]) {
					final IDigSigClientCallback retrievedCallback = 
							SecurityClientHelper.this.methodQueues[classMethods.signXml.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(
								ITrustClient.INTENT_EXCEPTION_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new DigSigClientInvocationException(
									exceptionMessage));
							return;
						}
						final Double defaultTrustValue = -1.0d;
						final Double trustValue = intent.getDoubleExtra(ITrustClient.INTENT_RETURN_VALUE_KEY, defaultTrustValue);
						if (defaultTrustValue.equals(trustValue))
							retrievedCallback.onRetrievedTrustValue(null);
						else
							retrievedCallback.onRetrievedTrustValue(trustValue);
					} else {
						Log.e(TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else  {
					Log.e(TAG, "Could not find callback method queue for received action " + intent.getAction());
				}

			} else {
				Log.e(TAG, "Received unexpected action " + intent.getAction());
			}
		}
    }

    /**
     * Creates a broadcast receiver.
     * 
     * @return the created broadcast receiver.
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	
        Log.d(TAG, "Set up broadcast receiver");
        
        this.receiver = new DigSigClientHelperReceiver();
        this.context.registerReceiver(this.receiver, createIntentFilter());    
        Log.d(TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
    	
        Log.d(TAG, "Tear down broadcast receiver");
    	this.context.unregisterReceiver(this.receiver);
    }

    /**
     * Create an intent filter to receive Trust Client return values.
     * 
     * @return an intent filter to receive Trust Client return values.
     */
    private IntentFilter createIntentFilter() {
    	
        final IntentFilter intentFilter = new IntentFilter();
        
        Log.d(TAG, "intentFilter.addAction " + ITrustClient.RETRIEVE_TRUST_RELATIONSHIPS); 
        intentFilter.addAction(ITrustClient.RETRIEVE_TRUST_RELATIONSHIPS);
        Log.d(TAG, "intentFilter.addAction " + ITrustClient.RETRIEVE_TRUST_RELATIONSHIP); 
        intentFilter.addAction(ITrustClient.RETRIEVE_TRUST_RELATIONSHIP);
        Log.d(TAG, "intentFilter.addAction " + ITrustClient.RETRIEVE_TRUST_VALUE); 
        intentFilter.addAction(ITrustClient.RETRIEVE_TRUST_VALUE);
        Log.d(TAG, "intentFilter.addAction " + ITrustClient.ADD_DIRECT_TRUST_EVIDENCE); 
        intentFilter.addAction(ITrustClient.ADD_DIRECT_TRUST_EVIDENCE);

        return intentFilter;
    }
    /**
     * Initialises a queue for the given service methods index.
     * 
     * @param int 
     *            index of class methods queue array
     */
    private void initialiseQueue(int index) {
    	
    	if (null == this.methodQueues[index]) {
    		Log.d(TAG, "Create queue for index: " + index);
    		this.methodQueues[index] = new ConcurrentLinkedQueue<IDigSigClientCallback>();
    	}
    }
 }