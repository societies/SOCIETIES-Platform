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

import org.societies.android.api.internal.useragent.IAndroidUserFeedback;
//import org.societies.android.platform.androidutils.AndroidNotifier;
import org.societies.android.platform.useragent.feedback.guis.AcknackPopup;
import org.societies.android.platform.useragent.feedback.guis.CheckboxPopup;
import org.societies.android.platform.useragent.feedback.guis.ExplicitPopup;
import org.societies.android.platform.useragent.feedback.guis.ImplicitPopup;
import org.societies.android.platform.useragent.feedback.guis.RadioPopup;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;
import org.societies.identity.IdentityManagerImpl;
import org.societies.useragent.api.model.UserFeedbackEventTopics;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

public class AndroidUserFeedbackBase implements IAndroidUserFeedback, Subscriber{

	private static final String LOG_TAG = AndroidUserFeedbackBase.class.getName();
	private Context androidContext;
	private ClientCommunicationMgr ccm;
	private PubsubClientAndroid pubsubClient;
	private boolean restrictBroadcast;
	private IIdentity myCloudID;
	private HashMap<String, ExplicitPopup> expPopups;
	private HashMap<String, ImplicitPopup> impPopups;

	public AndroidUserFeedbackBase(Context androidContext, ClientCommunicationMgr ccm, PubsubClientAndroid pubsubClient, boolean restrictBroadcast){
		this.androidContext = androidContext;
		this.ccm = ccm;
		this.pubsubClient = pubsubClient;
		this.restrictBroadcast = restrictBroadcast;
		
		expPopups = new HashMap<String, ExplicitPopup>();
		impPopups = new HashMap<String, ImplicitPopup>();

		assignConnectionParameters();

		//register for events from user feedback pubsub node
		try {
			Log.d(LOG_TAG, "Registering for user feedback pubsub node");
			pubsubClient.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.REQUEST, this);
			pubsubClient.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, this);
			pubsubClient.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, this);
			Log.d(LOG_TAG, "Pubsub registration complete!");
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
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
		try {
			Log.d(LOG_TAG, "Sending user feedback request event via pubsub");
			pubsubClient.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
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
		try {
			Log.d(LOG_TAG, "Sending user feedback request event via pubsub");
			pubsubClient.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
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
		try {
			Log.d(LOG_TAG, "Sending user feedback request event via pubsub");
			pubsubClient.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}

	public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
		Log.d(LOG_TAG, "Received pubsub event with topic: "+eventTopic);
		
		if(eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.REQUEST)){
			//read from request bean
			UserFeedbackBean ufBean = (UserFeedbackBean)item;
			switch(ufBean.getMethod()){
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
			}
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
		//Get the Cloud destination
		String cloudCommsDestination = this.ccm.getIdManager().getCloudNode().getJid();
		Log.d(LOG_TAG, "Cloud Node: " + cloudCommsDestination);

		//String domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
		//Log.d(LOG_TAG, "Domain Authority Node: " + domainCommsDestination);

		try {
			this.myCloudID = IdentityManagerImpl.staticfromJid(cloudCommsDestination);
			Log.d(LOG_TAG, "Cloud node identity: " + this.myCloudID);

			//this.domainNodeIdentity = IdentityManagerImpl.staticfromJid(domainCommsDestination);
			//Log.d(LOG_TAG, "Domain node identity: " + this.cloudNodeIdentity);

		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, "Unable to get cloud node identity", e);
			throw new RuntimeException(e);
		}     
	}
}
