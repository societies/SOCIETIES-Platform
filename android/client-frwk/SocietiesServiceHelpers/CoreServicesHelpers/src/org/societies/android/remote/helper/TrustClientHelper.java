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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.societies.android.api.common.ADate;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.privacytrust.trust.ITrustClient;
import org.societies.android.api.privacytrust.trust.ITrustClientCallback;
import org.societies.android.api.privacytrust.trust.ITrustClientHelper;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Parcelable;
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
public class TrustClientHelper implements ITrustClientHelper {
	
	private final static String TAG = TrustClientHelper.class.getName();
	
	/** Service method enum. Order does not matter. */ 
    private enum classMethods {
    	retrieveTrustRelationships,
    	retrieveTrustRelationship,
    	retrieveTrustValue,
    	addDirectTrustEvidence
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
	private ConcurrentLinkedQueue<ITrustClientCallback> methodQueues[];
	
	/**
     * Trust Client service connection.
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection trustClientConnection = new ServiceConnection() {

    	/*
    	 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
    	 */
    	@Override
    	public void onServiceConnected(ComponentName name, IBinder service) {
    		
        	Log.d(TAG, "Connecting to Trust Client service");
        	TrustClientHelper.this.connectedToTrustClient = true;
        	// get a remote binder
        	TrustClientHelper.this.targetService = new Messenger(service);
        	Log.d(TAG, "Target service " + name.getShortClassName() 
        			+ " acquired: " + TrustClientHelper.this.targetService.getClass().getName());
        	
			Log.d(TAG, "Retrieve setup callback");
			if (null != TrustClientHelper.this.startupCallback) {
				Log.d(TAG, "Setup callback valid");
				TrustClientHelper.this.startupCallback.returnAction(true);
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
			TrustClientHelper.this.teardownBroadcastReceiver();
			TrustClientHelper.this.connectedToTrustClient = false;
        }
    };

