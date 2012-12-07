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

package org.societies.useragent.feedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.societies.useragent.api.remote.IUserAgentRemoteMgr;
import org.societies.useragent.feedback.guis.AckNackGUI;
import org.societies.useragent.feedback.guis.CheckBoxGUI;
import org.societies.useragent.feedback.guis.RadioGUI;
import org.societies.useragent.feedback.guis.TimedGUI;
import org.springframework.scheduling.annotation.AsyncResult;

public class UserFeedback implements IUserFeedback, IInternalUserFeedback, Subscriber{

	//pubsub event schemas
	private static final List<String> EVENT_SCHEMA_CLASSES = 
			Collections.unmodifiableList(Arrays.asList(
					"org.societies.api.schema.useragent.feedback.UserFeedbackBean",
					"org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean",
					"org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean"));

	//GUI types for forms
	private static final String RADIO = "radio";
	private static final String CHECK = "check";
	private static final String ACK = "ack";
	private static final String ABORT = "abort";
	private static final String NOTIFICATION = "notification";

	Logger LOG = LoggerFactory.getLogger(UserFeedback.class);
	ICtxBroker ctxBroker;
	ICommManager commsMgr;
	PubsubClient pubsub;
	IUserAgentRemoteMgr uaRemote;
	RequestManager requestMgr;
	IIdentity myCloudID;
	HashMap<String, List<String>> expResults;
	HashMap<String, Boolean> impResults;
	static String UNDEFINED = "undefined";



