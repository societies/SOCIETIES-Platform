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
package org.societies.android.platform.personalisation.impl;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.internal.personalisation.IPersonalisationManagerInternalAndroid;
import org.societies.android.api.personalisation.IPersonalisationManagerAndroid;
import org.societies.android.api.utilities.RemoteServiceHandler;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.personalisation.mgmt.PersonalisationManagerBean;
import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * @author Eliza
 *
 */
public class PersonalisationManagerAndroidRemote extends Service implements IPersonalisationManagerInternalAndroid{

	private static final String LOG_TAG = PersonalisationManagerAndroidRemote.class.getName();
	private Messenger inMessenger = new Messenger(new IncomingHandler());
	private ClientCommunicationMgr ccm;
	private Context androidContext;
	private PersonalisationManagerAndroid personalisationService;


	public PersonalisationManagerAndroidRemote(Context androidContext, boolean restrictBroadcast){
		this.androidContext = androidContext;
		personalisationService = new PersonalisationManagerAndroid(androidContext, restrictBroadcast, ccm);

	}

	/**
	 * For remote IPC
	 * @author Eliza
	 *
	 */
	class IncomingHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			Log.d(LOG_TAG, "Received msg");
			if (msg.getData().containsKey(IPersonalisationManagerAndroid.GET_PREFERENCE)){
				Log.d(LOG_TAG, "Received request for "+IPersonalisationManagerAndroid.GET_PREFERENCE);
				PersonalisationManagerBean payload = msg.getData().getParcelable(IPersonalisationManagerAndroid.GET_PREFERENCE);

			}else{
				Log.d(LOG_TAG, "Received request for "+IPersonalisationManagerAndroid.GET_INTENT_ACTION);
				PersonalisationManagerBean payload = msg.getData().getParcelable(IPersonalisationManagerAndroid.GET_INTENT_ACTION);
			}
		}
	}
	
	
	@Override
	public void onCreate () {
		ccm = new ClientCommunicationMgr(getApplicationContext(), true);

		ccm.bindCommsService(new IMethodCallback() {

			@Override
			public void returnAction(String result) {
				Log.d(LOG_TAG, "comms callback: returnAction(string) called. ??");
			}

			@Override
			public void returnAction(boolean resultFlag) {
				Log.d(LOG_TAG, "comms callback: returnAction(boolean) called. Connected");
				ccm.register(PersonalisationManagerAndroid.ELEMENT_NAMES, PersonalisationManagerAndroid.NAMESPACES, PersonalisationManagerAndroid.PACKAGES, new IMethodCallback() {

					@Override
					public void returnAction(String result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void returnAction(boolean resultFlag) {
						Log.d(LOG_TAG, "comms callback: returnAction(boolean) called. Registered");

					}
				});
				PersonalisationManagerAndroid serviceBase = new PersonalisationManagerAndroid(PersonalisationManagerAndroidRemote.this.getApplicationContext(),  false, PersonalisationManagerAndroidRemote.this.ccm);

				PersonalisationManagerAndroidRemote.this.inMessenger = new Messenger(new RemoteServiceHandler(serviceBase.getClass(), serviceBase, IPersonalisationManagerAndroid.methodsArray));
			}
		});


		Log.i(LOG_TAG, "PersonalisationManagerAndroidRemote creation");
	}




	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "PersonalisationManagerAndroidRemote onBind");
		return inMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		this.ccm.unregister(PersonalisationManagerAndroid.ELEMENT_NAMES, PersonalisationManagerAndroid.NAMESPACES, new IMethodCallback() {

			@Override
			public void returnAction(String result) {
				// TODO Auto-generated method stub

			}

			@Override
			public void returnAction(boolean resultFlag) {
				Log.d(LOG_TAG, "comms callback: returnAction(boolean) called. Unregistered");
				PersonalisationManagerAndroidRemote.this.ccm.unbindCommsService();
			}
		});
		Log.i(LOG_TAG, "PersonalisationManagerAndroidRemote terminating");
	}

	@Override
	public ActionBean getIntentAction(String clientID, RequestorBean requestor,
			String ownerID, ServiceResourceIdentifier serviceID,
			String preferenceName) {
		return this.personalisationService.getIntentAction(clientID, requestor, ownerID, serviceID, preferenceName);
	}

	@Override
	public ActionBean getPreference(String clientID, RequestorBean requestor,
			String ownerID, String serviceType,
			ServiceResourceIdentifier serviceID, String preferenceName) {
		return this.personalisationService.getPreference(clientID, requestor, ownerID, serviceType, serviceID, preferenceName);
	}

	@Override
	public ActionBean getIntentAction(String clientID, String ownerID,
			ServiceResourceIdentifier serviceID, String preferenceName) {
		return this.personalisationService.getIntentAction(clientID, ownerID, serviceID, preferenceName);
	}

	@Override
	public ActionBean getPreference(String clientID, String ownerID,
			String serviceType, ServiceResourceIdentifier serviceID,
			String preferenceName) {
		return this.personalisationService.getPreference(clientID, ownerID, serviceType, serviceID, preferenceName);
	}
}
