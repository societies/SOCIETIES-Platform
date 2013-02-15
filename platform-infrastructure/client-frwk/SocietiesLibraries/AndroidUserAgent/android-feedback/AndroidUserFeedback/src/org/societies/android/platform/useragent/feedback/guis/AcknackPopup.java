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

package org.societies.android.platform.useragent.feedback.guis;


import org.societies.android.api.R;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.ServiceMethodTranslator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class AcknackPopup extends Activity{
	
	
	private static final String LOG_TAG = AcknackPopup.class.getName();
	private Messenger eventMgrService = null;
	
	
	private static final String CLIENT_NAME      = "org.societies.android.platform.useragent.feedback.guis.AcknackPopup";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        Log.d(LOG_TAG, "onCreate in AcknackPopup");
    }

    
    //connection to pubsub
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		private boolean boundToEventMgrService;

		public void onServiceConnected(ComponentName name, IBinder service) {
			boundToEventMgrService = true;
			eventMgrService = new Messenger(service);
			
			Log.d(this.getClass().getName(), "Connected to the Societies Event Mgr Service");
			
			//BOUND TO SERVICE - SUBSCRIBE TO RELEVANT EVENTS
			InvokeRemoteMethod invoke  = new InvokeRemoteMethod(CLIENT_NAME);
    		invoke.execute();
		}
		
		public void onServiceDisconnected(ComponentName name) {
			boundToEventMgrService = false;
		}
	};
	
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>SUBSCRIBE TO PUBSUB EVENTS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/** Async task to invoke remote service method */
    private class InvokeRemoteMethod extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeRemoteMethod.class.getName();
    	private String client;

    	public InvokeRemoteMethod(String client) {
    		this.client = client;
    	}

    	protected Void doInBackground(Void... args) {
    		//METHOD: subscribeToEvents(String client, String intentFilter) - ARRAY POSITION: 1
    		String targetMethod = IAndroidSocietiesEvents.methodsArray[6];
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
    		Bundle outBundle = new Bundle();

    		//PARAMETERS
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), IAndroidSocietiesEvents.USER_FEEDBACK_EXPLICIT_RESPONSE_EVENT);
    		//outBundle.putParcelable(IAndroidSocietiesEvents.USER_FEEDBACK_EXPLICIT_RESPONSE_EVENT, "");
    		Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
    		outMessage.setData(outBundle);

    		Log.d(LOCAL_LOG_TAG, "Sending event registration");
    		try {
    			eventMgrService.send(outMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    		return null;
    	}
    }
}
