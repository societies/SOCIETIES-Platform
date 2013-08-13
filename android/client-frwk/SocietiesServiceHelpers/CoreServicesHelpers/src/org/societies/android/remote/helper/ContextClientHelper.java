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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.societies.android.api.common.ADate;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.context.ContextClientInvocationException;
import org.societies.android.api.context.ContextClientNotConnectedException;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.context.ICtxClientCallback;
import org.societies.android.api.context.ICtxClientHelper;
import org.societies.android.api.privacytrust.trust.ITrustClient;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;

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
 * This class provides a simple callback interface to the Societies Android Context service
 * It assumes that the service has already been bound to the Android Comms and Pubsub services.
 * 
 * @author <a href="mailto:pkosmidis@cn.ntua.gr">Pavlos Kosmides</a> (ICCS)
 * @since 1.1
 */
public class ContextClientHelper implements ICtxClientHelper {

	private final static String LOG_TAG = ContextClientHelper.class.getName();
	
    private enum classMethods {
    	createEntity,
    	createAttribute,
    	createAssociation,
    	lookup,
    	lookupEntities,
    	remove,
    	retrieve,
    	retrieveIndividualEntityId,
    	retrieveCommunityEntityId,
    	update
    }

	private Context context;
	private String clientPackageName;
	private boolean connectedToContextClient;
	private Messenger targetService;
	private BroadcastReceiver receiver;
	private IMethodCallback startupCallback;
	
	//Array of queues to store callbacks for each method
	//Assumption is that similar method calls will be answered in correct order
	//as remote service call is via Messenger mechanism which is itself queue oriented
	private ConcurrentLinkedQueue<ICtxClientCallback> methodQueues [];
	
