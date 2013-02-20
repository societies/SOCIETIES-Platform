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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.internal.useragent.IAndroidUserFeedback;
import org.societies.android.api.internal.useragent.model.ExpProposalContent;
import org.societies.android.api.internal.useragent.model.ImpProposalContent;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.useragent.feedback.guis.AcknackPopup;
import org.societies.android.platform.useragent.feedback.guis.CheckboxPopup;
import org.societies.android.platform.useragent.feedback.guis.ExplicitPopup;
import org.societies.android.platform.useragent.feedback.guis.ImplicitPopup;
import org.societies.android.platform.useragent.feedback.guis.RadioPopup;
import org.societies.android.platform.useragent.feedback.model.UserFeedbackEventTopics;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.identity.IdentityManagerImpl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class AndroidUserFeedbackBase extends Service implements IAndroidUserFeedback{

	private static final String LOG_TAG = AndroidUserFeedbackBase.class.getName();
	private static final String SERVICE_ACTION = "org.societies.android.platform.events.ServicePlatformEventsRemote";
	private static final String CLIENT_NAME = AndroidUserFeedbackBase.class.getCanonicalName();
	private Context androidContext;
	private ClientCommunicationMgr ccm;
	private Messenger eventMgrService = null;
	private boolean restrictBroadcast;
	private IIdentity myCloudID;
	private HashMap<String, ExplicitPopup> expPopups;
	private HashMap<String, ImplicitPopup> impPopups;
	//TRACKING CONNECTION TO EVENTS MANAGER
	private boolean boundToEventMgrService = false;
	private LocalBinder binder = null;


	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>STARTING THIS SERVICE>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}

	@Override
	public void onCreate () {
		this.binder = new LocalBinder();
		Log.d(LOG_TAG, "Friends service starting");

		setupBroadcastReceiver();
		bindToEventsManagerService();
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Friends service terminating");
	}

	/**Create Binder object for local service invocation */
	public class LocalBinder extends Binder {
		public AndroidUserFeedbackBase getService() {
			return AndroidUserFeedbackBase.this;
		}
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>REGISTER FOR EVENTS>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
			Log.d(LOG_TAG, "Received action: " + intent.getAction());

			//EVENT MANAGER INTENTS
			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				Log.d(LOG_TAG, "Subscribed to all event - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				Log.d(LOG_TAG, "Subscribed to events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				Log.d(LOG_TAG, "Un-subscribed from events - listening to: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
			}

			//if the event is a request for feedback, pop up the appropriate feedback GUI
			else if (intent.getAction().equals(IAndroidSocietiesEvents.USER_FEEDBACK_REQUEST_EVENT)){
				Log.d(LOG_TAG, IAndroidSocietiesEvents.USER_FEEDBACK_REQUEST_EVENT+" received: ");
				UserFeedbackBean feedbackBean = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				Log.d(LOG_TAG, "Feedback type: "+feedbackBean.getType()+", method: "+feedbackBean.getMethod()+",  proposalText: "+feedbackBean.getProposalText());
				switch(feedbackBean.getMethod()){
				case GET_EXPLICIT_FB:
					String expRequestID = feedbackBean.getRequestId();
					int expType = feedbackBean.getType();
					String expProposalText = feedbackBean.getProposalText();
					List<String> optionsList = feedbackBean.getOptions();
					processExpFeedbackRequestEvent(expRequestID, expType, expProposalText, optionsList);
					break;
				case GET_IMPLICIT_FB:
					String impRequestID = feedbackBean.getRequestId();
					int impType = feedbackBean.getType();
					String impProposalText = feedbackBean.getProposalText();
					int timeout = feedbackBean.getTimeout();
					processImpFeedbackRequestEvent(impRequestID, impType, impProposalText, timeout);
					break;
				case SHOW_NOTIFICATION:
					String notRequestID = feedbackBean.getRequestId();
					String notProposalText = feedbackBean.getProposalText();
					processNotificationRequestEvent(notRequestID, notProposalText);
					break;
				}
			}/*else if (intent.getAction().equals(IAndroidSocietiesEvents.USER_FEEDBACK_EXPLICIT_RESPONSE_EVENT)){
				ExpFeedbackResultBean expFeedbackBean = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				String expResponseID = expFeedbackBean.getRequestId();
				List<String> expResult = expFeedbackBean.getFeedback();
				processExpResponseEvent(expResponseID, expResult);
			}else if (intent.getAction().equals(IAndroidSocietiesEvents.USER_FEEDBACK_IMPLICIT_RESPONSE_EVENT)){
				ImpFeedbackResultBean impFeedbackBean = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				String impResponseID = impFeedbackBean.getRequestId();
				boolean impResult = impFeedbackBean.isAccepted();
				processImpResponseEvent(impResponseID, impResult);
			}*/
			/*			//PUBSUB EVENTS
			else if (intent.getAction().equals(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_RECEIVED_EVENT)) {
				Log.d(LOG_TAG, "Frient Request received: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
				CssAdvertisementRecord advert = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				String description = advert.getName() + " sent a friend request";
				addNotification(description, "Friend Request", advert);
			}
			else if (intent.getAction().equals(IAndroidSocietiesEvents.CSS_FRIEND_REQUEST_ACCEPTED_EVENT)) {
				Log.d(LOG_TAG, "Frient Request accepted: " + intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, -999));
				CssAdvertisementRecord advert = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
				String description = advert.getName() + " accepted your friend request";
				addNotification(description, "Friend Request Accepted", advert);
			}		*/	
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

		return intentFilter;
	}
	public AndroidUserFeedbackBase(Context androidContext, boolean restrictBroadcast){
		this.androidContext = androidContext;
		//check with Alec that login has been completed
		this.ccm = new ClientCommunicationMgr(androidContext, true);

		this.restrictBroadcast = restrictBroadcast;

		expPopups = new HashMap<String, ExplicitPopup>();
		impPopups = new HashMap<String, ImplicitPopup>();

		assignConnectionParameters();

		//register for events from user feedback pubsub node
/*		try {
			Log.d(LOG_TAG, "Registering for user feedback pubsub node");
			//eventMgrService.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.REQUEST, this);
			//eventMgrService.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, this);
			//eventMgrService.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, this);
			Log.d(LOG_TAG, "Pubsub registration complete!");
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}*/
	}

	/*
	 * This method returns null - String[] return type returned using Intents
	 */
	public String[] getExplicitFB(String client, int type, ExpProposalContent content) {
		Log.d(LOG_TAG, "Received request for explicit feedback");
		Log.d(LOG_TAG, "Content: "+content.getProposalText());

		//generate unique ID for this pubsub event and feedback request
		String requestID = UUID.randomUUID().toString();

		//create user feedback bean to fire in pubsub event
		UserFeedbackBean ufBean = new UserFeedbackBean();
		ufBean.setRequestId(requestID);
		ufBean.setType(type);
		ufBean.setProposalText(content.getProposalText());
		List<String> optionsList = new ArrayList<String>();
		for(String nextOption: content.getOptions()){
			optionsList.add(nextOption);
		}
		ufBean.setOptions(optionsList);
		ufBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);

		//send pubsub event to all user agents
/*		try {
			Log.d(LOG_TAG, "Sending user feedback request event via pubsub");
			//eventMgrService.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
			//TODO: send the event
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}*/
		return null;
	}

	/*
	 * This method returns null - Boolean return type returned using Intents
	 */
	public Boolean getImplicitFB(String client, int type, ImpProposalContent content) {
		Log.d(LOG_TAG, "Received request for implicit feedback");
		Log.d(LOG_TAG, "Content: "+ content.getProposalText());

		//generate unique ID for this pubsub event and feedback request
		String requestID = UUID.randomUUID().toString();

		//create user feedback bean to fire in pubsub event
		UserFeedbackBean ufBean = new UserFeedbackBean();
		ufBean.setRequestId(requestID);
		ufBean.setType(type);
		ufBean.setProposalText(content.getProposalText());
		ufBean.setTimeout(content.getTimeout());
		ufBean.setMethod(FeedbackMethodType.GET_IMPLICIT_FB);

		//send pubsub event to all user agents
/*		try {
			Log.d(LOG_TAG, "Sending user feedback request event via pubsub");
			eventMgrService.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
			//TODO: send the event
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}*/
		return null;
	}

	public void showNotification(String client, String notificationText) {
		Log.d(LOG_TAG, "Received request for notification");
		Log.d(LOG_TAG, "Content: "+ notificationText);

		//generate unique ID for this pubsub event and feedback request
		String requestID = UUID.randomUUID().toString();

		//create user feedback bean to fire in pubsub event
		UserFeedbackBean ufBean = new UserFeedbackBean();
		ufBean.setRequestId(requestID);
		ufBean.setProposalText(notificationText);
		ufBean.setMethod(FeedbackMethodType.SHOW_NOTIFICATION);

		//send pubsub event to all user agents
/*		try {
			Log.d(LOG_TAG, "Sending user feedback request event via pubsub");
			eventMgrService.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
			//TODO: send the event
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}*/
	}

	public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
		Log.d(LOG_TAG, "Received pubsub event with topic: "+eventTopic);

		if(eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.REQUEST)){
			//read from request bean
			UserFeedbackBean ufBean = (UserFeedbackBean)item;
/*			switch(ufBean.getMethod()){
			case GET_EXPLICIT_FB:
				String expRequestID = ufBean.getRequestId();
				int expType = ufBean.getType();
				String expProposalText = ufBean.getProposalText();
				List<String> optionsList = ufBean.getOptions();
				this.processExpFeedbackRequestEvent(expRequestID, expType, expProposalText, optionsList);
				break;
			case GET_IMPLICIT_FB:
				String impRequestID = ufBean.getRequestId();
				int impType = ufBean.getType();
				String impProposalText = ufBean.getProposalText();
				int timeout = ufBean.getTimeout();
				this.processImpFeedbackRequestEvent(impRequestID, impType, impProposalText, timeout);
				break;
			case SHOW_NOTIFICATION:
				String notRequestID = ufBean.getRequestId();
				String notProposalText = ufBean.getProposalText();
				this.processNotificationRequestEvent(notRequestID, notProposalText);
				break;
			}*/
		}else if(eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.EXPLICIT_RESPONSE)){
			//read from explicit response bean
			ExpFeedbackResultBean expFeedbackBean = (ExpFeedbackResultBean)item;
			String expResponseID = expFeedbackBean.getRequestId();
			List<String> expResult = expFeedbackBean.getFeedback();
			this.processExpResponseEvent(expResponseID, expResult);
		}else if(eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.IMPLICIT_RESPONSE)){
			//read from implicit response bean
			ImpFeedbackResultBean impFeedbackBean = (ImpFeedbackResultBean)item;
			String impResponseID = impFeedbackBean.getRequestId();
			boolean impResult = impFeedbackBean.isAccepted();
			this.processImpResponseEvent(impResponseID, impResult);
		}
	}

	private void processExpFeedbackRequestEvent(String requestID, int type, String proposalText, List<String> optionsList){
		//use android notification system
		//popupWindow with sound
		switch(type){
		case 0:	//radioPopup
			RadioPopup radPopup = new RadioPopup();
			break;
		case 1:	//checkboxPopup
			Log.d(LOG_TAG, "Creating a checkbox popup");
			CheckboxPopup cbPopup = new CheckboxPopup();
			expPopups.put(requestID, cbPopup);
			List<String> result = cbPopup.getFeedback(proposalText, optionsList);
			break;
		case 2: //acknackPopup
			AcknackPopup acknackPopup = new AcknackPopup();
			break;
		}

	}

	private void processImpFeedbackRequestEvent(String requestID, int type, String proposalText, int timeout){
		//use android notification system
		//popupWindow with sound
	}

	private void processNotificationRequestEvent(String requestID, String proposalText){
		//use android notification system
		//AndroidNotifier notifications;
	}

	private void processExpResponseEvent(String responseID, List<String> result){

	}

	private void processImpResponseEvent(String responseID, Boolean result){

	}

	/**
	 * Assign connection parameters (must happen after successful XMPP login)
	 */
	private void assignConnectionParameters() {
		try {
			//Get the Cloud destination
			String cloudCommsDestination = this.ccm.getIdManager().getCloudNode().getJid();
			Log.d(LOG_TAG, "Cloud Node: " + cloudCommsDestination);

			//String domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
			//Log.d(LOG_TAG, "Domain Authority Node: " + domainCommsDestination);


			this.myCloudID = IdentityManagerImpl.staticfromJid(cloudCommsDestination);
			Log.d(LOG_TAG, "Cloud node identity: " + this.myCloudID);

			//this.domainNodeIdentity = IdentityManagerImpl.staticfromJid(domainCommsDestination);
			//Log.d(LOG_TAG, "Domain node identity: " + this.cloudNodeIdentity);

		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, "Unable to get cloud node identity", e);
			throw new RuntimeException(e);
		}     
	}


	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BIND TO EXTERNAL "EVENT MANAGER">>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/** Bind to the Events Manager Service */
	private void bindToEventsManagerService() {
		Intent serviceIntent = new Intent(SERVICE_ACTION);
		Log.d(LOG_TAG, "Binding to Events Manager Service: ");
		bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	private ServiceConnection serviceConnection = new ServiceConnection() {

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
			String targetMethod = IAndroidSocietiesEvents.methodsArray[1];
			Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
			Bundle outBundle = new Bundle();

			//PARAMETERS
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
			outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), IAndroidSocietiesEvents.USER_FEEDBACK_EXPLICIT_RESPONSE_INTENT);
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
