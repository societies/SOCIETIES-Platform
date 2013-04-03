/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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

package org.societies.android.platform.useragent.feedback.container.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.api.internal.useragent.IAndroidUserFeedback;
import org.societies.android.api.internal.useragent.model.ExpProposalContent;
import org.societies.android.api.internal.useragent.model.ExpProposalType;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.android.platform.useragent.feedback.container.TestContainerFeedbackService;
import org.societies.android.platform.useragent.feedback.container.TestContainerFeedbackService.FeedbackContainerBinder;
import org.societies.android.platform.useragent.feedback.guis.AcknackPopup;
import org.societies.android.platform.useragent.feedback.model.UserFeedbackEventTopics;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * @author Eliza
 *
 */
public class TestUserFeedback extends ServiceTestCase <TestContainerFeedbackService>{

	private static final String LOG_TAG = TestUserFeedback.class.getName();
	private static final String CLIENT_ID = LOG_TAG;

	private Boolean receivedResult = false;
	private EventsHelper eventsHelper;
	private boolean isEventsConnected = false;

	public TestUserFeedback() {
		super(TestContainerFeedbackService.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.receivedResult = false;
		setupBroadcastReceiver();
		//setup eventsHelper
		eventsHelper = new EventsHelper(this.getContext());
		eventsHelper.setUpService(new IMethodCallback() {
			@Override
			public void returnAction(String result) {
				Log.d(LOG_TAG, "eventMgr callback: ReturnAction(String) called");
			}

			@Override
			public void returnAction(boolean resultFlag) {
				Log.d(LOG_TAG, "eventMgr callback: ReturnAction(boolean) called. Connected");
				isEventsConnected=true;
			}

			@Override
			public void returnException(String arg0) { }
		});

	}

	private class UserFeedbackBroadcastReceiver extends BroadcastReceiver{
		private final String LOG_TAG = UserFeedbackBroadcastReceiver.class.getName();
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());

			assertNotNull(intent.getParcelableExtra(IAndroidUserFeedback.INTENT_RETURN_VALUE));
			TestUserFeedback.this.receivedResult = true;
			Log.d(LOG_TAG, "OnReceive finished");
		}
	}


	@MediumTest
	public void testGetExplicitFB() {

		Intent userFeedbackIntent = new Intent(getContext(), this.getClass());

		FeedbackContainerBinder binder =  (FeedbackContainerBinder) bindService(userFeedbackIntent);

		IAndroidUserFeedback ufService = (IAndroidUserFeedback) binder.getService();

		ExpProposalContent proposal = new ExpProposalContent("Testing explicit proposal user feedback", new String[]{"Yes","No"});
		ExpFeedbackResultBean bean = ufService.getExplicitFB(CLIENT_ID, ExpProposalType.ACKNACK, proposal);

		while (!this.receivedResult){
			try {
				Log.d(LOG_TAG, "Not received result");
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.d(LOG_TAG, "Received result");
	}

	@MediumTest
	public void testGetExplicitFBTriggerFromCloud(){
		/*Intent userFeedbackIntent = new Intent(getContext(), this.getClass());
		FeedbackContainerBinder binder = (FeedbackContainerBinder) bindService(userFeedbackIntent);
		IAndroidUserFeedback ufService = binder.getService();*/

		IAndroidUserFeedback ufService = new AndroidUserFeedbackService(getContext(), false);
		
		boolean started = ufService.startService();

		if (started){
			Log.d(LOG_TAG, "Connecting to pubsub");

			Log.d(LOG_TAG, "Setup broadcast receiver");

			Log.d(LOG_TAG, "new EventService");
			if (isEventsConnected){

				Log.d(LOG_TAG, "Connected to eventsManager");
				subscribeToEvent();
				publishEvent();


			}else{
				android.test.AndroidTestCase.fail("Failed to connect to : "+EventsHelper.class.getName());
			}
		}
		else{
			android.test.AndroidTestCase.fail("Failed to startService(): "+IAndroidUserFeedback.class.getName());
		}


		while (!this.receivedResult){
			try {
				Log.d(LOG_TAG, "Not received result");
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.d(LOG_TAG, "Received result");
	}


	/**
	 * Create a broadcast receiver
	 * 
	 */
	private void setupBroadcastReceiver() {
		BroadcastReceiver receiver = null;

		Log.d(LOG_TAG, "Set up broadcast receiver");

		receiver = new UserFeedbackBroadcastReceiver();
		IntentFilter intentFilter = createTestIntentFilter();

		getContext().registerReceiver(receiver, intentFilter); 

		Log.d(LOG_TAG, "Registered broadcast receiver");
	}

	/**
	 * Create a suitable intent filter
	 * @return IntentFilter
	 */
	private IntentFilter createTestIntentFilter() {
		//register broadcast receiver to receive SocietiesEvents return values 
		IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(IAndroidUserFeedback.GET_IMPLICITFB);
		intentFilter.addAction(IAndroidUserFeedback.GET_EXPLICITFB);
		intentFilter.addAction(IAndroidUserFeedback.INTENT_RETURN_VALUE);

		return intentFilter;
	}


	private void publishEvent(){
		UserFeedbackBean bean = new UserFeedbackBean();
		bean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
		List<String> options = new ArrayList<String>();
		options.add("Yes");
		options.add("No");
		bean.setOptions(options);
		bean.setProposalText("Click here to test user feedback notifications triggered by pubsub");
		bean.setRequestId(UUID.randomUUID().toString());
		bean.setType(ExpProposalType.ACKNACK);
		try {
			if (eventsHelper==null){
				Log.d(LOG_TAG, "EventsHelper is null");
			}else{
				Log.d(LOG_TAG, "EventsHelper is NOT null");
			}

			eventsHelper.publishEvent(UserFeedbackEventTopics.REQUEST, bean, new IPlatformEventsCallback() {
				@Override
				public void returnAction(int arg0) {
					Log.d(LOG_TAG, "eventMgr callback: ReturnAction(String) called");
				}

				@Override
				public void returnAction(boolean arg0) {
					Log.d(LOG_TAG, "eventMgr callback: ReturnAction(boolean) called. Published event");
				}

				@Override
				public void returnException(int arg0) { }
			});
		} catch (PlatformEventsHelperNotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	private void subscribeToEvent() {
		try {
			eventsHelper.subscribeToEvent(IAndroidSocietiesEvents.UF_REQUEST_INTENT, new IPlatformEventsCallback() {
				@Override
				public void returnAction(int arg0) {
					Log.d(LOG_TAG, "eventMgr callback: ReturnAction(String) called");					
				}
				
				@Override
				public void returnAction(boolean arg0) {
					Log.d(LOG_TAG, "eventMgr callback: ReturnAction(boolean) called. Subscribed to event");
				}

				@Override
				public void returnException(int arg0) { }
			});
		} catch (PlatformEventsHelperNotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
