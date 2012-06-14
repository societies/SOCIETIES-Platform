/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.privacytrust.datamanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.privacytrust.datamanagement.R;
import org.societies.android.privacytrust.datamanagement.R.id;
import org.societies.android.privacytrust.datamanagement.R.layout;
import org.societies.android.privacytrust.datamanagement.service.PrivacyDataManagerLocalService.LocalBinder;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ActionConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.RequestorBean;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author Olivier Maridat (Trialog)
 * @date 28 nov. 2011
 */
public class PrivacyDataManagerActivity extends Activity {
	private final static String TAG = PrivacyDataManagerActivity.class.getSimpleName();
	
	private TextView txtLocation;

	private boolean ipBoundToService = false;
	private IPrivacyDataManager targetIPService = null;
	private boolean opBoundToService = false;
	private Messenger targetOPService = null;
	
	private long serviceInvoke;

	
	/* **************
	 * Activity Lifecycle
	 * ************** */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// -- Create a link with editable area
		txtLocation = (TextView) findViewById(R.id.txtLocation);
		
		// -- Create a link with services
		Intent ipIntent = new Intent(this, PrivacyDataManagerLocalService.class);
		Intent opIntent = new Intent(this, PrivacyDataManagerExternalService.class);
		bindService(ipIntent, inProcessServiceConnection, Context.BIND_AUTO_CREATE);
		bindService(opIntent, outProcessServiceConnection, Context.BIND_AUTO_CREATE);
		
		// Register the broadcast receiver to retrieve results of an out process service call
		IntentFilter intentFilter = new IntentFilter() ;
		intentFilter.addAction(IPrivacyDataManager.CHECK_PERMISSION);
		intentFilter.addAction(IPrivacyDataManager.HAS_OBFUSCATED_VERSION);
		intentFilter.addAction(IPrivacyDataManager.OBFUSCATE_DATA);
        this.registerReceiver(new ServiceReceiver(), intentFilter);
	}

	protected void onStop() {
		super.onStop();
		// -- Unlink with services
		if (ipBoundToService) {
			unbindService(inProcessServiceConnection);
		}
		if (opBoundToService) {
			unbindService(outProcessServiceConnection);
		}
	}

	private ServiceConnection inProcessServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			ipBoundToService = false;
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			targetIPService = (IPrivacyDataManager) binder.getService();
			ipBoundToService = true;
		}
	};

	private ServiceConnection outProcessServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			opBoundToService = false;
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			opBoundToService = true;
			targetOPService = new Messenger(service);
		}
	};
	
	
	/* **************
	 * Button Listeners
	 * ************** */
	
	/**
	 * Call an in-process service. Service consumer simply calls service API and can use 
	 * return value
	 *  
	 * @param view
	 */
	public void onButtonRefreshUsingSameProcessClick(View view) {
		// If this service is available
		if (ipBoundToService) {
			try {
				RequestorBean requestor = new RequestorBean();
	    		requestor.setRequestorId("red@societies.local");
	    		String ownerId = "me@societies.local";
	    		String dataId = "me@societies.local/ENTITY/person/1/ATTRIBUTE/name/13";
	    		Action action = new Action();
	    		action.setActionConstant(ActionConstants.READ);
				ResponseItem permission = targetIPService.checkPermission(requestor, ownerId, dataId, action);
				StringBuffer sb = new StringBuffer();
				sb.append("Permission retrieved: "+(null !=permission));
				if (null != permission) {
					sb.append("\nDecision: "+permission.getDecision().name());
					sb.append("\nOn resource: "+permission.getRequestItem().getResource().getCtxUriIdentifier());
				}
				txtLocation.setText(sb.toString());
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			Toast.makeText(this, "No service connected.", Toast.LENGTH_SHORT);
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
    public void onButtonRefreshUsingDifferentProcessClick(View view) {
    	// If this service is available
    	if (opBoundToService) {
    		//-- checkPermission
    		// Name the out process method
    		String[] privacyDataManagerMethodsArray = ServiceMethodTranslator.getMethodsArrayFromInterface(IPrivacyDataManager.class);// IPrivacyDataManager.methodsArray;//
    		String targetMethod = privacyDataManagerMethodsArray[0];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(privacyDataManagerMethodsArray, targetMethod), 0, 0);
    		// Fill parameters
    		Bundle outBundle = new Bundle();
    		RequestorBean requestor = new RequestorBean();
    		requestor.setRequestorId("red@societies.local");
    		String ownerId = "me@societies.local";
    		String dataId = "me@societies.local/ENTITY/person/1/ATTRIBUTE/name/13";
    		Action action = new Action();
    		action.setActionConstant(ActionConstants.READ);
    		outBundle.putSerializable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), requestor);
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), ownerId);
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), dataId);
    		outBundle.putSerializable(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), action);
    		outMessage.setData(outBundle);
    		// Call the out process method
    		try {
				targetOPService.send(outMessage);
			} catch (RemoteException e) {
				Toast.makeText(this, "No such method in this service.", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
    	}
    	else {
    		Toast.makeText(this, "No service connected.", Toast.LENGTH_SHORT);
		}
    }

    /**
     * Utilities button to reset all values of this activity
     * @param view
     */
	public void onButtonResetClick(View view) {
		txtLocation.setText("Nothing yet");
    }
	
	
	/* **************
	 * Broadcast receiver
	 * ************** */
	
	/**
	 * Broadcast receiver to receive intents from Service methods
	 * 
	 * TODO: Intent Categories could be used to discriminate between 
	 * returned method intents rather than an intent per method 
	 *
	 */
	private class ServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(this.getClass().getSimpleName(), intent.getAction());
			
			// CHECK_PERMISSION
			if (intent.getAction().equals(IPrivacyDataManager.CHECK_PERMISSION)) {
				Log.i(TAG, "Out of process real service received intent - CHECK_PERMISSION");
				
				ResponseItem permission = null;
				if(intent.hasExtra(IPrivacyDataManager.CHECK_PERMISSION_RESULT)) {
					permission = (ResponseItem) intent.getSerializableExtra(IPrivacyDataManager.CHECK_PERMISSION_RESULT);
				}
				
				StringBuffer sb = new StringBuffer();
				sb.append("Permission retrieved: "+(null !=permission));
				if (null != permission) {
					sb.append("\nDecision: "+permission.getDecision().name());
					sb.append("\nOn resource: "+permission.getRequestItem().getResource().getCtxUriIdentifier());
				}
				txtLocation.setText(sb.toString());
			}
			// OBFUSCATE_DATA
			if (intent.getAction().equals(IPrivacyDataManager.OBFUSCATE_DATA)) {
				Log.i(TAG, "Out of process real service received intent - OBFUSCATE_DATA");

				IDataWrapper obfuscatedDataWrapper = null;
				if(intent.hasExtra(IPrivacyDataManager.OBFUSCATE_DATA_RESULT)) {
					obfuscatedDataWrapper = (IDataWrapper) intent.getParcelableExtra(IPrivacyDataManager.OBFUSCATE_DATA_RESULT);
				}
				
				StringBuffer sb = new StringBuffer();
				sb.append("Data obfuscated: "+(null != obfuscatedDataWrapper));
				if (null != obfuscatedDataWrapper) {
					sb.append("\nData Id: "+obfuscatedDataWrapper.getDataId());
					sb.append("\nData type: "+obfuscatedDataWrapper.getData().getClass().getSimpleName());
				}
				txtLocation.setText(sb.toString());
			}
			else {
				Log.e(TAG, "Bad intent received: "+intent.getAction());
			}
		}
		
	}
}
