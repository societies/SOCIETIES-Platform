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

package org.societies.android.platform.useragent.feedback;

import java.lang.ref.WeakReference;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.api.internal.useragent.IAndroidUserFeedback;
import org.societies.android.api.internal.useragent.model.ExpProposalContent;
import org.societies.android.api.internal.useragent.model.ImpProposalContent;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.events.helper.EventsHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.identity.IdentityManagerImpl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class AndroidUserFeedbackService extends Service implements IAndroidUserFeedback{

	private static final String LOG_TAG = AndroidUserFeedbackService.class.getName();
	public static final String USER_FEEDBACK_EVENTS_ALL = "org.societies.android.api.internal.useragent";
	
	private static final String CLIENT_NAME = AndroidUserFeedbackService.class.getCanonicalName();
	
	private Context androidContext;
	private LocalBinder binder = null;
	
	private IIdentity myCloudID;
	
	
	private boolean isCommsConnected;
	private boolean restrictBroadcast;
	
	
	private AndroidUserFeedback userFeedback;
	private ClientCommunicationMgr clientCommsMgr;
	private EventsHelper eventsHelper;
	
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>STARTING THIS SERVICE>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}

	@Override
	public void onCreate () {
		this.binder = new LocalBinder(new AndroidUserFeedbackService(this.getApplicationContext()));
		this.clientCommsMgr = new ClientCommunicationMgr(getApplicationContext(), true);
		Log.d(LOG_TAG, "UserFeedback service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "UserFeedback service terminating");
	}

	/**Create Binder object for local service invocation */
	public class LocalBinder extends Binder {
		private WeakReference<AndroidUserFeedbackService> outerClassReference = null;
		
		public LocalBinder(AndroidUserFeedbackService instance) {
			outerClassReference = new WeakReference<AndroidUserFeedbackService>(instance);
		}
		
		public AndroidUserFeedbackService getService() {
			return outerClassReference.get();
		}
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Setup Broadcast Receiver for receiving events>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**Create a broadcast receiver
	 * @return the created broadcast receiver
	 */
	private BroadcastReceiver setupBroadcastReceiver() {
		BroadcastReceiver receiver = null;
		Log.d(LOG_TAG, "Set up broadcast receiver");

		receiver = new MainReceiver();
		this.registerReceiver(receiver, createIntentFilter());
		Log.d(LOG_TAG, "Registered broadcast receiver");

		return receiver;
	}

	/**Broadcast receiver to receive intent return values from EventManager service
	 * this is the class that is going to receive the events and has to pop up the notification. 
	 * */
	private class MainReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			AndroidUserFeedbackService.this.userFeedback.processEventReceived(context, intent);
		}
	}

	/**
	 * Create a suitable intent filter
	 * @return IntentFilter
	 */
	private IntentFilter createIntentFilter() {
		//register broadcast receiver to receive SocietiesEvents return values 
		IntentFilter intentFilter = new IntentFilter();
		//UserFeedback events
		intentFilter.addAction(IAndroidSocietiesEvents.USER_FEEDBACK_EXPLICIT_RESPONSE_EVENT);
		intentFilter.addAction(IAndroidSocietiesEvents.USER_FEEDBACK_IMPLICIT_RESPONSE_EVENT);
		intentFilter.addAction(IAndroidSocietiesEvents.USER_FEEDBACK_REQUEST_EVENT);
		//intentFilter.addAction(IAndroidSocietiesEvents.USER_FEEDBACK_SHOW_NOTIFICATION_EVENT);
		return intentFilter;
	}
	
	/**Default Constructor */
	public AndroidUserFeedbackService(Context androidContext){
		this(androidContext, true);
	}
	
	/**Parameterised Constructor */
	public AndroidUserFeedbackService(Context androidContext, boolean restrictBroadcast){
		this.androidContext = androidContext;
		this.userFeedback = new AndroidUserFeedback(androidContext);
		//check with Alec that login has been completed
		this.clientCommsMgr = new ClientCommunicationMgr(androidContext, true);
		this.restrictBroadcast = restrictBroadcast;
	}

	/*
	 * This method returns null - String[] return type returned using Intents
	 */
	public ExpFeedbackResultBean getExplicitFB(String client, int type, ExpProposalContent content) {
		return userFeedback.getExplicitFB(client, type, content);
	}

	/*
	 * This method returns null - Boolean return type returned using Intents
	 */
	public ImpFeedbackResultBean getImplicitFB(String client, int type, ImpProposalContent content) {
		return userFeedback.getImplicitFB(client, type, content);
	}

	public void showNotification(String client, String notificationText) {
		 userFeedback.showNotification(client, notificationText);
	}





	/**
	 * Assign connection parameters (must happen after successful XMPP login)
	 */
	private void assignConnectionParameters() {
		try {
			//Get the Cloud destination
			String cloudCommsDestination = this.clientCommsMgr.getIdManager().getCloudNode().getJid();
			Log.d(LOG_TAG, "Cloud Node: " + cloudCommsDestination);

			this.myCloudID = IdentityManagerImpl.staticfromJid(cloudCommsDestination);
			this.userFeedback.setMyCloudID(myCloudID);
			Log.d(LOG_TAG, "Cloud node identity: " + this.myCloudID);

		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, "Unable to get cloud node identity", e);
			throw new RuntimeException(e);
		}     
	}


	@Override
	public boolean startService() {
		if (!isCommsConnected){
			
		}else{
			this.clientCommsMgr.bindCommsService(new IMethodCallback() {
	
				@Override
				public void returnAction(String result) {
					Log.d(LOG_TAG, "comms callback: returnAction(string) called. ??");
				}
				
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "comms callback: returnAction(boolean) called. Connected");
					if (resultFlag){
						Log.d(LOG_TAG, "Connected to comms - resultflag true");
						assignConnectionParameters();
						Log.d(LOG_TAG, "Assigned connection parameters");
						setupBroadcastReceiver();
						Log.d(LOG_TAG, "Setup broadcast receiver");
						eventsHelper = new EventsHelper(AndroidUserFeedbackService.this.androidContext);
						Log.d(LOG_TAG, "new EventService");
						eventsHelper.setUpService(new IMethodCallback() {
							
							@Override
							public void returnAction(String result) {
								Log.d(LOG_TAG, "eventMgr callback: ReturnAction(String) called");
								
							}
							
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(LOG_TAG, "eventMgr callback: ReturnAction(boolean) called. Connected");
								if (resultFlag){
									Log.d(LOG_TAG, "Connected to eventsManager - resultFlag true");
									try {
										//subscribing to all user feedback events. 
										AndroidUserFeedbackService.this.eventsHelper.subscribeToEvents(AndroidUserFeedbackService.USER_FEEDBACK_EVENTS_ALL, new IPlatformEventsCallback() {
											
											@Override
											public void returnAction(int result) {	
												Log.d(LOG_TAG, "eventMgr callback: ReturnAction(int) called. ??");
											}
											@Override
											public void returnAction(boolean resultFlag) {
												Log.d(LOG_TAG, "eventMgr callback: ReturnAction(boolean) called. Subscribed to userfeedback events");
												if (resultFlag){
													Log.d(LOG_TAG, "resultFlag true - Subscribed to "+AndroidUserFeedbackService.USER_FEEDBACK_EVENTS_ALL+" events");
												}
												
											}
										});
									} catch (PlatformEventsHelperNotConnectedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							}
						});
						
					}
					
				}
			});
		}
		
		return true;
	}

	@Override
	public boolean stopService() {
		// TODO Auto-generated method stub
		return false;
	}

}
