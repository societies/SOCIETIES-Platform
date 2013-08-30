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
package org.societies.integration.test.userfeedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Rafik
 * @author Olivier Maridat (Trialog)
 */
public class UserFeedbackMocker implements Subscriber {
	private static Logger LOG = LoggerFactory.getLogger(UserFeedbackMocker.class);

	private IUserFeedback userFeedback;
	private ICommManager commManager;
	private PubsubClient pubsub;
	private boolean enabled;
	private IIdentity cloodNodeJid;
	/**
	 * Map of pre-selected result for user feedback requests
	 * <feedback type : result >
	 */
	private Map<UserFeedbackType, UserFeedbackMockResult> mockResults;

	public UserFeedbackMocker() {
		mockResults = new HashMap<UserFeedbackType, UserFeedbackMockResult>();
		enabled = false;
	}

	public void onCreate() {
		if (!isDepencyInjectionDone()) {
			LOG.error("[UserFeedbackMocker][Dependency Injection] Not ready. Missing dependencies.");
			return;
		}
		// -- Retrieve cloud node JID
		cloodNodeJid = commManager.getIdManager().getThisNetworkNode();

		// -- Register for events from created pubsub node
		LOG.debug("[UserFeedbackMocker] Registering for userfeedback pubsub node");
		try {
			pubsub.subscriberSubscribe(cloodNodeJid, "org/societies/useragent/feedback/event/REQUEST", this);
			pubsub.subscriberSubscribe(cloodNodeJid, EventTypes.UF_PRIVACY_NEGOTIATION, this);
			pubsub.subscriberSubscribe(cloodNodeJid, EventTypes.UF_PRIVACY_ACCESS_CONTROL, this);
		}
		catch (XMPPError e) {
			LOG.error("[UserFeedbackMocker] Can't subscribe to the userfeedback event due to XMPPError", e);
		}
		catch (CommunicationException e) {
			LOG.error("[UserFeedbackMocker] Can't subscribe to the userfeedback event due to communication exception", e);
		}
	}

	public void onDestroy() {
		// -- Unregister to events
		LOG.debug("[UserFeedbackMocker] Unregistering for userfeedback pubsub node");
		try {
			pubsub.subscriberUnsubscribe(cloodNodeJid, "org/societies/useragent/feedback/event/REQUEST", this);
			pubsub.subscriberUnsubscribe(cloodNodeJid, EventTypes.UF_PRIVACY_NEGOTIATION, this);
			pubsub.subscriberUnsubscribe(cloodNodeJid, EventTypes.UF_PRIVACY_ACCESS_CONTROL, this);
		}
		catch (XMPPError e) {
			LOG.error("[UserFeedbackMocker] Can't unsubscribe to the userfeedback event due to XMPPError", e);
		}
		catch (CommunicationException e) {
			LOG.error("[UserFeedbackMocker] Can't unsubscribe to the userfeedback event due to communication exception", e);
		}
	}


