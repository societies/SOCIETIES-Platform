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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.useragent.feedback.*;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

public class UserFeedback implements IUserFeedback, IInternalUserFeedback, Subscriber {

    private static final Logger log = LoggerFactory.getLogger(UserFeedback.class);

    //pubsub event schemas
    private static final List<String> EVENT_SCHEMA_CLASSES =
            Collections.unmodifiableList(Arrays.asList(
                    "org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent",
                    "org.societies.api.internal.schema.useragent.feedback.UserFeedbackHistoryRequest",
                    "org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent",
                    "org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean",
                    "org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean",
                    "org.societies.api.schema.useragent.feedback.UserFeedbackBean"
            ));


    @Autowired
    private ICommManager commsMgr;

    @Autowired
    private PubsubClient pubsub;

    //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
    private final Map<String, UserFeedbackResult<List<String>>> expResults = new HashMap<String, UserFeedbackResult<List<String>>>();
    private final Map<String, IUserFeedbackResponseEventListener<List<String>>> expCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<List<String>>>();
    private final Map<String, UserFeedbackResult<Boolean>> impResults = new HashMap<String, UserFeedbackResult<Boolean>>();
    private final Map<String, IUserFeedbackResponseEventListener<Boolean>> impCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<Boolean>>();
    private final Map<String, UserFeedbackResult<ResponsePolicy>> negotiationResults = new HashMap<String, UserFeedbackResult<ResponsePolicy>>();
    private final Map<String, IUserFeedbackResponseEventListener<ResponsePolicy>> negotiationCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<ResponsePolicy>>();
    private final Map<String, UserFeedbackResult<List<AccessControlResponseItem>>> accessCtrlResults = new HashMap<String, UserFeedbackResult<List<AccessControlResponseItem>>>();
    private final Map<String, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>>> accessCtrlCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>>>();

    private final Map<String, UserFeedbackBean> incompleteUserFeedbackBeans = new HashMap<String, UserFeedbackBean>();
    private final Map<String, UserFeedbackPrivacyNegotiationEvent> incompletePrivacyNegotiationEvents = new HashMap<String, UserFeedbackPrivacyNegotiationEvent>();
    private final Map<String, UserFeedbackAccessControlEvent> incompleteAccessControlEvents = new HashMap<String, UserFeedbackAccessControlEvent>();

    private final RequestManager requestMgr = new RequestManager();
    private IIdentity myCloudID;