	/**
	 * Default constructor
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public ContextClientHelper(Context context) {
		this.context = context;
		this.clientPackageName = this.context.getApplicationContext().getPackageName();
		this.connectedToContextClient = false;
		this.targetService = null;
		this.methodQueues = (ConcurrentLinkedQueue<ICtxClientCallback>[])new ConcurrentLinkedQueue[classMethods.values().length];
		this.startupCallback = null;
		
	}
	
	@Override
	public boolean setUpService(IMethodCallback callback) {
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "setUpService");
		
		if (!this.connectedToContextClient) {
			this.setupBroadcastReceiver();

			this.startupCallback = callback;
        	Intent serviceIntent = new Intent(ICoreSocietiesServices.CONTEXT_SERVICE_INTENT);
        	this.context.bindService(serviceIntent, this.contextClientConnection, Context.BIND_AUTO_CREATE);
		}
		return false;
		
	}

	@Override
	public boolean tearDownService(IMethodCallback callback) {
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "tearDownService");
		if (this.connectedToContextClient) {
			this.teardownBroadcastReceiver();
	       	this.context.unbindService(this.contextClientConnection);
			Log.d(LOG_TAG, "tearDownService completed");
	       	callback.returnAction(true);
		}
		return false;
	}

	@Override
	public CtxEntityBean createEntity(RequestorBean requestor,
			String targetCss, String type, ICtxClientCallback callback)
			throws CtxException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (targetCss == null) 
			throw new NullPointerException("targetCSS can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "createEntity with requestor=" + requestor.getRequestorId()
				+ ", targetCss=" + targetCss + ", type=" + type + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.createEntity.ordinal());
			this.methodQueues[classMethods.createEntity.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[0];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), targetCss);
			Log.d(LOG_TAG, "targetCss: " + targetCss);
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), type);
			Log.d(LOG_TAG, "type: " + type);
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.createEntity.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

	@Override
	public CtxAttributeBean createAttribute(RequestorBean requestor,
			CtxEntityIdentifierBean scope, String type,
			ICtxClientCallback callback) throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (scope == null) 
			throw new NullPointerException("scope can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "createAttribute with requestor=" + requestor.getRequestorId()
				+ ", scope=" + scope.toString() + ", type=" + type + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.createAttribute.ordinal());
			this.methodQueues[classMethods.createAttribute.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[1];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), scope);
			Log.d(LOG_TAG, "scope: " + scope.toString());
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), type);
			Log.d(LOG_TAG, "type: " + type);
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.createAttribute.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

	@Override
	public CtxAssociationBean createAssociation(RequestorBean requestor,
			String targetCss, String type, ICtxClientCallback callback)
			throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (targetCss == null) 
			throw new NullPointerException("targetCSS can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "createAssociation with requestor=" + requestor.getRequestorId()
				+ ", targetCss=" + targetCss + ", type=" + type + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.createAssociation.ordinal());
			this.methodQueues[classMethods.createAssociation.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[2];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), targetCss);
			Log.d(LOG_TAG, "targetCss: " + targetCss);
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), type);
			Log.d(LOG_TAG, "type: " + type);
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.createAssociation.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(RequestorBean requestor,
			String target, CtxModelTypeBean modelType, String type,
			ICtxClientCallback callback) throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (target == null) 
			throw new NullPointerException("targetCSS can't be null");
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "lookup with requestor=" + requestor.getRequestorId()
				+ ", target=" + target + ", modelType=" + modelType.toString() +", type=" + type + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.lookup.ordinal());
			this.methodQueues[classMethods.lookup.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[3];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), target);
			Log.d(LOG_TAG, "target: " + target);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), modelType);
			Log.d(LOG_TAG, "modelType: " + modelType.toString());
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 4), type);
			Log.d(LOG_TAG, "type: " + type);
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.lookup.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(RequestorBean requestor,
			CtxEntityIdentifierBean entityId, CtxModelTypeBean modelType,
			String type, ICtxClientCallback callback) throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (entityId == null) 
			throw new NullPointerException("entityId can't be null");
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "lookup with requestor=" + requestor.getRequestorId()
				+ ", entityId=" + entityId + ", modelType=" + modelType.toString() +", type=" + type + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.lookup.ordinal());
			this.methodQueues[classMethods.lookup.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[4];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), entityId);
			Log.d(LOG_TAG, "entityId: " + entityId);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 3), modelType);
			Log.d(LOG_TAG, "modelType: " + modelType.toString());
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 4), type);
			Log.d(LOG_TAG, "type: " + type);
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.lookup.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

	@Override
	public List<CtxEntityIdentifierBean> lookupEntities(
			RequestorBean requestor, String targetCss, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue, ICtxClientCallback callback)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean remove(RequestorBean requestor,
			CtxIdentifierBean identifier, ICtxClientCallback callback)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean retrieve(RequestorBean requestor,
			CtxIdentifierBean identifier, ICtxClientCallback callback)
			throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (identifier == null) 
			throw new NullPointerException("identifier can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "retrieve with requestor=" + requestor.getRequestorId()
				+ ", identifier=" + identifier.toString() + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieve.ordinal());
			this.methodQueues[classMethods.retrieve.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[7];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 2), identifier);
			Log.d(LOG_TAG, "identifier: " + identifier.toString());
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieve.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

	@Override
	public CtxEntityIdentifierBean retrieveIndividualEntityId(
			RequestorBean requestor, String cssId, ICtxClientCallback callback)
			throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (cssId == null) 
			throw new NullPointerException("cssId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "retrieveIndividualEntityId with requestor=" + requestor.getRequestorId()
				+ ", cssIs=" + cssId + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.retrieveIndividualEntityId.ordinal());
			this.methodQueues[classMethods.retrieveIndividualEntityId.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[8];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), cssId);
			Log.d(LOG_TAG, "cssId: " + cssId);
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.retrieveIndividualEntityId.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

	@Override
	public CtxEntityIdentifierBean retrieveCommunityEntityId(
			RequestorBean requestor, String cisId, ICtxClientCallback callback)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean update(RequestorBean requestor,
			CtxModelObjectBean object, ICtxClientCallback callback)
			throws CtxException {
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (object == null) 
			throw new NullPointerException("cssId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		Log.d(LOG_TAG, "update with requestor=" + requestor.getRequestorId()
				+ ", object=" + object + ", callback=" + callback);
		
		if (this.connectedToContextClient) {
			//Add callback class to method queue tail
			this.initialiseQueue(classMethods.update.ordinal());
			this.methodQueues[classMethods.update.ordinal()].add(callback);
			
			//Select target method and create message to convey remote invocation
			String targetMethod = ICtxClient.methodsArray[10];
			android.os.Message outMessage = 
					android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(
					ICtxClient.methodsArray, targetMethod), 0, 0);
			
			Bundle outBundle = new Bundle();
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 0), this.clientPackageName);
			Log.d(LOG_TAG, "client: " + this.clientPackageName);
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(
					targetMethod, 1), requestor);
			Log.d(LOG_TAG, "requestor: " + requestor.getRequestorId());
			
			outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), object);
			Log.d(LOG_TAG, "object: " + object);
			
			outMessage.setData(outBundle);
			Log.d(LOG_TAG, "Call service method: " + targetMethod);
			
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
				//Retrieve callback and signal failure
				final ICtxClientCallback retrievedCallback = 
						this.methodQueues[classMethods.update.ordinal()].poll();
				if (null != retrievedCallback) {
					retrievedCallback.onException(new ContextClientInvocationException());
				}
			}
		} else {
			Log.e(LOG_TAG, "Note connected to Context Client service");
			callback.onException(new ContextClientNotConnectedException());
		}
		return null;
	}

    /**
     * Context client service connection
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection contextClientConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Context Client service");
        	
        	//Unregister broadcast receiver if service binding broken. Otherwise
        	//the next set-up of the service will one extra receiver causing possible problems.
        	ContextClientHelper.this.teardownBroadcastReceiver();
        	connectedToContextClient = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Context Client service");

        	connectedToContextClient = true;
        	//get a remote binder
        	ContextClientHelper.this.targetService = new Messenger(service);
        	Log.d(LOG_TAG, "Target service " + name.getShortClassName() + " acquired: " + ContextClientHelper.this.targetService.getClass().getName());
        	
			Log.d(LOG_TAG, "Retrieve setup callback");
			if (null != ContextClientHelper.this.startupCallback) {
				Log.d(LOG_TAG, "Setup callback valid");
				ContextClientHelper.this.startupCallback.returnAction(true);
			}
        }
    };

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs or queues cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class ContextClientHelperReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());

			if (intent.getAction().equals(ICtxClient.CREATE_ASSOCIATION)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.createAssociation.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.createAssociation.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new ContextClientInvocationException(exceptionMessage));
						}
						final CtxAssociationBean ctxAssociation;
						final Parcelable pCtxAssociation = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
						if (pCtxAssociation instanceof CtxAssociationBean) {
							ctxAssociation = (CtxAssociationBean) pCtxAssociation;
						} else {
							retrievedCallback.onException(new ContextClientInvocationException ("Unexpected return value type: " 
						+ ((pCtxAssociation != null) ? pCtxAssociation.getClass() : "null")));
							return;
						}
						
						retrievedCallback.onCreatedAssociation(ctxAssociation);
					} else {
						Log.e(LOG_TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else {
					Log.e(LOG_TAG, "Could not find callback method queue for received action " + intent.getAction());
				}
			} else if (intent.getAction().equals(ICtxClient.CREATE_ATTRIBUTE)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.createAttribute.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.createAttribute.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new ContextClientInvocationException(exceptionMessage));
						}
						final CtxAttributeBean ctxAttribute;
						final Parcelable pCtxAttribute = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
						if (pCtxAttribute instanceof CtxAttributeBean) {
							ctxAttribute = (CtxAttributeBean) pCtxAttribute;
						} else {
							retrievedCallback.onException(new ContextClientInvocationException ("Unexpected return value type: " 
						+ ((pCtxAttribute != null) ? pCtxAttribute.getClass() : "null")));
							return;
						}
						
						retrievedCallback.onCreatedAttribute(ctxAttribute);
					} else {
						Log.e(LOG_TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else {
					Log.e(LOG_TAG, "Could not find callback method queue for received action " + intent.getAction());
				}
			} else if (intent.getAction().equals(ICtxClient.CREATE_ENTITY)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.createEntity.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.createEntity.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new ContextClientInvocationException(exceptionMessage));
						}
						final CtxEntityBean ctxEntity;
						final Parcelable pCtxEntity = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
						if (pCtxEntity instanceof CtxEntityBean) {
							ctxEntity = (CtxEntityBean) pCtxEntity;
						} else {
							retrievedCallback.onException(new ContextClientInvocationException ("Unexpected return value type: " 
						+ ((pCtxEntity != null) ? pCtxEntity.getClass() : "null")));
							return;
						}
						
						retrievedCallback.onCreatedEntity(ctxEntity);
					} else {
						Log.e(LOG_TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else {
					Log.e(LOG_TAG, "Could not find callback method queue for received action " + intent.getAction());
				}
			} else if (intent.getAction().equals(ICtxClient.LOOKUP)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.lookup.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.lookup.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new ContextClientInvocationException(exceptionMessage));
						}
						final List<CtxIdentifierBean> ctxIdsList = new ArrayList<CtxIdentifierBean>();
//						List<Parcelable> pCtxIdsList = new ArrayList<Parcelable>();
						final Parcelable[] pCtxIdsList = (Parcelable[]) 
								intent.getParcelableArrayExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
						for (final Parcelable pCtxIdList : pCtxIdsList) {
							if (pCtxIdList instanceof CtxIdentifierBean) {
								ctxIdsList.add((CtxIdentifierBean) pCtxIdList);
							} else { 
								retrievedCallback.onException(new ContextClientInvocationException ("Unexpected return value type: " 
							+ ((pCtxIdList != null) ? pCtxIdList.getClass() : "null")));
								return;
							}
						}
						
						retrievedCallback.onLookupCallback(ctxIdsList);
					} else {
						Log.e(LOG_TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else {
					Log.e(LOG_TAG, "Could not find callback method queue for received action " + intent.getAction());
				}				
			} else if (intent.getAction().equals(ICtxClient.RETRIEVE)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.retrieve.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.retrieve.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new ContextClientInvocationException(exceptionMessage));
						}
						final CtxModelObjectBean modelObject;
						final Parcelable pModelObject = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);	
						if (pModelObject instanceof CtxModelObjectBean) {
								modelObject = (CtxModelObjectBean) pModelObject;
						} else { 
							retrievedCallback.onException(new ContextClientInvocationException ("Unexpected return value type: " 
							+ ((pModelObject != null) ? pModelObject.getClass() : "null")));
							return;
						}
						
						retrievedCallback.onRetrieveCtx(modelObject);
					
					} else {
						Log.e(LOG_TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else {
					Log.e(LOG_TAG, "Could not find callback method queue for received action " + intent.getAction());
				}				
			} else if (intent.getAction().equals(ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.retrieveIndividualEntityId.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.retrieveIndividualEntityId.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new ContextClientInvocationException(exceptionMessage));
						}
						final CtxEntityIdentifierBean retrievedEntityId;
						final Parcelable pRetrievedEntityId = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
						if (pRetrievedEntityId instanceof CtxEntityIdentifierBean)  {
							retrievedEntityId = (CtxEntityIdentifierBean) pRetrievedEntityId;
						} else {
							retrievedCallback.onException(new ContextClientInvocationException("Unexpected return value type: " 
							+ ((pRetrievedEntityId != null) ? pRetrievedEntityId.getClass() : "null")));
							return;
						}
						
						retrievedCallback.onRetrievedEntityId(retrievedEntityId);
					
					} else {
						Log.e(LOG_TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else {
					Log.e(LOG_TAG, "Could not find callback method queue for received action " + intent.getAction());
				}				
			} else if (intent.getAction().equals(ICtxClient.UPDATE)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.update.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.update.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new ContextClientInvocationException(exceptionMessage));
						}
						final CtxModelObjectBean modelObject;
						final Parcelable pModelObject = (Parcelable) intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
						if (pModelObject instanceof CtxModelObjectBean) {
							modelObject = (CtxModelObjectBean) pModelObject;
						} else {
							retrievedCallback.onException(new ContextClientInvocationException("Unexpected return value type: " 
							+ ((pModelObject != null) ? pModelObject.getClass() : "null")));
							return;
						}

						retrievedCallback.onUpdateCtx(modelObject);
					
					} else {
						Log.e(LOG_TAG, "Could not find callback for received action " + intent.getAction());
					}
				} else {
					Log.e(LOG_TAG, "Could not find callback method queue for received action " + intent.getAction());
				}				
			} 
		}
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        this.receiver = new ContextClientHelperReceiver();
        this.context.registerReceiver(this.receiver, createIntentFilter());    
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
        Log.d(LOG_TAG, "Tear down broadcast receiver");
    	this.context.unregisterReceiver(this.receiver);
    }

    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.CREATE_ASSOCIATION); 
        intentFilter.addAction(ICtxClient.CREATE_ASSOCIATION);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.CREATE_ATTRIBUTE); 
        intentFilter.addAction(ICtxClient.CREATE_ATTRIBUTE);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.CREATE_ENTITY); 
        intentFilter.addAction(ICtxClient.CREATE_ENTITY);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.LOOKUP);
        intentFilter.addAction(ICtxClient.LOOKUP);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.RETRIEVE);
        intentFilter.addAction(ICtxClient.RETRIEVE);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID);
        intentFilter.addAction(ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.UPDATE);
        intentFilter.addAction(ICtxClient.UPDATE);
        
        return intentFilter;
    }
    /**
     * Initialise a queue
     * 
     * @param int index of class methods queue array
     */
    private void initialiseQueue(int index) {
    	if (null == this.methodQueues[index]) {
    		Log.d(LOG_TAG, "Create queue for index: " + index);
    		this.methodQueues[index] = new ConcurrentLinkedQueue<ICtxClientCallback>();
    	}
    }
}
