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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.societies.useragent.api.remote.IUserAgentRemoteMgr;
import org.societies.useragent.feedback.guis.AckNackGUI;
import org.societies.useragent.feedback.guis.CheckBoxGUI;
import org.societies.useragent.feedback.guis.RadioGUI;
import org.societies.useragent.feedback.guis.TimedGUI;
import org.springframework.scheduling.annotation.AsyncResult;

public class UserFeedback implements IUserFeedback, IInternalUserFeedback{

	Logger LOG = LoggerFactory.getLogger(UserFeedback.class);
	ICtxBroker ctxBroker;
	ICommManager commsMgr;
	IUserAgentRemoteMgr uaRemote;
	RequestManager requestMgr;
	String myDeviceID;
	HashMap<String, List<String>> expResults;
	HashMap<String, Boolean> impResults;
	static String UNDEFINED = "undefined";

	//GUI types
	private static final String RADIO = "radio";
	private static final String CHECK = "check";
	private static final String ACK = "ack";
	private static final String ABORT = "abort";
	private static final String NOTIFICATION = "notification";

	public void initialiseUserFeedback(){
		LOG.debug("User Feedback initialised!!");

		requestMgr = new RequestManager();
		expResults = new HashMap<String, List<String>>();
		impResults = new HashMap<String, Boolean>();

		//get current device ID
		myDeviceID = commsMgr.getIdManager().getThisNetworkNode().getJid();
	}

	@Override
	public Future<List<String>> getExplicitFB(int type, ExpProposalContent content){
		List<String> result = null;
		//create feedback form
		FeedbackForm fbForm = generateExpFeedbackForm(type, content);
		//create new request object with unique ID
		//add new request to queue
		requestMgr.addRequest(fbForm);

		//add request ID and result type to results hashmap
		expResults.put(fbForm.getID(), null);

		//wait until result is available
		while((List<String>)this.expResults.get(fbForm.getID()) == null){
			try{
				synchronized(expResults){
					this.expResults.wait();
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		//set result and remove id from hashmap
		result = this.expResults.get(fbForm.getID());
		this.expResults.remove(fbForm.getID());

		return new AsyncResult<List<String>>(result);
	}

	@Override
	public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
		Boolean result = false;
		//create feedback form
		FeedbackForm fbForm = generateImpFeedbackForm(type, content);
		//add new request to queue
		requestMgr.addRequest(fbForm);

		//add request ID and result type to results hashmap
		impResults.put(fbForm.getID(), null);

		//wait until result is available
		while((Boolean)this.impResults.get(fbForm.getID()) == null){
			try{
				synchronized(impResults){
					this.impResults.wait();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//set result and remove id from hashmap
		result = this.impResults.get(fbForm.getID());
		this.impResults.remove(fbForm.getID());

		return new AsyncResult<Boolean>(result);
	}

	@Override
	public void showNotification(String notificationTxt) {
		//create feedback form
		FeedbackForm fbForm = generateNotificationForm(notificationTxt);
		//add new request to queue
		requestMgr.addRequest(fbForm);

		//add request ID and result type to results hashmap
		impResults.put(fbForm.getID(), null);

		//wait until result is available
		while((Boolean)this.impResults.get(fbForm.getID()) == null){
			try{
				synchronized(impResults){
					this.impResults.wait();
				}
			}catch(Exception e){
				e.printStackTrace();

			}
		}
		//remove id from hashmap
		this.impResults.remove(fbForm.getID());		
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
	public void submitExplicitResponse(String requestId, List<String> result) {
		//set result value in hashmap
		synchronized(expResults){
			this.expResults.put(requestId, result);
			this.expResults.notifyAll();
		}
		//remove request from queue
		if(requestMgr.removeRequest(requestId)){
			LOG.error("Could not find specified request in queue");
		}
	}

	@Override
	public void submitImplicitResponse(String requestId, Boolean result) {
		//set result value in hashmap
		synchronized(impResults){
			this.impResults.put(requestId, result);
			this.impResults.notifyAll();
		}
		//remove request from queue
		if(requestMgr.removeRequest(requestId)){
			LOG.error("Could not find specified request in queue");
		}
	}


	/*
	 * Helper methods
	 */
	private FeedbackForm generateExpFeedbackForm(int type, ExpProposalContent content){
		FeedbackForm newFbForm = new FeedbackForm();
		//add unique id
		newFbForm.setID(UUID.randomUUID().toString());
		//add text to show to user
		newFbForm.setText(content.getProposalText());
		//add data
		newFbForm.setData(content.getOptions());
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

	private FeedbackForm generateImpFeedbackForm(int type, ImpProposalContent content){
		FeedbackForm newFbForm = new FeedbackForm();
		//add unique id
		newFbForm.setID(UUID.randomUUID().toString());
		//add text to show user
		newFbForm.setText(content.getProposalText());
		//add data
		String[] data = {new Integer(content.getTimeout()).toString()};
		newFbForm.setData(data);
		//add type
		if(type == ImpProposalType.TIMED_ABORT){
			newFbForm.setType(ABORT);
		}else{
			LOG.error("Could not understand this type of implicit GUI: "+type);
		}
		return newFbForm;
	}

	private FeedbackForm generateNotificationForm(String notificationTxt){
		FeedbackForm newFbForm = new FeedbackForm();
		//add id
		newFbForm.setID(UUID.randomUUID().toString());
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




	private String getCurrentUID(){
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
	}

	public void setCtxBroker(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
	}

	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}

	public void setUaRemote(IUserAgentRemoteMgr uaRemote){
		this.uaRemote = uaRemote;
	}
}