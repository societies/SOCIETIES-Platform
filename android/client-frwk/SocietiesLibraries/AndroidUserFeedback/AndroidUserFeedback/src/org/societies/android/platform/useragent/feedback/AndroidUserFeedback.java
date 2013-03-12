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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.internal.useragent.IAndroidUserFeedback;
import org.societies.android.api.internal.useragent.model.ExpProposalContent;
import org.societies.android.api.internal.useragent.model.ImpProposalContent;
import org.societies.android.platform.useragent.feedback.constants.UserFeedbackActivityIntentExtra;
import org.societies.android.platform.useragent.feedback.guis.AcknackPopup;
import org.societies.android.platform.useragent.feedback.guis.CheckboxPopup;
import org.societies.android.platform.useragent.feedback.guis.ExplicitPopup;
import org.societies.android.platform.useragent.feedback.guis.ImplicitPopup;
import org.societies.android.platform.useragent.feedback.guis.RadioPopup;
import org.societies.android.platform.useragent.feedback.guis.UserFeedbackNotification;
import org.societies.android.platform.useragent.feedback.model.UserFeedbackEventTopics;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * @author Eliza
 *
 */
public class AndroidUserFeedback {

	public static final String LOG_TAG = AndroidUserFeedback.class.getName();
	private HashMap<String, ExplicitPopup> expPopups;
	private HashMap<String, ImplicitPopup> impPopups;
	private IIdentity myCloudID;
	private final Context androidContext;
	public final static String RETURN_TO_CLOUD = "returnToCloud";

	public AndroidUserFeedback(Context context) {
		this.androidContext = context;

		expPopups = new HashMap<String, ExplicitPopup>();
		impPopups = new HashMap<String, ImplicitPopup>();
	}



	public void setMyCloudID(IIdentity myCloudID) {
		this.myCloudID = myCloudID;
	}


	/*
	 * This method returns null - String[] return type returned using Intents
	 */
	public ExpFeedbackResultBean getExplicitFB(String client, int type, ExpProposalContent content) {
		Log.d(LOG_TAG, "Received request for explicit feedback");
		Log.d(LOG_TAG, "Content: "+content.getProposalText());
		Log.d(LOG_TAG, "Options size:" +content.getOptions().length);
		//generate unique ID for this pubsub event and feedback request
		String requestID = UUID.randomUUID().toString();

		//create user feedback bean to fire in pubsub event
		UserFeedbackBean ufBean = new UserFeedbackBean();
		ufBean.setRequestId(requestID);
		ufBean.setType(type);
		ufBean.setProposalText(content.getProposalText());
		ArrayList<String> optionsList = new ArrayList<String>();
		String[] options = content.getOptions();
		for (int i=0; i<options.length; i++){
			optionsList.add(options[i]);
		}
		
		ufBean.setOptions(optionsList);
		ufBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);

		
		processExpFeedbackRequestEvent(client, requestID, type, content.getProposalText(), optionsList);

		
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


	public ImpFeedbackResultBean getImplicitFB(String client, int type, ImpProposalContent content) {
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


	public void processEventReceived(Context context, Intent intent) {
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
		else if (intent.getAction().equals(IAndroidSocietiesEvents.USER_FEEDBACK_REQUEST_INTENT)){
			Log.d(LOG_TAG, IAndroidSocietiesEvents.USER_FEEDBACK_REQUEST_INTENT+" received: ");
			UserFeedbackBean feedbackBean = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);
			//TEMP WORKAROUND
			String text = feedbackBean.getProposalText();
			text = text.replace(']', '>');
			text = text.replace('[', '<');
			feedbackBean.setProposalText(text);
			//
			Log.d(LOG_TAG, "Feedback type: "+feedbackBean.getType()+", method: "+feedbackBean.getMethod()+",  proposalText: "+feedbackBean.getProposalText());
			switch(feedbackBean.getMethod()){
			case GET_EXPLICIT_FB:
				String expRequestID = feedbackBean.getRequestId();
				int expType = feedbackBean.getType();
				String expProposalText = feedbackBean.getProposalText();
				ArrayList<String> optionsList = (ArrayList<String>) feedbackBean.getOptions();

				processExpFeedbackRequestEvent(this.RETURN_TO_CLOUD, expRequestID, expType, expProposalText, optionsList);
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
		}	

	}

	private void processExpFeedbackRequestEvent(String clientID, String requestID, int type, String proposalText, ArrayList<String> optionsList){
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
			//AcknackPopup acknackPopup = new AcknackPopup();
			//UserFeedbackNotification notification = new UserFeedbackNotification(this.androidContext);
			//notification.getExplicitFB(requestID);
			Log.d(LOG_TAG, "Starting AckNack activity");


			Intent ackNackIntent = new Intent(androidContext, AcknackPopup.class);
			ackNackIntent.putExtra(UserFeedbackActivityIntentExtra.CLIENT_ID, clientID);
			ackNackIntent.putExtra(UserFeedbackActivityIntentExtra.REQUEST_ID, requestID);
			ackNackIntent.putExtra(UserFeedbackActivityIntentExtra.TYPE, type);
			ackNackIntent.putExtra(UserFeedbackActivityIntentExtra.PROPOSAL_TEXT, proposalText);
			ackNackIntent.putStringArrayListExtra(UserFeedbackActivityIntentExtra.OPTIONS, optionsList);
			ackNackIntent.putExtra(UserFeedbackActivityIntentExtra.INTENT_RETURN, IAndroidUserFeedback.GET_EXPLICITFB);
			ackNackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.androidContext.startActivity(ackNackIntent);
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
}