	@Override
	public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
		// -- UserFeedbackMocker disabled
		if (!isEnabled()) {
			return;
		}
		// -- Not a relevant event
		if(!eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST")
				&& !eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION)
				&& !eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_ACCESS_CONTROL)) {
			LOG.error("[UserFeedbackMocker] Hum, bad event receive: "+eventTopic);
			return;
		}

		// -- Retrieve data
		String requestId = "";
		String method = "";
		int type = -1;
		UserFeedbackBean ufBean = null;
		UserFeedbackPrivacyNegotiationEvent ufNegotiationBean = null;
		UserFeedbackAccessControlEvent ufAccessControlBean = null;
		if (eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST")) {
			ufBean = (UserFeedbackBean)item;
			requestId = ufBean.getRequestId();
			method = ufBean.getMethod().value();
			type = ufBean.getType();
		}
		else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION)) {
			ufNegotiationBean = (UserFeedbackPrivacyNegotiationEvent)item;
			requestId = ufNegotiationBean.getRequestId();
			method = ufNegotiationBean.getMethod().value();
			type = ufNegotiationBean.getType();
		}
		else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_ACCESS_CONTROL)) {
			ufAccessControlBean = (UserFeedbackAccessControlEvent)item;
			requestId = ufAccessControlBean.getRequestId();
			method = ufAccessControlBean.getMethod().value();
			type = ufAccessControlBean.getType();
		}
		UserFeedbackType feedbackType = UserFeedbackType.fromValue(method+":"+type);
		LOG.debug("[UserFeedbackMocker] Received pubsub event: "+eventTopic+" - "+feedbackType);
		if (eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST")) {
			for (String string : ufBean.getOptions()) {
				LOG.debug("[UserFeedbackMocker] option: " + string);
			}
		}

		// -- Find in configuration
		boolean userfeedbackReplied = false;
		if (mockResults.containsKey(feedbackType)) {
			LOG.debug("[UserFeedbackMocker] Configuration found for this explicit request: "+ feedbackType);
			UserFeedbackMockResult mockResult = mockResults.get(feedbackType);
			// - Send result
			if (mockResult.isResult()) {
				LOG.debug("[UserfeedbackMocker] Ok, this is a result");
				userfeedbackReplied = true;
				// Send as output the specified data list
				if (eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST")) {
					userFeedback.submitExplicitResponse(requestId, mockResult.getResult());
				}
				// Send as output the specified ResponsePolicy
				else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION)) {
					LOG.info("[UserfeedbackMocker] submitExplicitResponse");
					userFeedback.submitPrivacyNegotiationResponse(requestId, ufNegotiationBean.getNegotiationDetails(), mockResult.getPrivacyAgreementResult());
				}
				else {
					userfeedbackReplied = false;
				}
			}
			// - Send result using option indexes
			else {
				LOG.debug("[UserfeedbackMocker] Ok, this is an index result");
				userfeedbackReplied = true;
				// Send as output the input at the specified index
				if (eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST")) {
					for(Integer resultIndex : mockResult.getResultIndexes()) {
						if (ufBean.getOptions().contains(resultIndex)) {
							mockResult.addResult(ufBean.getOptions().get(resultIndex));
						}
					}
					userFeedback.submitExplicitResponse(requestId, mockResult.getResult());
				}
				// Send as output a ResponsePolicy with request items equals to input request items at the specified index
				else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION)) {
					ResponsePolicy responsePolicy = new ResponsePolicy();
					responsePolicy.setNegotiationStatus(ufNegotiationBean.getResponsePolicy().getNegotiationStatus());
					responsePolicy.setRequestor(ufNegotiationBean.getResponsePolicy().getRequestor());
					List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
					for(Integer resultIndex : mockResult.getResultIndexes()) {
						if (ufNegotiationBean.getResponsePolicy().getResponseItems().size() >= resultIndex) {
							responseItems.add(ufNegotiationBean.getResponsePolicy().getResponseItems().get(resultIndex));
						}
					}
					responsePolicy.setResponseItems(responseItems);
					LOG.info("[UserfeedbackMocker] submitExplicitResponse");
					userFeedback.submitPrivacyNegotiationResponse(requestId, ufNegotiationBean.getNegotiationDetails(), responsePolicy);
				}
				else {
					userfeedbackReplied = false;
				}
			}

			// - Manage usage of this result value
			mockResult.incrNbOfusage(-1);
			if (!mockResult.isUsable()) {
				mockResults.remove(feedbackType);
			}
		}

		// -- Default behaviour
		if (!userfeedbackReplied) {
			LOG.debug("[UserFeedbackMocker] Use default configuration for this request: "+ feedbackType);
			if (FeedbackMethodType.GET_EXPLICIT_FB.value().equals(method)) {
				// Send as output the first input string
				if (eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST")) {
					List<String> result = new ArrayList<String>();
					result.add(ufBean.getOptions().size() > 0 ? ufBean.getOptions().get(0) : "Ouch!");
					userFeedback.submitExplicitResponse(ufBean.getRequestId(), result);
				}
				// Send as output the input ResponsePolicy
				else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION)) {
					LOG.debug("[UserfeedbackMocker] submitExplicitResponse");
					userFeedback.submitPrivacyNegotiationResponse(ufNegotiationBean.getRequestId(), ufNegotiationBean.getNegotiationDetails(), ufNegotiationBean.getResponsePolicy());
				}
			}
			else if (FeedbackMethodType.GET_IMPLICIT_FB.value().equals(method)) {
				userFeedback.submitImplicitResponse(ufBean.getRequestId(), true);
			}
			else if (FeedbackMethodType.SHOW_NOTIFICATION.value().equals(method)) {
				userFeedback.submitImplicitResponse(ufBean.getRequestId(), true);
			}
			else {
				LOG.error("But... but this is not a valid user feedback request!");
			}
		}
	}

	/**
	 * Add a pre-selected reply to a user feedback request
	 * @param feedbackType
	 * @param reply
	 */
	public void addReply(UserFeedbackType feedbackType, UserFeedbackMockResult reply) {
		mockResults.put(feedbackType, reply);
	}
	public void removeReply(UserFeedbackType feedbackType) {
		if (mockResults.containsKey(feedbackType)) {
			mockResults.remove(feedbackType);
		}
	}
	public void removeAllReplies() {
		mockResults.clear();
	}

	/* -- Dependency injection --- */
	@Autowired
	public void setCommManager(ICommManager commManager){
		this.commManager = commManager;
		LOG.info("[DependencyInjection] ICommManager injected");
	}
	@Autowired
	public void setPubsub(PubsubClient pubsub){
		this.pubsub = pubsub;
		LOG.info("[DependencyInjection] PubsubClient injected");
	}
	@Autowired
	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
		LOG.info("[DependencyInjection] IUserFeedback injected");
	}
	@Value("${userfeedback.mocked:0}")
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		LOG.info("[DependencyInjection] Userfeedback mocker is "+(isEnabled() ? "en" : "dis")+"abled");
	}
	public boolean isEnabled() {
		return enabled;
	}

	public boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	public boolean isDepencyInjectionDone(int level) {
		if (null == commManager) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == commManager.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		if (null == pubsub) {
			LOG.info("[Dependency Injection] Missing PubsubClient");
			return false;
		}
		if (null == userFeedback) {
			LOG.info("[Dependency Injection] Missing IUserFeedback");
			return false;
		}
		return true;
	}
}