	/**
	 * Default constructor
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public TrustClientHelper(Context context) {
		
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
        			ICoreSocietiesServices.TRUST_CLIENT_SERVICE_INTENT);
        	this.context.bindService(serviceIntent, this.trustClientConnection,
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
	       	this.context.unbindService(this.trustClientConnection);
			Log.d(TAG, "tearDownService completed");
	       	callback.returnAction(true);
		}
		return false;
	}

	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustRelationships(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "retrieveTrustRelationships: requestor="
				+ requestor.getRequestorId() + ", trustorId=" 
				+ trustorId.getEntityId() + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveTrustRelationships.ordinal());
			this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()]
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
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), trustorId);
			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}

	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustRelationships(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final TrustedEntityIdBean trusteeId, 
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "retrieveTrustRelationships: requestor="
				+ requestor.getRequestorId() + ", trustorId=" 
				+ trustorId.getEntityId() + ", trusteeId="
				+ trusteeId.getEntityId() + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveTrustRelationships.ordinal());
			this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[3];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), trustorId);
			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), trusteeId);
			Log.d(TAG, "trusteeId: " + trusteeId.getEntityId());
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustRelationship(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void retrieveTrustRelationship(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final TrustedEntityIdBean trusteeId,
    		final TrustValueTypeBean trustValueType,
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "retrieveTrustRelationship: requestor="
				+ requestor.getRequestorId() + ", trustorId=" 
				+ trustorId.getEntityId() + ", trusteeId="
				+ trusteeId.getEntityId() + ", trustValueType=" 
				+ trustValueType + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveTrustRelationship.ordinal());
			this.methodQueues[classMethods.retrieveTrustRelationship.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[4];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), trustorId);
			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), trusteeId);
			Log.d(TAG, "trusteeId: " + trusteeId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 4), trustValueType);
			Log.d(TAG, "trustValueType: " + trustValueType);
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustRelationship.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustValue(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void retrieveTrustValue(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final TrustedEntityIdBean trusteeId,
    		final TrustValueTypeBean trustValueType,
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "retrieveTrustValue: requestor="
				+ requestor.getRequestorId() + ", trustorId=" 
				+ trustorId.getEntityId() + ", trusteeId="
				+ trusteeId.getEntityId() + ", trustValueType=" 
				+ trustValueType + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveTrustValue.ordinal());
			this.methodQueues[classMethods.retrieveTrustValue.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[5];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), trustorId);
			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), trusteeId);
			Log.d(TAG, "trusteeId: " + trusteeId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 4), trustValueType);
			Log.d(TAG, "trustValueType: " + trustValueType);
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustValue.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustRelationships(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final TrustedEntityTypeBean trusteeType, 
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "retrieveTrustRelationships: requestor="
				+ requestor.getRequestorId() + ", trustorId=" 
				+ trustorId.getEntityId() + ", trusteeType="
				+ trusteeType + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveTrustRelationships.ordinal());
			this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[6];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), trustorId);
			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), trusteeType);
			Log.d(TAG, "trusteeType: " + trusteeType);
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustRelationships(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final TrustValueTypeBean trustValueType, 
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "retrieveTrustRelationships: requestor="
				+ requestor.getRequestorId() + ", trustorId=" 
				+ trustorId.getEntityId() + ", trustValueType="
				+ trustValueType + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveTrustRelationships.ordinal());
			this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[7];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), trustorId);
			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), trustValueType);
			Log.d(TAG, "trustValueType: " + trustValueType);
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#retrieveTrustRelationships(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId,
    		final TrustedEntityTypeBean trusteeType,
    		final TrustValueTypeBean trustValueType, 
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "retrieveTrustRelationships: requestor="
				+ requestor.getRequestorId() + ", trustorId=" 
				+ trustorId.getEntityId() + ", trusteeType=" 
				+ trusteeType + ", trustValueType="
				+ trustValueType + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveTrustRelationships.ordinal());
			this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[8];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), trustorId);
			Log.d(TAG, "trustorId: " + trustorId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), trusteeType);
			Log.d(TAG, "trusteeType: " + trusteeType);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 4), trustValueType);
			Log.d(TAG, "trustValueType: " + trustValueType);
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClientHelper#addDirectTrustEvidence(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean, org.societies.android.api.common.ADate, java.io.Serializable, org.societies.android.api.privacytrust.trust.ITrustClientCallback)
	 */
	@Override
	public void addDirectTrustEvidence(final RequestorBean requestor,
    		final TrustedEntityIdBean subjectId,
    		final TrustedEntityIdBean objectId,
    		final TrustEvidenceTypeBean type,
    		final ADate timestamp,
    		final Serializable info,
    		final ITrustClientCallback callback) {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(TAG, "addDirectTrustEvidence: requestor="
				+ requestor.getRequestorId() + ", subjectId=" 
				+ subjectId.getEntityId() + ", objectId=" 
				+ objectId.getEntityId() + ", type=" + type + ", timestmap="
				+ timestamp + ", info=" + info + ", callback=" + callback);

		if (this.connectedToTrustClient) {
			// Add callback class to method queue tail
			this.initialiseQueue(classMethods.addDirectTrustEvidence.ordinal());
			this.methodQueues[classMethods.addDirectTrustEvidence.ordinal()]
					.add(callback);

			// Select target method and create message to convey remote invocation
	   		String targetMethod = ITrustClient.methodsArray[9];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
							ITrustClient.methodsArray, targetMethod), 0, 0);

			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.client);
			Log.d(TAG, "client: " + this.client);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), subjectId);
			Log.d(TAG, "subjectId: " + subjectId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), objectId);
			Log.d(TAG, "objectId: " + objectId.getEntityId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 4), type);
			Log.d(TAG, "type: " + type);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 5), timestamp);
			Log.d(TAG, "timestamp: " + timestamp);
			
			outBundle.putSerializable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 6), info);
			Log.d(TAG, "info: " + info);
			
	   		outMessage.setData(outBundle);
			Log.d(TAG, "Invoking service method: " + targetMethod);

			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				
				Log.e(TAG, "Could not send remote method invocation", e);
				// Retrieve callback and signal failure
				final ITrustClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new TrustClientInvocationException());
				}
			}
		} else {
			Log.e(TAG, "Not connected to Trust Client service");
			callback.onException(new TrustClientNotConnectedException());
		}
	}

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs or queues cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class TrustClientHelperReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.d(TAG, "Received action: " + intent.getAction());

			if (intent.getAction().equals(ITrustClient.RETRIEVE_TRUST_RELATIONSHIPS)) {
				
				if (null != TrustClientHelper.this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()]) {
					final ITrustClientCallback retrievedCallback = 
							TrustClientHelper.this.methodQueues[classMethods.retrieveTrustRelationships.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(
								ITrustClient.INTENT_EXCEPTION_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new TrustClientInvocationException(
									exceptionMessage));
							return;
						}
						final Set<TrustRelationshipBean> trustRelationships = 
								new HashSet<TrustRelationshipBean>();
						final Parcelable[] pTrustRelationships = (Parcelable[]) 
								intent.getParcelableArrayExtra(ITrustClient.INTENT_RETURN_VALUE_KEY);
						for (final Parcelable pTrustRelationship : pTrustRelationships) {
							if (pTrustRelationship instanceof TrustRelationshipBean) {
								trustRelationships.add((TrustRelationshipBean) pTrustRelationship);
							} else {
								retrievedCallback.onException(new TrustClientInvocationException(
										"Unexpected return value type: " + ((pTrustRelationship != null) 
												? pTrustRelationship.getClass() : "null")));
								return;
							}
						}
						retrievedCallback.onRetrievedTrustRelationships(
								trustRelationships);
					} else {
						Log.e(TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else  {
					Log.e(TAG, "Could not find callback method queue for received action " + intent.getAction());
				}
				
			} else if (intent.getAction().equals(ITrustClient.RETRIEVE_TRUST_RELATIONSHIP)) {

				if (null != TrustClientHelper.this.methodQueues[classMethods.retrieveTrustRelationship.ordinal()]) {
					final ITrustClientCallback retrievedCallback = 
							TrustClientHelper.this.methodQueues[classMethods.retrieveTrustRelationship.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(
								ITrustClient.INTENT_EXCEPTION_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new TrustClientInvocationException(
									exceptionMessage));
							return;
						}
						final Parcelable pTrustRelationship = 
								intent.getParcelableExtra(ITrustClient.INTENT_RETURN_VALUE_KEY);
						if (pTrustRelationship == null) {
							retrievedCallback.onRetrievedTrustRelationship(
									null);
						} else if (pTrustRelationship instanceof TrustRelationshipBean) {
							retrievedCallback.onRetrievedTrustRelationship(
									(TrustRelationshipBean) pTrustRelationship);
						} else {
							retrievedCallback.onException(new TrustClientInvocationException(
									"Unexpected return value type: " + ((pTrustRelationship != null) 
											? pTrustRelationship.getClass() : "null")));
							return;
						}
						
					} else {
						Log.e(TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else  {
					Log.e(TAG, "Could not find callback method queue for received action " + intent.getAction());
				}
				
			} else if (intent.getAction().equals(ITrustClient.RETRIEVE_TRUST_VALUE)) {

				if (null != TrustClientHelper.this.methodQueues[classMethods.retrieveTrustValue.ordinal()]) {
					final ITrustClientCallback retrievedCallback = 
							TrustClientHelper.this.methodQueues[classMethods.retrieveTrustValue.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(
								ITrustClient.INTENT_EXCEPTION_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new TrustClientInvocationException(
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
				
			} else if (intent.getAction().equals(ITrustClient.ADD_DIRECT_TRUST_EVIDENCE)) {

				if (null != TrustClientHelper.this.methodQueues[classMethods.addDirectTrustEvidence.ordinal()]) {
					final ITrustClientCallback retrievedCallback = 
							TrustClientHelper.this.methodQueues[classMethods.addDirectTrustEvidence.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(
								ITrustClient.INTENT_EXCEPTION_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new TrustClientInvocationException(
									exceptionMessage));
							return;
						}
						retrievedCallback.onAddedDirectTrustEvidence();
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
        
        this.receiver = new TrustClientHelperReceiver();
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
    		this.methodQueues[index] = new ConcurrentLinkedQueue<ITrustClientCallback>();
    	}
    }
 }