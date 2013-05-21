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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.societies.android.api.common.ADate;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.context.ICtxClientCallback;
import org.societies.android.api.context.ICtxClientHelper;
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
import org.societies.utilities.DBC.Dbc;

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

public class ContextClientHelper implements ICtxClientHelper {

	private final static String LOG_TAG = ContextClientHelper.class.getName();
	private final static int ILLEGAL_VALUE = -999999;
	
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
		Dbc.require("Callback object must be specified", null != callback);
		Log.d(LOG_TAG, "setUpService");
		
		if (!this.connectedToContextClient) {
			this.setupBroadcastReceiver();

			this.startupCallback = callback;
        	Intent serviceIntent = new Intent(ICoreSocietiesServices.EVENTS_SERVICE_INTENT);
        	this.context.bindService(serviceIntent, eventsConnection, Context.BIND_AUTO_CREATE);
		}
		return false;
		
	}

	@Override
	public boolean tearDownService(IMethodCallback callback) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CtxEntityBean createEntity(RequestorBean requestor,
			String targetCss, String type, ICtxClientCallback callback)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttributeBean createAttribute(RequestorBean requestor,
			CtxEntityIdentifierBean scope, String type,
			ICtxClientCallback callback) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAssociationBean createAssociation(RequestorBean requestor,
			String targetCss, String type, ICtxClientCallback callback)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(RequestorBean requestor,
			String target, CtxModelTypeBean modelType, String type,
			ICtxClientCallback callback) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(RequestorBean requestor,
			CtxEntityIdentifierBean entityId, CtxModelTypeBean modelType,
			String type, ICtxClientCallback callback) throws CtxException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxEntityIdentifierBean retrieveIndividualEntityId(
			RequestorBean requestor, String cssId, ICtxClientCallback callback)
			throws CtxException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
/*
			if (intent.getAction().equals(ICtxClient.CREATE_ASSOCIATION)) {
				if (null != ContextClientHelper.this.methodQueues[classMethods.createAssociation.ordinal()]) {
					ICtxClientCallback retrievedCallback = ContextClientHelper.this.methodQueues[classMethods.createAssociation.ordinal()].poll();
					if (null != retrievedCallback) {
						final String exceptionMessage = intent.getStringExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY);
						if (exceptionMessage != null) {
							retrievedCallback.onException(new )
						}
						if (intent.getIntExtra(ICtxClient.INTENT_RETURN_VALUE_KEY, ILLEGAL_VALUE) >= 0) {
							retrievedCallback.onException(exception)returnException(intent.getIntExtra(ICtxClient.INTENT_EXCEPTION_VALUE_KEY, ILLEGAL_VALUE));
						} else {
							retrievedCallback.returnAction(intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
						}
					}
				}
			}*/
    }

    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        this.receiver = new EventsHelperReceiver();
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
    		this.methodQueues[index] = new ConcurrentLinkedQueue<IPlatformEventsCallback>();
    	}
    }
}