    public void initialiseUserFeedback() {
        if(log.isDebugEnabled()) log.debug("User Feedback initialising");

        expResults.clear();
        impResults.clear();
        negotiationResults.clear();
        accessCtrlResults.clear();
        expCallbacks.clear();
        impCallbacks.clear();
        negotiationCallbacks.clear();
        accessCtrlCallbacks.clear();
        incompleteUserFeedbackBeans.clear();
        incompletePrivacyNegotiationEvents.clear();
        incompleteAccessControlEvents.clear();

        //get cloud ID
        myCloudID = commsMgr.getIdManager().getThisNetworkNode();
        if(log.isDebugEnabled()) log.debug("Got my cloud ID: " + myCloudID);

        //create pubsub node
        try {
            if(log.isDebugEnabled()) log.debug("Creating user feedback pubsub node");
            pubsub.addSimpleClasses(EVENT_SCHEMA_CLASSES);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.REQUEST);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.COMPLETE);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE);
            if(log.isDebugEnabled()) log.debug("Pubsub node created!");
        } catch (Exception e) {
            log.error("Error creating user feedback pubsub nodes", e);
        }

        //register for events from created pubsub node
        try {
            if(log.isDebugEnabled()) log.debug("Registering for user feedback pubsub node");
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.REQUEST, this);
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP, this);
            if(log.isDebugEnabled()) log.debug("Pubsub registration complete!");
        } catch (Exception e) {
            log.error("Error registering for user feedback pubsub nodes", e);
        }

    }

    @Override
    public Future<List<String>> getExplicitFB(String requestId, int type, ExpProposalContent content) {
        Future<List<String>> result = getExplicitFBAsync(requestId, type, content, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
            return result;
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<List<String>> getExplicitFB(int type, ExpProposalContent content) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getExplicitFB(requestId, type, content);
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content) {
        return getExplicitFBAsync(type, content, null);
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getExplicitFBAsync(requestId, type, content, callback);
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(String requestId, int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback) {
        if (log.isDebugEnabled()) {
            log.debug("Received request for explicit feedback\n" +
                    "    Content: " + content.getProposalText());
        }

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
        ufBean.setRequestDate(new Date());
        ufBean.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        ufBean.setRequestId(requestId);
        ufBean.setType(type);
        ufBean.setProposalText(content.getProposalText());
        List<String> optionsList = new ArrayList<String>();
        Collections.addAll(optionsList, content.getOptions());
        ufBean.setOptions(optionsList);
        ufBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);

        //add new request to result hashmap
        UserFeedbackResult<List<String>> result = new UserFeedbackResult<List<String>>(requestId);

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompleteUserFeedbackBeans) {
            incompleteUserFeedbackBeans.put(requestId, ufBean);
        }
        synchronized (expResults) {
            expResults.put(requestId, result);
        }
        if (callback != null) {
            synchronized (expCallbacks) {
                expCallbacks.put(requestId, callback);
            }
        }

        //send pubsub event to all user agents
        try {
            if (log.isDebugEnabled())
                log.debug("Sending user feedback request event via pubsub with ID " + requestId);

            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            log.error("Error transmitting user feedback request bean via pubsub", ex);
        }

        return result;
    }


    @Override
    public Future<Boolean> getImplicitFB(String requestId, int type, ImpProposalContent content) {
        Future<Boolean> result = getImplicitFBAsync(requestId, type, content, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
            return result;
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getImplicitFB(requestId, type, content);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content) {
        return getImplicitFBAsync(type, content, null);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getImplicitFBAsync(requestId, type, content, callback);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(String requestId, int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback) {
        if (log.isDebugEnabled()) {
            log.debug("Received request for implicit feedback\n" +
                    "    Content: " + content.getProposalText());
        }

        if (content.getTimeout() < 1000) {
            log.warn("Implicit (Timed Abort) request timeout is < 1000ms - timeouts should be specified in milliseconds");
        }

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
        ufBean.setRequestDate(new Date());
        ufBean.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        ufBean.setRequestId(requestId);
        ufBean.setType(type);
        ufBean.setProposalText(content.getProposalText());
        ufBean.setTimeout(content.getTimeout());
        ufBean.setMethod(FeedbackMethodType.GET_IMPLICIT_FB);

        //add new request to result hashmap
        UserFeedbackResult<Boolean> result = new UserFeedbackResult<Boolean>(requestId);

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompleteUserFeedbackBeans) {
            incompleteUserFeedbackBeans.put(requestId, ufBean);
        }
        synchronized (impResults) {
            impResults.put(requestId, result);
        }
        if (callback != null) {
            synchronized (impCallbacks) {
                impCallbacks.put(requestId, callback);
            }
        }

        //send pubsub event to all user agents
        try {
            if (log.isDebugEnabled())
                log.debug("Sending user feedback request event via pubsub with ID " + requestId);

            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            log.error("Error transmitting user feedback request bean via pubsub", ex);
        }

        return result;
    }


    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(String requestId, ResponsePolicy policy, NegotiationDetailsBean details) {
        Future<ResponsePolicy> result = getPrivacyNegotiationFBAsync(requestId, policy, details, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
            return result;
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy, NegotiationDetailsBean details) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getPrivacyNegotiationFB(requestId, policy, details);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details) {
        return getPrivacyNegotiationFBAsync(policy, details, null);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getPrivacyNegotiationFBAsync(requestId, policy, details, callback);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(String requestId, ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback) {

        if (log.isDebugEnabled()) {
            log.debug("processing negotiationFeedback request");
            if (policy == null) {
                if(log.isDebugEnabled()) log.debug("Policy parameter is null");
            } else {
                if(log.isDebugEnabled()) log.debug("Policy contains: " + policy.getResponseItems().size() + " responseItems");
            }
        }

        UserFeedbackPrivacyNegotiationEvent event = new UserFeedbackPrivacyNegotiationEvent();
        event.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        event.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        event.setType(ExpProposalType.PRIVACY_NEGOTIATION);
        event.setRequestId(requestId);
        event.setNegotiationDetails(details);
        event.setResponsePolicy(policy);
        event.setRequestDate(new Date());

        //add new request to result hashmap
        UserFeedbackResult<ResponsePolicy> result = new UserFeedbackResult<ResponsePolicy>(requestId);

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompletePrivacyNegotiationEvents) {
            incompletePrivacyNegotiationEvents.put(requestId, event);
        }
        synchronized (negotiationResults) {
            negotiationResults.put(requestId, result);
        }
        if (callback != null) {
            synchronized (negotiationCallbacks) {
                negotiationCallbacks.put(requestId, callback);
            }
        }

        //send pubsub event to all user agents
        try {
            if (log.isDebugEnabled())
                log.debug("Sending PPN request event via pubsub");

            pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_NEGOTIATION,
                    requestId,
                    event);
        } catch (Exception ex) {
            log.error("Error transmitting PPN request bean via pubsub", ex);
        }

        return result;
    }


    @Override
    public Future<List<AccessControlResponseItem>> getAccessControlFB(String requestId, Requestor requestor, List<AccessControlResponseItem> items) {
        Future<List<AccessControlResponseItem>> result = getAccessControlFBAsync(requestId, requestor, items, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
        	List<AccessControlResponseItem> list = result.get();
        	
        	for (AccessControlResponseItem item: list){
            	if(log.isDebugEnabled()) log.debug("uf: Returning: "+item.getRequestItem().getResource().getDataType()+" decision: "+item.getDecision()+" remember set: "+item.isRemember()+" and obfuscationSelected: "+item.isObfuscationInput());

        	}
            return new AsyncResult<List<AccessControlResponseItem>>(list);
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<List<AccessControlResponseItem>> getAccessControlFB(Requestor requestor, List<AccessControlResponseItem> items) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getAccessControlFB(requestId, requestor, items);
    }

    @Override
    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(Requestor requestor, List<AccessControlResponseItem> items) {
        return getAccessControlFBAsync(requestor, items, null);
    }

    @Override
    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(Requestor requestor, List<AccessControlResponseItem> items, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getAccessControlFBAsync(requestId, requestor, items, callback);
    }

    @Override
    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(String requestId, Requestor requestor, List<AccessControlResponseItem> items, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>> callback) {
        UserFeedbackAccessControlEvent event = new UserFeedbackAccessControlEvent();
        event.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        event.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        event.setType(ExpProposalType.PRIVACY_ACCESS_CONTROL);
        event.setRequestId(requestId);
        event.setRequestor(RequestorUtils.toRequestorBean(requestor));
        event.setResponseItems(items);
        event.setRequestDate(new Date());

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompleteAccessControlEvents) {
            incompleteAccessControlEvents.put(requestId, event);
        }
        UserFeedbackResult<List<AccessControlResponseItem>> result = new UserFeedbackResult<List<AccessControlResponseItem>>(requestId);
        synchronized (accessCtrlResults) {
            accessCtrlResults.put(requestId, result);
        }
        if (callback != null) {
            synchronized (accessCtrlCallbacks) {
                accessCtrlCallbacks.put(requestId, callback);
            }
        }

        try {
            if (log.isDebugEnabled())
                log.debug("Sending access control request event via pubsub");

            this.pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_ACCESS_CONTROL,
                    requestId,
                    event);
        } catch (Exception ex) {
            log.error("Error transmitting access control request bean via pubsub", ex);
        }

        return result;
    }


    @Override
    public void showNotification(String notificationTxt) {
        if (log.isDebugEnabled()) {
            log.debug("Received request for notification\n" +
                    "    Content: " + notificationTxt);
        }

        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
        ufBean.setRequestDate(new Date());
        ufBean.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        ufBean.setRequestId(requestId);
        ufBean.setProposalText(notificationTxt);
        ufBean.setMethod(FeedbackMethodType.SHOW_NOTIFICATION);

        //add new request to result hashmap
        UserFeedbackResult<List<String>> result = new UserFeedbackResult<List<String>>(requestId);

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompleteUserFeedbackBeans) {
            incompleteUserFeedbackBeans.put(requestId, ufBean);
        }
        synchronized (expResults) {
            expResults.put(requestId, result);
        }

        //send pubsub event to all user agents
        try {
            if (log.isDebugEnabled())
                log.debug("Sending user feedback request event via pubsub with ID " + requestId);

            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            log.error("Error transmitting user feedback request bean via pubsub", ex);
        }

    }


    @Override
    public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
        if (eventTopic == null) {
            log.warn(String.format("Received pubsub event with NULL EVENT TOPIC - payload '%s', ID '%s'",
                    item != null ? item.getClass().getSimpleName() : "null",
                    itemID
            ));
            return;
        }
        if (item == null) {
            log.warn(String.format("Received pubsub event with NULL PAYLOAD - topic '%s', ID '%s'",
                    eventTopic,
                    itemID
            ));
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s'",
                    eventTopic,
                    itemID,
                    item.getClass().getSimpleName()
            ));
        }

        if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.REQUEST)) {
            // TODO: It would be nice to refactor all GUI code into a separate class (UserFeedbackGuiFactory?)

            //read from request bean
            UserFeedbackBean ufBean = (UserFeedbackBean) item;
            switch (ufBean.getMethod()) {
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
        } else if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.EXPLICIT_RESPONSE)) {
            //read from explicit response bean
            ExpFeedbackResultBean expFeedbackBean = (ExpFeedbackResultBean) item;
            this.processExpResponseEvent(expFeedbackBean);

        } else if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.IMPLICIT_RESPONSE)) {
            //read from implicit response bean
            ImpFeedbackResultBean impFeedbackBean = (ImpFeedbackResultBean) item;
            this.processImpResponseEvent(impFeedbackBean);

        } else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE)) {
            UserFeedbackPrivacyNegotiationEvent event = (UserFeedbackPrivacyNegotiationEvent) item;
            this.processPrivacyPolicyNegotiationResponseEvent(event);

        } else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE)) {
            UserFeedbackAccessControlEvent event = (UserFeedbackAccessControlEvent) item;
            this.processAccessControlResponseEvent(event);

        } else {
            log.warn(String.format("Unhandled pubsub event '%s' with ID '%s'", eventTopic, itemID));
        }
    }


    /*
     * Handle explicit feedback request and response events
     */
    private void processExpFeedbackRequestEvent(String requestId, int type, String proposalText, List<String> optionsList) {
        //create feedback form
        FeedbackForm fbForm = UserFeedbackGuiFactory.generateExpFeedbackForm(requestId, type, proposalText, optionsList);
        //add new request to queue
        requestMgr.addRequest(fbForm);
    }

    private void processExpResponseEvent(ExpFeedbackResultBean expFeedbackBean) {

        String responseID = expFeedbackBean.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompleteUserFeedbackBeans) {
            // remove from incomplete list
            if (incompleteUserFeedbackBeans.containsKey(responseID))
                incompleteUserFeedbackBeans.remove(responseID);

            //set result value in hashmap
            synchronized (expResults) {
                if (!expResults.containsKey(responseID)) {
                    if (log.isDebugEnabled())
                        log.debug(String.format("This isn't the node where the exp feedback request ID [%s] originated",
                                responseID));

                    if (log.isDebugEnabled()) {
                        StringBuilder bld = new StringBuilder();
                        bld.append("Exp feedback requests outstanding:-\n");
                        for (String s : expResults.keySet()) {
                            bld.append(" - ");
                            bld.append(s);
                            bld.append('\n');
                        }
                        log.debug(bld.toString());
                    }

                    return;
                }

                if (log.isDebugEnabled())
                    log.debug("This is the node where the exp feedback request originated");

                // inform clients that UF is complete
                try {
                    pubsub.publisherPublish(myCloudID,
                            UserFeedbackEventTopics.COMPLETE,
                            responseID,
                            expFeedbackBean);
                } catch (Exception ex) {
                    log.error("Error transmitting user feedback complete via pubsub", ex);
                }

                final UserFeedbackResult<List<String>> userFeedbackResult = expResults.get(responseID);
                synchronized (userFeedbackResult) {
                    userFeedbackResult.complete(expFeedbackBean.getFeedback());
                    userFeedbackResult.notifyAll();
                }

                if (expCallbacks.containsKey(responseID)) {
                    IUserFeedbackResponseEventListener<List<String>> callback = expCallbacks.remove(responseID);
                    callback.responseReceived(expFeedbackBean.getFeedback());
                }

                this.expResults.notifyAll();
            }
        }
    }

    private void processImpFeedbackRequestEvent(String requestId, int type, String proposalText, int timeout) {
        //create feedback form
        FeedbackForm fbForm = UserFeedbackGuiFactory.generateImpFeedbackForm(requestId, type, proposalText, timeout);
        //add new request to queue
        requestMgr.addRequest(fbForm);
    }

    private void processImpResponseEvent(ImpFeedbackResultBean impFeedbackBean) {

        String responseID = impFeedbackBean.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompleteUserFeedbackBeans) {
            // remove from incomplete list
            if (incompleteUserFeedbackBeans.containsKey(responseID))
                incompleteUserFeedbackBeans.remove(responseID);

            //set result value in hashmap
            synchronized (impResults) {
                if (!impResults.containsKey(responseID)) {
                    if (log.isDebugEnabled())
                        log.debug(String.format("This isn't the node where the imp feedback request ID [%s] originated",
                                responseID));

                    if (log.isDebugEnabled()) {
                        StringBuilder bld = new StringBuilder();
                        bld.append("Imp feedback requests outstanding:-\n");
                        for (String s : impResults.keySet()) {
                            bld.append(" - ");
                            bld.append(s);
                            bld.append('\n');
                        }
                        log.debug(bld.toString());
                    }

                    return;
                }

                if (log.isDebugEnabled())
                    log.debug("This is the node where the imp feedback request originated");

                // inform clients that UF is complete
                try {
                    pubsub.publisherPublish(myCloudID,
                            UserFeedbackEventTopics.COMPLETE,
                            responseID,
                            impFeedbackBean);
                } catch (Exception ex) {
                    log.error("Error transmitting user feedback complete via pubsub", ex);
                }

                final UserFeedbackResult<Boolean> userFeedbackResult = impResults.get(responseID);
                synchronized (userFeedbackResult) {
                    userFeedbackResult.complete(impFeedbackBean.isAccepted());
                    userFeedbackResult.notifyAll();
                }

                if (impCallbacks.containsKey(responseID)) {
                    IUserFeedbackResponseEventListener<Boolean> callback = impCallbacks.remove(responseID);
                    callback.responseReceived(impFeedbackBean.isAccepted());
                }

                this.impResults.notifyAll();
            }
        }
    }

    /*
     * Handle notification request events
     */
    private void processNotificationRequestEvent(String requestId, String proposalText) {
        //create feedback form
        FeedbackForm fbForm = UserFeedbackGuiFactory.generateNotificationForm(requestId, proposalText);
        //add new request to queue
        requestMgr.addRequest(fbForm);
    }

    private void processPrivacyPolicyNegotiationResponseEvent(UserFeedbackPrivacyNegotiationEvent result) {

        String responseID = result.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompletePrivacyNegotiationEvents) {
            // remove from incomplete list
            if (incompletePrivacyNegotiationEvents.containsKey(responseID))
                incompletePrivacyNegotiationEvents.remove(responseID);

            //set result value in hashmap
            synchronized (negotiationResults) {
                if (!negotiationResults.containsKey(responseID)) {
                    if (log.isDebugEnabled())
                        log.debug(String.format("This isn't the node where the PPN request ID [%s] originated",
                                responseID));

                    if (log.isDebugEnabled()) {
                        StringBuilder bld = new StringBuilder();
                        bld.append("PPN requests outstanding:-\n");
                        for (String s : negotiationResults.keySet()) {
                            bld.append(" - ");
                            bld.append(s);
                            bld.append('\n');
                        }
                        log.debug(bld.toString());
                    }

                    return;
                }

                if (log.isDebugEnabled())
                    log.debug("This is the node where the PPN request originated");

                // inform clients that negotiation is complete
                try {
                    pubsub.publisherPublish(myCloudID,
                            EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP,
                            responseID,
                            result);
                } catch (Exception ex) {
                    log.error("Error transmitting PPN complete via pubsub", ex);
                }

                final UserFeedbackResult<ResponsePolicy> userFeedbackResult = negotiationResults.get(responseID);
                synchronized (userFeedbackResult) {
                    userFeedbackResult.complete(result.getResponsePolicy());
                    userFeedbackResult.notifyAll();
                }

                if (negotiationCallbacks.containsKey(responseID)) {
                    IUserFeedbackResponseEventListener<ResponsePolicy> callback = negotiationCallbacks.remove(responseID);
                    callback.responseReceived(result.getResponsePolicy());
                }


                this.negotiationResults.notifyAll();
            }

        }
    }


    private void processAccessControlResponseEvent(UserFeedbackAccessControlEvent result) {

        String responseID = result.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //NB: To avoid deadlocks, ALWAYS synchronise on the incomplete beans map first, then results, then callbacks
        synchronized (incompleteAccessControlEvents) {
            // remove from incomplete list
            if (incompleteAccessControlEvents.containsKey(responseID))
                incompleteAccessControlEvents.remove(responseID);

            //set result value in hashmap
            synchronized (accessCtrlResults) {
                if (!accessCtrlResults.containsKey(responseID)) {
                    if (log.isDebugEnabled())
                        log.debug(String.format("This isn't the node where the AC request ID [%s] originated",
                                responseID));

                    if (log.isDebugEnabled()) {
                        StringBuilder bld = new StringBuilder();
                        bld.append("AC requests outstanding:-\n");
                        for (String s : accessCtrlResults.keySet()) {
                            bld.append(" - ");
                            bld.append(s);
                            bld.append('\n');
                        }
                        log.debug(bld.toString());
                    }

                    return;
                }

                if (log.isDebugEnabled())
                    log.debug("This is the node where the AC feedback request originated");

                // inform clients that negotiation is complete
                try {
                    pubsub.publisherPublish(myCloudID,
                            EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP,
                            responseID,
                            result);
                } catch (Exception ex) {
                    log.error("Error transmitting access control complete via pubsub", ex);
                }

                final UserFeedbackResult<List<AccessControlResponseItem>> userFeedbackResult = accessCtrlResults.get(responseID);
                synchronized (userFeedbackResult) {
                    userFeedbackResult.complete(result.getResponseItems());
                    userFeedbackResult.notifyAll();
                }

                if (accessCtrlCallbacks.containsKey(responseID)) {
                    IUserFeedbackResponseEventListener<List<AccessControlResponseItem>> callback = accessCtrlCallbacks.remove(responseID);
                    callback.responseReceived(result.getResponseItems());
                }

                this.accessCtrlResults.notifyAll();
            }

        }
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
        //create user feedback response bean
        ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
        resultBean.setRequestId(requestId);
        resultBean.setFeedback(result);

        //fire response pubsub event to all user agents
        try {
            pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
        } catch (XMPPError e) {
            log.error("Error submitting explicit response", e);
        } catch (CommunicationException e) {
            log.error("Error submitting explicit response", e);
        }
    }

    @Override
    public void submitImplicitResponse(String requestId, Boolean result) {
        //create user feedback response bean
        ImpFeedbackResultBean resultBean = new ImpFeedbackResultBean();
        resultBean.setRequestId(requestId);
        resultBean.setAccepted(result);

        //fire response pubsub event to all user agents
        try {
            pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);
        } catch (XMPPError e) {
            log.error("Error submitting implicit response", e);
        } catch (CommunicationException e) {
            log.error("Error submitting implicit response", e);
        }
    }

    @Override
    public void submitPrivacyNegotiationResponse(String requestId, NegotiationDetailsBean negotiationDetails, ResponsePolicy result) {
        //create user feedback response bean
        UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
        resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        resultBean.setType(ExpProposalType.PRIVACY_NEGOTIATION);
        resultBean.setRequestId(requestId);
        resultBean.setNegotiationDetails(negotiationDetails);
        resultBean.setResponsePolicy(result);
        resultBean.setRequestDate(new Date());
        log.debug("I HAVE SET THE DATE!!!"+resultBean.getRequestDate().toString());
		
      //  resultBean.setRequestDate(value);

        //fire response pubsub event to all user agents
        try {
            log.info("####### Publish " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + ": " + ResponseItemUtils.toXmlString(result.getResponseItems()));
            pubsub.publisherPublish(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        } catch (XMPPError e) {
            log.error("Error submitting negotiation response", e);
        } catch (CommunicationException e) {
            log.error("Error submitting negotiation response", e);
        }
    }

    @Override
    public void submitAccessControlResponse(String requestId, List<AccessControlResponseItem> responseItems, RequestorBean requestorBean) {
        //create user feedback response bean
        UserFeedbackAccessControlEvent resultBean = new UserFeedbackAccessControlEvent();
        resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        resultBean.setType(ExpProposalType.PRIVACY_ACCESS_CONTROL);
        resultBean.setRequestId(requestId);
        resultBean.setResponseItems(responseItems);
        resultBean.setRequestor(requestorBean);
        resultBean.setRequestDate(new Date());

        //fire response pubsub event to all user agents
        try {
//            log.info("####### Publish " + EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE + ": " + ResponseItemUtils.toXmlString(responseItems));
            pubsub.publisherPublish(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);
        } catch (XMPPError e) {
            log.error("Error submitting negotiation response", e);
        } catch (CommunicationException e) {
            log.error("Error submitting negotiation response", e);
        }
    }

    /*
     *Called by UACommsServer to request explicit feedback for remote User Agent
     *
     * (non-Javadoc)
     * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getExplicitFBforRemote(int, org.societies.api.internal.useragent.model.ExpProposalContent)
     */
    @Override
    public Future<List<String>> getExplicitFBforRemote(int type, ExpProposalContent content) {
        // TODO: Added this delegate so I didn't have to change classes consuming UF. Would be nice to refactor all GUI code into a separate class
        return UserFeedbackGuiFactory.getExplicitFBforRemote(type, content);
    }

    /*
     * Called by UACommsServer to request implicit feedback for remote User Agent
     *
     * (non-Javadoc)
     * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getImplicitFBforRemote(int, org.societies.api.internal.useragent.model.ImpProposalContent)
     */
    @Override
    public Future<Boolean> getImplicitFBforRemote(int type, ImpProposalContent content) {
        // TODO: Added this delegate so I didn't have to change classes consuming UF. Would be nice to refactor all GUI code into a separate class
        return UserFeedbackGuiFactory.getImplicitFBforRemote(type, content);
    }

    @Override
    public List<UserFeedbackBean> listIncompleteFeedbackBeans() {
        List<UserFeedbackBean> list = new ArrayList<UserFeedbackBean>(incompleteUserFeedbackBeans.values());
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<UserFeedbackPrivacyNegotiationEvent> listIncompletePrivacyRequests() {
        List<UserFeedbackPrivacyNegotiationEvent> list = new ArrayList<UserFeedbackPrivacyNegotiationEvent>(incompletePrivacyNegotiationEvents.values());
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<UserFeedbackAccessControlEvent> listIncompleteAccessRequests() {
        List<UserFeedbackAccessControlEvent> list = new ArrayList<UserFeedbackAccessControlEvent>(incompleteAccessControlEvents.values());
        return Collections.unmodifiableList(list);
    }

    public void setCommsMgr(ICommManager commsMgr) {
        this.commsMgr = commsMgr;
    }

    public void setPubsub(PubsubClient pubsub) {
        this.pubsub = pubsub;
    }

    /**
     * This is a non-api method which is used by integration tests to clear the internal state of the UF module
     */
    @Override
    public void clear() {
        synchronized (this) {
            // notify all nodes that UF has been cleared

            List<UserFeedbackBean> userFeedbackBeans = new ArrayList<UserFeedbackBean>(incompleteUserFeedbackBeans.values());
            List<UserFeedbackPrivacyNegotiationEvent> privacyNegotiationEvents = new ArrayList<UserFeedbackPrivacyNegotiationEvent>(incompletePrivacyNegotiationEvents.values());
            List<UserFeedbackAccessControlEvent> accessControlEvents = new ArrayList<UserFeedbackAccessControlEvent>(incompleteAccessControlEvents.values());

            log.warn(String.format("UserFeedback clear requested: Clearing %s UF events, %s PPN events, %s AC events",
                    userFeedbackBeans.size(),
                    privacyNegotiationEvents.size(),
                    accessControlEvents.size()));

            expResults.clear();
            impResults.clear();
            negotiationResults.clear();
            accessCtrlResults.clear();
            expCallbacks.clear();
            impCallbacks.clear();
            negotiationCallbacks.clear();
            accessCtrlCallbacks.clear();
            incompleteUserFeedbackBeans.clear();
            incompletePrivacyNegotiationEvents.clear();
            incompleteAccessControlEvents.clear();

            // notify other nodes that UF events are cleared
            for (UserFeedbackBean ufBean : userFeedbackBeans) {
                if (ufBean.getMethod() == FeedbackMethodType.GET_EXPLICIT_FB) {

                    ExpFeedbackResultBean expFeedbackBean = new ExpFeedbackResultBean();
                    expFeedbackBean.setRequestId(ufBean.getRequestId());
                    expFeedbackBean.setFeedback(new ArrayList<String>());

                    try {
                        pubsub.publisherPublish(myCloudID,
                                UserFeedbackEventTopics.COMPLETE,
                                ufBean.getRequestId(),
                                expFeedbackBean);
                    } catch (Exception ex) {
                        log.warn("Error transmitting user feedback complete via pubsub", ex);
                    }
                } else if (ufBean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {

                    ImpFeedbackResultBean impFeedbackBean = new ImpFeedbackResultBean();
                    impFeedbackBean.setRequestId(ufBean.getRequestId());
                    impFeedbackBean.setAccepted(false);

                    try {
                        pubsub.publisherPublish(myCloudID,
                                UserFeedbackEventTopics.COMPLETE,
                                ufBean.getRequestId(),
                                impFeedbackBean);
                    } catch (Exception ex) {
                        log.warn("Error transmitting user feedback clear via pubsub", ex);
                    }
                }
            }

            // notify other nodes that PPN events are cleared
            for (UserFeedbackPrivacyNegotiationEvent privacyNegotiationEvent : privacyNegotiationEvents) {
                UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
                resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
                resultBean.setType(ExpProposalType.PRIVACY_NEGOTIATION);
                resultBean.setRequestId(privacyNegotiationEvent.getRequestId());
                resultBean.setNegotiationDetails(privacyNegotiationEvent.getNegotiationDetails());
                resultBean.setResponsePolicy(privacyNegotiationEvent.getResponsePolicy());

                try {
                    pubsub.publisherPublish(myCloudID,
                            EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP,
                            resultBean.getRequestId(),
                            resultBean);
                } catch (Exception ex) {
                    log.warn("Error transmitting user feedback clear via pubsub", ex);
                }
            }

            // notify other nodes that AC events are cleared
            for (UserFeedbackAccessControlEvent accessControlEvent : accessControlEvents) {
                UserFeedbackAccessControlEvent resultBean = new UserFeedbackAccessControlEvent();
                resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
                resultBean.setType(ExpProposalType.PRIVACY_NEGOTIATION);
                resultBean.setRequestId(accessControlEvent.getRequestId());
                resultBean.setResponseItems(accessControlEvent.getResponseItems());
                resultBean.setRequestor(accessControlEvent.getRequestor());

                try {
                    pubsub.publisherPublish(myCloudID,
                            EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP,
                            resultBean.getRequestId(),
                            resultBean);
                } catch (Exception ex) {
                    log.warn("Error transmitting user feedback clear via pubsub", ex);
                }
            }

        }
    }
}