	public void initialiseUserFeedback(){
		LOG.debug("User Feedback initialised!!");

		requestMgr = new RequestManager();
		expResults = new HashMap<String, List<String>>();
		impResults = new HashMap<String, Boolean>();

		//get cloud ID
		myCloudID = commsMgr.getIdManager().getThisNetworkNode();
		LOG.debug("Got my cloud ID: "+myCloudID);

		//create pubsub node
		try {
			LOG.debug("Creating user feedback pubsub node");
			pubsub.addSimpleClasses(EVENT_SCHEMA_CLASSES);
			pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.REQUEST);
			pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE);
			pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE);
			LOG.debug("Pubsub node created!");
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		//register for events from created pubsub node
		try {
			LOG.debug("Registering for user feedback pubsub node");
			pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.REQUEST, this);
			pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, this);
			pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, this);
			LOG.debug("Pubsub registration complete!");
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Future<List<String>> getExplicitFB(int type, ExpProposalContent content){
		LOG.debug("Received request for explicit feedback");
		LOG.debug("Content: "+content.getProposalText());

		List<String> result = null;

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

		//add new request to result hashmap
		expResults.put(requestID, null);

		//send pubsub event to all user agents
		try {
			LOG.debug("Sending user feedback request event via pubsub");
			pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}

		//wait for result
		while((List<String>)this.expResults.get(requestID) == null){
			try{
				synchronized(expResults){
					this.expResults.wait();
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		//set result and remove id from hashmap
		result = this.expResults.get(requestID);
		this.expResults.remove(requestID);

		return new AsyncResult<List<String>>(result);
	}

	@Override
	public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
		LOG.debug("Received request for implicit feedback");
		LOG.debug("Content: "+ content.getProposalText());

		Boolean result = false;

		//generate unique ID for this pubsub event and feedback request
		String requestID = UUID.randomUUID().toString();

		//create user feedback bean to fire in pubsub event
		UserFeedbackBean ufBean = new UserFeedbackBean();
		ufBean.setRequestId(requestID);
		ufBean.setType(type);
		ufBean.setProposalText(content.getProposalText());
		ufBean.setTimeout(content.getTimeout());
		ufBean.setMethod(FeedbackMethodType.GET_IMPLICIT_FB);

		//add new request to result hashmap
		impResults.put(requestID, null);

		//send pubsub event to all user agents
		try {
			LOG.debug("Sending user feedback request event via pubsub");
			pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}

		//wait until result is available
		while((Boolean)this.impResults.get(requestID) == null){
			try{
				synchronized(impResults){
					this.impResults.wait();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//set result and remove id from hashmap
		result = this.impResults.get(requestID);
		this.impResults.remove(requestID);

		return new AsyncResult<Boolean>(result);
	}

	@Override
	public void showNotification(String notificationTxt) {
		LOG.debug("Received request for notification");
		LOG.debug("Content: "+ notificationTxt);

		//generate unique ID for this pubsub event and feedback request
		String requestID = UUID.randomUUID().toString();

		//create user feedback bean to fire in pubsub event
		UserFeedbackBean ufBean = new UserFeedbackBean();
		ufBean.setRequestId(requestID);
		ufBean.setProposalText(notificationTxt);
		ufBean.setMethod(FeedbackMethodType.SHOW_NOTIFICATION);

		//add request ID and result type to results hashmap
		impResults.put(requestID, null);

		//send pubsub event to all user agents
		try {
			LOG.debug("Sending user feedback request event via pubsub");
			pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.REQUEST, null, ufBean);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}

		//wait until result is available
		while((Boolean)this.impResults.get(requestID) == null){
			try{
				synchronized(impResults){
					this.impResults.wait();
				}
			}catch(Exception e){
				e.printStackTrace();

			}
		}

		//remove id from hashmap
		this.impResults.remove(requestID);		
	}


	@Override
	public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
		LOG.debug("Received pubsub event with topic: "+eventTopic);

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


	/*
	 * Handle explicit feedback request and response events
	 */
	private void processExpFeedbackRequestEvent(String requestID, int type, String proposalText, List<String> optionsList){
		//create feedback form
		FeedbackForm fbForm = generateExpFeedbackForm(requestID, type, proposalText, optionsList);
		//add new request to queue
		requestMgr.addRequest(fbForm);
	}

	private void processExpResponseEvent(String responseID, List<String> result){
		//remove from request manager list if exists
		requestMgr.removeRequest(responseID);
		//set result value in hashmap
		synchronized(expResults){
			if(expResults.containsKey(responseID)){
				LOG.debug("this is the node where the exp feedback request originated....adding result to expResults hashmap");
				this.expResults.put(responseID, result);
				this.expResults.notifyAll();
			}else{
				LOG.debug("This isn't the node where the exp feedback request originated...don't need to add result to expResults hashmap");
			}
		}
	}

	/*
	 * Handle implicit feedback request and response events
	 */
	private void processImpFeedbackRequestEvent(String requestID, int type, String proposalText, int timeout){
		//create feedback form
		FeedbackForm fbForm = generateImpFeedbackForm(requestID, type, proposalText, timeout);
		//add new request to queue
		requestMgr.addRequest(fbForm);
	}

	private void processImpResponseEvent(String responseID, Boolean result){
		//remove from request manager list if exists
		requestMgr.removeRequest(responseID);
		//set result value in hashmap
		synchronized(impResults){
			if(impResults.containsKey(responseID)){
				LOG.debug("this is the node where the imp feedback request originated....adding result to impResults hashmap");
				this.impResults.put(responseID, result);
				this.impResults.notifyAll();
			}else{
				LOG.debug("This isn't the node where the imp feedback request originated...don't need to add result to impResults hashmap");
			}
		}
	}

	/*
	 * Handle notification request events
	 */
	private void processNotificationRequestEvent(String requestID, String proposalText){
		//create feedback form
		FeedbackForm fbForm = generateNotificationForm(requestID, proposalText);
		//add new request to queue
		requestMgr.addRequest(fbForm);
	}





	/*
	 * The following methods are called by the UserFeedbackController as part of the platform web-app
	 * 
	 * (non-Javadoc)
	 * @see org.societies.api.internal.useragent.feedback.IUserFeedback#getNextRequest()
	 */
	@Override
	public FeedbackForm getNextRequest() {
		return requestMgr.getNextRequest();
	}

	@Override
	public void submitExplicitResponse(String requestID, List<String> result) {
		//create user feedback response bean
		ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
		resultBean.setRequestId(requestID);
		resultBean.setFeedback(result);

		//fire response pubsub event to all user agents
		try {
			pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, null, resultBean);
		} catch (XMPPError e1) {
			e1.printStackTrace();
		} catch (CommunicationException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void submitImplicitResponse(String requestID, Boolean result) {
		//create user feedback response bean
		ImpFeedbackResultBean resultBean = new ImpFeedbackResultBean();
		resultBean.setRequestId(requestID);
		resultBean.setAccepted(result);

		//fire response pubsub event to all user agents
		try {
			pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, null, resultBean);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}


	/*
	 * Helper methods to generate feedback forms - explicit, implicit and notification
	 */
	private FeedbackForm generateExpFeedbackForm(String requestID, int type, String proposalText, List<String> optionsList){
		FeedbackForm newFbForm = new FeedbackForm();
		//add unique id
		newFbForm.setID(requestID);
		//add text to show to user
		newFbForm.setText(proposalText);
		//add data
		String[] optionsArray = new String[optionsList.size()];
		for(int i=0; i<optionsList.size(); i++){
			optionsArray[i] = optionsList.get(i);
		}
		newFbForm.setData(optionsArray);
		//add type
		if(type == ExpProposalType.RADIOLIST){
			newFbForm.setType(RADIO);
		}else if(type == ExpProposalType.CHECKBOXLIST){
			newFbForm.setType(CHECK);
		}else if(type == ExpProposalType.ACKNACK){
			newFbForm.setType(ACK);
		}else{
			LOG.error("Could not understand this type of explicit GUI: "+type);
		}
		return newFbForm;
	}

	private FeedbackForm generateImpFeedbackForm(String requestID, int type, String proposalText, int timeout){
		FeedbackForm newFbForm = new FeedbackForm();
		//add unique id
		newFbForm.setID(requestID);
		//add text to show user
		newFbForm.setText(proposalText);
		//add data
		String[] data = {new Integer(timeout).toString()};
		newFbForm.setData(data);
		//add type
		if(type == ImpProposalType.TIMED_ABORT){
			newFbForm.setType(ABORT);
		}else{
			LOG.error("Could not understand this type of implicit GUI: "+type);
		}
		return newFbForm;
	}

	private FeedbackForm generateNotificationForm(String requestID, String notificationTxt){
		FeedbackForm newFbForm = new FeedbackForm();
		//add unique id
		newFbForm.setID(requestID);
		//add text to show user
		newFbForm.setText(notificationTxt);
		//add data
		String[] data = {"5000"};
		newFbForm.setData(data);
		//add type
		newFbForm.setType(NOTIFICATION);
		return newFbForm;
	}

	/*@Override
	public Future<List<String>> getExplicitFB(int type, ExpProposalContent content) {
		List<String> result = null;

		//check current UID
		String uid = getCurrentUID();
		if(uid.equals(UNDEFINED)){//don't know what current UID is
			LOG.error("UID is not defined - sending request to all interactable devices in CSS");

		}else if(uid.equals(myDeviceID)){  //local device is current UID
			//show GUIs on local device
			LOG.debug("Returning explicit feedback");
			String proposalText = content.getProposalText();
			String[] options = content.getOptions();
			if(type == ExpProposalType.RADIOLIST){
				LOG.debug("Radio list GUI");
				RadioGUI gui = new RadioGUI();
				result = gui.displayGUI(proposalText, options);
			}else if(type == ExpProposalType.CHECKBOXLIST){
				LOG.debug("Check box list GUI");
				CheckBoxGUI gui = new CheckBoxGUI();
				result = gui.displayGUI(proposalText, options);
			}else{ //ACK-NACK
				LOG.debug("ACK/NACK GUI");
				result = AckNackGUI.displayGUI(proposalText, options);
			}

		}else{  //remote device is current UID
			//show GUIs on remote UID
			try {
				result = uaRemote.getExplicitFB(type, content).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		return new AsyncResult<List<String>>(result);
	}*/

	/*@Override
	public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
		Boolean result = null;

		//check for current UID
		String uid = getCurrentUID();
		if(uid.equals(UNDEFINED)){  //don't know what current UID is

		}else if(uid.equals(myDeviceID)){  //local device is current UID
			//show GUIs on local device
			LOG.debug("Returning implicit feedback");

			String proposalText = content.getProposalText();
			int timeout = content.getTimeout();
			if(type == ImpProposalType.TIMED_ABORT){
				LOG.debug("Timed Abort GUI");
				TimedGUI gui = new TimedGUI();
				result = gui.displayGUI(proposalText, timeout);
			}
		}else{  //remote device is current UID
			//show GUIs on remote UID
			try {
				result = uaRemote.getImplicitFB(type, content).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		return new AsyncResult<Boolean>(result);
	}	*/

	/*
	 *Called by UACommsServer to request explicit feedback for remote User Agent
	 * 
	 * (non-Javadoc)
	 * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getExplicitFBforRemote(int, org.societies.api.internal.useragent.model.ExpProposalContent)
	 */
	@Override
	public Future<List<String>> getExplicitFBforRemote(int type, ExpProposalContent content) {
		LOG.debug("Request for explicit feedback received from remote User Agent");
		List<String> result = null;

		//show GUIs on local device
		LOG.debug("Returning explicit feedback to UACommsServer");
		String proposalText = content.getProposalText();
		String[] options = content.getOptions();
		if(type == ExpProposalType.RADIOLIST){
			LOG.debug("Radio list GUI");
			RadioGUI gui = new RadioGUI();
			result = gui.displayGUI(proposalText, options);
		}else if(type == ExpProposalType.CHECKBOXLIST){
			LOG.debug("Check box list GUI");
			CheckBoxGUI gui = new CheckBoxGUI();
			result = gui.displayGUI(proposalText, options);
		}else{ //ACK-NACK
			LOG.debug("ACK/NACK GUI");
			result = AckNackGUI.displayGUI(proposalText, options);
		}

		return new AsyncResult<List<String>>(result);
	}

	/*
	 * Called by UACommsServer to request implicit feedback for remote User Agent
	 * 
	 * (non-Javadoc)
	 * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getImplicitFBforRemote(int, org.societies.api.internal.useragent.model.ImpProposalContent)
	 */
	@Override
	public Future<Boolean> getImplicitFBforRemote(int type, ImpProposalContent content) {
		LOG.debug("Request for implicit feedback received from remote User Agent");
		Boolean result = null;

		//show GUIs on local device
		LOG.debug("Returning implicit feedback to UACommsServer");
		String proposalText = content.getProposalText();
		int timeout = content.getTimeout();
		if(type == ImpProposalType.TIMED_ABORT){
			LOG.debug("Timed Abort GUI");
			TimedGUI gui = new TimedGUI();
			result = gui.displayGUI(proposalText, timeout);
		}

		return new AsyncResult<Boolean>(result);
	}




	/*private String getCurrentUID(){
		String uid = "";
		try {
			List<CtxIdentifier> attrIDs = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID).get();
			if(attrIDs.size()>0){  //found existing UID
				CtxAttribute uidAttr = (CtxAttribute)ctxBroker.retrieve(attrIDs.get(0)).get();
				uid = uidAttr.getStringValue();
			}else{  //no existing UID
				uid = UNDEFINED;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return uid;
	}*/

	public void setCtxBroker(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
	}

	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}

	public void setPubsub(PubsubClient pubsub){
		this.pubsub = pubsub;
	}

	public void setUaRemote(IUserAgentRemoteMgr uaRemote){
		this.uaRemote = uaRemote;
	}
}